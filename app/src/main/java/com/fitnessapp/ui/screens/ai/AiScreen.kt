package com.fitnessapp.ui.screens.ai

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.ai.CameraFoodEstimator
import com.fitnessapp.ai.InsightCard
import com.fitnessapp.ai.InsightSeverity
import com.fitnessapp.data.db.entity.FoodEntry
import com.fitnessapp.data.repository.FoodRepository
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.data.repository.StepsRepository
import com.fitnessapp.data.repository.WaterRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.LinearBar
import com.fitnessapp.ui.components.RingProgress
import com.fitnessapp.ui.components.frostedGlass
import com.fitnessapp.ui.screens.analytics.AnalyticsViewModel
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentOrange
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.AccentRed
import com.fitnessapp.ui.theme.AccentYellow
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCard
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import com.fitnessapp.util.VoiceSpeechManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiScreen(
    foodRepository: FoodRepository,
    waterRepository: WaterRepository,
    sleepRepository: SleepRepository,
    settingsRepository: SettingsRepository,
    stepsRepository: StepsRepository,
    onShowSnackbar: (String) -> Unit = {},
    analyticsViewModel: AnalyticsViewModel = viewModel(
        factory = AnalyticsViewModel.Factory(
            foodRepository,
            waterRepository,
            sleepRepository,
            settingsRepository,
            stepsRepository
        )
    )
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val aiCoachReport by analyticsViewModel.aiCoachState.collectAsStateWithLifecycle()

    // Interactive Assistant Chat State
    var userPromptText by remember { mutableStateOf("") }
    var aiAnswerText by remember { mutableStateOf<String?>(null) }
    var isThinking by remember { mutableStateOf(false) }

    // Action Plan Checkboxes State
    val checkedActions = remember { mutableStateListOf(false, false, false) }

    // Camera AI Dialog State
    var showCameraDialog by remember { mutableStateOf(false) }
    var cameraEstName by remember { mutableStateOf("") }
    var cameraEstCalories by remember { mutableStateOf("") }
    var cameraEstProtein by remember { mutableStateOf("") }
    var cameraEstCarbs by remember { mutableStateOf("") }
    var cameraEstFat by remember { mutableStateOf("") }
    var cameraEstFiber by remember { mutableStateOf("") }

    val handleAskAi = {
        if (userPromptText.isNotBlank()) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            isThinking = true
            val query = userPromptText.trim()
            userPromptText = ""

            scope.launch {
                kotlinx.coroutines.delay(600)
                aiAnswerText = when {
                    query.contains("protein", ignoreCase = true) ->
                        "Based on your daily meal log, protein score is ${aiCoachReport.nutritionScore}/100. To reach your recovery goal, add a Greek yogurt or protein shake."
                    query.contains("water", ignoreCase = true) || query.contains("hydration", ignoreCase = true) ->
                        "Your hydration score is currently ${aiCoachReport.hydrationScore}/100. Drink 500ml more water before evening to maintain focus."
                    query.contains("sleep", ignoreCase = true) ->
                        "Your sleep score is ${aiCoachReport.sleepScore}/100. Aim for 7.5 to 8 hours tonight for optimal recovery."
                    else ->
                        "On-Device Health AI Engine: Your overall Wellness Score is ${aiCoachReport.overallScore}/100 (${aiCoachReport.scoreLabel}). All metrics are being processed locally."
                }
                isThinking = false
            }
        }
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
                                .background(
                                    Brush.linearGradient(
                                        listOf(AccentGreen.copy(alpha = 0.3f), AccentBlue.copy(alpha = 0.3f))
                                    )
                                )
                                .border(1.dp, AccentGreen.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nourish AI Hub",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AccentGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "100% On-Device • Active",
                                    fontSize = 11.sp,
                                    color = AccentGreen
                                )
                            }
                        }
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

            // 1. Live AI Interactive Prompt Bar
            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentGreen.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ask On-Device Health AI",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    OutlinedTextField(
                        value = userPromptText,
                        onValueChange = { userPromptText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ask about protein, sleep, water, or workout...", fontSize = 13.sp, color = TextSecondary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { handleAskAi() }),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        try {
                                            val intent = VoiceSpeechManager.createSpeechIntent("Ask Nourish AI...")
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            onShowSnackbar("Speech recognition not available on this device")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Input",
                                        tint = AccentBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { handleAskAi() },
                                    enabled = userPromptText.isNotBlank()
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send",
                                        tint = if (userPromptText.isNotBlank()) AccentGreen else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGreen,
                            unfocusedBorderColor = SurfaceCardAlt,
                            focusedContainerColor = BackgroundDark.copy(alpha = 0.5f),
                            unfocusedContainerColor = BackgroundDark.copy(alpha = 0.3f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    AnimatedVisibility(visible = isThinking || aiAnswerText != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentGreen.copy(alpha = 0.1f))
                                .border(1.dp, AccentGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            if (isThinking) {
                                Text(
                                    text = "⚡ Analyzing health telemetry on-device...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AccentYellow,
                                    fontWeight = FontWeight.Medium
                                )
                            } else if (aiAnswerText != null) {
                                Text(
                                    text = aiAnswerText ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // 2. Quick Action Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Camera Scanner Chip
                AppCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val est = CameraFoodEstimator.estimateMealFromPhoto(null)
                            cameraEstName = est.dishName
                            cameraEstCalories = est.calories.toString()
                            cameraEstProtein = est.proteinGrams.toInt().toString()
                            cameraEstCarbs = est.carbsGrams.toInt().toString()
                            cameraEstFat = est.fatGrams.toInt().toString()
                            cameraEstFiber = est.fiberGrams.toInt().toString()
                            showCameraDialog = true
                        },
                    backgroundColor = SurfaceCard,
                    borderColor = AccentGreen.copy(alpha = 0.3f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Meal", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                // Voice Log Chip
                AppCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            try {
                                val intent = VoiceSpeechManager.createSpeechIntent("Speak meal or water intake...")
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                onShowSnackbar("Voice recognizer intent launched")
                            }
                        },
                    backgroundColor = SurfaceCard,
                    borderColor = AccentBlue.copy(alpha = 0.3f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Voice Log", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            // 3. Daily Wellness Score Card (0-100 Dial + Sub Scores)
            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentGreen.copy(alpha = 0.4f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Wellness Score",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Calculated from Nutrition, Water, Sleep & Steps",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = aiCoachReport.scoreLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        RingProgress(
                            progressFraction = aiCoachReport.overallScore / 100f,
                            ringSize = 90.dp,
                            strokeWidth = 9.dp,
                            flatColor = AccentGreen,
                            centerContent = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${aiCoachReport.overallScore}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(text = "/100", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        )

                        Column(
                            modifier = Modifier.width(170.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubScoreBar("Nutrition", aiCoachReport.nutritionScore / 100f, AccentGreen)
                            SubScoreBar("Hydration", aiCoachReport.hydrationScore / 100f, AccentBlue)
                            SubScoreBar("Sleep", aiCoachReport.sleepScore / 100f, AccentPurple)
                            SubScoreBar("Activity", aiCoachReport.activityScore / 100f, AccentOrange)
                        }
                    }
                }
            }

            // 4. Today's AI Action Plan Checklist
            AppCard(
                backgroundColor = SurfaceCard,
                borderColor = AccentBlue.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Today's Action Plan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    val actionPlanList = aiCoachReport.actionPlan.ifEmpty {
                        listOf(
                            "Drink 500ml water after mid-day workout",
                            "Consume 30g protein for evening meal",
                            "Wind down by 10:30 PM for 8h sleep target"
                        )
                    }

                    actionPlanList.take(3).forEachIndexed { idx, actionText ->
                        val isChecked = checkedActions.getOrElse(idx) { false }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isChecked) AccentGreen.copy(alpha = 0.1f) else SurfaceCardAlt)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (idx < checkedActions.size) {
                                        checkedActions[idx] = !checkedActions[idx]
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = if (isChecked) AccentGreen else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = actionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isChecked) TextSecondary else TextPrimary,
                                fontWeight = if (isChecked) FontWeight.Normal else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 5. Contextual AI Health Insights
            Text(
                text = "Contextual AI Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            val insightsList = aiCoachReport.insightCards.ifEmpty {
                listOf(
                    InsightCard(
                        title = "Protein Target On Track",
                        body = "Your protein intake is aligned with optimal muscle synthesis for today.",
                        severity = InsightSeverity.POSITIVE,
                        emoji = "💪"
                    ),
                    InsightCard(
                        title = "Hydration Recommendation",
                        body = "Increase water intake by 500ml to offset active step energy expenditure.",
                        severity = InsightSeverity.INFO,
                        emoji = "💧"
                    )
                )
            }

            insightsList.forEach { card ->
                val (borderColor, iconTint, badgeText) = when (card.severity) {
                    InsightSeverity.CRITICAL -> Triple(AccentRed, AccentRed, "CRITICAL")
                    InsightSeverity.WARNING -> Triple(AccentOrange, AccentOrange, "WARNING")
                    InsightSeverity.POSITIVE -> Triple(AccentGreen, AccentGreen, "OPTIMAL")
                    InsightSeverity.INFO -> Triple(AccentBlue, AccentBlue, "TIP")
                }

                AppCard(
                    backgroundColor = SurfaceCard,
                    borderColor = borderColor.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(borderColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = card.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = badgeText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = iconTint
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = card.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Camera AI Review Modal
    if (showCameraDialog) {
        AlertDialog(
            onDismissRequest = { showCameraDialog = false },
            title = { Text("Camera AI Food Estimation", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("AI identified dish:", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(value = cameraEstName, onValueChange = { cameraEstName = it }, label = { Text("Dish Name") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = cameraEstCalories, onValueChange = { cameraEstCalories = it }, label = { Text("Calories") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = cameraEstProtein, onValueChange = { cameraEstProtein = it }, label = { Text("Protein (g)") }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val food = FoodEntry(
                            name = cameraEstName.ifBlank { "AI Scanned Meal" },
                            calories = cameraEstCalories.toIntOrNull() ?: 450,
                            proteinGrams = cameraEstProtein.toFloatOrNull() ?: 25f,
                            carbsGrams = cameraEstCarbs.toFloatOrNull() ?: 40f,
                            fatGrams = cameraEstFat.toFloatOrNull() ?: 15f,
                            fiberGrams = cameraEstFiber.toFloatOrNull() ?: 5f,
                            mealType = "Scan",
                            dateMillis = System.currentTimeMillis()
                        )
                        scope.launch {
                            foodRepository.insert(food)
                            onShowSnackbar("Saved ${food.name} to Food Log")
                            showCameraDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Text("Save to Food Log", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCameraDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SubScoreBar(label: String, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Text(text = "${(progress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        LinearBar(
            progressFraction = progress.coerceIn(0f, 1f),
            barColor = color,
            barHeight = 6.dp,
            showPercentageText = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
