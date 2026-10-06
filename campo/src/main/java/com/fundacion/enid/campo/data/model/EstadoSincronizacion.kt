package com.fundacion.enid.campo.data.model

/**
 * ESTADO DE SINCRONIZACIÓN DE UN REGISTRO
 * ---------------------------------------------------------------------------
 * Este enum es la única fuente de verdad del estado local. La decisión del
 * grupo (Matriz de decisiones, fila "Cómo se determinará que un registro ya
 * fue enviado") fue NO consultar Firebase antes de enviar: el estado vive en
 * Room y se actualiza sólo cuando el servidor confirma.
 */
enum class EstadoSincronizacion(val almacenado: String, val etiqueta: String) {

    /** Guardado en Room, sin confirmar en Firebase. Disponible para sincronización. */
    PENDIENTE("PENDIENTE", "Pendiente"),

    /** Firebase confirmó que lo guardó. No volver a crear un duplicado. */
    SINCRONIZADO("SINCRONIZADO", "Sincronizado"),

    /** Falló por algo del propio registro (p. ej. falta la foto). Mostrar causa y reintentar. */
    ERROR("ERROR", "Error");

    /** Un registro es sincronizable si y sólo si está en PENDIENTE o ERROR. */
    val esSincronizable: Boolean get() = this == PENDIENTE || this == ERROR

    /** Un registro sólo es editable mientras no se haya confirmado en el servidor. */
    val esEditable: Boolean get() = this != SINCRONIZADO

    companion object {
        fun desde(texto: String?): EstadoSincronizacion =
            entries.firstOrNull { it.almacenado.equals(texto, ignoreCase = true) } ?: PENDIENTE
    }
}
