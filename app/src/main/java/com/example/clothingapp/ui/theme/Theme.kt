package com.example.clothingapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4A853),
    secondary = Color(0xFF8E8E93),
    tertiary = Color(0xFFFF3B30),
    background = Color(0xFF1A1A1E),
    surface = Color(0xFF2C2C2E),
    onBackground = Color(0xFFEBEBF0),
    onSurface = Color(0xFFEBEBF0),
)

@Composable
fun ClothingAppTheme(
    darkTheme: Boolean = true, // Default to dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
