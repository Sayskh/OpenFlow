package org.openflow.feature.curve_editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.openflow.core.common.CubicBezier
import org.openflow.core.designsystem.ColorKeyframe
import org.openflow.core.designsystem.ColorOnSurface
import org.openflow.core.designsystem.ColorOutline
import org.openflow.core.designsystem.ColorOutlineVariant
import org.openflow.core.designsystem.ColorPrimary
import org.openflow.core.designsystem.ColorSurface
import org.openflow.core.designsystem.ColorSurfaceContainer
import org.openflow.core.designsystem.ColorSurfaceContainerHigh
import org.openflow.core.designsystem.ColorSurfaceDim
import org.openflow.core.designsystem.OpenFlowTypography
import kotlin.math.roundToInt

data class BezierCurve(
    val p1x: Float = 0.25f,
    val p1y: Float = 0.10f,
    val p2x: Float = 0.25f,
    val p2y: Float = 1.00f
) {
    companion object {
        val Linear = BezierCurve(0.0f, 0.0f, 1.0f, 1.0f)
        val EaseIn = BezierCurve(0.42f, 0.0f, 1.0f, 1.0f)
        val EaseOut = BezierCurve(0.0f, 0.0f, 0.58f, 1.0f)
        val EaseInOut = BezierCurve(0.42f, 0.0f, 0.58f, 1.0f)
        val FastOutSlowIn = BezierCurve(0.4f, 0.0f, 0.2f, 1.0f)
    }
}

/**
 * BezierCurveCanvas: Interactive cubic Bezier curve editor canvas.
 * P0 (0,0), P3 (1,1) fixed anchors; P1 and P2 draggable handles with tangent lines.
 * Preset selector for rapid curve configurations.
 */
@Composable
fun BezierCurveCanvas(
    curve: BezierCurve,
    onCurveChange: (BezierCurve) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorSurfaceContainer)
            .padding(12.dp)
    ) {
        // Preset Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetChip(
                label = "Linear",
                selected = curve == BezierCurve.Linear,
                onClick = { onCurveChange(BezierCurve.Linear) }
            )
            PresetChip(
                label = "Ease In",
                selected = curve == BezierCurve.EaseIn,
                onClick = { onCurveChange(BezierCurve.EaseIn) }
            )
            PresetChip(
                label = "Ease Out",
                selected = curve == BezierCurve.EaseOut,
                onClick = { onCurveChange(BezierCurve.EaseOut) }
            )
            PresetChip(
                label = "Ease In-Out",
                selected = curve == BezierCurve.EaseInOut,
                onClick = { onCurveChange(BezierCurve.EaseInOut) }
            )
        }

        // Curve Interactive Graph
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .size(height = 200.dp, width = 320.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorSurfaceDim)
                .border(1.dp, ColorOutlineVariant, RoundedCornerShape(8.dp))
                .padding(24.dp)
        ) {
            val graphWidth = constraints.maxWidth.toFloat()
            val graphHeight = constraints.maxHeight.toFloat()

            // Coordinates mapping: (0, 0) at bottom-left, (1, 1) at top-right
            val p0Offset = Offset(0f, graphHeight)
            val p3Offset = Offset(graphWidth, 0f)

            val p1Offset = Offset(curve.p1x * graphWidth, graphHeight - (curve.p1y * graphHeight))
            val p2Offset = Offset(curve.p2x * graphWidth, graphHeight - (curve.p2y * graphHeight))

            // Canvas drawing grid, tangent lines, and curve
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background grid lines (0.25, 0.50, 0.75, 1.0)
                for (i in 0..4) {
                    val ratio = i / 4f
                    val gx = ratio * graphWidth
                    val gy = ratio * graphHeight

                    // Vertical grid
                    drawLine(
                        color = if (i == 0 || i == 4) ColorOutline else ColorOutlineVariant,
                        start = Offset(gx, 0f),
                        end = Offset(gx, graphHeight),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Horizontal grid
                    drawLine(
                        color = if (i == 0 || i == 4) ColorOutline else ColorOutlineVariant,
                        start = Offset(0f, gy),
                        end = Offset(graphWidth, gy),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Tangent lines P0 -> P1 and P3 -> P2
                drawLine(
                    color = ColorKeyframe.copy(alpha = 0.6f),
                    start = p0Offset,
                    end = p1Offset,
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = ColorKeyframe.copy(alpha = 0.6f),
                    start = p3Offset,
                    end = p2Offset,
                    strokeWidth = 1.5.dp.toPx()
                )

                // Cubic Bezier curve path
                val curvePath = Path().apply {
                    moveTo(p0Offset.x, p0Offset.y)
                    cubicTo(
                        p1Offset.x, p1Offset.y,
                        p2Offset.x, p2Offset.y,
                        p3Offset.x, p3Offset.y
                    )
                }
                drawPath(
                    path = curvePath,
                    color = ColorPrimary,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Fixed Anchor P0 & P3 circles
                drawCircle(
                    color = ColorOnSurface,
                    radius = 5.dp.toPx(),
                    center = p0Offset
                )
                drawCircle(
                    color = ColorOnSurface,
                    radius = 5.dp.toPx(),
                    center = p3Offset
                )
            }

            // Draggable Handle P1 (Touch target min 48x48dp)
            DraggableHandle(
                centerOffset = p1Offset,
                onDrag = { dx, dy ->
                    val newX = ((p1Offset.x + dx) / graphWidth).coerceIn(0f, 1f)
                    val newY = (1f - ((p1Offset.y + dy) / graphHeight)).coerceIn(0f, 1f)
                    onCurveChange(curve.copy(p1x = newX, p1y = newY))
                }
            )

            // Draggable Handle P2 (Touch target min 48x48dp)
            DraggableHandle(
                centerOffset = p2Offset,
                onDrag = { dx, dy ->
                    val newX = ((p2Offset.x + dx) / graphWidth).coerceIn(0f, 1f)
                    val newY = (1f - ((p2Offset.y + dy) / graphHeight)).coerceIn(0f, 1f)
                    onCurveChange(curve.copy(p2x = newX, p2y = newY))
                }
            )
        }
    }
}

@Composable
private fun DraggableHandle(
    centerOffset: Offset,
    onDrag: (Float, Float) -> Unit
) {
    val handleTouchRadiusPx = 24.dp.value * androidx.compose.ui.platform.LocalDensity.current.density
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (centerOffset.x - handleTouchRadiusPx).roundToInt(),
                    (centerOffset.y - handleTouchRadiusPx).roundToInt()
                )
            }
            .size(48.dp)
            .pointerInput(centerOffset) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (isDragging) 18.dp else 14.dp)
                .clip(CircleShape)
                .background(ColorKeyframe)
                .border(2.dp, if (isDragging) ColorSurface else ColorOutline, CircleShape)
        )
    }
}

@Composable
private fun PresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = OpenFlowTypography.labelMedium,
                color = if (selected) ColorSurface else ColorOnSurface
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ColorPrimary,
            containerColor = ColorSurfaceContainerHigh
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
