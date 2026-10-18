package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.OrderDayEntity
import com.example.data.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Order Tracker", appName)
  }

  @Test
  fun `verify monthly profit and tiered commission calculations`() {
    val settings = UserSettings(
      currency = "K.D",
      baseSalary = 200.0,
      targetOrders = 400,
      maxOrders = 400,
      normalComm = 0.5,
      extraComm = 0.8,
      dailyEarningsGoal = 25.0
    )

    // Scenario A: Within target orders (35 orders)
    val day1 = OrderDayEntity(id = "2026-10-01", year = 2026, month = 10, day = 1, orders = 20, tip = 5.0, gas = 2.0)
    val day2 = OrderDayEntity(id = "2026-10-02", year = 2026, month = 10, day = 2, orders = 15, tip = 3.5, gas = 1.5)
    val orders = listOf(day1, day2)

    val totalOrders = orders.sumOf { it.orders }
    val totalTips = orders.sumOf { it.tip }
    val totalGas = orders.sumOf { it.gas }

    val normalOrders = totalOrders.coerceAtMost(settings.targetOrders)
    val extraOrders = (totalOrders - settings.targetOrders).coerceAtLeast(0)
    val commission = (normalOrders * settings.normalComm) + (extraOrders * settings.extraComm)
    val netProfit = settings.baseSalary + commission + totalTips - totalGas

    assertEquals(35, totalOrders)
    assertEquals(8.5, totalTips, 0.001)
    assertEquals(3.5, totalGas, 0.001)
    assertEquals(17.5, commission, 0.001)
    assertEquals(222.5, netProfit, 0.001)

    // Scenario B: Exceeding target (e.g. 420 orders)
    val highOrders = 420
    val normalHigh = highOrders.coerceAtMost(settings.targetOrders)
    val extraHigh = (highOrders - settings.targetOrders).coerceAtLeast(0)
    val highCommission = (normalHigh * settings.normalComm) + (extraHigh * settings.extraComm)
    assertEquals(216.0, highCommission, 0.001)
  }

  @Test
  fun `verify daily earnings goal calculation`() {
    val settings = UserSettings(
      currency = "K.D",
      normalComm = 0.5,
      dailyEarningsGoal = 25.0
    )

    // Day 1: 50 orders * 0.5 + 5.0 tip - 3.0 gas = 27.0 => meets goal!
    val day1Earnings = (50 * settings.normalComm) + 5.0 - 3.0
    assertTrue(day1Earnings >= settings.dailyEarningsGoal)
    assertEquals(27.0, day1Earnings, 0.001)

    // Day 2: 30 orders * 0.5 + 2.0 tip - 2.0 gas = 15.0 => under goal
    val day2Earnings = (30 * settings.normalComm) + 2.0 - 2.0
    val progress = (day2Earnings / settings.dailyEarningsGoal).toFloat()
    assertEquals(0.6f, progress, 0.001f)
  }
}
