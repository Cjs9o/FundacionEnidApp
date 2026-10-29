package com.fundacion.enid.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.R
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.state.SesionUi
import com.fundacion.enid.ui.shared.EnidBg
import com.fundacion.enid.ui.shared.EnidBlueMid
import com.fundacion.enid.ui.shared.EnidBlueSoft
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidIconBox
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidPurpleSoft
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidRedSoft
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

// ── Icon box (caja de icono estilo guías SVG) ─────────────────────────────────
@Composable
fun EnidIconBox(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    boxSize: Dp = 36.dp,
    iconSize: Dp = 19.dp,
    iconTint: Color = EnidDarkBlue,
    background: Color = EnidIconBox,
    shape: RoundedCornerShape = RoundedCornerShape(11.dp)
) {
    Box(
        modifier = modifier.size(boxSize).clip(shape).background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(iconSize))
    }
}

// ── Bottom navigation tabs ────────────────────────────────────────────────────
enum class EnidTab(val label: String, val icon: ImageVector, val destino: Screen) {
    INICIO("Inicio", EnidIcons.VInicio, Screen.Home),
    CONOCE("Conoce ENID", EnidIcons.VOferta, Screen.ProspectKnow),
    INSCRIBETE("Inscríbete", EnidIcons.VPreinscripcion, Screen.ProspectEnroll),
    APRENDER("Aprender", EnidIcons.VLibro, Screen.Learn),
    AGENDA("Agenda", EnidIcons.VCalendario, Screen.Agenda),
    PERFIL("Perfil", EnidIcons.VPerfil, Screen.Profile),
    SOPORTE("Soporte", EnidIcons.VAyuda, Screen.Faq),
    ADMIN("Panel", EnidIcons.VPerfil, Screen.Admin)
}

// ── Big primary button ────────────────────────────────────────────────────────
@Composable
fun BigButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = EnidDarkBlue,
    contentColor: Color = EnidWhite,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        // Altura mínima, no fija: los rótulos largos ("Quiero recibir las
        // promociones", "Hablar con un asesor") se reparten en dos líneas en vez
        // de recortarse dentro de una caja de 52 dp.
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 50.dp)
            .padding(vertical = 2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(it, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Outlined button ───────────────────────────────────────────────────────────
@Composable
fun BigOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    borderColor: Color = EnidPurple,
    contentColor: Color = EnidPurple,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp),
        border = BorderStroke(1.5.dp, borderColor),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(it, contentDescription = null, tint = contentColor, modifier = Modifier.size(19.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Small button (botones compactos tipo "Ver más") ──────────────────────────
@Composable
fun SmallButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = EnidRed,
    contentColor: Color = EnidWhite,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(it, contentDescription = null, tint = contentColor, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

// ── Big text field ────────────────────────────────────────────────────────────
@Composable
fun BigTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        placeholder = { Text(placeholder ?: label, style = MaterialTheme.typography.bodyMedium) },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        // Las contraseñas se ocultan siempre. Antes el teclado era de tipo
        // "Password" pero el texto se veía en pantalla; en un teléfono que se
        // comparte (o que se revisa en la sala de espera) eso expone la clave.
        visualTransformation = if (keyboardType == KeyboardType.Password) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        colors = OutlinedTextFieldDefaults.colors(
            // El campo se pinta de blanco: sin esto el OutlinedTextField deja
            // ver el crema del fondo de la pantalla y la caja de texto se
            // confunde con el resto del formulario.
            focusedContainerColor = EnidWhite,
            unfocusedContainerColor = EnidWhite,
            disabledContainerColor = EnidWhite,
            errorContainerColor = EnidWhite,
            focusedBorderColor = EnidDarkBlue,
            unfocusedBorderColor = EnidBorderSoft,
            errorBorderColor = EnidRed
        )
    )
}

// ── Section title ─────────────────────────────────────────────────────────────
@Composable
fun SectionTitle(text: String, subtitulo: String? = null) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = EnidInk,
        letterSpacing = 0.8.sp
    )
    if (subtitulo != null) {
        Text(
            text = subtitulo,
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

// ── Card ──────────────────────────────────────────────────────────────────────
@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.dp, EnidBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) { content() }
    }
}

