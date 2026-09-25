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
import androidx.compose.ui.draw.drawWithCache
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
            /*
             * El fondo de cada panel era recalculado en cada pasada de dibujo.
             * En Settings hay muchos paneles y, durante un fling, eso puede
             * provocar frames irregulares. drawWithCache conserva exactamente
             * la misma geometría visual pero reutiliza el cálculo hasta que
             * cambien tamaño, color u outset.
             */
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
