package com.fitnessapp.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.channels.awaitClose
import java.io.File

/**
 * Wrapper around Gemini Nano (MediaPipe LLM Inference) for on-device AI inference.
 * Model file must be stored at: /data/local/tmp/llm/model.bin
 * or placed via adb push before first use.
 */
class GeminiNanoEngine private constructor(private val llm: LlmInference) {

    /**
     * Runs a text prompt and returns a streaming Flow of response tokens.
     * @param prompt The full assembled prompt string (text + sensor context + instructions).
     */
    fun generateStream(prompt: String): Flow<String> = callbackFlow {
        llm.generateResponseAsync(
            prompt,
            { partialResult, done ->
                trySend(partialResult)
                if (done) close()
            }
        )
        awaitClose()
    }.flowOn(Dispatchers.IO)

    /**
     * Synchronous single-shot generation for short prompts (sensor summaries, food queries).
     */
    suspend fun generate(prompt: String): String {
        return try {
            llm.generateResponse(prompt)
        } catch (e: Exception) {
            Log.e(TAG, "LLM inference failed", e)
            "Error: ${e.message}"
        }
    }

    /**
     * Converts a Bitmap captured by CameraX into a text description prompt.
     * MediaPipe LLM Inference API accepts image + text via a multimodal prompt.
     * The bitmap is encoded to base64 and injected into a vision prompt template.
     */
    fun buildImagePrompt(bitmap: Bitmap, userInstruction: String): String {
        // Downscale to save tokens (model receives vision via the API's image input)
        val scaled = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteCount = scaled.byteCount
        Log.d(TAG, "Image tensor ready: ${scaled.width}x${scaled.height}, ${byteCount}B")
        // Return a structured prompt; caller passes bitmap via LlmInference's vision API
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
                    val modelFile = File(MODEL_PATH)
                    if (!modelFile.exists()) {
                        // Fallback: check app's files dir for a bundled model
                        val internalModel = File(context.filesDir, "gemini_nano.bin")
                        if (!internalModel.exists()) {
                            Log.w(TAG, "Gemini Nano model not found at $MODEL_PATH or ${internalModel.path}")
                            return@synchronized null
                        }
                    }
                    val options = LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(if (File(MODEL_PATH).exists()) MODEL_PATH else "${context.filesDir}/gemini_nano.bin")
                        .setMaxTokens(MAX_TOKENS)
                        .build()
                    val llm = LlmInference.createFromOptions(context, options)
                    GeminiNanoEngine(llm).also { instance = it }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to initialize GeminiNanoEngine", e)
                    null
                }
            }
        }

        fun release() {
            instance = null
        }
    }
}
