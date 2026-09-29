package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.model.RutaStep
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.SmallButton
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidGreenSoft
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidRedSoft
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

/**
 * Requerimientos 02 y 03: Home del invitado.
 *
 * Este es el "camino B" (quiero estudiar en ENID). El "camino A" (soy
 * estudiante) sigue en [HomeScreen] y solo se muestra cuando hay sesión
 * iniciada. Aquí el invitado ve, en orden, los 10 pasos que lo separan de
 * matricularse, de modo que la app funciona también como herramienta de venta:
 * puede recorrer el proceso completo sin crear cuenta.
 */
@Composable
fun ProspectHomeScreen(
    repository: DataRepository,
    onNavigate: (Screen) -> Unit,
    onIrAInicio: () -> Unit
) {
    val ruta = repository.rutaProspecto

    AppScaffold(
        title = "Quiero estudiar en ENID",
        titleEnd = "Invitado",
        brandLine = null,
        mostrarAsesor = true,
        content = {
            // Bienvenida
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EnidDarkBlue)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "No capacitamos personas, transformamos vidas.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EnidWhite
                    )
                    VerticalSpacer(6)
                    Text(
                        text = "Técnicos y complementarios conTitulación SENA. " +
                            "Te mostramos el camino completo para que decidas con claridad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EnidWhite.copy(alpha = 0.85f)
                    )
                    VerticalSpacer(14)
                    BigButton(
                        text = "¿Qué puedo estudiar?",
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = EnidRed,
                        onClick = { onNavigate(Screen.ProgramFinder) }
                    )
                }
            }

            VerticalSpacer(22)

            // ── Paso a paso ────────────────────────────────────────────────
            SectionTitle("Tu camino para estudiar en ENID", "Cada paso es tuyo. Puedes empezar sin crear cuenta.")

            ruta.forEach { paso ->
                RutaStepCard(paso = paso, onClick = { paso.pantalla?.let { onNavigate(destinoDe(paso.pantalla)) } })
            }

            VerticalSpacer(10)

            // Atajos a los pasos que sí tienen pantalla
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
                    text = "Dejar mis datos",
                    modifier = Modifier.weight(1f),
                    icon = EnidIcons.VPreinscripcion,
                    onClick = { onNavigate(Screen.Preinscription(null)) }
                )
            }

            VerticalSpacer(20)

            // ── Si ya eres estudiante ─────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onIrAInicio() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EnidWhite),
                border = BorderStroke(1.5.dp, EnidBorderSoft)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EnidIconBox(
                        icon = EnidIcons.VPerfil,
                        boxSize = 38.dp,
                        iconSize = 21.dp,
                        iconTint = EnidRed,
                        background = EnidRedSoft
                    )
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(
                            text = "¿Ya eres estudiante ENID?",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = EnidInk
                        )
                        Text(
                            text = "Inicia sesión para ver tu horario, pagos y soporte.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EnidTextSec
                        )
                    }
                    Text(
                        text = "Entrar",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = EnidRed
                    )
                }
            }

            VerticalSpacer(18)

            // ── Enlaces institucionales ────────────────────────────────────
            SectionTitle("Conoce ENID", "Sedes, preguntas frecuentes y contacto.")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallButton(
                    text = "Sedes",
                    modifier = Modifier.weight(1f),
                    icon = EnidIcons.VSedes,
                    onClick = { onNavigate(Screen.Locations) }
                )
                SmallButton(
                    text = "Preguntas",
                    modifier = Modifier.weight(1f),
                    icon = EnidIcons.VAyuda,
                    onClick = { onNavigate(Screen.Faq) }
                )
            }

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

/** Un paso del camino. Los pasos de sede/asesor no navegan: se explican y se offeren al asesor. */
@Composable
private fun RutaStepCard(paso: RutaStep, onClick: () -> Unit) {
    val navegable = paso.pantalla != null
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .then(if (navegable) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (navegable) EnidWhite else EnidGreenSoft
        ),
        border = BorderStroke(1.5.dp, if (navegable) EnidBorderSoft else EnidGreen.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número del paso
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.size(30.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (navegable) EnidRed else EnidGreen
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${paso.numero}",
                            color = EnidWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = paso.titulo,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = EnidInk,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (!navegable) {
                        Text(
                            text = "  con asesor",
                            style = MaterialTheme.typography.bodySmall,
                            color = EnidGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = paso.detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidTextSec
                )
            }

            if (navegable) {
                Text(
                    text = "›",
                    color = EnidRed,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/** Traduce el nombre de pantalla guardado en [RutaStep.pantalla] a la ruta de navegación. */
private fun destinoDe(nombre: String): Screen = when (nombre) {
    "Locations" -> Screen.Locations
    "Catalog" -> Screen.Catalog
    "ProgramFinder" -> Screen.ProgramFinder
    "Preinscription" -> Screen.Preinscription(null)
    "Payments" -> Screen.Payments
    "Platform" -> Screen.Platform
    else -> Screen.Home
}
