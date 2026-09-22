package com.fundacion.enid.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.model.FaqItem
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.BigOutlinedButton
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

private data class Categoria(val titulo: String, val claves: List<String>)

private val CATEGORIAS = listOf(
    Categoria(
        "MATRÍCULAS",
        listOf("requisitos", "inscribirme", "edad", "preinscripci")
    ),
    Categoria(
        "PAGOS",
        listOf("pago", "matricula", "matrícula", "pagar", "vence", "mora", "recargo")
    ),
    Categoria(
        "TRÁMITES Y CERTIFICADOS",
        listOf("certificado", "plataforma")
    ),
    Categoria(
        "ACCESIBILIDAD Y DESCUENTOS",
        listOf("referir", "descuentos", "accesible")
    )
)

@Composable
fun FaqScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    var abierta by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    AppScaffold(title = "Preguntas Frecuentes", onBack = onBack, activeTab = EnidTab.SOPORTE, onTabNavigate = onTabNavigate) {
        Text(
            text = "Resolvemos tus dudas rápidamente",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
        VerticalSpacer(16)

        val categorizadas = categorizar(repository.faqs)
        CATEGORIAS.forEach { categoria ->
            val items = categorizadas[categoria.titulo].orEmpty()
            if (items.isEmpty()) {
                return@forEach
            }
            Text(
                text = "CATEGORÍA: ${categoria.titulo}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EnidTextSec
            )
            VerticalSpacer(6)
            items.forEachIndexed { i, faq ->
                val id = "${categoria.titulo}-$i"
                FaqRow(faq, abierta == id) {
                    abierta = if (abierta == id) null else id
                }
                VerticalSpacer(8)
            }
            VerticalSpacer(10)
        }

        VerticalSpacer(10)
        Text(
            text = "¿Prefieres hablar con una persona?",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = EnidInk
        )
        VerticalSpacer(8)
        BigButton(
            text = "Chat de WhatsApp Directo",
            containerColor = EnidGreen,
            icon = EnidIcons.VWhatsApp,
            onClick = { abrirWhatsApp(context) }
        )
        VerticalSpacer(8)
        BigOutlinedButton(
            text = "Visita www.enid.edu.co",
            icon = EnidIcons.VExterno,
            onClick = { abrirPaginaWeb(context) }
        )
    }
}

private fun abrirWhatsApp(context: Context) {
    val numero = DataRepository.WHATSAPP_NUMERO
    val mensaje = Uri.encode("Hola, quisiera recibir más información sobre la Fundación ENID.")
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$numero?text=$mensaje"))
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

private fun abrirPaginaWeb(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.enid.edu.co"))
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

private fun categorizar(faqs: List<FaqItem>): Map<String, List<FaqItem>> {
    val resultado = LinkedHashMap<String, MutableList<FaqItem>>()
    CATEGORIAS.forEach { resultado[it.titulo] = mutableListOf() }
    val general = mutableListOf<FaqItem>()

    faqs.forEach { faq ->
        val match = CATEGORIAS.firstOrNull { categoria ->
            categoria.claves.any { clave -> faq.pregunta.contains(clave, ignoreCase = true) }
        }
        if (match != null) {
            resultado[match.titulo]?.add(faq)
        } else {
            general.add(faq)
        }
    }

    val lista = resultado.filterValues { it.isNotEmpty() }.toMutableMap()
    if (general.isNotEmpty()) {
        val contenedor = lista.keys.firstOrNull() ?: "TRÁMITES Y CERTIFICADOS"
        lista[contenedor] = (lista[contenedor].orEmpty() + general).toMutableList()
    }
    return lista
}

@Composable
private fun FaqRow(faq: FaqItem, expandida: Boolean, onToggle: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.pregunta,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expandida) EnidIcons.VContraer else EnidIcons.VExpandir,
                    contentDescription = null,
                    tint = EnidTextSec,
                    modifier = Modifier.size(18.dp)
                )
            }
            if (expandida) {
                VerticalSpacer(8)
                Text(
                    text = faq.respuesta,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec
                )
            }
        }
    }
}