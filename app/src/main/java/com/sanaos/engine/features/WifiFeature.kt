package com.sanaos.engine.features

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log

class WifiFeature(private val context: Context) : SanaFeature {

    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    override fun execute(): FeatureResult = FeatureResult(true, "WiFi feature ready.")

    fun wifiOn(): FeatureResult {
        return try {
            wifiManager.isWifiEnabled = true
            FeatureResult(true, "Boss, WiFi on kar diya.")
        } catch (e: SecurityException) {
            Log.e("WIFI", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, WiFi permission chahiye.")
        } catch (e: Exception) {
            Log.e("WIFI", "WiFi on error: ${e.message}", e)
            FeatureResult(false, "Boss, WiFi on nahi ho paya.")
        }
    }

    fun wifiOff(): FeatureResult {
        return try {
            wifiManager.isWifiEnabled = false
            FeatureResult(true, "Boss, WiFi off kar diya.")
        } catch (e: SecurityException) {
            Log.e("WIFI", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, WiFi permission chahiye.")
        } catch (e: Exception) {
            Log.e("WIFI", "WiFi off error: ${e.message}", e)
            FeatureResult(false, "Boss, WiFi off nahi ho paya.")
        }
    }
}
