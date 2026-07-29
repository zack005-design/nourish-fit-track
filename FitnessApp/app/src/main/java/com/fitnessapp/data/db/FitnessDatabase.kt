package com.fitnessapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fitnessapp.data.db.dao.FoodEntryDao
import com.fitnessapp.data.db.dao.SleepEntryDao
import com.fitnessapp.data.db.dao.WorkoutEntryDao
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.WorkoutEntry

@Database(entities = [FoodEntry::class, SleepEntry::class, WorkoutEntry::class], version = 2, exportSchema = false)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun sleepEntryDao(): SleepEntryDao
    abstract fun workoutEntryDao(): WorkoutEntryDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `workout_entries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `type` TEXT NOT NULL,
                        `durationMinutes` INTEGER NOT NULL,
                        `caloriesBurned` INTEGER NOT NULL,
                        `distanceKm` REAL,
                        `notes` TEXT,
                        `dateMillis` INTEGER NOT NULL
                    )
                """
                )
            }
        }

        fun getInstance(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_db"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
