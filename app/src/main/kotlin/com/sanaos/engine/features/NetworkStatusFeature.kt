package com.sanaos.engine.features

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import com.sanaos.engine.SanaBrain

class NetworkStatusFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.QueryNetworkStatus -> {
                val type = getNetworkType(context)
                val wifiName = getWifiName(context)
                val msg = if (type == "WiFi" && wifiName.isNotBlank()) {
                    "Boss, network $type pe hai. Wi-Fi: $wifiName"
                } else {
                    "Boss, network status: $type"
                }
                FeatureResult(true, msg)
            }
            is SanaBrain.SanaIntent.QueryTime -> {
                FeatureResult(true, "Boss, time check karne ke liye system clock use kar lo, main sync mein hun.")
            }
            is SanaBrain.SanaIntent.QueryDate -> {
                FeatureResult(true, "Boss, date aaj ki system ke hisaab se set hai.")
            }
            else -> FeatureResult(false, "Boss, network status command samajh nahi aaya.")
        }
    }

    private fun getNetworkType(context: Context): String {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val net = cm.activeNetwork ?: return "No Network"
            val caps = cm.getNetworkCapabilities(net) ?: return "No Network"
            when {
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    when {
                        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED) -> "5G"
                        else -> "4G"
                    }
                }
                else -> "No Network"
            }
        } catch (_: Exception) {
            "No Network"
        }
    }

    private fun getWifiName(context: Context): String {
        return try {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ssid = wm.connectionInfo?.ssid.orEmpty().replace("\"", "")
            if (ssid.equals("<unknown ssid>", true)) "" else ssid
        } catch (_: Exception) {
            ""
        }
    }
}
