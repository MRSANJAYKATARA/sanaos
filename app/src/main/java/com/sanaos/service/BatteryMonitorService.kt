package com.sanaos.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BatteryMonitorService : Service() {

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                val level = intent?.getIntExtra("level", -1) ?: -1
                val plugged = intent?.getIntExtra("plugged", -1) ?: -1
                val stateIntent = Intent("com.sanaos.ACTION_BATTERY")
                stateIntent.putExtra("level", level)
                stateIntent.putExtra("plugged", plugged)
                LocalBroadcastManager.getInstance(this@BatteryMonitorService).sendBroadcast(stateIntent)
            } catch (e: Exception) {
                Log.e("BATTERY_MON", "Receiver error: ${e.message}", e)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            val ifilter = IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
            registerReceiver(batteryReceiver, ifilter)
        } catch (e: Exception) {
            Log.e("BATTERY_MON", "Register error: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
            Log.e("BATTERY_MON", "Unregister error: ${e.message}", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
