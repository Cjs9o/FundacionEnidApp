package com.fundacion.enid.navigation

sealed class Screen {
    // Flujo pre-login
    data object Welcome : Screen()
    data object Login : Screen()

    // Menú principal e institucional
    data object Home : Screen()
    data object Catalog : Screen()
    data class ProgramDetail(val programId: Int) : Screen()
    data object Locations : Screen()
    data object Faq : Screen()
    data object Profile : Screen()

    // Módulos de estudiante
    data object Preinscription : Screen()
    data object DailySchedule : Screen()
    data object Payments : Screen()
    data object Benefits : Screen()
    data object Platform : Screen()
    data object Pse : Screen()
}
