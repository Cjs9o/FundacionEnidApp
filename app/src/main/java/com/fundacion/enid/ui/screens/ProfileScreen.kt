package com.fundacion.enid.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.InfoCard
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidRed

@Composable
fun ProfileScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val usuario = repository.currentUser

    AppScaffold(
        title = "Mi Perfil",
        onBack = onBack,
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate
    ) {
        Text(
            text = "Tu información de usuario",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        VerticalSpacer(10)

        InfoCard {
            SectionTitle("Nombre")
            VerticalSpacer(4)
            Text(
                text = usuario?.nombre ?: "Sin nombre",
                style = MaterialTheme.typography.bodyLarge
            )
            VerticalSpacer(12)
            SectionTitle("Correo electrónico")
            VerticalSpacer(4)
            Text(
                text = usuario?.correo ?: "Sin correo",
                style = MaterialTheme.typography.bodyLarge
            )
            VerticalSpacer(12)
            SectionTitle("Rol")
            VerticalSpacer(4)
            Text(
                text = usuario?.rol?.etiqueta ?: "Sin rol",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        VerticalSpacer(20)
        BigButton(
            text = "Cerrar sesión",
            containerColor = EnidRed,
            onClick = onLogout
        )
    }
}