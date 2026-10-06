package com.fundacion.enid.campo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.fundacion.enid.ui.shared.EnidDimens
import com.fundacion.enid.ui.shared.EnidSincronizadoTexto
import com.fundacion.enid.ui.shared.EnidSincronizadoFondo
import com.fundacion.enid.ui.shared.EnidOroSoft
import com.fundacion.enid.ui.shared.EnidOro
import com.fundacion.enid.ui.shared.EnidVerdeWhatsapp
import com.fundacion.enid.ui.shared.EnidBg
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidCardBg
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidIconBox
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

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
 * Las métricas del sistema visual viven en `:ui` (`EnidDimens`), junto con la
 * paleta base. Este módulo ya no mantiene su propia copia: tener las medidas
 * en dos sitios era exactamente el problema que el módulo compartido resuelve.
 */

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
