package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.R
import com.fundacion.enid.data.CursoAprendizaje
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.EnidTabHeader
import com.fundacion.enid.ui.components.TabPageIntro
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidTeal
import com.fundacion.enid.ui.shared.EnidTealDark
import com.fundacion.enid.ui.shared.EnidTealSoft
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidPurpleSoft

/**
 * Módulo **Aprender** (pestaña del Figma).
 *
 * El temario sale del horario real de la Fundación, no de un curso inventado.
 * La persona marca cada lección y ese avance queda guardado en el teléfono;
 * Perfil lee el mismo dato para mostrar lecciones y logros.
 */
@Composable
fun LearnScreen(
    repository: DataRepository,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    val usuario = repository.currentUser
    val correo = usuario?.correo
    val lecciones = remember(repository) { CursoAprendizaje.lecciones(repository) }
    val total = lecciones.size

    var completadas by remember(correo) {
        mutableStateOf(CursoAprendizaje.completadas(repository, correo))
    }

    val porcentaje = CursoAprendizaje.porcentaje(completadas.size, total)
    val tituloCurso = CursoAprendizaje.tituloCurso(usuario?.programa)

    fun alternar(indice: Int) {
        completadas = if (indice in completadas) {
            completadas - indice
        } else {
            completadas + indice
        }
        repository.guardarLeccionesCompletadas(correo, completadas)
    }

    AppScaffold(
        title = "Aprender",
        header = { EnidTabHeader() },
        activeTab = EnidTab.APRENDER,
        onTabNavigate = onTabNavigate
    ) {
        TabPageIntro(
            eyebrow = "Mi aprendizaje",
            title = "Continúa creciendo",
            subtitle = "Avanza a tu ritmo y marca cada lección al terminar."
        )
        VerticalSpacer(20)

        // ── Portada del curso activo ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(EnidPurple, EnidDarkBlue, com.fundacion.enid.ui.shared.EnidBlueMid)
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.66f)) {
                Text(
                    text = "PROGRAMA ACTIVO",
                    color = EnidTealSoft,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                VerticalSpacer(6)
                Text(
                    text = tituloCurso,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidWhite,
                    lineHeight = 24.sp
                )
                VerticalSpacer(8)
                Text(
                    text = "${completadas.size} de $total lecciones completadas",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidWhite.copy(alpha = 0.88f)
                )
            }
            Image(
                painter = painterResource(R.drawable.img_74),
                contentDescription = "Asistente virtual de ENID",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 12.dp, y = 10.dp)
                    .size(122.dp),
                contentScale = ContentScale.Fit
            )
        }

        VerticalSpacer(14)

        // ── Barra de progreso del curso ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .height(7.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(EnidBorderSoft)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(porcentaje / 100f)
                    .height(7.dp)
                    .background(EnidTeal)
            )
        }
        VerticalSpacer(24)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Contenido del curso",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnidDarkBlue,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$porcentaje%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = com.fundacion.enid.ui.shared.EnidBlueMid
            )
        }
        VerticalSpacer(12)

        lecciones.forEachIndexed { indice, leccion ->
            LeccionFila(
                numero = indice + 1,
                titulo = leccion.titulo,
                detalle = leccion.detalle,
                completada = indice in completadas,
                onClick = { alternar(indice) }
            )
            VerticalSpacer(9)
        }

        VerticalSpacer(15)
        LogroAprendizaje(completadas = completadas.size, total = total)
        VerticalSpacer(15)

        Text(
            text = "Tu avance se guarda en este teléfono y se refleja en tu Perfil.",
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        VerticalSpacer(10)
    }
}

@Composable
private fun LeccionFila(
    numero: Int,
    titulo: String,
    detalle: String,
    completada: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = if (completada) "Marcar como pendiente" else "Marcar como completada", onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (completada) EnidTealSoft.copy(alpha = 0.45f) else EnidWhite
        ),
        border = BorderStroke(1.dp, if (completada) EnidTeal else EnidBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (completada) EnidTeal else EnidPurpleSoft),
                contentAlignment = Alignment.Center
            ) {
                if (completada) {
                    Icon(
                        EnidIcons.VOk,
                        contentDescription = null,
                        tint = EnidWhite,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "$numero",
                        color = EnidDarkBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk,
                    lineHeight = 21.sp
                )
                VerticalSpacer(3)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        EnidIcons.VReloj,
                        contentDescription = null,
                        tint = EnidTextSec,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "  $detalle",
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec
                    )
                }
            }
            Icon(
                imageVector = if (completada) EnidIcons.VLibro else EnidIcons.VPlay,
                contentDescription = null,
                tint = if (completada) EnidTealDark else EnidDarkBlue,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}

@Composable
private fun LogroAprendizaje(completadas: Int, total: Int) {
    val titulo = when {
        completadas <= 0 -> "¡Empieza hoy!"
        completadas < 3 -> "¡Vas por buen camino!"
        completadas < total -> "¡Casi lo logras!"
        else -> "¡Curso completado!"
    }
    val mensaje = when {
        completadas <= 0 -> "Marca tu primera lección para desbloquear tu insignia."
        completadas < 3 -> "Completa 3 lecciones para desbloquear tu primera insignia."
        completadas < total -> "Te falta ${total - completadas} lección para completar el curso."
        else -> "Terminaste las $total lecciones. Tu constancia ya es un logro."
    }
    // La etiqueta no puede decir "NUEVO LOGRO" cuando todavía no hay ninguno.
    val eyebrow = when {
        completadas <= 0 -> "TU PRÓXIMO LOGRO"
        completadas >= total -> "LOGRO DESBLOQUEADO"
        else -> "NUEVO LOGRO"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(19.dp))
            .background(EnidPurple)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.img_44),
            contentDescription = null,
            modifier = Modifier.size(54.dp),
            contentScale = ContentScale.Fit
        )
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(
                text = eyebrow,
                color = EnidTealSoft,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            VerticalSpacer(3)
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EnidWhite
            )
            VerticalSpacer(2)
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodySmall,
                color = EnidWhite.copy(alpha = 0.82f),
                lineHeight = 17.sp
            )
        }
    }
}
