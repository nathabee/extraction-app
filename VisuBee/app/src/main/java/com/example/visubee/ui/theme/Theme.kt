package com.example.visubee.ui.theme

/*
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// ✅ Define Primary Colors (Matching `themes.xml`)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), // Purple200
    primaryContainer = Color(0xFF3700B3), // Purple500
    secondary = Color(0xFF03DAC6), // Teal200
    background = Color.Black,
    surface = Color(0xFF121212),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // Purple500
    primaryContainer = Color(0xFF3700B3), // Purple700
    secondary = Color(0xFF03DAC5), // Teal200
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// ✅ Apply Theme Based on System Settings or Dynamic Colors
@Composable
fun VisuBeeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // ✅ Use Dynamic Colors (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // ✅ Use Dark Mode Colors
        darkTheme -> DarkColorScheme

        // ✅ Use Light Mode Colors
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Use your defined Typography.kt
        content = content
    )
}

*/
