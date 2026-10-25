package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.PreferencesManager

class DutyReminderReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val prefs = PreferencesManager(context)
    val settings = prefs.loadSettings()

    when (intent.action) {
      Intent.ACTION_BOOT_COMPLETED -> {
        // Re-schedule alarm on phone reboot if enabled
        if (settings.dutyEndReminderEnabled) {
          DutyReminderManager.scheduleReminder(
            context,
            settings.dutyEndHour,
            settings.dutyEndMinute
          )
        }
      }
      DutyReminderManager.ACTION_DUTY_REMINDER -> {
        if (settings.dutyEndReminderEnabled) {
          // 1. Show high-priority notification to log orders
          DutyReminderManager.showDutyReminderNotification(context)

          // 2. Schedule for next day's shift
          DutyReminderManager.scheduleReminder(
            context,
            settings.dutyEndHour,
            settings.dutyEndMinute
          )
        }
      }
    }
  }
}
