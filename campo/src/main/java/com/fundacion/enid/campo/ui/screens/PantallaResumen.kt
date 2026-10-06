package com.fundacion.enid.campo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fundacion.enid.campo.data.model.EstadoSincronizacion
import com.fundacion.enid.campo.ui.components.AppScaffold
import com.fundacion.enid.campo.ui.components.BannerEstado
import com.fundacion.enid.campo.ui.components.BannerSinConexion
import com.fundacion.enid.campo.ui.components.BigButton
import com.fundacion.enid.campo.ui.components.BigOutlinedButton
import com.fundacion.enid.campo.ui.components.BotonSalirDelModulo
import com.fundacion.enid.campo.ui.components.ContadorCard
import com.fundacion.enid.campo.ui.components.EnidIconBox
import com.fundacion.enid.campo.ui.components.EnidIconos
import com.fundacion.enid.campo.ui.components.InfoCard
import com.fundacion.enid.campo.ui.components.LogoEnid
import com.fundacion.enid.campo.ui.components.NotaFormulario
import com.fundacion.enid.campo.ui.components.SectionTitle
import com.fundacion.enid.campo.ui.components.TonoBanner
import com.fundacion.enid.campo.ui.components.VerticalSpacer
import com.fundacion.enid.campo.ui.theme.EnidErrorTexto
import com.fundacion.enid.campo.ui.theme.EnidPendiente
import com.fundacion.enid.campo.ui.vm.ResumenViewModel
import com.fundacion.enid.campo.ui.vm.formatearFechaSincronizacion
import com.fundacion.enid.ui.shared.EnidBlueSoft
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidCardBg
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidDimens
import com.fundacion.enid.ui.shared.EnidIconBox
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidSincronizadoTexto
import com.fundacion.enid.ui.shared.EnidTextSec



/**
 * ===========================================================================
 * PANTALLA 1 · INICIO / RESUMEN
 * ===========================================================================
 * QUE TIENE QUE MOSTRAR EL TALLER: "cantidad de registros locales, pendientes y
 * sincronizados, ademas de accesos a nuevo registro, listado y sincronizacion".
 *
 * QUE NO DEBE HACER: NADA de lo que hay en esta pantalla abre un socket. Al
 * entrar solo lee Room. Los tres contadores salen de Flows sobre Room, asi que
 * suben en el instante en que otro proceso inserta.
 */
