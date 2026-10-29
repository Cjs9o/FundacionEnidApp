package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.RedesSociales
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidRedSoft
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

/**
 * Requerimientos 02 y 03: Inicio del invitado.
 *
 * Este es el "camino B" (quiero estudiar en ENID). El "camino A" (soy
 * estudiante) sigue en [HomeScreen] y solo se muestra cuando hay sesión
 * iniciada. El invitado ya no recibe todo apilado en una sola pantalla larga:
 * Inicio presenta los dos caminos y la barra inferior los separa en **Conoce
 * ENID** y **Inscríbete**.
 */
@Composable
fun ProspectHomeScreen(
    repository: DataRepository,
    onNavigate: (Screen) -> Unit,
    onIrAInicio: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    // Requerimiento 08: recordatorio del programa que le interesa.
    val primerInteres = repository.consultarIntereses().firstOrNull()

    AppScaffold(
        title = "Quiero estudiar en ENID",
        titleEnd = "Invitado",
        brandLine = null,
        mostrarAsesor = true,
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate,
        content = {
            // Bienvenida (tarjeta con ilustración del Figma)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EnidDarkBlue)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth(0.76f).padding(16.dp)) {
                        Text(
                            text = "No capacitamos personas, transformamos vidas.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EnidWhite
                        )
                        VerticalSpacer(6)
                        Text(
                            text = "Técnicos y complementarios con Titulación SENA. " +
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
                    Image(
                        painter = androidx.compose.ui.res.painterResource(com.fundacion.enid.R.drawable.img_74),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(112.dp)
                            .offset(x = 10.dp, y = 10.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }

            VerticalSpacer(22)

            // Recordatorio: solo si ya guardó algún programa.
            if (primerInteres != null) {
                RecordatorioInteres(
                    nombrePrograma = primerInteres.nombre,
                    paso = repository.siguientePaso(primerInteres.nombre),
                    onVerResumen = { onNavigate(Screen.Intereses) }
                )
            }

            // ── Los dos caminos del invitado ───────────────────────────────
            SectionTitle(
                "¿Qué quieres hacer?",
                "Puedes recorrer todo el proceso sin crear cuenta."
            )
            VerticalSpacer(12)

            CaminoCard(
                icon = EnidIcons.VOferta,
                title = "Conoce ENID",
                detalle = "La Fundación, sus programas, la sede de Chía y las respuestas a tus dudas.",
                iconTint = EnidDarkBlue,
                iconBg = EnidRedSoft,
                onClick = { onNavigate(Screen.ProspectKnow) }
            )
            VerticalSpacer(10)
            CaminoCard(
                icon = EnidIcons.VPreinscripcion,
                title = "Inscríbete",
                detalle = "Mira los pasos, explora programas y deja tus datos cuando estés listo.",
                iconTint = EnidRed,
                iconBg = EnidRedSoft,
                onClick = { onNavigate(Screen.ProspectEnroll) }
            )

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
                        boxSize = 40.dp,
                        iconSize = 24.dp,
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
                            text = "Inicia sesión para ver tu horario y soporte.",
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

            VerticalSpacer(20)

            // Redes oficiales también en el Inicio del invitado: es la primera
            // pantalla que ve quien todavía no tiene cuenta.
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

/** Tarjeta grande para elegir uno de los dos caminos del invitado. */
@Composable
private fun CaminoCard(
    icon: ImageVector,
    title: String,
    detalle: String,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.dp, EnidBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EnidIconBox(
                icon = icon,
                boxSize = 48.dp,
                iconSize = 26.dp,
                iconTint = iconTint,
                background = iconBg
            )
            Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(3)
                Text(
                    text = detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidTextSec
                )
            }
            Icon(
                imageVector = EnidIcons.VFlechaAdelante,
                contentDescription = null,
                tint = EnidRed,
                modifier = Modifier.padding(start = 8.dp).size(20.dp)
            )
        }
    }
}
