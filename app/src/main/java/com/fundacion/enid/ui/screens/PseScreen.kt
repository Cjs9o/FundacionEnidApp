package com.fundacion.enid.ui.screens

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fundacion.enid.data.DataRepository
import com.fundacion.enid.data.EnlacesExternos
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.components.AppScaffold
import com.fundacion.enid.ui.components.BigButton
import com.fundacion.enid.ui.components.EnidIcons
import com.fundacion.enid.ui.components.EnidTab
import com.fundacion.enid.ui.components.VerticalSpacer
import com.fundacion.enid.ui.theme.EnidBorder
import com.fundacion.enid.ui.shared.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.shared.EnidTextSec
import com.fundacion.enid.ui.shared.EnidWhite

private val BANCOS = listOf(
    "Bancolombia", "Banco de Bogotá", "Davivienda", "BBVA Colombia", "Banco de Occidente"
)

/**
 * Pago por PSE.
 *
 * Esta pantalla **no cobra nada**: no hay pasarela conectada ni confirmación del
 * banco. Antes, al tocar "Pagar" ponía `pagado = true` y mostraba "Pago procesado
 * con éxito" sin que hubiera ocurrido nada, incluso con [EnlacesExternos]
 * apagado. Ahora el único resultado honesto es el que devuelve
 * [EnlacesExternos.abrirPse]: si se abrió la pasarela se dice que el pago se
 * confirma allá; si no se abrió, se dice que la Fundación lo confirma. Nunca se
 * afirma que el pago se haya hecho.
 */
@Composable
fun PseScreen(
    onBack: () -> Unit,
    onTabNavigate: ((Screen) -> Unit)? = null
) {
    var banco by remember { mutableStateOf(BANCOS.first()) }
    var abierto by remember { mutableStateOf<Boolean?>(null) }
    val context = LocalContext.current
    val monto = DataRepository.montoMatriculaTexto()

    AppScaffold(
        title = "PAGO POR PSE",
        onBack = onBack,
        activeTab = EnidTab.PAGOS,
        onTabNavigate = onTabNavigate
    ) {
        // `null` = todavía no se intentó nada. `true` = la pasarela se abrió y
        // el pago se completa allá. `false` = no se pudo abrir desde la app.
        abierto?.let { seAbrio ->
            Icon(
                imageVector = if (seAbrio) EnidIcons.VOkCirculo else EnidIcons.VInfo,
                contentDescription = null,
                tint = if (seAbrio) EnidDarkBlue else EnidPurple,
                modifier = Modifier.size(44.dp)
            )
            VerticalSpacer(8)
            Text(
                text = if (seAbrio) "Continúa en la pasarela del banco"
                else "El pago se confirma con un asesor",
                style = MaterialTheme.typography.headlineMedium,
                color = if (seAbrio) EnidDarkBlue else EnidPurple
            )
            VerticalSpacer(8)
            Text(
                text = if (seAbrio)
                    "Te llevamos a la pasarela segura de tu banco. El pago se " +
                        "confirma cuando el banco lo apruebe; esta app no puede " +
                        "decirte si se acreditó."
                else
                    "Desde la app no se puede abrir la banca en este momento. Un " +
                        "asesor ENID te confirma el pago y te envía el recibo.",
                style = MaterialTheme.typography.bodyLarge,
                color = EnidTextSec
            )
            VerticalSpacer(16)
            BigButton(
                text = "VOLVER A INTENTAR",
                containerColor = EnidDarkBlue,
                onClick = { abierto = null }
            )
            return@AppScaffold
        }

        Text(
            text = "Resumen de la compra",
            style = MaterialTheme.typography.headlineMedium,
            color = EnidDarkBlue
        )
        VerticalSpacer(10)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(2.dp, EnidBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Matrícula y pensiones — primer semestre",
                    style = MaterialTheme.typography.titleMedium,
                    color = EnidDarkBlue
                )
                VerticalSpacer(4)
                Text(
                    text = "$monto COP",
                    style = MaterialTheme.typography.displayMedium,
                    color = EnidDarkBlue
                )
                VerticalSpacer(8)
                Text(
                    text = DataRepository.AVISO_MONTO,
                    style = MaterialTheme.typography.bodyMedium,
                    color = EnidTextSec
                )
            }
        }
        VerticalSpacer(16)
        Text(
            text = "Selecciona tu banco",
            style = MaterialTheme.typography.titleMedium,
            color = EnidDarkBlue
        )
        VerticalSpacer(8)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EnidWhite),
            border = BorderStroke(2.dp, EnidBorder)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                BANCOS.forEach { opcion ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { banco = opcion }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        RowAndRadio(opcion, banco) { banco = opcion }
                    }
                }
            }
        }
        VerticalSpacer(18)
        BigButton(
            text = "PAGAR $monto CON $banco",
            containerColor = EnidPurple,
            icon = EnidIcons.VCandado,
            onClick = { abierto = EnlacesExternos.abrirPse(context) }
        )
        VerticalSpacer(8)
        Text(
            text = "Te llevamos a la pasarela segura de tu banco. " +
                "ENID no cobra desde la app: el pago se confirma con el banco " +
                "y con el asesor.",
            style = MaterialTheme.typography.bodyMedium,
            color = EnidTextSec
        )
    }
}

@Composable
private fun RowAndRadio(opcion: String, seleccion: String, onSeleccionar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = seleccion == opcion,
            onClick = onSeleccionar,
            colors = RadioButtonDefaults.colors(selectedColor = EnidPurple)
        )
        Text(
            text = opcion,
            style = MaterialTheme.typography.bodyLarge,
            color = EnidDarkBlue,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}