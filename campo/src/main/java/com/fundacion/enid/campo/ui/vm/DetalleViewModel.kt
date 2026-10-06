package com.fundacion.enid.campo.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fundacion.enid.campo.data.AspirantesRepository
import com.fundacion.enid.campo.data.EnidApp
import com.fundacion.enid.campo.data.evidencia.VerificacionEvidencia
import com.fundacion.enid.campo.data.local.AspiranteEntity
import com.fundacion.enid.campo.data.model.CatalogoProgramas
import com.fundacion.enid.campo.data.model.EstadoSincronizacion
import com.fundacion.enid.campo.data.model.TiposDocumento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ESTADO DEL DETALLE
 * ---------------------------------------------------------------------------
 * `evidenciaVerificada` se calcula al entrar, y es lo que permite distinguir tres
 * situaciones que el taller exige que la interfaz refleja:
 *   1. la foto esta bien
 *   2. la foto NO esta y el registro quedo PENDIENTE (aun no se nota)
 *   3. la foto NO esta y el registro quedo en ERROR por ello
 */
data class EstadoDetalle(
    val registro: AspiranteEntity? = null,
    val evidencia: VerificacionEvidencia? = null,
    val cargando: Boolean = true
) {
    val existe: Boolean get() = registro != null
    val estado: EstadoSincronizacion? get() = registro?.estado
    val editable: Boolean get() = registro?.esEditable == true

    /** Motivo del bloqueo de edicion, listo para pintar. */
    val motivoBloqueo: String? get() = if (editable) null else {
        "Este registro ya fue sincronizado con el servidor. Es de solo lectura para que " +
            "el teléfono y Firebase muestren exactamente la misma información."
    }

    val programaLegible: String get() =
        registro?.let { CatalogoProgramas.etiquetaDe(it.programaInteres) } ?: "—"

    val tipoDocumentoLegible: String get() =
        registro?.let {
            TiposDocumento.porCodigo(it.tipoDocumento)?.descripcion ?: it.tipoDocumento
        } ?: "—"
}

class DetalleViewModel(app: EnidApp, private val idRegistro: String) : ViewModel() {

    private val repositorio: AspirantesRepository = app.repositorio

    val estado: StateFlow<EstadoDetalle> = repositorio.observarPorId(idRegistro)
        .map { registro ->
            EstadoDetalle(
                registro = registro,
                evidencia = registro?.let { app.almacen.verificar(it.rutaEvidenciaFoto) },
                cargando = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoDetalle())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun consumirMensaje() {
        _mensaje.value = null
    }

    fun reintentar() {
        viewModelScope.launch {
            repositorio.rearmarError(idRegistro)
            _mensaje.value = "Registro devuelto a PENDIENTE. Pulsa SINCRONIZAR para reenviarlo."
        }
    }
}

/** Formatea el epoch millis de la ultima sincronizacion para el resumen. */
fun formatearFechaSincronizacion(millis: Long?): String {
    if (millis == null) return "Nunca"
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(Date(millis))
}
