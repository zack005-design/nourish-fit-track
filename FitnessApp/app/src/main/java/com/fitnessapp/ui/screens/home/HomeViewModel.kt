package com.fitnessapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val calorieTarget: Int = 2000,
    val sleepTargetHours: Float = 8.0f,
    val todaySleep: SleepEntry? = null,
    val recentFoodEntries: List<FoodEntry> = emptyList(),
    val recentSleepEntries: List<SleepEntry> = emptyList()
)

class HomeViewModel(
    private val foodRepository: FoodRepository,
    private val sleepRepository: SleepRepository,
    private val settingsRepository: com.fitnessapp.data.repository.SettingsRepository
) : ViewModel() {

    private val todayStartMillis = DateUtils.todayStartMillis()

    val uiState: StateFlow<HomeUiState> = combine(
        foodRepository.getTotalCaloriesForDate(todayStartMillis),
        foodRepository.getTotalProteinForDate(todayStartMillis),
        foodRepository.getTotalCarbsForDate(todayStartMillis),
        foodRepository.getTotalFatForDate(todayStartMillis),
        sleepRepository.getEntriesForDate(todayStartMillis),
        foodRepository.getEntriesForDate(todayStartMillis)
    ) { totalCalories, totalProtein, totalCarbs, totalFat, sleepEntries, foodEntries ->
        HomeUiState(
            totalCalories = totalCalories ?: 0,
            totalProtein = totalProtein ?: 0f,
            totalCarbs = totalCarbs ?: 0f,
            totalFat = totalFat ?: 0f,
            calorieTarget = settingsRepository.getDailyCalorieTarget(),
            sleepTargetHours = settingsRepository.getSleepTargetHours(),
            todaySleep = sleepEntries.maxByOrNull { it.endMillis - it.startMillis },
            recentFoodEntries = foodEntries,
            recentSleepEntries = sleepEntries
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    class Factory(
        private val foodRepository: FoodRepository,
        private val sleepRepository: SleepRepository,
        private val settingsRepository: com.fitnessapp.data.repository.SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(foodRepository, sleepRepository, settingsRepository) as T
        }
    }
}
