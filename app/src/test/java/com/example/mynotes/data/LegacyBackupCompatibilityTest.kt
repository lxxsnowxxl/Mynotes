package com.example.mynotes.data

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

/** Fixtures use fields written by the original v261 codec, without the newer metadata. */
class LegacyBackupCompatibilityTest {
    @Test fun originalNotesKeepTheirContentAndFlags() {
        val legacy=JSONObject().put("id",17).put("title","Original").put("content","Original content")
            .put("createdAt",123456L).put("color","blue").put("priority",3).put("category","work")
            .put("isFavorite",true).put("isPinned",true)
        val note=jsonToNotes(JSONArray().put(legacy)).single()
        assertEquals(17,note.id);assertEquals("Original content",note.content)
        assertEquals(123456L,note.createdAt);assertEquals("blue",note.color)
        assertEquals(3,note.priority);assertEquals("work",note.category)
        assertTrue(note.isFavorite);assertTrue(note.isPinned)
        assertEquals("default",note.libraryId);assertNull(note.folderId);assertFalse(note.isDeleted)
    }
    @Test fun originalSettingsKeepIndependentVolumesAndMigrateTheSavedMotionStyle() {
        val legacy=JSONObject().put("configurationMode","advanced").put("backgroundColor","lavender")
            .put("backgroundToneIndex",3).put("settingsBackgroundStyle","animated_blur")
            .put("soundEffectsVolume",35).put("reminderSoundVolume",80).put("hapticEffectsIntensity",55)
            .put("animationStyle","zoom").put("darkMode",false).put("language","en")
        val settings=jsonToSettings(legacy)
        assertEquals(35f,settings.soundEffectsVolume,0f);assertEquals(80f,settings.reminderSoundVolume,0f)
        assertEquals(55f,settings.hapticEffectsIntensity,0f);assertEquals("animated_blur",settings.settingsBackgroundStyle)
        assertEquals("lavender",settings.backgroundColor);assertEquals(3,settings.backgroundToneIndex)
        assertEquals("lift_card",settings.animationStyle);assertFalse(settings.darkMode);assertEquals("en",settings.language)
    }
    @Test fun originalAttachmentRecordsKeepAllFiveMediaTypesAndArchivePaths() {
        val types=listOf("image","video","audio","voice","file")
        val legacy=JSONArray()
        types.forEachIndexed{i,type->legacy.put(JSONObject().put("id",i+1).put("noteId",17).put("type",type)
            .put("name","Original $i").put("createdAt",123456L).put("fileEntry","attachments/original_$i"))}
        val decoded=jsonToAttachmentRecords(legacy)
        assertEquals(types,decoded.map{it.type});assertTrue(decoded.all{it.noteId==17})
        assertEquals((0..4).map{"attachments/original_$it"},decoded.map{it.fileEntry})
    }
}
