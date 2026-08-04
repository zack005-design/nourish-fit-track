package com.fitnessapp.ui.screens.sleep

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.ui.components.frostedGlass
import com.fitnessapp.ui.theme.AccentBlue
import com.fitnessapp.ui.theme.AccentGreen
import com.fitnessapp.ui.theme.AccentPurple
import com.fitnessapp.ui.theme.BackgroundDark
import com.fitnessapp.ui.theme.SurfaceCard
import com.fitnessapp.ui.theme.SurfaceCardAlt
import com.fitnessapp.ui.theme.TextPrimary
import com.fitnessapp.ui.theme.TextSecondary
import com.fitnessapp.ui.theme.TextTertiary
import com.fitnessapp.util.DateUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

private val qualityLabels = listOf("Poor", "Fair", "Restful", "Optimal", "Excellent")

enum class TimeTarget { BEDTIME, WAKE_TIME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSleepScreen(
    sleepRepository: SleepRepository,
    entryId: Long? = null,
    onBack: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    var quality by remember { mutableIntStateOf(4) } // 1-5, default 4 (Optimal)
    var notes by remember { mutableStateOf("") }
    var startHour by remember { mutableIntStateOf(22) } // 24-hr format
    var startMinute by remember { mutableIntStateOf(30) }
    var endHour by remember { mutableIntStateOf(7) } // 24-hr format
    var endMinute by remember { mutableIntStateOf(0) }
    var existingDateMillis by remember { mutableStateOf(DateUtils.todayStartMillis()) }

    var activeTimeTarget by remember { mutableStateOf<TimeTarget?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scope = rememberCoroutineScope()

    LaunchedEffect(entryId) {
        if (entryId != null && entryId > 0) {
            val entry = sleepRepository.getEntryById(entryId).firstOrNull()
            if (entry != null) {
                quality = entry.quality.coerceIn(1, 5)
                notes = entry.notes
                val startCal = Calendar.getInstance().apply { timeInMillis = entry.startMillis }
                val endCal = Calendar.getInstance().apply { timeInMillis = entry.endMillis }
                startHour = startCal.get(Calendar.HOUR_OF_DAY)
                startMinute = startCal.get(Calendar.MINUTE)
                endHour = endCal.get(Calendar.HOUR_OF_DAY)
                endMinute = endCal.get(Calendar.MINUTE)
                existingDateMillis = entry.dateMillis
            }
        }
    }

    val bedCal = Calendar.getInstance().apply {
        timeInMillis = existingDateMillis
        set(Calendar.HOUR_OF_DAY, startHour)
        set(Calendar.MINUTE, startMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val wakeCal = Calendar.getInstance().apply {
        timeInMillis = existingDateMillis
        set(Calendar.HOUR_OF_DAY, endHour)
        set(Calendar.MINUTE, endMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(bedCal) || equals(bedCal)) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    val computedMinutes = ((wakeCal.timeInMillis - bedCal.timeInMillis) / (1000 * 60)).toInt().coerceAtLeast(0)
    val computedHours = computedMinutes / 60
    val computedMins = computedMinutes % 60

    val bedTimeFormatted = format12Hour(startHour, startMinute)
    val wakeTimeFormatted = format12Hour(endHour, endMinute)

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                modifier = Modifier.frostedGlass(
                    backgroundColor = BackgroundDark.copy(alpha = 0.85f),
                    fallbackColor = BackgroundDark
                ),
                title = {
                    Text(
                        text = if (entryId != null && entryId > 0) "Edit Sleep Session" else "Log Sleep Session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ─── 1. HERO SLEEP DURATION BANNER ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceCardAlt)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(AccentBlue.copy(alpha = 0.5f), Color(0xFF2C3242))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightlightRound,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TOTAL SLEEP DURATION",
                            fontSize = 11.sp,
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${computedHours}h ${computedMins}m",
                        fontSize = 46.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$bedTimeFormatted → $wakeTimeFormatted",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // ─── 2. TIME SELECTION CARDS (Tap to open Phone Clock Wheel) ───────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomTimeTile(
                    modifier = Modifier.weight(1f),
                    label = "BEDTIME",
                    timeText = bedTimeFormatted,
                    accentColor = AccentBlue,
                    icon = Icons.Default.NightlightRound,
                    onClick = { activeTimeTarget = TimeTarget.BEDTIME }
                )

                CustomTimeTile(
                    modifier = Modifier.weight(1f),
                    label = "WAKE TIME",
                    timeText = wakeTimeFormatted,
                    accentColor = AccentGreen,
                    icon = Icons.Default.WbSunny,
                    onClick = { activeTimeTarget = TimeTarget.WAKE_TIME }
                )
            }

            // ─── 3. SEGMENTED SLEEP QUALITY CONTROL ──────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceCardAlt)
                    .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sleep Quality",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = qualityLabels[quality - 1],
                        fontSize = 13.sp,
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard.copy(alpha = 0.6f))
                        .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (1..5).forEach { rating ->
                        val isSelected = quality == rating
                        val animatedBg by animateColorAsState(
                            targetValue = if (isSelected) AccentBlue else Color.Transparent,
                            animationSpec = tween(200),
                            label = "segment_bg_$rating"
                        )
                        val animatedText by animateColorAsState(
                            targetValue = if (isSelected) BackgroundDark else TextSecondary,
                            animationSpec = tween(200),
                            label = "segment_text_$rating"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(animatedBg)
                                .clickable { quality = rating },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    tint = animatedText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$rating",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = animatedText
                                )
                            }
                        }
                    }
                }
            }

