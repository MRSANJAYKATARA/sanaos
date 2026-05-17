package com.sanaos.engine.features

data class FeatureResult(
    val success: Boolean,
    val spokenResponse: String
)

interface SanaFeature {
    fun execute(): FeatureResult
}
