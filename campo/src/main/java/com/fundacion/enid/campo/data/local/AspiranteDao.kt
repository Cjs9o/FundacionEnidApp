package com.fundacion.enid.campo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO DE LA TABLA `aspirantes`
 * ---------------------------------------------------------------------------
 * Todas las lecturas de pantalla usan [Flow]: al cambiar el estado de un
 * registro las pantallas se refrescan solas, sin que el ViewModel tenga que
 * recargar a mano. Por eso el contador "PENDIENTES" del resumen sube en el
 * instante en que el usuario pulsa GUARDAR.
 */
@Dao
interface AspiranteDao {

    // ---------------------------------------------------------------- LECTURA --

    @Query("SELECT * FROM aspirantes ORDER BY fechaRegistro DESC")
    fun observarTodos(): Flow<List<AspiranteEntity>>

    @Query("SELECT * FROM aspirantes WHERE idRegistro = :idRegistro LIMIT 1")
    fun observarPorId(idRegistro: String): Flow<AspiranteEntity?>

    @Query("SELECT * FROM aspirantes WHERE idRegistro = :idRegistro LIMIT 1")
    suspend fun leerPorId(idRegistro: String): AspiranteEntity?

    /** Filtros del listado local. Todo se resuelve contra Room, sin red. */
    @Query(
        """
        SELECT * FROM aspirantes
        WHERE (:estado IS NULL OR estadoSincronizacion = :estado)
        ORDER BY fechaRegistro DESC
        """
    )
    fun observarPorEstado(estado: String?): Flow<List<AspiranteEntity>>

    @Query("SELECT COUNT(*) FROM aspirantes")
    fun observarTotal(): Flow<Int>

    /** Conteo puntual, usado por los tests y por las comprobaciones previas a guardar. */
    @Query("SELECT COUNT(*) FROM aspirantes")
    suspend fun contarTodos(): Int

    @Query("SELECT COUNT(*) FROM aspirantes WHERE estadoSincronizacion = :estado")
    fun observarConteo(estado: String): Flow<Int>

    @Query(
        """
        SELECT MAX(fechaSincronizacion) FROM aspirantes
        WHERE estadoSincronizacion = 'SINCRONIZADO'
        """
    )
    fun observarUltimaSincronizacion(): Flow<Long?>

    // ------------------------------------------------ SELECCION PARA ENVIAR ---

    /**
     * Seleccion de la sincronizacion: solo PENDIENTE y ERROR, del mas viejo al
     * mas nuevo. Es exactamente lo que exige la politica de sincronizacion del
     * taller y se apoya en el indice (estadoSincronizacion, fechaRegistro).
     */
    @Query(
        """
        SELECT * FROM aspirantes
        WHERE estadoSincronizacion IN ('PENDIENTE', 'ERROR')
        ORDER BY fechaRegistro ASC
        """
    )
    suspend fun pendientesParaSincronizar(): List<AspiranteEntity>

    /** Hay algo que enviar? Permite avisar sin lanzar la sincronizacion. */
    @Query(
        """
        SELECT COUNT(*) FROM aspirantes
        WHERE estadoSincronizacion IN ('PENDIENTE', 'ERROR')
        """
    )
    suspend fun contarPorSincronizar(): Int

    /**
     * Verificacion de duplicados en origen: ese documento ya quedo registrado
     * en este dispositivo por una captura anterior.
     */
    @Query(
        """
        SELECT * FROM aspirantes
        WHERE documentoIdentidad = :documento AND idRegistro != :idRegistroExcluido
        LIMIT 1
        """
    )
    suspend fun buscarPorDocumento(
        documento: String,
        idRegistroExcluido: String
    ): AspiranteEntity?

    // -------------------------------------------------------------- ESCRITURA --

