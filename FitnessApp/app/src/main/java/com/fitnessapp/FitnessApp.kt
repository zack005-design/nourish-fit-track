package com.fitnessapp

import android.app.Application
import com.fitnessapp.data.db.FitnessDatabase
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.WorkoutRepository

class FitnessApp : Application() {

    lateinit var foodRepository: FoodRepository
        private set
    lateinit var sleepRepository: SleepRepository
        private set
    lateinit var workoutRepository: WorkoutRepository
        private set
    lateinit var settingsRepository: com.fitnessapp.data.repository.SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val database = FitnessDatabase.getInstance(this)
        foodRepository = FoodRepository(database.foodEntryDao())
        sleepRepository = SleepRepository(database.sleepEntryDao())
        workoutRepository = WorkoutRepository(database.workoutEntryDao())

        val sharedPreferences = getSharedPreferences("fitness_prefs", android.content.Context.MODE_PRIVATE)
        settingsRepository = com.fitnessapp.data.repository.SettingsRepository(sharedPreferences)
    }
}
