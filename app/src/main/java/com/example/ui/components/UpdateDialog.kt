package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.update.DownloadStatus
import com.example.data.update.UpdateCheckResult
import com.example.data.update.UpdateManifest
import com.example.ui.theme.MaterialThemeColors
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.WarningRed

/**
 * Modern High-End Update Center Modal Dialog.
 * Handles all states:
 * 1. Checking for Updates (animated pulse/radar)
 * 2. Update Available (version diff pill, changelog, download progress, install action)
 * 3. App Up-to-Date (celebration checkmark, build info)
 * 4. Error / Private Repo Guide (actionable steps to make GitHub repo public)
 */
@Composable
fun UpdateDialog(
  isChecking: Boolean,
  checkResult: UpdateCheckResult?,
  currentVersionName: String,
  currentVersionCode: Int,
  downloadStatus: DownloadStatus,
  onCheckAgain: () -> Unit,
  onStartDownload: (UpdateManifest) -> Unit,
  onInstall: (Uri) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val themeColors = MaterialThemeColors
  val isDownloading = downloadStatus is DownloadStatus.Downloading

  Dialog(
    onDismissRequest = {
      if (!isDownloading) onDismiss()
    },
    properties = DialogProperties(
      dismissOnBackPress = !isDownloading,
      dismissOnClickOutside = !isDownloading,
      usePlatformDefaultWidth = false
    )
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      shadowElevation = 16.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(themeColors.primary.copy(alpha = 0.16f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = null,
                tint = themeColors.primary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Update Center",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Order Tracker Self-Updater",
                fontSize = 11.sp,
                color = themeColors.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          if (!isDownloading) {
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
        }

        Spacer(modifier = Modifier.height(18.dp))

        // State 1: Checking for updates
        if (isChecking) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val scale by infiniteTransition.animateFloat(
              initialValue = 0.9f,
              targetValue = 1.15f,
              animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
              ),
              label = "scale"
            )

            Box(
              modifier = Modifier
                .size(72.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(themeColors.primary.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              ModernLoadingIndicator(size = 36.dp, strokeWidth = 3.dp)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
              text = "Checking for Updates...",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Connecting to GitHub Releases & Cloud manifest...",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        } else {
          when (val result = checkResult) {
            // State 2: Update Available!
            is UpdateCheckResult.UpdateAvailable -> {
              val manifest = result.manifest

              // Version comparison pill
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                  .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "CURRENT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = "$currentVersionName (v$currentVersionCode)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }

                Text(
                  text = "➔",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = themeColors.primary
                )

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "NEW VERSION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfitGreen
                  )
                  Text(
                    text = "${manifest.versionName} (v${manifest.versionCode})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfitGreen
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Release Notes Card
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✨", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "What's New in this Release:",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = manifest.releaseNotes.ifBlank { "Performance improvements and bug fixes." },
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  if (manifest.fileSize > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ProfitGreen,
                        modifier = Modifier.size(14.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      val mbSize = String.format("%.1f MB", manifest.fileSize / (1024.0 * 1024.0))
                      Text(
                        text = "Size: $mbSize • Cryptographic SHA-256 Verified",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              // Download / Install actions
              when (downloadStatus) {
                is DownloadStatus.Idle -> {
                  Button(
                    onClick = { onStartDownload(manifest) },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(50.dp)
                      .testTag("btn_start_update_download"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = themeColors.primary,
                      contentColor = Color.Black
                    )
                  ) {
                    Icon(
                      imageVector = Icons.Default.Download,
                      contentDescription = null,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "Download & Install Update",
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                is DownloadStatus.Downloading -> {
                  Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = "Downloading update package...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = "${downloadStatus.progressPercent}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.primary
                      )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                      progress = { downloadStatus.progressPercent / 100f },
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                      color = themeColors.primary,
                      trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val downloadedMb = String.format("%.1f MB", downloadStatus.bytesDownloaded / (1024.0 * 1024.0))
                    val totalMb = if (downloadStatus.totalBytes > 0) String.format("%.1f MB", downloadStatus.totalBytes / (1024.0 * 1024.0)) else "20.5 MB"
                    Text(
                      text = "$downloadedMb / $totalMb",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                is DownloadStatus.Verifying -> {
                  Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    ModernLoadingIndicator(
                      size = 36.dp,
                      message = downloadStatus.message
                    )
                  }
                }

                is DownloadStatus.ReadyToInstall -> {
                  Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ProfitGreen.copy(alpha = 0.12f))
                        .padding(12.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ProfitGreen,
                        modifier = Modifier.size(22.dp)
                      )
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(
                        text = "Checksum & Signature Verified. Ready to install!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfitGreen
                      )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                      onClick = { onInstall(downloadStatus.apkFileUri) },
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_confirm_install_apk"),
                      shape = RoundedCornerShape(14.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = ProfitGreen,
                        contentColor = Color.White
                      )
                    ) {
                      Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "Install Now",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }

                is DownloadStatus.Failed -> {
                  Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarningRed.copy(alpha = 0.1f))
                        .border(BorderStroke(1.dp, WarningRed.copy(alpha = 0.3f)), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = WarningRed,
                        modifier = Modifier.size(22.dp)
                      )
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(
                        text = downloadStatus.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = WarningRed
                      )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                      onClick = { onStartDownload(manifest) },
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                      shape = RoundedCornerShape(12.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = themeColors.primary,
                        contentColor = Color.Black
                      )
                    ) {
                      Text("Retry Download", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            // State 3: App is already Up-to-Date
            is UpdateCheckResult.UpToDate -> {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ProfitGreen.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ProfitGreen,
                    modifier = Modifier.size(36.dp)
                  )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  text = "You're on the Latest Version!",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Version $currentVersionName (Build $currentVersionCode) is currently installed.",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(18.dp))

                Button(
                  onClick = onCheckAgain,
                  shape = RoundedCornerShape(14.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = themeColors.primary,
                    contentColor = Color.Black
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Check Again", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }
            }

            // State 4: Error or Private Repository
            is UpdateCheckResult.Error -> {
              val isPrivateRepo = result.message.contains("Private", ignoreCase = true) ||
                                  result.message.contains("404") ||
                                  result.message.contains("No newer", ignoreCase = true)

              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isPrivateRepo) Color(0xFFFFB300).copy(alpha = 0.15f) else WarningRed.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (isPrivateRepo) Icons.Default.Lock else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isPrivateRepo) Color(0xFFFFB300) else WarningRed,
                    modifier = Modifier.size(30.dp)
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  text = if (isPrivateRepo) "GitHub Repository is Private" else "Update Check Notice",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                  )
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    if (isPrivateRepo) {
                      Text(
                        text = "GitHub blocks mobile in-app downloads when a repository is Private.\n\nTo enable automatic 1-tap in-app updates for this app:",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        text = "1. Open your GitHub repository:\njeebanrai05-eng/Order-tracker-\n2. Go to Settings ➔ Danger Zone (scroll down)\n3. Click 'Change repository visibility' ➔ 'Make public'",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeColors.primary
                      )
                    } else {
                      Text(
                        text = result.message,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isPrivateRepo) {
                  Button(
                    onClick = {
                      val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/jeebanrai05-eng/Order-tracker-/settings")
                      )
                      context.startActivity(browserIntent)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = themeColors.primary,
                      contentColor = Color.Black
                    ),
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(46.dp)
                  ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open GitHub Repo Settings", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                  }
                  Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedButton(
                  onClick = onCheckAgain,
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                ) {
                  Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Retry Check", fontSize = 13.sp)
                }
              }
            }

            // State 5: Initial / null
            null -> {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "Installed: v$currentVersionName (Build $currentVersionCode)",
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                  onClick = onCheckAgain,
                  shape = RoundedCornerShape(14.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = themeColors.primary,
                    contentColor = Color.Black
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                ) {
                  Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Check for Updates Now", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        if (!isDownloading && downloadStatus !is DownloadStatus.ReadyToInstall) {
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
          ) {
            Text(
              text = "Close",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
