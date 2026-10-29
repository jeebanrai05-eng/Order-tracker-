package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// 1. Neon Cyan Palette (Matrix Cyber Aqua)
val CyanPrimary = Color(0xFF00E5FF)
val CyanVariant = Color(0xFF00B4D8)
val CyanDark = Color(0xFF0096C7)
val CyanAccent = Color(0xFF18E0F7)

// 2. Mid Purple Palette (Electric Violet Amethyst)
val PurplePrimary = Color(0xFF8B5CF6)
val PurpleVariant = Color(0xFF7C3AED)
val PurpleDark = Color(0xFF6D28D9)
val PurpleAccent = Color(0xFFA855F7)

// 3. Luxury Gold Palette (24K Royal Sovereign)
val GoldPrimary = Color(0xFFFFB300)
val GoldVariant = Color(0xFFF59E0B)
val GoldDark = Color(0xFFD97706)
val GoldAccent = Color(0xFFFFD54F)

// 4. Imperial Jade Palette (Royal Emerald Green)
val JadePrimary = Color(0xFF00E676)
val JadeVariant = Color(0xFF00C853)
val JadeDark = Color(0xFF1B5E20)
val JadeAccent = Color(0xFF69F0AE)

// 5. Crimson Velvet Palette (Midnight OLED Ruby)
val CrimsonPrimary = Color(0xFFFF1744)
val CrimsonVariant = Color(0xFFD50000)
val CrimsonDark = Color(0xFF880E4F)
val CrimsonAccent = Color(0xFFFF5252)

// 6. Sunset Copper Palette (Cyberpunk Amber Bronze)
val CopperPrimary = Color(0xFFFF9100)
val CopperVariant = Color(0xFFFF6D00)
val CopperDark = Color(0xFFDD2C00)
val CopperAccent = Color(0xFFFFAB40)

// 7. Royal Sapphire Palette (Deep Ocean Cobalt)
val SapphirePrimary = Color(0xFF2979FF)
val SapphireVariant = Color(0xFF0091EA)
val SapphireDark = Color(0xFF0D47A1)
val SapphireAccent = Color(0xFF82B1FF)

// 8. Rose Titanium Palette (Champagne Rose Gold)
val RosePrimary = Color(0xFFFF4081)
val RoseVariant = Color(0xFFF50057)
val RoseDark = Color(0xFFC51162)
val RoseAccent = Color(0xFFFF80AB)

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

data class LuxuryThemeMeta(
  val name: String,
  val tagline: String,
  val iconEmoji: String,
  val primary: Color,
  val variant: Color,
  val accent: Color
)

val LUXURY_THEMES = listOf(
  LuxuryThemeMeta(
    name = "Glass Neo",
    tagline = "Ultra-Frosted Cyan Glass",
    iconEmoji = "🧊",
    primary = Color(0xFF00F5FF),
    variant = Color(0xFF00B4D8),
    accent = Color(0xFF80F9FF)
  ),
  LuxuryThemeMeta(
    name = "Neon Cyan",
    tagline = "Cyber Aqua Matrix",
    iconEmoji = "⚡",
    primary = CyanPrimary,
    variant = CyanVariant,
    accent = CyanAccent
  ),
  LuxuryThemeMeta(
    name = "Mid Purple",
    tagline = "Electric Violet Amethyst",
    iconEmoji = "🔮",
    primary = PurplePrimary,
    variant = PurpleVariant,
    accent = PurpleAccent
  ),
  LuxuryThemeMeta(
    name = "Luxury Gold",
    tagline = "24K Royal Sovereign",
    iconEmoji = "👑",
    primary = GoldPrimary,
    variant = GoldVariant,
    accent = GoldAccent
  ),
  LuxuryThemeMeta(
    name = "Imperial Jade",
    tagline = "Royal Emerald Green",
    iconEmoji = "💎",
    primary = JadePrimary,
    variant = JadeVariant,
    accent = JadeAccent
  ),
  LuxuryThemeMeta(
    name = "Crimson Velvet",
    tagline = "Midnight OLED Ruby",
    iconEmoji = "🍷",
    primary = CrimsonPrimary,
    variant = CrimsonVariant,
    accent = CrimsonAccent
  ),
  LuxuryThemeMeta(
    name = "Sunset Copper",
    tagline = "Cyberpunk Amber Bronze",
    iconEmoji = "🔥",
    primary = CopperPrimary,
    variant = CopperVariant,
    accent = CopperAccent
  ),
  LuxuryThemeMeta(
    name = "Royal Sapphire",
    tagline = "Deep Ocean Cobalt",
    iconEmoji = "🌊",
    primary = SapphirePrimary,
    variant = SapphireVariant,
    accent = SapphireAccent
  ),
  LuxuryThemeMeta(
    name = "Rose Titanium",
    tagline = "Champagne Rose Gold",
    iconEmoji = "🌸",
    primary = RosePrimary,
    variant = RoseVariant,
    accent = RoseAccent
  )
)

