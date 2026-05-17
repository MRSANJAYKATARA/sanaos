package com.sanaos.engine

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

// ════════════════════════════════════════════════════════════════════════════
// SEALED CLASS: ALL INTENTS THAT SANA CAN HANDLE
// ════════════════════════════════════════════════════════════════════════════

sealed class SanaIntent {
    // Torch/Flashlight
    object TorchOn : SanaIntent()
    object TorchOff : SanaIntent()
    object TorchToggle : SanaIntent()

    // Volume
    data class SetVolume(val percent: Int) : SanaIntent()
    object MuteVolume : SanaIntent()
    object MaxVolume : SanaIntent()

    // Brightness
    data class SetBrightness(val percent: Int) : SanaIntent()
    object AutoBrightness : SanaIntent()
    object MaxBrightness : SanaIntent()
    object MinBrightness : SanaIntent()

    // Screen Control
    object LockScreen : SanaIntent()
    object Screenshot : SanaIntent()
    object ScreenRecord : SanaIntent()

    // Connectivity
    object WifiOn : SanaIntent()
    object WifiOff : SanaIntent()
    object BluetoothOn : SanaIntent()
    object BluetoothOff : SanaIntent()
    object MobileDataOn : SanaIntent()
    object MobileDataOff : SanaIntent()
    object AirplaneOn : SanaIntent()
    object AirplaneOff : SanaIntent()

    // Calls
    object AnswerCall : SanaIntent()
    object RejectCall : SanaIntent()
    data class DialCall(val contact: String) : SanaIntent()

    // WhatsApp
    data class SendWhatsApp(val contact: String, val message: String) : SanaIntent()
    object OpenWhatsApp : SanaIntent()
    data class WhatsAppAudioCall(val contact: String) : SanaIntent()
    data class WhatsAppVideoCall(val contact: String) : SanaIntent()

    // SMS
    data class SendSms(val contact: String, val message: String) : SanaIntent()

    // Telegram
    data class OpenTelegram(val username: String) : SanaIntent()

    // Media
    data class PlaySpotify(val query: String) : SanaIntent()
    data class PlayYoutube(val query: String) : SanaIntent()

    // Camera
    object OpenCamera : SanaIntent()
    object TakePhoto : SanaIntent()
    object FlipCamera : SanaIntent()

    // Navigation & Maps
    data class NavigateTo(val destination: String) : SanaIntent()
    data class SearchNearby(val type: String) : SanaIntent()
    data class ViewOnMap(val query: String) : SanaIntent()

    // App Launcher
    data class LaunchApp(val appName: String) : SanaIntent()

    // System Info Queries
    object QueryBattery : SanaIntent()
    object QueryRam : SanaIntent()
    object QueryStorage : SanaIntent()
    object QueryLocation : SanaIntent()
    object QueryWeather : SanaIntent()
    object QueryTime : SanaIntent()
    object QueryDate : SanaIntent()
    object QueryNetworkStatus : SanaIntent()

    // Screen Reading & Scrolling
    object ReadScreen : SanaIntent()
    object ScrollDown : SanaIntent()
    object ScrollUp : SanaIntent()
    object ScrollToTop : SanaIntent()
    object ScrollToBottom : SanaIntent()

    // Reminders
    data class SetReminder(val label: String, val naturalTimeText: String) : SanaIntent()

    // Fallback: Just converse
    data class Converse(val reply: String) : SanaIntent()
}

// ════════════════════════════════════════════════════════════════════════════
// SANA BRAIN: GEMINI API + INTENT PARSING
// ════════════════════════════════════════════════════════════════════════════

class SanaBrain(private val apiKey: String) {

    private val okHttpClient = OkHttpClient()
    private val geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent?key=$apiKey"

    private val systemPrompt = """Tu SANA hai — Sanjay Katara ki AI assistant.
        Sanjay Boss ki har baat Hinglish mein samajh aur jawab de.
        IMPORTANT: Sirf ek JSON object return kar, koi markdown nahi, koi backticks nahi.
        Format:
        {"action":"ACTION_NAME","param1":"...","reply":"Hinglish mein response..."}
        Agar koi system action nahi, action="CONVERSE" use kar.
        reply field mein natural commas, ellipses (...) aur exclamation (!) use kar.
        Reply max 2-3 sentences. Professional, warm, loyal.
    """.trimIndent()

    fun processQuery(userText: String): Pair<SanaIntent, String> {
        return try {
            val (intent, reply) = callGemini(userText)
            Pair(intent, reply)
        } catch (e: Exception) {
            Log.e("SANA_BRAIN", "Process query error: ${e.message}", e)
            Pair(
                SanaIntent.Converse(userText),
                "Boss, kuch to gadbad ho gaya. Dobara koshish kar."
            )
        }
    }

