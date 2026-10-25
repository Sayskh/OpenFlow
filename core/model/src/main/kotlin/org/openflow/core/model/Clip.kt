package org.openflow.core.model

import kotlinx.serialization.Serializable

/**
 * Individual media, graphic, or adjustment clip on a track.
 */
@Serializable
data class Clip(
    val id: String,
    val trackId: String,
    val startFrame: Long,
    val durationFrames: Long,
    val sourceOffsetFrames: Long = 0L,
    val speed: Float = 1.0f,
    val sourceUri: String? = null,
    val transform: Transform = Transform.Default,
    val keyframes: List<Keyframe> = emptyList(),
    val effects: List<Effect> = emptyList(),
    val expressions: List<Expression> = emptyList()
) {
    val endFrame: Long
        get() = startFrame + durationFrames

    fun containsFrame(frame: Long): Boolean {
        return frame in startFrame until endFrame
    }

    fun mapToSourceFrame(projectFrame: Long): Long {
        val localOffset = (projectFrame - startFrame).coerceAtLeast(0L)
        return (sourceOffsetFrames + (localOffset * speed)).toLong()
    }
}
