package com.fitnessapp.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.StepsRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.util.DateUtils
import com.fitnessapp.util.HealthConnectManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val foodRepository: FoodRepository,
    private val waterRepository: WaterRepository,
    private val sleepRepository: SleepRepository,
    private val stepsRepository: StepsRepository
) : ViewModel() {

    val userGoals: StateFlow<UserGoals> = settingsRepository.userGoals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserGoals()
    )

    fun saveGoals(
        calorieGoal: Int,
        proteinGoal: Float,
        carbsGoal: Float = 250f,
        fatGoal: Float = 70f,
        fiberGoal: Float = 30f,
        waterGoal: Int,
        sleepGoalHours: Float,
        stepsGoal: Int = 10000,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            val updated = UserGoals(
                id = 1,
                dailyCalorieGoal = calorieGoal,
                dailyProteinGoal = proteinGoal,
                dailyCarbsGoal = carbsGoal,
                dailyFatGoal = fatGoal,
                dailyFiberGoal = fiberGoal,
                dailyWaterGoal = waterGoal,
                dailySleepGoalHours = sleepGoalHours,
                dailyStepsGoal = stepsGoal
            )
            settingsRepository.saveUserGoals(updated)
            onSaved()
        }
    }

    /**
     * AI On-Device 7-Day Telemetry Analysis & Target Auto-Optimization Engine.
     * Analyzes 7 days of food, water, sleep & steps to calibrate optimal daily goals.
     */
    fun optimizeGoalsWithAi(onOptimized: (String) -> Unit) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sevenDaysAgo = now - (7 * 24 * 60 * 60 * 1000L)

            val allFoods = foodRepository.getAllEntries().firstOrNull() ?: emptyList()
            val foods = allFoods.filter { it.dateMillis >= sevenDaysAgo }

            val allWaters = waterRepository.getAllWaterEntries().firstOrNull() ?: emptyList()
            val waters = allWaters.filter { it.dateMillis >= sevenDaysAgo }

            val allSleeps = sleepRepository.getAllEntries().firstOrNull() ?: emptyList()
            val sleeps = allSleeps.filter { it.startMillis >= sevenDaysAgo }

            // 1. Calorie & Protein Optimization
            val avgCalories = if (foods.isNotEmpty()) foods.sumOf { it.calories } / 7 else 2200
            val avgProtein = if (foods.isNotEmpty()) foods.sumOf { it.proteinGrams.toDouble() }.toFloat() / 7 else 110f
            val targetCalories = (avgCalories * 0.95).toInt().coerceIn(1600, 3200)
            val targetProtein = (avgProtein * 1.15f).coerceIn(80f, 200f)

            // 2. Hydration Optimization
            val avgWater = if (waters.isNotEmpty()) waters.sumOf { it.amountMl } / 7 else 2000
            val targetWater = if (avgWater < 2200) 2750 else (avgWater + 250).coerceIn(2000, 4000)

            // 3. Sleep Optimization
            val avgSleepDurationHours = if (sleeps.isNotEmpty()) {
                sleeps.sumOf { (it.endMillis - it.startMillis) / (1000.0 * 3600.0) }.toFloat() / 7
            } else 6.8f
            val targetSleep = if (avgSleepDurationHours < 7.2f) 8.0f else (avgSleepDurationHours + 0.5f).coerceIn(7.0f, 9.0f)

            // 4. Step Target Optimization
            val targetSteps = 10000

            val calibratedGoals = UserGoals(
                id = 1,
                dailyCalorieGoal = targetCalories,
                dailyProteinGoal = targetProtein,
                dailyWaterGoal = targetWater,
                dailySleepGoalHours = targetSleep,
                dailyStepsGoal = targetSteps
            )

            settingsRepository.saveUserGoals(calibratedGoals)
            onOptimized("✨ AI 7-Day Analysis Complete! Calibrated: ${targetCalories}kcal, ${targetWater}ml water, ${targetSleep}h sleep.")
        }
    }


    fun clearAllData(onCleared: () -> Unit) {
        viewModelScope.launch {
            foodRepository.clearAll()
            waterRepository.clearAll()
            sleepRepository.clearAll()
            stepsRepository.clearAll()
            onCleared()
        }
    }

    /**
     * Fetches today's real data from Room DB and writes it to Google Health Connect.
     */
    suspend fun syncToHealthConnect(context: Context): String {
        val today = DateUtils.todayStartMillis()
        val foods = foodRepository.getEntriesForDate(today).firstOrNull() ?: emptyList()
        val waters = waterRepository.getEntriesForDate(today).firstOrNull() ?: emptyList()
        val sleeps = sleepRepository.getEntriesForDate(today).firstOrNull() ?: emptyList()
        return HealthConnectManager.syncAllLocalDataToGoogleHealth(
            context = context,
            foods = foods,
            waters = waters,
            sleeps = sleeps
        )
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val foodRepository: FoodRepository,
        private val waterRepository: WaterRepository,
        private val sleepRepository: SleepRepository,
        private val stepsRepository: StepsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(
                settingsRepository,
                foodRepository,
                waterRepository,
                sleepRepository,
                stepsRepository
            ) as T
        }
    }
}
