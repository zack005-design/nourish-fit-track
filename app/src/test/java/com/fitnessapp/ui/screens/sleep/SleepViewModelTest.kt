package com.fitnessapp.ui.screens.sleep

import com.fitnessapp.MainDispatcherRule
import com.fitnessapp.data.db.dao.SleepEntryDao
import com.fitnessapp.data.db.dao.UserGoalsDao
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SleepViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeSleepEntryDao : SleepEntryDao {
        val entries = MutableStateFlow<List<SleepEntry>>(emptyList())

        override fun getEntriesForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<SleepEntry>> {
            return entries.map { list ->
                list.filter { it.dateMillis in startOfDay..endOfDay }
            }
        }

        override fun getAllEntries(): Flow<List<SleepEntry>> = entries

        override fun insert(entry: SleepEntry): Long {
            val current = entries.value.toMutableList()
            current.removeAll { it.id == entry.id }
            current.add(entry)
            entries.value = current
            return entry.id
        }

        override fun update(entry: SleepEntry): Int {
            insert(entry)
            return 1
        }

        override fun delete(entry: SleepEntry): Int {
            val current = entries.value.toMutableList()
            val removed = current.removeAll { it.id == entry.id }
            entries.value = current
            return if (removed) 1 else 0
        }

        override fun getEntryById(id: Long): Flow<SleepEntry?> {
            return entries.map { list -> list.find { it.id == id } }
        }

        override fun clearAll() {
            entries.value = emptyList()
        }
    }

    private class FakeUserGoalsDao : UserGoalsDao {
        val goals = MutableStateFlow<UserGoals?>(UserGoals())

        override fun getUserGoals(): Flow<UserGoals?> = goals

        override fun insert(userGoals: UserGoals): Long {
            goals.value = userGoals
            return userGoals.id.toLong()
        }
    }

    @Test
    fun testSleepViewModelScoreCalculation() = runTest {
        val fakeSleepDao = FakeSleepEntryDao()
        val fakeGoalsDao = FakeUserGoalsDao()

        val sleepRepo = SleepRepository(fakeSleepDao, mainDispatcherRule.testDispatcher)
        val settingsRepo = SettingsRepository(fakeGoalsDao, mainDispatcherRule.testDispatcher)

        val today = DateUtils.todayStartMillis()
        val eightHoursMs = 8 * 3600 * 1000L

        fakeSleepDao.insert(
            SleepEntry(
                id = 1,
                dateMillis = today,
                startMillis = today + 22 * 3600 * 1000L,
                endMillis = today + 22 * 3600 * 1000L + eightHoursMs,
                quality = 5
            )
        )

        val viewModel = SleepViewModel(sleepRepo, settingsRepo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value

        assertEquals(8.0f, state.totalSleepHours, 0.1f)
        assertEquals(95, state.sleepScore)
        assertEquals(1, state.entries.size)
        assertEquals(22, state.deepPercentage)
        assertEquals(26, state.remPercentage)
        assertEquals(5, state.awakePercentage)
    }

    @Test
    fun testDeleteSleepEntry() = runTest {
        val fakeSleepDao = FakeSleepEntryDao()
        val fakeGoalsDao = FakeUserGoalsDao()

        val sleepRepo = SleepRepository(fakeSleepDao, mainDispatcherRule.testDispatcher)
        val settingsRepo = SettingsRepository(fakeGoalsDao, mainDispatcherRule.testDispatcher)

        val today = DateUtils.todayStartMillis()
        val entry = SleepEntry(
            id = 1,
            dateMillis = today,
            startMillis = today + 22 * 3600 * 1000L,
            endMillis = today + 23 * 3600 * 1000L,
            quality = 4
        )
        fakeSleepDao.insert(entry)

        val viewModel = SleepViewModel(sleepRepo, settingsRepo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        var deleted = false
        viewModel.deleteEntry(entry) {
            deleted = true
        }

        assertTrue(deleted)
        assertEquals(0, viewModel.uiState.value.entries.size)
    }
}
