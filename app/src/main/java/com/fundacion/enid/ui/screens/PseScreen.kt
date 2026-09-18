package com.fundacion.enid.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

private val BANCOS = listOf(
    "Bancolombia", "Banco de Bogotá", "Davivienda", "BBVA Colombia", "Banco de Occidente"
)

@Composable
fun PseScreen(
    repository: DataRepository,
    onBack: () -> Unit
) {
    var banco by remember { mutableStateOf(BANCOS.first()) }
    var pagado by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AppScaffold(title = "PAGO POR PSE", onBack = onBack) {
        if (pagado) {
            Icon(
                imageVector = EnidIcons.VOkCirculo,
                contentDescription = null,
                tint = EnidGreen,
                modifier = Modifier.size(44.dp)
            )
            VerticalSpacer(8)
            Text(
                text = "Pago procesado con éxito",
                style = MaterialTheme.typography.headlineMedium,
                color = EnidGreen
            )
            VerticalSpacer(8)
            Text(
                text = "Tu transacción fue aprobada. Recibirás el recibo en tu correo.",
                style = MaterialTheme.typography.bodyLarge,
                color = EnidTextSec
            )
            return@AppScaffold
        }

        Text(
            text = "Resumen de la compra",
            style = MaterialTheme.typography.headlineMedium,
            color = EnidDarkBlue
        )
        VerticalSpacer(10)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(2.dp, com.fundacion.enid.ui.theme.EnidBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Matrícula y pensiones — primer semestre",
                    style = MaterialTheme.typography.titleMedium,
                    color = EnidDarkBlue
                )
                VerticalSpacer(4)
                Text(
                    text = "$830.000 COP",
                    style = MaterialTheme.typography.displayMedium,
                    color = EnidDarkBlue
                )
            }
        }
        VerticalSpacer(16)
        Text(
            text = "Selecciona tu banco",
            style = MaterialTheme.typography.titleMedium,
            color = EnidDarkBlue
        )
        VerticalSpacer(8)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(2.dp, com.fundacion.enid.ui.theme.EnidBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                BANCOS.forEach { opcion ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { banco = opcion }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Column {
                            RowAndRadio(opcion, banco) { banco = opcion }
                        }
                    }
                }
            }
        }
        VerticalSpacer(18)
        BigButton(
            text = "PAGAR $830.000 CON $banco",
            containerColor = EnidPurple,
            icon = EnidIcons.VCandado,
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DataRepository.PSE_URL)))
                } catch (_: Exception) {
                }
                pagado = true
            }
        )
        VerticalSpacer(8)
        Text(
            text = "Transacción protegida por el sistema PSE. Serás redirigido a " +
                "la pasarela segura de tu entidad.",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
    }
}

@Composable
private fun RowAndRadio(opcion: String, seleccion: String, onSeleccionar: () -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = seleccion == opcion,
            onClick = onSeleccionar,
            colors = RadioButtonDefaults.colors(selectedColor = EnidPurple)
        )
        Text(
            text = opcion,
            style = MaterialTheme.typography.bodyLarge,
            color = EnidDarkBlue,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}