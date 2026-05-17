package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import com.sanaos.engine.SanaBrain

class MobileDataFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.MobileDataOn -> setMobileData(true, context)
            is SanaBrain.SanaIntent.MobileDataOff -> setMobileData(false, context)
            else -> FeatureResult(false, "Boss, mobile data command samajh nahi aaya.")
        }
    }

    private fun setMobileData(enable: Boolean, context: Context): FeatureResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val method = ConnectivityManager::class.java.getDeclaredMethod("setMobileDataEnabled", Boolean::class.javaPrimitiveType)
                method.isAccessible = true
                method.invoke(cm, enable)
                return FeatureResult(true, if (enable) "Boss, mobile data on kar diya." else "Boss, mobile data off kar diya.")
            } catch (_: Exception) {
            }
        }
        return try {
            val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(intent)
            FeatureResult(true, "Boss, mobile network settings khol di. Wahan se data switch kar lo.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, mobile data control nahi ho paaya.")
        }
    }
}
