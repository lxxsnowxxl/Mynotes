package com.example.mynotes.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.DesignPreferencePolicy
import com.example.mynotes.ui.NoteEditorScreen
import com.example.mynotes.ui.NotesScreen
import com.example.mynotes.ui.components.DesignSettingsPanel
import com.example.mynotes.ui.preview.LayoutPreviewData
import com.example.mynotes.ui.theme.appFontFamily

@Composable
private fun DesignScene(preset: String, editor: Boolean = false, tablet: Boolean = false) {
    val settings = remember(preset) { DesignPreferencePolicy.applyPreset(
        AppSettings(configurationMode = "advanced", animationsEnabled = false, soundEffectsEnabled = false), preset) }
    val notes = remember { previewNotes() }
    val actions = remember { PreviewNotes(previewAttachments()) }
    LayoutPreviewFrame(settings, LayoutPreviewData(nowMillis = PreviewTimestamp,
        selectedNoteId = if (tablet) 1 else null)) {
        if (editor) NoteEditorScreen(settings, initialTitle = "Ideas para hoy",
            initialContent = "Un espacio tranquilo para escribir.\n\nOrganizar las ideas, guardar referencias y crear algo nuevo.",
            onSave = { _ -> }, onCancel = {})
        else NotesScreen(notes, actions, settings, onAddNote = {}, onDrawNote = {}, onOpenReminders = {},
            onAddPdf = {}, onOpenSettings = {}, onOpenNote = {}, onEditNote = {}, settingsActions = PreviewSettingsActions)
    }
}

@Preview(name = "v311 Minimalist", group = "v311 Design", widthDp = 411, heightDp = 891, locale = "es")
@Composable fun DesignMinimalPreview() = DesignScene("minimal")

@Preview(name = "v311 Notebook", group = "v311 Design", widthDp = 411, heightDp = 891, locale = "es")
@Composable fun DesignNotebookPreview() = DesignScene("notebook")

@Preview(name = "Default", group = "Design", widthDp = 411, heightDp = 891, locale = "es")
@Composable fun DesignDefaultPreview() = DesignScene("default")

@Preview(name = "v311 OLED", group = "v311 Design", widthDp = 411, heightDp = 891, locale = "es")
@Composable fun DesignOledPreview() = DesignScene("oled")

@Preview(name = "v311 Focus editor", group = "v311 Design", widthDp = 360, heightDp = 800, locale = "es")
@Composable fun DesignFocusPreview() = DesignScene("notebook", editor = true)

@Preview(name = "v311 Tablet", group = "v311 Design", widthDp = 1100, heightDp = 800, locale = "es")
@Composable fun DesignTabletPreview() = DesignScene("minimal", tablet = true)

@Preview(name = "v311 Styles", group = "v311 Design", widthDp = 411, heightDp = 400, locale = "es")
@Composable fun DesignStylesPreview() {
    val settings = AppSettings(configurationMode = "advanced", animationsEnabled = false)
    LayoutPreviewFrame(settings, LayoutPreviewData()) {
        DesignSettingsPanel(settings, PreviewSettingsActions, appFontFamily(settings.font), onPreview = {})
    }
}
