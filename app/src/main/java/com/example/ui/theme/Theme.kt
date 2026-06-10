package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CelestialNightPrimary,
    secondary = CelestialNightSecondary,
    tertiary = CelestialNightTertiary,
    background = CelestialNightBg,
    surface = CelestialNightSurface,
    onPrimary = CelestialNightBg,
    onSecondary = CelestialNightBg,
    onTertiary = CelestialNightBg,
    onBackground = CelestialNightOnBg,
    onSurface = CelestialNightOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = CelestialDayPrimary,
    secondary = CelestialDaySecondary,
    tertiary = CelestialDayTertiary,
    background = CelestialDayBg,
    surface = CelestialDaySurface,
    onPrimary = CelestialDayBg,
    onSecondary = CelestialDayBg,
    onTertiary = CelestialDayBg,
    onBackground = CelestialDayOnBg,
    onSurface = CelestialDayOnSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
