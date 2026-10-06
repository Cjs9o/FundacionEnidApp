package com.fundacion.enid.campo.data.sync

/**
 * CONTRATO DE LA PUERTA REMOTA
 * ===========================================================================
 * SEPARACION LOCAL / REMOTO (Fase 4, punto clave del taller):
 * la app tiene DOS mundos separados y esta interfaz es la frontera entre ellos.
 *
 *   · MUNDO LOCAL : Room (`aspirantes`) + `filesDir/evidencias`. Room, el DAO,
 *                   el repositorio y las pantallas NUNCA conocen esta interfaz.
 *   · MUNDO REMOTO: RTDB. Solo [SyncManager] la usa, y solo cuando el usuario
 *                   pulsa SINCRONIZAR.
 *
 * Esta interfaz es la costura (seam) que permite construir, probar y sustains
 * TODO el taller sin Firebase conectado: [PuertaRemotaSimulada] implementa el
 * mismo contrato y permite ejecutar de punta a punta seleccion, envio,
 * confirmacion, actualizacion de estado y fallos parciales.
 *
 * Cuando se conecte Firebase, se implementa [PuertaRemotaFirebase] contra
 * `DatabaseReference` y se cambia una sola linea en `EnidApp`: el resto de la
 * app no se toca.
 */
interface PuertaRemota {

    /** Nombre legible del destino; se muestra en el resumen de la app. */
    val nombreDestino: String

    /**
     * Envia UN registro. La implementacion debe devolver exito solo si el
     * servidor confirmo la escritura; cualquier otra cosa es [ResultadoEnvio.Fallo].
     *
     * @param claveLocal UUID del registro. Sera la llave en RTDB.
     * @param datos mapa plano de los 18 atributos a escribir en
     *              `aspirantes/{claveLocal}`.
     * @param evidencia evidencia en Base64 a escribir en `evidencias/{claveLocal}`.
     * @param hayEvidencia si es false, la foto no se envia y el registro no debe
     *                     marcarse como enviado.
     */
    suspend fun enviar(
        claveLocal: String,
        datos: Map<String, Any?>,
        evidencia: EvidenciaPayload?,
        hayEvidencia: Boolean
    ): ResultadoEnvio
}

/**
 * Evidencia lista para enviar. Se construye en `AlmacenEvidencias` y viaja como
 * Base64 dentro de una sola escritura multi-ruta de RTDB, de modo que los datos
 * y la foto nunca quedan desincronizados en el servidor.
 */
data class EvidenciaPayload(
    val nombreArchivo: String,
    val tamanoBytes: Long,
    val anchoPx: Int,
    val altoPx: Int,
    val contenidoBase64: String
)

/**
 * Resultado de un envio individual.
 *
 * La distincion entre [Fallo] y [FalloDeRed] es la decision mas importante del
 * algoritmo de sincronizacion:
 *
 *  · [Fallo]       -> el registro tiene un problema propio. Pasa a ERROR y se sigue
 *                     con el siguiente. El motivo se muestra bajo el chip.
 *  · [FalloDeRed]  -> se cayo la conexion. El registro QUEDA PENDIENTE y el
 *                     envio se detiene, porque los siguientes tambien fallarian.
 */
sealed class ResultadoEnvio {
    /** El servidor confirmo la escritura. */
    data object Confirmado : ResultadoEnvio()

    /** Fallo atribuible al registro. El registro pasa a ERROR. */
    data class Fallo(val causa: String) : ResultadoEnvio()

    /** Se perdio la conexion. El registro queda PENDIENTE y se detiene el envio. */
    data class FalloDeRed(val causa: String) : ResultadoEnvio()
}
