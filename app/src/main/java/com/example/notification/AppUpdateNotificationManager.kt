package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object AppUpdateNotificationManager {

  const val UPDATE_CHANNEL_ID = "app_updates_channel"
  const val UPDATE_NOTIFICATION_ID = 2005

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "App Updates & Releases"
      val descriptionText = "Notifies when a new version of Order Tracker is available to install"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(UPDATE_CHANNEL_ID, name, importance).apply {
        description = descriptionText
        enableVibration(true)
        setShowBadge(true)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun showUpdateAvailableNotification(
    context: Context,
    versionName: String,
    versionCode: Int,
    releaseNotes: String
  ) {
    createNotificationChannel(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      putExtra("open_update_center", true)
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      2051,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notesSnippet = if (releaseNotes.isNotBlank()) releaseNotes else "Glass Neo Theme, redesigned orders dashboard, dynamic themes, and bug fixes."

    val builder = NotificationCompat.Builder(context, UPDATE_CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle("🚀 Update Available: v$versionName (Build $versionCode)")
      .setContentText("Order Tracker v$versionName is ready to install! Tap to update.")
      .setStyle(
        NotificationCompat.BigTextStyle()
          .bigText("Order Tracker v$versionName (Build $versionCode) is ready!\n\n✨ $notesSnippet\n\nTap here to download and install now.")
      )
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_STATUS)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .addAction(
        android.R.drawable.stat_sys_download_done,
        "⬇️ Update Now",
        pendingIntent
      )

    try {
      with(NotificationManagerCompat.from(context)) {
        notify(UPDATE_NOTIFICATION_ID, builder.build())
      }
    } catch (_: SecurityException) {
      // Permission not granted
    }
  }

  fun triggerTestUpdateNotification(context: Context) {
    showUpdateAvailableNotification(
      context = context,
      versionName = "2.0.5",
      versionCode = 6,
      releaseNotes = "Glass Neo luxury theme, redesigned orders dashboard, dynamic top/bottom bars, and update notifications."
    )
  }
}
