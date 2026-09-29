package com.fundacion.enid.data

import com.fundacion.enid.model.Familiar
import com.fundacion.enid.model.Preinscripcion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La preinscripción se guarda como una línea de texto con campos separados por
 * `|`. Estos casos cubren lo que se rompe en silencio: un nombre con `|`, un
 * familiar con comas, y las líneas viejas de 6 y de 21 campos que ya están en
 * los dispositivos de quienes usan la app.
 */
class PreinscripcionTextoTest {

    private val completa = Preinscripcion(
        nombre = "Ana Gómez",
        documento = "CC 1025123456",
        correo = "ana@ejemplo.com",
        telefono = "+57 3124567890",
        programa = "Control y Manejo Ambiental",
        fecha = "29 sep. 2026",
        tipoDocumento = "CC",
        prefijoTelefono = "+57",
        fechaNacimiento = "14/03/1972",
        fechaExpedicion = "08/07/1994",
        nivelFormacion = "Bachillerato completo",
        icfes = "99052345",
        documentos = listOf("DOCUMENTO", "TITULOS", "ICFES"),
        estadoCivil = "Soltero/a",
        esMenorDeEdad = false,
        tieneAcudiente = false,
        nombreAcudiente = "",
        telefonoEmergencia = "3105554433",
        prefijoEmergencia = "+57",
        condicionEspecial = "",
        familiar = Familiar(
            nombre = "Luis Fernando Gómez",
            vinculo = "Hijo",
            tipoDocumento = "CC",
            numeroDocumento = "79876543",
            telefono = "+57 3204441122",
            correo = "luis@ejemplo.com"
        )
    )

    @Test
    fun `una preinscripcion completa vuelve igual`() {
        val leida = PreinscripcionTexto.leer(PreinscripcionTexto.serializar(completa))
        assertEquals(completa, leida)
    }

    @Test
    fun `un familiar con comas en el nombre no se pierde`() {
        // El formato antiguo unía los seis campos del familiar con comas, así que
        // esta fila se descartaba al leerla.
        val conComas = completa.copy(
            familiar = completa.familiar!!.copy(nombre = "Gómez, Luis Fernando")
        )
        val leida = PreinscripcionTexto.leer(PreinscripcionTexto.serializar(conComas))
        assertNotNull(leida?.familiar)
        assertEquals("Gómez, Luis Fernando", leida?.familiar?.nombre)
        assertEquals("luis@ejemplo.com", leida?.familiar?.correo)
    }

    @Test
    fun `un nombre con barra vertical no desalinea los campos`() {
        val conBarra = completa.copy(nombre = "Ana | Gómez")
        val leida = PreinscripcionTexto.leer(PreinscripcionTexto.serializar(conBarra))
        assertEquals("Ana | Gómez", leida?.nombre)
        assertEquals("ana@ejemplo.com", leida?.correo)
        assertEquals("14/03/1972", leida?.fechaNacimiento)
        assertEquals("CC", leida?.tipoDocumento)
    }

    @Test
    fun `un salto de linea no desalinea los campos`() {
        val conSalto = completa.copy(condicionEspecial = "Silla de ruedas\nnecesita apoyo")
        val leida = PreinscripcionTexto.leer(PreinscripcionTexto.serializar(conSalto))
        assertEquals("Silla de ruedas necesita apoyo", leida?.condicionEspecial)
    }

    @Test
    fun `la lista de documentos se conserva completa`() {
        val leida = PreinscripcionTexto.leer(PreinscripcionTexto.serializar(completa))
        assertEquals(listOf("DOCUMENTO", "TITULOS", "ICFES"), leida?.documentos)
    }

    // ── Compatibilidad con lo ya guardado ──────────────────────────────
    @Test
    fun `lee una preinscripcion vieja de seis campos`() {
        val vieja = "Ana Perez|3001234567|ana@ejemplo.com|+57 3001234567|Control y Manejo Ambiental|01 ago. 2026"
        val leida = PreinscripcionTexto.leer(vieja)
        assertNotNull(leida)
        assertEquals("Ana Perez", leida?.nombre)
        assertEquals("Control y Manejo Ambiental", leida?.programa)
        // Los campos que no existían quedan vacíos, no inventados.
        assertEquals("", leida?.tipoDocumento)
        assertEquals("", leida?.fechaNacimiento)
        assertTrue(leida?.documentos?.isEmpty() == true)
        assertNull(leida?.esMenorDeEdad)
        assertNull(leida?.familiar)
    }

