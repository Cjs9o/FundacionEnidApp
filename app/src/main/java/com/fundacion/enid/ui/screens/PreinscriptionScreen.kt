package com.fundacion.enid.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.FormatoEntrada
import com.fundacion.enid.model.DocumentoAdjunto
import com.fundacion.enid.model.DocumentoRequerido
import com.fundacion.enid.model.EstadoCivil
import com.fundacion.enid.model.Familiar
import com.fundacion.enid.model.FormatoArchivo
import com.fundacion.enid.model.NivelFormacion
import com.fundacion.enid.model.PrefijoTelefono
import com.fundacion.enid.model.Preinscripcion
import com.fundacion.enid.model.adjuntosDeDocumentosMarcados
import com.fundacion.enid.model.TipoDocumento
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BanderaPrefijo
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigTextField
import com.fundacion.enid.ui.components.CampoFecha
import com.fundacion.enid.ui.components.CampoLabel
import com.fundacion.enid.ui.components.CasillaDocumento
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.ListaOpciones
import com.fundacion.enid.ui.components.NotaFormulario
import com.fundacion.enid.ui.components.PreguntaSiNo
import com.fundacion.enid.ui.components.SelectorBotones
import com.fundacion.enid.ui.components.SelectorCompacto
import com.fundacion.enid.ui.components.SelectorDesplegable
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidGreenSoft
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

private const val TOTAL_PASOS = 3

/**
 * Preinscripción de ENID, en tres pasos.
 *
 * **Paso 1 — Quién eres.** Nombre, tipo y número de documento, fechas de
 * nacimiento y expedición, prefijo y número de contacto, correo y programa.
 *
 * **Paso 2 — Tu formación y documentos.** Nivel de formación, los documentos que
 * debes entregar (fotocopia del documento, de los títulos de bachiller y el
 * certificado del ICFES).
 *
 * **Paso 3 — Tu situación.** Estado civil (solo si eres mayor de edad), si eres
 * menor de edad o tienes una condición especial, si tienes acudiente, un
 * contacto de emergencia y, si quieres, un familiar de confianza.
 *
 * El tipo de documento y el prefijo van **separados del número** porque
 * "12345678" no dice si es una cédula, una tarjeta de identidad o un NIT, y ENID
 * los tramita distinto. Igual con el teléfono: el número sin prefijo no permite
 * devolver la llamada.
 */
