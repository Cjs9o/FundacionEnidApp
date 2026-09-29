package com.fundacion.enid.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Punto único de salida de la app hacia otras aplicaciones.
 *
 * Todas las acciones externas (WhatsApp, llamadas, mapas, sitio web, plataforma y PSE)
 * pasan por aquí. Centralizarlas permite:
 * - respetar la restricción de pruebas: con [habilitados] en `false` **no se abre
 *   ninguna app externa** (nada de llamadas, páginas web, banca, WhatsApp ni correos);
 * - cambiar un número o una URL en un solo lugar;
 * - no romper la app si el dispositivo no tiene la app instalada.
 */
object EnlacesExternos {

    private const val TAG = "EnlacesENID"

    /**
     * Interruptor de acciones externas. En `true` se comportan como botones normales.
     * Durante las pruebas se deja en `false` para no abrir ninguna app del teléfono.
     */
    var habilitados: Boolean = false

    /** Abre WhatsApp con un mensaje listo para enviar. */
    fun abrirWhatsApp(context: Context, numero: String = DataRepository.WHATSAPP_NUMERO, mensaje: String) {
        val texto = Uri.encode(mensaje)
        abrir(context, "https://wa.me/$numero?text=$texto", "whatsapp")
    }

    /** "Hablar con un asesor": WhatsApp con el mensaje ya escrito. */
    fun abrirAsesor(context: Context, mensaje: String) {
        abrirWhatsApp(context, DataRepository.WHATSAPP_NUMERO, mensaje)
    }

    /** Abre el marcador telefónico con el número ya escrito (no marca). */
    fun abrirLlamar(context: Context, telefono: String) {
        val numero = telefono.replace(Regex("[^+\\d]"), "")
        abrir(context, "tel:$numero", "telefono")
    }

    /** Abre Google Maps buscando la dirección. */
    fun abrirMapa(context: Context, direccion: String) {
        val consulta = Uri.encode(direccion)
        abrir(context, "https://www.google.com/maps/search/?api=1&query=$consulta", "mapa")
    }

    /** Abre cualquier dirección web https. */
    fun abrirWeb(context: Context, url: String) {
        abrir(context, url, "web")
    }

    private fun abrir(context: Context, uri: String, tipo: String) {
        if (!habilitados) {
            Log.i(TAG, "Acción externa '$tipo' omitida durante pruebas (habilitados=false): $uri")
            return
        }
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo abrir la acción externa '$tipo'", e)
        }
    }
}
