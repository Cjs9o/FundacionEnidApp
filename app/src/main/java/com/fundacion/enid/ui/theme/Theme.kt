package com.fundacion.enid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EnidColorScheme = lightColorScheme(
    primary = EnidDarkBlue,
    onPrimary = EnidWhite,
    primaryContainer = EnidIconBox,
    onPrimaryContainer = EnidDarkBlue,
    secondary = EnidGreen,
    onSecondary = EnidWhite,
    secondaryContainer = EnidChipDoneBg,
    onSecondaryContainer = EnidChipDoneText,
    tertiary = EnidPurple,
    onTertiary = EnidWhite,
    tertiaryContainer = EnidIconBox,
    onTertiaryContainer = EnidDarkBlue,
    background = EnidBg,
    onBackground = EnidInk,
    surface = EnidWhite,
    onSurface = EnidInk,
    surfaceVariant = EnidBg,
    onSurfaceVariant = EnidTextSec,
    outline = EnidBorder,
    error = EnidRed,
    onError = EnidWhite
)

@Composable
fun FundacionEnidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EnidColorScheme,
        typography = Typography,
        content = content
    )
}
