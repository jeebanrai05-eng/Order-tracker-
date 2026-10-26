package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.MONTH_NAMES
import com.example.ui.theme.MaterialThemeColors
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.WarningRed
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
  val currentThemeColors = MaterialThemeColors

  var showMetricDropdown by remember { mutableStateOf(false) }
  var showExportDialog by remember { mutableStateOf(false) }
  var showMonthPicker by remember { mutableStateOf(false) }
  var exportedText by remember { mutableStateOf("") }

  val daysInMonth = viewModel.getDaysInSelectedMonth()

  // Theme-Adaptive Net Profit Hero Card Gradient
  val heroCardGradient = remember(currentThemeColors.primary) {
    Brush.linearGradient(
      colors = listOf(
        currentThemeColors.variant,
        currentThemeColors.primary.copy(alpha = 0.85f),
        Color(0xFF0F172A).copy(alpha = 0.95f)
      )
    )
  }

  // Dynamic Theme Adaptive Background
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(currentThemeColors.backgroundDark)
  ) {
    // 1. Top-Right Ambient Liquid Glow Aura (Adapts to Active Theme)
    Box(
      modifier = Modifier
        .size(360.dp)
        .offset(x = 130.dp, y = (-70).dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              currentThemeColors.backgroundGlow.copy(alpha = 0.22f),
              currentThemeColors.variant.copy(alpha = 0.08f),
              Color.Transparent
            )
          )
        )
    )

    // 2. Center-Left Ambient Glow Aura (Adapts to Active Theme)
    Box(
      modifier = Modifier
        .size(340.dp)
        .offset(x = (-120).dp, y = 380.dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              currentThemeColors.variant.copy(alpha = 0.16f),
              currentThemeColors.accent.copy(alpha = 0.06f),
              Color.Transparent
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp)
        .padding(bottom = 120.dp)
    ) {
      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // Top Header: ☰ October 2026 Report  [🗂️]
      // ==========================================
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          IconButton(
            onClick = onOpenMenu,
            modifier = Modifier.testTag("btn_chart_menu")
          ) {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = "Menu",
              tint = currentThemeColors.primary,
              modifier = Modifier.size(28.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Text(
              text = "$monthName $year Report",
              fontSize = 22.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              letterSpacing = (-0.4).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Track your performance and growth",
              fontSize = 12.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        // Right Action: Select Month / Export Report
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, currentThemeColors.cardBorder, RoundedCornerShape(12.dp))
            .clickable { showMonthPicker = true }
            .testTag("btn_month_picker"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = "Select Month",
            tint = currentThemeColors.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // ==========================================
      // 1. HERO CARD: NET PROFIT (Wave Gradient + Golden KD Bag)
      // ==========================================
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(heroCardGradient)
          .border(1.2.dp, currentThemeColors.primary.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
          .padding(horizontal = 20.dp, vertical = 20.dp)
          .testTag("card_net_profit")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color(0xFF67E8F9),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "NET PROFIT",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF67E8F9),
                letterSpacing = 1.sp
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "${settings.currency} ${String.format("%.3f", summary.netProfit)}",
              fontSize = 30.sp,
              fontWeight = FontWeight.Black,
              color = Color.White,
              letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Base + Tips (${String.format("%.2f", summary.totalTips)}) - Gas (${String.format("%.2f", summary.totalGas)})",
              fontSize = 11.sp,
              color = Color.White.copy(alpha = 0.85f),
              fontWeight = FontWeight.Medium
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          // Golden KD Bag Illustration with Stacked Coins and Rising Green Arrow
          NetProfitIllustration(
            currency = settings.currency.ifBlank { "KD" }
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // 2. CARD: ORDER PERFORMANCE (Themed Bar Chart)
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_chart_container"),
        colors = CardDefaults.cardColors(containerColor = currentThemeColors.cardBackground),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, currentThemeColors.cardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          // Card Header: ORDER PERFORMANCE & Dropdown Filter
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = currentThemeColors.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "ORDER PERFORMANCE",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
              )
            }

            // Dropdown Filter: Daily Orders ▾
            Box {
              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF070E1A))
                  .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                  .clickable { showMetricDropdown = true }
                  .padding(horizontal = 10.dp, vertical = 5.dp)
                  .testTag("btn_metric_dropdown"),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = selectedMetric,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.ArrowDropDown,
                  contentDescription = "Dropdown",
                  tint = Color(0xFF94A3B8),
                  modifier = Modifier.size(16.dp)
                )
              }

              DropdownMenu(
                expanded = showMetricDropdown,
                onDismissRequest = { showMetricDropdown = false },
                modifier = Modifier.background(Color(0xFF0F172A))
              ) {
                listOf("Daily Orders", "Daily Tips", "Daily Gas", "Daily Profit").forEach { metric ->
                  DropdownMenuItem(
                    text = { Text(metric, color = Color.White, fontSize = 13.sp) },
                    onClick = {
                      viewModel.setMetric(metric)
                      showMetricDropdown = false
                    }
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Chart Summary Numbers: Total & Daily Avg
          val dayMap = ordersList.associateBy { it.day }
          val values = (1..daysInMonth).map { day ->
            val rec = dayMap[day]
            when (selectedMetric) {
              "Daily Tips" -> rec?.tip?.toFloat() ?: 0f
              "Daily Gas" -> rec?.gas?.toFloat() ?: 0f
              "Daily Profit" -> ((rec?.orders ?: 0) * 0.5f + (rec?.tip?.toFloat() ?: 0f) - (rec?.gas?.toFloat() ?: 0f)).coerceAtLeast(0f)
              else -> (rec?.orders ?: 0).toFloat()
            }
          }
          val total = values.sum()
          val avg = if (daysInMonth > 0) total / daysInMonth else 0f

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Total: ${if (selectedMetric == "Daily Orders") total.toInt().toString() else "${settings.currency} ${String.format("%.2f", total)}"}",
              fontSize = 15.sp,
              fontWeight = FontWeight.ExtraBold,
              color = currentThemeColors.primary
            )
            Text(
              text = "Daily Avg: ${if (selectedMetric == "Daily Orders") String.format("%.1f", avg) else "${settings.currency} ${String.format("%.2f", avg)}"}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF94A3B8)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Interactive Themed Bar Chart Canvas
          ThemedPerformanceBarChart(
            values = values,
            daysInMonth = daysInMonth,
            primaryColor = currentThemeColors.primary,
            variantColor = currentThemeColors.variant
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // 3. TARGET TRACKER SECTION (3 Metric Cards + Glowing Bar)
      // ==========================================
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
      ) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color(0xFFE11D48).copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Text(text = "🎯", fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "TARGET TRACKER",
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White,
          letterSpacing = 0.5.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 3 Summary Cards: Target | Completed | Remaining
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // 1. Target Card (Deep Blue)
        TargetMetricBox(
          title = "Target",
          value = "${summary.targetOrders}",
          valueColor = Color.White,
          badgeColor = Color(0xFF2563EB),
          iconText = "🎯",
          badgeIconText = "🎯",
          borderColor = Color(0xFF2563EB).copy(alpha = 0.4f),
          containerColor = Color(0xFF0C182E),
          modifier = Modifier.weight(1f)
        )

        // 2. Completed Card (Deep Emerald / Theme Tinted)
        TargetMetricBox(
          title = "Completed",
          value = "${summary.completedOrders}",
          valueColor = currentThemeColors.primary,
          badgeColor = Color(0xFF059669),
          iconText = "✓",
          badgeIconText = "✓",
          borderColor = currentThemeColors.primary.copy(alpha = 0.5f),
          containerColor = Color(0xFF06221E),
          modifier = Modifier.weight(1f)
        )

        // 3. Remaining Card (Deep Purple)
        TargetMetricBox(
          title = "Remaining",
          value = "${summary.remainingOrders}",
          valueColor = Color.White,
          badgeColor = Color(0xFF7C3AED),
          iconText = "⌛",
          badgeIconText = "⌛",
          borderColor = Color(0xFF7C3AED).copy(alpha = 0.4f),
          containerColor = Color(0xFF1C102E),
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Target Progress Info & Glowing Progress Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${(summary.targetProgress * 100).toInt()}% completed",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = currentThemeColors.primary
        )
        Text(
          text = "${summary.completedOrders} / ${summary.targetOrders} orders",
          fontSize = 12.sp,
          color = Color(0xFF94A3B8),
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Glowing Horizontal Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp))
          .background(Color(0xFF131C31))
      ) {
        val progressFraction = summary.targetProgress.coerceIn(0f, 1f)
        if (progressFraction > 0f) {
          Box(
            modifier = Modifier
              .fillMaxWidth(fraction = progressFraction)
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(currentThemeColors.primary, currentThemeColors.variant)
                )
              )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // ==========================================
      // 4. CARD: DAILY GOAL STATUS
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            scope.launch {
              exportedText = viewModel.exportReport()
              showExportDialog = true
            }
          },
        colors = CardDefaults.cardColors(
          containerColor = Color(0xFF091629)
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, currentThemeColors.cardBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE11D48).copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = "🎯", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "Daily Goal: ${settings.currency} ${String.format("%.1f", settings.dailyEarningsGoal)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${summary.daysHittingGoal} of $daysInMonth days achieved daily target (${if (daysInMonth > 0) (summary.daysHittingGoal * 100 / daysInMonth) else 0}%)",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // Green Pill Badge
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF064E3B).copy(alpha = 0.7f),
              border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.8f))
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${summary.daysHittingGoal} Days Met",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF34D399)
                )
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
              text = "›",
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF94A3B8)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Month Picker Dialog
  if (showMonthPicker) {
    AlertDialog(
      onDismissRequest = { showMonthPicker = false },
      title = { Text("Select Report Month", fontWeight = FontWeight.Bold, color = Color.White) },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text("Choose a month to review earnings and delivery trends:", fontSize = 12.sp, color = Color(0xFF94A3B8))
          Spacer(modifier = Modifier.height(12.dp))
          MONTH_NAMES.chunked(3).forEachIndexed { rowIdx, rowMonths ->
            Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              rowMonths.forEachIndexed { colIdx, mName ->
                val targetM = rowIdx * 3 + colIdx + 1
                val isSelected = (targetM == month)
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) currentThemeColors.primary else Color(0xFF070E1A))
                    .border(1.dp, if (isSelected) currentThemeColors.primary else Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .clickable {
                      viewModel.setYearAndMonth(year, targetM)
                      showMonthPicker = false
                    }
                    .padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = mName.take(3),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Black else Color.White
                  )
                }
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showMonthPicker = false }) {
          Text("Done", color = currentThemeColors.primary, fontWeight = FontWeight.Bold)
        }
      },
      containerColor = Color(0xFF0F172A)
    )
  }

  // Export Report Dialog
  if (showExportDialog) {
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      title = { Text("$monthName $year Report Summary", fontWeight = FontWeight.Bold, color = Color.White) },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(text = exportedText, fontSize = 12.sp, color = Color(0xFF94A3B8), lineHeight = 16.sp)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, exportedText)
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Share Monthly Report"))
            showExportDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = currentThemeColors.primary, contentColor = Color.Black)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share Report", fontWeight = FontWeight.Bold)
          }
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Close", color = Color(0xFF94A3B8))
        }
      },
      containerColor = Color(0xFF0F172A)
    )
  }
}

