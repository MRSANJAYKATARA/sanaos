package com.sanaos.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.sanaos.data.SharedPrefsManager
import com.sanaos.engine.VocalEngine

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return
        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            val mode = SharedPrefsManager.get(context, SharedPrefsManager.Keys.CALL_MODE).orEmpty()
            val message = when (mode.lowercase()) {
                "receive_reject" -> "Boss, incoming call aa rahi hai. Bolo answer karun ya reject?"
                else -> "Boss, incoming call aa rahi hai."
            }
            VocalEngine.speak(message, context)
        }
    }
}
