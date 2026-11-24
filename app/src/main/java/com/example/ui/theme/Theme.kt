package com.example.ui.theme

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
data class QuizExtendedColors(
    val headerGradientStart: Color,
    val headerGradientEnd: Color,
    val correctContainer: Color,
    val onCorrectContainer: Color,
    val wrongContainer: Color,
    val onWrongContainer: Color,
    val optionSurface: Color,
    val cardBorder: Color,
    val goldAccent: Color,
    val completedSummaryBg: Color
)

private val LightQuizExtendedColors = QuizExtendedColors(
    headerGradientStart = SaffronPrimary,
    headerGradientEnd = SaffronPrimaryLight,
    correctContainer = CorrectContainerLight,
    onCorrectContainer = CorrectTextLight,
    wrongContainer = WrongContainerLight,
    onWrongContainer = WrongTextLight,
    optionSurface = WarmOptionSurface,
    cardBorder = WarmOutline,
    goldAccent = GoldAccentLight,
    completedSummaryBg = Color(0xFFFFF8F0)
)

private val DarkQuizExtendedColors = QuizExtendedColors(
    headerGradientStart = Color(0xFF4A2C0A),
    headerGradientEnd = Color(0xFF6A4011),
    correctContainer = CorrectContainerDark,
    onCorrectContainer = CorrectTextDark,
    wrongContainer = WrongContainerDark,
    onWrongContainer = WrongTextDark,
    optionSurface = WarmOptionSurfaceDark,
    cardBorder = WarmOutlineDark,
    goldAccent = GoldAccentDark,
    completedSummaryBg = Color(0xFF2B2014)
)

val LocalQuizColors = staticCompositionLocalOf { LightQuizExtendedColors }

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    onPrimary = OnSaffronPrimary,
    primaryContainer = SaffronContainer,
    onPrimaryContainer = OnSaffronContainer,
    secondary = AmberSecondary,
    onSecondary = OnAmberSecondary,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    tertiary = LotusTertiary,
    onTertiary = OnLotusTertiary,
    tertiaryContainer = LotusContainer,
    onTertiaryContainer = OnLotusContainer,
    background = WarmIvoryBackground,
    onBackground = WarmInk,
    surface = WarmSurface,
    onSurface = WarmInk,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = WarmInkMuted,
    outline = WarmOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = SaffronPrimaryDark,
    onPrimary = OnSaffronPrimaryDark,
    primaryContainer = SaffronContainerDark,
    onPrimaryContainer = OnSaffronContainerDark,
    secondary = AmberSecondaryDark,
    onSecondary = OnAmberSecondaryDark,
    secondaryContainer = AmberContainerDark,
    onSecondaryContainer = OnAmberContainerDark,
    tertiary = LotusTertiaryDark,
    onTertiary = OnLotusTertiaryDark,
    tertiaryContainer = LotusContainerDark,
    onTertiaryContainer = OnLotusContainerDark,
    background = WarmBackgroundDark,
    onBackground = WarmInkDark,
    surface = WarmSurfaceDark,
    onSurface = WarmInkDark,
    surfaceVariant = WarmSurfaceVariantDark,
    onSurfaceVariant = WarmInkMutedDark,
    outline = WarmOutlineDark
)

@Composable
fun BuddhismQuizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkQuizExtendedColors else LightQuizExtendedColors

    CompositionLocalProvider(LocalQuizColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
