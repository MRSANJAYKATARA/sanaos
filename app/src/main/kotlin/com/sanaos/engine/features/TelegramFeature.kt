package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sanaos.engine.SanaBrain

class TelegramFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.OpenTelegram -> openChat(intent.username, context)
            else -> FeatureResult(false, "Boss, Telegram command samajh nahi aaya.")
        }
    }

    private fun openChat(username: String, context: Context): FeatureResult {
        val clean = username.trim().removePrefix("@")
        val primaryUri = if (clean.isBlank()) Uri.parse("tg://resolve") else Uri.parse("tg://resolve?domain=$clean")
        return try {
            val primaryIntent = Intent(Intent.ACTION_VIEW, primaryUri).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(primaryIntent)
            FeatureResult(true, "Boss, Telegram open kar diya.")
        } catch (_: Exception) {
            try {
                val fallback = context.packageManager.getLaunchIntentForPackage("org.thunderdog.challegram")
                if (fallback != null) {
                    fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(fallback)
                    FeatureResult(true, "Boss, Telegram fallback app open kar diya.")
                } else {
                    FeatureResult(false, "Boss, Telegram app available nahi mili.")
                }
            } catch (_: Exception) {
                FeatureResult(false, "Boss, Telegram open nahi ho paaya.")
            }
        }
    }
}
