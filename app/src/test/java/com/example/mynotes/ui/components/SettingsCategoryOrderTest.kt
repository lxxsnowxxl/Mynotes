package com.example.mynotes.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Los cambios de agrupación no deben romper los accesos rápidos a secciones. */
class SettingsCategoryOrderTest {
    @Test fun sectionAnchorsMatchAdvancedLazyColumnOrder() {
        assertEquals(advancedSettingsItemKeys.size, advancedSettingsItemKeys.distinct().size)
        val indices = AdvancedSettingsCategory.ordered.map { category ->
            assertEquals(category.anchorKey, advancedSettingsItemKeys[category.firstItemIndex])
            category.firstItemIndex
        }
        assertEquals(indices.sorted(), indices)
        assertEquals(8, indices.size)
    }

    @Test fun currentCategoryTracksScrollIndex() {
        val categories = AdvancedSettingsCategory.ordered
        assertEquals(categories.first(), AdvancedSettingsCategory.forItem(0))
        categories.forEachIndexed { index, category ->
            val sectionIndex = category.firstItemIndex
            assertEquals(category, AdvancedSettingsCategory.forItem(sectionIndex))
            val next = categories.getOrNull(index + 1)?.firstItemIndex
            if (next != null && next > sectionIndex + 1) {
                assertEquals(category, AdvancedSettingsCategory.forItem(next - 1))
            }
        }
        assertTrue(advancedSettingsItemKeys.contains("layout_backup_restore"))
    }

    @Test fun motionAndCardAnimationsAreOnlyAvailableInAdvancedMode() {
        val animationKey = "note_card_animation_styles"
        val advancedIndex = advancedSettingsItemKeys.indexOf(animationKey)
        assertEquals("extreme_motion", advancedSettingsItemKeys.getOrNull(advancedIndex - 1))
        assertEquals(AdvancedSettingsCategory.MOTION, AdvancedSettingsCategory.forItem(advancedIndex))
        assertFalse(basicSettingsItemKeys.contains("section_motion"))
        assertFalse(basicSettingsItemKeys.contains(animationKey))
        val basicFontIndex = basicSettingsItemKeys.indexOf("font")
        assertTrue(basicFontIndex >= 0)
        assertEquals("language", basicSettingsItemKeys.getOrNull(basicFontIndex + 1))
        for (keys in listOf(basicSettingsItemKeys, advancedSettingsItemKeys)) {
            assertFalse(keys.contains("library_insights_export"))
        }
    }

    @Test fun completeStylesFollowColorPaletteInBothModes() {
        for (keys in listOf(basicSettingsItemKeys, advancedSettingsItemKeys)) {
            val paletteIndex = keys.indexOf("palette_background")
            assertTrue(paletteIndex >= 0)
            assertEquals("design_styles", keys.getOrNull(paletteIndex + 1))
        }
        val stylesIndex = advancedSettingsItemKeys.indexOf("design_styles")
        assertEquals(AdvancedSettingsCategory.APPEARANCE, AdvancedSettingsCategory.forItem(stylesIndex))
    }
}
