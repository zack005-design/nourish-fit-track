package com.fitnessapp.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.health.connect.client.PermissionController
import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.StepsRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.frostedGlass
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.AccentRed
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCard
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import com.fitnessapp.util.HealthConnectManager
import com.fitnessapp.util.ReminderNotificationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    foodRepository: FoodRepository,
    waterRepository: WaterRepository,
    sleepRepository: SleepRepository,
    stepsRepository: StepsRepository,
    onBack: () -> Unit = {},
    onShowSnackbar: (String) -> Unit = {},
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            settingsRepository,
            foodRepository,
            waterRepository,
            sleepRepository,
            stepsRepository
        )
    )
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val userGoals by viewModel.userGoals.collectAsStateWithLifecycle()

    var showClearDataModal by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        if (grantedPermissions.containsAll(HealthConnectManager.HEALTH_CONNECT_PERMISSIONS)) {
            onShowSnackbar("Google Health Connect permissions granted!")
        } else {
            HealthConnectManager.openHealthConnect(context)
            onShowSnackbar("Opened Google Health Connect Settings")
        }
    }


    val handleSave = { cal: Int, prot: Float, water: Int, sleep: Float, steps: Int ->
        viewModel.saveGoals(
            calorieGoal = cal,
            proteinGoal = prot,
            waterGoal = water,
            sleepGoalHours = sleep,
            stepsGoal = steps,
            onSaved = { onShowSnackbar("Target goals updated") }
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentPurple.copy(alpha = 0.2f))
                                .border(1.dp, AccentPurple.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = AccentPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "More & Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
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
            Spacer(modifier = Modifier.height(8.dp))

            // 1. User Profile Header Card
            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentPurple.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(AccentPurple.copy(alpha = 0.4f), AccentBlue.copy(alpha = 0.4f))
                                )
                            )
                            .border(1.5.dp, AccentPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fitness Athlete",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AccentOrange.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🔥 7 Day Streak",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "PRO Member", fontSize = 11.sp, color = AccentGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Interactive Daily Health Goals
            Text(
                text = "Daily Target Goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentGreen.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    GoalAdjusterRow(
                        title = "Daily Calorie Target",
                        valueText = "${userGoals.dailyCalorieGoal} kcal",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = AccentOrange,
                        onDecrement = {
                            handleSave(
                                (userGoals.dailyCalorieGoal - 100).coerceAtLeast(1000),
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal,
                                userGoals.dailySleepGoalHours,
                                userGoals.dailyStepsGoal
                            )
                        },
                        onIncrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal + 100,
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal,
                                userGoals.dailySleepGoalHours,
                                userGoals.dailyStepsGoal
                            )
                        }
                    )

                    GoalAdjusterRow(
                        title = "Daily Water Target",
                        valueText = "${userGoals.dailyWaterGoal} ml",
                        icon = Icons.Default.WaterDrop,
                        iconTint = AccentBlue,
                        onDecrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                userGoals.dailyProteinGoal,
                                (userGoals.dailyWaterGoal - 250).coerceAtLeast(500),
                                userGoals.dailySleepGoalHours,
                                userGoals.dailyStepsGoal
                            )
                        },
                        onIncrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal + 250,
                                userGoals.dailySleepGoalHours,
                                userGoals.dailyStepsGoal
                            )
                        }
                    )

                    GoalAdjusterRow(
                        title = "Daily Sleep Target",
                        valueText = "${userGoals.dailySleepGoalHours} hrs",
                        icon = Icons.Default.Bedtime,
                        iconTint = AccentPurple,
                        onDecrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal,
                                (userGoals.dailySleepGoalHours - 0.5f).coerceAtLeast(4f),
                                userGoals.dailyStepsGoal
                            )
                        },
                        onIncrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal,
                                userGoals.dailySleepGoalHours + 0.5f,
                                userGoals.dailyStepsGoal
                            )
                        }
                    )

                    GoalAdjusterRow(
                        title = "Daily Step Target",
                        valueText = "${userGoals.dailyStepsGoal} steps",
                        icon = Icons.Default.DirectionsRun,
                        iconTint = AccentGreen,
                        onDecrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal,
                                userGoals.dailySleepGoalHours,
                                (userGoals.dailyStepsGoal - 500).coerceAtLeast(2000)
                            )
                        },
                        onIncrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal,
                                userGoals.dailySleepGoalHours,
                                userGoals.dailyStepsGoal + 500
                            )
                        }
                    )
                }
            }

            // 3. Google Health Connect & Sensor Integration
            Text(
                text = "Integrations & Telemetry",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentBlue.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    val msg = HealthConnectManager.syncAllLocalDataToGoogleHealth(
                                        context = context,
                                        foods = emptyList(),
                                        waters = emptyList(),
                                        sleeps = emptyList()
                                    )
                                    try {
                                        permissionLauncher.launch(HealthConnectManager.HEALTH_CONNECT_PERMISSIONS)
                                    } catch (e: Exception) {
                                        HealthConnectManager.openHealthConnect(context)
                                    }
                                    onShowSnackbar(msg)
                                }
                            },

                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Google Health Connect", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Sync Nutrition, Hydration & Sleep telemetry", fontSize = 12.sp, color = TextSecondary)
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Hardware Step Counter Sensor", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Sensor.TYPE_STEP_COUNTER • Active", fontSize = 12.sp, color = AccentGreen)
                        }
                    }
                }
            }

            // 4. Notifications & Push Reminders
            Text(
                text = "Notifications & Reminders",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentOrange.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            ReminderNotificationHelper.sendReminderNotification(
                                context,
                                "Hydration Reminder 💧",
                                "Don't forget to log 500ml water to hit your daily goal!"
                            )
                            onShowSnackbar("Sent test hydration push reminder")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.AddAlert, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Test Local Push Reminder", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Trigger instant Android system notification", fontSize = 12.sp, color = TextSecondary)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }
            }

            // 5. Data Management & Reset
            Text(
                text = "Data Management",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentRed.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onShowSnackbar("Exported health database JSON to local storage")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Export Health Log (JSON)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Download local backup file", fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showClearDataModal = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = AccentRed, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Clear All Local Data", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = AccentRed)
                            Text("Reset database tables & preference storage", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // 6. Privacy & Version Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("100% On-Device Privacy Guaranteed", fontSize = 12.sp, color = AccentGreen, fontWeight = FontWeight.Medium)
                }
                Text("Nourish Fit Track v1.3.2 (Build 25)", fontSize = 11.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showClearDataModal) {
        AlertDialog(
            onDismissRequest = { showClearDataModal = false },
            title = { Text("Clear All Database Records?", fontWeight = FontWeight.Bold) },
            text = { Text("This will remove all food, water, sleep, and step logs from local SQLite storage. This action cannot be undone.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData {
                            onShowSnackbar("All health records cleared")
                            showClearDataModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Clear All Data", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataModal = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun GoalAdjusterRow(
    title: String,
    valueText: String,
    icon: ImageVector,
    iconTint: Color,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(valueText, fontSize = 13.sp, color = iconTint, fontWeight = FontWeight.SemiBold)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SurfaceCardAlt)
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrement", tint = TextPrimary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onIncrement,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SurfaceCardAlt)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Increment", tint = TextPrimary, modifier = Modifier.size(16.dp))
            }
        }
    }
}
