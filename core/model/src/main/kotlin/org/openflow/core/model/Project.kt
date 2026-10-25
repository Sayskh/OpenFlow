package org.openflow.core.model

import kotlinx.serialization.Serializable

/**
 * Top-level project entity representing a complete timeline and video composition.
 */
@Serializable
data class Project(
    val id: String,
    val name: String,
    val width: Int,
    val height: Int,
    val fps: Double = 30.0,
    val durationFrames: Long,
    val tracks: List<Track> = emptyList()
) {
    val durationSeconds: Double
        get() = durationFrames / fps

    val aspectRatio: Float
        get() = if (height > 0) width.toFloat() / height.toFloat() else 1.0f

    fun getClipsAtFrame(frame: Long): List<Pair<Track, Clip>> {
        return tracks
            .sortedBy { it.zIndex }
            .flatMap { track ->
                track.getActiveClips(frame).map { clip -> track to clip }
            }
    }
}
