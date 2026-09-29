package com.fundacion.enid.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * El formulario de preinscripción pide fechas en dd/mm/aaaa. Estos casos son los
 * que un teclado libre deja pasar y que romperían el registro: "31/02", el 29 de
 * febrero de un año que no es bisiesto, o una edad calculada un día antes del
 * cumpleaños.
 */
class FormatoEntradaTest {

    private fun dia(anio: Int, mes: Int, dia: Int): Calendar =
        Calendar.getInstance().apply {
            clear()
            set(anio, mes - 1, dia)
        }

    // ── Orden cronológico ──────────────────────────────────────────────
    @Test
    fun `no trata como anterior una expedicion de 1994 frente a un nacimiento de 1972`() {
        // Compararlas como texto daba verdadero porque el día va primero.
        assertFalse(FormatoEntrada.esAnterior("08/07/1994", "14/03/1972"))
    }

    @Test
    fun `compara por año antes que por día aunque el texto diga lo contrario`() {
        assertTrue(FormatoEntrada.esAnterior("31/12/1999", "01/01/2000"))
        // 2 de enero SÍ es anterior a 1 de febrero, aunque como texto "02/01/2000"
        // parezca mayor que "01/02/2000".
        assertTrue(FormatoEntrada.esAnterior("02/01/2000", "01/02/2000"))
        assertFalse(FormatoEntrada.esAnterior("01/02/2000", "02/01/2000"))
    }

    @Test
    fun `el mismo dia no es anterior a si mismo`() {
        assertFalse(FormatoEntrada.esAnterior("14/03/1972", "14/03/1972"))
    }

    // ── Máscara ────────────────────────────────────────────────────────
    @Test
    fun `la mascara coloca la primera barra al pasar de dos digitos`() {
        assertEquals("14", FormatoEntrada.enmascaraFecha("14"))
        // Con dos dígitos todavía no hay barra: aparece al teclear el tercero.
        assertEquals("14", FormatoEntrada.enmascaraFecha("14/"))
        assertEquals("14/0", FormatoEntrada.enmascaraFecha("140"))
    }

    @Test
    fun `la mascara coloca la segunda barra al pasar de cuatro digitos`() {
        assertEquals("14/03", FormatoEntrada.enmascaraFecha("1403"))
        assertEquals("14/03/1", FormatoEntrada.enmascaraFecha("14031"))
    }

    @Test
    fun `la mascara acepta un texto con guiones y lo deja con barras`() {
        assertEquals("14/03/1972", FormatoEntrada.enmascaraFecha("14-03-1972"))
    }

    @Test
    fun `la mascara no deja pasar mas de ocho digitos`() {
        assertEquals("14/03/1972", FormatoEntrada.enmascaraFecha("14/03/19721234"))
    }

    @Test
    fun `borrar un digito vuelve a enmascalar el resto`() {
        // Si se borra el 3 de "14/03/1972" el campo se reescribe y no queda
        // una fecha con la barra colgando.
        assertEquals("14/03", FormatoEntrada.enmascaraFecha("14/03"))
        assertEquals("14", FormatoEntrada.enmascaraFecha("14/"))
        assertEquals("1", FormatoEntrada.enmascaraFecha("1"))
        assertEquals("", FormatoEntrada.enmascaraFecha(""))
    }

    // ── Fechas válidas ──────────────────────────────────────────────────
    @Test
    fun `acepta una fecha que existe`() {
        assertTrue(FormatoEntrada.esFechaValida("14/03/1972"))
        assertTrue(FormatoEntrada.esFechaValida("29/02/2024"))
        assertTrue(FormatoEntrada.esFechaValida("31/12/1999"))
    }

    // ── Fechas que NO existen ───────────────────────────────────────────
    @Test
    fun `rechaza el treinta y uno de febrero`() {
        assertFalse(FormatoEntrada.esFechaValida("31/02/2001"))
    }

    @Test
    fun `rechaza el veintinueve de febrero en un ano que no es bisiesto`() {
        assertFalse(FormatoEntrada.esFechaValida("29/02/1900"))
        assertFalse(FormatoEntrada.esFechaValida("29/02/2023"))
    }

    @Test
    fun `acepta el veintinueve de febrero cada cuatro anos, incluso en los siglos`() {
        assertTrue(FormatoEntrada.esFechaValida("29/02/2000"))
        assertTrue(FormatoEntrada.esFechaValida("29/02/2400"))
    }

