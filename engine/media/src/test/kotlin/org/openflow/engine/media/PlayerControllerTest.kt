package org.openflow.engine.media

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.openflow.core.common.Timecode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakePlayerController : PlayerController {
    private val _currentFrame = MutableStateFlow(0L)
    override val currentFrame: StateFlow<Long> = _currentFrame.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.Idle)
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _videoDimensions = MutableStateFlow(0 to 0)
    override val videoDimensions: StateFlow<Pair<Int, Int>> = _videoDimensions.asStateFlow()

    var preparedUri: String? = null
        private set
    var playbackSpeed: Float = 1.0f
        private set
    var released: Boolean = false
        private set
    var lastSeekMs: Long = 0L
        private set
    private var fps: Int = 30

    override fun prepare(uri: String) {
        preparedUri = uri
        _playbackState.value = PlaybackState.Ready
    }

    override fun play() {
        _isPlaying.value = true
    }

    override fun pause() {
        _isPlaying.value = false
    }

    override fun seekToFrame(frame: Long, fps: Int) {
        this.fps = fps
        val targetMs = Timecode(frame = frame, fps = fps.toDouble()).totalMilliseconds
        seekToMillisecond(targetMs)
    }

    override fun seekToMillisecond(ms: Long) {
        lastSeekMs = ms
        _currentFrame.value = Timecode.fromMilliseconds(ms, fps.toDouble()).frame
    }

    override fun setPlaybackSpeed(speed: Float) {
        playbackSpeed = speed
    }

    override fun release() {
        released = true
        _isPlaying.value = false
        _playbackState.value = PlaybackState.Idle
    }

    fun setDimensions(width: Int, height: Int) {
        _videoDimensions.value = width to height
    }

    fun setState(state: PlaybackState) {
        _playbackState.value = state
    }
}

class PlayerControllerTest {

    @Test
    fun testTimeToFrameConversion_30fps() {
        val fps = 30
        // 0 ms -> frame 0
        assertEquals(0L, Timecode.fromMilliseconds(0L, fps.toDouble()).frame)

        // 1000 ms -> frame 30
        assertEquals(30L, Timecode.fromMilliseconds(1000L, fps.toDouble()).frame)

        // 500 ms -> frame 15
        assertEquals(15L, Timecode.fromMilliseconds(500L, fps.toDouble()).frame)

        // Frame to ms check: frame 30 at 30fps = 1000ms
        val timecode = Timecode(frame = 30L, fps = fps.toDouble())
        assertEquals(1000L, timecode.totalMilliseconds)
    }

    @Test
    fun testTimeToFrameConversion_60fps() {
        val fps = 60
        // 1000 ms -> frame 60
        assertEquals(60L, Timecode.fromMilliseconds(1000L, fps.toDouble()).frame)

        // 250 ms -> frame 15
        assertEquals(15L, Timecode.fromMilliseconds(250L, fps.toDouble()).frame)

        val timecode = Timecode(frame = 60L, fps = fps.toDouble())
        assertEquals(1000L, timecode.totalMilliseconds)
    }

    @Test
    fun testPlayerController_StateTransitions() {
        val controller = FakePlayerController()

        assertEquals(PlaybackState.Idle, controller.playbackState.value)
        assertEquals(false, controller.isPlaying.value)
        assertEquals(0L, controller.currentFrame.value)
        assertEquals(0 to 0, controller.videoDimensions.value)

        controller.prepare("content://media/external/video/media/1")
        assertEquals(PlaybackState.Ready, controller.playbackState.value)
        assertEquals("content://media/external/video/media/1", controller.preparedUri)

        controller.play()
        assertTrue(controller.isPlaying.value)

        controller.pause()
        assertEquals(false, controller.isPlaying.value)

        controller.seekToFrame(45L, fps = 30)
        assertEquals(45L, controller.currentFrame.value)
        assertEquals(1500L, controller.lastSeekMs)

        controller.setPlaybackSpeed(1.5f)
        assertEquals(1.5f, controller.playbackSpeed)

        controller.setDimensions(1920, 1080)
        assertEquals(1920 to 1080, controller.videoDimensions.value)

        controller.release()
        assertTrue(controller.released)
        assertEquals(PlaybackState.Idle, controller.playbackState.value)
        assertEquals(false, controller.isPlaying.value)
    }

    @Test
    fun testPlaybackStateEnumValues() {
        val states = PlaybackState.values().map { it.name }
        assertTrue(states.contains("Idle"))
        assertTrue(states.contains("Buffering"))
        assertTrue(states.contains("Ready"))
        assertTrue(states.contains("Ended"))
    }
}
