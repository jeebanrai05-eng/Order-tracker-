package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun OrderTrackerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  themeName: String = "Neon Cyan",
  content: @Composable () -> Unit
) {
  val targetPalette = getThemePalette(themeName)

  // Silky Smooth Animated Color Interpolation across Theme Switches
  val animatedPrimary by animateColorAsState(
    targetValue = targetPalette.primary,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
    label = "animPrimary"
  )
  val animatedVariant by animateColorAsState(
    targetValue = targetPalette.variant,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
    label = "animVariant"
  )
  val animatedAccent by animateColorAsState(
    targetValue = targetPalette.accent,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
    label = "animAccent"
  )
  val animatedBgDark by animateColorAsState(
    targetValue = targetPalette.backgroundDark,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
    label = "animBgDark"
  )
  val animatedBgGlow by animateColorAsState(
    targetValue = targetPalette.backgroundGlow,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
    label = "animBgGlow"
  )
  val animatedCardBg by animateColorAsState(
    targetValue = targetPalette.cardBackground,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
    label = "animCardBg"
  )
  val animatedCardBorder by animateColorAsState(
    targetValue = targetPalette.cardBorder,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
    label = "animCardBorder"
  )

  val animatedPalette = AppThemeColors(
    primary = animatedPrimary,
    variant = animatedVariant,
    accent = animatedAccent,
    gradient = Brush.horizontalGradient(listOf(animatedPrimary, animatedVariant)),
    backgroundDark = animatedBgDark,
    backgroundGlow = animatedBgGlow,
    cardBackground = animatedCardBg,
    cardBorder = animatedCardBorder
  )

  val colorScheme = if (darkTheme) {
    darkColorScheme(
      primary = animatedPrimary,
      secondary = animatedVariant,
      tertiary = ProfitGreen,
      background = animatedBgDark,
      surface = animatedCardBg,
      surfaceVariant = animatedCardBg,
      onPrimary = Color.Black,
      onSecondary = Color.White,
      onBackground = TextPrimaryDark,
      onSurface = TextPrimaryDark,
      outline = animatedCardBorder
    )
  } else {
    lightColorScheme(
      primary = animatedPrimary,
      secondary = animatedVariant,
      tertiary = ProfitGreen,
      background = BackgroundLight,
      surface = SurfaceLight,
      surfaceVariant = SurfaceCardLight,
      onPrimary = Color.White,
      onSecondary = Color.White,
      onBackground = TextPrimaryLight,
      onSurface = TextPrimaryLight,
      outline = BorderLight
    )
  }

  CompositionLocalProvider(LocalAppThemeColors provides animatedPalette) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
