package com.anshul.indulge.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val IndulgeDarkColorScheme = darkColorScheme(
    primary = IndulgePurplePrimary,
    onPrimary = TextWhitePrimary,
    primaryContainer = IndulgePurpleDark,
    onPrimaryContainer = IndulgePurpleLight,
    secondary = IndulgePurpleLight,
    onSecondary = DarkBg,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextWhitePrimary,
    tertiary = IndulgePurpleSecondary,
    background = DarkBg,
    onBackground = TextWhitePrimary,
    surface = DarkSurface,
    onSurface = TextWhitePrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextGraySecondary,
    outline = DarkCardBorder,
    outlineVariant = AccordionInactiveBorder
)

@Composable
fun IndulgeTheme(
    // App uses the signature unified Indulge Dark theme across the entire app
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = IndulgeDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = DarkBg.toArgb()
                it.navigationBarColor = DarkBg.toArgb()
                val controller = WindowCompat.getInsetsController(it, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}