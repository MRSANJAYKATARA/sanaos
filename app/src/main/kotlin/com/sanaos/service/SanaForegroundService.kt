package com.sanaos.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.NotificationCompat
import com.sanaos.R
import com.sanaos.data.SharedPrefsManager
import com.sanaos.engine.IntentRouter
import com.sanaos.engine.SanaBrain
import com.sanaos.engine.VocalEngine

class SanaForegroundService : Service() {
    private val binder = Binder()
    private lateinit var speechRecognizer: SpeechRecognizer
    private val handler = Handler(Looper.getMainLooper())
    private var isSystemActive = true
    private var isProcessing = false
    private lateinit var audioManager: AudioManager
    private var savedVolumeMusic: Int = -1
    private var savedVolumeRing: Int = -1

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        createNotificationChannel()
        startForeground(1201, buildNotification("SANA active"))
        initRecognizer()
        startListeningInternal()
    }

    private fun initRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { unmuteAfterListening(); broadcastState("LISTENING") }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) { sendBroadcast(Intent("com.sanaos.ACTION_RMS").putExtra("rms_value", rmsdB)) }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { unmuteAfterListening() }
            override fun onError(error: Int) {
                unmuteAfterListening()
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> if (isSystemActive && !isProcessing) handler.postDelayed({ startListeningInternal() }, 300)
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        speechRecognizer.destroy()
                        initRecognizer()
                        handler.postDelayed({ startListeningInternal() }, 1000)
                    }
                    else -> broadcastState("IDLE")
                }
            }
            override fun onResults(results: Bundle?) {
                unmuteAfterListening()
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: return
                processVoiceQuery(text)
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun processVoiceQuery(text: String) {
        if (isProcessing) return
        isProcessing = true
        broadcastState("THINKING")
        SanaBrain.processQuery(text, this) { intent, response ->
            val result = IntentRouter.route(intent, this)
            val finalSpeak = if (result.success) result.spokenResponse else response
            sendBroadcast(Intent("com.sanaos.ACTION_TRANSCRIPT").putExtra("user_text", text).putExtra("sana_text", finalSpeak))
            broadcastState("SPEAKING")
            VocalEngine.speak(finalSpeak, this)
            isProcessing = false
            if (SharedPrefsManager.getBoolean(this, SharedPrefsManager.Keys.LIVE_MODE, true)) {
                handler.postDelayed({ startListeningInternal() }, 1200)
            } else {
                broadcastState("IDLE")
            }
        }
    }

    private fun startListeningInternal() {
        if (!isSystemActive || isProcessing) return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        muteForListening()
        speechRecognizer.startListening(intent)
    }

    private fun muteForListening() { try { savedVolumeMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC); savedVolumeRing = audioManager.getStreamVolume(AudioManager.STREAM_RING); audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,0); audioManager.setStreamVolume(AudioManager.STREAM_RING,0,0) } catch (_: Exception) {} }
    private fun unmuteAfterListening() { try { if (savedVolumeMusic >= 0) audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,savedVolumeMusic,0); if (savedVolumeRing >= 0) audioManager.setStreamVolume(AudioManager.STREAM_RING,savedVolumeRing,0) } catch (_: Exception) {} }

    private fun broadcastState(state: String) { sendBroadcast(Intent("com.sanaos.ACTION_STATE").putExtra("state", state)) }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("sana_fg", "SANA Service", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }
    private fun buildNotification(text: String): Notification = NotificationCompat.Builder(this, "sana_fg").setSmallIcon(android.R.drawable.ic_btn_speak_now).setContentTitle("SANA PRO 2.0").setContentText(text).setOngoing(true).build()
}
