package org.openflow.core.model

import kotlinx.serialization.Serializable

/**
 * Filter and processing effect types.
 */
@Serializable
enum class EffectType {
    Shader,
    LUT,
    Blur,
    Mask
}

/**
 * Visual or audio processing effect definition.
 */
@Serializable
data class Effect(
    val id: String,
    val name: String,
    val type: EffectType,
    val parameters: Map<String, Float> = emptyMap(),
    val isEnabled: Boolean = true
)
