package com.sanaos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.sanaos.R
import com.sanaos.data.SharedPrefsManager
import com.sanaos.engine.IntentRouter
import com.sanaos.engine.SanaIntent
import kotlin.concurrent.thread

class ControlFragment : Fragment() {

    private var intentRouter: IntentRouter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intentRouter = IntentRouter(requireContext())

        val switchFlash = view.findViewById<SwitchMaterial>(R.id.switchFlashlight)
        val switchWifi = view.findViewById<SwitchMaterial>(R.id.switchWifi)
        val switchBt = view.findViewById<SwitchMaterial>(R.id.switchBluetooth)

        // Restore persisted states
        switchFlash.isChecked = SharedPrefsManager.getBoolean(requireContext(), SharedPrefsManager.Keys.FLASHLIGHT_STATE, false)
        switchWifi.isChecked = SharedPrefsManager.getBoolean(requireContext(), SharedPrefsManager.Keys.WIFI_STATE, false)
        switchBt.isChecked = SharedPrefsManager.getBoolean(requireContext(), SharedPrefsManager.Keys.BLUETOOTH_STATE, false)

        switchFlash.setOnCheckedChangeListener { _, isChecked ->
            thread {
                try {
                    val result = if (isChecked) intentRouter?.route(SanaIntent.TorchOn) else intentRouter?.route(SanaIntent.TorchOff)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), result?.spokenResponse ?: "Done", Toast.LENGTH_SHORT).show()
                    }
                    SharedPrefsManager.setBoolean(requireContext(), SharedPrefsManager.Keys.FLASHLIGHT_STATE, isChecked)
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Feature error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        switchWifi.setOnCheckedChangeListener { _, isChecked ->
            thread {
                try {
                    val result = if (isChecked) intentRouter?.route(SanaIntent.WifiOn) else intentRouter?.route(SanaIntent.WifiOff)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), result?.spokenResponse ?: "Done", Toast.LENGTH_SHORT).show()
                    }
                    SharedPrefsManager.setBoolean(requireContext(), SharedPrefsManager.Keys.WIFI_STATE, isChecked)
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Feature error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        switchBt.setOnCheckedChangeListener { _, isChecked ->
            thread {
                try {
                    val result = if (isChecked) intentRouter?.route(SanaIntent.BluetoothOn) else intentRouter?.route(SanaIntent.BluetoothOff)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), result?.spokenResponse ?: "Done", Toast.LENGTH_SHORT).show()
                    }
                    SharedPrefsManager.setBoolean(requireContext(), SharedPrefsManager.Keys.BLUETOOTH_STATE, isChecked)
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Feature error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
