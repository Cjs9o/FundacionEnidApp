package com.fundacion.enid.campo.data.model

/**
 * CATÁLOGOS DE LA FUNDACIÓN ENID
 * ---------------------------------------------------------------------------
 * Estos catálogos son la fuente de las validaciones de tipo enumerado.
 * No se permiten valores libres: el usuario SELECCIONA de un catálogo, lo que
 * elimina de raíz la basura textual y garantiza que lo guardado sea
 * interpretable por el equipo de admisiones.
 *
 * Recuperados del catálogo real de la Fundación Educativa ENID
 * (programas técnicos, diplomados y cursos cortos disponibles para 2026).
 */

data class TipoDocumento(val codigo: String, val descripcion: String) {
    override fun toString(): String = "$codigo · $descripcion"
}

data class Programa(val codigo: String, val nombre: String, val nivel: NivelPrograma)

enum class NivelPrograma(val etiqueta: String) {
    TECNICO("Técnico"),
    DIPLOMADO("Diplomado"),
    CURSO("Curso corto")
}

/** Municipios donde la Fundación ejecuta brigadas de captación. */
object MunicipiosCobertura {
    val todos = listOf(
        "Chía",
        "Sopó",
        "Zipaquirá",
        "Cota",
        "Cajicá",
        "Facatativá",
        "Chocontá",
        "Guatavita",
        "San Carlos de Pinjalote"
    )
}

/** Tipo de documento de identidad aceptado por admisiones. */
object TiposDocumento {
    val todos = listOf(
        TipoDocumento("CC", "Cédula de ciudadanía"),
        TipoDocumento("CE", "Cédula de extranjería"),
        TipoDocumento("TI", "Tarjeta de identidad"),
        TipoDocumento("CM", "Carné de matrícula"),
        TipoDocumento("NIT", "NIT de persona jurídica")
    )

    fun porCodigo(codigo: String): TipoDocumento? = todos.firstOrNull { it.codigo == codigo }
}

/**
 * Programas ofrecidos por ENID. El campo `codigo` es el que viaja a Firebase:
 * es estable, corto y legible directamente desde la consola del servidor.
 */
object CatalogoProgramas {

    val programas: List<Programa> = listOf(
        // --- Técnicos ---
        Programa("T-AGU", "Técnico en Agropecuaria", NivelPrograma.TECNICO),
        Programa("T-ANA", "Técnico en Analítica de Sistemas", NivelPrograma.TECNICO),
        Programa("T-COO", "Técnico en Cocina", NivelPrograma.TECNICO),
        Programa("T-CON", "Técnico en Contaduría", NivelPrograma.TECNICO),
        Programa("T-DIS", "Técnico en Diseño Gráfico", NivelPrograma.TECNICO),
        Programa("T-ELE", "Técnico en Electrónica", NivelPrograma.TECNICO),
        Programa("T-EST", "Técnico en Estética y Cosmetología", NivelPrograma.TECNICO),
        Programa("T-GES", "Técnico en Gestión de Empresas", NivelPrograma.TECNICO),
        Programa("T-INF", "Técnico en Infinite Systems", NivelPrograma.TECNICO),
        Programa("T-MEC", "Técnico en Mecánica Automotriz", NivelPrograma.TECNICO),
        Programa("T-SAL", "Técnico en Salud Ocupacional", NivelPrograma.TECNICO),
        Programa("T-SIS", "Técnico en Sistemas", NivelPrograma.TECNICO),
        Programa("T-TUR", "Técnico en Turismo", NivelPrograma.TECNICO),
        // --- Diplomados ---
        Programa("D-EMP", "Diplomado en Emprendimiento", NivelPrograma.DIPLOMADO),
        Programa("D-EDU", "Diplomado en Educación Preescolar", NivelPrograma.DIPLOMADO),
        Programa("D-SAL", "Diplomado en Salud Ocupacional", NivelPrograma.DIPLOMADO),
        // --- Cursos cortos ---
       Programa("C-EXC", "Excel intermedio", NivelPrograma.CURSO),
        Programa("C-NAT", "Primeros auxilios", NivelPrograma.CURSO)
    )

    /** Alimenta el desplegable de la pantalla de captura, agrupado por nivel. */
    fun porNivel(nivel: NivelPrograma): List<Programa> = programas.filter { it.nivel == nivel }

    fun porCodigo(codigo: String): Programa? = programas.firstOrNull { it.codigo == codigo }

    fun existe(codigo: String): Boolean = porCodigo(codigo) != null

    /** Etiqueta legible para el chip del detalle y de la tarjeta del listado. */
    fun etiquetaDe(codigo: String): String =
        porCodigo(codigo)?.let { "${it.codigo} · ${it.nombre}" } ?: "Programa no identificado"
}

/** Jornada en la que se realizó la brigada. Alimenta el reporte de admisiones. */
object JornadasCaptura {
    val todas = listOf("Mañana", "Tarde", "Nocturna")
}

/** ¿Cómo conoció el campo la Fundación? Sirve para medir disponibilidad de cupos. */
object MediosConocimiento {
    val todos = listOf(
        "Recomendado por un amigo o familiar",
        "Institución educativa",
        "Redes sociales de la Fundación",
        "Evento o feria educativa",
        "Aviso en el municipio",
        "Otro"
    )
}
