package org.openflow.core.model

import kotlinx.serialization.Serializable

/**
 * Functional classification of a timeline track.
 */
@Serializable
enum class TrackType {
    Video,
    Audio,
    Graphic,
    Adjustment
}

/**
 * Track container holding sequenced clips.
 */
@Serializable
data class Track(
    val id: String,
    val name: String,
    val type: TrackType,
    val zIndex: Int = 0,
    val isLocked: Boolean = false,
    val isMuted: Boolean = false,
    val isVisible: Boolean = true,
    val clips: List<Clip> = emptyList()
) {
    fun getActiveClips(frame: Long): List<Clip> {
        if (!isVisible) return emptyList()
        return clips.filter { it.containsFrame(frame) }
    }
}
