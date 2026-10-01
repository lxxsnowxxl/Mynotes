package com.example.mynotes

import android.content.Context
import android.content.Intent
import android.media.AudioManager
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.mynotes.reminders.ReminderReceiver
import com.example.mynotes.ui.DrawingScreen
import com.example.mynotes.ui.pdf.PdfLibraryActivity
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
import com.example.mynotes.widget.WidgetActions
import com.example.mynotes.widget.MyNotesWidgetUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private enum class AppDestination {
    NOTES, SETTINGS, DEVELOPMENT_INFO, SOURCE_CODE_INFO, EDITOR, DRAWING, REMINDERS, DETAIL
}

private data class NavigationSnapshot(val destination: AppDestination, val note: Note? = null)

class MainActivity : ComponentActivity() {
    /*
     * El teclado puede volver a hacer visible la barra de navegación.
     * Guardamos el estado anterior del IME para detectar exactamente
     * cuando se cierra y restaurar el modo inmersivo en ese momento.
     */
    private var wasImeVisible = false

    /*
     * Mientras el IME está visible podemos silenciar temporalmente el canal
     * de sonidos de sistema que normalmente usa el teclado para sus clics.
     *
     * Es importante separar este canal del audio propio de MyNotes: los
     * efectos de la aplicación se reproducen por el canal multimedia desde
     * UiSoundPlayer, así que siguen oyéndose aunque STREAM_SYSTEM esté mudo.
     *
     * Guardamos si el canal ya estaba silenciado antes de intervenir para no
     * deshacer una preferencia del usuario cuando el teclado se cierre.
     */
    private lateinit var audioManager: AudioManager
    private var suppressSystemKeyboardSounds = false
    private var systemStreamMutedByMyNotes = false
    private var systemStreamWasMutedBeforeIme = false
    private var activityIsResumed = false
    /*
     * Texto recibido mediante Compartir desde otras aplicaciones
     * (navegador, YouTube, Spotify, noticias, etc.).
     */
    private var pendingSharedText by
        mutableStateOf<String?>(null)
    private var pendingSharedTitle by
        mutableStateOf<String?>(null)

    private var pendingWidgetNewNote by
        mutableStateOf(false)
    private var pendingWidgetNoteId by
        mutableStateOf<Int?>(null)
    private var pendingWidgetCollection by
        mutableStateOf<String?>(null)
    private var pendingWidgetSearch by
        mutableStateOf(false)
    private var widgetNavigationToken by
        mutableIntStateOf(0)
    private var pendingOpenReminders by
        mutableStateOf(false)

    private fun handleReminderIntent(incomingIntent: Intent?) {
        if (incomingIntent?.getBooleanExtra(ReminderReceiver.EXTRA_OPEN_REMINDERS, false) == true) {
            pendingOpenReminders = true
        }
    }

    private fun prepareWidgetNavigation(
        newNote: Boolean = false,
        noteId: Int? = null,
        collection: String? = null,
        search: Boolean = false
    ) {
        clearPendingShare()
        pendingWidgetNewNote = newNote
        pendingWidgetNoteId = noteId
        pendingWidgetCollection = collection
        pendingWidgetSearch = search
        widgetNavigationToken++
    }

