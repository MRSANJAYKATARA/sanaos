package com.sanaos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.ImageButton
import com.sanaos.ui.*
import com.sanaos.data.SharedPrefsManager

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabMic: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        bottomNav = findViewById(R.id.bottomNav)
        fabMic = findViewById(R.id.fabMic)

        viewPager.adapter = ScreenSlidePagerAdapter(this)
        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = 4

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_history -> viewPager.currentItem = 1
                R.id.nav_control -> viewPager.currentItem = 2
                R.id.nav_files -> viewPager.currentItem = 3
                R.id.nav_settings -> viewPager.currentItem = 4
            }
            true
        }

        fabMic.setOnClickListener {
            toggleForegroundService()
        }

        // Sync selected nav with pager
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> bottomNav.selectedItemId = R.id.nav_home
                    1 -> bottomNav.selectedItemId = R.id.nav_history
                    2 -> bottomNav.selectedItemId = R.id.nav_control
                    3 -> bottomNav.selectedItemId = R.id.nav_files
                    4 -> bottomNav.selectedItemId = R.id.nav_settings
                }
            }
        })
    }

    private fun toggleForegroundService() {
        val enabled = SharedPrefsManager.getBoolean(this, SharedPrefsManager.Keys.SERVICE_ENABLED, false)
        val svcIntent = Intent(this, service.SanaForegroundService::class.java)
        if (!enabled) {
            svcIntent.action = service.SanaForegroundService.ACTION_START
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(svcIntent)
            } else {
                startService(svcIntent)
            }
            SharedPrefsManager.setBoolean(this, SharedPrefsManager.Keys.SERVICE_ENABLED, true)
        } else {
            svcIntent.action = service.SanaForegroundService.ACTION_STOP
            startService(svcIntent)
            SharedPrefsManager.setBoolean(this, SharedPrefsManager.Keys.SERVICE_ENABLED, false)
        }
    }

    private inner class ScreenSlidePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 5
        override fun createFragment(position: Int) = when (position) {
            0 -> HomeFragment()
            1 -> HistoryFragment()
            2 -> ControlFragment()
            3 -> FileManagerFragment()
            4 -> SettingsFragment()
            else -> HomeFragment()
        }
    }
}
