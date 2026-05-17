package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class YoutubeFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "YouTube feature ready.")

    fun playVideo(query: String): FeatureResult {
        return try {
            if (query.isEmpty()) {
                return FeatureResult(false, "Boss, kaunsa video chalna hai batao.")
            }
            val uri = Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
            FeatureResult(true, "Boss, YouTube mein $query dhundh rahi hun.")
        } catch (e: Exception) {
            Log.e("YOUTUBE", "Play video error: ${e.message}", e)
            FeatureResult(false, "Boss, YouTube khol nahi paya.")
        }
    }
}
