package com.fitnessapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fitnessapp.data.db.dao.FoodEntryDao
import com.fitnessapp.data.db.dao.ScannedBarcodeDao
import com.fitnessapp.data.db.dao.SleepEntryDao
import com.fitnessapp.data.db.dao.UserGoalsDao
import com.fitnessapp.data.db.dao.WaterEntryDao
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.db.entity.ScannedBarcode
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.db.entity.WaterEntry

@Database(
    entities = [FoodEntry::class, SleepEntry::class, WaterEntry::class, UserGoals::class, ScannedBarcode::class],
    version = 6,
    exportSchema = false
)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun sleepEntryDao(): SleepEntryDao
    abstract fun waterEntryDao(): WaterEntryDao
    abstract fun userGoalsDao(): UserGoalsDao
    abstract fun scannedBarcodeDao(): ScannedBarcodeDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS steps_entries")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_food_entries_dateMillis` ON `food_entries` (`dateMillis`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_sleep_entries_dateMillis` ON `sleep_entries` (`dateMillis`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_sleep_entries_startMillis` ON `sleep_entries` (`startMillis`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_water_entries_dateMillis` ON `water_entries` (`dateMillis`)")
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `scanned_barcodes` (
                        `barcode` TEXT NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `brand` TEXT NOT NULL, 
                        `servingSize` TEXT NOT NULL, 
                        `calories` INTEGER NOT NULL, 
                        `protein` REAL NOT NULL, 
                        `carbs` REAL NOT NULL, 
                        `fat` REAL NOT NULL, 
                        `fiber` REAL NOT NULL, 
                        `cachedAtMillis` INTEGER NOT NULL, 
                        PRIMARY KEY(`barcode`)
                    )""".trimIndent()
                )
            }
        }

        fun getInstance(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_db"
                )
                .addMigrations(MIGRATION_5_6)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
