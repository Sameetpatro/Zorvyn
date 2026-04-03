package com.example.zorvyn_task.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

// ── Color schemes ──────────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary          = DarkAccentBlue,
    secondary        = DarkAccentGreen,
    tertiary         = DarkAccentRed,
    background       = DarkBgTop,
    surface          = DarkGlassWhite10,
    onPrimary        = DarkGlassWhite,
    onSecondary      = DarkGlassWhite,
    onBackground     = DarkTextPrimary,
    onSurface        = DarkTextPrimary,
    error            = DarkAccentRed,
    outline          = DarkGlassBorder
)

private val LightColorScheme = lightColorScheme(
    primary          = LightAccentMint,
    secondary        = LightAccentGreen,
    tertiary         = LightAccentRed,
    background       = LightBgTop,
    surface          = LightCardBg,
    onPrimary        = Color.White,
    onSecondary      = Color.White,
    onBackground     = LightTextPrimary,
    onSurface        = LightTextPrimary,
    error            = LightAccentRed,
    outline          = LightGlassBorder
)

// ── CompositionLocal for theme tokens ─────────────────────────────────────────

data class AppColors(
    val bgTop: Color,
    val bgMid: Color,
    val bgBottom: Color,
    val glassWhite15: Color,
    val glassWhite10: Color,
    val glassBorder: Color,
    val glassBorderStrong: Color,
    val accentGreen: Color,
    val accentRed: Color,
    val accentBlue: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val cardBg: Color,
    val isDark: Boolean
)

val darkAppColors = AppColors(
    bgTop            = DarkBgTop,
    bgMid            = DarkBgMid,
    bgBottom         = DarkBgBottom,
    glassWhite15     = DarkGlassWhite15,
    glassWhite10     = DarkGlassWhite10,
    glassBorder      = DarkGlassBorder,
    glassBorderStrong= DarkGlassBorderStrong,
    accentGreen      = DarkAccentGreen,
    accentRed        = DarkAccentRed,
    accentBlue       = DarkAccentBlue,
    textPrimary      = DarkTextPrimary,
    textSecondary    = DarkTextSecondary,
    textTertiary     = DarkTextTertiary,
    cardBg           = DarkGlassWhite15,
    isDark           = true
)

val lightAppColors = AppColors(
    bgTop            = LightBgTop,
    bgMid            = LightBgMid,
    bgBottom         = LightBgBottom,
    glassWhite15     = LightGlassWhite15,
    glassWhite10     = LightGlassWhite10,
    glassBorder      = LightGlassBorder,
    glassBorderStrong= LightGlassBorderStrong,
    accentGreen      = LightAccentGreen,
    accentRed        = LightAccentRed,
    accentBlue       = LightAccentBlue,
    textPrimary      = LightTextPrimary,
    textSecondary    = LightTextSecondary,
    textTertiary     = LightTextTertiary,
    cardBg           = LightCardBg,
    isDark           = false
)

val LocalAppColors = staticCompositionLocalOf { darkAppColors }

@Composable
fun ZorvynTaskTheme(
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val appColors = if (isDark) darkAppColors else lightAppColors
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}