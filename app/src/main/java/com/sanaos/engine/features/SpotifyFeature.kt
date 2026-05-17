package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class SpotifyFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = FeatureResult(true, "Spotify feature ready.")

    fun playSong(query: String): FeatureResult {
        return try {
            if (query.isEmpty()) {
                return FeatureResult(false, "Boss, kaunsa song chalna hai batao.")
            }
            val uri = Uri.parse("spotify:search:$query")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.spotify.music")
            }
            context.startActivity(intent)
            FeatureResult(true, "Boss, Spotify mein $query dhundh rahi hun.")
        } catch (e: Exception) {
            Log.e("SPOTIFY", "Play song error: ${e.message}", e)
            FeatureResult(false, "Boss, Spotify app nahi mila. Pehle install kar.")
        }
    }
}
