package com.fitnessapp.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sqrt

data class SensorSleepState(
    val isTracking: Boolean = false,
    val startTimeMillis: Long = 0L,
    val elapsedSeconds: Long = 0L,
    val motionEventsCount: Int = 0,
    val ambientLux: Float = 0f,
    val motionStatusText: String = "Calm (Bed Stationary)",
    val isDarkEnvironment: Boolean = true
)

class SensorSleepTracker(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val lightSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val _state = MutableStateFlow(SensorSleepState())
    val state: StateFlow<SensorSleepState> = _state.asStateFlow()

    private var lastMotionTime = 0L

    fun startTracking() {
        if (_state.value.isTracking) return

        _state.value = SensorSleepState(
            isTracking = true,
            startTimeMillis = System.currentTimeMillis(),
            elapsedSeconds = 0L,
            motionEventsCount = 0,
            ambientLux = 0f,
            motionStatusText = "Tracking Motion & Light...",
            isDarkEnvironment = true
        )

        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopTracking(): SensorSleepState {
        sensorManager?.unregisterListener(this)
        val finalState = _state.value
        _state.value = SensorSleepState(isTracking = false)
        return finalState
    }

    fun updateElapsedSeconds(seconds: Long) {
        if (_state.value.isTracking) {
            _state.value = _state.value.copy(elapsedSeconds = seconds)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !_state.value.isTracking) return

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
                    val newCount = _state.value.motionEventsCount + 1
                    val status = when {
                        newCount > 15 -> "Restless (Tossing & Turning)"
                        newCount > 5 -> "Light Micro-Movements"
                        else -> "Calm (Bed Stationary)"
                    }
                    _state.value = _state.value.copy(
                        motionEventsCount = newCount,
                        motionStatusText = status
                    )
                }
            }
            Sensor.TYPE_LIGHT -> {
                val lux = event.values[0]
                _state.value = _state.value.copy(
                    ambientLux = lux,
                    isDarkEnvironment = lux < 15.0f
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
