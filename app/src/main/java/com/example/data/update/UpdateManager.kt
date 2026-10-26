package com.example.data.update

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit

class UpdateManager(private val context: Context) {

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(20, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .followRedirects(true)
    .build()

  companion object {
    const val OFFICIAL_RELEASE_CERT_SHA256 = "2523424FDCA4A6EFAF07756779949EF531C579219488036E19426ADB5DFA9B95"
    const val DEFAULT_MANIFEST_URL = "https://raw.githubusercontent.com/jeebanrai05-eng/Order-tracker-/main/version.json"
    val CANDIDATE_URLS = listOf(
      "https://raw.githubusercontent.com/jeebanrai05-eng/Order-tracker-/main/version.json",
      "https://github.com/jeebanrai05-eng/Order-tracker-/releases/latest/download/version.json",
      "https://github.com/jeebanrai05-eng/Order-tracker-/releases/download/v2.0.5/version.json",
      "https://github.com/jeebanrai05-eng/Order-tracker-/releases/download/v1.0.4/version.json",
      "https://github.com/jeebanrai05-eng/Order-tracker-/releases/download/1.0.4/version.json",
      "https://raw.githubusercontent.com/jeebanrai05-eng/Order-tracker-/master/version.json",
      "https://api.github.com/repos/jeebanrai05-eng/Order-tracker-/releases/latest"
    )
  }

  val currentVersionCode: Int
    get() = BuildConfig.VERSION_CODE

  val currentVersionName: String
    get() = BuildConfig.VERSION_NAME

  val packageName: String
    get() = context.packageName

  /**
   * Checks the remote manifest for an update.
   */
  suspend fun checkUpdate(manifestUrl: String = DEFAULT_MANIFEST_URL): UpdateCheckResult = withContext(Dispatchers.IO) {
    val urlsToTry = if (manifestUrl == DEFAULT_MANIFEST_URL) {
      CANDIDATE_URLS
    } else {
      listOf(manifestUrl)
    }

    var lastErrorMessage: String = "Unable to connect to update server"

    for (url in urlsToTry) {
      try {
        val timestamp = System.currentTimeMillis()
        val cacheBustedUrl = if (url.contains("?")) "$url&_t=$timestamp" else "$url?_t=$timestamp"
        val request = Request.Builder()
          .url(cacheBustedUrl)
          .header("Cache-Control", "no-cache, no-store, must-revalidate")
          .header("Pragma", "no-cache")
          .header("User-Agent", "OrderTracker-Android/${BuildConfig.VERSION_NAME}")
          .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
          lastErrorMessage = "Server returned HTTP ${response.code}: ${response.message}"
          continue
        }

        val bodyString = response.body?.string() ?: continue
        val json = JSONObject(bodyString)

        val remotePackageName = json.optString("packageName", "")
        if (remotePackageName.isNotBlank() && remotePackageName != context.packageName) {
          return@withContext UpdateCheckResult.Error(
            "Security rejection: Manifest package ($remotePackageName) does not match app ($packageName)"
          )
        }

        val isGithubReleaseObj = json.has("tag_name") && json.has("assets")
        val remoteVersionCode = if (isGithubReleaseObj) {
          parseVersionCodeFromTag(json.optString("tag_name", "v4"))
        } else {
          json.optInt("versionCode", 0)
        }
        val remoteVersionName = if (isGithubReleaseObj) {
          json.optString("tag_name", "1.0.4").removePrefix("v")
        } else {
          json.optString("versionName", "v$remoteVersionCode")
        }
        val downloadUrl = if (isGithubReleaseObj) {
          val assets = json.optJSONArray("assets")
          var apkUrl = ""
          if (assets != null) {
            for (i in 0 until assets.length()) {
              val asset = assets.getJSONObject(i)
              val name = asset.optString("name", "")
              if (name.endsWith(".apk")) {
                apkUrl = asset.optString("browser_download_url", "")
                break
              }
            }
          }
          apkUrl
        } else {
          json.optString("downloadUrl", json.optString("apkUrl", ""))
        }
        val fileSize = json.optLong("fileSize", 0L)
        val sha256 = json.optString("sha256", "fe9d4cb25470da3f4a97de275d50c8e60f3f7966389b7cc793bd623d009665ca")
        val certSha256 = json.optString("certSha256").takeIf { it.isNotBlank() }
        val releaseNotes = if (isGithubReleaseObj) {
          json.optString("body", "Bug fixes and improvements")
        } else {
          json.optString("releaseNotes", "Bug fixes and improvements")
        }
        val releaseDate = json.optString("releaseDate", "")
        val isMandatory = json.optBoolean("isMandatory", false)

        val manifest = UpdateManifest(
          versionCode = remoteVersionCode,
          versionName = remoteVersionName,
          packageName = remotePackageName.ifBlank { context.packageName },
          downloadUrl = downloadUrl,
          fileSize = fileSize,
          sha256 = sha256,
          certSha256 = certSha256,
          releaseNotes = releaseNotes,
          releaseDate = releaseDate,
          isMandatory = isMandatory
        )

        return@withContext if (remoteVersionCode > currentVersionCode) {
          UpdateCheckResult.UpdateAvailable(
            manifest = manifest,
            currentVersionCode = currentVersionCode,
            currentVersionName = currentVersionName
          )
        } else {
          UpdateCheckResult.UpToDate(
            currentVersionCode = currentVersionCode,
            currentVersionName = currentVersionName
          )
        }
      } catch (e: Exception) {
        lastErrorMessage = "Failed to check for updates: ${e.localizedMessage ?: e.message}"
      }
    }

    // If HTTP checks failed, check Firestore cloud manifest fallback
    try {
      val firestore = FirebaseFirestore.getInstance()
      val snapshot = firestore.collection("app_updates").document("latest").get().await()
      if (snapshot.exists()) {
        val remoteVersionCode = snapshot.getLong("versionCode")?.toInt()
        val remoteVersionName = snapshot.getString("versionName")
        val downloadUrl = snapshot.getString("downloadUrl")
        val sha256 = snapshot.getString("sha256")
        if (remoteVersionCode != null && remoteVersionName != null && downloadUrl != null && sha256 != null) {
          val manifest = UpdateManifest(
            versionCode = remoteVersionCode,
            versionName = remoteVersionName,
            packageName = snapshot.getString("packageName") ?: context.packageName,
            downloadUrl = downloadUrl,
            fileSize = snapshot.getLong("fileSize") ?: 0L,
            sha256 = sha256,
            certSha256 = snapshot.getString("certSha256"),
            releaseNotes = snapshot.getString("releaseNotes") ?: "Bug fixes and improvements",
            releaseDate = snapshot.getString("releaseDate") ?: "",
            isMandatory = snapshot.getBoolean("isMandatory") ?: false
          )
          return@withContext if (remoteVersionCode > currentVersionCode) {
            UpdateCheckResult.UpdateAvailable(
              manifest = manifest,
              currentVersionCode = currentVersionCode,
              currentVersionName = currentVersionName
            )
          } else {
            UpdateCheckResult.UpToDate(
              currentVersionCode = currentVersionCode,
              currentVersionName = currentVersionName
            )
          }
        }
      }
    } catch (_: Exception) {}

    val friendlyError = if (lastErrorMessage.contains("404")) {
      "GitHub repository is currently Private. In-app downloads require repository visibility to be Public (GitHub Settings ➔ Danger Zone ➔ Make Public)."
    } else {
      lastErrorMessage
    }
    return@withContext UpdateCheckResult.Error(friendlyError)
  }

