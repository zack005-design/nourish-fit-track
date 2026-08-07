package com.fitnessapp.ui.screens.food

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.LinearBar
import com.fitnessapp.ui.components.frostedGlass
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentCyan
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.AccentRed
import com.fitnessapp.ui.theme.AccentYellow
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import com.fitnessapp.ui.theme.TextTertiary
import com.fitnessapp.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionDetailsScreen(
    foodRepository: FoodRepository,
    onBack: () -> Unit,
    onAddFoodClick: () -> Unit,
    onEditEntryClick: (Long) -> Unit,
    viewModel: FoodViewModel = viewModel(factory = FoodViewModel.Factory(foodRepository))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    val dateLabel = remember(uiState.selectedDateMillis) {
        val isToday = DateUtils.startOfDayMillis(uiState.selectedDateMillis) == DateUtils.todayStartMillis()
        if (isToday) "Today" else SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(uiState.selectedDateMillis))
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
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {

            TopAppBar(
                modifier = Modifier.frostedGlass(
                    backgroundColor = BackgroundDark.copy(alpha = 0.82f),
                    fallbackColor = BackgroundDark
                ),
                title = {
                    Text(
                        text = "Nutrition Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            // Pinned solid AccentGreen "+ Add Food" Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(
                        backgroundColor = BackgroundDark.copy(alpha = 0.82f),
                        fallbackColor = BackgroundDark
                    )
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onAddFoodClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = BackgroundDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Food",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BackgroundDark
                    )
                }
            }
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
            // Date Selector Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.onPreviousDay() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Day",
                        tint = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceCardAlt)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = dateLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                }

                IconButton(onClick = { viewModel.onNextDay() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Day",
                        tint = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Calendar",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 4-Up Macro Summary Strip Card
            AppCard(contentPadding = 12.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MacroSummaryCell("Calories", "${uiState.totalCalories}", "2,200 kcal", AccentOrange)
                    MacroSummaryCell("Protein", "${uiState.totalProtein.toInt()} g", "140 g", AccentGreen)
                    MacroSummaryCell("Carbs", "${uiState.totalCarbs.toInt()} g", "250 g", AccentBlue)
                    MacroSummaryCell("Fats", "${uiState.totalFat.toInt()} g", "70 g", AccentYellow)
                }
            }

            // Nutrient Summary Card
            AppCard {
                Text(
                    text = "Nutrient Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                NutrientDetailRow("Fiber", "${uiState.totalFiber.toInt()} g / 30 g", uiState.totalFiber / 30f, AccentPurple)
                Spacer(modifier = Modifier.height(10.dp))

                NutrientDetailRow("Sugar", "${uiState.totalSugar.toInt()} g / 50 g", uiState.totalSugar / 50f, AccentPurple)
                Spacer(modifier = Modifier.height(10.dp))

                NutrientDetailRow("Sodium", "${uiState.totalSodium.toInt()} mg / 2,300 mg", uiState.totalSodium / 2300f, AccentRed)
                Spacer(modifier = Modifier.height(10.dp))

                NutrientDetailRow("Cholesterol", "${uiState.totalCholesterol.toInt()} mg / 300 mg", uiState.totalCholesterol / 300f, AccentRed)
            }

            // Meals List Card
            AppCard {
                Text(
                    text = "Meals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.entries.isEmpty()) {
                    Text(
                        text = "No meals logged for this date.",
                        fontSize = 13.sp,
                        color = TextTertiary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    uiState.entries.forEach { entry ->
                        MealItemRow(
                            entry = entry,
                            onClick = { onEditEntryClick(entry.id) },
                            onDelete = {
                                viewModel.deleteEntry(entry) {}
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
private fun MacroSummaryCell(
    label: String,
    valueText: String,
    goalText: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = valueText, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "/ $goalText", fontSize = 10.sp, color = TextTertiary)
    }
}

@Composable
private fun NutrientDetailRow(
    label: String,
    valueText: String,
    progressFraction: Float,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = TextSecondary
            )
            Text(
                text = valueText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        LinearBar(
            progressFraction = progressFraction,
            barColor = color,
            barHeight = 6.dp
        )
    }
}
