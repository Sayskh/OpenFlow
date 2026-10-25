package org.openflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.openflow.core.designsystem.ColorOnSurface
import org.openflow.core.designsystem.ColorOnSurfaceVariant
import org.openflow.core.designsystem.ColorPrimary
import org.openflow.core.designsystem.ColorSurface
import org.openflow.core.designsystem.ColorSurfaceContainer
import org.openflow.core.designsystem.DiamondKeyframeToggle
import org.openflow.core.designsystem.KeyframeState
import org.openflow.core.designsystem.OpenFlowTheme
import org.openflow.core.designsystem.OpenFlowTypography
import org.openflow.core.designsystem.ScrubField
import org.openflow.feature.curve_editor.BezierCurve
import org.openflow.feature.curve_editor.BezierCurveCanvas
import org.openflow.feature.preview.AspectRatioPreset
import org.openflow.feature.preview.PreviewViewport
import org.openflow.feature.preview.TransformBoxState
import org.openflow.feature.preview.TransportControls
import org.openflow.feature.timeline.ClipItem
import org.openflow.feature.timeline.TimelineView
import org.openflow.feature.timeline.TrackState
import org.openflow.feature.timeline.TrackType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenFlowTheme {
                OpenFlowEditingShell()
            }
        }
    }
}

enum class BottomPanelTab {
    TIMELINE,
    CURVE_EDITOR,
    INSPECTOR
}

