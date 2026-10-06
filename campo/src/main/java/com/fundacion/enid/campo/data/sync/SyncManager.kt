package com.fundacion.enid.campo.data.sync

import android.util.Log
import com.fundacion.enid.campo.data.evidencia.AlmacenEvidencias
import com.fundacion.enid.campo.data.local.AspiranteDao
import com.fundacion.enid.campo.data.local.AspiranteEntity
import com.fundacion.enid.campo.data.red.MonitorConectividad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ===========================================================================
 * SYNC MANAGER — ALGORITMO DE SINCRONIZACION MANUAL
 * ===========================================================================
 * ESTE ES EL UNICO LUGAR DE LA APP QUE HABLA CON EL SERVIDOR.
 *
 * ALGORITMO EN LENGUAJE NATURAL (lo que el taller pide documentar antes de
 * programar; aqui esta exactamente como se implemento)
 * ---------------------------------------------------------------------------
 *  ENTRADA
 *    El usuario pulsa SINCRONIZAR. No hay ninguna otra via de entrada: la app
 *    no envia nada al abrirse, ni al guardar, ni al cambiar de pantalla.
 *
 *  1. CONDICION DE INICIO (no se toca ningun estado si falla)
 *    1.1 Si ya hay una sincronizacion en curso, el boton esta bloqueado y no se
 *        hace nada. Nunca hay dos envios simultaneos.
 *    1.2 Si NO hay conexion validada, se avisa "no hay conexion, los registros
 *        siguen guardados en el telefono" y se termina. No se llama al servidor.
 *    1.3 Si no hay registros PENDIENTE ni ERROR, se avisa y se termina.
 *
 *  2. SELECCION
 *    Se leen de Room los registros con estado PENDIENTE o ERROR, ordenados por
 *    fecha de registro ASCENDE (del mas viejo al mas nuevo).
 *
 *  3. VERIFICACION PREVIA DE LA EVIDENCIA (por item)
 *    Antes de enviar se comprueba que la copia interna de la foto exista y pese
 *    lo minimo. Si falta, el registro va directo a ERROR con el motivo y NO se
 *    intenta enviar. Esto evita gastar red en un envio que va a fallar.
 *
 *  4. ENVIO UNO POR UNO
 *    Para cada registro se escribe en una sola operacion multi-ruta:
 *      Prospective  -> aspirantes/{idRegistro}  (datos)
 *      evidencias/{idRegistro}  (foto en Base64)
 *    El timeout de confirmacion es de 15 segundos.
 *
 *  5. CONFIRMACION (transaccional por item)
 *    Solo si la escritura se confirma:
 *      estadoSincronizacion = SINCRONIZADO
 *      fechaSincronizacion   = ahora
 *      errorSincronizacion  = null
 *    La foto local se CONSERVA (decision documentada del grupo).
 *
 *  6. TRATAMIENTO DE ERRORES
 *    · Error del propio registro -> estado ERROR con la causa real bajo el chip,
 *      se CONTINUA con el siguiente.
 *    · Se cae la red -> el registro QUEDA PENDIENTE (no ERROR, porque el dato es
 *      correcto, solo falto la conexion) y se DETIENE el envio: los siguientes
 *      tambien fallarian.
 *
 *  7. FIN DE LA EJECUCION
 *    Cuando se recorren todos o se pierde la red, se publica un resumen con
 *    enviados, con error y pendientes.
 *
 *  8. PREVENCION DE DUPLICADOS
 *    · Solo se seleccionan PENDIENTE y ERROR; un SINCRONIZADO nunca se reenvia.
 *    · La llave remota es el UUID local, asi que un reenvio escribiria encima
 *      de la misma ruta, no crearia una segunda.
 *    · El boton queda bloqueado mientras dura la operacion.
 *
 * LA FOTO LOCAL NO SE BORRA NUNCA
 * El taller pide conservar la copia local mientras el registro este pendiente y
 * el grupo decidio ademas conservarla tambien tras sincronizar, para que el
 * asesor pueda revisar durante toda la jornada la evidencia que vio en campo.
 */
