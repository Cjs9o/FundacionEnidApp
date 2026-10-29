package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.model.Sede
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.RedesSociales
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidPurpleSoft
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

@Composable
fun LocationsScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    AppScaffold(
        title = "Nuestra Sede",
        onBack = onBack,
        activeTab = EnidTab.SOPORTE,
        onTabNavigate = onTabNavigate,
    ) {
        Text(
            text = "Visítanos en la Sede Chía, en el Parque Ospina.",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
        VerticalSpacer(14)
        InstitucionalBlock()
        VerticalSpacer(20)
        repository.sedes.forEach { sede ->
            SedeCard(sede)
            VerticalSpacer(16)
        }
        RedesSociales()
        VerticalSpacer(20)
    }
}

@Composable
private fun InstitucionalBlock() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SOBRE LA FUNDACIÓN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EnidTextSec
            )
            VerticalSpacer(10)
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard("25+", "años de experiencia", Modifier.weight(1f))
                Spacer(modifier = Modifier.width(10.dp))
                StatCard("+22", "programas técnicos", Modifier.weight(1f))
            }
            VerticalSpacer(10)
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard("+9000", "graduados", Modifier.weight(1f))
                Spacer(modifier = Modifier.width(10.dp))
                StatCard("1", "sede: Chía, Cundinamarca", Modifier.weight(1f))
            }
            VerticalSpacer(16)
            Text(
                text = "Misión (2030)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            VerticalSpacer(4)
            Text(
                text = "Ser en 2030 la institución educativa de referencia en Colombia, " +
                    "reconocida por transformar vidas a través de una formación incluyente y " +
                    "con equidad, que impulsa la empleabilidad, el emprendimiento y el " +
                    "desarrollo integral de sus estudiantes, familias y comunidades.",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
            VerticalSpacer(14)
            Text(
                text = "Visión (2030)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            VerticalSpacer(4)
            Text(
                text = "Para 2030, ser un referente de la transformación educativa en " +
                    "Colombia, formando generaciones preparadas académica, espiritual y " +
                    "humanamente para emprender, acceder a mejores oportunidades laborales " +
                    "y mejorar la calidad de vida de sus familias y comunidades.",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
        }
    }
}

@Composable
private fun StatCard(valor: String, texto: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidRed
            )
            Text(
                text = texto,
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun SedeCard(sede: Sede) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.dp, EnidBorderSoft)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = sede.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            VerticalSpacer(6)
            Text(
                text = sede.direccion,
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
            Text(
                text = sede.horario,
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )
            VerticalSpacer(12)
            Text(
                text = "TELÉFONOS DE ATENCIÓN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EnidTextSec
            )
            VerticalSpacer(6)
            // Cada número abre el marcador con ese número, no con una cadena
            // que junte los dos: antes el modelo guardaba un solo teléfono y
            // "3166301033 / 3132048325" se habría marcado como un número único.
            sede.telefonos.forEach { numero ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { EnlacesExternos.abrirLlamar(context, numero) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EnidIconBox(
                        icon = EnidIcons.VLlamar,
                        boxSize = 36.dp,
                        iconSize = 19.dp,
                        iconTint = EnidPurple,
                        background = EnidPurpleSoft
                    )
                    Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                        Text(
                            text = numero,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EnidInk
                        )
                        Text(
                            text = "Llamar a la sede",
                            style = MaterialTheme.typography.bodySmall,
                            color = EnidTextSec
                        )
                    }
                }
            }
            VerticalSpacer(10)
            BigButton(
                text = "Cómo Llegar",
                modifier = Modifier.fillMaxWidth(),
                containerColor = EnidRed,
                icon = EnidIcons.VComoLlegar,
                onClick = { EnlacesExternos.abrirMapa(context, sede.direccion) }
            )
        }
    }
}