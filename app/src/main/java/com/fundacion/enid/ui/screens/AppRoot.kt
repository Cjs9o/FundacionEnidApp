package com.fundacion.enid.ui.screens

import androidx.compose.runtime.Composable
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.NavController
import com.fundacion.enid.navigation.Screen

/**
 * Enrutador principal: decide qué pantalla mostrar según el tope de la pila.
 */
@Composable
fun AppRoot(
    nav: NavController,
    repository: DataRepository,
    onSessionChanged: () -> Unit
) {
    when (val pantalla = nav.current) {
        is Screen.Welcome -> WelcomeScreen(
            onLogin = { nav.navegar(Screen.Login) },
            onRegister = { nav.navegar(Screen.Register) }
        )

        is Screen.Login -> AuthScreen(
            repository = repository,
            onLoginSuccess = {
                nav.reemplazarTodo(Screen.Home)
                onSessionChanged()
            },
            onRegistered = {
                nav.reemplazarTodo(Screen.Home)
                onSessionChanged()
            },
            onBack = { nav.retroceder() }
        )

        is Screen.Register -> AuthScreen(
            repository = repository,
            modoRegistro = true,
            onLoginSuccess = {
                nav.reemplazarTodo(Screen.Home)
                onSessionChanged()
            },
            onRegistered = {
                nav.reemplazarTodo(Screen.Home)
                onSessionChanged()
            },
            onBack = { nav.retroceder() }
        )

        is Screen.Home -> HomeScreen(
            repository = repository,
            onNavigate = { nav.navegar(it) },
            onTabNavigate = { nav.reemplazarTodo(it) }
        )

        is Screen.Catalog -> CatalogScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onOpenProgram = { id -> nav.navegar(Screen.ProgramDetail(id)) },
            onTabNavigate = { nav.reemplazarTodo(it) }
        )

        is Screen.ProgramDetail -> ProgramDetailScreen(
            programId = pantalla.programId,
            repository = repository,
            onBack = { nav.retroceder() },
            onPreinscription = { nav.navegar(Screen.Preinscription) },
            onTabNavigate = { nav.reemplazarTodo(it) }
        )

        is Screen.Locations -> LocationsScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onTabNavigate = { nav.reemplazarTodo(it) }
        )

        is Screen.Faq -> FaqScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onTabNavigate = { nav.reemplazarTodo(it) }
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
            onBack = { nav.retroceder() },
            onTabNavigate = { nav.reemplazarTodo(it) }
        )

        is Screen.DailySchedule -> DailyScheduleScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onTabNavigate = { nav.reemplazarTodo(it) }
        )

        is Screen.Payments -> PaymentsScreen(
            repository = repository,
            onBack = { nav.retroceder() },
            onPse = { nav.navegar(Screen.Pse) },
            onTabNavigate = { nav.reemplazarTodo(it) }
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
            onTabNavigate = { nav.reemplazarTodo(it) }
        )
    }
}