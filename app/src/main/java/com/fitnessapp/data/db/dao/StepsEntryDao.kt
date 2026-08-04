package com.fitnessapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnessapp.data.db.entity.StepsEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface StepsEntryDao {

    @Query("SELECT * FROM steps_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay LIMIT 1")
    fun getStepsForDateRange(startOfDay: Long, endOfDay: Long): Flow<StepsEntry?>

    @Query("SELECT * FROM steps_entries ORDER BY dateMillis DESC")
    fun getAllStepsEntries(): Flow<List<StepsEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: StepsEntry): Long

    @Update
    fun update(entry: StepsEntry): Int

    @Query("DELETE FROM steps_entries")
    fun clearAll()
}
