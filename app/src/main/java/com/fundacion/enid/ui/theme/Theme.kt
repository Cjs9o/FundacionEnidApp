package com.fundacion.enid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
// La paleta base vive en `:ui`. Este fichero está en el mismo paquete que el
// `Color.kt` de la app, así que sin estos imports dejaría de ver los colores.
import com.fundacion.enid.ui.shared.EnidBg
import com.fundacion.enid.ui.shared.EnidBlueMid
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidIconBox
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTeal
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

private val EnidColorScheme = lightColorScheme(
    primary = EnidDarkBlue,
    onPrimary = EnidWhite,
    primaryContainer = EnidIconBox,
    onPrimaryContainer = EnidDarkBlue,
    secondary = EnidBlueMid,
    onSecondary = EnidWhite,
    secondaryContainer = EnidChipDoneBg,
    onSecondaryContainer = EnidChipDoneText,
    tertiary = EnidTeal,
    onTertiary = EnidInk,
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
