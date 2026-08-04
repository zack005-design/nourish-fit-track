package com.fitnessapp.ui.screens.settings

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import com.fitnessapp.util.HealthConnectManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.StepsRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.ui.theme.SurfaceCard
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.frostedGlass
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.AccentRed
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import com.fitnessapp.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    foodRepository: FoodRepository,
    waterRepository: WaterRepository,
    sleepRepository: SleepRepository,
    stepsRepository: StepsRepository,
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            settingsRepository, foodRepository, waterRepository, sleepRepository, stepsRepository
        )
    )
) {
    val userGoals by viewModel.userGoals.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var calorieGoal by remember { mutableStateOf("") }
    var proteinGoal by remember { mutableStateOf("") }
    var fiberGoal by remember { mutableStateOf("") }
    var waterGoal by remember { mutableStateOf("") }
    var sleepGoalHours by remember { mutableStateOf("") }
    var stepsGoal by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userGoals) {
        calorieGoal = userGoals.dailyCalorieGoal.toString()
        proteinGoal = userGoals.dailyProteinGoal.toInt().toString()
        fiberGoal = userGoals.dailyFiberGoal.toInt().toString()
        waterGoal = userGoals.dailyWaterGoal.toString()
        sleepGoalHours = userGoals.dailySleepGoalHours.toString()
        stepsGoal = userGoals.dailyStepsGoal.toString()
    }

    var showHealthDialog by remember { mutableStateOf(false) }

    if (showHealthDialog) {
        AlertDialog(
            onDismissRequest = { showHealthDialog = false },
            containerColor = SurfaceCardAlt,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Health Connect",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Read & Write nutrition, hydration, sleep, and steps data directly with Google Health Connect.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Action 1: Write / Export Data to Google Health
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentGreen.copy(alpha = 0.15f))
                            .border(1.dp, AccentGreen.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable {
                                showHealthDialog = false
                                val msg = HealthConnectManager.writeDataToHealthConnect(
                                    foodCount = 1,
                                    waterCount = 1,
                                    sleepCount = 1
                                )
                                onShowSnackbar(msg)
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Sync & Write Data to Google Health", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Export food, water, sleep & steps to Health Connect", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }

                    // Action 2: Read / Import Data from Google Health
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentBlue.copy(alpha = 0.15f))
                            .border(1.dp, AccentBlue.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable {
                                showHealthDialog = false
                                val msg = HealthConnectManager.readDataFromHealthConnect()
                                onShowSnackbar(msg)
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Read Data from Google Health", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Import health & activity records from Health Connect", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }

                    // Action 3: Open Google Health Connect System Settings
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCard)
                            .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(12.dp))
                            .clickable {
                                showHealthDialog = false
                                val launched = HealthConnectManager.openHealthConnect(context)
                                onShowSnackbar(if (launched) "Opening Google Health Connect..." else "Could not open Google Health Connect")
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Open Health Connect Settings", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Manage app permissions & connected sources", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHealthDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = SurfaceCardAlt,
            title = {
                Text(
                    text = "Clear All Data?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all food, water, sleep, and steps entries. Your goals will not be affected. This cannot be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDialog = false
                        viewModel.clearAllData {
                            onShowSnackbar("All data cleared successfully")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Clear All", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {

            TopAppBar(
                modifier = Modifier.frostedGlass(
                    backgroundColor = BackgroundDark.copy(alpha = 0.85f),
                    fallbackColor = BackgroundDark
                ),
                title = {
                    Text(
                        text = "Account & Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
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
            Spacer(modifier = Modifier.height(4.dp))

            // ─── 1. Apple Health User Profile Card ──────────────────────────────────────
            AppCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShowSnackbar("Profile details") },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AccentGreen.copy(alpha = 0.15f))
                            .border(1.dp, AccentGreen.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = AccentGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "My Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Personal health & fitness profile",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextTertiary
                    )
                }
            }

            // ─── 2. Quick Shortcuts (Google Health, Reminder Notification, & Clear All Data) ───
            AppCard {
                Text(
                    text = "Quick Shortcuts & Features",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val haptic = LocalHapticFeedback.current

                    ShortcutTile(
                        title = "Google Health",
                        icon = Icons.Default.Favorite,
                        tint = AccentGreen,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            showHealthDialog = true
                        }
                    )

                    ShortcutTile(
                        title = "Remind Hydrate",
                        icon = Icons.Default.Notifications,
                        tint = AccentBlue,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            com.fitnessapp.util.ReminderNotificationHelper.sendReminderNotification(
                                context,
                                "Hydration Check-in 💧",
                                "Time to drink a glass of water to hit your daily goal!"
                            )
                            onShowSnackbar("Reminder notification sent!")
                        }
                    )

                    ShortcutTile(
                        title = "Clear Data",
                        icon = Icons.Default.DeleteForever,
                        tint = AccentRed,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            showClearDialog = true
                        }
                    )
                }
            }

            // ─── 3. Daily Goals Configuration Form ────────────────────────────────────
            AppCard {
                Text(
                    text = "Daily Goals Configuration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                GoalTextField(
                    value = calorieGoal,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) calorieGoal = it },
                    label = "Daily Calorie Goal (kcal)",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(10.dp))

                GoalTextField(
                    value = proteinGoal,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) proteinGoal = it },
                    label = "Daily Protein Goal (g)",
                    keyboardType = KeyboardType.Decimal
                )

                Spacer(modifier = Modifier.height(10.dp))

                GoalTextField(
                    value = fiberGoal,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) fiberGoal = it },
                    label = "Daily Fiber Goal (g)",
                    keyboardType = KeyboardType.Decimal
                )

                Spacer(modifier = Modifier.height(10.dp))

                GoalTextField(
                    value = waterGoal,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) waterGoal = it },
                    label = "Daily Water Goal (ml)",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(10.dp))

                GoalTextField(
                    value = sleepGoalHours,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) sleepGoalHours = it },
                    label = "Daily Sleep Target (hours)",
                    keyboardType = KeyboardType.Decimal
                )

                Spacer(modifier = Modifier.height(10.dp))

                GoalTextField(
                    value = stepsGoal,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) stepsGoal = it },
                    label = "Daily Steps Target",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(16.dp))

                val haptic = LocalHapticFeedback.current
                Button(
                    onClick = {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        viewModel.saveGoals(
                            calorieGoal = calorieGoal.toIntOrNull() ?: 2200,
                            proteinGoal = proteinGoal.toFloatOrNull() ?: 140f,
                            fiberGoal = fiberGoal.toFloatOrNull() ?: 30f,
                            waterGoal = waterGoal.toIntOrNull() ?: 2500,
                            sleepGoalHours = sleepGoalHours.toFloatOrNull() ?: 8.0f,
                            stepsGoal = stepsGoal.toIntOrNull() ?: 10000
                        ) {
                            onShowSnackbar("Goals saved successfully")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Text(text = "Save Goals", fontWeight = FontWeight.Bold, color = BackgroundDark)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ─── 4. App Version Footer ────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nourish Fitness v1.2.0 (Build 13)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextTertiary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Designed for Personal High Performance Tracking",
                    fontSize = 10.sp,
                    color = TextTertiary.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun GoalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentGreen,
            unfocusedBorderColor = Color(0xFF2C3242),
            focusedLabelColor = AccentGreen,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        )
    )
}

@Composable
private fun ShortcutTile(
    title: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCardAlt)
            .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun HelpOptionRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SurfaceCardAlt),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextTertiary
        )
    }
}
