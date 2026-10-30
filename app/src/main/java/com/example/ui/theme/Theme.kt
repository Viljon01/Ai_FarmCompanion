package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AgriLightColorScheme = lightColorScheme(
    primary = AgriForestGreen,
    onPrimary = AgriWarmBg,
    primaryContainer = AgriSageLight,
    onPrimaryContainer = AgriForestGreenDark,
    secondary = AgriWarmClay,
    onSecondary = AgriWarmBg,
    secondaryContainer = AgriClayLight,
    onSecondaryContainer = AgriEarthBrown,
    tertiary = AgriTomatoRed,
    onTertiary = AgriWarmBg,
    tertiaryContainer = AgriTomatoLight,
    onTertiaryContainer = AgriTomatoRed,
    background = AgriWarmBg,
    onBackground = AgriTextPrimary,
    surface = AgriCardSurface,
    onSurface = AgriTextPrimary,
    surfaceVariant = AgriSurfaceSubtle,
    onSurfaceVariant = AgriTextSecondary,
    outline = AgriLineSubtle,
    outlineVariant = AgriLineArtGreen.copy(alpha = 0.2f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // The design direction specifies a warm off-white / light agricultural aesthetic
    val colorScheme = AgriLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = AgriWarmBg.toArgb()
                it.navigationBarColor = AgriWarmBg.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = true
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
