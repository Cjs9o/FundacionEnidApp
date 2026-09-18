package com.fundacion.enid.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundacion.enid.navigation.Screen
import com.fundacion.enid.ui.theme.EnidBg
import com.fundacion.enid.ui.theme.EnidBorderSoft
import com.fundacion.enid.ui.theme.EnidDarkBlue
import com.fundacion.enid.ui.theme.EnidIconBox
import com.fundacion.enid.ui.theme.EnidInk
import com.fundacion.enid.ui.theme.EnidPurple
import com.fundacion.enid.ui.theme.EnidRed
import com.fundacion.enid.ui.theme.EnidTextSec
import com.fundacion.enid.ui.theme.EnidWhite

// ── Icon box (caja de icono estilo guías SVG) ─────────────────────────────────
@Composable
fun EnidIconBox(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    boxSize: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    iconTint: Color = EnidDarkBlue,
    background: Color = EnidIconBox,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp)
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
    HORARIO("Horario", EnidIcons.VCalendario, Screen.DailySchedule),
    PAGOS("Pagos", EnidIcons.VPagos, Screen.Payments),
    SOPORTE("Soporte", EnidIcons.VAyuda, Screen.Faq)
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
        modifier = modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(it, contentDescription = null, tint = contentColor, modifier = Modifier.size(19.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
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
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(48.dp),
        border = BorderStroke(1.5.dp, borderColor),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(it, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
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
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(34.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium)
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
        label = { Text(label, style = MaterialTheme.typography.bodyLarge) },
        placeholder = { Text(placeholder ?: label, style = MaterialTheme.typography.bodyLarge) },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EnidDarkBlue,
            unfocusedBorderColor = EnidBorderSoft,
            errorBorderColor = EnidRed
        )
    )
}

// ── Section title ─────────────────────────────────────────────────────────────
@Composable
fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = EnidInk,
        letterSpacing = 0.5.sp
    )
}

// ── Card ──────────────────────────────────────────────────────────────────────
@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnidWhite),
        border = BorderStroke(1.5.dp, EnidBorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) { content() }
    }
}

// ── Status chip (Pagado / Vence pronto / Por pagar) ──────────────────────────
@Composable
fun StatusChip(label: String, containerColor: Color, contentColor: Color = EnidWhite) {
    Card(
        shape = RoundedCornerShape(11.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
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
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(EnidBg).statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp)) {
            if (onBack != null) {
                Row(
                    modifier = Modifier.clickable { onBack() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = EnidIcons.VFlechaAtras,
                        contentDescription = null,
                        tint = EnidTextSec,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Volver",
                        color = EnidTextSec,
                        fontSize = 14.sp
                    )
                }
            } else if (brandLine != null) {
                Text(brandLine, color = EnidTextSec, fontSize = 11.sp)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
        }

        Box(modifier = Modifier.weight(1f)) {
            val body = if (scrollable) {
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            } else {
                Modifier.fillMaxSize()
            }
            Column(
                modifier = body.padding(horizontal = 20.dp).padding(top = 10.dp, bottom = 24.dp)
            ) { content() }
        }

        if (activeTab != null && onTabNavigate != null) {
            EnidBottomNav(activeTab = activeTab, onNavigate = onTabNavigate)
        }
    }
}

// ── Barra de navegación inferior (guías SVG: iconos casa, calendario, ticket, ayuda) ──
@Composable
private fun EnidBottomNav(activeTab: EnidTab, onNavigate: (Screen) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(EnidWhite)) {
        HorizontalDivider(color = EnidBorderSoft, thickness = 1.dp)
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(top = 8.dp, bottom = 10.dp)
        ) {
            EnidTab.entries.forEach { tab ->
                val active = tab == activeTab
                Column(
                    modifier = Modifier.weight(1f).clickable { onNavigate(tab.destino) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EnidIconBox(
                        icon = tab.icon,
                        boxSize = 34.dp,
                        iconSize = 19.dp,
                        iconTint = if (active) EnidDarkBlue else EnidTextSec,
                        background = if (active) EnidIconBox else EnidWhite
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = tab.label,
                        fontSize = 10.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                        color = if (active) EnidDarkBlue else EnidTextSec
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
        Text(text, style = MaterialTheme.typography.bodyLarge, color = color)
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