package com.fundacion.enid.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fundacion.enid.model.Promocion
import com.fundacion.enid.model.Testimonio
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidGold
import com.fundacion.enid.ui.theme.EnidGreen
import com.fundacion.enid.ui.theme.EnidGreenSoft
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

/**
 * Requerimientos 05 y 06: promociones y testimonios.
 *
 * Las listas llegan vacías desde `DataRepository` porque no hay promociones
 * confirmadas ni testimonios entregados por la Fundación. En vez de inventar
 * cifras o citas, esta sección explica qué va a aparecer ahí y convierte el
 * espacio en un punto de captura: el interesado deja su dato o habla con un asesor.
 *
 * En cuanto la Fundación entregue el contenido, basta con llenar
 * `DataRepository.promociones` y `DataRepository.testimonios`: las tarjetas
 * aparecen tal cual, sin tocar esta pantalla.
 */
@Composable
fun PromocionesTestimoniosSection(
    promociones: List<Promocion>,
    testimonios: List<Testimonio>,
    onHablarAsesor: () -> Unit,
    onDejarDatos: () -> Unit
) {
    VerticalSpacer(22)

    // ── Promociones ──────────────────────────────────────────────────────
    SectionTitle(
        "Promociones y beneficios",
        "Descuentos, alianzas y convocatorias de la Fundación."
    )
    VerticalSpacer(10)

    if (promociones.isEmpty()) {
        SeccionEnPreparacion(
            texto = "Estamos preparando las próximas promociones de ENID. " +
                "Déjanos tus datos y te avisamos apenas se abran."
        )
        VerticalSpacer(8)
        BigButton(
            text = "Quiero recibir las promociones",
            modifier = Modifier.fillMaxWidth(),
            containerColor = EnidGreen,
            onClick = onDejarDatos
        )
    } else {
        promociones.forEach { promo ->
            PromocionCard(promo)
            VerticalSpacer(8)
        }
    }

    VerticalSpacer(22)

    // ── Testimonios ──────────────────────────────────────────────────────
    SectionTitle(
        "Historias de estudiantes ENID",
        "Lo que dicen quienes ya estudian con nosotros."
    )
    VerticalSpacer(10)

    if (testimonios.isEmpty()) {
        SeccionEnPreparacion(
            texto = "Pronto podrás conocer aquí las historias de los estudiantes " +
                "que ya hacen parte de ENID."
        )
        VerticalSpacer(8)
        BigButton(
            text = "Hablar con un asesor",
            modifier = Modifier.fillMaxWidth(),
            containerColor = EnidDarkBlue,
            onClick = onHablarAsesor
        )
    } else {
        testimonios.forEach { testimonio ->
            TestimonioCard(testimonio)
            VerticalSpacer(8)
        }
    }
}

@Composable
private fun SeccionEnPreparacion(texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EnidGreenSoft),
        border = BorderStroke(1.5.dp, EnidGreen.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Contenido en preparación",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = EnidGreen
            )
            Text(
                text = texto,
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun PromocionCard(promo: Promocion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (promo.destacada) EnidGold.copy(alpha = 0.14f) else EnidWhite
        ),
        border = BorderStroke(1.5.dp, if (promo.destacada) EnidGold else EnidBorderSoft)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = etiqueta(promo.tipo.name),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (promo.destacada) EnidGold else EnidPurple,
                    modifier = Modifier
                        .background(
                            color = if (promo.destacada) EnidGold.copy(alpha = 0.2f) else EnidPurple.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
                Text(
                    text = promo.vigencia,
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidTextSec,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = promo.titulo,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = EnidInk,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = promo.detalle,
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun TestimonioCard(testimonio: Testimonio) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "“${testimonio.cita}”",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = EnidInk
            )
            Text(
                text = "${testimonio.nombre} · ${testimonio.programa}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = EnidGold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun etiqueta(tipo: String): String = when (tipo) {
    "DESCUENTO" -> "Descuento"
    "ALIANZA" -> "Alianza"
    "CONVOCATORIA" -> "Convocatoria"
    else -> "Oportunidad"
}
