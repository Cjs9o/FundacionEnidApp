package com.fundacion.enid.campo.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fundacion.enid.campo.data.EnidApp
import com.fundacion.enid.campo.data.validation.Campo
import com.fundacion.enid.campo.ui.components.AppScaffold
import com.fundacion.enid.campo.ui.components.BigButton
import com.fundacion.enid.campo.ui.components.BigOutlinedButton
import com.fundacion.enid.campo.ui.components.BigTextField
import com.fundacion.enid.campo.ui.components.BloqueDeDatos
import com.fundacion.enid.campo.ui.components.BloqueEvidencia
import com.fundacion.enid.campo.ui.components.CasillaConsentimiento
import com.fundacion.enid.campo.ui.components.EnidIconos
import com.fundacion.enid.campo.ui.components.HojaOpciones
import com.fundacion.enid.campo.ui.components.NotaFormulario
import com.fundacion.enid.campo.ui.components.SelectorDesplegable
import com.fundacion.enid.campo.ui.components.VerticalSpacer
import com.fundacion.enid.campo.ui.vm.CapturaViewModel
import com.fundacion.enid.campo.ui.vm.EventoCaptura
import com.fundacion.enid.campo.ui.vm.HojaAbierta
import com.fundacion.enid.ui.shared.EnidBlueSoft
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidDimens
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTextSec
import java.io.File



/**
 * ===========================================================================
 * PANTALLA 2 · CAPTURA
 * ===========================================================================
 * REQUISITOS QUE CUMPLE
 * ---------------------------------------------------------------------------
 * · "debe agrupar los campos por significado, no como una lista plana"
 *      -> cuatro bloques: IDENTIFICACIÓN, CONTACTO, INTERÉS ACADÉMICO,
 *         EVIDENCIA Y CONSENTIMIENTO.
 * · "validaciones visibles"  -> cada campo pinta su error debajo y el scroll
 *         baja solo hasta el primer campo con problema.
 * · "evidencia fotográfica"  -> BloqueEvidencia con camara y galeria.
 * · "una acción clara para guardar localmente" -> boton GUARDAR fijo abajo,
 *         deshabilitado mientras falte la foto.
 *
 * NADA DE ESTA PANTALLA TOCA LA RED. Ni al abrir, ni al escribir, ni al guardar.
 */
