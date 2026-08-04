package com.fitnessapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_goals")
data class UserGoals(
    @PrimaryKey
    val id: Int = 1,
    val dailyCalorieGoal: Int = 2200,
    val dailyProteinGoal: Float = 140f,
    val dailyCarbsGoal: Float = 250f,
    val dailyFatGoal: Float = 70f,
    val dailyFiberGoal: Float = 30f,
    val dailySugarGoal: Float = 50f,
    val dailySodiumGoal: Float = 2300f,
    val dailyCholesterolGoal: Float = 300f,
    val dailyWaterGoal: Int = 2500,
    val dailySleepGoalHours: Float = 8.0f,
    val dailyStepsGoal: Int = 10000
)
