package com.example.mynotes.studio

import com.example.mynotes.data.Note
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class StudioAlgorithmsTest {
    @Test fun nestedFoldersProduceRootToLeafBreadcrumbs() {
        assertEquals(listOf("a", "b", "c"), StudioAlgorithms.path("c", mapOf("c" to "b", "b" to "a")))
    }
    @Test fun corruptFolderCyclesTerminateAndCannotBeExtended() {
        val parents = mapOf("a" to "b", "b" to "a")
        assertEquals(2, StudioAlgorithms.path("a", parents).size)
        assertFalse(StudioAlgorithms.canParent("a", "b", parents))
    }
    @Test fun movingFolderUnderItsDescendantIsRejected() {
        assertFalse(StudioAlgorithms.canParent("root", "leaf", mapOf("leaf" to "root")))
        assertTrue(StudioAlgorithms.canParent("leaf", null, emptyMap()))
    }
    @Test fun unicodeWordCountKeepsContractionsAndHyphenatedWords() {
        assertEquals(5, StudioAlgorithms.words("¡Hola, José! don't co-operate 2026 😀"))
    }
    @Test fun outlineOffsetsReferToOriginalDocument() {
        val text = "Intro\n# One\nbody\n### Three\n"
        assertEquals(listOf(Triple(1,"One",6),Triple(3,"Three",17)), StudioAlgorithms.headings(text))
    }
    @Test fun failedReviewResetsScheduleAndKeepsMinimumEase() {
        assertEquals(Triple(0,1,1.3), StudioAlgorithms.review(0,8,60,1.3))
    }
    @Test fun successfulReviewUsesOneDayThenSixDays() {
        assertEquals(1, StudioAlgorithms.review(4,0,0,2.5).second)
        assertEquals(6, StudioAlgorithms.review(4,1,1,2.5).second)
    }
    @Test fun reviewIntervalAndEaseAreBounded() {
        val result=StudioAlgorithms.review(5,100,100000,10.0)
        assertEquals(3650,result.second); assertEquals(3.0,result.third,0.0)
    }
    @Test fun rotationIsReversibleAroundNonzeroPivot() {
        val p=StudioPoint(-12f,39f);val pivot=StudioPoint(15f,9f)
        val back=StudioAlgorithms.rotate(StudioAlgorithms.rotate(p,37f,pivot),-37f,pivot)
        assertEquals(p.x,back.x,.0001f);assertEquals(p.y,back.y,.0001f)
    }
    @Test fun polygonFillRejectsOutsideAndEmptyShapes() {
        val square=listOf(StudioPoint(0f,0f),StudioPoint(10f,0f),StudioPoint(10f,10f),StudioPoint(0f,10f))
        assertTrue(StudioAlgorithms.inside(StudioPoint(5f,5f),square))
        assertFalse(StudioAlgorithms.inside(StudioPoint(20f,5f),square))
        assertFalse(StudioAlgorithms.inside(StudioPoint(0f,0f),emptyList()))
    }
    @Test fun smartCollectionCombinesWordsFolderDatesPropertyAndMedia() {
        val note=Note(title="Project launch",content="Review the agenda",folderId="work",createdAt=100)
        val meta=JSONObject().put("stage","progress").put("properties",JSONObject().put("Owner","José Luis"))
        val rule=JSONObject().put("query","launch agenda").put("folder","work").put("stage","progress").put("after",90).put("before",110).put("property","Owner").put("value","josé").put("media",true)
        assertTrue(StudioRules.matches(note,meta,rule,true))
        assertFalse(StudioRules.matches(note,meta,rule,false))
        assertFalse(StudioRules.matches(note.copy(createdAt=111),meta,rule,true))
        assertFalse(StudioRules.matches(note.copy(folderId="personal"),meta,rule,true))
    }
    @Test fun smartCollectionRequiresEverySearchWord() {
        val rule=JSONObject().put("query","alpha gamma")
        assertFalse(StudioRules.matches(Note(title="alpha",content="beta"),JSONObject(),rule,false))
    }
    @Test fun shortcutNormalizationIsStable() {
        assertEquals("Ctrl+Alt+Shift+F2",StudioKeys.normalize("shift + alt + Ctrl + f2"))
    }
    @Test fun unsafeOrAmbiguousShortcutsAreRejected() {
        listOf("A","Shift+A","Ctrl+F13","Super+A","Ctrl+Enter","Ctrl++").forEach { assertNull(it,StudioKeys.normalize(it)) }
    }
    @Test fun shortcutModifiersMustMatchExactly() {
        assertTrue(StudioKeys.matches("Ctrl+S",47,true,false,false,false))
        assertFalse(StudioKeys.matches("Ctrl+S",47,true,false,false,true))
        assertFalse(StudioKeys.matches("Ctrl+S",47,false,true,false,false))
    }
}
