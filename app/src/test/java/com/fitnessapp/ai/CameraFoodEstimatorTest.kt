package com.fitnessapp.ai

import android.graphics.Bitmap
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraFoodEstimatorTest {

    @Test
    fun testNullBitmapReturnsFallbackPreset() {
        val result = CameraFoodEstimator.estimateMealFromPhoto(null)
        assertNotNull(result)
        assertTrue(result.calories > 0)
        assertTrue(result.confidenceScore >= 80)
    }
}
