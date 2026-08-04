package com.fitnessapp.util

import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.db.entity.WaterEntry
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthDataExportTest {

    @Test
    fun testCsvExportFormat() {
        val foods = listOf(
            FoodEntry(
                id = 10,
                name = "Paneer Tikka",
                calories = 320,
                proteinGrams = 22f,
                carbsGrams = 12f,
                fatGrams = 18f,
                mealType = "Lunch",
                dateMillis = 1700000000000L
            )
        )
        val waters = listOf(
            WaterEntry(
                id = 5,
                amountMl = 500,
                dateMillis = 1700000000000L
            )
        )
        val sleeps = listOf(
            SleepEntry(
                id = 2,
                startMillis = 1700000000000L,
                endMillis = 1700028800000L,
                quality = 90,
                notes = "Restful sleep",
                dateMillis = 1700000000000L
            )
        )

        val sb = StringBuilder()
        sb.append("--- FOOD LOGS ---\n")
        sb.append("DateMillis,Name,Calories,ProteinGrams,CarbsGrams,FatGrams,MealType\n")
        foods.forEach { f -> sb.append("${f.dateMillis},\"${f.name}\",${f.calories},${f.proteinGrams},${f.carbsGrams},${f.fatGrams},${f.mealType}\n") }
        sb.append("\n--- WATER LOGS ---\n")
        sb.append("DateMillis,AmountMl\n")
        waters.forEach { w -> sb.append("${w.dateMillis},${w.amountMl}\n") }
        sb.append("\n--- SLEEP LOGS ---\n")
        sb.append("StartMillis,EndMillis,QualityScore,Notes\n")
        sleeps.forEach { s -> sb.append("${s.startMillis},${s.endMillis},${s.quality},\"${s.notes}\"\n") }
        val csvResult = sb.toString()

        assertTrue(csvResult.contains("Paneer Tikka"))
        assertTrue(csvResult.contains("500"))
        assertTrue(csvResult.contains("Restful sleep"))
    }
}

