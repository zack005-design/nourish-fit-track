package com.fitnessapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_entries")
data class SleepEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startMillis: Long,
    val endMillis: Long,
    val quality: Int,
    val notes: String = "",
    val dateMillis: Long
)
