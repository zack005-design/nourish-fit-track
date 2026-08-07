package com.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitnessapp.ui.theme.SurfaceCardAlt
import java.util.Locale

@Composable
fun LinearBar(
    progressFraction: Float,
    barColor: Color,
    modifier: Modifier = Modifier,
    barHeight: Dp = 8.dp,
    showPercentageText: Boolean = true
) {
    val clampedProgress = progressFraction.coerceIn(0f, 1f)
    val percentageInt = (clampedProgress * 100).toInt()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
                .clip(RoundedCornerShape(barHeight / 2))
                .background(SurfaceCardAlt)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(clampedProgress)
                    .clip(RoundedCornerShape(barHeight / 2))
                    .background(barColor)
            )
        }
        if (showPercentageText) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = String.format(Locale.US, "%d%%", percentageInt),
                color = barColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
