package com.example.mynotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.example.mynotes.ui.theme.ensureUiContrast

@Composable
fun AppDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh, properties: PopupProperties = PopupProperties(
        focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true), content: @Composable ColumnScope.() -> Unit) {
    val borderColor = ensureUiContrast(preferred = MaterialTheme.colorScheme.outline.copy(alpha = 0.70f), background = containerColor,
        minimumContrast = 2.6f)
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest, modifier = modifier, shape = RoundedCornerShape(18.dp),
        containerColor = containerColor, tonalElevation = 0.dp, shadowElevation = 12.dp, border = BorderStroke(1.dp, borderColor),
        properties = properties, content = content)
}

@Composable
fun AppAlertDialog(onDismissRequest: () -> Unit, modifier: Modifier = Modifier, properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true, dismissOnClickOutside = true), title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null, confirmButton: @Composable () -> Unit, dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null) {
    val containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    AlertDialog(onDismissRequest = onDismissRequest, modifier = modifier, properties = properties, shape = RoundedCornerShape(24.dp),
        containerColor = containerColor, tonalElevation = 0.dp, icon = icon, title = title, text = text, confirmButton = confirmButton,
        dismissButton = dismissButton, iconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface, textContentColor = MaterialTheme.colorScheme.onSurfaceVariant)
}
