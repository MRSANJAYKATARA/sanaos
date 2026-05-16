package com.sanaos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sanaos.service.SanaForegroundService
import com.sanaos.ui.HistoryFragment
import com.sanaos.ui.HomeFragment
import com.sanaos.ui.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.viewPager)
        bottomNav = findViewById(R.id.bottomNav)

        setupPager()
        setupBottomNav()
        startSanaServiceSafely()
    }

    private fun setupPager() {
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3
            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> HomeFragment()
                1 -> HistoryFragment()
                else -> SettingsFragment()
            }
        }
        viewPager.isUserInputEnabled = true
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNav.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun setupBottomNav() {
        bottomNav.menu.clear()
        bottomNav.menu.add(0, 0, 0, getString(R.string.nav_home)).setIcon(android.R.drawable.ic_menu_view)
        bottomNav.menu.add(0, 1, 1, getString(R.string.nav_history)).setIcon(android.R.drawable.ic_menu_recent_history)
        bottomNav.menu.add(0, 2, 2, getString(R.string.nav_settings)).setIcon(android.R.drawable.ic_menu_manage)
        bottomNav.setOnItemSelectedListener {
            viewPager.currentItem = it.itemId
            true
        }
    }

    private fun startSanaServiceSafely() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION)
                return
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE_AUDIO_PERMISSION)
            return
        }
        val serviceIntent = Intent(this, SanaForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_NOTIFICATION_PERMISSION,
            REQUEST_CODE_AUDIO_PERMISSION -> startSanaServiceSafely()
        }
    }

    companion object {
        private const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1001
        private const val REQUEST_CODE_AUDIO_PERMISSION = 1002
    }
}
