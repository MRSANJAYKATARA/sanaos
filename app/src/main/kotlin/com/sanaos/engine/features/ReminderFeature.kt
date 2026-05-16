package com.sanaos.engine.features

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import com.sanaos.data.SanaDatabaseHelper
import com.sanaos.engine.SanaBrain
import com.sanaos.service.ReminderAlarmReceiver
import java.util.Calendar
import kotlin.math.max

class ReminderFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.SetReminder -> setReminder(intent.label, intent.naturalTimeText, context)
            else -> FeatureResult(false, "Boss, reminder command samajh nahi aaya.")
        }
    }

    private fun setReminder(label: String, naturalTimeText: String, context: Context): FeatureResult {
        val triggerAtMs = parseNaturalLanguageTime(naturalTimeText)
        if (triggerAtMs <= System.currentTimeMillis()) {
            return FeatureResult(false, "Boss, reminder ka time future mein hona chahiye.")
        }
        return try {
            val reqCode = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            val alarmIntent = Intent(context, ReminderAlarmReceiver::class.java).apply {
                putExtra("label", label)
                putExtra("trigger_ms", triggerAtMs)
            }
            val pending = PendingIntent.getBroadcast(
                context,
                reqCode,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pending)
            saveReminder(label, triggerAtMs, context)
            FeatureResult(true, "Boss, reminder set ho gaya: $label")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, reminder set nahi ho paaya.")
        }
    }

    private fun saveReminder(label: String, triggerAtMs: Long, context: Context) {
        try {
            val db = SanaDatabaseHelper(context).writableDatabase
            val cv = ContentValues().apply {
                put("label", label)
                put("trigger_ms", triggerAtMs)
                put("fired", 0)
            }
            db.insert("reminders_table", null, cv)
        } catch (_: Exception) {
        }
    }

    fun parseNaturalLanguageTime(text: String): Long {
        val now = System.currentTimeMillis()
        val lower = text.lowercase().trim()

        val minuteRegex = Regex("(\\d+)\\s*(minute|min|minutes|mint)")
        val hourRegex = Regex("(\\d+)\\s*(hour|hours|ghante|ghanta)")

        minuteRegex.find(lower)?.let {
            val mins = it.groupValues[1].toLongOrNull() ?: 0L
            return now + max(1L, mins) * 60_000L
        }

        hourRegex.find(lower)?.let {
            val hrs = it.groupValues[1].toLongOrNull() ?: 0L
            return now + max(1L, hrs) * 3_600_000L
        }

        if (lower.contains("kal")) {
            val cal = Calendar.getInstance().apply { timeInMillis = now }
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val timeRegex = Regex("(\\d{1,2})")
            val hour = timeRegex.find(lower)?.groupValues?.get(1)?.toIntOrNull() ?: 8
            cal.set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        return now + 5 * 60_000L
    }
}
