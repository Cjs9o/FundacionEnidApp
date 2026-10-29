package com.fundacion.enid.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Punto único de salida de la app hacia otras aplicaciones.
 *
 * Todas las acciones externas (WhatsApp, llamadas, mapas, sitio web, plataforma y redes)
 * pasan por aquí. Centralizarlas permite:
 * - respetar la restricción de pruebas: con [habilitados] en `false` **no se abre
 *   ninguna app externa** (nada de llamadas, páginas web, banca, WhatsApp ni correos);
 * - cambiar un número o una URL en un solo lugar;
 * - no romper la app si el dispositivo no tiene la app instalada;
 * - **saber si la acción ocurrió de verdad**: cada función devuelve `true` solo si
 *   la app externa se abrió. Sin eso, una pantalla podía mostrar "Listo" sin
 *   que hubiera pasado nada.
 */
object EnlacesExternos {

    private const val TAG = "EnlacesENID"

    /** Mensaje por defecto del botón de asesor. */
    const val MENSAJE_ASESOR =
        "Hola, soy interesado en estudiar en ENID. ¿Me pueden orientar?"

    /**
     * Interruptor de acciones externas. En `true` se comportan como botones normales.
     * Durante las pruebas se deja en `false` para no abrir ninguna app del teléfono.
     */
    var habilitados: Boolean = false

    /**
     * Abre WhatsApp con un mensaje listo para enviar.
     * @return `true` si la app de WhatsApp se abrió.
     */
    fun abrirWhatsApp(
        context: Context,
        mensaje: String,
        numero: String = DataRepository.WHATSAPP_NUMERO
    ): Boolean = abrir(context, "https://wa.me/$numero?text=${Uri.encode(mensaje)}", "whatsapp")

    /** "Hablar con un asesor": WhatsApp con el mensaje ya escrito. */
    fun abrirAsesor(context: Context, mensaje: String = MENSAJE_ASESOR): Boolean =
        abrirWhatsApp(context, mensaje)

    /** Abre el marcador telefónico con el número ya escrito (no marca). */
    fun abrirLlamar(context: Context, telefono: String): Boolean =
        abrir(context, "tel:${telefono.replace(Regex("[^+\\d]"), "")}", "telefono")

    /** Abre Google Maps buscando la dirección. */
    fun abrirMapa(context: Context, direccion: String): Boolean = abrir(
        context,
        "https://www.google.com/maps/search/?api=1&query=${Uri.encode(direccion)}",
        "mapa"
    )

    /** Abre cualquier dirección web https. */
    fun abrirWeb(context: Context, url: String): Boolean = abrir(context, url, "web")

    /** Sitio web institucional (Requerimiento 11). */
    fun abrirSitio(context: Context): Boolean = abrirWeb(context, DataRepository.SITIO_WEB)

    /** Abre la plataforma educativa externa (Requerimiento 12). */
    fun abrirPlataforma(context: Context): Boolean = abrirWeb(context, DataRepository.PLATAFORMA_URL)

    /** Redes sociales oficiales de la Fundación. */
    fun abrirFacebook(context: Context): Boolean = abrirWeb(context, DataRepository.FACEBOOK_URL)

    fun abrirInstagram(context: Context): Boolean = abrirWeb(context, DataRepository.INSTAGRAM_URL)

    fun abrirYouTube(context: Context): Boolean = abrirWeb(context, DataRepository.YOUTUBE_URL)

    /**
     * Ejecuta la acción externa.
     * @return `true` solo si la app externa se abrió. `false` significa que el
     *         interruptor [habilitados] está apagado o que no había app capaz
     *         de atender la acción: en ambos casos **nada ocurrió** y la
     *         pantalla debe decirlo, no darlo por hecho.
     */
    private fun abrir(context: Context, uri: String, tipo: String): Boolean {
        if (!habilitados) {
            Log.i(TAG, "Acción externa '$tipo' omitida durante pruebas (habilitados=false): $uri")
            return false
        }
        return try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
            true
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo abrir la acción externa '$tipo'", e)
            false
        }
    }
}