package com.sanaos.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionManager {

    fun getRequiredPermissions(): List<String> {
        return listOf(
            // Network
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.CHANGE_NETWORK_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            // Audio
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            // Location
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            // Device Control
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_SETTINGS,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            // Calls & SMS
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.ANSWER_PHONE_CALLS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_SMS,
            // Contacts
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            // Calendar & Reminders
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.WRITE_CALENDAR,
            android.Manifest.permission.SET_ALARM,
            // Notifications
            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
            android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
            android.Manifest.permission.POST_NOTIFICATIONS,
            // Storage
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            // System
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
            android.Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
            android.Manifest.permission.CHANGE_DEVICE_ADMIN
        )
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun hasAllPermissions(context: Context): Boolean {
        return getRequiredPermissions().all { hasPermission(context, it) }
    }
}
