package com.fitnessapp.ui.screens.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FoodViewModel(private val foodRepository: FoodRepository) : ViewModel() {

    private val todayStartMillis = DateUtils.todayStartMillis()

    val todayEntries: StateFlow<List<FoodEntry>> = foodRepository
        .getEntriesForDate(todayStartMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEntries: StateFlow<List<FoodEntry>> = foodRepository
        .getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCalories: StateFlow<Int?> = foodRepository
        .getTotalCaloriesForDate(todayStartMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addEntry(name: String, calories: Int, protein: Float, carbs: Float, fat: Float, mealType: String) {
        viewModelScope.launch {
            foodRepository.insert(
                FoodEntry(
                    name = name,
                    calories = calories,
                    proteinGrams = protein,
                    carbsGrams = carbs,
                    fatGrams = fat,
                    mealType = mealType,
                    dateMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateEntry(id: Long, name: String, calories: Int, protein: Float, carbs: Float, fat: Float, mealType: String) {
        viewModelScope.launch {
            // We need to fetch it to keep the original dateMillis or pass it. 
            // Simple approach: we fetch first
            val currentEntry = kotlinx.coroutines.flow.firstOrNull(foodRepository.getEntryById(id))
            if (currentEntry != null) {
                foodRepository.update(
                    currentEntry.copy(
                        name = name,
                        calories = calories,
                        proteinGrams = protein,
                        carbsGrams = carbs,
                        fatGrams = fat,
                        mealType = mealType
                    )
                )
            }
        }
    }

    fun getEntry(id: Long) = foodRepository.getEntryById(id)

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch {
            foodRepository.delete(entry)
        }
    }

    class Factory(private val foodRepository: FoodRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodViewModel(foodRepository) as T
        }
    }
}
