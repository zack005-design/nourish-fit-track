package com.fitnessapp.ui.theme

import androidx.compose.ui.graphics.Color

// Premium Apple Health / Whoop / Oura Space Theme Color Tokens
val BackgroundDark = Color(0xFF0B0E14)
val SurfaceCard = Color(0xFF141923)
val SurfaceCardAlt = Color(0xFF1C2230)
val BorderSubtle = Color(0xFF2B3446)

// Typography Color Tokens (Apple Human Interface Guidelines)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

// Ultra-Vibrant Neon Accent Tokens
val AccentOrange = Color(0xFFFF6D00)   // Calories / Energy
val AccentGreen = Color(0xFF00E676)    // Protein / Recovery / Active
val AccentBlue = Color(0xFF00B0FF)     // Hydration / Primary
val AccentPurple = Color(0xFF7B61FF)   // Sleep / REM stage — iOS-style soft indigo
val AccentYellow = Color(0xFFFFD600)   // Healthy Fats / Tips
val AccentRed = Color(0xFFFF1744)      // Critical Alerts / Warnings


// Brand Gradient Tokens
val BrandGradientStart = Color(0xFF00E676) // Emerald Green
val BrandGradientEnd = Color(0xFF00B0FF)   // Cyan Blue

// Backwards-compatibility aliases
val DarkBackground = BackgroundDark
val DarkSurface = SurfaceCard
val DarkSurfaceVariant = SurfaceCardAlt
val CaloriesColor = AccentOrange
val ProteinColor = AccentGreen
val SleepColor = AccentPurple
val WaterColor = AccentBlue
