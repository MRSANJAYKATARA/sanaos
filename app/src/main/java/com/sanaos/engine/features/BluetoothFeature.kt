package com.sanaos.engine.features

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log

class BluetoothFeature(private val context: Context) : SanaFeature {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun execute(): FeatureResult = FeatureResult(true, "Bluetooth feature ready.")

    fun bluetoothOn(): FeatureResult {
        return try {
            if (bluetoothAdapter != null) {
                bluetoothAdapter.enable()
                FeatureResult(true, "Boss, Bluetooth on kar diya.")
            } else {
                FeatureResult(false, "Boss, Bluetooth nahi mila.")
            }
        } catch (e: SecurityException) {
            Log.e("BLUETOOTH", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, Bluetooth permission chahiye.")
        } catch (e: Exception) {
            Log.e("BLUETOOTH", "Bluetooth on error: ${e.message}", e)
            FeatureResult(false, "Boss, Bluetooth on nahi ho paya.")
        }
    }

    fun bluetoothOff(): FeatureResult {
        return try {
            if (bluetoothAdapter != null) {
                bluetoothAdapter.disable()
                FeatureResult(true, "Boss, Bluetooth off kar diya.")
            } else {
                FeatureResult(false, "Boss, Bluetooth nahi mila.")
            }
        } catch (e: SecurityException) {
            Log.e("BLUETOOTH", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, Bluetooth permission chahiye.")
        } catch (e: Exception) {
            Log.e("BLUETOOTH", "Bluetooth off error: ${e.message}", e)
            FeatureResult(false, "Boss, Bluetooth off nahi ho paya.")
        }
    }
}
