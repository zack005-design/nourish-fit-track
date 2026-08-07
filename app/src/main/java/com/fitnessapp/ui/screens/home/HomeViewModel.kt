package com.fitnessapp.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.db.entity.WaterEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.util.DateUtils
import com.fitnessapp.util.HealthConnectManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class HomeUiState(
    val selectedDateMillis: Long = DateUtils.todayStartMillis(),
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val totalWaterL: Float = 0f,
    val totalSleepHours: Float = 0f,
    val sleepScore: Int = 0,
    val userGoals: UserGoals = UserGoals(),
    val logStreak: Int = 0,
    val isHealthConnectAvailable: Boolean = false,
    val aiInsightPrimary: String = "Welcome to Nourish! Start by logging your meals, water, or sleep today.",
    val aiInsightSecondary: String = "Tap the + icon on Water or Sleep to record your progress."
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
    val goals: UserGoals
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val foodRepository: FoodRepository,
    private val sleepRepository: SleepRepository,
    private val waterRepository: WaterRepository,
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
            settingsRepository.userGoals
        ) { sleep, goals ->
            ActivityTotals(sleep, goals)
        }
    }

    private val allFoodEntriesFlow = foodRepository.getAllEntries()

    val uiState: StateFlow<HomeUiState> = combine(
        _selectedDateMillis, macrosFlow, microsFlow, activityFlow, allFoodEntriesFlow
    ) { date, macros, micros, activity, allEntries ->
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
        val goals = activity.goals

        // Compute consecutive log streak from food entry history
        val loggedDays = allEntries
            .map { entry ->
                val cal = Calendar.getInstance().apply { timeInMillis = entry.dateMillis }
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            .toSortedSet(reverseOrder())

        var streak = 0
        var checkDay = DateUtils.todayStartMillis()
        while (loggedDays.contains(checkDay)) {
            streak++
            checkDay -= TimeUnit.DAYS.toMillis(1)
        }

        val (primaryInsight, secondaryInsight) = generateSmartAiInsights(
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            fiber = fiber,
            totalWaterL = totalWaterL,
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
            userGoals = goals,
            logStreak = streak,
            aiInsightPrimary = primaryInsight,
            aiInsightSecondary = secondaryInsight
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun addWater(amountMl: Int, context: Context? = null) {
        viewModelScope.launch {
            val entry = WaterEntry(
                dateMillis = _selectedDateMillis.value,
                amountMl = amountMl
            )
            val insertedId = waterRepository.insert(entry)
            context?.let { ctx ->
                val entryToSync = entry.copy(id = insertedId)
                HealthConnectManager.insertHydrationRecords(ctx, listOf(entryToSync))
            }
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

    private fun generateSmartAiInsights(
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float,
        fiber: Float,
        totalWaterL: Float,
        sleepEntry: SleepEntry?,
        goals: UserGoals
    ): Pair<String, String> {
        val hasAnyData = calories > 0 || protein > 0f || totalWaterL > 0f || sleepEntry != null

        if (!hasAnyData) {
            return Pair(
                "Welcome to Nourish AI! Log your meals, water, or sleep to activate your personalized health insights.",
                "Tap + on Food or Water to record your progress for today."
            )
        }

        val calRatio = if (goals.dailyCalorieGoal > 0) calories.toFloat() / goals.dailyCalorieGoal else 0f
        val protRatio = if (goals.dailyProteinGoal > 0) protein / goals.dailyProteinGoal else 0f
        val waterGoalL = goals.dailyWaterGoal / 1000f
        val timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeGreeting = when (timeHour) {
            in 5..11 -> "Morning"
            in 12..16 -> "Afternoon"
            in 17..21 -> "Evening"
            else -> "Night"
        }

        val primary = when {
            calRatio >= 1.1f ->
                "[$timeGreeting] Calorie intake is above daily target ($calories / ${goals.dailyCalorieGoal} kcal). Focus on hydration and high-fiber foods for satiety."
            calRatio in 0.85f..1.1f ->
                "[$timeGreeting] Excellent caloric balance! Energy intake is right on target for your body goals."
            protRatio >= 1.0f ->
                "[$timeGreeting] Protein goal hit! ${protein.toInt()}g logged today. Great support for muscle synthesis and recovery."
            totalWaterL >= waterGoalL ->
                "[$timeGreeting] Hydration goal achieved! ${String.format("%.1f", totalWaterL)} L logged today."
            else ->
                "[$timeGreeting] Healthy progress started! $calories kcal and ${String.format("%.1f", totalWaterL)} L water recorded."
        }

        val secondary = when {
            totalWaterL < waterGoalL ->
                "Hydration recommendation: Drink another ${String.format("%.1f", (waterGoalL - totalWaterL).coerceAtLeast(0.2f))} L to complete your daily goal."
            protRatio < 0.7f && calories > 1000 ->
                "Nutrition tip: Protein is currently at ${protein.toInt()}g / ${goals.dailyProteinGoal.toInt()}g. Consider adding Greek yogurt, eggs, or chicken."
            sleepEntry != null && sleepEntry.quality >= 4 ->
                "Recovery status: Optimal sleep quality recorded (${sleepEntry.quality}/5). Great baseline for metabolic stability."
            else ->
                "Consistency tip: Keep logging daily to optimize your health metrics."
        }

        return Pair(primary, secondary)
    }

    class Factory(
        private val foodRepository: FoodRepository,
        private val sleepRepository: SleepRepository,
        private val waterRepository: WaterRepository,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(
                foodRepository,
                sleepRepository,
                waterRepository,
                settingsRepository
            ) as T
        }
    }
}
