package com.sanaos.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.sanaos.data.SharedPrefsManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val shouldStart = SharedPrefsManager.getBoolean(context, SharedPrefsManager.Keys.FOREGROUND_ENABLED, false)
        if (!shouldStart) return
        try {
            val serviceIntent = Intent(context, SanaForegroundService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        } catch (_: Exception) {
        }
    }
}
