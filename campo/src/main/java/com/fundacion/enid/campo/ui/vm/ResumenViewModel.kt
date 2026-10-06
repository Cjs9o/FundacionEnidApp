package com.fundacion.enid.campo.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fundacion.enid.campo.data.EnidApp
import com.fundacion.enid.campo.data.AspirantesRepository
import com.fundacion.enid.campo.data.sync.SyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Fabrica manual de ViewModels: lee el contenedor de [EnidAppHolder].
 *
 * No se usa Hilt. La justificacion esta en `EnidApp`: con seis objetos, una
 * fabrica de treinta lineas es mas facil de defender en la sustentacion que un
 * grafo de inyeccion, y es el punto exacto que hay que tocar si en la prueba de
 * ajuste en vivo hay que cambiar una dependencia.
 */
class FabricaViewModel(private val app: EnidApp) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(ResumenViewModel::class.java) ->
            ResumenViewModel(app) as T
        modelClass.isAssignableFrom(CapturaViewModel::class.java) ->
            CapturaViewModel(app) as T
        modelClass.isAssignableFrom(ListadoViewModel::class.java) ->
            ListadoViewModel(app) as T
        modelClass.isAssignableFrom(DetalleViewModel::class.java) ->
            DetalleViewModel(app, idRegistro) as T
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }

    /** El detalle necesita el id del registro, que se pasa por la navegacion. */
    var idRegistro: String = ""
}

// ===========================================================================
// RESUMEN / INICIO
// ===========================================================================

/**
 * CONTADORES DEL RESUMEN
 * ---------------------------------------------------------------------------
 * Se agrupan en un solo Flow en vez de seis Flows sueltos: asi Compose hace una
 * sola recomposicion por cambio y no seis. NINGUNO de estos numeros esta
 * guardado en memoria: los cuatro salen de Room y cambian solos.
 */
data class Contadores(
    val total: Int = 0,
    val pendientes: Int = 0,
    val sincronizados: Int = 0,
    val conError: Int = 0,
    val ultimaSincronizacion: Long? = null
)

/** Estado completo de la pantalla de inicio. */
data class EstadoResumen(
    val contadores: Contadores = Contadores(),
    val hayConexion: Boolean = true,
    val tipoConexion: String? = null,
    val sincronizando: Boolean = false,
    val fraccionProgreso: Float = 0f,
    val registroEnCurso: String? = null,
    val totalEnLote: Int = 0,
    val procesadosEnLote: Int = 0
) {
    val pendientes: Int get() = contadores.pendientes
    val total: Int get() = contadores.total
    val hayParaEnviar: Boolean get() = contadores.pendientes + contadores.conError > 0
}

class ResumenViewModel(app: EnidApp) : ViewModel() {

    private val repositorio: AspirantesRepository = app.repositorio
    private val sync: SyncManager = app.syncManager
    private val conectividad = app.conectividad

    private val contadores: StateFlow<Contadores> = combine(
        repositorio.observarTotal(),
        repositorio.observarPendientes(),
        repositorio.observarSincronizados(),
        repositorio.observarConError()
    ) { total, pendientes, sincronizados, conError ->
        Contadores(total = total, pendientes = pendientes, sincronizados = sincronizados, conError = conError)
    }.combine(repositorio.observarUltimaSincronizacion()) { c, ultima ->
        c.copy(ultimaSincronizacion = ultima)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Contadores())

    val estado: StateFlow<EstadoResumen> = combine(
        contadores,
        conectividad.observar(),
        sync.progreso,
        sync.sincronizando
    ) { c, conectado, progreso, sincronizando ->
        EstadoResumen(
            contadores = c,
            hayConexion = conectado,
            tipoConexion = if (conectado) conectividad.tipoDeConexion() else null,
            sincronizando = sincronizando,
            fraccionProgreso = progreso.fraccion,
            registroEnCurso = progreso.registroActual?.nombreCompleto,
            totalEnLote = progreso.total,
            procesadosEnLote = progreso.procesados
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoResumen())

    /** Evento de un solo uso: el motivo de la ultima sincronizacion. */
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun consumirMensaje() {
        _mensaje.value = null
    }

    /**
     * UNICA ACCION DE LA APP QUE HABLA CON EL SERVIDOR.
     * Solo se invoca al pulsar SINCRONIZAR. Ademas se auto-bloquea: si ya hay
     * una sincronizacion en curso, la llamada se ignora.
     */
    fun sincronizar() {
        if (sync.sincronizando.value) return
        viewModelScope.launch {
            val resumen = sync.sincronizar()
            _mensaje.value = resumen.motivo
        }
    }
}
