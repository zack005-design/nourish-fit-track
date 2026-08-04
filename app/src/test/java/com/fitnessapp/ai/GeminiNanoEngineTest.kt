package com.fitnessapp.ai

import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiNanoEngineTest {

    @Test
    fun testPromptFormatting() {
        val prompt = buildString {
            appendLine("[SYSTEM: Nourish AI Health Assistant]")
            appendLine("User Query: What is my daily protein target?")
        }
        assertTrue(prompt.contains("Nourish AI Health Assistant"))
        assertTrue(prompt.contains("What is my daily protein target?"))
    }
}
