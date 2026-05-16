package com.sanaos.engine.features

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sanaos.engine.SanaBrain

class SpotifyFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.PlaySpotify -> playQuery(intent.query, context)
            else -> FeatureResult(false, "Boss, Spotify command samajh nahi aaya.")
        }
    }

    private fun playQuery(query: String, context: Context): FeatureResult {
        val clean = query.trim().ifBlank { "top hits" }
        return try {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:search:${Uri.encode(clean)}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(appIntent)
            FeatureResult(true, "Boss, Spotify pe $clean search kar diya.")
        } catch (_: ActivityNotFoundException) {
            try {
                val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/search/${Uri.encode(clean)}")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(web)
                FeatureResult(true, "Boss, Spotify web search open kar diya.")
            } catch (_: Exception) {
                FeatureResult(false, "Boss, Spotify open nahi ho paaya.")
            }
        } catch (_: Exception) {
            FeatureResult(false, "Boss, Spotify play request fail ho gayi.")
        }
    }
}
