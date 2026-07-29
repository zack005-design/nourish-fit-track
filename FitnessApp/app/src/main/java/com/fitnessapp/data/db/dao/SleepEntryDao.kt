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

    @Query("SELECT * FROM sleep_entries WHERE dateMillis >= :dateMillis ORDER BY id DESC")
    fun getEntriesForDate(dateMillis: Long): Flow<List<SleepEntry>>

    @Query("SELECT * FROM sleep_entries ORDER BY dateMillis DESC, id DESC")
    fun getAllEntries(): Flow<List<SleepEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SleepEntry)

    @Update
    suspend fun update(entry: SleepEntry)

    @Delete
    suspend fun delete(entry: SleepEntry)

    @Query("SELECT * FROM sleep_entries WHERE id = :id")
    fun getEntryById(id: Long): Flow<SleepEntry?>
}
