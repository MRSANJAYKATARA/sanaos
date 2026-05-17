package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.util.Log

class AppLauncherFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "App launcher feature ready.")

    fun launchApp(appName: String): FeatureResult {
        return try {
            if (appName.isEmpty()) {
                return FeatureResult(false, "Boss, konsa app khol du?")
            }
            val pm = context.packageManager
            val packageName = when (appName.lowercase()) {
                "whatsapp" -> "com.whatsapp"
                "instagram" -> "com.instagram.android"
                "facebook" -> "com.facebook.katana"
                "youtube" -> "com.google.android.youtube"
                "chrome" -> "com.android.chrome"
                "gmail" -> "com.google.android.gm"
                "telegram" -> "org.telegram.messenger"
                else -> null
            }
            if (packageName != null) {
                val intent = pm.getLaunchIntentForPackage(packageName)
                if (intent != null) {
                    context.startActivity(intent)
                    FeatureResult(true, "Boss, $appName khol diya.")
                } else {
                    FeatureResult(false, "Boss, $appName app nahi mila.")
                }
            } else {
                FeatureResult(false, "Boss, $appName nahi patah.")
            }
        } catch (e: Exception) {
            Log.e("APP_LAUNCHER", "Launch app error: ${e.message}", e)
            FeatureResult(false, "Boss, app launch nahi ho paya.")
        }
    }
}
