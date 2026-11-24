package com.example.mynotes.ui.workspace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.components.AppIconButton

@Composable
internal fun WorkspaceToolSection(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    fallbackColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    fallbackTonalElevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val drawer = LocalWorkspaceDrawerColors.current
    Surface(
        modifier = modifier,
        shape = shape,
        color = drawer?.section ?: fallbackColor,
        contentColor = drawer?.controls?.onSurface ?: contentColorFor(fallbackColor),
        tonalElevation = if (drawer == null) fallbackTonalElevation else 0.dp,
        content = content
    )
}

@Composable
internal fun WorkspaceToolFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    val colors = LocalWorkspaceDrawerColors.current?.controls
    if (colors == null) {
        FilterChip(selected = selected, onClick = onClick, label = label)
        return
    }
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = label,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colors.surfaceContainerHigh,
            labelColor = colors.onSurface,
            selectedContainerColor = colors.primaryContainer,
            selectedLabelColor = colors.onPrimaryContainer
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = colors.outlineVariant,
            selectedBorderColor = colors.outline,
            selectedBorderWidth = 2.dp
        )
    )
}

@Composable
internal fun workspaceToolActionButtonColors(
    colors: ColorScheme? = LocalWorkspaceDrawerColors.current?.controls
): ButtonColors? = colors?.let {
    ButtonDefaults.outlinedButtonColors(containerColor = it.surfaceContainerHigh, contentColor = it.onSurface)
}

@Composable
internal fun WorkspaceToolActionButton(
    onClick: () -> Unit,
    outlined: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val colors = LocalWorkspaceDrawerColors.current?.controls
    if (colors != null) {
        OutlinedButton(
            onClick = onClick,
            colors = workspaceToolActionButtonColors(colors) ?: ButtonDefaults.outlinedButtonColors(),
            border = BorderStroke(1.dp, colors.outlineVariant),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            content = content
        )
    } else if (outlined) {
        OutlinedButton(onClick = onClick, content = content)
    } else {
        TextButton(onClick = onClick, content = content)
    }
}

@Composable
internal fun workspaceToolIconButtonColors(): IconButtonColors? {
    val drawer = LocalWorkspaceDrawerColors.current ?: return null
    return IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = drawer.controls.surfaceContainerHigh,
        contentColor = drawer.controls.onSurface,
        disabledContainerColor = drawer.controls.surfaceContainerLow,
        disabledContentColor = drawer.disabledButtonContent
    )
}

@Composable
internal fun WorkspaceToolIconButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val colors = workspaceToolIconButtonColors()
    AppIconButton(
        imageVector = imageVector,
        contentDescription = contentDescription,
        onClick = onClick,
        enabled = enabled,
        tonal = colors != null,
        colors = colors
    )
}

@Composable
internal fun workspaceToolSwitchColors(): SwitchColors {
    val colors = LocalWorkspaceDrawerColors.current?.controls ?: return SwitchDefaults.colors()
    return SwitchDefaults.colors(
        checkedThumbColor = colors.onPrimaryContainer,
        checkedTrackColor = colors.primaryContainer,
        checkedBorderColor = colors.outline,
        uncheckedThumbColor = colors.onSurfaceVariant,
        uncheckedTrackColor = colors.surfaceContainerHigh,
        uncheckedBorderColor = colors.outlineVariant
    )
}
