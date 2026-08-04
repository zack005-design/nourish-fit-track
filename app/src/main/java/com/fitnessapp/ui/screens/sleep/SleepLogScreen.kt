package com.fitnessapp.ui.screens.sleep

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
                val start = endState.startTimeMillis
                val end = System.currentTimeMillis()
                val elapsedMinutes = ((end - start) / (1000 * 60)).coerceAtLeast(1).toInt()
                val qualityScore = when {
                    endState.motionEventsCount > 20 -> 2
                    endState.motionEventsCount > 10 -> 3
                    endState.motionEventsCount > 4 -> 4
                    else -> 5
                }
                val notesStr = "Xiaomi 17T sensor track · ${endState.motionEventsCount} motion events · ${String.format(Locale.US, "%.1f", endState.ambientLux)} lux"

                scope.launch {
                    val entry = SleepEntry(
                        startMillis = start,
                        endMillis = end,
                        quality = qualityScore,
                        notes = notesStr,
                        dateMillis = DateUtils.todayStartMillis()
                    )
                    sleepRepository.insert(entry)
                    onShowSnackbar("Xiaomi 17T sleep session saved successfully!")
                }
            },
            onDismiss = {
                sensorTracker.stopTracking()
                showSensorLiveSheet = false
            }
        )
    }
}

// ─── LIVE SENSOR SLEEP TRACKING SHEET (XIAOMI 17T NIGHT MODE) ─────────────────
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCardAlt,
        scrimColor = Color.Black.copy(alpha = 0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hardware Sensor Night Mode",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timer Digital Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BackgroundDark)
                    .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LIVE SLEEP DURATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = timerFormatted,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Sensor Telemetry Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Motion Status Tile
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard.copy(alpha = 0.6f))
                        .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "MOTION SENSOR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sensorState.motionStatusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${sensorState.motionEventsCount} micro-events",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Light Level Sensor Tile
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard.copy(alpha = 0.6f))
                        .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "ROOM LIGHT SENSOR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (sensorState.isDarkEnvironment) "Dark Bedroom" else "Room Light On",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sensorState.isDarkEnvironment) AccentBlue else AccentOrange
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.1f", sensorState.ambientLux)} lux",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val finalState = sensorTracker.stopTracking()
                    onStopAndSave(finalState)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = null,
                    tint = BackgroundDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Wake Up & Save Sleep Session",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BackgroundDark
                )
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
