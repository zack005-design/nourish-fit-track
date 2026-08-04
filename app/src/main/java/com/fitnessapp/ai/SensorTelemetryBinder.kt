package com.fitnessapp.ai

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Locale
import kotlin.math.sqrt

/**
 * Binds hardware sensor telemetry (Accelerometer, Step Counter, Gyroscope)
 * from the Android [SensorManager] and formats the live values as text
 * suitable for injection into the AI's runtime prompt context.
 *
 * Lifecycle: Call [start] to begin listening, [stop] to release the sensor.
 */
class SensorTelemetryBinder(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Sensors
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val stepCounter: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    // Snapshot state
    private var accelX = 0f; private var accelY = 0f; private var accelZ = 0f
    private var gyroX = 0f; private var gyroY = 0f; private var gyroZ = 0f
    private var stepCount = 0f
    private var isActive = false

    // Hot flow emitting telemetry context strings every time a sensor fires
    private val _telemetryFlow = MutableSharedFlow<String>(
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val telemetryFlow: SharedFlow<String> = _telemetryFlow.asSharedFlow()

    companion object {
        private const val TAG = "SensorTelemetryBinder"
    }

    /** Begin listening to hardware sensors. Call from a lifecycle-aware component. */
    fun start() {
        if (isActive) return
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Accelerometer registered")
        } ?: Log.w(TAG, "Accelerometer not available")

        stepCounter?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Step Counter registered")
        } ?: Log.w(TAG, "Step Counter not available")

        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Gyroscope registered")
        } ?: Log.w(TAG, "Gyroscope not available")

        isActive = true
    }

    /** Stop listening and release the sensor listener. */
    fun stop() {
        sensorManager.unregisterListener(this)
        isActive = false
        Log.d(TAG, "Sensors unregistered")
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelX = event.values[0]; accelY = event.values[1]; accelZ = event.values[2]
            }
            Sensor.TYPE_STEP_COUNTER -> {
                stepCount = event.values[0]
            }
            Sensor.TYPE_GYROSCOPE -> {
                gyroX = event.values[0]; gyroY = event.values[1]; gyroZ = event.values[2]
            }
        }
        _telemetryFlow.tryEmit(buildContextString())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Accuracy changed: ${sensor?.name} -> $accuracy")
    }

    /**
     * Builds a clean, AI-readable telemetry context string from current sensor values.
     *
     * Example output:
     * ```
     * [LIVE SENSOR TELEMETRY]
     * Accelerometer: X=-0.12 Y=9.81 Z=0.03 m/s² | Magnitude: 9.81 m/s²
     * Motion State: Stationary
     * Step Counter: 4231 steps (session total)
     * Gyroscope: X=0.00 Y=0.01 Z=-0.02 rad/s
     * ```
     */
    fun buildContextString(): String {
        val magnitude = sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ)
        val motionState = when {
            magnitude < 1.5f  -> "Stationary"
            magnitude < 5.0f  -> "Light Movement"
            magnitude < 12.0f -> "Active / Walking"
            else              -> "Intense Activity"
        }

        return buildString {
            appendLine("[LIVE SENSOR TELEMETRY]")
            appendLine(
                "Accelerometer: X=${fmt(accelX)} Y=${fmt(accelY)} Z=${fmt(accelZ)} m/s² " +
                "| Magnitude: ${fmt(magnitude)} m/s²"
            )
            appendLine("Motion State: $motionState")
            if (stepCount > 0f) appendLine("Step Counter: ${stepCount.toInt()} steps (session total)")
            if (gyroscope != null) {
                appendLine("Gyroscope: X=${fmt(gyroX)} Y=${fmt(gyroY)} Z=${fmt(gyroZ)} rad/s")
            }
        }.trim()
    }

    /** Current snapshot — call from coroutine to get latest reading without subscribing to flow. */
    fun currentSnapshot(): String = buildContextString()

    private fun fmt(v: Float): String = String.format(Locale.US, "%.2f", v)
}
