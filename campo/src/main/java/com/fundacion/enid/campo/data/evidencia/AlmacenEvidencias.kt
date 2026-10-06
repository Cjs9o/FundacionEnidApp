package com.fundacion.enid.campo.data.evidencia

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * ALMACENAMIENTO DE LA EVIDENCIA FOTOGRAFICA
 * ===========================================================================
 * DECISION DOCUMENTADA DEL GRUPO (Matriz de decisiones, fila "Forma de
 * almacenar la imagen localmente"):
 *
 *   ELEGIDO    : copia fisica comprimida en el directorio interno de la app
 *                (`context.filesDir/evidencias/{idRegistro}.jpg`) + se guarda
 *                SOLO la ruta en Room.
 *   DESCARTADO : Base64 dentro de la base de datos.
 *
 * POR QUE: Base64 infla la foto ~33% y obliga a materializar el byte array en
 * memoria cada vez que se lista la base, lo que produce OutOfMemoryError en
 * equipos de gama baja y retrasa el scroll. Con copia fisica, la miniatura de
 * la tarjeta se decodifica desde el archivo y el tamaño de la base de datos
 * queda en kilobytes.
 *
 * TAMANO MAXIMO: 1024 px del lado mayor, JPEG calidad 70. Una cedula
 * fotografiada a 1024 px es legible para el equipo de admisiones y pesa entre
 * 60 y 140 KB, suficiente para un envio razonable a RTDB.
 *
 * POR QUE NO LA URI DE LA GALERIA: una `content://` de MediaStore depende del
 * permiso de lectura y de que el archivo siga existiendo. Si el usuario borra
 * la foto original, el registro queda huerfano. Guardamos nuestra propia copia
 * y asi la evidencia sobrevive al cierre de la app, a la desinstalacion de la
 * app de camara y al borrado de la galeria.
 */
class AlmacenEvidencias(private val context: Context) {

    companion object {
        private const val TAG = "AlmacenEvidencias"
        const val CARPETA_EVIDENCIAS = "evidencias"
        const val CARPETA_CAPTURA_TEMPORAL = "capturas"

        /** Lado mayor maximo en pixeles. */
        const val LADO_MAXIMO_PX = 1024

        /** Calidad JPEG. 70 es el punto donde la cedula sigue siendo legible. */
        const val CALIDAD_JPEG = 70

        /** Si el archivo pesan mas de esto, se considera evidencia no valida. */
        const val TAMANO_MINIMO_BYTES = 2_000L
    }

    private val dirEvidencias: File
        get() = File(context.filesDir, CARPETA_EVIDENCIAS).apply { if (!exists()) mkdirs() }

    private val dirTemporal: File
        get() = File(context.filesDir, CARPETA_CAPTURA_TEMPORAL).apply { if (!exists()) mkdirs() }

    /**
     * Crea un archivo temporal para que la app de camara escriba ahi.
     * Se expone como `content://` mediante FileProvider porque las apps
     * externas no pueden escribir en el sistema de archivos de la nuestra.
     *
     * Se recuerda el ultimo archivo creado porque, al usar EXTRA_OUTPUT, la app
     * de camara devuelve RESULT_OK SIN datos: el camino para recuperar la foto
     * es el archivo que nosotros indicamos, no `result.data`.
     */
    @Volatile
    private var ultimoArchivoTemporal: String? = null

    fun archivoTemporalDeCaptura(): File =
        File(dirTemporal, "${UUID.randomUUID()}.jpg").also {
            ultimoArchivoTemporal = it.absolutePath
        }

    /** El archivo temporal de la ultima captura solicitada, si todavia existe. */
    fun ultimoTemporal(): File? = ultimoArchivoTemporal?.let { File(it) }

    fun uriParaCamara(archivo: File): Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )

    /** Borra las capturas temporales que la app de camara dejo sin usar. */
    fun limpiarTemporales() {
        ultimoArchivoTemporal = null
        runCatching { dirTemporal.listFiles()?.forEach { it.delete() } }
            .onFailure { Log.w(TAG, "No se pudieron limpiar las capturas temporales", it) }
    }

    /**
     * Copia, comprime y guarda la evidencia dentro de `filesDir/evidencias`.
     *
     * @param origen Uri devuelta por la camara o por el selector de galeria.
     * @param idRegistro UUID del registro; determina el nombre del archivo.
     * @return Result con la ruta ABSOLUTA ya guardada, o el motivo del fallo.
     */
    fun guardarEvidencia(origen: Uri, idRegistro: String): Result<String> = runCatching {
        val destino = File(dirEvidencias, "$idRegistro.jpg")

        val bitmap = decodificar(origen) ?: error(
            "No se pudo leer la imagen seleccionada. Elija otra foto."
        )

        val escalada = escalar(bitmap, origen)
        FileOutputStream(destino).use { salida ->
            if (!escalada.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)) {
                error("No se pudo comprimir la imagen. Intente de nuevo.")
            }
        }
        escalada.recycle()
        if (bitmap !== escalada) bitmap.recycle()

        if (destino.length() < TAMANO_MINIMO_BYTES) {
            destino.delete()
            error("La foto quedó ilegible o vacía. Tome la foto con más luz.")
        }

        Log.i(TAG, "Evidencia guardada: ${destino.name} (${destino.length()} bytes)")
        destino.absolutePath
    }

    /**
     * Lee la evidencia con downsample. `inSampleSize` evita que el proceso
     * cargue en memoria la foto original a resolucion completa de la camara
     * (que puede pesar 8 MB) cuando solo necesitamos 1024 px.
     */
    private fun decodificar(origen: Uri): Bitmap? {
        // Primera pasada: SOLO los limites. OJO, con `inJustDecodeBounds = true`
        // `decodeStream` devuelve SIEMPRE null por diseno, asi que el exito se
        // comprueba con los `outWidth/outHeight`, nunca con el bitmap devuelto.
        val opcionesAbiertas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val abrio = runCatching {
            context.contentResolver.openInputStream(origen)?.use {
                BitmapFactory.decodeStream(it, null, opcionesAbiertas)
            }
        }.isSuccess

        val ancho = opcionesAbiertas.outWidth
        val alto = opcionesAbiertas.outHeight
        if (!abrio || ancho <= 0 || alto <= 0) return null

        var muestreo = 1
        while (ancho / muestreo > LADO_MAXIMO_PX * 2 || alto / muestreo > LADO_MAXIMO_PX * 2) {
            muestreo *= 2
        }

        val opciones = BitmapFactory.Options().apply {
            inSampleSize = muestreo
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return context.contentResolver.openInputStream(origen)?.use {
            BitmapFactory.decodeStream(it, null, opciones)
        }
    }

    /** Encaja el bitmap dentro de LADO_MAXIMO_PX sin deformar la relacion de aspecto. */
    private fun escalar(bitmap: Bitmap, origen: Uri): Bitmap {
        val ladoMayor = maxOf(bitmap.width, bitmap.height)
        val factor = if (ladoMayor <= LADO_MAXIMO_PX) {
            1f
        } else {
            LADO_MAXIMO_PX.toFloat() / ladoMayor
        }
        val nuevoAncho = (bitmap.width * factor).toInt().coerceAtLeast(1)
        val nuevoAlto = (bitmap.height * factor).toInt().coerceAtLeast(1)

        val reescalado =
            if (factor == 1f) bitmap
            else Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, true)

        // La orientacion EXIF se aplica SIEMPRE, no solo cuando hubo que reducir:
        // una foto de 800 px tomada en vertical tambien viene marcada como rotada.
        val grados = gradosRotacion(origen, bitmap)
        return if (grados == 0f) reescalado else rotar(reescalado, grados)
    }

    /**
     * Lee la orientacion EXIF del archivo original. Sin esto, las fotos de
     * camara tomadas en vertical se guardarian tumbadas y la cedula seria
     * ilegible para admisiones.
     */
    private fun gradosRotacion(origen: Uri, bitmap: Bitmap): Float = runCatching {
        val grados = context.contentResolver.openInputStream(origen)?.use { entrada ->
            ExifInterface(entrada).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } ?: ExifInterface.ORIENTATION_NORMAL

        when (grados) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }.getOrElse { e ->
        Log.w(TAG, "No se pudo leer la orientacion EXIF, se guarda sin rotar", e)
        0f
    }

    private fun rotar(bitmap: Bitmap, grados: Float): Bitmap {
        val matriz = Matrix().apply { postRotate(grados) }
        val rotada = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true
        )
        // `rotar` es terminal: recicla el bitmap de entrada para no dejar copias
        // de 8 MB en memoria durante una jornada de 400 capturas.
        if (rotada !== bitmap) bitmap.recycle()
        return rotada
    }

    /** El archivo de evidencia sigue existiendo en el disco? */
    fun existe(ruta: String?): Boolean {
        if (ruta.isNullOrBlank()) return false
        val archivo = File(ruta)
        return archivo.exists() && archivo.length() > 0
    }

    /**
     * Verificacion que la sincronizacion hace ANTES de enviar: si la copia
     * interna desaparecio (borrada con Device File Explorer, restauracion de
     * respaldo, etc.) el registro no se envia y pasa a ERROR con esta causa.
     */
    fun verificar(ruta: String?): VerificacionEvidencia =
        if (ruta.isNullOrBlank()) {
            VerificacionEvidencia(false, "La evidencia fotográfica no está asociada al registro.")
        } else {
            val archivo = File(ruta)
            when {
                !archivo.exists() -> VerificacionEvidencia(
                    false,
                    "La evidencia fotográfica no está disponible: el archivo interno fue borrado."
                )
                archivo.length() < TAMANO_MINIMO_BYTES -> VerificacionEvidencia(
                    false,
                    "La evidencia fotográfica está dañada o vacía."
                )
                else -> VerificacionEvidencia(true, archivo.absolutePath)
            }
        }

    /** Borra la evidencia fisica. Solo se usa al descartar un registro. */
    fun eliminar(ruta: String?) {
        if (ruta.isNullOrBlank()) return
        runCatching { File(ruta).delete() }
            .onFailure { Log.w(TAG, "No se pudo eliminar la evidencia $ruta", it) }
    }

    /** Metadatos que se envian a Firebase junto a los datos. */
    data class MetadatosEvidencia(
        val nombreArchivo: String,
        val tamanoBytes: Long,
        val anchoPx: Int,
        val altoPx: Int,
        val contenidoBase64: String
    )

    /**
     * Prepara el payload de la foto para la escritura en RTDB.
     * La foto viaja como Base64 dentro del nodo `evidencias/{idRegistro}`.
     * Con 1024 px y calidad 70 el Base64 queda por debajo de 200 KB, que es el
     * orden de magnitud que RTDB maneja sin problema.
     */
    fun prepararParaEnvio(ruta: String, idRegistro: String): Result<MetadatosEvidencia> = runCatching {
        val archivo = File(ruta)
        if (!archivo.exists()) error("No existe el archivo de evidencia $ruta")

        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, limites)
        if (limites.outWidth <= 0 || limites.outHeight <= 0) {
            error("No se pudieron leer las dimensiones de la evidencia")
        }
        val ancho = limites.outWidth
        val alto = limites.outHeight
        val base64 = Base64.encodeToString(archivo.readBytes(), Base64.NO_WRAP)

        MetadatosEvidencia(
            nombreArchivo = "$idRegistro.jpg",
            tamanoBytes = archivo.length(),
            anchoPx = ancho,
            altoPx = alto,
            contenidoBase64 = base64
        )
    }
}

/** Resultado de verificar que la evidencia existe y es utilizable. */
data class VerificacionEvidencia(val esValida: Boolean, val detalle: String) {
    val ruta: String? get() = if (esValida) detalle else null
}
