package com.fundacion.enid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.model.Preinscripcion
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidIconBox
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.InfoCard
import com.fundacion.enid.ui.components.SectionTitle
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.shared.EnidBlueSoft
import com.fundacion.enid.ui.shared.EnidBorderSoft
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.shared.EnidInk
import com.fundacion.enid.ui.shared.EnidRed
import com.fundacion.enid.ui.shared.EnidRedSoft
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.theme.EnidChipDoneText
import com.fundacion.enid.ui.theme.EnidGreenSoft
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidPurpleSoft

/**
 * Ventana de administración.
 *
 * No es una maqueta: lee las preinscripciones, los interesados, los programas y
 * las cuentas locales que ya existen en el dispositivo. Muestra solo datos que
 * la app puede verificar; no hay cifras de ventas ni estados de matrícula
 * inventados (esos los lleva la Fundación en su sistema).
 */
@Composable
fun AdminScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null,
    onOpenPreinscripcion: (() -> Unit)? = null
) {
    val preinscripciones = repository.consultarPreinscripciones()
    val interesados = repository.consultarIntereses()
    val usuarios = repository.listarUsuarios()

    AppScaffold(
        title = "Panel de administración",
        onBack = onBack,
        titleEnd = "Admin",
        activeTab = EnidTab.ADMIN,
        onTabNavigate = onTabNavigate
    ) {
        Text(
            text = "Gestión interna de la Fundación",
            style = MaterialTheme.typography.headlineMedium,
            color = EnidDarkBlue
        )
        Text(
            text = "Resumen de lo registrado en este dispositivo.",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
        VerticalSpacer(18)

        // ── Indicadores ──────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Indicador(
                icono = EnidIcons.VPreinscripcion,
                valor = "${preinscripciones.size}",
                etiqueta = "Preinscripciones",
                colorIcono = EnidChipDoneText,
                fondoIcono = EnidGreenSoft,
                modifier = Modifier.weight(1f)
            )
            Indicador(
                icono = EnidIcons.VCuenta,
                valor = "${interesados.size}",
                etiqueta = "Interesados",
                colorIcono = EnidPurple,
                fondoIcono = EnidPurpleSoft,
                modifier = Modifier.weight(1f)
            )
        }
        VerticalSpacer(10)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Indicador(
                icono = EnidIcons.VOferta,
                valor = "${repository.programas.size}",
                etiqueta = "Programas",
                colorIcono = EnidDarkBlue,
                fondoIcono = EnidBlueSoft,
                modifier = Modifier.weight(1f)
            )
            Indicador(
                icono = EnidIcons.VPerfil,
                valor = "${usuarios.size}",
                etiqueta = "Cuentas locales",
                colorIcono = EnidRed,
                fondoIcono = EnidRedSoft,
                modifier = Modifier.weight(1f)
            )
        }

        VerticalSpacer(22)
        SectionTitle(
            "Preinscripciones recibidas",
            "Cada registro quedó guardado en el teléfono. El estado de matrícula lo lleva la Fundación."
        )
        if (preinscripciones.isEmpty()) {
            EstadoVacio(
                icono = EnidIcons.VPreinscripcion,
                titulo = "Aún no hay preinscripciones",
                detalle = "Cuando alguien complete el formulario aparecerá aquí, con los datos que dejó."
            )
        } else {
            preinscripciones.take(10).forEach { p -> TarjetaPreinscripcion(p) }
            if (onOpenPreinscripcion != null) {
                VerticalSpacer(4)
                Text(
                    text = "El formulario completo se abre desde el menú de la persona interesada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidTextSec
                )
            }
        }

        VerticalSpacer(22)
        SectionTitle("Interesados", "Programas marcados con “Me interesa”.")
        if (interesados.isEmpty()) {
            EstadoVacio(
                icono = EnidIcons.VOferta,
                titulo = "Sin programas marcados",
                detalle = "Los intereses se guardan cuando la persona toca “Me interesa” en el detalle de un programa."
            )
        } else {
            InfoCard {
                interesados.forEachIndexed { indice, programa ->
                    if (indice > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = "• ${programa.nombre}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = EnidInk
                    )
                    Text(
                        text = "Área: ${programa.area.etiqueta}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec
                    )
                }
            }
        }

        VerticalSpacer(22)
        SectionTitle("Cuentas en este dispositivo", "Solo se listan las cuentas creadas sin conexión.")
        if (usuarios.isEmpty()) {
            EstadoVacio(
                icono = EnidIcons.VCuenta,
                titulo = "Sin cuentas locales",
                detalle = "Las cuentas creadas con internet viven en Firebase y no se listan aquí."
            )
        } else {
            InfoCard {
                usuarios.forEachIndexed { indice, usuario ->
                    if (indice > 0) Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = usuario.nombre,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = EnidInk
                    )
                    Text(
                        text = "${usuario.correo} · ${usuario.rol.etiqueta}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EnidTextSec
                    )
                }
            }
        }

        VerticalSpacer(22)
        SectionTitle("Contenido pendiente de la Fundación")
        InfoCard {
            Text(
                text = "Promociones, testimonios y montos oficiales siguen vacíos a propósito: " +
                    "no se inventan datos comerciales. Se activan al llenar las listas de " +
                    "DataRepository.",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidInk
            )
        }
    }
}

@Composable
private fun Indicador(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    valor: String,
    etiqueta: String,
    colorIcono: androidx.compose.ui.graphics.Color,
    fondoIcono: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnidIconBox(
            icon = icono,
            boxSize = 42.dp,
            iconSize = 24.dp,
            iconTint = colorIcono,
            background = fondoIcono
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun TarjetaPreinscripcion(p: Preinscripcion) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(EnidChipDoneText, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = p.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = EnidInk,
                    modifier = Modifier.weight(1f)
                )
                Text(text = p.fecha, style = MaterialTheme.typography.bodySmall, color = EnidTextSec)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Programa: ${p.programa.ifBlank { "Sin definir" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = EnidInk
            )
            Text(
                text = "Año o ciclo: ${p.anio.ifBlank { "Sin definir" }}",
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )
            Text(
                text = "Contacto: ${p.prefijoTelefono} ${p.telefono} · ${p.correo}",
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )
            Text(
                text = "Documento: ${p.tipoDocumento} ${p.documento}",
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )
            if (p.documentos.isNotEmpty()) {
                Text(
                    text = "Documentos declarados: ${p.documentos.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EnidTextSec
                )
            }
        }
    }
}

@Composable
private fun EstadoVacio(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    detalle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnidIconBox(
            icon = icono,
            boxSize = 44.dp,
            iconSize = 25.dp,
            iconTint = EnidTextSec,
            background = EnidBorderSoft
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = EnidInk
            )
            Text(
                text = detalle,
                style = MaterialTheme.typography.bodySmall,
                color = EnidTextSec
            )
        }
    }
}
