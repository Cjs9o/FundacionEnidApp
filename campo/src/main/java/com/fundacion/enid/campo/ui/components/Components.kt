package com.fundacion.enid.campo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fundacion.enid.campo.R
import com.fundacion.enid.campo.data.local.AspiranteEntity
import com.fundacion.enid.campo.data.model.EstadoSincronizacion
import com.fundacion.enid.campo.ui.theme.EnidErrorFondo
import com.fundacion.enid.campo.ui.theme.EnidErrorTexto
import com.fundacion.enid.campo.ui.theme.EnidPendiente
import com.fundacion.enid.campo.ui.theme.EnidPendienteFondo
import com.fundacion.enid.campo.ui.theme.EnidPendienteTexto
import com.fundacion.enid.ui.shared.EnidFotoPlaceholder
import com.fundacion.enid.ui.shared.EnidBg
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidCardBg
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidDimens
import com.fundacion.enid.ui.shared.EnidIconBox
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidOroSoft
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidSincronizadoFondo
import com.fundacion.enid.ui.shared.EnidSincronizadoTexto
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidVerdeSoft
import com.fundacion.enid.ui.shared.EnidWhite
import java.io.File



/**
 * BIBLIOTECA DE COMPONENTES
 * ===========================================================================
 * Todos los bloques visuales del taller salen de aqui, para que las cuatro
 * pantallas sean visualmente consistentes y el sistema visual se pueda
 * defender punto por punto.
 */

// =============================================================== ESTRUCTURA ==

/**
 * Marco de todas las pantallas.
 *
 * @param titulo texto de la cabecera
 * @param onBack si no es null, muestra la flecha de volver
 * @param accionesCompose contenido opcional a la derecha de la cabecera
 * @param bannerContenido se inserta DEBAJO de la cabecera: es donde vive el
 *        aviso de trabajo offline, para que no tape nada ni interrumpa el flujo
 */
@Composable
fun AppScaffold(
    titulo: String,
    onBack: (() -> Unit)? = null,
    accionesCabecera: @Composable (() -> Unit)? = null,
    banner: @Composable (() -> Unit)? = null,
    abajo: @Composable (() -> Unit)? = null,
    contenido: @Composable () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(EnidBg)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        CabeceraEnid(titulo = titulo, onBack = onBack, acciones = accionesCabecera)
        if (banner != null) banner()
        Box(Modifier.weight(1f)) { contenido() }
        if (abajo != null) abajo()
        // Sin esto, el pie con acciones queda tapado por la barra de navegacion
        // del sistema y el boton principal deja de ser alcanzable.
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
private fun CabeceraEnid(
    titulo: String,
    onBack: (() -> Unit)?,
    acciones: @Composable (() -> Unit)?
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(EnidDarkBlue)
            .padding(horizontal = EnidDimens.margenPantalla)
            .padding(bottom = 14.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = EnidDimens.altoTactilMinimo),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        EnidIconos.Volver,
                        contentDescription = "Volver",
                        tint = EnidWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(Modifier.width(4.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge,
                    color = EnidWhite
                )
                Text(
                    text = "Fundación Educativa ENID · Captura en campo",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidWhite.copy(alpha = 0.72f)
                )
            }
            if (acciones != null) acciones()
        }
    }
}

// ================================================================== BANNERS ==

/**
 * BOTON "VOLVER A LA APP" EN LA CABECERA DEL RESUMEN.
 *
 * Este modulo nació como app independiente y su resumen era la pantalla de
 * inicio, sin nada de dónde salir. Al integrarse dentro de `FundacionEnidApp`
 * eso ya no es cierto: se entra desde el menú y hay que poder volver.
 *
 * Va en la cabecera, no como un boton suelto en el pie, porque el pie lo ocupa
 * la accion principal de la jornada (SINCRONIZAR) y el usuario no debe tener
 * que desplazarse para salir. Y es texto y no un icono de flecha: la flecha ya
 * significa "volver a la pantalla anterior" dentro del modulo, y esta accion
 * es otra cosa —salir del modulo—, que se distingue por las palabras.
 */
