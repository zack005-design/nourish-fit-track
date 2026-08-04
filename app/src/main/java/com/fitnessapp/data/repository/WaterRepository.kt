package com.fitnessapp.data.repository

import com.fitnessapp.data.db.dao.WaterEntryDao
import com.fitnessapp.data.db.entity.WaterEntry
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WaterRepository(
    private val dao: WaterEntryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getEntriesForDate(dateMillis: Long): Flow<List<WaterEntry>> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getEntriesForDateRange(startOfDay, endOfDay)
    }

    fun getTotalWaterForDate(dateMillis: Long): Flow<Int?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getTotalWaterForDateRange(startOfDay, endOfDay)
    }

    fun getAllWaterEntries(): Flow<List<WaterEntry>> = dao.getAllWaterEntries()

    suspend fun insert(entry: WaterEntry): Long = withContext(ioDispatcher) {
        dao.insert(entry)
    }

    suspend fun update(entry: WaterEntry): Int = withContext(ioDispatcher) {
        dao.update(entry)
    }

    suspend fun delete(entry: WaterEntry): Int = withContext(ioDispatcher) {
        dao.delete(entry)
    }

    suspend fun clearAll() = withContext(ioDispatcher) { dao.clearAll() }
}
