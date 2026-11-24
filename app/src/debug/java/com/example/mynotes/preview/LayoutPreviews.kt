package com.example.mynotes.preview

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.data.NoteFolder
import com.example.mynotes.ui.notes.FolderManagerDialog
import com.example.mynotes.ui.notes.IdleMotionOptions
import com.example.mynotes.ui.notes.NoteCardAnimationSettingsDialog
import com.example.mynotes.ui.components.DesignSettingsPanel
import com.example.mynotes.ui.components.advancedSettingsItemKeys
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.AttachmentViewerScreen
import com.example.mynotes.ui.components.ConfigurationModeDialog
import com.example.mynotes.ui.components.ProfileImageEditorDialog
import com.example.mynotes.ui.pdf.PdfEditorScreen
import com.example.mynotes.ui.pdf.PdfImageCropDialog
import com.example.mynotes.ui.pdf.PdfImageElement
import com.example.mynotes.ui.pdf.PdfLibraryScreen
import com.example.mynotes.ui.pdf.PdfPageModel
import com.example.mynotes.ui.pdf.PdfProjectRepository
import com.example.mynotes.ui.pdf.PdfTextElement
import com.example.mynotes.ui.preview.LayoutPreviewData
import com.example.mynotes.ui.DevelopmentInfoScreen
import com.example.mynotes.ui.DrawingScreen
import com.example.mynotes.ui.NoteDetailScreen
import com.example.mynotes.ui.NoteEditorScreen
import com.example.mynotes.ui.NotesScreen
import com.example.mynotes.ui.ReminderScreen
import com.example.mynotes.ui.SettingsScreen
import com.example.mynotes.ui.SourceCodeInfoScreen

