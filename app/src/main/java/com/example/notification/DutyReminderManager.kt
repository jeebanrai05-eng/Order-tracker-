package com.example.notification

import android.app.AlarmManager
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
import java.util.Calendar

object DutyReminderManager {

  const val CHANNEL_ID = "duty_end_reminder_channel"
  const val NOTIFICATION_ID = 1001
  const val REQUEST_CODE = 2002
  const val ACTION_DUTY_REMINDER = "com.ordercom.ACTION_DUTY_REMINDER"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Duty & Shift End Reminders"
      val descriptionText = "Notifies drivers at their shift end time to log orders, tips, and fuel"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
        enableVibration(true)
        setShowBadge(true)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun scheduleReminder(context: Context, hour: Int, minute: Int) {
    createNotificationChannel(context)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

    val intent = Intent(context, DutyReminderReceiver::class.java).apply {
      action = ACTION_DUTY_REMINDER
    }
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val calendar = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, hour)
      set(Calendar.MINUTE, minute)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
      if (before(Calendar.getInstance())) {
        add(Calendar.DAY_OF_YEAR, 1)
      }
    }

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          calendar.timeInMillis,
          pendingIntent
        )
      } else {
        alarmManager.setExact(
          AlarmManager.RTC_WAKEUP,
          calendar.timeInMillis,
          pendingIntent
        )
      }
    } catch (_: SecurityException) {
      // Fallback for devices restricting exact alarms
      alarmManager.set(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
      )
    }
  }

  fun cancelReminder(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intent = Intent(context, DutyReminderReceiver::class.java).apply {
      action = ACTION_DUTY_REMINDER
    }
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE,
      intent,
      PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )
    if (pendingIntent != null) {
      alarmManager.cancel(pendingIntent)
      pendingIntent.cancel()
    }
  }

  fun showDutyReminderNotification(context: Context) {
    createNotificationChannel(context)

    val todayDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    // Main tap intent -> opens MainActivity and pops up today's order entry
    val mainIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      putExtra("open_add_order", true)
      putExtra("target_day", todayDay)
    }
    val mainPendingIntent = PendingIntent.getActivity(
      context,
      101,
      mainIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Action 1: "📝 Add Order Now"
    val addOrderIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      putExtra("open_add_order", true)
      putExtra("target_day", todayDay)
    }
    val addOrderPendingIntent = PendingIntent.getActivity(
      context,
      102,
      addOrderIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Action 2: "📊 View Summary"
    val summaryIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      putExtra("open_tab", "report")
    }
    val summaryPendingIntent = PendingIntent.getActivity(
      context,
      103,
      summaryIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle("Shift Ended! 📦 Log Today's Orders")
      .setContentText("Tap here to quickly enter today's completed orders, tips, and fuel.")
      .setStyle(
        NotificationCompat.BigTextStyle()
          .bigText("Great work today! 🛵 Don't forget to record your delivery count, cash collected, and tips before you rest.")
      )
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_REMINDER)
      .setAutoCancel(true)
      .setContentIntent(mainPendingIntent)
      .addAction(
        android.R.drawable.ic_input_add,
        "📝 Add Today's Order",
        addOrderPendingIntent
      )
      .addAction(
        android.R.drawable.ic_menu_today,
        "📊 View Summary",
        summaryPendingIntent
      )

    try {
      with(NotificationManagerCompat.from(context)) {
        notify(NOTIFICATION_ID, builder.build())
      }
    } catch (_: SecurityException) {
      // Notification permission not granted
    }
  }

  fun triggerTestNotification(context: Context) {
    showDutyReminderNotification(context)
  }
}
