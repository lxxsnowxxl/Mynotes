package com.example.mynotes.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsSectionPreferencePolicyTest {
    @Test
    fun existingInstallationsStartExpanded() {
        assertEquals("", AppSettings().collapsedSettingsSections)
        assertEquals(setOf(SettingsSectionPreferencePolicy.NOTE_CARDS), SettingsSectionPreferencePolicy.keys)
        assertTrue(SettingsSectionPreferencePolicy.isExpanded("", SettingsSectionPreferencePolicy.NOTE_CARDS))
    }

    @Test
    fun onlyTheActiveSectionCanBeCollapsed() {
        val key = SettingsSectionPreferencePolicy.NOTE_CARDS
        assertEquals(key, SettingsSectionPreferencePolicy.withExpanded("", key, false))
        assertFalse(SettingsSectionPreferencePolicy.isExpanded(key, key))
        assertEquals("", SettingsSectionPreferencePolicy.withExpanded(key, key, true))
    }

    @Test
    fun obsoleteImportedKeysAreIgnoredAndDataIsMigrated() {
        val saved = SettingsSectionPreferencePolicy.normalize(" color_submenu,unknown,note_cards,main_actions,priority_submenu, ")
        assertEquals("note_cards", saved)
        assertFalse(SettingsSectionPreferencePolicy.isExpanded(saved, SettingsSectionPreferencePolicy.NOTE_CARDS))
        assertEquals(saved, SettingsSectionPreferencePolicy.withExpanded(saved, "unknown", false))
        assertTrue(SettingsSectionPreferencePolicy.isExpanded(saved, "unknown"))
        assertEquals("", SettingsSectionPreferencePolicy.normalize("color_submenu,priority_submenu"))
    }
}
