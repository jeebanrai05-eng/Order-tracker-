package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.update.DownloadStatus
import com.example.data.update.UpdateManifest
import com.example.ui.theme.MaterialThemeColors
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.WarningRed

@Composable
fun UpdateDialog(
  manifest: UpdateManifest,
  currentVersionName: String,
  currentVersionCode: Int,
  downloadStatus: DownloadStatus,
  onStartDownload: () -> Unit,
  onInstall: (android.net.Uri) -> Unit,
  onDismiss: () -> Unit
) {
  val themeColors = MaterialThemeColors

  Dialog(
    onDismissRequest = {
      if (downloadStatus !is DownloadStatus.Downloading && !manifest.isMandatory) {
        onDismiss()
      }
    },
    properties = DialogProperties(
      dismissOnBackPress = downloadStatus !is DownloadStatus.Downloading && !manifest.isMandatory,
      dismissOnClickOutside = downloadStatus !is DownloadStatus.Downloading && !manifest.isMandatory,
      usePlatformDefaultWidth = false
    )
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp),
      shape = RoundedCornerShape(26.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      shadowElevation = 14.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(themeColors.primary.copy(alpha = 0.16f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = null,
                tint = themeColors.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "App Update Available",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Secure GitHub Release",
                fontSize = 11.sp,
                color = themeColors.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          if (!manifest.isMandatory && downloadStatus !is DownloadStatus.Downloading) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(28.dp)
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

        // Version comparison pill
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
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
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "What's New in this Release:",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = manifest.releaseNotes.ifBlank { "Performance improvements and bug fixes." },
              fontSize = 12.sp,
              lineHeight = 18.sp,
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
                  text = "Size: $mbSize • SHA-256 Verified",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Download & Verification Status
        when (downloadStatus) {
          is DownloadStatus.Idle -> {
            Button(
              onClick = onStartDownload,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
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
                fontSize = 14.sp,
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
                  text = "Downloading update...",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "${downloadStatus.progressPercent}%",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = themeColors.primary
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              LinearProgressIndicator(
                progress = { downloadStatus.progressPercent / 100f },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = themeColors.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
              )
              Spacer(modifier = Modifier.height(6.dp))
              val downloadedMb = String.format("%.1f MB", downloadStatus.bytesDownloaded / (1024.0 * 1024.0))
              val totalMb = if (downloadStatus.totalBytes > 0) String.format("%.1f MB", downloadStatus.totalBytes / (1024.0 * 1024.0)) else "Unknown"
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
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = ProfitGreen,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
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
                  .height(48.dp)
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
                  fontSize = 14.sp,
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
                onClick = onStartDownload,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp),
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

        if (!manifest.isMandatory && downloadStatus !is DownloadStatus.Downloading && downloadStatus !is DownloadStatus.ReadyToInstall) {
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
              text = "Remind Me Later",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
