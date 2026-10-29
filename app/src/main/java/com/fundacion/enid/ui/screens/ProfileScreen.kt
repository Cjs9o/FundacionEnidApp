package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.R
import com.fundacion.enid.data.CursoAprendizaje
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.model.Rol
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.EnidTabHeader
import com.fundacion.enid.ui.components.InfoCard
import com.fundacion.enid.ui.components.NotaFormulario
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite
import com.fundacion.enid.ui.theme.EnidPurpleSoft
import com.fundacion.enid.ui.theme.EnidGreenSoft

/**
 * Módulo **Perfil** (pestaña del Figma).
 *
 * La carrera y el año/ciclo siguen siendo datos de solo lectura tomados de la
 * preinscripción: no se editan desde la cuenta. El botón "Ver mis datos"
 * despliega esa información en vez de esconderla detrás de una pantalla que ya
 * no tiene pestaña propia. Los logros y las horas salen del avance real marcado
 * en Aprender.
 */
@Composable
fun ProfileScreen(
    repository: DataRepository,
    onLogout: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null,
    onOpenAdmin: (() -> Unit)? = null,
    onPreinscription: (() -> Unit)? = null
) {
    val usuario = repository.currentUser
    val esAdmin = usuario?.rol == Rol.ADMIN || usuario?.rol == Rol.ADMINISTRATIVO
    val esEstudiante = usuario?.rol == Rol.ESTUDIANTE || usuario?.rol == Rol.ASPIRANTE
    val preinscripcion = usuario?.correo?.let { repository.preinscripcionDe(it) }

    // La preinscripción manda; lo guardado en la cuenta es respaldo de versiones
    // anteriores.
    val programa = preinscripcion?.programa?.takeIf { it.isNotBlank() } ?: usuario?.programa
    val anio = preinscripcion?.anio?.takeIf { it.isNotBlank() } ?: usuario?.anio
    val telefono = preinscripcion?.telefono?.takeIf { it.isNotBlank() } ?: usuario?.telefono
    val documento = preinscripcion?.documento?.takeIf { it.isNotBlank() } ?: usuario?.documento

    val lecciones = remember(repository) { CursoAprendizaje.lecciones(repository) }
    val completadas = remember(usuario?.correo, lecciones) {
        CursoAprendizaje.completadas(repository, usuario?.correo)
    }
    val totalLecciones = lecciones.size
    val logros = listOf(
        completadas.size >= 1,
        completadas.size >= 3,
        totalLecciones > 0 && completadas.size >= totalLecciones
    )
    val logrosObtenidos = logros.count { it }
    val minutos = completadas.sumOf { indice -> lecciones.getOrNull(indice)?.minutos ?: 0 }
    val horas = minutos / 60

    var mostrarDatos by remember { mutableStateOf(false) }
    var notificaciones by remember { mutableStateOf(repository.notificacionesActivas) }
    var mostrarPrivacidad by remember { mutableStateOf(false) }

    AppScaffold(
        title = "Perfil",
        header = { EnidTabHeader(etiquetaRol = if (esAdmin) "Admin" else null) },
        activeTab = EnidTab.PERFIL,
        onTabNavigate = onTabNavigate
    ) {
        // ── Cabecera del perfil ─────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(EnidGreenSoft)
                    .border(4.dp, EnidWhite, RoundedCornerShape(27.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicialesDe(usuario?.nombre ?: "ENID"),
                    color = EnidDarkBlue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            VerticalSpacer(12)
            Text(
                text = usuario?.nombre ?: "Invitado",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = EnidDarkBlue,
                textAlign = TextAlign.Center
            )
            VerticalSpacer(3)
            Text(
                text = when {
                    esAdmin -> "Administrador ENID"
                    programa.isNullOrBlank() -> "Estudiante ENID"
                    else -> "Estudiante ENID · $programa"
                },
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec,
                textAlign = TextAlign.Center
            )
            VerticalSpacer(12)
            Text(
                text = if (mostrarDatos) "Ocultar mis datos" else "Ver mis datos",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = EnidDarkBlue,
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .border(1.dp, EnidBorderSoft, RoundedCornerShape(99.dp))
                    .background(EnidWhite)
                    .clickable { mostrarDatos = !mostrarDatos }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        VerticalSpacer(24)

        // ── Estadísticas reales ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(19.dp))
                .background(EnidDarkBlue)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Estadistica(
                valor = if (programa.isNullOrBlank()) "0" else "1",
                etiqueta = "Programa",
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(34.dp)
                    .background(EnidWhite.copy(alpha = 0.16f))
            )
            Estadistica(
                valor = "$horas h",
                etiqueta = "Aprendizaje",
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(34.dp)
                    .background(EnidWhite.copy(alpha = 0.16f))
            )
            Estadistica(
                valor = "$logrosObtenidos",
                etiqueta = "Logros",
                modifier = Modifier.weight(1f)
            )
        }

        VerticalSpacer(26)

        // ── Mis logros ──────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis logros",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnidDarkBlue,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$logrosObtenidos de 3",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = com.fundacion.enid.ui.shared.EnidBlueMid
            )
        }
        VerticalSpacer(12)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Insignia(
                imagen = R.drawable.img_33,
                etiqueta = "Primer paso",
                obtenida = logros[0],
                modifier = Modifier.weight(1f)
            )
            Insignia(
                imagen = R.drawable.img_55,
                etiqueta = "Constancia",
                obtenida = logros[1],
                modifier = Modifier.weight(1f)
            )
            Insignia(
                imagen = R.drawable.img_62,
                etiqueta = "Innovadora",
                obtenida = logros[2],
                modifier = Modifier.weight(1f)
            )
        }

        VerticalSpacer(26)

        // ── Datos de la cuenta (desplegables) ───────────────────────────
        if (mostrarDatos) {
            DatosDeLaCuenta(
                esAdmin = esAdmin,
                esEstudiante = esEstudiante,
                nombre = usuario?.nombre,
                correo = usuario?.correo,
                rol = usuario?.rol?.etiqueta,
                programa = programa,
                anio = anio,
                telefono = telefono,
                documento = documento,
                preinscripcion = preinscripcion,
                onPreinscription = onPreinscription
            )
            VerticalSpacer(24)
        }

        // ── Ajustes ─────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(19.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(1.dp, EnidBorderSoft),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                FilaAjuste(
                    icono = EnidIcons.VCertificado,
                    titulo = "Mis certificados",
                    detalle = "Se consultan en la plataforma educativa"
                ) { onTabNavigate?.invoke(Screen.Platform) }
                HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
                FilaAjusteSwitch(
                    icono = EnidIcons.VCampana,
                    titulo = "Notificaciones",
                    detalle = "Avisos y recordatorios en este teléfono",
                    activo = notificaciones
                ) { activo ->
                    notificaciones = activo
                    repository.notificacionesActivas = activo
                }
                HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
                FilaAjuste(
                    icono = EnidIcons.VAyuda,
                    titulo = "Ayuda y soporte",
                    detalle = "Preguntas frecuentes y contacto"
                ) { onTabNavigate?.invoke(Screen.Faq) }
                HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
                FilaAjuste(
                    icono = EnidIcons.VPrivacidad,
                    titulo = "Privacidad",
                    detalle = "Qué datos guarda la app"
                ) { mostrarPrivacidad = true }
                if (esAdmin && onOpenAdmin != null) {
                    HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
                    FilaAjuste(
                        icono = EnidIcons.VPerfil,
                        titulo = "Panel de administración",
                        detalle = "Preinscripciones, interesados y cuentas"
                    ) { onOpenAdmin() }
                }
            }
        }

        VerticalSpacer(8)
        Text(
            text = "Cerrar sesión",
            color = EnidRed,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = "Cerrar sesión", onClick = onLogout)
                .padding(vertical = 14.dp)
        )
        VerticalSpacer(6)
    }

    if (mostrarPrivacidad) {
        AlertDialog(
            onDismissRequest = { mostrarPrivacidad = false },
            title = { Text("Privacidad y datos") },
            text = {
                Text(
                    "La app guarda en este teléfono tu sesión, tus preinscripciones y " +
                        "tu avance en Aprender. No procesa pagos ni recibe datos bancarios. " +
                        "Cuando abres la plataforma, las redes sociales o el mapa, la app " +
                        "externa aplica sus propias políticas."
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarPrivacidad = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
private fun Estadistica(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor,
            color = EnidWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        VerticalSpacer(2)
        Text(
            text = etiqueta,
            color = EnidWhite.copy(alpha = 0.78f),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun Insignia(
    imagen: Int,
    etiqueta: String,
    obtenida: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(if (obtenida) EnidPurpleSoft else EnidBorderSoft.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(imagen),
                contentDescription = if (obtenida) etiqueta else "$etiqueta bloqueado",
                modifier = Modifier.size(54.dp).alpha(if (obtenida) 1f else 0.38f),
                contentScale = ContentScale.Fit
            )
        }
        VerticalSpacer(6)
        Text(
            text = etiqueta,
            color = if (obtenida) EnidInk else EnidTextSec,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        if (!obtenida) {
            Text(
                text = "Bloqueado",
                color = EnidTextSec,
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FilaAjuste(
    icono: ImageVector,
    titulo: String,
    detalle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = titulo, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnidIconBox(
            icon = icono,
            boxSize = 36.dp,
            iconSize = 19.dp,
            iconTint = EnidDarkBlue,
            background = EnidPurpleSoft
        )
        Column(modifier = Modifier.weight(1f).padding(start = 11.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            Text(
                text = detalle,
                fontSize = 11.sp,
                color = EnidTextSec,
                lineHeight = 15.sp
            )
        }
        Icon(
            EnidIcons.VFlechaAdelante,
            contentDescription = null,
            tint = EnidTextSec,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun FilaAjusteSwitch(
    icono: ImageVector,
    titulo: String,
    detalle: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnidIconBox(
            icon = icono,
            boxSize = 36.dp,
            iconSize = 19.dp,
            iconTint = EnidDarkBlue,
            background = EnidPurpleSoft
        )
        Column(modifier = Modifier.weight(1f).padding(start = 11.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            Text(
                text = detalle,
                fontSize = 11.sp,
                color = EnidTextSec,
                lineHeight = 15.sp
            )
        }
        Switch(
            checked = activo,
            onCheckedChange = onCambio,
            colors = SwitchDefaults.colors(
                checkedThumbColor = EnidWhite,
                checkedTrackColor = com.fundacion.enid.ui.shared.EnidTeal,
                uncheckedThumbColor = EnidWhite,
                uncheckedTrackColor = EnidBorderSoft
            )
        )
    }
}

@Composable
private fun DatosDeLaCuenta(
    esAdmin: Boolean,
    esEstudiante: Boolean,
    nombre: String?,
    correo: String?,
    rol: String?,
    programa: String?,
    anio: String?,
    telefono: String?,
    documento: String?,
    preinscripcion: com.fundacion.enid.model.Preinscripcion?,
    onPreinscription: (() -> Unit)?
) {
    Text(
        text = "Mi información",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = EnidDarkBlue
    )
    VerticalSpacer(10)
    InfoCard {
        DatoPerfil("Nombre", nombre)
        DatoPerfil("Correo electrónico", correo)
        DatoPerfil("Rol", rol)
    }

    if (esEstudiante) {
        VerticalSpacer(16)
        Text(
            text = "Datos académicos",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = EnidDarkBlue
        )
        Text(
            text = "Se definen en tu preinscripción. Para cambiarlos, contacta a la Fundación.",
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec
        )
        VerticalSpacer(8)
        InfoCard {
            DatoPerfil("Carrera / Programa", programa)
            DatoPerfil("Año o ciclo", anio)
            preinscripcion?.fecha?.takeIf { it.isNotBlank() }?.let {
                DatoPerfil("Fecha de preinscripción", it)
            }
        }
        if (preinscripcion == null) {
            VerticalSpacer(10)
            NotaFormulario(
                "Aún no tienes una preinscripción registrada. La carrera y el " +
                    "año o ciclo se eligen al diligenciarla."
            )
            if (onPreinscription != null) {
                VerticalSpacer(12)
                BigButton(
                    text = "HACER MI PREINSCRIPCIÓN",
                    containerColor = EnidDarkBlue,
                    onClick = onPreinscription
                )
            }
        }

        VerticalSpacer(16)
        Text(
            text = "Datos personales",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = EnidDarkBlue
        )
        Text(
            text = "Tomados de tu preinscripción. No se editan desde la cuenta.",
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec
        )
        VerticalSpacer(8)
        InfoCard {
            DatoPerfil("Documento", documento)
            DatoPerfil("Teléfono", telefono)
            preinscripcion?.fechaNacimiento
                ?.takeIf { it.isNotBlank() }
                ?.let { DatoPerfil("Fecha de nacimiento", it) }
            preinscripcion?.nivelFormacion
                ?.takeIf { it.isNotBlank() }
                ?.let { DatoPerfil("Nivel de formación", it) }
            preinscripcion?.estadoCivil
                ?.takeIf { it.isNotBlank() }
                ?.let { DatoPerfil("Estado civil", it) }
            preinscripcion?.telefonoEmergencia
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    val prefijo = preinscripcion.prefijoEmergencia
                    DatoPerfil("Contacto de emergencia", "$prefijo $it".trim())
                }
        }
    }

    if (esAdmin) {
        VerticalSpacer(12)
        InfoCard {
            Text(
                text = "Como administrador puedes revisar preinscripciones, interesados y " +
                    "cuentas locales desde el panel de administración.",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
        }
    }
}

/** Fila de dato de solo lectura; si no hay valor, lo dice en vez de inventarlo. */
@Composable
private fun DatoPerfil(etiqueta: String, valor: String?) {
    Text(
        text = etiqueta,
        style = MaterialTheme.typography.labelMedium,
        color = EnidTextSec,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = valor?.takeIf { it.isNotBlank() } ?: "Pendiente",
        style = MaterialTheme.typography.bodyLarge,
        color = if (valor.isNullOrBlank()) EnidTextSec else EnidInk,
        modifier = Modifier.fillMaxWidth()
    )
    VerticalSpacer(10)
}

private fun inicialesDe(nombre: String): String =
    nombre.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "EN" }
