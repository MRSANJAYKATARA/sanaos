package com.sanaos.engine.features

import android.content.Context
import com.sanaos.engine.SanaBrain
import com.sanaos.service.SanaAccessibilityService

class ScreenReaderFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        if (intent !is SanaBrain.SanaIntent.ReadScreen) {
            return FeatureResult(false, "Boss, screen reader command samajh nahi aaya.")
        }
        val service = SanaAccessibilityService.instance
            ?: return FeatureResult(false, "Accessibility Service enable nahi hai, Boss.")
        return try {
            val text = service.readScreen().ifBlank { "Boss, screen pe readable text nahi mila." }
            FeatureResult(true, text)
        } catch (_: Exception) {
            FeatureResult(false, "Boss, screen read nahi ho paaya.")
        }
    }
}
