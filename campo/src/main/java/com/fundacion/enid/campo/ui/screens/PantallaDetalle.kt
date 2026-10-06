package com.fundacion.enid.campo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fundacion.enid.campo.data.model.EstadoSincronizacion
import com.fundacion.enid.campo.ui.components.AppScaffold
import com.fundacion.enid.campo.ui.components.BigButton
import com.fundacion.enid.campo.ui.components.BigOutlinedButton
import com.fundacion.enid.campo.ui.components.ChipEstado
import com.fundacion.enid.campo.ui.components.ChipNeutro
import com.fundacion.enid.campo.ui.components.EnidIconBox
import com.fundacion.enid.campo.ui.components.EnidIconos
import com.fundacion.enid.campo.ui.components.EstadoVacio
import com.fundacion.enid.campo.ui.components.FotoEvidenciaAmpliada
import com.fundacion.enid.campo.ui.components.InfoCard
import com.fundacion.enid.campo.ui.components.ParDato
import com.fundacion.enid.campo.ui.components.SectionTitle
import com.fundacion.enid.campo.ui.components.SmallButton
import com.fundacion.enid.campo.ui.components.VerticalSpacer
import com.fundacion.enid.campo.ui.theme.EnidErrorFondo
import com.fundacion.enid.campo.ui.theme.EnidErrorTexto
import com.fundacion.enid.campo.ui.theme.EnidPendienteTexto
import com.fundacion.enid.campo.ui.vm.DetalleViewModel
import com.fundacion.enid.campo.ui.vm.formatearFechaSincronizacion
import com.fundacion.enid.ui.shared.EnidBg
import com.fundacion.enid.ui.shared.EnidBlueSoft
import com.fundacion.enid.ui.shared.EnidCardBg
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidDimens
import com.fundacion.enid.ui.shared.EnidIconBox
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidOroSoft
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTextSec
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



/**
 * ===========================================================================
 * PANTALLA 4 · DETALLE / REVISIÓN
 * ===========================================================================
 * CASOS DE COMPORTAMIENTO QUE CUMPLE
 * ---------------------------------------------------------------------------
 * · "Campos en pares, foto arriba"                    -> ParDato + FotoEvidenciaAmpliada
 * · "Muestra Sin conexión junto a Sincronizado el ..." -> fila de estado con ambos datos
 * · "Bloqueo de edición con el motivo del bloqueo,
 *    no un simple campo deshabilitado"               -> BannerSoloLectura + texto explicito
 * · "El usuario intenta editar un registro ya
 *    sincronizado: el botón de editar desaparece y
 *    sale un aviso de que ya fue enviado"             -> se cumple exactamente: el
 *                                                       botón NO se renderiza y se
 *                                                       muestra el motivo
 *
 * La evidencia se muestra ARRIBA y a tamaño completo porque es el documento que
 * el equipo de admisiones va a comparar con la cédula real. Dejarla en miniatura
 * obligaría a abrir la galería, y en campo eso no es posible.
 */
