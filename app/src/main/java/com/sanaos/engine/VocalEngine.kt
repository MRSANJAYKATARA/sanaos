package com.sanaos.engine

import android.content.Context
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.util.Log
import com.sanaos.data.SharedPrefsManager
import com.sanaos.util.AudioFocusManager
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Locale

class VocalEngine(private val context: Context) {

    private var tts: TextToSpeech? = null
    private val okHttpClient = OkHttpClient()
    private val audioFocusManager = AudioFocusManager(context)
    private var isInitialized = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("hi", "IN")
                isInitialized = true
            } else {
                Log.e("SANA_VOCAL", "TTS init failed with status $status")
            }
        }
    }

    fun speak(text: String) {
        if (!audioFocusManager.requestAudioFocus()) {
            Log.w("SANA_VOCAL", "Could not request audio focus")
            return
        }

        try {
            val elevenlabsKey = SharedPrefsManager.get(context, SharedPrefsManager.Keys.ELEVENLABS_API_KEY)
            if (elevenlabsKey.isNotEmpty()) {
                speakWithElevenLabs(text)
            } else {
                speakWithNativeTTS(text)
            }
        } catch (e: Exception) {
            Log.e("SANA_VOCAL", "Speak error: ${e.message}", e)
            speakWithNativeTTS(text)
        } finally {
            audioFocusManager.abandonAudioFocus()
        }
    }

    private fun speakWithElevenLabs(text: String) {
        try {
            val elevenlabsKey = SharedPrefsManager.get(context, SharedPrefsManager.Keys.ELEVENLABS_API_KEY)
            val url = "https://api.elevenlabs.io/v1/text-to-speech/EXAVITQu4vr4xnSDxMaL"
            val requestBody = """{"text":"$text","model_id":"eleven_multilingual_v2","voice_settings":{"stability":0.5,"similarity_boost":0.75}}"""
                .toRequestBody()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("xi-api-key", elevenlabsKey)
                .header("Content-Type", "application/json")
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: "no body"
                Log.e("SANA_VOCAL", "ElevenLabs error [${response.code}]: $errBody")
                val fallbackText = "Boss, ElevenLabs fail ho gaya, native voice use kar rahi hun."
                speakWithNativeTTS(fallbackText)
                return
            }

            val audioStream = response.body?.byteStream()
            audioStream?.use {
                val audioData = it.readBytes()
                playAudio(audioData)
            }
        } catch (e: Exception) {
            Log.e("SANA_VOCAL", "ElevenLabs call error: ${e.message}", e)
            val fallbackText = "Boss, voice service mein problem aaya. Dobara koshish kar."
            speakWithNativeTTS(fallbackText)
        }
    }

    private fun speakWithNativeTTS(text: String) {
        try {
            if (isInitialized && tts != null) {
                tts?.speak(text, TextToSpeech.QUEUE_ADD, null)
            } else {
                Log.w("SANA_VOCAL", "TTS not initialized, cannot speak")
            }
        } catch (e: Exception) {
            Log.e("SANA_VOCAL", "Native TTS error: ${e.message}", e)
        }
    }

    private fun playAudio(audioData: ByteArray) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val audioTrack = android.media.AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100,
                android.media.AudioFormat.CHANNEL_OUT_MONO,
                android.media.AudioFormat.ENCODING_PCM_16BIT,
                audioData.size,
                android.media.AudioTrack.MODE_STREAM
            ).apply {
                write(audioData, 0, audioData.size)
                play()
            }
        } catch (e: Exception) {
            Log.e("SANA_VOCAL", "Audio playback error: ${e.message}", e)
        }
    }

    fun shutdown() {
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("SANA_VOCAL", "Shutdown error: ${e.message}", e)
        }
    }
}
