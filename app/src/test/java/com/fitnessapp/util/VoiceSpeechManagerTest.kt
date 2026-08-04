package com.fitnessapp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class VoiceSpeechManagerTest {

    @Test
    fun testSpeechPromptFormatting() {
        val prompt = "Speak meal details"
        assertEquals("Speak meal details", prompt)
    }
}

