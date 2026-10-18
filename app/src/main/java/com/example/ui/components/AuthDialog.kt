package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AuthUiState
import com.example.ui.MainViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.CyanVariant
import com.example.ui.theme.MaterialThemeColors
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.WarningRed

/**
 * Modern High-End Login & Account Management Modal.
 * Provides:
 * 1. Direct one-tap Google Sign-In with official Google branding.
 * 2. Manual Sign-In and Sign-Up tabs with email/password validation.
 * 3. Password reset support.
 * 4. Logged-in profile overview with sync and sign-out buttons.
 */
@Composable
fun AuthDialog(
  viewModel: MainViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val authState by viewModel.authState.collectAsState()
  val isSyncing by viewModel.isSyncing.collectAsState()
  val themeColors = MaterialThemeColors

  var isSignUpTab by remember { mutableStateOf(false) }
  var emailInput by remember { mutableStateOf("") }
  var passwordInput by remember { mutableStateOf("") }
  var confirmPasswordInput by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showResetSuccess by remember { mutableStateOf(false) }
  var showSha1Info by remember { mutableStateOf(false) }

  val authErrorFromVm by viewModel.authError.collectAsState()
  androidx.compose.runtime.LaunchedEffect(authErrorFromVm) {
    if (!authErrorFromVm.isNullOrBlank()) {
      errorMessage = authErrorFromVm
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 24.dp)
        .imePadding(),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      shadowElevation = 12.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Row: Dismiss Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(themeColors.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "SECURE CLOUD BACKUP",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = themeColors.primary,
              letterSpacing = 1.sp
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (val currentAuth = authState) {
          is AuthUiState.LoggedIn -> {
            // --- LOGGED IN PROFILE VIEW ---
            Box(
              modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(themeColors.primary, themeColors.variant)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              val initial = currentAuth.displayName.firstOrNull()?.uppercase()
                ?: currentAuth.email.firstOrNull()?.uppercase()
                ?: "U"
              Text(
                text = initial,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = if (currentAuth.displayName.isNotBlank()) currentAuth.displayName else "Delivery Partner",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Text(
              text = currentAuth.email,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Verified Status Banner
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(ProfitGreen.copy(alpha = 0.12f))
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = ProfitGreen,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Cloud Sync Active",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = ProfitGreen
                )
                Text(
                  text = "Your daily orders, tips, gas logs, and salary targets are backed up safely.",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isSyncing) {
              ModernLoadingIndicator(
                size = 48.dp,
                message = "Synchronizing with Cloud..."
              )
            } else {
              // Sync Now Button
              Button(
                onClick = { viewModel.syncCloudData() },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("btn_sync_now_modal"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = themeColors.primary,
                  contentColor = Color.Black
                )
              ) {
                Icon(
                  imageVector = Icons.Default.Sync,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Sync Now with Cloud",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Sign Out Button
              OutlinedButton(
                onClick = {
                  viewModel.logout()
                  onDismiss()
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("btn_logout_modal"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, WarningRed.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningRed)
              ) {
                Text(
                  text = "Log Out of Account",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          is AuthUiState.LoggedOut -> {
            // --- NOT LOGGED IN: DIRECT GOOGLE + MANUAL SIGN-IN / UP ---
            Text(
              text = if (isSignUpTab) "Create Driver Account" else "Welcome Back",
              fontSize = 24.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Never lose your delivery records, tips, or monthly earnings.",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isSyncing) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
              ) {
                ModernLoadingCard(
                  message = "Signing in...",
                  subMessage = "Connecting with Google & Cloud storage"
                )
              }
            } else {
              // 1. Direct Google Sign-In Button
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    errorMessage = null
                    viewModel.signInWithGoogle(context)
                  }
                  .testTag("btn_modal_google_signin"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  GoogleLogoIcon(size = 22.dp)
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

              // 2. Instant 1-Tap Driver Login (Guaranteed immediate Firebase sync, no Google Play setup needed)
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    errorMessage = null
                    viewModel.quickDriverLogin()
                  }
                  .testTag("btn_modal_quick_driver_signin"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = themeColors.primary.copy(alpha = 0.12f)
                ),
                border = BorderStroke(1.5.dp, themeColors.primary.copy(alpha = 0.45f))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  Text(text = "⚡", fontSize = 18.sp)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "1-Tap Instant Driver Login",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Instant cloud backup (bypasses Google device setup)",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // 3. Fix Google Error 16 / SHA-1 Information Helper
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { showSha1Info = !showSha1Info }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (showSha1Info) "Hide Google Error 16 Fix ▲" else "ℹ️ Google Sign-In Error 16 Fix & SHA-1 ▼",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = themeColors.primary
                )
              }

              AnimatedVisibility(visible = showSha1Info) {
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                  )
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                      text = "Why error [16] happens:",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "• Google Play requires your account on the device.\n• In Firebase Console (order-tracking-ec763), register this Debug SHA-1 for package com.ordercom:",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                    ) {
                      Text(
                        text = "BD:91:4A:89:15:4C:7E:39:E7:16:41:43:28:30:37:E7:A8:0D:4F:93",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.primary
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Divider: OR EMAIL
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                Text(
                  text = "  OR WITH EMAIL  ",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  letterSpacing = 0.8.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
              }

              Spacer(modifier = Modifier.height(18.dp))

              // Segmented Tab Switcher (Sign In vs Create Account)
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                  .padding(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (!isSignUpTab) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable {
                      isSignUpTab = false
                      errorMessage = null
                    }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "Sign In",
                    fontSize = 13.sp,
                    fontWeight = if (!isSignUpTab) FontWeight.Bold else FontWeight.Medium,
                    color = if (!isSignUpTab) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (isSignUpTab) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable {
                      isSignUpTab = true
                      errorMessage = null
                    }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "Create Account",
                    fontSize = 13.sp,
                    fontWeight = if (isSignUpTab) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSignUpTab) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              // Email Input
              OutlinedTextField(
                value = emailInput,
                onValueChange = {
                  emailInput = it
                  errorMessage = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_modal_email"),
                label = { Text("Email Address") },
                placeholder = { Text("driver@example.com") },
                singleLine = true,
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Password Input
              OutlinedTextField(
                value = passwordInput,
                onValueChange = {
                  passwordInput = it
                  errorMessage = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_modal_password"),
                label = { Text("Password") },
                placeholder = { Text("••••••••") },
                singleLine = true,
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                  IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                      imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                      contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                  }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(14.dp)
              )

              // Confirm Password (if Sign Up)
              if (isSignUpTab) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                  value = confirmPasswordInput,
                  onValueChange = {
                    confirmPasswordInput = it
                    errorMessage = null
                  },
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_modal_confirm_password"),
                  label = { Text("Confirm Password") },
                  placeholder = { Text("••••••••") },
                  singleLine = true,
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  },
                  visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                  shape = RoundedCornerShape(14.dp)
                )
              }

              // Error display
              if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = WarningRed.copy(alpha = 0.08f)
                  ),
                  border = BorderStroke(1.dp, WarningRed.copy(alpha = 0.35f))
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                      text = errorMessage ?: "",
                      color = WarningRed,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.SemiBold
                    )

                    if (errorMessage?.contains("Google", ignoreCase = true) == true ||
                        errorMessage?.contains("SHA-1", ignoreCase = true) == true ||
                        errorMessage?.contains("credential", ignoreCase = true) == true) {
                      Spacer(modifier = Modifier.height(10.dp))
                      Button(
                        onClick = {
                          errorMessage = null
                          viewModel.quickDriverLogin()
                        },
                        modifier = Modifier
                          .fillMaxWidth()
                          .height(40.dp)
                          .testTag("btn_error_quick_login"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                          containerColor = themeColors.primary,
                          contentColor = Color.Black
                        )
                      ) {
                        Text(
                          text = "⚡ Click Here: 1-Tap Driver Login",
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }
                }
              }

              if (showResetSuccess) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Password reset email sent! Check your inbox.",
                  color = ProfitGreen,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.fillMaxWidth()
                )
              }

              // Forgot password link (only for Sign In)
              if (!isSignUpTab) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End
                ) {
                  TextButton(
                    onClick = {
                      if (emailInput.isBlank()) {
                        errorMessage = "Please enter your email to reset password"
                      } else {
                        viewModel.forgotPassword(emailInput)
                        showResetSuccess = true
                      }
                    }
                  ) {
                    Text(
                      text = "Forgot Password?",
                      fontSize = 12.sp,
                      color = themeColors.primary,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              } else {
                Spacer(modifier = Modifier.height(16.dp))
              }

              // Primary Action Button (Sign In / Create Account)
              Button(
                onClick = {
                  if (emailInput.isBlank() || passwordInput.isBlank()) {
                    errorMessage = "Please enter both email and password"
                    return@Button
                  }
                  if (isSignUpTab) {
                    if (passwordInput != confirmPasswordInput) {
                      errorMessage = "Passwords do not match"
                      return@Button
                    }
                    if (passwordInput.length < 6) {
                      errorMessage = "Password must be at least 6 characters"
                      return@Button
                    }
                    viewModel.signup(emailInput, passwordInput)
                  } else {
                    viewModel.login(emailInput, passwordInput)
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("btn_modal_auth_submit"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = themeColors.primary,
                  contentColor = Color.Black
                )
              ) {
                Text(
                  text = if (isSignUpTab) "Create Driver Account" else "Sign In",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
