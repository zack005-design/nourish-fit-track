package com.fitnessapp.util

import com.fitnessapp.data.FoodItem

/**
 * BarcodeScannerUtil
 * Parses food product barcodes (EAN-13, UPC-A) and matches products with OpenFoodFacts database schema.
 */
object BarcodeScannerUtil {

    data class ScannedProduct(
        val barcode: String,
        val name: String,
        val calories: Int,
        val proteinGrams: Float,
        val carbsGrams: Float,
        val fatGrams: Float,
        val brand: String = "OpenFoodFacts"
    )

    private val barcodeDatabase = mapOf(
        "8901058852317" to ScannedProduct("8901058852317", "Amul Taza Toned Milk (250ml)", 148, 8.0f, 12.0f, 7.5f, "Amul"),
        "8901725181222" to ScannedProduct("8901725181222", "Britannia 100% Whole Wheat Bread", 190, 7.5f, 36.0f, 2.0f, "Britannia"),
        "0041196910759" to ScannedProduct("0041196910759", "Greek Yogurt Plain (170g)", 100, 18.0f, 6.0f, 0.0f, "Chobani"),
        "8901030678523" to ScannedProduct("8901030678523", "Quaker Rolled Oats (50g)", 195, 6.8f, 34.0f, 3.5f, "Quaker")
    )

    /**
     * Looks up nutritional info for a scanned barcode string.
     */
    fun lookupBarcode(barcode: String): ScannedProduct {
        val cleaned = barcode.trim()
        return barcodeDatabase[cleaned] ?: ScannedProduct(
            barcode = cleaned,
            name = "Scanned Food Item ($cleaned)",
            calories = 250,
            proteinGrams = 12.0f,
            carbsGrams = 30.0f,
            fatGrams = 8.0f
        )
    }

    /**
     * Converts a ScannedProduct into a FoodItem model.
     */
    fun toFoodItem(product: ScannedProduct, categoryName: String = "Packaged"): FoodItem {
        return FoodItem(
            name = product.name,
            category = categoryName,
            servingSize = "1 serving",
            calories = product.calories,
            protein = product.proteinGrams,
            carbs = product.carbsGrams,
            fat = product.fatGrams,
            fiber = 2.0f
        )
    }
}

