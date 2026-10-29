package com.fundacion.enid

import com.fundacion.enid.data.CursoAprendizaje
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Progreso de **Aprender**.
 *
 * La barra, el porcentaje y las estadísticas de **Perfil** salen de estas dos
 * funciones. Se prueban aquí porque un error de redondeo no rompe la compilación
 * pero sí miente en pantalla: 2 de 5 lecciones no son 39% ni 41%.
 */
class AprendizajeTest {

    @Test
    fun `el porcentaje se calcula sobre el total real`() {
        assertEquals(0, CursoAprendizaje.porcentaje(0, 5))
        assertEquals(20, CursoAprendizaje.porcentaje(1, 5))
        assertEquals(60, CursoAprendizaje.porcentaje(3, 5))
        assertEquals(100, CursoAprendizaje.porcentaje(5, 5))
    }

    @Test
    fun `el porcentaje no se sale de cero a cien`() {
        assertEquals(0, CursoAprendizaje.porcentaje(2, 0))
        assertEquals(0, CursoAprendizaje.porcentaje(-1, 5))
        assertEquals(100, CursoAprendizaje.porcentaje(9, 5))
    }

    @Test
    fun `el curso usa el programa de la cuenta y si falta uno no queda vacio`() {
        assertEquals("Analítica de Datos", CursoAprendizaje.tituloCurso("Analítica de Datos"))
        assertEquals("Soporte de Sistemas e Informática", CursoAprendizaje.tituloCurso(null))
        assertEquals("Soporte de Sistemas e Informática", CursoAprendizaje.tituloCurso("   "))
    }
}
