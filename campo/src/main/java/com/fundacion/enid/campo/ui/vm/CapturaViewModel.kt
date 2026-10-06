package com.fundacion.enid.campo.ui.vm

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fundacion.enid.campo.data.AspirantesRepository
import com.fundacion.enid.campo.data.EnidApp
import com.fundacion.enid.campo.data.ResultadoEdicion
import com.fundacion.enid.campo.data.ResultadoGuardado
import com.fundacion.enid.campo.data.model.CatalogoProgramas
import com.fundacion.enid.campo.data.model.JornadasCaptura
import com.fundacion.enid.campo.data.model.MediosConocimiento
import com.fundacion.enid.campo.data.model.MunicipiosCobertura
import com.fundacion.enid.campo.data.model.TiposDocumento
import com.fundacion.enid.campo.data.validation.BorradorAspirante
import com.fundacion.enid.campo.data.validation.Campo
import com.fundacion.enid.campo.data.validation.ValidadorAspirante
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Que hoja de opciones esta abierta. Una a la vez: la UI no puede quedar bloqueada. */
enum class HojaAbierta { NINGUNA, TIPO_DOCUMENTO, MUNICIPIO_RESIDENCIA, MUNICIPIO_CAPTURA, JORNADA, PROGRAMA, MEDIO }

/**
 * ESTADO DE LA PANTALLA DE CAPTURA
 * ---------------------------------------------------------------------------
 * `borrador` es la copia en memoria de lo que el asesor esta escribiendo.
 * Segun la arquitectura del taller (Fase 4, paso 1) NADA de esto se escribe en
 * disco hasta pulsar GUARDAR.
 */
data class EstadoCaptura(
    val borrador: BorradorAspirante = BorradorAspirante(),
    val errores: Map<Campo, String> = emptyMap(),
    val hojaAbierta: HojaAbierta = HojaAbierta.NINGUNA,
    val esEdicion: Boolean = false,
    val soloLectura: Boolean = false,
    val motivoSoloLectura: String? = null,
    val guardando: Boolean = false,
    val erroresDeCaptura: List<String> = emptyList()
) {
    /** El taller exige GUARDAR deshabilitado mientras falte la foto. */
    val tieneEvidencia: Boolean get() = borrador.tieneEvidencia
    val esValidoEnMemoria: Boolean get() = errores.isEmpty() && borrador.tieneEvidencia

    fun errorDe(campo: Campo): String? = errores[campo]
}

/** Eventos de salida de la pantalla. */
sealed class EventoCaptura {
    data object Guardado : EventoCaptura()
    data object Cancelado : EventoCaptura()
    data class Editado(val idRegistro: String) : EventoCaptura()
}

