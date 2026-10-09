package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = PrimaryElectricViolet,
    secondary = SecondaryTeal,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = TertiaryLight,
    onTertiary = OnTertiary,
    background = SurfaceCanvas,
    onBackground = OnSurfaceText,
    surface = SurfaceCanvas,
    onSurface = OnSurfaceText,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariantText,
    outline = OutlineColor,
    outlineVariant = OutlineVariantColor
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Use intentional command-center dark scheme
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
