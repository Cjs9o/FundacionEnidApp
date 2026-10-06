package com.fundacion.enid.campo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * Esquema de color único.
 *
 * Decisión documentada: la app se usa a plena luz solar en audiencias y plazas
 * audiencias y plazas rurales, por eso se fija explícitamente el esquema claro
 * y NO se aplica color dinámico de Android. El sistema visual del grupo manda
 * sobre el del dispositivo.
 */
private val EnidColorScheme = lightColorScheme(
    primary = EnidDarkBlue,
    onPrimary = EnidWhite,
    primaryContainer = EnidIconBox,
    onPrimaryContainer = EnidDarkBlue,
    secondary = EnidVerdeWhatsapp,
    onSecondary = EnidWhite,
    secondaryContainer = EnidSincronizadoFondo,
    onSecondaryContainer = EnidSincronizadoTexto,
    tertiary = EnidOro,
    onTertiary = EnidWhite,
    tertiaryContainer = EnidOroSoft,
    onTertiaryContainer = EnidOro,
    background = EnidBg,
    onBackground = EnidInk,
    surface = EnidCardBg,
    onSurface = EnidInk,
    surfaceVariant = EnidBg,
    onSurfaceVariant = EnidTextSec,
    outline = EnidBorderSoft,
    outlineVariant = EnidBorderSoft,
    error = EnidRed,
    onError = EnidWhite,
    errorContainer = EnidErrorFondo,
    onErrorContainer = EnidErrorTexto
)

/**
 * Métricas de contenedores del sistema visual.
 *   · Margen horizontal de pantalla : 20 dp
 *   · Separación entre tarjetas    : 12 dp
 *   · Separación entre secciones   : 20 dp
 *   · Padding interno de tarjeta   : 14 dp
 *   · Alto mínimo táctil           : 48 dp
 */
object EnidDimens {
    val margenPantalla = 20.dp
    val separacionTarjeta = 12.dp
    val separacionSeccion = 20.dp
    val paddingTarjeta = 14.dp
    val altoTactilMinimo = 48.dp
    val radioTarjeta = 12.dp
    val radioCampo = 8.dp
    val altoBotonPrimario = 56.dp
}

private val EnidShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(EnidDimens.radioCampo),
    medium = RoundedCornerShape(EnidDimens.radioTarjeta),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun EnidAppTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EnidColorScheme,
        typography = EnidTypography,
        shapes = EnidShapes,
        content = content
    )
}