    /**
     * ABORT y no REPLACE es deliberado: si se intenta guardar dos veces el
     * mismo documento, el segundo insert revienta por el indice unico y la app
     * informa "ya existe una preinscripcion con ese documento". Un REPLACE
     * sobrescribiria el primer registro y perderiamos trazabilidad.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(aspirante: AspiranteEntity): Long

    /** Actualizacion completa al editar. Solo se usa con estado PENDIENTE. */
    @Query(
        """
        UPDATE aspirantes SET
            nombreCompleto = :nombreCompleto,
            tipoDocumento = :tipoDocumento,
            documentoIdentidad = :documentoIdentidad,
            fechaNacimiento = :fechaNacimiento,
            edad = :edad,
            telefono = :telefono,
            correoElectronico = :correoElectronico,
            municipioResidencia = :municipioResidencia,
            municipioCaptura = :municipioCaptura,
            jornadaCaptura = :jornadaCaptura,
            programaInteres = :programaInteres,
            medioConocimiento = :medioConocimiento,
            comoSeEntero = :comoSeEntero,
            condicionesEconomicas = :condicionesEconomicas,
            consentimientoDatos = :consentimientoDatos,
            observaciones = :observaciones,
            rutaEvidenciaFoto = :rutaEvidenciaFoto,
            errorSincronizacion = NULL
        WHERE idRegistro = :idRegistro
        """
    )
    suspend fun actualizarDatos(
        idRegistro: String,
        nombreCompleto: String,
        tipoDocumento: String,
        documentoIdentidad: String,
        fechaNacimiento: String,
        edad: Int,
        telefono: String,
        correoElectronico: String,
        municipioResidencia: String,
        municipioCaptura: String,
        jornadaCaptura: String,
        programaInteres: String,
        medioConocimiento: String,
        comoSeEntero: String,
        condicionesEconomicas: String,
        consentimientoDatos: Boolean,
        observaciones: String,
        rutaEvidenciaFoto: String
    )

    /**
     * Confirmacion transaccional por item (decision documentada del grupo):
     * solo cuando el servidor responde con exito se marca SINCRONIZADO y se
     * guarda la fecha. Estado, fecha y error se mueven en la MISMA sentencia,
     * asi nunca existe un estado intermedio imposible.
     */
    @Query(
        """
        UPDATE aspirantes SET
            estadoSincronizacion = 'SINCRONIZADO',
            fechaSincronizacion = :fechaSincronizacion,
            errorSincronizacion = NULL,
            intentosSincronizacion = intentosSincronizacion + 1
        WHERE idRegistro = :idRegistro
        """
    )
    suspend fun marcarSincronizado(idRegistro: String, fechaSincronizacion: Long)

    /**
     * Fallo del propio registro: pasa a ERROR y guarda la CAUSA REAL, que la
     * UI muestra bajo el chip junto al boton "Reintentar".
     */
    @Query(
        """
        UPDATE aspirantes SET
            estadoSincronizacion = 'ERROR',
            errorSincronizacion = :causa,
            intentosSincronizacion = intentosSincronizacion + 1
        WHERE idRegistro = :idRegistro
        """
    )
    suspend fun marcarError(idRegistro: String, causa: String)

    /** Se cae la red a mitad del envio: sigue PENDIENTE pero queda registrado. */
    @Query(
        """
        UPDATE aspirantes SET
            estadoSincronizacion = 'PENDIENTE',
            errorSincronizacion = :causa,
            intentosSincronizacion = intentosSincronizacion + 1
        WHERE idRegistro = :idRegistro
        """
    )
    suspend fun marcarPendientePorCausaDeRed(idRegistro: String, causa: String)

    /** Al editar un registro PENDIENTE se limpia cualquier error anterior. */
    @Query("UPDATE aspirantes SET errorSincronizacion = NULL WHERE idRegistro = :idRegistro")
    suspend fun limpiarError(idRegistro: String)

    /**
     * Devuelve un registro ERROR a PENDIENTE para que el boton "Reintentar"
     * tenga sentido antes de volver a pulsar SINCRONIZAR.
     *
     * El motivo tambien se borra: un registro PENDIENTE que conserve el texto
     * "Sin conexión a internet" mostraria un error que ya no existe. Los intentos
     * SI se conservan, porque son el historial de por que se fallo.
     */
    @Query(
        """
        UPDATE aspirantes
        SET estadoSincronizacion = 'PENDIENTE', errorSincronizacion = NULL
        WHERE idRegistro = :idRegistro AND estadoSincronizacion = 'ERROR'
        """
    )
    suspend fun rearmarError(idRegistro: String)

    /** Solo para pruebas y utilidades de soporte tecnico. */
    @Query("DELETE FROM aspirantes WHERE idRegistro = :idRegistro")
    suspend fun borrar(idRegistro: String)
}
