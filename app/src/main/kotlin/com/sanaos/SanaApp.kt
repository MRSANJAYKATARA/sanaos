package com.sanaos

import android.app.Application

class SanaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Shared.appContext = applicationContext
    }

    object Shared {
        lateinit var appContext: android.content.Context
            private set
    }
}
