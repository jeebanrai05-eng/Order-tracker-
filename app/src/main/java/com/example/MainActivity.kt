package com.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.NavigationTab
import com.example.ui.components.AuthDialog
import com.example.ui.components.FloatingBottomBar
import com.example.ui.components.ModernLoadingOverlay
import com.example.ui.components.ThemeLauncherDialog
import com.example.ui.components.UniversalAppTopBar
import com.example.ui.components.UpdateDialog
import com.example.ui.screens.ChartScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.OrderTrackerTheme
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

class MainActivity : ComponentActivity() {

  private var mainViewModel: MainViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val viewModel: MainViewModel = viewModel()
      mainViewModel = viewModel
      val settings by viewModel.settings.collectAsState()

      // Handle notification deep links (e.g. 1-tap add order from shift end notification)
      LaunchedEffect(intent) {
        handleNotificationIntent(intent, viewModel)
      }

      // Notification permission request for Android 13+ (Tiramisu)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
          ActivityResultContracts.RequestPermission()
        ) { /* permission handled */ }
        LaunchedEffect(Unit) {
          permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
      }

      // Ensure system bar icons (battery, WiFi, notifications, clock) are clearly visible
      DisposableEffect(settings.isDarkMode) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = !settings.isDarkMode
        insetsController.isAppearanceLightNavigationBars = !settings.isDarkMode
        onDispose {}
      }

      OrderTrackerTheme(
        darkTheme = settings.isDarkMode,
        themeName = settings.selectedTheme
      ) {
        OrderTrackerApp(viewModel = viewModel)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    mainViewModel?.let { handleNotificationIntent(intent, it) }
  }

  private fun handleNotificationIntent(intent: Intent?, viewModel: MainViewModel) {
    if (intent?.getBooleanExtra("open_add_order", false) == true) {
      val now = Calendar.getInstance()
      val targetDay = intent.getIntExtra("target_day", now.get(Calendar.DAY_OF_MONTH))
      viewModel.setYearAndMonth(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1)
      viewModel.setTab(NavigationTab.ORDERS)
      viewModel.setEditingDay(targetDay)
    } else if (intent?.getStringExtra("open_tab") == "report") {
      viewModel.setTab(NavigationTab.CHART)
    }
  }
}

@Composable
fun OrderTrackerApp(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val currentTab by viewModel.currentTab.collectAsState()
  val isSyncing by viewModel.isSyncing.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }
  var showAuthDialog by remember { mutableStateOf(false) }

  // Listen to snackbar messages from ViewModel
  LaunchedEffect(Unit) {
    viewModel.snackbarMessage.collectLatest { msg ->
      snackbarHostState.showSnackbar(msg)
    }
  }

  // Handle back navigation to return to ORDERS tab when on other tabs
  if (currentTab != NavigationTab.ORDERS) {
    BackHandler {
      viewModel.setTab(NavigationTab.ORDERS)
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      UniversalAppTopBar(
        viewModel = viewModel,
        onOpenAuthDialog = { showAuthDialog = true },
        onQuickLogToday = {
          // Switch to orders tab and trigger today's edit dialog
          val now = Calendar.getInstance()
          viewModel.setYearAndMonth(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1)
          viewModel.setTab(NavigationTab.ORDERS)
          viewModel.setEditingDay(now.get(Calendar.DAY_OF_MONTH))
        }
      )
    },
    bottomBar = {
      FloatingBottomBar(
        currentTab = currentTab,
        onTabSelected = { tab -> viewModel.setTab(tab) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentTab,
        transitionSpec = {
          fadeIn(animationSpec = tween(durationMillis = 260)) togetherWith
            fadeOut(animationSpec = tween(durationMillis = 200))
        },
        label = "tabTransition"
      ) { tab ->
        when (tab) {
          NavigationTab.ORDERS -> {
            OrdersScreen(viewModel = viewModel)
          }
          NavigationTab.CHART -> {
            ChartScreen(
              viewModel = viewModel,
              onOpenMenu = { viewModel.setTab(NavigationTab.SETTINGS) }
            )
          }
          NavigationTab.SETTINGS -> {
            SettingsScreen(viewModel = viewModel)
          }
        }
      }
    }
  }

  // Auth Modal for Google One-Tap and Manual Sign-in/Sign-up
  if (showAuthDialog) {
    AuthDialog(
      viewModel = viewModel,
      onDismiss = { showAuthDialog = false }
    )
  }

  // Luxury Theme Studio Dialog
  val showThemeStudio by viewModel.showThemeStudio.collectAsState()
  if (showThemeStudio) {
    ThemeLauncherDialog(
      currentTheme = settings.selectedTheme,
      onThemeSelect = { newTheme ->
        viewModel.updateTheme(newTheme)
      },
      onDismiss = { viewModel.closeThemeStudio() }
    )
  }

  // Self-Update Modal Dialog with progress and cryptographic verification
  val updateCheckResult by viewModel.updateCheckResult.collectAsState()
  val downloadStatus by viewModel.downloadStatus.collectAsState()
  val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()
  val showUpdateCenterModal by viewModel.showUpdateCenterModal.collectAsState()

  if (showUpdateCenterModal || updateCheckResult is com.example.data.update.UpdateCheckResult.UpdateAvailable) {
    UpdateDialog(
      isChecking = isCheckingUpdate,
      checkResult = updateCheckResult,
      currentVersionName = viewModel.updateManager.currentVersionName,
      currentVersionCode = viewModel.updateManager.currentVersionCode,
      downloadStatus = downloadStatus,
      onCheckAgain = { viewModel.checkForUpdates() },
      onStartDownload = { manifest -> viewModel.startDownloadUpdate(manifest) },
      onInstall = { uri -> viewModel.installDownloadedApk(uri) },
      onDismiss = { viewModel.dismissUpdateCenter() }
    )
  }

  // Modern Fullscreen Loading Overlay during sync operations (when auth dialog is closed)
  if (isSyncing && !showAuthDialog) {
    ModernLoadingOverlay(
      isVisible = true,
      message = "Syncing Shifts with Cloud...",
      subMessage = "Securing daily orders, tips, and goals"
    )
  }
}