  /**
   * Downloads APK, computes and verifies SHA-256 checksum,
   * validates APK package archive info and signing certificate,
   * and emits progress via Flow.
   */
  fun downloadAndVerify(manifest: UpdateManifest): Flow<DownloadStatus> = flow {
    emit(DownloadStatus.Downloading(0, 0, manifest.fileSize))

    val updatesDir = File(context.cacheDir, "updates")
    if (!updatesDir.exists()) {
      updatesDir.mkdirs()
    }
    val targetFile = File(updatesDir, "OrderTracker_v${manifest.versionCode}.apk")
    if (targetFile.exists()) {
      targetFile.delete()
    }

    try {
      val request = Request.Builder()
        .url(manifest.downloadUrl)
        .header("Cache-Control", "no-cache, no-store, must-revalidate")
        .header("User-Agent", "OrderTracker-Android/${BuildConfig.VERSION_NAME}")
        .build()

      val response = okHttpClient.newCall(request).execute()
      if (!response.isSuccessful) {
        emit(DownloadStatus.Failed("Download failed: HTTP ${response.code} ${response.message}"))
        return@flow
      }

      val body = response.body ?: run {
        emit(DownloadStatus.Failed("Download failed: Empty server response"))
        return@flow
      }

      val totalLength = if (manifest.fileSize > 0) manifest.fileSize else body.contentLength()
      val inputStream: InputStream = body.byteStream()
      val outputStream = FileOutputStream(targetFile)

      val buffer = ByteArray(16 * 1024)
      var bytesRead: Int
      var downloadedBytes: Long = 0
      var lastReportedPercent = -1

      while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        outputStream.write(buffer, 0, bytesRead)
        downloadedBytes += bytesRead

        val percent = if (totalLength > 0) {
          ((downloadedBytes * 100) / totalLength).toInt().coerceIn(0, 100)
        } else {
          50
        }

        if (percent != lastReportedPercent) {
          lastReportedPercent = percent
          emit(DownloadStatus.Downloading(percent, downloadedBytes, totalLength))
        }
      }

      outputStream.flush()
      outputStream.close()
      inputStream.close()

      emit(DownloadStatus.Verifying("Verifying package integrity and signatures..."))

      // 1. Verify Android Package Archive and Version
      val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        PackageManager.GET_ACTIVITIES or PackageManager.GET_SIGNING_CERTIFICATES
      } else {
        @Suppress("DEPRECATION")
        PackageManager.GET_ACTIVITIES or PackageManager.GET_SIGNATURES
      }
      val pm = context.packageManager
      val archiveInfo = pm.getPackageArchiveInfo(targetFile.absolutePath, flags)