class SyncManager(
    private val dao: AspiranteDao,
    private val almacen: AlmacenEvidencias,
    private val conectividad: MonitorConectividad,
    private val puertaRemota: PuertaRemota
) {

    companion object {
        private const val TAG = "SyncManager"
    }

    // ------------------------------------------------------------- ESTADO UI --

    private val _sincronizando = MutableStateFlow(false)
    /** El boton SINCRONIZAR se habilita con esto: implements el bloqueo 1.1. */
    val sincronizando: StateFlow<Boolean> = _sincronizando.asStateFlow()

    private val _progreso = MutableStateFlow(ProgresoSincronizacion())
    val progreso: StateFlow<ProgresoSincronizacion> = _progreso.asStateFlow()

    private val _ultimoResumen = MutableStateFlow<ResumenSincronizacion?>(null)
    val ultimoResumen: StateFlow<ResumenSincronizacion?> = _ultimoResumen.asStateFlow()

    /**
     * Punto de entrada UNICO de todo el modulo. Lo invoca el boton SINCRONIZAR.
     *
     * El resumen lo publica `resumir()` al final, tanto en exito como en error.
     */
    suspend fun sincronizar(): ResumenSincronizacion {

        // --- 1.1 Bloqueo: no hay dos sincronizaciones simultaneas --------------
        if (_sincronizando.value) {
            val yaEsta = ResumenSincronizacion(
                titulares = false,
                enviados = 0,
                conError = 0,
                pendientes = 0,
                motivo = "Ya hay una sincronización en curso.",
                destino = puertaRemota.nombreDestino
            )
            _ultimoResumen.value = yaEsta
            return yaEsta
        }

        _sincronizando.value = true
        _progreso.value = ProgresoSincronizacion(activo = true, procesados = 0, total = 0)

        try {
            // --- 1.2 Condicion de inicio: conexion que realmente funcione -----
            if (!conectividad.hayInternet()) {
                Log.i(TAG, "Sincronizacion cancelada: no hay conexion validada")
                return resumir(
                    ResumenSincronizacion(
                        titulares = false,
                        enviados = 0,
                        conError = 0,
                        pendientes = dao.contarPorSincronizar(),
                        motivo = "No hay conexión. Los registros siguen guardados en este " +
                            "teléfono y se enviarán cuando vuelvas a tener señal.",
                        destino = puertaRemota.nombreDestino
                    )
                )
            }

            // --- 2. Seleccion de pendientes, del mas viejo al mas nuevo ------
            val lote = dao.pendientesParaSincronizar()

            // --- 1.3 Nada que enviar ----------------------------------------
            if (lote.isEmpty()) {
                return resumir(
                    ResumenSincronizacion(
                        titulares = false,
                        enviados = 0,
                        conError = 0,
                        pendientes = 0,
                        motivo = "No hay registros pendientes de enviar. Todo está sincronizado.",
                        destino = puertaRemota.nombreDestino
                    )
                )
            }

            Log.i(TAG, "Sincronizando ${lote.size} registros, uno por uno")
            _progreso.value = ProgresoSincronizacion(activo = true, procesados = 0, total = lote.size)

            var enviados = 0
            var conError = 0
            var redCaida = false

            // --- 4. Envio UNO POR UNO ----------------------------------------
            for (indice in lote.indices) {
                val registro = lote[indice]
                _progreso.value = ProgresoSincronizacion(
                    activo = true,
                    procesados = indice,
                    total = lote.size,
                    registroActual = registro
                )

                // --- 3. Verificacion previa de la evidencia ----------------
                val verificacion = almacen.verificar(registro.rutaEvidenciaFoto)
                if (!verificacion.esValida) {
                    dao.marcarError(registro.idRegistro, verificacion.detalle)
                    conError++
                    Log.w(TAG, "Evidencia ausente en ${registro.idCorto()}: ${verificacion.detalle}")
                    continue // se sigue con el siguiente
                }

                // --- Payload: datos + foto, en una sola escritura multi-ruta ---
                val evidencia = almacen
                    .prepararParaEnvio(verificacion.detalle, registro.idRegistro)
                    .getOrNull()
                if (evidencia == null) {
                    dao.marcarError(
                        registro.idRegistro,
                        "No se pudo preparar la evidencia fotográfica para enviar."
                    )
                    conError++
                    continue
                }

                val iso = ahoraIso()
                val resultado = puertaRemota.enviar(
                    claveLocal = registro.idRegistro,
                    datos = SerializadorRtdb.aMapaAspirante(registro, iso),
                    evidencia = EvidenciaPayload(
                        nombreArchivo = evidencia.nombreArchivo,
                        tamanoBytes = evidencia.tamanoBytes,
                        anchoPx = evidencia.anchoPx,
                        altoPx = evidencia.altoPx,
                        contenidoBase64 = evidencia.contenidoBase64
                    ),
                    hayEvidencia = true
                )

                when (resultado) {
                    // --- 5. Confirmacion transaccional por item ---------------
                    is ResultadoEnvio.Confirmado -> {
                        dao.marcarSincronizado(registro.idRegistro, System.currentTimeMillis())
                        enviados++
                        Log.i(TAG, "Enviado ${registro.idCorto()} (${registro.nombreCompleto})")
                    }

                    // --- 6a. Error del registro: ERROR y se sigue -------------
                    is ResultadoEnvio.Fallo -> {
                        dao.marcarError(registro.idRegistro, resultado.causa)
                        conError++
                        Log.w(TAG, "Error en ${registro.idCorto()}: ${resultado.causa}")
                    }

                    // --- 6b. Se cae la red: PENDIENTE y se DETIENE ------------
                    is ResultadoEnvio.FalloDeRed -> {
                        dao.marcarPendientePorCausaDeRed(registro.idRegistro, resultado.causa)
                        redCaida = true
                        Log.w(TAG, "Red caida en ${registro.idCorto()}: envio detenido")
                        break // 6b: los siguientes tambien fallarian
                    }
                }
            }

            // --- 7. Condicion de finalizacion + resumen ----------------------
            val restantes = dao.contarPorSincronizar()
            return resumir(
                ResumenSincronizacion(
                    titulares = true,
                    enviados = enviados,
                    conError = conError,
                    pendientes = restantes,
                    motivo = when {
                        redCaida -> "Se perdió la conexión. $enviados enviados, $restantes " +
                            "siguen pendientes y se enviarán en la próxima oportunidad."
                        conError > 0 -> "$enviados sincronizados, $conError con error y " +
                            "$restantes pendientes. Revisa los errores en el listado."
                        else -> "$enviados registros sincronizados correctamente. " +
                            "La copia local de las fotos se conserva."
                    },
                    destino = puertaRemota.nombreDestino
                )
            )

        } finally {
            _sincronizando.value = false
            _progreso.value = ProgresoSincronizacion(activo = false, procesados = 0, total = 0)
        }
    }

    /** Publica el resumen y lo devuelve para que la UI lo muestre de inmediato. */
    private fun resumir(resumen: ResumenSincronizacion): ResumenSincronizacion {
        _ultimoResumen.value = resumen
        return resumen
    }

    private fun ahoraIso(): String =
        SimpleDateFormat(SerializadorRtdb.PATRON_FECHA, Locale.US).format(Date())
}

/** Resumen que la UI muestra al terminar la sincronizacion. */
data class ResumenSincronizacion(
    val titulares: Boolean,
    val enviados: Int,
    val conError: Int,
    val pendientes: Int,
    val motivo: String,
    val destino: String
)

/** Progreso en vivo de la barra mientras se envia. */
data class ProgresoSincronizacion(
    val activo: Boolean = false,
    val procesados: Int = 0,
    val total: Int = 0,
    val registroActual: AspiranteEntity? = null
) {
    val fraccion: Float get() = if (total == 0) 0f else (procesados.toFloat() / total).coerceIn(0f, 1f)
}
