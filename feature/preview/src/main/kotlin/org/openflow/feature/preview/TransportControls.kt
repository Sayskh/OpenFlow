package org.openflow.feature.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.openflow.core.common.Timecode
import org.openflow.core.designsystem.ColorOnSurface
import org.openflow.core.designsystem.ColorOnSurfaceVariant
import org.openflow.core.designsystem.ColorPrimary
import org.openflow.core.designsystem.ColorSurfaceContainer
import org.openflow.core.designsystem.IconButton48
import org.openflow.core.designsystem.TextStyleTimecode

/**
 * TimecodeDisplay: Displays current timecode in HH:MM:SS:FF format.
 */
@Composable
fun TimecodeDisplay(
    timecode: Timecode,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = timecode.toString(),
            style = TextStyleTimecode,
            color = ColorOnSurface
        )
    }
}

/**
 * TransportControls: Play/pause toggle, frame stepping, and timecode display.
 * All buttons respect >= 48dp touch targets.
 */
@Composable
fun TransportControls(
    isPlaying: Boolean,
    currentFrame: Long,
    fps: Double,
    onPlayPauseToggle: () -> Unit,
    onStepPreviousFrame: () -> Unit,
    onStepNextFrame: () -> Unit,
    onJumpToStart: () -> Unit = {},
    onJumpToEnd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentTimecode = Timecode(frame = currentFrame, fps = fps)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ColorSurfaceContainer)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Jump to start and Step -1 frame
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton48(
                onClick = onJumpToStart,
                contentDescription = "Jump to start",
                tint = ColorOnSurfaceVariant
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = null
                )
            }

            IconButton48(
                onClick = onStepPreviousFrame,
                contentDescription = "Step previous frame",
                tint = ColorOnSurface
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null
                )
            }
        }

        // Center: Timecode readout and Play/Pause Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimecodeDisplay(timecode = currentTimecode)

            IconButton48(
                onClick = onPlayPauseToggle,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = ColorPrimary
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
        }

        // Right: Step +1 frame and Jump to end
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton48(
                onClick = onStepNextFrame,
                contentDescription = "Step next frame",
                tint = ColorOnSurface
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }

            IconButton48(
                onClick = onJumpToEnd,
                contentDescription = "Jump to end",
                tint = ColorOnSurfaceVariant
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = null
                )
            }
        }
    }
}
