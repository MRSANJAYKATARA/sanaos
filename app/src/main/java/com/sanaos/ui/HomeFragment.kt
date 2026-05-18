package com.sanaos.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanaos.R
import com.sanaos.data.SharedPrefsManager
import com.sanaos.service.SanaForegroundService
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var tvDate: TextView
    private lateinit var statusText: TextView
    private lateinit var statusPill: LinearLayout
    private lateinit var startBtn: LinearLayout
    private lateinit var btnText: TextView
    private lateinit var waveformView: WaveformView
    private lateinit var rvTranscript: RecyclerView

    private val transcripts = mutableListOf<String>()
    private val adapter = TranscriptAdapter(transcripts)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                when (intent?.action) {
                    SanaForegroundService.BROADCAST_STATE -> {
                        val state = intent.getStringExtra(SanaForegroundService.EXTRA_STATE) ?: SanaForegroundService.STATE_IDLE
                        updateState(state)
                    }
                    SanaForegroundService.ACTION_TRANSCRIPT -> {
                        val text = intent.getStringExtra("transcript") ?: ""
                        if (text.isNotBlank()) addTranscript(text)
                    }
                    SanaForegroundService.ACTION_RMS -> {
                        val rms = intent.getFloatExtra("rms", 0f)
                        waveformView.setRms(rms)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HOME_FRAG", "Broadcast onReceive error: ${e.message}", e)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDate = view.findViewById(R.id.tvDate)
        statusText = view.findViewById(R.id.statusText)
        statusPill = view.findViewById(R.id.statusPill)
        startBtn = view.findViewById(R.id.startBtn)
        btnText = view.findViewById(R.id.btnText)
        waveformView = view.findViewById(R.id.waveformView)
        rvTranscript = view.findViewById(R.id.rvTranscript)

        rvTranscript.layoutManager = LinearLayoutManager(requireContext())
        rvTranscript.adapter = adapter

        startBtn.setOnClickListener {
            toggleService()
        }

        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        tvDate.text = sdf.format(Date())

        val enabled = SharedPrefsManager.getBoolean(requireContext(), SharedPrefsManager.Keys.SERVICE_ENABLED, false)
        btnText.text = if (enabled) getString(R.string.active_button_text) else getString(R.string.start_button_text)
    }

    override fun onStart() {
        super.onStart()
        val lbm = LocalBroadcastManager.getInstance(requireContext())
        lbm.registerReceiver(receiver, IntentFilter(SanaForegroundService.BROADCAST_STATE))
        lbm.registerReceiver(receiver, IntentFilter(SanaForegroundService.ACTION_TRANSCRIPT))
        lbm.registerReceiver(receiver, IntentFilter(SanaForegroundService.ACTION_RMS))
    }

    override fun onStop() {
        super.onStop()
        try {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        } catch (e: Exception) {
            android.util.Log.e("HOME_FRAG", "Unregister receiver error: ${e.message}", e)
        }
    }

    private fun toggleService() {
        val enabled = SharedPrefsManager.getBoolean(requireContext(), SharedPrefsManager.Keys.SERVICE_ENABLED, false)
        val svcIntent = Intent(requireContext(), com.sanaos.service.SanaForegroundService::class.java)
        if (!enabled) {
            svcIntent.action = com.sanaos.service.SanaForegroundService.ACTION_START
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                requireContext().startForegroundService(svcIntent)
            } else {
                requireContext().startService(svcIntent)
            }
            SharedPrefsManager.setBoolean(requireContext(), SharedPrefsManager.Keys.SERVICE_ENABLED, true)
            btnText.text = getString(R.string.active_button_text)
        } else {
            svcIntent.action = com.sanaos.service.SanaForegroundService.ACTION_STOP
            requireContext().startService(svcIntent)
            SharedPrefsManager.setBoolean(requireContext(), SharedPrefsManager.Keys.SERVICE_ENABLED, false)
            btnText.text = getString(R.string.start_button_text)
        }
    }

    private fun updateState(state: String) {
        statusText.text = when (state) {
            SanaForegroundService.STATE_LISTENING -> getString(R.string.listening)
            SanaForegroundService.STATE_THINKING -> getString(R.string.thinking)
            SanaForegroundService.STATE_SPEAKING -> getString(R.string.speaking)
            SanaForegroundService.STATE_IDLE, SanaForegroundService.STATE_STOPPED -> getString(R.string.idle)
            else -> getString(R.string.idle)
        }

        val colorRes = when (state) {
            SanaForegroundService.STATE_LISTENING -> R.color.status_listening
            SanaForegroundService.STATE_THINKING -> R.color.status_thinking
            SanaForegroundService.STATE_SPEAKING -> R.color.status_speaking
            else -> R.color.status_idle
        }
        statusPill.setBackgroundColor(resources.getColor(colorRes, requireContext().theme))
    }

    private fun addTranscript(text: String) {
        transcripts.add(0, text)
        adapter.notifyItemInserted(0)
        rvTranscript.scrollToPosition(0)
    }

    // Simple transcript adapter showing user messages
    private class TranscriptAdapter(private val data: List<String>) : RecyclerView.Adapter<TranscriptAdapter.VH>() {
        class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tv: TextView = itemView.findViewById(R.id.userMessage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_user, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.tv.text = data[position]
        }

        override fun getItemCount(): Int = data.size
    }
}
