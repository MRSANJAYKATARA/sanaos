package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class MapsFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "Maps feature ready.")

    fun navigateTo(destination: String): FeatureResult {
        return try {
            if (destination.isEmpty()) {
                return FeatureResult(false, "Boss, kahaan jana hai batao.")
            }
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(destination)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
            FeatureResult(true, "Boss, $destination ke liye directions dhundh rahi hun.")
        } catch (e: SecurityException) {
            Log.e("MAPS", "Security exception: ${e.message}", e)
            FeatureResult(false, "Boss, location permission chahiye.")
        } catch (e: Exception) {
            Log.e("MAPS", "Navigate error: ${e.message}", e)
            FeatureResult(false, "Boss, maps nahi khul paye.")
        }
    }

    fun searchNearby(type: String): FeatureResult {
        return try {
            if (type.isEmpty()) {
                return FeatureResult(false, "Boss, kya dhundnu?")
            }
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(type)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
            FeatureResult(true, "Boss, paas mein $type dhundh rahi hun.")
        } catch (e: Exception) {
            Log.e("MAPS", "Search nearby error: ${e.message}", e)
            FeatureResult(false, "Boss, search nahi ho paya.")
        }
    }

    fun viewOnMap(query: String): FeatureResult {
        return try {
            if (query.isEmpty()) {
                return FeatureResult(false, "Boss, kaunsa location dekhun?")
            }
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
            FeatureResult(true, "Boss, $query ko map par dikha rahi hun.")
        } catch (e: Exception) {
            Log.e("MAPS", "View on map error: ${e.message}", e)
            FeatureResult(false, "Boss, map nahi khul paya.")
        }
    }
}
