package com.fundacion.enid.campo.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * ICONOGRAFIA
 * ===========================================================================
 * Decision del grupo: NO se agrega `material-icons-extended` ni ningun paquete
 * de fuentes de iconos. Los 22 iconos que necesita la app se definen aqui con
 * los path data originales de Material Design, a 24dp.
 *
 * Justificacion tecnica: `material-icons-extended` son 2 MB de vectores que la
 * app descargaria en el APK para usar quince iconos. Ademas, tener las rutas
 * dentro del proyecto permite cambiar la marca de cada icono sin depender de una
 * version de la libreria.
 *
 * Todos se dibujan en negro y se tiñen en el punto de uso con `tint`.
 */
object EnidIconos {

    private fun icono(nombre: String, vararg trazos: String): ImageVector =
        ImageVector.Builder(
            name = nombre,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            trazos.forEach { trazo ->
                addPath(
                    pathData = PathParser().parsePathString(trazo).toNodes(),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f
                )
            }
        }.build()

    // ---------------------------------------------------------------- INICIO --
    val Inicio: ImageVector by lazy {
        icono(
            "Inicio",
            "M3,13h8L11,3L3,3v10zM3,21h8v-6L3,15v6zM13,21h8L21,11h-8v10zM13,3v6h8L21,3h-8z"
        )
    }

    /** Lista de registros locales. */
    val Listado: ImageVector by lazy {
        icono(
            "Listado",
            "M3,13h2v-2L3,11v2zM3,17h2v-2L3,15v2zM3,9h2L3,7v2zM7,13h14v-2L7,11v2zM7,17h14v-2L7,15v2zM7,7v2h14L21,7L7,7z"
        )
    }

    // -------------------------------------------------------------- CAPTURA --
    val Mas: ImageVector by lazy {
        icono("Mas", "M19,13h-6v6h-2v-6L5,13v-2h6L11,5h2v6h6v2z")
    }

    /** Camara: disparador de la evidencia fotografica. */
    val Camara: ImageVector by lazy {
        icono(
            "Camara",
            "M12,15.2a3.2,3.2 0,1 0,0 -6.4a3.2,3.2 0,0 0,0 6.4zM9,2L7.17,4L4,4c-1.1,0 -2,0.9 -2,2v12c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2L22,6c0,-1.1 -0.9,-2 -2,-2h-3.17L15,2L9,2zM12,17c-2.76,0 -5,-2.24 -5,-5s2.24,-5 5,-5s5,2.24 5,5s-2.24,5 -5,5z"
        )
    }

    /** Galeria: seleccionar la evidencia desde el dispositivo. */
    val Galeria: ImageVector by lazy {
        icono(
            "Galeria",
            "M22,16L22,4c0,-1.1 -0.9,-2 -2,-2L8,2c-1.1,0 -2,0.9 -2,2v12c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2zM11,12l2.03,2.71L16,11l4,5L8,16l3,-4zM2,6v14c0,1.1 0.9,2 2,2h14v-2L4,20L4,6L2,6z"
        )
    }

    // ------------------------------------------------------------- SINCRONIA --
    /** Nube con flecha: la unica accion que habla con el servidor. */
    val Sincronizar: ImageVector by lazy {
        icono(
            "Sincronizar",
            "M19.35,10.04C18.67,6.59 15.64,4 12,4 9.11,4 6.6,5.64 5.35,8.04 2.34,8.36 0,10.91 0,14c0,3.31 2.69,6 6,6h13c2.76,0 5,-2.24 5,-5 0,-2.64 -2.05,-4.78 -4.65,-4.96zM17,13l-5,-5 -5,5h3v4h4v-4h3z"
        )
    }

    // ---------------------------------------------------------------- ESTADO --
    /** Reloj: PENDIENTE. */
    val Reloj: ImageVector by lazy {
        icono(
            "Reloj",
            "M11.99,2C6.47,2 2,6.48 2,12s4.47,10 9.99,10C17.52,22 22,17.52 22,12S17.52,2 11.99,2zM12,20c-4.42,0 -8,-3.58 -8,-8s3.58,-8 8,-8s8,3.58 8,8s-3.58,8 -8,8zM12.5,7L11,7v6l5.25,3.15 0.75,-1.23 -4.5,-2.67z"
        )
    }

