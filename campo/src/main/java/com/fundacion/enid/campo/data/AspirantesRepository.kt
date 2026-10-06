package com.fundacion.enid.campo.data

import android.net.Uri
import com.fundacion.enid.campo.data.evidencia.AlmacenEvidencias
import com.fundacion.enid.campo.data.local.AspiranteDao
import com.fundacion.enid.campo.data.local.AspiranteEntity
import com.fundacion.enid.campo.data.model.EstadoSincronizacion
import com.fundacion.enid.campo.data.validation.BorradorAspirante
import com.fundacion.enid.campo.data.validation.Campo
import com.fundacion.enid.campo.data.validation.ResultadoValidacion
import com.fundacion.enid.campo.data.validation.ValidadorAspirante
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * REPOSITORIO DE ASPIRANTES — EL CORAZON DEL OFFLINE-FIRST
 * ===========================================================================
 * Esta clase responde a la pregunta 3 de la sustentacion: "¿que ocurre
 * exactamente desde que se pulsa GUARDAR hasta que aparece PENDIENTE?"
 *
 * La secuencia exacta de `guardar` es:
 *
 *   1. Valida el borrador EN MEMORIA. Si algo falla, no se escribe nada.
 *   2. Genera el UUID (`UUID.randomUUID().toString()`) — decision documentada
 *      frente a un ID autoincremental.
 *   3. Verifica que la foto sea legible. Si no, no se escribe nada.
 *   4. COPIA Y COMPRIME la foto en `filesDir/evidencias/{uuid}.jpg`.
 *   5. Inserta la fila en Room con estado PENDIENTE.
 *
 * Importante: en ningun paso se toca la red. Ni una sola linea de este archivo
 * sabe que existe Firebase.
 *
 * Si el paso 5 falla (por ejemplo, el indice unico de documento), se borra la
 * foto recien copiada en el paso 4. Sin esa limpieza, un reintento fallido
 * dejaria archivos huerfanos acumulandose en filesDir.
 */
