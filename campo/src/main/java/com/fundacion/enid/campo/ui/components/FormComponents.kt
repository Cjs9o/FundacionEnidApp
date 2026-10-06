package com.fundacion.enid.campo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fundacion.enid.campo.ui.theme.EnidBorderSoft
import com.fundacion.enid.campo.ui.theme.EnidCardBg
import com.fundacion.enid.campo.ui.theme.EnidDarkBlue
import com.fundacion.enid.campo.ui.theme.EnidErrorFondo
import com.fundacion.enid.campo.ui.theme.EnidErrorTexto
import com.fundacion.enid.campo.ui.theme.EnidIconBox
import com.fundacion.enid.campo.ui.theme.EnidInk
import com.fundacion.enid.campo.ui.theme.EnidRed
import com.fundacion.enid.campo.ui.theme.EnidSincronizadoFondo
import com.fundacion.enid.campo.ui.theme.EnidSincronizadoTexto
import com.fundacion.enid.campo.ui.theme.EnidTextSec

/**
 * BLOQUE DE EVIDENCIA FOTOGRAFICA
 * ===========================================================================
 * Es el componente mas importante de la pantalla de captura, porque de el
 * depende el requisito "cada registro debe incluir minimo 1 imagen".
 *
 * DECIONES DE DISENO
 * ---------------------------------------------------------------------------
 * · Ofrece DOS vias explicitas: CAMARA y GALERIA. El caso de uso real es tomar
 *   la foto al documento que el aspirante trae en la mano, pero en un escritorio
 *   de admisiones es igual de legitimo adjuntar una foto ya existente.
 * · El estado vacio NUNCA se muestra como un hueco en blanco: dice exactamente
 *   que falta y por que. Es el requisito del taller sobre interfaz que refleja
 *   estados y errores.
 * · El error de evidencia tiene su propio fondo y su propio mensaje debajo, y no
 *   se mezcla con los errores de texto.
 * · La miniatura se muestra en vivo en cuanto hay foto: el asesor confirma que
 *   salio legible ANTES de guardar, que es donde se detecta un Foto Botero
 *   demasiado oscuro.
 */
@Composable
fun BloqueEvidencia(
    rutaFoto: String?,
    onAbrirCamara: () -> Unit,
    onAbrirGaleria: () -> Unit,
    mensajeError: String? = null,
    modifier: Modifier = Modifier,
    esError: Boolean = false,
    soloLectura: Boolean = false
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Evidencia fotográfica",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidInk,
                modifier = Modifier.weight(1f)
            )
            Text(
                "OBLIGATORIO",
                style = MaterialTheme.typography.labelMedium,
                color = EnidRed,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Fotografía del documento de identidad. Sin esta foto no se puede guardar " +
                "la preinscripción.",
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec
        )

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            // ---- Miniatura o estado vacio --------------------------------
            Column(
                Modifier
                    .width(112.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(EnidIconBox)
                    .border(
                        if (esError) 2.dp else 1.5.dp,
                        if (esError) EnidRed else EnidBorderSoft,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EnidCardBg),
                    contentAlignment = Alignment.Center
                ) {
                    FotoEvidencia(rutaFoto, lado = 96.dp)
                    if (rutaFoto.isNullOrBlank()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                EnidIconos.Documento,
                                null,
                                tint = EnidDarkBlue.copy(alpha = 0.4f),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Sin foto",
                                style = MaterialTheme.typography.labelMedium,
                                color = EnidDarkBlue.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    if (rutaFoto.isNullOrBlank()) "PENDIENTE" else "ADJUNTA",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (rutaFoto.isNullOrBlank()) EnidRed else EnidSincronizadoTexto
                )
            }

            // ---- Acciones -----------------------------------------------
            Column(Modifier.weight(1f)) {
                if (soloLectura) {
                    Text(
                        "Registro ya sincronizado. La evidencia queda en solo lectura para " +
                            "que lo que se ve en el teléfono sea exactamente lo que está en el " +
                            "servidor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec
                    )
                } else {
                    BigOutlinedButton(
                        texto = "Tomar foto",
                        onClick = onAbrirCamara,
                        icono = EnidIconos.Camara,
                        colorBorde = EnidDarkBlue,
                        colorContenido = EnidDarkBlue
                    )
                    Spacer(Modifier.height(8.dp))
                    BigOutlinedButton(
                        texto = "Elegir de la galería",
                        onClick = onAbrirGaleria,
                        icono = EnidIconos.Galeria,
                        colorBorde = EnidBorderSoft,
                        colorContenido = EnidInk
                    )
                }
            }
        }

        // ---- Mensaje de error bajo el bloque, no escondido --------------
        if (esError && mensajeError != null) {
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EnidErrorFondo)
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    EnidIconos.Alerta,
                    null,
                    tint = EnidErrorTexto,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    mensajeError,
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidErrorTexto
                )
            }
        } else if (!rutaFoto.isNullOrBlank()) {
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EnidSincronizadoFondo)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    EnidIconos.Confirmado,
                    null,
                    tint = EnidSincronizadoTexto,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Evidencia lista. Se copiará a la carpeta interna de la app al guardar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidSincronizadoTexto
                )
            }
        }
    }
}

/**
 * BLOQUE DE DATOS: agrupa los campos por significado, nunca como lista plana.
 * El taller lo pide explicito en "Pantallas minimas > Captura".
 */
@Composable
fun BloqueDeDatos(
    titulo: String,
    subtitulo: String?,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    InfoCard(modifier) {
        SectionTitle(titulo, subtitulo)
        VerticalSpacer(12)
        contenido()
    }
}

/**
 * Fila compacta para un dato de una sola linea (por ejemplo el selector de
 * programa), con el chip del valor a la derecha.
 */
@Composable
fun FilaResumen(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    colorValor: Color = EnidInk
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            valor,
            style = MaterialTheme.typography.bodyMedium,
            color = colorValor,
            fontWeight = FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            modifier = Modifier.weight(0.5f)
        )
    }
}
