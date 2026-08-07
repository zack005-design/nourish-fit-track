package com.fitnessapp.ui.theme

import androidx.compose.ui.graphics.Color

// Authentic Apple Fitness & Apple Health iOS Human Interface Tokens
val BackgroundDark = Color(0xFF000000)   // Apple Pitch OLED Black
val SurfaceCard = Color(0xFF1C1C1E)      // Apple iOS Dark Grouped Inset Surface
val SurfaceCardAlt = Color(0xFF2C2C2E)   // Apple iOS Secondary Dark Surface
val BorderSubtle = Color(0xFF38383A)     // Apple Hairline Border

// Apple iOS Typography Tokens
val TextPrimary = Color(0xFFFFFFFF)     // Pure Crisp White
val TextSecondary = Color(0xFF8E8E93)   // Apple iOS System Gray
val TextTertiary = Color(0xFF636366)    // Apple iOS System Secondary Gray

// Apple Fitness Activity & Health Tokens (Official iOS Color Palette)
val AccentOrange = Color(0xFFFA114F)     // Apple Move Ring / Calories (Warm Coral Red)
val AccentGreen = Color(0xFF00E676)      // Apple Exercise Ring / Protein (Neon Green)
val AccentBlue = Color(0xFF00C7BE)       // Apple Stand Ring / Water (Tech Cyan Blue)
val AccentCyan = Color(0xFF00C7BE)       // Apple Stand Cyan Blue
val AccentPurple = Color(0xFF5E5CE6)     // Apple Bedtime / Sleep (System Indigo Violet)
val AccentYellow = Color(0xFFFFD60A)     // Apple Activity Gold
val AccentRed = Color(0xFFFF3B30)        // Apple System Red (Destructive Actions)
val AppleSystemBlue = Color(0xFF0A84FF)  // Apple System Blue

// Apple Activity Ring Gradients (Move Red -> Coral)
val BrandGradientStart = Color(0xFFFA114F) // Apple Coral Red
val BrandGradientEnd = Color(0xFFFF5252)   // Apple Flame Red

// Backwards-compatibility aliases mapped to Apple Health Activity System
val DarkBackground = BackgroundDark
val DarkSurface = SurfaceCard
val DarkSurfaceVariant = SurfaceCardAlt
val CaloriesColor = AccentOrange
val ProteinColor = AccentGreen
val SleepColor = AccentPurple
val WaterColor = AccentBlue