@Composable
fun BotonSalirDelModulo(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, EnidWhite.copy(alpha = 0.75f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = EnidWhite),
        modifier = Modifier.heightIn(min = 48.dp)
    ) {
        Text(
            text = "Volver a la app",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * BANNER DE ESTADO — el bloque que el taller pide explicitly.
 *
 * Es la version estatica del componente (no la que anuncia cambios al TalkBack
 * como la NotaFormulario del proyecto base): el banner de red se dibuja una
 * sola vez por cambio de estado, asi que un `liveRegion` con polite mantiene al
 * TalkBack informado sin interruptiones.
 */
@Composable
fun BannerEstado(
    tono: TonoBanner,
    texto: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null
) {
    val fondo = when (tono) {
        TonoBanner.Offline -> EnidOroSoft
        TonoBanner.Exito -> EnidVerdeSoft
        TonoBanner.Error -> EnidErrorFondo
        TonoBanner.Info -> EnidIconBox
    }
    val texto1 = when (tono) {
        TonoBanner.Offline -> EnidPendienteTexto
        TonoBanner.Exito -> EnidSincronizadoTexto
        TonoBanner.Error -> EnidErrorTexto
        TonoBanner.Info -> EnidDarkBlue
    }

    Row(
        modifier
            .fillMaxWidth()
            .background(fondo)
            .padding(horizontal = EnidDimens.margenPantalla, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = texto1, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
        }
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            color = texto1,
            modifier = Modifier.weight(1f)
        )
    }
}

enum class TonoBanner { Offline, Exito, Error, Info }

/**
 * Aviso fijo de trabajo offline. Aparece mientras `conectado == false`.
 * El texto es exactamente el que el taller fijo para el caso "sin Internet al
 * abrir la app": informa y no interrumpe.
 */
@Composable
fun BannerSinConexion(pendientes: Int, modifier: Modifier = Modifier) {
    val sufijo = when (pendientes) {
        0 -> "No hay registros pendientes."
        1 -> "1 registro pendiente en este teléfono."
        else -> "$pendientes registros pendientes en este teléfono."
    }
    BannerEstado(
        tono = TonoBanner.Offline,
        texto = "Trabajando sin conexión. " + sufijo + " Se enviarán solo cuando pulses SINCRONIZAR.",
        modifier = modifier,
        icono = EnidIconos.SinConexion
    )
}

// ================================================================== CHIPS ====

/**
 * CHIP DE ESTADO — doble codificacion (color + punto + texto).
 *
 * El taller senala que un chip solo de color NO cumple la regla de
 * accesibilidad. Este componente por eso siempre lleva las tres señales:
 * el punto de color, el icono del estado y la etiqueta con texto.
 */
@Composable
fun ChipEstado(
    estado: EstadoSincronizacion,
    modifier: Modifier = Modifier,
    compacto: Boolean = false
) {
    val fondo: Color
    val colorTexto: Color
    val icono: ImageVector

    when (estado) {
        EstadoSincronizacion.PENDIENTE -> {
            fondo = EnidPendienteFondo
            colorTexto = EnidPendienteTexto
            icono = EnidIconos.Reloj
        }
        EstadoSincronizacion.SINCRONIZADO -> {
            fondo = EnidSincronizadoFondo
            colorTexto = EnidSincronizadoTexto
            icono = EnidIconos.Confirmado
        }
        EstadoSincronizacion.ERROR -> {
            fondo = EnidErrorFondo
            colorTexto = EnidErrorTexto
            icono = EnidIconos.Alerta
        }
    }

    Row(
        modifier
            .clip(CircleShape)
            .background(fondo)
            .padding(horizontal = if (compacto) 10.dp else 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Punto: primera senal de color, redundante con el fondo del chip.
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    when (estado) {
                        EstadoSincronizacion.PENDIENTE -> EnidPendiente
                        EstadoSincronizacion.SINCRONIZADO -> EnidSincronizadoTexto
                        EstadoSincronizacion.ERROR -> EnidErrorTexto
                    }
                )
        )
        Spacer(Modifier.width(6.dp))
        Icon(
            icono,
            contentDescription = null,
            tint = colorTexto,
            modifier = Modifier.size(if (compacto) 14.dp else 16.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = estado.etiqueta.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = colorTexto
        )
    }
}

/** Chip neutral para metadatos (tipo de documento, jornada, municipio). */
@Composable
fun ChipNeutro(texto: String, icono: ImageVector? = null, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(CircleShape)
            .background(EnidIconBox)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Icon(icono, null, tint = EnidDarkBlue, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(5.dp))
        }
        Text(texto, style = MaterialTheme.typography.labelMedium, color = EnidDarkBlue)
    }
}

// =============================================================== CONTENEDOR ==

