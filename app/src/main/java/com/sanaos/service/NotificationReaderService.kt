package com.sanaos.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.Intent

class NotificationReaderService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            val pkg = sbn.packageName
            val extras = sbn.notification.extras
            val title = extras.getString(Notification.EXTRA_TITLE, "")
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

            val intent = Intent("com.sanaos.ACTION_NOTIFICATION")
            intent.putExtra("pkg", pkg)
            intent.putExtra("title", title)
            intent.putExtra("text", text)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

            Log.d("SANA_NOTIF", "Posted: $pkg — $title: $text")
        } catch (e: Exception) {
            Log.e("SANA_NOTIF", "onNotificationPosted error: ${e.message}", e)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // No-op
    }
}
