package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.R
import com.fundacion.enid.data.AgendaClases
import com.fundacion.enid.data.CursoAprendizaje
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.data.ProximaClase
import com.fundacion.enid.model.Area
import com.fundacion.enid.model.Program
import com.fundacion.enid.model.Rol
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.EnidTabHeader
import com.fundacion.enid.ui.components.RedesSociales
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBlueMid
import com.fundacion.enid.ui.shared.EnidBlueSoft
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidRedSoft
import com.fundacion.enid.ui.shared.EnidTeal
import com.fundacion.enid.ui.shared.EnidTealSoft
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidChipDoneText
import com.fundacion.enid.ui.theme.EnidGold
import com.fundacion.enid.ui.theme.EnidGoldSoft
import com.fundacion.enid.ui.theme.EnidGreenSoft
import com.fundacion.enid.ui.theme.EnidPurpleSoft
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Inicio del estudiante y del administrador.
 *
 * La estructura del estudiante sigue el Figma: saludo con fecha, curso en
 * progreso, programas sugeridos con filtros, próxima clase y respaldo
 * institucional. Los accesos del diseño anterior (horario, plataforma,
 * preinscripción, calculadora, sede y soporte) se conservan en una sección de
 * servicios para no perder funciones al cambiar la barra inferior.
 */
@Composable
fun HomeScreen(
    repository: DataRepository,
    onNavigate: (Screen) -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val usuario = repository.currentUser
    val conectado = usuario != null
    val nombre = usuario?.nombre ?: "Invitado"
    val context = LocalContextCompat()
    val esAdmin = usuario?.rol == Rol.ADMIN || usuario?.rol == Rol.ADMINISTRATIVO

    AppScaffold(
        title = if (esAdmin) "Administración" else "Inicio",
        header = { EnidTabHeader(etiquetaRol = if (esAdmin) "Admin" else null) },
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate,
        mostrarAsesor = !esAdmin
    ) {
        Saludo(
            nombre = nombre,
            esAdmin = esAdmin,
            programa = usuario?.programa
        )
        VerticalSpacer(18)

        if (esAdmin) {
            HomeAdmin(
                repository = repository,
                onNavigate = onNavigate
            )
        } else {
            HomeEstudiante(
                repository = repository,
                usuario = usuario,
                conectado = conectado,
                onNavigate = onNavigate,
                context = context
            )
        }
    }
}

