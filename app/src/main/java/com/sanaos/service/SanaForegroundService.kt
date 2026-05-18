package com.sanaos.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sanaos.data.SharedPrefsManager
import java.util.*

class SanaForegroundService : Service() {

    companion object {
        const val ACTION_START = "com.sanaos.action.START"
        const val ACTION_STOP = "com.sanaos.action.STOP"

        const val BROADCAST_STATE = "com.sanaos.ACTION_STATE"
        const val ACTION_TRANSCRIPT = "com.sanaos.ACTION_TRANSCRIPT"
        const val ACTION_RMS = "com.sanaos.ACTION_RMS"

        const val EXTRA_STATE = "state"
        const val STATE_LISTENING = "LISTENING"
        const val STATE_THINKING = "THINKING"
        const val STATE_SPEAKING = "SPEAKING"
        const val STATE_IDLE = "IDLE"
        const val STATE_STOPPED = "STOPPED"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var isRunning = false
    private var audioManager: AudioManager? = null
    private var savedVolumeMusic = -1
    private var savedVolumeRing = -1

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    unmuteAfterListening()
                    sendStateBroadcast(STATE_LISTENING)
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {
                    try {
                        val intent = Intent(ACTION_RMS)
                        intent.putExtra("rms", rmsdB)
                        LocalBroadcastManager.getInstance(this@SanaForegroundService).sendBroadcast(intent)
                    } catch (e: Exception) {
                        Log.e("SANA_FG", "RMS broadcast error: ${e.message}", e)
                    }
                }

                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    unmuteAfterListening()
                }

                override fun onError(error: Int) {
                    Log.e("SANA_FG", "SpeechRecognizer error: $error")
                    unmuteAfterListening()
                    sendStateBroadcast(STATE_IDLE)
                }

                override fun onResults(results: Bundle?) {
                    try {
                        unmuteAfterListening()
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.joinToString(separator = " ") ?: ""
                        val intent = Intent(ACTION_TRANSCRIPT)
                        intent.putExtra("transcript", text)
                        LocalBroadcastManager.getInstance(this@SanaForegroundService).sendBroadcast(intent)
                        sendStateBroadcast(STATE_THINKING)
                    } catch (e: Exception) {
                        Log.e("SANA_FG", "onResults error: ${e.message}", e)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } catch (e: Exception) {
            Log.e("SANA_FG", "SpeechRecognizer init error: ${e.message}", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val action = intent?.action
            if (action == ACTION_START) {
                if (!isRunning) {
                    startForegroundServiceWithNotification()
                    isRunning = true
                    startListeningLoop()
                }
            } else if (action == ACTION_STOP) {
                stopListening()
                stopForeground(true)
                stopSelf()
                isRunning = false
                sendStateBroadcast(STATE_STOPPED)
            }
        } catch (e: Exception) {
            Log.e("SANA_FG", "onStartCommand error: ${e.message}", e)
        }
        return START_STICKY
    }

    private fun startForegroundServiceWithNotification() {
        try {
            val channelId = "sana_fg"
            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("SANA PRO")
                .setContentText("Voice assistant running")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setOngoing(true)
                .build()

            startForeground(1201, notification)
            sendStateBroadcast(STATE_IDLE)
        } catch (e: Exception) {
            Log.e("SANA_FG", "Start foreground error: ${e.message}", e)
        }
    }

    private fun startListeningLoop() {
        try {
            muteForListening()
            val prefsLang = SharedPrefsManager.get(this, SharedPrefsManager.Keys.ASSISTANT_LANG, "")
            val primaryLang: String
            val secondaryLang: String
            if (prefsLang.equals("english", ignoreCase = true)) {
                primaryLang = "en-IN"
                secondaryLang = "hi-IN"
            } else {
                primaryLang = "hi-IN"
                secondaryLang = "en-IN"
            }

            val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, primaryLang)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, primaryLang)
                putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf(secondaryLang))
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, primaryLang)
            }

            speechRecognizer?.startListening(recognizerIntent)
        } catch (se: SecurityException) {
            Log.e("SANA_FG", "Security error starting STT: ${se.message}", se)
            sendStateBroadcast(STATE_IDLE)
        } catch (e: Exception) {
            Log.e("SANA_FG", "Start listening error: ${e.message}", e)
            sendStateBroadcast(STATE_IDLE)
        }
    }

    private fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            unmuteAfterListening()
        } catch (e: Exception) {
            Log.e("SANA_FG", "Stop listening error: ${e.message}", e)
        }
    }

    private fun muteForListening() {
        try {
            savedVolumeMusic = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: -1
            savedVolumeRing = audioManager?.getStreamVolume(AudioManager.STREAM_RING) ?: -1
            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
            audioManager?.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
        } catch (e: Exception) {
            Log.w("SANA_STT", "mute: ${e.message}")
        }
    }

    private fun unmuteAfterListening() {
        try {
            if (savedVolumeMusic >= 0) audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, savedVolumeMusic, 0)
            if (savedVolumeRing >= 0) audioManager?.setStreamVolume(AudioManager.STREAM_RING, savedVolumeRing, 0)
        } catch (e: Exception) {
            Log.w("SANA_STT", "unmute: ${e.message}")
        }
    }

    private fun sendStateBroadcast(state: String) {
        try {
            val intent = Intent(BROADCAST_STATE)
            intent.putExtra(EXTRA_STATE, state)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } catch (e: Exception) {
            Log.e("SANA_FG", "State broadcast error: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SANA_FG", "Destroy error: ${e.message}", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
