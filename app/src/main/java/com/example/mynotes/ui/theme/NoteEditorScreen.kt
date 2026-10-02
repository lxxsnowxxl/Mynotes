package com.example.mynotes.ui
import com.example.mynotes.ui.theme.rememberUiContentColors
import com.example.mynotes.ui.theme.rememberAppFontFamily
import com.example.mynotes.ui.components.AppCircularIconButton

import android.Manifest
import android.content.res.Configuration
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mynotes.R
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.PendingAttachment
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.components.LinkPreviewCard
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.components.extractLinkUrls
import com.example.mynotes.ui.components.extractEmbeddedLinkUrls
import com.example.mynotes.ui.components.stripEmbeddedLinkMetadata
import com.example.mynotes.ui.components.noteContentForStorage
import com.example.mynotes.ui.components.rememberPreviewReady
import com.example.mynotes.ui.openAttachmentViewer
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.MenuPreferencePolicy
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.util.uriDisplayName
import com.example.mynotes.ui.theme.typographyWithFontFamily
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.noteBackgroundColor
import java.io.File

private data class AttachmentAction(val text: String, val icon: ImageVector, val onClick: () -> Unit)

private fun removeProcessedUrl(value: TextFieldValue, url: String): TextFieldValue {
    val index = value.text.indexOf(url)
    if (index < 0) return value

    var removeStart = index
    var removeEnd = index + url.length
    val lineStart = value.text.lastIndexOf('\n', startIndex = (index - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
    val nextNewLine = value.text.indexOf('\n', startIndex = removeEnd)
    val lineEnd = if (nextNewLine < 0) value.text.length else nextNewLine
    if (value.text.substring(lineStart, lineEnd).trim() == url) {
        removeStart = lineStart
        removeEnd = if (nextNewLine >= 0) nextNewLine + 1 else lineEnd
    }

    val newText = value.text.removeRange(removeStart, removeEnd)
    val removedLength = removeEnd - removeStart
    fun shifted(position: Int): Int = when {
        position <= removeStart -> position
        position >= removeEnd -> position - removedLength
        else -> removeStart
    }.coerceIn(0, newText.length)

    return value.copy(
        text = newText,
        selection = TextRange(shifted(value.selection.start), shifted(value.selection.end))
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteEditorScreen(settings: AppSettings, initialTitle: String = "", initialContent: String = "", initialColor: String = "default",
    isEditing: Boolean = false,
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
    val editorFontFamily = rememberAppFontFamily(settings.font)
    val appTypography = typographyWithFontFamily(MaterialTheme.typography, editorFontFamily)
    var selectedColor by rememberSaveable(initialColor) {
        mutableStateOf(initialColor)
    }
    val editorBackground = noteBackgroundColor(selectedColor)
    val (editorTextColor, editorSecondaryTextColor, editorGraphicColor) = rememberUiContentColors(settings.textColor, editorBackground)
    val editorAccentOutline = ensureUiContrast(preferred = MaterialTheme.colorScheme.primary, background = editorBackground,
            minimumContrast = 3f)
    var title by rememberSaveable(initialTitle, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialTitle))
    }
    val initialVisibleContent = remember(initialContent) { stripEmbeddedLinkMetadata(initialContent) }
    var content by rememberSaveable(initialContent, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialVisibleContent))
    }
    var embeddedLinkState by rememberSaveable(initialContent) {
        mutableStateOf(extractEmbeddedLinkUrls(initialContent).take(3).joinToString("\u001F"))
    }
    val embeddedLinkUrls = remember(embeddedLinkState) {
        embeddedLinkState.split("\u001F").map { it.trim() }.filter { it.isNotBlank() }.distinct().take(3)
    }
    var newAttachments by remember {
        mutableStateOf<
                List<PendingAttachment>
                >(emptyList())
    }
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
            file.delete()
            recordingFile = null
            recordingError = recordingTooShortText
        }
    }
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
            if (isRecording) {
                recordingFile?.delete()
            }
            recordingFile = null
            isRecording = false
        }
    }
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
    fun addPickedAttachments(uris: List<Uri>, type: String) {
        val newItems = uris.map { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {
            }
            PendingAttachment(uri = uri, type = type, name = context.uriDisplayName(uri),
                mimeType = context.contentResolver.getType(uri))
        }
        newAttachments = (newAttachments + newItems).distinctBy { it.uri }
        if (newItems.isNotEmpty()) UiSoundPlayer.play(context = context, sound = UiSound.Attachment)
    }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
        addPickedAttachments(it, "image")
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
        addPickedAttachments(it, "video")
    }
    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
        addPickedAttachments(it, "audio")
    }
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
        addPickedAttachments(it, "file")
    }
    val editorScrollState = rememberScrollState()
    MaterialTheme(colorScheme = MaterialTheme.colorScheme, typography = appTypography, shapes = MaterialTheme.shapes) {
        Scaffold(containerColor = editorBackground,
            topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = editorBackground,
                                scrolledContainerColor = editorBackground,
                                titleContentColor = editorTextColor,
                                navigationIconContentColor = editorGraphicColor,
                                actionIconContentColor = editorGraphicColor),
                    navigationIcon = {
                        AppCircularIconButton(Icons.Default.ArrowBack, stringResource(R.string.back),
                            onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Back) {
                                if (isRecording) stopRecording()
                                onCancel()
                            }, containerColor = MaterialTheme.colorScheme.primaryContainer,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    },
                    title = {
                        Text(text = if (isEditing) {
                                    stringResource(R.string.edit_note)
                                } else {
                                    stringResource(R.string.new_note)
                                })
                    },
                    actions = {
                        AppCircularIconButton(Icons.Default.Check, stringResource(R.string.save), enabled = !isRecording,
                            onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Save) {
                                onSave(title.text, noteContentForStorage(content.text, embeddedLinkUrls), selectedColor, newAttachments, removedExistingAttachments)
                            }, containerColor = MaterialTheme.colorScheme.primaryContainer,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    })
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
                Column(modifier = Modifier.widthIn(max = 760.dp).fillMaxWidth().verticalScroll(editorScrollState).padding(
                            horizontal = 18.dp)) {
                Spacer(modifier = Modifier.height(12.dp))
                val editorFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = editorTextColor, unfocusedTextColor = editorTextColor,
                    focusedPlaceholderColor = editorSecondaryTextColor, unfocusedPlaceholderColor = editorSecondaryTextColor,
                    cursorColor = editorGraphicColor, focusedBorderColor = editorAccentOutline,
                    unfocusedBorderColor = editorGraphicColor, focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { newValue ->
                        UiSoundPlayer.playTextInput(context, title.text, newValue.text)
                        if (newValue.text == title.text && newValue.selection != title.selection) {
                            UiSoundPlayer.playThrottled(context = context, sound = UiSound.SliderTick, minimumIntervalMs = 55L)
                        }
                        title = newValue
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(stringResource(R.string.title))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = editorFieldColors)
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { newValue ->
                        UiSoundPlayer.playTextInput(context, content.text, newValue.text)
                        if (newValue.text == content.text && newValue.selection != content.selection) {
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
                    colors = editorFieldColors)
                val editorLinkUrls = remember(content.text, embeddedLinkState) {
                        (embeddedLinkUrls + extractLinkUrls(content.text)).distinct().take(3)
                    }
                if (editorLinkUrls.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    editorLinkUrls.forEach { linkUrl ->
                        LinkPreviewCard(
                            url = linkUrl,
                            compact = false,
                            textColorMode = settings.textColor,
                            onPreviewReady = { readyUrl ->
                                val currentEmbeddedLinks = embeddedLinkState.split("\u001F")
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }
                                    .distinct()
                                if (readyUrl !in currentEmbeddedLinks) {
                                    embeddedLinkState = (currentEmbeddedLinks + readyUrl).distinct().take(3).joinToString("\u001F")
                                }
                                if (content.text.contains(readyUrl)) {
                                    content = removeProcessedUrl(content, readyUrl)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(text = stringResource(R.string.mock_color_palette), color = editorTextColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                val editorColorOptions = MenuPreferencePolicy.colorOptions
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 10.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    editorColorOptions.forEach { option -> val selected = selectedColor == option.key
                        val optionColor = option.argb?.let { Color(it) } ?: MaterialTheme.colorScheme.background
                        Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = optionColor, border = BorderStroke(
                                width = if (selected) 3.dp else 1.dp, color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                }), onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Color) {
                                selectedColor = option.key
                            }) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (selected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = stringResource(option.labelRes),
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
                if (visibleExistingAttachments.isNotEmpty() || newAttachments.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        visibleExistingAttachments.forEachIndexed {
                                    index, attachment ->
                                val previewDelay = AppMotion.performanceValue(settings.performanceMode, 260L + index * 70L, 120L + index * 40L, 0L, 0L)
                                AttachmentPreview(
                                    attachment = PendingAttachment(uri = Uri.parse(attachment.uri),
                                            type = attachment.type,
                                            name = attachment.name),
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
                        newAttachments.forEachIndexed {
                                    index, attachment ->
                                val previewIndex = visibleExistingAttachments.size + index
                                val previewDelay = AppMotion.performanceValue(settings.performanceMode, 260L + previewIndex * 70L, 120L + previewIndex * 40L, 0L, 0L)
                                AttachmentPreview(
                                    attachment = attachment,
                                    previewDelayMillis = previewDelay,
                                    performanceMode = settings.performanceMode,
                                    onRemove = {
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
                AttachmentButtonRow(
                    AttachmentAction(stringResource(R.string.image), Icons.Default.Image) { imagePicker.launch(arrayOf("image/*")) },
                    AttachmentAction(stringResource(R.string.video), Icons.Default.Videocam) { videoPicker.launch(arrayOf("video/*")) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                AttachmentButtonRow(
                    AttachmentAction(stringResource(R.string.audio), Icons.Default.MusicNote) { audioPicker.launch(arrayOf("audio/*")) },
                    AttachmentAction(stringResource(R.string.file), Icons.Default.Description) { filePicker.launch(arrayOf("*/*")) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (isRecording) {
                    Button(modifier = Modifier.fillMaxWidth(),
                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.PlayPause, ::stopRecording),
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
                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Add, ::requestVoiceRecording),
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
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Cancel, onCancel),
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
                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Save) {
                            onSave(title.text, noteContentForStorage(content.text, embeddedLinkUrls), selectedColor, newAttachments, removedExistingAttachments)
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
                ScrollPositionCapsule(state = editorScrollState, modifier = Modifier.align(Alignment.CenterEnd), backgroundColor = editorBackground, preferredColor = editorGraphicColor)
            }
        }
    }
}

@Composable
private fun AttachmentButtonRow(first: AttachmentAction, second: AttachmentAction) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AttachmentButton(Modifier.weight(1f), first.text, first.icon, first.onClick)
        AttachmentButton(Modifier.weight(1f), second.text, second.icon, second.onClick)
    }
}

@Composable
private fun AttachmentButton(modifier: Modifier = Modifier, text: String, icon: ImageVector, onClick: () -> Unit) {
    val context = LocalContext.current
    OutlinedButton(modifier = modifier,
        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Add, onClick),
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
                "image" -> {
                    val previewReady = rememberPreviewReady(attachment.uri, previewDelayMillis)
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
                "video" -> {
                    AttachmentIconPreview(icon = Icons.Default.Videocam,
                        title = stringResource(R.string.video),
                        name = attachment.name)
                }
                "audio" -> {
                    AttachmentIconPreview(icon = Icons.Default.MusicNote,
                        title = stringResource(R.string.audio),
                        name = attachment.name)
                }
                "voice" -> {
                    AttachmentIconPreview(icon = Icons.Default.Mic,
                        title = stringResource(R.string.voice_note),
                        name = attachment.name)
                }
                else -> {
                    AttachmentIconPreview(icon = Icons.Default.Description,
                        title = stringResource(R.string.file),
                        name = attachment.name)
                }
            }
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