/** Tarjeta blanca: el contenedor base de contenido. */
@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    borde: Color = EnidBorderSoft,
    padding: androidx.compose.ui.unit.Dp = EnidDimens.paddingTarjeta,
    contenido: @Composable () -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(EnidDimens.radioTarjeta))
            .background(EnidCardBg)
            .border(1.5.dp, borde, RoundedCornerShape(EnidDimens.radioTarjeta))
            .padding(padding)
    ) { contenido() }
}

/** Titulo de seccion en mayusculas y negrita (titleSmall). */
@Composable
fun SectionTitle(texto: String, subtitulo: String? = null, modifier: Modifier = Modifier) {
    Column(modifier.padding(vertical = 4.dp)) {
        Text(
            texto.uppercase(),
            style = MaterialTheme.typography.titleSmall,
            color = EnidDarkBlue
        )
        if (subtitulo != null) {
            Text(
                subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )
        }
    }
}

@Composable
fun VerticalSpacer(alto: Int = 12) = Spacer(Modifier.height(alto.dp))

@Composable
fun HorizontalSpacer(ancho: Int = 10) = Spacer(Modifier.width(ancho.dp))

/** Caja de icono redondeada de 44dp. */
@Composable
fun EnidIconBox(
    icono: ImageVector,
    modifier: Modifier = Modifier,
    tamano: androidx.compose.ui.unit.Dp = 44.dp,
    fondo: Color = EnidIconBox,
    tinte: Color = EnidDarkBlue
) {
    Box(
        modifier
            .size(tamano)
            .clip(RoundedCornerShape(10.dp))
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(22.dp))
    }
}

// ================================================================= BOTONES ==

/** Boton principal. Alto minimo 48dp, radio 10dp. */
@Composable
fun BigButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    colorContenedor: Color = EnidDarkBlue,
    colorContenido: Color = EnidWhite,
    icono: ImageVector? = null,
    cargando: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(EnidDimens.altoBotonPrimario),
        enabled = habilitado && !cargando,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorContenedor,
            contentColor = colorContenido,
            disabledContainerColor = EnidBorderSoft,
            disabledContentColor = EnidWhite
        )
    ) {
        if (cargando) {
            CircularProgressIndicator(
                color = colorContenido,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
            )
            Spacer(Modifier.width(10.dp))
        } else if (icono != null) {
            Icon(icono, null, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
        }
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

/** Boton secundario con borde. */
@Composable
fun BigOutlinedButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    colorBorde: Color = EnidDarkBlue,
    colorContenido: Color = EnidDarkBlue,
    icono: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(EnidDimens.altoTactilMinimo),
        enabled = habilitado,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, colorBorde),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colorContenido,
            disabledContentColor = EnidTextSec
        )
    ) {
        if (icono != null) {
            Icon(icono, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

/** Boton compacto para acciones dentro de una tarjeta. */
@Composable
fun SmallButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorContenedor: Color = EnidDarkBlue,
    colorContenido: Color = EnidWhite,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorContenedor,
            contentColor = colorContenido
        )
    ) {
        if (icono != null) {
            Icon(icono, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(texto, style = MaterialTheme.typography.labelMedium)
    }
}

// ================================================================== CAMPOS ===

/** Campo de texto con la tipografia del sistema visual. */
@Composable
fun BigTextField(
    valor: String,
    onValueChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    esError: Boolean = false,
    mensajeError: String? = null,
    ayuda: String? = null,
    soloDigitos: Boolean = false,
    maxCaracteres: Int? = null,
    lineasVisibles: Int = 1
) {
    val textoAyuda = mensajeError ?: ayuda
    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo ->
            val filtrado = if (soloDigitos) nuevo.filter { it.isDigit() } else nuevo
            if (maxCaracteres == null || filtrado.length <= maxCaracteres) onValueChange(filtrado)
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text(etiqueta, style = MaterialTheme.typography.bodyMedium) },
        placeholder = placeholder?.let {
            { Text(it, style = MaterialTheme.typography.bodyMedium, color = EnidTextSec.copy(alpha = 0.7f)) }
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = teclado),
        singleLine = lineasVisibles == 1,
        minLines = lineasVisibles,
        maxLines = if (lineasVisibles == 1) 1 else lineasVisibles,
        isError = esError,
        supportingText = textoAyuda?.let {
            {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (esError) EnidErrorTexto else EnidTextSec
                )
            }
        },
        shape = RoundedCornerShape(EnidDimens.radioCampo),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = EnidCardBg,
            unfocusedContainerColor = EnidCardBg,
            errorContainerColor = EnidCardBg,
            focusedBorderColor = EnidDarkBlue,
            unfocusedBorderColor = EnidBorderSoft,
            errorBorderColor = EnidRed,
            focusedLabelColor = EnidDarkBlue,
            unfocusedLabelColor = EnidTextSec,
            cursorColor = EnidDarkBlue
        )
    )
}

