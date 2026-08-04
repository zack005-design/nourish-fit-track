package com.fitnessapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fitnessapp.ui.theme.BrandGradientEnd
import com.fitnessapp.ui.theme.BrandGradientStart
import com.fitnessapp.ui.theme.SurfaceCardAlt

@Composable
fun RingProgress(
    progressFraction: Float,
    modifier: Modifier = Modifier,
    ringSize: Dp = 220.dp,
    strokeWidth: Dp = 16.dp,
    flatColor: Color? = null,
    gradientColors: List<Color> = listOf(BrandGradientStart, BrandGradientEnd),
    centerContent: @Composable () -> Unit = {}
) {
    val clampedProgress = progressFraction.coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(ringSize)) {
            val strokePx = strokeWidth.toPx()

            // Background Track Arc
            drawArc(
                color = SurfaceCardAlt,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx)
            )

            // Progress Arc
            if (clampedProgress > 0f) {
                val brush: Brush = if (flatColor != null) {
                    SolidColor(flatColor)
                } else {
                    Brush.sweepGradient(
                        colors = gradientColors
                    )
                }

                drawArc(
                    brush = brush,
                    startAngle = -90f,
                    sweepAngle = 360f * clampedProgress,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        centerContent()
    }
}
