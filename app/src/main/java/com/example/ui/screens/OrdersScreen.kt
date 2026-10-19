package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
  WORKED("Worked", "🛍️"),
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
  var isMilestoneCardExpanded by remember { mutableStateOf(true) }

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
      val targetIndex = (todayDay - 1).coerceAtLeast(0)
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

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Spacer(modifier = Modifier.height(6.dp))

    // 1. Month Selector Header: < October 2026 ▾ > with TODAY button
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

    Spacer(modifier = Modifier.height(6.dp))

    // 2. Animated Monthly Milestone Card
    MonthlyMilestoneCard(
      summary = summary,
      settings = settings,
      monthName = monthName,
      year = year,
      isExpanded = isMilestoneCardExpanded,
      onToggleExpand = { isMilestoneCardExpanded = !isMilestoneCardExpanded },
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(6.dp))

    // 3. Interactive Filter Chips Bar
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

        val chipBg by animateColorAsState(
          targetValue = if (isSelected) themeColors.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          animationSpec = tween(250),
          label = "chip_bg"
        )
        val chipTextColor by animateColorAsState(
          targetValue = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
          animationSpec = tween(250),
          label = "chip_text"
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(chipBg)
            .clickable { selectedFilter = filter }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("filter_${filter.name.lowercase()}"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = filter.icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${filter.label} ($count)",
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = chipTextColor
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 4. Column Headers: Date | 🛍️ Orders | 💵 Tip | ⛽ Gas
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Date",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(62.dp)
      )

      Text(
        text = "🛍️ Orders",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.weight(1f)
      )

      Text(
        text = "💵 Tip",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.weight(1f)
      )

      Text(
        text = "⛽ Gas",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    // 5. List of Day Rows or Empty State
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
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Tap any day in the calendar to log orders and fuel.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredDays.size, key = { filteredDays[it] }) { idx ->
          val dayNum = filteredDays[idx]
          val record = ordersMap[dayNum]

          val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, dayNum)
          }
          val dayOfWeekName = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sun"
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            else -> ""
          }

          val isToday = (isViewingCurrentMonth && dayNum == todayDay)

          OrderDayCard(
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

@Composable
fun MonthlyMilestoneCard(
  summary: com.example.ui.MonthlySummary,
  settings: com.example.data.UserSettings,
  monthName: String,
  year: Int,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
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

  val isTargetHit = currentOrders >= targetOrders

  Card(
    modifier = modifier
      .fillMaxWidth()
      .animateContentSize(animationSpec = tween(300))
      .shadow(elevation = 3.dp, shape = RoundedCornerShape(18.dp)),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ),
    border = BorderStroke(
      width = 1.dp,
      color = if (isTargetHit) ProfitGreen.copy(alpha = 0.6f) else themeColors.primary.copy(alpha = 0.25f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isTargetHit) Icons.Default.Celebration else Icons.Default.TrackChanges,
            contentDescription = null,
            tint = if (isTargetHit) ProfitGreen else themeColors.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isTargetHit) "Monthly Target Reached! 🎉" else "$monthName Target Progress",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isTargetHit) ProfitGreen else MaterialTheme.colorScheme.onSurface
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onToggleExpand() }
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${(rawProgress * 100).toInt()}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isTargetHit) ProfitGreen else themeColors.primary
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "Toggle",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Smooth Animated Linear Progress Bar
      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = if (isTargetHit) ProfitGreen else themeColors.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(200))
      ) {
        Column {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Orders Delivered",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "$currentOrders / $targetOrders",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Total Tips",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${settings.currency} ${String.format("%.2f", summary.totalTips)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ProfitGreen
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Est. Net Profit",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${settings.currency} ${String.format("%.2f", summary.netProfit)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (summary.netProfit >= 0) ProfitGreen else Color(0xFFFF5252)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun OrderDayCard(
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

  val dayEarnings = if (record != null) {
    (record.orders * settings.normalComm) + record.tip - record.gas
  } else 0.0

  val goal = settings.dailyEarningsGoal
  val isGoalMet = goal > 0 && dayEarnings >= goal
  val goalProgress = if (goal > 0) (dayEarnings / goal).toFloat().coerceIn(0f, 1f) else 0f

  val isWeekend = dayOfWeek.equals("Fri", ignoreCase = true) ||
                  dayOfWeek.equals("Sat", ignoreCase = true) ||
                  dayOfWeek.equals("Sun", ignoreCase = true)

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
      .shadow(
        elevation = if (isToday) 6.dp else 2.dp,
        shape = RoundedCornerShape(18.dp)
      )
      .clip(RoundedCornerShape(18.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .testTag("day_row_$day"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isToday) {
        themeColors.primary.copy(alpha = 0.08f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    border = if (isToday) {
      BorderStroke(2.dp, themeColors.primary)
    } else {
      BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Day number + Day of week box on left
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
              if (isToday) themeColors.primary else MaterialTheme.colorScheme.surfaceVariant
            ),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = day.toString(),
              fontSize = 17.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (isToday) Color.Black else MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = dayOfWeek.uppercase(),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (isToday) {
                Color.Black.copy(alpha = 0.85f)
              } else if (isWeekend) {
                themeColors.primary
              } else {
                MaterialTheme.colorScheme.onSurfaceVariant
              }
            )
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Orders Column
        Box(
          modifier = Modifier.weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (hasOrders) "${record?.orders}" else "Ord",
            fontSize = 16.sp,
            fontWeight = if (hasOrders) FontWeight.Bold else FontWeight.Medium,
            color = if (hasOrders) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
          )
        }

        // Tip Column
        Box(
          modifier = Modifier.weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (hasTip) String.format("%.2f", record?.tip) else "—",
            fontSize = 15.sp,
            fontWeight = if (hasTip) FontWeight.SemiBold else FontWeight.Normal,
            color = if (hasTip) ProfitGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
          )
        }

        // Gas Column
        Box(
          modifier = Modifier.weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (hasGas) String.format("%.2f", record?.gas) else "—",
            fontSize = 15.sp,
            fontWeight = if (hasGas) FontWeight.SemiBold else FontWeight.Normal,
            color = if (hasGas) Color(0xFFFF5252) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
          )
        }

        // Status indicator (Goal Met or Today badge)
        if (isToday) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(themeColors.primary)
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text(
              text = "TODAY",
              fontSize = 9.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.Black
            )
          }
        } else if (isGoalMet) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(ProfitGreen.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text(
              text = "🎯",
              fontSize = 11.sp
            )
          }
        }
      }

      // Today's Live Daily Earnings Goal Progress bar
      if (isToday && goal > 0) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isGoalMet) Icons.Default.CheckCircle else Icons.Default.TrackChanges,
              contentDescription = null,
              tint = if (isGoalMet) ProfitGreen else themeColors.primary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isGoalMet) "Daily Goal Achieved! 🎯" else "Daily Goal Progress",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isGoalMet) ProfitGreen else themeColors.primary
            )
          }
          Text(
            text = "${settings.currency} ${String.format("%.1f", dayEarnings)} / ${String.format("%.1f", goal)}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
          progress = { goalProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = if (isGoalMet) ProfitGreen else themeColors.primary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
      }
    }
  }
}
