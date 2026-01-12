package com.example.playlistmaker.root.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val ysBlack: Color,
    val ysGray: Color,
    val ysBlue: Color,
    val ysLightGray: Color,
    val themeSwitchTrackColor: Color,
    val foregroundVariant: Color,
    val textField: Color,
    val onTextField: Color,
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        ysBlack = Color(0XFF1A1B22),
        ysGray = Color(0xFFAEAFB4),
        ysBlue = Color(0xFF3772E7),
        ysLightGray = Color(0xFFE6E8EB),
        themeSwitchTrackColor = Color(0xFFE6E8EB),
        foregroundVariant = Color(0xFFAEAFB4),
        textField = Color(0xFFE6E8EB),
        onTextField = Color(0xFFAEAFB4),
    )
}

val MaterialTheme.appColors: AppColors
    @Composable get() = LocalAppColors.current
