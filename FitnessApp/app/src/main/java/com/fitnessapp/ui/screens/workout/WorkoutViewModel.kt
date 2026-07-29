package com.fitnessapp.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.data.db.entity.WorkoutEntry
import com.fitnessapp.data.repository.WorkoutRepository
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class WorkoutViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    private val todayStartMillis = DateUtils.todayStartMillis()

    val todayEntries: StateFlow<List<WorkoutEntry>> = workoutRepository
        .getEntriesForDate(todayStartMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEntries: StateFlow<List<WorkoutEntry>> = workoutRepository
        .getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalDuration: StateFlow<Int?> = workoutRepository
        .getTotalDurationForDate(todayStartMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalCalories: StateFlow<Int?> = workoutRepository
        .getTotalCaloriesForDate(todayStartMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addEntry(type: String, durationMinutes: Int, caloriesBurned: Int, distanceKm: Float?, notes: String?) {
        viewModelScope.launch {
            workoutRepository.insert(
                WorkoutEntry(
                    type = type,
                    durationMinutes = durationMinutes,
                    caloriesBurned = caloriesBurned,
                    distanceKm = distanceKm,
                    notes = notes,
                    dateMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateEntry(id: Long, type: String, durationMinutes: Int, caloriesBurned: Int, distanceKm: Float?, notes: String?) {
        viewModelScope.launch {
            val currentEntry = kotlinx.coroutines.flow.firstOrNull(workoutRepository.getEntryById(id))
            if (currentEntry != null) {
                workoutRepository.update(
                    currentEntry.copy(
                        type = type,
                        durationMinutes = durationMinutes,
                        caloriesBurned = caloriesBurned,
                        distanceKm = distanceKm,
                        notes = notes
                    )
                )
            }
        }
    }

    fun getEntry(id: Long) = workoutRepository.getEntryById(id)

    fun deleteEntry(entry: WorkoutEntry) {
        viewModelScope.launch {
            workoutRepository.delete(entry)
        }
    }

    class Factory(private val workoutRepository: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WorkoutViewModel(workoutRepository) as T
        }
    }
}
