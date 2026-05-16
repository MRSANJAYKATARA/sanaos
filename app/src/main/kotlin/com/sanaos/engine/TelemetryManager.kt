package com.sanaos.engine

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.sanaos.data.SharedPrefsManager
import java.util.Locale

class TelemetryManager(private val context: Context) {
    private val fused = LocationServices.getFusedLocationProviderClient(context)
    private var callback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationTracking() {
        if (callback != null) return
        val req = LocationRequest.Builder(15_000L)
            .setMinUpdateIntervalMillis(10_000L)
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                persistLocation(loc)
            }
        }
        fused.requestLocationUpdates(req, callback as LocationCallback, Looper.getMainLooper())
    }

    fun stopLocationTracking() {
        callback?.let { fused.removeLocationUpdates(it) }
        callback = null
    }

    private fun persistLocation(location: Location) {
        SharedPrefsManager.put(context, SharedPrefsManager.Keys.LAST_GPS_LAT, location.latitude.toString())
        SharedPrefsManager.put(context, SharedPrefsManager.Keys.LAST_GPS_LNG, location.longitude.toString())
        if (SanaBrain.isNetworkAvailable(context)) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val address = addresses?.firstOrNull()?.getAddressLine(0).orEmpty()
                if (address.isNotBlank()) {
                    SharedPrefsManager.put(context, SharedPrefsManager.Keys.LAST_GPS_ADDRESS, address)
                }
            } catch (_: Exception) {
            }
        }
    }
}
