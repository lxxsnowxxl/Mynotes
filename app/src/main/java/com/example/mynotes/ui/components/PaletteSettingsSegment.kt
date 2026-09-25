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
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.theme.SettingsSectionColors
import com.example.mynotes.ui.theme.settingsSectionColors

/**
 * Segmento visual del panel de paletas usado por la LazyColumn de Settings.
 * Permite dividir las paletas en items lazy sin convertir visualmente cada fila
 * en una tarjeta independiente.
 */
@Composable
internal fun PaletteSettingsSegment(
    textColorMode: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalOutset: Dp = 8.dp,
    roundTop: Boolean = false,
    roundBottom: Boolean = false,
    content: @Composable ColumnScope.(SettingsSectionColors) -> Unit
) {
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
                val topRadius = if (roundTop) CornerRadius(radius, radius) else CornerRadius(0f, 0f)
                val bottomRadius = if (roundBottom) CornerRadius(radius, radius) else CornerRadius(0f, 0f)
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = -outset,
                            top = 0f,
                            right = size.width + outset,
                            bottom = size.height,
                            topLeftCornerRadius = topRadius,
                            topRightCornerRadius = topRadius,
                            bottomRightCornerRadius = bottomRadius,
                            bottomLeftCornerRadius = bottomRadius
                        )
                    )
                }
                onDrawBehind {
                    drawPath(path = path, color = colors.background)
                }
            }
            .padding(contentPadding)
    ) {
        content(colors)
    }
}
