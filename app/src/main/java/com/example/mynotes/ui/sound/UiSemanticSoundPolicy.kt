package com.example.mynotes.ui.sound

import java.util.Locale

/**
 * Reserva el sonido específico para la función del control, no para la pantalla.
 * Los iconos heredan la descripción accesible, los botones de texto su etiqueta.
 * Si la intención no es evidente se usa un clic discreto de selección.
 */
internal object UiSemanticSoundPolicy {
    fun forTextChange(previousText: String, newText: String): UiActionSound =
        if (newText.length < previousText.length) UiActionSound.TextErase else UiActionSound.TextInput

    fun forLabel(label: String?): UiActionSound {
        val value = label?.trim()?.lowercase(Locale.ROOT).orEmpty()
        if (value.isEmpty()) return UiActionSound.Select
        return when {
            matches(value, "undo", "deshacer") -> UiActionSound.Undo
            matches(value, "redo", "rehacer") -> UiActionSound.Redo
            matches(value, "backspace", "erase text", "clear text", "borrar texto", "borrar caracteres", "limpiar texto") -> UiActionSound.TextErase
            matches(value, "delete", "remove", "trash", "discard", "eliminar", "borrar", "quitar", "papelera") -> UiActionSound.Delete
            matches(value, "save", "guardar", "salvar", "apply", "aplicar") -> UiActionSound.Save
            matches(value, "done", "hecho", "listo", "accept", "aceptar", "confirm", "confirmar") -> UiActionSound.Confirm
            matches(value, "cancel", "cancelar", "dismiss", "descartar") -> UiActionSound.Cancel
            matches(value, "back", "volver", "atrás", "atras", "close", "cerrar") -> UiActionSound.Back
            matches(value, "share", "compartir") -> UiActionSound.Share
            matches(value, "search", "buscar") -> UiActionSound.Search
            matches(value, "palette", "paleta", "color", "colour", "tono", "tonalidad") -> UiActionSound.Color
            matches(value, "attach", "adjuntar", "attachment", "adjunto", "add image", "add file", "insertar archivo", "insertar imagen") -> UiActionSound.Add
            matches(value, "favorite", "favourite", "favorito", "favorita") -> UiActionSound.Favorite
            matches(value, "pin", "fijar", "anclar") -> UiActionSound.Pin
            matches(value, "link", "enlace", "vincular") -> UiActionSound.Link
            matches(value, "play", "pause", "reproducir", "pausar") -> UiActionSound.PlayPause
            matches(value, "settings", "configuración", "configuracion", "ajustes") -> UiActionSound.Settings
            matches(value, "sort", "ordenar", "clasificar") -> UiActionSound.Sort
            matches(value, "new", "create", "crear", "nuevo", "nueva", "añadir", "agregar") -> UiActionSound.Add
            else -> UiActionSound.Select
        }
    }

    private fun matches(label: String, vararg tokens: String): Boolean = tokens.any { token ->
        // Coincidencias de palabras completas o frases, no fragmentos de palabras.
        var start = label.indexOf(token)
        var found = false
        while (start >= 0 && !found) {
            val before = start == 0 || !label[start - 1].isLetterOrDigit()
            val end = start + token.length
            val after = end >= label.length || !label[end].isLetterOrDigit()
            found = before && after
            if (!found) start = label.indexOf(token, start + 1)
        }
        found
    }
}
