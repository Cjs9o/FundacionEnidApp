package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.data.FinderOpcion
import com.fundacion.enid.data.ProgramFinder
import com.fundacion.enid.model.Program
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.tabsSegunSesion
import com.fundacion.enid.ui.components.SmallButton
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGold
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

/**
 * Orientador "¿Qué puedo estudiar?": 5 preguntas y una lista de programas sugeridos.
 * Es una herramienta de orientación, no de venta directa: al final ofrece hablar
 * con un asesor y preinscribirse en el programa elegido.
 */
@Composable
fun ProgramFinderScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onOpenProgram: (Int) -> Unit,
    onPreinscription: (Int) -> Unit = {},
    onTabNavigate: ((com.fundacion.enid.navigation.Screen) -> Unit)? = null
) {
    val context = LocalContext.current
    val preguntas = ProgramFinder.preguntas
    val respuestas = remember { mutableStateListOf<FinderOpcion?>() }
    var indice by remember { mutableStateOf(0) }
    var terminado by remember { mutableStateOf(false) }
    var motivo by remember { mutableStateOf<String?>(null) }

    val sugeridos: List<Program> = remember(respuestas.toList(), terminado) {
        if (!terminado) emptyList()
        else ProgramFinder.recomendar(repository.programas, respuestas.filterNotNull())
    }

    AppScaffold(
        title = "¿Qué puedo estudiar?",
        onBack = onBack,
        titleEnd = if (terminado) null else "Pregunta ${indice + 1} de ${preguntas.size}",
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate,
        tabs = tabsSegunSesion(repository.estaConectado)
    ) {
        if (terminado) {
            Text(
                text = "Estos programas pueden ajustarse a ti",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            VerticalSpacer(4)
            Text(
                text = "Revísalos y habla con un asesor para confirmar el que más te convenga.",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec
            )
            VerticalSpacer(16)

            if (sugeridos.isEmpty()) {
                Text(
                    text = "No pudimos afinar la búsqueda. Pruebas con el catálogo completo o habla con un asesor.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = EnidTextSec
                )
            }

            sugeridos.forEachIndexed { i, programa ->
                SugerenciaCard(
                    numero = i + 1,
                    programa = programa,
                    onOpen = { onOpenProgram(programa.id) },
                    onElegir = { onPreinscription(programa.id) }
                )
                VerticalSpacer(12)
            }

            VerticalSpacer(8)
            BigButton(
                text = "Hablar con un asesor",
                containerColor = EnidDarkBlue,
                icon = EnidIcons.VAyuda,
                onClick = {
                    val detalle = if (sugeridos.isEmpty()) {
                        "Hola, no sé qué programa elegir y necesito orientación."
                    } else {
                        "Hola, Respondí el cuestionario de la app. Los programas que más me interesaron " +
                            "fueron: ${sugeridos.joinToString(", ") { it.nombre }}. ¿Me pueden orientar?"
                    }
                    EnlacesExternos.abrirAsesor(context, detalle)
                }
            )
            VerticalSpacer(10)
            BigButton(
                text = "Repetir las preguntas",
                containerColor = EnidWhite,
                contentColor = EnidDarkBlue,
                onClick = {
                    respuestas.clear()
                    indice = 0
                    terminado = false
                }
            )
            return@AppScaffold
        }

        val pregunta = preguntas[indice]
        Text(
            text = pregunta.titulo,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = EnidInk
        )
        VerticalSpacer(4)
        Text(
            text = pregunta.ayuda,
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
        VerticalSpacer(18)

        // Progreso
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(preguntas.size) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = if (i == preguntas.size - 1) 0.dp else 5.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (i <= indice) EnidRed else EnidBorderSoft)
                )
            }
        }
        VerticalSpacer(18)

        pregunta.opciones.forEach { opcion ->
            OpcionCard(
                opcion = opcion,
                seleccionada = respuestas.getOrNull(indice) == opcion,
                onClick = {
                    if (respuestas.size <= indice) {
                        while (respuestas.size <= indice) respuestas.add(null)
                    }
                    respuestas[indice] = opcion
                }
            )
            VerticalSpacer(10)
        }

        VerticalSpacer(10)
        motivo?.let {
            Text(it, color = EnidRed, style = MaterialTheme.typography.bodyMedium)
            VerticalSpacer(8)
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            if (indice > 0) {
                SmallButton(
                    text = "Atrás",
                    containerColor = EnidWhite,
                    contentColor = EnidDarkBlue,
                    onClick = {
                        motivo = null
                        indice = indice - 1
                    }
                )
            }
        }
        VerticalSpacer(12)
        BigButton(
            text = if (indice == preguntas.lastIndex) "Ver programas para mí" else "Siguiente",
            containerColor = EnidRed,
            onClick = {
                if (respuestas.getOrNull(indice) == null) {
                    motivo = "Elige una opción para continuar."
                } else {
                    motivo = null
                    if (indice == preguntas.lastIndex) terminado = true else indice = indice + 1
                }
            }
        )
    }
}

@Composable
private fun OpcionCard(opcion: FinderOpcion, seleccionada: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, if (seleccionada) EnidRed else EnidBorderSoft)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = seleccionada,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = EnidRed)
            )
            Text(
                text = opcion.texto,
                style = MaterialTheme.typography.bodyLarge,
                color = EnidInk,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun SugerenciaCard(
        numero: Int,
        programa: Program,
        onOpen: () -> Unit,
    onElegir: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // "Opción 1" como distintivo redondeado, en vez de un icono suelto con
            // texto: el número queda centrado y la caja se ajusta al rótulo.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(EnidGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$numero",
                        color = EnidWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = "OPCIÓN $numero PARA TI",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EnidGold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            VerticalSpacer(6)
            Text(
                text = programa.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            VerticalSpacer(4)
            Text(
                text = "${programa.tipo.etiqueta} · ${programa.duracion} · ${programa.modalidad}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = EnidRed
            )
            VerticalSpacer(6)
            Text(
                text = programa.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = EnidTextSec,
                maxLines = 3
            )
            VerticalSpacer(12)
            Row(modifier = Modifier.fillMaxWidth()) {
                SmallButton(text = "Ver Más", onClick = onOpen)
            }
            VerticalSpacer(8)
            BigButton(
                text = "Quiero estudiar este programa",
                containerColor = EnidDarkBlue,
                onClick = onElegir
            )
        }
    }
}
