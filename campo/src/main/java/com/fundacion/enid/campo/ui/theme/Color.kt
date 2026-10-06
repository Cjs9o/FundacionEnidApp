package com.fundacion.enid.campo.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * COLORES PROPIOS DEL MÓDULO DE CAMPO
 * ---------------------------------------------------------------------------
 * La paleta vive desde la integración en `:ui` (módulo compartido). Este
 * fichero queda solo con lo que el módulo de campo añade de su lado: el ámbar
 * del estado pendiente y el fondo/rojo del estado de error.
 *
 * Antes de integrarse, `:campo` traía copia de la paleta completa. Con dos
 * copias en el mismo APK, cambiar un color en un módulo dejaba el otro
 * desalineado sin aviso.
 */

// ── Estado PENDIENTE ------------------------------------------------------
/**
 * #F59E0B — Punto e indicador del estado pendiente y de la barra de progreso
 * de la sincronización. Contrasta 2.1:1 sobre blanco: NUNCA como color de texto.
 */
val EnidPendiente = Color(0xFFF59E0B)

/** #FEF3C7 — Fondo del chip "Pendiente". */
val EnidPendienteFondo = Color(0xFFFEF3C7)

/** #92400E — Texto del chip "Pendiente". Contraste 6.4:1 sobre su fondo. */
val EnidPendienteTexto = Color(0xFF92400E)

// ── Estado ERROR ----------------------------------------------------------
/**
 * Fondo del chip "Error" y de los banners de fallo.
 *
 * Deliberadamente **no** se fusiona con el chip "Por pagar" de `:app`, aunque
 * ambos sean rojizos: significan cosas distintas (fallo de captura frente a
 * deuda pendiente) y confundirlos haría que un error de sincronización pareciera
 * un aviso de pago.
 */
val EnidErrorFondo = Color(0xFFFDE8E9)

/** Texto del chip "Error". */
val EnidErrorTexto = Color(0xFFB91C1C)