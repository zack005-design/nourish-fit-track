package com.fitnessapp.data.repository

import com.fitnessapp.data.db.dao.SleepEntryDao
import com.fitnessapp.data.db.entity.SleepEntry
import kotlinx.coroutines.flow.Flow

class SleepRepository(private val dao: SleepEntryDao) {

    fun getEntriesForDate(dateMillis: Long): Flow<List<SleepEntry>> = dao.getEntriesForDate(dateMillis)

    fun getAllEntries(): Flow<List<SleepEntry>> = dao.getAllEntries()

    suspend fun insert(entry: SleepEntry) = dao.insert(entry)

    suspend fun update(entry: SleepEntry) = dao.update(entry)

    suspend fun delete(entry: SleepEntry) = dao.delete(entry)

    fun getEntryById(id: Long): Flow<SleepEntry?> = dao.getEntryById(id)
}
