package com.fitnessapp.data.repository

import com.fitnessapp.data.db.dao.SleepEntryDao
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SleepRepository(
    private val dao: SleepEntryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getEntriesForDate(dateMillis: Long): Flow<List<SleepEntry>> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getEntriesForDateRange(startOfDay, endOfDay)
    }

    fun getAllEntries(): Flow<List<SleepEntry>> = dao.getAllEntries()

    fun getTotalSleepMinutesForDate(dateMillis: Long): Flow<Int> {
        return getEntriesForDate(dateMillis).map { list ->
            list.sumOf { (it.endMillis - it.startMillis).coerceAtLeast(0).toInt() / (1000 * 60) }
        }
    }

    suspend fun insert(entry: SleepEntry): Long = withContext(ioDispatcher) {
        dao.insert(entry)
    }

    suspend fun update(entry: SleepEntry): Int = withContext(ioDispatcher) {
        dao.update(entry)
    }

    suspend fun delete(entry: SleepEntry): Int = withContext(ioDispatcher) {
        dao.delete(entry)
    }

    fun getEntryById(id: Long): Flow<SleepEntry?> = dao.getEntryById(id)

    suspend fun clearAll() = withContext(ioDispatcher) { dao.clearAll() }
}
