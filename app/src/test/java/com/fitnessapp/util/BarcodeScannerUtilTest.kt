package com.fitnessapp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BarcodeScannerUtilTest {

    @Test
    fun testKnownBarcodeLookup() {
        val product = BarcodeScannerUtil.lookupBarcode("8901058852317")
        assertEquals("Amul Taza Toned Milk (250ml)", product.name)
        assertEquals(148, product.calories)
    }

    @Test
    fun testUnknownBarcodeFallback() {
        val product = BarcodeScannerUtil.lookupBarcode("9999999999999")
        assertNotNull(product)
        assertEquals("Scanned Food Item (9999999999999)", product.name)
        assertEquals(250, product.calories)
    }

    @Test
    fun testToFoodItemConversion() {
        val product = BarcodeScannerUtil.lookupBarcode("8901725181222")
        val foodItem = BarcodeScannerUtil.toFoodItem(product, "Grain")
        assertEquals("Britannia 100% Whole Wheat Bread", foodItem.name)
        assertEquals("Grain", foodItem.category)
    }
}

