package com.example.mynotes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.data.AppDatabase
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.data.PendingAttachment
import com.example.mynotes.performance.DisplayPerformanceController
import com.example.mynotes.reminders.ReminderRepository
import com.example.mynotes.reminders.ReminderFeedbackPreferences
import com.example.mynotes.ui.DrawingScreen
import com.example.mynotes.ui.pdf.PdfLibraryActivity
import com.example.mynotes.ui.pdf.suppressPendingActivityAnimation
import com.example.mynotes.ui.pdf.withPdfScreenMotion
import com.example.mynotes.ui.NoteDetailScreen
import com.example.mynotes.ui.NoteEditorScreen
import com.example.mynotes.ui.ReminderScreen
import com.example.mynotes.ui.NotesScreen
import com.example.mynotes.ui.DevelopmentInfoScreen
import com.example.mynotes.ui.SourceCodeInfoScreen
import com.example.mynotes.ui.components.ConfigurationModeDialog
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.motion.ConfigurableAnimatedContent
import com.example.mynotes.ui.SettingsScreen
import com.example.mynotes.ui.theme.MyNotesTheme
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.effectiveDarkTheme
import com.example.mynotes.viewmodel.NoteViewModel
import com.example.mynotes.viewmodel.SettingsViewModel
import com.example.mynotes.widget.MyNotesWidgetUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private enum class AppDestination {
    NOTES, SETTINGS, DEVELOPMENT_INFO, SOURCE_CODE_INFO, EDITOR, DRAWING, REMINDERS, DETAIL
}

private data class NavigationSnapshot(val destination: AppDestination, val note: Note? = null)

