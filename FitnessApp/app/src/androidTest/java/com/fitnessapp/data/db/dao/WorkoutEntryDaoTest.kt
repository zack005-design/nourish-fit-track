package com.fitnessapp.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fitnessapp.data.db.AppDatabase
import com.fitnessapp.data.db.entity.WorkoutEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WorkoutEntryDaoTest {
    private lateinit var workoutDao: WorkoutEntryDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        workoutDao = db.workoutDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeWorkoutAndReadInList() = runBlocking {
        val workout = WorkoutEntry(
            type = "Running",
            durationMinutes = 45,
            caloriesBurned = 500,
            distanceKm = 8.0f,
            notes = "Morning run",
            dateMillis = 1600000000000L
        )
        workoutDao.insert(workout)
        val allWorkouts = workoutDao.getAllEntries().first()
        
        assertEquals(allWorkouts.size, 1)
        assertEquals(allWorkouts[0].type, "Running")
        assertEquals(allWorkouts[0].durationMinutes, 45)
    }

    @Test
    @Throws(Exception::class)
    fun updateWorkout() = runBlocking {
        val workout = WorkoutEntry(
            type = "Cycling",
            durationMinutes = 60,
            caloriesBurned = 400,
            distanceKm = 20.0f,
            notes = null,
            dateMillis = 1600000000000L
        )
        workoutDao.insert(workout)
        
        val insertedWorkout = workoutDao.getAllEntries().first().first()
        val updatedWorkout = insertedWorkout.copy(durationMinutes = 90, caloriesBurned = 600)
        workoutDao.update(updatedWorkout)
        
        val result = workoutDao.getEntryById(insertedWorkout.id).first()
        assertNotNull(result)
        assertEquals(90, result?.durationMinutes)
        assertEquals(600, result?.caloriesBurned)
    }

    @Test
    @Throws(Exception::class)
    fun deleteWorkout() = runBlocking {
        val workout = WorkoutEntry(
            type = "Swimming",
            durationMinutes = 30,
            caloriesBurned = 300,
            distanceKm = 1.0f,
            notes = null,
            dateMillis = 1600000000000L
        )
        workoutDao.insert(workout)
        
        val insertedWorkout = workoutDao.getAllEntries().first().first()
        workoutDao.delete(insertedWorkout)
        
        val allWorkouts = workoutDao.getAllEntries().first()
        assertEquals(0, allWorkouts.size)
    }
}
