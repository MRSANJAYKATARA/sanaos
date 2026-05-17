package com.sanaos.engine.features

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log

class NetworkStatusFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = queryNetworkStatus()

    fun queryNetworkStatus(): FeatureResult {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected) {
                val typeName = activeNetwork.typeName
                FeatureResult(true, "Boss, network $typeName se connected hai.")
            } else {
                FeatureResult(false, "Boss, network se disconnect hai.")
            }
        } catch (e: Exception) {
            Log.e("NETWORK_STATUS", "Query network error: ${e.message}", e)
            FeatureResult(false, "Boss, network status nahi mil paya.")
        }
    }
}
