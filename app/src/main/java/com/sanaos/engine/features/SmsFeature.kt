package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import kotlin.math.abs

class SmsFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "SMS feature ready.")

    fun sendSms(contactName: String, message: String): FeatureResult {
        return try {
            if (contactName.isEmpty() || message.isEmpty()) {
                return FeatureResult(false, "Boss, contact aur message dono batao.")
            }
            val phoneNumber = resolveContactName(contactName)
            if (phoneNumber.isEmpty()) {
                return FeatureResult(false, "Boss, $contactName ka number nahi mila.")
            }
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", message)
            }
            context.startActivity(intent)
            FeatureResult(true, "Boss, $contactName ko SMS bhej rahi hun.")
        } catch (e: SecurityException) {
            Log.e("SMS", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, SMS permission chahiye.")
        } catch (e: Exception) {
            Log.e("SMS", "Send SMS error: ${e.message}", e)
            FeatureResult(false, "Boss, SMS nahi bheja ja paya.")
        }
    }

    private fun resolveContactName(name: String): String {
        return try {
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
                        bestMatch = it.getString(numberColumn)
                    }
                }
            }
            bestMatch
        } catch (e: Exception) {
            Log.e("SMS", "Resolve contact error: ${e.message}", e)
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
