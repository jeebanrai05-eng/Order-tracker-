package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("order_tracker_prefs", Context.MODE_PRIVATE)

  private val _settingsFlow = MutableStateFlow(loadSettings())
  val settingsFlow: StateFlow<UserSettings> = _settingsFlow.asStateFlow()

  fun loadSettings(): UserSettings {
    return UserSettings(
      currency = prefs.getString(KEY_CURRENCY, "K.D") ?: "K.D",
      baseSalary = prefs.getFloat(KEY_BASE_SALARY, 0.0f).toDouble(),
      targetOrders = prefs.getInt(KEY_TARGET_ORDERS, 400),
      maxOrders = prefs.getInt(KEY_MAX_ORDERS, 400),
      normalComm = prefs.getFloat(KEY_NORMAL_COMM, 0.5f).toDouble(),
      extraComm = prefs.getFloat(KEY_EXTRA_COMM, 0.8f).toDouble(),
      dailyEarningsGoal = prefs.getFloat(KEY_DAILY_EARNINGS_GOAL, 25.0f).toDouble(),
      isDarkMode = prefs.getBoolean(KEY_IS_DARK_MODE, false),
      selectedTheme = prefs.getString(KEY_SELECTED_THEME, "Neon Cyan") ?: "Neon Cyan",
      autoMidnightReminder = prefs.getBoolean(KEY_AUTO_MIDNIGHT_REMINDER, true),
      dutyEndReminderEnabled = prefs.getBoolean(KEY_DUTY_END_REMINDER_ENABLED, true),
      dutyEndHour = prefs.getInt(KEY_DUTY_END_HOUR, 23),
      dutyEndMinute = prefs.getInt(KEY_DUTY_END_MINUTE, 0)
    )
  }

  fun updateDarkMode(isDark: Boolean) {
    prefs.edit().putBoolean(KEY_IS_DARK_MODE, isDark).apply()
    _settingsFlow.value = _settingsFlow.value.copy(isDarkMode = isDark)
  }

  fun updateTheme(theme: String) {
    prefs.edit().putString(KEY_SELECTED_THEME, theme).apply()
    _settingsFlow.value = _settingsFlow.value.copy(selectedTheme = theme)
  }

  fun updateAutoMidnightReminder(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_AUTO_MIDNIGHT_REMINDER, enabled).apply()
    _settingsFlow.value = _settingsFlow.value.copy(autoMidnightReminder = enabled)
  }

  fun updateDutyEndReminder(enabled: Boolean, hour: Int, minute: Int) {
    prefs.edit()
      .putBoolean(KEY_DUTY_END_REMINDER_ENABLED, enabled)
      .putInt(KEY_DUTY_END_HOUR, hour)
      .putInt(KEY_DUTY_END_MINUTE, minute)
      .apply()
    _settingsFlow.value = _settingsFlow.value.copy(
      dutyEndReminderEnabled = enabled,
      dutyEndHour = hour,
      dutyEndMinute = minute
    )
  }

  fun updateCurrency(currency: String) {
    prefs.edit().putString(KEY_CURRENCY, currency).apply()
    _settingsFlow.value = _settingsFlow.value.copy(currency = currency)
  }

  fun updateBaseSalary(salary: Double) {
    prefs.edit().putFloat(KEY_BASE_SALARY, salary.toFloat()).apply()
    _settingsFlow.value = _settingsFlow.value.copy(baseSalary = salary)
  }

  fun updateTargetOrders(target: Int) {
    prefs.edit().putInt(KEY_TARGET_ORDERS, target).apply()
    _settingsFlow.value = _settingsFlow.value.copy(targetOrders = target)
  }

  fun updateMaxOrders(max: Int) {
    prefs.edit().putInt(KEY_MAX_ORDERS, max).apply()
    _settingsFlow.value = _settingsFlow.value.copy(maxOrders = max)
  }

  fun updateNormalComm(rate: Double) {
    prefs.edit().putFloat(KEY_NORMAL_COMM, rate.toFloat()).apply()
    _settingsFlow.value = _settingsFlow.value.copy(normalComm = rate)
  }

  fun updateExtraComm(rate: Double) {
    prefs.edit().putFloat(KEY_EXTRA_COMM, rate.toFloat()).apply()
    _settingsFlow.value = _settingsFlow.value.copy(extraComm = rate)
  }

  fun updateDailyEarningsGoal(goal: Double) {
    prefs.edit().putFloat(KEY_DAILY_EARNINGS_GOAL, goal.toFloat()).apply()
    _settingsFlow.value = _settingsFlow.value.copy(dailyEarningsGoal = goal)
  }

  fun updateAll(settings: UserSettings) {
    prefs.edit()
      .putString(KEY_CURRENCY, settings.currency)
      .putFloat(KEY_BASE_SALARY, settings.baseSalary.toFloat())
      .putInt(KEY_TARGET_ORDERS, settings.targetOrders)
      .putInt(KEY_MAX_ORDERS, settings.maxOrders)
      .putFloat(KEY_NORMAL_COMM, settings.normalComm.toFloat())
      .putFloat(KEY_EXTRA_COMM, settings.extraComm.toFloat())
      .putFloat(KEY_DAILY_EARNINGS_GOAL, settings.dailyEarningsGoal.toFloat())
      .putBoolean(KEY_IS_DARK_MODE, settings.isDarkMode)
      .putString(KEY_SELECTED_THEME, settings.selectedTheme)
      .putBoolean(KEY_AUTO_MIDNIGHT_REMINDER, settings.autoMidnightReminder)
      .putBoolean(KEY_DUTY_END_REMINDER_ENABLED, settings.dutyEndReminderEnabled)
      .putInt(KEY_DUTY_END_HOUR, settings.dutyEndHour)
      .putInt(KEY_DUTY_END_MINUTE, settings.dutyEndMinute)
      .apply()
    _settingsFlow.value = settings
  }

  companion object {
    private const val KEY_CURRENCY = "key_currency"
    private const val KEY_BASE_SALARY = "key_base_salary"
    private const val KEY_TARGET_ORDERS = "key_target_orders"
    private const val KEY_MAX_ORDERS = "key_max_orders"
    private const val KEY_NORMAL_COMM = "key_normal_comm"
    private const val KEY_EXTRA_COMM = "key_extra_comm"
    private const val KEY_DAILY_EARNINGS_GOAL = "key_daily_earnings_goal"
    private const val KEY_IS_DARK_MODE = "key_is_dark_mode"
    private const val KEY_SELECTED_THEME = "key_selected_theme"
    private const val KEY_AUTO_MIDNIGHT_REMINDER = "key_auto_midnight_reminder"
    private const val KEY_DUTY_END_REMINDER_ENABLED = "key_duty_end_reminder_enabled"
    private const val KEY_DUTY_END_HOUR = "key_duty_end_hour"
    private const val KEY_DUTY_END_MINUTE = "key_duty_end_minute"
  }
}
