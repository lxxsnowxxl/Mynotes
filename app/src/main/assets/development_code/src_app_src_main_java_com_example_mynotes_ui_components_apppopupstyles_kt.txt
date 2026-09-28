package com.example.mynotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.example.mynotes.ui.theme.ensureUiContrast

/**
 * Menú desplegable común de MyNotes.
 *
 * Además del aspecto visual compartido, este wrapper posee su propio [ScrollState]
 * y lo entrega al DropdownMenu de Material 3. Gracias a ello el indicador en forma
 * de cápsula observa exactamente el mismo desplazamiento que mueve las opciones.
 *
 * El indicador se dibuja encima del popup mediante [Modifier.drawWithContent], por
 * lo que no añade ancho, no desplaza el texto, no intercepta gestos y no modifica
 * la lógica de selección. Sólo aparece cuando [ScrollState.maxValue] es mayor que
 * cero, es decir, cuando realmente existen opciones fuera del área visible.
 */
@Composable
fun AppDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    properties: PopupProperties = PopupProperties(
        focusable = false,
        dismissOnBackPress = true,
        dismissOnClickOutside = true
    ),
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    val borderColor = ensureUiContrast(
        preferred = MaterialTheme.colorScheme.outline.copy(alpha = 0.70f),
        background = containerColor,
        minimumContrast = 2.6f
    )
    val capsuleColor = adaptiveScrollCapsuleColor(
        backgroundColor = containerColor,
        preferredColor = MaterialTheme.colorScheme.onSurface,
        active = scrollState.isScrollInProgress
    )

    /*
     * El dibujo se realiza sobre el viewport final del DropdownMenu. El valor
     * maxValue representa la parte del contenido que queda fuera de ese viewport,
     * por lo que viewport + maxValue aproxima la altura total desplazable.
     * Con esa relación se calcula tanto el tamaño de la cápsula como su recorrido.
     */
    val menuModifier = modifier.drawWithContent {
        drawContent()

        val maxScroll = scrollState.maxValue
        if (maxScroll <= 0 || size.height <= 0f || size.width <= 0f) return@drawWithContent

        val edgeInset = 2.dp.toPx()
        val verticalInset = 10.dp.toPx()
        val thumbWidth = 4.dp.toPx()
        val usableHeight = (size.height - verticalInset * 2f).coerceAtLeast(0f)
        if (usableHeight <= 0f) return@drawWithContent

        val totalScrollableHeight = size.height + maxScroll.toFloat()
        val visibleFraction = (size.height / totalScrollableHeight).coerceIn(0.06f, 0.65f)
        val minimumThumbHeight = 28.dp.toPx()
        val thumbHeight = (usableHeight * visibleFraction)
            .coerceAtLeast(minimumThumbHeight)
            .coerceAtMost(usableHeight)
        val progress = (scrollState.value.toFloat() / maxScroll.toFloat()).coerceIn(0f, 1f)
        val travel = (usableHeight - thumbHeight).coerceAtLeast(0f)
        val top = verticalInset + travel * progress
        val left = size.width - edgeInset - thumbWidth

        drawRoundRect(
            color = capsuleColor,
            topLeft = Offset(left, top),
            size = Size(thumbWidth, thumbHeight),
            cornerRadius = CornerRadius(thumbWidth / 2f, thumbWidth / 2f)
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = menuModifier,
        scrollState = scrollState,
        shape = RoundedCornerShape(18.dp),
        containerColor = containerColor,
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, borderColor),
        properties = properties,
        content = content
    )
}

@Composable
fun AppAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true
    ),
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null
) {
    val containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = properties,
        shape = RoundedCornerShape(24.dp),
        containerColor = containerColor,
        tonalElevation = 0.dp,
        icon = icon,
        title = title,
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        iconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
