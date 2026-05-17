package com.sanaos.engine.features

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.util.Log
import com.sanaos.service.SanaAccessibilityService

class ScrollFeature(private val context: Context) : SanaFeature {

    override fun execute(): FeatureResult = scrollDown()

    fun scrollDown(): FeatureResult {
        return try {
            SanaAccessibilityService.scrollDown()
            FeatureResult(true, "Boss, screen scroll down kar diya.")
        } catch (e: Exception) {
            Log.e("SCROLL", "Scroll down error: ${e.message}", e)
            FeatureResult(false, "Boss, scroll nahi ho paya.")
        }
    }

    fun scrollUp(): FeatureResult {
        return try {
            SanaAccessibilityService.scrollUp()
            FeatureResult(true, "Boss, screen scroll up kar diya.")
        } catch (e: Exception) {
            Log.e("SCROLL", "Scroll up error: ${e.message}", e)
            FeatureResult(false, "Boss, scroll nahi ho paya.")
        }
    }

    fun scrollToTop(): FeatureResult {
        return try {
            SanaAccessibilityService.scrollToTop()
            FeatureResult(true, "Boss, top par gaya.")
        } catch (e: Exception) {
            Log.e("SCROLL", "Scroll to top error: ${e.message}", e)
            FeatureResult(false, "Boss, top par nahi ja paya.")
        }
    }

    fun scrollToBottom(): FeatureResult {
        return try {
            SanaAccessibilityService.scrollToBottom()
            FeatureResult(true, "Boss, neeche par gaya.")
        } catch (e: Exception) {
            Log.e("SCROLL", "Scroll to bottom error: ${e.message}", e)
            FeatureResult(false, "Boss, neeche par nahi ja paya.")
        }
    }
}
