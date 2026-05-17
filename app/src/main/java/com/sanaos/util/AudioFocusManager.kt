package com.sanaos.util

import android.content.Context
import android.media.AudioManager
import android.util.Log

class AudioFocusManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioManager.OnAudioFocusChangeListener? = null

    fun requestAudioFocus(): Boolean {
        return try {
            val result = audioManager.requestAudioFocus(
                { focusChange ->
                    Log.d("AUDIO_FOCUS", "Focus change: $focusChange")
                }.also { audioFocusRequest = it },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } catch (e: Exception) {
            Log.e("AUDIO_FOCUS", "Error requesting focus: ${e.message}", e)
            false
        }
    }

    fun abandonAudioFocus() {
        try {
            audioFocusRequest?.let { listener ->
                audioManager.abandonAudioFocus(listener)
                audioFocusRequest = null
            }
        } catch (e: Exception) {
            Log.e("AUDIO_FOCUS", "Error abandoning focus: ${e.message}", e)
        }
    }
}
