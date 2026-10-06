package com.fundacion.enid.ui.shared

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * MEDIDAS DEL SISTEMA VISUAL
 * ---------------------------------------------------------------------------
 * Las medidas son la parte más fácil de tener contradictoria: `14.dp` aparecía
 * dieciocho veces escrito a mano en `:app` y `20.dp` cinco veces, mientras
 * `:campo` ya tenía su propio `EnidDimens` con los mismos números y otro
 * nombre. Nombrarlas una vez hace que "el margen de pantalla" signifique algo
 * concreto y que se pueda cambiar en un sitio.
 *
 * Guías del sistema visual (guías SVG del proyecto):
 *   · Margen horizontal de pantalla : 20 dp
 *   · Separación entre tarjetas    : 12 dp
 *   · Separación entre secciones   : 20 dp
 *   · Padding interno de tarjeta   : 14 dp
 *   · Alto mínimo táctil           : 48 dp
 */
object EnidDimens {

    /** Margen horizontal de la pantalla. */
    val margenPantalla: Dp = 20.dp

    /** Separación entre tarjetas apiladas. */
    val separacionTarjeta: Dp = 12.dp

    /** Separación entre secciones de una pantalla. */
    val separacionSeccion: Dp = 20.dp

    /** Padding interno de una tarjeta. */
    val paddingTarjeta: Dp = 14.dp

    /**
     * Alto mínimo del área táctil. Accessibility: 48 dp es el mínimo
     * recomendable y la app está pensada para mayores de 60 años, así que
     * ningún control interactivo debería bajar de aquí.
     */
    val altoTactilMinimo: Dp = 48.dp

    /** Radio de esquina de las tarjetas. */
    val radioTarjeta: Dp = 12.dp

    /** Radio de esquina de campos y botones secundarios. */
    val radioCampo: Dp = 8.dp

    /** Alto del botón primario. */
    val altoBotonPrimario: Dp = 56.dp
}