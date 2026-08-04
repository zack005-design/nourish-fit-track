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

    @Query("SELECT * FROM food_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay ORDER BY id DESC")
    fun getEntriesForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries ORDER BY dateMillis DESC, id DESC")
    fun getAllEntries(): Flow<List<FoodEntry>>

    @Query("SELECT SUM(calories) FROM food_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    fun getTotalCaloriesForDateRange(startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Query("SELECT SUM(proteinGrams) FROM food_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    fun getTotalProteinForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Query("SELECT SUM(carbsGrams) FROM food_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    fun getTotalCarbsForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Query("SELECT SUM(fatGrams) FROM food_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    fun getTotalFatForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Query("SELECT SUM(fiberGrams) FROM food_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    fun getTotalFiberForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Query("SELECT SUM(sugarGrams) FROM food_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    fun getTotalSugarForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Query("SELECT SUM(sodiumMg) FROM food_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    fun getTotalSodiumForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Query("SELECT SUM(cholesterolMg) FROM food_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    fun getTotalCholesterolForDateRange(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: FoodEntry): Long

    @Update
    fun update(entry: FoodEntry): Int

    @Delete
    fun delete(entry: FoodEntry): Int

    @Query("SELECT * FROM food_entries WHERE id = :id")
    fun getEntryById(id: Long): Flow<FoodEntry?>

    @Query("DELETE FROM food_entries")
    fun clearAll()
}
