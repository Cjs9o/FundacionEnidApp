package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.model.Preinscripcion
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigTextField
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.HorizontalSpacer
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

@Composable
fun PreinscriptionScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    var paso by remember { mutableStateOf(1) }
    var nombre by remember { mutableStateOf(repository.currentUser?.nombre ?: "") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf(repository.currentUser?.correo ?: "") }
    var documento by remember { mutableStateOf("") }
    var programaSeleccionado by remember {
        mutableStateOf(repository.programas.firstOrNull()?.nombre ?: "")
    }
    var mostrarProgramas by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var enviado by remember { mutableStateOf(false) }

    fun validarPaso1(): Boolean {
        return when {
            nombre.isBlank() -> { error = "Ingresa tu nombre."; false }
            documento.isBlank() -> { error = "Ingresa tu documento de identidad."; false }
            telefono.isBlank() -> { error = "Ingresa tu teléfono."; false }
            correo.isBlank() || !correo.contains("@") -> { error = "Ingresa un correo válido."; false }
            else -> { error = null; true }
        }
    }

    fun enviar() {
        repository.registrarPreinscripcion(
            Preinscripcion(
                nombre = nombre.trim(),
                documento = documento.trim(),
                correo = correo.trim().lowercase(),
                telefono = telefono.trim(),
                programa = programaSeleccionado,
                fecha = repository.fechaActual()
            )
        )
        enviado = true
    }

    AppScaffold(
        title = "Preinscripción",
        titleEnd = "Paso $paso de 2",
        onBack = onBack,
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate
    ) {
        if (enviado) {
            Icon(
                imageVector = EnidIcons.VOkCirculo,
                contentDescription = null,
                tint = EnidGreen,
                modifier = Modifier.size(46.dp)
            )
            VerticalSpacer(8)
            Text(
                text = "¡Preinscripción enviada con éxito!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidDarkBlue
            )
            VerticalSpacer(10)
            Text(
                text = "Tu información fue almacenada. La Fundación ENID se " +
                    "comunicará contigo para continuar con el proceso.",
                style = MaterialTheme.typography.bodyLarge,
                color = EnidTextSec
            )
            VerticalSpacer(20)
            BigButton(text = "Enviar otra preinscripción") { paso = 1; enviado = false }
            return@AppScaffold
        }

        if (paso == 1) {
            Text(
                text = "Datos Personales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            VerticalSpacer(4)
            Text(
                text = "Por favor, introduce tu información.",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
            VerticalSpacer(18)

            FieldLabel("Nombre Completo")
            BigTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "",
                placeholder = "María Elvira Rodríguez"
            )
            VerticalSpacer(14)
            FieldLabel("Documento de Identidad")
            BigTextField(
                value = documento,
                onValueChange = { documento = it },
                label = "",
                placeholder = "1.025.123.456",
                keyboardType = KeyboardType.Number
            )
            VerticalSpacer(14)
            FieldLabel("Teléfono de Contacto")
            BigTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = "",
                placeholder = "312 456 7890",
                keyboardType = KeyboardType.Phone
            )
            VerticalSpacer(14)
            FieldLabel("Correo Electrónico")
            BigTextField(
                value = correo,
                onValueChange = { correo = it },
                label = "",
                placeholder = "maria.rodriguez@gmail.com",
                keyboardType = KeyboardType.Email
            )
            VerticalSpacer(14)
            FieldLabel("Programa Seleccionado")

            // Selector de programa (guía SVG: caja con ▼)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = EnidWhite),
                border = BorderStroke(1.5.dp, EnidBorderSoft)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mostrarProgramas = !mostrarProgramas }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = programaSeleccionado,
                        style = MaterialTheme.typography.bodyLarge,
                        color = EnidInk,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = EnidIcons.VExpandir,
                        contentDescription = null,
                        tint = EnidTextSec,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (mostrarProgramas) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = EnidWhite),
                    border = BorderStroke(1.5.dp, EnidBorderSoft)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                        repository.programas.forEach { programa ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        programaSeleccionado = programa.nombre
                                        mostrarProgramas = false
                                    }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = programaSeleccionado == programa.nombre,
                                    onClick = {
                                        programaSeleccionado = programa.nombre
                                        mostrarProgramas = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = EnidRed)
                                )
                                Text(
                                    text = programa.nombre,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            VerticalSpacer(18)
            error?.let {
                Text(it, color = EnidRed, style = MaterialTheme.typography.bodyMedium)
                VerticalSpacer(8)
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                BigButton(
                    text = "CONTINUAR",
                    modifier = Modifier.weight(1f),
                    containerColor = EnidDarkBlue
                ) { if (validarPaso1()) paso = 2 }
            }
        } else {
            Text(
                text = "Revisa tus datos antes de enviar.",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
            VerticalSpacer(16)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EnidWhite),
                border = BorderStroke(1.5.dp, EnidBorderSoft)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Campo("Nombre", nombre)
                    Campo("Teléfono", telefono)
                    Campo("Documento", documento.ifBlank { "Sin documento ingresado" })
                    Campo("Correo", correo)
                    Campo("Programa", programaSeleccionado)
                }
            }
            VerticalSpacer(18)
            error?.let {
                Text(it, color = EnidRed, style = MaterialTheme.typography.bodyMedium)
                VerticalSpacer(8)
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                BigButton(
                    text = "ENVIAR PREINSCRIPCIÓN",
                    modifier = Modifier.weight(1f),
                    containerColor = EnidRed,
                    onClick = ::enviar
                )
            }
            VerticalSpacer(10)
            Row(modifier = Modifier.fillMaxWidth()) {
                BigButton(
                    text = "VOLVER AL PASO 1",
                    modifier = Modifier.weight(1f),
                    containerColor = EnidDarkBlue,
                    icon = EnidIcons.VFlechaAtras,
                    onClick = { paso = 1 }
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = EnidTextSec,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun Campo(label: String, valor: String) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = EnidTextSec
    )
    Text(
        text = valor,
        style = MaterialTheme.typography.bodyLarge,
        color = EnidInk
    )
    VerticalSpacer(8)
}