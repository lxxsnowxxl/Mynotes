# MainActivity.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/MainActivity.kt`  
**SHA-256 actual del archivo, sin modificar:** `cf0f367d632f2795190a87ed22ae2b1731cbd4f1e0e47a08bf6cb90d0a05ce04`  
**Líneas del código real:** 831  
**Estado respecto de la documentación anterior:** **archivo modificado desde la instantánea anterior**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

Activity raíz y orquestador de navegación/estado global de MyNotes. Conecta ViewModels, tema, pantallas Compose, recepción de contenido compartido, política de barras del sistema, rendimiento de pantalla e integración con el teclado del sistema.

**Cambios recientes cubiertos por esta revisión.** En los cambios recientes concentra la supresión temporal del sonido del teclado del sistema, conserva el audio propio de MyNotes, incorpora las pantallas de Información del desarrollo/Código fuente y mantiene la recuperación del modo inmersivo.

## 2. Package e imports

El package declarado es `com.example.mynotes`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **37 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`android.content.Context`, `android.content.Intent`, `android.content.res.Configuration`, `android.media.AudioManager`, `android.os.Build`, `android.os.Bundle`, `androidx.activity.ComponentActivity`, `androidx.activity.compose.BackHandler`, `androidx.activity.compose.setContent`, `androidx.activity.enableEdgeToEdge`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.core.view.ViewCompat`, `androidx.core.view.WindowCompat`, `androidx.core.view.WindowInsetsCompat`, `androidx.core.view.WindowInsetsControllerCompat`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`, `androidx.lifecycle.viewmodel.compose.viewModel`

### Proyecto MyNotes

`com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.performance.DisplayPerformanceController`, `com.example.mynotes.ui.NoteDetailScreen`, `com.example.mynotes.ui.NoteEditorScreen`, `com.example.mynotes.ui.NotesScreen`, `com.example.mynotes.ui.DevelopmentInfoScreen`, `com.example.mynotes.ui.SourceCodeInfoScreen`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`, `com.example.mynotes.ui.SettingsScreen`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.MyNotesTheme`, `com.example.mynotes.viewmodel.NoteViewModel`, `com.example.mynotes.viewmodel.SettingsViewModel`

### Java / Kotlin estándar

`java.util.Locale`

## 3. Restricciones, límites e invariantes detectables

- **Llamada segura `?.`: 9 aparición/apariciones.** evita desreferenciar receptores nulos; si el receptor es `null`, la cadena se corta de forma segura.
- **Elvis `?:`: 5 aparición/apariciones.** define un fallback explícito cuando el operando izquierdo es nulo.
- **Aserción `!!`: 4 aparición/apariciones.** convierte una suposición de no nulidad en una posible excepción si se incumple.
- **Guardias de API Android: 3 aparición/apariciones.** protegen llamadas cuya disponibilidad cambia según la versión de Android.
- **Estado Compose: 21 aparición/apariciones.** introduce estado observado por Compose y, por tanto, puntos potenciales de recomposición.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `AppDestination` — class, líneas 41–43

```kotlin
private enum class AppDestination {
    NOTES, SETTINGS, DEVELOPMENT_INFO, SOURCE_CODE_INFO, EDITOR, DETAIL
}
```

**Firma/entrada.** `private enum class AppDestination {`

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.2 `NavigationSnapshot` — class, líneas 45–831

