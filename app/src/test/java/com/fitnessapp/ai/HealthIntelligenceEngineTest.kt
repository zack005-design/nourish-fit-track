package com.fitnessapp.ai

import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.UserGoals
import com.fitnessapp.data.db.entity.WaterEntry
import com.fitnessapp.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthIntelligenceEngineTest {

    @Test
    fun `buildReport returns default report when no data logged`() {
        val goals = UserGoals()
        val report = HealthIntelligenceEngine.buildReport(
            foodEntries = emptyList(),
            waterEntries = emptyList(),
            sleepEntries = emptyList(),
            goals = goals
        )

        assertEquals(0, report.overallScore)
        assertEquals("Poor", report.scoreLabel)
        assertEquals(false, report.hasData)
        assertTrue(report.insightCards.any { it.title == "Start Your Health Journey" })
    }

    @Test
    fun `buildReport calculates correct score with logged food water and sleep`() {
        val today = DateUtils.todayStartMillis()
        val goals = UserGoals(dailyCalorieGoal = 2000, dailyWaterGoal = 2000, dailySleepGoalHours = 8f)

        val food = listOf(
            FoodEntry(
                name = "Chicken & Rice",
                calories = 2000,
                proteinGrams = 140f,
                carbsGrams = 200f,
                fatGrams = 50f,
                fiberGrams = 30f,
                mealType = "Lunch",
                dateMillis = today
            )
        )
        val water = listOf(
            WaterEntry(dateMillis = today, amountMl = 2000)
        )
        val sleep = listOf(
            SleepEntry(dateMillis = today, startMillis = today, endMillis = today + 8 * 3600 * 1000L, quality = 5)
        )

        val report = HealthIntelligenceEngine.buildReportWithSteps(
            foodEntries = food,
            waterEntries = water,
            sleepEntries = sleep,
            goals = goals,
            todaySteps = 10000
        )

        assertTrue(report.overallScore >= 80)
        assertEquals("Excellent", report.scoreLabel)
        assertEquals(true, report.hasData)
        assertNotNull(report.actionPlan)
    }
}
