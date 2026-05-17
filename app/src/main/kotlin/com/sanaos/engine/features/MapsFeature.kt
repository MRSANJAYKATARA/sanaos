package com.sanaos.engine.features

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sanaos.engine.SanaBrain

class MapsFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        return when (intent) {
            is SanaBrain.SanaIntent.NavigateTo -> navigateTo(intent.destination, context)
            is SanaBrain.SanaIntent.SearchNearby -> searchNearby(intent.type, context)
            is SanaBrain.SanaIntent.ViewOnMap -> viewLocation(intent.query, context)
            else -> FeatureResult(false, "Boss, maps command samajh nahi aaya.")
        }
    }

    private fun navigateTo(destination: String, context: Context): FeatureResult {
        val encoded = Uri.encode(destination.trim())
        return launch(Uri.parse("google.navigation:q=$encoded"), context, "Boss, navigation start kar diya.")
    }

    private fun viewLocation(query: String, context: Context): FeatureResult {
        val encoded = Uri.encode(query.trim())
        return launch(Uri.parse("geo:0,0?q=$encoded"), context, "Boss, map location open kar di.")
    }

    private fun searchNearby(type: String, context: Context): FeatureResult {
        val encoded = Uri.encode(type.trim())
        return launch(Uri.parse("geo:0,0?q=${encoded}+near+me"), context, "Boss, nearby search open kar diya.")
    }

    private fun launch(uri: Uri, context: Context, okMessage: String): FeatureResult {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            FeatureResult(true, okMessage)
        } catch (_: Exception) {
            try {
                val fallback = Intent(Intent.ACTION_VIEW, uri).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(fallback)
                FeatureResult(true, okMessage)
            } catch (_: Exception) {
                FeatureResult(false, "Boss, maps open nahi ho paaya.")
            }
        }
    }
}
