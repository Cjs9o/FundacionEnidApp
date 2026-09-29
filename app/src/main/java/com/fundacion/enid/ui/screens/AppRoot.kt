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
    val onTabNavigate: (Screen) -> Unit = { destino ->
        if (destino != nav.current) {
            if (destino == Screen.Home) nav.reemplazarTodo(Screen.Home)
            else nav.navegar(destino)
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
            onIrAInicio = { nav.reemplazarTodo(Screen.Welcome) }
        )

        is Screen.Catalog -> CatalogScreen(
            repository = repository,
            onBack = { nav.retroceder() },
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
            }
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
            repository = repository,
            onBack = { nav.retroceder() }
        )

        is Screen.Benefits -> BenefitsCalculatorScreen(
            repository = repository,
            onBack = { nav.retroceder() }
        )

        is Screen.Platform -> PlatformScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onGoHome = { nav.reemplazarTodo(Screen.Home) },
            onTabNavigate = onTabNavigate
        )
    }
}