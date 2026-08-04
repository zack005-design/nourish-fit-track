package com.fitnessapp.ui.screens.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class FoodUiState(
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val entries: List<FoodEntry> = emptyList(),
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val totalFiber: Float = 0f,
    val totalSugar: Float = 0f,
    val totalSodium: Float = 0f,
    val totalCholesterol: Float = 0f
)

@OptIn(ExperimentalCoroutinesApi::class)
class FoodViewModel(private val foodRepository: FoodRepository) : ViewModel() {

    private val _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    fun setSelectedDate(millis: Long) {
        _selectedDateMillis.value = DateUtils.startOfDayMillis(millis)
    }

    val uiState: StateFlow<FoodUiState> = _selectedDateMillis.flatMapLatest { dateMillis ->
        foodRepository.getEntriesForDate(dateMillis).map { entries ->
            val calories = entries.sumOf { it.calories }
            val protein = entries.sumOf { it.proteinGrams.toDouble() }.toFloat()
            val carbs = entries.sumOf { it.carbsGrams.toDouble() }.toFloat()
            val fat = entries.sumOf { it.fatGrams.toDouble() }.toFloat()
            val fiber = entries.sumOf { it.fiberGrams.toDouble() }.toFloat()
            val sugar = entries.sumOf { it.sugarGrams.toDouble() }.toFloat()
            val sodium = entries.sumOf { it.sodiumMg.toDouble() }.toFloat()
            val cholesterol = entries.sumOf { it.cholesterolMg.toDouble() }.toFloat()

            FoodUiState(
                selectedDateMillis = dateMillis,
                entries = entries,
                totalCalories = calories,
                totalProtein = protein,
                totalCarbs = carbs,
                totalFat = fat,
                totalFiber = fiber,
                totalSugar = sugar,
                totalSodium = sodium,
                totalCholesterol = cholesterol
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FoodUiState()
    )

    fun onPreviousDay() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = _selectedDateMillis.value
            add(Calendar.DAY_OF_YEAR, -1)
        }
        _selectedDateMillis.value = calendar.timeInMillis
    }

    fun onNextDay() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = _selectedDateMillis.value
            add(Calendar.DAY_OF_YEAR, 1)
        }
        _selectedDateMillis.value = calendar.timeInMillis
    }

    fun deleteEntry(entry: FoodEntry, onDeleted: () -> Unit) {
        viewModelScope.launch {
            foodRepository.delete(entry)
            onDeleted()
        }
    }

    fun restoreEntry(entry: FoodEntry) {
        viewModelScope.launch {
            foodRepository.insert(entry)
        }
    }

    class Factory(private val foodRepository: FoodRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodViewModel(foodRepository) as T
        }
    }
}
