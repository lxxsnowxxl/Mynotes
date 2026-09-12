# NoteEditorScreen.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/NoteEditorScreen.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `cfeb5ea33c106e914059ab1d53a1558897db73d318beaee91607428d3b567a8e`  
**Líneas del código real:** 1004

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Pantalla Compose para crear o editar una nota. Gestiona campos de texto, selección de color, adjuntos pendientes, audio/voz y acciones de guardar/cancelar.

**Arquitectura.** Mantiene estado transitorio de edición y lo entrega al nivel superior al confirmar; no debe sustituir la persistencia de NoteViewModel.

**Flujo general.** Flujo típico: se inicializa el estado desde una nota existente o valores por defecto -> el usuario modifica campos/adjuntos/color -> los selectores del sistema devuelven URIs -> se mantienen como estado pendiente -> al guardar se entrega un snapshot coherente al callback superior.

## 2. Package e imports

El `package` es `com.example.mynotes.ui`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **102 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.Manifest`, `android.content.Context`, `android.content.res.Configuration`, `android.content.Intent`, `android.content.pm.PackageManager`, `android.media.MediaRecorder`, `android.os.Build`, `android.net.Uri`, `android.provider.OpenableColumns`.

**Jetpack/Compose:** `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.compose.foundation.background`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.ExperimentalLayoutApi`, `androidx.compose.foundation.layout.FlowRow`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.ArrowBack`, `androidx.compose.material.icons.filled.Check`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.Image`, `androidx.compose.material.icons.filled.Mic`, `androidx.compose.material.icons.filled.MusicNote`, `androidx.compose.material.icons.filled.Stop`, `androidx.compose.material.icons.filled.Videocam`, `androidx.compose.material3.Button`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.Card`, `androidx.compose.material3.CardDefaults`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.OutlinedButton`, `androidx.compose.material3.OutlinedTextField`, `androidx.compose.material3.OutlinedTextFieldDefaults`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TopAppBar`, `androidx.compose.material3.TopAppBarDefaults`, `androidx.compose.material3.Typography`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.DisposableEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.produceState`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.saveable.rememberSaveable`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.graphics.vector.ImageVector`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalConfiguration`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.Font`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontStyle`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.text.input.TextFieldValue`, `androidx.compose.ui.unit.dp`, `androidx.core.content.ContextCompat`.

