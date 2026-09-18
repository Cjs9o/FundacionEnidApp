package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

private enum class DetalleTab(val label: String) {
    PLAN("Plan de Estudios"), REQUISITOS("Requisitos")
}

@Composable
fun ProgramDetailScreen(
    programId: Int,
    repository: DataRepository,
    onBack: () -> Unit,
    onPreinscription: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val programa = repository.programas.firstOrNull { it.id == programId }
    var tab by remember { mutableStateOf(DetalleTab.PLAN) }

    AppScaffold(title = "Detalle del Curso", onBack = onBack, activeTab = EnidTab.INICIO, onTabNavigate = onTabNavigate) {
        if (programa == null) {
            Text(text = "Programa no encontrado.", style = MaterialTheme.typography.bodyLarge)
            return@AppScaffold
        }

        Text(
            text = programa.nombre,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = EnidInk
        )
        VerticalSpacer(6)
        Text(
            text = "Duración: ${programa.duracion}    Sede: ${programa.sedes.joinToString(", ")}",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
        VerticalSpacer(16)

        // Tabs (guía SVG)
        Row(modifier = Modifier.fillMaxWidth()) {
            DetalleTab.entries.forEachIndexed { i, t ->
                val selected = tab == t
                Card(
                    modifier = Modifier.weight(1f).padding(start = if (i == 0) 0.dp else 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) EnidDarkBlue else EnidWhite,
                        contentColor = if (selected) EnidWhite else EnidTextSec
                    ),
                    border = BorderStroke(1.5.dp, if (selected) EnidDarkBlue else EnidBorderSoft)
                ) {
                    Text(
                        text = t.label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tab = t }
                            .padding(vertical = 10.dp)
                    )
                }
            }
        }
        VerticalSpacer(18)

        when (tab) {
            DetalleTab.PLAN -> {
                programa.pensum.forEachIndexed { index, materia ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "%02d".format(index + 1),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EnidRed,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = materia,
                            style = MaterialTheme.typography.bodyLarge,
                            color = EnidInk,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
                }
                VerticalSpacer(12)
                Text(
                    text = "Perfil Ocupacional",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(6)
                Text(
                    text = programa.perfilOcupacional,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec
                )
            }

            DetalleTab.REQUISITOS -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = EnidWhite),
                    border = BorderStroke(1.5.dp, EnidBorderSoft)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        listOf(
                            "Documento de identidad vigente (mayores de 15 años).",
                            "Último grado de estudios aprobado (certificado).",
                            "Diligenciar el formulario de preinscripción en esta app.",
                            "Presentar entrevista virtual o presencial según la sede.",
                            "Formalizar la matrícula antes de la fecha límite."
                        ).forEach { req ->
                            Text("•  $req", style = MaterialTheme.typography.bodyLarge, color = EnidInk)
                            VerticalSpacer(6)
                        }
                    }
                }
            }
        }

        VerticalSpacer(28)
        BigButton(text = "Preinscribirme Ahora", containerColor = EnidRed, onClick = onPreinscription)
    }
}