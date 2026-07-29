package com.fitnessapp.workout

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import com.fitnessapp.data.db.entity.WorkoutEntry
import com.fitnessapp.data.repository.WorkoutRepository
import com.fitnessapp.ui.screens.workout.WorkoutViewModel

class FakeWorkoutRepository : WorkoutRepository(object : com.fitnessapp.data.db.dao.WorkoutEntryDao {
    override fun getEntriesForDate(dateMillis: Long) = MutableStateFlow<List<WorkoutEntry>>(emptyList())
    override fun getAllEntries() = MutableStateFlow<List<WorkoutEntry>>(emptyList())
    override fun getTotalDurationForDate(dateMillis: Long) = MutableStateFlow<Int?>(0)
    override fun getTotalCaloriesForDate(dateMillis: Long) = MutableStateFlow<Int?>(0)
    override suspend fun insert(entry: WorkoutEntry) {}
    override suspend fun update(entry: WorkoutEntry) {}
    override suspend fun delete(entry: WorkoutEntry) {}
    override fun getEntryById(id: Long) = MutableStateFlow<WorkoutEntry?>(null)
}) {
    // For tests we can extend or override behaviour, but simple smoke test below will exercise ViewModel construction
}

class WorkoutViewModelTest {

    @Test
    fun viewModel_construction_and_defaults() = runBlocking {
        val fakeDaoRepo = FakeWorkoutRepository()
        val viewModel = WorkoutViewModel.Factory(fakeDaoRepo).create(WorkoutViewModel::class.java)

        // Validate initial state objects exist
        assertNotNull(viewModel.todayEntries)
        assertNotNull(viewModel.allEntries)
        assertNotNull(viewModel.totalDuration)
        assertNotNull(viewModel.totalCalories)
    }
}
