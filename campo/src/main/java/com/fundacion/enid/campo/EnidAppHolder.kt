package com.fundacion.enid.campo

import android.content.Context
import com.fundacion.enid.campo.data.EnidApp

/**
 * PUNTO DE ENTRADA DEL MODULO `:campo` DENTRO DE LA APP INSTITUCIONAL
 * ---------------------------------------------------------------------------
 * Antes de la integracion este modulo era una app Android completa y por eso
 * tenia su propia `Activity` (`MainActivity`) y su propia clase `Application`
 * (`EnidApplication`), que era quien construia el contenedor [EnidApp].
 *
 * Al pasar a ser un modulo de biblioteca dentro de `FundacionEnidApp` eso deja
 * de ser correcto: solo hay una `Activity` y un `Application`, los de la app
 * institucional. Por eso el contenedor se resuelve aqui, con el mismo criterio
 * de "una sola instancia para toda la app" pero sin robarle el `Application` a
 * la app que lo contiene.
 *
 * Se conserva la decision de arquitectura del modulo (sin Hilt ni Dagger, un
 * contenedor escrito a mano) porque sigue siendo defendible tal cual: lo unico
 * que cambia es quien lo guarda y cuando se construye, no que se construye.
 */
object EnidAppHolder {

    @Volatile
    private var instancia: EnidApp? = null

    /**
     * Devuelve el contenedor, construyendolo la primera vez.
     *
     * Se usa `applicationContext` a proposito: un contenedor guardado con el
     * `Context` de una pantalla mantiene viva esa pantalla y, con ella, su
     * `Activity`. El contexto de aplicacion vive todo el proceso.
     *
     * La doble comprobacion con [Volatile] es lo que hace que dos pantallas
     * abiertas a la vez (por ejemplo, resumen y listado) compartan la misma base
     * Room. Sin ella, dos hilos podrian construir dos contenedores y dos bases
     * sobre el mismo archivo, que es la forma tipica de "no such table".
     */
    fun obtener(contexto: Context): EnidApp =
        instancia ?: synchronized(this) {
            instancia ?: EnidApp(contexto.applicationContext).also { instancia = it }
        }
}