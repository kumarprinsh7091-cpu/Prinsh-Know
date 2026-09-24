package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class AudioPlayerHelper(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var androidTts: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        androidTts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                androidTts?.language = Locale.US
                isTtsInitialized = true
            }
        }
    }

    fun playAudioBytes(bytes: ByteArray, onCompletion: () -> Unit = {}) {
        stop()
        try {
            val tempFile = File.createTempFile("tts_audio", ".mp3", context.cacheDir)
            FileOutputStream(tempFile).use { it.write(bytes) }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    tempFile.delete()
                    onCompletion()
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerHelper", "Failed to play audio bytes", e)
            onCompletion()
        }
    }

    fun speakWithFallback(text: String, onDone: () -> Unit = {}) {
        if (isTtsInitialized) {
            androidTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_${System.currentTimeMillis()}")
        }
    }

    fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("AudioPlayerHelper", "Error stopping player", e)
        }
        try {
            androidTts?.stop()
        } catch (e: Exception) {
            Log.e("AudioPlayerHelper", "Error stopping TTS", e)
        }
    }

    fun release() {
        stop()
        try {
            androidTts?.shutdown()
            androidTts = null
        } catch (e: Exception) {
            Log.e("AudioPlayerHelper", "Error shutting down TTS", e)
        }
    }
}
