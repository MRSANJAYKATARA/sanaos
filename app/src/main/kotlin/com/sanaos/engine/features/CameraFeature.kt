package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.sanaos.engine.SanaBrain
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.OpenCamera -> openCamera(context)
            is SanaBrain.SanaIntent.TakePhoto -> takePhoto(context)
            is SanaBrain.SanaIntent.FlipCamera -> flipCamera(context)
            else -> FeatureResult(false, "Boss, camera command samajh nahi aaya.")
        }
    }

    private fun openCamera(context: Context): FeatureResult {
        return try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            FeatureResult(true, "Boss, camera open kar diya.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, camera open nahi ho paaya.")
        }
    }

    private fun takePhoto(context: Context): FeatureResult {
        return try {
            val fileName = "SANA_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
            val file = File(context.getExternalFilesDir("Pictures"), fileName)
            val photoUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            FeatureResult(true, "Boss, photo capture ready hai.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, photo capture start nahi ho paaya.")
        }
    }

    private fun flipCamera(context: Context): FeatureResult {
        return FeatureResult(false, "Boss, camera flip ke liye screen pe camera app open honi chahiye. Main direct flip nahi kar pa rahi.")
    }
}