/**
 * Net Profit Golden KD Money Bag Illustration.
 */
@Composable
private fun NetProfitIllustration(
  currency: String = "KD",
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(76.dp)
      .clip(CircleShape)
      .background(Color(0xFF0284C7).copy(alpha = 0.25f))
      .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(56.dp)) {
      val bagColor = Color(0xFFFFB300)
      val bagDark = Color(0xFFD97706)
      val coinColor = Color(0xFFFFD54F)
      val arrowGreen = Color(0xFF00E676)

      // 1. Draw Money Bag Body
      val bagPath = Path().apply {
        moveTo(size.width * 0.44f, size.height * 0.28f)
        cubicTo(
          size.width * 0.16f, size.height * 0.36f,
          size.width * 0.08f, size.height * 0.85f,
          size.width * 0.44f, size.height * 0.85f
        )
        cubicTo(
          size.width * 0.72f, size.height * 0.85f,
          size.width * 0.72f, size.height * 0.36f,
          size.width * 0.44f, size.height * 0.28f
        )
        close()
      }
      drawPath(
        path = bagPath,
        brush = Brush.verticalGradient(
          listOf(coinColor, bagColor, bagDark),
          startY = size.height * 0.28f,
          endY = size.height * 0.85f
        )
      )

      // 2. Draw Neck tie / string
      drawOval(
        color = Color(0xFF92400E),
        topLeft = Offset(size.width * 0.34f, size.height * 0.25f),
        size = Size(size.width * 0.20f, size.height * 0.08f)
      )

      // 3. Draw Top ruffle
      drawOval(
        color = coinColor,
        topLeft = Offset(size.width * 0.30f, size.height * 0.17f),
        size = Size(size.width * 0.28f, size.height * 0.12f)
      )

      // 4. Draw Stacked Gold Coins on lower right
      drawOval(
        color = coinColor,
        topLeft = Offset(size.width * 0.58f, size.height * 0.72f),
        size = Size(size.width * 0.32f, size.height * 0.14f)
      )
      drawOval(
        color = bagDark,
        topLeft = Offset(size.width * 0.58f, size.height * 0.72f),
        size = Size(size.width * 0.32f, size.height * 0.14f),
        style = Stroke(1.5f)
      )

      drawOval(
        color = coinColor,
        topLeft = Offset(size.width * 0.58f, size.height * 0.62f),
        size = Size(size.width * 0.32f, size.height * 0.14f)
      )
      drawOval(
        color = bagDark,
        topLeft = Offset(size.width * 0.58f, size.height * 0.62f),
        size = Size(size.width * 0.32f, size.height * 0.14f),
        style = Stroke(1.5f)
      )

      // 5. Draw Rising Neon Green Arrow
      val arrowPath = Path().apply {
        moveTo(size.width * 0.62f, size.height * 0.44f)
        lineTo(size.width * 0.88f, size.height * 0.16f)
        lineTo(size.width * 0.70f, size.height * 0.16f)
        moveTo(size.width * 0.88f, size.height * 0.16f)
        lineTo(size.width * 0.88f, size.height * 0.34f)
      }
      drawPath(
        path = arrowPath,
        color = arrowGreen,
        style = Stroke(
          width = 3.5.dp.toPx(),
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
    }

    // Centered "KD" text on the golden bag
    Text(
      text = if (currency.contains("K.D") || currency.contains("KD")) "KD" else currency.take(2).uppercase(),
      fontSize = 11.sp,
      fontWeight = FontWeight.Black,
      color = Color(0xFF78350F),
      modifier = Modifier.offset(x = (-5).dp, y = 5.dp)
    )
  }
}

