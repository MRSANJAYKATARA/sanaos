package com.sanaos.engine.features

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.sanaos.data.SharedPrefsManager
import com.sanaos.engine.SanaBrain

class BatteryFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.QueryBattery -> queryBattery(context)
            is SanaBrain.SanaIntent.QueryRam -> FeatureResult(true, "Boss, RAM status jaldi live metrics se dungi.")
            is SanaBrain.SanaIntent.QueryStorage -> FeatureResult(true, "Boss, storage status jaldi live metrics se dungi.")
            is SanaBrain.SanaIntent.QueryLocation -> {
                val last = SharedPrefsManager.get(context, SharedPrefsManager.Keys.LAST_GPS_ADDRESS)
                if (last.isNullOrBlank()) FeatureResult(false, "Boss, location abhi available nahi hai.")
                else FeatureResult(true, "Boss, tumhari last location: $last")
            }
            else -> FeatureResult(false, "Boss, battery command samajh nahi aaya.")
        }
    }

    private fun queryBattery(context: Context): FeatureResult {
        return try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val pct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            FeatureResult(true, "Boss, battery abhi $pct% hai.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, battery status read nahi ho paaya.")
        }
    }

    fun startProactiveMonitoring(context: Context, announcer: (String) -> Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                if (level < 0 || ctx == null) return
                maybeAlert(ctx, level, announcer)
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun maybeAlert(context: Context, level: Int, announcer: (String) -> Unit) {
        when (level) {
            100 -> if (!SharedPrefsManager.getBoolean(context, SharedPrefsManager.Keys.BATTERY_ALERT_100)) {
                announcer("Boss, battery full ho gayi! Charger nikal sakte ho ab.")
                SharedPrefsManager.putBoolean(context, SharedPrefsManager.Keys.BATTERY_ALERT_100, true)
            }
            50 -> if (!SharedPrefsManager.getBoolean(context, SharedPrefsManager.Keys.BATTERY_ALERT_50)) {
                announcer("Boss, battery 50% reh gayi... thoda dhyan rakhna.")
                SharedPrefsManager.putBoolean(context, SharedPrefsManager.Keys.BATTERY_ALERT_50, true)
            }
            20 -> if (!SharedPrefsManager.getBoolean(context, SharedPrefsManager.Keys.BATTERY_ALERT_20)) {
                announcer("Boss! Battery sirf 20% hai. Please charger lagao jaldi.")
                SharedPrefsManager.putBoolean(context, SharedPrefsManager.Keys.BATTERY_ALERT_20, true)
            }
            10 -> if (!SharedPrefsManager.getBoolean(context, SharedPrefsManager.Keys.BATTERY_ALERT_10)) {
                announcer("Boss! Battery critical hai — sirf 10% bachi! Abhi charger lagao!")
                SharedPrefsManager.putBoolean(context, SharedPrefsManager.Keys.BATTERY_ALERT_10, true)
            }
        }
    }
}