// ── Inicio del estudiante (estructura del Figma) ─────────────────────────────
@Composable
private fun HomeEstudiante(
    repository: DataRepository,
    usuario: com.fundacion.enid.model.User?,
    conectado: Boolean,
    onNavigate: (Screen) -> Unit,
    context: android.content.Context
) {
    val lecciones = remember(repository) { CursoAprendizaje.lecciones(repository) }
    val completadas = remember(usuario?.correo, lecciones) {
        CursoAprendizaje.completadas(repository, usuario?.correo)
    }
    val porcentaje = CursoAprendizaje.porcentaje(completadas.size, lecciones.size)
    val tituloCurso = CursoAprendizaje.tituloCurso(usuario?.programa)
    val proxima = remember(repository) { AgendaClases.proxima(repository) }

    TarjetaCurso(
        titulo = tituloCurso,
        completadas = completadas.size,
        total = lecciones.size,
        porcentaje = porcentaje,
        onContinuar = { onNavigate(Screen.Learn) }
    )

    VerticalSpacer(28)

    // ── Programas para ti ───────────────────────────────────────────────
    var filtro by remember { mutableStateOf(FiltroPrograma.TODOS) }
    val destacados = remember(repository) {
        listOfNotNull(
            repository.programas.firstOrNull { it.id == 1 },
            repository.programas.firstOrNull { it.id == 2 },
            repository.programas.firstOrNull { it.id == 10 }
        )
    }
    val visibles = destacados.filter { filtro == FiltroPrograma.TODOS || filtro == filtroDe(it.area) }

    EncabezadoSeccion(
        eyebrow = "Explora",
        titulo = "Programas para ti",
        accion = "Ver todos",
        onAccion = { onNavigate(Screen.Catalog) }
    )
    VerticalSpacer(12)
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FiltroPrograma.entries.forEach { opcion ->
            val activo = opcion == filtro
            val forma = RoundedCornerShape(99.dp)
            Text(
                text = opcion.etiqueta,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (activo) EnidWhite else EnidTextSec,
                modifier = Modifier
                    .clip(forma)
                    .background(if (activo) EnidPurple else EnidWhite)
                    .clickable { filtro = opcion }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
    VerticalSpacer(12)
    visibles.forEach { programa ->
        TarjetaProgramaFigma(
            programa = programa,
            onClick = { onNavigate(Screen.ProgramDetail(programa.id)) }
        )
        VerticalSpacer(10)
    }
    if (visibles.isEmpty()) {
        Text(
            text = "No hay programas de esta categoría en la muestra. Mira el catálogo completo.",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
    }

    VerticalSpacer(18)

    // ── Próxima clase ──────────────────────────────────────────────────
    TarjetaProximaClase(
        proxima = proxima,
        onAbrirAgenda = { onNavigate(Screen.Agenda) }
    )

    VerticalSpacer(26)

    // ── Accesos del diseño anterior ────────────────────────────────────
    SectionTitle("Más servicios", "Horario, plataforma, sede y soporte.")
    VerticalSpacer(10)
    Row(modifier = Modifier.fillMaxWidth()) {
        MenuCard(EnidIcons.VCalendario, "Mi horario", EnidDarkBlue, EnidBlueSoft, { onNavigate(Screen.DailySchedule) }, Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))
        MenuCard(EnidIcons.VExterno, "Plataforma", EnidGold, EnidGoldSoft, { onNavigate(Screen.Platform) }, Modifier.weight(1f))
    }
    VerticalSpacer(10)
    Row(modifier = Modifier.fillMaxWidth()) {
        MenuCard(EnidIcons.VPreinscripcion, "Preinscripción", EnidChipDoneText, EnidGreenSoft, { onNavigate(Screen.Preinscription(null)) }, Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))
        MenuCard(EnidIcons.VCalculadora, "Beneficios", EnidPurple, EnidPurpleSoft, { onNavigate(Screen.Benefits) }, Modifier.weight(1f))
    }
    VerticalSpacer(10)
    Row(modifier = Modifier.fillMaxWidth()) {
        MenuCard(EnidIcons.VSedes, "Nuestra sede", EnidRed, EnidRedSoft, { onNavigate(Screen.Locations) }, Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))
        MenuCard(EnidIcons.VAyuda, "Soporte", EnidDarkBlue, EnidBlueSoft, { onNavigate(Screen.Faq) }, Modifier.weight(1f))
    }
    VerticalSpacer(10)
    Row(modifier = Modifier.fillMaxWidth()) {
        MenuCard(EnidIcons.VOferta, "Oferta académica", EnidGold, EnidGoldSoft, { onNavigate(Screen.Catalog) }, Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))
        MenuCard(EnidIcons.VPreinscripcion, "Captura en campo", EnidTealSoft, EnidGreenSoft, { onNavigate(Screen.Campo) }, Modifier.weight(1f))
    }

    val primerInteres = repository.consultarIntereses().firstOrNull()
    if (primerInteres != null) {
        VerticalSpacer(22)
        RecordatorioInteres(
            nombrePrograma = primerInteres.nombre,
            paso = repository.siguientePaso(primerInteres.nombre),
            onVerResumen = { onNavigate(Screen.Intereses) }
        )
    }

    VerticalSpacer(24)
    Text(
        text = "CON EL RESPALDO DE",
        color = EnidRed,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    VerticalSpacer(8)
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.img_10),
            contentDescription = "Organizaciones aliadas de la Fundación ENID",
            modifier = Modifier.width(220.dp).height(70.dp),
            contentScale = ContentScale.Fit
        )
    }

    VerticalSpacer(20)
    PromocionesTestimoniosSection(
        promociones = repository.promociones,
        testimonios = repository.testimonios,
        onHablarAsesor = { EnlacesExternos.abrirAsesor(context) },
        onDejarDatos = { onNavigate(Screen.Preinscription(null)) }
    )
    VerticalSpacer(10)
    RedesSociales()
    VerticalSpacer(8)
}

