package com.sanaos.engine.features

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import com.sanaos.engine.SanaBrain

class FlashlightFeature : SanaFeature {
    private var torchState = false

    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.TorchOn -> torchOn(context)
            is SanaBrain.SanaIntent.TorchOff -> torchOff(context)
            is SanaBrain.SanaIntent.TorchToggle -> torchToggle(context)
            else -> FeatureResult(false, "Boss, flashlight command samajh nahi aaya.")
        }
    }

    private fun torchOn(context: Context): FeatureResult {
        return try {
            val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = manager.cameraIdList.firstOrNull() ?: return FeatureResult(false, "Boss, torch hardware available nahi hai.")
            manager.setTorchMode(cameraId, true)
            torchState = true
            FeatureResult(true, "Torch chalu kar diya, Boss!")
        } catch (_: CameraAccessException) {
            FeatureResult(false, "Boss, torch access nahi mil paaya.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, torch on karte waqt issue aa gaya.")
        }
    }

    private fun torchOff(context: Context): FeatureResult {
        return try {
            val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = manager.cameraIdList.firstOrNull() ?: return FeatureResult(false, "Boss, torch hardware available nahi hai.")
            manager.setTorchMode(cameraId, false)
            torchState = false
            FeatureResult(true, "Torch band kar diya.")
        } catch (_: CameraAccessException) {
            FeatureResult(false, "Boss, torch access nahi mil paaya.")
        } catch (_: Exception) {
            FeatureResult(false, "Boss, torch off karte waqt issue aa gaya.")
        }
    }

    private fun torchToggle(context: Context): FeatureResult {
        return if (torchState) torchOff(context) else torchOn(context)
    }
}
