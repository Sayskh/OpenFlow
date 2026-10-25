package org.openflow.feature.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.openflow.core.designsystem.ColorPlayhead
import org.openflow.core.designsystem.ColorPlayheadScrub
import kotlin.math.roundToInt

/**
 * Playhead needle with 1.5dp shaft and 48dp touchable head for scrubbing.
 */
@Composable
fun Playhead(
    positionPx: Float,
    onScrub: (deltaPx: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .offset { IntOffset((positionPx - 24.dp.value).roundToInt(), 0) }
            .width(48.dp)
            .fillMaxHeight()
    ) {
        // Needle Shaft (1.5dp centered at 24dp)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(1.5.dp)
                .fillMaxHeight()
                .background(if (isDragging) ColorPlayheadScrub else ColorPlayhead)
        )

        // Scrub Handle (48x48dp touch target)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(48.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onScrub(dragAmount.x)
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.size(48.dp)) {
                val cx = size.width / 2f
                val headWidth = 14.dp.toPx()
                val headHeight = 16.dp.toPx()

                // Downward pointing arrow shape at top
                val path = Path().apply {
                    moveTo(cx - headWidth / 2f, 0f)
                    lineTo(cx + headWidth / 2f, 0f)
                    lineTo(cx + headWidth / 2f, headHeight * 0.6f)
                    lineTo(cx, headHeight)
                    lineTo(cx - headWidth / 2f, headHeight * 0.6f)
                    close()
                }

                drawPath(
                    path = path,
                    color = if (isDragging) ColorPlayheadScrub else ColorPlayhead,
                    style = Fill
                )
            }
        }
    }
}
