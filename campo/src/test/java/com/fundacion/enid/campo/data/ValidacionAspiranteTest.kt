package com.fundacion.enid.campo.data

import com.fundacion.enid.campo.data.validation.BorradorAspirante
import com.fundacion.enid.campo.data.validation.Campo
import com.fundacion.enid.campo.data.validation.ValidadorAspirante
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * TESTS DE LAS REGLAS R1..R8
 * ---------------------------------------------------------------------------
 * Son los casos del taller escritos como afirmacion verificable. La clase que
 * se prueba es Kotlin puro, sin Android, asi que corre en la JVM en milisegundos.
 */
class ValidacionAspiranteTest {

    /**
     * Edad cumplida hoy para una fecha "dd/mm/aaaa".
     *
     * Los tests NO fijan la edad a mano: la calculan igual que la calcula una
     * persona. Hacerlo al reves (edad constante mas fecha constante) deja que el
     * suite se Pudre en silencio: la prueba sigue en verde mientras el validador
     * calcula mal, porque ambos repiten el mismo error. Asi, cuando el reloj pasa
     * de un dia, los tests siguen describiendo la realidad de campo.
     */
    private fun edadCumplida(fecha: String): String {
        val (d, m, a) = fecha.split("/").map { it.toInt() }
        val hoy = Calendar.getInstance()
        var edad = hoy.get(Calendar.YEAR) - a
        val mesHoy = hoy.get(Calendar.MONTH) + 1
        if (mesHoy < m || (mesHoy == m && hoy.get(Calendar.DAY_OF_MONTH) < d)) edad--
        return edad.toString()
    }

    /** Borrador que pasa las ocho reglas: partiendo de aqui se rompe UNA. */
    private fun borradorValido() = BorradorAspirante(
        nombreCompleto = "Ana Maria Gomez",
        tipoDocumento = "CC",
        documentoIdentidad = "1098765432",
        fechaNacimiento = "15/04/2004",
        edad = edadCumplida("15/04/2004"),
        telefono = "3001234567",
        correoElectronico = "ana.gomez@correo.com",
        municipioResidencia = "Chía",
        municipioCaptura = "Cota",
        jornadaCaptura = "Mañana",
        programaInteres = "T-ANA",
        medioConocimiento = "Recomendado por un amigo o familiar",
        condicionesEconomicas = "Estrato 3, requiere beca",
        consentimientoDatos = true,
        rutaEvidencia = "content://prueba/foto.jpg"
    )

    // --------------------------------------------------- ESTADO INICIAL ---

    @Test
    fun `el borrador valido no produce ningun error`() {
        val resultado = ValidadorAspirante.validar(borradorValido())
        assertTrue(
            "No deberia haber errores, pero hubo: ${resultado.erroresPorCampo}",
            resultado.esValido
        )
        assertNull(resultado.primerCampoConError)
    }

    @Test
    fun `el borrador vacio falla en todos los campos obligatorios`() {
        val resultado = ValidadorAspirante.validar(BorradorAspirante())
        assertFalse(resultado.esValido)
        // El primer error debe ser el primer campo del formulario, no uno arbitrario.
        assertEquals(Campo.NOMBRE_COMPLETO, resultado.primerCampoConError)
        assertTrue(resultado.tieneErrorEn(Campo.CONSENTIMIENTO_DATOS))
        assertTrue(resultado.tieneErrorEn(Campo.EVIDENCIA_FOTOGRAFICA))
    }

    // --------------------------------------------------------------- R1 ---

