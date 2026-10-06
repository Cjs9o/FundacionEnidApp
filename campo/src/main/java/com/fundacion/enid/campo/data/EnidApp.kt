package com.fundacion.enid.campo.data

import android.content.Context
import com.fundacion.enid.campo.data.evidencia.AlmacenEvidencias
import com.fundacion.enid.campo.data.local.EnidDatabase
import com.fundacion.enid.campo.data.red.MonitorConectividad
import com.fundacion.enid.campo.data.sync.PuertaRemota
import com.fundacion.enid.campo.data.sync.PuertaRemotaSimulada
import com.fundacion.enid.campo.data.sync.SyncManager

/**
 * CONTENEDOR DE OBJETOS DE LA APLICACION
 * ===========================================================================
 * DECISION DE ARQUITECTURA DEL GRUPO: no se usa Hilt ni Dagger.
 *
 * Justificacion: el alcance del taller es un modulo de captura con Room y una
 * sincronizacion manual. Un contenedor de seis objetos escrito a mano es mas
 * facil de explicar, de auditar y de adaptar en la prueba de ajuste en vivo
 * (Fase 6) que un grafo de inyeccion con anotaciones. Todo se construye una
 * sola vez en `EnidAppHolder.obtener` y se comparte por pantalla.
 *
 * Lo mas importante de este archivo para la sustentacion es la linea comentada
 * de [PuertaRemota]: es el UNICO punto donde habra que cambiar para conectar
 * Firebase Realtime Database. Ni una pantalla ni un repositorio se modifica.
 */
class EnidApp(contextoApp: Context) {

    /**
     * Contexto de aplicacion, unico accesible desde el contenedor. Se expone
     * porque algunos componentes (lectura de la evidencia con ContentResolver,
     * permisos de camara) necesitan leer archivos del dispositivo y solo pueden
     * hacerlo con un `Context`.
     */
    val appContext: Context = contextoApp.applicationContext

    /** Base de datos local. Unico punto de acceso a Room. */
    private val baseDeDatos: EnidDatabase = EnidDatabase.obtener(appContext)

    val dao = baseDeDatos.AspiranteDao()

    /** Copia fisica y compresion de la evidencia fotografica. */
    val almacen = AlmacenEvidencias(appContext)

    /** Estado de red. Solo informa; nunca decide por el usuario. */
    val conectividad = MonitorConectividad(appContext)

    /**
     * FRONTERA LOCAL / REMOTO
     *
     * HOY  : puerta simulada en memoria. Permite ejecutar el taller completo
     *        (captura, validacion, persistencia, seleccion de pendientes,
     *        envio, confirmacion, estados, fallos parciales y anti-duplicados)
     *        sin depender de una conexion ni de un proyecto de Firebase.
     *
     * CUANDO SE CONECTE FIREBASE RTDB, se cambia SOLO esta linea por:
     *
     *     val puertaRemota: PuertaRemota = PuertaRemotaFirebase(
     *         reference = FirebaseDatabase.getInstance()
     *             .getReference(EstructuraRtdb.RAIZ)
     *     )
     *
     * el plugin `com.google.gms.google-services`, la dependencia
     * `firebase-database` y la clave `database_url` en google-services.json.
     * [SyncManager] ya habla con `PuertaRemota` y no requiere cambios.
     */
    val puertaRemota: PuertaRemota = PuertaRemotaSimulada()

    /** Repositorio: valida, guarda en disco y escribe en Room. Nunca usa la red. */
    val repositorio = AspirantesRepository(dao, almacen)

    /** Unico modulo que habla con el servidor. */
    val syncManager = SyncManager(dao, almacen, conectividad, puertaRemota)
}
