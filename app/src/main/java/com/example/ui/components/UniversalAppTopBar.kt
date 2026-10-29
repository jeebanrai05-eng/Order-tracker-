package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthUiState
import com.example.ui.MainViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.MaterialThemeColors
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.WarningRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Universal Modern Top Bar that spans the top of the app, seamlessly integrating
 * with Android's status bar so battery, WiFi, notifications, and clock are crystal clear.
 * Includes Today's Highlight badge with Day of the Week (SUN, MON, TUE, WED, THU, FRI, SAT),
 * App Branding, Live Sync Status, and Direct Google/Manual Login trigger.
 */
@Composable
fun UniversalAppTopBar(
  viewModel: MainViewModel,
  onOpenAuthDialog: () -> Unit,
  onQuickLogToday: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val authState by viewModel.authState.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val isSyncing by viewModel.isSyncing.collectAsState()
  val ordersMap by viewModel.ordersMap.collectAsState()
  val selectedYear by viewModel.selectedYear.collectAsState()
  val selectedMonth by viewModel.selectedMonth.collectAsState()
  val themeColors = MaterialThemeColors

  // Today calculation
  val now = Calendar.getInstance()
  val todayDay = now.get(Calendar.DAY_OF_MONTH)
  val todayMonth = now.get(Calendar.MONTH) + 1
  val todayYear = now.get(Calendar.YEAR)

  // Day of week name (SUN, MON, TUE, WED, THU, FRI, SAT)
  val dayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
  val fullDateFormat = SimpleDateFormat("d MMM", Locale.ENGLISH)
  val dayOfWeekStr = dayFormat.format(now.time).uppercase() // e.g. "SAT", "SUN", "MON"
  val dateFormatted = fullDateFormat.format(now.time) // e.g. "17 Oct"

  // Check today's orders if viewing current month/year
  val isViewingCurrent = (selectedYear == todayYear && selectedMonth == todayMonth)
  val todayRecord = if (isViewingCurrent) ordersMap[todayDay] else null
  val todayOrders = todayRecord?.orders ?: 0
  val todayTip = todayRecord?.tip ?: 0.0

  // Animation for sync spin
  val infiniteTransition = rememberInfiniteTransition(label = "top_bar_sync")
  val syncRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = androidx.compose.animation.core.LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "sync_spin"
  )

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "today_pulse"
  )

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = themeColors.cardBackground,
    tonalElevation = 3.dp,
    shadowElevation = 4.dp,
    border = BorderStroke(1.dp, themeColors.cardBorder.copy(alpha = 0.4f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding() // Ensures proper space under battery, WiFi, clock & notification icons
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // --- 1. Left: Brand & Live Cloud Status ---
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(
                Brush.linearGradient(
                  listOf(themeColors.primary, themeColors.variant)
                )
              )
              .shadow(2.dp, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "🚚",
              fontSize = 20.sp
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "Order Tracker",
              fontSize = 16.sp,
              fontWeight = FontWeight.ExtraBold,
              color = themeColors.textPrimary,
              letterSpacing = (-0.3).sp
            )

            // Cloud Status pill
            Row(verticalAlignment = Alignment.CenterVertically) {
              val statusColor = when {
                isSyncing -> CyanAccent
                authState is AuthUiState.LoggedIn -> ProfitGreen
                else -> GoldYellow
              }
              Box(
                modifier = Modifier
                  .size(7.dp)
                  .clip(CircleShape)
                  .background(statusColor)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = when {
                  isSyncing -> "Syncing..."
                  authState is AuthUiState.LoggedIn -> "Cloud Synced"
                  else -> "Local Mode"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = themeColors.textSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // --- 2. Right: Action Icons & Profile ---
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Manual Cloud Sync Button
          IconButton(
            onClick = {
              if (authState is AuthUiState.LoggedIn) {
                viewModel.syncCloudData()
              } else {
                onOpenAuthDialog()
              }
            },
            modifier = Modifier
              .size(36.dp)
              .testTag("btn_top_sync")
          ) {
            if (isSyncing) {
              Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "Syncing",
                tint = themeColors.primary,
                modifier = Modifier
                  .size(20.dp)
                  .rotate(syncRotation)
              )
            } else {
              Icon(
                imageVector = if (authState is AuthUiState.LoggedIn) Icons.Default.CloudDone else Icons.Default.Sync,
                contentDescription = "Sync Status",
                tint = if (authState is AuthUiState.LoggedIn) ProfitGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          // Luxury Theme Studio Launcher
          IconButton(
            onClick = { viewModel.openThemeStudio() },
            modifier = Modifier
              .size(36.dp)
              .testTag("btn_top_theme_studio")
          ) {
            Icon(
              imageVector = Icons.Default.Palette,
              contentDescription = "Theme Studio",
              tint = themeColors.primary,
              modifier = Modifier.size(20.dp)
            )
          }

          // Dark/Light Theme Switcher
          IconButton(
            onClick = { viewModel.setDarkMode(!settings.isDarkMode) },
            modifier = Modifier
              .size(36.dp)
              .testTag("btn_toggle_dark_mode")
          ) {
            Icon(
              imageVector = if (settings.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = "Toggle Theme",
              tint = if (settings.isDarkMode) GoldYellow else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(20.dp)
            )
          }

          // User Profile / Sign In Pill Button
          when (val currentAuth = authState) {
            is AuthUiState.LoggedIn -> {
              // User avatar pill
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(themeColors.primary.copy(alpha = 0.15f))
                  .border(1.dp, themeColors.primary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                  .clickable { onOpenAuthDialog() }
                  .padding(horizontal = 10.dp, vertical = 5.dp)
                  .testTag("btn_top_profile"),
                contentAlignment = Alignment.Center
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(20.dp)
                      .clip(CircleShape)
                      .background(themeColors.primary),
                    contentAlignment = Alignment.Center
                  ) {
                    val initial = currentAuth.displayName.firstOrNull()?.uppercase()
                      ?: currentAuth.email.firstOrNull()?.uppercase()
                      ?: "U"
                    Text(
                      text = initial,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.Black
                    )
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Account",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
            is AuthUiState.LoggedOut -> {
              // Sign In Button
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(
                    Brush.horizontalGradient(
                      listOf(themeColors.primary, themeColors.variant)
                    )
                  )
                  .clickable { onOpenAuthDialog() }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
                  .testTag("btn_top_login"),
                contentAlignment = Alignment.Center
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  GoogleLogoIcon(size = 14.dp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Sign In",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // --- 3. TODAY HIGHLIGHT BAR (Prominently mentions Day: SUN, TUE, SAT, FRI, MON etc.) ---
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .scale(pulseScale)
          .clickable { onQuickLogToday() }
          .testTag("card_today_highlight"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = themeColors.cardBackground
        ),
        border = BorderStroke(
          1.2.dp,
          themeColors.cardBorder
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: TODAY HIGHLIGHT with Day of Week
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Day of Week Tag (e.g. "SAT", "SUN", "MON")
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                  Brush.horizontalGradient(
                    listOf(themeColors.primary, themeColors.variant)
                  )
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = dayOfWeekStr, // e.g. "SAT", "MON", "TUE", "SUN"
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "TODAY",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = themeColors.primary,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "• $dateFormatted",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = themeColors.textSecondary
                )
              }
              Text(
                text = if (todayOrders > 0 || todayTip > 0.0) {
                  "Logged: $todayOrders Orders • ${String.format("%.3f", todayTip)} ${settings.currency} Tip"
                } else {
                  "Tap to log today's delivery shift"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = themeColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          // Right: Quick Goal status or edit icon
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(themeColors.primary.copy(alpha = 0.12f))
              .padding(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Today,
              contentDescription = "Log Today",
              tint = themeColors.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}
