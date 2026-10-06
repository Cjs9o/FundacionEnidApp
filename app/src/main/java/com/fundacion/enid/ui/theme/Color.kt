package com.fundacion.enid.ui.theme

import androidx.compose.ui.graphics.Color
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidFotoPlaceholder
import com.fundacion.enid.ui.shared.EnidSincronizadoFondo
import com.fundacion.enid.ui.shared.EnidSincronizadoTexto
import com.fundacion.enid.ui.shared.EnidVerdeSoft
import com.fundacion.enid.ui.shared.EnidVerdeWhatsapp
import com.fundacion.enid.ui.shared.EnidOro
import com.fundacion.enid.ui.shared.EnidOroSoft

/**
 * COLORES PROPIOS DE LA APP INSTITUCIONAL
 * ---------------------------------------------------------------------------
 * La paleta base y las medidas viven en `:ui` desde que `:campo` entró en el
 * proyecto. Ese módulo traía su propia copia y, entre las dos, había doce
 * colores con el mismo nombre y el mismo valor declarados dos veces, más seis
 * colores iguales con nombres distintos (`EnidGreen` / `EnidVerdeWhatsapp`).
 * Ahora hay un solo sitio: `com.fundacion.enid.ui.shared`.
 *
 * Este fichero conserva solo lo que la app institucional añade por su cuenta,
 * y mantiene los nombres cortos que ya usa el código como alias para que
 * ninguna pantalla cambie.
 */

// ── Alias de los colores compartidos ─────────────────────────────────────
// El nombre histórico de la app. Se dejan como alias, no como declaraciones
// nuevas: así el valor no puede volver a divergir del del módulo compartido.

/** Azul principal. Alias de [EnidDarkBlue]. */
val EnidBlue = EnidDarkBlue

/** Morado: se usa en PSE y en "Llamar a Sede". */
val EnidPurple = Color(0xFF322D6B)

/** Verde de contacto. Alias de [EnidVerdeWhatsapp]. */
val EnidGreen = EnidVerdeWhatsapp

/** Verde de WhatsApp. Alias de [EnidVerdeWhatsapp]. */
val EnidWhatsApp = EnidGreen

/** Negro puro. */
val EnidBlack = Color(0xFF000000)

/** Contorno de tarjetas y campos. Alias de la paleta compartida. */
val EnidBorder = com.fundacion.enid.ui.shared.EnidBorderSoft

/** Zona de foto o banner. Alias de [EnidFotoPlaceholder]. */
val EnidPhoto = EnidFotoPlaceholder

/** Azul muy claro, equivalente al fondo de la caja de ícono. */
val EnidSky = Color(0xFFEEEDF5)

/** Alias de [EnidSky]. */
val EnidBlueLight = EnidSky

/** Punto inactivo de la barra inferior (guía SVG). */
val EnidNavDot = Color(0xFFD1D5DB)

/** Tono ocre de la app. Alias de [EnidOro]. */
val EnidGold = EnidOro

/** Tono ocre claro de la app. Alias de [EnidOroSoft]. */
val EnidGoldSoft = EnidOroSoft

/** Tono verde claro de la app. Alias de [EnidVerdeSoft]. */
val EnidGreenSoft = EnidVerdeSoft

/** Morado claro de realce. */
val EnidPurpleSoft = Color(0xFFECE7F6)

// ── Chips de estado ───────────────────────────────────────────────────────
// El chip de resultado correcto es el color compartido de éxito. El de "por
// pagar" es propio de esta app: es una alerta de deuda, no un error de
// sistema, y por eso no se mezcla con el color de error de `:campo`.

/** Fondo del chip "Pagado". Alias del verde de éxito compartido. */
val EnidChipDoneBg = EnidSincronizadoFondo

/** Texto del chip "Pagado". Alias del verde de éxito compartido. */
val EnidChipDoneText = EnidSincronizadoTexto

/** Fondo del chip "Por pagar". */
val EnidChipWarnBg = Color(0xFFFDE8E9)

/** Texto del chip "Por pagar". */
val EnidChipWarnText = Color(0xFFC81920)