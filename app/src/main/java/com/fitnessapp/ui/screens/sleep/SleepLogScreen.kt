package com.fitnessapp.ui.screens.sleep

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold



import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.repository.SettingsRepository
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.ui.components.AppCard
import com.fitnessapp.ui.components.LinearBar
import com.fitnessapp.ui.components.RingProgress
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
import com.fitnessapp.ui.theme.TextTertiary
import com.fitnessapp.util.DateUtils
import com.fitnessapp.util.SensorSleepTracker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepLogScreen(
    sleepRepository: SleepRepository,
    settingsRepository: SettingsRepository,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: SleepViewModel = viewModel(
        factory = SleepViewModel.Factory(sleepRepository, settingsRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var entryToDelete by remember { mutableStateOf<SleepEntry?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Xiaomi 17T Hardware Sensor Sleep Tracker instance
    val sensorTracker = remember { SensorSleepTracker(context) }
    val sensorState by sensorTracker.state.collectAsStateWithLifecycle()

    var showSensorLiveSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val formattedDate = remember(uiState.selectedDateMillis) {
        SimpleDateFormat("EEEE, d MMMM", Locale.US).format(Date(uiState.selectedDateMillis))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.setSelectedDate(it) }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = AccentBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            containerColor = SurfaceCardAlt,
            title = { Text("Delete Sleep Record?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This sleep session will be permanently deleted.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        val target = entryToDelete!!
                        entryToDelete = null
                        viewModel.deleteEntry(target) {
                            onShowSnackbar("Sleep session deleted")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Delete", color = BackgroundDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
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
                    Column(modifier = Modifier.clickable { showDatePicker = true }) {
                        Text(
                            text = "Sleep",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 11.sp,
                            color = TextSecondary
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

            // ─── 0. WEEKLY SLEEP BAR CHART ─────────────────────────────────────────────
            if (uiState.weeklySleepDays.isNotEmpty()) {
                AppCard(contentPadding = 16.dp) {
                    Text(
                        text = "7-Day Sleep",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Target: ${uiState.sleepTargetHours.toInt()}h per night",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val maxHours = maxOf(uiState.sleepTargetHours, uiState.weeklySleepDays.maxOfOrNull { it.hours } ?: 1f, 1f)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        uiState.weeklySleepDays.forEach { day ->
                            val fraction = (day.hours / maxHours).coerceIn(0f, 1f)
                            val barColor = if (day.isToday) AccentPurple else AccentPurple.copy(alpha = 0.45f)
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // hour label
                                if (day.hours > 0f) {
                                    Text(
                                        text = "${day.hours.toInt()}h",
                                        fontSize = 9.sp,
                                        color = if (day.isToday) AccentPurple else TextSecondary,
                                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                // bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.55f)
                                            .fillMaxHeight(fraction.coerceAtLeast(0.05f))
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(barColor)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day.dayLabel,
                                    fontSize = 10.sp,
                                    color = if (day.isToday) AccentPurple else TextSecondary,
                                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // ─── 1. HERO SLEEP CARD (Dynamic according to logged status) ────────────────
            val isSleepLogged = uiState.entries.isNotEmpty()


            if (isSleepLogged) {
                val hoursInt = uiState.totalSleepHours.toInt()
                val minsInt = ((uiState.totalSleepHours - hoursInt) * 60).toInt()
                val targetHours = uiState.sleepTargetHours
                val progress = if (targetHours > 0f) (uiState.totalSleepHours / targetHours).coerceIn(0f, 1f) else 0f

                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NightlightRound,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TIME ASLEEP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${hoursInt}h ${minsInt}m",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${uiState.bedTimeText} — ${uiState.wakeTimeText}",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }

                        RingProgress(
                            progressFraction = uiState.sleepScore / 100f,
                            ringSize = 88.dp,
                            strokeWidth = 9.dp,
                            flatColor = when {
                                uiState.sleepScore >= 80 -> AccentGreen
                                uiState.sleepScore >= 60 -> AccentBlue
                                else -> AccentOrange
                            }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${uiState.sleepScore}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "SCORE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearBar(
                        progressFraction = progress,
                        barColor = AccentBlue,
                        barHeight = 6.dp,
                        showPercentageText = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.sleepStatusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                uiState.sleepScore >= 80 -> AccentGreen
                                uiState.sleepScore >= 60 -> AccentBlue
                                else -> AccentOrange
                            }
                        )
                        Text(
                            text = "Goal: ${targetHours.toInt()}h (${(progress * 100).toInt()}% achieved)",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                // Empty State Banner: Clean Action Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceCardAlt)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(AccentPurple.copy(alpha = 0.5f), Color(0xFF2C3242))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(AccentPurple.copy(alpha = 0.2f))
                                .border(1.dp, AccentPurple.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = AccentPurple,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "No Sleep Logged Today",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Log manually or use Xiaomi 17T hardware sensors for automatic overnight sleep tracking.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onAddClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                        ) {
                            Text(
                                text = "Log Sleep Session Manually",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // ─── 2. XIAOMI 17T SENSOR SLEEP TRACKER CARD ───────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceCardAlt)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(AccentBlue.copy(alpha = 0.4f), Color(0xFF2C3242))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AccentBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Sensor Sleep Tracker",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Motion & Ambient Light Detection",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "READY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Place your phone on your mattress. Accelerometer and ambient light sensors automatically detect restlessness, sleep duration, and bedroom darkness.",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            com.fitnessapp.service.SleepTrackingService.startService(context)
                            sensorTracker.startTracking()
                            showSensorLiveSheet = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = BackgroundDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start Sensor Tracking",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BackgroundDark
                        )
                    }
                }
            }

            // ─── 3. WEEKLY SLEEP BAR CHART (Always Visible) ─────────────────────────
            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SLEEP HISTORY (PAST 7 DAYS)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = "Target: ${uiState.sleepTargetHours.toInt()}h",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    uiState.weeklySleepDays.forEach { day ->
                        val maxBarHours = 12f
                        val heightFraction = (day.hours / maxBarHours).coerceIn(0.05f, 1f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.width(32.dp)
                        ) {
                            if (day.hours > 0f) {
                                Text(
                                    text = String.format(Locale.US, "%.1fh", day.hours),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (day.isToday) AccentBlue else TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .fillMaxHeight(heightFraction)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(
                                        when {
                                            day.isToday && day.hours > 0 -> AccentBlue
                                            day.hours > 0 -> AccentBlue.copy(alpha = 0.45f)
                                            else -> SurfaceCardAlt
                                        }
                                    )
                                    .border(
                                        width = if (day.isToday) 1.5.dp else 0.5.dp,
                                        color = if (day.isToday) AccentBlue else Color(0xFF2C3242),
                                        shape = RoundedCornerShape(9.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = day.dayLabel,
                                fontSize = 11.sp,
                                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (day.isToday) AccentBlue else TextSecondary
                            )
                        }
                    }
                }
            }

            // ─── 4. SLEEP STAGES BREAKDOWN ──────────────────────────────────────────
            AnimatedVisibility(visible = isSleepLogged) {
                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sleep Stages",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "Estimated",
                            fontSize = 11.sp,
                            color = TextTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    ) {
                        val w = size.width
                        val h = size.height

                        val path = Path()
                        path.moveTo(0f, h * 0.2f)
                        path.lineTo(w * 0.12f, h * 0.2f)
                        path.lineTo(w * 0.18f, h * 0.85f)
                        path.lineTo(w * 0.35f, h * 0.85f)
                        path.lineTo(w * 0.40f, h * 0.50f)
                        path.lineTo(w * 0.55f, h * 0.50f)
                        path.lineTo(w * 0.60f, h * 0.15f)
                        path.lineTo(w * 0.70f, h * 0.50f)
                        path.lineTo(w * 0.82f, h * 0.50f)
                        path.lineTo(w * 0.88f, h * 0.85f)
                        path.lineTo(w, h * 0.30f)

                        val fillPath = Path()
                        fillPath.addPath(path)
                        fillPath.lineTo(w, h)
                        fillPath.lineTo(0f, h)
                        fillPath.close()

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(AccentBlue.copy(alpha = 0.35f), AccentPurple.copy(alpha = 0.08f)),
                                startY = 0f,
                                endY = h
                            )
                        )

                        drawPath(
                            path = path,
                            color = AccentBlue,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val awakeDurationText = "${uiState.awakeMinutes}m"
                    val remHours = uiState.remMinutes / 60
                    val remMins = uiState.remMinutes % 60
                    val remDurationText = if (remHours > 0) "${remHours}h ${remMins}m" else "${remMins}m"

                    val lightHours = uiState.lightMinutes / 60
                    val lightMins = uiState.lightMinutes % 60
                    val lightDurationText = if (lightHours > 0) "${lightHours}h ${lightMins}m" else "${lightMins}m"

                    val deepHours = uiState.deepMinutes / 60
                    val deepMins = uiState.deepMinutes % 60
                    val deepDurationText = if (deepHours > 0) "${deepHours}h ${deepMins}m" else "${deepMins}m"

                    SleepStageRow("Awake", awakeDurationText, "${uiState.awakePercentage}%", AccentOrange)
                    Spacer(modifier = Modifier.height(8.dp))
                    SleepStageRow("REM", remDurationText, "${uiState.remPercentage}%", AccentPurple)
                    Spacer(modifier = Modifier.height(8.dp))
                    SleepStageRow("Light", lightDurationText, "${uiState.lightPercentage}%", AccentBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    SleepStageRow("Deep", deepDurationText, "${uiState.deepPercentage}%", Color(0xFF34495E))
                }
            }

            // ─── 5. LOGGED SESSIONS HISTORY LIST ───────────────────────────────────
            AnimatedVisibility(visible = isSleepLogged) {
                AppCard {
                    Text(
                        text = "Logged Sessions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.entries.forEach { entry ->
                        val durationMins = ((entry.endMillis - entry.startMillis) / (1000 * 60)).toInt()
                        val h = durationMins / 60
                        val m = durationMins % 60
                        val timeFmt = SimpleDateFormat("h:mm a", Locale.US)
                        val startStr = timeFmt.format(Date(entry.startMillis))
                        val endStr = timeFmt.format(Date(entry.endMillis))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceCardAlt)
                                .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(14.dp))
                                .clickable { onEditClick(entry.id) }
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(AccentBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "${h}h ${m}m duration",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "$startStr — $endStr",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            IconButton(onClick = { entryToDelete = entry }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete entry",
                                    tint = AccentRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ─── LIVE SENSOR SLEEP TRACKING MODAL OVERLAY ─────────────────────────
    if (showSensorLiveSheet) {
        LiveSensorSleepSheet(
            sheetState = sheetState,
            sensorTracker = sensorTracker,
            sensorState = sensorState,
            onStopAndSave = { endState ->
                showSensorLiveSheet = false
                sensorTracker.stopTracking()
                com.fitnessapp.service.SleepTrackingService.stopAndSaveService(context, sleepRepository) { savedEntry ->
                    onShowSnackbar("Overnight sleep session saved & synced to Google Health!")
                }
            },
            onDismiss = {
                sensorTracker.stopTracking()
                val stopIntent = android.content.Intent(context, com.fitnessapp.service.SleepTrackingService::class.java).apply {
                    action = com.fitnessapp.service.SleepTrackingService.ACTION_STOP_SLEEP_TRACKING
                }
                context.startService(stopIntent)
                showSensorLiveSheet = false
            }
        )
    }
}

// ─── LIVE SENSOR SLEEP TRACKING SHEET (PREMIUM MIDNIGHT NIGHT MODE) ─────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LiveSensorSleepSheet(
    sheetState: SheetState,
    sensorTracker: SensorSleepTracker,
    sensorState: com.fitnessapp.util.SensorSleepState,
    onStopAndSave: (com.fitnessapp.util.SensorSleepState) -> Unit,
    onDismiss: () -> Unit
) {
    var timerSeconds by remember { mutableLongStateOf(0L) }

    LaunchedEffect(sensorState.isTracking) {
        while (sensorState.isTracking) {
            delay(1000L)
            timerSeconds++
            sensorTracker.updateElapsedSeconds(timerSeconds)
        }
    }

    val hours = timerSeconds / 3600
    val mins = (timerSeconds % 3600) / 60
    val secs = timerSeconds % 60
    val timerFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs)

    // Breathing pulse animation for midnight sleep tracking
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "pulse_transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(1800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0D111A),
        scrimColor = Color.Black.copy(alpha = 0.85f),
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AccentPurple.copy(alpha = 0.2f))
                        .border(1.dp, AccentPurple.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Hardware Sensor Night Mode",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Glowing Breathing Timer Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF161B28),
                                BackgroundDark
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AccentPurple.copy(alpha = pulseAlpha),
                                AccentBlue.copy(alpha = pulseAlpha * 0.7f),
                                Color(0xFF2C3242)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AccentPurple.copy(alpha = pulseAlpha + 0.3f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE SLEEP DURATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentPurple,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = timerFormatted,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Sensors actively monitoring resting state",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-Time Hardware Telemetry Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Motion Sensor Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCardAlt)
                        .border(1.dp, Color(0xFF252D3D), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MOTION",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextTertiary,
                                letterSpacing = 0.8.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AccentGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = sensorState.motionStatusText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${sensorState.motionEventsCount} micro-events",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Room Light Sensor Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceCardAlt)
                        .border(1.dp, Color(0xFF252D3D), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ROOM LIGHT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextTertiary,
                                letterSpacing = 0.8.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (sensorState.isDarkEnvironment) AccentBlue.copy(alpha = 0.15f) else AccentOrange.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (sensorState.isDarkEnvironment) "DARK" else "LIGHT",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sensorState.isDarkEnvironment) AccentBlue else AccentOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (sensorState.isDarkEnvironment) "Dark Bedroom" else "Room Light On",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sensorState.isDarkEnvironment) AccentBlue else AccentOrange
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${String.format(Locale.US, "%.1f", sensorState.ambientLux)} lux",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Soothing Midnight Action Button
            Button(
                onClick = {
                    val finalState = sensorTracker.stopTracking()
                    onStopAndSave(finalState)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(27.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6B2FD9),
                                    Color(0xFF8B42F6)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Wake Up & Save Sleep Session",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SleepStageRow(
    label: String,
    durationText: String,
    percentageText: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = durationText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = percentageText,
                fontSize = 12.sp,
                color = TextTertiary,
                modifier = Modifier.width(36.dp)
            )
        }
    }
}
