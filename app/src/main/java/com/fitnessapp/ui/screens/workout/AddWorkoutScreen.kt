package com.fitnessapp.ui.screens.workout

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    workoutRepository: com.fitnessapp.data.repository.WorkoutRepository,
    entryId: Long?,
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModel.Factory(workoutRepository))
) {
    var type by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(entryId) {
        if (entryId != null) {
            val entry = viewModel.getEntry(entryId).firstOrNull()
            if (entry != null) {
                type = entry.type
                duration = entry.durationMinutes.toString()
                calories = entry.caloriesBurned.toString()
                distance = entry.distanceKm?.toString() ?: ""
                notes = entry.notes ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId != null) "Edit Workout" else "Add Workout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Type (e.g., Run, Lift)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duration (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = duration.isNotBlank() && duration.toIntOrNull() == null
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calories burned") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = calories.isNotBlank() && calories.toIntOrNull() == null
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Distance (km, optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = distance.isNotBlank() && distance.toFloatOrNull() == null
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(Modifier.height(24.dp))

            val durationInt = duration.toIntOrNull() ?: 0
            val saveEnabled = type.isNotBlank() && durationInt > 0

            Button(
                onClick = {
                    val calInt = calories.toIntOrNull() ?: 0
                    val distFloat = distance.toFloatOrNull()
                    val notesNullable = notes.takeIf { it.isNotBlank() }

                    if (entryId != null) {
                        viewModel.updateEntry(
                            id = entryId,
                            type = type,
                            durationMinutes = durationInt,
                            caloriesBurned = calInt,
                            distanceKm = distFloat,
                            notes = notesNullable
                        )
                    } else {
                        viewModel.addEntry(
                            type = type,
                            durationMinutes = durationInt,
                            caloriesBurned = calInt,
                            distanceKm = distFloat,
                            notes = notesNullable
                        )
                    }
                    onShowSnackbar("Workout saved")
                    onBack()
                },
                enabled = saveEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Workout")
            }
        }
    }
}
