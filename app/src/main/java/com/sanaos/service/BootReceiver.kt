package com.sanaos.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
                Log.d("BOOT_RECEIVER", "Boot completed — restarting services")
                val svcIntent = Intent(context, SanaForegroundService::class.java)
                svcIntent.action = SanaForegroundService.ACTION_START
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context?.startForegroundService(svcIntent)
                } else {
                    context?.startService(svcIntent)
                }
            }
        } catch (e: Exception) {
            Log.e("BOOT_RECEIVER", "onReceive error: ${e.message}", e)
        }
    }
}
