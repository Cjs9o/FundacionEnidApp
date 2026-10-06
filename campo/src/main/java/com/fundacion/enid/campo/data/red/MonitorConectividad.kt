package com.fundacion.enid.campo.data.red

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * MONITOR DE CONECTIVIDAD
 * ===========================================================================
 * DECISION DOCUMENTADA (Matriz de decisiones, fila "Como se informara al
 * usuario que esta trabajando offline"): banner contextual superior conectado
 * a ConnectivityManager, en lugar de dialogos emergentes.
 *
 * DETALLE TECNICO QUE IMPORTA:
 * `NET_CAPABILITY_VALIDATED` es lo que distingue "hay barra de senal" de "hay
 * Internet que funciona de verdad". En una zona rural el celular se engancha a
 * una torre que no tiene salida a Internet: el sistema reporta RED CONECTADA
 * pero la escritura en Firebase se quedaria colgada. Exigir VALIDATED es
 * precisamente el "condicion de inicio" que el taller pide: que haya una
 * conexion que realmente funcione.
 *
 * Este monitor SOLO informa; nunca decide por el usuario. La unica llamada al
 * servidor sigue siendo la que dispara el boton SINCRONIZAR.
 */
class MonitorConectividad(context: Context) {

    private val appContext = context.applicationContext

    private val conectividad: ConnectivityManager? =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    companion object {
        private const val TAG = "MonitorConectividad"
    }

    /**
     * `true` si existe una red VALIDADA. Es el unico metodo que el SyncManager
     * consulta antes de enviar el primer registro.
     */
    fun hayInternet(): Boolean {
        val cm = conectividad ?: return false
        return runCatching {
            val red = cm.activeNetwork ?: return false
            val capacidades = cm.getNetworkCapabilities(red) ?: return false
            capacidades.tieneInternetQueFunciona()
        }.getOrElse {
            Log.w(TAG, "No se pudo consultar el estado de red", it)
            false
        }
    }

    /** Descripcion del tipo de transporte, para el banner: "WiFi" / "Móvil" / null. */
    fun tipoDeConexion(): String? {
        val cm = conectividad ?: return null
        return runCatching {
            val red = cm.activeNetwork ?: return null
            val capacidades = cm.getNetworkCapabilities(red) ?: return null
            when {
                capacidades.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
                capacidades.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Móvil"
                capacidades.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> null
            }
        }.getOrNull()
    }

    /**
     * Flujo reactivo del estado de red. Se observa en `AppRoot` y se pasa a las
     * pantallas para que el banner aparezca y desaparezca sin intervencion.
     */
    fun observar(): Flow<Boolean> = callbackFlow {
        val cm = conectividad
        if (cm == null) {
            trySend(false)
            awaitClose { }
            return@callbackFlow
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(hayInternet())
            }

            override fun onLost(network: Network) {
                trySend(hayInternet())
            }

            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(caps.tieneInternetQueFunciona())
            }
        }

        val solicitud = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        runCatching { cm.registerNetworkCallback(solicitud, callback) }
            .onFailure { Log.w(TAG, "No se pudo observar la red", it) }

        trySend(hayInternet())

        awaitClose {
            runCatching { cm.unregisterNetworkCallback(callback) }
        }
    }.distinctUntilChanged()
}

/**
 * Extension privada para no repetir la combinacion de capacidades en dos sitios.
 * `INTERNET` = hay interfaz. `VALIDATED` = el sistema comprobo que realmente
 * hay salida a Internet.
 */
private fun NetworkCapabilities.tieneInternetQueFunciona(): Boolean =
    hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