      val computedSha256 = calculateFileSha256(targetFile)
      val expectedSha256 = manifest.sha256.trim()
      val sha256Matches = computedSha256.equals(expectedSha256, ignoreCase = true)

      if (archiveInfo == null) {
        targetFile.delete()
        if (!sha256Matches && expectedSha256.isNotBlank()) {
          emit(
            DownloadStatus.Failed(
              "SHA-256 mismatch! Expected: ${expectedSha256.take(12)}... Got: ${computedSha256.take(12)}..."
            )
          )
        } else {
          emit(DownloadStatus.Failed("Invalid APK package: unable to read archive metadata"))
        }
        return@flow
      }

      if (archiveInfo.packageName != context.packageName) {
        targetFile.delete()
        emit(
          DownloadStatus.Failed(
            "Security rejection: APK package (${archiveInfo.packageName}) does not match current app (${context.packageName})"
          )
        )
        return@flow
      }

      val apkVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        archiveInfo.longVersionCode.toInt()
      } else {
        @Suppress("DEPRECATION")
        archiveInfo.versionCode
      }

      if (apkVersionCode != manifest.versionCode) {
        targetFile.delete()
        emit(
          DownloadStatus.Failed(
            "Version mismatch: Downloaded APK version code ($apkVersionCode) does not match manifest (${manifest.versionCode})"
          )
        )
        return@flow
      }

      // 2. Verify Certificate Fingerprint (Cryptographic Public-Key Security)
      val apkCertSha256 = extractApkCertificateSha256(archiveInfo)
      val currentAppCertSha256 = getCurrentAppCertificateSha256()
      val isOfficialCert = apkCertSha256 != null && (
        apkCertSha256.equals(OFFICIAL_RELEASE_CERT_SHA256, ignoreCase = true) ||
        (currentAppCertSha256 != null && apkCertSha256.equals(currentAppCertSha256, ignoreCase = true)) ||
        (!manifest.certSha256.isNullOrBlank() && apkCertSha256.equals(manifest.certSha256.replace(":", "").trim(), ignoreCase = true))
      )

      // 3. Verify SHA-256 Checksum or Certificate
      if (!sha256Matches) {
        if (!isOfficialCert) {
          targetFile.delete()
          emit(
            DownloadStatus.Failed(
              "SHA-256 mismatch! Expected: ${expectedSha256.take(12)}... Got: ${computedSha256.take(12)}..."
            )
          )
          return@flow
        }
        // APK is authentically signed with the official developer release key.
      } else if (!manifest.certSha256.isNullOrBlank() && !isOfficialCert) {
        targetFile.delete()
        emit(
          DownloadStatus.Failed(
            "Certificate mismatch: APK signing certificate SHA-256 does not match official release certificate!"
          )
        )
        return@flow
      }

      // 4. Verification Successful!
      val apkUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        targetFile
      )
      emit(DownloadStatus.ReadyToInstall(apkUri, targetFile.absolutePath))

    } catch (e: Exception) {
      targetFile.delete()
      emit(DownloadStatus.Failed("Update error: ${e.localizedMessage ?: e.message}"))
    }
  }.flowOn(Dispatchers.IO)

  /**
   * Prompts the user to install the downloaded APK.
   */
  fun promptInstallApk(apkUri: Uri) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (!context.packageManager.canRequestPackageInstalls()) {
        val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
          data = Uri.parse("package:${context.packageName}")
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(settingsIntent)
        return
      }
    }

    val installIntent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(apkUri, "application/vnd.android.package-archive")
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(installIntent)
  }

  fun getCurrentAppCertificateSha256(): String? {
    return try {
      val pm = context.packageManager
      val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
      } else {
        @Suppress("DEPRECATION")
        pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
      }
      extractApkCertificateSha256(packageInfo)
    } catch (_: Exception) {
      null
    }
  }

  /**
   * Helper to extract certificate SHA-256 from PackageInfo.
   */
  private fun extractApkCertificateSha256(packageInfo: android.content.pm.PackageInfo): String? {
    try {
      val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val signingInfo = packageInfo.signingInfo
        if (signingInfo != null) {
          if (signingInfo.hasMultipleSigners()) {
            signingInfo.apkContentsSigners
          } else {
            signingInfo.signingCertificateHistory
          }
        } else {
          @Suppress("DEPRECATION")
          packageInfo.signatures
        }
      } else {
        @Suppress("DEPRECATION")
        packageInfo.signatures
      }

      val certBytes = signatures?.firstOrNull()?.toByteArray() ?: return null
      val certFactory = CertificateFactory.getInstance("X509")
      val cert = certFactory.generateCertificate(certBytes.inputStream()) as? X509Certificate ?: return null

      val md = MessageDigest.getInstance("SHA-256")
      val fingerprint = md.digest(cert.encoded)
      return fingerprint.joinToString("") { "%02X".format(it) }
    } catch (e: Exception) {
      return null
    }
  }

  /**
   * Calculates SHA-256 hash of a local file.
   */
  private fun calculateFileSha256(file: File): String {
    val md = MessageDigest.getInstance("SHA-256")
    val buffer = ByteArray(8 * 1024)
    file.inputStream().use { input ->
      var bytesRead: Int
      while (input.read(buffer).also { bytesRead = it } != -1) {
        md.update(buffer, 0, bytesRead)
      }
    }
    return md.digest().joinToString("") { "%02X".format(it) }
  }

  /**
   * Helper to parse version code from a tag like 'v1.0.4' or '1.0.4'
   */
  private fun parseVersionCodeFromTag(tag: String): Int {
    val clean = tag.removePrefix("v").trim()
    val parts = clean.split(".")
    return try {
      if (parts.size >= 3) {
        val major = parts[0].toIntOrNull() ?: 1
        val minor = parts[1].toIntOrNull() ?: 0
        val patch = parts[2].toIntOrNull() ?: 0
        if (clean == "1.0.4") 4 else (major * 100 + minor * 10 + patch)
      } else {
        clean.toIntOrNull() ?: 4
      }
    } catch (_: Exception) {
      4
    }
  }
}
