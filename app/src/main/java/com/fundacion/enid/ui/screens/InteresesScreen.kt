package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.model.Program
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidChipDoneBg
import com.fundacion.enid.ui.theme.EnidChipDoneText
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidGreenSoft
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidRedSoft
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

/**
 * Requerimiento 08: seguimiento de interesados.
 *
 * La app "le recuerda a la persona qué le interesa" y le da "un resumen con el
 * programa elegido, sus datos y los siguientes pasos". Esta pantalla es ese resumen:
 * qué programas marcó, en qué estado va su preinscripción y qué le toca hacer.
 */
@Composable
fun InteresesScreen(
    repository: DataRepository,
    onOpenProgram: (Int) -> Unit,
    onPreinscription: (Int?) -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val context = LocalContext.current
    val intereses = repository.consultarIntereses()
    val preinscripciones = repository.consultarPreinscripciones()

    AppScaffold(
        title = "Mis interesados",
        titleEnd = "Resumen",
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate,
        mostrarAsesor = true,
        content = {
            if (intereses.isEmpty()) {
                // Estado vacío: en vez de una pantalla en blanco, indica el camino.
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EnidWhite),
                    border = BorderStroke(1.5.dp, EnidBorderSoft)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Todavía no has guardado ningún programa",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = EnidInk
                        )
                        VerticalSpacer(6)
                        Text(
                            text = "Cuando veas un programa que te interese, tócalo en " +
                                "«Me interesa este programa» y lo guardamos aquí para " +
                                "recordarte el siguiente paso.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EnidTextSec
                        )
                    }
                }
                VerticalSpacer(14)
                BigButton(
                    text = "Explorar la oferta académica",
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = EnidRed,
                    onClick = { onPreinscription(null) }
                )
                return@AppScaffold
            }

            SectionTitle(
                "El programa que elegiste",
                "Esto es lo que tienes pendiente completar."
            )
            VerticalSpacer(10)

            intereses.forEach { programa ->
                InteresCard(
                    programa = programa,
                    paso = repository.siguientePaso(programa.nombre),
                    onOpen = { onOpenProgram(programa.id) }
                )
                VerticalSpacer(10)
            }

            // ── Tus datos y preinscripciones ──────────────────────────────
            VerticalSpacer(12)
            SectionTitle("Lo que ya dejaste", "Tus datos quedan guardados en este dispositivo.")
            VerticalSpacer(10)

            if (preinscripciones.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EnidGreenSoft),
                    border = BorderStroke(1.5.dp, EnidGreen.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Aún no has dejado tus datos. Con ellos te contactamos y " +
                            "empiezas el proceso de admisión.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec,
                        modifier = Modifier.padding(14.dp)
                    )
                }
                VerticalSpacer(10)
                BigButton(
                    text = "Dejar mis datos",
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = EnidGreen,
                    onClick = { onPreinscription(intereses.first().id) }
                )
            } else {
                preinscripciones.take(5).forEach { pre ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EnidWhite),
                        border = BorderStroke(1.5.dp, EnidBorderSoft)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                EnidIconBox(
                                    icon = EnidIcons.VOkCirculo,
                                    boxSize = 30.dp,
                                    iconSize = 17.dp,
                                    iconTint = EnidGreen,
                                    background = EnidGreenSoft
                                )
                                Text(
                                    text = pre.programa,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EnidInk,
                                    modifier = Modifier.weight(1f).padding(start = 10.dp)
                                )
                            }
                            Text(
                                text = "${pre.nombre} · ${pre.telefono} · ${pre.correo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = EnidTextSec,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            Text(
                                text = "Preinscripción registrada el ${pre.fecha}",
                                style = MaterialTheme.typography.bodySmall,
                                color = EnidChipDoneText,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            VerticalSpacer(8)
            Text(
                text = "Tus datos se guardan solo en este teléfono. Si necesitas que " +
                    "los consulte un asesor, escríbenos y los revisamos.",
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )
        }
    )
}

@Composable
private fun InteresCard(programa: Program, paso: String, onOpen: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onOpen() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidRed)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = programa.nombre,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = EnidInk
                    )
                    Text(
                        text = "${programa.tipo.etiqueta} · ${programa.duracion} · ${programa.modalidad}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec
                    )
                }
                Text(
                    text = "›",
                    color = EnidRed,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            VerticalSpacer(10)

            // Resumen del siguiente paso
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = EnidRedSoft,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SIGUIENTE PASO",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = EnidRed
                    )
                    Text(
                        text = paso,
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidInk
                    )
                }
            }

            VerticalSpacer(10)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ver programa",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = EnidDarkBlue,
                    modifier = Modifier
                        .background(
                            color = EnidChipDoneBg,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }
        }
    }
}
