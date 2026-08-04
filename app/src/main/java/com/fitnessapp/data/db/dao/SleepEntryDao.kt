package com.fitnessapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnessapp.data.db.entity.SleepEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepEntryDao {

    @Query("SELECT * FROM sleep_entries WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay ORDER BY id DESC")
    fun getEntriesForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<SleepEntry>>

    @Query("SELECT * FROM sleep_entries ORDER BY dateMillis DESC, id DESC")
    fun getAllEntries(): Flow<List<SleepEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: SleepEntry): Long

    @Update
    fun update(entry: SleepEntry): Int

    @Delete
    fun delete(entry: SleepEntry): Int

    @Query("SELECT * FROM sleep_entries WHERE id = :id")
    fun getEntryById(id: Long): Flow<SleepEntry?>

    @Query("DELETE FROM sleep_entries")
    fun clearAll()
}
