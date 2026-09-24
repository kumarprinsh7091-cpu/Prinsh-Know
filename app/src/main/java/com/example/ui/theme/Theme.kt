package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrandRed,
    onPrimary = Color.White,
    primaryContainer = BrandRedDark,
    onPrimaryContainer = Color.White,
    secondary = BrandCyan,
    onSecondary = Color.Black,
    secondaryContainer = BrandCyanDark,
    onSecondaryContainer = Color.White,
    tertiary = BrandYellow,
    background = DarkBackground,
    onBackground = Color(0xFFEDEDED),
    surface = DarkSurface,
    onSurface = Color(0xFFEDEDED),
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFB0B0C0)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = BrandRedDark,
    secondary = BrandCyanDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F7FA),
    onSecondaryContainer = Color(0xFF006064),
    tertiary = BrandYellow,
    background = LightBackground,
    onBackground = Color(0xFF1A1A24),
    surface = LightSurface,
    onSurface = Color(0xFF1A1A24),
    surfaceVariant = LightCard,
    onSurfaceVariant = Color(0xFF49454F)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinctive brand theme colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
