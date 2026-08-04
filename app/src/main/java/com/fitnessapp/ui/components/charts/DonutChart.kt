package com.fitnessapp.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import java.util.Locale

data class DonutSlice(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 140.dp,
    strokeWidth: Dp = 24.dp
) {
    val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Ring Canvas
        Box(
            modifier = Modifier.size(chartSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(chartSize)) {
                val strokePx = strokeWidth.toPx()
                var currentAngle = -90f

                slices.forEach { slice ->
                    val sweep = (slice.value / total) * 360f
                    drawArc(
                        color = slice.color,
                        startAngle = currentAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = strokePx)
                    )
                    currentAngle += sweep
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Legend Column
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            slices.forEach { slice ->
                val percentage = (slice.value / total) * 100f
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(slice.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = slice.label,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.width(60.dp)
                    )
                    Text(
                        text = String.format(Locale.US, "%.0f%%", percentage),
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
