package com.fitnessapp.ai

import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

/**
 * Reads local `.txt` and `.json` log files from Scoped Storage for AI context injection.
 *
 * Reads from two locations:
 * 1. App-private directory: [Context.getFilesDir]/ai_logs/
 * 2. External public Downloads dir (requires READ_MEDIA_IMAGES or READ_EXTERNAL_STORAGE)
 *
 * All operations are performed on [Dispatchers.IO].
 */
class LocalStorageHarvester(private val context: Context) {

    companion object {
        private const val TAG = "LocalStorageHarvester"
        private const val MAX_FILE_SIZE_BYTES = 50_000L  // 50 KB cap per file
        private const val LOG_DIR = "ai_logs"
    }

    /**
     * Returns the app-private AI logs directory, creating it if absent.
     */
    val logDirectory: File
        get() = File(context.filesDir, LOG_DIR).also { it.mkdirs() }

    /**
     * Reads all `.txt` files from [logDirectory] and concatenates their content
     * into a single string suitable for injecting into an AI prompt.
     *
     * @return Formatted context string, e.g.:
     * "[LOG: workout_notes.txt]\nRan 5km this morning..."
     */
    suspend fun readTxtLogs(): String = withContext(Dispatchers.IO) {
        val logs = StringBuilder()
        logDirectory.listFiles { f -> f.extension == "txt" }
            ?.sortedByDescending { it.lastModified() }
            ?.take(5) // Limit to 5 most recent to keep prompt size manageable
            ?.forEach { file ->
                if (file.length() > MAX_FILE_SIZE_BYTES) {
                    Log.w(TAG, "Skipping large file: ${file.name} (${file.length()}B)")
                    return@forEach
                }
                try {
                    logs.append("[LOG: ${file.name}]\n")
                    logs.append(file.readText(Charsets.UTF_8).trim())
                    logs.append("\n\n")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to read ${file.name}", e)
                }
            }
        logs.toString().ifEmpty { "" }
    }

    /**
     * Reads all `.json` files from [logDirectory], parses them, and returns
     * a flattened key=value text block for AI prompt injection.
     *
     * @return Formatted JSON context string, e.g.:
     * "[JSON: nutrition_export.json]\ncalories=1850\nprotein=92..."
     */
    suspend fun readJsonLogs(): String = withContext(Dispatchers.IO) {
        val output = StringBuilder()
        logDirectory.listFiles { f -> f.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?.take(3)
            ?.forEach { file ->
                if (file.length() > MAX_FILE_SIZE_BYTES) {
                    Log.w(TAG, "Skipping large JSON: ${file.name}")
                    return@forEach
                }
                try {
                    val raw = file.readText(Charsets.UTF_8).trim()
                    val json = JSONObject(raw)
                    output.append("[JSON: ${file.name}]\n")
                    json.keys().forEach { key ->
                        output.append("$key=${json.optString(key)}\n")
                    }
                    output.append("\n")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse ${file.name}", e)
                }
            }
        output.toString()
    }

    /**
     * Writes a text log entry to [logDirectory]/[fileName].
     * Useful for persisting AI interactions or sensor summaries.
     */
    suspend fun writeLog(fileName: String, content: String) = withContext(Dispatchers.IO) {
        try {
            val file = File(logDirectory, fileName)
            file.writeText(content, Charsets.UTF_8)
            Log.d(TAG, "Written log: ${file.path}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write log $fileName", e)
        }
    }

    /**
     * Builds a combined context string from all available logs.
     * Inject this into your AI prompt as background knowledge.
     */
    suspend fun buildAiContext(): String = withContext(Dispatchers.IO) {
        val txt = readTxtLogs()
        val json = readJsonLogs()
        buildString {
            if (txt.isNotBlank()) {
                appendLine("=== LOCAL TEXT LOGS ===")
                appendLine(txt)
            }
            if (json.isNotBlank()) {
                appendLine("=== LOCAL JSON LOGS ===")
                appendLine(json)
            }
        }
    }

    /**
     * Returns Android SDK version string for debugging scoped storage behavior.
     */
    fun storageApiLevel(): String = "API ${Build.VERSION.SDK_INT} - " +
            if (Build.VERSION.SDK_INT >= 33) "Scoped Storage (READ_MEDIA_IMAGES)"
            else "Legacy READ_EXTERNAL_STORAGE"
}
