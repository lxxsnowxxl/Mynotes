package com.example.mynotes.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import com.example.mynotes.settings.MenuPreferencePolicy

class NoteColorSelectionTest {
    @Test
    fun familyVisibilityKeepsLegacyPreferencesAndUsesEverySettingsFamily() {
        val options = MenuPreferencePolicy.paletteColorOptions
        assertEquals(47, options.size)
        assertEquals(listOf("default") + PaletteCatalog.settingsPaletteKeys.map { "palette:$it" }, options.map { it.key })
        assertEquals("palette:rose,red", MenuPreferencePolicy.normalizeHiddenItems(
            "palette:rose,palette:rose,red,unknown,palette:rose:2", MenuPreferencePolicy.colorKeys))
    }

    @Test
    fun allSettingsPalettesCanBeSavedWithFiveExactTones() {
        PaletteCatalog.settingsPalettes.forEach { palette ->
            palette.tones.indices.forEach { index ->
                val saved = NoteColorSelection.encode(palette.key, index)
                val restored = NoteColorSelection.decode(saved)
                assertEquals(palette.key, restored?.paletteKey)
                assertEquals(index, restored?.toneIndex)
                assertFalse(noteUsesGlobalBackground(saved))
            }
        }
    }

    @Test
    fun defaultAndLegacyColorsRemainValid() {
        assertNull(NoteColorSelection.decode("default"))
        assertNull(NoteColorSelection.decode("yellow"))
        assertTrue(noteUsesGlobalBackground("default"))
        assertFalse(noteUsesGlobalBackground("yellow"))
        assertFalse(noteUsesGlobalBackground("pink"))
    }

    @Test
    fun invalidOrUnknownColorsFollowTheGlobalBackground() {
        listOf("palette:missing:0", "palette:neutral:-1", "palette:neutral:5",
            "palette:neutral:nan", "palette:neutral:0:more", "something-else")
            .forEach { value ->
                assertNull(NoteColorSelection.decode(value))
                assertTrue(noteUsesGlobalBackground(value))
            }
    }
}
