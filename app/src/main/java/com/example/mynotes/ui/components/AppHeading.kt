package com.example.mynotes.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/** Emite ambos textos en el contenedor existente, sin añadir espacio ni cambiar su estilo. */
@Composable
internal fun AppHeading(
    title: String, subtitle: String, fontFamily: FontFamily,
    titleColor: Color, subtitleColor: Color,
    titleSize: TextUnit = 20.sp, subtitleSize: TextUnit = 13.sp,
    titleModifier: Modifier = Modifier, subtitleModifier: Modifier = Modifier
) {
    Text(title, modifier = titleModifier, color = titleColor, fontFamily = fontFamily,
        fontWeight = FontWeight.Bold, fontSize = titleSize)
    Text(subtitle, modifier = subtitleModifier, color = subtitleColor,
        fontFamily = fontFamily, fontSize = subtitleSize)
}
