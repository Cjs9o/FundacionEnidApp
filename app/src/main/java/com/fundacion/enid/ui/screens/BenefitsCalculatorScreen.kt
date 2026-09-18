package com.fundacion.enid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigTextField
import com.fundacion.enid.ui.components.InfoCard
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidGreen

@Composable
fun BenefitsCalculatorScreen(
    repository: DataRepository,
    onBack: () -> Unit
) {
    var referidos by remember { mutableStateOf("") }
    var descuento by remember { mutableStateOf(0) }
    var calculado by remember { mutableStateOf(false) }

    AppScaffold(title = "Calculadora de Beneficios", onBack = onBack) {
        Text(
            text = "Calcula tu descuento",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        VerticalSpacer(8)
        Text(
            text = "Ingresa el número de estudiantes que has referido a la " +
                "Fundación ENID.",
            style = MaterialTheme.typography.bodyLarge
        )
        VerticalSpacer(14)
        BigTextField(
            value = referidos,
            onValueChange = { referidos = it.filter { c -> c.isDigit() } },
            label = "Número de referidos",
            keyboardType = KeyboardType.Number
        )
        VerticalSpacer(12)
        BigButton(
            text = "Calcular descuento",
            containerColor = EnidGreen,
            onClick = {
                val n = referidos.toIntOrNull() ?: 0
                val pct = (n * 10).coerceAtMost(50)
                descuento = pct
                calculado = true
            }
        )
        VerticalSpacer(14)
        if (calculado) {
            InfoCard {
                Text(
                    text = "Descuento estimado en matrícula: $descuento%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                VerticalSpacer(8)
                Text(
                    text = "Por cada estudiante referido se aplica un 10% de descuento " +
                        "en la matrícula, con un máximo del 50%.",
                    style = MaterialTheme.typography.bodyLarge
                )
                VerticalSpacer(8)
                val montoBase = 650000
                val ahorro = montoBase * descuento / 100
                Text(
                    text = "Para una matrícula base de $$montoBase, ahorrarías $" +
                        "${String.format("%,d", ahorro).replace(",", ".")} COP.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}