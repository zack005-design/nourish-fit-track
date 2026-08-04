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
