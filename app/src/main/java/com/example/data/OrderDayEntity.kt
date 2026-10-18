package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderDayEntity(
  @PrimaryKey val id: String, // Format: "yyyy-MM-dd", e.g. "2026-10-01"
  val year: Int,
  val month: Int, // 1 - 12
  val day: Int, // 1 - 31
  val orders: Int = 0,
  val tip: Double = 0.0,
  val gas: Double = 0.0,
  val notes: String = "",
  val updatedAt: Long = System.currentTimeMillis()
)
