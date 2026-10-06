package com.fundacion.enid.campo.data.sync

import com.fundacion.enid.campo.data.local.AspiranteEntity

/**
 * SERIALIZACION DE UN REGISTRO PARA RTDB
 * ===========================================================================
 * Room guarda la foto como RUTA (el archivo vive en filesDir). RTDB necesita el
 * CONTENIDO. Esta clase es el unico punto donde se hace esa traduccion, para que
 * el formato remoto este documentado en un solo archivo y sea trivial de auditar
 * en la sustentacion.
 *
 * Tipos usados en RTDB:
 *   · los 14 datos del aspirante van como texto o numero, nunca como null
 *   · `consentimientoDatos` como boolean nativo
 *   · los automaticos (id, fecha de registro, fecha de sincronizacion) como texto
 *     ISO-8601 para que la consola sea legible por una persona, no solo por la app
 *   · la evidencia va aparte, en su propio nodo
 */
object SerializadorRtdb {

    /** Formato unico de fecha/hora legible en el servidor. */
    const val PATRON_FECHA = "yyyy-MM-dd'T'HH:mm:ss"

    /** Map plano `aspirantes/{idRegistro}`. */
    fun aMapaAspirante(a: AspiranteEntity, fechaSincronizacionIso: String): Map<String, Any?> =
        mapOf(
            "idRegistro" to a.idRegistro,
            "nombreCompleto" to a.nombreCompleto,
            "tipoDocumento" to a.tipoDocumento,
            "documentoIdentidad" to a.documentoIdentidad,
            "fechaNacimiento" to a.fechaNacimiento,
            "edad" to a.edad,
            "telefono" to a.telefono,
            "correoElectronico" to a.correoElectronico,
            "municipioResidencia" to a.municipioResidencia,
            "municipioCaptura" to a.municipioCaptura,
            "jornadaCaptura" to a.jornadaCaptura,
            "programaInteres" to a.programaInteres,
            "medioConocimiento" to a.medioConocimiento,
            "comoSeEntero" to a.comoSeEntero,
            "condicionesEconomicas" to a.condicionesEconomicas,
            "consentimientoDatos" to a.consentimientoDatos,
            "observaciones" to a.observaciones,
            "fechaRegistro" to a.fechaRegistroLegible,
            "fechaSincronizacion" to fechaSincronizacionIso,
            // Viaja el estado REAL del telefono. El servidor no lo reescribe por su
            // cuenta: si el paquete se pierde a medio camino, la app sigue mostrando
            // PENDIENTE y el proximo SINCRONIZAR reintenta el mismo registro.
            "estadoSincronizacion" to a.estado.almacenado,
            // El motivo del ultimo fallo y el numero de intentos viajan para que el
            // equipo del servidor pueda diagnosticar sin pedirle la pantalla al asesor.
            "errorSincronizacion" to a.errorSincronizacion,
            "intentosSincronizacion" to a.intentosSincronizacion
        )

    /**
     * Mapa de rutas para la escritura multi-ruta de RTDB.
     *
     * `aspirantes/{id}` lleva los datos y, dentro, `_indice/documentoIdentidad`.
     * Ese indice subordinado permite responder "¿este documento ya esta
     * preinscrito?" con una sola lectura, sin recorrer el arbol.
     */
    fun aMapaEscritura(
        a: AspiranteEntity,
        evidencia: EvidenciaPayload?,
        fechaSincronizacionIso: String
    ): Map<String, Any?> {
        val datos = aMapaAspirante(a, fechaSincronizacionIso).toMutableMap()
        datos["${EstructuraRtdb.NODO_INDICE}/documentoIdentidad"] = a.documentoIdentidad
        datos["${EstructuraRtdb.NODO_INDICE}/telefono"] = a.telefono
        datos["${EstructuraRtdb.NODO_INDICE}/programaInteres"] = a.programaInteres

        val mapa = mutableMapOf<String, Any?>()
        mapa.putAll(datos.mapKeys { "${EstructuraRtdb.rutaAspirante(a.idRegistro)}/${it.key}" })

        if (evidencia != null) {
            mapa["${EstructuraRtdb.rutaEvidencia(a.idRegistro)}/nombreArchivo"] = evidencia.nombreArchivo
            mapa["${EstructuraRtdb.rutaEvidencia(a.idRegistro)}/tamanoBytes"] = evidencia.tamanoBytes
            mapa["${EstructuraRtdb.rutaEvidencia(a.idRegistro)}/anchoPx"] = evidencia.anchoPx
            mapa["${EstructuraRtdb.rutaEvidencia(a.idRegistro)}/altoPx"] = evidencia.altoPx
            mapa["${EstructuraRtdb.rutaEvidencia(a.idRegistro)}/${EstructuraRtdb.CAMPO_CONTENIDO}"] =
                evidencia.contenidoBase64
        }
        return mapa
    }

    /** Nombres de los 14 datos del usuario, para la checklist de la Fase 3. */
    val DATOS_DILIGENCIADOS_POR_USUARIO = listOf(
        "nombreCompleto",
        "tipoDocumento",
        "documentoIdentidad",
        "fechaNacimiento",
        "edad",
        "telefono",
        "correoElectronico",
        "municipioResidencia",
        "municipioCaptura",
        "jornadaCaptura",
        "programaInteres",
        "medioConocimiento",
        "comoSeEntero",
        "condicionesEconomicas",
        "consentimientoDatos",
        "observaciones",
        "rutaEvidenciaFoto"
    )

    val DATOS_AUTOMATICOS = listOf(
        "idRegistro",
        "fechaRegistro",
        "fechaRegistroLegible",
        "estadoSincronizacion",
        "fechaSincronizacion",
        "errorSincronizacion",
        "intentosSincronizacion"
    )
}
