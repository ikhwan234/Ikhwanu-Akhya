package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DesaGreenPrimary,
    onPrimary = DesaGreenOnPrimary,
    primaryContainer = DesaGreenContainer,
    onPrimaryContainer = DesaGreenOnContainer,
    secondary = ShopeeOrangeSecondary,
    onSecondary = ShopeeOrangeOnSecondary,
    secondaryContainer = ShopeeOrangeContainer,
    onSecondaryContainer = ShopeeOrangeOnContainer,
    tertiary = DigitalBlueTertiary,
    onTertiary = DigitalBlueOnTertiary,
    tertiaryContainer = DigitalBlueContainer,
    onTertiaryContainer = DigitalBlueOnContainer,
    background = DesaBackground,
    surface = DesaSurface,
    surfaceVariant = DesaSurfaceVariant,
    onBackground = DesaOnSurface,
    onSurface = DesaOnSurface,
    onSurfaceVariant = DesaOnSurfaceVariant,
    outline = DesaOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6FDC8F),
    onPrimary = Color(0xFF003917),
    primaryContainer = Color(0xFF005324),
    onPrimaryContainer = Color(0xFFCEF5D5),
    secondary = Color(0xFFFFB4A3),
    onSecondary = Color(0xFF5F1504),
    secondaryContainer = Color(0xFF812711),
    onSecondaryContainer = Color(0xFFFFDCD4),
    tertiary = Color(0xFF9ECAFF),
    onTertiary = Color(0xFF003258),
    background = Color(0xFF111412),
    surface = Color(0xFF191C1A),
    onBackground = Color(0xFFE1E3DF),
    onSurface = Color(0xFFE1E3DF)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep brand identity prominent
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
