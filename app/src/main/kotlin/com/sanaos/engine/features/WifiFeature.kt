package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import com.sanaos.engine.SanaBrain

class WifiFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.WifiOn -> setWifi(true, context)
            is SanaBrain.SanaIntent.WifiOff -> setWifi(false, context)
            else -> FeatureResult(false, "Boss, Wi-Fi command samajh nahi aaya.")
        }
    }

    private fun setWifi(enable: Boolean, context: Context): FeatureResult {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wm.isWifiEnabled = enable
                FeatureResult(true, if (enable) "Wi-Fi on kar diya, Boss." else "Wi-Fi off kar diya, Boss.")
            } else {
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(panelIntent)
                FeatureResult(true, "Boss, Wi-Fi panel khol diya. Wahan se switch kar lo.")
            }
        } catch (_: Exception) {
            FeatureResult(false, "Boss, Wi-Fi control nahi ho paaya.")
        }
    }
}
