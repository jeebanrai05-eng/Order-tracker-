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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderDayEntity
import com.example.ui.theme.CyanPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEditSheet(
  day: Int,
  monthName: String,
  year: Int,
  currency: String,
  currentRecord: OrderDayEntity?,
  onDismiss: () -> Unit,
  onSave: (day: Int, orders: Int, tip: Double, gas: Double, notes: String) -> Unit,
  onClear: (day: Int) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var ordersText by remember { mutableStateOf(if ((currentRecord?.orders ?: 0) > 0) currentRecord?.orders.toString() else "") }
  var tipText by remember { mutableStateOf(if ((currentRecord?.tip ?: 0.0) > 0.0) String.format("%.3f", currentRecord?.tip) else "") }
  var gasText by remember { mutableStateOf(if ((currentRecord?.gas ?: 0.0) > 0.0) String.format("%.3f", currentRecord?.gas) else "") }
  var notesText by remember { mutableStateOf(currentRecord?.notes ?: "") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    dragHandle = { BottomSheetDefaults.DragHandle() }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .imePadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp)
        .padding(bottom = 36.dp)
        .navigationBarsPadding()
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Day $day",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "$monthName $day, $year",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 1. Orders Input with +/- quick buttons
      Text(
        text = "🛍️ Orders Count",
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = ordersText,
          onValueChange = { ordersText = it.filter { char -> char.isDigit() } },
          placeholder = { Text("0") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("input_day_orders")
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = {
            val current = ordersText.toIntOrNull() ?: 0
            if (current > 0) ordersText = (current - 1).toString()
          },
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Icon(Icons.Default.Remove, contentDescription = "Minus Order")
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
          onClick = {
            val current = ordersText.toIntOrNull() ?: 0
            ordersText = (current + 1).toString()
          },
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Icon(Icons.Default.Add, contentDescription = "Plus Order")
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Tip Input
      Text(
        text = "💵 Tip Amount ($currency)",
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = tipText,
        onValueChange = { tipText = it },
        placeholder = { Text("0.000") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_day_tip")
      )
      // Quick chips for Tip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(0.500, 1.000, 2.000, 5.000).forEach { inc ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .clickable {
                val current = tipText.toDoubleOrNull() ?: 0.0
                tipText = String.format("%.3f", current + inc)
              }
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text("+$inc", fontSize = 12.sp, fontWeight = FontWeight.Medium)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Gas Input
      Text(
        text = "⛽ Fuel / Gas ($currency)",
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = gasText,
        onValueChange = { gasText = it },
        placeholder = { Text("0.000") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_day_gas")
      )
      // Quick chips for Gas
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(1.000, 2.000, 3.000, 5.000).forEach { inc ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .clickable {
                val current = gasText.toDoubleOrNull() ?: 0.0
                gasText = String.format("%.3f", current + inc)
              }
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text("+$inc", fontSize = 12.sp, fontWeight = FontWeight.Medium)
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = {
            onClear(day)
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("btn_clear_day")
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
          Text("Clear")
        }

        Button(
          onClick = {
            val orders = ordersText.toIntOrNull() ?: 0
            val tip = tipText.toDoubleOrNull() ?: 0.0
            val gas = gasText.toDoubleOrNull() ?: 0.0
            onSave(day, orders, tip, gas, notesText)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(2f)
            .height(50.dp)
            .testTag("btn_save_day")
        ) {
          Text("Save Entry", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      }
    }
  }
}
