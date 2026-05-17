package com.sanaos.engine.features

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.sanaos.data.SharedPrefsManager
import java.io.File

class SystemInfoFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = queryRam()

    fun queryRam(): FeatureResult {
        return try {
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1048576 // MB
            val maxMemory = runtime.maxMemory() / 1048576 // MB
            FeatureResult(true, "Boss, RAM mein $usedMemory MB istemal ho raha hai, total $maxMemory MB hai.")
        } catch (e: Exception) {
            Log.e("SYSTEM_INFO", "Query RAM error: ${e.message}", e)
            FeatureResult(false, "Boss, RAM info nahi mil paya.")
        }
    }

    fun queryStorage(): FeatureResult {
        return try {
            val externalStoragePath = Environment.getExternalStorageDirectory()
            val stat = StatFs(externalStoragePath.path)
            val totalSpace = stat.totalBlockSize * stat.blockCount / 1073741824 // GB
            val availableSpace = stat.availableBlockSize * stat.blockCount / 1073741824 // GB
            FeatureResult(true, "Boss, storage mein $availableSpace GB khali hai, total $totalSpace GB hai.")
        } catch (e: Exception) {
            Log.e("SYSTEM_INFO", "Query storage error: ${e.message}", e)
            FeatureResult(false, "Boss, storage info nahi mil paya.")
        }
    }

    fun queryLocation(context: Context): FeatureResult {
        return try {
            val lastAddress = SharedPrefsManager.get(context, SharedPrefsManager.Keys.LAST_GPS_ADDRESS)
            if (lastAddress.isNotEmpty()) {
                FeatureResult(true, "Boss, aapka last location tha $lastAddress.")
            } else {
                FeatureResult(false, "Boss, location data nahi mila.")
            }
        } catch (e: Exception) {
            Log.e("SYSTEM_INFO", "Query location error: ${e.message}", e)
            FeatureResult(false, "Boss, location nahi mil paya.")
        }
    }
}
