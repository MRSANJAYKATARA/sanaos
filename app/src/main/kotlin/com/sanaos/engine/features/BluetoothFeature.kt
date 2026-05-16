package com.sanaos.engine.features

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.sanaos.engine.SanaBrain

class BluetoothFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.BluetoothOn -> setBluetooth(true, context)
            is SanaBrain.SanaIntent.BluetoothOff -> setBluetooth(false, context)
            else -> FeatureResult(false, "Boss, Bluetooth command samajh nahi aaya.")
        }
    }

    private fun setBluetooth(enable: Boolean, context: Context): FeatureResult {
        return try {
            val adapter = BluetoothAdapter.getDefaultAdapter()
                ?: return FeatureResult(false, "Boss, Bluetooth hardware available nahi hai.")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (enable) adapter.enable() else adapter.disable()
                FeatureResult(true, if (enable) "Bluetooth on kar diya, Boss." else "Bluetooth off kar diya, Boss.")
            } else {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(intent)
                FeatureResult(true, "Boss, Bluetooth settings khol di. Wahan se switch kar lo.")
            }
        } catch (_: Exception) {
            FeatureResult(false, "Boss, Bluetooth control nahi ho paaya.")
        }
    }
}
