package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Neon Cyan Palette
val CyanPrimary = Color(0xFF00E5FF)
val CyanVariant = Color(0xFF00B4D8)
val CyanDark = Color(0xFF0096C7)
val CyanAccent = Color(0xFF18E0F7)

// Mid Purple Palette
val PurplePrimary = Color(0xFF8B5CF6)
val PurpleVariant = Color(0xFF7C3AED)
val PurpleDark = Color(0xFF6D28D9)
val PurpleAccent = Color(0xFFA855F7)

// Luxury Gold Palette
val GoldPrimary = Color(0xFFF59E0B)
val GoldVariant = Color(0xFFD97706)
val GoldDark = Color(0xFFB45309)
val GoldAccent = Color(0xFFFBBF24)

// Status colors
val ProfitGreen = Color(0xFF00C853)
val WarningRed = Color(0xFFFF334B)
val HourglassRed = Color(0xFFFF5252)
val GoldYellow = Color(0xFFFFD166)

// Light Theme Neutrals
val BackgroundLight = Color(0xFFF7F9FC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceCardLight = Color(0xFFFFFFFF)
val TextPrimaryLight = Color(0xFF1E293B)
val TextSecondaryLight = Color(0xFF64748B)
val BorderLight = Color(0xFFE2E8F0)
val DayBoxBgLight = Color(0xFFF1F5F9)

// Dark Theme Neutrals (Night Mode)
val BackgroundDark = Color(0xFF0F172A)
val SurfaceDark = Color(0xFF1E293B)
val SurfaceCardDark = Color(0xFF1E293B)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val BorderDark = Color(0xFF334155)
val DayBoxBgDark = Color(0xFF334155)

// Floating Bottom Bar
val BottomBarDark = Color(0xFF181B20)
val BottomBarItemUnselected = Color(0xFF9E9E9E)

data class AppThemeColors(
  val primary: Color,
  val variant: Color,
  val accent: Color,
  val gradient: Brush
)

val LocalAppThemeColors = compositionLocalOf {
  AppThemeColors(
    primary = CyanPrimary,
    variant = CyanVariant,
    accent = CyanAccent,
    gradient = Brush.horizontalGradient(listOf(CyanPrimary, CyanVariant))
  )
}

val MaterialThemeColors: AppThemeColors
  @Composable
  @ReadOnlyComposable
  get() = LocalAppThemeColors.current

fun getThemePalette(themeName: String): AppThemeColors {
  return when (themeName) {
    "Mid Purple" -> AppThemeColors(
      primary = PurplePrimary,
      variant = PurpleVariant,
      accent = PurpleAccent,
      gradient = Brush.horizontalGradient(listOf(PurpleAccent, PurpleDark))
    )
    "Luxury Gold" -> AppThemeColors(
      primary = GoldPrimary,
      variant = GoldVariant,
      accent = GoldAccent,
      gradient = Brush.horizontalGradient(listOf(GoldAccent, GoldVariant))
    )
    else -> AppThemeColors(
      primary = CyanPrimary,
      variant = CyanVariant,
      accent = CyanAccent,
      gradient = Brush.horizontalGradient(listOf(CyanPrimary, CyanVariant))
    )
  }
}
