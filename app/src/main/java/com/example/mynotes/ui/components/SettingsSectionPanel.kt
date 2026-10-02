package com.example.mynotes.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.SettingsSectionColors
import com.example.mynotes.ui.theme.settingsSectionColors

/**
 * El mismo fondo y radio de las tarjetas Perfil / Iconos de la referencia.
 * Usa el color del tema: conserva la paleta y la intensidad elegidas.
 *
 * Todos los paneles de Configuración comparten por defecto un outset horizontal
 * de 8 dp. Así los recuadros mantienen la misma anchura visual aunque procedan
 * de secciones distintas (Perfil, Sonido, Vibración, Backup, personalización,
 * etc.). El parámetro sigue disponible para casos excepcionales.
 */
@Composable
internal fun SettingsSectionPanel(textColorMode: String, modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(14.dp), horizontalOutset: Dp = 8.dp,
    content: @Composable ColumnScope.(SettingsSectionColors) -> Unit) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val colors = remember(background, textColorMode) {
        settingsSectionColors(background, textColorMode)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                val outset = horizontalOutset.toPx()
                val radius = 18.dp.toPx()
                val topLeft = Offset(-outset, 0f)
                val panelSize = Size(size.width + outset * 2f, size.height)
                onDrawBehind {
                    drawRoundRect(
                        color = colors.background,
                        topLeft = topLeft,
                        size = panelSize,
                        cornerRadius = CornerRadius(radius, radius)
                    )
                }
            }
            .padding(contentPadding)
    ) {
        content(colors)
    }
}

@Composable
internal fun SettingsDropdownItem(
    label: String, textColor: Color, fontFamily: FontFamily,
    fontWeight: FontWeight? = null, overflow: TextOverflow = TextOverflow.Clip,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        modifier = Modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
        text = {
            Text(text = label, modifier = Modifier.fillMaxWidth(), color = textColor,
                fontFamily = fontFamily, fontSize = 12.sp, maxLines = 1,
                textAlign = TextAlign.Center, fontWeight = fontWeight, overflow = overflow)
        },
        onClick = onClick
    )
}

@Composable
internal fun SettingsTitle(
    text: String, color: Color, fontFamily: FontFamily, modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Bold, fontSize: TextUnit = TextUnit.Unspecified
) = Text(text, modifier, color, fontFamily = fontFamily, fontWeight = fontWeight, fontSize = fontSize)

@Composable
internal fun SettingsSecondaryText(
    text: String, color: Color, fontFamily: FontFamily, modifier: Modifier = Modifier, lineHeight: TextUnit = TextUnit.Unspecified
) = Text(text, modifier, color, fontFamily = fontFamily, fontSize = 12.sp, lineHeight = lineHeight)

@Composable
internal fun SettingsToggleRow(
    title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color,
    enabled: Boolean = true, modifier: Modifier = Modifier, compact: Boolean = false, topPadding: Dp = 0.dp
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.then(if (compact) Modifier else Modifier.fillMaxWidth()).padding(top = topPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, Modifier.weight(1f), textColor.copy(alpha = if (enabled) 1f else 0.42f), fontFamily = fontFamily,
            fontSize = if (compact || topPadding > 0.dp) 13.sp else 14.sp, maxLines = if (compact) 2 else Int.MAX_VALUE)
        Switch(checked, UiSoundPlayer.toggleHandler(context, actionBlock = onCheckedChange), enabled = enabled)
    }
}