    @Test
    fun `R1 el telefono debe tener 10 digitos`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(telefono = "300123456"))
        assertNotNull(resultado.mensajeDe(Campo.TELEFONO))
    }

    @Test
    fun `R1 el telefono debe empezar en 3`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(telefono = "2001234567"))
        assertNotNull(resultado.mensajeDe(Campo.TELEFONO))
    }

    @Test
    fun `R1 un telefono valido de 10 digitos que empieza en 3 se acepta`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(telefono = "3112223344"))
        assertNull(resultado.mensajeDe(Campo.TELEFONO))
    }

    // --------------------------------------------------------------- R2 ---

    @Test
    fun `R2 se rechazan los correos mal formados`() {
        listOf("ana@correo", "ana@@correo.com", "ana corre@x.com", "ana@correo com", "@correo.com")
            .forEach { correo ->
                val resultado = ValidadorAspirante.validar(
                    borradorValido().copy(correoElectronico = correo)
                )
                assertNotNull("Deberia rechazar '$correo'", resultado.mensajeDe(Campo.CORREO_ELECTRONICO))
            }
    }

    // --------------------------------------------------------------- R3 ---

    @Test
    fun `R3 la edad debe estar entre 15 y 100`() {
        listOf("14", "101", "0", "-5").forEach { edad ->
            val resultado = ValidadorAspirante.validar(borradorValido().copy(edad = edad))
            assertNotNull("Deberia rechazar la edad $edad", resultado.mensajeDe(Campo.EDAD))
        }
    }

    @Test
    fun `R3 los extremos de la edad se aceptan`() {
        listOf("15", "100").forEach { edad ->
            val resultado = ValidadorAspirante.validar(borradorValido().copy(edad = edad))
            assertNull("Deberia aceptar la edad $edad", resultado.mensajeDe(Campo.EDAD))
        }
    }

    // --------------------------------------------------------------- R4 ---

    @Test
    fun `R4 el documento debe tener exactamente 10 digitos`() {
        listOf("109876543", "10987654321", "109876543a").forEach { doc ->
            val resultado = ValidadorAspirante.validar(borradorValido().copy(documentoIdentidad = doc))
            assertNotNull("Deberia rechazar '$doc'", resultado.mensajeDe(Campo.DOCUMENTO_IDENTIDAD))
        }
    }

    @Test
    fun `R4 el tipo de documento debe pertenecer al catalogo`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(tipoDocumento = "XX"))
        assertNotNull(resultado.mensajeDe(Campo.TIPO_DOCUMENTO))
    }

    // --------------------------------------------------------------- R5 ---

    @Test
    fun `R5 el nombre debe tener entre 2 y 4 palabras`() {
        listOf("Ana", "Ana Maria Gomez Restrepo Perez").forEach { nombre ->
            val resultado = ValidadorAspirante.validar(borradorValido().copy(nombreCompleto = nombre))
            assertNotNull("Deberia rechazar '$nombre'", resultado.mensajeDe(Campo.NOMBRE_COMPLETO))
        }
    }

    @Test
    fun `R5 se rechazan los nombres con numeros`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(nombreCompleto = "Ana Maria123"))
        assertNotNull(resultado.mensajeDe(Campo.NOMBRE_COMPLETO))
    }

    @Test
    fun `R5 cada palabra debe tener al menos 3 letras`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(nombreCompleto = "Ana Maria Li"))
        assertNotNull(resultado.mensajeDe(Campo.NOMBRE_COMPLETO))
    }

    // --------------------------------------------------------------- R6 ---

    @Test
    fun `R6 el programa debe existir en el catalogo`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(programaInteres = "T-INVENTADO"))
        assertNotNull(resultado.mensajeDe(Campo.PROGRAMA_INTERES))
    }

    @Test
    fun `R6 un programa real del catalogo se acepta`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(programaInteres = "T-AGU"))
        assertNull(resultado.mensajeDe(Campo.PROGRAMA_INTERES))
    }

    // --------------------------------------------------------------- R7 ---

    @Test
    fun `R7 los municipios deben estar dentro de la cobertura ENID`() {
        val resultado = ValidadorAspirante.validar(
            borradorValido().copy(municipioResidencia = "Medellin")
        )
        assertNotNull(resultado.mensajeDe(Campo.MUNICIPIO_RESIDENCIA))
    }

    @Test
    fun `R7 el municipio de captura tambien se valida`() {
        val resultado = ValidadorAspirante.validar(
            borradorValido().copy(municipioCaptura = "Bogota")
        )
        assertNotNull(resultado.mensajeDe(Campo.MUNICIPIO_CAPTURA))
    }

    // --------------------------------------------------------------- R8 ---

    @Test
    fun `R8 la fecha de nacimiento debe coincidir con la edad escrita`() {
        // Se escribe 40 sobre una fecha que no puede dar 40: debe rechazarse.
        val resultado = ValidadorAspirante.validar(borradorValido().copy(edad = "40"))
        assertNotNull(resultado.mensajeDe(Campo.FECHA_NACIMIENTO))
    }

    @Test
    fun `R8 no se admite fecha de nacimiento futura`() {
        val resultado = ValidadorAspirante.validar(
            borradorValido().copy(fechaNacimiento = "15/04/2030", edad = "0")
        )
        assertNotNull(resultado.mensajeDe(Campo.FECHA_NACIMIENTO))
    }

    @Test
    fun `R8 la fecha debe tener el formato dd mm aaaa`() {
        val resultado = ValidadorAspirante.validar(
            borradorValido().copy(fechaNacimiento = "2004-04-15")
        )
        assertNotNull(resultado.mensajeDe(Campo.FECHA_NACIMIENTO))
    }

    // ---- R8 y los cumpleaños: la trampa del dia-del-año ---------------------
    // Estos tres casosfallen con la version que armaba el cumpleaños usando el
    // dia y el mes de HOY en vez de los de nacimiento.

    @Test
    fun `R8 acepta la edad cuando el cumpleaños ya paso este ano`() {
        val hoy = Calendar.getInstance()
        val fecha = "01/01/${hoy.get(Calendar.YEAR) - 22}"
        val resultado = ValidadorAspirante.validar(
            borradorValido().copy(fechaNacimiento = fecha, edad = "22")
        )
        assertNull(
            "No debe exigir N-1 anos solo porque el dia actual es posterior al dia del dia del cumpleanos",
            resultado.mensajeDe(Campo.FECHA_NACIMIENTO)
        )
    }

    @Test
    fun `R8 acepta la edad cuando el cumpleaños aun no ocurre este ano`() {
        val hoy = Calendar.getInstance()
        val fecha = "31/12/${hoy.get(Calendar.YEAR) - 22}"
        val resultado = ValidadorAspirante.validar(
            borradorValido().copy(fechaNacimiento = fecha, edad = "21")
        )
        assertNull(
            "Todavia no ha cumplido 22: la edad correcta es 21",
            resultado.mensajeDe(Campo.FECHA_NACIMIENTO)
        )
    }

    @Test
    fun `R8 acepta la edad el mismo dia del cumpleaños`() {
        val hoy = Calendar.getInstance()
        val fecha = "%02d/%02d/%d".format(
            hoy.get(Calendar.DAY_OF_MONTH),
            hoy.get(Calendar.MONTH) + 1,
            hoy.get(Calendar.YEAR) - 22
        )
        val resultado = ValidadorAspirante.validar(
            borradorValido().copy(fechaNacimiento = fecha, edad = "22")
        )
        assertNull(
            "El dia del cumpleaños ya se cumplieron los 22",
            resultado.mensajeDe(Campo.FECHA_NACIMIENTO)
        )
    }

    // ------------------------------------------------- EVIDENCIA Y CONSENTIMIENTO ---

    @Test
    fun `sin foto no se puede guardar`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(rutaEvidencia = null))
        assertNotNull(resultado.mensajeDe(Campo.EVIDENCIA_FOTOGRAFICA))
        assertFalse(resultado.esValido)
    }

    @Test
    fun `sin consentimiento de datos no se puede guardar`() {
        val resultado = ValidadorAspirante.validar(borradorValido().copy(consentimientoDatos = false))
        assertNotNull(resultado.mensajeDe(Campo.CONSENTIMIENTO_DATOS))
    }

    // ------------------------------------------------- DOCUMENTO DUPLICADO ---

    @Test
    fun `un documento ya registrado se reporta como duplicado`() {
        val resultado = ValidadorAspirante.validar(
            borradorValido(),
            documentoYaRegistrado = "1098765432"
        )
        assertNotNull(resultado.mensajeDe(Campo.DOCUMENTO_IDENTIDAD))
    }
}
