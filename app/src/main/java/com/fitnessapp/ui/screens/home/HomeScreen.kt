package com.fitnessapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.R
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.ui.components.NutrientRow
import com.fitnessapp.ui.components.StatCard
import com.fitnessapp.ui.theme.Blue40
import com.fitnessapp.ui.theme.Purple40
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    foodRepository: FoodRepository,
    sleepRepository: SleepRepository,
    settingsRepository: com.fitnessapp.data.repository.SettingsRepository,
    onNavigateToAddFood: () -> Unit,
    onNavigateToAddSleep: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(foodRepository, sleepRepository, settingsRepository))
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.home_title_today_summary)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {

        Text(
            text = stringResource(R.string.home_section_nutrition) + " (${state.totalCalories} / ${state.calorieTarget} kcal)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        if (state.recentFoodEntries.isNotEmpty()) {
            NutrientRow(
                calories = state.totalCalories,
                protein = state.totalProtein,
                carbs = state.totalCarbs,
                fat = state.totalFat
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = stringResource(R.string.home_empty_meals),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.home_section_sleep),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        if (state.todaySleep != null) {
            SleepSummaryCard(state.todaySleep, state.sleepTargetHours)
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = stringResource(R.string.home_empty_sleep),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigateToAddFood,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(stringResource(R.string.home_action_add_food))
            }
            Button(
                onClick = onNavigateToAddSleep,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(stringResource(R.string.home_action_log_sleep))
            }
        }

        if (state.recentFoodEntries.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.home_section_todays_meals),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            state.recentFoodEntries.forEach { entry ->
                FoodEntryCard(entry)
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun SleepSummaryCard(entry: com.fitnessapp.data.db.entity.SleepEntry, targetHours: Float) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val hours = (entry.endMillis - entry.startMillis) / (1000f * 60 * 60)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Purple40.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                label = "Duration",
                value = String.format(Locale.getDefault(), "%.1f", hours),
                unit = "hrs",
                color = Purple40,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            StatCard(
                label = "Quality",
                value = "${entry.quality}/5",
                color = Blue40,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            StatCard(
                label = "From",
                value = timeFormat.format(Date(entry.startMillis)),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }
        
        val progress = (hours / targetHours).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Purple40,
            trackColor = Purple40.copy(alpha = 0.2f)
        )
        
        Text(
            text = String.format(Locale.getDefault(), "%.1f / %.1f hours (Goal)", hours, targetHours),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun FoodEntryCard(entry: com.fitnessapp.data.db.entity.FoodEntry) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${entry.calories} kcal",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "P: ${entry.proteinGrams}g | C: ${entry.carbsGrams}g | F: ${entry.fatGrams}g",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
