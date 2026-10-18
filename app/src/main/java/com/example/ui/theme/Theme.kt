package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun OrderTrackerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  themeName: String = "Neon Cyan",
  content: @Composable () -> Unit
) {
  val palette = getThemePalette(themeName)

  val colorScheme = if (darkTheme) {
    darkColorScheme(
      primary = palette.primary,
      secondary = palette.variant,
      tertiary = ProfitGreen,
      background = BackgroundDark,
      surface = SurfaceDark,
      surfaceVariant = SurfaceCardDark,
      onPrimary = Color.Black,
      onSecondary = Color.White,
      onBackground = TextPrimaryDark,
      onSurface = TextPrimaryDark,
      outline = BorderDark
    )
  } else {
    lightColorScheme(
      primary = palette.primary,
      secondary = palette.variant,
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

  CompositionLocalProvider(LocalAppThemeColors provides palette) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
