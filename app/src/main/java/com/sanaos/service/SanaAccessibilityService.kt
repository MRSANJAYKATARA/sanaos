package com.sanaos.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo

class SanaAccessibilityService : AccessibilityService() {

    companion object {
        private var instance: SanaAccessibilityService? = null

        fun scrollDown() {
            try {
                instance?.rootInActiveWindow?.let { root ->
                    val performed = root.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                    if (!performed) {
                        instance?.performGlobalAction(GLOBAL_ACTION_SCROLL_DOWN)
                    }
                }
            } catch (e: Exception) {
                Log.e("SANA_ACCESS", "scrollDown error: ${e.message}", e)
            }
        }

        fun scrollUp() {
            try {
                instance?.rootInActiveWindow?.let { root ->
                    val performed = root.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
                    if (!performed) {
                        instance?.performGlobalAction(GLOBAL_ACTION_SCROLL_UP)
                    }
                }
            } catch (e: Exception) {
                Log.e("SANA_ACCESS", "scrollUp error: ${e.message}", e)
            }
        }

        fun scrollToTop() {
            try {
                instance?.performGlobalAction(GLOBAL_ACTION_HOME)
            } catch (e: Exception) {
                Log.e("SANA_ACCESS", "scrollToTop error: ${e.message}", e)
            }
        }

        fun scrollToBottom() {
            try {
                instance?.performGlobalAction(GLOBAL_ACTION_RECENTS)
            } catch (e: Exception) {
                Log.e("SANA_ACCESS", "scrollToBottom error: ${e.message}", e)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("SANA_ACCESS", "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No-op
    }

    override fun onInterrupt() {
        // No-op
    }

    // Collect text from node including contentDescription (BUG-17 FIX)
    private fun collectText(node: AccessibilityNodeInfo?, outList: MutableList<String>) {
        try {
            if (node == null) return
            val text = node.text?.toString()?.trim()
            val desc = node.contentDescription?.toString()?.trim()
            if (!text.isNullOrBlank()) outList.add(text)
            if (!desc.isNullOrBlank()) outList.add(desc)
            for (i in 0 until node.childCount) {
                collectText(node.getChild(i), outList)
            }
        } catch (e: Exception) {
            Log.e("SANA_ACCESS", "collectText error: ${e.message}", e)
        }
    }
}
