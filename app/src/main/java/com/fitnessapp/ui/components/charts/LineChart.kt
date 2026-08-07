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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.TextTertiary

data class LineChartPoint(
    val label: String,
    val value: Float
)

@Composable
fun LineChart(
    points: List<LineChartPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = AccentOrange,
    chartHeight: Dp = 140.dp
) {
    if (points.isEmpty()) return

    val maxValue = points.maxOfOrNull { it.value }?.coerceAtLeast(1f) ?: 1f
    val minValue = points.minOfOrNull { it.value } ?: 0f

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
        ) {
            val width = size.width
            val height = size.height
            val spacing = width / (points.size - 1).coerceAtLeast(1)

            val offsets = points.mapIndexed { index, point ->
                val x = index * spacing
                val normalizedY = (point.value - minValue) / (maxValue - minValue).coerceAtLeast(1f)
                val y = height - (normalizedY * (height * 0.75f) + height * 0.12f)
                Offset(x, y)
            }

            // Path for Smooth Curve
            val path = Path()
            path.moveTo(offsets[0].x, offsets[0].y)
            for (i in 0 until offsets.size - 1) {
                val p1 = offsets[i]
                val p2 = offsets[i + 1]
                val controlP1 = Offset(p1.x + (p2.x - p1.x) / 2f, p1.y)
                val controlP2 = Offset(p1.x + (p2.x - p1.x) / 2f, p2.y)
                path.cubicTo(controlP1.x, controlP1.y, controlP2.x, controlP2.y, p2.x, p2.y)
            }

            // Fill Gradient Path
            val fillPath = Path()
            fillPath.addPath(path)
            fillPath.lineTo(offsets.last().x, height)
            fillPath.lineTo(offsets.first().x, height)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw Curve Line
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw Hollow Dot Markers
            offsets.forEach { offset ->
                drawCircle(
                    color = lineColor,
                    radius = 5.dp.toPx(),
                    center = offset
                )
                drawCircle(
                    color = Color(0xFF16161C), // SurfaceCard background
                    radius = 3.dp.toPx(),
                    center = offset
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // X-Axis Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { point ->
                Text(
                    text = point.label,
                    fontSize = 11.sp,
                    color = TextTertiary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
