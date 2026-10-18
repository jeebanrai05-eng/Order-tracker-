package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MaterialThemeColors
import java.util.Calendar

val MONTH_NAMES = listOf(
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
)

@Composable
fun MonthSelectorHeader(
  year: Int,
  month: Int,
  onPreviousMonth: () -> Unit,
  onNextMonth: () -> Unit,
  onSelectYearMonth: (Int, Int) -> Unit,
  onTodayClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var showPicker by remember { mutableStateOf(false) }
  val monthName = MONTH_NAMES.getOrElse(month - 1) { "October" }
  val themeColors = MaterialThemeColors

  val now = Calendar.getInstance()
  val isCurrentMonth = (now.get(Calendar.YEAR) == year && (now.get(Calendar.MONTH) + 1) == month)

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left circular button
    IconButton(
      onClick = onPreviousMonth,
      modifier = Modifier
        .testTag("btn_prev_month")
        .size(44.dp)
        .shadow(6.dp, CircleShape)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surface)
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = "Previous Month",
        tint = themeColors.primary,
        modifier = Modifier.size(26.dp)
      )
    }

    // Center month-year dropdown trigger with optional TODAY button
    Row(verticalAlignment = Alignment.CenterVertically) {
      Row(
        modifier = Modifier
          .testTag("btn_select_month_dialog")
          .clip(RoundedCornerShape(14.dp))
          .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
          .clickable { showPicker = true }
          .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "$monthName $year",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Default.ArrowDropDown,
          contentDescription = "Select Month",
          tint = themeColors.primary,
          modifier = Modifier.size(24.dp)
        )
      }

      if (onTodayClick != null) {
        Spacer(modifier = Modifier.width(8.dp))
        Box(
          modifier = Modifier
            .testTag("btn_jump_today")
            .clip(RoundedCornerShape(12.dp))
            .background(if (isCurrentMonth) themeColors.primary.copy(alpha = 0.15f) else themeColors.primary)
            .clickable(onClick = onTodayClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Today,
              contentDescription = null,
              tint = if (isCurrentMonth) themeColors.primary else Color.Black,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "TODAY",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (isCurrentMonth) themeColors.primary else Color.Black
            )
          }
        }
      }
    }

    // Right circular button
    IconButton(
      onClick = onNextMonth,
      modifier = Modifier
        .testTag("btn_next_month")
        .size(44.dp)
        .shadow(6.dp, CircleShape)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surface)
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = "Next Month",
        tint = themeColors.primary,
        modifier = Modifier.size(26.dp)
      )
    }
  }

  if (showPicker) {
    MonthYearPickerDialog(
      initialYear = year,
      initialMonth = month,
      onDismiss = { showPicker = false },
      onConfirm = { y, m ->
        onSelectYearMonth(y, m)
        showPicker = false
      }
    )
  }
}

@Composable
fun MonthYearPickerDialog(
  initialYear: Int,
  initialMonth: Int,
  onDismiss: () -> Unit,
  onConfirm: (Int, Int) -> Unit
) {
  var selectedYear by remember { mutableIntStateOf(initialYear) }
  var selectedMonth by remember { mutableIntStateOf(initialMonth) }
  val themeColors = MaterialThemeColors

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = { onConfirm(selectedYear, selectedMonth) },
        modifier = Modifier.testTag("btn_confirm_month_picker")
      ) {
        Text("Select", color = themeColors.primary, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    },
    title = {
      Text("Choose Month & Year", fontWeight = FontWeight.Bold)
    },
    text = {
      Column {
        // Year selector row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { selectedYear-- }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev Year")
          }
          Text(text = "$selectedYear", fontSize = 20.sp, fontWeight = FontWeight.Bold)
          IconButton(onClick = { selectedYear++ }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Year")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Month grid
        LazyVerticalGrid(
          columns = GridCells.Fixed(3),
          modifier = Modifier.height(200.dp)
        ) {
          itemsIndexed(MONTH_NAMES) { index, name ->
            val monthNum = index + 1
            val isSelected = monthNum == selectedMonth
            Card(
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) themeColors.primary else MaterialTheme.colorScheme.surfaceVariant
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .padding(4.dp)
                .clickable { selectedMonth = monthNum }
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = name.substring(0, 3),
                  color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 14.sp
                )
              }
            }
          }
        }
      }
    }
  )
}