// ── Scaffold con cabecera clara + barra inferior opcional ───────────────────
@Composable
fun AppScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    titleEnd: String? = null,
    titleEndColor: Color = EnidTextSec,
    brandLine: String? = "ENID \u00B7 EDUCACIÓN",
    scrollable: Boolean = true,
    activeTab: EnidTab? = null,
    onTabNavigate: ((Screen) -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    mostrarAsesor: Boolean = false,
    tabs: List<EnidTab>? = null,
    // Permite que una pantalla controle su propio desplazamiento (la
    // preinscripción lo usa para volver arriba al cambiar de paso). Si no se
    // pasa, el scaffold crea su estado como siempre.
    scrollState: ScrollState? = null,
    content: @Composable () -> Unit
) {
    // Las pestañas dependen de quién mira, no de qué pantalla es. Se resuelve
    // aquí para que ninguna pantalla pueda olvidarlo: si no se pasan `tabs`,
    // se usan las de la sesión actual (el invitado no ve Horario).
    val tabsVisibles = tabs ?: tabsSegunSesion(SesionUi.conectado, SesionUi.rol)
    Column(
        modifier = Modifier.fillMaxSize().background(EnidBg).statusBarsPadding()
    ) {
        if (header != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EnidWhite)
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
            ) {
                header()
            }
        } else {
            // Bloque de título sobre fondo blanco, de borde a borde, con una línea
            // que lo separa del contenido crema. Así todas las pantallas comparten
            // la misma cabecera y ninguna queda "flotando" sobre el fondo.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EnidWhite)
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 10.dp)
            ) {
                if (brandLine != null) {
                    Text(brandLine, color = EnidTextSec, fontSize = 11.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de volver. Antes `onBack` se recibía y se ignoraba:
                    // no había flecha de retorno en ninguna pantalla y el único
                    // camino de vuelta era el gesto del sistema. Para una app
                    // pensada para mayores de 60 años eso deja fuera a quien no
                    // conoce ese gesto. El área táctil es de 48 dp (mínimo
                    // accesible) aunque el glifo sea de 24 dp.
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = EnidIcons.VVolver,
                                contentDescription = "Volver",
                                tint = EnidDarkBlue,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EnidInk,
                        modifier = Modifier.weight(1f)
                    )
                    titleEnd?.let {
                        Text(text = it, color = titleEndColor, fontSize = 12.sp)
                    }
                }
                HorizontalDivider(
                    color = EnidBorderSoft,
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            val estadoScroll = scrollState ?: rememberScrollState()
            val body = if (scrollable) {
                Modifier.fillMaxSize().verticalScroll(estadoScroll)
            } else {
                Modifier.fillMaxSize()
            }
            Column(
                modifier = body.padding(horizontal = 20.dp).padding(top = 12.dp, bottom = 20.dp)
            ) { content() }
        }

        if (mostrarAsesor) {
            // Requerimiento 04: el asesor queda siempre visible, no escondido en un menú.
            AsesorBar()
        }

        if (activeTab != null && onTabNavigate != null) {
            EnidBottomNav(activeTab = activeTab, tabs = tabsVisibles, onNavigate = onTabNavigate)
        }
    }
}

/**
 * Barra fija del asesor (Requerimiento 04: "asesor permanente").
 * Se apoya en [EnlacesExternos], que está deshabilitado durante las pruebas,
 * así que la barra es visible y segura incluso sin abrir apps externas.
 */
@Composable
fun AsesorBar(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // Rojo institucional: el asesor es el CTA principal y se ve como tal.
    // Antes llevaba fondo azul con detalles en ocre, que se leía como un aviso
    // apagado y no como una llamada a la acción.
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = EnidRed
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EnidIconBox(
                icon = EnidIcons.VTelfLocal,
                boxSize = 38.dp,
                iconSize = 21.dp,
                iconTint = EnidWhite,
                background = EnidWhite.copy(alpha = 0.18f)
            )
            Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                Text(
                    text = "Hablar con un asesor ENID",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidWhite
                )
                Text(
                    text = "Te orientamos en el programa y en el proceso de admisión",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidWhite.copy(alpha = 0.85f)
                )
            }
            Text(
                text = "Contactar",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = EnidWhite,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(EnidWhite.copy(alpha = 0.18f))
                    .clickable { EnlacesExternos.abrirAsesor(context) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

// ── Redes sociales oficiales ────────────────────────────────────────────────
/**
 * Accesos a las redes oficiales de la Fundación.
 *
 * Los enlaces salen de `DataRepository` y se abren con `EnlacesExternos`, que
 * durante las pruebas está deshabilitado: la fila se ve y se puede tocar, pero
 * ninguna app externa se abre sin permiso explícito.
 */
@Composable
fun RedesSociales(
    modifier: Modifier = Modifier,
    titulo: String = "Síguenos"
) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = EnidInk
        )
        VerticalSpacer(10)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RedSocial(
                nombre = "Facebook",
                icono = EnidIcons.VFacebook,
                color = EnidBlueMid,
                fondo = EnidBlueSoft,
                modifier = Modifier.weight(1f)
            ) { EnlacesExternos.abrirFacebook(context) }
            RedSocial(
                nombre = "Instagram",
                icono = EnidIcons.VInstagram,
                color = EnidPurple,
                fondo = EnidPurpleSoft,
                modifier = Modifier.weight(1f)
            ) { EnlacesExternos.abrirInstagram(context) }
            RedSocial(
                nombre = "YouTube",
                icono = EnidIcons.VYoutube,
                color = EnidRed,
                fondo = EnidRedSoft,
                modifier = Modifier.weight(1f)
            ) { EnlacesExternos.abrirYouTube(context) }
        }
    }
}

