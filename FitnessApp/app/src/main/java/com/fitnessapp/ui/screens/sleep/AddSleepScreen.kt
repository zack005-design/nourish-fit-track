package com.fitnessapp.ui.screens.sleep

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import android.widget.Toast
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.firstOrNull
import com.fitnessapp.R
import com.fitnessapp.data.repository.SleepRepository
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSleepScreen(
    sleepRepository: SleepRepository,
    entryId: Long? = null,
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: SleepViewModel = viewModel(factory = SleepViewModel.Factory(sleepRepository))
) {
    var quality by remember { mutableFloatStateOf(3f) }
    var notes by remember { mutableStateOf("") }
    var startHour by remember { mutableIntStateOf(22) }
    var startMinute by remember { mutableIntStateOf(0) }
    var endHour by remember { mutableIntStateOf(7) }
    var endMinute by remember { mutableIntStateOf(0) }

    LaunchedEffect(entryId) {
        if (entryId != null) {
            val entry = viewModel.getEntry(entryId).firstOrNull()
            if (entry != null) {
                quality = entry.quality.toFloat()
                notes = entry.notes
                val startCal = Calendar.getInstance().apply { timeInMillis = entry.startMillis }
                val endCal = Calendar.getInstance().apply { timeInMillis = entry.endMillis }
                startHour = startCal.get(Calendar.HOUR_OF_DAY)
                startMinute = startCal.get(Calendar.MINUTE)
                endHour = endCal.get(Calendar.HOUR_OF_DAY)
                endMinute = endCal.get(Calendar.MINUTE)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId != null) stringResource(R.string.sleep_entry_edit_title) else stringResource(R.string.sleep_entry_add_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.sleep_entry_back_content_description))
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
            Text(
                text = stringResource(R.string.sleep_entry_time_label),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.sleep_entry_bedtime_label),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TimePicker(
                    hour = startHour,
                    minute = startMinute,
                    onTimeSelected = { selectedHour, selectedMinute ->
                        startHour = selectedHour
                        startMinute = selectedMinute
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.sleep_entry_wake_time_label),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TimePicker(
                    hour = endHour,
                    minute = endMinute,
                    onTimeSelected = { selectedHour, selectedMinute ->
                        endHour = selectedHour
                        endMinute = selectedMinute
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.sleep_entry_quality_label, quality.toInt()),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Slider(
                value = quality,
                onValueChange = { quality = it },
                valueRange = 1f..5f,
                steps = 3
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.sleep_entry_notes_label)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    val now = Calendar.getInstance()
                    val bedTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, startHour)
                        set(Calendar.MINUTE, startMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        if (after(now)) {
                            add(Calendar.DAY_OF_YEAR, -1)
                        }
                    }
                    val wakeTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, endHour)
                        set(Calendar.MINUTE, endMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        if (before(bedTime) || equals(bedTime)) {
                            add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }

                    if (entryId != null) {
                        viewModel.updateEntry(
                            id = entryId,
                            startMillis = bedTime.timeInMillis,
                            endMillis = wakeTime.timeInMillis,
                            quality = quality.toInt(),
                            notes = notes
                        )
                    } else {
                        viewModel.addEntry(
                            startMillis = bedTime.timeInMillis,
                            endMillis = wakeTime.timeInMillis,
                            quality = quality.toInt(),
                            notes = notes
                        )
                    }
                    onShowSnackbar("Entry saved")
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.sleep_entry_save_action))
            }
        }
    }
}

@Composable
private fun TimePicker(
    hour: Int,
    minute: Int,
    onTimeSelected: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val locale = Locale.getDefault()

    OutlinedButton(onClick = {
        TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                onTimeSelected(selectedHour, selectedMinute)
            },
            hour,
            minute,
            true
        ).show()
    }) {
        Text(String.format(locale, "%02d:%02d", hour, minute))
    }
}
