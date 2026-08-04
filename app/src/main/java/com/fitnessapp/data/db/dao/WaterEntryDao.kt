package com.fitnessapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnessapp.data.db.entity.WaterEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterEntryDao {

    @Query("SELECT * FROM water_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay ORDER BY id DESC")
    fun getEntriesForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<WaterEntry>>

    @Query("SELECT SUM(amountMl) FROM water_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    fun getTotalWaterForDateRange(startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Query("SELECT * FROM water_entries ORDER BY dateMillis DESC, id DESC")
    fun getAllWaterEntries(): Flow<List<WaterEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: WaterEntry): Long

    @Update
    fun update(entry: WaterEntry): Int

    @Delete
    fun delete(entry: WaterEntry): Int

    @Query("DELETE FROM water_entries")
    fun clearAll()
}
