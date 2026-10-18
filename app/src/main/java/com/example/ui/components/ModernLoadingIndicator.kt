package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MaterialThemeColors

@Composable
fun ModernLoadingIndicator(
  modifier: Modifier = Modifier,
  size: Dp = 48.dp,
  strokeWidth: Dp = 4.dp,
  message: String? = null
) {
  val themeColors = MaterialThemeColors
  val infiniteTransition = rememberInfiniteTransition(label = "loading_anim")

  val rotationOuter by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation_outer"
  )

  val rotationInner by infiniteTransition.animateFloat(
    initialValue = 360f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 900, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation_inner"
  )

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier.size(size),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(size)) {
        val s = strokeWidth.toPx()

        // Outer gradient ring
        rotate(rotationOuter) {
          drawArc(
            brush = Brush.sweepGradient(
              listOf(
                Color.Transparent,
                themeColors.primary.copy(alpha = 0.3f),
                themeColors.primary
              )
            ),
            startAngle = 0f,
            sweepAngle = 280f,
            useCenter = false,
            style = Stroke(width = s, cap = StrokeCap.Round)
          )
        }

        // Inner counter-rotating ring
        val innerPadding = s * 2.2f
        val innerSize = Size(this.size.width - innerPadding * 2, this.size.height - innerPadding * 2)
        rotate(rotationInner) {
          drawArc(
            color = themeColors.variant,
            startAngle = 45f,
            sweepAngle = 200f,
            useCenter = false,
            topLeft = Offset(innerPadding, innerPadding),
            size = innerSize,
            style = Stroke(width = s * 0.75f, cap = StrokeCap.Round)
          )
        }

        // Center pulsing dot
        drawCircle(
          color = themeColors.primary,
          radius = (s * 0.9f) * pulseScale,
          center = center
        )
      }
    }

    if (!message.isNullOrBlank()) {
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = message,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
