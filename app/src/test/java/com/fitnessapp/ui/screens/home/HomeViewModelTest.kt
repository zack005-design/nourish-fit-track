package com.fitnessapp.ui.screens.home

import com.fitnessapp.data.db.dao.FoodEntryDao
import com.fitnessapp.data.db.dao.SleepEntryDao
import com.fitnessapp.data.db.dao.UserGoalsDao
import com.fitnessapp.data.db.dao.WaterEntryDao
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.db.entity.WaterEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.util.DateUtils
import com.fitnessapp.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeFoodDao : FoodEntryDao {
        val list = MutableStateFlow<List<FoodEntry>>(emptyList())
        override fun getEntriesForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> entries.filter { it.dateMillis in startOfDay..endOfDay } }

        override fun getAllEntries() = list
        override fun getTotalCaloriesForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> if (entries.isEmpty()) null else entries.sumOf { it.calories } }

        override fun getTotalProteinForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> if (entries.isEmpty()) null else entries.sumOf { it.proteinGrams.toDouble() }.toFloat() }

        override fun getTotalCarbsForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> if (entries.isEmpty()) null else entries.sumOf { it.carbsGrams.toDouble() }.toFloat() }

        override fun getTotalFatForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> if (entries.isEmpty()) null else entries.sumOf { it.fatGrams.toDouble() }.toFloat() }

        override fun getTotalFiberForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> if (entries.isEmpty()) null else entries.sumOf { it.fiberGrams.toDouble() }.toFloat() }

        override fun getTotalSugarForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> if (entries.isEmpty()) null else entries.sumOf { it.sugarGrams.toDouble() }.toFloat() }

        override fun getTotalSodiumForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> if (entries.isEmpty()) null else entries.sumOf { it.sodiumMg.toDouble() }.toFloat() }

        override fun getTotalCholesterolForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> if (entries.isEmpty()) null else entries.sumOf { it.cholesterolMg.toDouble() }.toFloat() }

        override fun insert(entry: FoodEntry): Long {
            val current = list.value.toMutableList()
            current.add(entry)
            list.value = current
            return 1L
        }

        override fun update(entry: FoodEntry) = 1
        override fun delete(entry: FoodEntry) = 1
        override fun getEntryById(id: Long) = list.map { it.find { item -> item.id == id } }
        override fun clearAll() { list.value = emptyList() }
    }

    private class FakeSleepDao : SleepEntryDao {
        val list = MutableStateFlow<List<SleepEntry>>(emptyList())
        override fun getEntriesForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> entries.filter { it.dateMillis in startOfDay..endOfDay } }

        override fun getAllEntries() = list
        override fun insert(entry: SleepEntry): Long {
            val current = list.value.toMutableList()
            current.add(entry)
            list.value = current
            return 1L
        }

        override fun update(entry: SleepEntry) = 1
        override fun delete(entry: SleepEntry) = 1
        override fun getEntryById(id: Long) = list.map { it.find { item -> item.id == id } }
        override fun clearAll() { list.value = emptyList() }
    }

    private class FakeWaterDao : WaterEntryDao {
        val list = MutableStateFlow<List<WaterEntry>>(emptyList())
        override fun getEntriesForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> entries.filter { it.dateMillis in startOfDay..endOfDay } }

        override fun getTotalWaterForDateRange(startOfDay: Long, endOfDay: Long) =
            list.map { entries -> if (entries.isEmpty()) null else entries.sumOf { it.amountMl } }

        override fun getAllWaterEntries() = list
        override fun insert(entry: WaterEntry): Long {
            val current = list.value.toMutableList()
            current.add(entry)
            list.value = current
            return 1L
        }

        override fun update(entry: WaterEntry) = 1
        override fun delete(entry: WaterEntry) = 1
        override fun deleteForDateRange(startOfDay: Long, endOfDay: Long) {
            list.value = list.value.filterNot { it.dateMillis in startOfDay..endOfDay }
        }

        override fun clearAll() { list.value = emptyList() }
    }

    private class FakeGoalsDao : UserGoalsDao {
        val goals = MutableStateFlow<UserGoals?>(UserGoals())
        override fun getUserGoals() = goals
        override fun insert(userGoals: UserGoals): Long {
            goals.value = userGoals
            return 1L
        }
    }

    @Test
    fun testHomeViewModelAggregationAndQuickActions() = runTest {
        val foodDao = FakeFoodDao()
        val sleepDao = FakeSleepDao()
        val waterDao = FakeWaterDao()
        val goalsDao = FakeGoalsDao()

        val foodRepo = FoodRepository(foodDao, mainDispatcherRule.testDispatcher)
        val sleepRepo = SleepRepository(sleepDao, mainDispatcherRule.testDispatcher)
        val waterRepo = WaterRepository(waterDao, mainDispatcherRule.testDispatcher)
        val settingsRepo = SettingsRepository(goalsDao, ioDispatcher = mainDispatcherRule.testDispatcher)

        val today = DateUtils.todayStartMillis()

        foodDao.list.value = listOf(
            FoodEntry(id = 1, name = "Salad", calories = 300, proteinGrams = 15f, carbsGrams = 20f, fatGrams = 10f, mealType = "Lunch", dateMillis = today)
        )

        val viewModel = HomeViewModel(foodRepo, sleepRepo, waterRepo, settingsRepo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        var state = viewModel.uiState.value
        assertEquals(300, state.totalCalories)

        viewModel.addWater(250)
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(0.25f, state.totalWaterL, 0.01f)
    }
}