@Composable
fun PantallaDetalle(
    vm: DetalleViewModel,
    onVolver: () -> Unit,
    onEditar: (String) -> Unit
) {
    val estado by vm.estado.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHost.showSnackbar(it)
            vm.consumirMensaje()
        }
    }

    val registro = estado.registro

    AppScaffold(
        titulo = "Revisión del registro",
        onBack = onVolver,
        accionesCabecera = {
            registro?.let { ChipEstado(it.estado, compacto = true) }
        },
        abajo = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(EnidBg)
                    .padding(horizontal = EnidDimens.margenPantalla, vertical = 10.dp)
            ) {
                // El taller: si NO es editable, el boton NO aparece y se explica.
                if (estado.editable) {
                    BigButton(
                        texto = "EDITAR REGISTRO",
                        onClick = { registro?.let { onEditar(it.idRegistro) } },
                        icono = EnidIconos.Editar
                    )
                } else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(EnidOroSoft)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EnidIconos.Alerta.let {
                            androidx.compose.material3.Icon(
                                it, null, tint = EnidPendienteTexto, modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Solo lectura. Ya fue enviado al servidor y no se puede modificar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EnidPendienteTexto
                        )
                    }
                }
                SnackbarHost(snackbarHost) { datos ->
                    Snackbar(
                        snackbarData = datos,
                        containerColor = EnidDarkBlue,
                        contentColor = EnidCardBg
                    )
                }
            }
        }
    ) {
        if (registro == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                EstadoVacio(
                    titulo = "Registro no encontrado",
                    mensaje = "Este registro ya no está en el dispositivo. Es posible que se " +
                        "haya borrado de la base local.",
                    icono = EnidIconos.Alerta
                )
            }
            return@AppScaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = EnidDimens.margenPantalla)
        ) {
            VerticalSpacer(16)

            // ---- Identidad ------------------------------------------------
            InfoCard {
                Text(
                    registro.nombreCompleto,
                    style = MaterialTheme.typography.titleLarge,
                    color = EnidInk
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ChipNeutro("${registro.tipoDocumento} ${registro.documentoIdentidad}")
                    ChipNeutro("ID ${registro.idCorto()}")
                }
            }

            VerticalSpacer(12)

            // ---- Estado de sincronización ---------------------------------
            InfoCard {
                SectionTitle("Estado de la preinscripción")
                VerticalSpacer(10)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EnidIconBox(
                        when (registro.estado) {
                            EstadoSincronizacion.PENDIENTE -> EnidIconos.Reloj
                            EstadoSincronizacion.SINCRONIZADO -> EnidIconos.Confirmado
                            EstadoSincronizacion.ERROR -> EnidIconos.Alerta
                        },
                        fondo = when (registro.estado) {
                            EstadoSincronizacion.PENDIENTE -> EnidOroSoft
                            EstadoSincronizacion.SINCRONIZADO -> com.fundacion.enid.ui.shared.EnidSincronizadoFondo
                            EstadoSincronizacion.ERROR -> EnidErrorFondo
                        },
                        tinte = when (registro.estado) {
                            EstadoSincronizacion.PENDIENTE -> EnidPendienteTexto
                            EstadoSincronizacion.SINCRONIZADO -> com.fundacion.enid.ui.shared.EnidSincronizadoTexto
                            EstadoSincronizacion.ERROR -> EnidErrorTexto
                        }
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            when (registro.estado) {
                                EstadoSincronizacion.PENDIENTE ->
                                    "Guardado en este teléfono. Aún no se ha enviado."
                                EstadoSincronizacion.SINCRONIZADO ->
                                    "Confirmado por el servidor."
                                EstadoSincronizacion.ERROR ->
                                    "No se pudo enviar. Revise el motivo."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = EnidInk
                        )
                        Text(
                            text = when (registro.estado) {
                                EstadoSincronizacion.SINCRONIZADO ->
                                    "Sincronizado el ${formatearFechaSincronizacion(registro.fechaSincronizacion)}"
                                EstadoSincronizacion.PENDIENTE ->
                                    "Registrado el ${registro.fechaRegistroLegible} · Sin conexión usada"
                                EstadoSincronizacion.ERROR ->
                                    "Intentos de envío: ${registro.intentosSincronizacion}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = EnidTextSec
                        )
                    }
                }

                // Motivo real del error, con su acción de recuperación.
                val motivoError = registro.errorSincronizacion
                if (registro.estado == EstadoSincronizacion.ERROR && motivoError != null) {
                    Spacer(Modifier.height(12.dp))
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EnidErrorFondo)
                            .padding(12.dp)
                    ) {
                        Text(
                            "Motivo del error",
                            style = MaterialTheme.typography.labelMedium,
                            color = EnidErrorTexto
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            motivoError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = EnidErrorTexto
                        )
                        Spacer(Modifier.height(10.dp))
                        SmallButton(
                            texto = "Reintentar",
                            onClick = vm::reintentar,
                            icono = EnidIconos.Reintentar,
                            colorContenedor = EnidErrorTexto
                        )
                    }
                }
            }

            VerticalSpacer(12)

            // ---- Evidencia fotográfica (ARRIBA del bloque de datos) --------
            InfoCard {
                val evidencia = estado.evidencia
                SectionTitle(
                    "Evidencia fotográfica",
                    if (evidencia?.esValida == true) {
                        "Copia guardada dentro de la app, no en la galería"
                    } else {
                        "La copia interna no está disponible"
                    }
                )
                VerticalSpacer(12)
                FotoEvidenciaAmpliada(registro.rutaEvidenciaFoto)

                if (evidencia != null && !evidencia.esValida) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        evidencia.detalle,
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidErrorTexto
                    )
                }
            }

            VerticalSpacer(12)

            // ---- Datos de identificación -----------------------------------
            InfoCard {
                SectionTitle("Identificación")
                VerticalSpacer(6)
                ParDato("Nombre completo", registro.nombreCompleto)
                ParDato("Tipo de documento", estado.tipoDocumentoLegible)
                ParDato("Número", registro.documentoIdentidad)
                ParDato("Fecha de nacimiento", registro.fechaNacimiento)
                ParDato("Edad", "${registro.edad} años")
                ParDato("Municipio de residencia", registro.municipioResidencia)
            }

            VerticalSpacer(12)

            // ---- Datos de contacto ----------------------------------------
            InfoCard {
                SectionTitle("Contacto")
                VerticalSpacer(6)
                ParDato("Celular", registro.telefono)
                ParDato("Correo", registro.correoElectronico)
                ParDato("Municipio de la brigada", registro.municipioCaptura)
                ParDato("Jornada", registro.jornadaCaptura)
            }

            VerticalSpacer(12)

            // ---- Interés académico ----------------------------------------
            InfoCard {
                SectionTitle("Interés académico")
                VerticalSpacer(6)
                ParDato("Programa", estado.programaLegible)
                ParDato("Cómo conoció ENID", registro.medioConocimiento)
                if (registro.comoSeEntero.isNotBlank()) {
                    ParDato("Detalle", registro.comoSeEntero)
                }
                ParDato("Condiciones económicas", registro.condicionesEconomicas)
            }

            VerticalSpacer(12)

            // ---- Observaciones ---------------------------------------------
            InfoCard {
                SectionTitle("Observaciones del asesor")
                VerticalSpacer(8)
                Text(
                    registro.observaciones.ifBlank { "Sin observaciones registradas." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (registro.observaciones.isBlank()) EnidTextSec else EnidInk
                )
            }

            VerticalSpacer(12)

            // ---- Consentimiento --------------------------------------------
            InfoCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(
                        EnidIconos.Escudo,
                        null,
                        tint = if (registro.consentimientoDatos)
                            com.fundacion.enid.ui.shared.EnidSincronizadoTexto else EnidRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (registro.consentimientoDatos) {
                            "Consentimiento de tratamiento de datos: ACEPTADO"
                        } else {
                            "Consentimiento de tratamiento de datos: NO ACEPTADO"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = EnidInk
                    )
                }
            }

            VerticalSpacer(12)

            // ---- Metadatos técnicos (trazabilidad) --------------------------
            InfoCard {
                SectionTitle("Trazabilidad del registro")
                VerticalSpacer(6)
                ParDato("ID interno", registro.idRegistro)
                ParDato("Fecha y hora de captura", registro.fechaRegistroLegible)
                ParDato(
                    "Fecha de sincronización",
                    formatearFechaSincronizacion(registro.fechaSincronizacion)
                )
                ParDato("Intentos de envío", "${registro.intentosSincronizacion}")
                ParDato("Archivo de evidencia", archivoLegible(registro.rutaEvidenciaFoto))
            }

            VerticalSpacer(20)

            BigOutlinedButton(
                texto = "VOLVER AL LISTADO",
                onClick = onVolver,
                icono = EnidIconos.Volver,
                colorBorde = EnidBlueSoft,
                colorContenido = EnidDarkBlue
            )

            VerticalSpacer(28)
        }
    }
}

private fun archivoLegible(ruta: String?): String {
    if (ruta.isNullOrBlank()) return "—"
    val nombre = ruta.substringAfterLast('/')
    val archivo = java.io.File(ruta)
    return if (archivo.exists()) {
        "$nombre · ${archivo.length() / 1024} KB"
    } else {
        "$nombre · NO DISPONIBLE"
    }
}
