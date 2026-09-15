# NoteEditorScreen.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/NoteEditorScreen.kt`  
**SHA-256 actual del archivo, sin modificar:** `3f744acd955a4e7ebfdfa783ddd461a7abdd40cfa337f76cc16aeb282a881cf9`  
**Líneas del código real:** 1010  
**Estado respecto de la documentación anterior:** **archivo modificado desde la instantánea anterior**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

Pantalla Compose para crear o editar notas. Gestiona texto, color, adjuntos, grabación de voz y confirmación/cancelación.

**Cambios recientes cubiertos por esta revisión.** El cambio más reciente centra adaptativamente los círculos de color con FlowRow para que cada fila quede centrada en cualquier ancho de pantalla sin alterar tamaño, selección ni persistencia.

## 2. Package e imports

El package declarado es `com.example.mynotes.ui`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **102 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`android.Manifest`, `android.content.Context`, `android.content.res.Configuration`, `android.content.Intent`, `android.content.pm.PackageManager`, `android.media.MediaRecorder`, `android.os.Build`, `android.net.Uri`, `android.provider.OpenableColumns`, `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.compose.foundation.background`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.ExperimentalLayoutApi`, `androidx.compose.foundation.layout.FlowRow`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.ArrowBack`, `androidx.compose.material.icons.filled.Check`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.Image`, `androidx.compose.material.icons.filled.Mic`, `androidx.compose.material.icons.filled.MusicNote`, `androidx.compose.material.icons.filled.Stop`, `androidx.compose.material.icons.filled.Videocam`, `androidx.compose.material3.Button`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.Card`, `androidx.compose.material3.CardDefaults`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.OutlinedButton`, `androidx.compose.material3.OutlinedTextField`, `androidx.compose.material3.OutlinedTextFieldDefaults`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TopAppBar`, `androidx.compose.material3.TopAppBarDefaults`, `androidx.compose.material3.Typography`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.DisposableEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.produceState`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.saveable.rememberSaveable`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.graphics.vector.ImageVector`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalConfiguration`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.Font`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontStyle`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.text.input.TextFieldValue`, `androidx.compose.ui.unit.dp`, `androidx.core.content.ContextCompat`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.PendingAttachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.components.LinkPreviewCard`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.openAttachmentViewer`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`

### Kotlin Coroutines / extensiones

`kotlinx.coroutines.delay`

### Java / Kotlin estándar

`java.io.File`

## 3. Restricciones, límites e invariantes detectables

- **Llamada segura `?.`: 10 aparición/apariciones.** evita desreferenciar receptores nulos; si el receptor es `null`, la cadena se corta de forma segura.
- **Aserción `!!`: 2 aparición/apariciones.** convierte una suposición de no nulidad en una posible excepción si se incumple.
- **Guardias de API Android: 1 aparición/apariciones.** protegen llamadas cuya disponibilidad cambia según la versión de Android.
- **Límites visuales: 6 aparición/apariciones.** evitan crecimiento o reducción de UI fuera de los límites previstos.
- **Estado Compose: 36 aparición/apariciones.** introduce estado observado por Compose y, por tanto, puntos potenciales de recomposición.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `NoteEditorScreen` — fun, líneas 108–811

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
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 10.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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

**Firma/entrada.** `fun NoteEditorScreen(settings: AppSettings, initialTitle: String = "", initialContent: String = "", initialColor: String = "default", isEditing: Boolean = false, /* * Adjuntos que ya pertenecen a la nota. * * Se muestran al editar, pero NO se envían otra vez * como adjuntos nuevos al guardar. */ existingAttachments: List<Attachment> = emptyList(), onSave: (String, String, String, List<PendingAttachment>, List<Attachment>) -> Unit, onCancel: () -> Unit) {`

