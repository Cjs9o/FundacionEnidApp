package com.fundacion.enid.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.NavController
import com.fundacion.enid.navigation.Screen

/**
 * Enrutador principal: decide qué pantalla mostrar según el tope de la pila.
 * También maneja el retroceso del sistema: cuando hay pantallas en pila, el
 * botón/gesto "atrás" del teléfono vuelve a la pantalla anterior.
 */
@Composable
fun AppRoot(
    nav: NavController,
    repository: DataRepository,
    onSessionChanged: () -> Unit
) {
    BackHandler(enabled = nav.puedeRetroceder) {
        nav.retroceder()
    }

    // Tabs de la barra inferior con historial real: al retroceder desde una
    // sección se vuelve a la pantalla anterior (y, en último término, a Inicio).
    // El tab "Inicio" reinicia la pila a la raíz.
    //
    // "Inicio" depende de la sesión: el invitado vuelve a su propio Home
    // (camino B) y el estudiante al suyo (camino A). Sin esto, el invitado que
    // abría el catálogo desde la barra terminaba en el Home de estudiante.
    val inicioDelUsuario: Screen =
        if (repository.estaConectado) Screen.Home else Screen.ProspectHome
    val onTabNavigate: (Screen) -> Unit = { destino ->
        val real = if (destino == Screen.Home) inicioDelUsuario else destino
        if (real != nav.current) {
            if (real == inicioDelUsuario) nav.reemplazarTodo(real)
            else nav.navegar(real)
        }
    }

    when (val pantalla = nav.current) {
        is Screen.Welcome -> WelcomeScreen(
            onLogin = { nav.navegar(Screen.Login) },
            onGuest = { nav.reemplazarTodo(Screen.ProspectHome) }
        )

        is Screen.Login -> AuthScreen(
            repository = repository,
            onLoginSuccess = {
                nav.reemplazarTodo(Screen.Home)
                onSessionChanged()
            },
            onGuest = { nav.reemplazarTodo(Screen.ProspectHome) },
            onBack = { nav.retroceder() }
        )

        is Screen.Home -> HomeScreen(
            repository = repository,
            onNavigate = { nav.navegar(it) },
            onTabNavigate = onTabNavigate
        )

        is Screen.ProspectHome -> ProspectHomeScreen(
            repository = repository,
            onNavigate = { nav.navegar(it) },
            onIrAInicio = { nav.reemplazarTodo(Screen.Welcome) },
            onTabNavigate = onTabNavigate
        )

        is Screen.Catalog -> CatalogScreen(
            repository = repository,
            onOpenProgram = { id -> nav.navegar(Screen.ProgramDetail(id)) },
            onOpenFinder = { nav.navegar(Screen.ProgramFinder) },
            onTabNavigate = onTabNavigate
        )

        is Screen.ProgramDetail -> ProgramDetailScreen(
            programId = pantalla.programId,
            repository = repository,
            onBack = { nav.retroceder() },
            onPreinscription = { id -> nav.navegar(Screen.Preinscription(id)) },
            onTabNavigate = onTabNavigate
        )

        is Screen.ProgramFinder -> ProgramFinderScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onOpenProgram = { id -> nav.navegar(Screen.ProgramDetail(id)) },
            onPreinscription = { id -> nav.navegar(Screen.Preinscription(id)) },
            onTabNavigate = onTabNavigate
        )

        is Screen.Intereses -> InteresesScreen(
            repository = repository,
            onOpenProgram = { id -> nav.navegar(Screen.ProgramDetail(id)) },
            onPreinscription = { id ->
                if (id == null) nav.navegar(Screen.Catalog) else nav.navegar(Screen.Preinscription(id))
            },
            onTabNavigate = onTabNavigate
        )

        is Screen.Locations -> LocationsScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onTabNavigate = onTabNavigate
        )

        is Screen.Faq -> FaqScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onTabNavigate = onTabNavigate
        )

        is Screen.Profile -> ProfileScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onLogout = {
                repository.cerrarSesion()
                nav.reemplazarTodo(Screen.Welcome)
                onSessionChanged()
            },
            onTabNavigate = onTabNavigate
        )

        is Screen.Preinscription -> PreinscriptionScreen(
            repository = repository,
            programaId = pantalla.programaId,
            onBack = { nav.retroceder() },
            onTabNavigate = onTabNavigate
        )

        is Screen.DailySchedule -> DailyScheduleScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onTabNavigate = onTabNavigate
        )

        is Screen.Payments -> PaymentsScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onPse = { nav.navegar(Screen.Pse) },
            onTabNavigate = onTabNavigate
        )

        is Screen.Pse -> PseScreen(
            onBack = { nav.retroceder() },
            onTabNavigate = onTabNavigate
        )

        is Screen.Benefits -> BenefitsCalculatorScreen(
            onBack = { nav.retroceder() },
            onTabNavigate = onTabNavigate
        )

        is Screen.Platform -> PlatformScreen(
            onBack = { nav.retroceder() },
            onTabNavigate = onTabNavigate
        )

        // El módulo de campo trae su propio NavHost. `onSalir` quita la hoja de
        // la pila en vez de reiniciarla: al volver, la app institucional debe
        // quedar donde estaba, no en el Inicio.
        is Screen.Campo -> CampoSeccion(onSalir = { nav.retroceder() })
    }
}