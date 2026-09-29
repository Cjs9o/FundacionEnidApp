package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.model.Area
import com.fundacion.enid.model.Program
import com.fundacion.enid.model.TipoPrograma
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigTextField
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.tabsSegunSesion
import com.fundacion.enid.ui.components.SmallButton
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGold
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

private data class Filtro(val label: String, val tipo: TipoPrograma?)

private val FILTROS = listOf(
    Filtro("Todos", null),
    Filtro("Técnicos", TipoPrograma.TECNICO),
    Filtro("Cursos", TipoPrograma.CURSO),
    Filtro("Diplomados", TipoPrograma.DIPLOMADO),
    Filtro("Seminarios", TipoPrograma.SEMINARIO)
)

/**
 * "Oferta académica": no solo buscar, sino **explorar y decidir**. Encima del buscador
 * está el orientador "¿Qué puedo estudiar?" y debajo los filtros por tipo y por área.
 */
@Composable
fun CatalogScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onOpenProgram: (Int) -> Unit,
    onOpenFinder: () -> Unit = {},
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    var consulta by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf<Filtro>(FILTROS[0]) }
    var area by remember { mutableStateOf<Area?>(null) }

    val lista = repository.buscarProgramas(consulta, filtro.tipo, area)

    AppScaffold(
        title = "Oferta Académica",
        activeTab = EnidTab.INICIO,
        onTabNavigate = onTabNavigate,
        tabs = tabsSegunSesion(repository.estaConectado)
    ) {
        Text(
            text = "Encuentra tu programa ideal",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = EnidInk
        )
        VerticalSpacer(4)
        Text(
            text = "Responde 5 preguntas y te mostramos los programas que mejor se ajustan a tus intereses.",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
        VerticalSpacer(12)
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onOpenFinder() },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(1.5.dp, EnidRed)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Qué puedo estudiar?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnidRed,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Comenzar",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidWhite,
                    modifier = Modifier
                        .background(EnidRed, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }
        }
        VerticalSpacer(16)

        BigTextField(
            value = consulta,
            onValueChange = { consulta = it },
            label = "",
            placeholder = "Buscar programas..."
        )
        VerticalSpacer(12)

        // Filtros por tipo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            FILTROS.forEach { f ->
                ChipFiltro(
                    texto = f.label,
                    seleccionado = filtro == f,
                    seleccionadoFondo = EnidDarkBlue,
                    onClick = { filtro = f }
                )
            }
        }
        VerticalSpacer(10)

        // Filtros por área
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            ChipFiltro(
                texto = "Todas las áreas",
                seleccionado = area == null,
                seleccionadoFondo = EnidRed,
                onClick = { area = null }
            )
            Area.entries.forEach { a ->
                ChipFiltro(
                    texto = a.etiqueta,
                    seleccionado = area == a,
                    seleccionadoFondo = EnidRed,
                    onClick = { area = if (area == a) null else a }
                )
            }
        }
        VerticalSpacer(14)

        Text(
            text = "${lista.size} programa${if (lista.size == 1) "" else "s"} encontrado${if (lista.size == 1) "" else "s"}",
            style = MaterialTheme.typography.bodySmall,
            color = EnidTextSec
        )
        VerticalSpacer(8)

        if (lista.isEmpty()) {
            Text(
                text = "No se encontraron programas con esa búsqueda.",
                style = MaterialTheme.typography.bodyLarge,
                color = EnidTextSec
            )
        }

        lista.forEach { programa ->
            CourseCard(programa = programa) { onOpenProgram(programa.id) }
            VerticalSpacer(12)
        }
    }
}

@Composable
private fun ChipFiltro(
    texto: String,
    seleccionado: Boolean,
    seleccionadoFondo: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) seleccionadoFondo else EnidWhite,
            contentColor = if (seleccionado) EnidWhite else EnidInk
        ),
        border = BorderStroke(1.5.dp, if (seleccionado) seleccionadoFondo else EnidBorderSoft)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun CourseCard(programa: Program, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = programa.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = EnidInk
            )
            VerticalSpacer(4)
            Text(
                text = programa.tipo.etiqueta + " · " + programa.duracion,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = EnidRed
            )
            VerticalSpacer(2)
            Text(
                text = "${programa.modalidad} · Sede ${programa.sedes.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )
            VerticalSpacer(4)
            Text(
                text = programa.area.etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = EnidGold
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
                SmallButton(
                    text = "Ver Más",
                    icon = EnidIcons.VFlechaAdelante,
                    onClick = onClick
                )
            }
        }
    }
}
