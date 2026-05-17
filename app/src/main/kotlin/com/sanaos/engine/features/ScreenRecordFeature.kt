package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.sanaos.engine.SanaBrain

class ScreenRecordFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        if (intent !is SanaBrain.SanaIntent.ScreenRecord) {
            return FeatureResult(false, "Boss, screen record command samajh nahi aaya.")
        }
        return startScreenRecord(context)
    }

    private fun startScreenRecord(context: Context): FeatureResult {
        val intents = listOf(
            Intent().setClassName("com.miui.screenrecorder", "com.miui.screenrecorder.ScreenRecorderActivity"),
            Intent().setClassName("com.samsung.android.app.smartcapture", "com.samsung.android.app.screenrecorder.ScreenRecorderActivity"),
            Intent(Settings.ACTION_CAST_SETTINGS)
        )
        intents.forEach { intent ->
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return FeatureResult(true, "Boss, screen recording panel open kar diya.")
            } catch (_: Exception) {
            }
        }
        return FeatureResult(false, "Boss, is device mein direct screen record shortcut available nahi mila.")
    }
}
