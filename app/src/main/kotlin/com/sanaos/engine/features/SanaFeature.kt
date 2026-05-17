package com.sanaos.engine.features

import android.content.Context
import com.sanaos.engine.SanaBrain

interface SanaFeature {
    fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult
}

data class FeatureResult(
    val success: Boolean,
    val message: String,
    val spokenResponse: String = message
)