class CapturaViewModel(
    private val app: EnidApp,
    idRegistro: String? = null
) : ViewModel() {

    private val repositorio: AspirantesRepository = app.repositorio

    private val _estado = MutableStateFlow(EstadoCaptura())
    val estado: StateFlow<EstadoCaptura> = _estado.asStateFlow()

    private val _evento = MutableStateFlow<EventoCaptura?>(null)
    val evento: StateFlow<EventoCaptura?> = _evento.asStateFlow()

    init {
        if (!idRegistro.isNullOrBlank()) cargarParaEditar(idRegistro)
    }

    /**
     * Carga un registro PENDIENTE para edicion. Un SINCRONIZADO entra en modo
     * solo lectura con el motivo del bloqueo, que es la regla del grupo para no
     * tener informacion distinta en el telefono y en el servidor.
     */
    private fun cargarParaEditar(idRegistro: String) {
        viewModelScope.launch {
            val registro = repositorio.leerPorId(idRegistro) ?: return@launch
            _estado.value = EstadoCaptura(
                borrador = repositorio.aBorrador(registro),
                esEdicion = true,
                soloLectura = !registro.esEditable,
                motivoSoloLectura = if (!registro.esEditable) {
                    "Este registro ya fue sincronizado con Firebase. Editarlo dejaría " +
                        "información distinta en el teléfono y en el servidor, así que es de " +
                        "solo lectura."
                } else null
            )
        }
    }

    // ------------------------------------------------------------ EDITORES ---

    private fun actualizar(transformar: (BorradorAspirante) -> BorradorAspirante) {
        val actual = _estado.value
        _estado.value = actual.copy(
            borrador = transformar(actual.borrador),
            // Editar limpia el error de ese campo: el mensaje ya no es cierto.
            errores = actual.errores
        )
    }

    fun cambiarNombre(v: String) = actualizar { it.copy(nombreCompleto = v) }
    fun cambiarDocumento(v: String) = actualizar { it.copy(documentoIdentidad = v) }
    fun cambiarEdad(v: String) = actualizar { it.copy(edad = v.filter { it.isDigit() }.take(3)) }
    fun cambiarTelefono(v: String) = actualizar { it.copy(telefono = v.filter { it.isDigit() }.take(11)) }
    fun cambiarCorreo(v: String) = actualizar { it.copy(correoElectronico = v.trim()) }
    fun cambiarFechaNacimiento(v: String) = actualizar { it.copy(fechaNacimiento = v) }
    fun cambiarComoSeEntero(v: String) = actualizar { it.copy(comoSeEntero = v.take(200)) }
    fun cambiarCondiciones(v: String) = actualizar { it.copy(condicionesEconomicas = v.take(200)) }
    fun cambiarObservaciones(v: String) = actualizar { it.copy(observaciones = v.take(ValidadorAspirante.MAX_OBSERVACIONES)) }
    fun cambiarConsentimiento(v: Boolean) = actualizar { it.copy(consentimientoDatos = v) }

    // ------------------------------------------------------------- SELECTORES --

    fun abrirHoja(hoja: HojaAbierta) = _estado.update { it.copy(hojaAbierta = hoja) }
    fun cerrarHoja() = _estado.update { it.copy(hojaAbierta = HojaAbierta.NINGUNA) }

    fun elegirTipoDocumento(codigo: String) = actualizar { it.copy(tipoDocumento = codigo) }
    fun elegirMunicipioResidencia(m: String) = actualizar { it.copy(municipioResidencia = m) }
    fun elegirMunicipioCaptura(m: String) = actualizar { it.copy(municipioCaptura = m) }
    fun elegirJornada(j: String) = actualizar { it.copy(jornadaCaptura = j) }
    fun elegirPrograma(codigo: String) = actualizar { it.copy(programaInteres = codigo) }
    fun elegirMedio(medio: String) = actualizar { it.copy(medioConocimiento = medio) }

    // -------------------------------------------------------------- EVIDENCIA --

    /**
     * Se llama cuando la camara o la galeria devuelven una Uri.
     *
     * CASO EXIGIDO POR EL TALLER: "Si al momento de escoger la foto la app no la
     * puede leer, sale un mensaje para escoger otra y no deja guardar." Aqui se
     * comprueba de inmediato que el archivo es legible, sin esperar al GUARDAR.
     */
    fun recibirEvidencia(uri: Uri) {
        viewModelScope.launch {
            val legible = uriLetible(uri)
            if (!legible) {
                _estado.update {
                    it.copy(
                        errores = it.errores + (
                            Campo.EVIDENCIA_FOTOGRAFICA to
                                "No se pudo leer la imagen seleccionada. Elija otra foto."
                            ),
                        erroresDeCaptura = listOf(
                            "La imagen seleccionada no se pudo abrir. Vuelva a tomarla."
                        )
                    )
                }
                return@launch
            }
            _estado.update {
                it.copy(
                    borrador = it.borrador.copy(rutaEvidencia = uri.toString()),
                    errores = it.errores - Campo.EVIDENCIA_FOTOGRAFICA,
                    erroresDeCaptura = emptyList()
                )
            }
        }
    }

    fun limpiarEvidencia() = actualizar { it.copy(rutaEvidencia = null) }

    fun descartarCaptura() {
        app.almacen.limpiarTemporales()
        _evento.value = EventoCaptura.Cancelado
    }

    /** Limpia el evento ya atendido para no repetir la navegacion al recomponer. */
    fun limpiarEvento() {
        _evento.value = null
    }

    fun mostrarMensaje(texto: String) {
        _estado.update { it.copy(erroresDeCaptura = listOf(texto)) }
    }

    fun limpiarMensajes() {
        _estado.update { it.copy(erroresDeCaptura = emptyList()) }
    }

    // ----------------------------------------------------------------- GUARDAR --

    /**
     * GUARDAR. Este es el unico boton que escribe en disco, y escribe SOLO en
     * local: copia la evidencia en `filesDir/evidencias/` y hace un INSERT en
     * Room. NO hay ninguna llamada de red en esta funcion ni en su cadena de
     * llamadas.
     */
    fun guardar() {
        val actual = _estado.value
        if (actual.guardando || actual.soloLectura) return

        _estado.update { it.copy(guardando = true, erroresDeCaptura = emptyList()) }

        viewModelScope.launch {
            val borrador = actual.borrador

            if (borrador.esEdicion) {
                when (val r = repositorio.editar(borrador)) {
                    is ResultadoEdicion.Editado -> _evento.value = EventoCaptura.Editado(borrador.idRegistro)
                    is ResultadoEdicion.Bloqueado -> _estado.update {
                        it.copy(guardando = false, soloLectura = true, motivoSoloLectura = r.motivo)
                    }
                    is ResultadoEdicion.Error -> _estado.update {
                        it.copy(guardando = false, errores = r.errores)
                    }
                }
                return@launch
            }

            when (val r = repositorio.guardar(borrador)) {
                is ResultadoGuardado.Guardado -> {
                    // La captura temporal cumplio su papel; la copia interna manda.
                    app.almacen.limpiarTemporales()
                    _estado.update { it.copy(guardando = false) }
                    _evento.value = EventoCaptura.Guardado
                }
                is ResultadoGuardado.Rechazado -> _estado.update {
                    it.copy(guardando = false, errores = r.validacion.erroresPorCampo)
                }
            }
        }
    }

    /** Valida sin guardar: usado por el boton para decidir si pintar errores. */
    fun validarAhora() {
        val resultado = ValidadorAspirante.validar(_estado.value.borrador)
        _estado.update { it.copy(errores = resultado.erroresPorCampo) }
    }

    // --------------------------------------------------------------- HELPERS --

    /** El primer campo con error, en el orden del formulario. */
    val primerError: Campo? get() = _estado.value.errores.keys.minByOrNull { it.orden }

    /**
     * Comprueba de inmediato que la Uri elegida es REALMENTE legible. Es el caso
     * "La imagen seleccionada deja de estar accesible": aqui se detecta, antes
     * de guardar, y se pide otra foto.
     */
    private fun uriLetible(uri: Uri): Boolean = runCatching {
        when (uri.scheme) {
            "content" -> contextContentResolver()?.openInputStream(uri)?.use { it.read() >= 0 } == true
            "file" -> uri.path?.let { java.io.File(it).exists() && java.io.File(it).length() > 0 } == true
            else -> uri.path?.let { java.io.File(it).exists() } == true
        }
    }.getOrDefault(false)

    private fun contextContentResolver() = app.appContext.contentResolver

    // ---------------------------------------------------------------- OPCIONES --

    val tiposDocumento get() = TiposDocumento.todos
    val municipios get() = MunicipiosCobertura.todos
    val jornadas get() = JornadasCaptura.todas
    val medios get() = MediosConocimiento.todos
    val programas get() = CatalogoProgramas.programas

    fun etiquetaPrograma(codigo: String): String =
        CatalogoProgramas.porCodigo(codigo)?.let { "${it.codigo} · ${it.nombre}" } ?: "Elegir programa"

    fun descripcionTipoDocumento(codigo: String): String =
        TiposDocumento.porCodigo(codigo)?.descripcion ?: "Tipo de documento"
}

/** `update` sin depender de la extension de kotlinx.collections. */
private inline fun MutableStateFlow<EstadoCaptura>.update(transformar: (EstadoCaptura) -> EstadoCaptura) {
    while (true) {
        val anterior = value
        val siguiente = transformar(anterior)
        if (compareAndSet(anterior, siguiente)) return
    }
}
