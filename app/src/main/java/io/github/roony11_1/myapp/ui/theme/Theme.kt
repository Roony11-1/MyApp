package io.github.roony11_1.myapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = PremiumPrimaryDark,
    onPrimary = PremiumOnPrimaryDark,
    primaryContainer = PremiumPrimaryContainerDark,
    onPrimaryContainer = PremiumOnPrimaryContainerDark,
    secondary = PremiumSecondaryDark,
    onSecondary = PremiumOnSecondaryDark,
    secondaryContainer = PremiumSecondaryContainerDark,
    onSecondaryContainer = PremiumOnSecondaryContainerDark,
    tertiary = PremiumTertiaryDark,
    onTertiary = PremiumOnTertiaryDark,
    tertiaryContainer = PremiumTertiaryContainerDark,
    onTertiaryContainer = PremiumOnTertiaryContainerDark,
    error = PremiumErrorDark,
    onError = PremiumOnErrorDark,
    errorContainer = PremiumErrorContainerDark,
    onErrorContainer = PremiumOnErrorContainerDark,
    background = PremiumBackgroundDark,
    onBackground = PremiumOnBackgroundDark,
    surface = PremiumSurfaceDark,
    onSurface = PremiumOnSurfaceDark,
    surfaceVariant = PremiumSurfaceContainerDark,
    onSurfaceVariant = PremiumOnSurfaceVariantDark,
    surfaceContainerLowest = PremiumSurfaceLowestDark,
    surfaceContainerLow = PremiumSurfaceLowDark,
    surfaceContainer = PremiumSurfaceContainerDark,
    surfaceContainerHigh = PremiumSurfaceHighDark,
    surfaceContainerHighest = PremiumSurfaceHighestDark,
    outline = PremiumOutlineDark,
    outlineVariant = PremiumOutlineVariantDark,
    inverseSurface = PremiumInverseSurfaceDark,
    inverseOnSurface = PremiumInverseOnSurfaceDark,
    inversePrimary = PremiumInversePrimaryDark,
    scrim = PremiumScrim
)

private val LightColorScheme = lightColorScheme(
    primary = PremiumPrimaryLight,
    onPrimary = PremiumOnPrimaryLight,
    primaryContainer = PremiumPrimaryContainerLight,
    onPrimaryContainer = PremiumOnPrimaryContainerLight,
    secondary = PremiumSecondaryLight,
    onSecondary = PremiumOnSecondaryLight,
    secondaryContainer = PremiumSecondaryContainerLight,
    onSecondaryContainer = PremiumOnSecondaryContainerLight,
    tertiary = PremiumTertiaryLight,
    onTertiary = PremiumOnTertiaryLight,
    tertiaryContainer = PremiumTertiaryContainerLight,
    onTertiaryContainer = PremiumOnTertiaryContainerLight,
    error = PremiumErrorLight,
    onError = PremiumOnErrorLight,
    errorContainer = PremiumErrorContainerLight,
    onErrorContainer = PremiumOnErrorContainerLight,
    background = PremiumBackgroundLight,
    onBackground = PremiumOnBackgroundLight,
    surface = PremiumSurfaceLight,
    onSurface = PremiumOnSurfaceLight,
    surfaceVariant = PremiumSurfaceContainerLight,
    onSurfaceVariant = PremiumOnSurfaceVariantLight,
    surfaceContainerLowest = PremiumSurfaceLowestLight,
    surfaceContainerLow = PremiumSurfaceLowLight,
    surfaceContainer = PremiumSurfaceContainerLight,
    surfaceContainerHigh = PremiumSurfaceHighLight,
    surfaceContainerHighest = PremiumSurfaceHighestLight,
    outline = PremiumOutlineLight,
    outlineVariant = PremiumOutlineVariantLight,
    inverseSurface = PremiumInverseSurfaceLight,
    inverseOnSurface = PremiumInverseOnSurfaceLight,
    inversePrimary = PremiumInversePrimaryLight,
    scrim = PremiumScrim
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
)
{
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
