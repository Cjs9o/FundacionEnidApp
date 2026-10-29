package com.fundacion.enid.ui.theme

import androidx.compose.ui.graphics.Color
import com.fundacion.enid.ui.shared.EnidBlueMid
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidFotoPlaceholder
import com.fundacion.enid.ui.shared.EnidTealDark
import com.fundacion.enid.ui.shared.EnidTealSoft
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

/** Azul de apoyo. Alias de [EnidBlueMid] (#4B6195). */
val EnidBlue = EnidBlueMid

/** Violeta institucional: se usa en "Llamar a Sede" y en la identidad. */
val EnidPurple = Color(0xFF352F6F)

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
val EnidSky = Color(0xFFECEBF4)

/** Alias de [EnidSky]. */
val EnidBlueLight = EnidSky

/** Punto inactivo de la barra inferior (guía SVG). */
val EnidNavDot = Color(0xFFD1D5DB)

/** Tono ocre de la app. Alias de [EnidOro]. */
val EnidGold = EnidOro

/** Tono ocre claro de la app. Alias de [EnidOroSoft]. */
val EnidGoldSoft = EnidOroSoft

/**
 * Fondo suave de estados completados y progreso. Ya no es verde: la paleta
 * reserva el turquesa para "hecho / positivo", y el verde queda solo para el
 * contacto de WhatsApp, que es una marca externa.
 */
val EnidGreenSoft = EnidTealSoft

/** Violeta claro de realce. */
val EnidPurpleSoft = Color(0xFFECE9F4)

// ── Chips de estado ───────────────────────────────────────────────────────
// El chip de resultado correcto es el color compartido de éxito.

/** Fondo del chip "Completado": turquesa suave. */
val EnidChipDoneBg = EnidTealSoft

/** Texto del chip "Completado": turquesa oscuro (AA sobre el fondo). */
val EnidChipDoneText = EnidTealDark