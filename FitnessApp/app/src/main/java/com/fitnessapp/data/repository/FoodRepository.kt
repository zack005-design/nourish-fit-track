package com.fitnessapp.data.repository

import com.fitnessapp.data.db.dao.FoodEntryDao
import com.fitnessapp.data.db.entity.FoodEntry
import kotlinx.coroutines.flow.Flow

class FoodRepository(private val dao: FoodEntryDao) {

    fun getEntriesForDate(dateMillis: Long): Flow<List<FoodEntry>> = dao.getEntriesForDate(dateMillis)

    fun getAllEntries(): Flow<List<FoodEntry>> = dao.getAllEntries()

    fun getTotalCaloriesForDate(dateMillis: Long): Flow<Int?> = dao.getTotalCaloriesForDate(dateMillis)

    fun getTotalProteinForDate(dateMillis: Long): Flow<Float?> = dao.getTotalProteinForDate(dateMillis)

    fun getTotalCarbsForDate(dateMillis: Long): Flow<Float?> = dao.getTotalCarbsForDate(dateMillis)

    fun getTotalFatForDate(dateMillis: Long): Flow<Float?> = dao.getTotalFatForDate(dateMillis)

    suspend fun insert(entry: FoodEntry) = dao.insert(entry)

    suspend fun update(entry: FoodEntry) = dao.update(entry)

    suspend fun delete(entry: FoodEntry) = dao.delete(entry)

    fun getEntryById(id: Long): Flow<FoodEntry?> = dao.getEntryById(id)
}
