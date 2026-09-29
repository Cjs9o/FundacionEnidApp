package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.model.Program
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigOutlinedButton
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.tabsSegunSesion
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidChipDoneBg
import com.fundacion.enid.ui.theme.EnidChipDoneText
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

private enum class DetalleTab(val label: String) {
    INFO("Información"), APRENDER("¿Qué aprenderás?"), REQUISITOS("Requisitos")
}

private val REQUISITOS = listOf(
    "Documento de identidad vigente (mayores de 15 años).",
    "Último grado de estudios aprobado (certificado).",
    "Diligenciar el formulario de preinscripción en esta app.",
    "Presentar entrevista virtual o presencial según la sede.",
    "Formalizar la matrícula antes de la fecha límite."
)

/**
 * Detalle de programa de la oferta académica. Muestra toda la información con la
 * que un prospecto decide (modalidad, duración, perfil, qué aprenderá, sedes) y cierra
 * con tres llamadas a la acción: estudiar, hablar con un asesor o escribir por WhatsApp.
 */
@Composable
fun ProgramDetailScreen(
    programId: Int,
    repository: DataRepository,
    onBack: () -> Unit,
    onPreinscription: (Int) -> Unit,
    onAdvisor: () -> Unit = {},
    onTabNavigate: ((com.fundacion.enid.navigation.Screen) -> Unit)? = null
) {
    val programa = repository.programas.firstOrNull { it.id == programId }
    val context = LocalContext.current
    var tab by remember { mutableStateOf(DetalleTab.INFO) }
    // El interés se relee en cada recomposición: el botón refleja el estado real.
    var interesado by remember { mutableStateOf(programId != 0 && repository.esInteres(programId)) }

    if (programa == null) {
        AppScaffold(title = "Detalle del Curso", onBack = onBack) {
            Text(
                text = "Programa no encontrado.",
                style = MaterialTheme.typography.bodyLarge,
                color = EnidTextSec
            )
        }
        return
    }

    AppScaffold(
        title = "Detalle del Programa",
        onBack = onBack,
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate,
        tabs = tabsSegunSesion(repository.estaConectado)
    ) {
        Text(
            text = programa.nombre,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = EnidInk
        )
        VerticalSpacer(8)
        Row(modifier = Modifier.fillMaxWidth()) {
            FichaChip(programa.tipo.etiqueta, EnidChipDoneBg, EnidChipDoneText)
            HorizontalSpacerChip()
            FichaChip(programa.area.etiqueta, EnidWhite, EnidDarkBlue)
        }
        VerticalSpacer(16)

        Row(modifier = Modifier.fillMaxWidth()) {
            DetalleTab.entries.forEachIndexed { i, t ->
                val selected = tab == t
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (i == 0) 0.dp else 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) EnidDarkBlue else EnidWhite,
                        contentColor = if (selected) EnidWhite else EnidTextSec
                    ),
                    border = BorderStroke(1.5.dp, if (selected) EnidDarkBlue else EnidBorderSoft)
                ) {
                    Text(
                        text = t.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tab = t }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                    )
                }
            }
        }
        VerticalSpacer(18)

        when (tab) {
            DetalleTab.INFO -> InfoTab(programa)
            DetalleTab.APRENDER -> AprenderTab(programa, onAdvisor = { abrirAsesor(context, programa) })
            DetalleTab.REQUISITOS -> RequisitosTab()
        }

        VerticalSpacer(26)

        // ── Llamadas a la acción ────────────────────────────────────────────
        // Requerimiento 08: guardar el interés es lo que permite después
        // recordarle a la persona qué eligió y cuál es el siguiente paso.
        BigOutlinedButton(
            text = if (interesado) "Guardado en mis interesados" else "Me interesa este programa",
            borderColor = if (interesado) EnidGreen else EnidRed,
            contentColor = if (interesado) EnidGreen else EnidRed,
            icon = if (interesado) EnidIcons.VOkCirculo else EnidIcons.VAgregar,
            onClick = {
                if (interesado) {
                    repository.quitarInteres(programa.id)
                } else {
                    repository.registrarInteres(programa.id)
                }
                interesado = repository.esInteres(programa.id)
            }
        )
        VerticalSpacer(10)
        BigButton(
            text = "Quiero estudiar este programa",
            containerColor = EnidRed,
            icon = EnidIcons.VPreinscripcion,
            onClick = { onPreinscription(programa.id) }
        )
        VerticalSpacer(10)
        BigButton(
            text = "Hablar con un asesor",
            containerColor = EnidDarkBlue,
            icon = EnidIcons.VAyuda,
            onClick = { abrirAsesor(context, programa) }
        )
        VerticalSpacer(10)
        BigOutlinedButton(
            text = "Escribir por WhatsApp",
            borderColor = EnidGreen,
            contentColor = EnidGreen,
            icon = EnidIcons.VWhatsApp,
            onClick = {
                EnlacesExternos.abrirWhatsApp(
                    context,
                    mensaje = "Hola, quiero información sobre el programa ${programa.nombre}."
                )
            }
        )
        VerticalSpacer(8)
        Text(
            text = "Un asesor de la Fundación ENID te orienta sin compromiso.",
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun abrirAsesor(context: android.content.Context, programa: Program) {
    EnlacesExternos.abrirAsesor(
        context,
        "Hola, soy interesado en el programa ${programa.nombre}. ¿Me pueden orientar?"
    )
}

@Composable
private fun InfoTab(programa: Program) {
    SectionTitle("Modalidad y duración")
    VerticalSpacer(6)
    DatoLinea("Modalidad", programa.modalidad)
    DatoLinea("Duración", programa.duracion)
    DatoLinea("Sedes", programa.sedes.joinToString(" · "))
    VerticalSpacer(18)

    SectionTitle("Descripción")
    VerticalSpacer(6)
    Text(
        text = programa.descripcion,
        style = MaterialTheme.typography.bodyLarge,
        color = EnidInk
    )
    VerticalSpacer(18)

    SectionTitle("Perfil ocupacional")
    VerticalSpacer(6)
    Text(
        text = programa.perfilOcupacional,
        style = MaterialTheme.typography.bodyLarge,
        color = EnidInk
    )

    if (programa.campoLaboral.isNotEmpty()) {
        VerticalSpacer(18)
        SectionTitle("Campo laboral")
        VerticalSpacer(6)
        programa.campoLaboral.forEach { campo ->
            Text("•  $campo", style = MaterialTheme.typography.bodyMedium, color = EnidInk)
            VerticalSpacer(5)
        }
    }
}

@Composable
private fun AprenderTab(programa: Program, onAdvisor: () -> Unit) {
    if (programa.competencias.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(1.5.dp, EnidBorderSoft)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Contenido del programa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(6)
                Text(
                    text = "Este programa es de formación complementaria. El contenido y el " +
                        "plan de estudios se confirman según la cohorte. Un asesor te puede " +
                        "explicar el programa completo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec
                )
                VerticalSpacer(10)
                Text(
                    text = "Consultar con un asesor",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidRed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAdvisor() }
                        .padding(vertical = 8.dp)
                )
            }
        }
        return
    }

    SectionTitle("Plan de estudios · qué aprenderás")
    VerticalSpacer(6)
    Text(
        text = "Contenidos del programa según el Portafolio ENID.",
        style = MaterialTheme.typography.bodySmall,
        color = EnidTextSec
    )
    VerticalSpacer(10)

    programa.competencias.forEachIndexed { index, competencia ->
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
                text = competencia,
                style = MaterialTheme.typography.bodyLarge,
                color = EnidInk,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
    }
}

@Composable
private fun RequisitosTab() {
    SectionTitle("Requisitos para inscribirme")
    VerticalSpacer(8)
    REQUISITOS.forEach { req ->
        Text(
            text = "•  $req",
            style = MaterialTheme.typography.bodyLarge,
            color = EnidInk
        )
        VerticalSpacer(8)
    }
}

@Composable
private fun FichaChip(texto: String, fondo: androidx.compose.ui.graphics.Color, tinta: androidx.compose.ui.graphics.Color) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = tinta,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun HorizontalSpacerChip() {
    Column(modifier = Modifier.size(width = 8.dp, height = 1.dp)) {}
}

@Composable
private fun DatoLinea(etiqueta: String, valor: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = etiqueta.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = EnidTextSec
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            color = EnidInk
        )
    }
}