    private fun handleWidgetIntent(incomingIntent: Intent?) {
        when (incomingIntent?.action) {
            WidgetActions.ACTION_NEW_NOTE -> prepareWidgetNavigation(newNote = true)
            WidgetActions.ACTION_OPEN_NOTE -> prepareWidgetNavigation(
                noteId = incomingIntent.getIntExtra(WidgetActions.EXTRA_NOTE_ID, -1).takeIf { it > 0 }
            )
            WidgetActions.ACTION_OPEN_COLLECTION -> prepareWidgetNavigation(
                collection = incomingIntent.getStringExtra(WidgetActions.EXTRA_COLLECTION)
                    ?: WidgetActions.COLLECTION_ALL
            )
            WidgetActions.ACTION_SEARCH -> prepareWidgetNavigation(
                collection = WidgetActions.COLLECTION_ALL,
                search = true
            )
        }
    }
    private fun handleIncomingShare(incomingIntent: Intent?) {
        if (incomingIntent?.action != Intent.ACTION_SEND) {
            return
        }
        val sharedText = incomingIntent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.trim().orEmpty()
        if (sharedText.isBlank()) {
            return
        }
        pendingSharedText = sharedText
        pendingSharedTitle = incomingIntent.getCharSequenceExtra(Intent.EXTRA_SUBJECT)?.toString()?.trim()?.takeIf { it.isNotBlank() }
    }
    private fun clearPendingShare() {
        pendingSharedText = null
        pendingSharedTitle = null
    }
    /*
     * ==========================================
     * IDIOMA PARA COMPONENTACTIVITY
     * ==========================================
     *
     * Se aplica antes de crear la Activity.
     * Esto permite que stringResource() lea
     * values-es, values-en o values-fr incluso
     * usando ComponentActivity.
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withSavedAppLocale())
    }
    private fun changeAppLanguage(language: String) {
        /*
         * Guardamos también en SharedPreferences porque
         * attachBaseContext() ocurre antes de que DataStore
         * pueda entregar AppSettings.
         *
         * commit() es intencional: necesitamos que el idioma
         * ya esté guardado antes de recreate().
         */
        saveAppLanguage(language)
        MyNotesWidgetUpdater.requestUpdate(this)
        recreate()
    }
    /*
     * ==========================================
     * BARRA DE NAVEGACIÓN DESPLEGABLE
     * ==========================================
     *
     * Oculta los botones de navegación de Android.
     * Un gesto desde el borde inferior los muestra
     * temporalmente.
     */
    /**
     * Activa o desactiva la supresión de los clics del teclado del sistema.
     *
     * La opción se liga al interruptor general de efectos de sonido de MyNotes:
     * si los sonidos de la app están desactivados, no alteramos el canal de
     * sistema. Si están activados y el IME es visible, intentamos mutear solo
     * STREAM_SYSTEM.
     */
    private fun setSystemKeyboardSoundSuppressionEnabled(enabled: Boolean) {
        suppressSystemKeyboardSounds = enabled
        updateSystemKeyboardSoundSuppression(wasImeVisible)
    }

    /**
     * Sincroniza el estado del canal de sistema con la visibilidad del IME.
     *
     * AudioManager controla un canal global del dispositivo, no un teclado
     * concreto. Por eso esta intervención dura únicamente mientras MyNotes
     * está en primer plano y el teclado está abierto. Cualquier fallo del OEM
     * o restricción del sistema se ignora para no afectar la estabilidad de la
     * aplicación.
     */
    private fun updateSystemKeyboardSoundSuppression(isImeVisible: Boolean) {
        if (!::audioManager.isInitialized) return

        val shouldMuteSystemStream =
            activityIsResumed && suppressSystemKeyboardSounds && isImeVisible

        if (shouldMuteSystemStream && !systemStreamMutedByMyNotes) {
            try {
                if (audioManager.isVolumeFixed) return

                systemStreamWasMutedBeforeIme =
                    audioManager.isStreamMute(AudioManager.STREAM_SYSTEM)

                if (!systemStreamWasMutedBeforeIme) {
                    audioManager.adjustStreamVolume(
                        AudioManager.STREAM_SYSTEM,
                        AudioManager.ADJUST_MUTE,
                        0
                    )
                }
                systemStreamMutedByMyNotes = true
            } catch (_: SecurityException) {
                systemStreamMutedByMyNotes = false
            } catch (_: RuntimeException) {
                systemStreamMutedByMyNotes = false
            }
        } else if (!shouldMuteSystemStream) {
            restoreSystemSoundStreamIfNeeded()
        }
    }

    /**
     * Devuelve STREAM_SYSTEM al estado previo a mostrar el teclado. Si el
     * usuario ya lo tenía silenciado, se deja exactamente así.
     */
    private fun restoreSystemSoundStreamIfNeeded() {
        if (!::audioManager.isInitialized || !systemStreamMutedByMyNotes) return

        try {
            if (!systemStreamWasMutedBeforeIme && !audioManager.isVolumeFixed) {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_SYSTEM,
                    AudioManager.ADJUST_UNMUTE,
                    0
                )
            }
        } catch (_: SecurityException) {
            // Algunos OEM restringen el mute global; nunca debe causar crash.
        } catch (_: RuntimeException) {
            // Protección adicional ante implementaciones de audio del fabricante.
        } finally {
            systemStreamMutedByMyNotes = false
            systemStreamWasMutedBeforeIme = false
        }
    }

    private fun installImeNavigationBarRecovery() {
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            updateSystemKeyboardSoundSuppression(isImeVisible)
            /*
             * No ocultamos la navegación mientras el teclado está abierto.
             * Solo actuamos en la transición visible -> oculto, que es el
             * caso en el que Android/Samsung deja los tres botones en pantalla.
             */
            if (wasImeVisible && !isImeVisible) {
                view.post {
                    applyAndroidNavigationBarPolicy()
                }
            }
            wasImeVisible = isImeVisible
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
        activityIsResumed = true
        updateSystemKeyboardSoundSuppression(wasImeVisible)
        ViewCompat.requestApplyInsets(window.decorView)
        /*
         * DisplayPerformanceController conserva el último perfil aplicado.
         * Reaplicamos esa preferencia al volver a primer plano sin duplicar
         * aquí ninguna regla de frecuencia de refresco.
         */
        DisplayPerformanceController.reapplyLastRequest(window)
        applyAndroidNavigationBarPolicy()
    }
    override fun onPause() {
        /*
         * Nunca dejamos STREAM_SYSTEM silenciado cuando MyNotes pierde el
         * primer plano. Esto evita afectar sonidos de otras aplicaciones si
         * el usuario cambia de app con el teclado todavía abierto.
         */
        activityIsResumed = false
        restoreSystemSoundStreamIfNeeded()
        super.onPause()
    }
    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        applyAndroidNavigationBarPolicy()
    }
    override fun onDestroy() {
        restoreSystemSoundStreamIfNeeded()
        DisplayPerformanceController.release(window)
        super.onDestroy()
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingShare(intent)
        handleWidgetIntent(intent)
        handleReminderIntent(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        /*
         * El sistema ya mostró Theme.MyNotes.Starting mientras
         * el proceso arrancaba. Ahora cambiamos al tema normal
         * antes de crear la Activity.
         */
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        handleIncomingShare(intent)
        handleWidgetIntent(intent)
        handleReminderIntent(intent)
        /*
         * La frecuencia de refresco se aplica únicamente cuando AppSettings
         * entrega performanceMode. MainActivity no conoce valores concretos
         * de Hz; toda esa política vive en DisplayPerformanceController.
         * Compose ya sincroniza el renderizado con VSYNC.
         */
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
            /*
             * Configuración de audio/hápticos separada de la sincronización
             * visual de recordatorios. Antes, cambiar paleta, intensidad,
             * tamaño de fuente o tema podía volver a ejecutar también la ruta
             * de SoundPool aunque el audio no hubiera cambiado.
             */
            SyncUiFeedback(context = this@MainActivity, settings = settings)

            /*
             * El mute temporal del clic del teclado solo depende del switch
             * maestro de sonidos. No se vuelve a tocar AudioManager por cambios
             * de volumen, paleta, tipografía o recordatorios.
             */
            LaunchedEffect(settings.soundEffectsEnabled) {
                setSystemKeyboardSoundSuppressionEnabled(settings.soundEffectsEnabled)
            }

            /*
             * Las preferencias de recordatorio sí dependen de su audio y de
             * los colores/tamaño usados por la notificación personalizada.
             * Se mantienen exactamente las mismas claves visuales de antes,
             * pero ya no arrastran una reconfiguración innecesaria del audio UI.
             */
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
            /*
             * En Configuración básica el tema claro/oscuro pertenece al
             * sistema del teléfono. Esto hace que MyNotes cambie en tiempo
             * real cuando Android cambia entre modo claro y oscuro.
             *
             * El modo avanzado conserva el interruptor manual existente y,
             * por tanto, sigue usando settings.darkMode.
             * Mientras el usuario todavía no ha elegido modo (primer inicio),
             * también seguimos al sistema para evitar un destello claro en un
             * teléfono configurado en oscuro.
             */
            
            val effectiveDarkTheme = settings.effectiveDarkTheme(systemDarkTheme)
            LaunchedEffect(effectiveDarkTheme) {
                applySystemBarAppearance(effectiveDarkTheme)
            }
            /*
             * El controlador traduce el perfil seleccionado a la frecuencia
             * adecuada y escoge el modo compatible sin cambiar
             * voluntariamente la resolución física.
             */
            SyncDisplayPerformance(window = window, performanceMode = settings.performanceMode)
            /*
             * ==========================================
             * NAVEGACIÓN
             * ==========================================
             */
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
            /*
             * null:
             * crear nota.
             *
             * Note:
             * editar nota existente.
             */
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
                if (clearShare) clearPendingShare()
                resetNavigation()
                action()
            }
            fun openEditor(note: Note? = null) = openExclusive { editingNote = note; showEditor = true }
            fun openDrawing() = openExclusive { showDrawing = true }
            fun openReminders() = openExclusive { createReminderOnOpen = false; showReminders = true }
            fun openNote(note: Note) = openExclusive(clearShare = false) { selectedNote = note }
            fun closeEditor() { editingNote = null; clearPendingShare(); showEditor = false }
            LaunchedEffect(pendingOpenReminders) {
                if (pendingOpenReminders) {
                    openReminders()
                    pendingOpenReminders = false
                }
            }
            /*
             * Acciones lanzadas desde los widgets de la pantalla de inicio.
             * Se consumen una sola vez para que una recomposición no vuelva a
             * abrir el editor o el detalle.
             */
            LaunchedEffect(pendingWidgetNewNote) {
                if (pendingWidgetNewNote) {
                    openEditor()
                    pendingWidgetNewNote = false
                }
            }

            LaunchedEffect(pendingWidgetNoteId) {
                val noteId = pendingWidgetNoteId ?: return@LaunchedEffect
                val note = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(applicationContext)
                        .noteDao()
                        .getNoteByIdOnce(noteId)
                }
                pendingWidgetNoteId = null

                if (note != null) {
                    clearPendingShare()
                    openNote(note)
                }
            }

            LaunchedEffect(widgetNavigationToken) {
                if (widgetNavigationToken > 0 &&
                    (pendingWidgetCollection != null || pendingWidgetSearch)
                ) {
                    clearPendingShare()
                    resetNavigation()
                }
            }

            /*
             * Cuando llega un enlace mediante Compartir, abrimos una
             * nueva nota con el texto recibido. También funciona si la
             * Activity ya estaba abierta gracias a onNewIntent().
             */
            LaunchedEffect(pendingSharedText, pendingSharedTitle) {
                if (!pendingSharedText.isNullOrBlank()) {
                    resetNavigation()
                    showEditor = true
                }
            }
            /*
             * ==========================================
             * BOTÓN BACK DE ANDROID
             * ==========================================
             *
             * Mientras estemos en Ajustes, Editor o
             * Detalle, Back regresa a "Mis notas".
             *
             * Solo cuando ya estamos en "Mis notas",
             * Android puede cerrar la aplicación.
             */
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
            /*
             * Guardamos la nota en el estado de navegación para que la
             * pantalla saliente conserve sus datos durante el Zoom Out.
             */
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
                    /*
                     * ==========================================
                     * AJUSTES
                     * ==========================================
                     */
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
                    /*
                     * ==========================================
                     * INFORMACIÓN DEL DESARROLLO
                     * ==========================================
                     */
                    AppDestination.DEVELOPMENT_INFO -> {
                        DevelopmentInfoScreen(settings = settings,
                            onOpenSourceCode = {
                                showSourceCodeInfo = true
                            },
                            onBack = {
                                showDevelopmentInfo = false
                            })
                    }
                    /*
                     * ==========================================
                     * MAPA DEL CÓDIGO FUENTE
                     * ==========================================
                     */
                    AppDestination.SOURCE_CODE_INFO -> {
                        SourceCodeInfoScreen(settings = settings, onBack = {
                            showSourceCodeInfo = false
                        })
                    }
                    /*
                     * ==========================================
                     * EDITOR
                     * ==========================================
                     */
                    AppDestination.EDITOR -> {
                        /*
                         * Si estamos editando, escuchamos los adjuntos
                         * que ya pertenecen a esa nota para mostrarlos
                         * dentro del editor.
                         */
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
                            initialTitle = screen.note?.title?: pendingSharedTitle.orEmpty(),
                            initialContent = screen.note?.content?: pendingSharedText.orEmpty(),
                            initialColor = screen.note?.color?: "default",
                            isEditing = screen.note != null,
                            existingAttachments = existingAttachments,
                            /*
                             * attachments contiene únicamente
                             * adjuntos NUEVOS agregados durante
                             * esta edición.
                             *
                             * List<PendingAttachment>
                             *
                             * y puede contener:
                             *
                             * image
                             * video
                             * audio
                             * voice
                             * file
                             */
                            onSave = {
                                    title, content, color, attachments, removedAttachments ->
                                val noteBeingEdited = screen.note
                                if (noteBeingEdited == null) {
                                    /*
                                     * ==========================
                                     * NUEVA NOTA
                                     * ==========================
                                     */
                                    noteViewModel.addNote(title = title,
                                            content = content,
                                            color = color,
                                            attachments = attachments)
                                } else {
                                    /*
                                     * ==========================
                                     * EDITAR NOTA
                                     * ==========================
                                     *
                                     * Conserva los adjuntos
                                     * anteriores y agrega
                                     * los nuevos.
                                     */
                                    noteViewModel.updateNote(note = noteBeingEdited,
                                            title = title,
                                            content = content,
                                            color = color,
                                            newAttachments = attachments)
                                    /*
                                     * Eliminamos únicamente los adjuntos
                                     * existentes que el usuario marcó con X.
                                     */
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
                    /*
                     * ==========================================
                     * DIBUJO
                     * ==========================================
                     */
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
                    /*
                     * ==========================================
                     * RECORDATORIOS
                     * ==========================================
                     */
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
                    /*
                     * ==========================================
                     * DETALLE DE NOTA
                     * ==========================================
                     */
                    AppDestination.DETAIL -> {
                        /*
                         * La lista de notas se observa únicamente mientras la
                         * pantalla de detalle la necesita. Así una escritura en
                         * Room no recompone Settings/Editor/Info cuando esas
                         * pantallas están activas.
                         */
                        val detailNotes by noteViewModel.notes.collectAsStateWithLifecycle()
                        /*
                         * Usamos la instancia más reciente de Room para
                         * reflejar Favorite / Pin / Category / Priority
                         * sin salir de la pantalla de detalle.
                         */
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
                    /*
                     * ==========================================
                     * PANTALLA PRINCIPAL
                     * ==========================================
                     */
                    AppDestination.NOTES -> {
                        /*
                         * Room solo se colecciona mientras la lista principal
                         * está en composición. Esto evita recomposiciones raíz
                         * innecesarias en Configuración, Editor e Información.
                         */
                        val notes by noteViewModel.notes.collectAsStateWithLifecycle()
                        NotesScreen(
                            notes = notes,
                            noteViewModel = noteViewModel,
                            settings = settings,
                            initialFilterKey = pendingWidgetCollection,
                            requestSearchFocus = pendingWidgetSearch,
                            widgetRequestToken = widgetNavigationToken,
                            /*
                             * Nueva nota.
                             */
                            onAddNote = { openEditor() },
                            onDrawNote = ::openDrawing,
                            onOpenReminders = ::openReminders,
                            onAddPdf = {
                                clearPendingShare()
                                startActivity(Intent(this@MainActivity, PdfLibraryActivity::class.java))
                            },
                            /*
                             * Ajustes.
                             */
                            onOpenSettings = {
                                showDevelopmentInfo = false
                                showDrawing = false
                                showReminders = false
                                showSettings = true
                            },
                            /*
                             * Abrir nota.
                             */
                            onOpenNote = ::openNote,
                            /*
                             * Editar desde ⋮.
                             */
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
