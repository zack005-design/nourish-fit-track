package com.fitnessapp.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Assembles a unified AI prompt from all native hardware sources:
 * - Live sensor telemetry (Accelerometer, Step Counter, Gyroscope)
 * - Local storage logs (.txt / .json)
 * - Optional camera image description
 *
 * Pass the assembled prompt to [GeminiNanoEngine.generate] or [GeminiNanoEngine.generateStream].
 *
 * Example usage:
 * ```kotlin
 * val engine = GeminiNanoEngine.getInstance(context) ?: return
 * val binder = SensorTelemetryBinder(context)
 * val harvester = LocalStorageHarvester(context)
 * val assembler = AiContextAssembler(engine, binder, harvester)
 *
 * val response = assembler.askWithFullContext("What should I eat for recovery?")
 * ```
 */
class AiContextAssembler(
    private val engine: GeminiNanoEngine,
    private val sensorBinder: SensorTelemetryBinder,
    private val storageHarvester: LocalStorageHarvester
) {

    companion object {
        private const val TAG = "AiContextAssembler"
    }

    /**
     * Builds a full context prompt from sensors + local logs, then queries the AI.
     * @param userQuestion The user's question or task instruction.
     * @param imageBitmap Optional camera capture bitmap for image-based queries.
     * @return The AI response string.
     */
    suspend fun askWithFullContext(
        userQuestion: String,
        imageBitmap: Bitmap? = null
    ): String = withContext(Dispatchers.IO) {
        val sensorContext = sensorBinder.currentSnapshot()
        val storageContext = storageHarvester.buildAiContext()

        val prompt = buildPrompt(
            userQuestion = userQuestion,
            sensorContext = sensorContext,
            storageContext = storageContext,
            imageBitmap = imageBitmap
        )

        Log.d(TAG, "Prompt assembled (${prompt.length} chars)")
        engine.generate(prompt)
    }

    /**
     * Streaming version — returns result tokens as they are generated.
     */
    fun askWithFullContextStream(
        userQuestion: String,
        sensorSnapshot: String = sensorBinder.currentSnapshot(),
        storageContext: String = ""
    ) = engine.generateStream(
        buildPrompt(
            userQuestion = userQuestion,
            sensorContext = sensorSnapshot,
            storageContext = storageContext
        )
    )

    private fun buildPrompt(
        userQuestion: String,
        sensorContext: String,
        storageContext: String,
        imageBitmap: Bitmap? = null
    ): String = buildString {
        appendLine("You are a personal health and fitness AI assistant integrated into the Nourish app.")
        appendLine("You have access to the user's live hardware sensor data and local health logs.")
        appendLine("Answer concisely, accurately, and in a helpful coaching tone.")
        appendLine()

        if (sensorContext.isNotBlank()) {
            appendLine(sensorContext)
            appendLine()
        }

        if (storageContext.isNotBlank()) {
            appendLine(storageContext)
        }

        if (imageBitmap != null) {
            appendLine("[CAMERA INPUT AVAILABLE]")
            appendLine("An image has been captured and processed. Analyze its nutritional content if it contains food.")
            appendLine()
        }

        appendLine("USER QUESTION: $userQuestion")
    }
}
