package com.sanaos.data

import android.content.Context
import android.content.SharedPreferences

object SharedPrefsManager {

    object Keys {
        const val ASSISTANT_LANG = "assistant_language"
        const val GEMINI_API_KEY = "gemini_api_key"
        const val ELEVENLABS_API_KEY = "elevenlabs_api_key"
        const val ENABLE_NOTIFICATIONS = "enable_notifications"
        const val LAST_GPS_ADDRESS = "last_gps_address"
        const val SERVICE_ENABLED = "service_enabled"
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("sana_prefs", Context.MODE_PRIVATE)
    }

    fun get(context: Context, key: String, default: String = ""): String {
        return getPrefs(context).getString(key, default) ?: default
    }

    fun getBoolean(context: Context, key: String, default: Boolean = false): Boolean {
        return getPrefs(context).getBoolean(key, default)
    }

    fun getInt(context: Context, key: String, default: Int = 0): Int {
        return getPrefs(context).getInt(key, default)
    }

    fun set(context: Context, key: String, value: String) {
        getPrefs(context).edit().putString(key, value).apply()
    }

    fun setBoolean(context: Context, key: String, value: Boolean) {
        getPrefs(context).edit().putBoolean(key, value).apply()
    }

    fun setInt(context: Context, key: String, value: Int) {
        getPrefs(context).edit().putInt(key, value).apply()
    }
}
