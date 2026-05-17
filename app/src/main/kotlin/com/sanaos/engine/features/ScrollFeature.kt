package com.sanaos.engine.features

import android.content.Context
import com.sanaos.engine.SanaBrain
import com.sanaos.service.SanaAccessibilityService

class ScrollFeature : SanaFeature {
    override fun execute(intent: SanaBrain.SanaIntent, context: Context): FeatureResult {
        val service = SanaAccessibilityService.instance
            ?: return FeatureResult(false, "Accessibility Service enable nahi hai.")
        return try {
            when (intent) {
                is SanaBrain.SanaIntent.ScrollDown -> {
                    service.scrollDown()
                    FeatureResult(true, "Boss, scroll down kar diya.")
                }
                is SanaBrain.SanaIntent.ScrollUp -> {
                    service.scrollUp()
                    FeatureResult(true, "Boss, scroll up kar diya.")
                }
                is SanaBrain.SanaIntent.ScrollToTop -> {
                    repeat(10) {
                        service.scrollUp()
                        Thread.sleep(200)
                    }
                    FeatureResult(true, "Boss, top pe le aayi.")
                }
                is SanaBrain.SanaIntent.ScrollToBottom -> {
                    repeat(10) {
                        service.scrollDown()
                        Thread.sleep(200)
                    }
                    FeatureResult(true, "Boss, bottom pe le aayi.")
                }
                else -> FeatureResult(false, "Boss, scroll command samajh nahi aaya.")
            }
        } catch (_: Exception) {
            FeatureResult(false, "Boss, scroll action fail ho gaya.")
        }
    }
}
