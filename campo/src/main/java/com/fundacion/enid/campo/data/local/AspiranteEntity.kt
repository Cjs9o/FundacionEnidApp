package com.fundacion.enid.campo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fundacion.enid.campo.data.model.EstadoSincronizacion

/**
 * ENTIDAD LOCAL DE UN ASPIRANTE — 18 DATOS
 * ===========================================================================
 * Tabla: `aspirantes`   ·   Llave primaria: `idRegistro` (UUID)
 *
 * DICCIONARIO DE DATOS (Fase 3 del taller)
 * ---------------------------------------------------------------------------
 *  #  | Columna                    | Tipo      | Origen   | Uso
 * --- |---------------------------|-----------|----------|------------------------------------------
 *  1  | idRegistro                 | Text/UUID | Automático| Llave primaria. Evita duplicados en RTDB.
 *  2  | nombreCompleto             | Text      | Usuario  | Identificación del aspirante.
 *  3  | tipoDocumento              | Text      | Usuario  | CC / CE / TI / CM / NIT.
 *  4  | documentoIdentidad         | Text      | Usuario  | Clave legal; controla la preinscripción duplicada.
 *  5  | fechaNacimiento            | Text      | Usuario  | Fecha real de nacimiento (dd/mm/aaaa).
 *  6  | edad                       | Int       | Usuario  | Regla R3: 15..100. Criterio de admisión.
 *  7  | telefono                   | Text      | Usuario  | Regla R1: 10 dígitos iniciando en 3.
 *  8  | correoElectronico          | Text      | Usuario  | Regla R2: formato de correo válido.
 *  9  | municipioResidencia         | Text      | Usuario  | Regla R5: debe ser municipio de cobertura ENID.
 * 10  | municipioCaptura           | Text      | Usuario  | Dónde se hizo la brigada. Alimenta el reporte.
 * 11  | jornadaCaptura             | Text      | Usuario  | Mañana / Tarde / Nocturna.
 * 12  | programaInteres            | Text      | Usuario  | Código del catálogo ENID. Regla R6.
 * 13  | medioConocimiento          | Text      | Usuario  | Referido / colegio / redes / evento / otro.
 * 14  | comoSeEntero              | Text      | Usuario  | Detalle libre del medio de conocimiento.
 * 15  |(addr) condicionesEconomicas| Text      | Usuario  | ¿Trabaja? ¿Ingresos? Becas. Ordena la cola de admisiones.
 * 16  | consentimientoidatos       | Boolean   | Usuario  | Habeas data. Obligatorio para guardar.
 * 17  | observaciones              | Text      | Usuario  | Notas del asesor. Máx. 300 caracteres.
 * 18  | rutaEvidenciaFoto          | Text      | Usuario  | Copia física en filesDir, NO la URI de galería.
 * --- |---------------------------|-----------|----------|------------------------------------------
 *  -- | fechaRegistro              | Long      | Automático| Epoch millis del momento de GUARDAR.
 *  -- | fechaRegistroLegible       | Text      | Automático| "12/03/2026 14:35" para listados y respaldo.
 *  -- | estadoSincronizacion       | Text/Enum | Automático| PENDIENTE / SINCRONIZADO / ERROR.
 *  -- | fechaSincronizacion        | Long?     | Automático| Epoch millis; null hasta que RTDB confirme.
 *  -- | errorSincronizacion        | Text?     | Automático| Causa real del fallo, mostrada bajo el chip.
 *  -- | intentosSincronizacion     | Int       | Automático| Trazabilidad de reintentos.
 *
 * RESULTADO: 13 datos diligenciados por el usuario (incluida la imagen), muy
 * por encima del mínimo de 8, y 5 generados automáticamente.
 *
 * ÍNDICES
 * ---------------------------------------------------------------------------
 * · `documentoIdentidad` único → impide guardar DOS veces al mismo aspirante
 *   dentro del dispositivo. Es la red de seguridad local del requisito
 *   "Una segunda sincronización no deberá duplicar registros ya enviados".
 * · `estadoSincronizacion` + `fechaRegistro` → la consulta de pendientes es
 *   un índice compuesto y el orden "del más viejo al más nuevo" sale gratis.
 */
