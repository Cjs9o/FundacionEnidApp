package com.fundacion.enid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigTextField
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBg
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite
import kotlinx.coroutines.launch

private enum class AuthTab(val label: String) {
    LOGIN("Ingresar"), REGISTRO("Registro"), INVITADO("Invitado")
}

@Composable
fun AuthScreen(
    repository: DataRepository,
    onLoginSuccess: () -> Unit,
    onGuest: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    var tab by remember { mutableStateOf(AuthTab.LOGIN) }
    val scope = rememberCoroutineScope()
    var cargando by remember { mutableStateOf(false) }

    // ── Login state ───────────────────────────────────────────────────────
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var errorLogin by remember { mutableStateOf<String?>(null) }

    // ── Registro state ────────────────────────────────────────────────────
    var nombreRegistro by remember { mutableStateOf("") }
    var correoRegistro by remember { mutableStateOf("") }
    var claveRegistro by remember { mutableStateOf("") }
    var errorRegistro by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EnidBg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
    ) {
        VerticalSpacer(18)

        // Botón de volver a Bienvenida. Esta pantalla no usa `AppScaffold`, así
        // que no lo recibe sola: hay que dibujarlo aquí. Antes no existía, y se
        // llegaba desde "Iniciar Sesión", de modo que la única forma de volver
        // era el gesto del sistema.
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = EnidIcons.VVolver,
                    contentDescription = "Volver",
                    tint = EnidDarkBlue,
                    modifier = Modifier.size(26.dp)
                )
            }
            VerticalSpacer(8)
        }

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
                VerticalSpacer(14)
                Text(
                    text = "Regístrate para guardar tu programa, tus datos y tus avances en este teléfono.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec,
                    lineHeight = 22.sp
                )
                VerticalSpacer(18)
                BigTextField(
                    value = nombreRegistro,
                    onValueChange = { nombreRegistro = it },
                    label = "Nombre completo",
                    placeholder = "Ej. María Fernanda Díaz"
                )
                VerticalSpacer(14)
                BigTextField(
                    value = correoRegistro,
                    onValueChange = { correoRegistro = it },
                    label = "Correo Electrónico",
                    placeholder = "ejemplo@correo.com",
                    keyboardType = KeyboardType.Email
                )
                VerticalSpacer(14)
                BigTextField(
                    value = claveRegistro,
                    onValueChange = { claveRegistro = it },
                    label = "Contraseña",
                    placeholder = "Mínimo 6 caracteres",
                    keyboardType = KeyboardType.Password
                )
                VerticalSpacer(16)
                errorRegistro?.let {
                    Text(it, color = EnidRed, style = MaterialTheme.typography.bodyMedium)
                    VerticalSpacer(10)
                }
                BigButton(
                    text = if (cargando) "Creando cuenta..." else "Crear cuenta",
                    enabled = !cargando
                ) {
                    when {
                        nombreRegistro.isBlank() || correoRegistro.isBlank() || claveRegistro.isBlank() ->
                            errorRegistro = "Completa tu nombre, tu correo y tu contraseña."
                        claveRegistro.length < 6 ->
                            errorRegistro = "La contraseña debe tener al menos 6 caracteres."
                        else -> {
                            errorRegistro = null
                            cargando = true
                            scope.launch {
                                try {
                                    repository.registrar(
                                        com.fundacion.enid.model.User(
                                            nombre = nombreRegistro.trim(),
                                            correo = correoRegistro.trim(),
                                            rol = com.fundacion.enid.model.Rol.ESTUDIANTE
                                        ),
                                        claveRegistro
                                    )
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    errorRegistro = repository.mensajeErrorAuth(e)
                                } finally {
                                    cargando = false
                                }
                            }
                        }
                    }
                }
            }

            AuthTab.INVITADO -> {
                Text(
                    text = "Explora como invitado",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk
                )
                VerticalSpacer(14)
                Text(
                    text = "Navega por la oferta académica, los horarios, las sedes y la información institucional de la Fundación ENID sin necesidad de crear una cuenta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec,
                    lineHeight = 22.sp
                )
                VerticalSpacer(24)
                BigButton(
                    text = "Ingresar como Invitado",
                    onClick = onGuest
                )
            }
        }

        VerticalSpacer(24)
        // Acceso de demostración: mientras la Fundación no tenga un servidor
        // que asigne roles, la app reconoce al administrador por el correo.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EnidWhite, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "Acceso de administrador",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidDarkBlue
                )
                VerticalSpacer(4)
                Text(
                    text = "Mientras la Fundación no asigne roles desde un servidor, " +
                        "cualquier correo que contenga «admin» (por ejemplo " +
                        "admin@enid.edu.co) entra al panel de administración. " +
                        "Los demás correos ven el menú de estudiante.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidTextSec,
                    lineHeight = 18.sp
                )
            }
        }
    }
}