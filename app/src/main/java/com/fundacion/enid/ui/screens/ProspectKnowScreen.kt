package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.RedesSociales
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.SmallButton
import com.fundacion.enid.ui.components.TabPageIntro
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidTealSoft
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidRedSoft
import com.fundacion.enid.ui.shared.EnidTealDark
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite
import com.fundacion.enid.ui.theme.EnidGold
import com.fundacion.enid.ui.theme.EnidGoldSoft

/**
 * Pestaña **Conoce ENID** del invitado.
 *
 * Reúne la información institucional que antes estaba al final del Home:
 * trayectoria, sede, preguntas frecuentes y redes. No promete nada que la
 * Fundación no haya publicado; la oferta y los trámites siguen en sus propias
 * pantallas.
 */
@Composable
fun ProspectKnowScreen(
    repository: DataRepository,
    onNavigate: (Screen) -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val sede = repository.sedes.firstOrNull()

    AppScaffold(
        title = "Conoce ENID",
        titleEnd = "Invitado",
        brandLine = null,
        mostrarAsesor = true,
        activeTab = EnidTab.CONOCE,
        onTabNavigate = onTabNavigate,
        content = {
            TabPageIntro(
                eyebrow = "Fundación ENID",
                title = "Conoce ENID",
                subtitle = "Educación técnica y complementaria con Titulación SENA."
            )
            VerticalSpacer(16)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EnidDarkBlue)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "No capacitamos personas, transformamos vidas.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EnidWhite
                    )
                    VerticalSpacer(6)
                    Text(
                        text = "Somos la Fundación de Educación para el Desarrollo y la " +
                            "Dignidad Humana. Acompañamos a personas de todas las edades " +
                            "que quieren formarse para el trabajo y para la vida.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EnidWhite.copy(alpha = 0.86f)
                    )
                }
            }

            VerticalSpacer(22)
            SectionTitle("Por qué ENID", "Trayectoria, titulación y cercanía.")
            VerticalSpacer(12)

            DatoInstitucional(
                icon = EnidIcons.VLogro,
                iconTint = EnidRed,
                iconBg = EnidRedSoft,
                title = "Más de veinte años",
                detalle = "Formando para el trabajo y el liderazgo, con calidad certificada bajo NTC desde 2015."
            )
            VerticalSpacer(10)
            DatoInstitucional(
                icon = EnidIcons.VCertificado,
                iconTint = EnidTealDark,
                iconBg = EnidTealSoft,
                title = "Titulación SENA",
                detalle = "Programas técnicos laborales y complementarios con certificación."
            )
            VerticalSpacer(10)
            DatoInstitucional(
                icon = EnidIcons.VSedes,
                iconTint = EnidGold,
                iconBg = EnidGoldSoft,
                title = "Sede Chía",
                detalle = "Un solo lugar para estudiar, resolver dudas y entregar documentos."
            )

            VerticalSpacer(22)
            SectionTitle("Explora", "Programas, sede y respuestas.")
            VerticalSpacer(10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallButton(
                    text = "Ver programas",
                    modifier = Modifier.weight(1f),
                    icon = EnidIcons.VOferta,
                    onClick = { onNavigate(Screen.Catalog) }
                )
                SmallButton(
                    text = "¿Qué estudiar?",
                    modifier = Modifier.weight(1f),
                    icon = EnidIcons.VBuscar,
                    onClick = { onNavigate(Screen.ProgramFinder) }
                )
            }

            VerticalSpacer(14)
            if (sede != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EnidWhite),
                    border = BorderStroke(1.dp, EnidBorderSoft),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = sede.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EnidInk
                        )
                        VerticalSpacer(4)
                        Text(
                            text = sede.direccion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = EnidTextSec
                        )
                        VerticalSpacer(4)
                        Text(
                            text = sede.telefonos.joinToString(" · "),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = EnidDarkBlue
                        )
                        VerticalSpacer(10)
                        SmallButton(
                            text = "Ver sede y cómo llegar",
                            icon = EnidIcons.VSedes,
                            onClick = { onNavigate(Screen.Locations) }
                        )
                    }
                }
            }

            VerticalSpacer(12)
            SecondaryLink(EnidIcons.VSedes, "Sedes y contacto") { onNavigate(Screen.Locations) }
            SecondaryLink(EnidIcons.VAyuda, "Preguntas frecuentes") { onNavigate(Screen.Faq) }

            VerticalSpacer(20)
            SectionTitle("Equipo de campo", "Captura de preinscripciones sin señal.")
            SecondaryLink(EnidIcons.VPreinscripcion, "Abrir captura en campo") { onNavigate(Screen.Campo) }

            VerticalSpacer(20)
            RedesSociales()
            VerticalSpacer(20)
            Text(
                text = "ENID · Fundación de Educación para el Desarrollo y la Dignidad Humana",
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec,
                fontSize = 11.sp
            )
        }
    )
}

@Composable
private fun DatoInstitucional(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    iconBg: androidx.compose.ui.graphics.Color,
    title: String,
    detalle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.dp, EnidBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EnidIconBox(
                icon = icon,
                boxSize = 42.dp,
                iconSize = 24.dp,
                iconTint = iconTint,
                background = iconBg
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                Text(
                    text = detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidTextSec
                )
            }
        }
    }
}