    @Test
    fun `rechaza meses y dias fuera de rango`() {
        assertFalse(FormatoEntrada.esFechaValida("14/13/1972"))
        assertFalse(FormatoEntrada.esFechaValida("00/03/1972"))
        assertFalse(FormatoEntrada.esFechaValida("32/01/1972"))
        assertFalse(FormatoEntrada.esFechaValida("01/00/1972"))
    }

    @Test
    fun `rechaza un formato distinto de dd por mm por aaaa`() {
        assertFalse(FormatoEntrada.esFechaValida("1972-03-14"))
        assertFalse(FormatoEntrada.esFechaValida("1/3/72"))
        assertFalse(FormatoEntrada.esFechaValida("14-03-1972"))
        assertFalse(FormatoEntrada.esFechaValida(""))
        assertFalse(FormatoEntrada.esFechaValida("14/03/72"))
    }

    @Test
    fun `rechaza un anio anterior a 1900`() {
        assertFalse(FormatoEntrada.esFechaValida("14/03/1899"))
    }

    // ── Edad ───────────────────────────────────────────────────────────
    @Test
    fun `la edad cuenta los anos cumplidos, no los que faltan`() {
        // El 14/03/1972 ya cumplio anos el dia de su cumpleanos.
        assertEquals(53, FormatoEntrada.edadCumplida("14/03/1972", dia(2025, 3, 14)))
    }

    @Test
    fun `un dia antes del cumpleaños todavia no suma el ano`() {
        assertEquals(52, FormatoEntrada.edadCumplida("14/03/1972", dia(2025, 3, 13)))
    }

    @Test
    fun `la edad distingue un menor de edad de un mayor de edad`() {
        // Nacido en 2010: 15 anios despues, todavia menor en cualquier fecha de 2025.
        assertEquals(14, FormatoEntrada.edadCumplida("20/06/2010", dia(2025, 1, 1)))
        assertEquals(15, FormatoEntrada.edadCumplida("20/06/2010", dia(2025, 7, 1)))
        assertEquals(18, FormatoEntrada.edadCumplida("01/01/2007", dia(2025, 6, 1)))
    }

    @Test
    fun `la edad es nula si la fecha no es valida`() {
        assertNull(FormatoEntrada.edadCumplida("31/02/2001", dia(2025, 1, 1)))
        assertNull(FormatoEntrada.edadCumplida("", dia(2025, 1, 1)))
        assertNull(FormatoEntrada.edadCumplida("14/3/1972", dia(2025, 1, 1)))
    }

    @Test
    fun `la edad no sale de un rango creible`() {
        // Una edad de 130 anos o de 0 no sirven para decidir si es menor de edad.
        assertNull(FormatoEntrada.edadCumplida("01/01/1850", dia(2025, 1, 1)))
    }

    // ── Solo dígitos ───────────────────────────────────────────────────
    @Test
    fun `soloDigitos quita puntos guiones y espacios y respeta el tope`() {
        assertEquals("1025123456", FormatoEntrada.soloDigitos("1.025.123.456", 12))
        assertEquals("102551", FormatoEntrada.soloDigitos("1-025-512-3456", 6))
        assertEquals("312", FormatoEntrada.soloDigitos("312 456 7890", 3))
        assertEquals("", FormatoEntrada.soloDigitos("abc", 10))
    }

    // ── Correo ─────────────────────────────────────────────────────────
    @Test
    fun `acepta un correo con arroba y punto en el dominio`() {
        assertTrue(FormatoEntrada.esCorreoValido("maria.rodriguez@correo.com"))
        assertTrue(FormatoEntrada.esCorreoValido("  maria@enid.edu.co  "))
    }

    @Test
    fun `rechaza un correo sin arroba, sin dominio o con dos arrobas`() {
        assertFalse(FormatoEntrada.esCorreoValido("maria.rodriguez"))
        assertFalse(FormatoEntrada.esCorreoValido("maria@"))
        assertFalse(FormatoEntrada.esCorreoValido("@correo.com"))
        assertFalse(FormatoEntrada.esCorreoValido("maria@correo"))
        assertFalse(FormatoEntrada.esCorreoValido("maria@@correo.com"))
        assertFalse(FormatoEntrada.esCorreoValido(""))
    }
}