@Composable
fun PreinscriptionScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    programaId: Int? = null,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    /** Nombre real del archivo escolhido, para mostrarlo en vez de un `content://`. */
    fun nombreDeArchivo(uri: Uri): String {
        val desdeColumna = context.contentResolver.query(uri, null, null, null, null)?.use { c ->
            val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (i >= 0 && c.moveToFirst()) c.getString(i) else null
        }
        return desdeColumna ?: uri.lastPathSegment?.substringAfterLast('/') ?: "archivo"
    }

    /**
     * Suelta el permiso de lectura de un archivo que ya no se referencia.
     *
     * El permiso es persistente (`takePersistableUriPermission` en el paso 2) y vive
     * hasta que se libera o hasta que se reinstala la app. Si la persona desmarca
     * la casilla o quita el archivo, dejarlo concedido es dejar acceso al
     * documento de identidad más tiempo del necesario.
     */
    fun soltarPermiso(uri: String) {
        try {
            context.contentResolver.releasePersistableUriPermission(
                Uri.parse(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            // El permiso puede no haber sido persistente (proveedor sin
            // persistencia), o el archivo ya no existir. No hay nada que soltar.
        }
    }

    // ── Paso 1: datos personales ────────────────────────────────────────
    var paso by remember { mutableStateOf(1) }
    var nombre by remember { mutableStateOf(repository.currentUser?.nombre ?: "") }
    var tipoDocumento by remember { mutableStateOf<TipoDocumento?>(null) }
    var numeroDocumento by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var fechaExpedicion by remember { mutableStateOf("") }
    var prefijo by remember { mutableStateOf(PrefijoTelefono.COL.codigo) }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf(repository.currentUser?.correo ?: "") }
    var busquedaPrograma by remember { mutableStateOf("") }
    var programaSeleccionado by remember {
        mutableStateOf(
            repository.programas.firstOrNull { it.id == programaId }?.nombre
                ?: repository.programas.firstOrNull()?.nombre
                ?: ""
        )
    }
    // El año/ciclo se elige de la lista cerrada de [DataRepository.ciclos]. La
    // cuenta no lo edita a mano: lo que se guarda aquí es lo que muestran el
    // Inicio y Perfil.
    var cicloSeleccionado by remember { mutableStateOf("") }
    var mostrarDocumento by remember { mutableStateOf(false) }
    var mostrarPrograma by remember { mutableStateOf(false) }
    var mostrarCiclo by remember { mutableStateOf(false) }

    // ── Paso 2: archivo adjunto de cada documento ──────────────────────
    // Se guarda la referencia (documento -> adjunto), no el archivo. Al elegir
    // un formato se abre el selector del sistema y se guarda lo que la persona
    // escoja. `documentoPendiente` transporta qué casilla se está adjuntando,
    // porque el lanzador devuelve solo la URI, sin decir de qué documento era.
    var documentos by remember { mutableStateOf(setOf<String>()) }
    var adjuntos by remember { mutableStateOf<Map<String, DocumentoAdjunto>>(emptyMap()) }
    // El formato elegido vive aquí, no dentro de cada tarjeta: el bloque de
    // adjunto se recompone al cambiar de paso y un `remember` en la tarjeta se
    // perdería, dejando el botón en PDF sin avisar.
    var formatosElegidos by remember { mutableStateOf<Map<String, FormatoArchivo>>(emptyMap()) }
    var documentoPendiente by remember { mutableStateOf<Pair<String, FormatoArchivo>?>(null) }
    val selectorArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        val pendiente = documentoPendiente
        documentoPendiente = null
        if (uri == null || pendiente == null) return@rememberLauncherForActivityResult
        val (clave, formato) = pendiente
        // El permiso de lectura se conserva para poder volver a abrir el archivo
        // al reabrir la preinscripción; sin esto la URI deja de servir.
        try {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            // Algunos proveedores no dan permiso persistente: el archivo se
            // puede leer mientras la app esté abierta, que es lo habitual.
        }
        // Reemplazar el archivo de un documento que ya tenía uno suelta el
        // permiso del anterior: si no, quedan concedidos permisos sobre
        // documentos que la preinscripción ya no referencia.
        adjuntos[clave]?.let { viejo -> soltarPermiso(viejo.uri) }
        adjuntos = adjuntos + (clave to DocumentoAdjunto(
            documento = clave,
            formato = formato.etiqueta,
            nombreArchivo = nombreDeArchivo(uri),
            uri = uri.toString()
        ))
    }
    val pedirArchivo: (DocumentoRequerido, FormatoArchivo) -> Unit = { doc, formato ->
        documentoPendiente = doc.name to formato
        // Solo se ofrecen archivos del formato elegido, para que lo que se
        // guarda como `formato` sea siempre cierto. Con los tres MIME abiertos a
        // la vez, se podía marcar JPG y terminar guardando un PDF.
        selectorArchivo.launch(arrayOf(formato.mime))
    }
    // Desmarcar la casilla borra también el adjunto: el archivo es de ese
    // documento, y dejarlo guardado enviaría un archivo que la persona dijo no
    // poder aportar.
    fun alternarDocumento(doc: DocumentoRequerido) {
        val clave = doc.name
        if (clave in documentos) {
            documentos = documentos - clave
            adjuntos[clave]?.let { soltarPermiso(it.uri) }
            adjuntos = adjuntos - clave
            formatosElegidos = formatosElegidos - clave
        } else {
            documentos = documentos + clave
        }
    }
    fun quitarAdjunto(doc: DocumentoRequerido) {
        val clave = doc.name
        adjuntos[clave]?.let { soltarPermiso(it.uri) }
        adjuntos = adjuntos - clave
    }

    // ── Paso 2: formación y documentos ─────────────────────────────────
    var nivelFormacion by remember { mutableStateOf<NivelFormacion?>(null) }

    // ── Paso 3: situación personal ─────────────────────────────────────
    var esMenor by remember { mutableStateOf<Boolean?>(null) }
    var tieneAcudiente by remember { mutableStateOf<Boolean?>(null) }
    var nombreAcudiente by remember { mutableStateOf("") }
    var prefijoEmergencia by remember { mutableStateOf(PrefijoTelefono.COL.codigo) }
    var telefonoEmergencia by remember { mutableStateOf("") }
    var estadoCivil by remember { mutableStateOf<EstadoCivil?>(null) }
    var condicionEspecial by remember { mutableStateOf("") }
    var registraFamiliar by remember { mutableStateOf(false) }
    var familiarNombre by remember { mutableStateOf("") }
    var familiarVinculo by remember { mutableStateOf("") }
    var familiarTipoDoc by remember { mutableStateOf<TipoDocumento?>(null) }
    var familiarNumeroDoc by remember { mutableStateOf("") }
    var familiarPrefijo by remember { mutableStateOf(PrefijoTelefono.COL.codigo) }
    var familiarTelefono by remember { mutableStateOf("") }
    var familiarCorreo by remember { mutableStateOf("") }
    var mostrarFamiliarTipoDoc by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }
    var enviado by remember { mutableStateOf(false) }

    // La edad se deduce de la fecha de nacimiento para proponer el bloque de
    // menor de edad, pero la persona puede corregirlo: hay quienes tienen 17 años
    // y ya no dependen de un acudiente.
    val edadEstimada = remember(fechaNacimiento) { FormatoEntrada.edadCumplida(fechaNacimiento) }

    fun validarPaso1(): Boolean {
        error = when {
            nombre.isBlank() -> "Ingresa tu nombre."
            tipoDocumento == null -> "Selecciona el tipo de documento."
            numeroDocumento.filter { it.isDigit() }.length < 6 ->
                "El número de documento debe tener al menos 6 dígitos."
            !FormatoEntrada.esFechaValida(fechaNacimiento) -> "Escribe la fecha de nacimiento como dd/mm/aaaa."
            !FormatoEntrada.esFechaValida(fechaExpedicion) -> "Escribe la fecha de expedición como dd/mm/aaaa."
            FormatoEntrada.esAnterior(fechaExpedicion, fechaNacimiento) ->
                "La fecha de expedición no puede ser anterior a tu nacimiento."
            telefono.filter { it.isDigit() }.length < 7 -> "Ingresa un número de contacto válido."
            !FormatoEntrada.esCorreoValido(correo) -> "Ingresa un correo válido."
            programaSeleccionado.isBlank() -> "Selecciona el programa de tu interés."
            cicloSeleccionado.isBlank() -> "Selecciona el año o ciclo en el que quieres iniciar."
            else -> null
        }
        return error == null
    }

    fun validarPaso2(): Boolean {
        error = when {
            nivelFormacion == null -> "Selecciona tu nivel de formación."
            documentos.none { it == DocumentoRequerido.DOCUMENTO.name } ->
                "Confirma que puedes entregar la fotocopia de tu documento."
            documentos.none { it == DocumentoRequerido.ICFES.name } ->
                "Confirma que puedes entregar el certificado del ICFES."
            else -> null
        }
        return error == null
    }

    fun validarPaso3(): Boolean {
        error = when {
            esMenor == null -> "Indica si eres menor de edad."
            esMenor == false && estadoCivil == null ->
                "Selecciona tu estado civil."
            esMenor == true && nombreAcudiente.isBlank() ->
                "Como eres menor de edad, escribe el nombre de tu acudiente."
            esMenor == false && tieneAcudiente == true && nombreAcudiente.isBlank() ->
                "Escribe el nombre de tu acudiente."
            telefonoEmergencia.filter { it.isDigit() }.length < 7 ->
                "Ingresa un número de contacto de emergencia."
            registraFamiliar && familiarNombre.isBlank() ->
                "Escribe el nombre del familiar, o desactiva la casilla para no registrarlo."
            registraFamiliar && familiarTelefono.filter { it.isDigit() }.length < 7 ->
                "Escribe un número de contacto de tu familiar."
            else -> null
        }
        return error == null
    }

    fun enviar() {
        repository.registrarPreinscripcion(
            Preinscripcion(
                nombre = nombre.trim(),
                documento = "${tipoDocumento?.etiqueta ?: ""} $numeroDocumento".trim(),
                correo = correo.trim(),
                telefono = "$prefijo $telefono",
                programa = programaSeleccionado,
                fecha = repository.fechaActual(),
                tipoDocumento = tipoDocumento?.etiqueta ?: "",
                prefijoTelefono = prefijo,
                fechaNacimiento = fechaNacimiento,
                fechaExpedicion = fechaExpedicion,
                nivelFormacion = nivelFormacion?.etiqueta ?: "",
                documentos = documentos.toList(),
                estadoCivil = estadoCivil?.etiqueta ?: "",
                esMenorDeEdad = esMenor,
                tieneAcudiente = if (esMenor == true) true else tieneAcudiente,
                nombreAcudiente = nombreAcudiente.trim(),
                prefijoEmergencia = prefijoEmergencia,
                telefonoEmergencia = telefonoEmergencia,
                condicionEspecial = condicionEspecial.trim(),
                familiar = if (registraFamiliar) {
                    Familiar(
                        nombre = familiarNombre.trim(),
                        vinculo = familiarVinculo.trim(),
                        tipoDocumento = familiarTipoDoc?.etiqueta ?: "",
                        numeroDocumento = familiarNumeroDoc.trim(),
                        telefono = "$familiarPrefijo $familiarTelefono",
                        correo = familiarCorreo.trim()
                    )
                } else null,
                adjuntos = adjuntosDeDocumentosMarcados(adjuntos, documentos),
                anio = cicloSeleccionado
            )
        )
        enviado = true
    }

    // Un error de validación describe el formulario tal como estaba al pulsar
    // CONTINUAR. Si la persona corrige cualquier dato, ese texto deja de ser
    // cierto: se limpia solo, sin obligarla a pulsar otra vez para descubrirlo.
    // El efecto solo se reinicia cuando cambia alguna entrada; mostrar el error
    // no lo reinicia, así que el mensaje permanece hasta que se edite algo.
    LaunchedEffect(
        nombre, tipoDocumento, numeroDocumento, fechaNacimiento, fechaExpedicion,
        prefijo, telefono, correo, programaSeleccionado, cicloSeleccionado,
        nivelFormacion, documentos, esMenor, estadoCivil, tieneAcudiente,
        nombreAcudiente, telefonoEmergencia, registraFamiliar, familiarNombre,
        familiarTelefono
    ) {
        error = null
    }

    // Al pasar de un paso a otro el contenido cambia de altura, pero el
    // desplazamiento anterior se conservaba: quien avanzaba desde el final del
    // paso 1 abría el paso 2 ya bajado, con el título y el primer campo fuera
    // de pantalla. Cada paso —y la confirmación— empieza arriba.
    val estadoScroll = rememberScrollState()
    LaunchedEffect(paso, enviado) { estadoScroll.scrollTo(0) }

    AppScaffold(
        title = "Preinscripción",
        titleEnd = "Paso $paso de $TOTAL_PASOS",
        onBack = onBack,
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate,
        scrollState = estadoScroll,
    ) {
        if (enviado) {
            PantallaEnviado(
                onOtra = {
                    paso = 1; enviado = false; error = null
                    documentos = setOf(); esMenor = null; estadoCivil = null
                    // Los adjuntos tampoco sobreviven a una preinscripción nueva:
                    // si no, el segundo registro hereda los archivos del primero.
                    adjuntos.values.forEach { soltarPermiso(it.uri) }
                    adjuntos = emptyMap(); formatosElegidos = emptyMap()
                }
            )
            return@AppScaffold
        }

        IndicadorPasos(paso)

        when (paso) {
            1 -> PasoDatosPersonales(
                nombre = nombre, onNombre = { nombre = it; error = null },
                tipoDocumento = tipoDocumento, onTipoDocumento = { tipoDocumento = it },
                numeroDocumento = numeroDocumento, onNumeroDocumento = { numeroDocumento = FormatoEntrada.soloDigitos(it, FormatoEntrada.MAX_DIGITOS_DOCUMENTO) },
                fechaNacimiento = fechaNacimiento, onFechaNacimiento = { fechaNacimiento = it },
                fechaExpedicion = fechaExpedicion, onFechaExpedicion = { fechaExpedicion = it },
                edadEstimada = edadEstimada,
                prefijo = prefijo, onPrefijo = { prefijo = it },
                telefono = telefono, onTelefono = { telefono = FormatoEntrada.soloDigitos(it, FormatoEntrada.MAX_DIGITOS_TELEFONO) },
                correo = correo, onCorreo = { correo = it },
                mostrarDocumento = mostrarDocumento, onToggleDocumento = { mostrarDocumento = it },
                programa = programaSeleccionado, onPrograma = { programaSeleccionado = it },
                busquedaPrograma = busquedaPrograma, onBusquedaPrograma = { busquedaPrograma = it },
                mostrarPrograma = mostrarPrograma, onTogglePrograma = { mostrarPrograma = it },
                ciclo = cicloSeleccionado, onCiclo = { cicloSeleccionado = it },
                ciclos = repository.ciclos,
                mostrarCiclo = mostrarCiclo, onToggleCiclo = { mostrarCiclo = it },
                programas = repository
            )

            2 -> PasoFormacion(
                nivelFormacion = nivelFormacion, onNivelFormacion = { nivelFormacion = it },
                edadEstimada = edadEstimada,
                documentos = documentos, onDocumento = { d -> alternarDocumento(d) },
                adjuntos = adjuntos, formatosElegidos = formatosElegidos,
                onAdjuntar = { doc, formato -> pedirArchivo(doc, formato) },
                onFormatoElegido = { doc, formato ->
                    formatosElegidos = formatosElegidos + (doc.name to formato)
                },
                onQuitarAdjunto = { doc -> quitarAdjunto(doc) }
            )

            else -> PasoSituacion(
                esMenor = esMenor, onEsMenor = { esMenor = it },
                edadEstimada = edadEstimada,
                tieneAcudiente = tieneAcudiente, onTieneAcudiente = { tieneAcudiente = it },
                nombreAcudiente = nombreAcudiente, onNombreAcudiente = { nombreAcudiente = it },
                estadoCivil = estadoCivil, onEstadoCivil = { estadoCivil = it },
                condicionEspecial = condicionEspecial, onCondicionEspecial = { condicionEspecial = it },
                prefijoEmergencia = prefijoEmergencia, onPrefijoEmergencia = { prefijoEmergencia = it },
                telefonoEmergencia = telefonoEmergencia, onTelefonoEmergencia = { telefonoEmergencia = FormatoEntrada.soloDigitos(it, FormatoEntrada.MAX_DIGITOS_TELEFONO) },
                registraFamiliar = registraFamiliar, onRegistraFamiliar = { registraFamiliar = it },
                familiarNombre = familiarNombre, onFamiliarNombre = { familiarNombre = it },
                familiarVinculo = familiarVinculo, onFamiliarVinculo = { familiarVinculo = it },
                familiarTipoDoc = familiarTipoDoc, onFamiliarTipoDoc = { familiarTipoDoc = it },
                mostrarFamiliarTipoDoc = mostrarFamiliarTipoDoc,
                onToggleFamiliarTipoDoc = { mostrarFamiliarTipoDoc = it },
                familiarNumeroDoc = familiarNumeroDoc, onFamiliarNumeroDoc = { familiarNumeroDoc = FormatoEntrada.soloDigitos(it, FormatoEntrada.MAX_DIGITOS_DOCUMENTO) },
                familiarPrefijo = familiarPrefijo, onFamiliarPrefijo = { familiarPrefijo = it },
                familiarTelefono = familiarTelefono, onFamiliarTelefono = { familiarTelefono = FormatoEntrada.soloDigitos(it, FormatoEntrada.MAX_DIGITOS_TELEFONO) },
                familiarCorreo = familiarCorreo, onFamiliarCorreo = { familiarCorreo = it }
            )
        }
        VerticalSpacer(14)
        error?.let {
            Text(it, color = EnidRed, style = MaterialTheme.typography.bodyMedium)
            VerticalSpacer(8)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (paso > 1) {
                BigButton(
                    text = "Atrás",
                    modifier = Modifier.weight(1f),
                    containerColor = EnidWhite,
                    contentColor = EnidDarkBlue,
                    onClick = { error = null; paso = paso - 1 }
                )
            }
            BigButton(
                text = if (paso == TOTAL_PASOS) "ENVIAR PREINSCRIPCIÓN" else "CONTINUAR",
                modifier = Modifier.weight(if (paso > 1) 1.4f else 1f),
                containerColor = if (paso == TOTAL_PASOS) EnidRed else EnidDarkBlue,
                onClick = {
                    val valido = when (paso) {
                        1 -> validarPaso1()
                        2 -> validarPaso2()
                        else -> validarPaso3()
                    }
                    if (valido) {
                        error = null
                        if (paso < TOTAL_PASOS) paso = paso + 1 else enviar()
                    }
                }
            )
        }
    }
}

