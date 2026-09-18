package com.fundacion.enid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.model.Rol
import com.fundacion.enid.model.User
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigTextField
import com.fundacion.enid.ui.components.InfoCard
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBg
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite
import kotlinx.coroutines.launch

private enum class AuthTab(val label: String) {
    LOGIN("Iniciar Sesión"), REGISTRO("Registrarse")
}

@Composable
fun AuthScreen(
    repository: DataRepository,
    modoRegistro: Boolean = false,
    onLoginSuccess: () -> Unit,
    onRegistered: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    var tab by remember { mutableStateOf(if (modoRegistro) AuthTab.REGISTRO else AuthTab.LOGIN) }
    val scope = rememberCoroutineScope()
    var cargando by remember { mutableStateOf(false) }

    // ── Login state ───────────────────────────────────────────────────────
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var errorLogin by remember { mutableStateOf<String?>(null) }

    // ── Register state ────────────────────────────────────────────────────
    var nombreR by remember { mutableStateOf("") }
    var correoR by remember { mutableStateOf("") }
    var claveR by remember { mutableStateOf("") }
    var confirmaR by remember { mutableStateOf("") }
    var rolR by remember { mutableStateOf(Rol.ASPIRANTE.etiqueta) }
    var errorReg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EnidBg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
    ) {
        if (onBack != null) {
            Row(
                modifier = Modifier.padding(top = 8.dp).clickable { onBack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = com.fundacion.enid.ui.components.EnidIcons.VFlechaAtras,
                    contentDescription = null,
                    tint = EnidTextSec,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Volver",
                    color = EnidTextSec,
                    fontSize = 14.sp
                )
            }
        }

        VerticalSpacer(18)

        // ── Tabs (guía SVG: dos botones rectangulares lado a lado) ─────────
        Row(modifier = Modifier.fillMaxWidth()) {
            AuthTab.entries.forEachIndexed { i, t ->
                val selected = tab == t
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = if (i == 0) 0.dp else 8.dp)
                        .background(
                            color = if (selected) EnidDarkBlue else EnidWhite,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    TextButton(onClick = { tab = t }, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = t.label,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) EnidWhite else EnidTextSec,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        VerticalSpacer(24)

        // ── Form content ──────────────────────────────────────────────────
        when (tab) {
            AuthTab.LOGIN -> {
                Text(
                    text = "Ingresa a tu cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(20)
                BigTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = "Correo Electrónico",
                    placeholder = "ejemplo@correo.com",
                    keyboardType = KeyboardType.Email
                )
                VerticalSpacer(14)
                BigTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = "Contraseña",
                    placeholder = "Introduce tu contraseña",
                    keyboardType = KeyboardType.Password
                )
                VerticalSpacer(16)
                errorLogin?.let {
                    Text(it, color = EnidRed, style = MaterialTheme.typography.bodyMedium)
                    VerticalSpacer(10)
                }
                BigButton(
                    text = if (cargando) "Ingresando..." else "Ingresar",
                    enabled = !cargando
                ) {
                    when {
                        correo.isBlank() || clave.isBlank() ->
                            errorLogin = "Ingresa tu correo y tu contraseña."
                        else -> {
                            errorLogin = null
                            cargando = true
                            scope.launch {
                                try {
                                    repository.iniciarSesion(correo.trim(), clave)
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    errorLogin = repository.mensajeErrorAuth(e)
                                } finally {
                                    cargando = false
                                }
                            }
                        }
                    }
                }
                VerticalSpacer(10)
                Text(
                    text = "¿Olvidó mi contraseña?",
                    color = EnidRed,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            errorLogin = "Contacta a la Fundación ENID para recuperar tu acceso."
                        }
                )
            }

            AuthTab.REGISTRO -> {
                Text(
                    text = "Crea tu cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(16)
                BigTextField(value = nombreR, onValueChange = { nombreR = it }, label = "Nombre completo")
                VerticalSpacer(12)
                BigTextField(value = correoR, onValueChange = { correoR = it }, label = "Correo electrónico", keyboardType = KeyboardType.Email)
                VerticalSpacer(12)
                BigTextField(value = claveR, onValueChange = { claveR = it }, label = "Contraseña", keyboardType = KeyboardType.Password)
                VerticalSpacer(12)
                BigTextField(value = confirmaR, onValueChange = { confirmaR = it }, label = "Confirmar contraseña", keyboardType = KeyboardType.Password)
                VerticalSpacer(16)
                Text("Selecciona tu rol:", style = MaterialTheme.typography.titleSmall, color = EnidInk)
                VerticalSpacer(6)
                InfoCard {
                    Rol.entries.forEach { opcion ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = rolR == opcion.etiqueta,
                                onClick = { rolR = opcion.etiqueta },
                                colors = RadioButtonDefaults.colors(selectedColor = EnidDarkBlue)
                            )
                            Text(opcion.etiqueta, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                }
                VerticalSpacer(16)
                errorReg?.let {
                    Text(it, color = EnidRed, style = MaterialTheme.typography.bodyMedium)
                    VerticalSpacer(10)
                }
                BigButton(
                    text = if (cargando) "Registrando..." else "Registrarme",
                    containerColor = EnidRed,
                    enabled = !cargando
                ) {
                    when {
                        nombreR.isBlank() -> errorReg = "Ingresa tu nombre completo."
                        correoR.isBlank() || !correoR.contains("@") -> errorReg = "Ingresa un correo válido."
                        claveR.length < 6 -> errorReg = "La contraseña debe tener al menos 6 caracteres."
                        claveR != confirmaR -> errorReg = "Las contraseñas no coinciden."
                        else -> {
                            val user = User(nombreR.trim(), correoR.trim().lowercase(), Rol.desde(rolR))
                            errorReg = null
                            cargando = true
                            scope.launch {
                                try {
                                    repository.registrar(user, claveR)
                                    onRegistered()
                                } catch (e: Exception) {
                                    errorReg = repository.mensajeErrorAuth(e)
                                } finally {
                                    cargando = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}