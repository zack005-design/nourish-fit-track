package com.fitnessapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_barcodes")
data class ScannedBarcode(
    @PrimaryKey
    val barcode: String,
    val name: String,
    val brand: String = "",
    val servingSize: String = "100 g",
    val calories: Int = 0,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val fiber: Float = 0f,
    val cachedAtMillis: Long = System.currentTimeMillis()
)