/**
 * Clean Themed Bar Chart Canvas with Gridlines & X-Axis labels.
 */
@Composable
private fun ThemedPerformanceBarChart(
  values: List<Float>,
  daysInMonth: Int,
  primaryColor: Color,
  variantColor: Color
) {
  val maxValue = values.maxOrNull()?.coerceAtLeast(10f) ?: 10f
  val scrollState = rememberScrollState()

  Column(modifier = Modifier.fillMaxWidth()) {
    // Horizontal scrollable chart container
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(scrollState)
        .padding(vertical = 4.dp)
    ) {
      Canvas(
        modifier = Modifier
          .width((daysInMonth * 20).dp.coerceAtLeast(320.dp))
          .height(170.dp)
      ) {
        val chartHeight = size.height - 24.dp.toPx()
        val barWidth = 9.dp.toPx()
        val spacing = (size.width - (daysInMonth * barWidth)) / (daysInMonth + 1)

        // Draw 3 horizontal gridlines
        val gridColor = Color(0xFF1E293B).copy(alpha = 0.7f)
        listOf(0.25f, 0.5f, 0.75f).forEach { fraction ->
          val y = chartHeight * fraction
          drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx()
          )
        }

        // Draw baseline
        drawLine(
          color = Color(0xFF334155),
          start = Offset(0f, chartHeight),
          end = Offset(size.width, chartHeight),
          strokeWidth = 1.5.dp.toPx()
        )

        // Draw bars
        values.forEachIndexed { i, v ->
          val x = spacing + i * (barWidth + spacing)
          val barHeight = (v / maxValue) * (chartHeight - 12.dp.toPx())
          val topY = chartHeight - barHeight

          if (v > 0) {
            drawRoundRect(
              brush = Brush.verticalGradient(
                colors = listOf(primaryColor, variantColor),
                startY = topY,
                endY = chartHeight
              ),
              topLeft = Offset(x, topY),
              size = Size(barWidth, barHeight),
              cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
          } else {
            // Subtle baseline dot for days with 0 records
            drawCircle(
              color = Color(0xFF334155),
              radius = 2.dp.toPx(),
              center = Offset(x + barWidth / 2, chartHeight - 2.dp.toPx())
            )
          }
        }
      }
    }

    // X-Axis Day Numbers: 1, 5, 10, 15, 20, 25, 31
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      listOf(1, 5, 10, 15, 20, 25, daysInMonth).forEach { d ->
        Text(
          text = "$d",
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF64748B)
        )
      }
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = "Days 1 to $daysInMonth (Swipe to see all days)",
      fontSize = 11.sp,
      color = Color(0xFF94A3B8),
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

/**
 * Metric Card for Target Tracker matching screenshot.
 */
@Composable
private fun TargetMetricBox(
  title: String,
  value: String,
  valueColor: Color,
  badgeColor: Color,
  iconText: String,
  badgeIconText: String,
  borderColor: Color,
  containerColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(containerColor)
      .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
      .padding(horizontal = 12.dp, vertical = 12.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {
        Box(
          modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(badgeColor.copy(alpha = 0.25f)),
          contentAlignment = Alignment.Center
        ) {
          Text(text = iconText, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = title,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF94A3B8)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = value,
          fontSize = 20.sp,
          fontWeight = FontWeight.ExtraBold,
          color = valueColor
        )

        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Text(text = badgeIconText, fontSize = 11.sp)
        }
      }
    }
  }
}
