package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.model.EstadoPago
import com.fundacion.enid.model.PaymentNotice
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.StatusChip
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidChipDoneBg
import com.fundacion.enid.ui.theme.EnidChipDoneText
import com.fundacion.enid.ui.theme.EnidChipWarnBg
import com.fundacion.enid.ui.theme.EnidChipWarnText
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

@Composable
fun PaymentsScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onPse: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val venceMatricula = repository.pagos.firstOrNull { it.estado == EstadoPago.VENCE_PRONTAMENTE }
    val pagados = repository.pagos.filter { it.estado == EstadoPago.PAGADO }
    val monto = DataRepository.montoMatriculaTexto(conSigno = false)

    AppScaffold(title = "Mis Pagos", onBack = onBack, activeTab = EnidTab.PAGOS, onTabNavigate = onTabNavigate) {
        // Valor de matrícula destacado
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(1.5.dp, EnidBorderSoft)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Valor de la Matrícula",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EnidTextSec,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Vence: ${venceMatricula?.fechaLimite ?: ""}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EnidRed
                    )
                }
                VerticalSpacer(8)
                Text(
                    text = "$$monto COP",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                VerticalSpacer(6)
                Text(
                    text = DataRepository.AVISO_MONTO,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                VerticalSpacer(10)
                HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
                VerticalSpacer(8)
                Text(
                    text = "Matrícula del periodo: \$$monto",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec
                )
                VerticalSpacer(4)
                Text(
                    text = "Total Pendiente: \$$monto",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
            }
        }
        VerticalSpacer(14)
        BigButton(text = "PAGAR CON PSE", containerColor = EnidPurple, onClick = onPse)
        VerticalSpacer(24)

        SectionTitle("Historial de Pagos")
        VerticalSpacer(6)
        if (pagados.isEmpty()) {
            Text(
                text = "Todavía no tienes pagos registrados.",
                style = MaterialTheme.typography.bodyLarge,
                color = EnidTextSec
            )
        } else {
            pagados.forEach { pago ->
                PaymentRow(pago)
                VerticalSpacer(8)
            }
            VerticalSpacer(4)
            Text(
                text = "Historial de demostración. La Fundación aún no ha confirmado " +
                    "estos movimientos; no consultes este saldo como el definitivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
        }
    }
}

private fun estadoColor(pago: PaymentNotice): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> = when (pago.estado) {
    EstadoPago.PAGADO -> Pair(EnidChipDoneBg, EnidChipDoneText)
    EstadoPago.VENCE_PRONTAMENTE -> Pair(EnidChipWarnBg, EnidChipWarnText)
    EstadoPago.POR_PAGAR -> Pair(EnidRed, EnidWhite)
}

@Composable
private fun PaymentRow(pago: PaymentNotice) {
    val (bg, fg) = estadoColor(pago)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pago.concepto,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(2)
                Text(
                    text = if (pago.estado == EstadoPago.PAGADO) {
                        "Pagado el ${pago.fechaLimite} · ${pago.monto}"
                    } else {
                        "${pago.monto} · Vence: ${pago.fechaLimite}"
                    },
                    fontSize = 12.sp,
                    color = EnidTextSec
                )
                if (pago.estado == EstadoPago.PAGADO) {
                    VerticalSpacer(2)
                    Text(
                        text = pago.detalle,
                        fontSize = 12.sp,
                        color = EnidTextSec
                    )
                }
            }
            StatusChip(label = pago.estado.etiqueta, containerColor = bg, contentColor = fg)
        }
    }
}