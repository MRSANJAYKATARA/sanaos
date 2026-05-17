package com.sanaos.engine.features

import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class BrightnessFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "Brightness feature ready.")

    fun setBrightness(percent: Int): FeatureResult {
        return try {
            val brightness = (255 * percent / 100).coerceIn(0, 255)
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
            FeatureResult(true, "Boss, brightness $percent percent kar diya.")
        } catch (e: Exception) {
            Log.e("BRIGHTNESS", "Set brightness error: ${e.message}", e)
            FeatureResult(false, "Boss, brightness set nahi ho paya.")
        }
    }

    fun autoBrightness(): FeatureResult {
        return try {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
            FeatureResult(true, "Boss, auto brightness on kar diya.")
        } catch (e: Exception) {
            Log.e("BRIGHTNESS", "Auto brightness error: ${e.message}", e)
            FeatureResult(false, "Boss, auto brightness nahi ho paya.")
        }
    }

    fun maxBrightness(): FeatureResult {
        return try {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255)
            FeatureResult(true, "Boss, brightness maximum kar diya.")
        } catch (e: Exception) {
            Log.e("BRIGHTNESS", "Max brightness error: ${e.message}", e)
            FeatureResult(false, "Boss, max brightness nahi ho paya.")
        }
    }

    fun minBrightness(): FeatureResult {
        return try {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 10)
            FeatureResult(true, "Boss, brightness minimum kar diya.")
        } catch (e: Exception) {
            Log.e("BRIGHTNESS", "Min brightness error: ${e.message}", e)
            FeatureResult(false, "Boss, min brightness nahi ho paya.")
        }
    }
}
