package com.fundacion.enid.campo.data.validation

/**
 * RESULTADO DE VALIDACION POR CAMPO
 * ---------------------------------------------------------------------------
 * La validacion se devuelve como mapa campo -> mensaje y no como un simple
 * `Boolean`, porque la UI necesita PINTAR el campo que falla y BAJAR LA
 * PANTALLA hasta el primer error: es el comportamiento exigido en la matriz de
 * casos de comportamiento del taller.
 */
data class ResultadoValidacion(
    val erroresPorCampo: Map<Campo, String>
) {
    val esValido: Boolean get() = erroresPorCampo.isEmpty()

    /** Primer campo con error, en el orden en que aparece en el formulario. */
    val primerCampoConError: Campo?
        get() = erroresPorCampo.keys.minByOrNull { it.orden }

    fun mensajeDe(campo: Campo): String? = erroresPorCampo[campo]

    fun tieneErrorEn(campo: Campo): Boolean = erroresPorCampo.containsKey(campo)

    companion object {
        val VALIDO = ResultadoValidacion(emptyMap())
    }
}

/**
 * CAMPOS DEL FORMULARIO
 * ---------------------------------------------------------------------------
 * `orden` replica la disposicion de la pantalla de captura. Sirve para dos
 * cosas: ordenar los mensajes de error como los ve el usuario y hacer que el
 * scroll automatico baje al primer error real, no a uno arbitrario.
 */
enum class Campo(val orden: Int) {
    NOMBRE_COMPLETO(1),
    TIPO_DOCUMENTO(2),
    DOCUMENTO_IDENTIDAD(3),
    FECHA_NACIMIENTO(4),
    EDAD(5),
    TELEFONO(6),
    CORREO_ELECTRONICO(7),
    MUNICIPIO_RESIDENCIA(8),
    MUNICIPIO_CAPTURA(9),
    JORNADA_CAPTURA(10),
    PROGRAMA_INTERES(11),
    MEDIO_CONOCIMIENTO(12),
    COMO_SE_ENTERO(13),
    CONDICIONES_ECONOMICAS(14),
    CONSENTIMIENTO_DATOS(15),
    OBSERVACIONES(16),
    EVIDENCIA_FOTOGRAFICA(17)
}

/** Datos crudos del formulario, tal como llegan de la pantalla de captura. */
data class BorradorAspirante(
    val nombreCompleto: String = "",
    val tipoDocumento: String = "",
    val documentoIdentidad: String = "",
    val fechaNacimiento: String = "",
    val edad: String = "",
    val telefono: String = "",
    val correoElectronico: String = "",
    val municipioResidencia: String = "",
    val municipioCaptura: String = "",
    val jornadaCaptura: String = "",
    val programaInteres: String = "",
    val medioConocimiento: String = "",
    val comoSeEntero: String = "",
    val condicionesEconomicas: String = "",
    val consentimientoDatos: Boolean = false,
    val observaciones: String = "",
    /** Ruta o URI temporal de la foto ya capturada. Vacio si no hay evidencia. */
    val rutaEvidencia: String? = null,
    /** Id del registro al editar; vacio si es una captura nueva. */
    val idRegistro: String = ""
) {
    val esEdicion: Boolean get() = idRegistro.isNotBlank()
    val tieneEvidencia: Boolean get() = !rutaEvidencia.isNullOrBlank()
}
