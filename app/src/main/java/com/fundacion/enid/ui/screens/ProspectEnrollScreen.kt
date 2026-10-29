package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.model.RutaStep
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.SmallButton
import com.fundacion.enid.ui.components.TabPageIntro
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTeal
import com.fundacion.enid.ui.shared.EnidTealDark
import com.fundacion.enid.ui.shared.EnidTealSoft
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

/**
 * Pestaña **Inscríbete** del invitado.
 *
 * Antes los diez pasos, los atajos y las promociones compartían pantalla con la
 * información institucional. Aquí queda solo el proceso de inscripción: qué
 * pasos se resuelven en la app, cuáles con el asesor y dónde dejar los datos.
 */
@Composable
fun ProspectEnrollScreen(
    repository: DataRepository,
    onNavigate: (Screen) -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val ruta = DataRepository.rutaProspecto
    val context = LocalContext.current

    AppScaffold(
        title = "Inscríbete",
        titleEnd = "Invitado",
        brandLine = null,
        mostrarAsesor = true,
        activeTab = EnidTab.INSCRIBETE,
        onTabNavigate = onTabNavigate,
        content = {
            TabPageIntro(
                eyebrow = "Tu proceso",
                title = "Inscríbete en ENID",
                subtitle = "Diez pasos claros. Puedes empezar sin crear cuenta y un asesor te acompaña."
            )
            VerticalSpacer(16)

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
            SectionTitle(
                "Tu camino para estudiar en ENID",
                "Cada paso es tuyo. Los que se resuelven con el asesor están marcados."
            )
            VerticalSpacer(10)

            ruta.forEach { paso ->
                RutaStepCard(
                    paso = paso,
                    onClick = { paso.pantalla?.let { onNavigate(destinoDe(it)) } }
                )
            }

            VerticalSpacer(8)
            BigButton(
                text = "Dejar mi información",
                modifier = Modifier.fillMaxWidth(),
                containerColor = EnidRed,
                icon = EnidIcons.VPreinscripcion,
                onClick = { onNavigate(Screen.Preinscription(null)) }
            )

            PromocionesTestimoniosSection(
                promociones = repository.promociones,
                testimonios = repository.testimonios,
                onHablarAsesor = { EnlacesExternos.abrirAsesor(context) },
                onDejarDatos = { onNavigate(Screen.Preinscription(null)) }
            )

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

/** Un paso del camino. Los pasos de sede/asesor no navegan: se explican y se ofrecen al asesor. */
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
            containerColor = if (navegable) EnidWhite else EnidTealSoft
        ),
        border = BorderStroke(1.5.dp, if (navegable) EnidBorderSoft else EnidTeal.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número del paso: círculo con la cifra centrada.
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (navegable) EnidRed else EnidTeal),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${paso.numero}",
                    color = EnidWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
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
                            color = EnidTealDark,
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

/**
 * Traduce el nombre de pantalla guardado en [RutaStep.pantalla] a la ruta de
 * navegación. Solo los pasos 1 a 4 de [DataRepository.rutaProspecto] tienen
 * pantalla; el resto se hace en sede o con el asesor.
 */
private fun destinoDe(nombre: String): Screen = when (nombre) {
    "Locations" -> Screen.Locations
    "Catalog" -> Screen.Catalog
    "ProgramFinder" -> Screen.ProgramFinder
    "Preinscription" -> Screen.Preinscription(null)
    else -> Screen.ProspectEnroll
}
