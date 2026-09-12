package com.example.mynotes.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.theme.SettingsSectionColors
import com.example.mynotes.ui.theme.settingsSectionColors

/**
 * El mismo fondo y radio de las tarjetas Perfil / Iconos de la referencia.
 * Usa el color del tema: conserva la paleta y la intensidad elegidas.
 *
 * Los grupos que antes no tenían tarjeta usan horizontalOutset para dibujar
 * dentro del margen disponible, sin quitar ancho a sus controles. Los que ya
 * tenían tarjeta conservan su relleno. No se recorta ni se intercepta contenido.
 */
@Composable
internal fun SettingsSectionPanel(textColorMode: String, modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(14.dp), horizontalOutset: Dp = 0.dp,
    content: @Composable ColumnScope.(SettingsSectionColors) -> Unit) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val colors = remember(background, textColorMode) {
        settingsSectionColors(background, textColorMode)
    }
    Column(modifier = modifier.fillMaxWidth().drawBehind {
                val outset = horizontalOutset.toPx()
                val radius = 18.dp.toPx()
                drawRoundRect(color = colors.background, topLeft = Offset(-outset, 0f), size = Size(size.width + outset * 2f, size.height),
                    cornerRadius = CornerRadius(radius, radius))
            }.padding(contentPadding)) {
        content(colors)
    }
}
