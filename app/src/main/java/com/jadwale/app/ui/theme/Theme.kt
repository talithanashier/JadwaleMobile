package com.jadwale.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1D68E4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFF6FF),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFF0EA5E9),
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE2E8F0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color.White,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
)

// Kemdikbud Color Accents
object JadwaleColors {
    val KemdikbudGreenBg = Color(0xFFDCFCE7)
    val KemdikbudGreenText = Color(0xFF15803D)
    val KemdikbudPrimary = Color(0xFF1D68E4)
    val SoftBorder = Color(0xFFE2E8F0)
    val WarningAmberBg = Color(0xFFFEF3C7)
    val WarningAmberText = Color(0xFFB45309)
    val BluePillBg = Color(0xFFDBEAFE)
    val BluePillText = Color(0xFF1D4ED8)
    val PurplePillBg = Color(0xFFE0E7FF)
    val PurplePillText = Color(0xFF3730A3)
    val OrangePillBg = Color(0xFFFFEDD5)
    val OrangePillText = Color(0xFFC2410C)
}

@Composable
fun JadwaleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent Jadwale Kemdikbud branding by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
