package com.fitnessapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun StatCard(
    label: String,
    value: String,
    unit: String = "",
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                if (unit.isNotEmpty()) {
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NutrientRow(
    calories: Int,
    protein: Float,
    carbs: Float,
    fat: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val locale = Locale.getDefault()
        StatCard(
            label = "Calories",
            value = calories.toString(),
            unit = "kcal",
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatCard(
            label = "Protein",
            value = String.format(locale, "%.1f", protein),
            unit = "g",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatCard(
            label = "Carbs",
            value = String.format(locale, "%.1f", carbs),
            unit = "g",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatCard(
            label = "Fat",
            value = String.format(locale, "%.1f", fat),
            unit = "g",
            color = Color(0xFFE91E63),
            modifier = Modifier.weight(1f)
        )
    }
}