data class AppThemeColors(
  val primary: Color,
  val variant: Color,
  val accent: Color,
  val gradient: Brush,
  val backgroundDark: Color = Color(0xFFF8FAFC),
  val backgroundGlow: Color = CyanPrimary,
  val cardBackground: Color = Color(0xFFFFFFFF),
  val cardBorder: Color = Color(0xFFE2E8F0),
  val isDark: Boolean = false,
  val textPrimary: Color = Color(0xFF0F172A),
  val textSecondary: Color = Color(0xFF334155),
  val textMuted: Color = Color(0xFF64748B),
  val topBarBackground: Color = Color(0xFFFFFFFF),
  val bottomBarBackground: Color = Color(0xFFFFFFFF),
  val dividerColor: Color = Color(0xFFE2E8F0)
) {
  val background: Color
    get() = backgroundDark
}

val LocalAppThemeColors = compositionLocalOf {
  AppThemeColors(
    primary = CyanPrimary,
    variant = CyanVariant,
    accent = CyanAccent,
    gradient = Brush.horizontalGradient(listOf(CyanPrimary, CyanVariant)),
    backgroundDark = Color(0xFFF8FAFC),
    backgroundGlow = CyanPrimary.copy(alpha = 0.08f),
    cardBackground = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFE2E8F0),
    isDark = false,
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF334155),
    textMuted = Color(0xFF64748B),
    topBarBackground = Color(0xFFFFFFFF),
    bottomBarBackground = Color(0xFFFFFFFF),
    dividerColor = Color(0xFFE2E8F0)
  )
}

val MaterialThemeColors: AppThemeColors
  @Composable
  @ReadOnlyComposable
  get() = LocalAppThemeColors.current

fun getThemePalette(themeName: String, isDark: Boolean = false): AppThemeColors {
  val baseColors = when (themeName) {
    "Glass Neo" -> Triple(Color(0xFF00B4D8), Color(0xFF0077B6), Color(0xFF00E5FF)) to Color(0xFF040A14)
    "Mid Purple" -> Triple(PurplePrimary, PurpleVariant, PurpleAccent) to Color(0xFF0C0819)
    "Luxury Gold" -> Triple(GoldPrimary, GoldVariant, GoldAccent) to Color(0xFF141007)
    "Imperial Jade" -> Triple(JadePrimary, JadeVariant, JadeAccent) to Color(0xFF06140D)
    "Crimson Velvet" -> Triple(CrimsonPrimary, CrimsonVariant, CrimsonAccent) to Color(0xFF16060A)
    "Sunset Copper" -> Triple(CopperPrimary, CopperVariant, CopperAccent) to Color(0xFF160B05)
    "Royal Sapphire" -> Triple(SapphirePrimary, SapphireVariant, SapphireAccent) to Color(0xFF060D1D)
    "Rose Titanium" -> Triple(RosePrimary, RoseVariant, RoseAccent) to Color(0xFF170710)
    else -> Triple(CyanPrimary, CyanVariant, CyanAccent) to Color(0xFF070D18)
  }

  val primary = baseColors.first.first
  val variant = baseColors.first.second
  val accent = baseColors.first.third
  val rawDarkBg = baseColors.second

  return if (isDark) {
    AppThemeColors(
      primary = primary,
      variant = variant,
      accent = accent,
      gradient = Brush.horizontalGradient(listOf(primary, variant)),
      backgroundDark = rawDarkBg,
      backgroundGlow = primary,
      cardBackground = when (themeName) {
        "Glass Neo" -> Color(0xFF0A162B).copy(alpha = 0.85f)
        "Mid Purple" -> Color(0xFF140D27)
        "Luxury Gold" -> Color(0xFF1E170A)
        "Imperial Jade" -> Color(0xFF0B1F15)
        "Crimson Velvet" -> Color(0xFF220A10)
        "Sunset Copper" -> Color(0xFF221108)
        "Royal Sapphire" -> Color(0xFF0A162E)
        "Rose Titanium" -> Color(0xFF230B19)
        else -> Color(0xFF0A1325)
      },
      cardBorder = primary.copy(alpha = 0.35f),
      isDark = true,
      textPrimary = Color(0xFFF8FAFC),
      textSecondary = Color(0xFF94A3B8),
      textMuted = Color(0xFF64748B),
      topBarBackground = Color(0xFF0A1325),
      bottomBarBackground = Color(0xFF070E1A),
      dividerColor = Color(0xFF1E293B)
    )
  } else {
    // Ultra-Clean Pure Professional White Mode
    AppThemeColors(
      primary = primary,
      variant = variant,
      accent = accent,
      gradient = Brush.horizontalGradient(listOf(primary, variant)),
      backgroundDark = Color(0xFFF8FAFC), // Pure clean bright surface
      backgroundGlow = primary.copy(alpha = 0.08f), // Soft subtle luxury glow aura
      cardBackground = Color(0xFFFFFFFF), // Crisp pure white cards
      cardBorder = primary.copy(alpha = 0.16f), // Delicate theme-tinted border
      isDark = false,
      textPrimary = Color(0xFF0F172A), // Crisp bold slate black - 100% visible
      textSecondary = Color(0xFF334155), // Refined slate text - 100% visible
      textMuted = Color(0xFF64748B),
      topBarBackground = Color(0xFFFFFFFF),
      bottomBarBackground = Color(0xFFFFFFFF),
      dividerColor = Color(0xFFE2E8F0)
    )
  }
}
