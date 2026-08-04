package com.fitnessapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val fiberGrams: Float = 0f,
    val sugarGrams: Float = 0f,
    val sodiumMg: Float = 0f,
    val cholesterolMg: Float = 0f,
    val mealType: String,
    val dateMillis: Long
)
