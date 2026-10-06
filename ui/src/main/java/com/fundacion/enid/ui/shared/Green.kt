package com.fundacion.enid.ui.shared

import androidx.compose.ui.graphics.Color

/**
 * COLORES SECUNDARIOS COMPARTIDOS
 * ---------------------------------------------------------------------------
 * Cada valor de aquí estaba declarado en los dos módulos con dos nombres
 * distintos: `:app` lo llamaba `EnidGreen` y `:campo` lo llamaba
 * `EnidVerdeWhatsapp`; `:app` decía `EnidGold` y `:campo` `EnidOro`; el verde de
 * "exito" era `EnidChipDoneBg` en un lado y `EnidSincronizadoFondo` en el otro.
 * Seis veces el mismo color por duplicado.
 *
 * Aquí queda **un solo nombre** por color. Cada módulo conserva el suyo como
 * alias (`val EnidGreen = EnidVerdeWhatsapp`), así que ninguna pantalla cambia
 * y el nombre corto sigue leyéndose bien en el código que ya existe.
 */

// ── Verde de contacto (WhatsApp y soporte) ────────────────────────────────

/** #25D366 — verde de WhatsApp. */
val EnidVerdeWhatsapp = Color(0xFF25D366)

/** #DCF7E3 — verde claro de realce. */
val EnidVerdeSoft = Color(0xFFDCF7E3)

// ── Ocre de acento ────────────────────────────────────────────────────────

/** #B08D00 — ocre: "vence pronto", detalles de realce. */
val EnidOro = Color(0xFFB08D00)

/** #F7EFD9 — ocre claro de fondo. */
val EnidOroSoft = Color(0xFFF7EFD9)

// ── Verde de estado correcto ──────────────────────────────────────────────

/**
 * #DCFCE7 — fondo de los chips de resultado correcto. En `:app` marcaba
 * "Pagado" y en `:campo` "Sincronizado": mismo color, mismo significado.
 */
val EnidSincronizadoFondo = Color(0xFFDCFCE7)

/** #15803D — texto de los chips de resultado correcto. Contraste 4.6:1. */
val EnidSincronizadoTexto = Color(0xFF15803D)

// ── Fotografía ────────────────────────────────────────────────────────────

/** #CBD5C0 — zona vacía de la fotografía de evidencia. */
val EnidFotoPlaceholder = Color(0xFFCBD5C0)