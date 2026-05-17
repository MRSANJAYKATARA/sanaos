package com.sanaos.data

import android.content.Context
import android.util.Base64

object SharedPrefsManager {
    private const val PREFS_NAME = "sana_prefs"

    object Keys {
        const val GEMINI_API_KEY = "gemini_api_key"
        const val ELEVENLABS_API_KEY = "elevenlabs_api_key"
        const val PRIMARY_USER_NAME = "primary_user_name"
        const val ASSISTANT_LANG = "assistant_lang"
        const val LIVE_MODE = "live_mode"
        const val CALL_MODE = "call_mode"
        const val LAST_GPS_LAT = "last_gps_lat"
        const val LAST_GPS_LNG = "last_gps_lng"
        const val LAST_GPS_ADDRESS = "last_gps_address"
        const val LAST_WEATHER_CACHE = "last_weather_cache"
        const val LAST_WEATHER_TS = "last_weather_timestamp"
        const val BATTERY_ALERT_100 = "battery_alert_100_fired"
        const val BATTERY_ALERT_50 = "battery_alert_50_fired"
        const val BATTERY_ALERT_20 = "battery_alert_20_fired"
        const val BATTERY_ALERT_10 = "battery_alert_10_fired"
        const val FOREGROUND_ENABLED = "foreground_enabled"
        const val ONBOARDING_DONE = "onboarding_done"
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun put(context: Context, key: String, value: String?) {
        val encoded = if (value.isNullOrEmpty()) "" else Base64.encodeToString(value.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        prefs(context).edit().putString(key, encoded).apply()
    }

    fun get(context: Context, key: String): String? {
        val encoded = prefs(context).getString(key, null) ?: return null
        if (encoded.isBlank()) return ""
        return try {
            String(Base64.decode(encoded, Base64.NO_WRAP), Charsets.UTF_8)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    fun putBoolean(context: Context, key: String, value: Boolean) {
        prefs(context).edit().putBoolean(key, value).apply()
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return prefs(context).getBoolean(key, defaultValue)
    }

    fun putLong(context: Context, key: String, value: Long) {
        prefs(context).edit().putLong(key, value).apply()
    }

    fun getLong(context: Context, key: String, defaultValue: Long = 0L): Long {
        return prefs(context).getLong(key, defaultValue)
    }
}
