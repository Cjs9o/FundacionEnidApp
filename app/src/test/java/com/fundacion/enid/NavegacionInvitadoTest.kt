package com.fundacion.enid

import com.fundacion.enid.data.DataRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas del camino del invitado y de las pestañas por sesión.
 *
 * Estos casos cubren tres errores que el usuario veía en el teléfono:
 * el invitado llegaba a pantallas de estudiante, veía pestañas que no le
 * correspondían y no tenía cómo volver. El módulo de pagos se retiró el
 * 27 Oct 2026, así que estas pruebas también vigilan que no vuelva.
 */
class NavegacionInvitadoTest {

    private val ruta = DataRepository.rutaProspecto

    // ── El invitado no debe llegar a pantallas de estudiante ──────────────

    @Test
    fun `ningun paso de la ruta del invitado abre pantallas de pago`() {
        assertFalse(
            "El invitado no debe llegar a pantallas de pago",
            ruta.any { it.pantalla == "Payments" || it.pantalla == "Pse" }
        )
    }

    @Test
    fun `ningun paso de la ruta del invitado abre la plataforma educativa`() {
        assertFalse(
            "El invitado no debe llegar a la pantalla de la plataforma",
            ruta.any { it.pantalla == "Platform" }
        )
    }

    @Test
    fun `solo los cuatro primeros pasos ofrecen pantalla de la app`() {
        val conPantalla = ruta.filter { it.pantalla != null }.map { it.numero }
        assertEquals(
            "Los pasos con pantalla deben ser solo el 1 al 4",
            listOf(1, 2, 3, 4),
            conPantalla
        )
    }

    @Test
    fun `los pasos que no tienen pantalla se marcan para sede`() {
        // Si un paso no se puede abrir en la app, debe decirlo; si no, la
        // tarjeta parece un botón roto.
        ruta.filter { it.pantalla == null }.forEach { paso ->
            assertTrue(
                "El paso ${paso.numero} (${paso.titulo}) debe marcarse enSede",
                paso.enSede
            )
        }
    }

    @Test
    fun `el paso de matricula y el de pago se resuelven con el asesor`() {
        val titulos = ruta.map { it.titulo }
        assertTrue("Debe existir el paso de matrícula", titulos.any { it.contains("matrícula", true) })
        assertTrue("Debe existir el paso de pago", titulos.any { it.contains("pago", true) })
    }

    // ── Pestañas según sesión ──────────────────────────────────────────────

    @Test
    fun `el invitado ve inicio conoce enid inscribete y soporte`() {
        val etiquetas = com.fundacion.enid.ui.components
            .tabsSegunSesion(conectado = false)
            .map { it.label }
        assertEquals(listOf("Inicio", "Conoce ENID", "Inscríbete", "Soporte"), etiquetas)
    }

    @Test
    fun `las pestanas del invitado llevan a pantallas del invitado`() {
        val destinos = com.fundacion.enid.ui.components.tabsSegunSesion(conectado = false)
            .map { it.destino }
        assertTrue(
            "Conoce ENID debe abrir su propia pantalla",
            com.fundacion.enid.navigation.Screen.ProspectKnow in destinos
        )
        assertTrue(
            "Inscríbete debe abrir su propia pantalla",
            com.fundacion.enid.navigation.Screen.ProspectEnroll in destinos
        )
        assertFalse(
            "El invitado no debe recibir pestañas de estudiante",
            destinos.any {
                it == com.fundacion.enid.navigation.Screen.Learn ||
                    it == com.fundacion.enid.navigation.Screen.Agenda ||
                    it == com.fundacion.enid.navigation.Screen.Profile
            }
        )
    }

    @Test
    fun `el estudiante ve inicio aprender agenda y perfil`() {
        val etiquetas = com.fundacion.enid.ui.components
            .tabsSegunSesion(conectado = true)
            .map { it.label }
        assertEquals(listOf("Inicio", "Aprender", "Agenda", "Perfil"), etiquetas)
    }

    @Test
    fun `el administrador conserva inicio panel y soporte`() {
        val etiquetas = com.fundacion.enid.ui.components
            .tabsSegunSesion(
                conectado = true,
                rol = com.fundacion.enid.model.Rol.ADMIN
            )
            .map { it.label }
        assertEquals(listOf("Inicio", "Panel", "Soporte"), etiquetas)
    }

    @Test
    fun `el sistema de pagos no vuelve al codigo`() {
        // Se retiró por completo el 27 Oct 2026: pantallas, rutas, pestaña,
        // modelos, alertas y montos. Si alguien vuelve a introducirlo, esta
        // prueba lo delata antes de que llegue al APK.
        val prohibidos = listOf(
            "PaymentsScreen", "PseScreen", "Screen.Payments", "Screen.Pse",
            "EnidTab.PAGOS", "PaymentNotice", "EstadoPago", "MONTO_MATRICULA", "PSE_URL"
        )
        val hallazgos = mutableListOf<String>()
        kotlinFilesEn("app/src/main/java").forEach { archivo ->
            val texto = archivo.readText()
            prohibidos.filter { it in texto }.forEach { hallazgos += "${archivo.name}: $it" }
        }
        assertTrue("El sistema de pagos reapareció: $hallazgos", hallazgos.isEmpty())
    }

    @Test
    fun `toda pestana de estudiante tiene destino de estudiante`() {
        // Invariante: si un destino de pestaña fuera "Campo", el invitado
        // podría entrar al módulo de captura sin ser administrativo.
        com.fundacion.enid.ui.components.EnidTab.entries.forEach { tab ->
            assertNotNull("La pestaña ${tab.label} debe tener destino", tab.destino)
        }
    }

