package com.example.zorvyn_task.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GlassColorScheme = darkColorScheme(
    primary = AccentBlue,
    secondary = AccentGreen,
    tertiary = AccentRed,
    background = BgTop,
    surface = GlassWhite10,
    onPrimary = GlassWhite,
    onSecondary = GlassWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = AccentRed,
    outline = GlassBorder
)

@Composable
fun ZorvynTaskTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GlassColorScheme,
        typography = Typography,
        content = content
    )
}