package com.fundacion.enid.campo.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fundacion.enid.campo.data.AspirantesRepository
import com.fundacion.enid.campo.data.EnidApp
import com.fundacion.enid.campo.data.local.AspiranteEntity
import com.fundacion.enid.campo.data.model.EstadoSincronizacion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Filtros del listado local. Todo se resuelve contra Room, sin red. */
enum class FiltroListado(val etiqueta: String) {
    TODOS("Todas"),
    PENDIENTE("Pendientes"),
    SINCRONIZADO("Sincronizados"),
    ERROR("Con error");

    fun aEstado(): EstadoSincronizacion? = when (this) {
        TODOS -> null
        PENDIENTE -> EstadoSincronizacion.PENDIENTE
        SINCRONIZADO -> EstadoSincronizacion.SINCRONIZADO
        ERROR -> EstadoSincronizacion.ERROR
    }
}

data class EstadoListado(
    val filtro: FiltroListado = FiltroListado.TODOS,
    val registros: List<AspiranteEntity> = emptyList(),
    val total: Int = 0,
    val hayConexion: Boolean = true,
    val sincronizando: Boolean = false
) {
    val estaVacio: Boolean get() = registros.isEmpty()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ListadoViewModel(private val app: EnidApp) : ViewModel() {

    private val repositorio: AspirantesRepository = app.repositorio

    private val _filtro = MutableStateFlow(FiltroListado.TODOS)
    val filtro: StateFlow<FiltroListado> = _filtro.asStateFlow()

    /**
     * `flatMapLatest` sobre el filtro: al cambiar de pestaña se cambia la
     * consulta SQL, no se filtra en memoria. Es la razon por la que el listado
     * escala igual con 10 que con 5.000 registros.
     */
    private val registros: StateFlow<List<AspiranteEntity>> = _filtro
        .flatMapLatest { f -> repositorio.observarPorEstado(f.aEstado()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val estado: StateFlow<EstadoListado> = combine(
        _filtro,
        registros,
        app.conectividad.observar(),
        app.syncManager.sincronizando
    ) { filtro, lista, conectado, sincronizando ->
        EstadoListado(
            filtro = filtro,
            registros = lista,
            total = lista.size,
            hayConexion = conectado,
            sincronizando = sincronizando
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoListado())

    fun cambiarFiltro(nuevo: FiltroListado) {
        _filtro.value = nuevo
    }

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun consumirMensaje() {
        _mensaje.value = null
    }

    /**
     * "Reintentar" del chip ERROR: devuelve el registro a PENDIENTE para que
     * entre en la proxima seleccion de la sincronizacion.
     */
    fun reintentar(idRegistro: String) {
        viewModelScope.launch {
            repositorio.rearmarError(idRegistro)
            _mensaje.value = "Registro devuelto a PENDIENTE. Pulsa SINCRONIZAR para reenviarlo."
        }
    }

    fun sincronizar() {
        if (app.syncManager.sincronizando.value) return
        viewModelScope.launch {
            val resumen = app.syncManager.sincronizar()
            _mensaje.value = resumen.motivo
        }
    }
}
