package com.fundacion.enid.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.model.Sede
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigOutlinedButton
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidPhoto
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

@Composable
fun LocationsScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    AppScaffold(title = "Nuestras Sedes", onBack = onBack, activeTab = EnidTab.SOPORTE, onTabNavigate = onTabNavigate) {
        Text(
            text = "Encuentra tu campus más cercano",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
        VerticalSpacer(14)
        repository.sedes.forEach { sede ->
            SedeCard(sede)
            VerticalSpacer(16)
        }
    }
}

@Composable
private fun SedeCard(sede: Sede) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Column {
            // Zona de foto (guía SVG: bloque con "FOTO")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(EnidPhoto),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[ FOTO ]",
                    color = EnidWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = sede.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(6)
                Text(
                    text = sede.telefono,
                    fontSize = 12.sp,
                    color = EnidTextSec
                )
                Text(
                    text = sede.horario,
                    fontSize = 12.sp,
                    color = EnidTextSec
                )
                VerticalSpacer(4)
                Text(
                    text = sede.direccion,
                    fontSize = 12.sp,
                    color = EnidTextSec
                )
                VerticalSpacer(12)
                Row(modifier = Modifier.fillMaxWidth()) {
                    BigOutlinedButton(
                        text = "Llamar a Sede",
                        modifier = Modifier.weight(1f),
                        borderColor = EnidPurple,
                        contentColor = EnidPurple,
                        icon = EnidIcons.VLlamar,
                        onClick = {
                            val numero = sede.telefono.replace(Regex("[^+\\d]"), "")
                            try {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero")))
                            } catch (_: Exception) {
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BigButton(
                        text = "Cómo Llegar",
                        modifier = Modifier.weight(1f),
                        containerColor = EnidRed,
                        icon = EnidIcons.VComoLlegar,
                        onClick = {
                            val query = Uri.encode(sede.direccion)
                            try {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$query"))
                                )
                            } catch (_: Exception) {
                            }
                        }
                    )
                }
            }
        }
    }
}