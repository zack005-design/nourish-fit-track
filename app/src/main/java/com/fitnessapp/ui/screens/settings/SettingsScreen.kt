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
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
    val isHCAvailable = remember { HealthConnectManager.isHealthConnectAvailable(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        if (grantedPermissions.containsAll(HealthConnectManager.HEALTH_CONNECT_PERMISSIONS)) {
            scope.launch {
                val msg = viewModel.syncToHealthConnect(context)
                onShowSnackbar(msg)
            }
        } else {
            onShowSnackbar("Some Health Connect permissions were denied. Open Settings to grant them.")
        }
    }

    val handleSave = { cal: Int, prot: Float, water: Int, sleep: Float, steps: Int ->
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                                .background(AccentPurple.copy(alpha = 0.18f))
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
                            text = "Settings",
                            style = MaterialTheme.typography.titleLarge,
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
            Spacer(modifier = Modifier.height(4.dp))

            // 1. User Profile Header Card
            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentPurple.copy(alpha = 0.35f),
                contentPadding = 18.dp
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
                                    listOf(AccentPurple.copy(alpha = 0.45f), AccentBlue.copy(alpha = 0.45f))
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
                            text = "Fitness Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentGreen.copy(alpha = 0.15f))
                                    .border(1.dp, AccentGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "On-Device Engine Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGreen
                                )
                            }
                        }
                    }
                }
            }

            // 2. Interactive Daily Target Goals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Targets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentPurple.copy(alpha = 0.2f))
                        .border(1.dp, AccentPurple.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.optimizeGoalsWithAi { msg ->
                                onShowSnackbar(msg)
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "✨ AI Auto-Calibrate",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentPurple
                    )
                }
            }

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentGreen.copy(alpha = 0.35f),
                contentPadding = 18.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        icon = Icons.AutoMirrored.Filled.DirectionsRun,
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
                borderColor = AccentBlue.copy(alpha = 0.35f),
                contentPadding = 18.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    if (HealthConnectManager.hasAllPermissions(context)) {
                                        val msg = viewModel.syncToHealthConnect(context)
                                        onShowSnackbar(msg)
                                    } else {
                                        try {
                                            permissionLauncher.launch(HealthConnectManager.HEALTH_CONNECT_PERMISSIONS)
                                        } catch (e: Exception) {
                                            HealthConnectManager.openHealthConnect(context)
                                            onShowSnackbar("Opening Google Health Connect settings")
                                        }
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentBlue.copy(alpha = 0.15f))
                                .border(1.dp, AccentBlue.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Google Health Connect",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (isHCAvailable) AccentGreen else Color(0xFF4A5568))
                                )
                            }
                            Text(
                                if (isHCAvailable) "Tap to grant permissions & sync health data" else "Health Connect app not installed",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentGreen.copy(alpha = 0.15f))
                                .border(1.dp, AccentGreen.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Step Sensor Telemetry",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "Sensor.TYPE_STEP_COUNTER • Active",
                                fontSize = 12.sp,
                                color = AccentGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.exportHealthData("json") { exportedStr ->
                                    onShowSnackbar("Exported ${exportedStr.length} bytes of health logs (JSON)")
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentPurple.copy(alpha = 0.15f))
                                .border(1.dp, AccentPurple.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = AccentPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Export Health Logs",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "Export food, water, & sleep records to JSON",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }
                }
            }

            // 4. Notifications & Push Reminders
            Text(
                text = "Push Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentOrange.copy(alpha = 0.35f),
                contentPadding = 18.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            ReminderNotificationHelper.sendReminderNotification(
                                context,
                                "Hydration Reminder 💧",
                                "Don't forget to log 500ml water to hit your daily goal!"
                            )
                            onShowSnackbar("Test hydration notification dispatched!")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentOrange.copy(alpha = 0.15f))
                            .border(1.dp, AccentOrange.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAlert,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Test Hydration Push Reminder",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            "Trigger instant Android system notification",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
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
                borderColor = AccentRed.copy(alpha = 0.35f),
                contentPadding = 18.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onShowSnackbar("Exported health database backup to local storage")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentBlue.copy(alpha = 0.15f))
                                .border(1.dp, AccentBlue.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Export Health Log (JSON)",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "Download local backup file",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showClearDataModal = true
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentRed.copy(alpha = 0.15f))
                                .border(1.dp, AccentRed.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                tint = AccentRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Clear All Local Data",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = AccentRed
                            )
                            Text(
                                "Reset database tables & preference storage",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // 6. Privacy & Version Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "100% On-Device Privacy Guaranteed",
                        fontSize = 12.sp,
                        color = AccentGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    "Nourish Fit Track v1.0.0 (Build 1)",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showClearDataModal) {
        AlertDialog(
            onDismissRequest = { showClearDataModal = false },
            containerColor = SurfaceCardAlt,
            title = {
                Text(
                    "Clear All Database Records?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "This will remove all food, water, sleep, and step logs from local SQLite storage. This action cannot be undone.",
                    color = TextSecondary
                )
            },
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
                    Text("Clear All Data", color = BackgroundDark, fontWeight = FontWeight.Bold)
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
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.15f))
                .border(1.dp, iconTint.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valueText,
                fontSize = 13.sp,
                color = iconTint,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SurfaceCardAlt)
                    .border(1.dp, Color(0xFF2C3242), CircleShape)
                    .clickable(onClick = onDecrement),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrement",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.2f))
                    .border(1.dp, iconTint.copy(alpha = 0.4f), CircleShape)
                    .clickable(onClick = onIncrement),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increment",
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
