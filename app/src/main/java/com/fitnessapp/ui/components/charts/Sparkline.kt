package com.fitnessapp.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.SurfaceCardAlt

@Composable
fun Sparkline(
    values: List<Float>,
    modifier: Modifier = Modifier,
    barColor: Color = AccentGreen,
    chartWidth: Dp = 80.dp
) {
    if (values.isEmpty()) return
    val maxVal = values.maxOfOrNull { it }?.coerceAtLeast(1f) ?: 1f

    Canvas(
        modifier = modifier
            .width(chartWidth)
            .fillMaxHeight()
    ) {
        val width = size.width
        val height = size.height
        val count = values.size
        val gap = 3.dp.toPx()
        val barWidth = (width - gap * (count - 1)) / count

        values.forEachIndexed { i, v ->
            val x = i * (barWidth + gap)
            val h = (v / maxVal) * height
            val y = height - h

            // Track
            drawRoundRect(
                color = SurfaceCardAlt,
                topLeft = Offset(x, 0f),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )

            // Value Bar
            if (h > 0) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, h),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
        }
    }
}