    /** Circulo con check: SINCRONIZADO. */
    val Confirmado: ImageVector by lazy {
        icono(
            "Confirmado",
            "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10s10,-4.48 10,-10S17.52,2 12,2zM10,17l-5,-5 1.41,-1.41L10,14.17l7.59,-7.59L19,8l-9,9z"
        )
    }

    /** Circulo con exclamacion: ERROR. */
    val Alerta: ImageVector by lazy {
        icono(
            "Alerta",
            "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10s10,-4.48 10,-10S17.52,2 12,2zM13,17h-2v-2h2v2zM13,13h-2L11,7h2v6z"
        )
    }

    // -------------------------------------------------------------- SIN RED --
    /** Nube tachada: el aviso de trabajo offline. */
    val SinConexion: ImageVector by lazy {
        icono(
            "SinConexion",
            "M24,8.98C20.93,5.9 16.69,4 12,4c-1.32,0 -2.61,0.15 -3.85,0.44l2.75,2.75c0.36,-0.06 0.73,-0.11 1.1,-0.11 3.54,0 6.55,2.31 7.6,5.5l2.37,-2.37c0.39,-0.7 0.07,-1.78 -0.6,-2.36zM2.28,3.5L1,4.77l2.05,2.06C1.91,7.4 0.73,8.63 0,10.07 0.1,10.22 0.15,10.37 0.15,10.55 0.15,13.1 2.19,15.14 4.74,15.14h10.67l1.44,1.44 -1.4,1.4 3.1,3.1 1.27,-1.27L3.7,4.92 2.28,3.5zM7.52,21.48l1.77,-1.77c-1.06,-0.85 -1.75,-2.09 -1.75,-3.45 0,-2.51 2.05,-4.55 4.57,-4.55l0.51,0.01 1.28,1.28 -1.28,1.28 1.28,1.28 1.51,1.51 -2.63,2.63 -0.99,-0.99 -2.07,-2.06z"
        )
    }

    // ------------------------------------------------------------ NAVEGACION --
    val Volver: ImageVector by lazy {
        icono(
            "Volver",
            "M20,11H7.83l5.59,-5.59L12,4l-8,8 8,8 1.41,-1.41L7.83,13H20v-2z"
        )
    }

    val Editar: ImageVector by lazy {
        icono(
            "Editar",
            "M3,17.25V21h3.75L17.81,9.94l-3.75,-3.75L3,17.25zM20.71,7.04c0.39,-0.39 0.39,-1.02 0,-1.41l-2.34,-2.34c-0.39,-0.39 -1.02,-0.39 -1.41,0l-1.83,1.83 3.75,3.75 1.83,-1.83z"
        )
    }

    val Ver: ImageVector by lazy {
        icono(
            "Ver",
            "M12,4.5C7,4.5 2.73,7.61 1,12c1.73,4.39 6,7.5 11,7.5s9.27,-3.11 11,-7.5c-1.73,-4.39 -6,-7.5 -11,-7.5zM12,17c-2.76,0 -5,-2.24 -5,-5s2.24,-5 5,-5s5,2.24 5,5s-2.24,5 -5,5zM12,9c-1.66,0 -3,1.34 -3,3s1.34,3 3,3s3,-1.34 3,-3s-1.34,-3 -3,-3z"
        )
    }

    /** Lupa sobre documento: el bloque de evidencia fotografica. */
    val Documento: ImageVector by lazy {
        icono(
            "Documento",
            "M14,2L6,2c-1.1,0 -1.99,0.9 -1.99,2L4,20c0,1.1 0.89,2 1.99,2L18,22c1.1,0 2,-0.9 2,-2L20,8l-6,-6zM16,18L8,18v-2h8v2zM16,14L8,14v-2h8v2zM13,9L13,3.5L18.5,9L13,9z"
        )
    }

    val Usuario: ImageVector by lazy {
        icono(
            "Usuario",
            "M12,12c2.21,0 4,-1.79 4,-4s-1.79,-4 -4,-4s-4,1.79 -4,4s1.79,4 4,4zM12,14c-2.67,0 -8,1.34 -8,4v2h16v-2c0,-2.66 -5.33,-4 -8,-4z"
        )
    }

    val Telefono: ImageVector by lazy {
        icono(
            "Telefono",
            "M6.62,10.79c1.44,2.83 3.76,5.14 6.59,6.59l2.2,-2.2c0.27,-0.27 0.67,-0.36 1.02,-0.24 1.12,0.37 2.33,0.57 3.57,0.57 0.55,0 1,0.45 1,1L20,20c0,0.55 -0.45,1 -1,1 -9.39,0 -17,-7.61 -17,-17 0,-0.55 0.45,-1 1,-1h3.5c0.55,0 1,0.45 1,1 0,1.25 0.2,2.45 0.57,3.57 0.11,0.35 0.03,0.74 -0.25,1.02l-2.2,2.2z"
        )
    }

