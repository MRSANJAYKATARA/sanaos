package com.sanaos.engine.features

import android.content.Context
import android.media.AudioManager
import android.util.Log

class VolumeFeature(private val context: Context) : SanaFeature {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun execute(): FeatureResult = FeatureResult(true, "Volume feature ready.")

    fun setVolume(percent: Int): FeatureResult {
        return try {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val volume = (maxVolume * percent / 100).coerceIn(0, maxVolume)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
            FeatureResult(true, "Boss, volume $percent percent kar diya.")
        } catch (e: Exception) {
            Log.e("VOLUME", "Set volume error: ${e.message}", e)
            FeatureResult(false, "Boss, volume set nahi ho paya.")
        }
    }

    fun muteVolume(): FeatureResult {
        return try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
            FeatureResult(true, "Boss, volume mute kar diya.")
        } catch (e: Exception) {
            Log.e("VOLUME", "Mute error: ${e.message}", e)
            FeatureResult(false, "Boss, mute nahi ho paya.")
        }
    }

    fun maxVolume(): FeatureResult {
        return try {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)
            FeatureResult(true, "Boss, volume maximum kar diya.")
        } catch (e: Exception) {
            Log.e("VOLUME", "Max volume error: ${e.message}", e)
            FeatureResult(false, "Boss, max volume nahi ho paya.")
        }
    }
}
