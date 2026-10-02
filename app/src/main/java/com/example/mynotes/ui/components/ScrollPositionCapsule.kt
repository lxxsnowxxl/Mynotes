package com.example.mynotes.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.theme.softenUiColorToContrast
import kotlin.math.abs

/**
 * Indicador vertical no interactivo con forma de cápsula.
 *
 * No reemplaza el gesto de desplazamiento ni modifica el estado del contenido:
 * únicamente observa el estado ya existente y dibuja una referencia visual de
 * la posición actual. De esta forma puede añadirse a pantallas antiguas sin
 * alterar su comportamiento de scroll, sus límites ni la lógica de negocio.
 */
@Composable
fun ScrollPositionCapsule(
    state: ScrollState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    preferredColor: Color = MaterialTheme.colorScheme.onBackground
) {
    var viewportHeightPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(10.dp)
            .onSizeChanged { viewportHeightPx = it.height }
    ) {
        if (state.maxValue > 0 && viewportHeightPx > 0) {
            val viewport = viewportHeightPx.toFloat()
            val content = viewport + state.maxValue.toFloat()
            val visibleFraction = (viewport / content).coerceIn(0f, 1f)

            ScrollStateCapsuleThumb(
                state = state,
                visibleFraction = visibleFraction,
                backgroundColor = backgroundColor,
                preferredColor = preferredColor,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ScrollStateCapsuleThumb(
    state: ScrollState,
    visibleFraction: Float,
    backgroundColor: Color,
    preferredColor: Color,
    modifier: Modifier = Modifier
) {
    CapsuleFrame(visibleFraction, state.isScrollInProgress, backgroundColor, preferredColor, modifier) { thumbHeight, availableTravel,
            thumbColor ->
        val availableTravelPx = with(androidx.compose.ui.platform.LocalDensity.current) { availableTravel.toPx() }
        Box(modifier = Modifier.graphicsLayer {
                    val max = state.maxValue
                    val progress = if (max > 0) (state.value.toFloat() / max.toFloat()).coerceIn(0f, 1f) else 0f
                    translationY = availableTravelPx * progress
                }.width(4.dp).height(thumbHeight).clip(CircleShape).background(thumbColor))
    }
}

/**
 * Variante para listas LazyColumn/LazyRow verticales. La longitud de la
 * cápsula representa de forma aproximada cuántos elementos están visibles y
 * su posición se calcula a partir del primer elemento visible y su offset.
 */
@Composable
fun ScrollPositionCapsule(
    state: LazyListState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    preferredColor: Color = MaterialTheme.colorScheme.onBackground
) {
    val layoutInfo = state.layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo
    val totalItems = layoutInfo.totalItemsCount

    if (totalItems <= 0 || visibleItems.isEmpty() || (!state.canScrollBackward && !state.canScrollForward)) return

    val first = visibleItems.minByOrNull { it.index } ?: return
    val itemSize = first.size.coerceAtLeast(1)
    val hiddenPart = (-first.offset).coerceAtLeast(0).coerceAtMost(itemSize)
    val fractionalOffset = hiddenPart.toFloat() / itemSize.toFloat()
    val scrollableItems = (totalItems - visibleItems.size).coerceAtLeast(1)
    val estimatedProgress = ((first.index + fractionalOffset) / scrollableItems.toFloat()).coerceIn(0f, 1f)
    LazyCapsuleThumb(estimatedProgress, visibleItems.size, totalItems, state.canScrollBackward, state.canScrollForward,
        state.isScrollInProgress, backgroundColor, preferredColor, modifier)
}

/**
 * Variante para LazyVerticalStaggeredGrid. Como las tarjetas pueden tener
 * alturas diferentes y ocupar varias columnas, no existe una distancia lineal
 * perfecta equivalente a ScrollState. Se usa el menor índice visible como
 * referencia estable y el número de elementos visibles para dimensionar la
 * cápsula. Esto evita cálculos costosos durante flings rápidos.
 */
@Composable
fun ScrollPositionCapsule(
    state: LazyStaggeredGridState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    preferredColor: Color = MaterialTheme.colorScheme.onBackground,
    fixedThumbHeight: Dp? = null,
    smoothMovement: Boolean = false
) {
    val layoutInfo = state.layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo
    val totalItems = layoutInfo.totalItemsCount

    if (totalItems <= 0 || visibleItems.isEmpty() || (!state.canScrollBackward && !state.canScrollForward)) return

    val viewportStart = layoutInfo.viewportStartOffset
    val anchorItem = visibleItems.minByOrNull { abs(it.offset.y - viewportStart) } ?: return
    val itemHeight = anchorItem.size.height.coerceAtLeast(1)
    val hiddenPart = (viewportStart - anchorItem.offset.y).coerceAtLeast(0).coerceAtMost(itemHeight)
    val fractionalOffset = hiddenPart.toFloat() / itemHeight.toFloat()

    val stableRange = (totalItems - 1).coerceAtLeast(1)
    val estimatedProgress = ((anchorItem.index + fractionalOffset) / stableRange.toFloat()).coerceIn(0f, 1f)
    LazyCapsuleThumb(estimatedProgress, visibleItems.size, totalItems, state.canScrollBackward, state.canScrollForward,
        state.isScrollInProgress, backgroundColor, preferredColor, modifier, fixedThumbHeight, smoothMovement)
}

@Composable
private fun LazyCapsuleThumb(
    estimatedProgress: Float, visibleCount: Int, totalCount: Int, canScrollBackward: Boolean, canScrollForward: Boolean,
    active: Boolean, backgroundColor: Color, preferredColor: Color, modifier: Modifier, fixedThumbHeight: Dp? = null,
    smoothMovement: Boolean = false
) {
    val progress = when {
        !canScrollBackward -> 0f
        !canScrollForward -> 1f
        else -> estimatedProgress
    }
    CapsuleThumb(progress, (visibleCount.toFloat() / totalCount).coerceIn(0.08f, 0.65f), active, backgroundColor, preferredColor,
        modifier, fixedThumbHeight, smoothMovement)
}

/**
 * Devuelve un color OPACO para la cápsula con contraste garantizado.
 *
 * Activa: >= 4.5:1 para que sea evidente durante el gesto.
 * Inactiva: >= 3.2:1 para permanecer visible sin dominar la interfaz.
 *
 * [softenUiColorToContrast] respeta el color preferido cuando es viable y,
 * si éste no contrasta con el fondo seleccionado, elige automáticamente la
 * alternativa negra/blanca más legible.
 */
internal fun adaptiveScrollCapsuleColor(
    backgroundColor: Color,
    preferredColor: Color,
    active: Boolean
): Color = softenUiColorToContrast(
    foreground = preferredColor.copy(alpha = 1f),
    background = backgroundColor.copy(alpha = 1f),
    minimumContrast = if (active) 4.5f else 3.2f,
    maximumSoftening = if (active) 0.12f else 0.38f
)

@Composable
private fun CapsuleFrame(
    visibleFraction: Float,
    active: Boolean,
    backgroundColor: Color,
    preferredColor: Color,
    modifier: Modifier,
    fixedThumbHeight: Dp? = null,
    content: @Composable (thumbHeight: Dp, availableTravel: Dp, thumbColor: Color) -> Unit
) {
    val thumbColor = remember(backgroundColor, preferredColor, active) {
        adaptiveScrollCapsuleColor(backgroundColor, preferredColor, active)
    }
    BoxWithConstraints(
        modifier = modifier.fillMaxHeight().width(10.dp).padding(top = 10.dp, bottom = 10.dp, end = 2.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val calculatedHeight = maxHeight * visibleFraction.coerceIn(0.06f, 0.65f)
        val thumbHeight = (fixedThumbHeight ?: calculatedHeight.coerceAtLeast(36.dp)).coerceAtMost(maxHeight)
        content(thumbHeight, (maxHeight - thumbHeight).coerceAtLeast(0.dp), thumbColor)
    }
}

@Composable
private fun CapsuleThumb(
    progress: Float,
    visibleFraction: Float,
    active: Boolean,
    backgroundColor: Color,
    preferredColor: Color,
    modifier: Modifier = Modifier,
    fixedThumbHeight: Dp? = null,
    smoothMovement: Boolean = false
) {
    // El contraste no depende de la posición: reutilizarlo durante el gesto.
    CapsuleFrame(visibleFraction, active, backgroundColor, preferredColor, modifier, fixedThumbHeight) { thumbHeight, availableTravel,
            thumbColor ->
        val displayedProgress by animateFloatAsState(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = if (smoothMovement) 110 else 0),
            label = "scrollCapsuleProgress"
        )
        Box(
            modifier = Modifier
                // Leer la animación en la capa evita recomponer por cada frame.
                .graphicsLayer { translationY = (availableTravel * displayedProgress).toPx() }
                .width(4.dp)
                .height(thumbHeight)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}
