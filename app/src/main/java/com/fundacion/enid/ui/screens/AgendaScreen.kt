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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.R
import com.fundacion.enid.data.AgendaClases
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.model.ClassSlot
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.EnidTabHeader
import com.fundacion.enid.ui.components.TabPageIntro
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBlueMid
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidRedSoft
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Módulo **Agenda** (pestaña del Figma).
 *
 * La franja de días es real: muestra los próximos cinco días y, al elegir uno,
 * la agenda de esa fecha sale del horario de la Fundación. No hay eventos
 * inventados. El acceso al horario detallado y al plan de estudios se conserva
 * al final, porque esa vista ya no tiene pestaña propia.
 */
@Composable
fun AgendaScreen(
    repository: DataRepository,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val locale = Locale.forLanguageTag("es-CO")
    val hoy = remember { LocalDate.now() }
    val dias = remember(hoy) { (0L until 5L).map { hoy.plusDays(it) } }
    var seleccionado by remember { mutableIntStateOf(0) }

    val diaElegido = dias[seleccionado]
    val clasesDelDia = AgendaClases.clasesDe(repository, diaElegido)
    // La tarjeta no repite la primera clase del día elegido: muestra la próxima
    // clase real y descarta las que ya terminaron. A las 3 p. m. no puede decir
    // "próxima" de una clase de las 7 a. m.
    val proxima = remember(repository, hoy) { AgendaClases.proxima(repository, hoy) }
    val destacada = proxima?.slot

    AppScaffold(
        title = "Agenda",
        header = { EnidTabHeader() },
        activeTab = EnidTab.AGENDA,
        onTabNavigate = onTabNavigate
    ) {
        TabPageIntro(
            eyebrow = hoy.format(DateTimeFormatter.ofPattern("MMMM yyyy", locale)),
            title = "Tu agenda",
            subtitle = "Tus clases y el plan de estudios, día por día."
        )
        VerticalSpacer(20)

        // ── Franja de días ──────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            dias.forEachIndexed { indice, fecha ->
                val activo = indice == seleccionado
                val forma = RoundedCornerShape(16.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(66.dp)
                        .clip(forma)
                        .background(if (activo) EnidRed else EnidWhite)
                        .border(1.dp, if (activo) EnidRed else EnidBorderSoft, forma)
                        .clickable(onClickLabel = "Ver ${fechaLarga(fecha, locale)}") {
                            seleccionado = indice
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = inicialDia(fecha.dayOfWeek),
                        color = if (activo) EnidWhite.copy(alpha = 0.85f) else EnidTextSec,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    VerticalSpacer(3)
                    Text(
                        text = fecha.dayOfMonth.toString(),
                        color = if (activo) EnidWhite else EnidInk,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        VerticalSpacer(22)

        // ── Tarjeta destacada ───────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(1.dp, EnidBorderSoft),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.img_71),
                contentDescription = "Clase presencial en la Fundación ENID",
                modifier = Modifier.fillMaxWidth().height(136.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (destacada != null) "PRÓXIMA CLASE" else "SIN PRÓXIMAS CLASES",
                    color = EnidRed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                VerticalSpacer(6)
                Text(
                    text = destacada?.asignatura ?: "No hay clases en los próximos días",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidDarkBlue
                )
                VerticalSpacer(7)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        EnidIcons.VReloj,
                        contentDescription = null,
                        tint = EnidTextSec,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (proxima != null) {
                            "  ${AgendaClases.fechaCorta(proxima.fecha)} · ${proxima.slot.hora} · ${proxima.slot.aula}"
                        } else {
                            "  Revisa el plan de estudios o habla con un asesor."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec
                    )
                }
                if (destacada != null) {
                    VerticalSpacer(2)
                    Text(
                        text = "Docente: ${destacada.docente}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec
                    )
                }
                VerticalSpacer(14)
                BigButton(
                    text = "VER HORARIO Y PLAN DE ESTUDIOS",
                    containerColor = EnidRed,
                    onClick = { onTabNavigate?.invoke(Screen.DailySchedule) }
                )
            }
        }

        VerticalSpacer(24)

        // ── Clases del día elegido ──────────────────────────────────────
        Text(
            text = "Clases del día",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = EnidDarkBlue
        )
        VerticalSpacer(4)
        Text(
            text = fechaLarga(diaElegido, locale) + if (seleccionado == 0) " · Hoy" else "",
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec
        )
        VerticalSpacer(8)

        if (clasesDelDia.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EnidWhite),
                border = BorderStroke(1.dp, EnidBorderSoft)
            ) {
                Text(
                    text = "Este día no tiene clases en el horario. Puedes revisar otro día de la franja.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            clasesDelDia.forEachIndexed { indice, clase ->
                FilaClase(clase) { onTabNavigate?.invoke(Screen.DailySchedule) }
                if (indice != clasesDelDia.lastIndex) {
                    HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
                }
            }
        }

        VerticalSpacer(18)
        Text(
            text = "El horario y el plan de estudios completos están en Mi Horario.",
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec
        )
        VerticalSpacer(8)
    }
}

@Composable
private fun FilaClase(clase: ClassSlot, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Abrir horario de ${clase.asignatura}", onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(58.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(EnidRedSoft)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = clase.hora.substringBefore("-").trim(),
                color = EnidRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                text = clase.asignatura,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = EnidInk,
                lineHeight = 21.sp
            )
            VerticalSpacer(2)
            Text(
                text = "${clase.aula} · ${clase.docente}",
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )
        }
        Icon(
            EnidIcons.VFlechaAdelante,
            contentDescription = null,
            tint = EnidBlueMid,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun inicialDia(dia: DayOfWeek): String = when (dia) {
    DayOfWeek.MONDAY -> "L"
    DayOfWeek.TUESDAY -> "M"
    DayOfWeek.WEDNESDAY -> "M"
    DayOfWeek.THURSDAY -> "J"
    DayOfWeek.FRIDAY -> "V"
    DayOfWeek.SATURDAY -> "S"
    DayOfWeek.SUNDAY -> "D"
}

private fun fechaLarga(fecha: LocalDate, locale: Locale): String =
    fecha.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", locale))
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