// ── Indicador de pasos ───────────────────────────────────────────────────────
@Composable
private fun IndicadorPasos(pasoActual: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(TOTAL_PASOS) { i ->
            val activo = i + 1 == pasoActual
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            activo -> EnidRed
                            i + 1 < pasoActual -> EnidGreenSoft
                            else -> EnidWhite
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${i + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        activo -> EnidWhite
                        i + 1 < pasoActual -> EnidGreen
                        else -> EnidTextSec
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    Text(
        text = listOf(
            "Paso 1 · Quién eres",
            "Paso 2 · Formación y documentos",
            "Paso 3 · Situación personal"
        )[pasoActual - 1],
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = EnidInk
    )
    VerticalSpacer(4)
    Text(
        text = listOf(
            "Tu nombre y tu identificación, tal como aparecen en el documento.",
            "Qué documentos debes entregar para completar el proceso.",
            "Estos datos los usa la Fundación para contactarte y acompañarte."
        )[pasoActual - 1],
        style = MaterialTheme.typography.bodyMedium,
        color = EnidTextSec
    )
    VerticalSpacer(14)
}

// ── Paso 1 ───────────────────────────────────────────────────────────────────
@Composable
private fun PasoDatosPersonales(
    nombre: String, onNombre: (String) -> Unit,
    tipoDocumento: TipoDocumento?, onTipoDocumento: (TipoDocumento) -> Unit,
    numeroDocumento: String, onNumeroDocumento: (String) -> Unit,
    fechaNacimiento: String, onFechaNacimiento: (String) -> Unit,
    fechaExpedicion: String, onFechaExpedicion: (String) -> Unit,
    edadEstimada: Int?,
    prefijo: String, onPrefijo: (String) -> Unit,
    telefono: String, onTelefono: (String) -> Unit,
    correo: String, onCorreo: (String) -> Unit,
    mostrarDocumento: Boolean, onToggleDocumento: (Boolean) -> Unit,
    programa: String, onPrograma: (String) -> Unit,
    busquedaPrograma: String, onBusquedaPrograma: (String) -> Unit,
    mostrarPrograma: Boolean, onTogglePrograma: (Boolean) -> Unit,
    ciclo: String, onCiclo: (String) -> Unit,
    ciclos: List<String>,
    mostrarCiclo: Boolean, onToggleCiclo: (Boolean) -> Unit,
    programas: DataRepository
) {
    CampoLabel("Nombre completo", "Tal como aparece en tu documento de identidad.")
    BigTextField(value = nombre, onValueChange = onNombre, label = "", placeholder = "María Elvira Rodríguez")
    VerticalSpacer(12)

    CampoLabel("Documento de identidad", "Elige la sigla y escribe solo los números.")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectorCompacto(
            valor = tipoDocumento?.etiqueta ?: "",
            placeholder = "Tipo",
            onClick = { onToggleDocumento(!mostrarDocumento) },
            ancho = 92.dp
        )
        Box(modifier = Modifier.weight(1f)) {
            BigTextField(
                value = numeroDocumento,
                onValueChange = onNumeroDocumento,
                label = "",
                placeholder = "1025123456",
                keyboardType = KeyboardType.Number
            )
        }
    }
    if (mostrarDocumento) {
        ListaOpciones(
            opciones = TipoDocumento.entries,
            etiqueta = { it.etiqueta },
            seleccionado = tipoDocumento?.etiqueta ?: "",
            onElegir = onTipoDocumento,
            onCerrar = { onToggleDocumento(false) }
        )
    }
    VerticalSpacer(12)

    CampoLabel("Fecha de nacimiento", "Formato dd/mm/aaaa.")
    CampoFecha(
        valor = fechaNacimiento,
        onValor = onFechaNacimiento,
        placeholder = "14/03/1972"
    )
    if (edadEstimada != null) {
        VerticalSpacer(6)
        NotaFormulario("Tienes $edadEstimada años según esa fecha.")
    }
    VerticalSpacer(12)

    CampoLabel("Fecha de expedición del documento", "Está impresa en la cédula.")
    CampoFecha(
        valor = fechaExpedicion,
        onValor = onFechaExpedicion,
        placeholder = "08/07/1994"
    )
    VerticalSpacer(12)

    CampoLabel(
        "Número de contacto",
        "Elige el prefijo del país y escribe solo el número."
    )
    CampoTelefono(
        prefijo = prefijo, onPrefijo = onPrefijo,
        telefono = telefono, onTelefono = onTelefono
    )
    VerticalSpacer(12)

    CampoLabel("Correo electrónico", "Ahí te enviamos la información del programa.")
    BigTextField(
        value = correo,
        onValueChange = onCorreo,
        label = "",
        placeholder = "maria.rodriguez@correo.com",
        keyboardType = KeyboardType.Email
    )
    VerticalSpacer(12)

    CampoLabel("Programa de tu interés", "Puedes cambiarlo más adelante con un asesor.")
    SelectorDesplegable(
        valor = programa,
        placeholder = "Selecciona un programa",
        onClick = { onTogglePrograma(!mostrarPrograma) }
    )
    if (mostrarPrograma) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(1.5.dp, EnidBorderSoft)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                BigTextField(
                    value = busquedaPrograma,
                    onValueChange = onBusquedaPrograma,
                    label = "",
                    placeholder = "Buscar entre los ${programas.programas.size} programas..."
                )
                VerticalSpacer(8)
                val filtrados = programas.buscarProgramas(busquedaPrograma, null, null)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        filtrados.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (programa == p.nombre) EnidGreenSoft
                                        else Color.Transparent
                                    )
                                    .clickable {
                                        onPrograma(p.nombre)
                                        onBusquedaPrograma("")
                                        onTogglePrograma(false)
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = p.nombre,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EnidInk
                                )
                            }
                        }
                        if (filtrados.isEmpty()) {
                            Text(
                                text = "No se encontraron programas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EnidTextSec,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    VerticalSpacer(12)
    CampoLabel(
        "Año o ciclo",
        "Elige el periodo en el que quieres iniciar. La Fundación confirma la fecha."
    )
    SelectorDesplegable(
        valor = ciclo,
        placeholder = "Selecciona el año o ciclo",
        onClick = { onToggleCiclo(!mostrarCiclo) }
    )
    if (mostrarCiclo) {
        ListaOpciones(
            opciones = ciclos,
            etiqueta = { it },
            seleccionado = ciclo,
            onElegir = {
                onCiclo(it)
                onToggleCiclo(false)
            },
            onCerrar = { onToggleCiclo(false) }
        )
    }
}

/**
 * Prefijo + número en un solo bloque.
 *
 * El prefijo queda a la izquierda, como selector plegable: cerrado muestra solo
 * la sigla (`+57`) para no robarle ancho al número, y al desplegarlo aparece la
 * lista con la mini bandera y el nombre del país. El campo del número lleva un
 * ejemplo como texto de fondo para que se sepa qué formato escribir.
 */
@Composable
private fun CampoTelefono(
    prefijo: String,
    onPrefijo: (String) -> Unit,
    telefono: String,
    onTelefono: (String) -> Unit
) {
    var mostrarPrefijo by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectorCompacto(
            valor = prefijo,
            placeholder = "Prefijo",
            onClick = { mostrarPrefijo = !mostrarPrefijo },
            ancho = 88.dp
        )
        Box(modifier = Modifier.weight(1f)) {
            BigTextField(
                value = telefono,
                onValueChange = onTelefono,
                label = "",
                placeholder = "300 123 4567",
                keyboardType = KeyboardType.Phone
            )
        }
    }
    if (mostrarPrefijo) {
        ListaOpciones(
            opciones = PrefijoTelefono.entries,
            etiqueta = { it.codigo },
            seleccionado = prefijo,
            onElegir = { onPrefijo(it.codigo) },
            onCerrar = { mostrarPrefijo = false },
            icono = { BanderaPrefijo(it) }
        )
    }
}

