package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderDayEntity
import com.example.ui.MainViewModel
import com.example.ui.components.DayEditSheet
import com.example.ui.components.MONTH_NAMES
import com.example.ui.components.MonthSelectorHeader
import com.example.ui.theme.MaterialThemeColors
import com.example.ui.theme.ProfitGreen
import kotlinx.coroutines.launch
import java.util.Calendar

enum class DayFilter(val label: String, val icon: String) {
  ALL("All Days", "📅"),
  WORKED("Worked", "✓"),
  FUEL("Gas Logged", "⛽"),
  GOAL("Goal Met", "🎯")
}

@Composable
fun OrdersScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val year by viewModel.selectedYear.collectAsState()
  val month by viewModel.selectedMonth.collectAsState()
  val ordersMap by viewModel.ordersMap.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val summary by viewModel.monthlySummary.collectAsState()
  val daysInMonth = viewModel.getDaysInSelectedMonth()
  val monthName = MONTH_NAMES.getOrElse(month - 1) { "October" }
  val themeColors = MaterialThemeColors
  val scope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  var selectedFilter by remember { mutableStateOf(DayFilter.ALL) }

  val editingDayFromVm by viewModel.editingDay.collectAsState()
  var editingDay by remember { mutableStateOf<Int?>(null) }

  LaunchedEffect(editingDayFromVm) {
    val dayToEdit = editingDayFromVm
    if (dayToEdit != null) {
      editingDay = dayToEdit
      viewModel.setEditingDay(null)
    }
  }

  val now = Calendar.getInstance()
  val todayYear = now.get(Calendar.YEAR)
  val todayMonth = now.get(Calendar.MONTH) + 1
  val todayDay = now.get(Calendar.DAY_OF_MONTH)
  val isViewingCurrentMonth = (year == todayYear && month == todayMonth)

  // Scroll to today's row when current month loads
  LaunchedEffect(isViewingCurrentMonth, year, month) {
    if (isViewingCurrentMonth && todayDay in 1..daysInMonth) {
      val targetIndex = todayDay.coerceAtLeast(0)
      listState.animateScrollToItem(targetIndex)
    }
  }

  // Pre-calculate filtered days
  val allDays = (1..daysInMonth).toList()
  val filteredDays = allDays.filter { dayNum ->
    val record = ordersMap[dayNum]
    when (selectedFilter) {
      DayFilter.ALL -> true
      DayFilter.WORKED -> (record != null && record.orders > 0)
      DayFilter.FUEL -> (record != null && record.gas > 0.0)
      DayFilter.GOAL -> {
        if (record == null) false
        else {
          val dayEarnings = (record.orders * settings.normalComm) + record.tip - record.gas
          settings.dailyEarningsGoal > 0 && dayEarnings >= settings.dailyEarningsGoal
        }
      }
    }
  }

  // Theme-Adaptive Liquid Background
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(themeColors.backgroundDark)
  ) {
    // 1. Ambient Glow Aura Top Right
    Box(
      modifier = Modifier
        .size(360.dp)
        .offset(x = 130.dp, y = (-70).dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              themeColors.backgroundGlow.copy(alpha = 0.22f),
              themeColors.variant.copy(alpha = 0.08f),
              Color.Transparent
            )
          )
        )
    )

    // 2. Ambient Glow Aura Center Left
    Box(
      modifier = Modifier
        .size(340.dp)
        .offset(x = (-120).dp, y = 380.dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              themeColors.variant.copy(alpha = 0.16f),
              themeColors.accent.copy(alpha = 0.06f),
              Color.Transparent
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
    ) {
      Spacer(modifier = Modifier.height(6.dp))

      // 1. Month Selector Header: < October 2026 ▾ > with [📅 TODAY] Button
      MonthSelectorHeader(
        year = year,
        month = month,
        onPreviousMonth = { viewModel.previousMonth() },
        onNextMonth = { viewModel.nextMonth() },
        onSelectYearMonth = { y, m -> viewModel.setYearAndMonth(y, m) },
        onTodayClick = {
          viewModel.setYearAndMonth(todayYear, todayMonth)
          scope.launch {
            val targetIndex = (todayDay - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
          }
        }
      )

      Spacer(modifier = Modifier.height(4.dp))

      // 2. Horizontal Filter Chips Bar: [All Days (31)] [Worked (17)] [Gas Logged (0)] [Goal Met (0)]
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        DayFilter.values().forEach { filter ->
          val isSelected = selectedFilter == filter
          val count = when (filter) {
            DayFilter.ALL -> daysInMonth
            DayFilter.WORKED -> ordersMap.values.count { it.orders > 0 }
            DayFilter.FUEL -> ordersMap.values.count { it.gas > 0.0 }
            DayFilter.GOAL -> ordersMap.values.count {
              val earnings = (it.orders * settings.normalComm) + it.tip - it.gas
              settings.dailyEarningsGoal > 0 && earnings >= settings.dailyEarningsGoal
            }
          }

          val chipBorderColor = when (filter) {
            DayFilter.ALL -> if (isSelected) themeColors.primary else themeColors.cardBorder
            DayFilter.WORKED -> if (isSelected) themeColors.primary else Color(0xFF0D9488).copy(alpha = 0.5f)
            DayFilter.FUEL -> if (isSelected) Color(0xFFF97316) else Color(0xFFEA580C).copy(alpha = 0.4f)
            DayFilter.GOAL -> if (isSelected) Color(0xFFA855F7) else Color(0xFF7C3AED).copy(alpha = 0.4f)
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(
                if (isSelected) {
                  Brush.horizontalGradient(
                    listOf(themeColors.primary, themeColors.variant)
                  )
                } else {
                  Brush.horizontalGradient(
                    if (themeColors.isDark) {
                      listOf(Color(0xFF0A1325), Color(0xFF070E1A))
                    } else {
                      listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                    }
                  )
                }
              )
              .border(1.dp, chipBorderColor, RoundedCornerShape(12.dp))
              .clickable { selectedFilter = filter }
              .padding(horizontal = 12.dp, vertical = 7.dp)
              .testTag("filter_${filter.name.lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = filter.icon,
                fontSize = 12.sp,
                color = if (isSelected) Color.Black else themeColors.primary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${filter.label} ($count)",
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                color = if (isSelected) Color.Black else themeColors.textPrimary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4. Table Header: 📅 Date | 🛍️ Orders | 🪙 Tip | ⛽ Gas
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 22.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.width(62.dp)
        ) {
          Text(text = "📅", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Date",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.textSecondary
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.weight(1f)
        ) {
          Text(text = "🛍️", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Orders",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.textSecondary
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.weight(1f)
        ) {
          Text(text = "🪙", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Tip",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.textSecondary
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.weight(1f)
        ) {
          Text(text = "⛽", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Gas",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.textSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // 5. Daily Order Rows List
      if (filteredDays.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🔍", fontSize = 42.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "No records match '${selectedFilter.label}'",
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = themeColors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Tap any day in the list to log orders and fuel.",
              fontSize = 12.sp,
              color = themeColors.textSecondary,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = { selectedFilter = DayFilter.ALL },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary, contentColor = Color.Black)
            ) {
              Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Show All Days", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 2.dp, bottom = 140.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // 1. Hero Target Progress Card (scrolls naturally with order list to free screen space)
          item(key = "hero_target_progress") {
            HeroTargetProgressCard(
              summary = summary,
              settings = settings,
              monthName = monthName,
              modifier = Modifier.padding(bottom = 2.dp)
            )
          }

          items(filteredDays.size, key = { filteredDays[it] }) { idx ->
            val dayNum = filteredDays[idx]
            val record = ordersMap[dayNum]

            val cal = Calendar.getInstance().apply {
              set(Calendar.YEAR, year)
              set(Calendar.MONTH, month - 1)
              set(Calendar.DAY_OF_MONTH, dayNum)
            }
            val dayOfWeekName = when (cal.get(Calendar.DAY_OF_WEEK)) {
              Calendar.SUNDAY -> "SUN"
              Calendar.MONDAY -> "MON"
              Calendar.TUESDAY -> "TUE"
              Calendar.WEDNESDAY -> "WED"
              Calendar.THURSDAY -> "THU"
              Calendar.FRIDAY -> "FRI"
              Calendar.SATURDAY -> "SAT"
              else -> ""
            }

            val isToday = (isViewingCurrentMonth && dayNum == todayDay)

            NeonOrderDayCard(
              day = dayNum,
              dayOfWeek = dayOfWeekName,
              isToday = isToday,
              record = record,
              settings = settings,
              onClick = { editingDay = dayNum }
            )
          }
        }
      }
    }
  }

  // Edit sheet modal
  editingDay?.let { day ->
    DayEditSheet(
      day = day,
      monthName = monthName,
      year = year,
      currency = settings.currency,
      currentRecord = ordersMap[day],
      onDismiss = { editingDay = null },
      onSave = { d, orders, tip, gas, notes ->
        viewModel.saveDayRecord(d, orders, tip, gas, notes)
      },
      onClear = { d ->
        viewModel.saveDayRecord(d, 0, 0.0, 0.0, "")
      }
    )
  }
}

/**
 * Hero Target Progress Card matching screenshot:
 * Shows October Target Progress, Progress bar, 3-column stats with dividers.
 */
@Composable
private fun HeroTargetProgressCard(
  summary: com.example.ui.MonthlySummary,
  settings: com.example.data.UserSettings,
  monthName: String,
  modifier: Modifier = Modifier
) {
  val themeColors = MaterialThemeColors
  val targetOrders = if (settings.targetOrders > 0) settings.targetOrders else 400
  val currentOrders = summary.totalOrders
  val rawProgress = (currentOrders.toFloat() / targetOrders.toFloat()).coerceIn(0f, 1f)

  val animatedProgress by animateFloatAsState(
    targetValue = rawProgress,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "progress_anim"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("card_target_progress"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = themeColors.cardBackground),
    border = BorderStroke(1.2.dp, themeColors.cardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header: [🚀 Rocket] Month Target Progress ... 47% ↘
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(themeColors.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.RocketLaunch,
              contentDescription = null,
              tint = themeColors.primary,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Text(
            text = "$monthName Target Progress",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.textPrimary
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${(rawProgress * 100).toInt()}%",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = themeColors.primary
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "↘",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Glowing Horizontal Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(if (themeColors.isDark) Color(0xFF131C31) else Color(0xFFE2E8F0))
      ) {
        if (animatedProgress > 0f) {
          Box(
            modifier = Modifier
              .fillMaxWidth(fraction = animatedProgress)
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(themeColors.primary, themeColors.variant)
                )
              )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3-Column Metrics Row: Orders Delivered | Total Tips | Est. Net Profit
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Col 1: Orders Delivered
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "📦", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Orders Delivered",
              fontSize = 10.sp,
              color = themeColors.textSecondary,
              fontWeight = FontWeight.Medium
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$currentOrders / $targetOrders",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = themeColors.textPrimary
          )
        }

        // Divider
        Box(
          modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(themeColors.dividerColor)
        )

        // Col 2: Total Tips
        Column(
          modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🪙", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Total Tips",
              fontSize = 10.sp,
              color = themeColors.textSecondary,
              fontWeight = FontWeight.Medium
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${settings.currency} ${String.format("%.2f", summary.totalTips)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = themeColors.primary
          )
        }

        // Divider
        Box(
          modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(themeColors.dividerColor)
        )

        // Col 3: Est. Net Profit
        Column(
          modifier = Modifier.weight(1f).padding(start = 8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "💰", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Est. Net Profit",
              fontSize = 10.sp,
              color = themeColors.textSecondary,
              fontWeight = FontWeight.Medium
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${settings.currency} ${String.format("%.2f", summary.netProfit)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ProfitGreen
          )
        }
      }
    }
  }
}

/**
 * Neon Order Day Card matching the screenshot:
 * Date badge on left (day number + day of week), orders count with bag icon, tip, gas, and right arrow.
 */
@Composable
private fun NeonOrderDayCard(
  day: Int,
  dayOfWeek: String,
  isToday: Boolean,
  record: OrderDayEntity?,
  settings: com.example.data.UserSettings,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val themeColors = MaterialThemeColors
  val hasOrders = record != null && record.orders > 0
  val hasTip = record != null && record.tip > 0.0
  val hasGas = record != null && record.gas > 0.0

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.98f else 1f,
    animationSpec = tween(120),
    label = "press_scale"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .clip(RoundedCornerShape(14.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .testTag("day_row_$day"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isToday) {
        if (themeColors.isDark) Color(0xFF03224C).copy(alpha = 0.85f) else themeColors.primary.copy(alpha = 0.12f)
      } else {
        themeColors.cardBackground
      }
    ),
    border = if (isToday) {
      BorderStroke(1.8.dp, themeColors.primary)
    } else {
      BorderStroke(1.dp, themeColors.cardBorder)
    }
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Date Badge Container: Day Number (top) + Day Of Week (bottom)
        Box(
          modifier = Modifier
            .width(46.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (themeColors.isDark) Color(0xFF070E1A) else Color(0xFFF1F5F9))
            .border(1.dp, if (isToday) themeColors.primary else themeColors.cardBorder, RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = "$day",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = themeColors.textPrimary
            )
            Text(
              text = dayOfWeek.uppercase(),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = themeColors.primary
            )
          }
        }

        // If today, show a small "📅 Today" pill tag beside date badge
        if (isToday) {
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = themeColors.primary.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, themeColors.primary.copy(alpha = 0.6f))
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Text(text = "📅", fontSize = 9.sp)
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Today",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.primary
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Orders Column
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(text = "🛍️", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = if (hasOrders) "${record?.orders}" else "--",
            fontSize = 14.sp,
            fontWeight = if (hasOrders) FontWeight.Bold else FontWeight.Medium,
            color = if (hasOrders) themeColors.textPrimary else themeColors.textMuted
          )
        }

        // Tip Column
        Box(
          modifier = Modifier.weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (hasTip) String.format("%.2f", record?.tip) else "--",
            fontSize = 14.sp,
            fontWeight = if (hasTip) FontWeight.Bold else FontWeight.Normal,
            color = if (hasTip) themeColors.primary else themeColors.textMuted
          )
        }

        // Gas Column
        Box(
          modifier = Modifier.weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (hasGas) String.format("%.2f", record?.gas) else "--",
            fontSize = 14.sp,
            fontWeight = if (hasGas) FontWeight.Bold else FontWeight.Normal,
            color = if (hasGas) Color(0xFFFF5252) else themeColors.textMuted
          )
        }

        // Right Chevron
        Text(
          text = "›",
          fontSize = 18.sp,
          fontWeight = FontWeight.ExtraBold,
          color = themeColors.textMuted
        )
      }

      // Checkmark Badge in top-right corner if day has orders
      if (hasOrders) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 4.dp, end = 6.dp)
        ) {
          Text(
            text = "✓",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF10B981)
          )
        }
      }
    }
  }
}
