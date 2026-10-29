package com.fundacion.enid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.NavController
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.screens.AppRoot
import com.fundacion.enid.ui.state.SesionUi
import com.fundacion.enid.ui.theme.FundacionEnidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FundacionEnidTheme {
                AppEntry()
            }
        }
    }
}

@Composable
fun AppEntry() {
    val context = LocalContext.current
    val repository = remember { DataRepository(context.applicationContext) }
    var loggedIn by remember { mutableStateOf(repository.estaConectado) }

    // La capa visual (pestañas, rótulos) lee el mismo valor que `loggedIn`.
    // Sin esto, `AppScaffold` no sabría si quien mira es invitado o estudiante.
    LaunchedEffect(loggedIn) {
        SesionUi.actualizar(loggedIn, repository.currentUser?.rol)
    }

    val nav = remember {
        NavController(if (repository.estaConectado) Screen.Home else Screen.Welcome)
    }

    AppRoot(
        nav = nav,
        repository = repository,
        onSessionChanged = { loggedIn = repository.estaConectado }
    )
}