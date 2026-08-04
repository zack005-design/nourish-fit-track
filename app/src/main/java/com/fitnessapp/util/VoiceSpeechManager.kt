package com.fitnessapp.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * VoiceSpeechManager
 * Manages Speech-To-Text voice prompt input and Text-To-Speech audio response playback.
 */
class VoiceSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    /**
     * Speaks out the given text string via Android TextToSpeech engine.
     */
    fun speak(text: String): Boolean {
        if (isTtsReady && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "NourishSpeechId")
            return true
        }
        return false
    }

    /**
     * Stops current speech output.
     */
    fun stopSpeech() {
        tts?.stop()
    }

    /**
     * Releases TTS resources.
     */
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }

    companion object {
        fun createSpeechIntent(prompt: String = "Speak your log or question"): Intent {
            return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
            }
        }
    }
}
