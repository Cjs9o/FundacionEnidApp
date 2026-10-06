package com.fundacion.enid.campo.data.sync

/**
 * ESTRUCTURA JERARQUICA EN FIREBASE REALTIME DATABASE
 * ===========================================================================
 * Cada registro se guarda con su UUID local como llave, de modo que escribir
 * dos veces el mismo registro SOBREESCRIBE la misma ruta y jamas duplica.
 *
 *   raiz
 *   |--.Aspirantes
 *   |   |-- {idRegistro}          <-- llave = UUID local (no autoincremental)
 *   |   |   |-- nombreCompleto
 *   |   |   |-- tipoDocumento
 *   |   |   |-- documentoIdentidad
 *   |   |   |-- fechaNacimiento
 *   |   |   |-- edad
 *   |   |   |-- telefono
 *   |   |   |-- correoElectronico
 *   |   |   |-- municipioResidencia
 *   |   |   |-- municipioCaptura
 *   |   |   |-- jornadaCaptura
 *   |   |   |-- programaInteres
 *   |   |   |-- medioConocimiento
 *   |   |   |-- comoSeEntero
 *   |   |   |-- condicionesEconomicas
 *   |   |   |-- consentimientoDatos
 *   |   |   |-- observaciones
 *   |   |   |-- fechaRegistro
 *   |   |   |-- fechaRegistroLegible
 *   |   |   |-- fechaSincronizacion
 *    *   |   `-- {idRegistro}/_indice/documentoIdentidad
 *   |                            <-- permite "ya esta preinscrito?" en O(1)
 *   `-- Evidencias
 *       `-- {idRegistro}
 *           |-- nombreArchivo
 *           |-- tamanoBytes
 *           |-- anchoPx
 *           |-- altoPx
 *           `-- contenido      (Base64 JPEG <=1024 px, calidad 70)
 *
 * POR QUE UNA SOLA ESCRITURA MULTI-RUTA:
 * RTDB no tiene transacciones entre rutas. Si los datos se escribieran en una
 * llamada y la foto en otra, un corte de red dejaria un aspirante sin evidencia,
 * que es justo la condicion que el taller marca como ERROR. `updateChildren` con
 * un mapa de rutas escribe todo en una sola operacion atomica.
 *
 * POR QUE LA EVIDENCIA VA EN RTDB Y NO EN CLOUD STORAGE:
 * el taller pide definir una estrategia "compatible con el alcance del taller".
 * Cloud Storage exigiria reglas de seguridad, signed URLs y una dependencia
 * extra; ademas, con la foto comprimida a <=1024 px la foto queda por debajo de
 * 200 KB, que RTDB maneja sin problema. Se mantiene ademas la copia local: el
 * taller exige conservarla mientras el registro este pendiente.
 */
object EstructuraRtdb {
    const val RAIZ = "fundacionenid_campo"
    const val NODO_ASPIRANTES = "aspirantes"
    const val NODO_EVIDENCIAS = "evidencias"
    const val NODO_INDICE = "_indice"
    const val CAMPO_CONTENIDO = "contenido"

    fun rutaAspirante(idRegistro: String) = "$NODO_ASPIRANTES/$idRegistro"
    fun rutaEvidencia(idRegistro: String) = "$NODO_EVIDENCIAS/$idRegistro"
    fun rutaIndice(idRegistro: String) = "$NODO_ASPIRANTES/$idRegistro/$NODO_INDICE"
}
