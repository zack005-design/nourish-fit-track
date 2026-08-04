package com.fitnessapp.data.repository

import com.fitnessapp.data.db.dao.StepsEntryDao
import com.fitnessapp.data.db.entity.StepsEntry
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StepsRepository(
    private val dao: StepsEntryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getStepsForDate(dateMillis: Long): Flow<StepsEntry?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getStepsForDateRange(startOfDay, endOfDay)
    }

    fun getAllStepsEntries(): Flow<List<StepsEntry>> = dao.getAllStepsEntries()

    suspend fun insertOrUpdate(entry: StepsEntry): Long = withContext(ioDispatcher) {
        dao.insert(entry)
    }

    suspend fun clearAll() = withContext(ioDispatcher) { dao.clearAll() }
}
