package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidPhoto
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

@Composable
fun HomeScreen(
    repository: DataRepository,
    onNavigate: (Screen) -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val usuario = repository.currentUser

    AppScaffold(
        title = "ENID",
        titleEnd = "Hola, ${usuario?.nombre ?: "invitado"}",
        titleEndColor = EnidInk,
        brandLine = null,
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate
    ) {
        // Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EnidPhoto, RoundedCornerShape(10.dp))
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "Bienvenidos A La Fundación ENID",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidWhite
                )
                VerticalSpacer(6)
                Text(
                    text = "Educación accesible y cercana para personas mayores en Colombia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidWhite
                )
            }
        }
        VerticalSpacer(24)

        Text(
            text = "¿Qué deseas hacer hoy?",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = EnidInk
        )
        VerticalSpacer(12)

        Row(modifier = Modifier.fillMaxWidth()) {
            MenuCard(EnidIcons.VCalendario, "Mis Clases", { onNavigate(Screen.DailySchedule) }, Modifier.weight(1f))
            SpacerW(12)
            MenuCard(EnidIcons.VPagos, "Pagos", { onNavigate(Screen.Payments) }, Modifier.weight(1f))
        }
        VerticalSpacer(12)
        Row(modifier = Modifier.fillMaxWidth()) {
            MenuCard(EnidIcons.VOferta, "Oferta Académica", { onNavigate(Screen.Catalog) }, Modifier.weight(1f))
            SpacerW(12)
            MenuCard(EnidIcons.VPreinscripcion, "Preinscripción", { onNavigate(Screen.Preinscription) }, Modifier.weight(1f))
        }

        VerticalSpacer(28)
        SectionTitle("Más servicios")
        VerticalSpacer(8)
        SecondaryLink(EnidIcons.VSedes, "Sedes y Contacto") { onNavigate(Screen.Locations) }
        SecondaryLink(EnidIcons.VAyuda, "Preguntas Frecuentes") { onNavigate(Screen.Faq) }
        SecondaryLink(EnidIcons.VExterno, "Plataforma Educativa") { onNavigate(Screen.Platform) }
        SecondaryLink(EnidIcons.VCalculadora, "Calculadora de Beneficios") { onNavigate(Screen.Benefits) }
        SecondaryLink(EnidIcons.VPerfil, "Mi Perfil") { onNavigate(Screen.Profile) }
        VerticalSpacer(24)
    }
}

@Composable
private fun SpacerW(width: Int = 12) {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(width.dp))
}

@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EnidIconBox(icon = icon, boxSize = 44.dp, iconSize = 23.dp)
            VerticalSpacer(8)
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = EnidInk,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun SecondaryLink(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnidIconBox(icon = icon, boxSize = 36.dp, iconSize = 18.dp)
        SpacerW(12)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = EnidInk,
            modifier = Modifier.weight(1f)
        )
        Icon(
            EnidIcons.VFlechaAdelante,
            contentDescription = null,
            tint = EnidRed,
            modifier = Modifier.width(18.dp)
        )
    }
}