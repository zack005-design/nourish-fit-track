package com.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fitnessapp.ui.theme.BackgroundDark

/**
 * Reusable modifier to apply an iOS-style translucent glass backdrop to floating surfaces
 * without blurring text or icon contents inside the bar.
 */
fun Modifier.frostedGlass(
    backgroundColor: Color = BackgroundDark.copy(alpha = 0.90f),
    fallbackColor: Color = BackgroundDark,
    blurRadius: Float = 0f,
    shape: Shape = RectangleShape,
    borderWidth: Dp = 0.5.dp,
    borderColor: Color = Color.White.copy(alpha = 0.12f)
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(borderWidth, borderColor, shape)

/**
 * Reusable container wrapper for floating translucent glass surfaces.
 */
@Composable
fun FrostedGlassSurface(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BackgroundDark.copy(alpha = 0.90f),
    fallbackColor: Color = BackgroundDark,
    blurRadius: Float = 0f,
    shape: Shape = RectangleShape,
    borderWidth: Dp = 0.5.dp,
    borderColor: Color = Color.White.copy(alpha = 0.12f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.frostedGlass(
            backgroundColor = backgroundColor,
            fallbackColor = fallbackColor,
            blurRadius = blurRadius,
            shape = shape,
            borderWidth = borderWidth,
            borderColor = borderColor
        ),
        content = content
    )
}
