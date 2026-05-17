package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

class ScreenshotFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = takeScreenshot()

    fun takeScreenshot(): FeatureResult {
        return try {
            val intent = Intent(Intent.ACTION_SCREENSHOT)
            context.startService(intent)
            FeatureResult(true, "Boss, screenshot le rahi hun.")
        } catch (e: Exception) {
            Log.e("SCREENSHOT", "Take screenshot error: ${e.message}", e)
            FeatureResult(false, "Boss, screenshot nahi li ja paya.")
        }
    }
}
