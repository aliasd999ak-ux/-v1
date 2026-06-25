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
    primary = SageGreenDark,
    secondary = EmeraldGreenDark,
    tertiary = WarmOrange,
    background = DarkBg,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE2E3DF),
    onSurface = Color(0xFFE2E3DF)
)

private val LightColorScheme = lightColorScheme(
    primary = FreshGreen,
    onPrimary = Color.White,
    primaryContainer = FreshGreenLight,
    secondary = WarmOrange,
    onSecondary = Color.White,
    tertiary = OliveDark,
    background = CreamWhite,
    surface = Color.White,
    onBackground = SlateText,
    onSurface = SlateText,
    surfaceVariant = SoftGrey,
    onSurfaceVariant = SlateText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic colors to ensure our beautiful warm organic theme remains distinctive!
    content: @Composable () -> Unit,
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