```kotlin
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
    companion object {
        private const val LOCALE_PREFS = "locale_prefs"
        private const val LANGUAGE_KEY = "language"
        private const val DEFAULT_LANGUAGE = "es"
    }
    /*
     * Texto recibido mediante Compartir desde otras aplicaciones
     * (navegador, YouTube, Spotify, noticias, etc.).
     */
    private var pendingSharedText by
        mutableStateOf<String?>(null)
    private var pendingSharedTitle by
        mutableStateOf<String?>(null)
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
        val preferences = newBase.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)
        val language = preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        val localizedContext = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(localizedContext)
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
        getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE).edit().putString(LANGUAGE_KEY, language).commit()
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
    private fun applyAndroidNavigationBarPolicy() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        /*
         * En pantalla completa conservamos el modo inmersivo. En modo
         * multiventana (disponible desde Android 7) dejamos visible la barra
         * del sistema: ocultarla dentro de split-screen/desktop windowing
         * produce saltos de tamaño y controles inaccesibles en algunos OEM.
         */
        val isMultiWindow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode
        if (isMultiWindow) {
            controller.show(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        /*
         * En Android 7.0/7.1 no existe el modo de iconos oscuros para la
         * barra de navegación. Cuando el sistema la muestra usamos negro
         * para garantizar contraste con los botones blancos.
         */
        if (Build.VERSION.SDK_INT <
            Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = android.graphics.Color.BLACK
        }
    }
    private fun applySystemBarAppearance(darkMode: Boolean) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = !darkMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller.isAppearanceLightNavigationBars = !darkMode
        }
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
            val notes by noteViewModel.notes.collectAsStateWithLifecycle()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            LaunchedEffect(settings.soundEffectsEnabled, settings.soundEffectsVolume, settings.soundEffectsTheme,
                settings.hapticEffectsEnabled, settings.hapticEffectsIntensity, settings.hapticEffectsStyle) {
                UiSoundPlayer.configure(context = this@MainActivity, enabled = settings.soundEffectsEnabled,
                    volumePercent = settings.soundEffectsVolume, theme = settings.soundEffectsTheme,
                    hapticEnabled = settings.hapticEffectsEnabled, hapticIntensityPercent = settings.hapticEffectsIntensity,
                    hapticStyle = settings.hapticEffectsStyle)
                setSystemKeyboardSoundSuppressionEnabled(settings.soundEffectsEnabled)
            }
            LaunchedEffect(settings.darkMode) {
                applySystemBarAppearance(settings.darkMode)
            }
            /*
             * El controlador traduce el perfil seleccionado a la frecuencia
             * adecuada y escoge el modo compatible sin cambiar
             * voluntariamente la resolución física.
             */
            LaunchedEffect(settings.performanceMode) {
                DisplayPerformanceController.requestForPerformanceMode(window = window, performanceMode = settings.performanceMode)
            }
            /*
             * ==========================================
             * NAVEGACIÓN
             * ==========================================
             */
            var showEditor by remember {
                mutableStateOf(false)
            }
            var showSettings by remember {
                mutableStateOf(false)
            }
            /* Pantalla técnica secundaria abierta desde Configuración. */
            var showDevelopmentInfo by remember {
                mutableStateOf(false)
            }
            /* Subpantalla informativa con el mapa del código fuente del proyecto. */
            var showSourceCodeInfo by remember {
                mutableStateOf(false)
            }
            var selectedNote by remember {
                mutableStateOf<Note?>(null)
            }
            /*
             * null:
             * crear nota.
             *
             * Note:
             * editar nota existente.
             */
            var editingNote by remember {
                mutableStateOf<Note?>(null)
            }
            /*
             * Cuando llega un enlace mediante Compartir, abrimos una
             * nueva nota con el texto recibido. También funciona si la
             * Activity ya estaba abierta gracias a onNewIntent().
             */
            LaunchedEffect(pendingSharedText, pendingSharedTitle) {
                if (!pendingSharedText.isNullOrBlank()) {
                    showSourceCodeInfo = false
                    showDevelopmentInfo = false
                    showSettings = false
                    selectedNote = null
                    editingNote = null
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
            BackHandler(enabled = showSourceCodeInfo || showDevelopmentInfo || showSettings || showEditor || selectedNote != null) {
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
                        editingNote = null
                        clearPendingShare()
                        showEditor = false
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
            val currentScreen = when {
                    showSourceCodeInfo -> NavigationSnapshot(AppDestination.SOURCE_CODE_INFO)
                    showDevelopmentInfo -> NavigationSnapshot(AppDestination.DEVELOPMENT_INFO)
                    showSettings -> NavigationSnapshot(AppDestination.SETTINGS)
                    showEditor -> NavigationSnapshot(AppDestination.EDITOR, editingNote)
                    selectedNote != null -> NavigationSnapshot(AppDestination.DETAIL, selectedNote)
                    else -> NavigationSnapshot(AppDestination.NOTES)
                }
            MyNotesTheme(darkTheme = settings.darkMode,
                backgroundColor = settings.backgroundColor,
                backgroundToneIndex = settings.backgroundToneIndex,
                backgroundIntensity = settings.backgroundIntensity,
                surfacePanelIntensity = settings.surfacePanelIntensity,
                headerIntensity = settings.headerIntensity,
                /*
                 * auto adapta el texto al contraste del fondo; negro/blanco
                 * siguen siendo anulaciones manuales persistentes.
                 */
                textColor = settings.textColor,
                textOutlineEnabled = settings.textOutlineEnabled,
                accentColor = settings.accentColor) {
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
                            onDarkModeChange = {
                                settingsViewModel.setDarkMode(it)
                            },
                            onBackgroundColorChange = {
                                settingsViewModel.setBackgroundColor(it)
                            },
                            onBackgroundToneIndexChange = {
                                settingsViewModel.setBackgroundToneIndex(it)
                            },
                            onBackgroundIntensityChange = {
                                settingsViewModel.setBackgroundIntensity(it)
                            },
                            onSettingsPanelToneChange = {
                                settingsViewModel.setSettingsPanelTone(it)
                            },
                            onSurfacePanelIntensityChange = {
                                settingsViewModel.setSurfacePanelIntensity(it)
                            },
                            onHeaderIntensityChange = {
                                settingsViewModel.setHeaderIntensity(it)
                            },
                            onTextColorChange = {
                                settingsViewModel.setTextColor(it)
                            },
                            onTextOutlineEnabledChange = {
                                settingsViewModel.setTextOutlineEnabled(it)
                            },
                            onNoteUiTextColorChange = {
                                settingsViewModel.setNoteUiTextColor(it)
                            },
                            onSliderStyleChange = {
                                settingsViewModel.setSliderStyle(it)
                            },
                            onFontChange = {
                                settingsViewModel.setFont(it)
                            },
                            onFontSizeChange = {
                                settingsViewModel.setFontSize(it)
                            },
                            onSoundEffectsEnabledChange = {
                                settingsViewModel.setSoundEffectsEnabled(it)
                            },
                            onSoundEffectsVolumeChange = {
                                settingsViewModel.setSoundEffectsVolume(it)
                            },
                            onSoundEffectsThemeChange = {
                                settingsViewModel.setSoundEffectsTheme(it)
                            },
                            onHapticEffectsEnabledChange = {
                                settingsViewModel.setHapticEffectsEnabled(it)
                            },
                            onHapticEffectsIntensityChange = {
                                settingsViewModel.setHapticEffectsIntensity(it)
                            },
                            onHapticEffectsStyleChange = {
                                settingsViewModel.setHapticEffectsStyle(it)
                            },
                            onLanguageChange = { language ->
                                settingsViewModel.setLanguage(language)
                                changeAppLanguage(language)
                            },
                            onGridColumnsChange = {
                                settingsViewModel.setGridColumns(it)
                            },
                            onProfileImageUriChange = {
                                settingsViewModel.setProfileImageUri(it)
                            },
                            onProfileImageSizeChange = {
                                settingsViewModel.setProfileImageSize(it)
                            },
                            onIconStyleChange = {
                                settingsViewModel.setIconStyle(it)
                            },
                            onIconSizeChange = {
                                settingsViewModel.setIconSize(it)
                            },
                            onAccentColorChange = {
                                settingsViewModel.setAccentColor(it)
                            },
                            onNoteCardCornerRadiusChange = {
                                settingsViewModel.setNoteCardCornerRadius(it)
                            },
                            onNoteCardElevationChange = {
                                settingsViewModel.setNoteCardElevation(it)
                            },
                            onNoteCardPaddingChange = {
                                settingsViewModel.setNoteCardPadding(it)
                            },
                            onNoteCardImageHeightChange = {
                                settingsViewModel.setNoteCardImageHeight(it)
                            },
                            onNoteTitleMaxLinesChange = {
                                settingsViewModel.setNoteTitleMaxLines(it)
                            },
                            onNoteContentMaxLinesChange = {
                                settingsViewModel.setNoteContentMaxLines(it)
                            },
                            onNoteLineSpacingChange = {
                                settingsViewModel.setNoteLineSpacing(it)
                            },
                            onShowNoteDateChange = {
                                settingsViewModel.setShowNoteDate(it)
                            },
                            onShowCategoryChipChange = {
                                settingsViewModel.setShowCategoryChip(it)
                            },
                            onShowFavoriteIconChange = {
                                settingsViewModel.setShowFavoriteIcon(it)
                            },
                            onFabSizeChange = {
                                settingsViewModel.setFabSize(it)
                            },
                            onOptionMenuOrderChange = {
                                settingsViewModel.setOptionMenuOrder(it)
                            },
                            onOptionMenuHiddenItemsChange = {
                                settingsViewModel.setOptionMenuHiddenItems(it)
                            },
                            onOptionMenuShowIconsChange = {
                                settingsViewModel.setOptionMenuShowIcons(it)
                            },
                            onOptionMenuTextColorChange = {
                                settingsViewModel.setOptionMenuTextColor(it)
                            },
                            onOptionMenuOpacityChange = {
                                settingsViewModel.setOptionMenuOpacity(it)
                            },
                            onPriorityMenuHiddenItemsChange = {
                                settingsViewModel.setPriorityMenuHiddenItems(it)
                            },
                            onColorMenuHiddenItemsChange = {
                                settingsViewModel.setColorMenuHiddenItems(it)
                            },
                            onResetOptionMenu = {
                                settingsViewModel.resetOptionMenuSettings()
                            },
                            onPerformanceModeChange = {
                                settingsViewModel.setPerformanceMode(it)
                            },
                            onAnimationsEnabledChange = {
                                settingsViewModel.setAnimationsEnabled(it)
                            },
                            onAnimationStyleChange = {
                                settingsViewModel.setAnimationStyle(it)
                            },
                            onAnimationEasingChange = {
                                settingsViewModel.setAnimationEasing(it)
                            },
                            onAnimationSpeedChange = {
                                settingsViewModel.setAnimationSpeed(it)
                            },
                            onAnimationIntensityChange = {
                                settingsViewModel.setAnimationIntensity(it)
                            },
                            onOpenDevelopmentInfo = {
                                showDevelopmentInfo = true
                            },
                            onBack = {
                                showSettings = false
                            })
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
                                editingNote = null
                                clearPendingShare()
                                showEditor = false
                            },
                            onCancel = {
                                editingNote = null
                                clearPendingShare()
                                showEditor = false
                            })
                        }
                    }
                    /*
                     * ==========================================
                     * DETALLE DE NOTA
                     * ==========================================
                     */
                    AppDestination.DETAIL -> {
                        /*
                         * Usamos la instancia más reciente de Room para
                         * reflejar Favorite / Pin / Category / Priority
                         * sin salir de la pantalla de detalle.
                         */
                        val currentSelectedNote = notes.firstOrNull {
                                    it.id == screen.note!!.id
                                }?: screen.note!!
                        NoteDetailScreen(
                            note = currentSelectedNote,
                            noteViewModel = noteViewModel,
                            settings = settings,
                            onBack = {
                                selectedNote = null
                            },
                            onEdit = {
                                    note ->
                                clearPendingShare()
                                editingNote = note
                                selectedNote = null
                                showEditor = true
                            })
                    }
                    /*
                     * ==========================================
                     * PANTALLA PRINCIPAL
                     * ==========================================
                     */
                    AppDestination.NOTES -> {
                        NotesScreen(
                            notes = notes,
                            noteViewModel = noteViewModel,
                            settings = settings,
                            /*
                             * Nueva nota.
                             */
                            onAddNote = {
                                clearPendingShare()
                                editingNote = null
                                showEditor = true
                            },
                            /*
                             * Ajustes.
                             */
                            onOpenSettings = {
                                showDevelopmentInfo = false
                                showSettings = true
                            },
                            /*
                             * Abrir nota.
                             */
                            onOpenNote = { note ->
                                selectedNote = note
                            },
                            /*
                             * Editar desde ⋮.
                             */
                            onEditNote = { note ->
                                clearPendingShare()
                                editingNote = note
                                showEditor = true
                            })
                    }
                }
                }
            }
        }
    }
}
```

