package com.example.zorvyn_task.ui.theme

import androidx.compose.ui.graphics.Color

// ── Dark theme (original deep blue-purple glass) ──────────────────────────────
val DarkBgTop    = Color(0xFF0A0F2E)
val DarkBgMid    = Color(0xFF0D1F4E)
val DarkBgBottom = Color(0xFF1A0A3C)

val DarkGlassWhite    = Color(0xFFFFFFFF)
val DarkGlassWhite70  = Color(0xB3FFFFFF)
val DarkGlassWhite40  = Color(0x66FFFFFF)
val DarkGlassWhite15  = Color(0x26FFFFFF)
val DarkGlassWhite10  = Color(0x1AFFFFFF)
val DarkGlassBorder   = Color(0x33FFFFFF)
val DarkGlassBorderStrong = Color(0x55FFFFFF)

val DarkAccentGreen = Color(0xFF34D399)
val DarkAccentRed   = Color(0xFFFC8181)
val DarkAccentBlue  = Color(0xFF60A5FA)

val DarkTextPrimary   = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xB3FFFFFF)
val DarkTextTertiary  = Color(0x80FFFFFF)

// ── Light theme (mint green + #072d24 dark accent) ────────────────────────────
val LightBgTop    = Color(0xFFE8F5F0)
val LightBgMid    = Color(0xFFF0FAF6)
val LightBgBottom = Color(0xFFD4EDE4)

val LightGlassWhite    = Color(0xFFFFFFFF)
val LightGlassWhite70  = Color(0xB3FFFFFF)
val LightGlassWhite40  = Color(0x99FFFFFF)
val LightGlassWhite15  = Color(0x55FFFFFF)
val LightGlassWhite10  = Color(0x44FFFFFF)
val LightGlassBorder   = Color(0x4A072D24)
val LightGlassBorderStrong = Color(0x7A072D24)

val LightAccentGreen = Color(0xFF0D9E6A)
val LightAccentRed   = Color(0xFFE05252)
val LightAccentBlue  = Color(0xFF1A7A5E)
val LightAccentMint  = Color(0xFF3DBE8A)

val LightTextPrimary   = Color(0xFF072D24)
val LightTextSecondary = Color(0xFF1A5C44)
val LightTextTertiary  = Color(0xFF4A8C72)
val LightCardBg        = Color(0xCCFFFFFF)
val LightCardBgAlt     = Color(0xAAFFFFFF)
val LightDark          = Color(0xFF072D24)

// ── Aliases (resolved at runtime by theme) ─────────────────────────────────────
// Keep old names so existing screens compile; ThemeHelper picks the right set.
val GlassWhite    = DarkGlassWhite
val GlassWhite70  = DarkGlassWhite70
val GlassWhite40  = DarkGlassWhite40
val GlassWhite15  = DarkGlassWhite15
val GlassWhite10  = DarkGlassWhite10
val GlassBorder   = DarkGlassBorder
val GlassBorderStrong = DarkGlassBorderStrong

val BgTop    = DarkBgTop
val BgMid    = DarkBgMid
val BgBottom = DarkBgBottom

val AccentGreen = DarkAccentGreen
val AccentRed   = DarkAccentRed
val AccentBlue  = DarkAccentBlue

val TextPrimary   = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextTertiary  = DarkTextTertiary