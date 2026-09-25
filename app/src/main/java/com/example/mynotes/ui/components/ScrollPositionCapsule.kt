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

            /*
             * Para ScrollState no animamos un valor que cambia prácticamente
             * en cada frame del gesto. La cápsula sigue exactamente al scroll
             * y evitamos crear una animación tween de duración 0 repetidamente.
             */
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
    val active = state.isScrollInProgress
    val thumbColor = remember(backgroundColor, preferredColor, active) {
        adaptiveScrollCapsuleColor(
            backgroundColor = backgroundColor,
            preferredColor = preferredColor,
            active = active
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .width(10.dp)
            .padding(top = 10.dp, bottom = 10.dp, end = 2.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val thumbHeight = (maxHeight * visibleFraction.coerceIn(0.06f, 0.65f))
            .coerceAtLeast(36.dp)
            .coerceAtMost(maxHeight)
        val availableTravelPx = with(androidx.compose.ui.platform.LocalDensity.current) {
            (maxHeight - thumbHeight).coerceAtLeast(0.dp).toPx()
        }

        Box(
            modifier = Modifier
                .graphicsLayer {
                    val max = state.maxValue
                    val progress = if (max > 0) {
                        (state.value.toFloat() / max.toFloat()).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                    translationY = availableTravelPx * progress
                }
                .width(4.dp)
                .height(thumbHeight)
                .clip(CircleShape)
                .background(thumbColor)
        )
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
    val progress = when {
        !state.canScrollBackward -> 0f
        !state.canScrollForward -> 1f
        else -> estimatedProgress
    }
    val visibleFraction = (visibleItems.size.toFloat() / totalItems.toFloat()).coerceIn(0.08f, 0.65f)

    CapsuleThumb(
        progress = progress,
        visibleFraction = visibleFraction,
        active = state.isScrollInProgress,
        backgroundColor = backgroundColor,
        preferredColor = preferredColor,
        modifier = modifier
    )
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

    /*
     * En un StaggeredGrid el elemento con el índice menor no siempre es el
     * que está visualmente pegado al borde superior: una tarjeta alta puede
     * permanecer parcialmente visible durante mucho tiempo en una columna.
     * Si se usa ese índice como ancla, al desaparecer esa tarjeta el indicador
     * puede saltar varios índices de golpe.
     *
     * Tomamos en cambio el elemento cuyo borde superior está más cerca del
     * inicio real del viewport. Esto hace que el ancla represente mejor lo que
     * el usuario está viendo en ese instante.
     */
    val viewportStart = layoutInfo.viewportStartOffset
    val anchorItem = visibleItems.minByOrNull { abs(it.offset.y - viewportStart) } ?: return
    val itemHeight = anchorItem.size.height.coerceAtLeast(1)
    val hiddenPart = (viewportStart - anchorItem.offset.y).coerceAtLeast(0).coerceAtMost(itemHeight)
    val fractionalOffset = hiddenPart.toFloat() / itemHeight.toFloat()

    /*
     * El denominador NO depende ya de visibleItems.size. Ese número cambia
     * constantemente en un masonry/staggered grid cuando una tarjeta entra o
     * sale del viewport y era la causa principal de los saltos bruscos. El
     * total de notas sí es estable durante el gesto, por lo que proporciona
     * una escala consistente de principio a fin.
     */
    val stableRange = (totalItems - 1).coerceAtLeast(1)
    val estimatedProgress = ((anchorItem.index + fractionalOffset) / stableRange.toFloat()).coerceIn(0f, 1f)
    val progress = when {
        !state.canScrollBackward -> 0f
        !state.canScrollForward -> 1f
        else -> estimatedProgress
    }
    val visibleFraction = (visibleItems.size.toFloat() / totalItems.toFloat()).coerceIn(0.08f, 0.65f)

    CapsuleThumb(
        progress = progress,
        visibleFraction = visibleFraction,
        active = state.isScrollInProgress,
        backgroundColor = backgroundColor,
        preferredColor = preferredColor,
        modifier = modifier,
        fixedThumbHeight = fixedThumbHeight,
        smoothMovement = smoothMovement
    )
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
    /*
     * El indicador nunca depende de un alpha fijo. Un color semitransparente
     * que se ve bien sobre una paleta puede desaparecer casi por completo
     * sobre otra. En su lugar se parte del color visual preferido (normalmente
     * el mismo de texto/gráficos configurado por el usuario) y se atenúa sólo
     * hasta el punto en que todavía conserva contraste medible contra el fondo
     * real de la pantalla. Si el color preferido no alcanza ese contraste, la
     * función de contraste cae automáticamente en negro o blanco.
     */
    // El contraste no depende de la posición: reutilizarlo durante el gesto.
    val thumbColor = remember(backgroundColor, preferredColor, active) {
        adaptiveScrollCapsuleColor(
            backgroundColor = backgroundColor,
            preferredColor = preferredColor,
            active = active
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .width(10.dp)
            .padding(top = 10.dp, bottom = 10.dp, end = 2.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val calculatedHeight = maxHeight * visibleFraction.coerceIn(0.06f, 0.65f)
        /*
         * Algunas superficies lazy (especialmente un StaggeredGrid) van
         * descubriendo/remeasureando elementos mientras el usuario avanza.
         * Cuando [fixedThumbHeight] está definido, la longitud visual del
         * indicador deja de depender del número momentáneo de elementos
         * visibles. Solo cambia su posición, no su tamaño.
         */
        val thumbHeight = (fixedThumbHeight ?: calculatedHeight.coerceAtLeast(36.dp))
            .coerceAtMost(maxHeight)
        val availableTravel = (maxHeight - thumbHeight).coerceAtLeast(0.dp)

        /*
         * El estado lazy puede actualizar su elemento ancla de forma discreta.
         * En la pantalla principal suavizamos únicamente la representación
         * gráfica durante 110 ms: el contenido y el estado de scroll no se
         * animan ni se modifican. Esto elimina el salto visual al cambiar de
         * tarjeta ancla sin introducir una sensación de retraso apreciable.
         */
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
