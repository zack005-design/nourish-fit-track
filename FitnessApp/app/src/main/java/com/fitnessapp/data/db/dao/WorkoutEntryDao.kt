package com.fitnessapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnessapp.data.db.entity.WorkoutEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutEntryDao {

    @Query("SELECT * FROM workout_entries WHERE dateMillis >= :dateMillis ORDER BY id DESC")
    fun getEntriesForDate(dateMillis: Long): Flow<List<WorkoutEntry>>

    @Query("SELECT * FROM workout_entries ORDER BY dateMillis DESC, id DESC")
    fun getAllEntries(): Flow<List<WorkoutEntry>>

    @Query("SELECT SUM(durationMinutes) FROM workout_entries WHERE dateMillis >= :dateMillis")
    fun getTotalDurationForDate(dateMillis: Long): Flow<Int?>

    @Query("SELECT SUM(caloriesBurned) FROM workout_entries WHERE dateMillis >= :dateMillis")
    fun getTotalCaloriesForDate(dateMillis: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WorkoutEntry)

    @Update
    suspend fun update(entry: WorkoutEntry)

    @Delete
    suspend fun delete(entry: WorkoutEntry)

    @Query("SELECT * FROM workout_entries WHERE id = :id")
    fun getEntryById(id: Long): Flow<WorkoutEntry?>
}
