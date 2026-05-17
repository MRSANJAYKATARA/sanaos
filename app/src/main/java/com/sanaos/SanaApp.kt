package com.sanaos

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sanaos.data.SanaDatabaseHelper
import kotlin.concurrent.thread

class SanaApp : Application() {

    companion object {
        lateinit var appContext: Context
        lateinit var database: SanaDatabaseHelper
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        database = SanaDatabaseHelper(this)
        
        // Create notification channel for SANA foreground service
        createNotificationChannel()
        
        // Warm-up database
        database.writableDatabase.close()
        
        // Trim history asynchronously
        trimHistoryAsync()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "sana_fg"
            val channelName = "SANA Foreground Service"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notification for SANA voice assistant foreground service"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun trimHistoryAsync() {
        thread(isDaemon = true, name = "HistoryTrimThread") {
            try {
                database.trimHistoryToLimit(500)
            } catch (e: Exception) {
                android.util.Log.e("SANA_APP", "Error trimming history: ${e.message}", e)
            }
        }
    }
}
