package com.example.mynotes.preview

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.mynotes.R
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.reminders.Reminder
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.preview.LayoutPreviewData
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.state.NoteActions
import com.example.mynotes.ui.state.ReminderActions
import com.example.mynotes.ui.state.SettingsActions
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.ui.theme.effectiveDarkTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Servicios del editor visual. No instancia Activity, ViewModels, Room, alarmas ni DataStore. */
private class PreviewOwners : OnBackPressedDispatcherOwner, ActivityResultRegistryOwner {
    override val lifecycle: Lifecycle = LifecycleRegistry.createUnsafe(this).apply {
        currentState = Lifecycle.State.RESUMED
    }
    override val onBackPressedDispatcher = OnBackPressedDispatcher()
    override val activityResultRegistry = object : ActivityResultRegistry() {
        override fun <I, O> onLaunch(
            requestCode: Int, contract: ActivityResultContract<I, O>, input: I,
            options: ActivityOptionsCompat?
        ) { /* Selectores y permisos se ejercitan en la app, no durante el render del layout. */ }
    }
}

internal class PreviewNotes(attachments: List<Attachment>) : NoteActions {
    override fun toggleFavorite(note: Note) { }
    override fun togglePinned(note: Note) { }
    override fun changePriority(note: Note, priority: Int) { }
    override fun changeNoteColor(note: Note, color: String) { }
    override fun changeCategory(note: Note, category: String) { }
    override fun deleteNote(note: Note) { }
    override val allAttachments = MutableStateFlow(attachments)
    override fun getAttachments(noteId: Int): Flow<List<Attachment>> =
        MutableStateFlow(allAttachments.value.filter { it.noteId == noteId })
}

internal class PreviewReminders(items: List<Reminder>) : ReminderActions {
    override fun upsert(reminder: Reminder) { }
    override fun delete(id: Long) { }
    override fun setEnabled(id: Long, enabled: Boolean) { }
    override val reminders = MutableStateFlow(items)
}

internal val PreviewSettingsActions = object : SettingsActions {
    override fun setConfigurationMode(value: String) { }
    override fun setDarkMode(value: Boolean) { }
    override fun setBackgroundSelection(paletteKey: String, toneIndex: Int) { }
    override fun setSettingsPanelTone(value: Float) { }
    override fun setSurfacePanelIntensity(value: Float) { }
    override fun setTextColor(value: String) { }
    override fun setTextOutlineEnabled(value: Boolean) { }
    override fun setSliderStyle(value: String) { }
    override fun setFont(value: String) { }
    override fun setFontSize(value: Float) { }
    override fun setSoundEffectsEnabled(value: Boolean) { }
    override fun setSoundEffectsVolume(value: Float) { }
    override fun setSoundEffectsTheme(value: String) { }
    override fun setReminderSoundEnabled(value: Boolean) { }
    override fun setReminderSoundVolume(value: Float) { }
    override fun setReminderRingtone(value: String) { }
    override fun setHapticEffectsEnabled(value: Boolean) { }
    override fun setHapticEffectsIntensity(value: Float) { }
    override fun setHapticEffectsStyle(value: String) { }
    override fun setLanguage(value: String) { }
    override fun setGridColumns(value: Int) { }
    override fun setSortOrder(value: String) { }
    override fun setProfileImageUri(value: String) { }
    override fun setProfileImageSize(value: Float) { }
    override fun setIconStyle(value: String) { }
    override fun setIconSize(value: Float) { }
    override fun setAccentColor(value: String) { }
    override fun setNoteCardCornerRadius(value: Float) { }
    override fun setNoteCardElevation(value: Float) { }
    override fun setNoteCardPadding(value: Float) { }
    override fun setNoteCardImageHeight(value: Float) { }
    override fun setNoteCardOutlineWidth(value: Float) { }
    override fun setNoteTitleMaxLines(value: Int) { }
    override fun setNoteContentMaxLines(value: Int) { }
    override fun setNoteLineSpacing(value: Float) { }
    override fun setShowNoteDate(value: Boolean) { }
    override fun setShowCategoryChip(value: Boolean) { }
    override fun setShowFavoriteIcon(value: Boolean) { }
    override fun setFabSize(value: Float) { }
    override fun setOptionMenuOrder(value: String) { }
    override fun setOptionMenuHiddenItems(value: String) { }
    override fun setOptionMenuShowIcons(value: Boolean) { }
    override fun setOptionMenuTextColor(value: String) { }
    override fun setOptionMenuOpacity(value: Float) { }
    override fun setPriorityMenuHiddenItems(value: String) { }
    override fun setColorMenuHiddenItems(value: String) { }
    override fun setPerformanceMode(value: String) { }
    override fun setAnimationsEnabled(value: Boolean) { }
    override fun setAnimationSpeed(value: Float) { }
    override fun setAnimationStyle(value: String) { }
    override fun setAnimationEasing(value: String) { }
    override fun setAnimationIntensity(value: Float) { }
}

