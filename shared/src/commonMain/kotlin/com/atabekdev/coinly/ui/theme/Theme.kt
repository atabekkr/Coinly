package com.atabekdev.coinly.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Only a light theme was designed.
private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = BrandSurface,
    primaryContainer = BrandPrimaryTint,
    onPrimaryContainer = BrandPrimary,
    secondary = BrandPrimary,
    onSecondary = BrandSurface,
    secondaryContainer = BrandPrimaryTint,
    onSecondaryContainer = BrandPrimary,
    tertiary = BrandPrimary,
    onTertiary = BrandSurface,
    tertiaryContainer = BrandPrimaryTint,
    onTertiaryContainer = BrandPrimary,
    background = BrandBackground,
    onBackground = BrandText,
    surface = BrandSurface,
    onSurface = BrandText,
    surfaceVariant = BrandTrack,
    onSurfaceVariant = BrandTextSecondary,
    surfaceTint = BrandPrimary,
    outline = BrandOutline,
    outlineVariant = BrandDivider,
    error = BrandDanger,
    onError = BrandSurface,
    errorContainer = BrandDangerTint,
    onErrorContainer = BrandDanger,
    surfaceBright = BrandSurface,
    surfaceDim = BrandTrack,
    surfaceContainerLowest = BrandSurface,
    surfaceContainerLow = BrandSurface,
    surfaceContainer = BrandSurface,
    surfaceContainerHigh = BrandSurface,
    surfaceContainerHighest = BrandTrack,
)

private val CoinlyShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun CoinlyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = coinlyTypography(),
        shapes = CoinlyShapes,
        content = content,
    )
}
