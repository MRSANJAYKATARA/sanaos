package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.sanaos.engine.SanaBrain

class BrightnessFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.SetBrightness -> setBrightness(intent.percent, context)
            is SanaBrain.SanaIntent.AutoBrightness -> setAutoBrightness(context)
            is SanaBrain.SanaIntent.MaxBrightness -> setBrightness(100, context)
            is SanaBrain.SanaIntent.MinBrightness -> setBrightness(5, context)
            else -> FeatureResult(false, "Boss, brightness command samajh nahi aaya.")
        }
    }

    private fun setBrightness(percent: Int, context: Context): FeatureResult {
        if (!Settings.System.canWrite(context)) {
            openWriteSettings(context)
            return FeatureResult(false, "Boss, brightness change ke liye settings permission allow karo.")
        }
        return try {
            val safe = percent.coerceIn(0, 100)
            val value255 = ((safe / 100f) * 255f).toInt().coerceIn(0, 255)
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, value255)
            FeatureResult(true, "Boss, brightness $safe% kar di.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, brightness set nahi ho paayi.")
        }
    }

    private fun setAutoBrightness(context: Context): FeatureResult {
        if (!Settings.System.canWrite(context)) {
            openWriteSettings(context)
            return FeatureResult(false, "Boss, auto brightness ke liye settings permission allow karo.")
        }
        return try {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
            FeatureResult(true, "Boss, auto brightness enable kar di.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, auto brightness enable nahi ho paayi.")
        }
    }

    private fun openWriteSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }
}
