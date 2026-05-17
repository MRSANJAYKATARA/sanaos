package com.sanaos.engine.features

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sanaos.data.SanaDatabaseHelper
import com.sanaos.service.ReminderAlarmReceiver
import java.util.Calendar

class ReminderFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "Reminder feature ready.")

    fun setReminder(label: String, naturalTimeText: String): FeatureResult {
        return try {
            if (label.isEmpty() || naturalTimeText.isEmpty()) {
                return FeatureResult(false, "Boss, reminder ka naam aur time batao.")
            }
            val triggerMs = parseNaturalTime(naturalTimeText)
            if (triggerMs <= 0) {
                return FeatureResult(false, "Boss, time samajh nahi aaya.")
            }
            val db = SanaDatabaseHelper(context)
            val cv = android.content.ContentValues().apply {
                put(SanaDatabaseHelper.COL_REMINDER_LABEL, label)
                put(SanaDatabaseHelper.COL_REMINDER_TRIGGER_MS, triggerMs)
                put(SanaDatabaseHelper.COL_REMINDER_FIRED, 0)
            }
            db.writableDatabase.insert(SanaDatabaseHelper.TABLE_REMINDERS, null, cv)
            scheduleReminder(triggerMs, label)
            FeatureResult(true, "Boss, reminder '$label' set kar diya.")
        } catch (e: Exception) {
            Log.e("REMINDER", "Set reminder error: ${e.message}", e)
            FeatureResult(false, "Boss, reminder set nahi ho paya.")
        }
    }

    private fun parseNaturalTime(timeText: String): Long {
        return try {
            val now = System.currentTimeMillis()
            when {
                timeText.contains("5 min") || timeText.contains("5 minute") -> now + (5 * 60 * 1000)
                timeText.contains("10 min") || timeText.contains("10 minute") -> now + (10 * 60 * 1000)
                timeText.contains("30 min") || timeText.contains("30 minute") -> now + (30 * 60 * 1000)
                timeText.contains("1 hour") -> now + (60 * 60 * 1000)
                timeText.contains("tomorrow") -> now + (24 * 60 * 60 * 1000)
                else -> now + (5 * 60 * 1000) // Default to 5 minutes
            }
        } catch (e: Exception) {
            -1L
        }
    }

    private fun scheduleReminder(triggerMs: Long, label: String) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
                putExtra("label", label)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                label.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent)
        } catch (e: Exception) {
            Log.e("REMINDER", "Schedule reminder error: ${e.message}", e)
        }
    }
}
