package com.fitnessapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BevelDarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentGreen,
    tertiary = AccentBlue,
    background = BackgroundDark,
    surface = SurfaceCard,
    surfaceVariant = SurfaceCardAlt,
    onPrimary = Color(0xFF001F29),
    onSecondary = Color(0xFF001F29),
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val BevelLightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    secondary = Color(0xFF0EA5E9),
    tertiary = Color(0xFF00E5FF),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F5F9),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

@Composable
fun FitnessTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BevelDarkColorScheme else BevelLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
