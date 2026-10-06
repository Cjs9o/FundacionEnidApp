package com.fundacion.enid.campo.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fundacion.enid.campo.data.EnidApp
import com.fundacion.enid.campo.ui.screens.PantallaCaptura
import com.fundacion.enid.campo.ui.screens.PantallaDetalle
import com.fundacion.enid.campo.ui.screens.PantallaListado
import com.fundacion.enid.campo.ui.screens.PantallaResumen
import com.fundacion.enid.campo.ui.vm.CapturaViewModel
import com.fundacion.enid.campo.ui.vm.DetalleViewModel
import com.fundacion.enid.campo.ui.vm.FabricaViewModel
import com.fundacion.enid.campo.ui.vm.ListadoViewModel
import com.fundacion.enid.campo.ui.vm.ResumenViewModel

/**
 * RUTAS DE LA APLICACION
 * ---------------------------------------------------------------------------
 * Cuatro pantallas, cuatro rutas. El taller las pide exactamente asi:
 * "captura, listado, revisión" mas el "inicio/resumen".
 */
object Rutas {
    const val RESUMEN = "resumen"
    const val LISTADO = "listado"
    const val CAPTURA_NUEVA = "captura"
    const val CAPTURA_EDICION = "captura/{idRegistro}"
    const val DETALLE = "detalle/{idRegistro}"

    const val ARG_ID = "idRegistro"

    fun edicion(id: String) = "captura/$id"
    fun detalle(id: String) = "detalle/$id"
}

/**
 * GRAFO DE NAVEGACION
 * ---------------------------------------------------------------------------
 * DECISION DEL GRUPO: `navigation-compose` con `NavHost`, no una pila manual como
 * el proyecto institucional base. La razon es concreta: el taller exige que al
 * editar y volver, y al navegar listado -> detalle -> captura, la pantalla
 * conserve su estado, y `NavHost` lo resuelve con el back stack real de Android
 * en vez de con una lista propia que puede desincronizarse del sistema.
 */
@Composable
fun EnidAppRaiz(
    app: EnidApp,
    onSalir: (() -> Unit)? = null,
    navController: NavHostController = rememberNavController()
) {
    val fabrica = FabricaViewModel(app)

    NavHost(navController = navController, startDestination = Rutas.RESUMEN) {

        composable(Rutas.RESUMEN) {
            val vm: ResumenViewModel = viewModel(factory = fabrica)
            PantallaResumen(
                vm = vm,
                onNuevaCaptura = { navController.navigate(Rutas.CAPTURA_NUEVA) },
                onVerListado = { navController.navigate(Rutas.LISTADO) },
                onSalir = onSalir
            )
        }

        composable(Rutas.LISTADO) {
            val vm: ListadoViewModel = viewModel(factory = fabrica)
            PantallaListado(
                vm = vm,
                onVolver = { navController.popBackStack() },
                onNuevaCaptura = {
                    navController.navigate(Rutas.CAPTURA_NUEVA) {
                        popUpTo(Rutas.RESUMEN)
                    }
                },
                onAbrirDetalle = { id -> navController.navigate(Rutas.detalle(id)) }
            )
        }

        composable(Rutas.CAPTURA_NUEVA) {
            CapturaDestino(
                idRegistro = null,
                app = app,
                fabrica = fabrica,
                alSalir = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.CAPTURA_EDICION,
            arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.StringType })
        ) { entrada ->
            CapturaDestino(
                idRegistro = entrada.arguments?.getString(Rutas.ARG_ID),
                app = app,
                fabrica = fabrica,
                alSalir = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.StringType })
        ) { entrada ->
            val id = entrada.arguments?.getString(Rutas.ARG_ID).orEmpty()
            fabrica.idRegistro = id
            val vm: DetalleViewModel = viewModel(key = "detalle-$id", factory = fabrica)
            PantallaDetalle(
                vm = vm,
                onVolver = { navController.popBackStack() },
                onEditar = { idRegistro -> navController.navigate(Rutas.edicion(idRegistro)) }
            )
        }
    }
}

/**
 * Envuelve la pantalla de captura para no duplicar la obtencion del ViewModel
 * entre la ruta de captura nueva y la de edicion: la unica diferencia es el id.
 */
@Composable
private fun CapturaDestino(
    idRegistro: String?,
    app: EnidApp,
    fabrica: FabricaViewModel,
    alSalir: () -> Unit
) {
    val clave = idRegistro ?: "nueva"
    fabrica.idRegistro = idRegistro.orEmpty()
    val vm: CapturaViewModel = viewModel(key = "captura-$clave", factory = fabrica)
    PantallaCaptura(
        vm = vm,
        app = app,
        onGuardado = { alSalir() },
        onEditado = { alSalir() },
        onCancelado = { alSalir() }
    )
}
