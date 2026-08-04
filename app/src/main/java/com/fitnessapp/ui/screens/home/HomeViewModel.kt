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

        val (primaryInsight, secondaryInsight) = generateSmartAiInsights(
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            fiber = fiber,
            totalWaterL = totalWaterL,
            stepsCount = stepsCount,
            sleepEntry = sleepEntry,
            goals = goals
        )

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

    fun removeWater(amountMl: Int) {
        viewModelScope.launch {
            val currentL = uiState.value.totalWaterL
            val currentMl = (currentL * 1000).toInt()
            val newMl = (currentMl - amountMl).coerceAtLeast(0)
            waterRepository.deleteForDate(_selectedDateMillis.value)
            if (newMl > 0) {
                waterRepository.insert(
                    WaterEntry(
                        dateMillis = _selectedDateMillis.value,
                        amountMl = newMl
                    )
                )
            }
        }
    }

    fun clearWaterForDate() {
        viewModelScope.launch {
            waterRepository.deleteForDate(_selectedDateMillis.value)
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

    private fun generateSmartAiInsights(
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float,
        fiber: Float,
        totalWaterL: Float,
        stepsCount: Int,
        sleepEntry: SleepEntry?,
        goals: UserGoals
    ): Pair<String, String> {
        val hasAnyData = calories > 0 || protein > 0f || totalWaterL > 0f || stepsCount > 0 || sleepEntry != null

        if (!hasAnyData) {
            return Pair(
                "Welcome to Nourish AI! Log your meals, water, or steps to activate your personalized health insights.",
                "Tap + on Water or Steps below, or log a meal to get real-time nutrition and recovery recommendations."
            )
        }

        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val timeGreeting = when (currentHour) {
            in 5..11 -> "Morning Briefing"
            in 12..16 -> "Afternoon Check"
            in 17..21 -> "Evening Summary"
            else -> "Night Recovery"
        }

        // Cross-metric intelligent reasoning
        val waterGoalL = goals.dailyWaterGoal / 1000f
        val proteinTarget = goals.dailyProteinGoal
        val calorieTarget = goals.dailyCalorieGoal
        val stepsTarget = goals.dailyStepsGoal

        val primary = when {
            protein >= proteinTarget -> "[$timeGreeting] Excellent work hitting your protein goal! (${protein.toInt()}g / ${proteinTarget.toInt()}g) Great for muscle synthesis."
            stepsCount >= stepsTarget && totalWaterL < waterGoalL -> "[$timeGreeting] You've hit your step goal of $stepsCount steps! Boost hydration now with extra water to support muscle recovery."
            calories >= calorieTarget -> "[$timeGreeting] You've reached your daily energy target of $calorieTarget kcal. Focus on fiber and light hydration for the rest of the day."
            sleepEntry != null && sleepEntry.quality >= 4 -> "[$timeGreeting] High quality sleep (${sleepEntry.quality}/5) logged! Your metabolism and recovery state are primed today."
            stepsCount >= stepsTarget -> "[$timeGreeting] Step target unlocked! $stepsCount steps logged today."
            totalWaterL >= waterGoalL -> "[$timeGreeting] Hydration goal achieved! ${String.format("%.1f", totalWaterL)} L logged today."
            protein > 0f -> "[$timeGreeting] You've logged ${protein.toInt()}g protein (${(protein / proteinTarget * 100).toInt()}% of goal) and $calories kcal so far."
            else -> "[$timeGreeting] Healthy progress started! $calories kcal and ${String.format("%.1f", totalWaterL)} L water recorded."
        }

        val secondary = when {
            totalWaterL < waterGoalL -> "Hydration recommendation: Drink another ${String.format("%.1f", (waterGoalL - totalWaterL).coerceAtLeast(0.2f))} L to complete your daily goal."
            fiber < goals.dailyFiberGoal -> "Nutrient tip: Increase fiber intake (${fiber.toInt()}g / ${goals.dailyFiberGoal.toInt()}g) by adding leafy greens or seeds to your next meal."
            stepsCount < stepsTarget -> "Activity suggestion: A quick 15-minute walk will add ~1,500 steps towards your $stepsTarget target."
            protein < proteinTarget -> "Protein focus: Add a protein snack (greek yogurt, eggs, paneer) to reach your ${proteinTarget.toInt()}g target."
            else -> "All core metrics are on track! Maintain this balanced nutrition and recovery schedule."
        }

        return Pair(primary, secondary)
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
