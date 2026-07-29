package com.fitnessapp.ui.screens.sleep

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.R
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.ui.components.StatCard
import com.fitnessapp.ui.theme.Blue40
import com.fitnessapp.ui.theme.Purple40
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SleepLogScreen(
    sleepRepository: SleepRepository,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: SleepViewModel = viewModel(factory = SleepViewModel.Factory(sleepRepository))
) {
    val entries by viewModel.allEntries.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.sleep_log_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        if (entries.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.sleep_log_empty_title),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.sleep_log_empty_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val context = LocalContext.current
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    SleepLogItem(
                        entry = entry,
                        onDelete = {
                            viewModel.deleteEntry(entry)
                            onShowSnackbar("Entry deleted")
                        },
                        onEdit = { onEditClick(entry.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.sleep_log_add_content_description))
        }
    }
}

@Composable
private fun SleepLogItem(
    entry: com.fitnessapp.data.db.entity.SleepEntry,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val timeFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
    val hours = (entry.endMillis - entry.startMillis) / (1000f * 60 * 60)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit() },
        colors = CardDefaults.cardColors(
            containerColor = Purple40.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format("%.1f hrs", hours),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${entry.quality}/5",
                        style = MaterialTheme.typography.titleLarge,
                        color = Blue40
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${timeFormat.format(Date(entry.startMillis))} - ${timeFormat.format(Date(entry.endMillis))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (entry.notes.isNotBlank()) {
                    Text(
                        text = entry.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.sleep_log_edit_content_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.sleep_log_delete_content_description),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
