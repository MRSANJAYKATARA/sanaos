package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.sanaos.data.SanaDatabaseHelper
import kotlin.math.abs

class WhatsAppFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = openWhatsApp()

    fun openWhatsApp(): FeatureResult {
        return try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage("com.whatsapp")
            if (intent != null) {
                context.startActivity(intent)
                FeatureResult(true, "Boss, WhatsApp khol diya.")
            } else {
                FeatureResult(false, "Boss, WhatsApp app nahi mila.")
            }
        } catch (e: Exception) {
            Log.e("WHATSAPP", "Open WhatsApp error: ${e.message}", e)
            FeatureResult(false, "Boss, WhatsApp nahi khul paya.")
        }
    }

    fun sendMessage(contactName: String, message: String): FeatureResult {
        return try {
            if (contactName.isEmpty() || message.isEmpty()) {
                return FeatureResult(false, "Boss, contact aur message dono batao.")
            }
            val phoneNumber = resolveContactName(contactName)
            if (phoneNumber.isEmpty()) {
                return FeatureResult(false, "Boss, $contactName ka number nahi mila.")
            }
            val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            FeatureResult(true, "Boss, $contactName ko WhatsApp message bheej rahi hun.")
        } catch (e: Exception) {
            Log.e("WHATSAPP", "Send message error: ${e.message}", e)
            FeatureResult(false, "Boss, message nahi bheja ja paya.")
        }
    }

    fun audioCall(contactName: String): FeatureResult {
        return try {
            if (contactName.isEmpty()) {
                return FeatureResult(false, "Boss, kisko call karun?")
            }
            val phoneNumber = resolveContactName(contactName)
            if (phoneNumber.isEmpty()) {
                return FeatureResult(false, "Boss, $contactName ka number nahi mila.")
            }
            val url = "https://wa.me/$phoneNumber"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            FeatureResult(true, "Boss, $contactName ko WhatsApp audio call kar rahi hun.")
        } catch (e: Exception) {
            Log.e("WHATSAPP", "Audio call error: ${e.message}", e)
            FeatureResult(false, "Boss, call nahi lag paya.")
        }
    }

    fun videoCall(contactName: String): FeatureResult {
        return try {
            if (contactName.isEmpty()) {
                return FeatureResult(false, "Boss, kisko call karun?")
            }
            val phoneNumber = resolveContactName(contactName)
            if (phoneNumber.isEmpty()) {
                return FeatureResult(false, "Boss, $contactName ka number nahi mila.")
            }
            val url = "https://wa.me/$phoneNumber"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            FeatureResult(true, "Boss, $contactName ko WhatsApp video call kar rahi hun.")
        } catch (e: Exception) {
            Log.e("WHATSAPP", "Video call error: ${e.message}", e)
            FeatureResult(false, "Boss, call nahi lag paya.")
        }
    }

    private fun resolveContactName(name: String): String {
        return try {
            // First try system contacts
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                null,
                null,
                null
            )
            var bestMatch = ""
            var bestScore = Int.MAX_VALUE
            cursor?.use {
                val numberColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val nameColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                while (it.moveToNext()) {
                    val contactName = it.getString(nameColumn)
                    val score = fuzzyScore(name, contactName)
                    if (score < bestScore) {
                        bestScore = score
                        bestMatch = it.getString(numberColumn).filter { c -> c.isDigit() || c == '+' }
                    }
                }
            }
            if (bestMatch.isNotEmpty()) return bestMatch

            // Fallback: try SQLite contacts_memory table
            val db = SanaDatabaseHelper(context)
            val allContacts = db.readableDatabase.rawQuery(
                "SELECT phone FROM contacts_memory WHERE name LIKE ?",
                arrayOf("%$name%")
            )
            allContacts?.use {
                if (it.moveToFirst()) {
                    return it.getString(0).filter { c -> c.isDigit() || c == '+' }
                }
            }
            ""
        } catch (e: Exception) {
            Log.e("WHATSAPP", "Resolve contact error: ${e.message}", e)
            ""
        }
    }

    private fun fuzzyScore(search: String, target: String): Int {
        var score = 0
        var searchIdx = 0
        for (c in target.lowercase()) {
            if (searchIdx < search.length && c == search[searchIdx].lowercaseChar()) {
                searchIdx++
            } else {
                score++
            }
        }
        return score + abs(search.length - target.length)
    }
}
