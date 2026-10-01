package com.example.mynotes.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

/** Conserva los componentes Material y sus valores predeterminados de contenido. */
@Composable
internal fun AppTextButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,
    color: Color = Color.Unspecified, fontFamily: FontFamily? = null,
    fontWeight: FontWeight? = null, fontSize: TextUnit = TextUnit.Unspecified
) {
    TextButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text = text, color = color, fontFamily = fontFamily, fontWeight = fontWeight, fontSize = fontSize)
    }
}

@Composable
internal fun AppIconButton(
    imageVector: ImageVector, contentDescription: String?, onClick: () -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true, tonal: Boolean = false,
    iconModifier: Modifier = Modifier, tint: Color? = null
) {
    // Resolver el tinte dentro del botón conserva el color de su estado deshabilitado.
    val content: @Composable () -> Unit = {
        Icon(imageVector = imageVector, contentDescription = contentDescription,
            modifier = iconModifier, tint = tint ?: LocalContentColor.current)
    }
    if (tonal) FilledTonalIconButton(onClick = onClick, modifier = modifier, enabled = enabled, content = content)
    else IconButton(onClick = onClick, modifier = modifier, enabled = enabled, content = content)
}

/** Emite los mismos elementos, sin añadir un Row ni otro contenedor. */
@Composable
internal fun AppIconLabel(
    imageVector: ImageVector, text: String, contentDescription: String? = null,
    iconModifier: Modifier = Modifier, tint: Color? = null, gap: Dp? = null,
    textModifier: Modifier = Modifier, color: Color = Color.Unspecified,
    fontFamily: FontFamily? = null, fontWeight: FontWeight? = null,
    fontSize: TextUnit = TextUnit.Unspecified, maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Icon(imageVector = imageVector, contentDescription = contentDescription,
        modifier = iconModifier, tint = tint ?: LocalContentColor.current)
    if (gap != null) Spacer(Modifier.width(gap))
    Text(text = text, modifier = textModifier, color = color, fontFamily = fontFamily,
        fontWeight = fontWeight, fontSize = fontSize, maxLines = maxLines, overflow = overflow)
}
