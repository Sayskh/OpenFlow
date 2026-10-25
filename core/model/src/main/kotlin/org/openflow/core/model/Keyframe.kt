package org.openflow.core.model

import kotlinx.serialization.Serializable

/**
 * Control handle coordinate for Bezier interpolation curves.
 */
@Serializable
data class BezierHandle(
    val x: Float = 0f,
    val y: Float = 0f
)

/**
 * Interpolation mode between keyframes.
 */
@Serializable
enum class InterpolationType {
    Linear,
    Hold,
    Bezier
}

/**
 * Keyframe representation for animatable parameters.
 */
@Serializable
data class Keyframe(
    val frame: Long,
    val parameterName: String,
    val value: Float,
    val interpolationType: InterpolationType = InterpolationType.Linear,
    val handleIn: BezierHandle? = null,
    val handleOut: BezierHandle? = null
)
