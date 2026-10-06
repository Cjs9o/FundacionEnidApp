package com.fundacion.enid.campo.data

import com.fundacion.enid.campo.data.local.AspiranteEntity
import com.fundacion.enid.campo.data.model.EstadoSincronizacion
import com.fundacion.enid.campo.data.sync.EvidenciaPayload
import com.fundacion.enid.campo.data.sync.EstructuraRtdb
import com.fundacion.enid.campo.data.sync.SerializadorRtdb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * TESTS DE LA ESTRUCTURA QUE VIAJA A FIREBASE
 * ---------------------------------------------------------------------------
 * Aunque la app todavia no habla con el servidor, el formato queda fijado desde
 * ahora con tests: si la estructura cambia, el test falla aqui y no se descubre
 * en produccion, cuando los datos ya estan guardados en el telefono.
 */
class SerializadorRtdbTest {

    private fun entidad(
        id: String = "reg-001",
        estado: EstadoSincronizacion = EstadoSincronizacion.PENDIENTE,
        error: String? = null
    ) = AspiranteEntity(
        idRegistro = id,
        nombreCompleto = "Ana Maria Gomez",
        tipoDocumento = "CC",
        documentoIdentidad = "1098765432",
        fechaNacimiento = "2004-04-15",
        edad = 21,
        telefono = "3001234567",
        correoElectronico = "ana.gomez@correo.com",
        municipioResidencia = "Chía",
        municipioCaptura = "Cota",
        jornadaCaptura = "Mañana",
        programaInteres = "T-ANA",
        medioConocimiento = "Redes sociales de la Fundación",
        comoSeEntero = "Instagram ENID",
        condicionesEconomicas = "Estrato 3",
        consentimientoDatos = true,
        observaciones = "Interesada en beca",
        rutaEvidenciaFoto = "/data/user/0/com.fundacion.enid.campo/files/evidencias/reg-001.jpg",
        fechaRegistro = 1_700_000_000_000L,
        fechaRegistroLegible = "02/10/2026 09:00",
        estadoSincronizacion = estado.almacenado,
        fechaSincronizacion = null,
        errorSincronizacion = error,
        intentosSincronizacion = if (error == null) 0 else 3
    )

    private val evidencia = EvidenciaPayload(
        nombreArchivo = "reg-001.jpg",
        tamanoBytes = 45_120L,
        anchoPx = 720,
        altoPx = 480,
        contenidoBase64 = "ZmlnaXJlYmFzZTY0"
    )

    @Test
    fun `el mapa del aspirante lleva todos los campos de admision`() {
        val mapa = SerializadorRtdb.aMapaAspirante(entidad(), "2026-10-02T10:00:00Z")

        assertEquals("Ana Maria Gomez", mapa["nombreCompleto"])
        assertEquals("1098765432", mapa["documentoIdentidad"])
        assertEquals("CC", mapa["tipoDocumento"])
        assertEquals(21, mapa["edad"])
        assertEquals("Cota", mapa["municipioCaptura"])
        assertEquals("Chía", mapa["municipioResidencia"])
        assertEquals("T-ANA", mapa["programaInteres"])
        assertEquals(true, mapa["consentimientoDatos"])
        assertEquals("reg-001", mapa["idRegistro"])
    }

    @Test
    fun `viaja el estado real del telefono, no uno inventado por el cliente`() {
        // El telefono NO se auto-declara SINCRONIZADO: solo el servidor confirma la
        // recepcion. Asi, si el paquete se pierde a medio camino, la app sigue
        // mostrando PENDIENTE y el siguiente SINCRONIZAR lo reintenta.
        val mapa = SerializadorRtdb.aMapaAspirante(entidad(), "2026-10-02T10:00:00Z")
        assertEquals("PENDIENTE", mapa["estadoSincronizacion"])
    }

    @Test
    fun `un registro con error conserva su estado y su motivo para diagnosticar`() {
        val conError = entidad(estado = EstadoSincronizacion.ERROR, error = "Sin conexión")
        val mapa = SerializadorRtdb.aMapaAspirante(conError, "2026-10-02T10:00:00Z")
        assertEquals("ERROR", mapa["estadoSincronizacion"])
        assertEquals("Sin conexión", mapa["errorSincronizacion"])
        assertEquals(3, mapa["intentosSincronizacion"])
    }

    @Test
    fun `un registro ya sincronizado mantiene ese estado al reenviarse`() {
        val mapa = SerializadorRtdb.aMapaAspirante(
            entidad(estado = EstadoSincronizacion.SINCRONIZADO),
            "2026-10-02T10:00:00Z"
        )
        assertEquals("SINCRONIZADO", mapa["estadoSincronizacion"])
    }

    @Test
    fun `la escritura incluye la evidencia y el indice anti-duplicados`() {
        val mapa = SerializadorRtdb.aMapaEscritura(
            entidad(),
            evidencia,
            "2026-10-02T10:00:00Z"
        )

        // La evidencia viaja como base64 dentro del nodo propio.
        assertEquals(
            "ZmlnaXJlYmFzZTY0",
            mapa["${EstructuraRtdb.rutaEvidencia("reg-001")}/${EstructuraRtdb.CAMPO_CONTENIDO}"]
        )
        assertEquals(
            "reg-001.jpg",
            mapa["${EstructuraRtdb.rutaEvidencia("reg-001")}/nombreArchivo"]
        )

        // El indice subordinado permite responder por documento en una sola lectura.
        assertEquals(
            "1098765432",
            mapa["${EstructuraRtdb.rutaAspirante("reg-001")}/${EstructuraRtdb.NODO_INDICE}/documentoIdentidad"]
        )
    }

    @Test
    fun `sin evidencia legible solo viaja el registro, no un nodo de foto vacio`() {
        val mapa = SerializadorRtdb.aMapaEscritura(
            entidad(),
            evidencia = null,
            fechaSincronizacionIso = "2026-10-02T10:00:00Z"
        )

        val conFoto = mapa.keys.any { it.startsWith(EstructuraRtdb.NODO_EVIDENCIAS) }
        assertFalse("No debe crear claves de evidencia cuando no hay foto", conFoto)
        assertTrue(
            "Los datos del registro siempre viajan",
            mapa.keys.any { it.startsWith(EstructuraRtdb.NODO_ASPIRANTES) }
        )
    }

    @Test
    fun `las rutas del arbol RTDB son las acordadas en el taller`() {
        assertEquals("aspirantes/reg-001", EstructuraRtdb.rutaAspirante("reg-001"))
        assertEquals("evidencias/reg-001", EstructuraRtdb.rutaEvidencia("reg-001"))
        assertEquals("aspirantes/reg-001/_indice", EstructuraRtdb.rutaIndice("reg-001"))
        assertEquals("fundacionenid_campo", EstructuraRtdb.RAIZ)
    }

    @Test
    fun `el mapa no lleva objetos de Android, solo tipos que RTDB entiende`() {
        val mapa = SerializadorRtdb.aMapaEscritura(entidad(), evidencia, "2026-10-02T10:00:00Z")
        // RTDB solo admite String, Number y Boolean: cualquier otro objeto fallaria
        // en tiempo de ejecucion con una excepcion que el asesor no podria ver.
        mapa.forEach { (ruta, valor) ->
            if (valor != null) {
                val permitido = valor is String || valor is Number || valor is Boolean
                assertTrue(
                    "El valor de '$ruta' es ${valor::class.java.name} y RTDB no lo acepta",
                    permitido
                )
            }
        }
    }
}
