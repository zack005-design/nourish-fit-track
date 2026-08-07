package com.fitnessapp.ui.screens.home

import androidx.compose.ui.text.style.TextOverflow
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.R
import com.fitnessapp.widget.NourishAppWidget
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.LinearBar
import com.fitnessapp.ui.components.RingProgress
import com.fitnessapp.ui.components.charts.Sparkline
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.AccentRed
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import com.fitnessapp.util.HealthConnectManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    foodRepository: FoodRepository,
    sleepRepository: SleepRepository,
    waterRepository: WaterRepository,
    settingsRepository: SettingsRepository,
    onNavigateToFoodLog: () -> Unit,
    onNavigateToSleepLog: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAddFood: () -> Unit = {},
    onNavigateToAddSleep: () -> Unit = {},
    onNavigateToAi: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(

        factory = HomeViewModel.Factory(
            foodRepository,
            sleepRepository,
            waterRepository,
            settingsRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showDatePicker by remember { mutableStateOf(false) }
    val isHCAvailable = remember { HealthConnectManager.isHealthConnectAvailable(context) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Clean Overview Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Streak pill badge + Health Connect status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Health Connect status dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isHCAvailable) AccentGreen else Color(0xFF4A5568))
                    )

                    // Real streak badge
                    if (uiState.logStreak > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(AccentOrange.copy(alpha = 0.15f))
                                .border(1.dp, AccentOrange.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔥", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${uiState.logStreak}d",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AccentOrange
                                )
                            }
                        }
                    }
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

            // Quick Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentOrange.copy(alpha = 0.15f))
                        .border(1.dp, AccentOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToAddFood()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+ Meal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentPurple.copy(alpha = 0.15f))
                        .border(1.dp, AccentPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToAddSleep()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+ Sleep", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentGreen.copy(alpha = 0.15f))
                        .border(1.dp, AccentGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToAi()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("AI Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentBlue.copy(alpha = 0.15f))
                        .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToAnalytics()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Spa, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Analytics", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }


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

            // ─── REMADE WATER TRACKING WIDGET ─────────────────────────────────────────
            val waterGoalL = uiState.userGoals.dailyWaterGoal / 1000f
            val waterGoalMl = uiState.userGoals.dailyWaterGoal
            val totalWaterMl = (uiState.totalWaterL * 1000).toInt()
            val waterProgress = if (waterGoalL > 0f) (uiState.totalWaterL / waterGoalL).coerceIn(0f, 1f) else 0f
            val waterPctInt = (waterProgress * 100).toInt()
            val remainingMl = (waterGoalMl - totalWaterMl).coerceAtLeast(0)

            AppCard(contentPadding = 18.dp) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentBlue.copy(alpha = 0.18f))
                                .border(1.dp, AccentBlue.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Hydration Tracker",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (remainingMl > 0) "${remainingMl} ml remaining" else "Daily goal accomplished! 💧",
                                fontSize = 11.sp,
                                color = if (remainingMl > 0) TextSecondary else AccentBlue,
                                fontWeight = if (remainingMl > 0) FontWeight.Normal else FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentBlue.copy(alpha = 0.15f))
                            .border(1.dp, AccentBlue.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$waterPctInt%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Large Volume Display Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.US, "%.1f L", uiState.totalWaterL),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = " / ${String.format(Locale.US, "%.1f L", waterGoalL)} (${totalWaterMl} ml)",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Liquid Progress Bar
                LinearBar(
                    progressFraction = waterProgress,
                    barColor = AccentBlue,
                    barHeight = 10.dp,
                    showPercentageText = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Add Cup Buttons — Row 1 (Adding Water)
                Text(
                    text = "Quick Log Water Cups:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "+250 ml" to 250,
                        "+500 ml" to 500,
                        "+750 ml" to 750
                    ).forEach { (label, amount) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AccentBlue.copy(alpha = 0.15f))
                                .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.addWater(amount)
                                    NourishAppWidget.updateAllWidgets(context)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Adjustment Row 2 (Subtract & Clear)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceCardAlt)
                            .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(14.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.removeWater(250)
                                NourishAppWidget.updateAllWidgets(context)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "-250 ml",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AccentRed.copy(alpha = 0.12f))
                            .border(1.dp, AccentRed.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.clearWaterForDate()
                                NourishAppWidget.updateAllWidgets(context)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear Water Log",
                                tint = AccentRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Reset",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentRed
                            )
                        }
                    }
                }
            }

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


