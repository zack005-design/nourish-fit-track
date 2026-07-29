package com.fitnessapp.ui.screens.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SleepViewModel(private val sleepRepository: SleepRepository) : ViewModel() {

    private val todayStartMillis = DateUtils.todayStartMillis()

    val todayEntries: StateFlow<List<SleepEntry>> = sleepRepository
        .getEntriesForDate(todayStartMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEntries: StateFlow<List<SleepEntry>> = sleepRepository
        .getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEntry(startMillis: Long, endMillis: Long, quality: Int, notes: String) {
        viewModelScope.launch {
            sleepRepository.insert(
                SleepEntry(
                    startMillis = startMillis,
                    endMillis = endMillis,
                    quality = quality,
                    notes = notes,
                    dateMillis = endMillis
                )
            )
        }
    }

    fun updateEntry(id: Long, startMillis: Long, endMillis: Long, quality: Int, notes: String) {
        viewModelScope.launch {
            val currentEntry = kotlinx.coroutines.flow.firstOrNull(sleepRepository.getEntryById(id))
            if (currentEntry != null) {
                sleepRepository.update(
                    currentEntry.copy(
                        startMillis = startMillis,
                        endMillis = endMillis,
                        quality = quality,
                        notes = notes,
                        dateMillis = endMillis
                    )
                )
            }
        }
    }

    fun getEntry(id: Long) = sleepRepository.getEntryById(id)

    fun deleteEntry(entry: SleepEntry) {
        viewModelScope.launch {
            sleepRepository.delete(entry)
        }
    }

    class Factory(private val sleepRepository: SleepRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SleepViewModel(sleepRepository) as T
        }
    }
}
