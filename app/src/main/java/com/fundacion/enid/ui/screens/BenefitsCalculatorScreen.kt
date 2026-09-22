package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigTextField
import com.fundacion.enid.ui.components.InfoCard
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidTextSec

@Composable
fun BenefitsCalculatorScreen(
    repository: DataRepository,
    onBack: () -> Unit
) {
    var referidos by remember { mutableStateOf("") }
    var descuento by remember { mutableStateOf(0) }
    var aplicado by remember { mutableStateOf(false) }

    val montoBase = 180000

    AppScaffold(title = "Plan Referidos", onBack = onBack) {
        Text(
            text = "Recomienda y obtén grandes descuentos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = EnidInk
        )
        VerticalSpacer(8)
        Text(
            text = "Invita a tus amigos y conocidos a estudiar en ENID. Por cada " +
                "persona que se matricule, recibirás un 5% de descuento en tu " +
                "matrícula actual.",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
        VerticalSpacer(16)
        BigTextField(
            value = referidos,
            onValueChange = { referidos = it.filter { c -> c.isDigit() } },
            label = "¿Cuántos amigos vas a referir?",
            placeholder = "0",
            keyboardType = KeyboardType.Number
        )
        VerticalSpacer(12)
        BigButton(
            text = "Aplicar Beneficio",
            containerColor = EnidGreen,
            onClick = {
                val n = referidos.toIntOrNull() ?: 0
                descuento = (n * 5).coerceAtMost(50)
                aplicado = true
            }
        )
        VerticalSpacer(18)
        if (aplicado) {
            InfoCard {
                Text(
                    text = "Descuento Obtenido: $descuento%",
                    style = MaterialTheme.typography.titleLarge,
                    color = EnidGreen
                )
                VerticalSpacer(8)
                val ahorro = montoBase * descuento / 100
                val final = montoBase - ahorro
                Text(
                    text = "Precio Final de Matrícula: \$${String.format("%,d", final).replace(",", ".")} COP",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(4)
                Text(
                    text = "Ahorraste \$${String.format("%,d", ahorro).replace(",", ".")} COP",
                    fontSize = 13.sp,
                    color = EnidTextSec
                )
                VerticalSpacer(10)
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = EnidDarkBlue)
                ) {
                    Text(
                        text = "El beneficio quedó calculado y se verá reflejado al formalizar tu matrícula.",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}