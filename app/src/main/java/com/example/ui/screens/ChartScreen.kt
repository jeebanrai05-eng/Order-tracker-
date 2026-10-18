package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.MONTH_NAMES
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.CyanVariant
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.HourglassRed
import com.example.ui.theme.ProfitGreen
import kotlinx.coroutines.launch

@Composable
fun ChartScreen(
  viewModel: MainViewModel,
  onOpenMenu: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  val year by viewModel.selectedYear.collectAsState()
  val month by viewModel.selectedMonth.collectAsState()
  val summary by viewModel.monthlySummary.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val ordersList by viewModel.ordersList.collectAsState()
  val selectedMetric by viewModel.selectedMetric.collectAsState()
  val monthName = MONTH_NAMES.getOrElse(month - 1) { "October" }
  val themeColors = com.example.ui.theme.MaterialThemeColors

  var showMetricDropdown by remember { mutableStateOf(false) }
  var showExportDialog by remember { mutableStateOf(false) }
  var exportedText by remember { mutableStateOf("") }

  val hasData = ordersList.any { it.orders > 0 || it.tip > 0.0 || it.gas > 0.0 }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(bottom = 96.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Top Bar: ☰ October 2026 Report   [💾 Save]
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onOpenMenu,
          modifier = Modifier.testTag("btn_chart_menu")
        ) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "$monthName $year Report",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      // Save / Export Report Icon
      IconButton(
        onClick = {
          scope.launch {
            exportedText = viewModel.exportReport()
            showExportDialog = true
          }
        },
        modifier = Modifier
          .testTag("btn_export_report")
          .size(46.dp)
          .shadow(4.dp, CircleShape)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surface)
      ) {
        // Diskette / Floppy disk style icon
        Canvas(modifier = Modifier.size(24.dp)) {
          // Outer disk
          drawRoundRect(
            color = CyanDark,
            topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
            size = Size(20.dp.toPx(), 20.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
          )
          // Inner slider label
          drawRoundRect(
            color = Color.White,
            topLeft = Offset(6.dp.toPx(), 4.dp.toPx()),
            size = Size(12.dp.toPx(), 8.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // NET PROFIT Gradient Card (matching Screenshot 2)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .shadow(10.dp, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
        .background(brush = com.example.ui.theme.MaterialThemeColors.gradient)
        .padding(horizontal = 24.dp, vertical = 22.dp)
        .testTag("card_net_profit")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "NET PROFIT",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f),
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "${settings.currency} ${String.format("%.3f", summary.netProfit)}",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Base + Tips (${String.format("%.2f", summary.totalTips)}) - Gas (${String.format("%.2f", summary.totalGas)})",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
          )
        }

        // Frosted translucent badge with Money Bag
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.25f)),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "💰", fontSize = 32.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // ORDER PERFORMANCE Header with Dropdown
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 22.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "📈", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "ORDER PERFORMANCE",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          letterSpacing = 0.5.sp
        )
      }

      // Dropdown selector: "Daily Orders ▾"
      Box {
        Row(
          modifier = Modifier
            .testTag("btn_metric_dropdown")
            .clip(RoundedCornerShape(8.dp))
            .clickable { showMetricDropdown = true }
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = selectedMetric,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Dropdown",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        DropdownMenu(
          expanded = showMetricDropdown,
          onDismissRequest = { showMetricDropdown = false }
        ) {
          listOf("Daily Orders", "Daily Tips", "Daily Gas", "Daily Profit").forEach { metric ->
            DropdownMenuItem(
              text = { Text(metric) },
              onClick = {
                viewModel.setMetric(metric)
                showMetricDropdown = false
              }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Performance Chart Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .testTag("card_chart_container"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      shape = RoundedCornerShape(20.dp),
      border = CardDefaults.outlinedCardBorder()
    ) {
      if (!hasData) {
        // Empty State matching Screenshot 2
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 44.dp, horizontal = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          // Bar chart illustration
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.size(48.dp)) {
              val barW = 8.dp.toPx()
              // bar 1
              drawRoundRect(
                color = Color(0xFFE2E8F0),
                topLeft = Offset(4.dp.toPx(), 20.dp.toPx()),
                size = Size(barW, 24.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
              )
              // bar 2
              drawRoundRect(
                color = Color(0xFFC7D2FE),
                topLeft = Offset(16.dp.toPx(), 10.dp.toPx()),
                size = Size(barW, 34.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
              )
              // bar 3
              drawRoundRect(
                color = Color(0xFFA5B4FC),
                topLeft = Offset(28.dp.toPx(), 16.dp.toPx()),
                size = Size(barW, 28.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "No chart data",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "There is no data available for this period.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
        }
      } else {
        // Visual Interactive Bar Chart
        PerformanceBarChart(
          metric = selectedMetric,
          ordersList = ordersList,
          currency = settings.currency,
          daysInMonth = viewModel.getDaysInSelectedMonth()
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // TARGET TRACKER Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 22.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = "🎯", fontSize = 16.sp)
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "TARGET TRACKER",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.5.sp
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 3 Cards: Target | Completed | Remaining
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 1. Target Card
      TargetMetricCard(
        title = "Target",
        value = "${summary.targetOrders}",
        valueColor = MaterialTheme.colorScheme.onSurface,
        iconEmoji = "🎯",
        modifier = Modifier.weight(1f)
      )

      // 2. Completed Card
      TargetMetricCard(
        title = "Completed",
        value = "${summary.completedOrders}",
        valueColor = ProfitGreen,
        iconEmoji = "✅",
        modifier = Modifier.weight(1f)
      )

      // 3. Remaining Card
      TargetMetricCard(
        title = "Remaining",
        value = "${summary.remainingOrders}",
        valueColor = HourglassRed,
        iconEmoji = "⏳",
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Target Progress bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "${(summary.targetProgress * 100).toInt()}% completed",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = CyanDark
        )
        Text(
          text = "${summary.completedOrders} / ${summary.targetOrders} orders",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      LinearProgressIndicator(
        progress = { summary.targetProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = themeColors.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )
    }

    // Daily Earnings Goal Tracker
    if (settings.dailyEarningsGoal > 0) {
      Spacer(modifier = Modifier.height(20.dp))
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🎯", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Daily Goal: ${settings.currency} ${String.format("%.1f", settings.dailyEarningsGoal)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "${summary.daysHittingGoal} Days Met",
              fontSize = 13.sp,
              fontWeight = FontWeight.ExtraBold,
              color = ProfitGreen
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          val daysInMonthVal = viewModel.getDaysInSelectedMonth()
          val goalRatio = if (daysInMonthVal > 0) (summary.daysHittingGoal.toFloat() / daysInMonthVal).coerceIn(0f, 1f) else 0f

          LinearProgressIndicator(
            progress = { goalRatio },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = ProfitGreen,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${summary.daysHittingGoal} of $daysInMonthVal days achieved daily target (${(goalRatio * 100).toInt()}%)",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }

  // Export report dialog
  if (showExportDialog) {
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      title = { Text("$monthName $year Report Summary", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(text = exportedText, fontSize = 13.sp)
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, exportedText)
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share Monthly Report"))
            showExportDialog = false
          }
        ) {
          Text("Share", color = CyanDark, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}

@Composable
fun TargetMetricCard(
  title: String,
  value: String,
  valueColor: Color,
  iconEmoji: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(18.dp),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
      Text(
        text = title,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = value,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = valueColor
        )
        Text(text = iconEmoji, fontSize = 20.sp)
      }
    }
  }
}

@Composable
fun PerformanceBarChart(
  metric: String,
  ordersList: List<com.example.data.OrderDayEntity>,
  currency: String,
  daysInMonth: Int
) {
  val dayMap = ordersList.associateBy { it.day }
  val values = (1..daysInMonth).map { day ->
    val rec = dayMap[day]
    when (metric) {
      "Daily Tips" -> rec?.tip?.toFloat() ?: 0f
      "Daily Gas" -> rec?.gas?.toFloat() ?: 0f
      "Daily Profit" -> ((rec?.orders ?: 0) * 0.5f + (rec?.tip?.toFloat() ?: 0f) - (rec?.gas?.toFloat() ?: 0f)).coerceAtLeast(0f)
      else -> (rec?.orders ?: 0).toFloat()
    }
  }

  val maxValue = values.maxOrNull()?.coerceAtLeast(10f) ?: 10f
  val total = values.sum()
  val avg = if (daysInMonth > 0) total / daysInMonth else 0f

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "Total: ${if (metric == "Daily Orders") total.toInt().toString() else "$currency ${String.format("%.2f", total)}"}",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = CyanDark
      )
      Text(
        text = "Daily Avg: ${if (metric == "Daily Orders") String.format("%.1f", avg) else "$currency ${String.format("%.2f", avg)}"}",
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Horizontal scrollable chart for all days
    val scrollState = rememberScrollState()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(scrollState)
        .padding(vertical = 8.dp)
    ) {
      Canvas(
        modifier = Modifier
          .width((daysInMonth * 22).dp.coerceAtLeast(320.dp))
          .height(160.dp)
      ) {
        val chartHeight = size.height - 24.dp.toPx()
        val barWidth = 12.dp.toPx()
        val spacing = (size.width - (daysInMonth * barWidth)) / (daysInMonth + 1)

        // Draw baseline
        drawLine(
          color = Color.LightGray.copy(alpha = 0.5f),
          start = Offset(0f, chartHeight),
          end = Offset(size.width, chartHeight),
          strokeWidth = 1.dp.toPx()
        )

        values.forEachIndexed { i, v ->
          val x = spacing + i * (barWidth + spacing)
          val barHeight = (v / maxValue) * (chartHeight - 16.dp.toPx())
          val topY = chartHeight - barHeight

          if (v > 0) {
            drawRoundRect(
              brush = Brush.verticalGradient(
                colors = listOf(CyanPrimary, CyanDark),
                startY = topY,
                endY = chartHeight
              ),
              topLeft = Offset(x, topY),
              size = Size(barWidth, barHeight),
              cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
          } else {
            // Subtle dot on baseline for empty day
            drawCircle(
              color = Color.LightGray.copy(alpha = 0.4f),
              radius = 2.dp.toPx(),
              center = Offset(x + barWidth / 2, chartHeight - 2.dp.toPx())
            )
          }
        }
      }
    }

    // Days label note
    Text(
      text = "Days 1 to $daysInMonth (Swipe to see all days)",
      fontSize = 11.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth()
    )
  }
}