class AspirantesRepository(
    private val dao: AspiranteDao,
    private val almacen: AlmacenEvidencias
) {

    companion object {
        private const val PATRON_FECHA_HUMANA = "dd/MM/yyyy HH:mm"
    }

    // ------------------------------------------------------------- OBSERVAR --

    fun observarTodos(): Flow<List<AspiranteEntity>> = dao.observarTodos()

    fun observarPorId(idRegistro: String): Flow<AspiranteEntity?> = dao.observarPorId(idRegistro)

    fun observarPorEstado(estado: EstadoSincronizacion?): Flow<List<AspiranteEntity>> =
        dao.observarPorEstado(estado?.almacenado)

    fun observarTotal(): Flow<Int> = dao.observarTotal()

    fun observarPendientes(): Flow<Int> = dao.observarConteo(EstadoSincronizacion.PENDIENTE.almacenado)

    fun observarSincronizados(): Flow<Int> =
        dao.observarConteo(EstadoSincronizacion.SINCRONIZADO.almacenado)

    fun observarConError(): Flow<Int> = dao.observarConteo(EstadoSincronizacion.ERROR.almacenado)

    fun observarUltimaSincronizacion(): Flow<Long?> = dao.observarUltimaSincronizacion()

    suspend fun contarPorSincronizar(): Int = dao.contarPorSincronizar()

    suspend fun leerPorId(idRegistro: String): AspiranteEntity? = dao.leerPorId(idRegistro)

    suspend fun pendientesParaSincronizar(): List<AspiranteEntity> = dao.pendientesParaSincronizar()

    // ------------------------------------------------------------- GUARDAR ---

    /**
     * Guarda un registro nuevo. SOLO escribe en disco y en Room.
     *
     * @return [ResultadoGuardado] con la entidad creada, o el motivo del rechazo.
     */
    suspend fun guardar(borrador: BorradorAspirante): ResultadoGuardado {

        // --- 1. Validacion en memoria. Nada se escribe si falla --------------
        val existenteConMismoDocumento = borrador.documentoIdentidad
            .trim()
            .takeIf { it.isNotEmpty() && it.all(Char::isDigit) && it.length == 10 }
            ?.let { dao.buscarPorDocumento(it, borrador.idRegistro) }
            ?.idRegistro

        val validacion = ValidadorAspirante.validar(borrador, existenteConMismoDocumento)
        if (!validacion.esValido) {
            return ResultadoGuardado.Rechazado(validacion)
        }

        val idRegistro = borrador.idRegistro.ifBlank { UUID.randomUUID().toString() }

        // --- 3. La foto debe ser legible ANTES de escribir nada -------------
        val origen = borrador.rutaEvidencia
        if (origen.isNullOrBlank()) {
            return ResultadoGuardado.Rechazado(
                ResultadoValidacion(
                    mapOf(
                        Campo.EVIDENCIA_FOTOGRAFICA to
                            "Adjunte la foto del documento: sin evidencia no se puede guardar."
                    )
                )
            )
        }

        // --- 4. Copia fisica comprimida en filesDir -------------------------
        val uriOrigen = aUri(origen)
        val copia = almacen.guardarEvidencia(uriOrigen, idRegistro)
        val rutaInterna = copia.getOrElse { causa ->
            return ResultadoGuardado.Rechazado(
                ResultadoValidacion(
                    mapOf(
                        Campo.EVIDENCIA_FOTOGRAFICA
                            to (causa.message ?: "No se pudo guardar la evidencia fotográfica.")
                    )
                )
            )
        }

        val ahora = System.currentTimeMillis()
        val entidad = AspiranteEntity(
            idRegistro = idRegistro,
            nombreCompleto = borrador.nombreCompleto.trim().replace(Regex("\\s+"), " "),
            tipoDocumento = borrador.tipoDocumento,
            documentoIdentidad = borrador.documentoIdentidad.trim(),
            fechaNacimiento = borrador.fechaNacimiento.trim(),
            edad = borrador.edad.trim().toInt(),
            telefono = borrador.telefono.trim(),
            correoElectronico = borrador.correoElectronico.trim().lowercase(Locale.ROOT),
            municipioResidencia = borrador.municipioResidencia.trim(),
            municipioCaptura = borrador.municipioCaptura.trim(),
            jornadaCaptura = borrador.jornadaCaptura.trim(),
            programaInteres = borrador.programaInteres.trim(),
            medioConocimiento = borrador.medioConocimiento.trim(),
            comoSeEntero = borrador.comoSeEntero.trim(),
            condicionesEconomicas = borrador.condicionesEconomicas.trim(),
            consentimientoDatos = borrador.consentimientoDatos,
            observaciones = borrador.observaciones.trim(),
            rutaEvidenciaFoto = rutaInterna,
            fechaRegistro = ahora,
            fechaRegistroLegible = fechaLegible(ahora),
            estadoSincronizacion = EstadoSincronizacion.PENDIENTE.almacenado,
            fechaSincronizacion = null,
            errorSincronizacion = null,
            intentosSincronizacion = 0
        )

        // --- 5. Insercion en Room -------------------------------------------
        return try {
            dao.insertar(entidad)
            ResultadoGuardado.Guardado(entidad)
        } catch (e: Exception) {
            // Si el insert falla, la foto recien copiada queda huerfana.
            almacen.eliminar(rutaInterna)
            ResultadoGuardado.Rechazado(
                ResultadoValidacion(
                    mapOf(
                        Campo.DOCUMENTO_IDENTIDAD to
                            (e.message ?: "Ya existe una preinscripción con este documento.")
                    )
                )
            )
        }
    }

    /**
     * Edita un registro PENDIENTE. Un registro SINCRONIZADO devuelve
     * [ResultadoEdicion.Bloqueado]: decision documentada del grupo para que no
     * exista informacion distinta en el telefono y en Firebase.
     */
    suspend fun editar(borrador: BorradorAspirante): ResultadoEdicion {
        val idRegistro = borrador.idRegistro
        val actual = dao.leerPorId(idRegistro)
            ?: return ResultadoEdicion.Error(
                mapOf(Campo.NOMBRE_COMPLETO to "El registro ya no existe en este dispositivo.")
            )

        if (!actual.esEditable) {
            return ResultadoEdicion.Bloqueado(
                "Este registro ya fue sincronizado y es de solo lectura."
            )
        }

        val duplicado = dao.buscarPorDocumento(borrador.documentoIdentidad.trim(), idRegistro)?.idRegistro
        val validacion = ValidadorAspirante.validar(borrador, duplicado)
        if (!validacion.esValido) return ResultadoEdicion.Error(validacion.mensajes())

        // Si se cambia la foto, la nueva copia reemplaza a la anterior.
        var rutaInterna = actual.rutaEvidenciaFoto
        if (!borrador.rutaEvidencia.isNullOrBlank() &&
            borrador.rutaEvidencia != actual.rutaEvidenciaFoto
        ) {
            val copia = almacen.guardarEvidencia(
                aUri(borrador.rutaEvidencia), idRegistro
            )
            rutaInterna = copia.getOrElse {
                return ResultadoEdicion.Error(
                    mapOf(
                        Campo.EVIDENCIA_FOTOGRAFICA
                            to (it.message ?: "No se pudo guardar la nueva evidencia.")
                    )
                )
            }
            if (rutaInterna != actual.rutaEvidenciaFoto) almacen.eliminar(actual.rutaEvidenciaFoto)
        }

        dao.actualizarDatos(
            idRegistro = idRegistro,
            nombreCompleto = borrador.nombreCompleto.trim().replace(Regex("\\s+"), " "),
            tipoDocumento = borrador.tipoDocumento,
            documentoIdentidad = borrador.documentoIdentidad.trim(),
            fechaNacimiento = borrador.fechaNacimiento.trim(),
            edad = borrador.edad.trim().toInt(),
            telefono = borrador.telefono.trim(),
            correoElectronico = borrador.correoElectronico.trim().lowercase(Locale.ROOT),
            municipioResidencia = borrador.municipioResidencia.trim(),
            municipioCaptura = borrador.municipioCaptura.trim(),
            jornadaCaptura = borrador.jornadaCaptura.trim(),
            programaInteres = borrador.programaInteres.trim(),
            medioConocimiento = borrador.medioConocimiento.trim(),
            comoSeEntero = borrador.comoSeEntero.trim(),
            condicionesEconomicas = borrador.condicionesEconomicas.trim(),
            consentimientoDatos = borrador.consentimientoDatos,
            observaciones = borrador.observaciones.trim(),
            rutaEvidenciaFoto = rutaInterna
        )
        return ResultadoEdicion.Editado
    }

    /** El boton "Reintentar" del chip ERROR devuelve el registro a PENDIENTE. */
    suspend fun rearmarError(idRegistro: String) = dao.rearmarError(idRegistro)

    /** Convierte una entidad a borrador para prellenar el formulario de edicion. */
    fun aBorrador(a: AspiranteEntity): BorradorAspirante = BorradorAspirante(
        nombreCompleto = a.nombreCompleto,
        tipoDocumento = a.tipoDocumento,
        documentoIdentidad = a.documentoIdentidad,
        fechaNacimiento = a.fechaNacimiento,
        edad = a.edad.toString(),
        telefono = a.telefono,
        correoElectronico = a.correoElectronico,
        municipioResidencia = a.municipioResidencia,
        municipioCaptura = a.municipioCaptura,
        jornadaCaptura = a.jornadaCaptura,
        programaInteres = a.programaInteres,
        medioConocimiento = a.medioConocimiento,
        comoSeEntero = a.comoSeEntero,
        condicionesEconomicas = a.condicionesEconomicas,
        consentimientoDatos = a.consentimientoDatos,
        observaciones = a.observaciones,
        rutaEvidencia = a.rutaEvidenciaFoto,
        idRegistro = a.idRegistro
    )

    /**
     * La foto puede llegar de tres sitios distintos y todos son validos:
     *   · `content://` de la app de camara o del selector de galeria
     *   · `file://` de una foto ya copiada
     *   · ruta absoluta de un archivo (el caso de editar un registro existente)
     */
    private fun aUri(origen: String): Uri = when {
        origen.startsWith("content://") || origen.startsWith("file://") -> Uri.parse(origen)
        else -> Uri.fromFile(java.io.File(origen))
    }

    private fun fechaLegible(millis: Long): String =
        SimpleDateFormat(PATRON_FECHA_HUMANA, Locale.US).format(Date(millis))

    private fun ResultadoValidacion.mensajes(): Map<Campo, String> =
        erroresPorCampo
}

/** Resultado de GUARDAR. */
sealed class ResultadoGuardado {
    data class Guardado(val registro: AspiranteEntity) : ResultadoGuardado()
    data class Rechazado(val validacion: ResultadoValidacion) : ResultadoGuardado()
}

/** Resultado de EDITAR. */
sealed class ResultadoEdicion {
    data object Editado : ResultadoEdicion()

    /** El registro ya fue sincronizado: solo lectura, con el motivo del bloqueo. */
    data class Bloqueado(val motivo: String) : ResultadoEdicion()

    data class Error(
        val errores: Map<Campo, String>
    ) : ResultadoEdicion() {
        constructor(validacion: ResultadoValidacion) : this(validacion.erroresPorCampo)
    }
}
