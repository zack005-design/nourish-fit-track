package com.fitnessapp.data.repository

import com.fitnessapp.data.db.dao.WorkoutEntryDao
import com.fitnessapp.data.db.entity.WorkoutEntry
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val dao: WorkoutEntryDao) {

    fun getEntriesForDate(dateMillis: Long): Flow<List<WorkoutEntry>> = dao.getEntriesForDate(dateMillis)

    fun getAllEntries(): Flow<List<WorkoutEntry>> = dao.getAllEntries()

    fun getTotalDurationForDate(dateMillis: Long): Flow<Int?> = dao.getTotalDurationForDate(dateMillis)

    fun getTotalCaloriesForDate(dateMillis: Long): Flow<Int?> = dao.getTotalCaloriesForDate(dateMillis)

    suspend fun insert(entry: WorkoutEntry) = dao.insert(entry)

    suspend fun update(entry: WorkoutEntry) = dao.update(entry)

    suspend fun delete(entry: WorkoutEntry) = dao.delete(entry)

    fun getEntryById(id: Long) = dao.getEntryById(id)
}
