package com.fitnessapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fitnessapp.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val dailyCalorieTarget: Int = 2000,
    val sleepTargetHours: Float = 8.0f
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.update {
            it.copy(
                dailyCalorieTarget = settingsRepository.getDailyCalorieTarget(),
                sleepTargetHours = settingsRepository.getSleepTargetHours()
            )
        }
    }

    fun saveDailyCalorieTarget(target: Int) {
        settingsRepository.setDailyCalorieTarget(target)
        loadSettings()
    }

    fun saveSleepTargetHours(target: Float) {
        settingsRepository.setSleepTargetHours(target)
        loadSettings()
    }

    class Factory(
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository) as T
        }
    }
}
