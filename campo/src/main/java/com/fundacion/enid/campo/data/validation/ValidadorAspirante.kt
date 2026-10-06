package com.fundacion.enid.campo.data.validation

import com.fundacion.enid.campo.data.model.CatalogoProgramas
import com.fundacion.enid.campo.data.model.JornadasCaptura
import com.fundacion.enid.campo.data.model.MediosConocimiento
import com.fundacion.enid.campo.data.model.MunicipiosCobertura
import com.fundacion.enid.campo.data.model.TiposDocumento
import java.util.Calendar

/**
 * REGLAS DE VALIDACION PROPIAS DEL CASO ENID
 * ===========================================================================
 * Ocho reglas. Las cinco primeras son las minimas exigidas por el taller y las
 * tres ultimas nacen del problema real: cobertura municipal delimitada,
 * عمر habil para admision, y la obligatoriedad del documento fotografico.
 *
 * Regla | Condicion                                              | Por que es necesaria
 * ------+--------------------------------------------------------+----------------------------------
 *  R1    | telefono = 10 digitos y empieza en 3                  | Contacto real por llamada/WhatsApp
 *  R2    | correo con formato valido (regex, un solo @, sin     | Evita rebotes en el correo de
 *        | espacios, dominio con punto)                          | matriculacion
 *  R3    | edad entera entre 15 y 100                            | Edad minima reglamentaria + detectar
 *        |                                                        | adultos mayores que requieren atencion
 *  R4    | documento = 10 digitos numericos y tipo del catalogo | Clave legal sin ambiguedad; el indice
 *        |                                                        | unico de la tabla depende de esto
 *  R5    | nombre = 2..4 palabras, solo letras y espacios,     | Evita "xx", "Juan123" o datos de una
 *        | cada palabra >= 3 letras                              | sola palabra que no identifican a nadie
 *  R6    | programa pertenece al catalogo ENID                  | No se admite texto libre: el programa
 *        |                                                        | debe existir en el plan academico
 *  R7    | municipio (residencia y captura) en la cobertura ENID | Solo se capturan municipios donde la
 *        |                                                        | Fundación tiene presencia real
 *  R8    | fecha de nacimiento coherente con la edad y           | Un nanny de 18 años con nacimiento en
 *        | fecha <= hoy; evidencia fotografica presente          | 2010 es un dato falso de origen
 *
 * TODAS son validadas EN MEMORIA antes de GUARDAR. Nada se escribe en Room ni
 * en Firebase hasta que las ocho pasan. Es el principio Offline-First: lo que
 * queda guardado esta limpio.
 *
 * Esta clase es `object` puro de Kotlin sin dependencias de Android, por eso
 * esta cubierta por tests unitarios JVM (ver ValidacionAspiranteTest).
 */
object ValidadorAspirante {

    const val LONGITUD_TELEFONO = 10
    const val LONGITUD_DOCUMENTO = 10
    const val EDAD_MINIMA = 15
    const val EDAD_MAXIMA = 100
    const val MAX_OBSERVACIONES = 300
    const val MIN_LETRAS_POR_PALABRA = 3
    const val MAX_PALABRAS_NOMBRE = 4

    /**
     * Regex de correo deliberadamente estricto: una sola @, dominio con al
     * menos un punto y TLD alfabetico de 2 a 6, sin espacios ni caracteres de
     * control. Filtra tanto "juan@" como "juan@@enid.edu.co".
     */
    private val REGEX_CORREO = Regex(
        "^[A-Za-z0-9._%+\\-]+@" +
            "[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,6}$"
    )

