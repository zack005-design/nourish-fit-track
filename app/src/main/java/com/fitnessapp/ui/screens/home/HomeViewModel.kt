package com.fitnessapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.StepsEntry
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.db.entity.WaterEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.StepsRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedDateMillis: Long = DateUtils.todayStartMillis(),
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val totalWaterL: Float = 0f,
    val totalSleepHours: Float = 0f,
    val sleepScore: Int = 0,
    val stepsCount: Int = 0,
    val userGoals: UserGoals = UserGoals(),
    val aiInsightPrimary: String = "Welcome to Nourish! Start by logging your meals, water, or steps today.",
    val aiInsightSecondary: String = "Tap the + icon on Water or Steps to record your progress."
)

private data class MacroTotals(
    val calories: Int?,
    val protein: Float?,
    val carbs: Float?,
    val fat: Float?
)

private data class MicroTotals(
    val fiber: Float?,
    val waterMl: Int?
)

private data class ActivityTotals(
    val sleepEntries: List<SleepEntry>,
    val stepsEntry: StepsEntry?,
    val goals: UserGoals
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val foodRepository: FoodRepository,
    private val sleepRepository: SleepRepository,
    private val waterRepository: WaterRepository,
    private val stepsRepository: StepsRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedDateMillis = MutableStateFlow(DateUtils.todayStartMillis())
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    fun setSelectedDate(millis: Long) {
        _selectedDateMillis.value = DateUtils.startOfDayMillis(millis)
    }

    private val macrosFlow = _selectedDateMillis.flatMapLatest { date ->
        combine(
            foodRepository.getTotalCaloriesForDate(date),
            foodRepository.getTotalProteinForDate(date),
            foodRepository.getTotalCarbsForDate(date),
            foodRepository.getTotalFatForDate(date)
        ) { calories, protein, carbs, fat ->
            MacroTotals(calories, protein, carbs, fat)
        }
    }

    private val microsFlow = _selectedDateMillis.flatMapLatest { date ->
        combine(
            foodRepository.getTotalFiberForDate(date),
            waterRepository.getTotalWaterForDate(date)
        ) { fiber, water ->
            MicroTotals(fiber, water)
        }
    }

    private val activityFlow = _selectedDateMillis.flatMapLatest { date ->
        combine(
            sleepRepository.getEntriesForDate(date),
            stepsRepository.getStepsForDate(date),
            settingsRepository.userGoals
        ) { sleep, steps, goals ->
            ActivityTotals(sleep, steps, goals)
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(_selectedDateMillis, macrosFlow, microsFlow, activityFlow) { date, macros, micros, activity ->
        val calories = macros.calories ?: 0
        val protein = macros.protein ?: 0f
        val carbs = macros.carbs ?: 0f
        val fat = macros.fat ?: 0f
        val fiber = micros.fiber ?: 0f
        val waterMl = micros.waterMl ?: 0

        val sleepEntry = activity.sleepEntries.firstOrNull()
        val sleepMinutes = if (sleepEntry != null) {
            ((sleepEntry.endMillis - sleepEntry.startMillis) / (1000 * 60)).toInt()
        } else {
            0
        }

        val totalWaterL = waterMl / 1000f
        val stepsCount = activity.stepsEntry?.count ?: 0
        val goals = activity.goals

        val hasAnyData = calories > 0 || protein > 0f || waterMl > 0 || stepsCount > 0 || sleepEntry != null

        val primaryInsight = if (!hasAnyData) {
            "Welcome to Nourish! Start logging your meals, water, or steps for today."
        } else when {
            protein >= goals.dailyProteinGoal -> "Good job hitting your protein goal! (${protein.toInt()}g / ${goals.dailyProteinGoal.toInt()}g)"
            calories >= goals.dailyCalorieGoal -> "You've met your daily calorie target of ${goals.dailyCalorieGoal} kcal."
            totalWaterL >= (goals.dailyWaterGoal / 1000f) -> "Hydration target reached! Great job drinking ${String.format("%.1f", totalWaterL)} L of water today."
            stepsCount >= goals.dailyStepsGoal -> "Step target unlocked! You've logged $stepsCount steps today."
            else -> "Stay consistent! You've logged ${protein.toInt()}g protein and $calories kcal so far."
        }

        val secondaryInsight = if (!hasAnyData) {
            "Tap the + icon on Water or Steps to record your progress."
        } else when {
            fiber < goals.dailyFiberGoal -> "Try adding more veggies to improve fiber intake (${fiber.toInt()}g / ${goals.dailyFiberGoal.toInt()}g)."
            totalWaterL < (goals.dailyWaterGoal / 1000f) -> "Drink another glass of water to reach your ${goals.dailyWaterGoal / 1000f}L hydration goal."
            stepsCount < goals.dailyStepsGoal -> "A short walk will add steps toward your ${goals.dailyStepsGoal} goal."
            else -> "Keep up the great balanced routine for the rest of the day."
        }

        HomeUiState(
            selectedDateMillis = date,
            totalCalories = calories,
            totalProtein = protein,
            totalCarbs = carbs,
            totalFat = fat,
            totalWaterL = totalWaterL,
            totalSleepHours = sleepMinutes / 60f,
            sleepScore = sleepEntry?.quality ?: 0,
            stepsCount = stepsCount,
            userGoals = goals,
            aiInsightPrimary = primaryInsight,
            aiInsightSecondary = secondaryInsight
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            waterRepository.insert(
                WaterEntry(
                    dateMillis = _selectedDateMillis.value,
                    amountMl = amountMl
                )
            )
        }
    }

    fun addSteps(countDelta: Int) {
        viewModelScope.launch {
            val current = uiState.value.stepsCount
            stepsRepository.insertOrUpdate(
                StepsEntry(
                    dateMillis = _selectedDateMillis.value,
                    count = current + countDelta
                )
            )
        }
    }

    class Factory(
        private val foodRepository: FoodRepository,
        private val sleepRepository: SleepRepository,
        private val waterRepository: WaterRepository,
        private val stepsRepository: StepsRepository,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(
                foodRepository,
                sleepRepository,
                waterRepository,
                stepsRepository,
                settingsRepository
            ) as T
        }
    }
}
