package com.example.mynotes.ui.theme

/**
 * Identificador estable para fondos de notas. Se guarda en Note.color sin cambiar
 * el esquema Room ni los archivos de respaldo: palette:<familia>:<tono 0..4>.
 * Las claves antiguas (yellow, pink, etc.) se siguen interpretando por separado.
 */
object NoteColorSelection {
    private const val PREFIX = "palette:"

    data class Selection(val paletteKey: String, val toneIndex: Int)

    fun encode(paletteKey: String, toneIndex: Int): String {
        val palette = PaletteCatalog.find(paletteKey).takeIf { it.key == paletteKey }
            ?: error("Unknown note palette: $paletteKey")
        require(toneIndex in palette.tones.indices) { "Invalid tone index: $toneIndex" }
        return "$PREFIX$paletteKey:$toneIndex"
    }

    fun decode(value: String): Selection? {
        if (!value.startsWith(PREFIX)) return null
        val components = value.removePrefix(PREFIX).split(':')
        if (components.size != 2) return null
        val palette = PaletteCatalog.find(components[0]).takeIf { it.key == components[0] } ?: return null
        val index = components[1].toIntOrNull() ?: return null
        if (index !in palette.tones.indices) return null
        return Selection(palette.key, index)
    }
}
