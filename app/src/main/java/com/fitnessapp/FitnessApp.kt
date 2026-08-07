package com.fitnessapp

import android.app.Application
import com.fitnessapp.data.db.FitnessDatabase
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.WaterRepository

class FitnessApp : Application() {

    lateinit var foodRepository: FoodRepository
        private set
    lateinit var sleepRepository: SleepRepository
        private set
    lateinit var waterRepository: WaterRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val database = FitnessDatabase.getInstance(this)
        foodRepository = FoodRepository(database.foodEntryDao())
        sleepRepository = SleepRepository(database.sleepEntryDao())
        waterRepository = WaterRepository(database.waterEntryDao())
        settingsRepository = SettingsRepository(database.userGoalsDao(), this)
    }
}
