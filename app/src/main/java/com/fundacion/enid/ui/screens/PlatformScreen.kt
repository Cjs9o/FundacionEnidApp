package com.fundacion.enid.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidChipDoneText
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

@Composable
fun PlatformScreen(
    onBack: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val context = LocalContext.current
    AppScaffold(title = "Plataforma Educativa", onBack = onBack, activeTab = EnidTab.SOPORTE, onTabNavigate = onTabNavigate) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(1.5.dp, EnidBorderSoft)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Ícono
                EnidIconBox(
                    icon = EnidIcons.VCandado,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    boxSize = 56.dp,
                    iconSize = 28.dp
                )
                VerticalSpacer(12)
                Text(
                    text = "Ingreso Seguro",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                VerticalSpacer(8)
                Text(
                    text = "Accede a tus lecciones, videos grabados y cuestionarios " +
                        "virtuales de forma sencilla. Esta plataforma abrirá una " +
                        "ventana nueva y segura.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                VerticalSpacer(16)
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = EnidIcons.VOkCirculo,
                        contentDescription = null,
                        tint = EnidGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Plataforma Activa",
                        color = EnidChipDoneText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }

        VerticalSpacer(24)
        BigButton(
            text = "Abrir Plataforma Externa",
            containerColor = EnidDarkBlue,
            icon = EnidIcons.VExterno,
            onClick = { abrirPlataforma(context) }
        )
        VerticalSpacer(20)
        Text(
            text = "Si presentas dificultades de acceso, contáctanos por el canal de " +
                "soporte de la Fundación.",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
    }
}

private fun abrirPlataforma(context: Context) = EnlacesExternos.abrirPlataforma(context)