package com.fitnessapp.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextTertiary

data class BarChartItem(
    val label: String,
    val value: Float
)

@Composable
fun BarChart(
    items: List<BarChartItem>,
    modifier: Modifier = Modifier,
    barColor: Color = AccentOrange,
    chartHeight: Dp = 120.dp,
    showLabels: Boolean = true
) {
    if (items.isEmpty()) return

    val maxValue = items.maxOfOrNull { it.value }?.coerceAtLeast(1f) ?: 1f

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
        ) {
            val width = size.width
            val height = size.height
            val barCount = items.size
            val availableWidth = width
            val totalSpacing = availableWidth * 0.3f
            val barWidth = (availableWidth - totalSpacing) / barCount
            val barSpacing = totalSpacing / (barCount + 1)

            items.forEachIndexed { index, item ->
                val x = barSpacing + index * (barWidth + barSpacing)
                val barHeightPx = (item.value / maxValue) * (height * 0.88f)
                val y = height - barHeightPx

                // Background track
                drawRoundRect(
                    color = SurfaceCardAlt,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, height),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Active bar
                if (barHeightPx > 0f) {
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeightPx),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        }

        if (showLabels) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items.forEach { item ->
                    if (item.label.isNotEmpty()) {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            color = TextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
