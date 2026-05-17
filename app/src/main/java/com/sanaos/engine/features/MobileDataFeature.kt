package com.sanaos.engine.features

import android.content.Context
import android.util.Log

class MobileDataFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "Mobile data feature ready.")

    fun mobileDataOn(): FeatureResult {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            connectivityManager.javaClass.getMethod("setMobileDataEnabled", Boolean::class.java).invoke(connectivityManager, true)
            FeatureResult(true, "Boss, mobile data on kar diya.")
        } catch (e: Exception) {
            Log.e("MOBILE_DATA", "Mobile data on error: ${e.message}", e)
            FeatureResult(false, "Boss, mobile data on nahi ho paya.")
        }
    }

    fun mobileDataOff(): FeatureResult {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            connectivityManager.javaClass.getMethod("setMobileDataEnabled", Boolean::class.java).invoke(connectivityManager, false)
            FeatureResult(true, "Boss, mobile data off kar diya.")
        } catch (e: Exception) {
            Log.e("MOBILE_DATA", "Mobile data off error: ${e.message}", e)
            FeatureResult(false, "Boss, mobile data off nahi ho paya.")
        }
    }
}
