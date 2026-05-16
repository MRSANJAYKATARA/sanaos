package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sanaos.engine.SanaBrain
import java.net.URLEncoder

class WhatsAppFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.SendWhatsApp -> sendMessage(intent.contact, intent.message, context)
            is SanaBrain.SanaIntent.OpenWhatsApp -> openWhatsApp(context)
            is SanaBrain.SanaIntent.WhatsAppAudioCall -> openCall(intent.contact, false, context)
            is SanaBrain.SanaIntent.WhatsAppVideoCall -> openCall(intent.contact, true, context)
            else -> FeatureResult(false, "Boss, WhatsApp command samajh nahi aaya.")
        }
    }

    private fun sendMessage(contact: String, message: String, context: Context): FeatureResult {
        return try {
            val phone = contact.filter { it.isDigit() || it == '+' }
            val encodedText = URLEncoder.encode(message, "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$phone&text=$encodedText"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(intent)
            FeatureResult(true, "Boss, WhatsApp message screen open kar di.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, WhatsApp message send flow open nahi ho paaya.")
        }
    }

    private fun openWhatsApp(context: Context): FeatureResult {
        return try {
            val launch = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                ?: context.packageManager.getLaunchIntentForPackage("com.whatsapp.w4b")
            if (launch == null) return FeatureResult(false, "Boss, WhatsApp install nahi hai.")
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launch)
            FeatureResult(true, "Boss, WhatsApp open kar diya.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, WhatsApp open nahi ho paaya.")
        }
    }

    private fun openCall(contact: String, video: Boolean, context: Context): FeatureResult {
        return try {
            val phone = contact.filter { it.isDigit() || it == '+' }
            val base = if (video) "https://wa.me/$phone?call=video" else "https://wa.me/$phone?call=voice"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(base)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(intent)
            FeatureResult(true, if (video) "Boss, WhatsApp video call flow open kar diya." else "Boss, WhatsApp audio call flow open kar diya.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, WhatsApp call flow open nahi ho paaya.")
        }
    }
}
