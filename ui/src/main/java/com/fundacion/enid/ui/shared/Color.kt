package com.fundacion.enid.ui.shared

import androidx.compose.ui.graphics.Color

/**
 * PALETA INSTITUCIONAL COMPARTIDA
 * ---------------------------------------------------------------------------
 * Este es el único lugar del proyecto donde se declaran los colores base de
 * ENID. Antes de integrar `:campo`, cada módulo traía su propio `Color.kt` con
 * los mismos doce nombres y los mismos valores: dos copias de la misma paleta
 * que podían divergir sin que nada lo notara.
 *
 * Los módulos siguen declarando **solo lo propio**: `:app` añade los colores
 * de sus chips y `:campo` los de sus estados de sincronización. Lo que no
 * vuelve a pasar es a declararse dos veces un color de la marca.
 *
 * Referencia de diseño: "Proyecto movil.svg" y paleta oficial entregada el
 * 27 Oct 2026.
 */

// ── Marca institucional ────────────────────────────────────────────────────

/**
 * #352F6F — violeta institucional: estructura, navegación y superficies de
 * marca. Es el color de mayor peso después del rojo.
 */
val EnidDarkBlue = Color(0xFF352F6F)

/**
 * #4B6195 — azul de apoyo: fondos, degradados y realces. Acompaña sin
 * competir con el rojo de conversión.
 */
val EnidBlueMid = Color(0xFF4B6195)

/** Alias histórico del azul de apoyo. */
val EnidBlueBright = EnidBlueMid

/** #E3E8F3 — azul muy suave para fondos de realce. */
val EnidBlueSoft = Color(0xFFE3E8F3)

/**
 * #C11721 — rojo institucional: CTA, etiquetas, fechas, navegación activa y
 * elementos de conversión. Es el acento con más protagonismo.
 */
val EnidRed = Color(0xFFC11721)

/** #F7DADC — rojo suave: fondo de chips y tarjetas con acento rojo. */
val EnidRedSoft = Color(0xFFF7DADC)

/**
 * #2BA7A2 — turquesa: progreso, lecciones completadas y estados positivos.
 */
val EnidTeal = Color(0xFF2BA7A2)

/**
 * #176B68 — turquesa oscuro para texto y glifos sobre fondos claros.
 * El turquesa puro no alcanza contraste AA como texto (2.9:1 sobre blanco);
 * este tono sí (5.1:1) y conserva la misma familia cromática.
 */
val EnidTealDark = Color(0xFF176B68)

/** #DEF3F1 — fondo suave turquesa para estados completados. */
val EnidTealSoft = Color(0xFFDEF3F1)

// ── Superficies y texto ────────────────────────────────────────────────────

/** #F4F3EF — fondo crema claro de pantalla. */
val EnidBg = Color(0xFFF4F3EF)

/** #FFFFFF — tarjetas y campos. */
val EnidCardBg = Color(0xFFFFFFFF)

/** #FFFFFF — blanco puro. */
val EnidWhite = Color(0xFFFFFFFF)

/** #111827 — texto principal. */
val EnidInk = Color(0xFF111827)

/** #4B5563 — texto secundario y placeholders. */
val EnidTextSec = Color(0xFF4B5563)

/** #D1D5DB — contorno de tarjetas y campos. */
val EnidBorderSoft = Color(0xFFD1D5DB)

/** #ECEBF4 — caja de ícono y superficies secundarias (violeta muy suave). */
val EnidIconBox = Color(0xFFECEBF4)
