package org.openflow.feature.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.openflow.core.designsystem.ColorSurfaceContainerLowest

/**
 * Multi-track horizontal scrollable timeline with pinch-to-zoom horizontal scaling.
 */
@Composable
fun TimelineView(
    tracks: List<TrackState>,
    currentTimeSeconds: Float,
    totalDurationSeconds: Float,
    onSeekSeconds: (Float) -> Unit,
    modifier: Modifier = Modifier,
    fps: Double = 30.0,
    selectedClipId: String? = null,
    onSelectClip: (String) -> Unit = {},
    onToggleVisibility: (String) -> Unit = {},
    onToggleLock: (String) -> Unit = {},
    onToggleMute: (String) -> Unit = {}
) {
    // Zoom factor: 20 dp to 480 dp per second
    var pixelsPerSecond by remember { mutableFloatStateOf(80f) }
    val minPxPerSec = 20f
    val maxPxPerSec = 480f

    val scrollState = rememberScrollState()
    val timelineWidthDp = ((totalDurationSeconds * pixelsPerSecond) / androidx.compose.ui.platform.LocalDensity.current.density).coerceAtLeast(800f).dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorSurfaceContainerLowest)
    ) {
        // Sticky Track Headers Column
        Column(
            modifier = Modifier.width(160.dp)
        ) {
            // Header spacer aligning with TimelineRuler
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(28.dp)
            )

            // Track Header List
            tracks.forEach { track ->
                TrackHeader(
                    track = track,
                    onToggleVisibility = { onToggleVisibility(track.id) },
                    onToggleLock = { onToggleLock(track.id) },
                    onToggleMute = { onToggleMute(track.id) }
                )
            }
        }

        // Scrollable & Pinchable Canvas Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        if (zoom != 1f) {
                            pixelsPerSecond = (pixelsPerSecond * zoom).coerceIn(minPxPerSec, maxPxPerSec)
                        }
                    }
                }
                .horizontalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .width(timelineWidthDp)
                    .fillMaxHeight()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Ruler
                    TimelineRuler(
                        totalDurationSeconds = totalDurationSeconds,
                        pixelsPerSecond = pixelsPerSecond,
                        scrollOffsetPx = scrollState.value.toFloat(),
                        fps = fps
                    )

                    // Track Lanes
                    tracks.forEach { track ->
                        TrackLane(
                            track = track,
                            pixelsPerSecond = pixelsPerSecond,
                            selectedClipId = selectedClipId,
                            onSelectClip = onSelectClip
                        )
                    }
                }

                // Playhead Needle
                val playheadPx = currentTimeSeconds * pixelsPerSecond
                Playhead(
                    positionPx = playheadPx,
                    onScrub = { deltaPx ->
                        val newTime = (currentTimeSeconds + (deltaPx / pixelsPerSecond)).coerceIn(0f, totalDurationSeconds)
                        onSeekSeconds(newTime)
                    }
                )
            }
        }
    }
}
