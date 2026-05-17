package com.sanaos.engine.features

import android.content.Context
import android.media.AudioManager
import com.sanaos.engine.SanaBrain

class VolumeFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.SetVolume -> setVolume(intent.percent, context)
            is SanaBrain.SanaIntent.MuteVolume -> muteVolume(context)
            is SanaBrain.SanaIntent.MaxVolume -> maxVolume(context)
            is SanaBrain.SanaIntent.SetSoundProfile -> setSoundProfile(intent.profile, context)
            else -> FeatureResult(false, "Boss, volume command samajh nahi aaya.")
        }
    }

    private fun setVolume(percent: Int, context: Context): FeatureResult {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val safePercent = percent.coerceIn(0, 100)
            val mediaMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val ringMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
            val mediaValue = (mediaMax * (safePercent / 100f)).toInt().coerceAtLeast(0)
            val ringValue = (ringMax * (safePercent / 100f)).toInt().coerceAtLeast(0)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediaValue, 0)
            audioManager.setStreamVolume(AudioManager.STREAM_RING, ringValue, 0)
            FeatureResult(true, "Boss, volume $safePercent% set kar diya.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, volume set nahi ho paaya.")
        }
    }

    private fun muteVolume(context: Context): FeatureResult = setVolume(0, context)

    private fun maxVolume(context: Context): FeatureResult = setVolume(100, context)

    private fun setSoundProfile(profile: String, context: Context): FeatureResult {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            when (profile.lowercase()) {
                "silent" -> audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                "vibrate" -> audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                else -> audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
            FeatureResult(true, "Boss, sound profile ${profile.lowercase()} kar diya.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, sound profile change nahi ho paaya.")
        }
    }
}
