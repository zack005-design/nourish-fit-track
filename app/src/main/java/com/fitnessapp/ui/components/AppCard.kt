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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fitnessapp.ui.theme.BorderSubtle
import com.fitnessapp.ui.theme.SurfaceCard

/**
 * Reusable iOS Apple Health Glass Bevel Card container.
 * Features rounded corners, elevated dark surface, and a sleek translucent bevel border gradient.
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
    val bevelBorderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.18f),
            borderColor,
            Color.White.copy(alpha = 0.04f)
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, bevelBorderBrush, shape)
            .padding(contentPadding),
        content = content
    )
}
