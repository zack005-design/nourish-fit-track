package com.fitnessapp.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fitnessapp.data.db.FitnessDatabase
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoodEntryDaoTest {

    private lateinit var database: FitnessDatabase
    private lateinit var dao: FoodEntryDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FitnessDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.foodEntryDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetFoodEntry() = runBlocking {
        val today = DateUtils.todayStartMillis()
        val entry = FoodEntry(
            name = "Chicken Breast",
            mealType = "Dinner",
            calories = 350,
            proteinGrams = 45f,
            carbsGrams = 0f,
            fatGrams = 5f,
            dateMillis = today
        )
        val id = dao.insert(entry)
        val fetched = dao.getEntryById(id).first()

        assertNotNull(fetched)
        assertEquals("Chicken Breast", fetched?.name)
        assertEquals(350, fetched?.calories)
    }

    @Test
    fun getTotalsForDateRange() = runBlocking {
        val today = DateUtils.todayStartMillis()
        val endOfDay = DateUtils.endOfDayMillis(today)

        val entry1 = FoodEntry(
            name = "Egg White",
            mealType = "Breakfast",
            calories = 100,
            proteinGrams = 20f,
            carbsGrams = 2f,
            fatGrams = 0f,
            dateMillis = today + 1000
        )
        val entry2 = FoodEntry(
            name = "Rice",
            mealType = "Lunch",
            calories = 200,
            proteinGrams = 4f,
            carbsGrams = 45f,
            fatGrams = 1f,
            dateMillis = today + 2000
        )
        dao.insert(entry1)
        dao.insert(entry2)

        val totalCalories = dao.getTotalCaloriesForDateRange(today, endOfDay).first()
        val totalProtein = dao.getTotalProteinForDateRange(today, endOfDay).first()

        assertEquals(300, totalCalories)
        assertEquals(24f, totalProtein ?: 0f, 0.01f)
    }
}
