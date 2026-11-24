package com.example.mynotes.ui.sound

import org.junit.Assert.assertEquals
import org.junit.Test

class UiSemanticSoundPolicyTest {
    @Test fun savesAndDeletesAreDifferentSounds() {
        assertEquals(UiActionSound.Save, UiSemanticSoundPolicy.forLabel("Guardar nota"))
        assertEquals(UiActionSound.Delete, UiSemanticSoundPolicy.forLabel("Eliminar nota"))
        assertEquals(UiActionSound.Delete, UiSemanticSoundPolicy.forLabel("Delete layer"))
        assertEquals(UiActionSound.TextErase, UiSemanticSoundPolicy.forLabel("Borrar texto"))
    }

    @Test fun editingAndUndoHaveDistinctSemantics() {
        assertEquals(UiActionSound.TextInput, UiSemanticSoundPolicy.forTextChange("Hola", "Hola!"))
        assertEquals(UiActionSound.TextErase, UiSemanticSoundPolicy.forTextChange("Hola", "Hol"))
        assertEquals(UiActionSound.Undo, UiSemanticSoundPolicy.forLabel("Deshacer"))
        assertEquals(UiActionSound.Redo, UiSemanticSoundPolicy.forLabel("Redo"))
        assertEquals(UiActionSound.Back, UiSemanticSoundPolicy.forLabel("Atrás"))
    }

    @Test fun doesNotClassifySubstringsAsDestructive() {
        assertEquals(UiActionSound.Select, UiSemanticSoundPolicy.forLabel("undeleteable"))
        assertEquals(UiActionSound.Select, UiSemanticSoundPolicy.forLabel("bookmarks"))
    }
}