            // ─── 4. NOTES INPUT ───────────────────────────────────────────────────
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Session Notes (optional)") },
                placeholder = { Text("Restfulness, sleep environment, etc.", color = TextTertiary) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = Color(0xFF2C3242),
                    focusedLabelColor = AccentBlue,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    unfocusedContainerColor = SurfaceCardAlt,
                    focusedContainerColor = SurfaceCardAlt
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ─── 5. SAVE PRIMARY ACTION BUTTON ────────────────────────────────────
            Button(
                onClick = {
                    scope.launch {
                        val entry = SleepEntry(
                            id = if (entryId != null && entryId > 0) entryId else 0,
                            startMillis = bedCal.timeInMillis,
                            endMillis = wakeCal.timeInMillis,
                            quality = quality,
                            notes = notes.trim(),
                            dateMillis = existingDateMillis
                        )
                        if (entryId != null && entryId > 0) {
                            sleepRepository.update(entry)
                        } else {
                            sleepRepository.insert(entry)
                        }
                        onShowSnackbar("Sleep session saved successfully")
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text(
                    text = if (entryId != null && entryId > 0) "Update Session" else "Save Sleep Session",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BackgroundDark
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ─── AUTHENTIC PHONE CLOCK SCROLLABLE WHEEL PICKER ─────────────────────────────
    if (activeTimeTarget != null) {
        val isBedtime = activeTimeTarget == TimeTarget.BEDTIME
        val initialHour = if (isBedtime) startHour else endHour
        val initialMinute = if (isBedtime) startMinute else endMinute

        PhoneClockWheelSheet(
            sheetState = sheetState,
            title = if (isBedtime) "Select Bedtime" else "Select Wake Time",
            accentColor = if (isBedtime) AccentBlue else AccentGreen,
            initialHour24 = initialHour,
            initialMinute = initialMinute,
            onDismiss = { activeTimeTarget = null },
            onConfirm = { h, m ->
                if (isBedtime) {
                    startHour = h
                    startMinute = m
                } else {
                    endHour = h
                    endMinute = m
                }
                activeTimeTarget = null
            }
        )
    }
}

// ─── CUSTOM TIME TILE CARD ─────────────────────────────────────────────────────
@Composable
private fun CustomTimeTile(
    modifier: Modifier = Modifier,
    label: String,
    timeText: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCardAlt)
            .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = TextTertiary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = timeText,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tap to open clock wheel",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

// ─── AUTHENTIC PHONE CLOCK SCROLLABLE WHEEL PICKER (iOS / ANDROID ALARM CLOCK STYLE) ───
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun PhoneClockWheelSheet(
    sheetState: SheetState,
    title: String,
    accentColor: Color,
    initialHour24: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val initialIsAm = initialHour24 < 12
    val initialHr12 = when {
        initialHour24 % 12 == 0 -> 12
        else -> initialHour24 % 12
    }

    // Scroll States for Hours (1-12) and Minutes (0-59)
    val itemHeightPx = 48.dp
    val hoursList = (1..12).toList()
    val minutesList = (0..59).toList()

    val initialHourIndex = hoursList.indexOf(initialHr12).coerceAtLeast(0)
    val initialMinuteIndex = initialMinute.coerceIn(0, 59)

    val hoursListState = rememberLazyListState(initialFirstVisibleItemIndex = initialHourIndex)
    val minutesListState = rememberLazyListState(initialFirstVisibleItemIndex = initialMinuteIndex)

    val hoursSnapBehavior = rememberSnapFlingBehavior(lazyListState = hoursListState)
    val minutesSnapBehavior = rememberSnapFlingBehavior(lazyListState = minutesListState)

    var isAm by remember { mutableStateOf(initialIsAm) }

    val currentSelectedHour by remember {
        derivedStateOf {
            val index = hoursListState.firstVisibleItemIndex % hoursList.size
            hoursList[index]
        }
    }

    val currentSelectedMinute by remember {
        derivedStateOf {
            val index = minutesListState.firstVisibleItemIndex % minutesList.size
            minutesList[index]
        }
    }

    val displayFormatted = remember(currentSelectedHour, currentSelectedMinute, isAm) {
        String.format(Locale.US, "%d:%02d %s", currentSelectedHour, currentSelectedMinute, if (isAm) "AM" else "PM")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCardAlt,
        scrimColor = Color.Black.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Time Digital Preview Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard.copy(alpha = 0.8f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayFormatted,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Phone Clock Scrollable Wheel Picker Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceCard.copy(alpha = 0.5f))
                    .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Center Selection Glass Band
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // HOURS WHEEL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyColumn(
                            state = hoursListState,
                            flingBehavior = hoursSnapBehavior,
                            contentPadding = PaddingValues(vertical = 56.dp),
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(1000 * hoursList.size) { globalIndex ->
                                val hr = hoursList[globalIndex % hoursList.size]
                                val isSelected = currentSelectedHour == hr

                                Box(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%02d", hr),
                                        fontSize = if (isSelected) 26.sp else 18.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                        color = if (isSelected) accentColor else TextTertiary
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = ":",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    // MINUTES WHEEL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyColumn(
                            state = minutesListState,
                            flingBehavior = minutesSnapBehavior,
                            contentPadding = PaddingValues(vertical = 56.dp),
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(1000 * minutesList.size) { globalIndex ->
                                val min = minutesList[globalIndex % minutesList.size]
                                val isSelected = currentSelectedMinute == min

                                Box(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%02d", min),
                                        fontSize = if (isSelected) 26.sp else 18.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                        color = if (isSelected) accentColor else TextTertiary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // AM / PM VERTICAL TOGGLE
                    Column(
                        modifier = Modifier
                            .width(60.dp)
                            .height(96.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BackgroundDark)
                            .border(1.dp, Color(0xFF2C3242), RoundedCornerShape(14.dp))
                            .padding(2.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAm) accentColor else Color.Transparent)
                                .clickable { isAm = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AM",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAm) BackgroundDark else TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isAm) accentColor else Color.Transparent)
                                .clickable { isAm = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PM",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isAm) BackgroundDark else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Confirm Button
            Button(
                onClick = {
                    val final24Hour = when {
                        isAm && currentSelectedHour == 12 -> 0
                        !isAm && currentSelectedHour != 12 -> currentSelectedHour + 12
                        else -> currentSelectedHour
                    }
                    onConfirm(final24Hour, currentSelectedMinute)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = BackgroundDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Confirm Time",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BackgroundDark
                )
            }
        }
    }
}

private fun format12Hour(hour24: Int, minute: Int): String {
    val isAm = hour24 < 12
    val hr12 = when {
        hour24 % 12 == 0 -> 12
        else -> hour24 % 12
    }
    return String.format(Locale.US, "%d:%02d %s", hr12, minute, if (isAm) "AM" else "PM")
}