**Proyecto MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.PendingAttachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.components.LinkPreviewCard`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.openAttachmentViewer`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.delay`, `java.io.File`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (10 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Aserción no nula (2 aparición/apariciones):** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción.
- **Guardia de API (1 aparición/apariciones):** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos.
- **Límite visual (2 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.

## 4. Bloques de código, uno por uno

### 4.1 `NoteEditorScreen` — fun, líneas 108–805

```kotlin
fun NoteEditorScreen(settings: AppSettings, initialTitle: String = "", initialContent: String = "", initialColor: String = "default",
    isEditing: Boolean = false,
    /*
     * Adjuntos que ya pertenecen a la nota.
     *
     * Se muestran al editar, pero NO se envían otra vez
     * como adjuntos nuevos al guardar.
     */
    existingAttachments: List<Attachment> = emptyList(),
    onSave: (String, String, String, List<PendingAttachment>, List<Attachment>) -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val recordingStartErrorText = stringResource(R.string.recording_start_error)
    val recordingTooShortText = stringResource(R.string.recording_too_short)
    val microphonePermissionRequiredText = stringResource(R.string.microphone_permission_required)
    val voiceNoteText = stringResource(R.string.voice_note)
    val microphoneUnavailableText = stringResource(R.string.microphone_not_available)
    val hasMicrophone = remember(context) {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
        }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val editorFontFamily = com.example.mynotes.ui.theme.appFontFamily(settings.font)
    val appTypography = MaterialTheme.typography.withEditorFontFamily(editorFontFamily)
    /*
     * El color elegido en la paleta es también el fondo vivo del editor.
     * Al cambiar selectedColor, Compose recompone inmediatamente la pantalla
     * y permite previsualizar cómo quedará la nota antes de guardarla.
     */
    var selectedColor by rememberSaveable(initialColor) {
        mutableStateOf(initialColor)
    }
    val editorBackground = noteBackgroundColor(selectedColor)
    val editorTextColor = resolveUiTextColor(value = settings.textColor, background = editorBackground)
    val editorSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = editorBackground)
    val editorGraphicColor = resolveUiGraphicColor(value = settings.textColor, background = editorBackground)
    val editorAccentOutline = ensureUiContrast(preferred = MaterialTheme.colorScheme.primary, background = editorBackground,
            minimumContrast = 3f)
    var title by rememberSaveable(initialTitle, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialTitle))
    }
    var content by rememberSaveable(initialContent, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialContent))
    }
    /*
     * Solo contiene adjuntos NUEVOS agregados durante esta
     * sesión de edición. Los adjuntos existentes se reciben
     * por existingAttachments.
     */
    var newAttachments by remember {
        mutableStateOf<
                List<PendingAttachment>
                >(emptyList())
    }
    /*
     * Adjuntos existentes marcados para eliminar.
     *
     * No se borran de Room ni del almacenamiento hasta que
     * el usuario pulse Guardar. Si pulsa Cancelar, no se pierde
     * ningún archivo.
     */
    var removedExistingAttachments by remember(initialTitle, initialContent) {
        mutableStateOf<
                List<Attachment>
                >(emptyList())
    }
    val visibleExistingAttachments = existingAttachments.filterNot {
                attachment ->
            removedExistingAttachments.any {
                    removed ->
                removed.id == attachment.id
            }
        }
    /*
     * ==========================================
     * GRABACIÓN DE VOZ
     * ==========================================
     */
    var mediaRecorder by remember {
        mutableStateOf<MediaRecorder?>(null)
    }
    var recordingFile by remember {
        mutableStateOf<File?>(null)
    }
    var isRecording by remember {
        mutableStateOf(false)
    }
    var recordingError by remember {
        mutableStateOf<String?>(null)
    }
    var startRecordingAfterPermission
            by remember {
                mutableStateOf(false)
            }
    /*
     * ==========================================
     * FUNCIONES DE GRABACIÓN
     * ==========================================
     */
    fun startRecording() {
        if (!hasMicrophone) {
            recordingError = microphoneUnavailableText
            return
        }
        try {
            recordingError = null
            val voiceDirectory = File(context.cacheDir, "voice_notes")
            if (!voiceDirectory.exists() && !voiceDirectory.mkdirs()) {
                recordingError = recordingStartErrorText
                return
            }
            val file = File(voiceDirectory, "voice_${System.currentTimeMillis()}.m4a")
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    @Suppress("DEPRECATION")
                    MediaRecorder()
                }
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioEncodingBitRate(128000)
            recorder.setAudioSamplingRate(44100)
            recorder.setOutputFile(file.absolutePath)
            recorder.prepare()
            recorder.start()
            recordingFile = file
            mediaRecorder = recorder
            isRecording = true
        } catch (e: Exception) {
            e.printStackTrace()
            recordingError = recordingStartErrorText
            try {
                mediaRecorder?.release()
            } catch (ignored: Exception) {
            }
            mediaRecorder = null
            recordingFile?.delete()
            recordingFile = null
            isRecording = false
        }
    }
    fun stopRecording() {
        val recorder = mediaRecorder
        val file = recordingFile
        if (recorder == null || file == null) {
            return
        }
        try {
            recorder.stop()
            recorder.release()
            mediaRecorder = null
            isRecording = false
            /*
             * Agregamos la grabación a
             * los adjuntos pendientes.
             */
            if (file.exists() && file.length() > 0L) {
                val voiceAttachment = PendingAttachment(uri = Uri.fromFile(file),
                        type = "voice",
                        name = voiceNoteText,
                        mimeType = "audio/mp4")
                newAttachments = newAttachments + voiceAttachment
            }
            recordingFile = null
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                recorder.release()
            } catch (ignored: Exception) {
            }
            mediaRecorder = null
            isRecording = false
            /*
             * Una grabación demasiado
             * corta puede hacer fallar stop().
             */
            file.delete()
            recordingFile = null
            recordingError = recordingTooShortText
        }
    }
    /*
     * Si salimos del editor mientras
     * todavía está grabando, liberamos
     * el micrófono.
     */
    DisposableEffect(Unit) {
        onDispose {
            try {
                if (isRecording) {
                    try {
                        mediaRecorder?.stop()
                    } catch (ignored: Exception) {
                    }
                }
                mediaRecorder?.release()
            } catch (ignored: Exception) {
            }
            mediaRecorder = null
            /* No dejamos una grabación incompleta huérfana en cacheDir. */
            if (isRecording) {
                recordingFile?.delete()
            }
            recordingFile = null
            isRecording = false
        }
    }
    /*
     * ==========================================
     * PERMISO DE MICRÓFONO
     * ==========================================
     */
    val microphonePermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                if (startRecordingAfterPermission) {
                    startRecording()
                    startRecordingAfterPermission = false
                }
            } else {
                startRecordingAfterPermission = false
                recordingError = microphonePermissionRequiredText
            }
        }
    fun requestVoiceRecording() {
        if (!hasMicrophone) {
            recordingError = microphoneUnavailableText
            return
        }
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            startRecording()
        } else {
            startRecordingAfterPermission = true
            microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    /*
     * ==========================================
     * IMÁGENES
     * ==========================================
     */
    val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            val newItems = uris.map { uri ->
                    try {
                        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } catch (_: Exception) {
                    }
                    PendingAttachment(uri = uri,
                        type = "image",
                        name = getFileName(context, uri),
                        mimeType = context.contentResolver.getType(uri))
                }
            newAttachments = (newAttachments + newItems).distinctBy {
                        it.uri
                    }
            if (newItems.isNotEmpty()) {
                UiSoundPlayer.play(context = context, sound = UiSound.Attachment)
            }
        }
    /*
     * ==========================================
     * VIDEOS
     * ==========================================
     */
    val videoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            val newItems = uris.map { uri ->
                    try {
                        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } catch (_: Exception) {
                    }
                    PendingAttachment(uri = uri,
                        type = "video",
                        name = getFileName(context, uri),
                        mimeType = context.contentResolver.getType(uri))
                }
            newAttachments = (newAttachments + newItems).distinctBy {
                        it.uri
                    }
            if (newItems.isNotEmpty()) {
                UiSoundPlayer.play(context = context, sound = UiSound.Attachment)
            }
        }
    /*
     * ==========================================
     * AUDIO / CANCIONES
     * ==========================================
     */
    val audioPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            val newItems = uris.map { uri ->
                    try {
                        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } catch (_: Exception) {
                    }
                    PendingAttachment(uri = uri,
                        type = "audio",
                        name = getFileName(context, uri),
                        mimeType = context.contentResolver.getType(uri))
                }
            newAttachments = (newAttachments + newItems).distinctBy {
                        it.uri
                    }
            if (newItems.isNotEmpty()) {
                UiSoundPlayer.play(context = context, sound = UiSound.Attachment)
            }
        }
    /*
     * ==========================================
     * ARCHIVOS
     * ==========================================
     */
    val filePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            val newItems = uris.map { uri ->
                    try {
                        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } catch (_: Exception) {
                    }
                    PendingAttachment(uri = uri,
                        type = "file",
                        name = getFileName(context, uri),
                        mimeType = context.contentResolver.getType(uri))
                }
            newAttachments = (newAttachments + newItems).distinctBy {
                        it.uri
                    }
            if (newItems.isNotEmpty()) {
                UiSoundPlayer.play(context = context, sound = UiSound.Attachment)
            }
        }
    /*
     * ==========================================
     * PANTALLA
     * ==========================================
     */
    MaterialTheme(colorScheme = MaterialTheme.colorScheme, typography = appTypography, shapes = MaterialTheme.shapes) {
        Scaffold(containerColor = editorBackground,
            topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = editorBackground,
                                scrolledContainerColor = editorBackground,
                                titleContentColor = editorTextColor,
                                navigationIconContentColor = editorGraphicColor,
                                actionIconContentColor = editorGraphicColor),
                    navigationIcon = {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            IconButton(onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                                    if (isRecording) {
                                        stopRecording()
                                    }
                                    onCancel()
                                }) {
                                Icon(imageVector = Icons.Default.ArrowBack,
                                    contentDescription = stringResource(R.string.back),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    },
                    title = {
                        Text(text = if (isEditing) {
                                    stringResource(R.string.edit_note)
                                } else {
                                    stringResource(R.string.new_note)
                                })
                    },
                    actions = {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            IconButton(enabled = !isRecording,
                                onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Save)
                                    onSave(title.text, content.text, selectedColor, newAttachments, removedExistingAttachments)
                                }) {
                                Icon(imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(R.string.save),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    })
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
                Column(modifier = Modifier.widthIn(max = 760.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(
                            horizontal = 18.dp)) {
                Spacer(modifier = Modifier.height(12.dp))
                /*
                 * ==========================================
                 * TÍTULO
                 * ==========================================
                 */
                OutlinedTextField(
                    value = title,
                    onValueChange = { newValue -> if (newValue.text != title.text) {
                            UiSoundPlayer.playActionThrottled(context = context, action = UiActionSound.TextInput, minimumIntervalMs = 48L)
                        } else if (newValue.selection != title.selection) {
                            UiSoundPlayer.playThrottled(context = context, sound = UiSound.SliderTick, minimumIntervalMs = 55L)
                        }
                        title = newValue
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(stringResource(R.string.title))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = editorTextColor, unfocusedTextColor = editorTextColor,
                            focusedPlaceholderColor = editorSecondaryTextColor, unfocusedPlaceholderColor = editorSecondaryTextColor,
                            cursorColor = editorGraphicColor, focusedBorderColor = editorAccentOutline,
                            unfocusedBorderColor = editorGraphicColor, focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent))
                Spacer(modifier = Modifier.height(14.dp))
                /*
                 * ==========================================
                 * CONTENIDO
                 * ==========================================
                 */
                OutlinedTextField(
                    value = content,
                    onValueChange = { newValue -> if (newValue.text != content.text) {
                            UiSoundPlayer.playActionThrottled(context = context, action = UiActionSound.TextInput, minimumIntervalMs = 42L)
                        } else if (newValue.selection != content.selection) {
                            UiSoundPlayer.playThrottled(context = context, sound = UiSound.SliderTick, minimumIntervalMs = 45L)
                        }
                        content = newValue
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = if (isLandscape) 150.dp else 220.dp,
                            max = if (isLandscape) 220.dp else 340.dp),
                    placeholder = {
                        Text(stringResource(R.string.write_your_note))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = editorTextColor, unfocusedTextColor = editorTextColor,
                            focusedPlaceholderColor = editorSecondaryTextColor, unfocusedPlaceholderColor = editorSecondaryTextColor,
                            cursorColor = editorGraphicColor, focusedBorderColor = editorAccentOutline,
                            unfocusedBorderColor = editorGraphicColor, focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent))
                val editorLinkUrls = remember(content.text) {
                        extractLinkUrls(content.text).take(3)
                    }
                if (editorLinkUrls.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    editorLinkUrls.forEach { linkUrl -> LinkPreviewCard(url = linkUrl, compact = false,
                            textColorMode = settings.noteUiTextColor)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(text = stringResource(R.string.mock_color_palette), color = editorTextColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                val editorColorOptions = listOf(Triple("default", R.string.mock_color_default, MaterialTheme.colorScheme.background),
                        Triple("yellow", R.string.mock_color_yellow, Color(0xFFFFF4C7)),
                        Triple("orange", R.string.mock_color_orange, Color(0xFFFFE7D1)),
                        Triple("red", R.string.mock_color_red, Color(0xFFFFE0E0)),
                        Triple("pink", R.string.mock_color_pink, Color(0xFFFFE5EC)),
                        Triple("purple", R.string.mock_color_purple, Color(0xFFF0E7FA)),
                        Triple("blue", R.string.mock_color_blue, Color(0xFFE5F1FB)),
                        Triple("cyan", R.string.mock_color_cyan, Color(0xFFE0F7FA)),
                        Triple("teal", R.string.mock_color_teal, Color(0xFFDDF4F0)),
                        Triple("green", R.string.mock_color_green, Color(0xFFE4F2E8)),
                        Triple("mint", R.string.mock_color_mint, Color(0xFFDFF7EA)),
                        Triple("lime", R.string.mock_color_lime, Color(0xFFF1F8D7)),
                        Triple("brown", R.string.mock_color_brown, Color(0xFFEDE2D9)),
                        Triple("gray", R.string.mock_color_gray, Color(0xFFE9ECEF)))
                FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    editorColorOptions.forEach { option -> val selected = selectedColor == option.first
                        Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = option.third, border = BorderStroke(
                                width = if (selected) 3.dp else 1.dp, color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                }), onClick = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                                selectedColor = option.first
                            }) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (selected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = stringResource(option.second),
                                        tint = Color.Black.copy(alpha = 0.72f), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(text = stringResource(R.string.attachments),
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                /*
                 * ==========================================
                 * MINIATURAS
                 * ==========================================
                 */
                if (visibleExistingAttachments.isNotEmpty() || newAttachments.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        /*
                         * ==========================================
                         * ADJUNTOS YA GUARDADOS
                         * ==========================================
                         *
                         * Se muestran mientras editamos, pero no
                         * se agregan otra vez al guardar.
                         */
                        visibleExistingAttachments.forEachIndexed {
                                    index, attachment ->
                                val previewDelay = when (settings.performanceMode) {
                                        "performance" -> 260L + index * 70L
                                        "balanced" -> 120L + index * 40L
                                        else -> 0L
                                    }
                                AttachmentPreview(
                                    attachment = PendingAttachment(uri = Uri.parse(attachment.uri),
                                            type = attachment.type,
                                            name = attachment.name),
                                    /*
                                     * Al tocar X solo lo quitamos de la
                                     * edición actual. El borrado real se
                                     * realiza al pulsar Guardar.
                                     */
                                    previewDelayMillis = previewDelay,
                                    performanceMode = settings.performanceMode,
                                    onRemove = {
                                        if (removedExistingAttachments.none {
                                                    removed ->
                                                removed.id == attachment.id
                                            }) {
                                            UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                                            removedExistingAttachments = removedExistingAttachments + attachment
                                        }
                                    })
                            }
                        /*
                         * ==========================================
                         * ADJUNTOS NUEVOS
                         * ==========================================
                         */
                        newAttachments.forEachIndexed {
                                    index, attachment ->
                                val previewIndex = visibleExistingAttachments.size + index
                                val previewDelay = when (settings.performanceMode) {
                                        "performance" -> 260L + previewIndex * 70L
                                        "balanced" -> 120L + previewIndex * 40L
                                        else -> 0L
                                    }
                                AttachmentPreview(
                                    attachment = attachment,
                                    previewDelayMillis = previewDelay,
                                    performanceMode = settings.performanceMode,
                                    onRemove = {
                                        /*
                                         * Si es una nota de voz temporal,
                                         * también eliminamos el archivo.
                                         */
                                        if (attachment.type == "voice") {
                                            val uri = attachment.uri
                                            if (uri.scheme == "file") {
                                                uri.path?.let {
                                                            path ->
                                                        File(path).delete()
                                                    }
                                            }
                                        }
                                        UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                                        newAttachments = newAttachments.filterNot {
                                                    it.uri == attachment.uri
                                                }
                                    })
                            }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
                /*
                 * ==========================================
                 * IMAGEN / VIDEO
                 * ==========================================
                 */
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AttachmentButton(modifier = Modifier.weight(1f),
                        text = stringResource(R.string.image),
                        icon = Icons.Default.Image,
                        onClick = {
                            imagePicker.launch(arrayOf("image/*"))
                        })
                    AttachmentButton(modifier = Modifier.weight(1f),
                        text = stringResource(R.string.video),
                        icon = Icons.Default.Videocam,
                        onClick = {
                            videoPicker.launch(arrayOf("video/*"))
                        })
                }
                Spacer(modifier = Modifier.height(8.dp))
                /*
                 * ==========================================
                 * AUDIO / ARCHIVO
                 * ==========================================
                 */
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AttachmentButton(modifier = Modifier.weight(1f),
                        text = stringResource(R.string.audio),
                        icon = Icons.Default.MusicNote,
                        onClick = {
                            audioPicker.launch(arrayOf("audio/*"))
                        })
                    AttachmentButton(modifier = Modifier.weight(1f),
                        text = stringResource(R.string.file),
                        icon = Icons.Default.Description,
                        onClick = {
                            filePicker.launch(arrayOf("*/*"))
                        })
                }
                Spacer(modifier = Modifier.height(10.dp))
                /*
                 * ==========================================
                 * NOTA DE VOZ
                 * ==========================================
                 */
                if (isRecording) {
                    Button(modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                            stopRecording()
                        },
                        shape = RoundedCornerShape(14.dp)) {
                        Icon(imageVector = Icons.Default.Stop,
                            contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(R.string.stop_recording))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = stringResource(R.string.recording_voice_note),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                            requestVoiceRecording()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))) {
                        Icon(imageVector = Icons.Default.Mic,
                            contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(R.string.record_voice_note))
                    }
                }
                if (recordingError != null) {
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(text = recordingError!!,
                        color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(20.dp))
                /*
                 * ==========================================
                 * CANCELAR / GUARDAR
                 * ==========================================
                 */
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Cancel)
                            onCancel()
                        },
                        enabled = !isRecording,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = !isRecording,
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Save)
                            onSave(title.text, content.text, selectedColor, newAttachments, removedExistingAttachments)
                        },
                        shape = RoundedCornerShape(14.dp)) {
                        Text(if (isEditing) {
                                stringResource(R.string.save)
                            } else {
                                stringResource(R.string.create_note)
                            })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `settings: AppSettings` — `settings` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.
- `initialTitle: String = ""` — `initialTitle` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `""`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `initialContent: String = ""` — `initialContent` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `""`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `initialColor: String = "default"` — `initialColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"default"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `isEditing: Boolean = false` — `isEditing` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `/* * Adjuntos que ya pertenecen a la nota. * * Se muestran al editar` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `pero NO se envían otra vez * como adjuntos nuevos al guardar. */ existingAttachments: List<Attachment> = emptyList()` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `onSave: (String, String, String, List<PendingAttachment>, List<Attachment>) -> Unit` — `onSave` recibe un valor de tipo `(String, String, String, List<PendingAttachment>, List<Attachment>) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onCancel: () -> Unit` — `onCancel` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 118 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 119 | `val recordingStartErrorText` | `inferido` | `stringResource(R.string.recording_start_error)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 120 | `val recordingTooShortText` | `inferido` | `stringResource(R.string.recording_too_short)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 121 | `val microphonePermissionRequiredText` | `inferido` | `stringResource(R.string.microphone_permission_required)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 122 | `val voiceNoteText` | `inferido` | `stringResource(R.string.voice_note)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 123 | `val microphoneUnavailableText` | `inferido` | `stringResource(R.string.microphone_not_available)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 124 | `val hasMicrophone` | `inferido` | `remember(context) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 127 | `val isLandscape` | `inferido` | `LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 128 | `val editorFontFamily` | `inferido` | `com.example.mynotes.ui.theme.appFontFamily(settings.font)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 129 | `val appTypography` | `inferido` | `MaterialTheme.typography.withEditorFontFamily(editorFontFamily)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 135 | `var selectedColor` | `inferido` | `by rememberSaveable(initialColor) {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 138 | `val editorBackground` | `inferido` | `noteBackgroundColor(selectedColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 139 | `val editorTextColor` | `inferido` | `resolveUiTextColor(value = settings.textColor, background = editorBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 140 | `val editorSecondaryTextColor` | `inferido` | `resolveSecondaryUiTextColor(value = settings.textColor, background = editorBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 141 | `val editorGraphicColor` | `inferido` | `resolveUiGraphicColor(value = settings.textColor, background = editorBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 142 | `val editorAccentOutline` | `inferido` | `ensureUiContrast(preferred = MaterialTheme.colorScheme.primary, background = editorBackground,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 144 | `var title` | `inferido` | `by rememberSaveable(initialTitle, stateSaver = TextFieldValue.Saver) {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 147 | `var content` | `inferido` | `by rememberSaveable(initialContent, stateSaver = TextFieldValue.Saver) {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 155 | `var newAttachments` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 167 | `var removedExistingAttachments` | `inferido` | `by remember(initialTitle, initialContent) {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 172 | `val visibleExistingAttachments` | `inferido` | `existingAttachments.filterNot {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 184 | `var mediaRecorder` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 187 | `var recordingFile` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 190 | `var isRecording` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 193 | `var recordingError` | `inferido` | `by remember {` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 212 | `val voiceDirectory` | `inferido` | `File(context.cacheDir, "voice_notes")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 217 | `val file` | `inferido` | `File(voiceDirectory, "voice_${System.currentTimeMillis()}.m4a")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `val recorder` | `inferido` | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 249 | `val recorder` | `inferido` | `mediaRecorder` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 250 | `val file` | `inferido` | `recordingFile` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 264 | `val voiceAttachment` | `inferido` | `PendingAttachment(uri = Uri.fromFile(file),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 319 | `val microphonePermissionLauncher` | `inferido` | `rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 336 | `val hasPermission` | `inferido` | `ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 350 | `val imagePicker` | `inferido` | `rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 351 | `val newItems` | `inferido` | `uris.map { uri ->` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 373 | `val videoPicker` | `inferido` | `rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 374 | `val newItems` | `inferido` | `uris.map { uri ->` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 396 | `val audioPicker` | `inferido` | `rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 397 | `val newItems` | `inferido` | `uris.map { uri ->` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 419 | `val filePicker` | `inferido` | `rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 420 | `val newItems` | `inferido` | `uris.map { uri ->` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 541 | `val editorLinkUrls` | `inferido` | `remember(content.text) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 554 | `val editorColorOptions` | `inferido` | `listOf(Triple("default", R.string.mock_color_default, MaterialTheme.colorScheme.background),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 613 | `val previewDelay` | `inferido` | `when (settings.performanceMode) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 646 | `val previewIndex` | `inferido` | `visibleExistingAttachments.size + index` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 647 | `val previewDelay` | `inferido` | `when (settings.performanceMode) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 662 | `val uri` | `inferido` | `attachment.uri` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 117 | `onSave: (String, String, String, List<PendingAttachment>, List<Attachment>) -> Unit, onCancel: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 173 | `attachment ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 175 | `removed ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 206 | `if (!hasMicrophone) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 208 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 210 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 213 | `if (!voiceDirectory.exists() && !voiceDirectory.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 215 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 238 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 251 | `if (recorder == null \|\| file == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 252 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 254 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 263 | `if (file.exists() && file.length() > 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 273 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 293 | `DisposableEffect(Unit) {` | Efecto de Compose con limpieza explícita: registra trabajo/recurso y exige `onDispose` al abandonar o cambiar las claves. |
| 295 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 296 | `if (isRecording) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 297 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 307 | `if (isRecording) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 321 | `if (granted) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 322 | `if (startRecordingAfterPermission) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 332 | `if (!hasMicrophone) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 334 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 338 | `if (hasPermission) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 352 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 364 | `if (newItems.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 375 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 387 | `if (newItems.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 398 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 410 | `if (newItems.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 421 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 433 | `if (newItems.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 454 | `if (isRecording) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 544 | `if (editorLinkUrls.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 581 | `if (selected) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 598 | `if (visibleExistingAttachments.isNotEmpty() \|\| newAttachments.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 612 | `index, attachment ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 614 | `"performance" -> 260L + index * 70L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 615 | `"balanced" -> 120L + index * 40L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 616 | `else -> 0L` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 630 | `if (removedExistingAttachments.none {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 631 | `removed ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 645 | `index, attachment ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 648 | `"performance" -> 260L + previewIndex * 70L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 649 | `"balanced" -> 120L + previewIndex * 40L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 650 | `else -> 0L` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 661 | `if (attachment.type == "voice") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 663 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 665 | `path ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 726 | `if (isRecording) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 759 | `if (recordingError != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 6.
- **Aserción no nula:** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción. Apariciones en este bloque: 1.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `emptyList`, `stringResource`, `remember`, `context.packageManager.hasSystemFeature`, `com.example.mynotes.ui.theme.appFontFamily`, `MaterialTheme.typography.withEditorFontFamily`, `rememberSaveable`, `mutableStateOf`, `noteBackgroundColor`, `resolveUiTextColor`, `resolveSecondaryUiTextColor`, `resolveUiGraphicColor`, `ensureUiContrast`, `TextFieldValue`, `File`, `voiceDirectory.exists`, `voiceDirectory.mkdirs`, `MediaRecorder`, `Suppress`, `recorder.setAudioSource`, `recorder.setOutputFormat`, `recorder.setAudioEncoder`, `recorder.setAudioEncodingBitRate`, `recorder.setAudioSamplingRate`, `recorder.setOutputFile`, `recorder.prepare`, `recorder.start`, `e.printStackTrace`, `release`, `delete`, `recorder.stop`, `recorder.release`, `file.exists`, `file.length`, `PendingAttachment`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.2 `startRecording` — fun, líneas 205–247

```kotlin
    fun startRecording() {
        if (!hasMicrophone) {
            recordingError = microphoneUnavailableText
            return
        }
        try {
            recordingError = null
            val voiceDirectory = File(context.cacheDir, "voice_notes")
            if (!voiceDirectory.exists() && !voiceDirectory.mkdirs()) {
                recordingError = recordingStartErrorText
                return
            }
            val file = File(voiceDirectory, "voice_${System.currentTimeMillis()}.m4a")
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    @Suppress("DEPRECATION")
                    MediaRecorder()
                }
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioEncodingBitRate(128000)
            recorder.setAudioSamplingRate(44100)
            recorder.setOutputFile(file.absolutePath)
            recorder.prepare()
            recorder.start()
            recordingFile = file
            mediaRecorder = recorder
            isRecording = true
        } catch (e: Exception) {
            e.printStackTrace()
            recordingError = recordingStartErrorText
            try {
                mediaRecorder?.release()
            } catch (ignored: Exception) {
            }
            mediaRecorder = null
            recordingFile?.delete()
            recordingFile = null
            isRecording = false
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 212 | `val voiceDirectory` | `inferido` | `File(context.cacheDir, "voice_notes")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 217 | `val file` | `inferido` | `File(voiceDirectory, "voice_${System.currentTimeMillis()}.m4a")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `val recorder` | `inferido` | `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 206 | `if (!hasMicrophone) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 208 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 210 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 213 | `if (!voiceDirectory.exists() && !voiceDirectory.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 215 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 238 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Guardia de API:** Se condiciona una API según la versión de Android para no ejecutar llamadas incompatibles en dispositivos antiguos. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `voiceDirectory.exists`, `voiceDirectory.mkdirs`, `MediaRecorder`, `Suppress`, `recorder.setAudioSource`, `recorder.setOutputFormat`, `recorder.setAudioEncoder`, `recorder.setAudioEncodingBitRate`, `recorder.setAudioSamplingRate`, `recorder.setOutputFile`, `recorder.prepare`, `recorder.start`, `e.printStackTrace`, `release`, `delete`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar las guardas de versión antes de APIs que no existen en Android antiguos.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.3 `stopRecording` — fun, líneas 248–287

```kotlin
    fun stopRecording() {
        val recorder = mediaRecorder
        val file = recordingFile
        if (recorder == null || file == null) {
            return
        }
        try {
            recorder.stop()
            recorder.release()
            mediaRecorder = null
            isRecording = false
            /*
             * Agregamos la grabación a
             * los adjuntos pendientes.
             */
            if (file.exists() && file.length() > 0L) {
                val voiceAttachment = PendingAttachment(uri = Uri.fromFile(file),
                        type = "voice",
                        name = voiceNoteText,
                        mimeType = "audio/mp4")
                newAttachments = newAttachments + voiceAttachment
            }
            recordingFile = null
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                recorder.release()
            } catch (ignored: Exception) {
            }
            mediaRecorder = null
            isRecording = false
            /*
             * Una grabación demasiado
             * corta puede hacer fallar stop().
             */
            file.delete()
            recordingFile = null
            recordingError = recordingTooShortText
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 249 | `val recorder` | `inferido` | `mediaRecorder` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 250 | `val file` | `inferido` | `recordingFile` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 264 | `val voiceAttachment` | `inferido` | `PendingAttachment(uri = Uri.fromFile(file),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 251 | `if (recorder == null \|\| file == null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 252 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 254 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 263 | `if (file.exists() && file.length() > 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 273 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `recorder.stop`, `recorder.release`, `file.exists`, `file.length`, `PendingAttachment`, `Uri.fromFile`, `e.printStackTrace`, `file.delete`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `requestVoiceRecording` — fun, líneas 331–344

```kotlin
    fun requestVoiceRecording() {
        if (!hasMicrophone) {
            recordingError = microphoneUnavailableText
            return
        }
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            startRecording()
        } else {
            startRecordingAfterPermission = true
            microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
```

#### Qué hace y por qué existe

Emite una solicitud a una capa de plataforma/controlador y adapta los parámetros a las restricciones disponibles.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 336 | `val hasPermission` | `inferido` | `ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 332 | `if (!hasMicrophone) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 334 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 338 | `if (hasPermission) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `ContextCompat.checkSelfPermission`, `startRecording`, `microphonePermissionLauncher.launch`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `AttachmentButton` — fun, líneas 813–829

```kotlin
private fun AttachmentButton(modifier: Modifier = Modifier, text: String, icon: ImageVector, onClick: () -> Unit) {
    val context = LocalContext.current
    OutlinedButton(modifier = modifier,
        onClick = {
            UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
            onClick()
        },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))) {
        Icon(imageVector = icon,
            contentDescription = null)
        Spacer(modifier = Modifier.size(6.dp))
        Text(text = text)
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `text: String` — `text` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `icon: ImageVector` — `icon` recibe un valor de tipo `ImageVector`. El contrato no marca este parámetro como anulable.
- `onClick: () -> Unit` — `onClick` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 814 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 813 | `private fun AttachmentButton(modifier: Modifier = Modifier, text: String, icon: ImageVector, onClick: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Semántica Compose/lifecycle

- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `OutlinedButton`, `UiSoundPlayer.playAction`, `onClick`, `RoundedCornerShape`, `ButtonDefaults.outlinedButtonColors`, `BorderStroke`, `MaterialTheme.colorScheme.primary.copy`, `Icon`, `Spacer`, `Modifier.size`, `Text`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `AttachmentPreview` — fun, líneas 837–947

```kotlin
private fun AttachmentPreview(attachment: PendingAttachment, previewDelayMillis: Long = 0L, performanceMode: String, onRemove: (() -> Unit)?
) {
    val context = LocalContext.current
    val isBorderlessPreview = attachment.type == "image" || attachment.type == "video" || attachment.name?.substringAfterLast(".", "")
                ?.equals("pdf", ignoreCase = true) == true
    Card(modifier = Modifier.size(width = 108.dp, height = 108.dp).clickable {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Open)
                    openAttachmentViewer(context = context, uri = attachment.uri.toString(), type = attachment.type, name = attachment.name,
                        mimeType = attachment.mimeType)
                },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isBorderlessPreview) {
                        Color.Transparent
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    }),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (attachment.type) {
                /*
                 * IMAGEN
                 */
                "image" -> {
                    val previewReady by
                        produceState(initialValue = previewDelayMillis <= 0L, key1 = attachment.uri, key2 = previewDelayMillis) {
                            if (previewDelayMillis > 0L) {
                                delay(previewDelayMillis)
                            }
                            value = true
                        }
                    if (previewReady) {
                        val imagePreview by
                            produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = performanceMode) {
                                value = AttachmentPreviewCache.withPreviewPermit {
                                    AttachmentPreviewCache.loadImagePreview(context = context, uri = attachment.uri,
                                        performanceMode = performanceMode)
                                }
                            }
                        if (imagePreview != null) {
                            androidx.compose.foundation.Image(bitmap = imagePreview!!.asImageBitmap(), contentDescription = attachment.name,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Fit)
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Image, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f))
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f))
                        }
                    }
                }
                /*
                 * VIDEO
                 */
                "video" -> {
                    AttachmentIconPreview(icon = Icons.Default.Videocam,
                        title = stringResource(R.string.video),
                        name = attachment.name)
                }
                /*
                 * CANCIÓN / AUDIO
                 */
                "audio" -> {
                    AttachmentIconPreview(icon = Icons.Default.MusicNote,
                        title = stringResource(R.string.audio),
                        name = attachment.name)
                }
                /*
                 * NOTA DE VOZ
                 */
                "voice" -> {
                    AttachmentIconPreview(icon = Icons.Default.Mic,
                        title = stringResource(R.string.voice_note),
                        name = attachment.name)
                }
                /*
                 * ARCHIVO
                 */
                else -> {
                    AttachmentIconPreview(icon = Icons.Default.Description,
                        title = stringResource(R.string.file),
                        name = attachment.name)
                }
            }
            /*
             * ==========================================
             * BOTÓN X
             * ==========================================
             *
             * Aparece tanto para adjuntos nuevos como para
             * adjuntos existentes. En los existentes, quitarlo
             * solo lo marca para borrar cuando se pulse Guardar.
             */
            if (onRemove != null) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(27.dp).clip(CircleShape).background(MaterialTheme
                                .colorScheme.surface.copy(alpha = 0.9f)).clickable {
                            onRemove()
                        },
                    contentAlignment = Alignment.Center) {
                    Text(text = "×",
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `attachment: PendingAttachment` — `attachment` recibe un valor de tipo `PendingAttachment`. El contrato no marca este parámetro como anulable.
- `previewDelayMillis: Long = 0L` — `previewDelayMillis` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `0L`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `performanceMode: String` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `onRemove: (() -> Unit)?` — `onRemove` recibe un valor de tipo `(() -> Unit)?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 839 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 840 | `val isBorderlessPreview` | `inferido` | `attachment.type == "image" \|\| attachment.type == "video" \|\| attachment.name?.substringAfterLast(".",…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 837 | `private fun AttachmentPreview(attachment: PendingAttachment, previewDelayMillis: Long = 0L, performanceMode: String, onRemove: (() -> Unit)?` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 855 | `when (attachment.type) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 859 | `"image" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 862 | `if (previewDelayMillis > 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 867 | `if (previewReady) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 875 | `if (imagePreview != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 896 | `"video" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 904 | `"audio" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 912 | `"voice" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 920 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 935 | `if (onRemove != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.
- **Aserción no nula:** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `substringAfterLast`, `equals`, `Card`, `Modifier.size`, `UiSoundPlayer.playAction`, `openAttachmentViewer`, `attachment.uri.toString`, `RoundedCornerShape`, `CardDefaults.cardColors`, `CardDefaults.cardElevation`, `Box`, `Modifier.fillMaxSize`, `produceState`, `delay`, `AttachmentPreviewCache.loadImagePreview`, `androidx.compose.foundation.Image`, `asImageBitmap`, `clip`, `Icon`, `MaterialTheme.colorScheme.onSurfaceVariant.copy`, `AttachmentIconPreview`, `stringResource`, `Modifier.align`, `padding`, `size`, `background`, `colorScheme.surface.copy`, `onRemove`, `Text`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.7 `AttachmentIconPreview` — fun, líneas 950–968

```kotlin
private fun AttachmentIconPreview(icon: ImageVector, title: String, name: String?) {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(34.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title,
            fontWeight = FontWeight.Bold,
            maxLines = 1)
        if (!name.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `icon: ImageVector` — `icon` recibe un valor de tipo `ImageVector`. El contrato no marca este parámetro como anulable.
- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `name: String?` — `name` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 961 | `if (!name.isNullOrBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 2.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Column`, `Modifier.fillMaxSize`, `padding`, `Icon`, `Modifier.size`, `Spacer`, `Modifier.height`, `Text`, `name.isNullOrBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `getFileName` — fun, líneas 975–993

```kotlin
private fun getFileName(context: Context, uri: Uri): String? {
    if (uri.scheme == "file") {
        return uri.path?.let {
                File(it).name
            }
    }
    return try {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst()) {
                    cursor.getString(index)
                } else {
                    null
                }
            }
    } catch (e: Exception) {
        null
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 983 | `val index` | `inferido` | `cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 976 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 977 | `return uri.path?.let {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 981 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 984 | `if (index >= 0 && cursor.moveToFirst()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `context.contentResolver.query`, `arrayOf`, `cursor.getColumnIndex`, `cursor.moveToFirst`, `cursor.getString`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.9 `Typography` — fun, líneas 995–1004

```kotlin
private fun Typography.withEditorFontFamily(fontFamily: FontFamily): Typography {
    return Typography(displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily), displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily), headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily), titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily), titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily), bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily), labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily), labelSmall = labelSmall.copy(fontFamily = fontFamily))
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.

**Retorno:** `Typography`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 996 | `return Typography(displayLarge = displayLarge.copy(fontFamily = fontFamily),` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withEditorFontFamily`, `Typography`, `displayLarge.copy`, `displayMedium.copy`, `displaySmall.copy`, `headlineLarge.copy`, `headlineMedium.copy`, `headlineSmall.copy`, `titleLarge.copy`, `titleMedium.copy`, `titleSmall.copy`, `bodyLarge.copy`, `bodyMedium.copy`, `bodySmall.copy`, `labelLarge.copy`, `labelMedium.copy`, `labelSmall.copy`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 118 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 119 | `recordingStartErrorText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 120 | `recordingTooShortText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 121 | `microphonePermissionRequiredText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 122 | `voiceNoteText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 123 | `microphoneUnavailableText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 124 | `hasMicrophone` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 127 | `isLandscape` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 128 | `editorFontFamily` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 129 | `appTypography` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 135 | `selectedColor` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 138 | `editorBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 139 | `editorTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 140 | `editorSecondaryTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 141 | `editorGraphicColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 142 | `editorAccentOutline` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 144 | `title` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 147 | `content` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 155 | `newAttachments` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 167 | `removedExistingAttachments` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 172 | `visibleExistingAttachments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 184 | `mediaRecorder` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 187 | `recordingFile` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 190 | `isRecording` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 193 | `recordingError` | `var` | `inferido` | Variable mutable: el código puede reasignarla; por tanto forma parte de un estado que cambia durante el flujo. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 212 | `voiceDirectory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 217 | `file` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `recorder` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 249 | `recorder` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 250 | `file` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 264 | `voiceAttachment` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 319 | `microphonePermissionLauncher` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 336 | `hasPermission` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 350 | `imagePicker` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 351 | `newItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 373 | `videoPicker` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 374 | `newItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 396 | `audioPicker` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 397 | `newItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 419 | `filePicker` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 420 | `newItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 541 | `editorLinkUrls` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 554 | `editorColorOptions` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 613 | `previewDelay` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 646 | `previewIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 647 | `previewDelay` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 662 | `uri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 814 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 839 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 840 | `isBorderlessPreview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 983 | `index` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 117–805 | 0 | `onSave: (String, String, String, List<PendingAttachment>, List<Attachment>) -> Unit, onCancel: () -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 124–126 | 1 | `val hasMicrophone = remember(context)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 135–137 | 1 | `var selectedColor by rememberSaveable(initialColor)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 144–146 | 1 | `var title by rememberSaveable(initialTitle, stateSaver = TextFieldValue.Saver)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 147–149 | 1 | `var content by rememberSaveable(initialContent, stateSaver = TextFieldValue.Saver)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 155–159 | 1 | `var newAttachments by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 167–171 | 1 | `var removedExistingAttachments by remember(initialTitle, initialContent)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 172–178 | 1 | `val visibleExistingAttachments = existingAttachments.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 174–177 | 2 | `removedExistingAttachments.any` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 184–186 | 1 | `var mediaRecorder by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 187–189 | 1 | `var recordingFile by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 190–192 | 1 | `var isRecording by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 193–195 | 1 | `var recordingError by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 197–199 | 1 | `by remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 205–247 | 1 | `fun startRecording()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 206–209 | 2 | `if (!hasMicrophone)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 210–235 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 213–216 | 3 | `if (!voiceDirectory.exists() && !voiceDirectory.mkdirs())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 218–220 | 3 | `val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 220–223 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 235–246 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 238–240 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 240–241 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 248–287 | 1 | `fun stopRecording()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 251–253 | 2 | `if (recorder == null \|\| file == null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 254–271 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 263–269 | 3 | `if (file.exists() && file.length() > 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 271–286 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 273–275 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 275–276 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 293–313 | 1 | `DisposableEffect(Unit)` | Efecto de Compose que exige liberar recursos mediante `onDispose` cuando cambia la clave o el composable abandona la composición. |
| 294–312 | 2 | `onDispose` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 295–303 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 296–301 | 4 | `if (isRecording)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 297–299 | 5 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 299–300 | 5 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 303–304 | 3 | `} catch (ignored: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 307–309 | 3 | `if (isRecording)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 320–330 | 1 | `)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 321–326 | 2 | `if (granted)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 322–325 | 3 | `if (startRecordingAfterPermission)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 326–329 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 331–344 | 1 | `fun requestVoiceRecording()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 332–335 | 2 | `if (!hasMicrophone)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 338–340 | 2 | `if (hasPermission)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 340–343 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 350–367 | 1 | `val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments())` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 351–360 | 2 | `val newItems = uris.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 352–354 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 354–355 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 361–363 | 2 | `newAttachments = (newAttachments + newItems).distinctBy` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 364–366 | 2 | `if (newItems.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 373–390 | 1 | `val videoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments())` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 374–383 | 2 | `val newItems = uris.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 375–377 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 377–378 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 384–386 | 2 | `newAttachments = (newAttachments + newItems).distinctBy` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 387–389 | 2 | `if (newItems.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 396–413 | 1 | `val audioPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments())` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 397–406 | 2 | `val newItems = uris.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 398–400 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 400–401 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 407–409 | 2 | `newAttachments = (newAttachments + newItems).distinctBy` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 410–412 | 2 | `if (newItems.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 419–436 | 1 | `val filePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments())` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 420–429 | 2 | `val newItems = uris.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 421–423 | 3 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 423–424 | 3 | `} catch (_: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 430–432 | 2 | `newAttachments = (newAttachments + newItems).distinctBy` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 433–435 | 2 | `if (newItems.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 442–804 | 1 | `MaterialTheme(colorScheme = MaterialTheme.colorScheme, typography = appTypography, shapes = MaterialTheme.shapes)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 444–485 | 2 | `topBar =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 450–464 | 3 | `navigationIcon =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 451–463 | 4 | `Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 452–458 | 5 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 454–456 | 6 | `if (isRecording)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 458–462 | 5 | `})` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 465–471 | 3 | `title =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 466–468 | 4 | `Text(text = if (isEditing)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 468–470 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 472–484 | 3 | `actions =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 473–483 | 4 | `Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 475–478 | 5 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 478–482 | 5 | `})` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 486–803 | 2 | `)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 487–802 | 3 | `Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 489–801 | 4 | `horizontal = 18.dp))` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 498–504 | 5 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 498–500 | 6 | `onValueChange = { newValue -> if (newValue.text != title.text)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 500–502 | 6 | `} else if (newValue.selection != title.selection)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 506–508 | 5 | `placeholder =` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 523–529 | 5 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 523–525 | 6 | `onValueChange = { newValue -> if (newValue.text != content.text)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 525–527 | 6 | `} else if (newValue.selection != content.selection)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 532–534 | 5 | `placeholder =` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 541–543 | 5 | `val editorLinkUrls = remember(content.text)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 544–550 | 5 | `if (editorLinkUrls.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 546–549 | 6 | `editorLinkUrls.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 569–588 | 5 | `verticalArrangement = Arrangement.spacedBy(10.dp))` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 570–587 | 6 | `editorColorOptions.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 572–574 | 7 | `width = if (selected) 3.dp else 1.dp, color = if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 574–576 | 7 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 576–579 | 7 | `}), onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 579–586 | 7 | `})` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 580–585 | 8 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 581–584 | 9 | `if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 598–678 | 5 | `if (visibleExistingAttachments.isNotEmpty() \|\| newAttachments.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 602–676 | 6 | `verticalArrangement = Arrangement.spacedBy(8.dp))` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 611–638 | 7 | `visibleExistingAttachments.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 613–617 | 8 | `val previewDelay = when (settings.performanceMode)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 629–637 | 8 | `onRemove =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 630–633 | 9 | `if (removedExistingAttachments.none` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 633–636 | 9 | `})` | Ámbito delimitado por llaves en profundidad 9. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 644–675 | 7 | `newAttachments.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 647–651 | 8 | `val previewDelay = when (settings.performanceMode)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 656–674 | 8 | `onRemove =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 661–669 | 9 | `if (attachment.type == "voice")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 663–668 | 10 | `if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 664–667 | 11 | `uri.path?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 671–673 | 9 | `newAttachments = newAttachments.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 685–698 | 5 | `horizontalArrangement = Arrangement.spacedBy(8.dp))` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 689–691 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 695–697 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 706–719 | 5 | `horizontalArrangement = Arrangement.spacedBy(8.dp))` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 710–712 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 716–718 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 726–742 | 5 | `if (isRecording)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 728–731 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 732–737 | 6 | `shape = RoundedCornerShape(14.dp))` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 742–758 | 5 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 745–748 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 752–757 | 6 | `border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)))` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 759–763 | 5 | `if (recordingError != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 771–799 | 5 | `horizontalArrangement = Arrangement.spacedBy(10.dp))` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 774–777 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 782–784 | 6 | `border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)))` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 788–791 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 792–798 | 6 | `shape = RoundedCornerShape(14.dp))` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 793–795 | 7 | `Text(if (isEditing)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 795–797 | 7 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 813–829 | 0 | `private fun AttachmentButton(modifier: Modifier = Modifier, text: String, icon: ImageVector, onClick: () -> Unit)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 816–819 | 1 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 823–828 | 1 | `border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 838–947 | 0 | `)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 842–846 | 1 | `Card(modifier = Modifier.size(width = 108.dp, height = 108.dp).clickable` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 848–850 | 1 | `colors = CardDefaults.cardColors(containerColor = if (isBorderlessPreview)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 850–852 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 853–946 | 1 | `elevation = CardDefaults.cardElevation(defaultElevation = 0.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 854–945 | 2 | `Box(modifier = Modifier.fillMaxSize())` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 855–925 | 3 | `when (attachment.type)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 859–892 | 4 | `"image" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 861–866 | 5 | `produceState(initialValue = previewDelayMillis <= 0L, key1 = attachment.uri, key2 = previewDelayMillis)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 862–864 | 6 | `if (previewDelayMillis > 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 867–884 | 5 | `if (previewReady)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 869–874 | 6 | `produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = performanceMode)` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 870–873 | 7 | `value = AttachmentPreviewCache.withPreviewPermit` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 875–878 | 6 | `if (imagePreview != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 878–883 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 879–882 | 7 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 884–891 | 5 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 886–890 | 6 | `contentAlignment = Alignment.Center)` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 896–900 | 4 | `"video" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 904–908 | 4 | `"audio" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 912–916 | 4 | `"voice" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 920–924 | 4 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 935–944 | 3 | `if (onRemove != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 937–939 | 4 | `.colorScheme.surface.copy(alpha = 0.9f)).clickable` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 940–943 | 4 | `contentAlignment = Alignment.Center)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 950–968 | 0 | `private fun AttachmentIconPreview(icon: ImageVector, title: String, name: String?)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 953–967 | 1 | `horizontalAlignment = Alignment.CenterHorizontally)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 961–966 | 2 | `if (!name.isNullOrBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 975–993 | 0 | `private fun getFileName(context: Context, uri: Uri): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 976–980 | 1 | `if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 977–979 | 2 | `return uri.path?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 981–990 | 1 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 982–989 | 2 | `context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 984–986 | 3 | `if (index >= 0 && cursor.moveToFirst())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 986–988 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 990–992 | 1 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 995–1004 | 0 | `private fun Typography.withEditorFontFamily(fontFamily: FontFamily): Typography` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
