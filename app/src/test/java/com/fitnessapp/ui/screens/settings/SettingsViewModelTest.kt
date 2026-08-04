package com.fitnessapp.ui.screens.settings

import com.fitnessapp.MainDispatcherRule
import com.fitnessapp.data.db.dao.FoodEntryDao
import com.fitnessapp.data.db.dao.SleepEntryDao
import com.fitnessapp.data.db.dao.StepsEntryDao
import com.fitnessapp.data.db.dao.UserGoalsDao
import com.fitnessapp.data.db.dao.WaterEntryDao
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.StepsEntry
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.db.entity.WaterEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.StepsRepository
import com.fitnessapp.data.repository.WaterRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeUserGoalsDao : UserGoalsDao {
        val goals = MutableStateFlow<UserGoals?>(UserGoals())
        override fun getUserGoals(): Flow<UserGoals?> = goals
        override fun insert(userGoals: UserGoals): Long {
            goals.value = userGoals
            return userGoals.id.toLong()
        }
    }

    private class FakeFoodDao : FoodEntryDao {
        val list = MutableStateFlow<List<FoodEntry>>(emptyList())
        override fun getEntriesForDateRange(s: Long, e: Long) = list
        override fun getAllEntries() = list
        override fun getTotalCaloriesForDateRange(s: Long, e: Long) = list.map { null }
        override fun getTotalProteinForDateRange(s: Long, e: Long) = list.map { null }
        override fun getTotalCarbsForDateRange(s: Long, e: Long) = list.map { null }
        override fun getTotalFatForDateRange(s: Long, e: Long) = list.map { null }
        override fun getTotalFiberForDateRange(s: Long, e: Long) = list.map { null }
        override fun getTotalSugarForDateRange(s: Long, e: Long) = list.map { null }
        override fun getTotalSodiumForDateRange(s: Long, e: Long) = list.map { null }
        override fun getTotalCholesterolForDateRange(s: Long, e: Long) = list.map { null }
        override fun insert(entry: FoodEntry) = 1L
        override fun update(entry: FoodEntry) = 1
        override fun delete(entry: FoodEntry) = 1
        override fun getEntryById(id: Long) = list.map { it.find { e -> e.id == id } }
        override fun clearAll() { list.value = emptyList() }
    }

    private class FakeWaterDao : WaterEntryDao {
        val list = MutableStateFlow<List<WaterEntry>>(emptyList())
        override fun getEntriesForDateRange(s: Long, e: Long) = list
        override fun getTotalWaterForDateRange(s: Long, e: Long) = list.map { null }
        override fun getAllWaterEntries() = list
        override fun insert(entry: WaterEntry) = 1L
        override fun update(entry: WaterEntry) = 1
        override fun delete(entry: WaterEntry) = 1
        override fun clearAll() { list.value = emptyList() }
    }

    private class FakeSleepDao : SleepEntryDao {
        val list = MutableStateFlow<List<SleepEntry>>(emptyList())
        override fun getEntriesForDateRange(s: Long, e: Long) = list
        override fun getAllEntries() = list
        override fun insert(entry: SleepEntry) = 1L
        override fun update(entry: SleepEntry) = 1
        override fun delete(entry: SleepEntry) = 1
        override fun getEntryById(id: Long) = list.map { it.find { e -> e.id == id } }
        override fun clearAll() { list.value = emptyList() }
    }

    private class FakeStepsDao : StepsEntryDao {
        val state = MutableStateFlow<StepsEntry?>(null)
        override fun getStepsForDateRange(s: Long, e: Long) = state
        override fun getAllStepsEntries() = state.map { if (it == null) emptyList() else listOf(it) }
        override fun insert(entry: StepsEntry) = 1L
        override fun update(entry: StepsEntry) = 1
        override fun clearAll() { state.value = null }
    }

    @Test
    fun testSaveUserGoals() = runTest {
        val fakeGoalsDao = FakeUserGoalsDao()
        val settingsRepo = SettingsRepository(fakeGoalsDao, mainDispatcherRule.testDispatcher)
        val foodRepo = FoodRepository(FakeFoodDao(), mainDispatcherRule.testDispatcher)
        val waterRepo = WaterRepository(FakeWaterDao(), mainDispatcherRule.testDispatcher)
        val sleepRepo = SleepRepository(FakeSleepDao(), mainDispatcherRule.testDispatcher)
        val stepsRepo = StepsRepository(FakeStepsDao(), mainDispatcherRule.testDispatcher)

        val viewModel = SettingsViewModel(settingsRepo, foodRepo, waterRepo, sleepRepo, stepsRepo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.userGoals.collect {} }

        var saved = false
        viewModel.saveGoals(
            calorieGoal = 2200,
            proteinGoal = 160f,
            carbsGoal = 220f,
            fatGoal = 65f,
            fiberGoal = 35f,
            waterGoal = 3000,
            sleepGoalHours = 8.5f,
            stepsGoal = 12000
        ) {
            saved = true
        }
        testScheduler.advanceUntilIdle()

        assertTrue(saved)
        val currentGoals = viewModel.userGoals.value
        assertEquals(2200, currentGoals.dailyCalorieGoal)
        assertEquals(160f, currentGoals.dailyProteinGoal, 0.01f)
        assertEquals(3000, currentGoals.dailyWaterGoal)
        assertEquals(8.5f, currentGoals.dailySleepGoalHours, 0.01f)
        assertEquals(12000, currentGoals.dailyStepsGoal)
    }
}
