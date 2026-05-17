package com.sanaos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sanaos.service.SanaForegroundService

class MainActivity : AppCompatActivity() {
    private lateinit var webUi: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webUi = findViewById(R.id.webUi)
        setupWebUi()
        startSanaServiceSafely()
    }

    private fun setupWebUi() {
        webUi.webViewClient = WebViewClient()
        webUi.webChromeClient = WebChromeClient()
        webUi.settings.javaScriptEnabled = true
        webUi.settings.domStorageEnabled = true
        webUi.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webUi.loadUrl("file:///android_asset/sana_ui.html")
    }

    private fun startSanaServiceSafely() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION)
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE_AUDIO_PERMISSION)
            return
        }
        ContextCompat.startForegroundService(this, Intent(this, SanaForegroundService::class.java))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_NOTIFICATION_PERMISSION,
            REQUEST_CODE_AUDIO_PERMISSION -> startSanaServiceSafely()
        }
    }

    override fun onDestroy() {
        webUi.destroy()
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1001
        private const val REQUEST_CODE_AUDIO_PERMISSION = 1002
    }
}
