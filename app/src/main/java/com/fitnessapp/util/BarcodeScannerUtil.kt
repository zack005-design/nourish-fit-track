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
     * Performs a live online lookup via OpenFoodFacts REST API.
     * Falls back to offline database if unnetworked or not found.
     */
    suspend fun lookupBarcodeOnline(barcode: String): ScannedProduct = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val cleaned = barcode.trim()
        val localMatch = barcodeDatabase[cleaned]
        if (localMatch != null) return@withContext localMatch

        try {
            val urlString = "https://world.openfoodfacts.org/api/v0/product/$cleaned.json"
            val url = java.net.URL(urlString)
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            connection.setRequestProperty("User-Agent", "NourishFitnessApp/1.3.0 (Android)")

            if (connection.responseCode == 200) {
                val stream = connection.inputStream
                val jsonString = stream.bufferedReader().use { it.readText() }
                val parsed = parseJsonFromOpenFoodFacts(cleaned, jsonString)
                if (parsed != null) return@withContext parsed
            }
        } catch (e: Exception) {
            // Network or parsing failure — fallback to local lookup
        }
        return@withContext lookupBarcode(cleaned)
    }

    /**
     * Parses raw OpenFoodFacts JSON string (pure Kotlin regex, 100% JVM unit test compatible).
     */
    fun parseJsonFromOpenFoodFacts(barcode: String, jsonString: String): ScannedProduct? {
        return try {
            val statusMatch = Regex("\"status\"\\s*:\\s*(\\d+)").find(jsonString)
            val status = statusMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
            if (status != 1) return null

            fun extractString(key: String): String? {
                val match = Regex("\"$key\"\\s*:\\s*\"([^\"]+)\"").find(jsonString)
                return match?.groupValues?.get(1)
            }

            fun extractFloat(key: String, defaultVal: Float): Float {
                val match = Regex("\"$key\"\\s*:\\s*([0-9.]+)").find(jsonString)
                return match?.groupValues?.get(1)?.toFloatOrNull() ?: defaultVal
            }

            val rawName = extractString("product_name") ?: "Scanned Item"
            val rawBrand = extractString("brands") ?: "OpenFoodFacts"
            val calories = extractFloat("energy-kcal_100g", extractFloat("energy-kcal", 250f)).toInt()
            val protein = extractFloat("proteins_100g", 12.0f)
            val carbs = extractFloat("carbohydrates_100g", 30.0f)
            val fat = extractFloat("fat_100g", 8.0f)

            ScannedProduct(
                barcode = barcode,
                name = "$rawName ($rawBrand)",
                calories = calories.coerceAtLeast(0),
                proteinGrams = protein.coerceAtLeast(0f),
                carbsGrams = carbs.coerceAtLeast(0f),
                fatGrams = fat.coerceAtLeast(0f),
                brand = rawBrand
            )
        } catch (e: Exception) {
            null
        }
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

    /**
     * Searches OpenFoodFacts API for products matching a name or barcode query.
     */
    suspend fun searchOpenFoodFactsOnline(query: String): List<ScannedProduct> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return@withContext emptyList()

        // If numeric (barcode), perform direct barcode lookup
        if (trimmed.all { it.isDigit() }) {
            val single = lookupBarcodeOnline(trimmed)
            return@withContext listOf(single)
        }

        val results = mutableListOf<ScannedProduct>()
        try {
            val encoded = java.net.URLEncoder.encode(trimmed, "UTF-8")
            val urlString = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$encoded&search_simple=1&action=process&json=1&page_size=8"
            val url = java.net.URL(urlString)
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 4000
            connection.readTimeout = 4000
            connection.setRequestProperty("User-Agent", "NourishFitnessApp/1.4.3 (Android)")

            if (connection.responseCode == 200) {
                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                val productsRegex = Regex("\"products\"\\s*:\\s*\\[(.*?)\\]\\s*,\\s*\"count\"", RegexOption.DOT_MATCHES_ALL)
                val match = productsRegex.find(jsonString)
                if (match != null) {
                    val productsArray = match.groupValues[1]
                    // extract individual product json blocks
                    val blocks = productsArray.split(Regex("\\},\\s*\\{"))
                    for (block in blocks) {
                        val nameMatch = Regex("\"product_name(?:_en)?\"\\s*:\\s*\"([^\"]+)\"").find(block)
                        val brandMatch = Regex("\"brands\"\\s*:\\s*\"([^\"]+)\"").find(block)
                        val codeMatch = Regex("\"code\"\\s*:\\s*\"([^\"]+)\"").find(block)
                        val name = nameMatch?.groupValues?.get(1) ?: continue
                        val brand = brandMatch?.groupValues?.get(1) ?: "OpenFoodFacts"
                        val code = codeMatch?.groupValues?.get(1) ?: "0000"

                        val calMatch = Regex("\"energy-kcal_100g\"\\s*:\\s*([0-9.]+)").find(block)
                        val protMatch = Regex("\"proteins_100g\"\\s*:\\s*([0-9.]+)").find(block)
                        val carbMatch = Regex("\"carbohydrates_100g\"\\s*:\\s*([0-9.]+)").find(block)
                        val fatMatch = Regex("\"fat_100g\"\\s*:\\s*([0-9.]+)").find(block)

                        val calories = calMatch?.groupValues?.get(1)?.toFloatOrNull()?.toInt() ?: 180
                        val protein = protMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 8.0f
                        val carbs = carbMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 24.0f
                        val fat = fatMatch?.groupValues?.get(1)?.toFloatOrNull() ?: 5.0f

                        results.add(
                            ScannedProduct(
                                barcode = code,
                                name = "$name ($brand)",
                                calories = calories.coerceAtLeast(0),
                                proteinGrams = protein.coerceAtLeast(0f),
                                carbsGrams = carbs.coerceAtLeast(0f),
                                fatGrams = fat.coerceAtLeast(0f),
                                brand = brand
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Network fallback
        }

        // Add matching local foods if API returns few items
        if (results.isEmpty()) {
            val localMatches = barcodeDatabase.values.filter {
                it.name.contains(trimmed, ignoreCase = true) || it.brand.contains(trimmed, ignoreCase = true)
            }
            results.addAll(localMatches)
        }

        return@withContext results
    }
}



