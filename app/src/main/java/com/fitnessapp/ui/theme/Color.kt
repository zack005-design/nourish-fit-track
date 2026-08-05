package com.fitnessapp.ui.theme

import androidx.compose.ui.graphics.Color

// Premium Whoop / Bevel / Apple Health Minimalist Dark Stealth Tokens
val BackgroundDark = Color(0xFF090B10)
val SurfaceCard = Color(0xFF131722)
val SurfaceCardAlt = Color(0xFF1B2130)
val BorderSubtle = Color(0xFF263044)

// Typography Color Tokens (Apple Human Interface Guidelines)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

// Whoop / Bevel / Apple Health Refined Accent Tokens
val AccentOrange = Color(0xFFF97316)   // Energy / Calories — Refined Warm Ember
val AccentGreen = Color(0xFF10B981)    // Whoop Recovery / Protein — Signature Electric Emerald
val AccentBlue = Color(0xFF0EA5E9)     // Hydration / Tech Telemetry — Bevel Electric Cyan
val AccentPurple = Color(0xFF6366F1)   // Sleep — Midnight Indigo
val AccentYellow = Color(0xFFEAB308)   // Warm Gold
val AccentRed = Color(0xFFEF4444)      // Critical Alert / Red

// Brand Gradient Tokens (Whoop Electric Emerald -> Tech Cyan)
val BrandGradientStart = Color(0xFF10B981) // Electric Emerald
val BrandGradientEnd = Color(0xFF0EA5E9)   // Tech Cyan

// Backwards-compatibility aliases
val DarkBackground = BackgroundDark
val DarkSurface = SurfaceCard
val DarkSurfaceVariant = SurfaceCardAlt
val CaloriesColor = AccentOrange
val ProteinColor = AccentGreen
val SleepColor = AccentPurple
val WaterColor = AccentBlue
