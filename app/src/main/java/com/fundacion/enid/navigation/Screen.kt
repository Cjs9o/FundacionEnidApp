package com.fundacion.enid.navigation

sealed class Screen {
    // Flujo pre-login
    data object Welcome : Screen()
    data object Login : Screen()

    // Menú principal e institucional
    data object Home : Screen()
    data object ProspectHome : Screen()
    data object Catalog : Screen()
    data class ProgramDetail(val programId: Int) : Screen()
    data object ProgramFinder : Screen()
    data object Intereses : Screen()
    data object Locations : Screen()
    data object Faq : Screen()
    data object Profile : Screen()

    // Módulos de estudiante
    data class Preinscription(val programaId: Int? = null) : Screen()
    data object DailySchedule : Screen()
    data object Payments : Screen()
    data object Benefits : Screen()
    data object Platform : Screen()
    data object Pse : Screen()

    /**
     * Módulo de captura en campo (`:campo`, offline-first).
     *
     * Es una hoja, no un destino más: dentro hay un `NavHost` propio con
     * resumen, listado, captura y detalle, y no comparte la pila de `Screen`.
     * Por eso no lleva parámetros y por eso el retroceso del sistema lo
     * resuelve el `NavHost` interno primero y esta pila después.
     */
    data object Campo : Screen()
}
