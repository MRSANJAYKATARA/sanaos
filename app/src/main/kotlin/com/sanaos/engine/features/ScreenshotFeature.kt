package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.os.Build
import com.sanaos.engine.SanaBrain

class ScreenshotFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        if (intent !is SanaBrain.SanaIntent.Screenshot) {
            return FeatureResult(false, "Boss, screenshot command samajh nahi aaya.")
        }
        return captureScreenshot(context)
    }

    private fun captureScreenshot(context: Context): FeatureResult {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val broadcast = Intent("android.intent.action.SCREENSHOT").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.sendBroadcast(broadcast)
                FeatureResult(true, "Boss, screenshot trigger kar diya.")
            } else {
                val broadcast = Intent("android.intent.action.SCREENSHOT")
                context.sendBroadcast(broadcast)
                FeatureResult(true, "Boss, screenshot le liya.")
            }
        } catch (_: Exception) {
            FeatureResult(false, "Boss, screenshot nahi ho paaya.")
        }
    }
}