    val Correo: ImageVector by lazy {
        icono(
            "Correo",
            "M20,4L4,4c-1.1,0 -1.99,0.9 -1.99,2L2,18c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2L22,6c0,-1.1 -0.9,-2 -2,-2zM20,8l-8,5 -8,-5L4,6l8,5 8,-5v2z"
        )
    }

    val Calendario: ImageVector by lazy {
        icono(
            "Calendario",
            "M19,3h-1L18,1h-2v2L8,3L8,1L6,1v2L5,3c-1.11,0 -1.99,0.9 -1.99,2L3,19c0,1.1 0.89,2 2,2h14c1.1,0 2,-0.9 2,-2L21,5c0,-1.1 -0.9,-2 -2,-2zM19,19L5,19L5,8h14v11z"
        )
    }

    val Ubicacion: ImageVector by lazy {
        icono(
            "Ubicacion",
            "M12,2C8.13,2 5,5.13 5,9c0,5.25 7,13 7,13s7,-7.75 7,-13c0,-3.87 -3.13,-7 -7,-7zM12,11.5c-1.38,0 -2.5,-1.12 -2.5,-2.5s1.12,-2.5 2.5,-2.5s2.5,1.12 2.5,2.5s-1.12,2.5 -2.5,2.5z"
        )
    }

    val Etiqueta: ImageVector by lazy {
        icono(
            "Etiqueta",
            "M21.41,11.58l-9,-9C12.05,2.22 11.55,2 11,2H4c-1.1,0 -2,0.9 -2,2v7c0,0.55 0.22,1.05 0.59,1.42l9,9c0.36,0.36 0.86,0.58 1.41,0.58s1.05,-0.22 1.41,-0.59l7,-7c0.37,-0.36 0.59,-0.86 0.59,-1.41s-0.23,-1.06 -0.59,-1.42zM5.5,7C4.67,7 4,6.33 4,5.5S4.67,4 5.5,4S7,4.67 7,5.5S6.33,7 5.5,7z"
        )
    }

    val Basura: ImageVector by lazy {
        icono(
            "Basura",
            "M6,19c0,1.1 0.9,2 2,2h8c1.1,0 2,-0.9 2,-2L18,7L6,7v12zM19,4h-3.5l-1,-1h-5l-1,1L5,4v2h14L19,4z"
        )
    }

    val Cerrar: ImageVector by lazy {
        icono(
            "Cerrar",
            "M19,6.41L17.59,5 12,10.59 6.41,5 5,6.41 10.59,12 5,17.59 6.41,19 12,13.41 17.59,19 19,17.59 13.41,12z"
        )
    }

    val Filtro: ImageVector by lazy {
        icono(
            "Filtro",
            "M10,18h4v-2h-4v2zM3,6v2h18L21,6L3,6zM6,13h12v-2L6,11v2z"
        )
    }

    val Reintentar: ImageVector by lazy {
        icono(
            "Reintentar",
            "M17.65,6.35C16.2,4.9 14.21,4 12,4c-4.42,0 -7.99,3.58 -8,8s3.58,8 8,8c3.73,0 6.84,-2.55 7.73,-6h-2.08c-0.82,2.33 -3.04,4 -5.65,4 -3.31,0 -6,-2.69 -6,-6s2.69,-6 6,-6c1.66,0 3.14,0.69 4.22,1.78L13,11h7V4l-2.35,2.35z"
        )
    }

    /** Chevron hacia abajo: indica que el campo abre una lista. */
    val FlechaAbajo: ImageVector by lazy {
        icono(
            "FlechaAbajo",
            "M7.41,8.59L12,13.17l4.59,-4.58L18,10l-6,6 -6,-6 1.41,-1.41z"
        )
    }

    val Escudo: ImageVector by lazy {
        icono(
            "Escudo",
            "M12,1L3,5v6c0,5.55 3.84,10.74 9,12 5.16,-1.26 9,-6.45 9,-12L21,5l-9,-4zM10,17l-4,-4 1.41,-1.41L10,14.17l6.59,-6.59L18,9l-8,8z"
        )
    }
}