/** Etiqueta de campo + texto de ayuda, para bloques sin caja de texto. */
@Composable
fun CampoLabel(texto: String, ayuda: String? = null) {
    Column(Modifier.padding(bottom = 6.dp)) {
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = EnidInk)
        if (ayuda != null) {
            Text(ayuda, style = MaterialTheme.typography.bodySmall, color = EnidTextSec)
        }
    }
}

/** Nota informativa dentro de un formulario. */
@Composable
fun NotaFormulario(texto: String, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(EnidIconBox)
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            EnidIconos.Escudo,
            null,
            tint = EnidDarkBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall, color = EnidDarkBlue)
    }
}

/**
 * Desplegable. No hay texto libre: el usuario OBLIGATORIAMENTE elige de la
 * lista, y esa es la base de las reglas R6 y R7.
 */
@Composable
fun SelectorDesplegable(
    valor: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    esError: Boolean = false,
    mensajeError: String? = null
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = EnidDimens.altoTactilMinimo + 8.dp)
                .clip(RoundedCornerShape(EnidDimens.radioCampo))
                .background(EnidCardBg)
                .border(
                    1.5.dp,
                    if (esError) EnidRed else EnidBorderSoft,
                    RoundedCornerShape(EnidDimens.radioCampo)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    valor.ifBlank { placeholder },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (valor.isBlank()) EnidTextSec.copy(alpha = 0.7f) else EnidInk
                )
            }
            Icon(
                EnidIconos.FlechaAbajo,
                contentDescription = "Elegir una opción",
                tint = EnidTextSec,
                modifier = Modifier.size(22.dp)
            )
        }
        if (mensajeError != null) {
            Text(
                mensajeError,
                style = MaterialTheme.typography.bodySmall,
                color = EnidErrorTexto,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

/**
 * Hoja de opciones. `listaOpciones` es generica para que el mismo componente
 * sirva para tipo de documento, municipio, jornada, medio y programa.
 */
@Composable
fun <T> HojaOpciones(
    opciones: List<T>,
    etiqueta: String,
    seleccionado: T?,
    detalle: (T) -> String? = { null },
    onElegir: (T) -> Unit,
    onCerrar: () -> Unit
) {
    Surface(
        Modifier.fillMaxSize(),
        color = EnidInk.copy(alpha = 0.45f)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(EnidCardBg)
                    .padding(vertical = 18.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = EnidDimens.margenPantalla),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        etiqueta,
                        style = MaterialTheme.typography.titleMedium,
                        color = EnidInk,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        EnidIconos.Cerrar,
                        "Cerrar",
                        tint = EnidTextSec,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onCerrar)
                            .padding(14.dp)
                    )
                }
                VerticalSpacer(8)
                Column(
                    Modifier
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    opciones.forEach { opcion ->
                        val esEl = opcion == seleccionado
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = EnidDimens.altoTactilMinimo)
                                .background(if (esEl) EnidIconBox else EnidCardBg)
                                .clickable { onElegir(opcion) }
                                .padding(horizontal = EnidDimens.margenPantalla, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    opcion.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (esEl) EnidDarkBlue else EnidInk,
                                    fontWeight = if (esEl) FontWeight.Bold else FontWeight.Normal
                                )
                                val extra = detalle(opcion)
                                if (extra != null) {
                                    Text(
                                        extra,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EnidTextSec
                                    )
                                }
                            }
                            if (esEl) {
                                Icon(
                                    EnidIconos.Confirmado,
                                    null,
                                    tint = EnidDarkBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Botones excluyentes en rejilla. */
@Composable
fun <T> SelectorBotones(
    opciones: List<T>,
    seleccionado: T?,
    onElegir: (T) -> Unit,
    modifier: Modifier = Modifier,
    columnas: Int = 2
) {
    val filas = opciones.chunked(columnas)
    Column(modifier.fillMaxWidth()) {
        filas.forEach { fila ->
            Row(Modifier.fillMaxWidth()) {
                fila.forEach { opcion ->
                    val esEl = opcion == seleccionado
                    Box(
                        Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .heightIn(min = EnidDimens.altoTactilMinimo)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (esEl) EnidDarkBlue else EnidCardBg)
                            .border(
                                1.5.dp,
                                if (esEl) EnidDarkBlue else EnidBorderSoft,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onElegir(opcion) }
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            opcion.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (esEl) EnidWhite else EnidInk,
                            textAlign = TextAlign.Center,
                            fontWeight = if (esEl) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                repeat(columnas - fila.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/** Casilla de consentimiento (habeas data). */
@Composable
fun CasillaConsentimiento(
    marcado: Boolean,
    onCambiar: (Boolean) -> Unit,
    texto: String,
    modifier: Modifier = Modifier,
    esError: Boolean = false
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (esError) EnidErrorFondo.copy(alpha = 0.5f) else EnidIconBox)
            .clickable { onCambiar(!marcado) }
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (marcado) EnidDarkBlue else EnidCardBg)
                .border(2.dp, if (marcado) EnidDarkBlue else EnidBorderSoft, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (marcado) {
                Icon(
                    EnidIconos.Confirmado,
                    null,
                    tint = EnidWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            color = EnidInk,
            modifier = Modifier.weight(1f)
        )
    }
}

// ============================================================== CONTADORES ===

/**
 * Contador del resumen. El taller lo pide con `headlineMedium Bold 26/32`:
 * la cifra tiene que leerse a un metro de distancia, en una plaza, con el sol
 * de cara. Por eso el numero domina y el titulo es discreto.
 */
@Composable
fun ContadorCard(
    titulo: String,
    cifra: Int,
    color: Color,
    modifier: Modifier = Modifier,
    pie: String? = null
) {
    Column(
        modifier
            .clip(RoundedCornerShape(EnidDimens.radioTarjeta))
            .background(EnidCardBg)
            .border(1.5.dp, EnidBorderSoft, RoundedCornerShape(EnidDimens.radioTarjeta))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            cifra.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            titulo.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = EnidTextSec,
            textAlign = TextAlign.Center
        )
        if (pie != null) {
            Text(
                pie,
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec,
                textAlign = TextAlign.Center
            )
        }
    }
}

// =============================================================== EVIDENCIA ===

/**
 * FOTOGRAFIA DE EVIDENCIA
 * ---------------------------------------------------------------------------
 * DECISION DE DISENO: se usa `BitmapFactory` directo y NO se agrega Coil ni
 * Glide. La app solo muestra JPEG de 1024 px que ella misma copio en su carpeta
 * interna, y necesita UN thumbnails en la tarjeta del listado. Una libreria de
 * carga de imagenes resuelve problemas de cache y de listas largas que aqui no
 * existen, y costaria otra dependencia y otro sistema de archivos de cache.
 *
 * `muestraEnMemoria` limita el bitmap a 160 px de lado: mostrar una foto de
 * 1024 px escalada dentro de una tarjeta de 80 dp desperdicia 40 veces la
 * memoria por elemento.
 */
@Composable
fun FotoEvidencia(
    ruta: String?,
    modifier: Modifier = Modifier,
    lado: androidx.compose.ui.unit.Dp = 84.dp,
    esError: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val bitmap = rememberMiniatura(ruta)
    val escala = Modifier
        .size(lado)
        .clip(RoundedCornerShape(10.dp))

    Column(modifier) {
        Box(
            escala.then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ).background(EnidFotoPlaceholder),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Evidencia fotográfica del documento",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        EnidIconos.Documento,
                        null,
                        tint = EnidDarkBlue.copy(alpha = 0.45f),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Sin foto",
                        style = MaterialTheme.typography.labelMedium,
                        color = EnidDarkBlue.copy(alpha = 0.6f)
                    )
                }
            }
            if (esError) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(EnidErrorFondo.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        EnidIconos.Alerta,
                        "Evidencia no disponible",
                        tint = EnidErrorTexto,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

/** Vista grande de la evidencia, en el detalle. */
@Composable
fun FotoEvidenciaAmpliada(ruta: String?, modifier: Modifier = Modifier) {
    val bitmap = rememberImagenGrande(ruta)
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 320.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(EnidFotoPlaceholder),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Fotografía del documento de identidad",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    EnidIconos.Alerta,
                    null,
                    tint = EnidErrorTexto,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Evidencia no disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidErrorTexto
                )
            }
        }
    }
}

@Composable
private fun rememberMiniatura(ruta: String?): ImageBitmap? =
    androidx.compose.runtime.remember(ruta) { leerBitmap(ruta, 160) }

@Composable
private fun rememberImagenGrande(ruta: String?): ImageBitmap? =
    androidx.compose.runtime.remember(ruta) { leerBitmap(ruta, 1024) }

/**
 * Lee un JPEG del disco con downsample. Si el archivo no existe devuelve null y
 * la UI muestra el estado de evidencia ausente, que es el caso que el taller
 * pide reflejar y no un crash.
 */
private fun leerBitmap(ruta: String?, ladoMaximo: Int): ImageBitmap? {
    if (ruta.isNullOrBlank()) return null
    val archivo = File(ruta)
    if (!archivo.exists() || archivo.length() == 0L) return null
    return runCatching {
        val limites = android.graphics.BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        android.graphics.BitmapFactory.decodeFile(ruta, limites)
        var muestreo = 1
        while (limites.outWidth / muestreo > ladoMaximo * 2) muestreo *= 2
        val opciones = android.graphics.BitmapFactory.Options().apply {
            inSampleSize = muestreo
            inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
        }
        android.graphics.BitmapFactory.decodeFile(ruta, opciones)?.asImageBitmap()
    }.getOrNull()
}

/** Logo de la fundacion, reutilizado de la app institucional. */
@Composable
fun LogoEnid(modifier: Modifier = Modifier, alto: androidx.compose.ui.unit.Dp = 40.dp) {
    Image(
        painter = painterResource(R.drawable.logo_enid),
        contentDescription = "Fundación Educativa ENID",
        modifier = modifier.height(alto)
    )
}

// ================================================================ PROGRESO ===

/** Barra de progreso de la sincronizacion, con el color del estado PENDIENTE. */
@Composable
fun BarraProgresoSincronizacion(
    fraccion: Float,
    texto: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(EnidOroSoft)
            .padding(horizontal = EnidDimens.margenPantalla, vertical = 12.dp)
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            color = EnidPendienteTexto
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { fraccion },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = EnidPendiente,
            trackColor = EnidWhite
        )
    }
}

/** Par etiqueta / valor del detalle. */
@Composable
fun ParDato(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec,
            modifier = Modifier.weight(0.45f)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            valor,
            style = MaterialTheme.typography.bodyMedium,
            color = EnidInk,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.55f)
        )
    }
}

