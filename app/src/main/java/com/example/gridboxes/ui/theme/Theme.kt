package com.example.gridboxes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class GridBoxesPalette(
    val background: Color,
    val surface: Color,
    val boardSurface: Color,
    val surfaceBorder: Color,
    val gridAnchor: Color,
    val gridAnchorSubtle: Color,
    val borderUnused: Color,
    val borderUsed: Color,
    val playerOnePrimary: Color,
    val playerOneFill: Color,
    val playerOneContainer: Color,
    val playerTwoPrimary: Color,
    val playerTwoFill: Color,
    val playerTwoContainer: Color,
    val accentGold: Color,
    val lastMoveHighlight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDark: Boolean
)

val LocalGridBoxesPalette = staticCompositionLocalOf {
    GridBoxesPalette(
        background = ClassicBackgroundDark,
        surface = ClassicSurfaceDark,
        boardSurface = ClassicBoardSurfaceDark,
        surfaceBorder = ClassicSurfaceBorderDark,
        gridAnchor = ClassicGridAnchorDark,
        gridAnchorSubtle = ClassicGridAnchorSubtleDark,
        borderUnused = ClassicBorderUnusedDark,
        borderUsed = ClassicBorderUsedDark,
        playerOnePrimary = PlayerOnePrimary,
        playerOneFill = PlayerOneFill,
        playerOneContainer = PlayerOneContainer,
        playerTwoPrimary = PlayerTwoPrimary,
        playerTwoFill = PlayerTwoFill,
        playerTwoContainer = PlayerTwoContainer,
        accentGold = AccentGold,
        lastMoveHighlight = LastMoveGlow,
        textPrimary = TextPrimaryDark,
        textSecondary = TextSecondaryDark,
        textMuted = TextMutedDark,
        isDark = true
    )
}

val ClassicDarkPalette = GridBoxesPalette(
    background = ClassicBackgroundDark,
    surface = ClassicSurfaceDark,
    boardSurface = ClassicBoardSurfaceDark,
    surfaceBorder = ClassicSurfaceBorderDark,
    gridAnchor = ClassicGridAnchorDark,
    gridAnchorSubtle = ClassicGridAnchorSubtleDark,
    borderUnused = ClassicBorderUnusedDark,
    borderUsed = ClassicBorderUsedDark,
    playerOnePrimary = PlayerOnePrimary,
    playerOneFill = PlayerOneFill,
    playerOneContainer = PlayerOneContainer,
    playerTwoPrimary = PlayerTwoPrimary,
    playerTwoFill = PlayerTwoFill,
    playerTwoContainer = PlayerTwoContainer,
    accentGold = AccentGold,
    lastMoveHighlight = LastMoveGlow,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    textMuted = TextMutedDark,
    isDark = true
)

val MidnightDarkPalette = GridBoxesPalette(
    background = MidnightBackgroundDark,
    surface = MidnightSurfaceDark,
    boardSurface = MidnightBoardSurfaceDark,
    surfaceBorder = MidnightSurfaceBorderDark,
    gridAnchor = MidnightGridAnchorDark,
    gridAnchorSubtle = MidnightGridAnchorSubtleDark,
    borderUnused = MidnightBorderUnusedDark,
    borderUsed = MidnightBorderUsedDark,
    playerOnePrimary = PlayerOnePrimary,
    playerOneFill = PlayerOneFill,
    playerOneContainer = PlayerOneContainer,
    playerTwoPrimary = PlayerTwoPrimary,
    playerTwoFill = PlayerTwoFill,
    playerTwoContainer = PlayerTwoContainer,
    accentGold = AccentGold,
    lastMoveHighlight = LastMoveGlow,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    textMuted = TextMutedDark,
    isDark = true
)

val LightPalette = GridBoxesPalette(
    background = SlateBackgroundLight,
    surface = SlateSurfaceLight,
    boardSurface = SlateBoardSurfaceLight,
    surfaceBorder = SlateSurfaceBorderLight,
    gridAnchor = SlateGridAnchorLight,
    gridAnchorSubtle = Color(0xFF6B7280),
    borderUnused = SlateBorderUnusedLight,
    borderUsed = SlateBorderUsedLight,
    playerOnePrimary = PlayerOnePrimary,
    playerOneFill = PlayerOneFill,
    playerOneContainer = Color(0xFFFFECE5),
    playerTwoPrimary = PlayerTwoPrimary,
    playerTwoFill = PlayerTwoFill,
    playerTwoContainer = Color(0xFFE2F4F2),
    accentGold = Color(0xFFC48B28),
    lastMoveHighlight = Color(0xFFE5A100),
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    textMuted = TextMutedLight,
    isDark = false
)

private val DarkM3ColorScheme = darkColorScheme(
    primary = PlayerOnePrimary,
    secondary = PlayerTwoPrimary,
    background = ClassicBackgroundDark,
    surface = ClassicSurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark
)

private val LightM3ColorScheme = lightColorScheme(
    primary = PlayerOnePrimary,
    secondary = PlayerTwoPrimary,
    background = SlateBackgroundLight,
    surface = SlateSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

@Composable
fun GridBoxesTheme(
    themeMode: String = "system", // "system", "dark", "light"
    boardTheme: String = "classic", // "classic", "midnight"
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemDark
    }

    val palette = when {
        !isDark -> LightPalette
        boardTheme == "midnight" -> MidnightDarkPalette
        else -> ClassicDarkPalette
    }

    val colorScheme = if (isDark) DarkM3ColorScheme else LightM3ColorScheme

    CompositionLocalProvider(LocalGridBoxesPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}