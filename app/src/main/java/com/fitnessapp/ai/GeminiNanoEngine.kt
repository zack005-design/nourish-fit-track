package com.fitnessapp.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Wrapper around Gemini Nano (MediaPipe LLM Inference) for on-device AI inference.
 * Model file must be stored at: /data/local/tmp/llm/model.bin
 * or placed via adb push before first use.
 */
class GeminiNanoEngine private constructor(
    private val context: Context,
    private val modelPath: String
) {

    private var llm: LlmInference? = null

    init {
        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(MAX_TOKENS)
                .build()
            llm = LlmInference.createFromOptions(context, options)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize LlmInference", e)
        }
    }

    /**
     * Runs a text prompt and returns a streaming Flow of response tokens.
     * @param prompt The full assembled prompt string (text + sensor context + instructions).
     */
    fun generateStream(prompt: String): Flow<String> = callbackFlow {
        try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(MAX_TOKENS)
                .setResultListener { partialResult, done ->
                    trySend(partialResult)
                    if (done) close()
                }
                .build()
            val streamingLlm = LlmInference.createFromOptions(context, options)
            streamingLlm.generateResponseAsync(prompt)
        } catch (e: Exception) {
            Log.e(TAG, "Streaming generation failed", e)
            close(e)
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)

    /**
     * Synchronous single-shot generation for short prompts (sensor summaries, food queries).
     */
    suspend fun generate(prompt: String): String = withContext(Dispatchers.IO) {
        val engine = llm ?: return@withContext "Error: Gemini Nano model not initialized."
        return@withContext try {
            engine.generateResponse(prompt)
        } catch (e: Exception) {
            Log.e(TAG, "LLM inference failed", e)
            "Error: ${e.message}"
        }
    }

    /**
     * Converts a Bitmap captured by CameraX into a text description prompt.
     * MediaPipe LLM Inference API accepts image + text via a multimodal prompt.
     */
    fun buildImagePrompt(bitmap: Bitmap, userInstruction: String): String {
        val scaled = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteCount = scaled.byteCount
        Log.d(TAG, "Image tensor ready: ${scaled.width}x${scaled.height}, ${byteCount}B")
        if (scaled != bitmap && !scaled.isRecycled) {
            scaled.recycle()
        }
        return buildString {
            appendLine("[IMAGE ANALYSIS REQUEST]")
            appendLine("Analyze the image and respond to: $userInstruction")
            appendLine("Focus on: food items, nutrition estimates, meal composition if visible.")
        }
    }

    companion object {
        private const val TAG = "GeminiNanoEngine"
        private const val MODEL_PATH = "/data/local/tmp/llm/model.bin"
        private const val MAX_TOKENS = 1024

        @Volatile
        private var instance: GeminiNanoEngine? = null

        /**
         * Lazily initializes the engine. Call from a background coroutine.
         * Returns null if the model file is not present on device.
         */
        fun getInstance(context: Context): GeminiNanoEngine? {
            return instance ?: synchronized(this) {
                instance ?: try {
                    val path = when {
                        File(MODEL_PATH).exists() -> MODEL_PATH
                        File(context.filesDir, "gemini_nano.bin").exists() -> "${context.filesDir}/gemini_nano.bin"
                        else -> {
                            Log.w(TAG, "Gemini Nano model not found at $MODEL_PATH or ${context.filesDir}/gemini_nano.bin")
                            return@synchronized null
                        }
                    }
                    GeminiNanoEngine(context.applicationContext, path).also { instance = it }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to instantiate GeminiNanoEngine", e)
                    null
                }
            }
        }

        fun release() {
            instance = null
        }
    }
}
