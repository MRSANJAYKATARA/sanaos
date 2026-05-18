package com.sanaos.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val label = intent?.getStringExtra("label") ?: "Reminder"
            val notifyIntent = Intent("com.sanaos.ACTION_REMINDER")
            notifyIntent.putExtra("label", label)
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(notifyIntent)
            Log.d("REMINDER_RX", "Reminder fired: $label")
        } catch (e: Exception) {
            Log.e("REMINDER_RX", "onReceive error: ${e.message}", e)
        }
    }
}
