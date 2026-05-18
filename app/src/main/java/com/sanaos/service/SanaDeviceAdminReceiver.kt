package com.sanaos.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SanaDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context?, intent: Intent?) {
        super.onEnabled(context, intent)
        Log.d("DEVICE_ADMIN", "Device admin enabled")
    }

    override fun onDisabled(context: Context?, intent: Intent?) {
        super.onDisabled(context, intent)
        Log.d("DEVICE_ADMIN", "Device admin disabled")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            super.onReceive(context, intent)
        } catch (e: Exception) {
            Log.e("DEVICE_ADMIN", "onReceive error: ${e.message}", e)
        }
    }
}
