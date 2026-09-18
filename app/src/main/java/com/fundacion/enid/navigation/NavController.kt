package com.fundacion.enid.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Controlador de navegación por pila (LIFO).
 * - navegar() agrega una pantalla al tope.
 * - retroceder() quita el tope.
 * Observa si se puede retroceder para ocultar botones "Atrás".
 */
class NavController(initial: Screen) {

    private val pila = mutableListOf(initial)

    var current: Screen by mutableStateOf(initial)
        private set

    val puedeRetroceder: Boolean
        get() = pila.size > 1

    fun navegar(destino: Screen) {
        pila.add(destino)
        current = destino
    }

    fun retroceder() {
        if (pila.size > 1) {
            pila.removeAt(pila.size - 1)
            current = pila[pila.size - 1]
        }
    }

    fun reemplazarTodo(destino: Screen) {
        pila.clear()
        pila.add(destino)
        current = destino
    }
}