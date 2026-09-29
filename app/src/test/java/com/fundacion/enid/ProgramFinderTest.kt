package com.fundacion.enid

import com.fundacion.enid.data.FinderOpcion
import com.fundacion.enid.data.ProgramFinder
import com.fundacion.enid.model.Area
import com.fundacion.enid.model.Program
import com.fundacion.enid.model.TipoPrograma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgramFinderTest {

    private fun programa(
        id: Int,
        nombre: String,
        area: Area,
        tipo: TipoPrograma = TipoPrograma.TECNICO,
        campoLaboral: List<String> = emptyList()
    ) = Program(
        id = id,
        nombre = nombre,
        tipo = tipo,
        area = area,
        descripcion = "desc",
        duracion = "6 meses",
        modalidad = "Presencial",
        competencias = emptyList(),
        perfilOcupacional = "perfil",
        sedes = listOf("Chía"),
        campoLaboral = campoLaboral
    )

    private val catalogo = listOf(
        programa(1, "Sistemas y Soporte Técnico", Area.SISTEMAS),
        programa(2, "Diseño Gráfico", Area.ARTE),
        programa(3, "Turismo", Area.TURISMO),
        programa(4, "Diplomado ambiental", Area.AMBIENTE, TipoPrograma.DIPLOMADO),
        programa(5, "Contable", Area.ADMINISTRACION),
        programa(6, "Community Manager", Area.MARKETING)
    )

    @Test
    fun `el interest en tecnologia ubica primero un programa de sistemas`() {
        val respuestas = listOf(FinderOpcion("x", mapOf(Area.SISTEMAS to 3)))
        val resultado = ProgramFinder.recomendar(catalogo, respuestas, limite = 2)
        assertEquals("Sistemas y Soporte Técnico", resultado.first().nombre)
    }

    @Test
    fun `una respuesta neutra devuelve los tecnicos como base`() {
        val respuestas = listOf(FinderOpcion("Todavia no lo se", tipos = TipoPrograma.entries.associateWith { 1 }))
        val resultado = ProgramFinder.recomendar(catalogo, respuestas, limite = 3)
        assertTrue(resultado.isNotEmpty())
        assertTrue(resultado.all { it.tipo == TipoPrograma.TECNICO })
    }

    @Test
    fun `el filtro por campo laboral suma a programas del sector elegido`() {
        val respuestas = listOf(
            FinderOpcion("turismo", campoClaves = listOf("hotel", "turismo"))
        )
        val catalogoTurismo = catalogo + listOf(
            programa(7, "Hosteleria", Area.OPERATIVOS, campoLaboral = listOf("Hoteles y hostels"))
        )
        val resultado = ProgramFinder.recomendar(catalogoTurismo, respuestas, limite = 1)
        assertEquals("Hosteleria", resultado.first().nombre)
    }

    @Test
    fun `sin respuestas devuelve los tecnicos`() {
        val resultado = ProgramFinder.recomendar(catalogo, emptyList(), limite = 3)
        assertTrue(resultado.all { it.tipo == TipoPrograma.TECNICO })
    }

    @Test
    fun `el cuestionario tiene cinco preguntas con opciones`() {
        assertEquals(5, ProgramFinder.preguntas.size)
        assertTrue(ProgramFinder.preguntas.all { it.opciones.size >= 4 })
    }
}
