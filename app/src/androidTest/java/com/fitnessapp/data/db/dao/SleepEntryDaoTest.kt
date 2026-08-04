package com.fitnessapp.data.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fitnessapp.data.db.FitnessDatabase
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SleepEntryDaoTest {

    private lateinit var database: FitnessDatabase
    private lateinit var dao: SleepEntryDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FitnessDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.sleepEntryDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndRetrieveSleepEntry() = runBlocking {
        val today = DateUtils.todayStartMillis()
        val entry = SleepEntry(
            dateMillis = today,
            startMillis = today - 8 * 3600 * 1000L,
            endMillis = today,
            quality = 90,
            notes = "Good sleep"
        )
        val id = dao.insert(entry)
        val fetched = dao.getEntryById(id).first()

        assertNotNull(fetched)
        assertEquals(90, fetched?.quality)
        assertEquals("Good sleep", fetched?.notes)
    }
}
