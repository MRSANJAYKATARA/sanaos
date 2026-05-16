package com.sanaos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sanaos.R
import com.sanaos.data.SharedPrefsManager

class SettingsFragment : Fragment() {
    private var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        root = inflater.inflate(R.layout.fragment_settings, container, false)
        return root as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAdded || root == null) return

        val etGeminiKey: EditText = view.findViewById(R.id.etGeminiKey)
        val etElevenKey: EditText = view.findViewById(R.id.etElevenKey)
        val etUserName: EditText = view.findViewById(R.id.etUserName)
        val btnSaveKeys: TextView = view.findViewById(R.id.btnSaveKeys)
        val switchLiveMode: Switch = view.findViewById(R.id.switchLiveMode)
        val chipHinglish: CheckBox = view.findViewById(R.id.chipHinglish)
        val chipEnglish: CheckBox = view.findViewById(R.id.chipEnglish)
        val rbAnnounceOnly: RadioButton = view.findViewById(R.id.rbAnnounceOnly)
        val rbReceiveReject: RadioButton = view.findViewById(R.id.rbReceiveReject)

        val ctx = requireContext()
        etGeminiKey.setText(SharedPrefsManager.get(ctx, SharedPrefsManager.Keys.GEMINI_API_KEY).orEmpty())
        etElevenKey.setText(SharedPrefsManager.get(ctx, SharedPrefsManager.Keys.ELEVENLABS_API_KEY).orEmpty())
        etUserName.setText(SharedPrefsManager.get(ctx, SharedPrefsManager.Keys.PRIMARY_USER_NAME).orEmpty())
        switchLiveMode.isChecked = SharedPrefsManager.getBoolean(ctx, SharedPrefsManager.Keys.LIVE_MODE, true)

        val lang = SharedPrefsManager.get(ctx, SharedPrefsManager.Keys.ASSISTANT_LANG).orEmpty().ifBlank { "hinglish" }
        chipHinglish.isChecked = lang == "hinglish"
        chipEnglish.isChecked = lang == "english"

        val callMode = SharedPrefsManager.get(ctx, SharedPrefsManager.Keys.CALL_MODE).orEmpty().ifBlank { "announce_only" }
        rbAnnounceOnly.isChecked = callMode == "announce_only"
        rbReceiveReject.isChecked = callMode == "receive_reject"

        chipHinglish.setOnCheckedChangeListener { _, checked -> if (checked) { chipEnglish.isChecked = false } }
        chipEnglish.setOnCheckedChangeListener { _, checked -> if (checked) { chipHinglish.isChecked = false } }

        btnSaveKeys.setOnClickListener {
            if (!isAdded || root == null) return@setOnClickListener
            SharedPrefsManager.put(ctx, SharedPrefsManager.Keys.GEMINI_API_KEY, etGeminiKey.text.toString().trim())
            SharedPrefsManager.put(ctx, SharedPrefsManager.Keys.ELEVENLABS_API_KEY, etElevenKey.text.toString().trim())
            SharedPrefsManager.put(ctx, SharedPrefsManager.Keys.PRIMARY_USER_NAME, etUserName.text.toString().trim())
            SharedPrefsManager.putBoolean(ctx, SharedPrefsManager.Keys.LIVE_MODE, switchLiveMode.isChecked)
            SharedPrefsManager.put(ctx, SharedPrefsManager.Keys.ASSISTANT_LANG, if (chipEnglish.isChecked) "english" else "hinglish")
            SharedPrefsManager.put(ctx, SharedPrefsManager.Keys.CALL_MODE, if (rbReceiveReject.isChecked) "receive_reject" else "announce_only")
            Toast.makeText(ctx, "Settings save ho gayi, Boss.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        root = null
    }
}
