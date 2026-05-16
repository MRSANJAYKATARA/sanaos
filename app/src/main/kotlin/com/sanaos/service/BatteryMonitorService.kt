package com.sanaos.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sanaos.engine.VocalEngine
import com.sanaos.engine.features.BatteryFeature

class BatteryMonitorService : Service() {
    private val batteryFeature = BatteryFeature()
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        batteryFeature.startProactiveMonitoring(this) { message ->
            VocalEngine.speak(message, this)
        }
        return START_STICKY
    }
}
