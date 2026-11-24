package com.example.mynotes.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mynotes.settings.MenuPreferencePolicy
import com.example.mynotes.ui.theme.NoteColorSelection
import com.example.mynotes.ui.theme.PaletteCatalog
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotePaletteBackupTest {
    @Test
    fun jsonRoundTripKeepsEveryPaletteToneAndEveryLegacyColor() {
        val colors = PaletteCatalog.settingsPalettes.flatMap { palette ->
            palette.tones.indices.map { NoteColorSelection.encode(palette.key, it) }
        } + MenuPreferencePolicy.colorOptions.map { it.key }
        val notes = colors.mapIndexed { index, color -> Note(id = index + 1, title = "Color $index",
            content = "Contenido", createdAt = 123456789L, color = color) }
        val array = JSONArray().apply { notes.forEach { put(noteToJson(it)) } }
        assertEquals(notes, jsonToNotes(JSONArray(array.toString())))
    }
}