    @Test
    fun `lee una preinscripcion de 21 campos con el familiar unido por comas`() {
        // Formato intermedio, antes de pasar el familiar a JSON.
        val conFamiliarAntiguo = listOf(
            "Ana Perez", "CC 1025", "ana@ejemplo.com", "+57 3001234567",
            "Control y Manejo Ambiental", "01 ago. 2026",
            "CC", "+57", "14/03/1972", "08/07/1994",
            "Bachillerato completo", "99052345",
            "DOCUMENTO,ICFES", "Soltero/a", "false", "false",
            "", "+57", "3105554433", "",
            "Luis Gomez,Hijo,CC,79876543,+57 3204441122,luis@ejemplo.com"
        ).joinToString("|")
        val leida = PreinscripcionTexto.leer(conFamiliarAntiguo)
        assertEquals("DOCUMENTO", leida?.documentos?.first())
        assertEquals("ICFES", leida?.documentos?.get(1))
        assertEquals("Luis Gomez", leida?.familiar?.nombre)
        assertEquals("Hijo", leida?.familiar?.vinculo)
        assertEquals("luis@ejemplo.com", leida?.familiar?.correo)
    }

    @Test
    fun `una linea con menos de seis campos no es una preinscripcion`() {
        assertNull(PreinscripcionTexto.leer("Ana Perez|CC 1025|ana@ejemplo.com"))
        assertNull(PreinscripcionTexto.leer(""))
    }

    @Test
    fun `una preinscripcion sin familiar deja el campo vacio`() {
        val sola = completa.copy(familiar = null)
        val leida = PreinscripcionTexto.leer(PreinscripcionTexto.serializar(sola))
        assertNull(leida?.familiar)
        assertEquals("Ana Gómez", leida?.nombre)
    }

    @Test
    fun `un menor de edad guarda el acudiente y el estado civil vacio`() {
        val menor = completa.copy(
            esMenorDeEdad = true,
            tieneAcudiente = true,
            nombreAcudiente = "María Pérez",
            estadoCivil = "",
            fechaNacimiento = "20/06/2010"
        )
        val leida = PreinscripcionTexto.leer(PreinscripcionTexto.serializar(menor))
        assertEquals(true, leida?.esMenorDeEdad)
        assertEquals(true, leida?.tieneAcudiente)
        assertEquals("María Pérez", leida?.nombreAcudiente)
        assertEquals("", leida?.estadoCivil)
    }

    // ── Orden de los registros ────────────────────────────────────────
    @Test
    fun `las lineas se pueden unir y separar sin perder el orden`() {
        // Así se guardan ahora: un solo texto con saltos de línea, para que
        // "Mis interesados" muestre la preinscripción más reciente y no una al azar.
        val primera = PreinscripcionTexto.serializar(completa)
        val segunda = PreinscripcionTexto.serializar(completa.copy(nombre = "Pedro Ruiz"))
        val guardadas = listOf(primera, segunda).joinToString("\n")

        val leidas = guardadas.split("\n").mapNotNull(PreinscripcionTexto::leer)
        assertEquals(2, leidas.size)
        assertEquals("Ana Gómez", leidas[0].nombre)
        assertEquals("Pedro Ruiz", leidas[1].nombre)
    }

    @Test
    fun `un nombre con salto de linea no parte el registro en dos`() {
        // Si un texto trajera un salto, al unir con "\n" parecería otro registro.
        val conSalto = completa.copy(nombre = "Ana\nGómez")
        val guardada = PreinscripcionTexto.serializar(conSalto)
        assertEquals(1, guardada.split("\n").size)
        assertEquals("Ana Gómez", PreinscripcionTexto.leer(guardada)?.nombre)
    }
}