// ── Inicio del administrador ────────────────────────────────────────────────
@Composable
private fun HomeAdmin(
    repository: DataRepository,
    onNavigate: (Screen) -> Unit
) {
    val usuario = repository.currentUser
    TarjetaAdmin(
        totalPreinscripciones = repository.consultarPreinscripciones().size,
        totalInteresados = repository.consultarIntereses().size,
        onAbrirPanel = { onNavigate(Screen.Admin) }
    )
    VerticalSpacer(24)

    Text(
        text = "Accesos del administrador",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = EnidInk
    )
    VerticalSpacer(12)
    Row(modifier = Modifier.fillMaxWidth()) {
        MenuCard(EnidIcons.VPerfil, "Panel Admin", EnidDarkBlue, EnidBlueSoft, { onNavigate(Screen.Admin) }, Modifier.weight(1f))
        Spacer(modifier = Modifier.width(12.dp))
        MenuCard(EnidIcons.VPreinscripcion, "Preinscripciones", EnidChipDoneText, EnidGreenSoft, { onNavigate(Screen.Admin) }, Modifier.weight(1f))
    }
    VerticalSpacer(12)
    Row(modifier = Modifier.fillMaxWidth()) {
        MenuCard(EnidIcons.VOferta, "Oferta Académica", EnidGold, EnidGoldSoft, { onNavigate(Screen.Catalog) }, Modifier.weight(1f))
        Spacer(modifier = Modifier.width(12.dp))
        MenuCard(EnidIcons.VCuenta, "Mi perfil", EnidPurple, EnidPurpleSoft, { onNavigate(Screen.Profile) }, Modifier.weight(1f))
    }

    VerticalSpacer(24)
    SectionTitle("Más servicios")
    VerticalSpacer(8)
    SecondaryLink(EnidIcons.VSedes, "Sedes y Contacto") { onNavigate(Screen.Locations) }
    SecondaryLink(EnidIcons.VAyuda, "Preguntas Frecuentes") { onNavigate(Screen.Faq) }
    SecondaryLink(EnidIcons.VExterno, "Plataforma Educativa") { onNavigate(Screen.Platform) }
    SecondaryLink(EnidIcons.VPreinscripcion, "Captura en campo") { onNavigate(Screen.Campo) }
    VerticalSpacer(18)
    RedesSociales()
    VerticalSpacer(20)
    Text(
        text = "Sesión de ${usuario?.nombre ?: "administrador"} · ${usuario?.correo.orEmpty()}",
        style = MaterialTheme.typography.bodySmall,
        color = EnidTextSec
    )
    VerticalSpacer(8)
}

// ── Hero del curso en progreso ───────────────────────────────────────────────
@Composable
private fun TarjetaCurso(
    titulo: String,
    completadas: Int,
    total: Int,
    porcentaje: Int,
    onContinuar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(EnidPurple, Color(0xFF414B82), EnidBlueMid)
                )
            )
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.64f)) {
            Text(
                text = "CURSO EN PROGRESO",
                color = EnidWhite,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(EnidRed)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            VerticalSpacer(10)
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidWhite,
                lineHeight = 24.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            VerticalSpacer(14)
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tu progreso",
                    color = EnidWhite.copy(alpha = 0.82f),
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$porcentaje%",
                    color = EnidWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            VerticalSpacer(5)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(EnidWhite.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(porcentaje / 100f)
                        .height(6.dp)
                        .background(EnidTeal)
                )
            }
            VerticalSpacer(5)
            Text(
                text = "$completadas de $total lecciones",
                color = EnidWhite.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
            VerticalSpacer(12)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(11.dp))
                    .background(EnidRed)
                    .clickable(onClickLabel = "Continuar en Aprender", onClick = onContinuar)
                    .padding(horizontal = 13.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Continuar",
                    color = EnidWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    EnidIcons.VPlay,
                    contentDescription = null,
                    tint = EnidWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.img_74),
            contentDescription = "Asistente virtual de ENID",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 14.dp, y = 12.dp)
                .size(118.dp),
            contentScale = ContentScale.Fit
        )
    }
}

