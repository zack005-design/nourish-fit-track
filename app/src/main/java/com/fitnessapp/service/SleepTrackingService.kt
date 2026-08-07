package com.fitnessapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.fitnessapp.MainActivity
import com.fitnessapp.data.db.entity.SleepEntry
import com.fitnessapp.data.repository.SleepRepository
import com.fitnessapp.util.DateUtils
import com.fitnessapp.util.HealthConnectManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

data class SleepTrackingState(
    val isTracking: Boolean = false,
    val startTimeMillis: Long = 0L,
    val motionEventsCount: Int = 0,
    val ambientLux: Float = 0f,
    val statusText: String = "Monitoring Bedside Motion..."
)

/**
 * SleepTrackingService
 * Foreground service with Partial WakeLock to track full-night sleep telemetry continuously,
 * even when the screen is locked and device enters Doze mode.
 */
class SleepTrackingService : Service(), SensorEventListener {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var lightSensor: Sensor? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var lastMotionTime = 0L

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Nourish:SleepTrackingWakeLock")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SLEEP_TRACKING -> {
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                startForegroundTracking()
                return START_STICKY
            }
        }
    }

    private fun startForegroundTracking() {
        createNotificationChannel()

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Nourish Sleep Tracker Active")
            .setContentText("Monitoring motion & environment sensors overnight...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        try {
            if (wakeLock?.isHeld != true) {
                wakeLock?.acquire(12 * 60 * 60 * 1000L) // 12 hours max
            }
        } catch (e: Exception) {
            // Ignore lock error
        }

        val startTime = if (_trackingState.value.startTimeMillis > 0) _trackingState.value.startTimeMillis else System.currentTimeMillis()
        _trackingState.value = SleepTrackingState(
            isTracking = true,
            startTimeMillis = startTime,
            motionEventsCount = 0,
            ambientLux = 0f,
            statusText = "Overnight Tracking Active"
        )

        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !_trackingState.value.isTracking) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val deltaFromGravity = abs(magnitude - SensorManager.GRAVITY_EARTH)

                val now = System.currentTimeMillis()
                if (deltaFromGravity > 1.2f && (now - lastMotionTime) > 3000L) {
                    lastMotionTime = now
                    val newCount = _trackingState.value.motionEventsCount + 1
                    val status = when {
                        newCount > 25 -> "Restless (Frequent Tossing)"
                        newCount > 10 -> "Light Motion"
                        else -> "Calm (Restful Bed State)"
                    }
                    _trackingState.value = _trackingState.value.copy(
                        motionEventsCount = newCount,
                        statusText = status
                    )
                }
            }
            Sensor.TYPE_LIGHT -> {
                val lux = event.values[0]
                _trackingState.value = _trackingState.value.copy(ambientLux = lux)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            // Ignore unlock error
        }
        _trackingState.value = SleepTrackingState(isTracking = false)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sleep Tracking Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows persistent status while overnight sleep tracking is active"
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "nourish_sleep_tracking_channel"
        const val NOTIFICATION_ID = 2001
        const val ACTION_START_SLEEP_TRACKING = "com.fitnessapp.action.START_SLEEP_TRACKING"
        const val ACTION_STOP_SLEEP_TRACKING = "com.fitnessapp.action.STOP_SLEEP_TRACKING"

        private val _trackingState = MutableStateFlow(SleepTrackingState())
        val trackingState: StateFlow<SleepTrackingState> = _trackingState.asStateFlow()

        fun startService(context: Context) {
            val intent = Intent(context, SleepTrackingService::class.java).apply {
                action = ACTION_START_SLEEP_TRACKING
            }
            context.startForegroundService(intent)
        }

        fun stopAndSaveService(context: Context, sleepRepository: SleepRepository, onComplete: (SleepEntry) -> Unit) {
            val currentState = _trackingState.value
            val startMillis = if (currentState.startTimeMillis > 0) currentState.startTimeMillis else System.currentTimeMillis() - 300000L
            val endMillis = System.currentTimeMillis()

            val qualityScore = when {
                currentState.motionEventsCount > 25 -> 2
                currentState.motionEventsCount > 15 -> 3
                currentState.motionEventsCount > 6 -> 4
                else -> 5
            }

            val notesStr = "Continuous Overnight Sensor Track · ${currentState.motionEventsCount} motion events · ${String.format(Locale.US, "%.1f", currentState.ambientLux)} lux"

            val entry = SleepEntry(
                startMillis = startMillis,
                endMillis = endMillis,
                quality = qualityScore,
                notes = notesStr,
                dateMillis = DateUtils.todayStartMillis()
            )

            CoroutineScope(Dispatchers.IO).launch {
                val insertedId = sleepRepository.insert(entry)
                val finalEntry = entry.copy(id = insertedId)
                HealthConnectManager.insertSleepRecords(context, listOf(finalEntry))
                
                CoroutineScope(Dispatchers.Main).launch {
                    onComplete(finalEntry)
                }
            }

            val intent = Intent(context, SleepTrackingService::class.java).apply {
                action = ACTION_STOP_SLEEP_TRACKING
            }
            context.startService(intent)
        }
    }
}
