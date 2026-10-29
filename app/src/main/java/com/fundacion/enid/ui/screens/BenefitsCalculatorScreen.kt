package com.fundacion.enid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigTextField
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.InfoCard
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidTextSec

@Composable
fun BenefitsCalculatorScreen(
    onBack: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    var referidos by remember { mutableStateOf("") }
    var descuento by remember { mutableStateOf(0) }
    var aplicado by remember { mutableStateOf(false) }

    AppScaffold(
        title = "Plan Referidos",
        onBack = onBack,
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate
    ) {
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
                Text(
                    text = "El porcentaje se aplicará cuando formalices tu matrícula con un asesor de la Fundación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec
                )
            }
        }
    }
}