internal const val PreviewTimestamp = 1_793_029_200_000L

internal fun previewNotes(): List<Note> = listOf(
    Note(1, "Proyecto de diseño", "Revisar materiales y medidas de la carcasa.\nPreparar la siguiente revisión.", PreviewTimestamp, isPinned = true),
    Note(2, "Lista de compras", "Café, fruta, pan y leche.", PreviewTimestamp - 60_000, color = "yellow"),
    Note(3, "Ideas para esta semana", "Organizar las notas y terminar los pendientes.", PreviewTimestamp - 120_000, color = "blue", isFavorite = true),
    Note(4, "Reunión de trabajo", "Confirmar entregas y documentar los acuerdos.", PreviewTimestamp - 180_000, color = "green", category = "work"),
    Note(5, "Lecturas", "Guardar referencias para el próximo proyecto.", PreviewTimestamp - 240_000, color = "purple"),
    Note(6, "Recordar", "Respaldar los archivos del proyecto.", PreviewTimestamp - 300_000, color = "pink", priority = 2)
)

internal fun previewAttachments(): List<Attachment> = listOf(
    Attachment(1, 1, "image", "preview://sample/foto.jpg", "Fotografía.jpg", PreviewTimestamp),
    Attachment(2, 1, "video", "preview://sample/video.mp4", "Video.mp4", PreviewTimestamp),
    Attachment(3, 1, "voice", "preview://sample/voz.m4a", "Nota de voz.m4a", PreviewTimestamp),
    Attachment(4, 1, "file", "preview://sample/documento.pdf", "Documento.pdf", PreviewTimestamp),
    Attachment(5, 1, "file", "preview://sample/reporte.docx", "Reporte.docx", PreviewTimestamp),
    Attachment(6, 1, "file", "preview://sample/archivo.zip", "Archivo.zip", PreviewTimestamp)
)

internal fun previewReminders(): List<Reminder> = listOf(
    Reminder(1, "Revisar el proyecto", "Verificar medidas y preparar los documentos.", PreviewTimestamp + 86_400_000,
        repeatMode = Reminder.REPEAT_DAILY, priority = Reminder.PRIORITY_HIGH),
    Reminder(2, "Respaldar mis notas", "Guardar una copia de seguridad.", PreviewTimestamp + 172_800_000,
        repeatMode = Reminder.REPEAT_WEEKLY),
    Reminder(3, "Comprar materiales", triggerAtMillis = PreviewTimestamp + 259_200_000, enabled = false)
)

@Composable
internal fun rememberPreviewBitmap(document: Boolean = false): Bitmap {
    val context = LocalContext.current
    return remember(context, document) {
        val bitmap = Bitmap.createBitmap(if (document) 595 else 720, if (document) 842 else 450, Bitmap.Config.ARGB_8888)
        val drawable = requireNotNull(context.getDrawable(if (document) R.drawable.preview_pdf_page else R.drawable.preview_media_photo))
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)
        drawable.draw(Canvas(bitmap))
        bitmap
    }
}

@Composable
internal fun LayoutPreviewFrame(settings: AppSettings, data: LayoutPreviewData, content: @Composable () -> Unit) {
    val owners = remember { PreviewOwners() }
    CompositionLocalProvider(
        LocalActivityResultRegistryOwner provides owners,
        LocalOnBackPressedDispatcherOwner provides owners,
        LocalLifecycleOwner provides owners,
        LocalLayoutPreview provides data
    ) {
        MyNotesTheme(settings = settings, darkTheme = settings.effectiveDarkTheme(), content = content)
    }
}
