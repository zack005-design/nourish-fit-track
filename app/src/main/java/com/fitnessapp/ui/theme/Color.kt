package com.fitnessapp.ui.theme

import androidx.compose.ui.graphics.Color

// Premium iOS Apple Health Theme Color Tokens
val BackgroundDark = Color(0xFF0E1116)
val SurfaceCard = Color(0xFF181C24)
val SurfaceCardAlt = Color(0xFF202632)
val BorderSubtle = Color(0xFF2A3140)

// Typography Color Tokens (Apple Human Interface Guidelines)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF8E8E93)
val TextTertiary = Color(0xFF636366)

// iOS System Vibrant Accent Tokens
val AccentOrange = Color(0xFFFF9F0A)   // Calories
val AccentGreen = Color(0xFF30D158)    // Protein, steps, active tab, success
val AccentBlue = Color(0xFF0A84FF)     // Water, sleep, primary actions
val AccentPurple = Color(0xFFBF5AF2)   // Fiber, sleep stage
val AccentYellow = Color(0xFFFFD60A)   // Fats
val AccentRed = Color(0xFFFF453A)      // Sodium, cholesterol, destructive actions

// Brand Gradient Tokens
val BrandGradientStart = Color(0xFFFF9F0A) // Vibrant iOS Orange
val BrandGradientEnd = Color(0xFF0A84FF)   // iOS System Blue

// Backwards-compatibility aliases
val DarkBackground = BackgroundDark
val DarkSurface = SurfaceCard
val DarkSurfaceVariant = SurfaceCardAlt
val CaloriesColor = AccentOrange
val ProteinColor = AccentGreen
val SleepColor = AccentBlue
val WaterColor = AccentBlue
