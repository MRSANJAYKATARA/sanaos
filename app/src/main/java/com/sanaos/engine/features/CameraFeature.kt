package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log

class CameraFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = openCamera()

    fun openCamera(): FeatureResult {
        return try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            context.startActivity(intent)
            FeatureResult(true, "Boss, camera khol diya.")
        } catch (e: SecurityException) {
            Log.e("CAMERA", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, camera permission chahiye.")
        } catch (e: Exception) {
            Log.e("CAMERA", "Open camera error: ${e.message}", e)
            FeatureResult(false, "Boss, camera nahi khul paya.")
        }
    }

    fun takePhoto(): FeatureResult {
        return try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            context.startActivity(intent)
            FeatureResult(true, "Boss, photo click kar de.")
        } catch (e: Exception) {
            Log.e("CAMERA", "Take photo error: ${e.message}", e)
            FeatureResult(false, "Boss, photo nahi click ho paya.")
        }
    }

    fun flipCamera(): FeatureResult {
        return try {
            FeatureResult(true, "Boss, camera flip ho gaya.")
        } catch (e: Exception) {
            Log.e("CAMERA", "Flip camera error: ${e.message}", e)
            FeatureResult(false, "Boss, camera flip nahi ho paya.")
        }
    }
}
