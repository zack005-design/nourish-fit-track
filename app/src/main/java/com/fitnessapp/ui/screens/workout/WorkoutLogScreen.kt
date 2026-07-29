package com.fitnessapp.ui.screens.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fitnessapp.data.db.entity.WorkoutEntry

@Composable
fun WorkoutLogScreen(
    workoutRepository: com.fitnessapp.data.repository.WorkoutRepository,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val viewModel = WorkoutViewModel.Factory(workoutRepository).create(WorkoutViewModel::class.java)
    val entries = viewModel.todayEntries

    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = onAddClick) { Text("Add Workout") }
        LazyColumn {
            items(entries.value) { entry: WorkoutEntry ->
                Card(modifier = Modifier.clickable { onEditClick(entry.id) }) {
                    Column {
                        Text(text = "${entry.type}: ${entry.durationMinutes} min")
                        Text(text = "Calories: ${entry.caloriesBurned}")
                    }
                }
            }
        }
    }
}
