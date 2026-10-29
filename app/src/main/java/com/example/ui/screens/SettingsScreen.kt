package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.data.CurrencyRegistry
import com.example.data.CountryCurrency
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuthUiState
import com.example.ui.MainViewModel
import com.example.ui.components.GoogleLogoIcon
import com.example.ui.components.ModernLoadingIndicator
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.MaterialThemeColors
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.WarningRed
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val authState by viewModel.authState.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val isSyncing by viewModel.isSyncing.collectAsState()
  val currentThemeColors = MaterialThemeColors

  var showDutyTimeDialog by remember { mutableStateOf(false) }

  var emailInput by remember { mutableStateOf("") }
  var passwordInput by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  // Salary configuration fields
  var currencyInput by remember(settings.currency) { mutableStateOf(settings.currency) }
  var showWorldwideCurrencyDialog by remember { mutableStateOf(false) }
  var currencySearchQuery by remember { mutableStateOf("") }
  var autoDetectedCountryName by remember { mutableStateOf<String?>(null) }

  var baseSalaryInput by remember(settings.baseSalary) {
    mutableStateOf(if (settings.baseSalary > 0) String.format("%.3f", settings.baseSalary) else "0.000")
  }
  var targetOrdersInput by remember(settings.targetOrders) {
    mutableStateOf(settings.targetOrders.toString())
  }
  var maxOrdersInput by remember(settings.maxOrders) {
    mutableStateOf(settings.maxOrders.toString())
  }
  var normalCommInput by remember(settings.normalComm) {
    mutableStateOf(settings.normalComm.toString())
  }
  var extraCommInput by remember(settings.extraComm) {
    mutableStateOf(settings.extraComm.toString())
  }
  var dailyEarningsGoalInput by remember(settings.dailyEarningsGoal) {
    mutableStateOf(if (settings.dailyEarningsGoal > 0) String.format("%.3f", settings.dailyEarningsGoal) else "15.000")
  }

  // Clear data confirmation dialog
  var showClearConfirmDialog by remember { mutableStateOf(false) }

  // App self-update states
  val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()

  // Dynamic Theme Adaptive Background Container
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(currentThemeColors.backgroundDark)
  ) {
    // 1. Top-Right Ambient Liquid Glow Aura (Adapts to Active Theme)
    Box(
      modifier = Modifier
        .size(360.dp)
        .offset(x = 130.dp, y = (-70).dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              currentThemeColors.backgroundGlow.copy(alpha = 0.22f),
              currentThemeColors.variant.copy(alpha = 0.08f),
              Color.Transparent
            )
          )
        )
    )

    // 2. Center-Left Ambient Glow Aura (Adapts to Active Theme)
    Box(
      modifier = Modifier
        .size(340.dp)
        .offset(x = (-120).dp, y = 380.dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              currentThemeColors.variant.copy(alpha = 0.16f),
              currentThemeColors.accent.copy(alpha = 0.06f),
              Color.Transparent
            )
          )
        )
    )

    // 3. Bottom-Right Ambient Aura
    Box(
      modifier = Modifier
        .size(300.dp)
        .offset(x = 160.dp, y = 800.dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              currentThemeColors.accent.copy(alpha = 0.12f),
              Color.Transparent
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp)
        .padding(bottom = 120.dp)
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // ==========================================
      // Settings Header matching screenshot
      // ==========================================
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.linearGradient(
                listOf(Color(0xFF7C3AED), Color(0xFF6366F1))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = "Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = currentThemeColors.textPrimary,
            letterSpacing = (-0.5).sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Manage your preferences and data",
            fontSize = 13.sp,
            color = currentThemeColors.textSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // ==========================================
      // 1. CARD: LIFETIME CLOUD BACKUP
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_cloud_backup"),
        colors = CardDefaults.cardColors(containerColor = currentThemeColors.cardBackground),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, currentThemeColors.cardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          // Card Header: Cloud Icon, Title, and Secure & Encrypted Badge
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF0284C7)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Cloud,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = "LIFETIME CLOUD BACKUP",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.White,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "Keep your data safe, always",
                  fontSize = 11.sp,
                  color = Color(0xFF38BDF8)
                )
              }
            }

            // Green Shield Badge
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF064E3B).copy(alpha = 0.7f),
              border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.8f))
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(text = "🛡️", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Secure & Encrypted",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF34D399)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          when (val currentAuth = authState) {
            is AuthUiState.LoggedOut -> {
              // Red Alert Banner: Not Logged In - Data at Risk
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF3B0B14).copy(alpha = 0.85f),
                border = BorderStroke(1.dp, Color(0xFFE11D48).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFE11D48).copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(text = "🛡️", fontSize = 16.sp)
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column {
                    Text(
                      text = "Not Logged in - Data at Risk",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                    Text(
                      text = "Sign in to backup orders, tips, and salary goals permanently.",
                      fontSize = 11.sp,
                      color = Color(0xFFFDA4AF)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              if (isSyncing) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                  contentAlignment = Alignment.Center
                ) {
                  ModernLoadingIndicator(
                    size = 42.dp,
                    message = "Connecting to Cloud..."
                  )
                }
              } else {
                // 1. White Pill Button: Continue with Google
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = Color.White,
                  shadowElevation = 3.dp,
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.signInWithGoogle(context) }
                    .testTag("btn_google_signin")
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 18.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    GoogleLogoIcon(size = 20.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                      text = "Continue with Google",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                      text = "›",
                      fontSize = 18.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color(0xFF0284C7)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Vibrant Blue Pill Button: 1-Tap Driver Login (Direct Sync)
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                      Brush.horizontalGradient(
                        listOf(Color(0xFF0284C7), Color(0xFF2563EB))
                      )
                    )
                    .clickable { viewModel.quickDriverLogin() }
                    .padding(horizontal = 18.dp, vertical = 13.dp)
                    .testTag("btn_quick_driver_login_settings")
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(text = "⚡", fontSize = 16.sp, color = Color(0xFFFDE047))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = "1-Tap Driver Login (Direct Sync)",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                      text = "›",
                      fontSize = 18.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.White
                    )
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider: OR USE EMAIL
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .height(1.dp)
                      .background(Color(0xFF334155))
                  )
                  Text(
                    text = "  OR USE EMAIL  ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.8.sp
                  )
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .height(1.dp)
                      .background(Color(0xFF334155))
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email Address Field
                OutlinedTextField(
                  value = emailInput,
                  onValueChange = { emailInput = it },
                  placeholder = { Text("Email Address", color = Color(0xFF64748B), fontSize = 13.sp) },
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Default.Mail,
                      contentDescription = null,
                      tint = Color(0xFF64748B),
                      modifier = Modifier.size(18.dp)
                    )
                  },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF070E1A),
                    unfocusedContainerColor = Color(0xFF070E1A),
                    focusedBorderColor = currentThemeColors.primary,
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                  ),
                  singleLine = true,
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_email")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Password Field
                OutlinedTextField(
                  value = passwordInput,
                  onValueChange = { passwordInput = it },
                  placeholder = { Text("Password (Min 6 chars)", color = Color(0xFF64748B), fontSize = 13.sp) },
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = null,
                      tint = Color(0xFF64748B),
                      modifier = Modifier.size(18.dp)
                    )
                  },
                  trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                      Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                      )
                    }
                  },
                  visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF070E1A),
                    unfocusedContainerColor = Color(0xFF070E1A),
                    focusedBorderColor = currentThemeColors.primary,
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                  ),
                  singleLine = true,
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_password")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Row: LOG IN & Sign Up ➔
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  TextButton(
                    onClick = { viewModel.login(emailInput, passwordInput) },
                    modifier = Modifier.testTag("btn_auth_login")
                  ) {
                    Text(
                      text = "LOG IN",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  }

                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .background(
                        Brush.horizontalGradient(
                          listOf(Color(0xFF06B6D4), Color(0xFF2563EB))
                        )
                      )
                      .clickable { viewModel.signup(emailInput, passwordInput) }
                      .padding(horizontal = 24.dp, vertical = 11.dp)
                      .testTag("btn_auth_signup")
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "Sign Up",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "Forgot Password? Tap Here",
                  fontSize = 12.sp,
                  color = Color(0xFF38BDF8),
                  fontWeight = FontWeight.SemiBold,
                  textAlign = TextAlign.Center,
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.forgotPassword(emailInput) }
                    .testTag("btn_forgot_password")
                )
              }
            }

            is AuthUiState.LoggedIn -> {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.CloudDone,
                  contentDescription = null,
                  tint = ProfitGreen,
                  modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Cloud Backup Active",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfitGreen
                  )
                  Text(
                    text = currentAuth.email,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = { viewModel.syncCloudData() },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = currentThemeColors.primary,
                    contentColor = Color.Black
                  ),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("btn_sync_now")
                ) {
                  Text("SYNC NOW", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                  onClick = { viewModel.logout() },
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                  border = BorderStroke(1.dp, Color(0xFF334155)),
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("btn_logout")
                ) {
                  Text("LOG OUT", fontSize = 13.sp)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // 2. CARD: NIGHT MODE (DARK)
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_night_mode"),
        colors = CardDefaults.cardColors(containerColor = currentThemeColors.cardBackground),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, currentThemeColors.cardBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF3C7).copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🌙", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
              Text(
                text = "Night Mode (Dark)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Reduce eye strain in low light",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }

          Switch(
            checked = settings.isDarkMode,
            onCheckedChange = { isDark -> viewModel.setDarkMode(isDark) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = Color(0xFF0284C7),
              uncheckedThumbColor = Color(0xFF94A3B8),
              uncheckedTrackColor = Color(0xFF1E293B)
            ),
            modifier = Modifier.testTag("switch_dark_mode")
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // 3. CARD: DAILY EARNINGS GOAL
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_daily_earnings_goal"),
        colors = CardDefaults.cardColors(containerColor = currentThemeColors.cardBackground),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, currentThemeColors.cardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF8B5CF6).copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🎯", fontSize = 18.sp)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "Daily Earnings Goal",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Text(text = "📊", fontSize = 18.sp)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Set your target earnings per shift. Progress is tracked automatically on each day and securely backed up.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
          )

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Daily Target (${currencyInput.ifBlank { "K.D." }})",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF94A3B8)
          )

          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = dailyEarningsGoalInput,
            onValueChange = { dailyEarningsGoalInput = it },
            placeholder = { Text("15.000", color = Color(0xFF64748B)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF070E1A),
              unfocusedContainerColor = Color(0xFF070E1A),
              focusedBorderColor = currentThemeColors.primary,
              unfocusedBorderColor = Color(0xFF1E293B),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_daily_earnings_goal")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Preset Chips row: 15, 20, 25, 30, 40, 50
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(15.0, 20.0, 25.0, 30.0, 40.0, 50.0).forEach { preset ->
              val isSelected = (dailyEarningsGoalInput.toDoubleOrNull() == preset)
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    if (isSelected) Color(0xFF0284C7) else Color(0xFF070E1A)
                  )
                  .border(
                    1.dp,
                    if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                    RoundedCornerShape(10.dp)
                  )
                  .clickable { dailyEarningsGoalInput = String.format("%.3f", preset) }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${preset.toInt()}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else Color(0xFF94A3B8)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "☁️", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Saved and synchronized securely with Cloud Backup",
              fontSize = 11.sp,
              color = Color(0xFF10B981),
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // 4. CARD: SALARY CONFIGURATION
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_salary_config"),
        colors = CardDefaults.cardColors(containerColor = currentThemeColors.cardBackground),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, currentThemeColors.cardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF10B981)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "💼", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "Salary Configuration",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = currentThemeColors.textPrimary
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // ⚡ 1-Tap Auto-Detect Country & Currency Button
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.horizontalGradient(
                  if (currentThemeColors.isDark) {
                    listOf(currentThemeColors.primary.copy(alpha = 0.18f), currentThemeColors.variant.copy(alpha = 0.28f))
                  } else {
                    listOf(currentThemeColors.primary.copy(alpha = 0.12f), currentThemeColors.variant.copy(alpha = 0.18f))
                  }
                )
              )
              .border(1.2.dp, currentThemeColors.primary, RoundedCornerShape(14.dp))
              .clickable {
                val detected = viewModel.detectAndApplyCurrency()
                currencyInput = detected.currencyCode
                autoDetectedCountryName = "${detected.flagEmoji} ${detected.countryName}"
                scope.launch {
                  viewModel.showSnackbar("🌍 Detected ${detected.flagEmoji} ${detected.countryName} (${detected.currencyCode})")
                }
              }
              .padding(horizontal = 14.dp, vertical = 11.dp)
              .testTag("btn_auto_detect_currency")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(text = "⚡", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (autoDetectedCountryName != null) {
                  "Detected: $autoDetectedCountryName"
                } else {
                  "Auto-Detect Country & Currency 🌍"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = currentThemeColors.primary
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 2-Column Grid: Currency & Base Salary
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Worldwide Currency Selector Box
            val resolvedCurrency = remember(currencyInput) { CurrencyRegistry.resolveCurrency(currencyInput) }
            Box(modifier = Modifier.weight(1f)) {
              Column {
                Text("Currency", fontSize = 11.sp, color = currentThemeColors.textSecondary, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC))
                    .border(1.dp, currentThemeColors.cardBorder, RoundedCornerShape(12.dp))
                    .clickable { showWorldwideCurrencyDialog = true }
                    .padding(horizontal = 12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = resolvedCurrency.flagEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = currencyInput,
                      color = currentThemeColors.textPrimary,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = currentThemeColors.textSecondary
                  )
                }
              }
            }

            // Base Salary
            Column(modifier = Modifier.weight(1f)) {
              Text("Base Salary", fontSize = 11.sp, color = currentThemeColors.textSecondary, fontWeight = FontWeight.SemiBold)
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = baseSalaryInput,
                onValueChange = { baseSalaryInput = it },
                placeholder = { Text("0.000", color = currentThemeColors.textMuted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                  unfocusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                  focusedBorderColor = currentThemeColors.primary,
                  unfocusedBorderColor = currentThemeColors.cardBorder,
                  focusedTextColor = currentThemeColors.textPrimary,
                  unfocusedTextColor = currentThemeColors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_base_salary")
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Row 2: Target Orders & Max Orders
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Target Orders", fontSize = 11.sp, color = currentThemeColors.textSecondary, fontWeight = FontWeight.SemiBold)
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = targetOrdersInput,
                onValueChange = { targetOrdersInput = it.filter { c -> c.isDigit() } },
                placeholder = { Text("400", color = currentThemeColors.textMuted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                  unfocusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                  focusedBorderColor = currentThemeColors.primary,
                  unfocusedBorderColor = currentThemeColors.cardBorder,
                  focusedTextColor = currentThemeColors.textPrimary,
                  unfocusedTextColor = currentThemeColors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_target_orders")
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text("Max Orders", fontSize = 11.sp, color = currentThemeColors.textSecondary, fontWeight = FontWeight.SemiBold)
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedTextField(
                value = maxOrdersInput,
                onValueChange = { maxOrdersInput = it.filter { c -> c.isDigit() } },
                placeholder = { Text("400", color = currentThemeColors.textMuted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                  unfocusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                  focusedBorderColor = currentThemeColors.primary,
                  unfocusedBorderColor = currentThemeColors.cardBorder,
                  focusedTextColor = currentThemeColors.textPrimary,
                  unfocusedTextColor = currentThemeColors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_max_orders")
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Row 3: Normal Comm.
          Column(modifier = Modifier.fillMaxWidth()) {
            Text("Normal Comm.", fontSize = 11.sp, color = currentThemeColors.textSecondary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = normalCommInput,
              onValueChange = { normalCommInput = it },
              placeholder = { Text("0.5", color = currentThemeColors.textMuted) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                unfocusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                focusedBorderColor = currentThemeColors.primary,
                unfocusedBorderColor = currentThemeColors.cardBorder,
                focusedTextColor = currentThemeColors.textPrimary,
                unfocusedTextColor = currentThemeColors.textPrimary
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_normal_comm")
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Row 4: Extra Comm.
          Column(modifier = Modifier.fillMaxWidth()) {
            Text("Extra Comm.", fontSize = 11.sp, color = currentThemeColors.textSecondary, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
              value = extraCommInput,
              onValueChange = { extraCommInput = it },
              placeholder = { Text("0.8", color = currentThemeColors.textMuted) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                unfocusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC),
                focusedBorderColor = currentThemeColors.primary,
                unfocusedBorderColor = currentThemeColors.cardBorder,
                focusedTextColor = currentThemeColors.textPrimary,
                unfocusedTextColor = currentThemeColors.textPrimary
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_extra_comm")
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // 5. CARD: LUXURY THEMES (Adaptive Theme Controls)
      // ==========================================
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = currentThemeColors.cardBackground),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, currentThemeColors.cardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFF8B5CF6)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "💎", fontSize = 18.sp)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "Luxury Themes",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Text(
              text = "Theme Studio (8 Themes) ›",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF818CF8),
              modifier = Modifier
                .clickable { viewModel.openThemeStudio() }
                .testTag("btn_open_theme_studio")
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Luxury Theme Cards with Glass Neo
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // 1. Glass Neo (Ultra-Frosted Cyan Glass)
            LuxuryThemeCard(
              name = "Glass Neo",
              gradient = Brush.linearGradient(listOf(Color(0xFF002233), Color(0xFF00F5FF), Color(0xFF00B4D8))),
              textColor = Color(0xFF00F5FF),
              isSelected = settings.selectedTheme == "Glass Neo",
              onClick = { viewModel.updateTheme("Glass Neo") },
              modifier = Modifier.width(90.dp)
            )

            // 2. Neon Cyan
            LuxuryThemeCard(
              name = "Neon Cyan",
              gradient = Brush.linearGradient(listOf(Color(0xFF003B46), Color(0xFF00E5FF), Color(0xFF005F73))),
              textColor = Color(0xFF00E5FF),
              isSelected = settings.selectedTheme == "Neon Cyan",
              onClick = { viewModel.updateTheme("Neon Cyan") },
              modifier = Modifier.width(90.dp)
            )

            // 3. Luxury Gold
            LuxuryThemeCard(
              name = "Luxury Gold",
              gradient = Brush.linearGradient(listOf(Color(0xFF3D2600), Color(0xFFFFB300), Color(0xFF784800))),
              textColor = Color(0xFFFFB300),
              isSelected = settings.selectedTheme == "Luxury Gold",
              onClick = { viewModel.updateTheme("Luxury Gold") },
              modifier = Modifier.width(90.dp)
            )

            // 4. Imperial Jade
            LuxuryThemeCard(
              name = "Imperial Jade",
              gradient = Brush.linearGradient(listOf(Color(0xFF003816), Color(0xFF00E676), Color(0xFF005221))),
              textColor = Color(0xFF00E676),
              isSelected = settings.selectedTheme == "Imperial Jade",
              onClick = { viewModel.updateTheme("Imperial Jade") },
              modifier = Modifier.width(90.dp)
            )

            // 5. Mid Purple
            LuxuryThemeCard(
              name = "Mid Purple",
              gradient = Brush.linearGradient(listOf(Color(0xFF2E0854), Color(0xFFC084FC), Color(0xFF7E22CE))),
              textColor = Color(0xFFC084FC),
              isSelected = settings.selectedTheme == "Mid Purple",
              onClick = { viewModel.updateTheme("Mid Purple") },
              modifier = Modifier.width(90.dp)
            )

            // 6. More...
            LuxuryThemeMoreCard(
              onClick = { viewModel.openThemeStudio() },
              modifier = Modifier.width(90.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // 6. CARD: NOTIFICATIONS & SHIFT REMINDERS
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_duty_reminder"),
        colors = CardDefaults.cardColors(containerColor = currentThemeColors.cardBackground),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, currentThemeColors.cardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF8B5CF6)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "Notifications & Shift Reminders",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Duty / Shift End Reminder Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFF0891B2).copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "⏰", fontSize = 18.sp)
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = "Duty / Shift End Reminder",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF38BDF8)
                )
                Text(
                  text = "Pops up notification right at shift finish to log deliveries & tips!",
                  fontSize = 11.sp,
                  color = Color(0xFF94A3B8)
                )
              }
            }

            Switch(
              checked = settings.dutyEndReminderEnabled,
              onCheckedChange = { isChecked ->
                viewModel.updateDutyEndReminder(
                  isChecked,
                  settings.dutyEndHour,
                  settings.dutyEndMinute
                )
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0284C7)
              ),
              modifier = Modifier.testTag("switch_duty_reminder")
            )
          }

          // Shift Finish Time Row
          Spacer(modifier = Modifier.height(12.dp))

          val formattedHour = if (settings.dutyEndHour == 0) 12 else if (settings.dutyEndHour > 12) settings.dutyEndHour - 12 else settings.dutyEndHour
          val amPm = if (settings.dutyEndHour >= 12) "PM" else "AM"
          val formattedTime = String.format("%02d:%02d %s", formattedHour, settings.dutyEndMinute, amPm)

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF070E1A))
              .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
              .clickable { showDutyTimeDialog = true }
              .padding(horizontal = 14.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "⏰", fontSize = 15.sp)
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Shift Finish Time",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "$formattedTime  ›",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Send Test Notification Button
          OutlinedButton(
            onClick = { viewModel.triggerTestDutyReminder() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
            border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.5f)),
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp)
              .testTag("btn_test_duty_notification")
          ) {
            Text(
              text = "🔔 Send Test Notification (1-Tap Entry)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Auto Midnight Reminder
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF0D9488).copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🔔", fontSize = 18.sp)
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = "Auto Midnight Reminder",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF2DD4BF)
                )
                Text(
                  text = "Get reminded automatically at midnight",
                  fontSize = 11.sp,
                  color = Color(0xFF94A3B8)
                )
              }
            }

            Switch(
              checked = settings.autoMidnightReminder,
              onCheckedChange = { isChecked -> viewModel.updateAutoMidnightReminder(isChecked) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0284C7)
              ),
              modifier = Modifier.testTag("switch_midnight_reminder")
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Export Data & Clear Data Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                scope.launch {
                  val csvContent = viewModel.exportAllDataCsv()
                  val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, csvContent)
                    type = "text/csv"
                  }
                  context.startActivity(Intent.createChooser(sendIntent, "Export Order Records"))
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF070E1A),
                contentColor = Color.White
              ),
              border = BorderStroke(1.dp, Color(0xFF334155)),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("btn_export_data")
            ) {
              Text(text = "📥 EXPORT DATA", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
              onClick = { showClearConfirmDialog = true },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE11D48),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("btn_clear_data")
            ) {
              Text(text = "🗑️ CLEAR DATA", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // 7. CARD: APP VERSION & SECURE SELF-UPDATE
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_self_update"),
        colors = CardDefaults.cardColors(containerColor = currentThemeColors.cardBackground),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, currentThemeColors.cardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🛡️", fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "APP VERSION & SECURE SELF-UPDATE",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF94A3B8),
              letterSpacing = 0.8.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Order Tracker",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = currentThemeColors.textPrimary
              )
              Text(
                text = "Version 2.0.6 (Build 7)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(currentThemeColors.primary)
                .clickable { viewModel.openUpdateCenter() }
                .padding(horizontal = 14.dp, vertical = 9.dp)
                .testTag("btn_check_updates")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.SystemUpdate,
                  contentDescription = null,
                  tint = Color.Black,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Update Center",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 12.sp,
                  color = Color.Black
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // What's New Container
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC))
              .border(1.dp, currentThemeColors.cardBorder, RoundedCornerShape(14.dp))
              .padding(14.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "✨", fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "What's New in v2.0.6",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = currentThemeColors.primary
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "• ☀️ Pure Professional White Mode: Ultra-clean, crisp white canvas with high-contrast slate typography and vibrant neon accents.\n• 🌍 Worldwide Currencies with Flags: Full global currency registry covering Nepal (NPR Rs.), Kuwait (KWD KD), India (INR ₹), UAE (AED), USA (USD $), UK (GBP £), Euro (EUR €), and 35+ more countries with real flags.\n• ⚡ 1-Tap Auto-Detect Country: Automatically detects your device locale and configures the native currency instantly.\n• 💎 Complete Luxury Theme Harmonization: All 8 themes (Glass Neo, Neon Cyan, Luxury Gold, Imperial Jade, Mid Purple, etc.) adapt seamlessly across light and dark modes.\n• 🔒 Verified RSA Cryptographic Signature: Self-updater now validates official certificate fingerprint to eliminate SHA-256 false alarms.",
              fontSize = 11.sp,
              lineHeight = 16.sp,
              color = currentThemeColors.textSecondary
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedButton(
            onClick = { viewModel.triggerTestUpdateNotification() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = currentThemeColors.primary),
            border = BorderStroke(1.dp, currentThemeColors.primary.copy(alpha = 0.5f)),
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp)
              .testTag("btn_test_update_notification")
          ) {
            Text(
              text = "🚀 Send Test Update Notification",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // ==========================================
      // 8. PROMINENT [ SAVE CONFIGURATION ] BUTTON
      // ==========================================
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
            )
          )
          .clickable {
            viewModel.saveFullConfiguration(
              settings.copy(
                currency = currencyInput.trim().ifBlank { "K.D." },
                baseSalary = baseSalaryInput.toDoubleOrNull() ?: 0.0,
                targetOrders = targetOrdersInput.toIntOrNull() ?: 400,
                maxOrders = maxOrdersInput.toIntOrNull() ?: 400,
                normalComm = normalCommInput.toDoubleOrNull() ?: 0.5,
                extraComm = extraCommInput.toDoubleOrNull() ?: 0.8,
                dailyEarningsGoal = dailyEarningsGoalInput.toDoubleOrNull() ?: 15.0
              )
            )
          }
          .testTag("btn_save_configuration"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Save,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "SAVE CONFIGURATION",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 0.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Shift Finish Time Picker Dialog
  if (showDutyTimeDialog) {
    var tempHour by remember { mutableStateOf(settings.dutyEndHour) }
    var tempMinute by remember { mutableStateOf(settings.dutyEndMinute) }

    AlertDialog(
      onDismissRequest = { showDutyTimeDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("⏰", fontSize = 20.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Shift Finish Time", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Select when your delivery duty ends to receive the order logging reminder:",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
          )
          Spacer(modifier = Modifier.height(16.dp))

          val displayHour = if (tempHour == 0) 12 else if (tempHour > 12) tempHour - 12 else tempHour
          val isPm = tempHour >= 12

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            // Hour controls
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("HOUR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { tempHour = (tempHour - 1 + 24) % 24 }) {
                  Text("▼", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text(
                  text = "%02d".format(displayHour),
                  fontSize = 24.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = currentThemeColors.primary
                )
                IconButton(onClick = { tempHour = (tempHour + 1) % 24 }) {
                  Text("▲", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))

            // Minute controls
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("MINUTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { tempMinute = (tempMinute - 15 + 60) % 60 }) {
                  Text("▼", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text(
                  text = "%02d".format(tempMinute),
                  fontSize = 24.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = currentThemeColors.primary
                )
                IconButton(onClick = { tempMinute = (tempMinute + 15) % 60 }) {
                  Text("▲", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // AM/PM toggle
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = currentThemeColors.primary.copy(alpha = 0.15f),
              modifier = Modifier.clickable {
                tempHour = if (tempHour >= 12) tempHour - 12 else tempHour + 12
              }
            ) {
              Text(
                text = if (isPm) "PM" else "AM",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = currentThemeColors.primary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateDutyEndReminder(
              enabled = true,
              hour = tempHour,
              minute = tempMinute
            )
            showDutyTimeDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = currentThemeColors.primary, contentColor = Color.Black)
        ) {
          Text("Set Reminder", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDutyTimeDialog = false }) {
          Text("Cancel", color = currentThemeColors.textSecondary)
        }
      },
      containerColor = currentThemeColors.cardBackground
    )
  }

  // Clear data confirmation dialog
  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = { Text("Clear All Records?", fontWeight = FontWeight.Bold, color = currentThemeColors.textPrimary) },
      text = {
        Text(
          "This will permanently remove all orders, tips, and gas expense records. This action cannot be undone.",
          color = currentThemeColors.textSecondary
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.clearAllData()
            showClearConfirmDialog = false
          }
        ) {
          Text("Yes, Clear All", color = WarningRed, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text("Cancel", color = currentThemeColors.textSecondary)
        }
      },
      containerColor = currentThemeColors.cardBackground
    )
  }

  // Worldwide Currency Selection Dialog
  if (showWorldwideCurrencyDialog) {
    val filteredCurrencies = remember(currencySearchQuery) {
      if (currencySearchQuery.isBlank()) {
        CurrencyRegistry.WORLD_CURRENCIES
      } else {
        CurrencyRegistry.WORLD_CURRENCIES.filter {
          it.countryName.contains(currencySearchQuery, ignoreCase = true) ||
          it.currencyCode.contains(currencySearchQuery, ignoreCase = true) ||
          it.currencyName.contains(currencySearchQuery, ignoreCase = true) ||
          it.symbol.contains(currencySearchQuery, ignoreCase = true)
        }
      }
    }

    AlertDialog(
      onDismissRequest = {
        showWorldwideCurrencyDialog = false
        currencySearchQuery = ""
      },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🌍", fontSize = 20.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Select Country & Currency",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = currentThemeColors.textPrimary
          )
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
        ) {
          // Search Input
          OutlinedTextField(
            value = currencySearchQuery,
            onValueChange = { currencySearchQuery = it },
            placeholder = { Text("Search country or currency...", color = currentThemeColors.textMuted, fontSize = 12.sp) },
            leadingIcon = {
              Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = currentThemeColors.textSecondary, modifier = Modifier.size(18.dp))
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF1F5F9),
              unfocusedContainerColor = if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF1F5F9),
              focusedBorderColor = currentThemeColors.primary,
              unfocusedBorderColor = currentThemeColors.cardBorder,
              focusedTextColor = currentThemeColors.textPrimary,
              unfocusedTextColor = currentThemeColors.textPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // 1-Tap Auto-Detect Header Pill inside modal
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = currentThemeColors.primary.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, currentThemeColors.primary.copy(alpha = 0.4f)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                val detected = viewModel.detectAndApplyCurrency()
                currencyInput = detected.currencyCode
                autoDetectedCountryName = "${detected.flagEmoji} ${detected.countryName}"
                showWorldwideCurrencyDialog = false
                currencySearchQuery = ""
                scope.launch {
                  viewModel.showSnackbar("🌍 Auto-Detected: ${detected.flagEmoji} ${detected.countryName} (${detected.currencyCode})")
                }
              }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "⚡", fontSize = 14.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Auto-Detect My Current Location 📍",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = currentThemeColors.primary
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Currency Scrollable List
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            filteredCurrencies.forEach { curr ->
              val isSelected = (currencyInput.equals(curr.currencyCode, ignoreCase = true) || currencyInput.startsWith(curr.currencyCode))
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) currentThemeColors.primary.copy(alpha = 0.18f) else (if (currentThemeColors.isDark) Color(0xFF070E1A) else Color(0xFFF8FAFC)),
                border = BorderStroke(
                  1.dp,
                  if (isSelected) currentThemeColors.primary else currentThemeColors.cardBorder
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    currencyInput = curr.currencyCode
                    viewModel.updateCurrency(curr.currencyCode)
                    showWorldwideCurrencyDialog = false
                    currencySearchQuery = ""
                  }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = curr.flagEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = curr.countryName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentThemeColors.textPrimary
                      )
                      Text(
                        text = "${curr.currencyName} (${curr.symbol})",
                        fontSize = 11.sp,
                        color = currentThemeColors.textSecondary
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) currentThemeColors.primary else (if (currentThemeColors.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                  ) {
                    Text(
                      text = curr.currencyCode,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = if (isSelected) Color.Black else currentThemeColors.textPrimary,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = {
          showWorldwideCurrencyDialog = false
          currencySearchQuery = ""
        }) {
          Text("Cancel", color = currentThemeColors.textSecondary)
        }
      },
      containerColor = currentThemeColors.cardBackground
    )
  }
}

