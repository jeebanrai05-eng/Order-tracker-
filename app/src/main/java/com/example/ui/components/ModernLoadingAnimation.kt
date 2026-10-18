package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.CyanVariant
import com.example.ui.theme.MaterialThemeColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern high-tech loading spinner with dual orbital rings, glowing particles,
 * and delivery pulse center.
 */
@Composable
fun ModernLoadingSpinner(
  modifier: Modifier = Modifier,
  size: Dp = 64.dp,
  strokeWidth: Dp = 4.dp
) {
  val themeColors = MaterialThemeColors
  val infiniteTransition = rememberInfiniteTransition(label = "modern_loader")

  val spinOuter by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "spin_outer"
  )

  val spinInner by infiniteTransition.animateFloat(
    initialValue = 360f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "spin_inner"
  )

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val particleAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "particle_alpha"
  )

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val sw = strokeWidth.toPx()
      val w = this.size.width
      val h = this.size.height

      // 1. Subtle Outer Track
      drawCircle(
        color = themeColors.primary.copy(alpha = 0.08f),
        radius = w / 2 - sw,
        style = Stroke(width = sw)
      )

      // 2. Outer Gradient Arc with spinning
      rotate(spinOuter) {
        drawArc(
          brush = Brush.sweepGradient(
            listOf(
              Color.Transparent,
              themeColors.variant.copy(alpha = 0.2f),
              themeColors.primary
            )
          ),
          startAngle = 0f,
          sweepAngle = 260f,
          useCenter = false,
          topLeft = Offset(sw, sw),
          size = Size(w - 2 * sw, h - 2 * sw),
          style = Stroke(width = sw, cap = StrokeCap.Round)
        )

        // Orbital comet head dot
        val headAngleRad = Math.toRadians(260.0).toFloat()
        val r = (w - 2 * sw) / 2
        val cx = w / 2 + r * cos(headAngleRad)
        val cy = h / 2 + r * sin(headAngleRad)
        drawCircle(
          color = CyanAccent,
          radius = sw * 0.9f,
          center = Offset(cx, cy)
        )
      }

      // 3. Inner Reverse Ring
      val innerMargin = sw * 2.8f
      val innerW = w - 2 * innerMargin
      val innerH = h - 2 * innerMargin
      rotate(spinInner) {
        drawArc(
          brush = Brush.sweepGradient(
            listOf(
              Color.Transparent,
              themeColors.accent.copy(alpha = 0.3f),
              themeColors.variant
            )
          ),
          startAngle = 45f,
          sweepAngle = 180f,
          useCenter = false,
          topLeft = Offset(innerMargin, innerMargin),
          size = Size(innerW, innerH),
          style = Stroke(width = sw * 0.75f, cap = StrokeCap.Round)
        )
      }

      // 4. Glowing center core
      drawCircle(
        color = themeColors.primary.copy(alpha = 0.15f * particleAlpha),
        radius = (w * 0.22f) * pulseScale,
        center = center
      )
    }

    // Center icon pulse
    Box(
      modifier = Modifier
        .scale(pulseScale)
        .size(size * 0.38f)
        .clip(CircleShape)
        .background(themeColors.primary.copy(alpha = 0.18f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.LocalShipping,
        contentDescription = null,
        tint = themeColors.primary,
        modifier = Modifier.size(size * 0.24f)
      )
    }
  }
}

/**
 * Full modern loading card with title and subtitle
 */
@Composable
fun ModernLoadingCard(
  message: String = "Syncing with Cloud...",
  subMessage: String = "Securing your orders & shifts",
  modifier: Modifier = Modifier
) {
  val themeColors = MaterialThemeColors
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder(),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      ModernLoadingSpinner(size = 72.dp, strokeWidth = 4.5.dp)

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = message,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
      )

      if (subMessage.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = subMessage,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

/**
 * Fullscreen modern loading dialog overlay
 */
@Composable
fun ModernLoadingOverlay(
  isVisible: Boolean,
  message: String = "Connecting to Cloud...",
  subMessage: String = "Please wait a moment"
) {
  AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn(),
    exit = fadeOut()
  ) {
    Dialog(
      onDismissRequest = {},
      properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
      ModernLoadingCard(
        message = message,
        subMessage = subMessage
      )
    }
  }
}
