package com.fitnessapp.ui.screens.food

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.R
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.ui.components.NutrientRow
import java.util.Locale

@Composable
fun FoodLogScreen(
    foodRepository: FoodRepository,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: FoodViewModel = viewModel(factory = FoodViewModel.Factory(foodRepository))
) {
    val entries by viewModel.todayEntries.collectAsState()
    val totalCalories by viewModel.totalCalories.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.food_log_title),
                style = MaterialTheme.typography.headlineLarge
            )

            if (entries.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                NutrientRow(
                    calories = totalCalories ?: 0,
                    protein = entries.sumOf { it.proteinGrams.toDouble() }.toFloat(),
                    carbs = entries.sumOf { it.carbsGrams.toDouble() }.toFloat(),
                    fat = entries.sumOf { it.fatGrams.toDouble() }.toFloat()
                )
            }
        }

        if (entries.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.food_log_empty_title),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.food_log_empty_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val context = LocalContext.current
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    FoodLogItem(
                        entry = entry,
                        onDelete = {
                            viewModel.deleteEntry(entry)
                            onShowSnackbar("Entry deleted")
                        },
                        onEdit = { onEditClick(entry.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.food_log_add_content_description))
        }
    }
}

@Composable
private fun FoodLogItem(
    entry: com.fitnessapp.data.db.entity.FoodEntry,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onEdit() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${entry.calories} kcal",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%s | P: %.1fg C: %.1fg F: %.1fg",
                        entry.mealType,
                        entry.proteinGrams,
                        entry.carbsGrams,
                        entry.fatGrams
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.food_log_edit_content_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                       Icons.Default.Delete,
                       contentDescription = stringResource(R.string.food_log_delete_content_description),
                       tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
