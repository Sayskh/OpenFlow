package org.openflow.engine.media

import kotlinx.coroutines.flow.StateFlow

/**
 * Controller interface defining video playback operations and observable state.
 */
interface PlayerController {
    val currentFrame: StateFlow<Long>
    val playbackState: StateFlow<PlaybackState>
    val isPlaying: StateFlow<Boolean>
    val videoDimensions: StateFlow<Pair<Int, Int>>

    fun prepare(uri: String)
    fun play()
    fun pause()
    fun seekToFrame(frame: Long, fps: Int)
    fun seekToMillisecond(ms: Long)
    fun setPlaybackSpeed(speed: Float)
    fun release()
}