**Firma/entrada.** `private data class NavigationSnapshot(val destination: AppDestination, val note: Note? = null) class MainActivity : ComponentActivity() {`

**Parámetros.**
- `val destination: AppDestination` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val note: Note? = null) class MainActivity : ComponentActivity(` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; interactúa con el subsistema de audio de Android; observa insets/IME y por ello depende del estado de la ventana; ramifica o parametriza comportamiento según el perfil de rendimiento; produce navegación/interacción externa mediante Intent; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.3 `MainActivity` — class, líneas 47–831

```kotlin
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
    companion object {
        private const val LOCALE_PREFS = "locale_prefs"
        private const val LANGUAGE_KEY = "language"
        private const val DEFAULT_LANGUAGE = "es"
    }
    /*
     * Texto recibido mediante Compartir desde otras aplicaciones
     * (navegador, YouTube, Spotify, noticias, etc.).
     */
    private var pendingSharedText by
        mutableStateOf<String?>(null)
    private var pendingSharedTitle by
        mutableStateOf<String?>(null)
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
        val preferences = newBase.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)
        val language = preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        val localizedContext = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(localizedContext)
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
        getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE).edit().putString(LANGUAGE_KEY, language).commit()
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
    private fun applyAndroidNavigationBarPolicy() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        /*
         * En pantalla completa conservamos el modo inmersivo. En modo
         * multiventana (disponible desde Android 7) dejamos visible la barra
         * del sistema: ocultarla dentro de split-screen/desktop windowing
         * produce saltos de tamaño y controles inaccesibles en algunos OEM.
         */
        val isMultiWindow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode
        if (isMultiWindow) {
            controller.show(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        /*
         * En Android 7.0/7.1 no existe el modo de iconos oscuros para la
         * barra de navegación. Cuando el sistema la muestra usamos negro
         * para garantizar contraste con los botones blancos.
         */
        if (Build.VERSION.SDK_INT <
            Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = android.graphics.Color.BLACK
        }
    }
    private fun applySystemBarAppearance(darkMode: Boolean) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = !darkMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller.isAppearanceLightNavigationBars = !darkMode
        }
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
            val notes by noteViewModel.notes.collectAsStateWithLifecycle()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            LaunchedEffect(settings.soundEffectsEnabled, settings.soundEffectsVolume, settings.soundEffectsTheme,
                settings.hapticEffectsEnabled, settings.hapticEffectsIntensity, settings.hapticEffectsStyle) {
                UiSoundPlayer.configure(context = this@MainActivity, enabled = settings.soundEffectsEnabled,
                    volumePercent = settings.soundEffectsVolume, theme = settings.soundEffectsTheme,
                    hapticEnabled = settings.hapticEffectsEnabled, hapticIntensityPercent = settings.hapticEffectsIntensity,
                    hapticStyle = settings.hapticEffectsStyle)
                setSystemKeyboardSoundSuppressionEnabled(settings.soundEffectsEnabled)
            }
            LaunchedEffect(settings.darkMode) {
                applySystemBarAppearance(settings.darkMode)
            }
            /*
             * El controlador traduce el perfil seleccionado a la frecuencia
             * adecuada y escoge el modo compatible sin cambiar
             * voluntariamente la resolución física.
             */
            LaunchedEffect(settings.performanceMode) {
                DisplayPerformanceController.requestForPerformanceMode(window = window, performanceMode = settings.performanceMode)
            }
            /*
             * ==========================================
             * NAVEGACIÓN
             * ==========================================
             */
            var showEditor by remember {
                mutableStateOf(false)
            }
            var showSettings by remember {
                mutableStateOf(false)
            }
            /* Pantalla técnica secundaria abierta desde Configuración. */
            var showDevelopmentInfo by remember {
                mutableStateOf(false)
            }
            /* Subpantalla informativa con el mapa del código fuente del proyecto. */
            var showSourceCodeInfo by remember {
                mutableStateOf(false)
            }
            var selectedNote by remember {
                mutableStateOf<Note?>(null)
            }
            /*
             * null:
             * crear nota.
             *
             * Note:
             * editar nota existente.
             */
            var editingNote by remember {
                mutableStateOf<Note?>(null)
            }
            /*
             * Cuando llega un enlace mediante Compartir, abrimos una
             * nueva nota con el texto recibido. También funciona si la
             * Activity ya estaba abierta gracias a onNewIntent().
             */
            LaunchedEffect(pendingSharedText, pendingSharedTitle) {
                if (!pendingSharedText.isNullOrBlank()) {
                    showSourceCodeInfo = false
                    showDevelopmentInfo = false
                    showSettings = false
                    selectedNote = null
                    editingNote = null
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
            BackHandler(enabled = showSourceCodeInfo || showDevelopmentInfo || showSettings || showEditor || selectedNote != null) {
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
                        editingNote = null
                        clearPendingShare()
                        showEditor = false
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
            val currentScreen = when {
                    showSourceCodeInfo -> NavigationSnapshot(AppDestination.SOURCE_CODE_INFO)
                    showDevelopmentInfo -> NavigationSnapshot(AppDestination.DEVELOPMENT_INFO)
                    showSettings -> NavigationSnapshot(AppDestination.SETTINGS)
                    showEditor -> NavigationSnapshot(AppDestination.EDITOR, editingNote)
                    selectedNote != null -> NavigationSnapshot(AppDestination.DETAIL, selectedNote)
                    else -> NavigationSnapshot(AppDestination.NOTES)
                }
            MyNotesTheme(darkTheme = settings.darkMode,
                backgroundColor = settings.backgroundColor,
                backgroundToneIndex = settings.backgroundToneIndex,
                backgroundIntensity = settings.backgroundIntensity,
                surfacePanelIntensity = settings.surfacePanelIntensity,
                headerIntensity = settings.headerIntensity,
                /*
                 * auto adapta el texto al contraste del fondo; negro/blanco
                 * siguen siendo anulaciones manuales persistentes.
                 */
                textColor = settings.textColor,
                textOutlineEnabled = settings.textOutlineEnabled,
                accentColor = settings.accentColor) {
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
                            onDarkModeChange = {
                                settingsViewModel.setDarkMode(it)
                            },
                            onBackgroundColorChange = {
                                settingsViewModel.setBackgroundColor(it)
                            },
                            onBackgroundToneIndexChange = {
                                settingsViewModel.setBackgroundToneIndex(it)
                            },
                            onBackgroundIntensityChange = {
                                settingsViewModel.setBackgroundIntensity(it)
                            },
                            onSettingsPanelToneChange = {
                                settingsViewModel.setSettingsPanelTone(it)
                            },
                            onSurfacePanelIntensityChange = {
                                settingsViewModel.setSurfacePanelIntensity(it)
                            },
                            onHeaderIntensityChange = {
                                settingsViewModel.setHeaderIntensity(it)
                            },
                            onTextColorChange = {
                                settingsViewModel.setTextColor(it)
                            },
                            onTextOutlineEnabledChange = {
                                settingsViewModel.setTextOutlineEnabled(it)
                            },
                            onNoteUiTextColorChange = {
                                settingsViewModel.setNoteUiTextColor(it)
                            },
                            onSliderStyleChange = {
                                settingsViewModel.setSliderStyle(it)
                            },
                            onFontChange = {
                                settingsViewModel.setFont(it)
                            },
                            onFontSizeChange = {
                                settingsViewModel.setFontSize(it)
                            },
                            onSoundEffectsEnabledChange = {
                                settingsViewModel.setSoundEffectsEnabled(it)
                            },
                            onSoundEffectsVolumeChange = {
                                settingsViewModel.setSoundEffectsVolume(it)
                            },
                            onSoundEffectsThemeChange = {
                                settingsViewModel.setSoundEffectsTheme(it)
                            },
                            onHapticEffectsEnabledChange = {
                                settingsViewModel.setHapticEffectsEnabled(it)
                            },
                            onHapticEffectsIntensityChange = {
                                settingsViewModel.setHapticEffectsIntensity(it)
                            },
                            onHapticEffectsStyleChange = {
                                settingsViewModel.setHapticEffectsStyle(it)
                            },
                            onLanguageChange = { language ->
                                settingsViewModel.setLanguage(language)
                                changeAppLanguage(language)
                            },
                            onGridColumnsChange = {
                                settingsViewModel.setGridColumns(it)
                            },
                            onProfileImageUriChange = {
                                settingsViewModel.setProfileImageUri(it)
                            },
                            onProfileImageSizeChange = {
                                settingsViewModel.setProfileImageSize(it)
                            },
                            onIconStyleChange = {
                                settingsViewModel.setIconStyle(it)
                            },
                            onIconSizeChange = {
                                settingsViewModel.setIconSize(it)
                            },
                            onAccentColorChange = {
                                settingsViewModel.setAccentColor(it)
                            },
                            onNoteCardCornerRadiusChange = {
                                settingsViewModel.setNoteCardCornerRadius(it)
                            },
                            onNoteCardElevationChange = {
                                settingsViewModel.setNoteCardElevation(it)
                            },
                            onNoteCardPaddingChange = {
                                settingsViewModel.setNoteCardPadding(it)
                            },
                            onNoteCardImageHeightChange = {
                                settingsViewModel.setNoteCardImageHeight(it)
                            },
                            onNoteTitleMaxLinesChange = {
                                settingsViewModel.setNoteTitleMaxLines(it)
                            },
                            onNoteContentMaxLinesChange = {
                                settingsViewModel.setNoteContentMaxLines(it)
                            },
                            onNoteLineSpacingChange = {
                                settingsViewModel.setNoteLineSpacing(it)
                            },
                            onShowNoteDateChange = {
                                settingsViewModel.setShowNoteDate(it)
                            },
                            onShowCategoryChipChange = {
                                settingsViewModel.setShowCategoryChip(it)
                            },
                            onShowFavoriteIconChange = {
                                settingsViewModel.setShowFavoriteIcon(it)
                            },
                            onFabSizeChange = {
                                settingsViewModel.setFabSize(it)
                            },
                            onOptionMenuOrderChange = {
                                settingsViewModel.setOptionMenuOrder(it)
                            },
                            onOptionMenuHiddenItemsChange = {
                                settingsViewModel.setOptionMenuHiddenItems(it)
                            },
                            onOptionMenuShowIconsChange = {
                                settingsViewModel.setOptionMenuShowIcons(it)
                            },
                            onOptionMenuTextColorChange = {
                                settingsViewModel.setOptionMenuTextColor(it)
                            },
                            onOptionMenuOpacityChange = {
                                settingsViewModel.setOptionMenuOpacity(it)
                            },
                            onPriorityMenuHiddenItemsChange = {
                                settingsViewModel.setPriorityMenuHiddenItems(it)
                            },
                            onColorMenuHiddenItemsChange = {
                                settingsViewModel.setColorMenuHiddenItems(it)
                            },
                            onResetOptionMenu = {
                                settingsViewModel.resetOptionMenuSettings()
                            },
                            onPerformanceModeChange = {
                                settingsViewModel.setPerformanceMode(it)
                            },
                            onAnimationsEnabledChange = {
                                settingsViewModel.setAnimationsEnabled(it)
                            },
                            onAnimationStyleChange = {
                                settingsViewModel.setAnimationStyle(it)
                            },
                            onAnimationEasingChange = {
                                settingsViewModel.setAnimationEasing(it)
                            },
                            onAnimationSpeedChange = {
                                settingsViewModel.setAnimationSpeed(it)
                            },
                            onAnimationIntensityChange = {
                                settingsViewModel.setAnimationIntensity(it)
                            },
                            onOpenDevelopmentInfo = {
                                showDevelopmentInfo = true
                            },
                            onBack = {
                                showSettings = false
                            })
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
                                editingNote = null
                                clearPendingShare()
                                showEditor = false
                            },
                            onCancel = {
                                editingNote = null
                                clearPendingShare()
                                showEditor = false
                            })
                        }
                    }
                    /*
                     * ==========================================
                     * DETALLE DE NOTA
                     * ==========================================
                     */
                    AppDestination.DETAIL -> {
                        /*
                         * Usamos la instancia más reciente de Room para
                         * reflejar Favorite / Pin / Category / Priority
                         * sin salir de la pantalla de detalle.
                         */
                        val currentSelectedNote = notes.firstOrNull {
                                    it.id == screen.note!!.id
                                }?: screen.note!!
                        NoteDetailScreen(
                            note = currentSelectedNote,
                            noteViewModel = noteViewModel,
                            settings = settings,
                            onBack = {
                                selectedNote = null
                            },
                            onEdit = {
                                    note ->
                                clearPendingShare()
                                editingNote = note
                                selectedNote = null
                                showEditor = true
                            })
                    }
                    /*
                     * ==========================================
                     * PANTALLA PRINCIPAL
                     * ==========================================
                     */
                    AppDestination.NOTES -> {
                        NotesScreen(
                            notes = notes,
                            noteViewModel = noteViewModel,
                            settings = settings,
                            /*
                             * Nueva nota.
                             */
                            onAddNote = {
                                clearPendingShare()
                                editingNote = null
                                showEditor = true
                            },
                            /*
                             * Ajustes.
                             */
                            onOpenSettings = {
                                showDevelopmentInfo = false
                                showSettings = true
                            },
                            /*
                             * Abrir nota.
                             */
                            onOpenNote = { note ->
                                selectedNote = note
                            },
                            /*
                             * Editar desde ⋮.
                             */
                            onEditNote = { note ->
                                clearPendingShare()
                                editingNote = note
                                showEditor = true
                            })
                    }
                }
                }
            }
        }
    }
}
```

**Firma/entrada.** `class MainActivity : ComponentActivity() {`

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; interactúa con el subsistema de audio de Android; observa insets/IME y por ello depende del estado de la ventana; ramifica o parametriza comportamiento según el perfil de rendimiento; produce navegación/interacción externa mediante Intent; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.4 `handleIncomingShare` — fun, líneas 84–94

```kotlin
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
```

**Firma/entrada.** `private fun handleIncomingShare(incomingIntent: Intent?) {`

**Parámetros.**
- `incomingIntent: Intent?` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.5 `clearPendingShare` — fun, líneas 95–98

```kotlin
    private fun clearPendingShare() {
        pendingSharedText = null
        pendingSharedTitle = null
    }
```

**Firma/entrada.** `private fun clearPendingShare() {`

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.6 `changeAppLanguage` — fun, líneas 119–130

```kotlin
    private fun changeAppLanguage(language: String) {
        /*
         * Guardamos también en SharedPreferences porque
         * attachBaseContext() ocurre antes de que DataStore
         * pueda entregar AppSettings.
         *
         * commit() es intencional: necesitamos que el idioma
         * ya esté guardado antes de recreate().
         */
        getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE).edit().putString(LANGUAGE_KEY, language).commit()
        recreate()
    }
```

**Firma/entrada.** `private fun changeAppLanguage(language: String) {`

**Parámetros.**
- `language: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.7 `setSystemKeyboardSoundSuppressionEnabled` — fun, líneas 148–151

```kotlin
    private fun setSystemKeyboardSoundSuppressionEnabled(enabled: Boolean) {
        suppressSystemKeyboardSounds = enabled
        updateSystemKeyboardSoundSuppression(wasImeVisible)
    }
```

**Firma/entrada.** `private fun setSystemKeyboardSoundSuppressionEnabled(enabled: Boolean) {`

**Parámetros.**
- `enabled: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.8 `updateSystemKeyboardSoundSuppression` — fun, líneas 162–191

```kotlin
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
```

**Firma/entrada.** `private fun updateSystemKeyboardSoundSuppression(isImeVisible: Boolean) {`

**Parámetros.**
- `isImeVisible: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** interactúa con el subsistema de audio de Android.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.9 `restoreSystemSoundStreamIfNeeded` — fun, líneas 197–216

```kotlin
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
```

**Firma/entrada.** `private fun restoreSystemSoundStreamIfNeeded() {`

**Funcionamiento observable.** interactúa con el subsistema de audio de Android.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.10 `installImeNavigationBarRecovery` — fun, líneas 218–236

```kotlin
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
```

**Firma/entrada.** `private fun installImeNavigationBarRecovery() {`

**Funcionamiento observable.** observa insets/IME y por ello depende del estado de la ventana.

### 4.11 `applyAndroidNavigationBarPolicy` — fun, líneas 237–262

```kotlin
    private fun applyAndroidNavigationBarPolicy() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        /*
         * En pantalla completa conservamos el modo inmersivo. En modo
         * multiventana (disponible desde Android 7) dejamos visible la barra
         * del sistema: ocultarla dentro de split-screen/desktop windowing
         * produce saltos de tamaño y controles inaccesibles en algunos OEM.
         */
        val isMultiWindow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode
        if (isMultiWindow) {
            controller.show(WindowInsetsCompat.Type.navigationBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        /*
         * En Android 7.0/7.1 no existe el modo de iconos oscuros para la
         * barra de navegación. Cuando el sistema la muestra usamos negro
         * para garantizar contraste con los botones blancos.
         */
        if (Build.VERSION.SDK_INT <
            Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            window.navigationBarColor = android.graphics.Color.BLACK
        }
    }
```

**Firma/entrada.** `private fun applyAndroidNavigationBarPolicy() {`

**Funcionamiento observable.** observa insets/IME y por ello depende del estado de la ventana.

### 4.12 `applySystemBarAppearance` — fun, líneas 263–269

```kotlin
    private fun applySystemBarAppearance(darkMode: Boolean) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = !darkMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            controller.isAppearanceLightNavigationBars = !darkMode
        }
    }
```

**Firma/entrada.** `private fun applySystemBarAppearance(darkMode: Boolean) {`

**Parámetros.**
- `darkMode: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

## 5. Variables y propiedades, una por una

Se detectaron **38 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 53 | `wasImeVisible` | `private var wasImeVisible: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `private var wasImeVisible = false` |
| 66 | `audioManager` | `private lateinit var audioManager: A` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `private lateinit var audioManager: AudioManager` |
| 67 | `suppressSystemKeyboardSounds` | `private var suppressSystemKeyboardSounds: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `private var suppressSystemKeyboardSounds = false` |
| 68 | `systemStreamMutedByMyNotes` | `private var systemStreamMutedByMyNotes: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `private var systemStreamMutedByMyNotes = false` |
| 69 | `systemStreamWasMutedBeforeIme` | `private var systemStreamWasMutedBeforeIme: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `private var systemStreamWasMutedBeforeIme = false` |
| 70 | `activityIsResumed` | `private var activityIsResumed: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `private var activityIsResumed = false` |
| 72 | `LOCALE_PREFS` | `private const val LOCALE_PREFS: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `private const val LOCALE_PREFS = "locale_prefs"` |
| 73 | `LANGUAGE_KEY` | `private const val LANGUAGE_KEY: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private const val LANGUAGE_KEY = "language"` |
| 74 | `DEFAULT_LANGUAGE` | `private const val DEFAULT_LANGUAGE: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private const val DEFAULT_LANGUAGE = "es"` |
| 80 | `pendingSharedText` | `private var pendingSharedText: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `private var pendingSharedText by` |
| 82 | `pendingSharedTitle` | `private var pendingSharedTitle: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `private var pendingSharedTitle by` |
| 88 | `sharedText` | `local/pública por contexto val sharedText: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val sharedText = incomingIntent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.trim().orEmpty()` |
| 110 | `preferences` | `local/pública por contexto val preferences: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val preferences = newBase.getSharedPreferences(LOCALE_PREFS, Context.MODE_PRIVATE)` |
| 111 | `language` | `local/pública por contexto val language: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val language = preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE` |
| 112 | `locale` | `local/pública por contexto val locale: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val locale = Locale.forLanguageTag(language)` |
| 114 | `configuration` | `local/pública por contexto val configuration: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val configuration = Configuration(newBase.resources.configuration)` |
| 116 | `localizedContext` | `local/pública por contexto val localizedContext: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val localizedContext = newBase.createConfigurationContext(configuration)` |
| 165 | `shouldMuteSystemStream` | `local/pública por contexto val shouldMuteSystemStream: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val shouldMuteSystemStream =` |
| 220 | `isImeVisible` | `local/pública por contexto val isImeVisible: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())` |
| 238 | `controller` | `local/pública por contexto val controller: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val controller = WindowCompat.getInsetsController(window, window.decorView)` |
| 245 | `isMultiWindow` | `local/pública por contexto val isMultiWindow: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val isMultiWindow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode` |
| 264 | `controller` | `local/pública por contexto val controller: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val controller = WindowCompat.getInsetsController(window, window.decorView)` |
| 333 | `noteViewModel` | `local/pública por contexto val noteViewModel: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val noteViewModel:` |
| 335 | `settingsViewModel` | `local/pública por contexto val settingsViewModel: inferido` | `val` fija la referencia después de inicializarla; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `val settingsViewModel:` |
| 337 | `notes` | `local/pública por contexto val notes: inferido` | `val` fija la referencia después de inicializarla; adapta un Flow/StateFlow a estado observable por Compose. **Inicialización visible:** `val notes by noteViewModel.notes.collectAsStateWithLifecycle()` |
| 338 | `settings` | `local/pública por contexto val settings: inferido` | `val` fija la referencia después de inicializarla; adapta un Flow/StateFlow a estado observable por Compose; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `val settings by settingsViewModel.settings.collectAsStateWithLifecycle()` |
| 363 | `showEditor` | `local/pública por contexto var showEditor: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var showEditor by remember {` |
| 366 | `showSettings` | `local/pública por contexto var showSettings: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `var showSettings by remember {` |
| 370 | `showDevelopmentInfo` | `local/pública por contexto var showDevelopmentInfo: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var showDevelopmentInfo by remember {` |
| 374 | `showSourceCodeInfo` | `local/pública por contexto var showSourceCodeInfo: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var showSourceCodeInfo by remember {` |
| 377 | `selectedNote` | `local/pública por contexto var selectedNote: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var selectedNote by remember {` |
| 387 | `editingNote` | `local/pública por contexto var editingNote: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `var editingNote by remember {` |
| 441 | `currentScreen` | `local/pública por contexto val currentScreen: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val currentScreen = when {` |
| 674 | `existingAttachments` | `local/pública por contexto val existingAttachments: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val existingAttachments:` |
| 676 | `attachmentsFlow` | `local/pública por contexto val attachmentsFlow: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val attachmentsFlow = remember(screen.note!!.id) {` |
| 679 | `currentAttachments` | `local/pública por contexto val currentAttachments: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val currentAttachments by` |
| 711 | `noteBeingEdited` | `local/pública por contexto val noteBeingEdited: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val noteBeingEdited = screen.note` |
| 768 | `currentSelectedNote` | `local/pública por contexto val currentSelectedNote: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val currentSelectedNote = notes.firstOrNull {` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **61 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 41–43 | 0 | ámbito/lambda anónima | `private enum class AppDestination {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 47–831 | 0 | ámbito/lambda anónima | `class MainActivity : ComponentActivity() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 71–75 | 1 | ámbito/lambda anónima | `companion object {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 84–94 | 1 | ámbito/lambda anónima | `private fun handleIncomingShare(incomingIntent: Intent?) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 85–87 | 2 | condición `if` | `if (incomingIntent?.action != Intent.ACTION_SEND) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 89–91 | 2 | condición `if` | `if (sharedText.isBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 93–93 | 2 | ámbito/lambda anónima | `pendingSharedTitle = incomingIntent.getCharSequenceExtra(Intent.EXTRA_SUBJECT)?.toString()?.trim()?.takeIf { it.isNotBlank() }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 95–98 | 1 | ámbito/lambda anónima | `private fun clearPendingShare() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 109–118 | 1 | ámbito/lambda anónima | `override fun attachBaseContext(newBase: Context) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 119–130 | 1 | ámbito/lambda anónima | `private fun changeAppLanguage(language: String) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 148–151 | 1 | ámbito/lambda anónima | `private fun setSystemKeyboardSoundSuppressionEnabled(enabled: Boolean) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 162–191 | 1 | ámbito/lambda anónima | `private fun updateSystemKeyboardSoundSuppression(isImeVisible: Boolean) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 168–188 | 2 | condición `if` | `if (shouldMuteSystemStream && !systemStreamMutedByMyNotes) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 169–183 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 175–181 | 4 | condición `if` | `if (!systemStreamWasMutedBeforeIme) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 183–185 | 3 | `catch` / recuperación de error | `} catch (_: SecurityException) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 185–187 | 3 | `catch` / recuperación de error | `} catch (_: RuntimeException) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 188–190 | 2 | condición `if` | `} else if (!shouldMuteSystemStream) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 197–216 | 1 | ámbito/lambda anónima | `private fun restoreSystemSoundStreamIfNeeded() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 200–208 | 2 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 201–207 | 3 | condición `if` | `if (!systemStreamWasMutedBeforeIme && !audioManager.isVolumeFixed) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 208–210 | 2 | `catch` / recuperación de error | `} catch (_: SecurityException) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 210–212 | 2 | `catch` / recuperación de error | `} catch (_: RuntimeException) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 212–215 | 2 | `finally` / limpieza garantizada | `} finally {` | Se ejecuta tanto en éxito como en error y suele usarse para liberar recursos que no deben quedar abiertos. |
| 218–236 | 1 | ámbito/lambda anónima | `private fun installImeNavigationBarRecovery() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 219–234 | 2 | ámbito/lambda anónima | `ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 227–231 | 3 | condición `if` | `if (wasImeVisible && !isImeVisible) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 237–262 | 1 | ámbito/lambda anónima | `private fun applyAndroidNavigationBarPolicy() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 246–248 | 2 | condición `if` | `if (isMultiWindow) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 248–251 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 258–261 | 2 | ámbito/lambda anónima | `Build.VERSION_CODES.O) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 263–269 | 1 | ámbito/lambda anónima | `private fun applySystemBarAppearance(darkMode: Boolean) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 266–268 | 2 | condición `if` | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 270–275 | 1 | ámbito/lambda anónima | `override fun onWindowFocusChanged(hasFocus: Boolean) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 272–274 | 2 | condición `if` | `if (hasFocus) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 276–288 | 1 | ámbito/lambda anónima | `override fun onResume() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 289–298 | 1 | ámbito/lambda anónima | `override fun onPause() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 299–302 | 1 | ámbito/lambda anónima | `override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 303–307 | 1 | ámbito/lambda anónima | `override fun onDestroy() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 308–312 | 1 | ámbito/lambda anónima | `override fun onNewIntent(intent: Intent) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 313–830 | 1 | ámbito/lambda anónima | `override fun onCreate(savedInstanceState: Bundle?) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 332–829 | 2 | ámbito/lambda anónima | `setContent {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 340–346 | 3 | ámbito/lambda anónima | `settings.hapticEffectsEnabled, settings.hapticEffectsIntensity, settings.hapticEffectsStyle) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 347–349 | 3 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.darkMode) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 355–357 | 3 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(settings.performanceMode) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 363–365 | 3 | `remember` / memoria de composición | `var showEditor by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 366–368 | 3 | `remember` / memoria de composición | `var showSettings by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 370–372 | 3 | `remember` / memoria de composición | `var showDevelopmentInfo by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 374–376 | 3 | `remember` / memoria de composición | `var showSourceCodeInfo by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 377–379 | 3 | `remember` / memoria de composición | `var selectedNote by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 387–389 | 3 | `remember` / memoria de composición | `var editingNote by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 395–404 | 3 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(pendingSharedText, pendingSharedTitle) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 396–403 | 4 | condición `if` | `if (!pendingSharedText.isNullOrBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 416–436 | 3 | ámbito/lambda anónima | `BackHandler(enabled = showSourceCodeInfo \|\| showDevelopmentInfo \|\| showSettings \|\| showEditor \|\| selectedNote != null) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 441–448 | 3 | ámbito/lambda anónima | `val currentScreen = when {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 461–828 | 3 | ámbito/lambda anónima | `accentColor = settings.accentColor) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 469–826 | 5 | selección `when` | `when (screen.destination) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 675–682 | 7 | condición `if` | `List<Attachment> = if (screen.note != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 676–678 | 8 | `remember` / memoria de composición | `val attachmentsFlow = remember(screen.note!!.id) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 712–722 | 9 | condición `if` | `if (noteBeingEdited == null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 741–744 | 10 | iteración funcional | `removedAttachments.forEach {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |

## 7. Side effects, rendimiento y lifecycle

- **Persistencia / base de datos:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Audio / vibración:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Corrutinas:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Recomposición Compose:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Navegación / Intents:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

La supresión del sonido del teclado está deliberadamente separada del sonido de MyNotes. `wasImeVisible`, `suppressSystemKeyboardSounds`, `systemStreamMutedByMyNotes` y `systemStreamWasMutedBeforeIme` forman una pequeña máquina de estados que evita dos errores: dejar el sistema silenciado al salir, o reactivar un canal que el usuario ya había silenciado antes.

La visibilidad del IME llega mediante `WindowInsetsCompat`. Al aparecer el teclado se decide si debe mutearse `STREAM_SYSTEM`; al ocultarse o perderse el estado adecuado se restaura exactamente el estado anterior. Esta lógica tiene que conservar simetría **adquirir → restaurar**.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**
