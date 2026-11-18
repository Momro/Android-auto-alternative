package com.example.cardashboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CarDarkColorScheme = darkColorScheme(
    primary = Color(0xFF4FC3F7),
    onPrimary = Color.Black,
    secondary = Color(0xFF80D8FF),
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF161616),
    onSurface = Color(0xFFF5F5F5),
    error = Color(0xFFCF6679)
)

@Composable
fun CarDashboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CarDarkColorScheme,
        content = content
    )
}