**Parámetros.**
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `initialTitle: String = ""` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `initialContent: String = ""` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `initialColor: String = "default"` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `isEditing: Boolean = false` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `/* * Adjuntos que ya pertenecen a la nota. * * Se muestran al editar` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `pero NO se envían otra vez * como adjuntos nuevos al guardar. */ existingAttachments: List<Attachment> = emptyList()` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `onSave: (String, String, String, List<PendingAttachment>, List<Attachment>) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onCancel: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; accede o prepara almacenamiento local/caché; ramifica o parametriza comportamiento según el perfil de rendimiento; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

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

**Firma/entrada.** `fun startRecording() {`

**Funcionamiento observable.** accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

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

**Firma/entrada.** `fun stopRecording() {`

**Funcionamiento observable.** accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

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

**Firma/entrada.** `fun requestVoiceRecording() {`

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.5 `AttachmentButton` — fun, líneas 819–835

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

**Firma/entrada.** `private fun AttachmentButton(modifier: Modifier = Modifier, text: String, icon: ImageVector, onClick: () -> Unit) {`

**Parámetros.**
- `modifier: Modifier = Modifier` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `text: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `icon: ImageVector` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onClick: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; expone o consume callbacks de interacción.

### 4.6 `AttachmentPreview` — fun, líneas 843–953

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

**Firma/entrada.** `private fun AttachmentPreview(attachment: PendingAttachment, previewDelayMillis: Long = 0L, performanceMode: String, onRemove: (() -> Unit)? ) {`

**Parámetros.**
- `attachment: PendingAttachment` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `previewDelayMillis: Long = 0L` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `performanceMode: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onRemove: (() -> Unit)?` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; integra el pipeline de miniaturas y caché de adjuntos; ramifica o parametriza comportamiento según el perfil de rendimiento.

### 4.7 `AttachmentIconPreview` — fun, líneas 956–974

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

**Firma/entrada.** `private fun AttachmentIconPreview(icon: ImageVector, title: String, name: String?) {`

**Parámetros.**
- `icon: ImageVector` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `title: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `name: String?` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.8 `getFileName` — fun, líneas 981–999

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

**Firma/entrada.** `private fun getFileName(context: Context, uri: Uri): String? {`

**Parámetros.**
- `context: Context` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.9 `Typography` — fun, líneas 1001–1010

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

**Firma/entrada.** `private fun Typography.withEditorFontFamily(fontFamily: FontFamily): Typography {`

**Parámetros.**
- `fontFamily: FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

## 5. Variables y propiedades, una por una

Se detectaron **54 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 118 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 119 | `recordingStartErrorText` | `local/pública por contexto val recordingStartErrorText: inferido` | `val` fija la referencia después de inicializarla; resuelve texto localizado desde recursos. **Inicialización visible:** `val recordingStartErrorText = stringResource(R.string.recording_start_error)` |
| 120 | `recordingTooShortText` | `local/pública por contexto val recordingTooShortText: inferido` | `val` fija la referencia después de inicializarla; resuelve texto localizado desde recursos. **Inicialización visible:** `val recordingTooShortText = stringResource(R.string.recording_too_short)` |
| 121 | `microphonePermissionRequiredText` | `local/pública por contexto val microphonePermissionRequiredText: inferido` | `val` fija la referencia después de inicializarla; resuelve texto localizado desde recursos. **Inicialización visible:** `val microphonePermissionRequiredText = stringResource(R.string.microphone_permission_required)` |
| 122 | `voiceNoteText` | `local/pública por contexto val voiceNoteText: inferido` | `val` fija la referencia después de inicializarla; resuelve texto localizado desde recursos. **Inicialización visible:** `val voiceNoteText = stringResource(R.string.voice_note)` |
| 123 | `microphoneUnavailableText` | `local/pública por contexto val microphoneUnavailableText: inferido` | `val` fija la referencia después de inicializarla; resuelve texto localizado desde recursos. **Inicialización visible:** `val microphoneUnavailableText = stringResource(R.string.microphone_not_available)` |
| 124 | `hasMicrophone` | `local/pública por contexto val hasMicrophone: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val hasMicrophone = remember(context) {` |
| 127 | `isLandscape` | `local/pública por contexto val isLandscape: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE` |
| 128 | `editorFontFamily` | `local/pública por contexto val editorFontFamily: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val editorFontFamily = com.example.mynotes.ui.theme.appFontFamily(settings.font)` |
| 129 | `appTypography` | `local/pública por contexto val appTypography: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val appTypography = MaterialTheme.typography.withEditorFontFamily(editorFontFamily)` |
| 135 | `selectedColor` | `local/pública por contexto var selectedColor: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; intenta sobrevivir recreaciones compatibles mediante estado guardable; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var selectedColor by rememberSaveable(initialColor) {` |
| 138 | `editorBackground` | `local/pública por contexto val editorBackground: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val editorBackground = noteBackgroundColor(selectedColor)` |
| 139 | `editorTextColor` | `local/pública por contexto val editorTextColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val editorTextColor = resolveUiTextColor(value = settings.textColor, background = editorBackground)` |
| 140 | `editorSecondaryTextColor` | `local/pública por contexto val editorSecondaryTextColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val editorSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = editorBackground)` |
| 141 | `editorGraphicColor` | `local/pública por contexto val editorGraphicColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val editorGraphicColor = resolveUiGraphicColor(value = settings.textColor, background = editorBackground)` |
| 142 | `editorAccentOutline` | `local/pública por contexto val editorAccentOutline: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val editorAccentOutline = ensureUiContrast(preferred = MaterialTheme.colorScheme.primary, background = editorBackground,` |
| 144 | `title` | `local/pública por contexto var title: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; intenta sobrevivir recreaciones compatibles mediante estado guardable. **Inicialización visible:** `var title by rememberSaveable(initialTitle, stateSaver = TextFieldValue.Saver) {` |
| 147 | `content` | `local/pública por contexto var content: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; intenta sobrevivir recreaciones compatibles mediante estado guardable. **Inicialización visible:** `var content by rememberSaveable(initialContent, stateSaver = TextFieldValue.Saver) {` |
| 155 | `newAttachments` | `local/pública por contexto var newAttachments: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `var newAttachments by remember {` |
| 167 | `removedExistingAttachments` | `local/pública por contexto var removedExistingAttachments: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `var removedExistingAttachments by remember(initialTitle, initialContent) {` |
| 172 | `visibleExistingAttachments` | `local/pública por contexto val visibleExistingAttachments: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val visibleExistingAttachments = existingAttachments.filterNot {` |
| 184 | `mediaRecorder` | `local/pública por contexto var mediaRecorder: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `var mediaRecorder by remember {` |
| 187 | `recordingFile` | `local/pública por contexto var recordingFile: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `var recordingFile by remember {` |
| 190 | `isRecording` | `local/pública por contexto var isRecording: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var isRecording by remember {` |
| 193 | `recordingError` | `local/pública por contexto var recordingError: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `var recordingError by remember {` |
| 196 | `startRecordingAfterPermission` | `local/pública por contexto var startRecordingAfterPermission: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var startRecordingAfterPermission` |
| 212 | `voiceDirectory` | `local/pública por contexto val voiceDirectory: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val voiceDirectory = File(context.cacheDir, "voice_notes")` |
| 217 | `file` | `local/pública por contexto val file: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val file = File(voiceDirectory, "voice_${System.currentTimeMillis()}.m4a")` |
| 218 | `recorder` | `local/pública por contexto val recorder: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {` |
| 249 | `recorder` | `local/pública por contexto val recorder: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val recorder = mediaRecorder` |
| 250 | `file` | `local/pública por contexto val file: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val file = recordingFile` |
| 264 | `voiceAttachment` | `local/pública por contexto val voiceAttachment: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val voiceAttachment = PendingAttachment(uri = Uri.fromFile(file),` |
| 319 | `microphonePermissionLauncher` | `local/pública por contexto val microphonePermissionLauncher: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val microphonePermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()` |
| 336 | `hasPermission` | `local/pública por contexto val hasPermission: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==` |
| 350 | `imagePicker` | `local/pública por contexto val imagePicker: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->` |
| 351 | `newItems` | `local/pública por contexto val newItems: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val newItems = uris.map { uri ->` |
| 373 | `videoPicker` | `local/pública por contexto val videoPicker: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val videoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->` |
| 374 | `newItems` | `local/pública por contexto val newItems: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val newItems = uris.map { uri ->` |
| 396 | `audioPicker` | `local/pública por contexto val audioPicker: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val audioPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->` |
| 397 | `newItems` | `local/pública por contexto val newItems: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val newItems = uris.map { uri ->` |
| 419 | `filePicker` | `local/pública por contexto val filePicker: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val filePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->` |
| 420 | `newItems` | `local/pública por contexto val newItems: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val newItems = uris.map { uri ->` |
| 541 | `editorLinkUrls` | `local/pública por contexto val editorLinkUrls: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val editorLinkUrls = remember(content.text) {` |
| 554 | `editorColorOptions` | `local/pública por contexto val editorColorOptions: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val editorColorOptions = listOf(Triple("default", R.string.mock_color_default, MaterialTheme.colorScheme.background),` |
| 619 | `previewDelay` | `local/pública por contexto val previewDelay: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val previewDelay = when (settings.performanceMode) {` |
| 652 | `previewIndex` | `local/pública por contexto val previewIndex: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val previewIndex = visibleExistingAttachments.size + index` |
| 653 | `previewDelay` | `local/pública por contexto val previewDelay: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val previewDelay = when (settings.performanceMode) {` |
| 668 | `uri` | `local/pública por contexto val uri: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val uri = attachment.uri` |
| 820 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 845 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 846 | `isBorderlessPreview` | `local/pública por contexto val isBorderlessPreview: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val isBorderlessPreview = attachment.type == "image" \|\| attachment.type == "video" \|\| attachment.name?.substringAfterLast(".", "")` |
| 866 | `previewReady` | `local/pública por contexto val previewReady: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val previewReady by` |
| 874 | `imagePreview` | `local/pública por contexto val imagePreview: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val imagePreview by` |
| 989 | `index` | `local/pública por contexto val index: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **132 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 117–811 | 0 | ámbito/lambda anónima | `onSave: (String, String, String, List<PendingAttachment>, List<Attachment>) -> Unit, onCancel: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 124–126 | 1 | `remember` / memoria de composición | `val hasMicrophone = remember(context) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 135–137 | 1 | `remember` / memoria de composición | `var selectedColor by rememberSaveable(initialColor) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 144–146 | 1 | `remember` / memoria de composición | `var title by rememberSaveable(initialTitle, stateSaver = TextFieldValue.Saver) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 147–149 | 1 | `remember` / memoria de composición | `var content by rememberSaveable(initialContent, stateSaver = TextFieldValue.Saver) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 155–159 | 1 | `remember` / memoria de composición | `var newAttachments by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 167–171 | 1 | `remember` / memoria de composición | `var removedExistingAttachments by remember(initialTitle, initialContent) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 172–178 | 1 | ámbito/lambda anónima | `val visibleExistingAttachments = existingAttachments.filterNot {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 174–177 | 2 | ámbito/lambda anónima | `removedExistingAttachments.any {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 184–186 | 1 | `remember` / memoria de composición | `var mediaRecorder by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 187–189 | 1 | `remember` / memoria de composición | `var recordingFile by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 190–192 | 1 | `remember` / memoria de composición | `var isRecording by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 193–195 | 1 | `remember` / memoria de composición | `var recordingError by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 197–199 | 1 | `remember` / memoria de composición | `by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 205–247 | 1 | ámbito/lambda anónima | `fun startRecording() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 206–209 | 2 | condición `if` | `if (!hasMicrophone) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 210–235 | 2 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 213–216 | 3 | condición `if` | `if (!voiceDirectory.exists() && !voiceDirectory.mkdirs()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 218–220 | 3 | condición `if` | `val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 220–223 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 235–246 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 238–240 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 240–241 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 248–287 | 1 | ámbito/lambda anónima | `fun stopRecording() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 251–253 | 2 | condición `if` | `if (recorder == null \|\| file == null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 254–271 | 2 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 263–269 | 3 | condición `if` | `if (file.exists() && file.length() > 0L) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 271–286 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 273–275 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 275–276 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 293–313 | 1 | `DisposableEffect` / lifecycle Compose | `DisposableEffect(Unit) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 294–312 | 2 | ámbito/lambda anónima | `onDispose {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 295–303 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 296–301 | 4 | condición `if` | `if (isRecording) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 297–299 | 5 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 299–300 | 5 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 303–304 | 3 | `catch` / recuperación de error | `} catch (ignored: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 307–309 | 3 | condición `if` | `if (isRecording) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 320–330 | 1 | ámbito/lambda anónima | `) { granted ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 321–326 | 2 | condición `if` | `if (granted) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 322–325 | 3 | condición `if` | `if (startRecordingAfterPermission) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 326–329 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 331–344 | 1 | ámbito/lambda anónima | `fun requestVoiceRecording() {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 332–335 | 2 | condición `if` | `if (!hasMicrophone) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 338–340 | 2 | condición `if` | `if (hasPermission) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 340–343 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 350–367 | 1 | ámbito/lambda anónima | `val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 351–360 | 2 | ámbito/lambda anónima | `val newItems = uris.map { uri ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 352–354 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 354–355 | 3 | `catch` / recuperación de error | `} catch (_: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 361–363 | 2 | ámbito/lambda anónima | `newAttachments = (newAttachments + newItems).distinctBy {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 364–366 | 2 | condición `if` | `if (newItems.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 373–390 | 1 | ámbito/lambda anónima | `val videoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 374–383 | 2 | ámbito/lambda anónima | `val newItems = uris.map { uri ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 375–377 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 377–378 | 3 | `catch` / recuperación de error | `} catch (_: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 384–386 | 2 | ámbito/lambda anónima | `newAttachments = (newAttachments + newItems).distinctBy {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 387–389 | 2 | condición `if` | `if (newItems.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 396–413 | 1 | ámbito/lambda anónima | `val audioPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 397–406 | 2 | ámbito/lambda anónima | `val newItems = uris.map { uri ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 398–400 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 400–401 | 3 | `catch` / recuperación de error | `} catch (_: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 407–409 | 2 | ámbito/lambda anónima | `newAttachments = (newAttachments + newItems).distinctBy {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 410–412 | 2 | condición `if` | `if (newItems.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 419–436 | 1 | ámbito/lambda anónima | `val filePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uris ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 420–429 | 2 | ámbito/lambda anónima | `val newItems = uris.map { uri ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 421–423 | 3 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 423–424 | 3 | `catch` / recuperación de error | `} catch (_: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 430–432 | 2 | ámbito/lambda anónima | `newAttachments = (newAttachments + newItems).distinctBy {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 433–435 | 2 | condición `if` | `if (newItems.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 442–810 | 1 | ámbito/lambda anónima | `MaterialTheme(colorScheme = MaterialTheme.colorScheme, typography = appTypography, shapes = MaterialTheme.shapes) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 444–485 | 2 | ámbito/lambda anónima | `topBar = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 450–464 | 3 | ámbito/lambda anónima | `navigationIcon = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 451–463 | 4 | bloque UI Compose | `Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 454–456 | 6 | condición `if` | `if (isRecording) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 465–471 | 3 | ámbito/lambda anónima | `title = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 466–468 | 4 | condición `if` | `Text(text = if (isEditing) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 472–484 | 3 | ámbito/lambda anónima | `actions = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 473–483 | 4 | bloque UI Compose | `Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 486–809 | 2 | ámbito/lambda anónima | `) { paddingValues ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 487–808 | 3 | bloque UI Compose | `Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 498–500 | 6 | condición `if` | `onValueChange = { newValue -> if (newValue.text != title.text) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 498–504 | 5 | condición `if` | `onValueChange = { newValue -> if (newValue.text != title.text) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 500–502 | 6 | condición `if` | `} else if (newValue.selection != title.selection) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 523–525 | 6 | condición `if` | `onValueChange = { newValue -> if (newValue.text != content.text) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 523–529 | 5 | condición `if` | `onValueChange = { newValue -> if (newValue.text != content.text) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 525–527 | 6 | condición `if` | `} else if (newValue.selection != content.selection) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 541–543 | 5 | `remember` / memoria de composición | `val editorLinkUrls = remember(content.text) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 544–550 | 5 | condición `if` | `if (editorLinkUrls.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 546–549 | 6 | iteración funcional | `editorLinkUrls.forEach { linkUrl -> LinkPreviewCard(url = linkUrl, compact = false,` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 576–593 | 6 | iteración funcional | `editorColorOptions.forEach { option -> val selected = selectedColor == option.first` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 578–580 | 7 | condición `if` | `width = if (selected) 3.dp else 1.dp, color = if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 586–591 | 8 | bloque UI Compose | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 587–590 | 9 | condición `if` | `if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 604–684 | 5 | condición `if` | `if (visibleExistingAttachments.isNotEmpty() \|\| newAttachments.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 617–644 | 7 | iteración funcional | `visibleExistingAttachments.forEachIndexed {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 619–623 | 8 | selección `when` | `val previewDelay = when (settings.performanceMode) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 636–639 | 9 | condición `if` | `if (removedExistingAttachments.none {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 650–681 | 7 | iteración funcional | `newAttachments.forEachIndexed {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 653–657 | 8 | selección `when` | `val previewDelay = when (settings.performanceMode) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 667–675 | 9 | condición `if` | `if (attachment.type == "voice") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 669–674 | 10 | condición `if` | `if (uri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 732–748 | 5 | condición `if` | `if (isRecording) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 765–769 | 5 | condición `if` | `if (recordingError != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 799–801 | 7 | condición `if` | `Text(if (isEditing) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 819–835 | 0 | ámbito/lambda anónima | `private fun AttachmentButton(modifier: Modifier = Modifier, text: String, icon: ImageVector, onClick: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 822–825 | 1 | ámbito/lambda anónima | `onClick = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 829–834 | 1 | ámbito/lambda anónima | `border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 844–953 | 0 | ámbito/lambda anónima | `) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 848–852 | 1 | bloque UI Compose | `Card(modifier = Modifier.size(width = 108.dp, height = 108.dp).clickable {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 854–856 | 1 | condición `if` | `colors = CardDefaults.cardColors(containerColor = if (isBorderlessPreview) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 856–858 | 1 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 859–952 | 1 | ámbito/lambda anónima | `elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 860–951 | 2 | bloque UI Compose | `Box(modifier = Modifier.fillMaxSize()) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 861–931 | 3 | selección `when` | `when (attachment.type) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 868–870 | 6 | condición `if` | `if (previewDelayMillis > 0L) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 873–890 | 5 | condición `if` | `if (previewReady) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 881–884 | 6 | condición `if` | `if (imagePreview != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 885–888 | 7 | bloque UI Compose | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 941–950 | 3 | condición `if` | `if (onRemove != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 956–974 | 0 | ámbito/lambda anónima | `private fun AttachmentIconPreview(icon: ImageVector, title: String, name: String?) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 959–973 | 1 | ámbito/lambda anónima | `horizontalAlignment = Alignment.CenterHorizontally) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 967–972 | 2 | condición `if` | `if (!name.isNullOrBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 981–999 | 0 | ámbito/lambda anónima | `private fun getFileName(context: Context, uri: Uri): String? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 982–986 | 1 | condición `if` | `if (uri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 983–985 | 2 | ámbito/lambda anónima | `return uri.path?.let {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 987–996 | 1 | `try` / manejo de error | `return try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 988–995 | 2 | ámbito/lambda anónima | `context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 990–992 | 3 | condición `if` | `if (index >= 0 && cursor.moveToFirst()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 992–994 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 996–998 | 1 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 1001–1010 | 0 | ámbito/lambda anónima | `private fun Typography.withEditorFontFamily(fontFamily: FontFamily): Typography {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |

## 7. Side effects, rendimiento y lifecycle

- **Persistencia / base de datos:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Sistema de archivos / caché:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Audio / vibración:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Recomposición Compose:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Decodificación multimedia:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

El bloque de paleta usa `FlowRow` con arreglo horizontal centrado. La propiedad importante no es solo el tamaño del círculo, sino la **distribución por fila**: cuando el ancho disponible obliga a envolver elementos, cada fila se centra independientemente. Esto evita que el último renglón quede pegado a la izquierda en pantallas angostas y mantiene el mismo comportamiento en pantallas anchas.

No se modifica el identificador de color ni el callback de selección. Por tanto, centrar es una decisión de layout y no altera `selectedColor`, persistencia, contraste o el valor guardado en la nota.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**
