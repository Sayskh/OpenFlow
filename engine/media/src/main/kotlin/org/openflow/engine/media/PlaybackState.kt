package org.openflow.engine.media

/**
 * Playback state representing media player readiness.
 */
enum class PlaybackState {
    Idle,
    Buffering,
    Ready,
    Ended
}
