package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.util.Log

class ScreenLockFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = lockScreen()

    fun lockScreen(): FeatureResult {
        return try {
            val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context.sendBroadcast(intent)
            FeatureResult(true, "Boss, screen lock kar diya.")
        } catch (e: Exception) {
            Log.e("SCREEN_LOCK", "Lock screen error: ${e.message}", e)
            FeatureResult(false, "Boss, screen lock nahi ho paya.")
        }
    }
}
