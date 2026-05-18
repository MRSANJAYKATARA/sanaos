package com.sanaos.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                Log.d("CALL_RECEIVER", "Phone is ringing")
            }
        } catch (e: Exception) {
            Log.e("CALL_RECEIVER", "onReceive error: ${e.message}", e)
        }
    }
}
