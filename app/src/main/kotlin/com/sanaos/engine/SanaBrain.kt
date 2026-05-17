package com.sanaos.engine

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.sanaos.data.SharedPrefsManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object SanaBrain {
    private const val MODEL = "gemini-2.0-flash-lite"
    private const val HISTORY_LIMIT = 10
    private val conversationHistory = ArrayDeque<Pair<String, String>>()

    sealed class SanaIntent {
        object TorchOn : SanaIntent()
        object TorchOff : SanaIntent()
        object TorchToggle : SanaIntent()
        data class SetVolume(val percent: Int) : SanaIntent()
        object MuteVolume : SanaIntent()
        object MaxVolume : SanaIntent()
        data class SetSoundProfile(val profile: String) : SanaIntent()
        data class SetBrightness(val percent: Int) : SanaIntent()
        object AutoBrightness : SanaIntent()
        object MaxBrightness : SanaIntent()
        object MinBrightness : SanaIntent()
        object LockScreen : SanaIntent()
        object Screenshot : SanaIntent()
        object ScreenRecord : SanaIntent()
        object WifiOn : SanaIntent()
        object WifiOff : SanaIntent()
        object BluetoothOn : SanaIntent()
        object BluetoothOff : SanaIntent()
        object MobileDataOn : SanaIntent()
        object MobileDataOff : SanaIntent()
        object AirplaneOn : SanaIntent()
        object AirplaneOff : SanaIntent()
        object AnswerCall : SanaIntent()
        object RejectCall : SanaIntent()
        data class DialCall(val contact: String) : SanaIntent()
        data class SendWhatsApp(val contact: String, val message: String) : SanaIntent()
        object OpenWhatsApp : SanaIntent()
        data class WhatsAppAudioCall(val contact: String) : SanaIntent()
        data class WhatsAppVideoCall(val contact: String) : SanaIntent()
        data class SendSms(val contact: String, val message: String) : SanaIntent()
        data class OpenTelegram(val username: String = "") : SanaIntent()
        data class PlaySpotify(val query: String) : SanaIntent()
        data class PlayYoutube(val query: String) : SanaIntent()
        object OpenCamera : SanaIntent()
        object TakePhoto : SanaIntent()
        object FlipCamera : SanaIntent()
        data class NavigateTo(val destination: String) : SanaIntent()
        data class SearchNearby(val type: String) : SanaIntent()
        data class ViewOnMap(val query: String) : SanaIntent()
        data class LaunchApp(val appName: String) : SanaIntent()
        object QueryBattery : SanaIntent()
        object QueryRam : SanaIntent()
        object QueryStorage : SanaIntent()
        object QueryLocation : SanaIntent()
        object QueryWeather : SanaIntent()
        object QueryTime : SanaIntent()
        object QueryDate : SanaIntent()
        object QueryNetworkStatus : SanaIntent()
        object ReadScreen : SanaIntent()
        object ScrollDown : SanaIntent()
        object ScrollUp : SanaIntent()
        object ScrollToTop : SanaIntent()
        object ScrollToBottom : SanaIntent()
        data class SetReminder(val label: String, val naturalTimeText: String) : SanaIntent()
        data class Converse(val text: String) : SanaIntent()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun processQuery(userText: String, context: Context, callback: (SanaIntent, String) -> Unit) {
        if (!isNetworkAvailable(context)) {
            callback(
                SanaIntent.Converse("offline"),
                "Boss, internet connection nahi hai. Main abhi sirf offline commands kar sakti hun."
            )
            return
        }

        val fastIntent = parseOfflineIntent(userText)
        if (fastIntent !is SanaIntent.Converse || fastIntent.text != userText) {
            callback(fastIntent, "Command samajh gayi, Boss.")
            return
        }

        val geminiKey = SharedPrefsManager.get(context, SharedPrefsManager.Keys.GEMINI_API_KEY)
        if (geminiKey.isNullOrBlank()) {
            callback(SanaIntent.Converse(userText), "Boss, Gemini API key set nahi hai settings mein.")
            return
        }

        Thread {
            val aiReply = callGemini(userText, geminiKey, context)
            val intent = parseOfflineIntent(userText)
            synchronized(conversationHistory) {
                conversationHistory.addLast(userText to aiReply)
                while (conversationHistory.size > HISTORY_LIMIT) conversationHistory.removeFirst()
            }
            callback(intent, aiReply)
        }.start()
    }

    private fun parseOfflineIntent(text: String): SanaIntent {
        val lower = text.lowercase().trim()
        return when {
            lower.contains("torch on") || lower.contains("flash on") -> SanaIntent.TorchOn
            lower.contains("torch off") || lower.contains("flash off") -> SanaIntent.TorchOff
            lower.contains("wifi on") -> SanaIntent.WifiOn
            lower.contains("wifi off") -> SanaIntent.WifiOff
            lower.contains("bluetooth on") -> SanaIntent.BluetoothOn
            lower.contains("bluetooth off") -> SanaIntent.BluetoothOff
            lower.contains("weather") -> SanaIntent.QueryWeather
            lower.contains("battery") -> SanaIntent.QueryBattery
            lower.contains("time") -> SanaIntent.QueryTime
            lower.contains("date") -> SanaIntent.QueryDate
            lower.startsWith("open ") -> SanaIntent.LaunchApp(lower.removePrefix("open ").trim())
            else -> SanaIntent.Converse(text)
        }
    }

    private fun callGemini(userText: String, apiKey: String, context: Context): String {
        return try {
            val userName = SharedPrefsManager.get(context, SharedPrefsManager.Keys.PRIMARY_USER_NAME)
                ?.takeIf { it.isNotBlank() } ?: "Sanjay"
            val prompt = buildSystemPrompt(userName)
            val historyText = buildHistoryText()
            val fullText = "$prompt\n\nRecent conversation:\n$historyText\n\nUser: $userText"
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 12000
                readTimeout = 12000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
            val body = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", fullText)))
                }))
            }
            conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val response = BufferedReader(InputStreamReader(stream)).readText()
            if (code !in 200..299) {
                "Boss, AI service abhi response nahi de paayi. Main offline help kar sakti hun."
            } else {
                parseGeminiResponse(response)
            }
        } catch (_: Exception) {
            "Boss, network ya AI mein issue aa gaya... chalo offline mode mein kaam karte hain."
        }
    }

    private fun parseGeminiResponse(raw: String): String {
        return try {
            val root = JSONObject(raw)
            val candidates = root.optJSONArray("candidates") ?: return fallbackReply()
            if (candidates.length() == 0) return fallbackReply()
            val first = candidates.optJSONObject(0) ?: return fallbackReply()
            val content = first.optJSONObject("content") ?: return fallbackReply()
            val parts = content.optJSONArray("parts") ?: return fallbackReply()
            val text = parts.optJSONObject(0)?.optString("text")?.trim().orEmpty()
            if (text.isBlank()) fallbackReply() else text
        } catch (_: Exception) {
            fallbackReply()
        }
    }

    private fun fallbackReply(): String = "Haan Boss, main sun rahi hun... command do, main execute karti hun."

    private fun buildHistoryText(): String {
        val copy = synchronized(conversationHistory) { conversationHistory.toList() }
        if (copy.isEmpty()) return "No previous turns"
        return copy.joinToString("\n") { "User: ${it.first}\nSANA: ${it.second}" }
    }

    private fun buildSystemPrompt(userName: String): String {
        return """
            Tu SANA hai — System-Adaptive Neural Assistant. Tujhe Sanjay Katara ne banaya hai.
            Tu $userName Boss ki personal OS-level AI assistant hai.
            Tu Hinglish mein baat kar — Roman Hindi aur English ka natural mix.
            Responses 2-3 sentences max. Zyada lamba mat bol.
            User ko hamesha "$userName Boss" bol.
            Natural commas, ellipses (...), aur exclamation marks (!) use kar.
            Koi romantic content nahi. Professional, warm, aur loyal reh.
            Agar koi system action hua, uska confirmation bhi bol.
        """.trimIndent()
    }
}
