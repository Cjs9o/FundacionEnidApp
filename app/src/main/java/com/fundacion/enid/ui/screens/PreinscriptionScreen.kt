package com.fundacion.enid.ui.screens

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
import com.fundacion.enid.model.DocumentoRequerido
import com.fundacion.enid.model.EstadoCivil
import com.fundacion.enid.model.Familiar
import com.fundacion.enid.model.NivelFormacion
import com.fundacion.enid.model.PrefijoTelefono
import com.fundacion.enid.model.Preinscripcion
import com.fundacion.enid.model.TipoDocumento
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
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
import com.fundacion.enid.ui.components.SelectorDesplegable
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.components.tabsSegunSesion
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidGreenSoft
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

private const val TOTAL_PASOS = 3

/**
 * Preinscripción de ENID, en tres pasos.
 *
 * **Paso 1 — Quién eres.** Nombre, tipo y número de documento, fechas de
 * nacimiento y expedición, prefijo y número de contacto, correo y programa.
 *
 * **Paso 2 — Tu formación y documentos.** Nivel de formación, los documentos que
 * debes entregar (fotocopia del documento, de los títulos de bachiller y el
 * certificado del ICFES) y el número del ICFES.
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
    var mostrarDocumento by remember { mutableStateOf(false) }
    var mostrarPrograma by remember { mutableStateOf(false) }

    // ── Paso 2: formación y documentos ─────────────────────────────────
    var nivelFormacion by remember { mutableStateOf<NivelFormacion?>(null) }
    var icfes by remember { mutableStateOf("") }
    var documentos by remember { mutableStateOf(setOf<String>()) }

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
            icfes.isBlank() ->
                "Escribe tu número de ICFES. Si no lo tienes a la mano, escribe NS y el asesor lo gestiona."
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
                icfes = icfes.trim(),
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
                } else null
            )
        )
        enviado = true
    }

    AppScaffold(
        title = "Preinscripción",
        titleEnd = "Paso $paso de $TOTAL_PASOS",
        onBack = onBack,
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate,
        tabs = tabsSegunSesion(repository.estaConectado)
    ) {
        if (enviado) {
            PantallaEnviado(
                onOtra = {
                    paso = 1; enviado = false; error = null
                    documentos = setOf(); esMenor = null; estadoCivil = null
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
                programas = repository
            )

            2 -> PasoFormacion(
                nivelFormacion = nivelFormacion, onNivelFormacion = { nivelFormacion = it },
                edadEstimada = edadEstimada,
                documentos = documentos, onDocumento = { d ->
                    val clave: String = d.name
                    documentos = if (documentos.contains(clave)) documentos - clave else documentos + clave
                },
                icfes = icfes, onIcfes = { icfes = it }
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

        VerticalSpacer(18)
        error?.let {
            Text(it, color = EnidRed, style = MaterialTheme.typography.bodyMedium)
            VerticalSpacer(10)
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
    VerticalSpacer(18)
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
    programas: DataRepository
) {
    CampoLabel("Nombre completo", "Tal como aparece en tu documento de identidad.")
    BigTextField(value = nombre, onValueChange = onNombre, label = "", placeholder = "María Elvira Rodríguez")
    VerticalSpacer(16)

    CampoLabel("Tipo de documento", "Elige la sigla y luego escribe el número.")
    SelectorDesplegable(
        valor = tipoDocumento?.let { "${it.etiqueta} · ${it.ayuda}" } ?: "",
        placeholder = "Selecciona CC, CE, TI, NIT…",
        onClick = { onToggleDocumento(!mostrarDocumento) }
    )
    if (mostrarDocumento) {
        ListaOpciones(
            opciones = TipoDocumento.entries,
            etiqueta = { it.etiqueta },
            seleccionado = tipoDocumento?.etiqueta ?: "",
            onElegir = onTipoDocumento,
            onCerrar = { onToggleDocumento(false) }
        )
    }
    VerticalSpacer(16)

    CampoLabel("Número de documento", "Solo los números, sin puntos ni guiones.")
    BigTextField(
        value = numeroDocumento,
        onValueChange = onNumeroDocumento,
        label = "",
        placeholder = "1025123456",
        keyboardType = KeyboardType.Number
    )
    VerticalSpacer(16)

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
    VerticalSpacer(16)

    CampoLabel("Fecha de expedición del documento", "Está impresa en la cédula.")
    CampoFecha(
        valor = fechaExpedicion,
        onValor = onFechaExpedicion,
        placeholder = "08/07/1994"
    )
    VerticalSpacer(16)

    CampoLabel(
        "Número de contacto",
        "Elige el prefijo del país y escribe solo el número."
    )
    CampoTelefono(
        prefijo = prefijo, onPrefijo = onPrefijo,
        telefono = telefono, onTelefono = onTelefono
    )
    VerticalSpacer(16)

    CampoLabel("Correo electrónico", "Ahí te enviamos la información del programa.")
    BigTextField(
        value = correo,
        onValueChange = onCorreo,
        label = "",
        placeholder = "maria.rodriguez@correo.com",
        keyboardType = KeyboardType.Email
    )
    VerticalSpacer(16)

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
}

/**
 * Prefijo + número en un solo bloque.
 *
 * El prefijo va como botón y no como desplegable porque son solo siete y se eligen
 * de una: un menú que se abre y se cierra para marcar "+57" es más lento que un
 * botón. Se muestra el país completo en el campo largo, porque "+58" solo no le
 * dice a alguien si es Venezuela o Chile.
 */