/**
 * Luxury Theme Preview Card matching the screenshot artwork.
 */
@Composable
private fun LuxuryThemeCard(
  name: String,
  gradient: Brush,
  textColor: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFF070E1A))
      .border(
        width = if (isSelected) 1.8.dp else 1.dp,
        color = if (isSelected) textColor else Color(0xFF1E293B),
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(onClick = onClick)
      .padding(6.dp)
      .testTag("theme_btn_$name")
  ) {
    // Mini Artwork Preview
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(38.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(gradient)
    ) {
      if (isSelected) {
        Box(
          modifier = Modifier
            .padding(4.dp)
            .size(16.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .align(Alignment.TopEnd),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            tint = textColor,
            modifier = Modifier.size(12.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = name,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = if (isSelected) textColor else Color(0xFF94A3B8),
      textAlign = TextAlign.Center,
      maxLines = 1
    )
  }
}

/**
 * "More..." Card opening Theme Studio
 */
@Composable
private fun LuxuryThemeMoreCard(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFF070E1A))
      .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(6.dp)
      .testTag("theme_btn_more")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(38.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(
          Brush.linearGradient(
            listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.MoreHoriz,
        contentDescription = "More themes",
        tint = Color(0xFF818CF8),
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "More...",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF818CF8),
      textAlign = TextAlign.Center,
      maxLines = 1
    )
  }
}
