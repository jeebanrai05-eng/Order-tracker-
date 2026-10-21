package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.update.DownloadStatus
import com.example.data.update.UpdateCheckResult
import com.example.data.update.UpdateManager
import com.example.data.update.UpdateManifest
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.net.InetSocketAddress
import java.security.MessageDigest

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UpdateManagerTest {

  private lateinit var context: Context
  private lateinit var updateManager: UpdateManager
  private lateinit var server: HttpServer
  private var serverPort: Int = 0

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    updateManager = UpdateManager(context)
    server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.start()
    serverPort = server.address.port
  }

  @After
  fun tearDown() {
    server.stop(0)
  }

  private fun url(path: String) = "http://127.0.0.1:$serverPort$path"

  @Test
  fun `checkUpdate detects available new version when remote version is higher`() = runBlocking {
    val higherVersionCode = updateManager.currentVersionCode + 1
    val manifestJson = JSONObject().apply {
      put("versionCode", higherVersionCode)
      put("versionName", "2.0")
      put("packageName", context.packageName)
      put("downloadUrl", url("/app-release.apk"))
      put("fileSize", 1024L)
      put("sha256", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
      put("certSha256", "EE167487CDF9505F6E86361C93536BF4BCCBD85F63EF7B7910DC12489D12DE3A")
      put("releaseNotes", "New in-app features and optimizations")
      put("releaseDate", "2026-10-18")
      put("isMandatory", false)
    }.toString()

    server.createContext("/version.json") { exchange ->
      val bytes = manifestJson.toByteArray()
      exchange.responseHeaders.set("Content-Type", "application/json")
      exchange.sendResponseHeaders(200, bytes.size.toLong())
      exchange.responseBody.use { it.write(bytes) }
    }

    val result = updateManager.checkUpdate(url("/version.json"))

    assertTrue(result is UpdateCheckResult.UpdateAvailable)
    val update = result as UpdateCheckResult.UpdateAvailable
    assertEquals(higherVersionCode, update.manifest.versionCode)
    assertEquals("2.0", update.manifest.versionName)
    assertEquals(context.packageName, update.manifest.packageName)
    assertEquals("New in-app features and optimizations", update.manifest.releaseNotes)
  }

  @Test
  fun `checkUpdate detects up to date when remote version is equal or lower`() = runBlocking {
    val manifestJson = JSONObject().apply {
      put("versionCode", updateManager.currentVersionCode)
      put("versionName", updateManager.currentVersionName)
      put("packageName", context.packageName)
      put("downloadUrl", url("/app-release.apk"))
      put("fileSize", 500L)
      put("sha256", "dummy")
    }.toString()

    server.createContext("/version-current.json") { exchange ->
      val bytes = manifestJson.toByteArray()
      exchange.responseHeaders.set("Content-Type", "application/json")
      exchange.sendResponseHeaders(200, bytes.size.toLong())
      exchange.responseBody.use { it.write(bytes) }
    }

    val result = updateManager.checkUpdate(url("/version-current.json"))

    assertTrue(result is UpdateCheckResult.UpToDate)
    val upToDate = result as UpdateCheckResult.UpToDate
    assertEquals(updateManager.currentVersionCode, upToDate.currentVersionCode)
  }

  @Test
  fun `checkUpdate rejects manifest when remote package name does not match app`() = runBlocking {
    val manifestJson = JSONObject().apply {
      put("versionCode", 99)
      put("versionName", "9.9")
      put("packageName", "com.malicious.fakeapp")
      put("downloadUrl", url("/evil.apk"))
      put("fileSize", 500L)
      put("sha256", "abc")
    }.toString()

    server.createContext("/version-fake.json") { exchange ->
      val bytes = manifestJson.toByteArray()
      exchange.responseHeaders.set("Content-Type", "application/json")
      exchange.sendResponseHeaders(200, bytes.size.toLong())
      exchange.responseBody.use { it.write(bytes) }
    }

    val result = updateManager.checkUpdate(url("/version-fake.json"))

    assertTrue(result is UpdateCheckResult.Error)
    val error = result as UpdateCheckResult.Error
    assertTrue(error.message.contains("Security rejection"))
  }

  @Test
  fun `checkUpdate handles HTTP 404 gracefully`() = runBlocking {
    val result = updateManager.checkUpdate(url("/non-existent-version.json"))

    assertTrue(result is UpdateCheckResult.Error)
    val error = result as UpdateCheckResult.Error
    assertTrue(error.message.contains("HTTP 404") || error.message.contains("No newer release") || error.message.contains("Private"))
  }

  @Test
  fun `downloadAndVerify fails securely when SHA-256 checksum mismatches`() = runBlocking {
    val testPayload = "Fake APK Content For Testing Checksum Failure".toByteArray()
    val expectedHash = "0000000000000000000000000000000000000000000000000000000000000000"

    server.createContext("/mismatched.apk") { exchange ->
      exchange.sendResponseHeaders(200, testPayload.size.toLong())
      exchange.responseBody.use { it.write(testPayload) }
    }

    val manifest = UpdateManifest(
      versionCode = 2,
      versionName = "2.0",
      packageName = context.packageName,
      downloadUrl = url("/mismatched.apk"),
      fileSize = testPayload.size.toLong(),
      sha256 = expectedHash
    )

    val statuses = updateManager.downloadAndVerify(manifest).toList()

    val lastStatus = statuses.lastOrNull()
    assertTrue(lastStatus is DownloadStatus.Failed)
    val failed = lastStatus as DownloadStatus.Failed
    assertTrue(failed.error.contains("SHA-256 mismatch"))

    // Ensure corrupted/mismatched file is purged from cache
    val targetFile = File(context.cacheDir, "updates/OrderTracker_v2.apk")
    assertFalse("Corrupted file must be deleted upon checksum failure", targetFile.exists())
  }

  @Test
  fun `downloadAndVerify calculates correct SHA-256 digest`() = runBlocking {
    val testPayload = "Valid binary content test payload".toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(testPayload)
    val expectedHash = digest.joinToString("") { "%02x".format(it) }

    server.createContext("/valid_hash.apk") { exchange ->
      exchange.sendResponseHeaders(200, testPayload.size.toLong())
      exchange.responseBody.use { it.write(testPayload) }
    }

    val manifest = UpdateManifest(
      versionCode = 2,
      versionName = "2.0",
      packageName = context.packageName,
      downloadUrl = url("/valid_hash.apk"),
      fileSize = testPayload.size.toLong(),
      sha256 = expectedHash
    )

    val statuses = updateManager.downloadAndVerify(manifest).toList()

    // It should pass checksum verification and reach package verification
    val hasVerifyingStatus = statuses.any { it is DownloadStatus.Verifying }
    assertTrue("Should reach verifying state", hasVerifyingStatus)

    val failed = statuses.last() as DownloadStatus.Failed
    assertTrue(failed.error.contains("Invalid APK") || failed.error.contains("archive"))
  }

  @Test
  fun `downloadAndVerify safely handles invalid archive and purges target file`() = runBlocking {
    val dummyBytes = "Corrupted APK bytes that cannot be parsed as Android Package".toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(dummyBytes)
    val validHash = digest.joinToString("") { "%02x".format(it) }

    server.createContext("/corrupted.apk") { exchange ->
      exchange.sendResponseHeaders(200, dummyBytes.size.toLong())
      exchange.responseBody.use { it.write(dummyBytes) }
    }

    val manifest = UpdateManifest(
      versionCode = 1,
      versionName = "1.0",
      packageName = context.packageName,
      downloadUrl = url("/corrupted.apk"),
      fileSize = dummyBytes.size.toLong(),
      sha256 = validHash
    )

    val statuses = updateManager.downloadAndVerify(manifest).toList()
    val last = statuses.last()
    assertTrue("Should fail with invalid APK or unable to read metadata: $last", last is DownloadStatus.Failed)
    val failed = last as DownloadStatus.Failed
    assertTrue(failed.error.contains("Invalid APK") || failed.error.contains("metadata"))

    val targetFile = File(context.cacheDir, "updates/OrderTracker_v1.apk")
    assertFalse("Corrupted/unparseable file must be deleted", targetFile.exists())
  }
}
