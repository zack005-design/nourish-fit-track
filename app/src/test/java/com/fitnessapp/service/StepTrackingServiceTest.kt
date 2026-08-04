package com.fitnessapp.service

import org.junit.Assert.assertEquals
import org.junit.Test

class StepTrackingServiceTest {

    @Test
    fun testLiveStepsAccumulation() {
        val total = StepTrackingService.getLiveSteps(serviceSteps = 450, storedSteps = 2500)
        assertEquals(2950, total)
    }
}
