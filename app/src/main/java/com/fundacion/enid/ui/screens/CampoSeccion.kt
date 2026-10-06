package com.fundacion.enid.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.fundacion.enid.campo.EnidAppHolder
import com.fundacion.enid.campo.ui.EnidAppRaiz
import com.fundacion.enid.campo.ui.theme.EnidAppTheme

/**
 * ANFITRIONA DEL MÓDULO DE CAPTURA EN CAMPO
 * ---------------------------------------------------------------------------
 * El módulo `:campo` (offline-first, antes app independiente) expone un solo
 * composable público: [EnidAppRaiz], con su propio `NavHost` de sus cuatro
 * pantallas (resumen, listado, captura y detalle).
 *
 * Esta función es todo lo que la app institucional necesita saber de él. A
 * propósito no hay nada más: ni modelos, ni `Dao`, ni `SyncManager`, ni el
 * tema. `:app` no puede tocar la base de datos de campo porque no la ve, y el
 * módulo no sabe nada de catálogos, sesión ni preinscripción.
 *
 * Las dos decisiones que sí importan:
 *
 * **1. `EnidAppTheme` anidado dentro de `FundacionEnidTheme`.** Compose permite
 *    anidar `MaterialTheme`: el interno manda en su subárbol y el externo
 *    vuelve a tomar el control al salir. Por eso la captura en campo se ve
 *    igual que en su app original, sin repintar la app institucional.
 *
 * **2. `remember` para el contenedor.** [EnidAppHolder] ya garantiza una sola
 *    instancia por proceso; el `remember` solo evita reconstruir la clave en
 *    cada recomposición. Sin él, se pediría el contenedor en cada recomposición
 *    y `Room` se abriría y cerraría en el camino.
 */
@Composable
fun CampoSeccion(onSalir: () -> Unit) {
    val contexto = LocalContext.current
    val contenedor = remember(contexto) { EnidAppHolder.obtener(contexto) }

    EnidAppTheme {
        EnidAppRaiz(app = contenedor, onSalir = onSalir)
    }
}