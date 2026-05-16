package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sanaos.engine.SanaBrain

class SmsFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.SendSms -> composeSms(intent.contact, intent.message, context)
            else -> FeatureResult(false, "Boss, SMS command samajh nahi aaya.")
        }
    }

    private fun composeSms(contact: String, body: String, context: Context): FeatureResult {
        return try {
            val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${Uri.encode(contact)}")).apply {
                putExtra("sms_body", body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(smsIntent)
            FeatureResult(true, "Boss, SMS composer open kar diya.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, SMS compose nahi ho paaya.")
        }
    }
}
