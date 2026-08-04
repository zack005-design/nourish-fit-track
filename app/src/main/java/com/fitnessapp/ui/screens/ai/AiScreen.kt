package com.fitnessapp.ui.screens.ai

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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiScreen(
    foodRepository: FoodRepository,
    waterRepository: WaterRepository,
    sleepRepository: SleepRepository,
    settingsRepository: SettingsRepository,
    stepsRepository: StepsRepository,
    onShowSnackbar: (String) -> Unit,
    viewModel: AnalyticsViewModel = viewModel(
        factory = AnalyticsViewModel.Factory(foodRepository, waterRepository, sleepRepository, settingsRepository, stepsRepository)
    )
) {
    val aiCoachState by viewModel.aiCoachState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // Camera AI Estimation Dialog State
    var showCameraDialog by remember { mutableStateOf(false) }
    var cameraEstName by remember { mutableStateOf("") }
    var cameraEstCalories by remember { mutableStateOf("") }
    var cameraEstProtein by remember { mutableStateOf("") }
    var cameraEstCarbs by remember { mutableStateOf("") }
    var cameraEstFat by remember { mutableStateOf("") }
    var cameraEstFiber by remember { mutableStateOf("") }

    // Interactive Topic Advice
    var activeTopic by remember { mutableStateOf<String?>(null) }
    val topicAdvice = remember(activeTopic, aiCoachState) {
        when (activeTopic) {
            "Meal Suggestions" -> {
                "To optimize your wellness score today (${aiCoachState.overallScore}/100), try a protein-rich meal: 200g Greek yogurt with nuts, 3 boiled egg whites, or 150g grilled chicken/paneer with green vegetables."
            }
            "Hydration Strategy" -> {
                "Your hydration score is currently ${aiCoachState.hydrationScore}/100. Drink 500ml of fresh water right now to boost cellular recovery."
            }
            "Sleep & Recovery" -> {
                "Your sleep score is ${aiCoachState.sleepScore}/100. Turn off screens 30 minutes before bedtime and keep your bedroom cool for optimal REM deep sleep."
            }
            "Workout Advice" -> {
                if (aiCoachState.overallScore >= 70) {
                    "Wellness score is strong (${aiCoachState.overallScore}/100)! Excellent day for a high-intensity workout or strength training session."
                } else {
                    "Wellness score is in recovery zone (${aiCoachState.overallScore}/100). Light yoga, stretching, or a 30-minute moderate walk is recommended today."
                }
            }
            else -> null
        }
    }

    if (showCameraDialog) {
        AlertDialog(
            onDismissRequest = { showCameraDialog = false },
            containerColor = SurfaceCardAlt,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Photo Estimate (Editable)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Review and edit the AI detected meal values before saving:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = cameraEstName,
                        onValueChange = { cameraEstName = it },
                        label = { Text("Dish Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGreen)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cameraEstCalories,
                            onValueChange = { cameraEstCalories = it },
                            label = { Text("Calories") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = cameraEstProtein,
                            onValueChange = { cameraEstProtein = it },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cameraEstCarbs,
                            onValueChange = { cameraEstCarbs = it },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = cameraEstFat,
                            onValueChange = { cameraEstFat = it },
                            label = { Text("Fat (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showCameraDialog = false
                        scope.launch {
                            foodRepository.insert(
                                FoodEntry(
                                    name = cameraEstName.ifBlank { "Scanned Meal" },
                                    calories = cameraEstCalories.toIntOrNull() ?: 450,
                                    proteinGrams = cameraEstProtein.toFloatOrNull() ?: 25f,
                                    carbsGrams = cameraEstCarbs.toFloatOrNull() ?: 40f,
                                    fatGrams = cameraEstFat.toFloatOrNull() ?: 15f,
                                    fiberGrams = cameraEstFiber.toFloatOrNull() ?: 5f,
                                    mealType = "Lunch",
                                    dateMillis = System.currentTimeMillis()
                                )
                            )
                            onShowSnackbar("${cameraEstName.ifBlank { "Meal" }} logged successfully!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Text("Save Meal", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCameraDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AccentGreen.copy(alpha = 0.2f))
                                .border(1.dp, AccentGreen.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Nourish AI Coach",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "On-Device Health Intelligence Engine",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
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
            Spacer(modifier = Modifier.height(4.dp))

            // ─── 1. CAMERA AI MEAL SCANNER BANNER ───────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentGreen.copy(alpha = 0.12f))
                    .border(1.dp, AccentGreen.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val est = CameraFoodEstimator.estimateMealFromPhoto(null)
                        cameraEstName = est.dishName
                        cameraEstCalories = est.calories.toString()
                        cameraEstProtein = est.proteinGrams.toInt().toString()
                        cameraEstCarbs = est.carbsGrams.toInt().toString()
                        cameraEstFat = est.fatGrams.toInt().toString()
                        cameraEstFiber = est.fiberGrams.toInt().toString()
                        showCameraDialog = true
                    }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AccentGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Snap Photo with AI Meal Scanner",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Instantly estimate calories & macros with on-device AI",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AccentGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // ─── 2. DAILY WELLNESS SCORE RING ──────────────────────────────────
            AppCard(contentPadding = 20.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DAILY WELLNESS SCORE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = aiCoachState.scoreLabel,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "On-Device Health Intelligence",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Box(contentAlignment = Alignment.Center) {
                        RingProgress(
                            progressFraction = aiCoachState.overallScore / 100f,
                            ringSize = 100.dp,
                            strokeWidth = 10.dp,
                            flatColor = AccentGreen
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${aiCoachState.overallScore}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "/ 100",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Yesterday Comparison Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCardAlt)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isPositive = aiCoachState.scoreDelta >= 0
                    Icon(
                        imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = if (isPositive) AccentGreen else AccentRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPositive) "+${aiCoachState.scoreDelta} pts vs yesterday" else "${aiCoachState.scoreDelta} pts vs yesterday",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) AccentGreen else AccentRed
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sub-Score Progress Bars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SubScoreBar("Nutrition", aiCoachState.nutritionScore, AccentOrange, Modifier.weight(1f))
                    SubScoreBar("Hydration", aiCoachState.hydrationScore, AccentBlue, Modifier.weight(1f))
                    SubScoreBar("Sleep", aiCoachState.sleepScore, AccentPurple, Modifier.weight(1f))
                    SubScoreBar("Activity", aiCoachState.activityScore, AccentGreen, Modifier.weight(1f))
                }
            }

            // ─── 3. INTERACTIVE QUICK TOPIC COACHING ────────────────────────────
            AppCard {
                Text(
                    text = "Interactive AI Coaching",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap a topic for instant tailored recommendations:",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Meal Suggestions", "Hydration Strategy", "Sleep & Recovery", "Workout Advice").forEach { topic ->
                        val isSelected = activeTopic == topic
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentGreen.copy(alpha = 0.2f) else SurfaceCardAlt)
                                .border(
                                    1.dp,
                                    if (isSelected) AccentGreen else Color(0xFF2C3242),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { activeTopic = if (isSelected) null else topic }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = topic,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) AccentGreen else TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (topicAdvice != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AccentGreen.copy(alpha = 0.1f))
                            .border(1.dp, AccentGreen.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = topicAdvice,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // ─── 4. CONTEXTUAL AI INSIGHT CARDS ────────────────────────────────
            if (aiCoachState.insightCards.isNotEmpty()) {
                Text(
                    text = "Contextual AI Insights (${aiCoachState.insightCards.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                aiCoachState.insightCards.forEach { card ->
                    AiInsightCardItem(card = card)
                }
            }

            // ─── 5. TODAY'S ACTION PLAN ─────────────────────────────────────────
            if (aiCoachState.actionPlan.isNotEmpty()) {
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AccentGreen,
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

                    Spacer(modifier = Modifier.height(12.dp))

                    aiCoachState.actionPlan.forEachIndexed { index, action ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(AccentGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGreen
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = action,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            // ─── 6. 7-DAY AI SUMMARY DIGEST ─────────────────────────────────────
            if (aiCoachState.weeklySummary.isNotEmpty()) {
                AppCard {
                    Text(
                        text = "7-Day AI Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = aiCoachState.weeklySummary,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SubScoreBar(
    label: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
            Text(text = "$score", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearBar(
            progressFraction = score / 100f,
            barColor = color,
            barHeight = 4.dp,
            showPercentageText = false
        )
    }
}

@Composable
private fun AiInsightCardItem(card: InsightCard) {
    val (borderColor, iconTint, badgeBg) = when (card.severity) {
        InsightSeverity.CRITICAL -> Triple(AccentRed, AccentRed, AccentRed.copy(alpha = 0.15f))
        InsightSeverity.WARNING -> Triple(AccentYellow, AccentYellow, AccentYellow.copy(alpha = 0.15f))
        InsightSeverity.POSITIVE -> Triple(AccentGreen, AccentGreen, AccentGreen.copy(alpha = 0.15f))
        InsightSeverity.INFO -> Triple(AccentBlue, AccentBlue, AccentBlue.copy(alpha = 0.15f))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = card.emoji,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = card.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = card.severity.name,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconTint
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = card.body,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
