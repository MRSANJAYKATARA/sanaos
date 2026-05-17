package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class TelegramFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "Telegram feature ready.")

    fun openTelegram(username: String): FeatureResult {
        return try {
            if (username.isEmpty()) {
                return FeatureResult(false, "Boss, kaunsa user open karun?")
            }
            val url = "https://t.me/$username"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            FeatureResult(true, "Boss, Telegram mein $username ko dhundh rahi hun.")
        } catch (e: Exception) {
            Log.e("TELEGRAM", "Open Telegram error: ${e.message}", e)
            FeatureResult(false, "Boss, Telegram nahi khul paya.")
        }
    }
}
