package org.openflow.engine.media

import android.content.Context
import android.net.Uri
import android.view.Surface
import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.openflow.core.common.Timecode

/**
 * AndroidX Media3 ExoPlayer implementation of [PlayerController].
 */
@OptIn(UnstableApi::class)
class ExoPlayerController(
    private val context: Context,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val playerInstance: ExoPlayer? = null
) : PlayerController {

    private val coroutineScope = CoroutineScope(mainDispatcher)
    private var trackingJob: Job? = null

    private var currentFps: Int = 30

    private val _currentFrame = MutableStateFlow(0L)
    override val currentFrame: StateFlow<Long> = _currentFrame.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.Idle)
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _videoDimensions = MutableStateFlow(0 to 0)
    override val videoDimensions: StateFlow<Pair<Int, Int>> = _videoDimensions.asStateFlow()

    val player: ExoPlayer = playerInstance ?: ExoPlayer.Builder(context)
        .setSeekParameters(SeekParameters.EXACT)
        .build().apply {
            setSeekParameters(SeekParameters.EXACT)
        }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            _playbackState.value = when (state) {
                Player.STATE_IDLE -> PlaybackState.Idle
                Player.STATE_BUFFERING -> PlaybackState.Buffering
                Player.STATE_READY -> PlaybackState.Ready
                Player.STATE_ENDED -> PlaybackState.Ended
                else -> PlaybackState.Idle
            }
            updateFramePosition()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) {
                startTrackingLoop()
            } else {
                stopTrackingLoop()
                updateFramePosition()
            }
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            _videoDimensions.value = videoSize.width to videoSize.height
        }
    }

    init {
        player.addListener(playerListener)
        // Ensure exact seek parameter on provided playerInstance as well
        player.setSeekParameters(SeekParameters.EXACT)
    }

    override fun prepare(uri: String) {
        val mediaItem = MediaItem.fromUri(Uri.parse(uri))
        player.setMediaItem(mediaItem)
        player.prepare()
        updateFramePosition()
    }

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun seekToFrame(frame: Long, fps: Int) {
        require(fps > 0) { "FPS must be greater than zero, got $fps" }
        require(frame >= 0) { "Frame must be non-negative, got $frame" }
        currentFps = fps
        val targetMs = Timecode(frame = frame, fps = fps.toDouble()).totalMilliseconds
        seekToMillisecond(targetMs)
    }

    override fun seekToMillisecond(ms: Long) {
        val nonNegativeMs = ms.coerceAtLeast(0L)
        player.seekTo(nonNegativeMs)
        _currentFrame.value = Timecode.fromMilliseconds(nonNegativeMs, currentFps.toDouble()).frame
    }

    override fun setPlaybackSpeed(speed: Float) {
        require(speed > 0f) { "Playback speed must be positive, got $speed" }
        player.playbackParameters = PlaybackParameters(speed)
    }

    fun setVideoSurface(surface: Surface?) {
        player.setVideoSurface(surface)
    }

    fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        player.setVideoSurfaceView(surfaceView)
    }

    override fun release() {
        stopTrackingLoop()
        player.removeListener(playerListener)
        player.release()
        _playbackState.value = PlaybackState.Idle
        _isPlaying.value = false
    }

    private fun startTrackingLoop() {
        if (trackingJob?.isActive == true) return
        trackingJob = coroutineScope.launch {
            while (isActive) {
                updateFramePosition()
                delay(16L) // ~60fps poll interval
            }
        }
    }

    private fun stopTrackingLoop() {
        trackingJob?.cancel()
        trackingJob = null
    }

    private fun updateFramePosition() {
        val currentMs = player.currentPosition.coerceAtLeast(0L)
        val frame = Timecode.fromMilliseconds(currentMs, currentFps.toDouble()).frame
        _currentFrame.value = frame
    }
}
