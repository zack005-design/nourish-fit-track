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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.frostedGlass
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentCyan
import com.fitnessapp.ui.theme.AppleSystemBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.AccentRed
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.BorderSubtle
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
    onBack: () -> Unit = {},
    onShowSnackbar: (String) -> Unit = {},
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            settingsRepository,
            foodRepository,
            waterRepository,
            sleepRepository
        )
    )
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val userGoals by viewModel.userGoals.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    var showClearDataModal by remember { mutableStateOf(false) }
    var showImportModal by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
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
            onShowSnackbar("Some Health Connect permissions were denied.")
        }
    }

    val handleSave = { cal: Int, prot: Float, water: Int, sleep: Float ->
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        viewModel.saveGoals(
            calorieGoal = cal,
            proteinGoal = prot,
            waterGoal = water,
            sleepGoalHours = sleep,
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
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = "Settings",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. User Profile Header Card
            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentCyan.copy(alpha = 0.35f),
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
                                    listOf(AccentCyan.copy(alpha = 0.45f), AccentOrange.copy(alpha = 0.45f))
                                )
                            )
                            .border(1.5.dp, AccentCyan, CircleShape),
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
                            text = "Nourish Fitness Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentCyan.copy(alpha = 0.15f))
                                .border(1.dp, AccentCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "On-Device Engine Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                        }
                    }
                }
            }

            // 2. Interactive Daily Target Goals
            SectionHeader(title = "Wellness Targets") {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentCyan.copy(alpha = 0.15f))
                        .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
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
                        color = AccentCyan
                    )
                }
            }

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = BorderSubtle,
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
                                userGoals.dailySleepGoalHours
                            )
                        },
                        onIncrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal + 100,
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal,
                                userGoals.dailySleepGoalHours
                            )
                        }
                    )

                    GoalAdjusterRow(
                        title = "Daily Protein Target",
                        valueText = "${userGoals.dailyProteinGoal.toInt()} g",
                        icon = Icons.Default.Egg,
                        iconTint = AccentGreen,
                        onDecrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                (userGoals.dailyProteinGoal - 5f).coerceAtLeast(40f),
                                userGoals.dailyWaterGoal,
                                userGoals.dailySleepGoalHours
                            )
                        },
                        onIncrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                userGoals.dailyProteinGoal + 5f,
                                userGoals.dailyWaterGoal,
                                userGoals.dailySleepGoalHours
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
                                userGoals.dailySleepGoalHours
                            )
                        },
                        onIncrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal + 250,
                                userGoals.dailySleepGoalHours
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
                                (userGoals.dailySleepGoalHours - 0.5f).coerceAtLeast(4f)
                            )
                        },
                        onIncrement = {
                            handleSave(
                                userGoals.dailyCalorieGoal,
                                userGoals.dailyProteinGoal,
                                userGoals.dailyWaterGoal,
                                userGoals.dailySleepGoalHours + 0.5f
                            )
                        }
                    )
                }
            }

            // 3. App Visual Theme Selector
            SectionHeader(title = "Appearance")

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = BorderSubtle,
                contentPadding = 16.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "App Visual Theme",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select your preferred color mode for dark obsidian or light views",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceCardAlt)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val modes = listOf("OBSIDIAN" to "Obsidian", "SYSTEM" to "System", "LIGHT" to "Light")
                        modes.forEach { (modeKey, label) ->
                            val isSelected = themeMode.equals(modeKey, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) AppleSystemBlue.copy(alpha = 0.2f) else Color.Transparent
                                    )
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) AppleSystemBlue.copy(alpha = 0.5f) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.setThemeMode(modeKey)
                                        onShowSnackbar("Applied $label theme")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AppleSystemBlue else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 4. Google Health Connect & Reminders
            SectionHeader(title = "Sync & Reminders")

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = BorderSubtle,
                contentPadding = 18.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SettingsNavigationRow(
                        title = "Google Health Connect",
                        subtitle = if (isHCAvailable) "Tap to grant permissions & sync health records" else "Health Connect app not installed",
                        icon = Icons.Default.Sync,
                        iconTint = AccentCyan,
                        statusDotColor = if (isHCAvailable) AccentCyan else Color(0xFF4A5568),
                        onClick = {
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
                        }
                    )

                    SettingsNavigationRow(
                        title = "Test Hydration Notification",
                        subtitle = "Trigger instant Android system push reminder",
                        icon = Icons.Default.AddAlert,
                        iconTint = AccentOrange,
                        onClick = {
                            ReminderNotificationHelper.sendReminderNotification(
                                context,
                                "Hydration Reminder 💧",
                                "Don't forget to log 500ml water to hit your daily goal!"
                            )
                            onShowSnackbar("Test hydration notification dispatched!")
                        }
                    )
                }
            }

            // 5. Data Privacy & Backup
            SectionHeader(title = "Data Management")

            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = BorderSubtle,
                contentPadding = 18.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SettingsNavigationRow(
                        title = "Export Health Backup (JSON)",
                        subtitle = "Export food, water, and sleep logs to local JSON file",
                        icon = Icons.Default.Download,
                        iconTint = AccentCyan,
                        onClick = {
                            viewModel.exportHealthData("json") { jsonStr ->
                                onShowSnackbar("Exported ${jsonStr.length} bytes to local backup")
                            }
                        }
                    )

                    SettingsNavigationRow(
                        title = "Import Health Backup (JSON)",
                        subtitle = "Restore database records from a JSON backup file",
                        icon = Icons.Default.FileUpload,
                        iconTint = AccentPurple,
                        onClick = {
                            showImportModal = true
                        }
                    )

                    SettingsNavigationRow(
                        title = "Clear All Local Data",
                        subtitle = "Reset database tables and local stored preferences",
                        icon = Icons.Default.DeleteForever,
                        iconTint = AccentRed,
                        titleColor = AccentRed,
                        onClick = {
                            showClearDataModal = true
                        }
                    )
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
                        tint = AccentCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "100% On-Device Privacy Guaranteed",
                        fontSize = 12.sp,
                        color = AccentCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    "Nourish Fit Track v1.5.7 (Build 7)",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Modal: Clear Data Confirmation
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
                    "This will permanently erase all food, water, and sleep logs from local storage. This action cannot be undone.",
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

    // Modal: Import Backup JSON
    if (showImportModal) {
        AlertDialog(
            onDismissRequest = { showImportModal = false },
            containerColor = SurfaceCardAlt,
            title = {
                Text(
                    "Import JSON Backup",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Paste your JSON backup payload below to restore health logs:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("{ \"foodLogs\": [...], \"waterLogs\": [...] }", color = TextSecondary.copy(alpha = 0.5f), fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importJsonText.isNotBlank()) {
                            viewModel.importHealthDataJson(importJsonText) { msg ->
                                onShowSnackbar(msg)
                                showImportModal = false
                                importJsonText = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                ) {
                    Text("Restore Backup", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportModal = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        action?.invoke()
    }
}

@Composable
private fun SettingsNavigationRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    titleColor: Color = TextPrimary,
    statusDotColor: Color? = null,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconTint.copy(alpha = 0.15f))
                .border(1.dp, iconTint.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                if (statusDotColor != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(statusDotColor)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
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
                    .border(1.dp, BorderSubtle, CircleShape)
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