@Composable
fun PantallaCaptura(
    vm: CapturaViewModel,
    app: EnidApp,
    onGuardado: () -> Unit,
    onEditado: () -> Unit,
    onCancelado: () -> Unit
) {
    val estado by vm.estado.collectAsState()
    val evento by vm.evento.collectAsState()
    val contexto = LocalContext.current
    val teclado = LocalSoftwareKeyboardController.current

    // Estado local SOLO para el scroll. La posicion de los campos se deriva del
    // indice de la seccion a la que pertenece cada Campo.
    val scroll = rememberScrollState()

    // ------------------------------------------------------ CAMARA Y GALERIA --
    // Se pide el permiso de CAMERA solo al pulsar el boton, no al abrir la app:
    // pedir permisos al inicio es la razon numero uno de que el usuario la
    // rechace y la cierre.
    val launcherCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        // La app de cámara devuelve RESULT_OK sin datos cuando se usó EXTRA_OUTPUT:
        // por eso leemos el archivo temporal que YA creamos, no result.data.
        val capturada = resultado.resultCode == android.app.Activity.RESULT_OK
        val archivoTemporal = app.almacen.ultimoTemporal()
        if (capturada && archivoTemporal != null && archivoTemporal.exists() && archivoTemporal.length() > 0) {
            vm.recibirEvidencia(Uri.fromFile(archivoTemporal))
        } else {
            // El usuario canceló o la cámara no escribió nada.
            app.almacen.limpiarTemporales()
            vm.limpiarMensajes()
        }
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        // GetContent no necesita permiso de almacenamiento en API 30+.
        if (uri != null) vm.recibirEvidencia(uri) else vm.limpiarMensajes()
    }

    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamara(launcherCamara, app)
        } else {
            vm.mostrarMensaje(
                "Se necesita el permiso de cámara para tomar la foto del documento. " +
                    "También puede elegirla desde la galería."
            )
        }
    }

    // --------------------------------------------------- SCROLL AL PRIMER ERROR
    // El taller: "la pantalla baja hasta el primer error". Se hace con
    // LaunchedEffect sobre el campo mas temprano con problema.
    val primerError = estado.errores.keys.minByOrNull { it.orden }
    LaunchedEffect(primerError, estado.errores.size) {
        if (primerError != null) {
            teclado?.hide()
            scroll.animateScrollTo(anchoDeCampo(primerError))
        }
    }

    // Salida de la pantalla
    LaunchedEffect(evento) {
        when (evento) {
            is EventoCaptura.Guardado -> { vm.limpiarEvento(); onGuardado() }
            is EventoCaptura.Editado -> { vm.limpiarEvento(); onEditado() }
            is EventoCaptura.Cancelado -> { vm.limpiarEvento(); onCancelado() }
            null -> Unit
        }
    }

    AppScaffold(
        titulo = if (estado.esEdicion) "Editar registro" else "Nueva preinscripción",
        onBack = vm::descartarCaptura,
        accionesCabecera = {
            if (estado.esEdicion && !estado.soloLectura) {
                ChipEdicionActiva()
            }
        },
        banner = {
            if (estado.soloLectura) BannerSoloLectura(estado.motivoSoloLectura)
        },
        abajo = {
            BarraGuardar(
                vm = vm,
                estado = estado,
                habilitado = !estado.soloLectura
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = EnidDimens.margenPantalla)
        ) {
            VerticalSpacer(16)

            if (estado.soloLectura) {
                NotaFormulario(
                    estado.motivoSoloLectura ?: "Registro sincronizado: solo lectura."
                )
                VerticalSpacer(16)
            }

            // ============ BLOQUE 1 · IDENTIFICACIÓN ==========================
            BloqueDeDatos(
                titulo = "1 · Identificación del aspirante",
                subtitulo = "Datos que aparecen en el documento"
            ) {
                BigTextField(
                    valor = estado.borrador.nombreCompleto,
                    onValueChange = vm::cambiarNombre,
                    etiqueta = "Nombre completo *",
                    placeholder = "Ej.: María Fernanda López",
                    esError = estado.errores.containsKey(Campo.NOMBRE_COMPLETO),
                    mensajeError = estado.errorDe(Campo.NOMBRE_COMPLETO),
                    ayuda = "Nombre y apellidos tal como aparecen en el documento"
                )

                VerticalSpacer(12)

                SelectorDesplegable(
                    valor = vm.descripcionTipoDocumento(estado.borrador.tipoDocumento),
                    placeholder = "Elegir tipo de documento",
                    onClick = { vm.abrirHoja(HojaAbierta.TIPO_DOCUMENTO) },
                    esError = estado.errores.containsKey(Campo.TIPO_DOCUMENTO),
                    mensajeError = estado.errorDe(Campo.TIPO_DOCUMENTO)
                )

                VerticalSpacer(12)

                BigTextField(
                    valor = estado.borrador.documentoIdentidad,
                    onValueChange = vm::cambiarDocumento,
                    etiqueta = "Número de documento *",
                    placeholder = "10 dígitos",
                    teclado = KeyboardType.Number,
                    soloDigitos = true,
                    maxCaracteres = 10,
                    esError = estado.errores.containsKey(Campo.DOCUMENTO_IDENTIDAD),
                    mensajeError = estado.errorDe(Campo.DOCUMENTO_IDENTIDAD),
                    ayuda = "Regla R4: exactamente 10 dígitos numéricos"
                )

                VerticalSpacer(12)

                BigTextField(
                    valor = estado.borrador.fechaNacimiento,
                    onValueChange = vm::cambiarFechaNacimiento,
                    etiqueta = "Fecha de nacimiento *",
                    placeholder = "dd/mm/aaaa",
                    teclado = KeyboardType.Number,
                    maxCaracteres = 10,
                    esError = estado.errores.containsKey(Campo.FECHA_NACIMIENTO),
                    mensajeError = estado.errorDe(Campo.FECHA_NACIMIENTO),
                    ayuda = "Debe coincidir con la edad escrita abajo"
                )

                VerticalSpacer(12)

                BigTextField(
                    valor = estado.borrador.edad,
                    onValueChange = vm::cambiarEdad,
                    etiqueta = "Edad *",
                    placeholder = "Años",
                    teclado = KeyboardType.Number,
                    soloDigitos = true,
                    maxCaracteres = 3,
                    esError = estado.errores.containsKey(Campo.EDAD),
                    mensajeError = estado.errorDe(Campo.EDAD),
                    ayuda = "Regla R3: entre 15 y 100 años"
                )
            }

            VerticalSpacer(20)

            // ============ BLOQUE 2 · CONTACTO ================================
            BloqueDeDatos(
                titulo = "2 · Contacto",
                subtitulo = "Para llamar y enviar información de matrícula"
            ) {
                BigTextField(
                    valor = estado.borrador.telefono,
                    onValueChange = vm::cambiarTelefono,
                    etiqueta = "Celular *",
                    placeholder = "3001234567",
                    teclado = KeyboardType.Phone,
                    soloDigitos = true,
                    maxCaracteres = 11,
                    esError = estado.errores.containsKey(Campo.TELEFONO),
                    mensajeError = estado.errorDe(Campo.TELEFONO),
                    ayuda = "Regla R1: 10 dígitos empezando en 3"
                )

                VerticalSpacer(12)

                BigTextField(
                    valor = estado.borrador.correoElectronico,
                    onValueChange = vm::cambiarCorreo,
                    etiqueta = "Correo electrónico *",
                    placeholder = "nombre@correo.com",
                    teclado = KeyboardType.Email,
                    esError = estado.errores.containsKey(Campo.CORREO_ELECTRONICO),
                    mensajeError = estado.errorDe(Campo.CORREO_ELECTRONICO),
                    ayuda = "Regla R2: formato de correo válido"
                )

                VerticalSpacer(12)

                SelectorDesplegable(
                    valor = estado.borrador.municipioResidencia,
                    placeholder = "Elegir municipio de residencia",
                    onClick = { vm.abrirHoja(HojaAbierta.MUNICIPIO_RESIDENCIA) },
                    esError = estado.errores.containsKey(Campo.MUNICIPIO_RESIDENCIA),
                    mensajeError = estado.errorDe(Campo.MUNICIPIO_RESIDENCIA)
                )

                VerticalSpacer(8)

                SelectorDesplegable(
                    valor = estado.borrador.municipioCaptura,
                    placeholder = "¿Dónde se hizo la brigada?",
                    onClick = { vm.abrirHoja(HojaAbierta.MUNICIPIO_CAPTURA) },
                    esError = estado.errores.containsKey(Campo.MUNICIPIO_CAPTURA),
                    mensajeError = estado.errorDe(Campo.MUNICIPIO_CAPTURA)
                )
            }

            VerticalSpacer(20)

            // ============ BLOQUE 3 · INTERÉS ACADÉMICO ======================
            BloqueDeDatos(
                titulo = "3 · Interés académico",
                subtitulo = "Define a qué programa se postula"
            ) {
                SelectorDesplegable(
                    valor = vm.etiquetaPrograma(estado.borrador.programaInteres),
                    placeholder = "Elegir programa de interés",
                    onClick = { vm.abrirHoja(HojaAbierta.PROGRAMA) },
                    esError = estado.errores.containsKey(Campo.PROGRAMA_INTERES),
                    mensajeError = estado.errorDe(Campo.PROGRAMA_INTERES)
                )

                VerticalSpacer(12)

                SelectorDesplegable(
                    valor = estado.borrador.jornadaCaptura,
                    placeholder = "Elegir jornada de la brigada",
                    onClick = { vm.abrirHoja(HojaAbierta.JORNADA) },
                    esError = estado.errores.containsKey(Campo.JORNADA_CAPTURA),
                    mensajeError = estado.errorDe(Campo.JORNADA_CAPTURA)
                )

                VerticalSpacer(12)

                SelectorDesplegable(
                    valor = estado.borrador.medioConocimiento,
                    placeholder = "¿Cómo conoció la Fundación?",
                    onClick = { vm.abrirHoja(HojaAbierta.MEDIO) },
                    esError = estado.errores.containsKey(Campo.MEDIO_CONOCIMIENTO),
                    mensajeError = estado.errorDe(Campo.MEDIO_CONOCIMIENTO)
                )

                // Campo condicional: solo aparece si eligió "Otro". Evita
                // preguntar algo que no aplica a la mayoría.
                if (estado.borrador.medioConocimiento == "Otro") {
                    VerticalSpacer(12)
                    BigTextField(
                        valor = estado.borrador.comoSeEntero,
                        onValueChange = vm::cambiarComoSeEntero,
                        etiqueta = "¿Cómo se enteró? *",
                        placeholder = "Cuéntanos un poco más",
                        esError = estado.errores.containsKey(Campo.COMO_SE_ENTERO),
                        mensajeError = estado.errorDe(Campo.COMO_SE_ENTERO)
                    )
                }

                VerticalSpacer(12)

                BigTextField(
                    valor = estado.borrador.condicionesEconomicas,
                    onValueChange = vm::cambiarCondiciones,
                    etiqueta = "Condiciones económicas *",
                    placeholder = "Ej.: estudiante de colegio, sin empleo, busca beca",
                    lineasVisibles = 3,
                    maxCaracteres = 200,
                    esError = estado.errores.containsKey(Campo.CONDICIONES_ECONOMICAS),
                    mensajeError = estado.errorDe(Campo.CONDICIONES_ECONOMICAS),
                    ayuda = "Sirve para priorizar los Aspirantes que necesitan beca"
                )
            }

            VerticalSpacer(20)

            // ============ BLOQUE 4 · EVIDENCIA ==============================
            BloqueDeDatos(
                titulo = "4 · Evidencia fotográfica",
                subtitulo = "Foto del documento del aspirante"
            ) {
                BloqueEvidencia(
                    rutaFoto = estado.borrador.rutaEvidencia,
                    onAbrirCamara = {
                        if (contexto.tienePermisoCamara()) {
                            abrirCamara(launcherCamara, app)
                        } else {
                            permisoCamara.launch(android.Manifest.permission.CAMERA)
                        }
                    },
                    onAbrirGaleria = { launcherGaleria.launch("image/*") },
                    mensajeError = estado.errorDe(Campo.EVIDENCIA_FOTOGRAFICA),
                    esError = estado.errores.containsKey(Campo.EVIDENCIA_FOTOGRAFICA),
                    soloLectura = estado.soloLectura
                )
            }

            VerticalSpacer(20)

            // ============ BLOQUE 5 · CONSENTIMIENTO =========================
            BloqueDeDatos(
                titulo = "5 · Consentimiento y observaciones",
                subtitulo = "Autorización y notas del asesor"
            ) {
                CasillaConsentimiento(
                    marcado = estado.borrador.consentimientoDatos,
                    onCambiar = vm::cambiarConsentimiento,
                    texto = "El Aspirante autoriza a la Fundación Educativa ENID a tratar " +
                        "sus datos personales para el proceso de admisión y matrícula.",
                    esError = estado.errores.containsKey(Campo.CONSENTIMIENTO_DATOS)
                )
                if (estado.errores.containsKey(Campo.CONSENTIMIENTO_DATOS)) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        estado.errorDe(Campo.CONSENTIMIENTO_DATOS).orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = com.fundacion.enid.campo.ui.theme.EnidErrorTexto
                    )
                }

                VerticalSpacer(12)

                BigTextField(
                    valor = estado.borrador.observaciones,
                    onValueChange = vm::cambiarObservaciones,
                    etiqueta = "Observaciones (opcional)",
                    placeholder = "Algo relevante que deba saber admisiones",
                    lineasVisibles = 4,
                    maxCaracteres = 300,
                    esError = estado.errores.containsKey(Campo.OBSERVACIONES),
                    mensajeError = estado.errorDe(Campo.OBSERVACIONES),
                    ayuda = "${estado.borrador.observaciones.length}/300 caracteres"
                )
            }

            VerticalSpacer(20)

            NotaFormulario(
                "Al pulsar GUARDAR, esta información se copia a la memoria interna de " +
                    "este teléfono. No se envía nada a internet hasta que pulses SINCRONIZAR."
            )

            VerticalSpacer(28)
        }
    }

    // ---------------- HOJAS DE OPCIONES -----------------------------------
    when (estado.hojaAbierta) {
        HojaAbierta.TIPO_DOCUMENTO -> HojaOpciones(
            opciones = vm.tiposDocumento,
            etiqueta = "Tipo de documento",
            seleccionado = vm.tiposDocumento.firstOrNull { it.codigo == estado.borrador.tipoDocumento },
            detalle = { it.descripcion },
            onElegir = { vm.elegirTipoDocumento(it.codigo); vm.cerrarHoja() },
            onCerrar = vm::cerrarHoja
        )

        HojaAbierta.MUNICIPIO_RESIDENCIA -> HojaOpciones(
            opciones = vm.municipios,
            etiqueta = "Municipio de residencia",
            seleccionado = estado.borrador.municipioResidencia.takeIf { it.isNotBlank() },
            onElegir = { vm.elegirMunicipioResidencia(it); vm.cerrarHoja() },
            onCerrar = vm::cerrarHoja
        )

        HojaAbierta.MUNICIPIO_CAPTURA -> HojaOpciones(
            opciones = vm.municipios,
            etiqueta = "Municipio de la brigada",
            seleccionado = estado.borrador.municipioCaptura.takeIf { it.isNotBlank() },
            onElegir = { vm.elegirMunicipioCaptura(it); vm.cerrarHoja() },
            onCerrar = vm::cerrarHoja
        )

        HojaAbierta.JORNADA -> HojaOpciones(
            opciones = vm.jornadas,
            etiqueta = "Jornada de la brigada",
            seleccionado = estado.borradaJornada(),
            onElegir = { vm.elegirJornada(it); vm.cerrarHoja() },
            onCerrar = vm::cerrarHoja
        )

        HojaAbierta.PROGRAMA -> HojaOpciones(
            opciones = vm.programas,
            etiqueta = "Programa de interés",
            seleccionado = vm.programas.firstOrNull { it.codigo == estado.borrador.programaInteres },
            detalle = { "${it.nivel.etiqueta} · ${it.codigo}" },
            onElegir = { vm.elegirPrograma(it.codigo); vm.cerrarHoja() },
            onCerrar = vm::cerrarHoja
        )

        HojaAbierta.MEDIO -> HojaOpciones(
            opciones = vm.medios,
            etiqueta = "¿Cómo conoció la Fundación?",
            seleccionado = estado.borrador.medioConocimiento.takeIf { it.isNotBlank() },
            onElegir = { vm.elegirMedio(it); vm.cerrarHoja() },
            onCerrar = vm::cerrarHoja
        )

        HojaAbierta.NINGUNA -> Unit
    }
}

