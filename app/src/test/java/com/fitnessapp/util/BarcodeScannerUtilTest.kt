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

    @Test
    fun testOpenFoodFactsJsonParsing() {
        val sampleJson = """
            {
                "status": 1,
                "product": {
                    "product_name": "Dark Chocolate 70%",
                    "brands": "Lindt",
                    "nutriments": {
                        "energy-kcal_100g": 540,
                        "proteins_100g": 8.5,
                        "carbohydrates_100g": 34.0,
                        "fat_100g": 42.0
                    }
                }
            }
        """.trimIndent()

        val product = BarcodeScannerUtil.parseJsonFromOpenFoodFacts("7610400010000", sampleJson)
        assertNotNull(product)
        assertEquals("Dark Chocolate 70% (Lindt)", product?.name)
        assertEquals(540, product?.calories)
        assertEquals(8.5f, product?.proteinGrams)
        assertEquals(34.0f, product?.carbsGrams)
        assertEquals(42.0f, product?.fatGrams)
    }
}


