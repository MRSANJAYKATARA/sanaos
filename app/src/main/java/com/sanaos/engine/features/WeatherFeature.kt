package com.sanaos.engine.features

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class WeatherFeature(private val context: Context) : SanaFeature {

    private val okHttpClient = OkHttpClient()

    override fun execute(): FeatureResult = queryWeather()

    fun queryWeather(): FeatureResult {
        return try {
            val apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=28.6139&longitude=77.2090&current=temperature_2m,weather_code"
            val request = Request.Builder().url(apiUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e("WEATHER", "Weather API HTTP ${response.code}")
                return FeatureResult(false, "Boss, weather info nahi mil paya.")
            }

            val responseBody = response.body?.string() ?: "{}"
            val json = JSONObject(responseBody)
            val current = json.optJSONObject("current")
            val temp = current?.optDouble("temperature_2m", 0.0) ?: 0.0
            val weatherCode = current?.optInt("weather_code", 0) ?: 0

            val weatherDesc = when (weatherCode) {
                0 -> "clear sky"
                1, 2, 3 -> "partly cloudy"
                45, 48 -> "foggy"
                51, 53, 55 -> "light rain"
                61, 63, 65 -> "heavy rain"
                71, 73, 75 -> "snow"
                else -> "variable weather"
            }

            FeatureResult(true, "Boss, weather mein temperature $temp degrees hai aur $weatherDesc hai.")
        } catch (e: Exception) {
            Log.e("WEATHER", "Query weather error: ${e.message}", e)
            FeatureResult(false, "Boss, weather info nahi mil paya.")
        }
    }
}
