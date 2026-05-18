package com.sanaos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.sanaos.R

class ControlFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val switchFlash = view.findViewById<SwitchMaterial>(R.id.switchFlashlight)
        val switchWifi = view.findViewById<SwitchMaterial>(R.id.switchWifi)
        val switchBt = view.findViewById<SwitchMaterial>(R.id.switchBluetooth)

        // Placeholder listeners — feature implementations invoked elsewhere via IntentRouter
        val listener = CompoundButton.OnCheckedChangeListener { _, _ -> /* no-op */ }
        switchFlash.setOnCheckedChangeListener(listener)
        switchWifi.setOnCheckedChangeListener(listener)
        switchBt.setOnCheckedChangeListener(listener)
    }
}
