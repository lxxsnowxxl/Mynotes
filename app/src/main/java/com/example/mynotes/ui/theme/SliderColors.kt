package com.example.mynotes.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colores independientes para las barras deslizantes de Configuración.
 * "accent" conserva el color de acento actual de la app.
 */
@Immutable
data class SliderColorOption(
    val key: String,
    val color: Color?
)

val SettingsSliderColorOptions =
    listOf(
        SliderColorOption("accent", null),
        SliderColorOption("graphite", Color(0xFF59636A)),
        SliderColorOption("slate", Color(0xFF687684)),
        SliderColorOption("red", Color(0xFFE55757)),
        SliderColorOption("coral", Color(0xFFF27663)),
        SliderColorOption("deep_orange", Color(0xFFEA6A3A)),
        SliderColorOption("orange", Color(0xFFEF8A39)),
        SliderColorOption("amber", Color(0xFFE8B632)),
        SliderColorOption("gold", Color(0xFFD8A62A)),
        SliderColorOption("yellow", Color(0xFFE9C83B)),
        SliderColorOption("lime", Color(0xFF9BCB4B)),
        SliderColorOption("light_green", Color(0xFF7FBE55)),
        SliderColorOption("green", Color(0xFF63A85C)),
        SliderColorOption("emerald", Color(0xFF3FA36F)),
        SliderColorOption("mint", Color(0xFF58B98E)),
        SliderColorOption("teal", Color(0xFF3FA4A0)),
        SliderColorOption("cyan", Color(0xFF45B9C8)),
        SliderColorOption("sky", Color(0xFF4FA9E2)),
        SliderColorOption("light_blue", Color(0xFF5D9FE0)),
        SliderColorOption("blue", Color(0xFF4B8EDB)),
        SliderColorOption("indigo", Color(0xFF6275CF)),
        SliderColorOption("violet", Color(0xFF8B6BC5)),
        SliderColorOption("deep_purple", Color(0xFF7558B8)),
        SliderColorOption("purple", Color(0xFFA05BC1)),
        SliderColorOption("magenta", Color(0xFFC15BA8)),
        SliderColorOption("pink", Color(0xFFD96787)),
        SliderColorOption("rose", Color(0xFFE16F9A)),
        SliderColorOption("brown", Color(0xFF9A7157)),
        SliderColorOption("copper", Color(0xFFB27652)),
        SliderColorOption("steel", Color(0xFF607D8B))
    )

fun resolveSettingsSliderColor(
    key: String,
    accentFallback: Color
): Color =
    SettingsSliderColorOptions
        .firstOrNull { it.key == key }
        ?.color
        ?: accentFallback
