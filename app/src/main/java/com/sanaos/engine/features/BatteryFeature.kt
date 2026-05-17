package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class BatteryFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = queryBattery()

    fun queryBattery(): FeatureResult {
        return try {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, ifilter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1

            val statusMsg = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "full"
                else -> "unknown"
            }

            val msg = "Boss, battery $level percent hai aur $statusMsg chal raha hai."
            FeatureResult(true, msg)
        } catch (e: Exception) {
            Log.e("BATTERY", "Query battery error: ${e.message}", e)
            FeatureResult(false, "Boss, battery info nahi mil paya.")
        }
    }

    fun queryTime(): FeatureResult {
        return try {
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val time = format.format(Date())
            FeatureResult(true, "Boss, abhi samay hai $time.")
        } catch (e: Exception) {
            Log.e("BATTERY", "Query time error: ${e.message}", e)
            FeatureResult(false, "Boss, time nahi mil paya.")
        }
    }

    fun queryDate(): FeatureResult {
        return try {
            val format = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale("hi", "IN"))
            val date = format.format(Date())
            FeatureResult(true, "Boss, aaj ki tarikh hai $date.")
        } catch (e: Exception) {
            Log.e("BATTERY", "Query date error: ${e.message}", e)
            FeatureResult(false, "Boss, date nahi mil paya.")
        }
    }
}
