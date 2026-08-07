package com.fitnessapp.ui.screens.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitnessapp.data.FoodDatabase
import com.fitnessapp.data.FoodItem
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.frostedGlass
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentCyan
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.AccentRed
import com.fitnessapp.ui.theme.AccentYellow
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import com.fitnessapp.ui.theme.TextTertiary
import com.fitnessapp.util.BarcodeScannerUtil
import com.fitnessapp.util.HealthConnectManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    foodRepository: FoodRepository,
    entryId: Long? = null,
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var selectedItem by remember { mutableStateOf<FoodItem?>(null) }
    var servings by remember { mutableFloatStateOf(1.0f) }
    var isCustomMode by remember { mutableStateOf(false) }

    // Custom meal fields
    var customName by remember { mutableStateOf("") }
    var customCalories by remember { mutableStateOf("350") }
    var customProtein by remember { mutableStateOf("25") }
    var customCarbs by remember { mutableStateOf("40") }
    var customFat by remember { mutableStateOf("10") }
    var customFiber by remember { mutableStateOf("5") }
    var customSugar by remember { mutableStateOf("4") }
    var customSodium by remember { mutableStateOf("150") }
    var customCholesterol by remember { mutableStateOf("0") }

    // OpenFoodFacts API Search State
    var apiResults by remember { mutableStateOf<List<BarcodeScannerUtil.ScannedProduct>>(emptyList()) }
    var isSearchingApi by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val defaultMealType = when (currentHour) {
        in 5..10 -> "Breakfast"
        in 11..15 -> "Lunch"
        in 16..18 -> "Snack"
        else -> "Dinner"
    }
    var selectedMealType by remember { mutableStateOf(defaultMealType) }

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    val categories = listOf("All", "Kerala", "Tamil Nadu", "Karnataka", "South Indian", "North Indian", "Protein", "Grain", "Vegetable", "Fruit", "Snacks", "Beverages")
    val scope = rememberCoroutineScope()

    LaunchedEffect(entryId) {
        if (entryId != null) {
            val entry = foodRepository.getEntryById(entryId).firstOrNull()
            if (entry != null) {
                searchQuery = entry.name
                customName = entry.name
                selectedMealType = entry.mealType
                customCalories = entry.calories.toString()
                customProtein = entry.proteinGrams.toInt().toString()
                customCarbs = entry.carbsGrams.toInt().toString()
                customFat = entry.fatGrams.toInt().toString()
                customFiber = entry.fiberGrams.toInt().toString()
                customSugar = entry.sugarGrams.toInt().toString()
                customSodium = entry.sodiumMg.toInt().toString()
                customCholesterol = entry.cholesterolMg.toInt().toString()

                val match = FoodDatabase.items.find { it.name.equals(entry.name, ignoreCase = true) }
                if (match != null) {
                    selectedItem = match
                    if (match.calories > 0) {
                        servings = (entry.calories.toFloat() / match.calories.toFloat()).coerceAtLeast(0.5f)
                    }
                } else {
                    isCustomMode = true
                }
            }
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            val exactMatch = FoodDatabase.items.find { it.name.equals(searchQuery.trim(), ignoreCase = true) }
            if (exactMatch != null) {
                selectedItem = exactMatch
            } else {
                val partialMatch = FoodDatabase.items.find { it.name.startsWith(searchQuery.trim(), ignoreCase = true) }
                if (partialMatch != null && selectedItem == null) {
                    selectedItem = partialMatch
                }
            }

            if (searchQuery.trim().length >= 3) {
                kotlinx.coroutines.delay(400)
                isSearchingApi = true
                val results = BarcodeScannerUtil.searchOpenFoodFactsOnline(searchQuery.trim())
                apiResults = results
                isSearchingApi = false
                if (results.isNotEmpty() && selectedItem == null) {
                    selectedItem = BarcodeScannerUtil.toFoodItem(results.first())
                }
            }
        } else {
            apiResults = emptyList()
        }
    }

    val filteredDatabaseItems = remember(searchQuery, selectedCategoryIndex) {
        val selectedCat = categories.getOrElse(selectedCategoryIndex) { "All" }
        FoodDatabase.items.filter { item ->
            val matchesCategory = when (selectedCat) {
                "All" -> true
                "Kerala" -> item.category.contains("Kerala", ignoreCase = true)
                "Tamil Nadu" -> item.category.contains("Tamil", ignoreCase = true)
                "Karnataka" -> item.category.contains("Karnataka", ignoreCase = true)
                "South Indian" -> item.category.contains("South", ignoreCase = true) || item.category.contains("Kerala", ignoreCase = true) || item.category.contains("Tamil", ignoreCase = true) || item.category.contains("Karnataka", ignoreCase = true)
                "North Indian" -> item.category.contains("North", ignoreCase = true)
                "Snacks" -> item.category.contains("Snack", ignoreCase = true)
                else -> item.category.contains(selectedCat, ignoreCase = true)
            }
            val matchesQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val activeItem = selectedItem ?: filteredDatabaseItems.firstOrNull()

    val finalName = if (isCustomMode) {
        if (customName.isNotBlank()) customName else if (searchQuery.isNotBlank()) searchQuery else "Custom Meal"
    } else {
        activeItem?.name ?: if (searchQuery.isNotBlank()) searchQuery else "Custom Meal"
    }

    val finalCalories = if (isCustomMode) {
        customCalories.toIntOrNull() ?: 350
    } else {
        if (activeItem != null) (activeItem.calories * servings).toInt() else (customCalories.toIntOrNull() ?: 350)
    }

    val finalProtein = if (isCustomMode) {
        customProtein.toFloatOrNull() ?: 25f
    } else {
        if (activeItem != null) activeItem.protein * servings else (customProtein.toFloatOrNull() ?: 25f)
    }

    val finalCarbs = if (isCustomMode) {
        customCarbs.toFloatOrNull() ?: 40f
    } else {
        if (activeItem != null) activeItem.carbs * servings else (customCarbs.toFloatOrNull() ?: 40f)
    }

    val finalFat = if (isCustomMode) {
        customFat.toFloatOrNull() ?: 10f
    } else {
        if (activeItem != null) activeItem.fat * servings else (customFat.toFloatOrNull() ?: 10f)
    }

    val finalFiber = if (isCustomMode) {
        customFiber.toFloatOrNull() ?: 5f
    } else {
        if (activeItem != null) activeItem.fiber * servings else (customFiber.toFloatOrNull() ?: 5f)
    }

    val finalSugar = if (isCustomMode) customSugar.toFloatOrNull() ?: 0f else 0f
    val finalSodium = if (isCustomMode) customSodium.toFloatOrNull() ?: 0f else 0f
    val finalCholesterol = if (isCustomMode) customCholesterol.toFloatOrNull() ?: 0f else 0f

    Scaffold(
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {

            TopAppBar(
                modifier = Modifier.frostedGlass(
                    backgroundColor = BackgroundDark.copy(alpha = 0.82f),
                    fallbackColor = BackgroundDark
                ),
                title = {
                    Text(
                        text = if (entryId != null) "Edit Food" else "Add Food",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    if (entryId != null) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    foodRepository.delete(
                                        FoodEntry(
                                            id = entryId,
                                            name = "",
                                            calories = 0,
                                            proteinGrams = 0f,
                                            carbsGrams = 0f,
                                            fatGrams = 0f,
                                            mealType = "",
                                            dateMillis = 0L
                                        )
                                    )
                                    onShowSnackbar("Meal removed")
                                    onBack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Meal",
                                tint = AccentRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            // Pinned Log Meal Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(
                        backgroundColor = BackgroundDark.copy(alpha = 0.85f),
                        fallbackColor = BackgroundDark
                    )
                    .padding(16.dp)
            ) {
                val context = androidx.compose.ui.platform.LocalContext.current
                Button(
                    onClick = {
                        scope.launch {
                            val entry = FoodEntry(
                                id = entryId ?: 0L,
                                name = finalName,
                                calories = finalCalories,
                                proteinGrams = finalProtein,
                                carbsGrams = finalCarbs,
                                fatGrams = finalFat,
                                fiberGrams = finalFiber,
                                sugarGrams = finalSugar,
                                sodiumMg = finalSodium,
                                cholesterolMg = finalCholesterol,
                                mealType = selectedMealType,
                                dateMillis = System.currentTimeMillis()
                            )
                            val insertedId = foodRepository.insert(entry)
                            val entryToSync = entry.copy(id = if (entry.id == 0L) insertedId else entry.id)
                            HealthConnectManager.insertNutritionRecords(context, listOf(entryToSync))
                            onShowSnackbar(if (entryId != null) "Meal updated & synced to Google Health" else "$finalName added & synced to Google Health!")
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = BackgroundDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (entryId != null) "Update Meal ($finalCalories kcal)" else "+ Log $finalName ($finalCalories kcal)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BackgroundDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── 1. SEARCH BAR & MODE TOGGLE ───────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (isCustomMode && customName.isBlank()) {
                            customName = it
                        }
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search food, dish, or barcode (e.g. 8901058852317)...", color = TextTertiary, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = ""; selectedItem = null }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentGreen,
                        unfocusedBorderColor = SurfaceCardAlt,
                        focusedContainerColor = SurfaceCardAlt,
                        unfocusedContainerColor = SurfaceCardAlt,
                        focusedTextColor = TextPrimary,
                    )
                )
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCustomMode) AccentGreen else SurfaceCardAlt)
                        .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(16.dp))
                        .clickable { isCustomMode = !isCustomMode },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Custom Meal Mode",
                        tint = if (isCustomMode) BackgroundDark else TextPrimary
                    )
                }
            }

            // OpenFoodFacts API Live Search Button
            if (searchQuery.isNotBlank() && !isCustomMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentBlue.copy(alpha = 0.15f))
                        .border(1.dp, AccentBlue.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clickable {
                            if (!isSearchingApi) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isSearchingApi = true
                                scope.launch {
                                    val results = BarcodeScannerUtil.searchOpenFoodFactsOnline(searchQuery)
                                    apiResults = results
                                    isSearchingApi = false
                                    if (results.isNotEmpty()) {
                                        val firstProduct = results.first()
                                        selectedItem = BarcodeScannerUtil.toFoodItem(firstProduct)
                                        onShowSnackbar("Found ${results.size} items on OpenFoodFacts API")
                                    } else {
                                        onShowSnackbar("No OpenFoodFacts results for '$searchQuery'")
                                    }
                                }
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isSearchingApi) "⚡ Querying OpenFoodFacts API..." else "⚡ Search '$searchQuery' on OpenFoodFacts API",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )
                    }
                }
            }

            // OpenFoodFacts API Results Container
            if (apiResults.isNotEmpty() && !isCustomMode) {
                AppCard(
                    backgroundColor = SurfaceCardAlt,
                    borderColor = AccentBlue.copy(alpha = 0.4f),
                    contentPadding = 16.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OpenFoodFacts API Results (${apiResults.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )
                        Text(
                            text = "Clear",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.clickable { apiResults = emptyList() }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    apiResults.forEach { product ->
                        val foodItem = remember(product) { BarcodeScannerUtil.toFoodItem(product) }
                        val isSelected = selectedItem?.name == foodItem.name

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable {
                                    selectedItem = foodItem
                                    servings = 1.0f
                                    onShowSnackbar("Selected ${product.name}")
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Brand: ${product.brand} • ${product.proteinGrams}g Protein • ${product.carbsGrams}g Carbs",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentOrange.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${product.calories} kcal",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange
                                )
                            }
                        }
                    }
                }
            }



            // ─── 2. MEAL TYPE SELECTOR PILLS ───────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mealTypes.forEach { type ->
                    val isSelected = selectedMealType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(if (isSelected) AccentGreen.copy(alpha = 0.25f) else SurfaceCardAlt)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AccentGreen else Color(0xFF2C3242),
                                shape = RoundedCornerShape(19.dp)
                            )
                            .clickable { selectedMealType = type },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AccentGreen else TextSecondary
                        )
                    }
                }
            }

            // ─── 3. REDESIGNED SELECTED FOOD ITEM CARD (CIRCLED SECTION FIX) ────────
            val currentSelected = activeItem
            AnimatedVisibility(visible = !isCustomMode && currentSelected != null) {
                if (currentSelected != null) {
                    val totalCals = (currentSelected.calories * servings).toInt()
                    val totalProt = currentSelected.protein * servings
                    val totalCarb = currentSelected.carbs * servings
                    val totalFat = currentSelected.fat * servings
                    val totalFib = currentSelected.fiber * servings

                    AppCard(contentPadding = 20.dp) {
                        // Title & Calorie Header Row (Clean Spacing, No Truncation)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = currentSelected.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${currentSelected.category} • ${currentSelected.servingSize}",
                                    fontSize = 12.sp,
                                    color = AccentGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AccentOrange.copy(alpha = 0.18f))
                                    .border(1.dp, AccentOrange.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "$totalCals kcal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AccentOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Servings Stepper Control
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Serving Quantity",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (servings > 0.5f) servings -= 0.5f },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceCardAlt)
                                        .border(1.dp, Color(0xFF2C3242), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Decrease",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = String.format("%.1fx", servings),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )

                                IconButton(
                                    onClick = { servings += 0.5f },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceCardAlt)
                                        .border(1.dp, Color(0xFF2C3242), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Increase",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // 4-Column Equal-Width Macro Breakdown Strip (25% equal width per badge)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCardAlt)
                                .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(16.dp))
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                MacroNutrientBadge("Protein", String.format("%.1fg", totalProt), AccentGreen)
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                MacroNutrientBadge("Carbs", String.format("%.1fg", totalCarb), AccentBlue)
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                MacroNutrientBadge("Fat", String.format("%.1fg", totalFat), AccentYellow)
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                MacroNutrientBadge("Fiber", String.format("%.1fg", totalFib), AccentPurple)
                            }
                        }
                    }
                }
            }

            // ─── 4. CUSTOM MEAL EDITOR CARD (If custom mode is active) ──────────────
            AnimatedVisibility(visible = isCustomMode) {
                AppCard {
                    Text(
                        text = "Create Custom Meal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Meal / Dish Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGreen,
                            unfocusedBorderColor = SurfaceCardAlt,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customCalories,
                            onValueChange = { if (it.all { c -> c.isDigit() }) customCalories = it },
                            label = { Text("Calories (kcal)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                unfocusedBorderColor = SurfaceCardAlt,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = customProtein,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) customProtein = it },
                            label = { Text("Protein (g)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                unfocusedBorderColor = SurfaceCardAlt,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customCarbs,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) customCarbs = it },
                            label = { Text("Carbs (g)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                unfocusedBorderColor = SurfaceCardAlt,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = customFat,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) customFat = it },
                            label = { Text("Fat (g)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                unfocusedBorderColor = SurfaceCardAlt,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customFiber,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) customFiber = it },
                            label = { Text("Fiber (g)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                unfocusedBorderColor = SurfaceCardAlt,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = customSugar,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) customSugar = it },
                            label = { Text("Sugar (g)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGreen,
                                unfocusedBorderColor = SurfaceCardAlt,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }
            }

            // ─── 5. CATEGORY FILTER BAR ────────────────────────────────────────────
            if (!isCustomMode) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(categories) { index, cat ->
                        val isSelected = selectedCategoryIndex == index
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) AccentGreen else SurfaceCardAlt)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AccentGreen else Color(0xFF2C3242),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable { selectedCategoryIndex = index }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BackgroundDark else TextSecondary
                            )
                        }
                    }
                }
            }

            // ─── 6. FOOD DATABASE LIST ──────────────────────────────────────────────
            if (!isCustomMode) {
                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Food Database (${filteredDatabaseItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "+ Custom Meal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen,
                            modifier = Modifier.clickable { isCustomMode = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (filteredDatabaseItems.isEmpty()) {
                        Text(
                            text = "No matching foods found for '$searchQuery'. Tap '+ Custom Meal' to log it manually.",
                            fontSize = 13.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        filteredDatabaseItems.take(50).forEach { item ->
                            val isSelected = selectedItem?.name == item.name

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) AccentGreen.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        selectedItem = item
                                        servings = 1.0f
                                    }
                                    .padding(vertical = 8.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) AccentGreen else AccentOrange.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Restaurant,
                                            contentDescription = null,
                                            tint = if (isSelected) BackgroundDark else AccentOrange,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = item.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${item.category} • ${item.servingSize} • ${item.calories} kcal",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Text(
                                    text = "${item.calories} kcal",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MacroNutrientBadge(
    label: String,
    valueText: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = valueText, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
    }
}
