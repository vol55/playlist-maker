package com.example.playlistmaker.root.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3772FF),
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF1A1B22),
    surface = Color.White,
    onSurface = Color(0xFF1A1B22),
    onSurfaceVariant = Color(0xFFAEAFB4),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4FF),
    onPrimary = Color(0xFF1A1B22),
    background = Color(0xFF1A1B22),
    onBackground = Color.White,
    surface = Color(0xFF1A1B22),
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFFFFFFF),
)

val AppFontFamily = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.Normal),
    Font(R.font.ys_display_medium, FontWeight.Medium),
    Font(R.font.ys_display_bold, FontWeight.Bold),
)
private val AppTypography = Typography(
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.Medium, fontSize = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily, fontWeight = FontWeight.Normal, fontSize = 11.sp
    ),
)

private val LightAppColors = AppColors(
    ysBlack = Color(0XFF1A1B22),
    ysGray = Color(0xFFAEAFB4),
    ysBlue = Color(0xFF3772E7),
    ysLightGray = Color(0xFFE6E8EB),
    themeSwitchTrackColor = Color(0xFFE6E8EB),
    foregroundVariant = Color(0xFFAEAFB4),
    textField = Color(0xFFE6E8EB),
    onTextField = Color(0xFFAEAFB4),
)

private val DarkAppColors = AppColors(
    ysBlack = Color(0XFF1A1B22),
    ysGray = Color(0xFFAEAFB4),
    ysBlue = Color(0xFF3772E7),
    ysLightGray = Color(0xFFE6E8EB),
    themeSwitchTrackColor = Color(0xFFAEAFB4),
    foregroundVariant = Color(0xFFFFFFFF),
    textField = Color.White,
    onTextField = Color(0xFF1A1B22),
)

@Composable
fun MyAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
    ) {
        CompositionLocalProvider(
            LocalAppColors provides (if (useDarkTheme) DarkAppColors else LightAppColors),
            content = content
        )
    }
}