/** Estados Compose para comparar con los diseños XML editables. */
@Composable
internal fun LayoutPreviewScreen(scene: String) {
    val mediaBitmap = rememberPreviewBitmap()
    val documentBitmap = rememberPreviewBitmap(document = true)
    val settings = AppSettings(
        configurationMode = if (scene == "preview_25_settings_basic_profile_mode") "basic" else "advanced",
        animationsEnabled = scene in setOf("preview_07_settings_transition_dropdown", "preview_40_settings_background_motion", "preview_53_card_animations"),
        language = "es",
        editorFocusMode = scene !in setOf("preview_02b_note_editor_recording", "preview_14_note_editor_attachments"),
        profileImageUri = if (scene == "preview_26_settings_advanced_profile_mode") "preview://profile" else "",
        settingsBackgroundStyle = when (scene) {
            "preview_38_settings_background_gradient" -> "gradient"
            "preview_39_settings_background_blur" -> "blur"
            "preview_40_settings_background_motion" -> "animated_blur"
            else -> "solid"
        },
        soundEffectsVolume = 35f,
        reminderSoundVolume = 80f,
        gridColumns = when (scene) {
            "preview_10_notes_one_column", "preview_12_note_card_attachments" -> 1
            "preview_11_notes_three_columns" -> 3
            else -> 2
        },
        backgroundColor = when (scene) {
            "preview_22_settings_palette_tones", "preview_39_settings_background_blur", "preview_40_settings_background_motion" -> "lavender"
            "preview_38_settings_background_gradient" -> "ocean"
            else -> "neutral"
        }
    )
    val sampleImage = remember(mediaBitmap) {
        PdfImageElement(1, mediaBitmap, 0.10f, 0.32f, 0.80f, 0.36f)
    }
    val loadedPdf = remember(mediaBitmap) {
        listOf(
            PdfPageModel(texts = listOf(PdfTextElement(2, "Plan de trabajo", 0.10f, 0.14f, 26f, android.graphics.Color.BLACK)),
                images = listOf(sampleImage)),
            PdfPageModel(texts = listOf(PdfTextElement(3, "Notas y referencias", 0.10f, 0.16f, 22f, android.graphics.Color.BLACK)))
        )
    }
    val data = LayoutPreviewData(
        nowMillis = PreviewTimestamp,
        selectionIds = if (scene == "preview_44_batch_selection") listOf(1, 2) else emptyList(),
        archived = scene == "preview_52_archived_notes",
        reading = scene == "preview_46_reading_mode",
        templatePicker = scene == "preview_48_note_templates",
        coverPicker = scene == "preview_51_cover_framing",
        livePreview = scene == "preview_49_appearance_preview",
        formattingToolbar = scene == "preview_47_rich_editor",
        selectedNoteId = if (scene == "preview_42_notes_tablet") 1 else null,
        menuNoteId = 1,
        durationMillis = 180_000,
        positionMillis = 42_000,
        linkTitle = "Guía del proyecto",
        linkDescription = "Documentación, referencias y material de consulta.",
        query = if (scene == "preview_09_notes_search") "proyecto" else "",
        settingsItemIndex = when (scene) {
            "preview_04_settings" -> advancedSettingsItemKeys.indexOf("appearance_profile") // appearance_profile
            "preview_04b_settings_personalization" -> advancedSettingsItemKeys.indexOf("extreme_icons") // extreme_icons
            "preview_04c_settings_options_menu" -> advancedSettingsItemKeys.indexOf("options_appearance") // options_appearance
            "preview_04d_settings_sound_haptic" -> advancedSettingsItemKeys.indexOf("feedback_sound_effects") // feedback_sound_effects
            "preview_07_settings_transition_dropdown" -> advancedSettingsItemKeys.indexOf("extreme_motion") // extreme_motion
            "preview_18_settings_appearance_text" -> advancedSettingsItemKeys.indexOf("appearance_text_color") // appearance_text_color
            "preview_19_settings_backup_restore" -> advancedSettingsItemKeys.indexOf("layout_backup_restore") // layout_backup_restore
            "preview_20_settings_card_layout" -> advancedSettingsItemKeys.indexOf("extreme_note_cards") // extreme_note_cards
            "preview_20b_settings_card_layout_collapsed" -> advancedSettingsItemKeys.indexOf("extreme_note_cards") // extreme_note_cards
            "preview_21_settings_performance_motion" -> advancedSettingsItemKeys.indexOf("note_card_animation_styles") // note_card_animation_styles
            "preview_53_card_animations" -> advancedSettingsItemKeys.indexOf("note_card_animation_styles") // note_card_animation_styles
            "preview_22_settings_palette_tones" -> advancedSettingsItemKeys.indexOf("palette_background") // palette_background
            "preview_25_settings_basic_profile_mode" -> advancedSettingsItemKeys.indexOf("appearance_profile") // appearance_profile
            "preview_26_settings_advanced_profile_mode" -> advancedSettingsItemKeys.indexOf("appearance_profile") // appearance_profile
            "preview_37_settings_background_solid" -> advancedSettingsItemKeys.indexOf("palette_background") // palette_background
            "preview_38_settings_background_gradient" -> advancedSettingsItemKeys.indexOf("palette_background") // palette_background
            "preview_39_settings_background_blur" -> advancedSettingsItemKeys.indexOf("palette_background") // palette_background
            "preview_40_settings_background_motion" -> advancedSettingsItemKeys.indexOf("palette_background") // palette_background
            else -> 0
        },
        noteCardsExpanded = scene != "preview_20b_settings_card_layout_collapsed",
        noteMenu = when (scene) {
            "preview_06_note_options_menu" -> "main"
            "preview_06b_priority_menu" -> "priority"
            "preview_06c_color_menu" -> "color"
            "preview_06d_move_menu" -> "category"
            else -> null
        },
        deleteDialog = scene == "preview_03b_note_delete_dialog",
        quickCreateExpanded = scene == "preview_27_notes_quick_create_menu",
        recording = scene == "preview_02b_note_editor_recording",
        expandedDropdownTitle = if (scene == "preview_07_settings_transition_dropdown") stringResource(R.string.motion_style) else null,
        mediaBitmap = mediaBitmap,
        documentBitmap = documentBitmap,
        viewerText = "Plan del proyecto\n\n1. Revisar medidas y materiales.\n2. Registrar los resultados.\n3. Preparar la siguiente revisión.\n\nObservaciones\nConservar los documentos y las referencias de trabajo.",
        pdfPages = if (scene == "preview_33_pdf_editor_loaded" || scene == "preview_36_pdf_image_crop") loadedPdf else emptyList(),
        pdfProjects = listOf(
            PdfProjectRepository.Summary("sample-1", "Proyecto de diseño", PreviewTimestamp, 2),
            PdfProjectRepository.Summary("sample-2", "Documentos de trabajo", PreviewTimestamp - 86_400_000, 4)
        ),
        reminderEditingId = if (scene == "preview_30_reminder_editor") 1L else null
    )
    val sampleFolders = remember { listOf(NoteFolder("ideas", "Ideas", "idea", "sage"), NoteFolder("study", "Estudios", "study", "ocean")) }
    val samples = remember(scene) { previewNotes().mapIndexed { index, note ->
        if (scene.startsWith("preview_4") || scene.startsWith("preview_5")) note.copy(folderId = if (index % 2 == 0) "ideas" else "study",
            isArchived = scene == "preview_52_archived_notes" && index == 4,
            coverUri = if (scene == "preview_45_note_covers" && index == 0) "preview://cover" else null)
        else note
    } }
    val attachments = remember(scene) {
        val all = previewAttachments()
        when (scene) {
            "preview_01_notes" -> all.take(2)
            "preview_12_note_card_attachments", "preview_14_note_editor_attachments" -> all
            "preview_16_note_detail_media" -> all.filter { it.type == "image" || it.type == "video" || it.type == "voice" }
            "preview_17_note_detail_files" -> all.filter { it.type == "file" }
            else -> emptyList()
        }
    }
    val notesActions = remember(attachments, sampleFolders) { PreviewNotes(attachments, sampleFolders) }
    val reminderActions = remember { PreviewReminders(previewReminders()) }
    val notes = when {
        scene == "preview_08_notes_empty" -> emptyList()
        data.noteMenu != null || scene == "preview_12_note_card_attachments" -> samples.take(1)
        else -> samples
    }
    val detailNote = if (scene == "preview_13_link_previews") samples.first().copy(
        content = "Referencias del proyecto\nhttps://developer.android.com\nhttps://kotlinlang.org"
    ) else if (scene in setOf("preview_46_reading_mode", "preview_47_rich_editor", "preview_51_cover_framing")) samples.first().copy(
        content = "## Prioridades\n**Revisar** el proyecto y ==preparar materiales==.\n\n- [ ] Revisar medidas\n- [x] Guardar referencias",
        formattingEnabled = true, coverUri = if (scene == "preview_51_cover_framing") "preview://cover" else null
    ) else samples.first()
    LayoutPreviewFrame(settings, data) {
        when (scene) {
            "preview_50_folder_manager" -> FolderManagerDialog(sampleFolders, notesActions) { }
            "preview_41_design_styles" -> Box(Modifier.padding(22.dp)) {
                DesignSettingsPanel(settings, PreviewSettingsActions, FontFamily.Default, onPreview = {})
            }
            "preview_43_folders",
            "preview_44_batch_selection",
            "preview_45_note_covers",
            "preview_52_archived_notes",
            "preview_42_notes_tablet",
            "preview_01_notes",
            "preview_08_notes_empty",
            "preview_09_notes_search",
            "preview_10_notes_one_column",
            "preview_11_notes_three_columns",
            "preview_12_note_card_attachments",
            "preview_06_note_options_menu",
            "preview_06b_priority_menu",
            "preview_06c_color_menu",
            "preview_06d_move_menu",
            "preview_27_notes_quick_create_menu" -> NotesScreen(
                notes = notes, noteViewModel = notesActions, settings = settings,
                onAddNote = {}, onDrawNote = {}, onOpenReminders = {}, onAddPdf = {},
                onOpenSettings = {}, onOpenNote = {}, onEditNote = {}, settingsActions = PreviewSettingsActions
            )
            "preview_47_rich_editor",
            "preview_48_note_templates",
            "preview_51_cover_framing",
            "preview_02_note_editor",
            "preview_02b_note_editor_recording",
            "preview_14_note_editor_attachments",
            "preview_15_note_editor_edit_mode" -> NoteEditorScreen(
                settings = settings,
                initialTitle = if (scene in setOf("preview_47_rich_editor", "preview_51_cover_framing")) detailNote.title else if (scene == "preview_15_note_editor_edit_mode") "Proyecto de diseño" else "",
                initialContent = if (scene in setOf("preview_47_rich_editor", "preview_51_cover_framing")) detailNote.content else if (scene == "preview_15_note_editor_edit_mode") "Revisar las medidas y actualizar los documentos." else "",
                isEditing = scene in setOf("preview_15_note_editor_edit_mode", "preview_47_rich_editor", "preview_51_cover_framing"),
                initialNote = if (scene in setOf("preview_47_rich_editor", "preview_51_cover_framing")) detailNote else null,
                folders = sampleFolders,
                existingAttachments = attachments, onSave = { _ -> }, onCancel = {}
            )
            "preview_46_reading_mode",
            "preview_03_note_detail",
            "preview_03b_note_delete_dialog",
            "preview_13_link_previews",
            "preview_16_note_detail_media",
            "preview_17_note_detail_files" -> NoteDetailScreen(
                note = detailNote, noteViewModel = notesActions, settings = settings, onBack = {}, onEdit = {}
            )
            "preview_49_appearance_preview",
            "preview_04_settings",
            "preview_04b_settings_personalization",
            "preview_04c_settings_options_menu",
            "preview_04d_settings_sound_haptic",
            "preview_07_settings_transition_dropdown",
            "preview_18_settings_appearance_text",
            "preview_19_settings_backup_restore",
            "preview_20_settings_card_layout",
            "preview_20b_settings_card_layout_collapsed",
            "preview_21_settings_performance_motion",
            "preview_22_settings_palette_tones",
            "preview_25_settings_basic_profile_mode",
            "preview_26_settings_advanced_profile_mode",
            "preview_37_settings_background_solid",
            "preview_38_settings_background_gradient",
            "preview_39_settings_background_blur",
            "preview_40_settings_background_motion" -> SettingsScreen(
                settings = settings, viewModel = PreviewSettingsActions, onLanguageChange = {},
                onOpenDevelopmentInfo = {}, onBack = {}
            )
            "preview_05_attachment_viewer" -> AttachmentViewerScreen("preview://sample/documento.pdf", "file", "Documento.pdf", "application/pdf") {}
            "preview_05b_attachment_image" -> AttachmentViewerScreen("preview://sample/foto.jpg", "image", "Fotografía.jpg", "image/jpeg") {}
            "preview_05c_attachment_video" -> AttachmentViewerScreen("preview://sample/video.mp4", "video", "Video.mp4", "video/mp4") {}
            "preview_05d_attachment_audio" -> AttachmentViewerScreen("preview://sample/audio.m4a", "audio", "Nota de voz.m4a", "audio/mp4") {}
            "preview_05e_attachment_text" -> AttachmentViewerScreen("preview://sample/notas.txt", "file", "Notas.txt", "text/plain") {}
            "preview_05f_attachment_office" -> AttachmentViewerScreen("preview://sample/reporte.docx", "file", "Reporte.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {}
            "preview_05g_attachment_generic" -> AttachmentViewerScreen("preview://sample/archivo.zip", "file", "Archivo.zip", "application/zip") {}
            "preview_23_development_info" -> DevelopmentInfoScreen(settings, {}, {})
            "preview_24_source_code_info" -> SourceCodeInfoScreen(settings, {})
            "preview_28_drawing" -> DrawingScreen(settings, {}, {})
            "preview_29_reminders", "preview_30_reminder_editor" -> ReminderScreen(settings, reminderActions, {})
            "preview_31_pdf_library" -> PdfLibraryScreen(settings, 0, {}, {}, {})
            "preview_32_pdf_editor_empty", "preview_33_pdf_editor_loaded" -> PdfEditorScreen(settings, null, {})
            "preview_34_configuration_mode_dialog" -> {
                NotesScreen(notes = samples, noteViewModel = notesActions, settings = settings,
                    onAddNote = {}, onDrawNote = {}, onOpenReminders = {}, onAddPdf = {},
                    onOpenSettings = {}, onOpenNote = {}, onEditNote = {}, settingsActions = PreviewSettingsActions)
                ConfigurationModeDialog(FontFamily.Default, {}, {})
            }
            "preview_35_profile_image_editor" -> {
                SettingsScreen(settings, PreviewSettingsActions, {}, {}, {})
                ProfileImageEditorDialog(Uri.parse("preview://sample/perfil.jpg"), FontFamily.Default, {}, {})
            }
            "preview_53_card_animations" -> {
                SettingsScreen(settings, PreviewSettingsActions, {}, {}, {})
                NoteCardAnimationSettingsDialog(
                    options = IdleMotionOptions(),
                    globalAnimationsEnabled = true,
                    panelIntensity = settings.surfacePanelIntensity,
                    onChange = {},
                    onDismiss = {}
                )
            }
            "preview_36_pdf_image_crop" -> {
                PdfEditorScreen(settings, null, {})
                PdfImageCropDialog(sampleImage, FontFamily.Default, {}, {})
            }
            else -> error("Falta una entrada de vista previa para $scene")
        }
    }
}

@Preview(name = "preview_01_notes", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview01Notes() {
    LayoutPreviewScreen("preview_01_notes")
}

@Preview(name = "preview_02_note_editor", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview02NoteEditor() {
    LayoutPreviewScreen("preview_02_note_editor")
}

@Preview(name = "preview_02b_note_editor_recording", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview02bNoteEditorRecording() {
    LayoutPreviewScreen("preview_02b_note_editor_recording")
}

@Preview(name = "preview_03_note_detail", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview03NoteDetail() {
    LayoutPreviewScreen("preview_03_note_detail")
}

@Preview(name = "preview_03b_note_delete_dialog", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview03bNoteDeleteDialog() {
    LayoutPreviewScreen("preview_03b_note_delete_dialog")
}

@Preview(name = "preview_04_settings", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview04Settings() {
    LayoutPreviewScreen("preview_04_settings")
}

@Preview(name = "preview_04b_settings_personalization", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview04bSettingsPersonalization() {
    LayoutPreviewScreen("preview_04b_settings_personalization")
}

@Preview(name = "preview_04c_settings_options_menu", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview04cSettingsOptionsMenu() {
    LayoutPreviewScreen("preview_04c_settings_options_menu")
}

@Preview(name = "preview_04d_settings_sound_haptic", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview04dSettingsSoundHaptic() {
    LayoutPreviewScreen("preview_04d_settings_sound_haptic")
}

@Preview(name = "preview_05_attachment_viewer", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview05AttachmentViewer() {
    LayoutPreviewScreen("preview_05_attachment_viewer")
}

@Preview(name = "preview_05b_attachment_image", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview05bAttachmentImage() {
    LayoutPreviewScreen("preview_05b_attachment_image")
}

@Preview(name = "preview_05c_attachment_video", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview05cAttachmentVideo() {
    LayoutPreviewScreen("preview_05c_attachment_video")
}

@Preview(name = "preview_05d_attachment_audio", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview05dAttachmentAudio() {
    LayoutPreviewScreen("preview_05d_attachment_audio")
}

@Preview(name = "preview_05e_attachment_text", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview05eAttachmentText() {
    LayoutPreviewScreen("preview_05e_attachment_text")
}

@Preview(name = "preview_05f_attachment_office", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview05fAttachmentOffice() {
    LayoutPreviewScreen("preview_05f_attachment_office")
}

@Preview(name = "preview_05g_attachment_generic", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview05gAttachmentGeneric() {
    LayoutPreviewScreen("preview_05g_attachment_generic")
}

@Preview(name = "preview_06_note_options_menu", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview06NoteOptionsMenu() {
    LayoutPreviewScreen("preview_06_note_options_menu")
}

@Preview(name = "preview_06b_priority_menu", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview06bPriorityMenu() {
    LayoutPreviewScreen("preview_06b_priority_menu")
}

@Preview(name = "preview_06c_color_menu", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview06cColorMenu() {
    LayoutPreviewScreen("preview_06c_color_menu")
}

@Preview(name = "preview_06d_move_menu", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview06dMoveMenu() {
    LayoutPreviewScreen("preview_06d_move_menu")
}

@Preview(name = "preview_07_settings_transition_dropdown", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview07SettingsTransitionDropdown() {
    LayoutPreviewScreen("preview_07_settings_transition_dropdown")
}

@Preview(name = "preview_08_notes_empty", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview08NotesEmpty() {
    LayoutPreviewScreen("preview_08_notes_empty")
}

@Preview(name = "preview_09_notes_search", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview09NotesSearch() {
    LayoutPreviewScreen("preview_09_notes_search")
}

@Preview(name = "preview_10_notes_one_column", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview10NotesOneColumn() {
    LayoutPreviewScreen("preview_10_notes_one_column")
}

@Preview(name = "preview_11_notes_three_columns", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview11NotesThreeColumns() {
    LayoutPreviewScreen("preview_11_notes_three_columns")
}

@Preview(name = "preview_12_note_card_attachments", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview12NoteCardAttachments() {
    LayoutPreviewScreen("preview_12_note_card_attachments")
}

@Preview(name = "preview_13_link_previews", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview13LinkPreviews() {
    LayoutPreviewScreen("preview_13_link_previews")
}

@Preview(name = "preview_14_note_editor_attachments", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview14NoteEditorAttachments() {
    LayoutPreviewScreen("preview_14_note_editor_attachments")
}

@Preview(name = "preview_15_note_editor_edit_mode", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview15NoteEditorEditMode() {
    LayoutPreviewScreen("preview_15_note_editor_edit_mode")
}

@Preview(name = "preview_16_note_detail_media", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview16NoteDetailMedia() {
    LayoutPreviewScreen("preview_16_note_detail_media")
}

@Preview(name = "preview_17_note_detail_files", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview17NoteDetailFiles() {
    LayoutPreviewScreen("preview_17_note_detail_files")
}

@Preview(name = "preview_18_settings_appearance_text", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview18SettingsAppearanceText() {
    LayoutPreviewScreen("preview_18_settings_appearance_text")
}

@Preview(name = "preview_19_settings_backup_restore", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview19SettingsBackupRestore() {
    LayoutPreviewScreen("preview_19_settings_backup_restore")
}

@Preview(name = "preview_20_settings_card_layout", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview20SettingsCardLayout() {
    LayoutPreviewScreen("preview_20_settings_card_layout")
}

@Preview(name = "preview_20b_settings_card_layout_collapsed", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview20bSettingsCardLayoutCollapsed() {
    LayoutPreviewScreen("preview_20b_settings_card_layout_collapsed")
}

@Preview(name = "preview_21_settings_performance_motion", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview21SettingsPerformanceMotion() {
    LayoutPreviewScreen("preview_21_settings_performance_motion")
}

@Preview(name = "preview_22_settings_palette_tones", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview22SettingsPaletteTones() {
    LayoutPreviewScreen("preview_22_settings_palette_tones")
}

@Preview(name = "preview_23_development_info", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview23DevelopmentInfo() {
    LayoutPreviewScreen("preview_23_development_info")
}

@Preview(name = "preview_24_source_code_info", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview24SourceCodeInfo() {
    LayoutPreviewScreen("preview_24_source_code_info")
}

@Preview(name = "preview_25_settings_basic_profile_mode", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview25SettingsBasicProfileMode() {
    LayoutPreviewScreen("preview_25_settings_basic_profile_mode")
}

@Preview(name = "preview_26_settings_advanced_profile_mode", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview26SettingsAdvancedProfileMode() {
    LayoutPreviewScreen("preview_26_settings_advanced_profile_mode")
}

@Preview(name = "preview_27_notes_quick_create_menu", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview27NotesQuickCreateMenu() {
    LayoutPreviewScreen("preview_27_notes_quick_create_menu")
}

@Preview(name = "preview_28_drawing", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview28Drawing() {
    LayoutPreviewScreen("preview_28_drawing")
}

@Preview(name = "preview_29_reminders", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview29Reminders() {
    LayoutPreviewScreen("preview_29_reminders")
}

@Preview(name = "preview_30_reminder_editor", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview30ReminderEditor() {
    LayoutPreviewScreen("preview_30_reminder_editor")
}

@Preview(name = "preview_31_pdf_library", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview31PdfLibrary() {
    LayoutPreviewScreen("preview_31_pdf_library")
}

@Preview(name = "preview_32_pdf_editor_empty", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview32PdfEditorEmpty() {
    LayoutPreviewScreen("preview_32_pdf_editor_empty")
}

@Preview(name = "preview_33_pdf_editor_loaded", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview33PdfEditorLoaded() {
    LayoutPreviewScreen("preview_33_pdf_editor_loaded")
}

@Preview(name = "preview_34_configuration_mode_dialog", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview34ConfigurationModeDialog() {
    LayoutPreviewScreen("preview_34_configuration_mode_dialog")
}

@Preview(name = "preview_35_profile_image_editor", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview35ProfileImageEditor() {
    LayoutPreviewScreen("preview_35_profile_image_editor")
}

@Preview(name = "preview_36_pdf_image_crop", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview36PdfImageCrop() {
    LayoutPreviewScreen("preview_36_pdf_image_crop")
}


@Preview(name = "preview_37_settings_background_solid", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview37SettingsBackgroundSolid() {
    LayoutPreviewScreen("preview_37_settings_background_solid")
}

@Preview(name = "preview_38_settings_background_gradient", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview38SettingsBackgroundGradient() {
    LayoutPreviewScreen("preview_38_settings_background_gradient")
}

@Preview(name = "preview_39_settings_background_blur", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview39SettingsBackgroundBlur() {
    LayoutPreviewScreen("preview_39_settings_background_blur")
}

@Preview(name = "preview_40_settings_background_motion", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview40SettingsBackgroundMotion() {
    LayoutPreviewScreen("preview_40_settings_background_motion")
}

@Preview(name = "preview_41_design_styles", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview41DesignStyles() {
    LayoutPreviewScreen("preview_41_design_styles")
}

@Preview(name = "preview_42_notes_tablet", group = "Layouts Compose", widthDp = 1100, heightDp = 800, locale = "es", showBackground = true)
@Composable
fun Preview42NotesTablet() {
    LayoutPreviewScreen("preview_42_notes_tablet")
}

@Preview(name = "preview_43_folders", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview43Folders() {
    LayoutPreviewScreen("preview_43_folders")
}

@Preview(name = "preview_44_batch_selection", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview44BatchSelection() {
    LayoutPreviewScreen("preview_44_batch_selection")
}

@Preview(name = "preview_45_note_covers", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview45NoteCovers() {
    LayoutPreviewScreen("preview_45_note_covers")
}

@Preview(name = "preview_46_reading_mode", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview46ReadingMode() {
    LayoutPreviewScreen("preview_46_reading_mode")
}

@Preview(name = "preview_47_rich_editor", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview47RichEditor() {
    LayoutPreviewScreen("preview_47_rich_editor")
}

@Preview(name = "preview_48_note_templates", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview48NoteTemplates() {
    LayoutPreviewScreen("preview_48_note_templates")
}

@Preview(name = "preview_49_appearance_preview", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview49AppearancePreview() {
    LayoutPreviewScreen("preview_49_appearance_preview")
}

@Preview(name = "preview_50_folder_manager", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview50FolderManager() {
    LayoutPreviewScreen("preview_50_folder_manager")
}

@Preview(name = "preview_51_cover_framing", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview51CoverFraming() {
    LayoutPreviewScreen("preview_51_cover_framing")
}

@Preview(name = "preview_52_archived_notes", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview52ArchivedNotes() {
    LayoutPreviewScreen("preview_52_archived_notes")
}

@Preview(name = "preview_53_card_animations", group = "Layouts Compose", widthDp = 411, heightDp = 891, locale = "es", showBackground = true)
@Composable
fun Preview53CardAnimations() {
    LayoutPreviewScreen("preview_53_card_animations")
}
