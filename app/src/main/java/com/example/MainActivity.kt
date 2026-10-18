package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.NavigationTab
import com.example.ui.components.AuthDialog
import com.example.ui.components.FloatingBottomBar
import com.example.ui.components.ModernLoadingOverlay
import com.example.ui.components.UniversalAppTopBar
import com.example.ui.screens.ChartScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.OrderTrackerTheme
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel()
      val settings by viewModel.settings.collectAsState()

      // Ensure system bar icons (battery, WiFi, notifications, clock) are clearly visible with proper contrast
      androidx.compose.runtime.DisposableEffect(settings.isDarkMode) {
        val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        // In light mode, status bar icons must be dark. In dark mode, icons must be light/white.
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
}

@Composable
fun OrderTrackerApp(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val currentTab by viewModel.currentTab.collectAsState()
  val isSyncing by viewModel.isSyncing.collectAsState()
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
      when (currentTab) {
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

  // Auth Modal for Google One-Tap and Manual Sign-in/Sign-up
  if (showAuthDialog) {
    AuthDialog(
      viewModel = viewModel,
      onDismiss = { showAuthDialog = false }
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
