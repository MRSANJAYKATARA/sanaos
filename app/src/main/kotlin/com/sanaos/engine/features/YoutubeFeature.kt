package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sanaos.engine.SanaBrain

class YoutubeFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.PlayYoutube -> searchAndPlay(intent.query, context)
            else -> FeatureResult(false, "Boss, YouTube command samajh nahi aaya.")
        }
    }

    private fun searchAndPlay(query: String, context: Context): FeatureResult {
        val clean = query.trim().ifBlank { "trending" }
        return try {
            val appIntent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage("com.google.android.youtube")
                putExtra("query", clean)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(appIntent)
            FeatureResult(true, "Boss, YouTube pe $clean search kar diya.")
        } catch (_: Exception) {
            try {
                val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(clean)}")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(web)
                FeatureResult(true, "Boss, YouTube web search open kar diya.")
            } catch (_: Exception) {
                FeatureResult(false, "Boss, YouTube open nahi ho paaya.")
            }
        }
    }
}
