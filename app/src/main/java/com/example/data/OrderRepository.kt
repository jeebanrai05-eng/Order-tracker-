package com.example.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OrderRepository(
  private val orderDao: OrderDao
) {
  private val firestore: FirebaseFirestore? = try {
    FirebaseFirestore.getInstance()
  } catch (e: Exception) {
    null
  }

  fun getOrdersForMonth(year: Int, month: Int): Flow<List<OrderDayEntity>> {
    return orderDao.getOrdersForMonth(year, month)
  }

  suspend fun saveOrderDay(order: OrderDayEntity) = withContext(Dispatchers.IO) {
    orderDao.insertOrUpdate(order)
    // If logged in, optionally sync to firestore asynchronously
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null && firestore != null) {
      try {
        val map = hashMapOf(
          "id" to order.id,
          "year" to order.year,
          "month" to order.month,
          "day" to order.day,
          "orders" to order.orders,
          "tip" to order.tip,
          "gas" to order.gas,
          "notes" to order.notes,
          "updatedAt" to order.updatedAt
        )
        firestore.collection("users")
          .document(user.uid)
          .collection("orders")
          .document(order.id)
          .set(map, SetOptions.merge())
      } catch (_: Exception) {
        // Local save succeeded, cloud backup will retry on next sync
      }
    }
  }

  suspend fun syncWithCloud(): Result<Int> = withContext(Dispatchers.IO) {
    val user = FirebaseAuth.getInstance().currentUser
      ?: return@withContext Result.failure(Exception("User not logged in"))
    val db = firestore
      ?: return@withContext Result.failure(Exception("Firestore not available"))

    try {
      // 1. Upload local data to cloud
      val localOrders = orderDao.getAllOrders()
      for (order in localOrders) {
        val map = hashMapOf(
          "id" to order.id,
          "year" to order.year,
          "month" to order.month,
          "day" to order.day,
          "orders" to order.orders,
          "tip" to order.tip,
          "gas" to order.gas,
          "notes" to order.notes,
          "updatedAt" to order.updatedAt
        )
        db.collection("users")
          .document(user.uid)
          .collection("orders")
          .document(order.id)
          .set(map, SetOptions.merge())
      }

      // 2. Fetch remote data and merge into local Room
      val snapshot = db.collection("users")
        .document(user.uid)
        .collection("orders")
        .get()
        .await()

      val remoteOrders = mutableListOf<OrderDayEntity>()
      for (doc in snapshot.documents) {
        val id = doc.getString("id") ?: doc.id
        val year = doc.getLong("year")?.toInt() ?: 2026
        val month = doc.getLong("month")?.toInt() ?: 10
        val day = doc.getLong("day")?.toInt() ?: 1
        val orders = doc.getLong("orders")?.toInt() ?: 0
        val tip = doc.getDouble("tip") ?: 0.0
        val gas = doc.getDouble("gas") ?: 0.0
        val notes = doc.getString("notes") ?: ""
        val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()

        remoteOrders.add(
          OrderDayEntity(
            id = id,
            year = year,
            month = month,
            day = day,
            orders = orders,
            tip = tip,
            gas = gas,
            notes = notes,
            updatedAt = updatedAt
          )
        )
      }

      if (remoteOrders.isNotEmpty()) {
        orderDao.insertOrUpdateAll(remoteOrders)
      }

      Result.success(localOrders.size + remoteOrders.size)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun saveSettingsToCloud(settings: UserSettings): Result<Unit> = withContext(Dispatchers.IO) {
    val user = FirebaseAuth.getInstance().currentUser ?: return@withContext Result.success(Unit)
    val db = firestore ?: return@withContext Result.failure(Exception("Firestore not available"))
    try {
      val map = hashMapOf(
        "currency" to settings.currency,
        "baseSalary" to settings.baseSalary,
        "targetOrders" to settings.targetOrders,
        "maxOrders" to settings.maxOrders,
        "normalComm" to settings.normalComm,
        "extraComm" to settings.extraComm,
        "dailyEarningsGoal" to settings.dailyEarningsGoal,
        "selectedTheme" to settings.selectedTheme,
        "autoMidnightReminder" to settings.autoMidnightReminder,
        "updatedAt" to System.currentTimeMillis()
      )
      db.collection("users")
        .document(user.uid)
        .collection("config")
        .document("settings")
        .set(map, SetOptions.merge())
        .await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun fetchSettingsFromCloud(): UserSettings? = withContext(Dispatchers.IO) {
    val user = FirebaseAuth.getInstance().currentUser ?: return@withContext null
    val db = firestore ?: return@withContext null
    try {
      val doc = db.collection("users")
        .document(user.uid)
        .collection("config")
        .document("settings")
        .get()
        .await()
      if (doc.exists()) {
        UserSettings(
          currency = doc.getString("currency") ?: "K.D",
          baseSalary = doc.getDouble("baseSalary") ?: 0.0,
          targetOrders = doc.getLong("targetOrders")?.toInt() ?: 400,
          maxOrders = doc.getLong("maxOrders")?.toInt() ?: 400,
          normalComm = doc.getDouble("normalComm") ?: 0.5,
          extraComm = doc.getDouble("extraComm") ?: 0.8,
          dailyEarningsGoal = doc.getDouble("dailyEarningsGoal") ?: 25.0,
          selectedTheme = doc.getString("selectedTheme") ?: "Neon Cyan",
          autoMidnightReminder = doc.getBoolean("autoMidnightReminder") ?: true
        )
      } else null
    } catch (_: Exception) {
      null
    }
  }

  suspend fun clearMonthData(year: Int, month: Int) = withContext(Dispatchers.IO) {
    orderDao.deleteMonthData(year, month)
  }

  suspend fun exportMonthReportText(year: Int, month: Int, currency: String): String = withContext(Dispatchers.IO) {
    val orders = orderDao.getAllOrders().filter { it.year == year && it.month == month }
    val totalOrders = orders.sumOf { it.orders }
    val totalTips = orders.sumOf { it.tip }
    val totalGas = orders.sumOf { it.gas }

    val sb = StringBuilder()
    sb.append("=== ORDER TRACKER REPORT ===\n")
    sb.append("Month: $month/$year\n")
    sb.append("Total Orders: $totalOrders\n")
    sb.append("Total Tips: $currency ${String.format("%.3f", totalTips)}\n")
    sb.append("Total Gas: $currency ${String.format("%.3f", totalGas)}\n\n")
    sb.append("Day | Orders | Tip | Gas\n")
    sb.append("---------------------------\n")
    for (item in orders.sortedBy { it.day }) {
      sb.append(String.format("%2d  | %6d | %8.3f | %8.3f\n", item.day, item.orders, item.tip, item.gas))
    }
    sb.toString()
  }

  suspend fun clearAllData() = withContext(Dispatchers.IO) {
    orderDao.deleteAllOrders()
  }

  suspend fun exportAllDataCsv(currency: String): String = withContext(Dispatchers.IO) {
    val orders = orderDao.getAllOrders()
    val sb = StringBuilder()
    sb.append("Date,Year,Month,Day,Orders,Tip ($currency),Gas ($currency),Notes\n")
    for (o in orders) {
      sb.append("${o.id},${o.year},${o.month},${o.day},${o.orders},${String.format("%.3f", o.tip)},${String.format("%.3f", o.gas)},\"${o.notes.replace("\"", "\"\"")}\"\n")
    }
    sb.toString()
  }
}
