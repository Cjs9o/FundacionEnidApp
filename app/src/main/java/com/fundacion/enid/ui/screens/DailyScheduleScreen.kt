package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.fundacion.enid.model.ClassSlot
import com.fundacion.enid.model.SubjectPlan
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class HorarioTab(val label: String) {
    HOY("Hoy"), PLAN("Plan de Estudios")
}

@Composable
fun DailyScheduleScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    var tab by remember { mutableStateOf(HorarioTab.HOY) }
    val hoy = remember {
        SimpleDateFormat("EEEE", Locale("es", "CO")).format(Date())
            .replaceFirstChar { it.uppercase() }
    }

    AppScaffold(title = "Mi Horario", onBack = onBack, activeTab = EnidTab.AGENDA, onTabNavigate = onTabNavigate) {
        // Tabs
        Row(modifier = Modifier.fillMaxWidth()) {
            HorarioTab.entries.forEachIndexed { i, t ->
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
        VerticalSpacer(16)

        when (tab) {
            HorarioTab.HOY -> {
                Text(
                    text = "Clases para hoy ($hoy)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec
                )
                VerticalSpacer(12)
                val clasesHoy = repository.horarioClases.filter { it.dia.equals(hoy, ignoreCase = true) }
                if (clasesHoy.isEmpty()) {
                    Text(
                        text = "No hay clases programadas para hoy. Descansa o " +
                            "revisa el plan de estudios para las próximas materias.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EnidTextSec
                    )
                } else {
                    clasesHoy.forEach { slot ->
                        SlotCard(slot)
                        VerticalSpacer(12)
                    }
                }
            }

            HorarioTab.PLAN -> {
                Text(
                    text = "Plan de Estudios semestral — Soporte de Sistemas e Informática",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec
                )
                VerticalSpacer(12)
                repository.cronograma.forEach { materia ->
                    SubjectCard(materia)
                    VerticalSpacer(10)
                }
            }
        }
    }
}

@Composable
private fun SlotCard(slot: ClassSlot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 12.dp, bottom = 12.dp).height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(EnidDarkBlue, RoundedCornerShape(2.dp))
            )
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    text = "${slot.hora}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = EnidTextSec
                )
                VerticalSpacer(2)
                Text(
                    text = slot.asignatura,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(2)
                Text(
                    text = "Profe: ${slot.docente} · ${slot.aula}",
                    fontSize = 12.sp,
                    color = EnidTextSec
                )
                Text(
                    text = "Día: ${slot.dia}",
                    fontSize = 12.sp,
                    color = EnidTextSec
                )
            }
        }
    }
}

@Composable
private fun SubjectCard(materia: SubjectPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = materia.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            VerticalSpacer(2)
            Text(
                text = "${materia.inicio} → ${materia.fin} · Intensidad: ${materia.intensidad}",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
        }
    }
}