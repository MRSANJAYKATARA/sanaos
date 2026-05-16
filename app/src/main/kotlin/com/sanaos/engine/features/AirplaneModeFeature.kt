package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.sanaos.engine.SanaBrain

class AirplaneModeFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.AirplaneOn -> setAirplaneMode(true, context)
            is SanaBrain.SanaIntent.AirplaneOff -> setAirplaneMode(false, context)
            else -> FeatureResult(false, "Boss, airplane mode command samajh nahi aaya.")
        }
    }

    private fun setAirplaneMode(enable: Boolean, context: Context): FeatureResult {
        return try {
            // Requires: adb shell pm grant com.sanaos android.permission.WRITE_SECURE_SETTINGS
            Settings.Global.putInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, if (enable) 1 else 0)
            val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).apply {
                putExtra("state", enable)
                addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)
            }
            context.sendBroadcast(intent)
            FeatureResult(true, if (enable) "Boss, airplane mode on kar diya." else "Boss, airplane mode off kar diya.")
        } catch (_: Exception) {
            try {
                val fallback = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
                FeatureResult(false, "Boss, direct airplane switch allow nahi hua. Settings khol di hai.")
            } catch (_: Exception) {
                FeatureResult(false, "Boss, airplane mode control nahi ho paaya.")
            }
        }
    }
}