// ── Paso 2 ───────────────────────────────────────────────────────────────────
@Composable
private fun PasoFormacion(
    nivelFormacion: NivelFormacion?, onNivelFormacion: (NivelFormacion) -> Unit,
    edadEstimada: Int?,
    documentos: Set<String>, onDocumento: (DocumentoRequerido) -> Unit,
    adjuntos: Map<String, DocumentoAdjunto>,
    formatosElegidos: Map<String, FormatoArchivo>,
    onAdjuntar: (DocumentoRequerido, FormatoArchivo) -> Unit,
    onFormatoElegido: (DocumentoRequerido, FormatoArchivo) -> Unit,
    onQuitarAdjunto: (DocumentoRequerido) -> Unit
) {
    val esMenorSegunFecha = edadEstimada != null && edadEstimada < 18
    CampoLabel("Nivel de formación", "Nos sirve para saber qué documentos debes entregar.")
    SelectorBotones(
        opciones = NivelFormacion.entries,
        etiqueta = { it.etiqueta },
        seleccionado = nivelFormacion?.etiqueta ?: "",
        columnas = 1,
        onElegir = onNivelFormacion
    )
    VerticalSpacer(14)

    CampoLabel("Documentos para entregar", "Marca los que puedes aportar.")
    Text(
        text = "Al marcar cada casilla puedes adjuntar el archivo desde tu teléfono: " +
            "elige el formato y carga la foto o el PDF. El archivo se queda en tu " +
            "teléfono y no se sube a ningún servidor. Si no puedes tomarlo ahora, " +
            "marca la casilla y entrégaselo después al asesor.",
        style = MaterialTheme.typography.bodySmall,
        color = EnidTextSec,
        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
    )
    DocumentoRequerido.entries.forEach { doc ->
        val aplica = when (doc) {
            // "Técnico terminado" ya incluye bachillerato, así que no se vuelve a pedir.
            DocumentoRequerido.TITULOS -> nivelFormacion?.pideTitulos ?: true
            // Solo tiene sentido si la fecha de nacimiento indica que es menor.
            DocumentoRequerido.CEDULA_MENOR -> esMenorSegunFecha
            else -> true
        }
        if (aplica) {
            CasillaDocumento(
                titulo = doc.etiqueta,
                nota = doc.nota,
                obligatorio = doc.obligatorio,
                marcado = documentos.contains(doc.name),
                onCambiar = { onDocumento(doc) },
                adjunto = adjuntos[doc.name],
                formatoElegido = formatosElegidos[doc.name],
                onFormatoElegido = { formato -> onFormatoElegido(doc, formato) },
                onAdjuntar = { formato -> onAdjuntar(doc, formato) },
                onQuitarAdjunto = { onQuitarAdjunto(doc) }
            )
            VerticalSpacer(8)
        }
    }

    if (nivelFormacion == NivelFormacion.TECNICO) {
        NotaFormulario("Como ya terminaste técnico, el bachillerato ya hace parte de tu formación; no lo pedimos otra vez.")
        VerticalSpacer(12)
    }

}

