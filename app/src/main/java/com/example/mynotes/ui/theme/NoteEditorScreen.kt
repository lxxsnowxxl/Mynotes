package com.example.mynotes.ui

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mynotes.R
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.PendingAttachment
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.ui.components.LinkPreviewCard
import com.example.mynotes.ui.components.extractLinkUrls
import com.example.mynotes.ui.openAttachmentViewer
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.noteBackgroundColor
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
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

/*
 * ==========================================================
 * BOTÓN DE ADJUNTO
 * ==========================================================
 */
@Composable
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

/*
 * ==========================================================
 * MINIATURA DE ADJUNTO
 * ==========================================================
 */
@Composable
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

@Composable
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

/*
 * ==========================================================
 * OBTENER EL NOMBRE REAL DEL ARCHIVO
 * ==========================================================
 */
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
