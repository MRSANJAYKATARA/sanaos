package com.sanaos.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.ContentValues
import com.sanaos.data.SanaDatabaseHelper
import com.sanaos.engine.VocalEngine

class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val label = intent?.getStringExtra("label").orEmpty().ifBlank { "Reminder" }
        val triggerMs = intent?.getLongExtra("trigger_ms", 0L) ?: 0L
        VocalEngine.speak("Boss, reminder: $label", context)
        markAsFired(context, label, triggerMs)
    }

    private fun markAsFired(context: Context, label: String, triggerMs: Long) {
        try {
            val db = SanaDatabaseHelper(context).writableDatabase
            val cv = ContentValues().apply { put("fired", 1) }
            db.update(
                "reminders_table",
                cv,
                "label = ? AND trigger_ms = ?",
                arrayOf(label, triggerMs.toString())
            )
        } catch (_: Exception) {
        }
    }
}
