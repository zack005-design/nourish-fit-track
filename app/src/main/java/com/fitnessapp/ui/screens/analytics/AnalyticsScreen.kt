package com.fitnessapp.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.RingProgress
import com.fitnessapp.ui.components.charts.BarChart
import com.fitnessapp.ui.components.charts.DonutChart
import com.fitnessapp.ui.components.charts.LineChart
import com.fitnessapp.ui.components.frostedGlass
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import com.fitnessapp.ui.theme.TextTertiary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    foodRepository: FoodRepository,
    waterRepository: WaterRepository,
    sleepRepository: SleepRepository,
    settingsRepository: SettingsRepository,
    viewModel: AnalyticsViewModel = viewModel(
        factory = AnalyticsViewModel.Factory(foodRepository, waterRepository, sleepRepository, settingsRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val periodTitles = listOf("Week", "Month", "Year")
    var selectedMetricChip by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                modifier = Modifier.frostedGlass(
                    backgroundColor = BackgroundDark.copy(alpha = 0.85f),
                    fallbackColor = BackgroundDark
                ),
                title = {
                    Text(
                        text = "Analytics & Trends",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ─── 1. iOS SEGMENTED PERIOD SELECTOR ──────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceCardAlt.copy(alpha = 0.75f))
                    .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    periodTitles.forEachIndexed { index, title ->
                        val isSelected = uiState.selectedPeriodIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) AccentBlue else Color.Transparent)
                                .clickable { viewModel.setSelectedPeriod(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BackgroundDark else TextSecondary
                            )
                        }
                    }
                }
            }

            // ─── 2. METRIC FILTER CHIPS ───────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricFilterChip(
                    modifier = Modifier.weight(1f),
                    label = "Calories",
                    icon = Icons.Default.LocalFireDepartment,
                    color = AccentOrange,
                    isSelected = selectedMetricChip == 0,
                    onClick = { selectedMetricChip = 0 }
                )
                MetricFilterChip(
                    modifier = Modifier.weight(1f),
                    label = "Water",
                    icon = Icons.Default.WaterDrop,
                    color = AccentBlue,
                    isSelected = selectedMetricChip == 1,
                    onClick = { selectedMetricChip = 1 }
                )
                MetricFilterChip(
                    modifier = Modifier.weight(1f),
                    label = "Sleep",
                    icon = Icons.Default.Bedtime,
                    color = AccentPurple,
                    isSelected = selectedMetricChip == 2,
                    onClick = { selectedMetricChip = 2 }
                )
                MetricFilterChip(
                    modifier = Modifier.weight(1f),
                    label = "Macros",
                    icon = Icons.Default.PieChart,
                    color = AccentGreen,
                    isSelected = selectedMetricChip == 3,
                    onClick = { selectedMetricChip = 3 }
                )
            }

            // ─── 3. DYNAMIC HERO TREND CARD (100% REAL USER DATA) ─────────────────
            val calorieGoal = uiState.userGoals.dailyCalorieGoal
            val waterGoal = uiState.waterGoalL

            when (selectedMetricChip) {
                0 -> {
                    // CALORIES TREND
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "DAILY CALORIE INTAKE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange,
                                    letterSpacing = 0.8.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = String.format(Locale.US, "%,d", uiState.avgCalorieIntake),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = " / ${String.format(Locale.US, "%,d", calorieGoal)} kcal",
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 3.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = uiState.calorieDiffPercentage,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LineChart(
                            points = uiState.calorieLinePoints,
                            lineColor = AccentOrange,
                            chartHeight = 120.dp
                        )
                    }
                }

                1 -> {
                    // WATER TREND
                    val waterProgress = if (waterGoal > 0f) (uiState.avgWaterL / waterGoal).coerceIn(0f, 1f) else 0f
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "DAILY HYDRATION AVERAGE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue,
                                    letterSpacing = 0.8.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = String.format(Locale.US, "%.1f L / %.1f L", uiState.avgWaterL, waterGoal),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                text = "${(waterProgress * 100).toInt()}% goal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        BarChart(
                            items = uiState.waterBarItems,
                            barColor = AccentBlue,
                            chartHeight = 120.dp
                        )
                    }
                }

                2 -> {
                    // SLEEP TREND (REAL USER SLEEP DATA FROM DATABASE)
                    val sleepGoal = uiState.userGoals.dailySleepGoalHours
                    val sleepProgress = if (sleepGoal > 0f) (uiState.avgSleepHours / sleepGoal).coerceIn(0f, 1f) else 0f

                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "AVERAGE SLEEP DURATION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentPurple,
                                    letterSpacing = 0.8.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = String.format(Locale.US, "%.1f hrs / night", uiState.avgSleepHours),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                text = "${(sleepProgress * 100).toInt()}% goal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentPurple
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        BarChart(
                            items = uiState.sleepBarItems,
                            barColor = AccentPurple,
                            chartHeight = 120.dp
                        )
                    }
                }

                3 -> {
                    // MACROS TREND
                    if (uiState.nutrientDonutSlices.isNotEmpty()) {
                        AppCard {
                            Text(
                                text = "Macronutrient Ratio Breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                DonutChart(
                                    slices = uiState.nutrientDonutSlices,
                                    chartSize = 130.dp,
                                    strokeWidth = 18.dp
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    uiState.nutrientDonutSlices.forEach { slice ->
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
                                                fontSize = 13.sp,
                                                color = TextSecondary
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "${slice.value.toInt()}%",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        AppCard {
                            Text(
                                text = "Macronutrient Ratio Breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No food logged for this period yet. Log your meals to view macro ratios.",
                                fontSize = 13.sp,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }

            // ─── 4. WEEKLY GOAL CONSISTENCY GRID ──────────────────────────────────
            AppCard {
                Text(
                    text = "Weekly Goal Consistency",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val days = listOf("M", "T", "W", "T", "F", "S", "S")

                    days.forEachIndexed { i, day ->
                        val dayItem = uiState.calorieLinePoints.getOrNull(i)
                        val calVal = dayItem?.value ?: 0f
                        val progressFraction = if (calorieGoal > 0) (calVal / calorieGoal).coerceIn(0f, 1f) else 0f

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            RingProgress(
                                progressFraction = progressFraction,
                                ringSize = 36.dp,
                                strokeWidth = 4.dp,
                                flatColor = if (progressFraction > 0f) AccentGreen else SurfaceCardAlt
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                fontSize = 10.sp,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }

            // ─── 5. HEALTH TRENDS INSIGHT CARD ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceCardAlt.copy(alpha = 0.75f))
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(AccentGreen.copy(alpha = 0.4f), Color(0xFF2C3242))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AccentGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Consistency Insight",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (uiState.avgCalorieIntake > 0 || uiState.avgWaterL > 0f)
                                "Your nutrition and hydration records are actively tracked. Great job staying consistent!"
                            else
                                "No entries logged yet for this period. Log your meals, water, and sleep to generate personal trends.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricFilterChip(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) color else SurfaceCardAlt.copy(alpha = 0.75f))
            .border(
                width = 1.dp,
                color = if (isSelected) color else Color(0xFF2C3242),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) BackgroundDark else color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) BackgroundDark else TextPrimary
            )
        }
    }
}
