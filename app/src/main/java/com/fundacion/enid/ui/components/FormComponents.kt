package com.fundacion.enid.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.FormatoEntrada
import com.fundacion.enid.model.DocumentoAdjunto
import com.fundacion.enid.model.FormatoArchivo
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidGreenSoft
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

/**
 * Piezas de formulario compartidas por la preinscripción.
 *
 * Todas siguen la misma regla: la caja crece con el contenido, la etiqueta va
 * arriba con su ayuda debajo, y el valor seleccionado se muestra centrado para
 * que ninguna fila quede descuadrada respecto a las demás.
 */

/** Etiqueta de campo con texto de ayuda opcional. */
@Composable
fun CampoLabel(texto: String, ayuda: String? = null) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = EnidInk
    )
    if (ayuda != null) {
        Text(
            text = ayuda,
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}

/**
 * Campo de solo lectura que abre una lista de opciones.
 * Se usa para el tipo de documento, el prefijo telefónico y el programa, donde
 * escribir a mano lleva a errores de formato.
 */
@Composable
fun SelectorDesplegable(
    valor: String,
    placeholder: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, if (valor.isBlank()) EnidBorderSoft else EnidRed)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = valor.ifBlank { placeholder },
                style = MaterialTheme.typography.bodyLarge,
                color = if (valor.isBlank()) EnidTextSec else EnidInk,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = EnidIcons.VExpandir,
                contentDescription = null,
                tint = EnidTextSec,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Lista desplegable con desplazamiento interno. Se limita la altura para que
 * con 67 programas (o con las 7 opciones de documento) la lista no empuje el
 * resto del formulario fuera de la pantalla.
 */
@Composable
fun <T> ListaOpciones(
    opciones: List<T>,
    etiqueta: (T) -> String,
    seleccionado: String,
    onElegir: (T) -> Unit,
    onCerrar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    opciones.forEach { opcion ->
                        val texto = etiqueta(opcion)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onElegir(opcion); onCerrar() }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = texto == seleccionado,
                                onClick = { onElegir(opcion); onCerrar() },
                                colors = RadioButtonDefaults.colors(selectedColor = EnidRed)
                            )
                            Column(modifier = Modifier.padding(start = 6.dp)) {
                                Text(
                                    text = texto,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EnidInk
                                )
                                val extra = detalle(opcion)
                                if (extra != null) {
                                    Text(
                                        text = extra,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EnidTextSec
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Segunda línea de la opción (por ejemplo, el nombre completo de una sigla). */
private fun <T> detalle(opcion: T): String? = when (opcion) {
    is com.fundacion.enid.model.TipoDocumento -> opcion.ayuda
    is com.fundacion.enid.model.PrefijoTelefono -> opcion.pais
    is com.fundacion.enid.model.NivelFormacion ->
        if (opcion.pideTitulos) "Se pedirá el título registrado" else null
    else -> null
}

/** Grupo de opciones excluyentes que se muestran como botones, no como una lista. */
@Composable
fun <T> SelectorBotones(
    opciones: List<T>,
    etiqueta: (T) -> String,
    seleccionado: String,
    columnas: Int = 2,
    onElegir: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        opciones.chunked(columnas).forEach { fila ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { opcion ->
                    val texto = etiqueta(opcion)
                    val activo = texto == seleccionado
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activo) EnidRed else EnidWhite)
                            .border(
                                width = 1.5.dp,
                                color = if (activo) EnidRed else EnidBorderSoft,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onElegir(opcion) }
                            .padding(horizontal = 10.dp, vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = texto,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
                            color = if (activo) EnidWhite else EnidInk,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                // Rellena la última fila para que los botones no queden estirados.
                repeat(columnas - fila.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/** Sí / No. Se usa para "¿es menor de edad?", "¿tiene acudiente?" y "¿registró familiar?". */
@Composable
fun PreguntaSiNo(
    valor: Boolean?,
    onCambiar: (Boolean) -> Unit
) {
    SelectorBotones(
        opciones = listOf(true, false),
        etiqueta = { if (it) "Sí" else "No" },
        seleccionado = when (valor) {
            true -> "Sí"
            false -> "No"
            null -> ""
        },
        columnas = 2,
        onElegir = onCambiar
    )
}

/**
 * Casilla de un documento requerido, con su nota y, debajo, la opción de
 * adjuntar el archivo.
 *
 * La casilla y el archivo son cosas distintas: marcar dice "puedo aportar esto",
 * adjuntar deja la copia digital en el mismo teléfono. Por eso el bloque de
 * adjunto solo aparece cuando la casilla está marcada, y no es obligatorio:
 * quien no pueda tomar la foto ahora puede terminar la preinscripción y
 * adjuntar el archivo después con el asesor.
 */
@Composable
fun CasillaDocumento(
    titulo: String,
    nota: String,
    obligatorio: Boolean,
    marcado: Boolean,
    onCambiar: (Boolean) -> Unit,
    adjunto: DocumentoAdjunto? = null,
    formatoElegido: FormatoArchivo? = null,
    onFormatoElegido: ((FormatoArchivo) -> Unit)? = null,
    onAdjuntar: ((FormatoArchivo) -> Unit)? = null,
    onQuitarAdjunto: (() -> Unit)? = null
) {
    val activo = if (marcado) EnidGreen else EnidWhite
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = activo),
        border = BorderStroke(1.5.dp, if (marcado) EnidGreen else EnidBorderSoft)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCambiar(!marcado) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = marcado, onCheckedChange = onCambiar)
                Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = EnidInk
                    )
                    Text(
                        text = nota,
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
                if (obligatorio) {
                    Text(
                        text = "Obligatorio",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EnidRed,
                        modifier = Modifier
                            .background(EnidWhite.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (marcado && onAdjuntar != null) {
                SelectorFormatoArchivo(
                    adjunto = adjunto,
                    formatoElegido = formatoElegido,
                    onFormatoElegido = onFormatoElegido,
                    onAdjuntar = { formato -> onAdjuntar(formato) },
                    onQuitar = onQuitarAdjunto
                )
            }
        }
    }
}

/**
 * Lista de formatos + botón para cargar el archivo.
 *
 * El formato se elige de una lista, igual que el tipo de documento de la cédula,
 * porque el sistema de archivos decide la extensión y una foto de la galería
 * puede no tenerla.
 *
 * La elección llega desde arriba ([formatoElegido]) y no se guarda aquí: el
 * bloque se destruye al cambiar de paso, y con un `remember` propio el formato
 * se perdería y el botón volvería a PDF en silencio.
 */
@Composable
private fun SelectorFormatoArchivo(
    adjunto: DocumentoAdjunto?,
    formatoElegido: FormatoArchivo?,
    onFormatoElegido: ((FormatoArchivo) -> Unit)?,
    onAdjuntar: (FormatoArchivo) -> Unit,
    onQuitar: (() -> Unit)?
) {
    // En cuanto hay archivo, manda el formato del adjunto guardado. Mientras no
    // lo haya, manda el que eligió la persona.
    val formatoActual = FormatoArchivo.desdeEtiqueta(adjunto?.formato) ?: formatoElegido
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(EnidWhite, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(
            text = "Formato del archivo",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = EnidTextSec
        )
        SelectorBotones(
            opciones = FormatoArchivo.entries.toList(),
            etiqueta = { it.etiqueta },
            seleccionado = formatoActual?.etiqueta ?: "",
            columnas = 3,
            onElegir = { formato -> onFormatoElegido?.invoke(formato) }
        )
        Spacer(Modifier.height(8.dp))
        if (adjunto == null) {
            BigOutlinedButton(
                // Sin formato elegido no se abre el selector: antes ofrecía los
                // tres tipos y podía terminar guardando un archivo distinto del
                // que decía el registro.
                text = if (formatoActual == null) "Elige un formato" else "Cargar archivo ${formatoActual.etiqueta}",
                icon = EnidIcons.VExterno,
                enabled = formatoActual != null,
                onClick = { formatoActual?.let { onAdjuntar(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = adjunto.nombreArchivo,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = EnidGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${adjunto.formato} · listo para enviar",
                        style = MaterialTheme.typography.labelSmall,
                        color = EnidTextSec
                    )
                }
                if (onQuitar != null) {
                    Text(
                        text = "Quitar",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EnidRed,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onQuitar() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

/** Aviso informativo dentro del formulario. */
@Composable
fun NotaFormulario(texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EnidGreenSoft, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = EnidInk
        )
    }
}

/**
 * Campo de fecha dd/mm/aaaa.
 *
 * No se puede usar [BigTextField] aquí. Al enmascarar, el texto que se devuelve
 * es distinto del que teclea la persona ("1403" pasa a "14/03") y el cursor se
 * corre al inicio; el siguiente dígito se inserta en medio y la fecha termina
 * desordenada ("14/31/9270" en vez de "14/03/1972"). Con [TextFieldValue] el
 * cursor se coloca siempre al final después de enmascarar, que es donde escribe
 * quien teclea una fecha de corrido.
 */
@Composable
fun CampoFecha(
    valor: String,
    onValor: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var estado by remember {
        mutableStateOf(TextFieldValue(valor, selection = TextRange(valor.length)))
    }
    // El padre puede reiniciar el formulario: si el valor externo ya no coincide,
    // se sincroniza para no dejar una fecha vieja en pantalla.
    if (estado.text != valor) {
        estado = TextFieldValue(valor, selection = TextRange(valor.length))
    }
    OutlinedTextField(
        value = estado,
        onValueChange = { nuevo ->
            val enmascarado = FormatoEntrada.enmascaraFecha(nuevo.text)
            estado = TextFieldValue(enmascarado, selection = TextRange(enmascarado.length))
            onValor(enmascarado)
        },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyLarge) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = EnidWhite,
            unfocusedContainerColor = EnidWhite,
            disabledContainerColor = EnidWhite,
            errorContainerColor = EnidWhite,
            focusedBorderColor = EnidDarkBlue,
            unfocusedBorderColor = EnidBorderSoft,
            errorBorderColor = EnidRed
        )
    )
}
