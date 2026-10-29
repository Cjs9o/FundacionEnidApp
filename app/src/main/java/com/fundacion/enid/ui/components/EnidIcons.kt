package com.fundacion.enid.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * ICONOS DE LÍNEA — mismo tema que el Figma de referencia
 * ---------------------------------------------------------------------------
 * El Figma no usa iconos rellenos de Material: dibuja trazos de 1.8 px sobre
 * una cuadrícula de 24, con esquinas y remates redondeados (`stroke-linecap`
 * y `stroke-linejoin` en `round`). Por eso aquí se construyen con `stroke` y
 * `fill = null`, en vez de rellenar el contorno como antes.
 *
 * Todos los nombres públicos se conservan para no tocar las pantallas que ya
 * los usan. El color final lo pone `Icon(..., tint = ...)`.
 */
private const val GROSOR_TRAZO = 1.8f

private fun trazo(name: String, vararg paths: String): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        for (p in paths) {
            addPath(
                pathData = PathParser().parsePathString(p).toNodes(),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = GROSOR_TRAZO,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            )
        }
    }.build()

object EnidIcons {

    // ── Navegación principal (los mismos glifos del Figma) ────────────────
    val VInicio by lazy {
        trazo("home", "m3 10 9-7 9 7", "M5 9v11h14V9", "M9 20v-7h6v7")
    }

    val VCalendario by lazy {
        trazo(
            "calendar",
            "M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2z",
            "M16 3v4", "M8 3v4", "M3 10h18"
        )
    }

    val VAyuda by lazy {
        trazo(
            "help",
            "M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20z",
            "M9.1 9a3 3 0 0 1 5.8 1c0 2-3 2.4-3 4",
            "M12 17.5h.01"
        )
    }

    val VPerfil by lazy {
        trazo("user", "M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8z", "M4 21a8 8 0 0 1 16 0")
    }

    val VCuenta by lazy {
        trazo(
            "account-circle",
            "M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20z",
            "M8.5 19.5c1-1.6 2.1-2.4 3.5-2.4s2.5.8 3.5 2.4",
            "M12 12a3 3 0 1 0 0-6 3 3 0 0 0 0 6z"
        )
    }

    // ── Secciones y acciones ──────────────────────────────────────────────
    val VOferta by lazy {
        trazo(
            "graduation-cap",
            "M22 10 12 5 2 10l10 5 10-5z",
            "M6 12v5c0 1.6 2.7 3 6 3s6-1.4 6-3v-5",
            "M22 10v6"
        )
    }

    val VPreinscripcion by lazy {
        trazo(
            "clipboard-list",
            "M9 4H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2h-2",
            "M9 2h6v4H9z",
            "M8 11h8", "M8 15h8", "M8 19h5"
        )
    }

    val VSedes by lazy {
        trazo(
            "map-pin",
            "M21 10c0 7-9 12-9 12S3 17 3 10a9 9 0 0 1 18 0z",
            "M12 13a3 3 0 1 0 0-6 3 3 0 0 0 0 6z"
        )
    }

    val VLlamar by lazy {
        trazo(
            "phone",
            "M22 16.9v3a2 2 0 0 1-2.2 2 19.8 19.8 0 0 1-8.6-3.1 19.5 19.5 0 0 1-6-6A19.8 19.8 0 0 1 2.1 4.2 2 2 0 0 1 4.1 2h3a2 2 0 0 1 2 1.7c.1.9.3 1.8.7 2.8a2 2 0 0 1-.5 2.1L8.1 9.9a16 16 0 0 0 6 6l1.3-1.3a2 2 0 0 1 2.1-.4c1 .4 1.9.6 2.8.7a2 2 0 0 1 1.7 2z"
        )
    }

    val VTelfLocal by lazy {
        trazo(
            "local-phone",
            "M22 16.9v3a2 2 0 0 1-2.2 2 19.8 19.8 0 0 1-8.6-3.1 19.5 19.5 0 0 1-6-6A19.8 19.8 0 0 1 2.1 4.2 2 2 0 0 1 4.1 2h3a2 2 0 0 1 2 1.7c.1.9.3 1.8.7 2.8a2 2 0 0 1-.5 2.1L8.1 9.9a16 16 0 0 0 6 6l1.3-1.3a2 2 0 0 1 2.1-.4c1 .4 1.9.6 2.8.7a2 2 0 0 1 1.7 2z"
        )
    }

    val VComoLlegar by lazy {
        trazo("navigation", "M3 11 22 2l-9 19-2-8-8-2z")
    }

    val VWhatsApp by lazy {
        trazo(
            "message-circle",
            "M21 11.5a8.4 8.4 0 0 1-9 8.4 8.6 8.6 0 0 1-3.8-.9L3 21l1.9-5.7a8.4 8.4 0 0 1-.9-3.8 8.5 8.5 0 0 1 8.5-8.5h.5a8.5 8.5 0 0 1 8 8z",
            "M8.5 12h.01", "M12 12h.01", "M15.5 12h.01"
        )
    }

    val VExterno by lazy {
        trazo(
            "external-link",
            "M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6",
            "M15 3h6v6", "M10 14 21 3"
        )
    }

    // ── Redes sociales oficiales ─────────────────────────────────────────
    // Se dibujan con el mismo trazo de 1.8 que el resto del Figma; el glifo
    // conserva la forma reconocible de cada red.

    val VFacebook by lazy {
        trazo(
            "facebook",
            "M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"
        )
    }

    val VInstagram by lazy {
        trazo(
            "instagram",
            "M7 2h10a5 5 0 0 1 5 5v10a5 5 0 0 1-5 5H7a5 5 0 0 1-5-5V7a5 5 0 0 1 5-5z",
            "M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z",
            "M17.5 6.5h.01"
        )
    }

