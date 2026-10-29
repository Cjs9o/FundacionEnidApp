package com.fundacion.enid

import com.fundacion.enid.data.AgendaClases
import com.fundacion.enid.model.ClassSlot
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * “Próxima clase” tiene que ser la próxima de verdad.
 *
 * Antes la Agenda mostraba la primera clase del día elegido aunque ya hubiera
 * terminado: a las 3 p. m. llamaba “próxima” a la clase de las 7 a. m. Estas
 * pruebas fijan el comportamiento correcto.
 */
class AgendaClasesTest {

    private val lunes = LocalDate.of(2026, 10, 26)

    private fun clase(dia: String, hora: String, asignatura: String = "Clase") =
        ClassSlot(dia = dia, hora = hora, asignatura = asignatura, docente = "Docente", aula = "Aula 1")

    @Test
    fun `una clase de hoy ya terminada no es la proxima`() {
        val horario = listOf(
            clase("Lunes", "7:00 - 9:00", "Matemáticas"),
            clase("Martes", "7:00 - 9:00", "Ofimática")
        )
        val proxima = AgendaClases.proxima(horario, lunes, LocalTime.of(15, 0))
        assertEquals("Ofimática", proxima?.slot?.asignatura)
        assertEquals(lunes.plusDays(1), proxima?.fecha)
    }

    @Test
    fun `una clase de hoy que aun no termina si es la proxima`() {
        val horario = listOf(clase("Lunes", "15:00 - 17:00", "Redes"))
        val proxima = AgendaClases.proxima(horario, lunes, LocalTime.of(15, 30))
        assertEquals("Redes", proxima?.slot?.asignatura)
        assertEquals(lunes, proxima?.fecha)
    }

    @Test
    fun `una clase que empieza justo ahora todavia cuenta`() {
        val horario = listOf(clase("Lunes", "15:00 - 17:00", "Redes"))
        val proxima = AgendaClases.proxima(horario, lunes, LocalTime.of(15, 0))
        assertEquals("Redes", proxima?.slot?.asignatura)
    }

    @Test
    fun `sin clases en el horario no se inventa una`() {
        assertNull(AgendaClases.proxima(emptyList(), lunes, LocalTime.of(9, 0)))
    }

    @Test
    fun `si solo hay clase semanal se ofrece la de la semana siguiente`() {
        val horario = listOf(clase("Lunes", "7:00 - 9:00", "Matemáticas"))
        val proxima = AgendaClases.proxima(horario, lunes, LocalTime.of(10, 0))
        assertEquals(lunes.plusDays(7), proxima?.fecha)
        assertEquals("Matemáticas", proxima?.slot?.asignatura)
    }
}
