package com.fitnessapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_entries")
data class WorkoutEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val distanceKm: Float?,
    val notes: String?,
    val dateMillis: Long
)