// ── Encabezado de sección ───────────────────────────────────────────────────
@Composable
private fun EncabezadoSeccion(
    eyebrow: String,
    titulo: String,
    accion: String? = null,
    onAccion: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = eyebrow.uppercase(),
                color = EnidRed,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            VerticalSpacer(3)
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidDarkBlue
            )
        }
        if (accion != null && onAccion != null) {
            Row(
                modifier = Modifier.clickable(onClick = onAccion).padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = accion,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = EnidTextSec
                )
                Icon(
                    EnidIcons.VFlechaAdelante,
                    contentDescription = null,
                    tint = EnidTextSec,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ── Tarjeta de programa con ilustración (patrón del Figma) ───────────────────
@Composable
private fun TarjetaProgramaFigma(programa: Program, onClick: () -> Unit) {
    val ilustracion = ilustracionDe(programa.id)
    val fondo = when (programa.id % 3) {
        0 -> EnidGreenSoft
        1 -> EnidTealSoft
        else -> EnidPurpleSoft
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.dp, EnidBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(fondo),
                contentAlignment = Alignment.Center
            ) {
                if (ilustracion != null) {
                    Image(
                        painter = painterResource(ilustracion),
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    text = programa.area.etiqueta.uppercase(),
                    color = EnidRed,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                VerticalSpacer(3)
                Text(
                    text = programa.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidPurple,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                VerticalSpacer(5)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        EnidIcons.VReloj,
                        contentDescription = null,
                        tint = EnidTextSec,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "  ${programa.tipo.etiqueta} · ${programa.duracion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(EnidWhite)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    EnidIcons.VFlechaAdelante,
                    contentDescription = "Ver ${programa.nombre}",
                    tint = EnidDarkBlue,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

// ── Tarjeta de próxima clase ────────────────────────────────────────────────
@Composable
private fun TarjetaProximaClase(
    proxima: ProximaClase?,
    onAbrirAgenda: () -> Unit
) {
    val titulo = proxima?.slot?.asignatura ?: "Tu agenda está lista"
    val detalle = if (proxima != null) {
        "${AgendaClases.fechaCorta(proxima.fecha)} · ${proxima.slot.hora}"
    } else {
        "Revisa tus clases y el plan de estudios."
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClickLabel = "Abrir Agenda", onClick = onAbrirAgenda)
    ) {
        Image(
            painter = painterResource(R.drawable.img_71),
            contentDescription = "Clase presencial en la Fundación ENID",
            modifier = Modifier.fillMaxWidth().height(172.dp),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(172.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(EnidDarkBlue.copy(alpha = 0.94f), EnidDarkBlue.copy(alpha = 0.25f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(20.dp)
                .fillMaxWidth(0.74f)
        ) {
            Text(
                text = "PRÓXIMA CLASE",
                color = EnidWhite,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(EnidRed)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            VerticalSpacer(7)
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnidWhite,
                lineHeight = 22.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            VerticalSpacer(9)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    EnidIcons.VReloj,
                    contentDescription = null,
                    tint = EnidWhite.copy(alpha = 0.9f),
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "  $detalle",
                    color = EnidWhite.copy(alpha = 0.9f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ── Tarjeta principal del administrador ─────────────────────────────────────
@Composable
private fun TarjetaAdmin(
    totalPreinscripciones: Int,
    totalInteresados: Int,
    onAbrirPanel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(EnidDarkBlue)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.72f)) {
            Text(
                text = "PANEL DE ADMINISTRACIÓN",
                color = EnidWhite.copy(alpha = 0.75f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            VerticalSpacer(6)
            Text(
                text = "Gestión de la Fundación",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidWhite
            )
            VerticalSpacer(10)
            Row {
                Column {
                    Text(
                        text = "$totalPreinscripciones",
                        color = EnidWhite,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "preinscripciones",
                        color = EnidWhite.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.width(22.dp))
                Column {
                    Text(
                        text = "$totalInteresados",
                        color = EnidWhite,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "interesados",
                        color = EnidWhite.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
            VerticalSpacer(14)
            Text(
                text = "Abrir panel ›",
                color = EnidWhite,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(EnidRed)
                    .clickable { onAbrirPanel() }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            )
        }
        Image(
            painter = painterResource(R.drawable.img_74),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 14.dp, y = 14.dp)
                .size(110.dp),
            contentScale = ContentScale.Fit
        )
    }
}

// ── Saludo con fecha y avatar (patrón del Figma) ─────────────────────────────
@Composable
private fun Saludo(nombre: String, esAdmin: Boolean, programa: String?) {
    val fecha = rememberFechaEspanol()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fecha.uppercase(),
                color = EnidRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            VerticalSpacer(4)
            Text(
                text = "Hola, $nombre",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = EnidDarkBlue
            )
            Text(
                text = when {
                    esAdmin -> "Ventana de administración de la Fundación."
                    programa.isNullOrBlank() -> "Sigamos aprendiendo juntos."
                    else -> programa
                },
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
        }
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(EnidTealSoft),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = inicialesDe(nombre),
                color = EnidDarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.dp, EnidBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EnidIconBox(
                icon = icon,
                boxSize = 42.dp,
                iconSize = 24.dp,
                iconTint = iconColor,
                background = iconBg
            )
            VerticalSpacer(7)
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = EnidInk,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun SecondaryLink(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnidIconBox(
            icon = icon,
            boxSize = 44.dp,
            iconSize = 25.dp,
            iconTint = EnidTextSec
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = EnidInk,
            modifier = Modifier.weight(1f)
        )
        Icon(
            EnidIcons.VFlechaAdelante,
            contentDescription = null,
            tint = EnidRed,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Datos de apoyo ──────────────────────────────────────────────────────────
private enum class FiltroPrograma(val etiqueta: String) {
    TODOS("Todos"),
    NEGOCIOS("Negocios"),
    TECNOLOGIA("Tecnología"),
    SERVICIOS("Servicios")
}

private fun filtroDe(area: Area): FiltroPrograma = when (area) {
    Area.SISTEMAS -> FiltroPrograma.TECNOLOGIA
    Area.ADMINISTRACION, Area.MARKETING, Area.LEGAL -> FiltroPrograma.NEGOCIOS
    Area.TURISMO, Area.OPERATIVOS, Area.PERSONAS, Area.AMBIENTE, Area.ARTE -> FiltroPrograma.SERVICIOS
}

/** Ilustraciones del Figma para los programas destacados del Inicio. */
private fun ilustracionDe(id: Int): Int? = when (id) {
    1 -> R.drawable.img_13   // Analítica de Datos e IA
    2 -> R.drawable.img_23   // Auxiliar Administrativo
    10 -> R.drawable.img_30  // Servicios Hosteleros y Turismo
    else -> null
}

@Composable
private fun rememberFechaEspanol(): String {
    val locale = Locale.forLanguageTag("es-CO")
    return remember { SimpleDateFormat("EEEE, d 'de' MMMM", locale).format(Date()) }
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}

private fun inicialesDe(nombre: String): String =
    nombre.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "EN" }

/**
 * `LocalContext` se usa solo para abrir el asesor. Se deja en una función
 * aparte para que el resto del Home no dependa de Android y sea fácil de leer.
 */
@Composable
private fun LocalContextCompat(): android.content.Context =
    androidx.compose.ui.platform.LocalContext.current
