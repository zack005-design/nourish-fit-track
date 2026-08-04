package com.fitnessapp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun testStartOfDayMillis() {
        val now = System.currentTimeMillis()
        val startOfDay = DateUtils.startOfDayMillis(now)

        val cal = Calendar.getInstance().apply { timeInMillis = startOfDay }
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
        assertEquals(0, cal.get(Calendar.SECOND))
        assertEquals(0, cal.get(Calendar.MILLISECOND))
    }

    @Test
    fun testEndOfDayMillis() {
        val now = System.currentTimeMillis()
        val endOfDay = DateUtils.endOfDayMillis(now)

        val cal = Calendar.getInstance().apply { timeInMillis = endOfDay }
        assertEquals(23, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, cal.get(Calendar.MINUTE))
        assertEquals(59, cal.get(Calendar.SECOND))
        assertEquals(999, cal.get(Calendar.MILLISECOND))
    }

    @Test
    fun testStartAndEndOfDayRange() {
        val now = System.currentTimeMillis()
        val startOfDay = DateUtils.startOfDayMillis(now)
        val endOfDay = DateUtils.endOfDayMillis(now)

        assertTrue(endOfDay > startOfDay)
        assertEquals(86399999L, endOfDay - startOfDay)
    }
}
