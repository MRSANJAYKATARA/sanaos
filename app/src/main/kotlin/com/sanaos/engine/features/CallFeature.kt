package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.TelecomManager
import com.sanaos.data.SanaDatabaseHelper
import com.sanaos.engine.SanaBrain

class CallFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.AnswerCall -> answerCall(context)
            is SanaBrain.SanaIntent.RejectCall -> rejectCall(context)
            is SanaBrain.SanaIntent.DialCall -> dialCall(intent.contact, context)
            else -> FeatureResult(false, "Boss, call command samajh nahi aaya.")
        }
    }

    private fun answerCall(context: Context): FeatureResult {
        return try {
            val telecom = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecom.acceptRingingCall()
            FeatureResult(true, "Boss, incoming call answer kar di.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, call answer nahi ho paayi.")
        }
    }

    private fun rejectCall(context: Context): FeatureResult {
        return try {
            val telecom = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecom.endCall()
            FeatureResult(true, "Boss, call reject kar di.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, call reject nahi ho paayi.")
        }
    }

    private fun dialCall(contact: String, context: Context): FeatureResult {
        val number = resolveContact(contact, context)
        if (number.isNullOrBlank()) {
            return FeatureResult(false, "Boss, $contact ka number nahi mila.")
        }
        return try {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            FeatureResult(true, "Boss, $contact ko call laga rahi hun.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, call place nahi ho paayi.")
        }
    }

    private fun resolveContact(name: String, context: Context): String? {
        val cleaned = name.trim()
        if (cleaned.isBlank()) return null

        try {
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
            )
            cursor?.use {
                var bestNumber: String? = null
                var bestScore = -1
                while (it.moveToNext()) {
                    val display = it.getString(0).orEmpty()
                    val number = it.getString(1).orEmpty()
                    val score = fuzzyScore(cleaned, display)
                    if (score > bestScore && number.isNotBlank()) {
                        bestScore = score
                        bestNumber = number
                    }
                }
                if (!bestNumber.isNullOrBlank()) return bestNumber
            }
        } catch (_: Exception) {
        }

        return resolveFromMemory(cleaned, context)
    }

    private fun resolveFromMemory(name: String, context: Context): String? {
        return try {
            val db = SanaDatabaseHelper(context).readableDatabase
            db.query(
                "contacts_memory",
                arrayOf("name", "phone"),
                null,
                null,
                null,
                null,
                "priority DESC"
            ).use { cursor ->
                var bestNumber: String? = null
                var bestScore = -1
                while (cursor.moveToNext()) {
                    val n = cursor.getString(0).orEmpty()
                    val p = cursor.getString(1).orEmpty()
                    val score = fuzzyScore(name, n)
                    if (score > bestScore && p.isNotBlank()) {
                        bestScore = score
                        bestNumber = p
                    }
                }
                bestNumber
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun fuzzyScore(query: String, candidate: String): Int {
        val q = query.lowercase()
        val c = candidate.lowercase()
        return when {
            c == q -> 100
            c.startsWith(q) -> 80
            c.contains(q) -> 60
            q.split(" ").all { it.isNotBlank() && c.contains(it) } -> 40
            else -> 0
        }
    }
}