class MainActivity : ComponentActivity() {
    private lateinit var keyboardSounds: SystemKeyboardSoundController
    private val incoming = MainIntentState()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withSavedAppLocale())
    }
    private fun changeAppLanguage(language: String) {
        saveAppLanguage(language)
        MyNotesWidgetUpdater.requestUpdate(this)
        recreate()
    }
    private fun installImeNavigationBarRecovery() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (keyboardSounds.onImeVisibilityChanged(isImeVisible)) {
                view.post { applyAndroidNavigationBarPolicy() }
            }
            insets
        }
        ViewCompat.requestApplyInsets(window.decorView)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            applyAndroidNavigationBarPolicy()
        }
    }
    override fun onResume() {
        super.onResume()
        keyboardSounds.onResume()
        ViewCompat.requestApplyInsets(window.decorView)
        DisplayPerformanceController.reapplyLastRequest(window)
        applyAndroidNavigationBarPolicy()
    }
    override fun onPause() {
        keyboardSounds.onPause()
        super.onPause()
    }
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        applyAndroidNavigationBarPolicy()
    }
    override fun onDestroy() {
        keyboardSounds.restore()
        DisplayPerformanceController.release(window)
        super.onDestroy()
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incoming.handle(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        keyboardSounds = SystemKeyboardSoundController(this)
        incoming.handle(intent)
        enableEdgeToEdge()
        installImeNavigationBarRecovery()
        applyAndroidNavigationBarPolicy()
        setContent {
            val noteViewModel:
                    NoteViewModel = viewModel()
            val settingsViewModel:
                    SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            val reminderRepository = remember {
                ReminderRepository.getInstance(applicationContext)
            }
            val systemDarkTheme = isSystemInDarkTheme()
            SyncUiFeedback(context = this@MainActivity, settings = settings)

            LaunchedEffect(settings.soundEffectsEnabled) {
                keyboardSounds.setEnabled(settings.soundEffectsEnabled)
            }

            LaunchedEffect(
                settings.soundEffectsEnabled,
                settings.soundEffectsVolume,
                settings.soundEffectsTheme,
                settings.reminderSoundEnabled,
                settings.reminderSoundVolume,
                settings.reminderRingtone,
                settings.hapticEffectsEnabled,
                settings.hapticEffectsIntensity,
                settings.hapticEffectsStyle,
                settings.configurationMode,
                settings.darkMode,
                settings.backgroundColor,
                settings.backgroundToneIndex,
                settings.backgroundIntensity,
                settings.surfacePanelIntensity,
                settings.headerIntensity,
                settings.textColor,
                settings.accentColor,
                settings.fontSize,
                systemDarkTheme
            ) {
                ReminderFeedbackPreferences.sync(this@MainActivity, settings)
            }
            
            val effectiveDarkTheme = settings.effectiveDarkTheme(systemDarkTheme)
            LaunchedEffect(effectiveDarkTheme) {
                applySystemBarAppearance(effectiveDarkTheme)
            }
            SyncDisplayPerformance(window = window, performanceMode = settings.performanceMode)
            var showEditor by remember { mutableStateOf(false) }
            var showDrawing by remember { mutableStateOf(false) }
            var showReminders by remember { mutableStateOf(false) }
            var createReminderOnOpen by remember { mutableStateOf(false) }
            var showSettings by remember { mutableStateOf(false) }
            /* Pantalla técnica secundaria abierta desde Configuración. */
            var showDevelopmentInfo by remember { mutableStateOf(false) }
            /* Subpantalla informativa con el mapa del código fuente del proyecto. */
            var showSourceCodeInfo by remember { mutableStateOf(false) }
            var selectedNote by remember { mutableStateOf<Note?>(null) }
            var editingNote by remember { mutableStateOf<Note?>(null) }
            fun resetNavigation() {
                showSourceCodeInfo = false
                showDevelopmentInfo = false
                showSettings = false
                showEditor = false
                showDrawing = false
                showReminders = false
                selectedNote = null
                editingNote = null
            }
            fun openExclusive(clearShare: Boolean = true, action: () -> Unit) {
                if (clearShare) incoming.clearShare()
                resetNavigation()
                action()
            }
            fun openEditor(note: Note? = null) = openExclusive { editingNote = note; showEditor = true }
            fun openDrawing() = openExclusive { showDrawing = true }
            fun openReminders() = openExclusive { createReminderOnOpen = false; showReminders = true }
            fun openNote(note: Note) = openExclusive(clearShare = false) { selectedNote = note }
            fun closeEditor() { editingNote = null; incoming.clearShare(); showEditor = false }
            LaunchedEffect(incoming.openReminders) {
                if (incoming.openReminders) {
                    openReminders()
                    incoming.openReminders = false
                }
            }
            LaunchedEffect(incoming.widgetNewNote) {
                if (incoming.widgetNewNote) {
                    openEditor()
                    incoming.widgetNewNote = false
                }
            }

            LaunchedEffect(incoming.widgetNoteId) {
                val noteId = incoming.widgetNoteId ?: return@LaunchedEffect
                val note = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(applicationContext)
                        .noteDao()
                        .getNoteByIdOnce(noteId)
                }
                incoming.widgetNoteId = null

                if (note != null) {
                    incoming.clearShare()
                    openNote(note)
                }
            }

            LaunchedEffect(incoming.widgetNavigationToken) {
                if (incoming.widgetNavigationToken > 0 &&
                    (incoming.widgetCollection != null || incoming.widgetSearch)
                ) {
                    incoming.clearShare()
                    resetNavigation()
                }
            }

            LaunchedEffect(incoming.sharedText, incoming.sharedTitle) {
                if (!incoming.sharedText.isNullOrBlank()) {
                    resetNavigation()
                    showEditor = true
                }
            }
            BackHandler(enabled = showSourceCodeInfo || showDevelopmentInfo || showSettings || showEditor || showDrawing || showReminders || selectedNote != null) {
                when {
                    showSourceCodeInfo -> {
                        showSourceCodeInfo = false
                    }
                    showDevelopmentInfo -> {
                        showDevelopmentInfo = false
                    }
                    showSettings -> {
                        showSettings = false
                    }
                    showEditor -> {
                        closeEditor()
                    }
                    showDrawing -> {
                        showDrawing = false
                    }
                    showReminders -> {
                        showReminders = false
                    }
                    selectedNote != null -> {
                        selectedNote = null
                    }
                }
            }
            val currentScreen = remember(
                showSourceCodeInfo,
                showDevelopmentInfo,
                showSettings,
                showEditor,
                showDrawing,
                showReminders,
                editingNote,
                selectedNote
            ) {
                when {
                    showSourceCodeInfo -> NavigationSnapshot(AppDestination.SOURCE_CODE_INFO)
                    showDevelopmentInfo -> NavigationSnapshot(AppDestination.DEVELOPMENT_INFO)
                    showSettings -> NavigationSnapshot(AppDestination.SETTINGS)
                    showDrawing -> NavigationSnapshot(AppDestination.DRAWING)
                    showReminders -> NavigationSnapshot(AppDestination.REMINDERS)
                    showEditor -> NavigationSnapshot(AppDestination.EDITOR, editingNote)
                    selectedNote != null -> NavigationSnapshot(AppDestination.DETAIL, selectedNote)
                    else -> NavigationSnapshot(AppDestination.NOTES)
                }
            }
            // El overload conserva exactamente la misma paleta, contraste y tipografía.
            MyNotesTheme(settings = settings, darkTheme = effectiveDarkTheme, fontFamily = appFontFamily(settings.font)) {
                Box(modifier = Modifier.fillMaxSize()) {
                ConfigurableAnimatedContent(targetState = currentScreen,
                    animationsEnabled = settings.animationsEnabled,
                    animationSpeed = settings.animationSpeed,
                    animationStyle = settings.animationStyle,
                    animationEasing = settings.animationEasing,
                    animationIntensity = settings.animationIntensity,
                    performanceMode = settings.performanceMode) { screen ->
                    when (screen.destination) {
                    AppDestination.SETTINGS -> {
                        SettingsScreen(
                            settings = settings,
                            viewModel = settingsViewModel,
                            onLanguageChange = { language ->
                                settingsViewModel.setLanguage(language)
                                changeAppLanguage(language)
                            },
                            onOpenDevelopmentInfo = { showDevelopmentInfo = true },
                            onBack = { showSettings = false }
                        )
                    }
                    AppDestination.DEVELOPMENT_INFO -> {
                        DevelopmentInfoScreen(settings = settings,
                            onOpenSourceCode = {
                                showSourceCodeInfo = true
                            },
                            onBack = {
                                showDevelopmentInfo = false
                            })
                    }
                    AppDestination.SOURCE_CODE_INFO -> {
                        SourceCodeInfoScreen(settings = settings, onBack = {
                            showSourceCodeInfo = false
                        })
                    }
                    AppDestination.EDITOR -> {
                        val existingAttachments:
                                List<Attachment> = if (screen.note != null) {
                                val attachmentsFlow = remember(screen.note!!.id) {
                                        noteViewModel.getAttachments(screen.note!!.id)
                                    }
                                val currentAttachments by
                                    attachmentsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
                                currentAttachments
                            } else {
                                emptyList()
                            }
                        AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled,
                            animationSpeed = settings.animationSpeed) {
                            NoteEditorScreen(
                            settings = settings,
                            initialTitle = screen.note?.title?: incoming.sharedTitle.orEmpty(),
                            initialContent = screen.note?.content?: incoming.sharedText.orEmpty(),
                            initialColor = screen.note?.color?: "default",
                            isEditing = screen.note != null,
                            existingAttachments = existingAttachments,
                            onSave = {
                                    title, content, color, attachments, removedAttachments ->
                                val noteBeingEdited = screen.note
                                if (noteBeingEdited == null) {
                                    noteViewModel.addNote(title = title,
                                            content = content,
                                            color = color,
                                            attachments = attachments)
                                } else {
                                    noteViewModel.updateNote(note = noteBeingEdited,
                                            title = title,
                                            content = content,
                                            color = color,
                                            newAttachments = attachments)
                                    removedAttachments.forEach {
                                                attachment ->
                                            noteViewModel.deleteAttachment(attachment)
                                        }
                                }
                                closeEditor()
                            },
                            onCancel = {
                                closeEditor()
                            })
                        }
                    }
                    AppDestination.DRAWING -> {
                        DrawingScreen(
                            settings = settings,
                            onCancel = {
                                showDrawing = false
                            },
                            onSave = { drawingUri: Uri ->
                                noteViewModel.addNote(
                                    title = getString(R.string.drawing_default_note_title),
                                    content = "",
                                    color = "default",
                                    attachments = listOf(
                                        PendingAttachment(
                                            uri = drawingUri,
                                            type = "image",
                                            name = "drawing_${System.currentTimeMillis()}.png",
                                            mimeType = "image/png"
                                        )
                                    )
                                )
                                showDrawing = false
                            }
                        )
                    }
                    AppDestination.REMINDERS -> {
                        ReminderScreen(
                            settings = settings,
                            repository = reminderRepository,
                            onBack = {
                                createReminderOnOpen = false
                                showReminders = false
                            },
                            initialCreate = createReminderOnOpen
                        )
                    }
                    AppDestination.DETAIL -> {
                        val detailNotes by noteViewModel.notes.collectAsStateWithLifecycle()
                        val currentSelectedNote = detailNotes.firstOrNull {
                                    it.id == screen.note!!.id
                                }?: screen.note!!
                        NoteDetailScreen(
                            note = currentSelectedNote,
                            noteViewModel = noteViewModel,
                            settings = settings,
                            onBack = {
                                selectedNote = null
                            },
                            onEdit = ::openEditor)
                    }
                    AppDestination.NOTES -> {
                        val notes by noteViewModel.notes.collectAsStateWithLifecycle()
                        NotesScreen(
                            notes = notes,
                            noteViewModel = noteViewModel,
                            settings = settings,
                            initialFilterKey = incoming.widgetCollection,
                            requestSearchFocus = incoming.widgetSearch,
                            widgetRequestToken = incoming.widgetNavigationToken,
                            onAddNote = { openEditor() },
                            onDrawNote = ::openDrawing,
                            onOpenReminders = ::openReminders,
                            onAddPdf = {
                                incoming.clearShare()
                                startActivity(
                                    Intent(this@MainActivity, PdfLibraryActivity::class.java)
                                        .withPdfScreenMotion(settings)
                                )
                                suppressPendingActivityAnimation()
                            },
                            onOpenSettings = {
                                showDevelopmentInfo = false
                                showDrawing = false
                                showReminders = false
                                showSettings = true
                            },
                            onOpenNote = ::openNote,
                            onEditNote = ::openEditor)
                    }
                }
                }
                if (settings.configurationMode == "unset") {
                    ConfigurationModeDialog(
                        fontFamily = appFontFamily(settings.font),
                        onBasicSelected = {
                            settingsViewModel.setConfigurationMode("basic")
                        },
                        onAdvancedSelected = {
                            settingsViewModel.setConfigurationMode("advanced")
                        }
                    )
                }
                }
            }
        }
    }
}
