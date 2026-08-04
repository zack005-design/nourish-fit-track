package com.fitnessapp.data.repository

import com.fitnessapp.data.db.dao.FoodEntryDao
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FoodRepository(
    private val dao: FoodEntryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getEntriesForDate(dateMillis: Long): Flow<List<FoodEntry>> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getEntriesForDateRange(startOfDay, endOfDay)
    }

    fun getAllEntries(): Flow<List<FoodEntry>> = dao.getAllEntries()

    fun getTotalCaloriesForDate(dateMillis: Long): Flow<Int?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getTotalCaloriesForDateRange(startOfDay, endOfDay)
    }

    fun getTotalProteinForDate(dateMillis: Long): Flow<Float?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getTotalProteinForDateRange(startOfDay, endOfDay)
    }

    fun getTotalCarbsForDate(dateMillis: Long): Flow<Float?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getTotalCarbsForDateRange(startOfDay, endOfDay)
    }

    fun getTotalFatForDate(dateMillis: Long): Flow<Float?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getTotalFatForDateRange(startOfDay, endOfDay)
    }

    fun getTotalFiberForDate(dateMillis: Long): Flow<Float?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getTotalFiberForDateRange(startOfDay, endOfDay)
    }

    fun getTotalSugarForDate(dateMillis: Long): Flow<Float?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getTotalSugarForDateRange(startOfDay, endOfDay)
    }

    fun getTotalSodiumForDate(dateMillis: Long): Flow<Float?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getTotalSodiumForDateRange(startOfDay, endOfDay)
    }

    fun getTotalCholesterolForDate(dateMillis: Long): Flow<Float?> {
        val startOfDay = DateUtils.startOfDayMillis(dateMillis)
        val endOfDay = DateUtils.endOfDayMillis(dateMillis)
        return dao.getTotalCholesterolForDateRange(startOfDay, endOfDay)
    }

    suspend fun insert(entry: FoodEntry): Long = withContext(ioDispatcher) {
        dao.insert(entry)
    }

    suspend fun update(entry: FoodEntry): Int = withContext(ioDispatcher) {
        dao.update(entry)
    }

    suspend fun delete(entry: FoodEntry): Int = withContext(ioDispatcher) {
        dao.delete(entry)
    }

    fun getEntryById(id: Long): Flow<FoodEntry?> = dao.getEntryById(id)

    suspend fun clearAll() = withContext(ioDispatcher) { dao.clearAll() }
}