// ── Paso 3 ───────────────────────────────────────────────────────────────────
@Composable
private fun PasoSituacion(
    esMenor: Boolean?, onEsMenor: (Boolean) -> Unit,
    edadEstimada: Int?,
    tieneAcudiente: Boolean?, onTieneAcudiente: (Boolean) -> Unit,
    nombreAcudiente: String, onNombreAcudiente: (String) -> Unit,
    estadoCivil: EstadoCivil?, onEstadoCivil: (EstadoCivil) -> Unit,
    condicionEspecial: String, onCondicionEspecial: (String) -> Unit,
    prefijoEmergencia: String, onPrefijoEmergencia: (String) -> Unit,
    telefonoEmergencia: String, onTelefonoEmergencia: (String) -> Unit,
    registraFamiliar: Boolean, onRegistraFamiliar: (Boolean) -> Unit,
    familiarNombre: String, onFamiliarNombre: (String) -> Unit,
    familiarVinculo: String, onFamiliarVinculo: (String) -> Unit,
    familiarTipoDoc: TipoDocumento?, onFamiliarTipoDoc: (TipoDocumento) -> Unit,
    mostrarFamiliarTipoDoc: Boolean, onToggleFamiliarTipoDoc: (Boolean) -> Unit,
    familiarNumeroDoc: String, onFamiliarNumeroDoc: (String) -> Unit,
    familiarPrefijo: String, onFamiliarPrefijo: (String) -> Unit,
    familiarTelefono: String, onFamiliarTelefono: (String) -> Unit,
    familiarCorreo: String, onFamiliarCorreo: (String) -> Unit
) {
    CampoLabel("¿Eres menor de edad?", "Si tienes 18 años o más, elige No.")
    PreguntaSiNo(valor = esMenor, onCambiar = onEsMenor)
    // Mientras no responda, se muestra el bloque que concuerda con la fecha de
    // nacimiento. Antes, al no haber respuesta todavía (null), caía en el
    // `else` y a una menor de 14 años le aparecía "Estado civil · Solo para
    // mayores de edad" como primera pregunta. La respuesta sigue siendo
    // obligatoria: quien tiene 17 años y ya no depende de un acudiente puede
    // corregirla, y sin responder no se deja enviar.
    val menorSegunFecha = edadEstimada?.let { it < 18 }
    val menorEfectivo = esMenor ?: menorSegunFecha
    if (esMenor == null && edadEstimada != null) {
        VerticalSpacer(6)
        NotaFormulario("Según tu fecha de nacimiento tienes $edadEstimada años.")
    }
    VerticalSpacer(14)

    if (menorEfectivo == true) {
        CampoLabel("Nombre de tu acudiente", "Quien responde por ti ante la Fundación.")
        BigTextField(
            value = nombreAcudiente,
            onValueChange = onNombreAcudiente,
            label = "",
            placeholder = "Nombre del responsable"
        )
        VerticalSpacer(14)
    } else {
        CampoLabel("Estado civil", "Solo para mayores de edad.")
        SelectorBotones(
            opciones = EstadoCivil.entries,
            etiqueta = { it.etiqueta },
            seleccionado = estadoCivil?.etiqueta ?: "",
            columnas = 3,
            onElegir = onEstadoCivil
        )
        VerticalSpacer(14)

        CampoLabel("¿Tienes alguna condición especial?", "Opcional. Déjalo vacío si no aplica.")
        BigTextField(
            value = condicionEspecial,
            onValueChange = onCondicionEspecial,
            label = "",
            placeholder = "Por ejemplo: silla de ruedas, excelente vista, dificultad para leer"
        )
        VerticalSpacer(14)

        CampoLabel("¿Tienes acudiente?", "Persona que te acompaña en el proceso.")
        PreguntaSiNo(valor = tieneAcudiente, onCambiar = onTieneAcudiente)
        if (tieneAcudiente == true) {
            VerticalSpacer(12)
            BigTextField(
                value = nombreAcudiente,
                onValueChange = onNombreAcudiente,
                label = "",
                placeholder = "Nombre del acudiente"
            )
        }
        VerticalSpacer(14)
    }

    CampoLabel("Número de contacto de emergencia", "A quién llamamos si hay una novedad.")
    CampoTelefono(
        prefijo = prefijoEmergencia, onPrefijo = onPrefijoEmergencia,
        telefono = telefonoEmergencia, onTelefono = onTelefonoEmergencia
    )
    VerticalSpacer(16)

    // ── Familiar de confianza ────────────────────────────────────────────
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, if (registraFamiliar) EnidRed else EnidBorderSoft)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Registrar un familiar de confianza",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            Text(
                text = "Opcional. La Fundación puede contactarlo si no logras responder.",
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )
            PreguntaSiNo(valor = registraFamiliar, onCambiar = onRegistraFamiliar)

            if (registraFamiliar) {
                VerticalSpacer(12)
                CampoLabel("Nombre completo del familiar")
                BigTextField(
                    value = familiarNombre,
                    onValueChange = onFamiliarNombre,
                    label = "",
                    placeholder = "Luis Fernando Gómez"
                )
                VerticalSpacer(12)

                CampoLabel("¿Qué parentesco tiene contigo?")
                BigTextField(
                    value = familiarVinculo,
                    onValueChange = onFamiliarVinculo,
                    label = "",
                    placeholder = "Hijo, hermana, esposo, vecina…"
                )
                VerticalSpacer(12)

                CampoLabel("Documento del familiar", "Elige la sigla y escribe solo los números.")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SelectorCompacto(
                        valor = familiarTipoDoc?.etiqueta ?: "",
                        placeholder = "Tipo",
                        onClick = { onToggleFamiliarTipoDoc(!mostrarFamiliarTipoDoc) },
                        ancho = 92.dp
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        BigTextField(
                            value = familiarNumeroDoc,
                            onValueChange = onFamiliarNumeroDoc,
                            label = "",
                            placeholder = "79876543",
                            keyboardType = KeyboardType.Number
                        )
                    }
                }
                if (mostrarFamiliarTipoDoc) {
                    ListaOpciones(
                        opciones = TipoDocumento.entries,
                        etiqueta = { it.etiqueta },
                        seleccionado = familiarTipoDoc?.etiqueta ?: "",
                        onElegir = onFamiliarTipoDoc,
                        onCerrar = { onToggleFamiliarTipoDoc(false) }
                    )
                }
                VerticalSpacer(12)

                CampoLabel("Número de contacto del familiar")
                CampoTelefono(
                    prefijo = familiarPrefijo, onPrefijo = onFamiliarPrefijo,
                    telefono = familiarTelefono, onTelefono = onFamiliarTelefono
                )
                VerticalSpacer(12)

                CampoLabel("Correo electrónico del familiar", "Opcional.")
                BigTextField(
                    value = familiarCorreo,
                    onValueChange = onFamiliarCorreo,
                    label = "",
                    placeholder = "correo@ejemplo.com",
                    keyboardType = KeyboardType.Email
                )
            }
        }
    }
}

// ── Confirmación ─────────────────────────────────────────────────────────────
@Composable
private fun PantallaEnviado(onOtra: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(58.dp).clip(CircleShape).background(EnidGreenSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineMedium,
                    color = EnidGreen
                )
            }
            VerticalSpacer(12)
            Text(
                text = "¡Preinscripción enviada!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidDarkBlue,
                textAlign = TextAlign.Center
            )
            VerticalSpacer(8)
            Text(
                text = "La Fundación ENID se comunicará contigo para confirmar la " +
                    "entrega de los documentos y continuar con el proceso.",
                style = MaterialTheme.typography.bodyLarge,
                color = EnidTextSec,
                textAlign = TextAlign.Center
            )
            VerticalSpacer(18)
            BigButton(text = "Enviar otra preinscripción", onClick = onOtra)
        }
    }
}

