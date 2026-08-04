package com.fitnessapp.ui.screens.food

import com.fitnessapp.MainDispatcherRule
import com.fitnessapp.data.db.dao.FoodEntryDao
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.repository.FoodRepository
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
class FoodViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeFoodEntryDao : FoodEntryDao {
        val entries = MutableStateFlow<List<FoodEntry>>(emptyList())
        private var idCounter = 1L

        override fun getEntriesForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<FoodEntry>> {
            return entries.map { list ->
                list.filter { it.dateMillis in startOfDay..endOfDay }
            }
        }

        override fun getAllEntries(): Flow<List<FoodEntry>> = entries

        override fun getTotalCaloriesForDateRange(startOfDay: Long, endOfDay: Long): Flow<Int?> {
            return getEntriesForDateRange(startOfDay, endOfDay).map { list ->
                if (list.isEmpty()) null else list.sumOf { it.calories }
            }
        }

        override fun getTotalProteinForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?> {
            return getEntriesForDateRange(startOfDay, endOfDay).map { list ->
                if (list.isEmpty()) null else list.sumOf { it.proteinGrams.toDouble() }.toFloat()
            }
        }

        override fun getTotalCarbsForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?> {
            return getEntriesForDateRange(startOfDay, endOfDay).map { list ->
                if (list.isEmpty()) null else list.sumOf { it.carbsGrams.toDouble() }.toFloat()
            }
        }

        override fun getTotalFatForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?> {
            return getEntriesForDateRange(startOfDay, endOfDay).map { list ->
                if (list.isEmpty()) null else list.sumOf { it.fatGrams.toDouble() }.toFloat()
            }
        }

        override fun getTotalFiberForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?> {
            return getEntriesForDateRange(startOfDay, endOfDay).map { list ->
                if (list.isEmpty()) null else list.sumOf { it.fiberGrams.toDouble() }.toFloat()
            }
        }

        override fun getTotalSugarForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?> {
            return getEntriesForDateRange(startOfDay, endOfDay).map { list ->
                if (list.isEmpty()) null else list.sumOf { it.sugarGrams.toDouble() }.toFloat()
            }
        }

        override fun getTotalSodiumForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?> {
            return getEntriesForDateRange(startOfDay, endOfDay).map { list ->
                if (list.isEmpty()) null else list.sumOf { it.sodiumMg.toDouble() }.toFloat()
            }
        }

        override fun getTotalCholesterolForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?> {
            return getEntriesForDateRange(startOfDay, endOfDay).map { list ->
                if (list.isEmpty()) null else list.sumOf { it.cholesterolMg.toDouble() }.toFloat()
            }
        }

        override fun insert(entry: FoodEntry): Long {
            val assignedId = if (entry.id == 0L) idCounter++ else entry.id
            val newEntry = entry.copy(id = assignedId)
            val current = entries.value.toMutableList()
            current.removeAll { it.id == assignedId }
            current.add(newEntry)
            entries.value = current
            return assignedId
        }

        override fun update(entry: FoodEntry): Int {
            insert(entry)
            return 1
        }

        override fun delete(entry: FoodEntry): Int {
            val current = entries.value.toMutableList()
            val removed = current.removeAll { it.id == entry.id }
            entries.value = current
            return if (removed) 1 else 0
        }

        override fun getEntryById(id: Long): Flow<FoodEntry?> {
            return entries.map { list -> list.find { it.id == id } }
        }

        override fun clearAll() { entries.value = emptyList() }
    }

    @Test
    fun testFoodViewModelTotalsCalculation() = runTest {
        val fakeDao = FakeFoodEntryDao()
        val repository = FoodRepository(fakeDao, mainDispatcherRule.testDispatcher)
        val now = System.currentTimeMillis()

        fakeDao.insert(
            FoodEntry(
                id = 1,
                name = "Oatmeal",
                mealType = "Breakfast",
                calories = 300,
                proteinGrams = 10f,
                carbsGrams = 50f,
                fatGrams = 5f,
                fiberGrams = 8f,
                sugarGrams = 2f,
                dateMillis = now
            )
        )
        fakeDao.insert(
            FoodEntry(
                id = 2,
                name = "Chicken Salad",
                mealType = "Lunch",
                calories = 450,
                proteinGrams = 40f,
                carbsGrams = 15f,
                fatGrams = 20f,
                fiberGrams = 4f,
                sugarGrams = 3f,
                dateMillis = now
            )
        )

        val viewModel = FoodViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        val state = viewModel.uiState.value

        assertEquals(2, state.entries.size)
        assertEquals(750, state.totalCalories)
        assertEquals(50f, state.totalProtein, 0.01f)
        assertEquals(65f, state.totalCarbs, 0.01f)
        assertEquals(25f, state.totalFat, 0.01f)
        assertEquals(12f, state.totalFiber, 0.01f)
    }

    @Test
    fun testDeleteAndRestoreEntry() = runTest {
        val fakeDao = FakeFoodEntryDao()
        val repository = FoodRepository(fakeDao, mainDispatcherRule.testDispatcher)
        val now = System.currentTimeMillis()
        val entry = FoodEntry(
            id = 1,
            name = "Apple",
            mealType = "Snack",
            calories = 95,
            proteinGrams = 0.5f,
            carbsGrams = 25f,
            fatGrams = 0.3f,
            dateMillis = now
        )

        fakeDao.insert(entry)
        val viewModel = FoodViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        var onDeletedCalled = false
        viewModel.deleteEntry(entry) {
            onDeletedCalled = true
        }
        testScheduler.advanceUntilIdle()

        assertTrue(onDeletedCalled)
        assertEquals(0, viewModel.uiState.value.entries.size)

        viewModel.restoreEntry(entry)
        testScheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.entries.size)
    }
}