@Composable
private fun CampoTelefono(
    prefijo: String,
    onPrefijo: (String) -> Unit,
    telefono: String,
    onTelefono: (String) -> Unit
) {
    val pais = PrefijoTelefono.entries.firstOrNull { it.codigo == prefijo }?.pais
    SelectorBotones(
        opciones = PrefijoTelefono.entries,
        etiqueta = { it.codigo },
        seleccionado = prefijo,
        columnas = 4,
        onElegir = { onPrefijo(it.codigo) }
    )
    VerticalSpacer(8)
    BigTextField(
        value = telefono,
        onValueChange = onTelefono,
        label = "",
        placeholder = pais?.let { "Número · $it" } ?: "Número de contacto",
        keyboardType = KeyboardType.Phone
    )
}

// ── Paso 2 ───────────────────────────────────────────────────────────────────
@Composable
private fun PasoFormacion(
    nivelFormacion: NivelFormacion?, onNivelFormacion: (NivelFormacion) -> Unit,
    edadEstimada: Int?,
    documentos: Set<String>, onDocumento: (DocumentoRequerido) -> Unit,
    icfes: String, onIcfes: (String) -> Unit
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
    VerticalSpacer(20)

    CampoLabel("Documentos para entregar", "Marca los que puedes aportar.")
    Text(
        text = "La app no adjunta archivos: las fotocopias se entregan al asesor por el canal que él indique.",
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
                onCambiar = { onDocumento(doc) }
            )
            VerticalSpacer(8)
        }
    }

    if (nivelFormacion == NivelFormacion.TECNICO) {
        NotaFormulario("Como ya terminaste técnico, el bachillerato ya hace parte de tu formación; no lo pedimos otra vez.")
        VerticalSpacer(14)
    }

    CampoLabel("Número del ICFES", "El que aparece en tu resultado de Saber 11.")
    BigTextField(
        value = icfes,
        onValueChange = { onIcfes(it.uppercase().take(20)) },
        label = "",
        placeholder = "99052345",
        keyboardType = KeyboardType.Text
    )
    VerticalSpacer(6)
    Text(
        text = "¿No lo tienes a la mano? Escribe NS y el asesor te orienta.",
        style = MaterialTheme.typography.bodySmall,
        color = EnidTextSec
    )
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
    VerticalSpacer(20)

    if (menorEfectivo == true) {
        CampoLabel("Nombre de tu acudiente", "Quien responde por ti ante la Fundación.")
        BigTextField(
            value = nombreAcudiente,
            onValueChange = onNombreAcudiente,
            label = "",
            placeholder = "Nombre del responsable"
        )
        VerticalSpacer(20)
    } else {
        CampoLabel("Estado civil", "Solo para mayores de edad.")
        SelectorBotones(
            opciones = EstadoCivil.entries,
            etiqueta = { it.etiqueta },
            seleccionado = estadoCivil?.etiqueta ?: "",
            columnas = 3,
            onElegir = onEstadoCivil
        )
        VerticalSpacer(20)

        CampoLabel("¿Tienes alguna condición especial?", "Opcional. Déjalo vacío si no aplica.")
        BigTextField(
            value = condicionEspecial,
            onValueChange = onCondicionEspecial,
            label = "",
            placeholder = "Por ejemplo: silla de ruedas, excelente vista, dificultad para leer"
        )
        VerticalSpacer(20)

        CampoLabel("¿Tienes acudiente?", "Persona que te acompaña en el proceso.")
        PreguntaSiNo(valor = tieneAcudiente, onCambiar = onTieneAcudiente)
        if (tieneAcudiente == true) {
            VerticalSpacer(14)
            BigTextField(
                value = nombreAcudiente,
                onValueChange = onNombreAcudiente,
                label = "",
                placeholder = "Nombre del acudiente"
            )
        }
        VerticalSpacer(20)
    }

    CampoLabel("Número de contacto de emergencia", "A quién llamamos si hay una novedad.")
    CampoTelefono(
        prefijo = prefijoEmergencia, onPrefijo = onPrefijoEmergencia,
        telefono = telefonoEmergencia, onTelefono = onTelefonoEmergencia
    )
    VerticalSpacer(22)

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
                VerticalSpacer(16)
                CampoLabel("Nombre completo del familiar")
                BigTextField(
                    value = familiarNombre,
                    onValueChange = onFamiliarNombre,
                    label = "",
                    placeholder = "Luis Fernando Gómez"
                )
                VerticalSpacer(14)

                CampoLabel("¿Qué parentesco tiene contigo?")
                BigTextField(
                    value = familiarVinculo,
                    onValueChange = onFamiliarVinculo,
                    label = "",
                    placeholder = "Hijo, hermana, esposo, vecina…"
                )
                VerticalSpacer(14)

                CampoLabel("Tipo de documento del familiar")
                SelectorDesplegable(
                    valor = familiarTipoDoc?.let { "${it.etiqueta} · ${it.ayuda}" } ?: "",
                    placeholder = "Selecciona el tipo",
                    onClick = { onToggleFamiliarTipoDoc(!mostrarFamiliarTipoDoc) }
                )
                if (mostrarFamiliarTipoDoc) {
                    ListaOpciones(
                        opciones = TipoDocumento.entries,
                        etiqueta = { it.etiqueta },
                        seleccionado = familiarTipoDoc?.etiqueta ?: "",
                        onElegir = onFamiliarTipoDoc,
                        onCerrar = { onToggleFamiliarTipoDoc(false) }
                    )
                }
                VerticalSpacer(14)

                CampoLabel("Número de documento del familiar")
                BigTextField(
                    value = familiarNumeroDoc,
                    onValueChange = onFamiliarNumeroDoc,
                    label = "",
                    placeholder = "79876543",
                    keyboardType = KeyboardType.Number
                )
                VerticalSpacer(14)

                CampoLabel("Número de contacto del familiar")
                CampoTelefono(
                    prefijo = familiarPrefijo, onPrefijo = onFamiliarPrefijo,
                    telefono = familiarTelefono, onTelefono = onFamiliarTelefono
                )
                VerticalSpacer(14)

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
            VerticalSpacer(16)
            Text(
                text = "¡Preinscripción enviada!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidDarkBlue,
                textAlign = TextAlign.Center
            )
            VerticalSpacer(10)
            Text(
                text = "La Fundación ENID se comunicará contigo para confirmar la " +
                    "entrega de los documentos y continuar con el proceso.",
                style = MaterialTheme.typography.bodyLarge,
                color = EnidTextSec,
                textAlign = TextAlign.Center
            )
            VerticalSpacer(24)
            BigButton(text = "Enviar otra preinscripción", onClick = onOtra)
        }
    }
}

// ── Utilidades de entrada ────────────────────────────────────────────────────

