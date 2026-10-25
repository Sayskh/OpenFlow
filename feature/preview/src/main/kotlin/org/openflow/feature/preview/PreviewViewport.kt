package org.openflow.feature.preview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.openflow.core.designsystem.ColorOutlineVariant
import org.openflow.core.designsystem.ColorPrimary
import org.openflow.core.designsystem.ColorSafeMargin80
import org.openflow.core.designsystem.ColorSafeMargin90
import org.openflow.core.designsystem.ColorSurfaceContainerLowest
import org.openflow.core.designsystem.ColorSurfaceDim
import kotlin.math.roundToInt

enum class AspectRatioPreset(val ratio: Float, val label: String) {
    RATIO_16_9(16f / 9f, "16:9"),
    RATIO_9_16(9f / 16f, "9:16"),
    RATIO_1_1(1f / 1f, "1:1")
}

data class TransformBoxState(
    val x: Float = 0.2f,
    val y: Float = 0.2f,
    val width: Float = 0.6f,
    val height: Float = 0.6f
)

/**
 * PreviewViewport: Compose view with aspect ratio constraint, safe-zone guides, and interactive transform bounding box.
 */
@Composable
fun PreviewViewport(
    aspectRatioPreset: AspectRatioPreset = AspectRatioPreset.RATIO_16_9,
    showSafeGuides: Boolean = true,
    transformState: TransformBoxState = TransformBoxState(),
    onTransformChange: (TransformBoxState) -> Unit = {},
    modifier: Modifier = Modifier,
    videoContent: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorSurfaceDim),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(aspectRatioPreset.ratio)
                .background(ColorSurfaceContainerLowest)
        ) {
            // Video renderer slot
            videoContent()

            // Safe Zone Overlay (Action Safe 90%, Title Safe 80%)
            if (showSafeGuides) {
                SafeZoneOverlay()
            }

            // Transform Bounding Box with 4 corner resize handles
            TransformBoundingBox(
                transform = transformState,
                onTransformChange = onTransformChange
            )
        }
    }
}

@Composable
private fun SafeZoneOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 90% Action Safe
        val margin90X = w * 0.05f
        val margin90Y = h * 0.05f
        drawRect(
            color = ColorSafeMargin90,
            topLeft = Offset(margin90X, margin90Y),
            size = Size(w * 0.9f, h * 0.9f),
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            )
        )

        // 80% Title Safe
        val margin80X = w * 0.10f
        val margin80Y = h * 0.10f
        drawRect(
            color = ColorSafeMargin80,
            topLeft = Offset(margin80X, margin80Y),
            size = Size(w * 0.8f, h * 0.8f),
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
        )

        // Center crosshair
        val cx = w / 2f
        val cy = h / 2f
        val chLen = 16.dp.toPx()
        drawLine(
            color = ColorSafeMargin80,
            start = Offset(cx - chLen, cy),
            end = Offset(cx + chLen, cy),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = ColorSafeMargin80,
            start = Offset(cx, cy - chLen),
            end = Offset(cx, cy + chLen),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
private fun TransformBoundingBox(
    transform: TransformBoxState,
    onTransformChange: (TransformBoxState) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val parentW = constraints.maxWidth.toFloat()
        val parentH = constraints.maxHeight.toFloat()

        val boxLeft = (transform.x * parentW).coerceAtLeast(0f)
        val boxTop = (transform.y * parentH).coerceAtLeast(0f)
        val boxW = (transform.width * parentW).coerceIn(48f, parentW - boxLeft)
        val boxH = (transform.height * parentH).coerceIn(48f, parentH - boxTop)

        // Move Bounding Box Container
        Box(
            modifier = Modifier
                .offset { IntOffset(boxLeft.roundToInt(), boxTop.roundToInt()) }
                .size(
                    width = (boxW / density).dp,
                    height = (boxH / density).dp
                )
                .border(width = 1.5.dp, color = ColorPrimary)
                .pointerInput(transform, parentW, parentH) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newX = ((boxLeft + dragAmount.x) / parentW).coerceIn(0f, 1f - transform.width)
                        val newY = ((boxTop + dragAmount.y) / parentH).coerceIn(0f, 1f - transform.height)
                        onTransformChange(transform.copy(x = newX, y = newY))
                    }
                }
        )

        // Handle Size = 48dp minimum touch target
        val handleTouchSize = 48.dp
        val handleTouchRadiusPx = (handleTouchSize.value * density) / 2f

        // Top-Left Handle
        CornerHandle(
            centerOffset = Offset(boxLeft, boxTop),
            touchRadiusPx = handleTouchRadiusPx,
            onDrag = { dx, dy ->
                val newLeft = (boxLeft + dx).coerceIn(0f, boxLeft + boxW - 48f)
                val newTop = (boxTop + dy).coerceIn(0f, boxTop + boxH - 48f)
                val newW = boxW + (boxLeft - newLeft)
                val newH = boxH + (boxTop - newTop)
                onTransformChange(
                    transform.copy(
                        x = newLeft / parentW,
                        y = newTop / parentH,
                        width = newW / parentW,
                        height = newH / parentH
                    )
                )
            }
        )

        // Top-Right Handle
        CornerHandle(
            centerOffset = Offset(boxLeft + boxW, boxTop),
            touchRadiusPx = handleTouchRadiusPx,
            onDrag = { dx, dy ->
                val newTop = (boxTop + dy).coerceIn(0f, boxTop + boxH - 48f)
                val newW = (boxW + dx).coerceIn(48f, parentW - boxLeft)
                val newH = boxH + (boxTop - newTop)
                onTransformChange(
                    transform.copy(
                        y = newTop / parentH,
                        width = newW / parentW,
                        height = newH / parentH
                    )
                )
            }
        )

        // Bottom-Left Handle
        CornerHandle(
            centerOffset = Offset(boxLeft, boxTop + boxH),
            touchRadiusPx = handleTouchRadiusPx,
            onDrag = { dx, dy ->
                val newLeft = (boxLeft + dx).coerceIn(0f, boxLeft + boxW - 48f)
                val newW = boxW + (boxLeft - newLeft)
                val newH = (boxH + dy).coerceIn(48f, parentH - boxTop)
                onTransformChange(
                    transform.copy(
                        x = newLeft / parentW,
                        width = newW / parentW,
                        height = newH / parentH
                    )
                )
            }
        )

        // Bottom-Right Handle
        CornerHandle(
            centerOffset = Offset(boxLeft + boxW, boxTop + boxH),
            touchRadiusPx = handleTouchRadiusPx,
            onDrag = { dx, dy ->
                val newW = (boxW + dx).coerceIn(48f, parentW - boxLeft)
                val newH = (boxH + dy).coerceIn(48f, parentH - boxTop)
                onTransformChange(
                    transform.copy(
                        width = newW / parentW,
                        height = newH / parentH
                    )
                )
            }
        )
    }
}

@Composable
private fun CornerHandle(
    centerOffset: Offset,
    touchRadiusPx: Float,
    onDrag: (Float, Float) -> Unit
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (centerOffset.x - touchRadiusPx).roundToInt(),
                    (centerOffset.y - touchRadiusPx).roundToInt()
                )
            }
            .size(48.dp)
            .pointerInput(centerOffset) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(ColorPrimary)
                .border(2.dp, ColorSurfaceDim, CircleShape)
        )
    }
}
