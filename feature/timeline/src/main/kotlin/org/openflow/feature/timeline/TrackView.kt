package org.openflow.feature.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.openflow.core.designsystem.ColorOnPrimaryContainer
import org.openflow.core.designsystem.ColorOnSurface
import org.openflow.core.designsystem.ColorOnSurfaceVariant
import org.openflow.core.designsystem.ColorOutline
import org.openflow.core.designsystem.ColorOutlineVariant
import org.openflow.core.designsystem.ColorPrimary
import org.openflow.core.designsystem.ColorPrimaryContainer
import org.openflow.core.designsystem.ColorSecondary
import org.openflow.core.designsystem.ColorSurfaceContainer
import org.openflow.core.designsystem.ColorSurfaceContainerLow
import org.openflow.core.designsystem.IconButton48
import org.openflow.core.designsystem.OpenFlowTypography
import kotlin.math.roundToInt

enum class TrackType {
    VIDEO,
    AUDIO
}

data class ClipItem(
    val id: String,
    val name: String,
    val startSeconds: Float,
    val durationSeconds: Float,
    val color: Color? = null
)

data class TrackState(
    val id: String,
    val title: String,
    val type: TrackType,
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val isMuted: Boolean = false,
    val clips: List<ClipItem> = emptyList()
)

/**
 * TrackHeader: Track status icons (visibility, lock, mute) with guaranteed >= 48dp hitbox.
 */
@Composable
fun TrackHeader(
    track: TrackState,
    onToggleVisibility: () -> Unit,
    onToggleLock: () -> Unit,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(160.dp)
            .height(56.dp)
            .background(ColorSurfaceContainer)
            .border(width = 0.5.dp, color = ColorOutlineVariant)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = track.title,
            style = OpenFlowTypography.bodySmall,
            color = ColorOnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton48(
                onClick = onToggleVisibility,
                contentDescription = if (track.isVisible) "Hide track" else "Show track",
                modifier = Modifier.size(36.dp) // padding handled inside or wrapper
            ) {
                Icon(
                    imageVector = if (track.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (track.isVisible) ColorOnSurface else ColorOnSurfaceVariant
                )
            }

            IconButton48(
                onClick = onToggleLock,
                contentDescription = if (track.isLocked) "Unlock track" else "Lock track",
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (track.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (track.isLocked) ColorPrimary else ColorOnSurfaceVariant
                )
            }

            if (track.type == TrackType.AUDIO) {
                IconButton48(
                    onClick = onToggleMute,
                    contentDescription = if (track.isMuted) "Unmute track" else "Mute track",
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (track.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (track.isMuted) ColorOutline else ColorSecondary
                    )
                }
            }
        }
    }
}

/**
 * TrackLane: Horizontal strip hosting video/audio clip blocks.
 */
@Composable
fun TrackLane(
    track: TrackState,
    pixelsPerSecond: Float,
    selectedClipId: String?,
    onSelectClip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ColorSurfaceContainerLow)
            .border(width = 0.5.dp, color = ColorOutlineVariant)
    ) {
        track.clips.forEach { clip ->
            val clipStartPx = clip.startSeconds * pixelsPerSecond
            val clipWidthPx = clip.durationSeconds * pixelsPerSecond
            val isSelected = clip.id == selectedClipId

            ClipView(
                clip = clip,
                isSelected = isSelected,
                trackType = track.type,
                modifier = Modifier
                    .offset { IntOffset(clipStartPx.roundToInt(), 4.dp.value.toInt()) }
                    .width((clipWidthPx / androidx.compose.ui.platform.LocalDensity.current.density).dp)
                    .height(48.dp)
                    .clickable { onSelectClip(clip.id) }
            )
        }
    }
}

@Composable
private fun ClipView(
    clip: ClipItem,
    isSelected: Boolean,
    trackType: TrackType,
    modifier: Modifier = Modifier
) {
    val defaultBg = if (trackType == TrackType.VIDEO) ColorPrimaryContainer else ColorSurfaceContainer
    val borderColor = if (isSelected) ColorPrimary else ColorOutline

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(clip.color ?: defaultBg)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = clip.name,
            style = OpenFlowTypography.bodySmall,
            color = if (isSelected) ColorOnPrimaryContainer else ColorOnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
