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
 * Referencia de diseño: "Proyecto movil.svg".
 */

// ── Marca institucional ────────────────────────────────────────────────────

/** #322D6B — azul institucional: botones activos, tabs, CTA. */
val EnidDarkBlue = Color(0xFF322D6B)

/** #3A3A6E — azul de apoyo. */
val EnidBlueBright = Color(0xFF3A3A6E)

/** #DCE4F6 — azul claro: fondos de realce. */
val EnidBlueSoft = Color(0xFFDCE4F6)

/** #C81920 — rojo institucional: CTA y punto activo. */
val EnidRed = Color(0xFFC81920)

/** #F5BDB8 — coral / rojo claro: fondo del tab activo. */
val EnidRedSoft = Color(0xFFF5BDB8)

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

/** #EEEDF5 — caja de ícono y superficies secundarias. */
val EnidIconBox = Color(0xFFEEEDF5)