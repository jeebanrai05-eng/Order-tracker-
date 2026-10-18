package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GoogleLogoIcon(
  modifier: Modifier = Modifier,
  size: Dp = 22.dp
) {
  Canvas(modifier = modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height
    val cx = w / 2f
    val cy = h / 2f
    val radius = w / 2f

    // Official Google branding colors
    val blue = Color(0xFF4285F4)
    val red = Color(0xFFEA4335)
    val yellow = Color(0xFFFBBC05)
    val green = Color(0xFF34A853)

    // Red segment (top)
    val redPath = Path().apply {
      moveTo(cx, cy)
      arcTo(Rect(0f, 0f, w, h), 220f, 90f, false)
      close()
    }
    drawPath(redPath, red)

    // Yellow segment (top left to bottom left)
    val yellowPath = Path().apply {
      moveTo(cx, cy)
      arcTo(Rect(0f, 0f, w, h), 140f, 85f, false)
      close()
    }
    drawPath(yellowPath, yellow)

    // Green segment (bottom)
    val greenPath = Path().apply {
      moveTo(cx, cy)
      arcTo(Rect(0f, 0f, w, h), 40f, 105f, false)
      close()
    }
    drawPath(greenPath, green)

    // Blue segment (right and horizontal bar)
    val bluePath = Path().apply {
      moveTo(cx, cy)
      arcTo(Rect(0f, 0f, w, h), 310f, 95f, false)
      close()
    }
    drawPath(bluePath, blue)

    // Inner cutout to form the 'G'
    val innerRadius = radius * 0.55f
    drawCircle(
      color = Color.White,
      radius = innerRadius,
      center = Offset(cx, cy)
    )

    // Horizontal bar of the 'G'
    drawRect(
      color = blue,
      topLeft = Offset(cx, cy - (h * 0.12f)),
      size = Size(w * 0.5f, h * 0.24f)
    )

    // Cutout right top wedge
    val wedgePath = Path().apply {
      moveTo(cx, cy)
      lineTo(w, cy)
      lineTo(w, cy - (h * 0.35f))
      lineTo(cx, cy - (h * 0.35f))
      close()
    }
    drawPath(wedgePath, Color.White)
  }
}
