package com.sanaos.engine

import android.util.Log
import com.sanaos.data.SanaApp

class TelemetryManager {

    fun logEvent(event: String, params: Map<String, String> = emptyMap()) {
        try {
            Log.d("SANA_TELEMETRY", "Event: $event, Params: $params")
            // In production, send to Firebase Analytics or similar
        } catch (e: Exception) {
            Log.e("SANA_TELEMETRY", "Telemetry error: ${e.message}", e)
        }
    }

    fun logIntentExecution(intent: String, success: Boolean, durationMs: Long) {
        logEvent(
            "intent_execution",
            mapOf(
                "intent" to intent,
                "success" to success.toString(),
                "duration_ms" to durationMs.toString()
            )
        )
    }

    fun logApiCall(apiName: String, statusCode: Int, durationMs: Long) {
        logEvent(
            "api_call",
            mapOf(
                "api" to apiName,
                "status_code" to statusCode.toString(),
                "duration_ms" to durationMs.toString()
            )
        )
    }

    fun logError(component: String, errorMessage: String) {
        logEvent(
            "error",
            mapOf(
                "component" to component,
                "message" to errorMessage
            )
        )
    }
}
