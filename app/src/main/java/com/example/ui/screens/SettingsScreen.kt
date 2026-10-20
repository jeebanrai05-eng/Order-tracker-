package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MaterialThemeColors
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.PurplePrimary
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

  var emailInput by remember { mutableStateOf("") }
  var passwordInput by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  // Salary configuration fields
  var currencyInput by remember(settings.currency) { mutableStateOf(settings.currency) }
  var baseSalaryInput by remember(settings.baseSalary) {
    mutableStateOf(if (settings.baseSalary > 0) String.format("%.3f", settings.baseSalary) else "")
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
    mutableStateOf(if (settings.dailyEarningsGoal > 0) String.format("%.3f", settings.dailyEarningsGoal) else "25.000")
  }

  // Clear data confirmation dialog
  var showClearConfirmDialog by remember { mutableStateOf(false) }

  // App self-update states
  val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Decorative Cyan Circle in top right (matching screenshots)
    Box(
      modifier = Modifier
        .size(160.dp)
        .offset(x = 240.dp, y = (-40).dp)
        .clip(CircleShape)
        .background(Color(0xFF67E8F9).copy(alpha = 0.55f))
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .imePadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 110.dp)
    ) {
      Spacer(modifier = Modifier.height(24.dp))

      // Header: Settings / Manage your preferences and data
      Text(
        text = "Settings",
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Manage your preferences and data",
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(28.dp))

      // 1. Section: LIFETIME CLOUD BACKUP
      Text(
        text = "LIFETIME CLOUD BACKUP",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Cloud Backup Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_cloud_backup"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          when (val currentAuth = authState) {
            is AuthUiState.LoggedOut -> {
              // Status banner
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(WarningRed.copy(alpha = 0.08f))
                  .border(1.dp, WarningRed.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = "🛡️", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Not Logged In • Data at Risk",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningRed
                  )
                  Text(
                    text = "Sign in to backup orders, tips, and salary goals permanently.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              if (isSyncing) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                  contentAlignment = Alignment.Center
                ) {
                  com.example.ui.components.ModernLoadingIndicator(
                    size = 48.dp,
                    message = "Connecting to Cloud..."
                  )
                }
              } else {
                // 1. Direct Google Sign-In Button
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.signInWithGoogle(context) }
                    .testTag("btn_google_signin"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                  ),
                  border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                  elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                  ) {
                    com.example.ui.components.GoogleLogoIcon(size = 22.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                      text = "Continue with Google",
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Instant 1-Tap Driver Login
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.quickDriverLogin() }
                    .testTag("btn_quick_driver_login_settings"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = currentThemeColors.primary.copy(alpha = 0.12f)
                  ),
                  border = BorderStroke(1.2.dp, currentThemeColors.primary.copy(alpha = 0.4f))
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                  ) {
                    Text(text = "⚡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "1-Tap Driver Login (Direct Sync)",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Divider
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(modifier = Modifier.weight(1f).height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)))
                  Text(
                    text = "  OR USE EMAIL  ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                  )
                  Box(modifier = Modifier.weight(1f).height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Manual Email & Password Input
                OutlinedTextField(
                  value = emailInput,
                  onValueChange = { emailInput = it },
                  label = { Text("Email Address") },
                  placeholder = { Text("you@example.com") },
                  leadingIcon = { Text("✉️", fontSize = 16.sp) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                  singleLine = true,
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_email")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                  value = passwordInput,
                  onValueChange = { passwordInput = it },
                  label = { Text("Password (Min 6 chars)") },
                  leadingIcon = { Text("🔒", fontSize = 16.sp) },
                  visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                  shape = RoundedCornerShape(12.dp),
                  trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                      Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                      )
                    }
                  },
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_password")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Side by side Log In & Sign Up buttons
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Button(
                    onClick = { viewModel.login(emailInput, passwordInput) },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.surfaceVariant,
                      contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                      .weight(1f)
                      .height(48.dp)
                      .testTag("btn_auth_login")
                  ) {
                    Text("LOG IN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  }

                  Button(
                    onClick = { viewModel.signup(emailInput, passwordInput) },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = currentThemeColors.primary,
                      contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                      .weight(1f)
                      .height(48.dp)
                      .testTag("btn_auth_signup")
                  ) {
                    Text("SIGN UP", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                  text = "Forgot Password? Tap Here",
                  fontSize = 13.sp,
                  color = currentThemeColors.primary,
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfitGreen
                  )
                  Text(
                    text = currentAuth.email,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

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
                  Text("SYNC NOW", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                  onClick = { viewModel.logout() },
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("btn_logout")
                ) {
                  Text("LOG OUT")
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 2. Section: Night Mode (Dark)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_night_mode"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
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
                .background(Color(0xFFFEF3C7)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🌙", fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
              Text(
                text = "Night Mode (Dark)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Reduce eye strain in low light",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Switch(
            checked = settings.isDarkMode,
            onCheckedChange = { isDark -> viewModel.setDarkMode(isDark) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = currentThemeColors.primary
            ),
            modifier = Modifier.testTag("switch_dark_mode")
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section: 🎯 Daily Earnings Goal (Firestore Cloud Sync)
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "🎯", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Daily Earnings Goal",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_daily_earnings_goal"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Text(
            text = "Set your target earnings per shift. Progress is tracked automatically on each day and securely backed up.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = dailyEarningsGoalInput,
            onValueChange = { dailyEarningsGoalInput = it },
            label = { Text("Daily Target (${currencyInput.ifBlank { "K.D" }})") },
            placeholder = { Text("25.000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_daily_earnings_goal")
          )

          // Quick preset chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(15.0, 20.0, 25.0, 30.0, 40.0, 50.0).forEach { preset ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant)
                  .clickable { dailyEarningsGoalInput = String.format("%.3f", preset) }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "${preset.toInt()}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "☁️", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Saved and synchronized securely with Cloud Backup",
              fontSize = 11.sp,
              color = currentThemeColors.primary,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 3. Section: 💳 Salary Configuration matching Screenshot_20261018-004754.png
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "💳", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Salary Configuration",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_salary_config"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          // Row 1: Currency & Base Salary
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            OutlinedTextField(
              value = currencyInput,
              onValueChange = { currencyInput = it },
              label = { Text("Currency") },
              placeholder = { Text("K.D") },
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("input_currency")
            )

            OutlinedTextField(
              value = baseSalaryInput,
              onValueChange = { baseSalaryInput = it },
              label = { Text("Base Salary") },
              placeholder = { Text("0.000") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier
                .weight(1.3f)
                .testTag("input_base_salary")
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Row 2: Target Orders & Max Orders
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            OutlinedTextField(
              value = targetOrdersInput,
              onValueChange = { targetOrdersInput = it.filter { c -> c.isDigit() } },
              label = { Text("Target Orders") },
              placeholder = { Text("400") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("input_target_orders")
            )

            OutlinedTextField(
              value = maxOrdersInput,
              onValueChange = { maxOrdersInput = it.filter { c -> c.isDigit() } },
              label = { Text("Max Orders") },
              placeholder = { Text("400") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("input_max_orders")
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Row 3: Normal Comm.
          OutlinedTextField(
            value = normalCommInput,
            onValueChange = { normalCommInput = it },
            label = { Text("Normal Comm.") },
            placeholder = { Text("0.5") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_normal_comm")
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Row 4: Extra Comm.
          OutlinedTextField(
            value = extraCommInput,
            onValueChange = { extraCommInput = it },
            label = { Text("Extra Comm.") },
            placeholder = { Text("0.8") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_extra_comm")
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 4. Section: 🎨 Premium Themes matching Screenshot_20261018-004754.png
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "🎨", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Premium Themes",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3 Theme selection cards in a row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        ThemePillButton(
          name = "Neon Cyan",
          color = CyanPrimary,
          isSelected = settings.selectedTheme == "Neon Cyan",
          onClick = { viewModel.updateTheme("Neon Cyan") },
          modifier = Modifier.weight(1f)
        )

        ThemePillButton(
          name = "Mid Purple",
          color = PurplePrimary,
          isSelected = settings.selectedTheme == "Mid Purple",
          onClick = { viewModel.updateTheme("Mid Purple") },
          modifier = Modifier.weight(1f)
        )

        ThemePillButton(
          name = "Luxury Gold",
          color = GoldPrimary,
          isSelected = settings.selectedTheme == "Luxury Gold",
          onClick = { viewModel.updateTheme("Luxury Gold") },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 5. Section: 🔔 Notifications & Data matching Screenshot_20261018-004754.png
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "🔔", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Notifications & Data",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Auto Midnight Reminder Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_midnight_reminder"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Auto Midnight Reminder",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = currentThemeColors.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Get reminded automatically at midnight",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Switch(
            checked = settings.autoMidnightReminder,
            onCheckedChange = { isChecked -> viewModel.updateAutoMidnightReminder(isChecked) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = currentThemeColors.primary
            ),
            modifier = Modifier.testTag("switch_midnight_reminder")
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Export Data & Clear Data buttons side by side
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // [ EXPORT DATA ]
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("btn_export_data")
        ) {
          Text("EXPORT DATA", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        // [ CLEAR DATA ]
        Button(
          onClick = { showClearConfirmDialog = true },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFDE8E8),
            contentColor = Color(0xFFE02424)
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("btn_clear_data")
        ) {
          Text("CLEAR DATA", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 5. Section: APP VERSION & GITHUB SELF-UPDATE
      Text(
        text = "APP VERSION & SECURE SELF-UPDATE",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_self_update"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder()
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
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Order Tracker",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Version ${viewModel.updateManager.currentVersionName} (Build ${viewModel.updateManager.currentVersionCode})",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = currentThemeColors.primary
              )
            }

            Button(
              onClick = {
                viewModel.checkForUpdates()
              },
              enabled = !isCheckingUpdate,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = currentThemeColors.primary,
                contentColor = Color.Black
              ),
              modifier = Modifier.testTag("btn_check_updates")
            ) {
              if (isCheckingUpdate) {
                com.example.ui.components.ModernLoadingIndicator(size = 18.dp, strokeWidth = 2.dp)
              } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.SystemUpdate,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Check", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // What's New in v1.0.4 Section Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            )
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✨", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "What's New in v1.0.4",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = currentThemeColors.primary
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "• Google Sign-In & Cloud Sync: Dynamic Web Client ID and Credential Manager resolution\n• Instant 1-Tap Driver Login: Zero-friction authentication with automatic Cloud Sync\n• Live Shift Calculator: Real-time net balance preview before saving entries\n• Seamless in-app self-update engine with SHA-256 integrity verification",
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 6. Prominent [ SAVE CONFIGURATION ] Button with vibrant gradient
      Button(
        onClick = {
          viewModel.saveFullConfiguration(
            settings.copy(
              currency = currencyInput.trim().ifBlank { "K.D" },
              baseSalary = baseSalaryInput.toDoubleOrNull() ?: 0.0,
              targetOrders = targetOrdersInput.toIntOrNull() ?: 400,
              maxOrders = maxOrdersInput.toIntOrNull() ?: 400,
              normalComm = normalCommInput.toDoubleOrNull() ?: 0.5,
              extraComm = extraCommInput.toDoubleOrNull() ?: 0.8,
              dailyEarningsGoal = dailyEarningsGoalInput.toDoubleOrNull() ?: 25.0
            )
          )
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.Transparent,
          contentColor = Color.White
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(
            brush = Brush.horizontalGradient(
              listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
            )
          )
          .testTag("btn_save_configuration")
      ) {
        Text(
          text = "SAVE CONFIGURATION",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Clear data confirmation dialog
  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = { Text("Clear All Records?", fontWeight = FontWeight.Bold) },
      text = {
        Text("This will permanently remove all orders, tips, and gas expense records. This action cannot be undone.")
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
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun ThemePillButton(
  name: String,
  color: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clickable(onClick = onClick)
      .testTag("theme_btn_$name"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = name,
        color = if (isSelected) color else MaterialTheme.colorScheme.onSurface,
        fontSize = 13.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        textAlign = TextAlign.Center
      )
    }
  }
}
