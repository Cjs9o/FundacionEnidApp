package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.model.Program
import com.fundacion.enid.model.TipoPrograma
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigTextField
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.SmallButton
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

private data class Filtro(val label: String, val tipo: TipoPrograma?)

private val FILTROS = listOf(
    Filtro("Todos", null),
    Filtro("Técnicos", TipoPrograma.TECNICO),
    Filtro("Cursos", TipoPrograma.CURSO),
    Filtro("Diplomados", TipoPrograma.DIPLOMADO)
)

@Composable
fun CatalogScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onOpenProgram: (Int) -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    var consulta by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf<Filtro>(FILTROS[0]) }

    val lista = repository.programas.filter { programa ->
        val coincideTexto = consulta.isBlank() ||
            programa.nombre.contains(consulta, ignoreCase = true) ||
            programa.descripcion.contains(consulta, ignoreCase = true)
        val coincideFiltro = filtro.tipo == null || programa.tipo == filtro.tipo
        coincideTexto && coincideFiltro
    }

    AppScaffold(title = "Oferta Académica", activeTab = EnidTab.INICIO, onTabNavigate = onTabNavigate) {
        BigTextField(
            value = consulta,
            onValueChange = { consulta = it },
            label = "",
            placeholder = "\uD83D\uDD0D  Buscar programas..."
        )
        VerticalSpacer(12)

        // Filtros (guía SVG: chips redondeados)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            FILTROS.forEach { f ->
                val selected = filtro == f
                Card(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { filtro = f },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) EnidDarkBlue else EnidWhite,
                        contentColor = if (selected) EnidWhite else EnidInk
                    ),
                    border = BorderStroke(1.5.dp, if (selected) EnidDarkBlue else EnidBorderSoft)
                ) {
                    Text(
                        text = f.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                    )
                }
            }
        }
        VerticalSpacer(14)

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
                text = "${programa.duracion} · Sede ${programa.sedes.joinToString(", ")}",
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
                SmallButton(
                    text = "Ver Más",
                    modifier = Modifier.padding(end = 0.dp),
                    onClick = onClick
                )
            }
        }
    }
}