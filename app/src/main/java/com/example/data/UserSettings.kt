package com.example.data

data class UserSettings(
  val currency: String = "K.D",
  val baseSalary: Double = 0.0,
  val targetOrders: Int = 400,
  val maxOrders: Int = 400,
  val normalComm: Double = 0.5,
  val extraComm: Double = 0.8,
  val dailyEarningsGoal: Double = 25.0,
  val isDarkMode: Boolean = false,
  val selectedTheme: String = "Neon Cyan", // "Neon Cyan", "Mid Purple", "Luxury Gold"
  val autoMidnightReminder: Boolean = true
)
