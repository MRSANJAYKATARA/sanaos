package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.util.Log
import kotlin.math.abs

class CallFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "Call feature ready.")

    fun answerCall(): FeatureResult {
        return try {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.acceptRingingCall()
            FeatureResult(true, "Boss, call accept kar diya.")
        } catch (e: SecurityException) {
            Log.e("CALL", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, call accept permission chahiye.")
        } catch (e: Exception) {
            Log.e("CALL", "Answer call error: ${e.message}", e)
            FeatureResult(false, "Boss, call answer nahi ho paya.")
        }
    }

    fun rejectCall(): FeatureResult {
        return try {
            FeatureResult(true, "Boss, call reject kar diya.")
        } catch (e: Exception) {
            Log.e("CALL", "Reject call error: ${e.message}", e)
            FeatureResult(false, "Boss, call reject nahi ho paya.")
        }
    }

    fun dialCall(contactName: String): FeatureResult {
        return try {
            if (contactName.isEmpty()) {
                return FeatureResult(false, "Boss, kisko call karun?")
            }
            val phoneNumber = resolveContactName(contactName)
            if (phoneNumber.isEmpty()) {
                return FeatureResult(false, "Boss, $contactName ka number nahi mila.")
            }
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            context.startActivity(intent)
            FeatureResult(true, "Boss, $contactName ko call kar rahi hun.")
        } catch (e: SecurityException) {
            Log.e("CALL", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, call permission chahiye.")
        } catch (e: Exception) {
            Log.e("CALL", "Dial call error: ${e.message}", e)
            FeatureResult(false, "Boss, call nahi lag paya.")
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
            Log.e("CALL", "Resolve contact error: ${e.message}", e)
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
