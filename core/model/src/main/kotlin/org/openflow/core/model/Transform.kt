package org.openflow.core.model

import kotlinx.serialization.Serializable

/**
 * 2D transformation applied to visual clips (video, graphic, adjustment).
 */
@Serializable
data class Transform(
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotation: Float = 0f,
    val anchorX: Float = 0.5f,
    val anchorY: Float = 0.5f,
    val opacity: Float = 1f
) {
    companion object {
        val Default = Transform()
    }
}
