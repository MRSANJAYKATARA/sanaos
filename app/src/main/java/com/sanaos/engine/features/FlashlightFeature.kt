package com.sanaos.engine.features

import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.Log

class FlashlightFeature(private val context: Context) : SanaFeature {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    override fun execute(): FeatureResult = torchToggle()

    fun torchOn(): FeatureResult {
        return try {
            val cameraId = getCameraId()
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, true)
                FeatureResult(true, "Boss, torch on kar diya.")
            } else {
                FeatureResult(false, "Boss, flashlight nahi mila.")
            }
        } catch (e: SecurityException) {
            Log.e("FLASHLIGHT", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, flashlight permission chahiye.")
        } catch (e: Exception) {
            Log.e("FLASHLIGHT", "Torch on error: ${e.message}", e)
            FeatureResult(false, "Boss, torch on nahi ho paya.")
        }
    }

    fun torchOff(): FeatureResult {
        return try {
            val cameraId = getCameraId()
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, false)
                FeatureResult(true, "Boss, torch off kar diya.")
            } else {
                FeatureResult(false, "Boss, flashlight nahi mila.")
            }
        } catch (e: SecurityException) {
            Log.e("FLASHLIGHT", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, flashlight permission chahiye.")
        } catch (e: Exception) {
            Log.e("FLASHLIGHT", "Torch off error: ${e.message}", e)
            FeatureResult(false, "Boss, torch off nahi ho paya.")
        }
    }

    fun torchToggle(): FeatureResult {
        return try {
            val cameraId = getCameraId()
            if (cameraId != null) {
                val isOn = isTorchOn(cameraId)
                cameraManager.setTorchMode(cameraId, !isOn)
                val msg = if (isOn) "Boss, torch off kar diya." else "Boss, torch on kar diya."
                FeatureResult(true, msg)
            } else {
                FeatureResult(false, "Boss, flashlight nahi mila.")
            }
        } catch (e: SecurityException) {
            Log.e("FLASHLIGHT", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, flashlight permission chahiye.")
        } catch (e: Exception) {
            Log.e("FLASHLIGHT", "Torch toggle error: ${e.message}", e)
            FeatureResult(false, "Boss, torch toggle nahi ho paya.")
        }
    }

    private fun getCameraId(): String? {
        return try {
            cameraManager.cameraIdList.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private fun isTorchOn(cameraId: String): Boolean {
        return try {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            characteristics.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
        } catch (e: Exception) {
            false
        }
    }
}
