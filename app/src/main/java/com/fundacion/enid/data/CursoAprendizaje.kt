package com.fundacion.enid.data

import com.fundacion.enid.model.ClassSlot

/**
 * El curso que alimenta **Aprender** y las estadísticas de **Perfil**.
 *
 * No se inventa un temario aparte: las lecciones son las asignaturas que ya
 * están en el horario de la Fundación. Así el avance que la persona marca en
 * Aprender coincide con las materias que ve en Agenda, y el Perfil no muestra
 * cursos ni horas que no existan.
 */
data class LeccionAprendizaje(
    val titulo: String,
    val detalle: String,
    val minutos: Int
)

object CursoAprendizaje {

    /** Cuántas asignaturas del horario se presentan como curso activo. */
    const val TOTAL_LECCIONES = 5

    private val RANGO_HORA = Regex("""(\d{1,2}):(\d{2})""")

    /**
     * Primeras asignaturas distintas del horario. Se mantiene el orden en que
     * aparecen (lunes a viernes), que es el mismo del plan de estudios.
     */
    fun lecciones(repository: DataRepository): List<LeccionAprendizaje> =
        repository.horarioClases
            .distinctBy { it.asignatura }
            .take(TOTAL_LECCIONES)
            .map { slot ->
                LeccionAprendizaje(
                    titulo = slot.asignatura,
                    detalle = detalleDe(slot),
                    minutos = minutosDe(slot.hora)
                )
            }

    /** Nombre del programa activo, con un respaldo coherente con el horario. */
    fun tituloCurso(programa: String?): String =
        programa?.takeIf { it.isNotBlank() } ?: "Soporte de Sistemas e Informática"

    fun completadas(repository: DataRepository, correo: String?): Set<Int> {
        val total = lecciones(repository).size
        return repository.leccionesCompletadas(correo).filter { it in 0 until total }.toSet()
    }

    fun porcentaje(completadas: Int, total: Int): Int =
        if (total <= 0) 0 else (completadas * 100 / total).coerceIn(0, 100)

    private fun detalleDe(slot: ClassSlot): String {
        val minutos = minutosDe(slot.hora)
        val duracion = when {
            minutos <= 0 -> slot.hora
            minutos % 60 == 0 -> "${minutos / 60} h"
            else -> "$minutos min"
        }
        return if (slot.aula.isBlank()) duracion else "$duracion · ${slot.aula}"
    }

    private fun minutosDe(hora: String): Int {
        val partes = RANGO_HORA.findAll(hora).map { it.value }.toList()
        if (partes.size < 2) return 0
        val inicio = aMinutos(partes[0])
        val fin = aMinutos(partes[1])
        return (fin - inicio).coerceAtLeast(0)
    }

    private fun aMinutos(hora: String): Int {
        val (h, m) = hora.split(":").map { it.toIntOrNull() ?: 0 }
        return h * 60 + m
    }
}
