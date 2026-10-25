package org.openflow.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Touch-target compliant IconButton ensuring min 48x48dp hitbox.
 */
@Composable
fun IconButton48(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    tint: Color = ColorOnSurface,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = contentDescription,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, radius = 24.dp),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalContentColor provides if (enabled) tint else ColorOutline
        ) {
            content()
        }
    }
}

/**
 * Keyframe status states: Inactive, Active, Interpolated.
 */
enum class KeyframeState {
    INACTIVE,
    ACTIVE,
    INTERPOLATED
}

/**
 * DiamondKeyframeToggle: 48dp touchable diamond icon button.
 */
@Composable
fun DiamondKeyframeToggle(
    state: KeyframeState,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String = "Toggle keyframe"
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                role = Role.Checkbox,
                onClickLabel = contentDescription,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, radius = 24.dp),
                onClick = onToggle
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            val diamondPath = Path().apply {
                moveTo(cx, 0f)
                lineTo(w, cy)
                lineTo(cx, h)
                lineTo(0f, cy)
                close()
            }

            when (state) {
                KeyframeState.INACTIVE -> {
                    drawPath(
                        path = diamondPath,
                        color = ColorOutline,
                        style = Stroke(width = 1.8f.dp.toPx())
                    )
                }
                KeyframeState.ACTIVE -> {
                    drawPath(
                        path = diamondPath,
                        color = ColorKeyframe,
                        style = Fill
                    )
                }
                KeyframeState.INTERPOLATED -> {
                    drawPath(
                        path = diamondPath,
                        color = ColorKeyframe,
                        style = Stroke(width = 1.8f.dp.toPx())
                    )
                    drawCircle(
                        color = ColorKeyframe,
                        radius = 2.5f.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                }
            }
        }
    }
}

/**
 * ScrubField: Numeric box supporting horizontal drag virtual scrubbing and direct tap text edit.
 */
@Composable
fun ScrubField(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    sensitivity: Float = 0.5f,
    min: Float = Float.NEGATIVE_INFINITY,
    max: Float = Float.POSITIVE_INFINITY,
    decimalPlaces: Int = 1
) {
    var isEditing by remember { mutableStateOf(false) }
    var textInput by remember(value) {
        mutableStateOf(String.format(Locale.US, "%.${decimalPlaces}f", value))
    }
    var isDragging by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val formatString = "%.${decimalPlaces}f"

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label != null) {
            Text(
                text = label,
                style = OpenFlowTypography.bodySmall,
                color = ColorOnSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 72.dp, minHeight = 40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDragging) ColorSurfaceContainerHighest else ColorSurfaceContainerHigh)
                .border(
                    width = 1.dp,
                    color = if (isDragging) ColorPrimary else ColorOutlineVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .pointerInput(value, min, max, sensitivity) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val delta = dragAmount.x * (sensitivity / 10f)
                            val updated = (value + delta).coerceIn(min, max)
                            onValueChange(updated)
                        }
                    )
                }
                .clickable { isEditing = true },
            contentAlignment = Alignment.Center
        ) {
            if (isEditing) {
                BasicTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    textStyle = TextStyleTimecode.copy(
                        color = ColorOnSurface,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(ColorPrimary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val parsed = textInput.toFloatOrNull()
                            if (parsed != null) {
                                onValueChange(parsed.coerceIn(min, max))
                            } else {
                                textInput = String.format(Locale.US, formatString, value)
                            }
                            isEditing = false
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .onFocusChanged { focus ->
                            if (!focus.isFocused && isEditing) {
                                val parsed = textInput.toFloatOrNull()
                                if (parsed != null) {
                                    onValueChange(parsed.coerceIn(min, max))
                                }
                                isEditing = false
                            }
                        }
                )
            } else {
                Text(
                    text = String.format(Locale.US, formatString, value),
                    style = TextStyleTimecode,
                    color = ColorOnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}
