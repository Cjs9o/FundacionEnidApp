package com.fundacion.enid.campo.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.fundacion.enid.campo.ui.components.AppScaffold
import com.fundacion.enid.campo.ui.components.BannerSinConexion
import com.fundacion.enid.campo.ui.components.BigButton
import com.fundacion.enid.campo.ui.components.EnidIconos
import com.fundacion.enid.campo.ui.components.EstadoVacio
import com.fundacion.enid.campo.ui.components.SmallButton
import com.fundacion.enid.campo.ui.components.TarjetaAspirante
import com.fundacion.enid.campo.ui.components.VerticalSpacer
import com.fundacion.enid.campo.ui.vm.FiltroListado
import com.fundacion.enid.campo.ui.vm.ListadoViewModel
import com.fundacion.enid.ui.shared.EnidBg
import com.fundacion.enid.ui.shared.EnidBlueSoft
import com.fundacion.enid.ui.shared.EnidCardBg
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidDimens
import com.fundacion.enid.ui.shared.EnidIconBox
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidTextSec



/**
 * ===========================================================================
 * PANTALLA 3 · LISTADO LOCAL
 * ===========================================================================
 * CASOS DE COMPORTAMIENTO QUE CUMPLE, tal como los define el taller:
 *
 * · "Tarjetas con miniatura, fecha, id y chip"      -> TarjetaAspirante
 * · "No hay visitas con este filtro + volver a Todas" -> EstadoVacio
 * · "Estado ERROR con el motivo real bajo el chip y
 *    botón Reintentar"                              -> TarjetaAspirante
 * · "No cambia: la lista es local"                  -> no hay red en esta pantalla
 *
 * `LazyColumn` y no `Column` + `verticalScroll`: la lista crece durante la
 * jornada y materializar las 400 tarjetas a la vez es lo que hace que una app
 * de captura se sienta lenta en equipos de gama baja.
 */
@Composable
fun PantallaListado(
    vm: ListadoViewModel,
    onVolver: () -> Unit,
    onNuevaCaptura: () -> Unit,
    onAbrirDetalle: (String) -> Unit
) {
    val estado by vm.estado.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHost.showSnackbar(it)
            vm.consumirMensaje()
        }
    }

    AppScaffold(
        titulo = "Listado local",
        onBack = onVolver,
        banner = {
            if (!estado.hayConexion) BannerSinConexion(estado.registros.size)
        },
        abajo = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(EnidBg)
                    .padding(horizontal = EnidDimens.margenPantalla, vertical = 10.dp)
            ) {
                BigButton(
                    texto = "NUEVO REGISTRO",
                    onClick = onNuevaCaptura,
                    icono = EnidIconos.Mas
                )
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
        Column(Modifier.fillMaxSize()) {

            // ---- Filtros -------------------------------------------------
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(EnidCardBg)
                    .padding(horizontal = EnidDimens.margenPantalla, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FiltroListado.entries.forEach { opcion ->
                    ChipFiltro(
                        texto = opcion.etiqueta,
                        activo = opcion == filtro,
                        modifier = Modifier.weight(1f),
                        onClick = { vm.cambiarFiltro(opcion) }
                    )
                }
            }

            // ---- Contador del filtro activo -------------------------------
            Text(
                text = "${estado.total} ${if (estado.total == 1) "registro" else "registros"} " +
                    "· ${filtro.etiqueta.lowercase()}",
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec,
                modifier = Modifier.padding(horizontal = EnidDimens.margenPantalla, vertical = 8.dp)
            )

            if (estado.estaVacio) {
                // El taller pide acción dentro del estado vacío, no solo texto.
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    EstadoVacio(
                        titulo = if (filtro == FiltroListado.TODOS) {
                            "Todavía no hay registros"
                        } else {
                            "No hay registros con este filtro"
                        },
                        mensaje = if (filtro == FiltroListado.TODOS) {
                            "Empieza con la primera preinscripción. Todo se guarda en este " +
                                "teléfono, sin necesidad de internet."
                        } else {
                            "No hay registros en estado ${filtro.etiqueta.lowercase()}. " +
                                "Vuelve a ver todas las pestañas."
                        },
                        icono = if (filtro == FiltroListado.TODOS) EnidIconos.Listado else EnidIconos.Filtro,
                        accion = if (filtro != FiltroListado.TODOS) {
                            {
                                SmallButton(
                                    texto = "Ver todas",
                                    onClick = { vm.cambiarFiltro(FiltroListado.TODOS) }
                                )
                            }
                        } else {
                            { SmallButton(texto = "Crear el primero", onClick = onNuevaCaptura) }
                        }
                    )
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = EnidDimens.margenPantalla,
                        end = EnidDimens.margenPantalla,
                        top = 4.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(EnidDimens.separacionTarjeta)
                ) {
                    items(estado.registros, key = { it.idRegistro }) { registro ->
                        TarjetaAspirante(
                            registro = registro,
                            onClick = { onAbrirDetalle(registro.idRegistro) },
                            onReintentar = if (registro.estado.esSincronizable) {
                                { vm.reintentar(registro.idRegistro) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipFiltro(
    texto: String,
    activo: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier
            .clip(CircleShape)
            .background(if (activo) EnidDarkBlue else EnidIconBox)
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelMedium,
            color = if (activo) EnidCardBg else EnidDarkBlue,
            maxLines = 1
        )
    }
}
