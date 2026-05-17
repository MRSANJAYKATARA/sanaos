package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.util.Log

class AirplaneModeFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "Airplane mode feature ready.")

    fun airplaneOn(): FeatureResult {
        return try {
            android.provider.Settings.Global.putInt(context.contentResolver, android.provider.Settings.Global.AIRPLANE_MODE_ON, 1)
            val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).apply {
                putExtra("state", true)
            }
            context.sendBroadcast(intent)
            FeatureResult(true, "Boss, airplane mode on kar diya.")
        } catch (e: Exception) {
            Log.e("AIRPLANE_MODE", "Airplane on error: ${e.message}", e)
            FeatureResult(false, "Boss, airplane mode on nahi ho paya.")
        }
    }

    fun airplaneOff(): FeatureResult {
        return try {
            android.provider.Settings.Global.putInt(context.contentResolver, android.provider.Settings.Global.AIRPLANE_MODE_ON, 0)
            val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).apply {
                putExtra("state", false)
            }
            context.sendBroadcast(intent)
            FeatureResult(true, "Boss, airplane mode off kar diya.")
        } catch (e: Exception) {
            Log.e("AIRPLANE_MODE", "Airplane off error: ${e.message}", e)
            FeatureResult(false, "Boss, airplane mode off nahi ho paya.")
        }
    }
}
