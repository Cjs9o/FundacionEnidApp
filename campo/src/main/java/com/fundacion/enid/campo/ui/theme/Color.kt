package com.fundacion.enid.campo.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * SISTEMA VISUAL — ENID App
 * ---------------------------------------------------------------------------
 * Paleta institucional definida por el equipo (Fase 5 del taller).
 *
 * Cada token está justificado en la matriz de decisiones visuales. Los colores
 * se replican aquí tal como se definieron en el grupo, sin sustituirlos por
 * los defaults de Material 3.
 */

// ---- Marca institucional ---------------------------------------------------
/** #322D6B — Botón GUARDAR, SINCRONIZAR, enlaces, icono de la cabecera. */
val EnidDarkBlue = Color(0xFF322D6B)
val EnidBlueBright = Color(0xFF3A3A6E)
val EnidBlueSoft = Color(0xFFDCE4F6)

/** #C81920 — Acento institucional. Botón de captura, errores, cámara. */
val EnidRed = Color(0xFFC81920)
val EnidRedSoft = Color(0xFFF5BDB8)

// ---- Superficies y texto ---------------------------------------------------
/** #F4F3EF — Fondo de pantalla. */
val EnidBg = Color(0xFFF4F3EF)
/** #FFFFFF — Tarjetas y campos. */
val EnidCardBg = Color(0xFFFFFFFF)
val EnidWhite = Color(0xFFFFFFFF)
/** #111827 — Texto principal. */
val EnidInk = Color(0xFF111827)
/** #4B5563 — Etiquetas y textos secundarios. */
val EnidTextSec = Color(0xFF4B5563)
/** #D1D5DB — Contorno de tarjetas y campos. */
val EnidBorderSoft = Color(0xFFD1D5DB)

/** Caja de ícono / superficies secundarias. */
val EnidIconBox = Color(0xFFEEEDF5)
/** Zona vacía de la evidencia fotográfica. */
val EnidPhotoPlaceholder = Color(0xFFCBD5C0)

// ---- Estado PENDIENTE ------------------------------------------------------
/**
 * #F59E0B — Punto e indicador del estado pendiente y de la barra de progreso
 * de la sincronización. Contrasta 2.1:1 sobre blanco: NUNCA como color de texto.
 */
val EnidPendiente = Color(0xFFF59E0B)
/** #FEF3C7 — Fondo del chip "Pendiente". */
val EnidPendienteFondo = Color(0xFFFEF3C7)
/** #92400E — Texto del chip "Pendiente". Contraste 6.4:1 sobre su fondo. */
val EnidPendienteTexto = Color(0xFF92400E)

// ---- Estado SINCRONIZADO --------------------------------------------------
/** #DCFCE7 — Fondo del chip "Sincronizado" (= EnidChipDoneBg). */
val EnidSincronizadoFondo = Color(0xFFDCFCE7)
/** #15803D — Texto del chip "Sincronizado". Contraste 4.6:1 sobre su fondo. */
val EnidSincronizadoTexto = Color(0xFF15803D)

// ---- Estado ERROR ----------------------------------------------------------
/** Fondo del chip "Error" y de los banners de fallo. */
val EnidErrorFondo = Color(0xFFFDE8E9)
/** Texto del chip "Error". */
val EnidErrorTexto = Color(0xFFB91C1C)

// ---- Offline / navegación segura ------------------------------------------
val EnidVerdeWhatsapp = Color(0xFF25D366)
val EnidVerdeSoft = Color(0xFFDCF7E3)
val EnidOro = Color(0xFFB08D00)
val EnidOroSoft = Color(0xFFF7EFD9)
