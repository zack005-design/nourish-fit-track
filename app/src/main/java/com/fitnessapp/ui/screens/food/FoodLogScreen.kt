package com.fitnessapp.ui.screens.food

import com.fitnessapp.util.DateUtils

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import com.fitnessapp.ui.components.frostedGlass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.LinearBar
import com.fitnessapp.ui.components.charts.BarChart
import com.fitnessapp.ui.components.charts.BarChartItem
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.AccentRed
import com.fitnessapp.ui.theme.BorderSubtle
import com.fitnessapp.ui.theme.AccentYellow
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import com.fitnessapp.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogScreen(
    foodRepository: FoodRepository,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onNavigateToDetails: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: FoodViewModel = viewModel(factory = FoodViewModel.Factory(foodRepository))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Today", "Week", "Month")

    val hourlyBarItems = remember(uiState.entries) {
        val hoursMap = FloatArray(12)
        val calendar = java.util.Calendar.getInstance()
        uiState.entries.forEach { entry ->
            calendar.timeInMillis = entry.dateMillis
            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val slot = (hour / 2).coerceIn(0, 11)
            hoursMap[slot] += entry.calories.toFloat()
        }
        listOf(
            BarChartItem("12 AM", hoursMap[0]),
            BarChartItem("", hoursMap[1]),
            BarChartItem("", hoursMap[2]),
            BarChartItem("6 AM", hoursMap[3]),
            BarChartItem("", hoursMap[4]),
            BarChartItem("", hoursMap[5]),
            BarChartItem("12 PM", hoursMap[6]),
            BarChartItem("", hoursMap[7]),
            BarChartItem("", hoursMap[8]),
            BarChartItem("6 PM", hoursMap[9]),
            BarChartItem("", hoursMap[10]),
            BarChartItem("12 AM", hoursMap[11])
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = AccentGreen,
                contentColor = BackgroundDark,
                shape = CircleShape,
                modifier = Modifier
                    .navigationBarsPadding()
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Food",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Nutrition",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            // Segmented Tabs Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceCardAlt)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    tabTitles.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) AccentGreen.copy(alpha = 0.25f) else Color.Transparent)
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) AccentGreen else TextSecondary
                            )
                        }
                    }
                }
            }

            // Calories Card with Hourly BarChart
            val calorieGoal = 2200
            val calorieProgress = if (calorieGoal > 0) uiState.totalCalories.toFloat() / calorieGoal else 0f
            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Calories",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format(Locale.US, "%,d", uiState.totalCalories),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = " / ${String.format(Locale.US, "%,d", calorieGoal)} kcal",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "${(calorieProgress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                BarChart(
                    items = hourlyBarItems,
                    barColor = AccentOrange,
                    chartHeight = 110.dp
                )
            }

            // Macronutrients Breakdown Card
            val proteinGoal = 140f
            val carbsGoal = 250f
            val fatGoal = 70f
            val fiberGoal = 30f

            AppCard {
                Text(
                    text = "Macronutrients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Protein Row
                MacroRowItem(
                    label = "Protein",
                    currentGrams = uiState.totalProtein.toInt(),
                    goalGrams = proteinGoal.toInt(),
                    color = AccentGreen
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Carbs Row
                MacroRowItem(
                    label = "Carbs",
                    currentGrams = uiState.totalCarbs.toInt(),
                    goalGrams = carbsGoal.toInt(),
                    color = AccentBlue
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Fats Row
                MacroRowItem(
                    label = "Fats",
                    currentGrams = uiState.totalFat.toInt(),
                    goalGrams = fatGoal.toInt(),
                    color = AccentYellow
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Fiber Row
                MacroRowItem(
                    label = "Fiber",
                    currentGrams = uiState.totalFiber.toInt(),
                    goalGrams = fiberGoal.toInt(),
                    color = AccentPurple
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Link to Nutrition Details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToDetails() }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View Nutrition Details",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Nutrition Details",
                        tint = TextSecondary
                    )
                }
            }

            // Meals Card List
            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Meals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val filteredEntries = remember(uiState.entries, selectedTab) {
                    val todayStart = DateUtils.todayStartMillis()
                    val weekStart = todayStart - 6 * 24 * 60 * 60 * 1000L
                    val monthStart = todayStart - 29 * 24 * 60 * 60 * 1000L

                    when (selectedTab) {
                        0 -> uiState.entries.filter { DateUtils.startOfDayMillis(it.dateMillis) == todayStart }
                        1 -> uiState.entries.filter { it.dateMillis >= weekStart }
                        else -> uiState.entries.filter { it.dateMillis >= monthStart }
                    }
                }

                if (filteredEntries.isEmpty()) {
                    Text(
                        text = "No meals logged for this ${tabTitles[selectedTab].lowercase()}.",
                        fontSize = 13.sp,
                        color = TextTertiary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    filteredEntries.forEach { entry ->
                        MealItemRow(
                            entry = entry,
                            onClick = { onEditClick(entry.id) },
                            onDelete = {
                                viewModel.deleteEntry(entry) {
                                    onShowSnackbar("${entry.name} removed")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MacroRowItem(
    label: String,
    currentGrams: Int,
    goalGrams: Int,
    color: Color
) {
    val fraction = if (goalGrams > 0) currentGrams.toFloat() / goalGrams else 0f

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            Text(
                text = "$currentGrams g / $goalGrams g",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        LinearBar(
            progressFraction = fraction,
            barColor = color,
            barHeight = 6.dp
        )
    }
}

@Composable
fun MealItemRow(
    entry: FoodEntry,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(entry.dateMillis))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCardAlt)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AccentOrange.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                tint = AccentOrange,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${entry.mealType} • $timeFormatted",
                fontSize = 11.sp,
                color = TextTertiary
            )
        }

        Text(
            text = "${entry.calories} kcal",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AccentOrange
        )

        if (onDelete != null) {
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete meal",
                    tint = AccentRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Edit",
            tint = TextTertiary
        )
    }
}
