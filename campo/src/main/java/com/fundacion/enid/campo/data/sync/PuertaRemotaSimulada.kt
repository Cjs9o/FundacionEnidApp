package com.fundacion.enid.campo.data.sync

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * PUERTA REMOTA SIMULADA
 * ===========================================================================
 * Implementa exactamente el mismo contrato que usara Firebase RTDB, pero en
 * memoria. Sirve para TRES cosas:
 *
 *  1. Ejecutar y demostrar TODAY todo el taller: seleccion de pendientes,
 *     envio uno a uno, confirmacion, actualizacion de estado local y manejo de
 *     fallos parciales. La logica de `SyncManager` es real, no de mentira.
 *
 *  2. Probar los casos que en un dispositivo real son imposibles de provocar a
 *     voluntad: "falla el cuarto de siete" (Prueba E) y "se cae la red a mitad
 *     del envio". Se inyecta el fallo por indice.
 *
 *  3. Servir de banco de pruebas para la regla anti-duplicados: si se invoca
 *     dos veces el envio de la MISMA clave, el mapa de escritura se sobrescribe
 *     y el conteo de llaves NO crece. Es la simulacion exacta de lo que hace
 *     RTDB cuando la llave es el UUID.
 *
 * NO es un mock descuidado: respeta la confirmacion, el timeout de 15 segundos,
 * la escritura por llave y el fallo de red. Cuando se conecte Firebase solo se
 * cambia la instancia en `EnidApp`.
 */
class PuertaRemotaSimulada : PuertaRemota {

    override val nombreDestino: String = "Almacenamiento remoto simulado"

    companion object {
        /** El taller fija 15 segundos como maximo de espera de confirmacion. */
        const val TIMEOUT_CONFIRMACION_MS = 15_000L

        /** Latencia simulada de red, para que el boton se vea trabajando. */
        const val LATENCIA_SIMULADA_MS = 260L
    }

    /**
     * Simula el arbol remoto: llave -> datos. El conteo de entradas es la
     * prueba de que una segunda sincronizacion NO duplica.
     */
    val arbolRemoto = ConcurrentHashMap<String, Map<String, Any?>>()

    private val redActiva = AtomicBoolean(true)

    /** Claves que deben fallar en el proximo envio (indice 0-based). */
    private val clavesQueFallan = mutableSetOf<String>()

    /** Simula que la conexion se cae a partir del envio numero N. */
    var indiceDeCaidaDeRed: Int = -1

    /** Numero de envios intentados; util para el resumen y las pruebas. */
    var enviosIntentados: Int = 0
        private set

    /** Permite limpiar el banco simulado entre pruebas. */
    fun reiniciar() {
        arbolRemoto.clear()
        clavesQueFallan.clear()
        indiceDeCaidaDeRed = -1
        redActiva.set(true)
        enviosIntentados = 0
    }

    /** Programa el fallo de un registro concreto (Prueba E). */
    fun programarFalloDeRegistro(idRegistro: String) {
        clavesQueFallan.add(idRegistro)
    }

    /** Simula que el dispositivo se queda sin conexion. */
    fun cortarRed() = redActiva.set(false)

    /** Simula que vuelve la conexion. */
    fun restaurarRed() = redActiva.set(true)

    override suspend fun enviar(
        claveLocal: String,
        datos: Map<String, Any?>,
        evidencia: EvidenciaPayload?,
        hayEvidencia: Boolean
    ): ResultadoEnvio {
        val indiceDeEsteEnvio = enviosIntentados
        enviosIntentados++

        // La conexion se cae a mitad del lote: se detiene el envio.
        if (indiceDeCaidaDeRed >= 0 && indiceDeEsteEnvio >= indiceDeCaidaDeRed) {
            redActiva.set(false)
            return ResultadoEnvio.FalloDeRed("Se perdió la conexión durante la sincronización.")
        }
        if (!redActiva.get()) {
            return ResultadoEnvio.FalloDeRed("Sin conexión con el servidor.")
        }

        // Fallo atribuible al propio registro: se marca para ERROR.
        if (claveLocal in clavesQueFallan) {
            clavesQueFallan.remove(claveLocal)
            return ResultadoEnvio.Fallo(
                "El servidor rechazó este registro. Revise los datos e intente de nuevo."
            )
        }

        // Sin evidencia no se confirma: es la condicion que el taller define
        // como ERROR ("falta la foto").
        if (!hayEvidencia) {
            return ResultadoEnvio.Fallo("El registro no tiene evidencia fotográfica asociada.")
        }

        // Latencia de red real, acotada por el timeout de confirmacion.
        val inicio = System.currentTimeMillis()
        delay(LATENCIA_SIMULADA_MS)
        if (System.currentTimeMillis() - inicio > TIMEOUT_CONFIRMACION_MS) {
            return ResultadoEnvio.FalloDeRed(
                "El servidor no respondió dentro de ${TIMEOUT_CONFIRMACION_MS / 1000} segundos."
            )
        }

        // Escritura por llave: la MISMA clave sobrescribe, nunca duplica.
        // Esta es la demostracion mecanica de la Prueba D.
        val escrito = datos + mapOf(
            "_evidenciaBytes" to (evidencia?.tamanoBytes ?: 0L),
            "_evidenciaNombre" to (evidencia?.nombreArchivo ?: ""),
            "_escritoEn" to ahoraIso()
        )
        arbolRemoto[claveLocal] = escrito

        return ResultadoEnvio.Confirmado
    }

    /** Cuantas llaves distintas hay en el "servidor". Si no crece al repetir, no duplica. */
    fun totalDeLlavesEnElServidor(): Int = arbolRemoto.size

    private fun ahoraIso(): String =
        SimpleDateFormat(SerializadorRtdb.PATRON_FECHA, Locale.US).format(Date())
}