    /**
     * Valida el borrador completo.
     *
     * @param borrador datos del formulario
     * @param documentoYaRegistrado id de un registro previo con el mismo
     *        documento, o null si no existe. Se consulta en Room, NUNCA en red.
     */
    fun validar(borrador: BorradorAspirante, documentoYaRegistrado: String? = null): ResultadoValidacion {
        val errores = linkedMapOf<Campo, String>()

        // R5 ------------------------------------------------------------------
        val nombre = borrador.nombreCompleto.trim().replace(Regex("\\s+"), " ")
        when {
            nombre.isEmpty() ->
                errores[Campo.NOMBRE_COMPLETO] = "Ingrese el nombre completo del aspirante."
            !nombre.contains(' ') ->
                errores[Campo.NOMBRE_COMPLETO] =
                    "Escriba nombre y apellidos, separados por un espacio."
            nombre.split(' ').size > MAX_PALABRAS_NOMBRE ->
                errores[Campo.NOMBRE_COMPLETO] =
                    "Máximo $MAX_PALABRAS_NOMBRE palabras en el nombre."
            !nombre.matches(Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$")) ->
                errores[Campo.NOMBRE_COMPLETO] =
                    "El nombre solo puede llevar letras y espacios."
            nombre.split(' ').any { it.length < MIN_LETRAS_POR_PALABRA } ->
                errores[Campo.NOMBRE_COMPLETO] =
                    "Cada palabra debe tener al menos $MIN_LETRAS_POR_PALABRA letras."
        }

        // Tipo de documento ----------------------------------------------------
        if (TiposDocumento.porCodigo(borrador.tipoDocumento) == null) {
            errores[Campo.TIPO_DOCUMENTO] = "Seleccione el tipo de documento que presenta."
        }

        // R4 ------------------------------------------------------------------
        val documento = borrador.documentoIdentidad.trim()
        when {
            documento.isEmpty() ->
                errores[Campo.DOCUMENTO_IDENTIDAD] = "Ingrese el número de documento."
            !documento.all { it.isDigit() } ->
                errores[Campo.DOCUMENTO_IDENTIDAD] =
                    "El documento debe contener solo números ($LONGITUD_DOCUMENTO dígitos)."
            documento.length != LONGITUD_DOCUMENTO ->
                errores[Campo.DOCUMENTO_IDENTIDAD] =
                    "El documento debe tener $LONGITUD_DOCUMENTO dígitos."
            documentoYaRegistrado != null ->
                errores[Campo.DOCUMENTO_IDENTIDAD] =
                    "Ya existe una preinscripción guardada con este documento " +
                        "(${documentoYaRegistrado.take(8).uppercase()})."
        }

        // R8a - fecha de nacimiento -------------------------------------------
        val nacimiento = fechaDeNacimientoValida(borrador.fechaNacimiento)
        if (borrador.fechaNacimiento.isBlank()) {
            errores[Campo.FECHA_NACIMIENTO] = "Ingrese la fecha de nacimiento (dd/mm/aaaa)."
        } else if (nacimiento == null) {
            errores[Campo.FECHA_NACIMIENTO] =
                "Fecha inválida. Use el formato dd/mm/aaaa y una fecha real."
        }

        // R3 ------------------------------------------------------------------
        val edadTexto = borrador.edad.trim()
        val edad = edadTexto.toIntOrNull()
        when {
            edadTexto.isEmpty() ->
                errores[Campo.EDAD] = "Ingrese la edad del aspirante."
            edad == null ->
                errores[Campo.EDAD] = "La edad debe ser un número entero, sin letras ni signos."
            edad < EDAD_MINIMA ->
                errores[Campo.EDAD] =
                    "La edad mínima para preinscripción es $EDAD_MINIMA años."
            edad > EDAD_MAXIMA ->
                errores[Campo.EDAD] =
                    "La edad debe estar comprendida entre $EDAD_MINIMA y $EDAD_MAXIMA años."
        }

        // Coherencia edad <-> fecha de nacimiento (misma regla R8) ------------
        if (edad != null && nacimiento != null && edad in EDAD_MINIMA..EDAD_MAXIMA) {
            val aniosReales = aniosTranscurridos(nacimiento)
            if (aniosReales != edad) {
                errores[Campo.FECHA_NACIMIENTO] =
                    "La fecha de nacimiento indica $aniosReales años, pero se escribió $edad. " +
                        "Revise ambos datos."
            }
        }

        // R1 ------------------------------------------------------------------
        val telefono = borrador.telefono.trim()
        when {
            telefono.isEmpty() ->
                errores[Campo.TELEFONO] = "Ingrese el número de celular de contacto."
            !telefono.all { it.isDigit() } ->
                errores[Campo.TELEFONO] =
                    "Ingrese un número celular válido de Colombia " +
                        "($LONGITUD_TELEFONO dígitos iniciado en 3)."
            telefono.length != LONGITUD_TELEFONO ->
                errores[Campo.TELEFONO] =
                    "El celular debe tener $LONGITUD_TELEFONO dígitos."
            telefono[0] != '3' ->
                errores[Campo.TELEFONO] =
                    "Un celular colombiano empieza por 3. Revise el primer dígito."
        }

        // R2 ------------------------------------------------------------------
        val correo = borrador.correoElectronico.trim()
        when {
            correo.isEmpty() ->
                errores[Campo.CORREO_ELECTRONICO] = "Ingrese el correo electrónico."
            correo.count { it == '@' } != 1 ->
                errores[Campo.CORREO_ELECTRONICO] =
                    "El formato del correo electrónico es incorrecto."
            !REGEX_CORREO.matches(correo) ->
                errores[Campo.CORREO_ELECTRONICO] =
                    "El formato del correo electrónico es incorrecto. " +
                        "Ejemplo: nombre@correo.com"
        }

        // R7 ------------------------------------------------------------------
        if (borrador.municipioResidencia.trim().isEmpty()) {
            errores[Campo.MUNICIPIO_RESIDENCIA] = "Seleccione el municipio de residencia."
        } else if (!MunicipiosCobertura.todos.contains(borrador.municipioResidencia.trim())) {
            errores[Campo.MUNICIPIO_RESIDENCIA] =
                "Municipio fuera de la cobertura ENID. Comuníquese con la oficina de admisiones."
        }

        if (borrador.municipioCaptura.trim().isEmpty()) {
            errores[Campo.MUNICIPIO_CAPTURA] = "Seleccione el municipio donde se hizo la brigada."
        } else if (!MunicipiosCobertura.todos.contains(borrador.municipioCaptura.trim())) {
            errores[Campo.MUNICIPIO_CAPTURA] = "Municipio de captura no reconocido."
        }

        if (!JornadasCaptura.todas.contains(borrador.jornadaCaptura.trim())) {
            errores[Campo.JORNADA_CAPTURA] = "Seleccione la jornada de la brigada."
        }

        // R6 ------------------------------------------------------------------
        if (borrador.programaInteres.trim().isEmpty()) {
            errores[Campo.PROGRAMA_INTERES] = "Seleccione el programa de interés."
        } else if (!CatalogoProgramas.existe(borrador.programaInteres.trim())) {
            errores[Campo.PROGRAMA_INTERES] =
                "Ese programa no existe en el catálogo de ENID."
        }

        if (!MediosConocimiento.todos.contains(borrador.medioConocimiento.trim())) {
            errores[Campo.MEDIO_CONOCIMIENTO] = "Seleccione cómo conoció la Fundación."
        }

        // Detalle obligatorio cuando el medio elegido fue "Otro" -----------
        if (borrador.medioConocimiento.trim() == "Otro" &&
            borrador.comoSeEntero.trim().length < MIN_LETRAS_POR_PALABRA
        ) {
            errores[Campo.COMO_SE_ENTERO] =
                "Describa en al menos $MIN_LETRAS_POR_PALABRA caracteres cómo se enteró."
        }

        if (borrador.condicionesEconomicas.trim().length < MIN_LETRAS_POR_PALABRA) {
            errores[Campo.CONDICIONES_ECONOMICAS] =
                "Registre al menos una condición económica del Aspirante."
        }

        // Habeas data ---------------------------------------------------------
        if (!borrador.consentimientoDatos) {
            errores[Campo.CONSENTIMIENTO_DATOS] =
                "Debe aceptar el tratamiento de datos para guardar la preinscripción."
        }

        if (borrador.observaciones.length > MAX_OBSERVACIONES) {
            errores[Campo.OBSERVACIONES] =
                "Máximo $MAX_OBSERVACIONES caracteres (van ${borrador.observaciones.length})."
        }

        // R8b - evidencia fotografica ------------------------------------------
        if (!borrador.tieneEvidencia) {
            errores[Campo.EVIDENCIA_FOTOGRAFICA] =
                "Adjunte la foto del documento: sin evidencia no se puede guardar."
        }

        return ResultadoValidacion(errores)
    }

    /**
     * Convierte "dd/mm/aaaa" en [FechaNacimiento], o null si la fecha no existe
     * en el calendario (rechaza 31/02/2026 y aaaa < 1900).
     */
    private fun fechaDeNacimientoValida(texto: String): FechaNacimiento? {
        val partes = texto.trim().split("/")
        if (partes.size != 3) return null
        val dia = partes[0].toIntOrNull() ?: return null
        val mes = partes[1].toIntOrNull() ?: return null
        val anio = partes[2].toIntOrNull() ?: return null
        if (partes[0].length != 2 || partes[1].length != 2 || partes[2].length != 4) return null
        if (mes !in 1..12 || dia !in 1..31 || anio !in 1900..anioActual()) return null
        // Verificacion real con Calendar: descarta 31 de febrero.
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(anio, mes - 1, dia)
        if (cal.get(Calendar.DAY_OF_MONTH) != dia) return null
        if (cal.get(Calendar.MONTH) + 1 != mes) return null
        return FechaNacimiento(dia, mes, anio)
    }

    /** Dia, mes y anio por separado: la edad cumplida necesita los tres. */
    private data class FechaNacimiento(val dia: Int, val mes: Int, val anio: Int)

    /**
     * Anios cumplidos a hoy, para contrastar con la edad digitada.
     *
     * Solo puedeafiarse del dia y del mes de NACIMIENTO: se compara contra la
     * fecha de hoy, no contra el dia-del-anio. La version anterior armaba el
     * cumpleaños con el dia y el mes de HOY, con lo cual cualquier peralatan
     * capturado despues del dia de su cumpleanos (salvo el 1 de enero) recibia
     * "la fecha indica N-1 anos" y era rechazado.
     */
    private fun aniosTranscurridos(nacimiento: FechaNacimiento): Int {
        val ahora = Calendar.getInstance()
        val mesHoy = ahora.get(Calendar.MONTH) + 1
        val diaHoy = ahora.get(Calendar.DAY_OF_MONTH)
        var edad = ahora.get(Calendar.YEAR) - nacimiento.anio
        // aun no ha cumplo anos este ano si hoy es anterior al dia de su cumpleanos
        if (mesHoy < nacimiento.mes || (mesHoy == nacimiento.mes && diaHoy < nacimiento.dia)) edad--
        return edad
    }

    private fun anioActual(): Int = Calendar.getInstance().get(Calendar.YEAR)

    /** Formatea "dd/mm/aaaa" a epoch millis a medianoche local. */
    fun fechaAEpochMillis(texto: String): Long? {
        val nacimiento = fechaDeNacimientoValida(texto) ?: return null
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(nacimiento.anio, nacimiento.mes - 1, nacimiento.dia, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
