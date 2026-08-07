package com.fitnessapp.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "water_entries", indices = [Index(value = ["dateMillis"])])
data class WaterEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateMillis: Long,
    val amountMl: Int,
    val timestampMillis: Long = 0L
)
