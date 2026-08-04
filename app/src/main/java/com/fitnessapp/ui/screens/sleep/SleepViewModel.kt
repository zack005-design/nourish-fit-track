package com.fitnessapp.ui.screens.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

data class WeeklySleepDay(
    val dayLabel: String,
    val hours: Float,
    val isToday: Boolean
)

data class SleepUiState(
    val selectedDateMillis: Long = DateUtils.todayStartMillis(),
    val entries: List<SleepEntry> = emptyList(),
    val totalSleepHours: Float = 0f,
    val sleepScore: Int = 0,
    val sleepStatusText: String = "No Sleep Logged",
    val sleepMessage: String = "No sleep recorded for this date.",
    val bedTimeText: String = "--",
    val wakeTimeText: String = "--",
    val awakeMinutes: Int = 0,
    val awakePercentage: Int = 0,
    val remMinutes: Int = 0,
    val remPercentage: Int = 0,
    val lightMinutes: Int = 0,
    val lightPercentage: Int = 0,
    val deepMinutes: Int = 0,
    val deepPercentage: Int = 0,
    val sleepTargetHours: Float = 8.0f,
    val weeklySleepDays: List<WeeklySleepDay> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class SleepViewModel(
    private val sleepRepository: SleepRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedDateMillis = MutableStateFlow(DateUtils.todayStartMillis())
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis.asStateFlow()

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEE", Locale.US)

    fun setSelectedDate(dateMillis: Long) {
        _selectedDateMillis.value = dateMillis
    }

    val uiState: StateFlow<SleepUiState> = combine(
        _selectedDateMillis.flatMapLatest { dateMillis ->
            sleepRepository.getEntriesForDate(dateMillis)
        },
        sleepRepository.getAllEntries(),
        settingsRepository.userGoals
    ) { entries, allEntries, goals ->
        val sleepEntry = entries.firstOrNull()
        val targetHours = goals.dailySleepGoalHours.coerceAtLeast(4.0f)

        // Compute 7-day weekly sleep history
        val todayStart = DateUtils.todayStartMillis()
        val weeklyDays = (6 downTo 0).map { daysAgo ->
            val dayStart = DateUtils.startOfDayMillis(todayStart - daysAgo * 24 * 60 * 60 * 1000L)
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L - 1
            val dayEntry = allEntries.find { it.dateMillis in dayStart..dayEnd }
            val hours = if (dayEntry != null) {
                ((dayEntry.endMillis - dayEntry.startMillis) / (1000 * 60 * 60f)).coerceAtLeast(0f)
            } else 0f

            WeeklySleepDay(
                dayLabel = dayFormat.format(Date(dayStart)).take(1),
                hours = hours,
                isToday = (daysAgo == 0)
            )
        }

        if (sleepEntry != null) {
            val totalMins = ((sleepEntry.endMillis - sleepEntry.startMillis) / (1000 * 60)).coerceAtLeast(0).toInt()
            val totalHours = totalMins / 60f

            // Weighted Sleep Score (0-100)
            val durationRatio = totalHours / targetHours
            val durationScore = if (durationRatio <= 1.0f) {
                (durationRatio * 40f).coerceIn(0f, 40f)
            } else {
                (40f - (durationRatio - 1.0f) * 15f).coerceIn(20f, 40f)
            }

            val sevenDaysAgo = DateUtils.startOfDayMillis(sleepEntry.dateMillis - 6 * 24 * 60 * 60 * 1000L)
            val recentEntries = allEntries.filter { it.id != sleepEntry.id && it.dateMillis in sevenDaysAgo until sleepEntry.dateMillis }

            val bedCal = Calendar.getInstance().apply { timeInMillis = sleepEntry.startMillis }
            val currentBedMinutes = bedCal.get(Calendar.HOUR_OF_DAY) * 60 + bedCal.get(Calendar.MINUTE)

            val consistencyScore = if (recentEntries.isNotEmpty()) {
                val avgBedMinutes = recentEntries.map { entry ->
                    val cal = Calendar.getInstance().apply { timeInMillis = entry.startMillis }
                    cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
                }.average()

                val minuteDiff = abs(currentBedMinutes - avgBedMinutes)
                if (minuteDiff <= 15.0) {
                    30f
                } else {
                    (30f - ((minuteDiff - 15.0) / 120.0 * 20.0).toFloat()).coerceIn(10f, 30f)
                }
            } else {
                25f
            }

            val starRating = sleepEntry.quality.coerceIn(1, 5)
            val qualityScore = ((starRating - 1) / 4.0f * 24f) + 6f

            val calculatedScore = (durationScore + consistencyScore + qualityScore).roundToInt().coerceIn(0, 100)

            val status = when {
                calculatedScore >= 80 -> "Optimal Recovery"
                calculatedScore >= 60 -> "Fair Rest"
                else -> "Low Recovery"
            }

            val message = if (sleepEntry.notes.isNotBlank()) {
                sleepEntry.notes
            } else when {
                calculatedScore >= 85 -> "Great sleep regularity and duration! Your recovery is at peak levels today."
                calculatedScore >= 70 -> "Good rest overall. Try going to bed 20 minutes earlier tonight for optimal recovery."
                else -> "Sleep duration was below target. Focus on light activity and hydration today."
            }

            val bedTime = timeFormat.format(Date(sleepEntry.startMillis))
            val wakeTime = timeFormat.format(Date(sleepEntry.endMillis))

            val deepPct = (12 + (calculatedScore / 100f * 10f)).roundToInt().coerceIn(8, 22)
            val remPct = (18 + (calculatedScore / 100f * 8f)).roundToInt().coerceIn(16, 26)
            val awakePct = (14 - (calculatedScore / 100f * 9f)).roundToInt().coerceIn(4, 16)
            val lightPct = 100 - deepPct - remPct - awakePct

            val deepMins = (totalMins * (deepPct / 100f)).roundToInt()
            val remMins = (totalMins * (remPct / 100f)).roundToInt()
            val awakeMins = (totalMins * (awakePct / 100f)).roundToInt()
            val lightMins = totalMins - deepMins - remMins - awakeMins

            SleepUiState(
                selectedDateMillis = _selectedDateMillis.value,
                entries = entries,
                totalSleepHours = totalHours,
                sleepScore = calculatedScore,
                sleepStatusText = status,
                sleepMessage = message,
                bedTimeText = bedTime,
                wakeTimeText = wakeTime,
                awakeMinutes = awakeMins,
                awakePercentage = awakePct,
                remMinutes = remMins,
                remPercentage = remPct,
                lightMinutes = lightMins,
                lightPercentage = lightPct,
                deepMinutes = deepMins,
                deepPercentage = deepPct,
                sleepTargetHours = goals.dailySleepGoalHours,
                weeklySleepDays = weeklyDays
            )
        } else {
            SleepUiState(
                selectedDateMillis = _selectedDateMillis.value,
                entries = emptyList(),
                totalSleepHours = 0f,
                sleepScore = 0,
                sleepStatusText = "No Sleep Logged",
                sleepMessage = "Log your bedtime and wake time to view your recovery analysis and sleep stages.",
                bedTimeText = "--",
                wakeTimeText = "--",
                awakeMinutes = 0,
                awakePercentage = 0,
                remMinutes = 0,
                remPercentage = 0,
                lightMinutes = 0,
                lightPercentage = 0,
                deepMinutes = 0,
                deepPercentage = 0,
                sleepTargetHours = goals.dailySleepGoalHours,
                weeklySleepDays = weeklyDays
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SleepUiState()
    )

    fun deleteEntry(entry: SleepEntry, onDeleted: () -> Unit) {
        viewModelScope.launch {
            sleepRepository.delete(entry)
            onDeleted()
        }
    }

    class Factory(
        private val sleepRepository: SleepRepository,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SleepViewModel(sleepRepository, settingsRepository) as T
        }
    }
}
