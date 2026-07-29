package com.fitnessapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnessapp.data.db.entity.FoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {

    @Query("SELECT * FROM food_entries WHERE dateMillis >= :dateMillis ORDER BY id DESC")
    fun getEntriesForDate(dateMillis: Long): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries ORDER BY dateMillis DESC, id DESC")
    fun getAllEntries(): Flow<List<FoodEntry>>

    @Query("SELECT SUM(calories) FROM food_entries WHERE dateMillis >= :dateMillis")
    fun getTotalCaloriesForDate(dateMillis: Long): Flow<Int?>

    @Query("SELECT SUM(proteinGrams) FROM food_entries WHERE dateMillis >= :dateMillis")
    fun getTotalProteinForDate(dateMillis: Long): Flow<Float?>

    @Query("SELECT SUM(carbsGrams) FROM food_entries WHERE dateMillis >= :dateMillis")
    fun getTotalCarbsForDate(dateMillis: Long): Flow<Float?>

    @Query("SELECT SUM(fatGrams) FROM food_entries WHERE dateMillis >= :dateMillis")
    fun getTotalFatForDate(dateMillis: Long): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FoodEntry)

    @Update
    suspend fun update(entry: FoodEntry)

    @Delete
    suspend fun delete(entry: FoodEntry)

    @Query("SELECT * FROM food_entries WHERE id = :id")
    fun getEntryById(id: Long): Flow<FoodEntry?>
}
