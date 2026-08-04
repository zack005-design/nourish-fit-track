package com.fitnessapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.R
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.StepsRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.InsightCard
import com.fitnessapp.ui.components.LinearBar
import com.fitnessapp.ui.components.RingProgress
import com.fitnessapp.ui.components.charts.Sparkline
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    foodRepository: FoodRepository,
    sleepRepository: SleepRepository,
    waterRepository: WaterRepository,
    stepsRepository: StepsRepository,
    settingsRepository: SettingsRepository,
    onNavigateToFoodLog: () -> Unit,
    onNavigateToSleepLog: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            foodRepository,
            sleepRepository,
            waterRepository,
            stepsRepository,
            settingsRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    // Full date string — no "Today" label; always shows "Tuesday, 4 August"
    val formattedDate = remember(uiState.selectedDateMillis) {
        SimpleDateFormat("EEEE, d MMMM", Locale.US).format(Date(uiState.selectedDateMillis))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.setSelectedDate(it) }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = AccentGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher),
                    contentDescription = "Nourish Logo",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // Main Calorie Gauge (Apple Fitness Ring Aesthetic)
            val calorieGoal = uiState.userGoals.dailyCalorieGoal
            val calorieProgress = if (calorieGoal > 0) uiState.totalCalories.toFloat() / calorieGoal else 0f
            val percentageInt = (calorieProgress * 100).toInt()

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                RingProgress(
                    progressFraction = calorieProgress,
                    ringSize = 210.dp,
                    strokeWidth = 14.dp
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(Locale.US, "%,d", uiState.totalCalories),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "kcal",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "of ${String.format(Locale.US, "%,d", calorieGoal)} kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Text(
                text = "$percentageInt% of daily goal",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = AccentOrange,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // 4-Column Metric Grid — equal height via IntrinsicSize.Min with subtle glass outlines
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val calGoal = uiState.userGoals.dailyCalorieGoal
                val calProgress = if (calGoal > 0) uiState.totalCalories.toFloat() / calGoal else 0f
                MetricBox(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = AccentOrange,
                    label = "Calories",
                    valueText = String.format(Locale.US, "%,d", uiState.totalCalories),
                    subText = "/ ${String.format(Locale.US, "%,d", calGoal)} kcal",
                    progressFraction = calProgress,
                    progressColor = AccentOrange,
                    onClick = onNavigateToFoodLog
                )

                val protGoal = uiState.userGoals.dailyProteinGoal
                val protProgress = if (protGoal > 0f) uiState.totalProtein / protGoal else 0f
                MetricBox(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Spa,
                    iconTint = AccentGreen,
                    label = "Protein",
                    valueText = "${uiState.totalProtein.toInt()} g",
                    subText = "/ ${protGoal.toInt()} g",
                    progressFraction = protProgress,
                    progressColor = AccentGreen,
                    onClick = onNavigateToFoodLog
                )

                val waterGoalL = uiState.userGoals.dailyWaterGoal / 1000f
                val waterProgress = if (waterGoalL > 0f) uiState.totalWaterL / waterGoalL else 0f
                MetricBox(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.WaterDrop,
                    iconTint = AccentBlue,
                    label = "Water",
                    valueText = String.format(Locale.US, "%.1f L", uiState.totalWaterL),
                    subText = "/ ${String.format(Locale.US, "%.1f L", waterGoalL)}",
                    progressFraction = waterProgress,
                    progressColor = AccentBlue,
                    onClick = {}
                )

                val sleepHours = uiState.totalSleepHours
                val sleepHoursInt = sleepHours.toInt()
                val sleepMinutesInt = ((sleepHours - sleepHoursInt) * 60).toInt()
                val sleepTimeText = if (sleepHoursInt > 0 || sleepMinutesInt > 0) "${sleepHoursInt}h ${sleepMinutesInt}m" else "0h 0m"
                MetricBox(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.NightlightRound,
                    iconTint = AccentPurple,
                    label = "Sleep",
                    valueText = sleepTimeText,
                    subText = "${uiState.sleepScore} Score",
                    progressFraction = (sleepHours / 8f).coerceIn(0f, 1f),
                    progressColor = AccentPurple,
                    onClick = onNavigateToSleepLog
                )
            }

            // Steps Card
            val stepsGoal = uiState.userGoals.dailyStepsGoal
            val stepsProgress = if (stepsGoal > 0) uiState.stepsCount.toFloat() / stepsGoal else 0f
            val sparklineData = listOf(0.4f, 0.6f, 0.3f, 0.8f, 0.5f, 0.9f, stepsProgress.coerceIn(0.1f, 1f))

            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Steps",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${(stepsProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AccentGreen.copy(alpha = 0.2f))
                                .clickable { viewModel.addSteps(1000) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Steps",
                                tint = AccentGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "%,d", uiState.stepsCount),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = " / ${String.format(Locale.US, "%,d", stepsGoal)}",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    Sparkline(
                        values = sparklineData,
                        barColor = AccentGreen,
                        chartWidth = 90.dp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearBar(
                    progressFraction = stepsProgress,
                    barColor = AccentGreen,
                    barHeight = 6.dp,
                    showPercentageText = false
                )
            }

            // Water Tracking Card
            val waterGoalL = uiState.userGoals.dailyWaterGoal / 1000f
            val waterProgress = if (waterGoalL > 0f) uiState.totalWaterL / waterGoalL else 0f
            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Water",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = String.format(Locale.US, "%.1f L / %.1f L", uiState.totalWaterL, waterGoalL),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearBar(
                    progressFraction = waterProgress,
                    barColor = AccentBlue,
                    barHeight = 6.dp,
                    showPercentageText = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.addWater(250) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "+250 ml", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.addWater(500) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "+500 ml", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // AI Insight Card
            InsightCard(
                title = "AI Insight",
                primaryMessage = uiState.aiInsightPrimary,
                suggestionMessage = uiState.aiInsightSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun MetricBox(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    valueText: String,
    subText: String,
    progressFraction: Float,
    progressColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCardAlt)
            .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valueText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = subText,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.weight(1f))
            LinearBar(
                progressFraction = progressFraction,
                barColor = progressColor,
                barHeight = 4.dp,
                showPercentageText = false
            )
        }
    }
}
