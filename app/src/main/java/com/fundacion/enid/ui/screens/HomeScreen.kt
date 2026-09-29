package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.R
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidBlueSoft
import com.fundacion.enid.ui.theme.EnidChipDoneText
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGold
import com.fundacion.enid.ui.theme.EnidGoldSoft
import com.fundacion.enid.ui.theme.EnidGreenSoft
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidPhoto
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidPurpleSoft
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidRedSoft
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

@Composable
fun HomeScreen(
    repository: DataRepository,
    onNavigate: (Screen) -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val usuario = repository.currentUser
    val conectado = usuario != null
    val nombre = usuario?.nombre ?: "Invitado"
    val context = LocalContext.current

    AppScaffold(
        title = "",
        brandLine = null,
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate,
        mostrarAsesor = true,
        header = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EnidWhite, RoundedCornerShape(12.dp))
                    .border(1.5.dp, EnidBorderSoft, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bienvenidos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EnidInk
                        )
                        VerticalSpacer(2)
                        Text(
                            text = "Hola, $nombre",
                            style = MaterialTheme.typography.bodyLarge,
                            color = EnidTextSec
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.logo_enid),
                        contentDescription = "Logo Fundación ENID",
                        modifier = Modifier.size(width = 156.dp, height = 46.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
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
                    text = "No capacitamos personas, transformamos vidas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidWhite
                )
                VerticalSpacer(2)
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

        if (conectado) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MenuCard(EnidIcons.VCalendario, "Mis Clases", EnidDarkBlue, EnidBlueSoft, { onNavigate(Screen.DailySchedule) }, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                MenuCard(EnidIcons.VPagos, "Pagos", EnidChipDoneText, EnidGreenSoft, { onNavigate(Screen.Payments) }, Modifier.weight(1f))
            }
            VerticalSpacer(12)
            Row(modifier = Modifier.fillMaxWidth()) {
                MenuCard(EnidIcons.VExterno, "Plataforma Educativa", EnidGold, EnidGoldSoft, { onNavigate(Screen.Platform) }, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                MenuCard(EnidIcons.VPerfil, "Mi Cuenta", EnidPurple, EnidPurpleSoft, { onNavigate(Screen.Profile) }, Modifier.weight(1f))
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth()) {
                MenuCard(EnidIcons.VOferta, "Oferta Académica", EnidRed, EnidRedSoft, { onNavigate(Screen.Catalog) }, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                MenuCard(EnidIcons.VSedes, "Información Institucional", EnidDarkBlue, EnidBlueSoft, { onNavigate(Screen.Locations) }, Modifier.weight(1f))
            }
            VerticalSpacer(12)
            Row(modifier = Modifier.fillMaxWidth()) {
                MenuCard(EnidIcons.VPreinscripcion, "Preinscripción", EnidChipDoneText, EnidGreenSoft, { onNavigate(Screen.Preinscription(null)) }, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                MenuCard(EnidIcons.VWhatsApp, "Contáctanos", EnidGold, EnidGoldSoft, { onNavigate(Screen.Faq) }, Modifier.weight(1f))
            }
        }

        VerticalSpacer(28)
        SectionTitle("Más servicios")
        VerticalSpacer(8)
        SecondaryLink(EnidIcons.VSedes, "Sedes y Contacto") { onNavigate(Screen.Locations) }
        SecondaryLink(EnidIcons.VAyuda, "Preguntas Frecuentes") { onNavigate(Screen.Faq) }
        SecondaryLink(EnidIcons.VExterno, "Plataforma Educativa") { onNavigate(Screen.Platform) }
        if (conectado) {
            SecondaryLink(EnidIcons.VOferta, "Oferta Académica") { onNavigate(Screen.Catalog) }
            SecondaryLink(EnidIcons.VPreinscripcion, "Preinscripción") { onNavigate(Screen.Preinscription(null)) }
            SecondaryLink(EnidIcons.VCalculadora, "Calculadora de Beneficios") { onNavigate(Screen.Benefits) }
            SecondaryLink(EnidIcons.VPerfil, "Mi Perfil") { onNavigate(Screen.Profile) }
        }
        VerticalSpacer(24)

        // Promociones y testimonios también para el estudiante: puede verLAS y,
        // si le interesa algo nuevo, preinscribirse a otro programa.
        PromocionesTestimoniosSection(
            promociones = repository.promociones,
            testimonios = repository.testimonios,
            onHablarAsesor = { EnlacesExternos.abrirAsesor(context) },
            onDejarDatos = { onNavigate(Screen.Preinscription(null)) }
        )
        VerticalSpacer(24)
    }
}

@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    iconBg: Color,
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
            EnidIconBox(
                icon = icon,
                boxSize = 44.dp,
                iconSize = 23.dp,
                iconTint = iconColor,
                background = iconBg
            )
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
        EnidIconBox(
            icon = icon,
            boxSize = 42.dp,
            iconSize = 22.dp,
            iconTint = EnidTextSec
        )
        Spacer(modifier = Modifier.width(12.dp))
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
            modifier = Modifier.size(18.dp)
        )
    }
}