@Composable
fun PantallaResumen(
    vm: ResumenViewModel,
    onNuevaCaptura: () -> Unit,
    onVerListado: () -> Unit,
    onSalir: (() -> Unit)? = null
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

    AppScaffold(
        titulo = "Resumen de jornada",
        accionesCabecera = onSalir?.let { salir ->
            {
                BotonSalirDelModulo(onClick = salir)
            }
        },
        banner = {
            // El banner de red va DEBAJO de la cabecera, no encima: informa sin tapar.
            if (!estado.hayConexion) BannerSinConexion(estado.pendientes)
        },
        abajo = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(com.fundacion.enid.ui.shared.EnidBg)
                    .padding(horizontal = EnidDimens.margenPantalla)
            ) {
                // El Snackbar va ANTES de las acciones, no despues: al final del pie
                // quedaba por debajo del borde inferior de la ventana y nunca se veia.
                SnackbarHost(snackbarHost) { datos ->
                    Snackbar(
                        snackbarData = datos,
                        containerColor = EnidDarkBlue,
                        contentColor = EnidCardBg
                    )
                }

                // ---- PIE FIJO ---------------------------------------------------
                // Las tres acciones quedan ancladas abajo, FUERA del area desplazable.
                // Antes vivian dentro del scroll y, en cuanto aparecia el aviso de
                // "registros con error", SINCRONIZAR caia por debajo del borde y
                // obligaba a desplazar para llegar a la accion principal de la jornada.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp)
                ) {
                    BigButton(
                        texto = "NUEVO REGISTRO",
                        onClick = onNuevaCaptura,
                        icono = EnidIconos.Mas,
                        colorContenedor = EnidRed
                    )

                    VerticalSpacer(8)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BigOutlinedButton(
                            texto = "LISTADO",
                            onClick = onVerListado,
                            icono = EnidIconos.Listado,
                            modifier = Modifier.weight(1f)
                        )
                        BigButton(
                            texto = "SINCRONIZAR",
                            onClick = vm::sincronizar,
                            icono = EnidIconos.Sincronizar,
                            // El bloqueo del taller: deshabilitado mientras hay envio en curso.
                            habilitado = !estado.sincronizando && estado.hayParaEnviar,
                            cargando = estado.sincronizando,
                            modifier = Modifier.weight(1f),
                            colorContenedor =
                                if (estado.hayParaEnviar) EnidDarkBlue else EnidBorderSoft
                        )
                    }

                }

            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = EnidDimens.margenPantalla)
        ) {
            VerticalSpacer(16)

            // ---- Identidad de la app ---------------------------------
            InfoCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LogoEnid(alto = 34.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "ENID App",
                            style = MaterialTheme.typography.titleMedium,
                            color = EnidInk
                        )
                        Text(
                            "Captura de preinscripción en campo",
                            style = MaterialTheme.typography.bodySmall,
                            color = EnidTextSec
                        )
                    }
                }
            }

            VerticalSpacer(12)

            // ---- LOS TRES CONTADORES ----------------------------------
            // El taller pide "tres contadores + última sincronización".
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ContadorCard(
                    titulo = "Locales",
                    cifra = estado.total,
                    color = EnidDarkBlue,
                    modifier = Modifier.weight(1f)
                )
                ContadorCard(
                    titulo = "Pendientes",
                    cifra = estado.pendientes,
                    color = EnidPendiente,
                    modifier = Modifier.weight(1f)
                )
                ContadorCard(
                    titulo = "Sincronizados",
                    cifra = estado.contadores.sincronizados,
                    color = EnidSincronizadoTexto,
                    modifier = Modifier.weight(1f)
                )
            }

            // ---- Errores, solo si existen --------------------------------
            if (estado.contadores.conError > 0) {
                VerticalSpacer(12)
                FilaAviso(
                    icono = EnidIconos.Alerta,
                    texto = "${estado.contadores.conError} registro(s) con error de sincronización. " +
                        "Revísalos en el listado para corregirlos.",
                    color = EnidErrorTexto
                )
            }

            VerticalSpacer(12)

            // ---- Última sincronización -----------------------------------
            InfoCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EnidIconBox(EnidIconos.Sincronizar, fondo = EnidBlueSoft)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Última sincronización",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EnidTextSec
                        )
                        Text(
                            formatearFechaSincronizacion(estado.contadores.ultimaSincronizacion),
                            style = MaterialTheme.typography.titleMedium,
                            color = EnidInk
                        )
                        // El tipo de red es el de AHORA, no el de la ultima vez que se
                        // sincronizo: se rotula aparte para no mezclarlos. Antes caia
                        // en un "?: activa" que decia "Conexion: activa" mientras el
                        // banner de arriba advertise que no hay red.
                        Text(
                            text = "Red ahora: ${estado.tipoConexion ?: "sin conexión"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (estado.hayConexion) EnidTextSec else EnidErrorTexto
                        )
                    }
                }
            }

            VerticalSpacer(12)

            // ---- Barra de progreso de la sincronización ------------------
            if (estado.sincronizando) {
                VerticalSpacer(4)
                PanelProgreso(estado)
            }

            // ---- Aviso de trabajo offline ----------------------------------
            if (!estado.hayConexion) {
                VerticalSpacer(12)
                BannerEstado(
                    tono = TonoBanner.Offline,
                    texto = "Trabajando sin conexión. Las preinscripciones se guardan en este " +
                        "teléfono y no se enviarán hasta que pulses SINCRONIZAR.",
                    icono = EnidIconos.SinConexion
                )
            }

            VerticalSpacer(20)

            VerticalSpacer(20)

            // ---- Estado vacío guiado ----------------------------------------
            if (estado.total == 0) {
                InfoCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EnidIconBox(EnidIconos.Listado)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Todavía no hay visitas registradas. Empieza con la primera.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EnidInk
                            )
                        }
                    }
                }
                VerticalSpacer(16)
            }

            // ---- Explicación del modelo local ------------------------------
            NotaFormulario(
                "Todo lo que capture se guarda en este teléfono, sin internet. " +
                    "Firebase solo recibe los registros cuando tú lo ordenas con SINCRONIZAR."
            )

            VerticalSpacer(10)

            Text(
                text = when {
                    estado.sincronizando -> "Sincronizando, no pulses otra vez\u2026"
                    estado.hayParaEnviar ->
                        "Se enviar\u00e1n ${estado.pendientes} pendiente(s) y " +
                            "${estado.contadores.conError} con error, uno por uno."
                    estado.total == 0 -> "Todav\u00eda no hay registros. Empieza con el primero."
                    else -> "Todo est\u00e1 sincronizado. No hay nada pendiente de enviar."
                },
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )

            VerticalSpacer(28)
        }
    }
}

@Composable
private fun FilaAviso(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(EnidDimens.radioTarjeta))
            .background(com.fundacion.enid.campo.ui.theme.EnidErrorFondo)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall, color = color)
    }
}

@Composable
private fun PanelProgreso(estado: com.fundacion.enid.campo.ui.vm.EstadoResumen) {
    InfoCard(borde = EnidPendiente) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EnidIconBox(EnidIconos.Sincronizar, fondo = EnidBlueSoft)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Sincronizando con el servidor",
                    style = MaterialTheme.typography.titleMedium,
                    color = EnidInk
                )
                Text(
                    estado.registroEnCurso ?: "Preparando el lote…",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidTextSec,
                    maxLines = 1
                )
                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { estado.fraccionProgreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EnidPendiente,
                    trackColor = EnidIconBox
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Registro ${estado.procesadosEnLote + 1} de ${estado.totalEnLote}",
                    style = MaterialTheme.typography.labelMedium,
                    color = EnidTextSec
                )
            }
        }
    }
}
