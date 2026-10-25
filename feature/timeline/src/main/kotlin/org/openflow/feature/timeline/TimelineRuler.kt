package org.openflow.feature.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import org.openflow.core.designsystem.ColorOutlineVariant
import org.openflow.core.designsystem.ColorSurfaceContainerLow
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.graphics.toArgb
import org.openflow.core.designsystem.ColorOnSurfaceVariant
import kotlin.math.floor

/**
 * TimelineRuler: Canvas drawing frame/second tick marks and second markers.
 */
@Composable
fun TimelineRuler(
    totalDurationSeconds: Float,
    pixelsPerSecond: Float,
    scrollOffsetPx: Float,
    fps: Double = 30.0,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(ColorSurfaceContainerLow)
    ) {
        val width = size.width
        val height = size.height

        val visibleStartSec = (scrollOffsetPx / pixelsPerSecond).coerceAtLeast(0f)
        val visibleEndSec = ((scrollOffsetPx + width) / pixelsPerSecond)

        val textPaint = Paint().apply {
            color = ColorOnSurfaceVariant.toArgb()
            textSize = 10.dp.toPx()
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }

        // Second intervals or sub-second intervals depending on zoom
        val stepSeconds = when {
            pixelsPerSecond > 200f -> 0.2f
            pixelsPerSecond > 80f -> 0.5f
            pixelsPerSecond > 40f -> 1.0f
            else -> 2.0f
        }

        val startStep = floor(visibleStartSec / stepSeconds).toInt()
        val endStep = (visibleEndSec / stepSeconds).toInt() + 1

        for (step in startStep..endStep) {
            val sec = step * stepSeconds
            val x = (sec * pixelsPerSecond) - scrollOffsetPx

            if (x in 0f..width) {
                val isMajorSecond = (sec % 1.0f) < 0.001f || (1.0f - (sec % 1.0f)) < 0.001f
                val tickHeight = if (isMajorSecond) height * 0.5f else height * 0.25f

                drawLine(
                    color = ColorOutlineVariant,
                    start = Offset(x, height - tickHeight),
                    end = Offset(x, height),
                    strokeWidth = 1.dp.toPx()
                )

                if (isMajorSecond) {
                    val wholeSec = sec.toInt()
                    val m = wholeSec / 60
                    val s = wholeSec % 60
                    val label = "%02d:%02d".format(m, s)
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        x + 4.dp.toPx(),
                        height - 8.dp.toPx(),
                        textPaint
                    )
                }
            }
        }
    }
}