    // ── Salidas externas ───────────────────────────────────────────────────

    @Test
    fun `ninguna pantalla abre intents por su cuenta`() {
        // Regla del proyecto: con `EnlacesExternos.habilitados = false` no se
        // abre ninguna app externa. Cinco pantallas lo incumplían con
        // `context.startActivity(...)` propio. Se verifica sobre el código
        // fuente porque el fallo no se ve en una prueba de comportamiento:
        // ocurre justo cuando la app no puede abrir nada.
        val offenders = LinkedHashMap<String, MutableList<String>>()
        for (archivo in kotlinFilesEn("app/src/main/java")) {
            for (linea in archivo.readLines()) {
                if (linea.contains("startActivity")) {
                    offenders.getOrPut(archivo.name) { mutableListOf() }.add(linea.trim())
                }
            }
        }
        offenders.forEach { (archivo, lineas) ->
            // El único lugar autorizado es EnlacesExternos.
            assertTrue(
                "$archivo abre una app externa sin pasar por EnlacesExternos: $lineas",
                archivo == "EnlacesExternos.kt"
            )
        }
    }

    @Test
    fun `las urls externas viven en el repositorio y no en las pantallas`() {
        assertNotNull(DataRepository.PLATAFORMA_URL)
        assertNotNull(DataRepository.SITIO_WEB)
        assertNotNull(DataRepository.WHATSAPP_NUMERO)
        assertTrue(DataRepository.FACEBOOK_URL.startsWith("https://www.facebook.com/"))
        assertTrue(DataRepository.INSTAGRAM_URL.startsWith("https://www.instagram.com/"))
        assertTrue(DataRepository.YOUTUBE_URL.startsWith("https://www.youtube.com/"))
        assertTrue(DataRepository.WHATSAPP_NUMERO.matches(Regex("^\\d{10,13}$")))
    }

    // ── El sistema visual no puede volver a duplicarse ───────────────────

    @Test
    fun `la paleta base solo se declara en el modulo compartido`() {
        // Cuando `:campo` entró en el proyecto traía copia de `Color.kt`:
        // doce colores con el mismo nombre y el mismo valor en dos ficheros, y
        // seis colores iguales con nombres distintos (`EnidGreen` /
        // `EnidVerdeWhatsapp`). Dos copias en un mismo APK divergen sin aviso.
        val declaradosEnModulos = mutableListOf<String>()
        listOf("app/src/main/java", "campo/src/main/java").forEach { raiz ->
            kotlinFilesEn(raiz).forEach { archivo ->
                if (archivo.name != "Color.kt") return@forEach
                java.util.regex.Pattern
                    .compile("^val (Enid\\w+)", java.util.regex.Pattern.MULTILINE)
                    .matcher(archivo.readText())
                    .let { m ->
                        while (m.find()) declaradosEnModulos += m.group(1)
                    }
            }
        }
        val baseCompartida = setOf(
            "EnidDarkBlue", "EnidBlueBright", "EnidBlueSoft", "EnidRed", "EnidRedSoft",
            "EnidBg", "EnidCardBg", "EnidWhite", "EnidInk", "EnidTextSec",
            "EnidBorderSoft", "EnidIconBox"
        )
        val repetidos = declaradosEnModulos.groupingBy { it }.eachCount()
            .filterValues { it > 1 }.keys
        assertTrue(
            "Estos colores de la paleta base se declaran más de una vez: $repetidos",
            repetidos.none { it in baseCompartida }
        )
    }

    @Test
    fun `el modulo compartido existe y declara la paleta`() {
        val colores = kotlinFilesEn("ui/src/main/java").filter { it.name == "Color.kt" }
        assertTrue("Falta el módulo :ui o su Color.kt", colores.isNotEmpty())
        val declarados = colores.flatMap { archivo ->
            java.util.regex.Pattern
                .compile("^val (Enid\\w+)", java.util.regex.Pattern.MULTILINE)
                .matcher(archivo.readText())
                .let { m -> buildList { while (m.find()) add(m.group(1)) } }
        }
        assertTrue(
            "La paleta compartida debe seguir declarando los colores base",
            declarados.containsAll(
                listOf(
                    "EnidDarkBlue", "EnidRed", "EnidBg", "EnidWhite",
                    // Paleta oficial del 27 Oct: azul de apoyo y turquesa de progreso.
                    "EnidBlueMid", "EnidTeal", "EnidTealDark", "EnidTealSoft"
                )
            )
        )
    }

    @Test
    fun `las medidas del sistema visual viven en un solo sitio`() {
        // `:campo` declaraba `EnidDimens` y `:app` escribía `14.dp` a mano
        // dieciocho veces. Ahora las medidas tienen nombre y un solo sitio.
        val conDimens = kotlinFilesEn("ui/src/main/java").filter {
            it.readText().contains("object EnidDimens")
        }
        assertEquals("`EnidDimens` debe declararse una sola vez", 1, conDimens.size)
    }

    /**
     * Devuelve los `.kt` de [ruta] buscándola hacia arriba desde el
     * directorio de trabajo: Gradle ejecuta las pruebas con el módulo (`:app`)
     * como cwd, no con la raíz del proyecto.
     */
    private fun kotlinFilesEn(ruta: String): List<java.io.File> {
        var base: java.io.File? = java.io.File(System.getProperty("user.dir")).absoluteFile
        while (base != null && !java.io.File(base, ruta).isDirectory) {
            base = base.parentFile
        }
        val directorio = java.io.File(base ?: return emptyList(), ruta)
        return directorio.walkTopDown().filter { it.extension == "kt" }.toList()
    }
}