/** Estado vacio con mensaje y accion. */
@Composable
fun EstadoVacio(
    titulo: String,
    mensaje: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    accion: (@Composable () -> Unit)? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = EnidDimens.margenPantalla, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EnidIconBox(icono, tamano = 64.dp)
        Text(
            titulo,
            style = MaterialTheme.typography.titleMedium,
            color = EnidInk,
            textAlign = TextAlign.Center
        )
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec,
            textAlign = TextAlign.Center
        )
        if (accion != null) {
            Spacer(Modifier.height(6.dp))
            accion()
        }
    }
}

/** Tarjeta compacta de un registro para el listado. */
@Composable
fun TarjetaAspirante(
    registro: AspiranteEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onReintentar: (() -> Unit)? = null
) {
    InfoCard(
        modifier = modifier.clickable(onClick = onClick),
        borde = if (registro.estado == EstadoSincronizacion.ERROR) EnidErrorTexto else EnidBorderSoft
    ) {
        Row(verticalAlignment = Alignment.Top) {
            FotoEvidencia(registro.rutaEvidenciaFoto, lado = 76.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    registro.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium,
                    color = EnidInk,
                    maxLines = 2
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${registro.tipoDocumento} · ${registro.documentoIdentidad}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        EnidIconos.Calendario,
                        null,
                        tint = EnidTextSec,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        registro.fechaRegistroLegible,
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec
                    )
                    Spacer(Modifier.width(8.dp))
                    ChipNeutro(registro.idCorto())
                }
                Spacer(Modifier.height(8.dp))
                ChipEstado(registro.estado)
            }
        }

        // El motivo real del error va DEBAJO del chip, nunca escondido en un menu.
        if (registro.estado == EstadoSincronizacion.ERROR && registro.errorSincronizacion != null) {
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EnidErrorFondo)
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    EnidIconos.Alerta,
                    null,
                    tint = EnidErrorTexto,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    registro.errorSincronizacion,
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidErrorTexto,
                    modifier = Modifier.weight(1f)
                )
            }
            if (onReintentar != null) {
                Spacer(Modifier.height(10.dp))
                SmallButton(
                    texto = "Reintentar",
                    onClick = onReintentar,
                    icono = EnidIconos.Reintentar,
                    colorContenedor = EnidErrorTexto
                )
            }
        }
    }
}