@Composable
private fun RedSocial(
    nombre: String,
    icono: ImageVector,
    color: Color,
    fondo: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 68.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(fondo)
            .clickable(onClickLabel = "Abrir $nombre", onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = "Abrir $nombre",
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = nombre,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// ── Barra de navegación inferior (guías SVG: iconos casa, calendario, ticket, ayuda) ──
/**
 * Pestañas que corresponden a quien está viendo la app. El invitado (camino B)
 * no ve "Horario": es un módulo de estudiante y mostrarlo a alguien
 * que aún no decide es exactamente la confusión que las dos rutas buscan evitar.
 */
fun tabsSegunSesion(
    conectado: Boolean,
    rol: com.fundacion.enid.model.Rol? = null
): List<EnidTab> = when {
    // El administrador no cursa: ve su panel y el soporte institucional.
    rol == com.fundacion.enid.model.Rol.ADMIN ||
        rol == com.fundacion.enid.model.Rol.ADMINISTRATIVO ->
        listOf(EnidTab.INICIO, EnidTab.ADMIN, EnidTab.SOPORTE)
    // El estudiante recorre el Figma de cuatro pestañas. El horario detallado
    // sigue existiendo, pero se entra desde Agenda: la barra no necesita una
    // quinta pestaña para una vista que ya vive dentro de la agenda.
    conectado -> listOf(EnidTab.INICIO, EnidTab.APRENDER, EnidTab.AGENDA, EnidTab.PERFIL)
    // El invitado tiene su propio camino: primero conoce la Fundación y
    // después decide inscribirse. Antes las dos cosas vivían apiladas en una
    // sola pantalla larga; separarlas en la barra deja claro qué hace cada
    // pestaña sin cambiar el contenido ni inventar secciones nuevas.
    else -> listOf(EnidTab.INICIO, EnidTab.CONOCE, EnidTab.INSCRIBETE, EnidTab.SOPORTE)
}

/**
 * Cabecera de las cuatro pestañas del Figma: marca a la izquierda y campana a
 * la derecha. La campana no promete notificaciones reales: al tocarla dice que
 * no hay novedades, que es la verdad mientras la Fundación no conecte un
 * servicio de avisos.
 */
@Composable
fun EnidTabHeader(
    etiquetaRol: String? = null,
    modifier: Modifier = Modifier
) {
    var abierto by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo_enid),
                contentDescription = "Fundación ENID",
                modifier = Modifier.height(36.dp).widthIn(max = 150.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.weight(1f))
            if (!etiquetaRol.isNullOrBlank()) {
                Text(
                    text = etiquetaRol,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EnidTextSec,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Box {
                IconButton(
                    onClick = { abierto = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(EnidWhite)
                        .border(1.dp, EnidBorderSoft, RoundedCornerShape(14.dp))
                ) {
                    Icon(
                        EnidIcons.VCampana,
                        contentDescription = "Notificaciones",
                        tint = EnidDarkBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                DropdownMenu(
                    expanded = abierto,
                    onDismissRequest = { abierto = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("No tienes notificaciones nuevas") },
                        onClick = { abierto = false }
                    )
                }
            }
        }
    }
}

/**
 * Encabezado interno de las pestañas del Figma: antetítulo rojo, título violeta
 * y una línea de apoyo. Se usa en Aprender y Agenda; Perfil tiene su propia
 * cabecera centrada.
 */
@Composable
fun TabPageIntro(
    eyebrow: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = eyebrow.uppercase(),
            color = EnidRed,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EnidDarkBlue
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
    }
}

@Composable
private fun EnidBottomNav(
    activeTab: EnidTab,
    tabs: List<EnidTab>,
    onNavigate: (Screen) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().background(EnidWhite)) {
        HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(top = 4.dp, bottom = 8.dp)
        ) {
            tabs.forEach { tab ->
                val active = tab == activeTab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClickLabel = tab.label) { onNavigate(tab.destino) }
                        .padding(top = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Indicador rojo de pestaña activa, como en el Figma.
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                            .background(if (active) EnidRed else Color.Transparent)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (active) EnidPurple else EnidTextSec,
                        modifier = Modifier.size(23.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                        color = if (active) EnidPurple else EnidTextSec
                    )
                }
            }
        }
    }
}

// ── Link text ─────────────────────────────────────────────────────────────────
@Composable
fun LinkText(text: String, modifier: Modifier = Modifier, color: Color = EnidDarkBlue, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(text, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

// ── Spacers ───────────────────────────────────────────────────────────────────
@Composable
fun VerticalSpacer(height: Int = 12) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Composable
fun HorizontalSpacer(width: Int = 10) {
    Spacer(modifier = Modifier.width(width.dp))
}