@Entity(
    tableName = "aspirantes",
    indices = [
        Index(value = ["documentoIdentidad"], unique = true),
        Index(value = ["estadoSincronizacion", "fechaRegistro"])
    ]
)
data class AspiranteEntity(

    // 1 ------------------------------------------------------------------------
    @PrimaryKey
    @ColumnInfo(name = "idRegistro")
    val idRegistro: String,

    // 2 ------------------------------------------------------------------------
    @ColumnInfo(name = "nombreCompleto")
    val nombreCompleto: String,

    // 3 ------------------------------------------------------------------------
    @ColumnInfo(name = "tipoDocumento")
    val tipoDocumento: String,

    // 4 ------------------------------------------------------------------------
    @ColumnInfo(name = "documentoIdentidad")
    val documentoIdentidad: String,

    // 5 ------------------------------------------------------------------------
    @ColumnInfo(name = "fechaNacimiento")
    val fechaNacimiento: String,

    // 6 ------------------------------------------------------------------------
    @ColumnInfo(name = "edad")
    val edad: Int,

    // 7 ------------------------------------------------------------------------
    @ColumnInfo(name = "telefono")
    val telefono: String,

    // 8 ------------------------------------------------------------------------
    @ColumnInfo(name = "correoElectronico")
    val correoElectronico: String,

    // 9 ------------------------------------------------------------------------
    @ColumnInfo(name = "municipioResidencia")
    val municipioResidencia: String,

    // 10 -----------------------------------------------------------------------
    @ColumnInfo(name = "municipioCaptura")
    val municipioCaptura: String,

    // 11 -----------------------------------------------------------------------
    @ColumnInfo(name = "jornadaCaptura")
    val jornadaCaptura: String,

    // 12 -----------------------------------------------------------------------
    @ColumnInfo(name = "programaInteres")
    val programaInteres: String,

    // 13 -----------------------------------------------------------------------
    @ColumnInfo(name = "medioConocimiento")
    val medioConocimiento: String,

    // 14 -----------------------------------------------------------------------
    @ColumnInfo(name = "comoSeEntero")
    val comoSeEntero: String,

    // 15 -----------------------------------------------------------------------
    @ColumnInfo(name = "condicionesEconomicas")
    val condicionesEconomicas: String,

    // 16 -----------------------------------------------------------------------
    @ColumnInfo(name = "consentimientoDatos")
    val consentimientoDatos: Boolean,

    // 17 -----------------------------------------------------------------------
    @ColumnInfo(name = "observaciones")
    val observaciones: String,

    // 18 -----------------------------------------------------------------------
    @ColumnInfo(name = "rutaEvidenciaFoto")
    val rutaEvidenciaFoto: String,

    // --- Automáticos ----------------------------------------------------------
    @ColumnInfo(name = "fechaRegistro")
    val fechaRegistro: Long,

    @ColumnInfo(name = "fechaRegistroLegible")
    val fechaRegistroLegible: String,

    @ColumnInfo(name = "estadoSincronizacion")
    val estadoSincronizacion: String,

    @ColumnInfo(name = "fechaSincronizacion")
    val fechaSincronizacion: Long?,

    @ColumnInfo(name = "errorSincronizacion")
    val errorSincronizacion: String?,

    @ColumnInfo(name = "intentosSincronizacion")
    val intentosSincronizacion: Int = 0
) {
    // Estado tipado, para que la UI nunca trabaje con cadenas sueltas.
    val estado: EstadoSincronizacion get() = EstadoSincronizacion.desde(estadoSincronizacion)

    val esPendienteDeEnvio: Boolean get() = estado.esSincronizable
    val esEditable: Boolean get() = estado.esEditable

    /** Identificador corto y legible para reconocer el registro en el listado. */
    fun idCorto(): String = idRegistro.take(8).uppercase()
}
