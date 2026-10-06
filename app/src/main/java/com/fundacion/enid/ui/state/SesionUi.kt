package com.fundacion.enid.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Estado de sesión que la capa visual puede leer.
 *
 * Existe para que `AppScaffold` decida **por sí mismo** qué pestañas pinta.
 * Antes cada pantalla pasaba `tabs = tabsSegunSesion(repository.estaConectado)` y
 * ocho de ellas se olvidaron, así que el invitado veía las pestañas de
 * estudiante. Al leerlo aquí, se acaban las dos formas de equivocarse:
 * no pasar nada, o pasarlo a mano y equivocarse.
 *
 * Se actualiza desde [com.fundacion.enid.MainActivity.AppEntry] con el mismo
 * valor que ya alimentaba `onSessionChanged`, de modo que la app tiene una sola
 * fuente de verdad sobre quién está mirando.
 */
object SesionUi {

    var conectado by mutableStateOf(false)
        private set

    fun actualizar(conectado: Boolean) {
        if (this.conectado != conectado) this.conectado = conectado
    }
}