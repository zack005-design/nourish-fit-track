package com.fitnessapp.ui.screens.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import android.widget.Toast
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.firstOrNull
import com.fitnessapp.R
import com.fitnessapp.data.repository.FoodRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    foodRepository: FoodRepository,
    entryId: Long? = null,
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: FoodViewModel = viewModel(factory = FoodViewModel.Factory(foodRepository))
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("Meal") }
    val context = LocalContext.current

    LaunchedEffect(entryId) {
        if (entryId != null) {
            val entry = viewModel.getEntry(entryId).firstOrNull()
            if (entry != null) {
                name = entry.name
                calories = entry.calories.toString()
                protein = entry.proteinGrams.toString()
                carbs = entry.carbsGrams.toString()
                fat = entry.fatGrams.toString()
                mealType = entry.mealType
            }
        }
    }

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    val caloriesIsValid = calories.isNotBlank() && calories.toIntOrNull() != null
    val proteinIsValid = protein.isNotBlank() && protein.toFloatOrNull() != null
    val carbsIsValid = carbs.isNotBlank() && carbs.toFloatOrNull() != null
    val fatIsValid = fat.isNotBlank() && fat.toFloatOrNull() != null
    val isFormValid = name.isNotBlank() && caloriesIsValid && proteinIsValid && carbsIsValid && fatIsValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId != null) stringResource(R.string.food_entry_edit_title) else stringResource(R.string.food_entry_add_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.food_entry_back_content_description))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.food_entry_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text(stringResource(R.string.food_entry_calories_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = calories.isNotBlank() && !caloriesIsValid
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text(stringResource(R.string.food_entry_protein_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = protein.isNotBlank() && !proteinIsValid
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = carbs,
                onValueChange = { carbs = it },
                label = { Text(stringResource(R.string.food_entry_carbs_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = carbs.isNotBlank() && !carbsIsValid
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text(stringResource(R.string.food_entry_fat_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = fat.isNotBlank() && !fatIsValid
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.food_entry_meal_type_label),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(8.dp))

            Column {
                mealTypes.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { type ->
                            FilterChip(
                                selected = mealType == type,
                                onClick = { mealType = type },
                                label = { Text(type) }
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isFormValid) {
                        if (entryId != null) {
                            viewModel.updateEntry(
                                id = entryId,
                                name = name,
                                calories = calories.toInt(),
                                protein = protein.toFloat(),
                                carbs = carbs.toFloat(),
                                fat = fat.toFloat(),
                                mealType = mealType
                            )
                        } else {
                            viewModel.addEntry(
                                name = name,
                                calories = calories.toInt(),
                                protein = protein.toFloat(),
                                carbs = carbs.toFloat(),
                                fat = fat.toFloat(),
                                mealType = mealType
                            )
                        }
                        onShowSnackbar("Entry saved")
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text(stringResource(R.string.food_entry_save_action))
            }
        }
    }
}
