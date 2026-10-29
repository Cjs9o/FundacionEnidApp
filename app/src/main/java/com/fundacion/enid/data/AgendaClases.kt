package com.fundacion.enid.data

import com.fundacion.enid.model.ClassSlot
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

/** Una clase con la fecha concreta en que ocurre. */
data class ProximaClase(val fecha: LocalDate, val slot: ClassSlot)

/**
 * Cruce entre el horario semanal de la Fundación y el calendario.
 *
 * El horario guarda días de la semana ("Lunes", "Martes"…), no fechas. Tanto el
 * Inicio como la Agenda necesitan responder “¿cuál es la próxima clase?” sin
 * mostrar una clase que ya terminó. Esta lógica vive aquí una sola vez para que
 * las dos pantallas no puedan dar respuestas distintas.
 */
object AgendaClases {

    /** Clases del horario que caen en una fecha concreta. */
    fun clasesDe(repository: DataRepository, fecha: LocalDate): List<ClassSlot> =
        repository.horarioClases.filter {
            it.dia.equals(nombreDia(fecha.dayOfWeek), ignoreCase = true)
        }

    /**
     * Primera clase cuyo final todavía no pasó. Para hoy descarta las clases
     * terminadas; para los días siguientes toma la primera del día. Si en los
     * próximos ocho días no hay ninguna, devuelve `null` en vez de inventar una.
     */
    fun proxima(
        repository: DataRepository,
        desde: LocalDate = LocalDate.now(),
        ahora: LocalTime = LocalTime.now()
    ): ProximaClase? = proxima(repository.horarioClases, desde, ahora)

    /**
     * Variante sin `DataRepository` para poder probar el cálculo en JVM. La
     * pantalla siempre usa la variante de arriba.
     */
    fun proxima(
        horario: List<ClassSlot>,
        desde: LocalDate = LocalDate.now(),
        ahora: LocalTime = LocalTime.now()
    ): ProximaClase? {
        for (desplazamiento in 0L..7L) {
            val fecha = desde.plusDays(desplazamiento)
            val clases = horario.filter {
                it.dia.equals(nombreDia(fecha.dayOfWeek), ignoreCase = true)
            }
            val vigentes = if (desplazamiento == 0L) {
                clases.filter { clase -> finDe(clase.hora)?.isAfter(ahora) ?: true }
            } else {
                clases
            }
            vigentes.firstOrNull()?.let { return ProximaClase(fecha, it) }
        }
        return null
    }

    fun nombreDia(dia: DayOfWeek): String = when (dia) {
        DayOfWeek.MONDAY -> "Lunes"
        DayOfWeek.TUESDAY -> "Martes"
        DayOfWeek.WEDNESDAY -> "Miércoles"
        DayOfWeek.THURSDAY -> "Jueves"
        DayOfWeek.FRIDAY -> "Viernes"
        DayOfWeek.SATURDAY -> "Sábado"
        DayOfWeek.SUNDAY -> "Domingo"
    }

    /** Texto corto y en mayúsculas: `MIÉ 28 OCT`. */
    fun fechaCorta(fecha: LocalDate): String =
        fecha.format(java.time.format.DateTimeFormatter.ofPattern("EEE d MMM", Locale.forLanguageTag("es-CO")))
            .uppercase(Locale.forLanguageTag("es-CO"))
            .replace(".", "")

    private val RANGO_HORA = Regex("""(\d{1,2}):(\d{2})""")

    /**
     * Hora de final de un texto como `7:00 - 9:00`. Si el formato no se puede
     * leer, devuelve `null` y la clase se conserva: es mejor mostrarla de más
     * que esconder una clase real por un formato inesperado.
     */
    private fun finDe(hora: String): LocalTime? {
        val partes = RANGO_HORA.findAll(hora).toList()
        if (partes.size < 2) return null
        val (h, m) = partes[1].destructured
        return runCatching { LocalTime.of(h.toInt(), m.toInt()) }.getOrNull()
    }
}
