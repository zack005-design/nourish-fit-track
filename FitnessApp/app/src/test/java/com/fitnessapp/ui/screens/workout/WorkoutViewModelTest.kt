package com.fitnessapp.ui.screens.workout

import com.fitnessapp.data.db.entity.WorkoutEntry
import com.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FakeWorkoutRepository : WorkoutRepository {
    private val entries = MutableStateFlow<List<WorkoutEntry>>(emptyList())

    override fun getEntriesForDate(startOfDay: Long): Flow<List<WorkoutEntry>> = entries
    override fun getAllEntries(): Flow<List<WorkoutEntry>> = entries
    
    override fun getTotalDurationForDate(startOfDay: Long): Flow<Int?> = entries.map { list ->
        list.sumOf { it.durationMinutes }.takeIf { list.isNotEmpty() }
    }

    override fun getTotalCaloriesForDate(startOfDay: Long): Flow<Int?> = entries.map { list ->
        list.sumOf { it.caloriesBurned }.takeIf { list.isNotEmpty() }
    }

    override fun getEntryById(id: Long): Flow<WorkoutEntry?> = entries.map { list ->
        list.find { it.id == id }
    }

    override suspend fun insert(entry: WorkoutEntry) {
        val newEntry = entry.copy(id = (entries.value.size + 1).toLong())
        entries.value = entries.value + newEntry
    }

    override suspend fun delete(entry: WorkoutEntry) {
        entries.value = entries.value.filter { it.id != entry.id }
    }

    override suspend fun update(entry: WorkoutEntry) {
        entries.value = entries.value.map { if (it.id == entry.id) entry else it }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutViewModelTest {

    private lateinit var viewModel: WorkoutViewModel
    private lateinit var repository: FakeWorkoutRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkoutRepository()
        viewModel = WorkoutViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAddEntry() = runTest(testDispatcher) {
        viewModel.addEntry("Run", 30, 300, 5.0f, "Morning run")
        testDispatcher.scheduler.advanceUntilIdle()

        val allEntries = viewModel.allEntries.value
        assertEquals(1, allEntries.size)
        assertEquals("Run", allEntries.first().type)
        assertEquals(30, allEntries.first().durationMinutes)
    }

    @Test
    fun testUpdateEntry() = runTest(testDispatcher) {
        // First add an entry
        repository.insert(WorkoutEntry("Run", 30, 300, 5.0f, null, System.currentTimeMillis(), 1L))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then update it
        viewModel.updateEntry(1L, "Bike", 45, 400, 10.0f, "Evening ride")
        testDispatcher.scheduler.advanceUntilIdle()

        val allEntries = viewModel.allEntries.value
        assertEquals(1, allEntries.size)
        assertEquals("Bike", allEntries.first().type)
        assertEquals(45, allEntries.first().durationMinutes)
    }

    @Test
    fun testDeleteEntry() = runTest(testDispatcher) {
        val entry = WorkoutEntry("Run", 30, 300, 5.0f, null, System.currentTimeMillis(), 1L)
        repository.insert(entry)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.allEntries.value.size)

        viewModel.deleteEntry(entry)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.allEntries.value.size)
    }
}