@Composable
fun OpenFlowEditingShell() {
    val fps = 30.0
    val totalDurationSeconds = 30.0f

    // Playback state
    var isPlaying by remember { mutableStateOf(false) }
    var currentFrame by remember { mutableLongStateOf(0L) }
    var currentTimeSeconds by remember { mutableFloatStateOf(0f) }

    // Viewport transform state
    var transformState by remember { mutableStateOf(TransformBoxState()) }
    var activeRatio by remember { mutableStateOf(AspectRatioPreset.RATIO_16_9) }

    // Bottom tab selection
    var selectedTab by remember { mutableStateOf(BottomPanelTab.TIMELINE) }

    // Curve state
    var bezierCurve by remember { mutableStateOf(BezierCurve.EaseInOut) }

    // Keyframe & property demo state
    var keyframeState by remember { mutableStateOf(KeyframeState.ACTIVE) }
    var rotationValue by remember { mutableFloatStateOf(0.0f) }
    var scaleValue by remember { mutableFloatStateOf(100.0f) }

    // Timeline tracks mock data
    val tracks = remember {
        mutableStateListOf(
            TrackState(
                id = "track_v1",
                title = "Video 1",
                type = TrackType.VIDEO,
                clips = listOf(
                    ClipItem(id = "c1", name = "Intro.mp4", startSeconds = 0f, durationSeconds = 6f),
                    ClipItem(id = "c2", name = "Main_Shot.mov", startSeconds = 6f, durationSeconds = 14f)
                )
            ),
            TrackState(
                id = "track_v2",
                title = "Overlay",
                type = TrackType.VIDEO,
                clips = listOf(
                    ClipItem(id = "c3", name = "Logo_Watermark.png", startSeconds = 2f, durationSeconds = 12f)
                )
            ),
            TrackState(
                id = "track_a1",
                title = "Audio 1",
                type = TrackType.AUDIO,
                clips = listOf(
                    ClipItem(id = "c4", name = "Background_Music.wav", startSeconds = 0f, durationSeconds = 25f)
                )
            )
        )
    }

    var selectedClipId by remember { mutableStateOf<String?>("c1") }

    fun updateFrame(newFrame: Long) {
        val totalFrames = (totalDurationSeconds * fps).toLong()
        currentFrame = newFrame.coerceIn(0L, totalFrames)
        currentTimeSeconds = (currentFrame / fps).toFloat()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ColorSurface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Top Section: Preview Viewport (Takes remaining available space up to 50%)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                PreviewViewport(
                    aspectRatioPreset = activeRatio,
                    showSafeGuides = true,
                    transformState = transformState,
                    onTransformChange = { transformState = it },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 2. Middle Section: Transport Controls
            TransportControls(
                isPlaying = isPlaying,
                currentFrame = currentFrame,
                fps = fps,
                onPlayPauseToggle = { isPlaying = !isPlaying },
                onStepPreviousFrame = { updateFrame(currentFrame - 1) },
                onStepNextFrame = { updateFrame(currentFrame + 1) },
                onJumpToStart = { updateFrame(0L) },
                onJumpToEnd = { updateFrame((totalDurationSeconds * fps).toLong()) }
            )

            // Bottom Panel Tab Selector
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = ColorSurfaceContainer,
                contentColor = ColorPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = ColorPrimary
                    )
                },
                modifier = Modifier.height(48.dp)
            ) {
                Tab(
                    selected = selectedTab == BottomPanelTab.TIMELINE,
                    onClick = { selectedTab = BottomPanelTab.TIMELINE },
                    text = { Text("Timeline", style = OpenFlowTypography.labelLarge) },
                    icon = { Icon(Icons.Default.ViewTimeline, contentDescription = null) },
                    selectedContentColor = ColorPrimary,
                    unselectedContentColor = ColorOnSurfaceVariant
                )
                Tab(
                    selected = selectedTab == BottomPanelTab.CURVE_EDITOR,
                    onClick = { selectedTab = BottomPanelTab.CURVE_EDITOR },
                    text = { Text("Curves", style = OpenFlowTypography.labelLarge) },
                    icon = { Icon(Icons.Default.AutoGraph, contentDescription = null) },
                    selectedContentColor = ColorPrimary,
                    unselectedContentColor = ColorOnSurfaceVariant
                )
                Tab(
                    selected = selectedTab == BottomPanelTab.INSPECTOR,
                    onClick = { selectedTab = BottomPanelTab.INSPECTOR },
                    text = { Text("Inspector", style = OpenFlowTypography.labelLarge) },
                    icon = { Icon(Icons.Default.Tune, contentDescription = null) },
                    selectedContentColor = ColorPrimary,
                    unselectedContentColor = ColorOnSurfaceVariant
                )
            }

            // 3. Bottom Section: Timeline, Curve Editor, or Inspector Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                when (selectedTab) {
                    BottomPanelTab.TIMELINE -> {
                        TimelineView(
                            tracks = tracks,
                            currentTimeSeconds = currentTimeSeconds,
                            totalDurationSeconds = totalDurationSeconds,
                            onSeekSeconds = { sec ->
                                currentTimeSeconds = sec
                                currentFrame = (sec * fps).toLong()
                            },
                            fps = fps,
                            selectedClipId = selectedClipId,
                            onSelectClip = { selectedClipId = it },
                            onToggleVisibility = { trackId ->
                                val index = tracks.indexOfFirst { it.id == trackId }
                                if (index != -1) {
                                    tracks[index] = tracks[index].copy(isVisible = !tracks[index].isVisible)
                                }
                            },
                            onToggleLock = { trackId ->
                                val index = tracks.indexOfFirst { it.id == trackId }
                                if (index != -1) {
                                    tracks[index] = tracks[index].copy(isLocked = !tracks[index].isLocked)
                                }
                            },
                            onToggleMute = { trackId ->
                                val index = tracks.indexOfFirst { it.id == trackId }
                                if (index != -1) {
                                    tracks[index] = tracks[index].copy(isMuted = !tracks[index].isMuted)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    BottomPanelTab.CURVE_EDITOR -> {
                        BezierCurveCanvas(
                            curve = bezierCurve,
                            onCurveChange = { bezierCurve = it },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    BottomPanelTab.INSPECTOR -> {
                        InspectorPanel(
                            rotation = rotationValue,
                            onRotationChange = { rotationValue = it },
                            scale = scaleValue,
                            onScaleChange = { scaleValue = it },
                            keyframeState = keyframeState,
                            onToggleKeyframe = {
                                keyframeState = when (keyframeState) {
                                    KeyframeState.INACTIVE -> KeyframeState.ACTIVE
                                    KeyframeState.ACTIVE -> KeyframeState.INTERPOLATED
                                    KeyframeState.INTERPOLATED -> KeyframeState.INACTIVE
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectorPanel(
    rotation: Float,
    onRotationChange: (Float) -> Unit,
    scale: Float,
    onScaleChange: (Float) -> Unit,
    keyframeState: KeyframeState,
    onToggleKeyframe: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorSurfaceContainer)
            .padding(16.dp)
    ) {
        Text(
            text = "Transform Properties",
            style = OpenFlowTypography.titleMedium,
            color = ColorOnSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Keyframe",
                style = OpenFlowTypography.bodySmall,
                color = ColorOnSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            DiamondKeyframeToggle(
                state = keyframeState,
                onToggle = onToggleKeyframe
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rotation (°)",
                style = OpenFlowTypography.bodySmall,
                color = ColorOnSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            ScrubField(
                value = rotation,
                onValueChange = onRotationChange,
                min = -360f,
                max = 360f
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scale (%)",
                style = OpenFlowTypography.bodySmall,
                color = ColorOnSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            ScrubField(
                value = scale,
                onValueChange = onScaleChange,
                min = 0f,
                max = 1000f
            )
        }
    }
}