// ============================== PIEZAS AUXILIARES =========================

/**
 * Barra inferior fija con GUARDAR. Va fija, no al final del scroll, porque en
 * un formulario largo el botón debe estar siempre alcanzable con el pulgar:
 * el taller exige "una acción clara para guardar localmente".
 */
@Composable
private fun BarraGuardar(
    vm: CapturaViewModel,
    estado: com.fundacion.enid.campo.ui.vm.EstadoCaptura,
    habilitado: Boolean
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(com.fundacion.enid.ui.shared.EnidBg)
            .padding(horizontal = EnidDimens.margenPantalla)
            .padding(top = 10.dp, bottom = 12.dp)
    ) {
        if (estado.erroresDeCaptura.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(com.fundacion.enid.campo.ui.theme.EnidErrorFondo)
                    .padding(10.dp)
            ) {
                Text(
                    estado.erroresDeCaptura.first(),
                    style = MaterialTheme.typography.bodySmall,
                    color = com.fundacion.enid.campo.ui.theme.EnidErrorTexto
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        BigButton(
            texto = if (estado.esEdicion) "GUARDAR CAMBIOS" else "GUARDAR EN ESTE TELÉFONO",
            onClick = vm::guardar,
            habilitado = habilitado && !estado.guardando,
            cargando = estado.guardando,
            icono = if (estado.esEdicion) EnidIconos.Confirmado else EnidIconos.Documento,
            colorContenedor = if (!estado.soloLectura) EnidRed else EnidBorderSoft
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = if (!estado.tieneEvidencia) {
                "Adjunte la foto del documento para poder guardar."
            } else {
                "Se guarda solo en este teléfono. No se envía nada a internet."
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (!estado.tieneEvidencia) EnidRed else EnidTextSec,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ChipEdicionActiva() {
    Row(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(EnidBlueSoft)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            "EDITANDO",
            style = MaterialTheme.typography.labelMedium,
            color = EnidDarkBlue
        )
    }
}

@Composable
private fun BannerSoloLectura(motivo: String?) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(com.fundacion.enid.ui.shared.EnidOroSoft)
            .padding(horizontal = EnidDimens.margenPantalla, vertical = 12.dp)
    ) {
        Text(
            motivo ?: "Registro sincronizado: solo lectura.",
            style = MaterialTheme.typography.bodySmall,
            color = com.fundacion.enid.campo.ui.theme.EnidPendienteTexto
        )
    }
}

/**
 * Posicion aproximada de scroll de cada campo. Se calcula a mano en vez de
 * medir el layout porque los bloques tienen alturas previsibles y asi el
 * desplazamiento ocurre en el mismo frame en que se pinta el error.
 */
private fun anchoDeCampo(campo: Campo): Int = when (campo) {
    Campo.NOMBRE_COMPLETO, Campo.TIPO_DOCUMENTO, Campo.DOCUMENTO_IDENTIDAD,
    Campo.FECHA_NACIMIENTO, Campo.EDAD -> 240
    Campo.TELEFONO, Campo.CORREO_ELECTRONICO,
    Campo.MUNICIPIO_RESIDENCIA, Campo.MUNICIPIO_CAPTURA -> 760
    Campo.JORNADA_CAPTURA, Campo.PROGRAMA_INTERES, Campo.MEDIO_CONOCIMIENTO,
    Campo.COMO_SE_ENTERO, Campo.CONDICIONES_ECONOMICAS -> 1340
    Campo.CONSENTIMIENTO_DATOS, Campo.OBSERVACIONES -> 1940
    Campo.EVIDENCIA_FOTOGRAFICA -> 2180
}

/**
 * Abre la app de cámara escribiendo en un archivo temporal PROPIO.
 *
 * Poner `EXTRA_OUTPUT` con un Uri de FileProvider es obligatorio: desde Android
 * 7 el `thumbnail` que devuelve la cámara es demasiado pequeño para leer una
 * cédula, así que hay que darle una ruta donde escribir a resolución completa.
 * Además, escribir en nuestra carpeta hace que la foto exista aunque la app de
 * cámara se cierre sola.
 */
private fun abrirCamara(
    launcher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>,
    app: EnidApp
) {
    runCatching {
        val archivo = app.almacen.archivoTemporalDeCaptura()
        val uri = app.almacen.uriParaCamara(archivo)
        val intencion = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        launcher.launch(intencion)
    }.onFailure {
        // Si el dispositivo no tiene app de cámara, el usuario puede usar la galería.
        app.almacen.limpiarTemporales()
    }
}

private fun Context.tienePermisoCamara(): Boolean =
    androidx.core.content.ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.CAMERA
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

private fun com.fundacion.enid.campo.ui.vm.EstadoCaptura.borradaJornada(): String? =
    borrador.jornadaCaptura.takeIf { it.isNotBlank() }
