package com.sanaos.engine.features

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.sanaos.engine.SanaBrain
import com.sanaos.service.SanaDeviceAdminReceiver

class ScreenLockFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        if (intent !is SanaBrain.SanaIntent.LockScreen) {
            return FeatureResult(false, "Boss, screen lock command samajh nahi aaya.")
        }
        return lockScreen(context)
    }

    private fun lockScreen(context: Context): FeatureResult {
        return try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val admin = ComponentName(context, SanaDeviceAdminReceiver::class.java)
            if (!dpm.isAdminActive(admin)) {
                val enrollIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin)
                    putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Screen lock feature ke liye device admin enable karo.")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(enrollIntent)
                return FeatureResult(false, "Boss, pehle Device Admin enable karo, phir lock command chalegi.")
            }
            dpm.lockNow()
            FeatureResult(true, "Screen lock kar diya, Boss.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, screen lock nahi ho paaya.")
        }
    }
}
