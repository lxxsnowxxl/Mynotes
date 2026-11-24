package com.example.mynotes.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.DrawingScreen
import com.example.mynotes.ui.NoteEditorScreen
import com.example.mynotes.ui.ReminderScreen
import com.example.mynotes.ui.pdf.PdfEditorScreen
import com.example.mynotes.ui.preview.LayoutPreviewData

@Composable
private fun AdaptiveEditorScene(kind: String) {
    val settings = AppSettings(configurationMode = "advanced", animationsEnabled = false, soundEffectsEnabled = false)
    val notes = previewNotes()
    LayoutPreviewFrame(settings, LayoutPreviewData(nowMillis = PreviewTimestamp)) {
        when (kind) {
            "note" -> NoteEditorScreen(settings, initialTitle = "Ideas para el proyecto", initialContent = "Reunir referencias y preparar el primer boceto.\n\nCada pantalla conserva sus herramientas al lado cuando hay espacio.",
                initialNote = notes.firstOrNull(), isEditing = true, noteCatalog = notes, onSave = {}, onCancel = {})
            "drawing" -> DrawingScreen(settings, {}, {})
            "reminder" -> ReminderScreen(settings, PreviewReminders(emptyList()), {}, initialCreate = true, noteCatalog = notes)
            "pdf" -> PdfEditorScreen(settings, null, {})
        }
    }
}

@Preview(name = "Note · phone", group = "v314 Adaptive layouts", widthDp = 411, heightDp = 891, locale = "en")
@Composable fun AdaptiveNotePhone() = AdaptiveEditorScene("note")
@Preview(name = "Note · tablet", group = "v314 Adaptive layouts", widthDp = 1200, heightDp = 840, locale = "en")
@Composable fun AdaptiveNoteTablet() = AdaptiveEditorScene("note")
@Preview(name = "Drawing · phone", group = "v314 Adaptive layouts", widthDp = 411, heightDp = 891, locale = "en")
@Composable fun AdaptiveDrawingPhone() = AdaptiveEditorScene("drawing")
@Preview(name = "Drawing · tablet", group = "v314 Adaptive layouts", widthDp = 1200, heightDp = 840, locale = "en")
@Composable fun AdaptiveDrawingTablet() = AdaptiveEditorScene("drawing")
@Preview(name = "Reminder · phone", group = "v314 Adaptive layouts", widthDp = 411, heightDp = 891, locale = "en")
@Composable fun AdaptiveReminderPhone() = AdaptiveEditorScene("reminder")
@Preview(name = "Reminder · tablet", group = "v314 Adaptive layouts", widthDp = 1200, heightDp = 840, locale = "en")
@Composable fun AdaptiveReminderTablet() = AdaptiveEditorScene("reminder")
@Preview(name = "PDF · phone", group = "v314 Adaptive layouts", widthDp = 411, heightDp = 891, locale = "en")
@Composable fun AdaptivePdfPhone() = AdaptiveEditorScene("pdf")
@Preview(name = "PDF · tablet", group = "v314 Adaptive layouts", widthDp = 1200, heightDp = 840, locale = "en")
@Composable fun AdaptivePdfTablet() = AdaptiveEditorScene("pdf")
@Preview(name = "Note · narrow / large text", group = "v314 Adaptive layouts", widthDp = 320, heightDp = 700, fontScale = 1.8f, locale = "en")
@Composable fun AdaptiveNoteLargeText() = AdaptiveEditorScene("note")
