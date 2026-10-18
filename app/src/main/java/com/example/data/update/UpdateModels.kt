package com.example.data.update

import android.net.Uri

data class UpdateManifest(
  val versionCode: Int,
  val versionName: String,
  val packageName: String,
  val downloadUrl: String,
  val fileSize: Long,
  val sha256: String,
  val certSha256: String? = null,
  val releaseNotes: String = "",
  val releaseDate: String = "",
  val isMandatory: Boolean = false
)

sealed interface UpdateCheckResult {
  data class UpdateAvailable(
    val manifest: UpdateManifest,
    val currentVersionCode: Int,
    val currentVersionName: String
  ) : UpdateCheckResult

  data class UpToDate(
    val currentVersionCode: Int,
    val currentVersionName: String
  ) : UpdateCheckResult

  data class Error(
    val message: String,
    val throwable: Throwable? = null
  ) : UpdateCheckResult
}

sealed interface DownloadStatus {
  object Idle : DownloadStatus
  data class Downloading(
    val progressPercent: Int,
    val bytesDownloaded: Long,
    val totalBytes: Long
  ) : DownloadStatus
  data class Verifying(val message: String) : DownloadStatus
  data class ReadyToInstall(val apkFileUri: Uri, val filePath: String) : DownloadStatus
  data class Failed(val error: String) : DownloadStatus
}
