package com.sanaos.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class SanaAccessibilityService : AccessibilityService() {
    companion object { var instance: SanaAccessibilityService? = null }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        }
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun readScreen(): String {
        val root = rootInActiveWindow ?: return ""
        val out = mutableListOf<String>()
        collectText(root, out)
        return out.distinct().joinToString(". ")
    }

    private fun collectText(node: AccessibilityNodeInfo?, out: MutableList<String>) {
        if (node == null) return
        node.text?.toString()?.trim()?.takeIf { it.isNotBlank() }?.let { out.add(it) }
        for (i in 0 until node.childCount) collectText(node.getChild(i), out)
    }

    fun scrollDown(targetNode: AccessibilityNodeInfo? = null) {
        val node = targetNode ?: rootInActiveWindow
        if (node?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) == true) return
    }

    fun scrollUp(targetNode: AccessibilityNodeInfo? = null) {
        val node = targetNode ?: rootInActiveWindow
        if (node?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) == true) return
    }
}