    private fun callGemini(userText: String): Pair<SanaIntent, String> {
        return try {
            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", userText)
                            })
                        })
                        put("role", "user")
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemPrompt)
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.9)
                    put("maxOutputTokens", 200)
                })
            }.toString()

            val request = Request.Builder()
                .url(geminiUrl)
                .post(requestBody.toRequestBody())
                .header("Content-Type", "application/json")
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: "no body"
                Log.e("SANA_BRAIN", "Gemini HTTP ${response.code}: $errBody")
                return Pair(
                    SanaIntent.Converse(userText),
                    "Boss, Gemini API fail ho gaya. Error: ${response.code}"
                )
            }

            val responseBody = response.body?.string() ?: "{}"
            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val firstPart = parts?.optJSONObject(0)
            val responseText = firstPart?.optString("text", "") ?: ""

            Log.d("SANA_BRAIN", "Gemini raw response: $responseText")

            val intent = parseActionFromJson(responseText, userText)
            val reply = when (intent) {
                is SanaIntent.Converse -> intent.reply
                else -> extractReplyFromJson(responseText, "Action initiated.")
            }

            Pair(intent, reply)
        } catch (e: Exception) {
            Log.e("SANA_BRAIN", "Gemini call error: ${e.message}", e)
            Pair(
                SanaIntent.Converse(userText),
                "Boss, network error aaya: ${e.message}"
            )
        }
    }

    private fun parseActionFromJson(jsonText: String, userText: String): SanaIntent {
        return try {
            val obj = JSONObject(jsonText)
            val action = obj.optString("action", "CONVERSE").uppercase()
            when (action) {
                "TORCH_ON" -> SanaIntent.TorchOn
                "TORCH_OFF" -> SanaIntent.TorchOff
                "TORCH_TOGGLE" -> SanaIntent.TorchToggle
                "SET_VOLUME" -> SanaIntent.SetVolume(obj.optInt("percent", 50))
                "MUTE_VOLUME" -> SanaIntent.MuteVolume
                "MAX_VOLUME" -> SanaIntent.MaxVolume
                "SET_BRIGHTNESS" -> SanaIntent.SetBrightness(obj.optInt("percent", 50))
                "AUTO_BRIGHTNESS" -> SanaIntent.AutoBrightness
                "MAX_BRIGHTNESS" -> SanaIntent.MaxBrightness
                "MIN_BRIGHTNESS" -> SanaIntent.MinBrightness
                "LOCK_SCREEN" -> SanaIntent.LockScreen
                "SCREENSHOT" -> SanaIntent.Screenshot
                "SCREEN_RECORD" -> SanaIntent.ScreenRecord
                "WIFI_ON" -> SanaIntent.WifiOn
                "WIFI_OFF" -> SanaIntent.WifiOff
                "BLUETOOTH_ON" -> SanaIntent.BluetoothOn
                "BLUETOOTH_OFF" -> SanaIntent.BluetoothOff
                "MOBILE_DATA_ON" -> SanaIntent.MobileDataOn
                "MOBILE_DATA_OFF" -> SanaIntent.MobileDataOff
                "AIRPLANE_ON" -> SanaIntent.AirplaneOn
                "AIRPLANE_OFF" -> SanaIntent.AirplaneOff
                "ANSWER_CALL" -> SanaIntent.AnswerCall
                "REJECT_CALL" -> SanaIntent.RejectCall
                "DIAL_CALL" -> SanaIntent.DialCall(obj.optString("contact", ""))
                "SEND_WHATSAPP" -> SanaIntent.SendWhatsApp(obj.optString("contact", ""), obj.optString("message", ""))
                "OPEN_WHATSAPP" -> SanaIntent.OpenWhatsApp
                "WHATSAPP_AUDIO_CALL" -> SanaIntent.WhatsAppAudioCall(obj.optString("contact", ""))
                "WHATSAPP_VIDEO_CALL" -> SanaIntent.WhatsAppVideoCall(obj.optString("contact", ""))
                "SEND_SMS" -> SanaIntent.SendSms(obj.optString("contact", ""), obj.optString("message", ""))
                "OPEN_TELEGRAM" -> SanaIntent.OpenTelegram(obj.optString("username", ""))
                "PLAY_SPOTIFY" -> SanaIntent.PlaySpotify(obj.optString("query", ""))
                "PLAY_YOUTUBE" -> SanaIntent.PlayYoutube(obj.optString("query", ""))
                "OPEN_CAMERA" -> SanaIntent.OpenCamera
                "TAKE_PHOTO" -> SanaIntent.TakePhoto
                "FLIP_CAMERA" -> SanaIntent.FlipCamera
                "NAVIGATE_TO" -> SanaIntent.NavigateTo(obj.optString("destination", ""))
                "SEARCH_NEARBY" -> SanaIntent.SearchNearby(obj.optString("type", ""))
                "VIEW_ON_MAP" -> SanaIntent.ViewOnMap(obj.optString("query", ""))
                "LAUNCH_APP" -> SanaIntent.LaunchApp(obj.optString("appName", ""))
                "QUERY_BATTERY" -> SanaIntent.QueryBattery
                "QUERY_RAM" -> SanaIntent.QueryRam
                "QUERY_STORAGE" -> SanaIntent.QueryStorage
                "QUERY_LOCATION" -> SanaIntent.QueryLocation
                "QUERY_WEATHER" -> SanaIntent.QueryWeather
                "QUERY_TIME" -> SanaIntent.QueryTime
                "QUERY_DATE" -> SanaIntent.QueryDate
                "QUERY_NETWORK_STATUS" -> SanaIntent.QueryNetworkStatus
                "READ_SCREEN" -> SanaIntent.ReadScreen
                "SCROLL_DOWN" -> SanaIntent.ScrollDown
                "SCROLL_UP" -> SanaIntent.ScrollUp
                "SCROLL_TOP" -> SanaIntent.ScrollToTop
                "SCROLL_BOTTOM" -> SanaIntent.ScrollToBottom
                "SET_REMINDER" -> SanaIntent.SetReminder(obj.optString("label", ""), obj.optString("naturalTimeText", ""))
                else -> SanaIntent.Converse(obj.optString("reply", userText))
            }
        } catch (e: Exception) {
            Log.e("SANA_BRAIN", "JSON parse error: ${e.message}", e)
            SanaIntent.Converse(userText)
        }
    }

    private fun extractReplyFromJson(jsonText: String, default: String): String {
        return try {
            val obj = JSONObject(jsonText)
            obj.optString("reply", default)
        } catch (e: Exception) {
            default
        }
    }
}
