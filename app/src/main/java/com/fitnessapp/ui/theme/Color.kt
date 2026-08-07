package com.fitnessapp.ui.theme

import androidx.compose.ui.graphics.Color

// Bevel Health Minimalist Stealth Dark Tokens
val BackgroundDark = Color(0xFF08090C)   // Deep Matte Obsidian
val SurfaceCard = Color(0xFF11131A)      // Bevel Stealth Dark Surface
val SurfaceCardAlt = Color(0xFF171A24)   // Bevel Elevated Surface
val BorderSubtle = Color(0xFF1E2230)     // Hairline Stealth Border

// Typography Color Tokens (Apple & Bevel Human Interface Guidelines)
val TextPrimary = Color(0xFFFFFFFF)     // Pure Crisp White
val TextSecondary = Color(0xFF94A3B8)   // Muted Slate Silver
val TextTertiary = Color(0xFF64748B)    // Stealth Dark Slate

// Bevel Monochromatic & Electric Ice Cyan Accent System (No Rainbow Clutter)
val AccentCyan = Color(0xFF00E5FF)       // Signature Bevel Electric Ice Cyan
val AccentBlue = Color(0xFF00E5FF)       // Bevel Electric Ice Cyan
val AccentGreen = Color(0xFF38BDF8)      // Bevel Sky Blue / Ice Teal
val AccentOrange = Color(0xFF0284C7)     // Bevel Deep Blue (Replaces Bright Orange)
val AccentPurple = Color(0xFF7DD3FC)     // Bevel Soft Ice (Replaces Bright Purple)
val AccentYellow = Color(0xFF38BDF8)     // Bevel Soft Ice Cyan
val AccentRed = Color(0xFFF43F5E)        // Muted Rose Crimson (Only for Destructive Actions)

// Brand Gradient Tokens (Bevel Electric Ice Cyan -> Deep Bevel Blue)
val BrandGradientStart = Color(0xFF00E5FF) // Electric Ice Cyan
val BrandGradientEnd = Color(0xFF0284C7)   // Deep Bevel Blue

// Backwards-compatibility aliases mapped to Bevel Palette
val DarkBackground = BackgroundDark
val DarkSurface = SurfaceCard
val DarkSurfaceVariant = SurfaceCardAlt
val CaloriesColor = AccentOrange
val ProteinColor = AccentGreen
val SleepColor = AccentPurple
val WaterColor = AccentBlue
