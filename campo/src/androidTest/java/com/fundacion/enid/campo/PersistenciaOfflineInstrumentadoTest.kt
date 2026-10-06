package com.fundacion.enid.campo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fundacion.enid.campo.data.AspirantesRepository
import com.fundacion.enid.campo.data.evidencia.AlmacenEvidencias
import com.fundacion.enid.campo.data.ResultadoEdicion
import com.fundacion.enid.campo.data.ResultadoGuardado
import com.fundacion.enid.campo.data.local.AspiranteDao
import com.fundacion.enid.campo.data.local.EnidDatabase
import com.fundacion.enid.campo.data.model.EstadoSincronizacion
import com.fundacion.enid.campo.data.validation.BorradorAspirante
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

/**
 * TEST INSTRUMENTADO: LA APP REAL, EN EL TELEFONO REAL
 * ---------------------------------------------------------------------------
 * Los tests JVM anterior comprueban las reglas. Este comprueba lo que solo
 * existe dentro de Android: que Room escriba de verdad, que la foto quede en
 * el almacenamiento interno y que un registro sobreviva al cierre de la app.
 *
 * Corre en el dispositivo conectado con:
 *   ./gradlew :app:connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class PersistenciaOfflineInstrumentadoTest {

    private lateinit var contexto: Context
    private lateinit var dao: AspiranteDao
    private lateinit var almacen: AlmacenEvidencias
    private lateinit var repositorio: AspirantesRepository

    @Before
    fun preparar() {
        contexto = InstrumentationRegistry.getInstrumentation().targetContext
        dao = EnidDatabase.obtener(contexto).AspiranteDao()
        almacen = AlmacenEvidencias(contexto)
        repositorio = AspirantesRepository(dao, almacen)
    }

    @After
    fun limpiar() = runBlocking {
        dao.observarTodos().first().forEach { dao.borrar(it.idRegistro) }
        almacen.limpiarTemporales()
    }

    /** Crea una foto JPEG real en la cache y devuelve su Uri. */

    /**
     * Edad cumplida hoy para "dd/mm/aaaa".
     *
     * La edad se CALCULA y no se escribe a mano. Si el test fija "21" con
     * nacimiento 15/04/2004, el dia del cumpleanos el borrador pasa a ser
     * incoherente, todos los tests que guardan fallan por un motivo que no
     * tienen nada que ver con persistencia y el diagnostico se vuelve inutil.
     */
    private fun edadCumplida(fecha: String): String {
        val (d, m, a) = fecha.split("/").map { it.toInt() }
        val hoy = Calendar.getInstance()
        var edad = hoy.get(Calendar.YEAR) - a
        val mesHoy = hoy.get(Calendar.MONTH) + 1
        if (mesHoy < m || (mesHoy == m && hoy.get(Calendar.DAY_OF_MONTH) < d)) edad--
        return edad.toString()
    }

    private fun fotoDePrueba(nombre: String): Uri {
        val bitmap = Bitmap.createBitmap(600, 800, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).drawRect(
            0f, 0f, 600f, 800f,
            Paint().apply { color = Color.rgb(230, 230, 235) }
        )
        val archivo = File(contexto.cacheDir, "$nombre.jpg")
        FileOutputStream(archivo).use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, salida)
        }
        bitmap.recycle()
        return Uri.fromFile(archivo)
    }

    private fun borradorConFoto(documento: String) = BorradorAspirante(
        nombreCompleto = "Ana Maria Gomez",
        tipoDocumento = "CC",
        documentoIdentidad = documento,
        fechaNacimiento = "15/04/2004",
        edad = edadCumplida("15/04/2004"),
        telefono = "3001234567",
        correoElectronico = "ana.gomez@correo.com",
        municipioResidencia = "Chía",
        municipioCaptura = "Cota",
        jornadaCaptura = "Mañana",
        programaInteres = "T-ANA",
        medioConocimiento = "Redes sociales de la Fundación",
        condicionesEconomicas = "Estrato 3",
        consentimientoDatos = true,
        rutaEvidencia = fotoDePrueba("evidencia-$documento").toString()
    )

    // ------------------------------------------------------ GUARDADO LOCAL ---

    @Test
    fun guardarEscribeEnRoomYEnElAlmacenamientoInterno() = runBlocking {
        val resultado = repositorio.guardar(borradorConFoto("1098765432"))

        assertTrue("No se guardo: $resultado", resultado is ResultadoGuardado.Guardado)
        val id = (resultado as ResultadoGuardado.Guardado).registro.idRegistro

        // 1. La fila existe en SQLite.
        val fila = dao.leerPorId(id)
        assertNotNull("La fila no quedo en Room", fila)
        assertEquals("Ana Maria Gomez", fila!!.nombreCompleto)
        assertEquals(EstadoSincronizacion.PENDIENTE, fila.estado)

        // 2. La foto quedo COPIADA dentro de la app, no referenciada a la galeria.
        val foto = File(fila.rutaEvidenciaFoto)
        assertTrue("La foto no esta dentro de filesDir", foto.exists())
        assertTrue("La foto esta vacia", foto.length() > 0)
        assertTrue(
            "La foto debe vivir en el almacenamiento interno, no en la galeria",
            foto.absolutePath.contains(contexto.filesDir.absolutePath)
        )
    }

    @Test
    fun guardarNoEscribeNadaSiFaltaLaFoto() = runBlocking {
        val sinFoto = borradorConFoto("1098765433").copy(rutaEvidencia = null)
        val resultado = repositorio.guardar(sinFoto)

        assertTrue(resultado is ResultadoGuardado.Rechazado)
        assertEquals(0, dao.contarTodos())
    }

    @Test
    fun guardarRechazaUnDocumentoDuplicado() = runBlocking {
        repositorio.guardar(borradorConFoto("1098765434"))
        val repetido = repositorio.guardar(borradorConFoto("1098765434"))

        assertTrue(repetido is ResultadoGuardado.Rechazado)
        assertEquals("No debe haber dos filas con el mismo documento", 1, dao.contarTodos())
    }

    // ------------------------------------------------------------ PERSISTENCIA ---

    @Test
    fun elRegistroSobreviveAlCierreDeLaApp() = runBlocking {
        val resultado = repositorio.guardar(borradorConFoto("1098765435"))
        val id = (resultado as ResultadoGuardado.Guardado).registro.idRegistro

        // Se reabre el MISMO archivo de base de datos por la via publica: es lo que
        // ocurre cuando Android mata el proceso y la app vuelve a arrancar.
        val segunda = EnidDatabase.obtener(contexto).AspiranteDao()

        assertNotNull("El registro desaparecio al reabrir la base", segunda.leerPorId(id))
        assertEquals(1, segunda.contarTodos())
    }

    @Test
    fun seGuardanVariosRegistrosEnUnaMismaJornada() = runBlocking {
        listOf("1098765436", "1098765437", "1098765438", "1098765439").forEach {
            repositorio.guardar(borradorConFoto(it))
        }
        assertEquals(4, dao.contarTodos())
        assertEquals(4, dao.contarPorSincronizar())
    }

    // ------------------------------------------------------------- EDICION ---

    @Test
    fun unRegistroPendienteSePuedeEditar() = runBlocking {
        val id = (repositorio.guardar(borradorConFoto("1098765440")) as ResultadoGuardado.Guardado)
            .registro.idRegistro
        val actual = dao.leerPorId(id)!!

        val resultado = repositorio.editar(
            BorradorAspirante(
                nombreCompleto = "Ana Maria Gomez Perez",
                tipoDocumento = actual.tipoDocumento,
                documentoIdentidad = actual.documentoIdentidad,
                fechaNacimiento = actual.fechaNacimiento,
                edad = actual.edad.toString(),
                telefono = actual.telefono,
                correoElectronico = actual.correoElectronico,
                municipioResidencia = actual.municipioResidencia,
                municipioCaptura = actual.municipioCaptura,
                jornadaCaptura = actual.jornadaCaptura,
                programaInteres = actual.programaInteres,
                medioConocimiento = actual.medioConocimiento,
                condicionesEconomicas = actual.condicionesEconomicas,
                consentimientoDatos = true,
                rutaEvidencia = actual.rutaEvidenciaFoto,
                idRegistro = id
            )
        )

        assertTrue("No se edito: $resultado", resultado is ResultadoEdicion.Editado)
        assertEquals("Ana Maria Gomez Perez", dao.leerPorId(id)!!.nombreCompleto)
        assertEquals("La edicion no debe crear una fila nueva", 1, dao.contarTodos())
    }

    @Test
    fun unRegistroYaSincronizadoQuedaBloqueadoParaEdicion() = runBlocking {
        val id = (repositorio.guardar(borradorConFoto("1098765441")) as ResultadoGuardado.Guardado)
            .registro.idRegistro
        dao.marcarSincronizado(id, 1_700_000_000_000L)

        val actual = dao.leerPorId(id)!!
        val resultado = repositorio.editar(
            BorradorAspirante(
                nombreCompleto = "Nombre Cambiado",
                tipoDocumento = actual.tipoDocumento,
                documentoIdentidad = actual.documentoIdentidad,
                fechaNacimiento = actual.fechaNacimiento,
                edad = actual.edad.toString(),
                telefono = actual.telefono,
                correoElectronico = actual.correoElectronico,
                municipioResidencia = actual.municipioResidencia,
                municipioCaptura = actual.municipioCaptura,
                jornadaCaptura = actual.jornadaCaptura,
                programaInteres = actual.programaInteres,
                medioConocimiento = actual.medioConocimiento,
                condicionesEconomicas = actual.condicionesEconomicas,
                consentimientoDatos = true,
                rutaEvidencia = actual.rutaEvidenciaFoto,
                idRegistro = id
            )
        )

        assertTrue("Debe quedar bloqueado: $resultado", resultado is ResultadoEdicion.Bloqueado)
        assertEquals("Ana Maria Gomez", dao.leerPorId(id)!!.nombreCompleto)
    }

    // ------------------------------------------------------- CICLO DE ESTADOS ---

    @Test
    fun elCicloDeEstadosDeSincronizacionFunciona() = runBlocking {
        val id = (repositorio.guardar(borradorConFoto("1098765442")) as ResultadoGuardado.Guardado)
            .registro.idRegistro

        assertEquals(EstadoSincronizacion.PENDIENTE, dao.leerPorId(id)!!.estado)

        dao.marcarError(id, "Sin conexión a internet")
        val conError = dao.leerPorId(id)!!
        assertEquals(EstadoSincronizacion.ERROR, conError.estado)
        assertEquals("Sin conexión a internet", conError.errorSincronizacion)
        assertTrue("Con error debe poder reintentarse", conError.estado.esSincronizable)

        dao.rearmarError(id)
        assertEquals(EstadoSincronizacion.PENDIENTE, dao.leerPorId(id)!!.estado)
        assertNull(dao.leerPorId(id)!!.errorSincronizacion)

        dao.marcarSincronizado(id, 1_700_000_000_000L)
        val sincronizado = dao.leerPorId(id)!!
        assertEquals(EstadoSincronizacion.SINCRONIZADO, sincronizado.estado)
        assertFalse("Sincronizado ya no se edita", sincronizado.esEditable)
        assertNotNull(sincronizado.fechaSincronizacion)
    }

    @Test
    fun losPendientesParaSincronizarRespetanElOrdenDeCaptura() = runBlocking {
        listOf("1098765443", "1098765444", "1098765445").forEach {
            repositorio.guardar(borradorConFoto(it))
        }
        val pendientes = dao.pendientesParaSincronizar()
        assertEquals(3, pendientes.size)
        // El mas antiguo primero: es el orden en que se envian.
        val fechas = pendientes.map { it.fechaRegistro }
        assertEquals(fechas.sorted(), fechas)
    }

    // ------------------------------------------------------------ FOTO INACCESIBLE ---

    @Test
    fun unaFotoQueYaNoExisteImpideGuardar() = runBlocking {
        val rutaInexistente = Uri.fromFile(File(contexto.cacheDir, "no-existe-esta-foto.jpg"))
        val resultado = repositorio.guardar(
            borradorConFoto("1098765446").copy(rutaEvidencia = rutaInexistente.toString())
        )

        assertTrue(
            "Debe rechazarse la captura si la foto no se puede leer",
            resultado is ResultadoGuardado.Rechazado
        )
        assertEquals(0, dao.contarTodos())
    }
}
