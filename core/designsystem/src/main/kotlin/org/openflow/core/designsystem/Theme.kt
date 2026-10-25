package org.openflow.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OpenFlowDarkColorScheme = darkColorScheme(
    primary = ColorPrimary,
    onPrimary = ColorOnPrimary,
    primaryContainer = ColorPrimaryContainer,
    onPrimaryContainer = ColorOnPrimaryContainer,
    secondary = ColorSecondary,
    onSecondary = ColorOnSecondary,
    secondaryContainer = ColorSecondaryContainer,
    onSecondaryContainer = ColorOnSecondaryContainer,
    tertiary = ColorTertiary,
    onTertiary = ColorOnTertiary,
    tertiaryContainer = ColorTertiaryContainer,
    onTertiaryContainer = ColorOnTertiaryContainer,
    error = ColorError,
    onError = ColorOnError,
    errorContainer = ColorErrorContainer,
    onErrorContainer = ColorOnErrorContainer,
    background = ColorSurfaceDim,
    onBackground = ColorOnSurface,
    surface = ColorSurface,
    onSurface = ColorOnSurface,
    surfaceVariant = ColorSurfaceContainerHigh,
    onSurfaceVariant = ColorOnSurfaceVariant,
    outline = ColorOutline,
    outlineVariant = ColorOutlineVariant,
    surfaceDim = ColorSurfaceDim,
    surfaceBright = ColorSurfaceBright,
    surfaceContainerLowest = ColorSurfaceContainerLowest,
    surfaceContainerLow = ColorSurfaceContainerLow,
    surfaceContainer = ColorSurfaceContainer,
    surfaceContainerHigh = ColorSurfaceContainerHigh,
    surfaceContainerHighest = ColorSurfaceContainerHighest
)

@Composable
fun OpenFlowTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OpenFlowDarkColorScheme,
        typography = OpenFlowTypography,
        content = content
    )
}
