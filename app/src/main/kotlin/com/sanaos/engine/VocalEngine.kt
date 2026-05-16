package com.sanaos.engine

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import com.sanaos.data.SharedPrefsManager
import com.sanaos.util.AudioFocusManager
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

object VocalEngine {
    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    fun speak(text: String, context: Context) {
        val safeText = sanitizeForElevenLabs(text)
        if (!SanaBrain.isNetworkAvailable(context)) {
            speakWithNativeTts(safeText, context)
            return
        }
        val elevenLabsKey = SharedPrefsManager.get(context, SharedPrefsManager.Keys.ELEVENLABS_API_KEY)
        if (elevenLabsKey.isNullOrBlank()) {
            speakWithNativeTts(safeText, context)
            return
        }
        try {
            callElevenLabsApi(safeText, elevenLabsKey, context)
        } catch (_: Exception) {
            speakWithNativeTts(safeText, context)
        }
    }

    private fun callElevenLabsApi(rawText: String, apiKey: String, context: Context) {
        val safeText = sanitizeForElevenLabs(rawText)
        Thread {
            val focusManager = AudioFocusManager(context)
            try {
                val okFocus = focusManager.requestAudioFocus()
                if (!okFocus) {
                    speakWithNativeTts(safeText, context)
                    return@Thread
                }
                val endpoint = "https://api.elevenlabs.io/v1/text-to-speech/EXAVITQu4vr4xnSDxMaL"
                val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 12000
                    readTimeout = 18000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("xi-api-key", apiKey)
                }
                val requestBody = JSONObject().apply {
                    put("text", safeText)
                    put("model_id", "eleven_multilingual_v2")
                    put("voice_settings", JSONObject().apply {
                        put("stability", 0.5)
                        put("similarity_boost", 0.75)
                    })
                }.toString()
                conn.outputStream.use { it.write(requestBody.toByteArray(Charsets.UTF_8)) }
                val code = conn.responseCode
                if (code !in 200..299) {
                    speakWithNativeTts(safeText, context)
                    focusManager.abandonAudioFocus()
                    return@Thread
                }
                val tempFile = File.createTempFile("sana_voice", ".mp3", context.cacheDir)
                conn.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output -> input.copyTo(output) }
                }
                playMp3(tempFile, context, focusManager)
            } catch (_: Exception) {
                focusManager.abandonAudioFocus()
                speakWithNativeTts(safeText, context)
            }
        }.start()
    }

    private fun playMp3(file: File, context: Context, focusManager: AudioFocusManager) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnCompletionListener {
                    try {
                        it.release()
                        mediaPlayer = null
                        focusManager.abandonAudioFocus()
                        file.delete()
                    } catch (_: Exception) {
                    }
                }
                setOnErrorListener { mp, _, _ ->
                    try {
                        mp.release()
                        mediaPlayer = null
                        focusManager.abandonAudioFocus()
                        file.delete()
                    } catch (_: Exception) {
                    }
                    speakWithNativeTts("Boss, audio play nahi ho paya.", context)
                    true
                }
                prepare()
                start()
            }
        } catch (_: Exception) {
            focusManager.abandonAudioFocus()
            speakWithNativeTts("Boss, main native voice pe switch kar rahi hun.", context)
        }
    }

    private fun speakWithNativeTts(text: String, context: Context) {
        val focusManager = AudioFocusManager(context)
        if (!focusManager.requestAudioFocus()) return
        val existing = tts
        if (existing == null) {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    configureTts(tts)
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sana_native_tts")
                }
                focusManager.abandonAudioFocus()
            }
        } else {
            configureTts(existing)
            existing.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sana_native_tts")
            focusManager.abandonAudioFocus()
        }
    }

    private fun configureTts(tts: TextToSpeech?) {
        if (tts == null) return
        tts.setSpeechRate(1.0f)
        tts.setPitch(1.1f)
        val hiResult = tts.setLanguage(Locale("hi", "IN"))
        if (hiResult == TextToSpeech.LANG_MISSING_DATA || hiResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.setLanguage(Locale.ENGLISH)
        }
    }

    private fun sanitizeForElevenLabs(raw: String): String {
        val sb = StringBuilder(raw.length)
        var i = 0
        while (i < raw.length) {
            val cp: Int = if (
                i + 1 < raw.length &&
                Character.isHighSurrogate(raw[i]) &&
                Character.isLowSurrogate(raw[i + 1])
            ) {
                val decoded = Character.toCodePoint(raw[i], raw[i + 1])
                i += 2
                decoded
            } else {
                raw[i].code.also { i++ }
            }
            val category = Character.getType(cp)
            val isEmoji = cp >= 0x10000 ||
                category == Character.OTHER_SYMBOL.toInt() ||
                category == Character.SURROGATE.toInt() ||
                cp in 0x2600..0x27BF ||
                cp in 0x2300..0x23FF ||
                cp in 0x2B00..0x2BFF ||
                cp in 0xFE00..0xFE0F ||
                cp == 0x200D ||
                cp == 0xFEFF
            if (!isEmoji) sb.appendCodePoint(cp)
        }
        val cleaned = sb.toString().replace(Regex("\\s{2,}"), " ").trim()
        return cleaned.ifBlank { "Kaam ho gaya, Boss." }
    }
}
