package com.fitnessapp.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.ai.AiCoachReport
import com.fitnessapp.ai.HealthIntelligenceEngine
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.StepsRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.ui.components.charts.BarChartItem
import com.fitnessapp.ui.components.charts.DonutSlice
import com.fitnessapp.ui.components.charts.LineChartPoint
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentYellow
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AnalyticsUiState(
    val selectedPeriodIndex: Int = 0, // 0 = Week, 1 = Month, 2 = Year
    val avgCalorieIntake: Int = 0,
    val calorieDiffPercentage: String = "0%",
    val calorieLinePoints: List<LineChartPoint> = emptyList(),
    val nutrientDonutSlices: List<DonutSlice> = emptyList(),
    val avgWaterL: Float = 0f,
    val waterGoalL: Float = 2.5f,
    val waterBarItems: List<BarChartItem> = emptyList(),
    val avgSleepHours: Float = 0f,
    val sleepBarItems: List<BarChartItem> = emptyList(),
    val userGoals: UserGoals = UserGoals()
)

class AnalyticsViewModel(
    private val foodRepository: FoodRepository,
    private val waterRepository: WaterRepository,
    private val sleepRepository: SleepRepository,
    private val settingsRepository: SettingsRepository,
    private val stepsRepository: StepsRepository
) : ViewModel() {

    private val _selectedPeriodIndex = MutableStateFlow(0)
    val selectedPeriodIndex: StateFlow<Int> = _selectedPeriodIndex.asStateFlow()

    private val dayFormat = SimpleDateFormat("EEE", Locale.US)

    fun setSelectedPeriod(index: Int) {
        _selectedPeriodIndex.value = index
    }

    val uiState: StateFlow<AnalyticsUiState> = combine(
        _selectedPeriodIndex,
        foodRepository.getAllEntries(),
        waterRepository.getAllWaterEntries(),
        sleepRepository.getAllEntries(),
        settingsRepository.userGoals
    ) { periodIdx, foodEntries, waterEntries, sleepEntries, goals ->
        val now = System.currentTimeMillis()
        val numDays = when (periodIdx) {
            1 -> 30
            2 -> 365
            else -> 7
        }
        val startTime = DateUtils.startOfDayMillis(now - (numDays - 1) * 24 * 60 * 60 * 1000L)

        val recentFood = foodEntries.filter { it.dateMillis >= startTime }
        val recentWater = waterEntries.filter { it.dateMillis >= startTime }
        val recentSleep = sleepEntries.filter { it.dateMillis >= startTime }

        var totalProt = 0f
        var totalCarb = 0f
        var totalFat = 0f

        recentFood.forEach { entry ->
            totalProt += entry.proteinGrams
            totalCarb += entry.carbsGrams
            totalFat += entry.fatGrams
        }

        val totalCaloriesSum = recentFood.sumOf { it.calories }
        val activeFoodDays = recentFood.map { DateUtils.startOfDayMillis(it.dateMillis) }.distinct().size.coerceAtLeast(1)
        val avgCalorieIntake = if (recentFood.isNotEmpty()) totalCaloriesSum / activeFoodDays else 0

        val todayStart = DateUtils.todayStartMillis()
        val calorieLinePoints = (6 downTo 0).map { daysAgo ->
            val dayStart = DateUtils.startOfDayMillis(todayStart - daysAgo * 24 * 60 * 60 * 1000L)
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L - 1
            val dayTotal = foodEntries.filter { it.dateMillis in dayStart..dayEnd }.sumOf { it.calories }
            val label = dayFormat.format(Date(dayStart)).take(1)
            LineChartPoint(label, dayTotal.toFloat())
        }

        val sumMacros = (totalProt + totalCarb + totalFat).coerceAtLeast(0f)
        val donutSlices = if (recentFood.isNotEmpty() && sumMacros > 0f) {
            val protPct = (totalProt / sumMacros * 100).toInt()
            val carbPct = (totalCarb / sumMacros * 100).toInt()
            val fatPct = (100 - protPct - carbPct).coerceAtLeast(0)
            listOf(
                DonutSlice("Protein", protPct.toFloat(), AccentGreen),
                DonutSlice("Carbs", carbPct.toFloat(), AccentBlue),
                DonutSlice("Fats", fatPct.toFloat(), AccentYellow)
            )
        } else {
            emptyList()
        }

        val totalWaterSum = recentWater.sumOf { it.amountMl } / 1000f
        val activeWaterDays = recentWater.map { DateUtils.startOfDayMillis(it.dateMillis) }.distinct().size.coerceAtLeast(1)
        val avgWaterL = if (recentWater.isNotEmpty()) totalWaterSum / activeWaterDays else 0f

        val waterBarItems = (6 downTo 0).map { daysAgo ->
            val dayStart = DateUtils.startOfDayMillis(todayStart - daysAgo * 24 * 60 * 60 * 1000L)
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L - 1
            val dayWaterL = waterEntries.filter { it.dateMillis in dayStart..dayEnd }.sumOf { it.amountMl } / 1000f
            val label = dayFormat.format(Date(dayStart)).take(1)
            BarChartItem(label, dayWaterL)
        }

        val totalSleepHoursSum = recentSleep.sumOf { ((it.endMillis - it.startMillis) / (1000 * 60 * 60f)).toDouble() }.toFloat()
        val activeSleepDays = recentSleep.map { DateUtils.startOfDayMillis(it.dateMillis) }.distinct().size.coerceAtLeast(1)
        val avgSleepHours = if (recentSleep.isNotEmpty()) totalSleepHoursSum / activeSleepDays else 0f

        val sleepBarItems = (6 downTo 0).map { daysAgo ->
            val dayStart = DateUtils.startOfDayMillis(todayStart - daysAgo * 24 * 60 * 60 * 1000L)
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L - 1
            val daySleepEntry = sleepEntries.find { it.dateMillis in dayStart..dayEnd }
            val dayHours = if (daySleepEntry != null) {
                ((daySleepEntry.endMillis - daySleepEntry.startMillis) / (1000 * 60 * 60f)).coerceAtLeast(0f)
            } else 0f
            val label = dayFormat.format(Date(dayStart)).take(1)
            BarChartItem(label, dayHours)
        }

        val diffPct = if (avgCalorieIntake == 0) {
            "0%"
        } else if (avgCalorieIntake >= goals.dailyCalorieGoal) {
            "+${((avgCalorieIntake - goals.dailyCalorieGoal) * 100 / goals.dailyCalorieGoal)}%"
        } else {
            "-${((goals.dailyCalorieGoal - avgCalorieIntake) * 100 / goals.dailyCalorieGoal)}%"
        }

        AnalyticsUiState(
            selectedPeriodIndex = periodIdx,
            avgCalorieIntake = avgCalorieIntake,
            calorieDiffPercentage = diffPct,
            calorieLinePoints = calorieLinePoints,
            nutrientDonutSlices = donutSlices,
            avgWaterL = avgWaterL,
            waterGoalL = goals.dailyWaterGoal / 1000f,
            waterBarItems = waterBarItems,
            avgSleepHours = avgSleepHours,
            sleepBarItems = sleepBarItems,
            userGoals = goals
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )

    // ── AI Coach State (Whoop / Google Health style deep insights) ────────────

    val aiCoachState: StateFlow<AiCoachReport> = combine(
        foodRepository.getAllEntries(),
        waterRepository.getAllWaterEntries(),
        sleepRepository.getAllEntries(),
        stepsRepository.getAllStepsEntries(),
        settingsRepository.userGoals
    ) { foodEntries, waterEntries, sleepEntries, stepsEntries, goals ->
        val todayStart = DateUtils.todayStartMillis()
        val todaySteps = stepsEntries.filter { it.dateMillis >= todayStart }
            .maxByOrNull { it.count }?.count ?: 0
        HealthIntelligenceEngine.buildReportWithSteps(
            foodEntries = foodEntries,
            waterEntries = waterEntries,
            sleepEntries = sleepEntries,
            goals = goals,
            todaySteps = todaySteps
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AiCoachReport()
    )

    class Factory(
        private val foodRepository: FoodRepository,
        private val waterRepository: WaterRepository,
        private val sleepRepository: SleepRepository,
        private val settingsRepository: SettingsRepository,
        private val stepsRepository: StepsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AnalyticsViewModel(
                foodRepository, waterRepository, sleepRepository, settingsRepository, stepsRepository
            ) as T
        }
    }
}
