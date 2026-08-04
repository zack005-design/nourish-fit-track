package com.fitnessapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fitnessapp.data.db.dao.FoodEntryDao
import com.fitnessapp.data.db.dao.SleepEntryDao
import com.fitnessapp.data.db.dao.StepsEntryDao
import com.fitnessapp.data.db.dao.UserGoalsDao
import com.fitnessapp.data.db.dao.WaterEntryDao
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.StepsEntry
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.db.entity.WaterEntry

@Database(
    entities = [FoodEntry::class, SleepEntry::class, WaterEntry::class, StepsEntry::class, UserGoals::class],
    version = 5,
    exportSchema = false
)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun sleepEntryDao(): SleepEntryDao
    abstract fun waterEntryDao(): WaterEntryDao
    abstract fun stepsEntryDao(): StepsEntryDao
    abstract fun userGoalsDao(): UserGoalsDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        fun getInstance(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
