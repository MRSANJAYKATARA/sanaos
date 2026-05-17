package com.sanaos.engine.features

import android.content.Context
import com.sanaos.data.SharedPrefsManager
import com.sanaos.engine.SanaBrain
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        if (intent !is SanaBrain.SanaIntent.QueryWeather) {
            return FeatureResult(false, "Boss, weather command samajh nahi aaya.")
        }
        if (!SanaBrain.isNetworkAvailable(context)) {
            return FeatureResult(
                success = false,
                message = "Boss, internet nahi hai. Weather check nahi kar sakti abhi.",
                spokenResponse = "Boss, internet connection nahi hai."
            )
        }
        val cached = getRecentCache(context)
        if (cached != null) {
            return FeatureResult(true, cached)
        }
        return fetchWeather(context)
    }

    private fun fetchWeather(context: Context): FeatureResult {
        return try {
            val lat = SharedPrefsManager.get(context, SharedPrefsManager.Keys.LAST_GPS_LAT)?.toDoubleOrNull() ?: 28.6139
            val lng = SharedPrefsManager.get(context, SharedPrefsManager.Keys.LAST_GPS_LNG)?.toDoubleOrNull() ?: 77.2090
            val apiKey = SharedPrefsManager.get(context, SharedPrefsManager.Keys.GEMINI_API_KEY)
            if (apiKey.isNullOrBlank()) return FeatureResult(false, "Boss, weather API key missing hai.")
            val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lng&appid=$apiKey&units=metric"
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
            }
            val code = conn.responseCode
            if (code !in 200..299) return FeatureResult(false, "Boss, weather server response sahi nahi aaya.")
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(body)
            val city = root.optString("name", "tumhari location")
            val temp = root.optJSONObject("main")?.optDouble("temp", Double.NaN)
            val desc = root.optJSONArray("weather")?.optJSONObject(0)?.optString("description", "clear") ?: "clear"
            val message = "Boss, abhi $city mein ${"%.1f".format(temp)}°C hai, aur weather $desc hai."
            SharedPrefsManager.put(context, SharedPrefsManager.Keys.LAST_WEATHER_CACHE, message)
            SharedPrefsManager.putLong(context, SharedPrefsManager.Keys.LAST_WEATHER_TS, System.currentTimeMillis())
            FeatureResult(true, message)
        } catch (_: Exception) {
            FeatureResult(false, "Boss, weather fetch nahi ho paaya.")
        }
    }

    private fun getRecentCache(context: Context): String? {
        val ts = SharedPrefsManager.getLong(context, SharedPrefsManager.Keys.LAST_WEATHER_TS, 0L)
        val now = System.currentTimeMillis()
        if (now - ts > 30 * 60 * 1000L) return null
        return SharedPrefsManager.get(context, SharedPrefsManager.Keys.LAST_WEATHER_CACHE)
    }
}
