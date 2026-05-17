package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.util.Log

class ScreenRecordFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = toggleScreenRecord()

    fun toggleScreenRecord(): FeatureResult {
        return try {
            val projectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val intent = projectionManager.createScreenCaptureIntent()
            context.startActivity(intent)
            FeatureResult(true, "Boss, screen recording shuru kar rahi hun.")
        } catch (e: SecurityException) {
            Log.e("SCREEN_RECORD", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, screen record permission chahiye.")
        } catch (e: Exception) {
            Log.e("SCREEN_RECORD", "Toggle screen record error: ${e.message}", e)
            FeatureResult(false, "Boss, screen record nahi ho paya.")
        }
    }
}
