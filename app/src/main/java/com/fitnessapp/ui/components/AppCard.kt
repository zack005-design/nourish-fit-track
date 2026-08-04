package com.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fitnessapp.ui.theme.BorderSubtle
import com.fitnessapp.ui.theme.SurfaceCard

/**
 * iOS Apple Health Bevel Card with inner glow, drop shadow depth, and gradient border highlight.
 * Achieves an elevated glass-like look aligned with Apple Fitness / Health aesthetics.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceCard,
    borderColor: Color = BorderSubtle,
    cornerRadius: Dp = 20.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    // Bevel border: bright top-left highlight, subtle bottom-right shadow edge
    val bevelBorderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.28f),  // top-left inner light
            Color.White.copy(alpha = 0.10f),
            borderColor.copy(alpha = 0.6f),
            Color.Black.copy(alpha = 0.25f)   // bottom-right shadow edge
        )
    )

    // Inner surface: very subtle radial inner-glow on top for depth
    val innerGlowBrush = Brush.verticalGradient(
        colors = listOf(
            backgroundColor.copy(alpha = 1.0f),
            backgroundColor.copy(alpha = 0.97f)
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(innerGlowBrush)
            .border(1.dp, bevelBorderBrush, shape)
            .padding(contentPadding),
        content = content
    )
}

