package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
  @Query("SELECT * FROM orders WHERE year = :year AND month = :month ORDER BY day ASC")
  fun getOrdersForMonth(year: Int, month: Int): Flow<List<OrderDayEntity>>

  @Query("SELECT * FROM orders WHERE id = :id")
  suspend fun getOrderById(id: String): OrderDayEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(order: OrderDayEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateAll(orders: List<OrderDayEntity>)

  @Query("DELETE FROM orders WHERE year = :year AND month = :month")
  suspend fun deleteMonthData(year: Int, month: Int)

  @Query("SELECT * FROM orders ORDER BY year DESC, month DESC, day DESC")
  suspend fun getAllOrders(): List<OrderDayEntity>

  @Query("DELETE FROM orders")
  suspend fun deleteAllOrders()
}