    val VYoutube by lazy {
        trazo(
            "youtube",
            "M22.5 6.4a2.8 2.8 0 0 0-1.9-2C18.9 4 12 4 12 4s-6.9 0-8.6.4a2.8 2.8 0 0 0-1.9 2A29 29 0 0 0 1 11.8a29 29 0 0 0 .5 5.3A2.8 2.8 0 0 0 3.4 19c1.7.4 8.6.4 8.6.4s6.9 0 8.6-.4a2.8 2.8 0 0 0 1.9-2 29 29 0 0 0 .5-5.2 29 29 0 0 0-.5-5.4z",
            "m9.8 15 5.7-3.2-5.7-3.3v6.5z"
        )
    }

    val VCalculadora by lazy {
        trazo(
            "calculator",
            "M6 2h12a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z",
            "M8 6h8v3H8z",
            "M8 13h.01", "M12 13h.01", "M16 13h.01",
            "M8 17h.01", "M12 17h.01", "M16 17h.01"
        )
    }

    val VBuscar by lazy {
        trazo("search", "M11 19a8 8 0 1 0 0-16 8 8 0 0 0 0 16z", "M21 21l-4.3-4.3")
    }

    // ── Flechas y estados ─────────────────────────────────────────────────
    val VFlechaAtras by lazy { trazo("arrow-back", "M19 12H5", "m12 19-7-7 7-7") }

    val VFlechaAdelante by lazy { trazo("arrow-forward", "M5 12h14", "m12 5 7 7-7 7") }

    val VVolver by lazy { trazo("arrow-back", "M19 12H5", "m12 19-7-7 7-7") }

    val VExpandir by lazy { trazo("chevron-down", "m6 9 6 6 6-6") }

    val VContraer by lazy { trazo("chevron-up", "m18 15-6-6-6 6") }

    val VOk by lazy { trazo("check", "m20 6-11 11-5-5") }

    val VOkCirculo by lazy {
        trazo(
            "check-circle",
            "M22 11.1V12a10 10 0 1 1-5.9-9.1",
            "m22 4-10 10-3-3"
        )
    }

    val VHecho by lazy { trazo("done", "m20 6-11 11-5-5") }

    val VInfo by lazy {
        trazo("info", "M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20z", "M12 16v-4", "M12 8h.01")
    }

    val VCerrar by lazy { trazo("close", "M18 6 6 18", "M6 6l12 12") }

    val VAgregar by lazy { trazo("add", "M12 5v14", "M5 12h14") }

    val VCandado by lazy {
        trazo(
            "lock",
            "M5 11h14a2 2 0 0 1 2 2v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2z",
            "M7 11V7a5 5 0 0 1 10 0v4"
        )
    }

    val VMenu by lazy { trazo("menu", "M3 6h18", "M3 12h18", "M3 18h18") }

    val VApps by lazy {
        trazo(
            "apps",
            "M4 4h6v6H4z", "M14 4h6v6h-6z",
            "M4 14h6v6H4z", "M14 14h6v6h-6z"
        )
    }

    val VPrimera by lazy {
        trazo("first-page", "m17 18-6-6 6-6", "M7 18V6")
    }

    val VUltima by lazy {
        trazo("last-page", "m7 18 6-6-6-6", "M17 18V6")
    }

    val VEditar by lazy {
        trazo(
            "edit",
            "M11 4H5a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2h13a2 2 0 0 0 2-2v-6",
            "M18.5 2.5a2.1 2.1 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"
        )
    }

    val VCorreo by lazy {
        trazo(
            "mail",
            "M4 4h16a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z",
            "m22 7-10 5L2 7"
        )
    }

    val VHoy by lazy {
        trazo(
            "today",
            "M5 5h14a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2z",
            "M16 3v4", "M8 3v4", "M3 10h18", "m9 15 2 2 4-4"
        )
    }

    val VEvento by lazy {
        trazo(
            "event",
            "M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z",
            "M16 2v4", "M8 2v4", "M3 10h18", "M8 14h3"
        )
    }

    val VDescripcion by lazy {
        trazo(
            "file-text",
            "M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6z",
            "M14 2v6h6",
            "M16 13H8", "M16 17H8", "M10 9H8"
        )
    }

    // ── Aprender, agenda y perfil (glifos del Figma) ─────────────────────
    val VLibro by lazy {
        trazo(
            "book-open",
            "M4 4h6a3 3 0 0 1 3 3v13a3 3 0 0 0-3-3H4z",
            "M20 4h-4a3 3 0 0 0-3 3v13a3 3 0 0 1 3-3h4z"
        )
    }

    val VPlay by lazy {
        trazo("play", "m9 7 8 5-8 5z")
    }

    val VReloj by lazy {
        trazo("clock", "M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z", "M12 7v5l3 2")
    }

    val VCampana by lazy {
        trazo(
            "bell",
            "M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9",
            "M10 21h4"
        )
    }

    val VLogro by lazy {
        trazo(
            "award",
            "M12 15a6 6 0 1 0 0-12 6 6 0 0 0 0 12z",
            "M8.2 13.9 7 22l5-3 5 3-1.2-8.1"
        )
    }

    val VCertificado by lazy {
        trazo(
            "certificate",
            "M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6z",
            "M14 2v6h6",
            "M8 13h8", "M8 17h5"
        )
    }

    val VPrivacidad by lazy {
        trazo(
            "shield-check",
            "M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z",
            "m9 12 2 2 4-4"
        )
    }

    val VSalir by lazy {
        trazo(
            "log-out",
            "M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4",
            "m16 17 5-5-5-5", "M21 12H9"
        )
    }
}
