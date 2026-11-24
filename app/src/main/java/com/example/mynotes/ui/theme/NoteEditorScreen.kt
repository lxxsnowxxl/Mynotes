package com.example.mynotes.ui

import com.example.mynotes.ui.components.AppAlertDialog
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.mutableLongStateOf
import com.example.mynotes.ui.workspace.*
import com.example.mynotes.data.workspace.NoteWorkspaceStore
import com.example.mynotes.data.workspace.RecoveredNoteDraft
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link

import com.example.mynotes.ui.components.appScreenBackground
import com.example.mynotes.ui.components.appScreenContainerColor
import com.example.mynotes.ui.theme.noteUsesGlobalBackground
import com.example.mynotes.ui.preview.LocalLayoutPreview
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
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mynotes.R
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.data.NoteDraft
import com.example.mynotes.data.NoteFolder
import com.example.mynotes.data.PendingAttachment
import com.example.mynotes.ui.notes.*
import androidx.compose.material3.AssistChip
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.components.LinkPreviewCard
import com.example.mynotes.ui.components.NotePaletteDialog
import com.example.mynotes.ui.components.NotePalettePickerField
import com.example.mynotes.ui.components.extractLinkUrls
import com.example.mynotes.ui.components.extractEmbeddedLinkUrls
import com.example.mynotes.ui.components.stripEmbeddedLinkMetadata
import com.example.mynotes.ui.components.noteContentForStorage
import com.example.mynotes.ui.components.rememberPreviewReady
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.util.uriDisplayName
import com.example.mynotes.ui.theme.typographyWithFontFamily
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.automaticUiTextColor
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.rememberAdaptiveUiButtonColors
import com.example.mynotes.ui.workspace.PaletteWorkspaceTheme
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
    onSave: (NoteDraft) -> Unit, onCancel: () -> Unit, onFocusModeChange: (Boolean) -> Unit = {},
    initialNote: Note? = null, initialFolderId: String? = initialNote?.folderId, folders: List<NoteFolder> = emptyList(), noteCatalog: List<Note> = emptyList(), workspaceDraftId:String?=null) {
    val context = LocalContext.current
    val layoutPreview = LocalLayoutPreview.current
    val accessStore=remember{com.example.mynotes.studio.StudioStore.get(context)}
    val access = com.example.mynotes.studio.rememberStudioRow(accessStore, "notes", initialNote?.id?.toString().orEmpty())
    LaunchedEffect(Unit){if(layoutPreview==null)accessStore.load()}
    if(initialNote!=null&&access.optString("remoteRole")=="viewer"){
        Column(Modifier.fillMaxSize().padding(24.dp)){Text("Shared notebook · read only");Button(onClick={com.example.mynotes.studio.StudioJobs.start("Make local copy"){com.example.mynotes.studio.StudioSync.copyLocal(context,initialNote.id)}; UiSoundPlayer.playFallbackClick()}){Text("Make editable local copy")};TextButton(onClick = { onCancel(); UiSoundPlayer.playFallbackClick() }){Text("Back")}};return
    }
    val recordingStartErrorText = stringResource(R.string.recording_start_error)
    val recordingTooShortText = stringResource(R.string.recording_too_short)
    val microphonePermissionRequiredText = stringResource(R.string.microphone_permission_required)
    val voiceNoteText = stringResource(R.string.voice_note)
    val microphoneUnavailableText = stringResource(R.string.microphone_not_available)
    val hasMicrophone = remember(context) {
            layoutPreview != null || context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
        }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val editorFontFamily = rememberAppFontFamily(settings.font)
    val appTypography = typographyWithFontFamily(MaterialTheme.typography, editorFontFamily)
    var selectedColor by rememberSaveable(initialColor) {
        mutableStateOf(initialColor)
    }
    var paletteDialogVisible by remember { mutableStateOf(false) }
    if (paletteDialogVisible) {
        NotePaletteDialog(
            selectedColor = selectedColor,
            onDismiss = { paletteDialogVisible = false },
            onColorSelected = { color ->
                selectedColor = color
            }
        )
    }
    val editorBackground = noteBackgroundColor(selectedColor)
    val useGlobalBackground = noteUsesGlobalBackground(selectedColor)
    val (editorTextColor, editorSecondaryTextColor, editorGraphicColor) = rememberUiContentColors(settings.textColor, editorBackground)
    var title by rememberSaveable(initialTitle, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialTitle))
    }
    val initialVisibleContent = remember(initialContent, initialNote?.formattingEnabled) {
        if (initialNote?.formattingEnabled == true) NoteMarkup.editorSource(initialContent) else stripEmbeddedLinkMetadata(initialContent)
    }
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
    val visibleExistingAttachments = existingAttachments.filter { it.type != "cover" }.filterNot {
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
        mutableStateOf(layoutPreview?.recording == true)
    }
    var recordingError by remember {
        mutableStateOf<String?>(null)
    }
    var startRecordingAfterPermission
            by remember {
                mutableStateOf(false)
            }
    fun startRecording() {
        if (layoutPreview != null) {
            isRecording = true
            return
        }
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
    var folderId by rememberSaveable { mutableStateOf(initialFolderId) }
    var coverIcon by rememberSaveable { mutableStateOf(initialNote?.coverIcon) }
    var coverX by rememberSaveable { mutableStateOf(initialNote?.coverX ?: 0.5f) }
    var coverY by rememberSaveable { mutableStateOf(initialNote?.coverY ?: 0.5f) }
    var pendingCoverUri by rememberSaveable { mutableStateOf<String?>(null) }
    var removeCover by rememberSaveable { mutableStateOf(false) }
    var coverDialog by remember { mutableStateOf(layoutPreview?.coverPicker == true) }
    var folderDialog by remember { mutableStateOf(false) }
    var templatesDialog by remember { mutableStateOf(layoutPreview?.templatePicker == true) }
    var richFormatting by rememberSaveable { mutableStateOf(initialNote?.formattingEnabled ?: false) }
    var bodyFocused by remember { mutableStateOf(layoutPreview?.formattingToolbar == true) }
    val bodyFocus = remember { FocusRequester() }
    val visibleCoverUri = pendingCoverUri ?: initialNote?.coverUri?.takeUnless { removeCover }
    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: SecurityException) { }
            pendingCoverUri = uri.toString(); coverIcon = null; removeCover = false; coverX = .5f; coverY = .5f
        }
    }
    val workspaceScope = rememberCoroutineScope()
    val draftKey = remember(initialNote?.id,workspaceDraftId) { NoteWorkspaceStore.key(initialNote?.id,workspaceDraftId) }
    val draftSession = rememberSaveable(initialNote?.id) { java.util.UUID.randomUUID().toString() }
    var draftReady by remember { mutableStateOf(false) }
    var savedEditor by remember { mutableStateOf(false) }
    var draftStatus by remember { mutableStateOf(0) }
    var recoveredDraft by remember { mutableStateOf<RecoveredNoteDraft?>(null) }
    var historyDialog by remember { mutableStateOf(false) }
    var linkedNoteDialog by remember { mutableStateOf(false) }
    val textUndo = remember { mutableStateListOf<EditorTextSnapshot>() }
    val textRedo = remember { mutableStateListOf<EditorTextSnapshot>() }
    fun editorSnapshot() = EditorTextSnapshot(title, content, richFormatting, selectedColor)
    var undoGroup by remember { mutableStateOf<String?>(null) }
    var undoAt by remember { mutableLongStateOf(0L) }
    fun rememberTextEdit(group: String? = null) {
        val nowEdit = android.os.SystemClock.uptimeMillis()
        if (group == null || group != undoGroup || nowEdit - undoAt > 700) textUndo.add(editorSnapshot())
        undoGroup = group; undoAt = nowEdit
        while (textUndo.size > 50 || (textUndo.size > 1 && textUndo.sumOf { it.title.text.length.toLong() + it.body.text.length } > 4_000_000L)) textUndo.removeAt(0)
        textRedo.clear()
    }
    fun applyTextSnapshot(snapshot: EditorTextSnapshot) {
        title = snapshot.title; content = snapshot.body; richFormatting = snapshot.formatted; selectedColor = snapshot.color
    }
    fun undoText() { undoGroup = null; if (textUndo.isNotEmpty()) { textRedo.add(editorSnapshot()); applyTextSnapshot(textUndo.removeAt(textUndo.lastIndex)) } }
    fun redoText() { undoGroup = null; if (textRedo.isNotEmpty()) { textUndo.add(editorSnapshot()); applyTextSnapshot(textRedo.removeAt(textRedo.lastIndex)) } }
    fun editorDraft() = NoteDraft(title.text, noteContentForStorage(content.text, embeddedLinkUrls), selectedColor,
        newAttachments + listOfNotNull(pendingCoverUri?.let { PendingAttachment(Uri.parse(it), "cover") }),
        removedExistingAttachments, folderId, coverIcon, coverX, coverY, removeCover, richFormatting, draftSession, android.os.SystemClock.elapsedRealtimeNanos())
    fun restoreDraft(draft:NoteDraft){
            title = TextFieldValue(draft.title)
            content = TextFieldValue(if (draft.formattingEnabled) NoteMarkup.editorSource(draft.content) else stripEmbeddedLinkMetadata(draft.content))
            embeddedLinkState = extractEmbeddedLinkUrls(draft.content).take(3).joinToString("\u001F")
            selectedColor = draft.color; newAttachments = draft.attachments.filter { it.type != "cover" }
            pendingCoverUri = draft.attachments.firstOrNull { it.type == "cover" }?.uri?.toString()
            removedExistingAttachments = draft.removedAttachments
            folderId = draft.folderId; coverIcon = draft.coverIcon; coverX = draft.coverX; coverY = draft.coverY
            removeCover = draft.removeCover; richFormatting = draft.formattingEnabled
    }
    LaunchedEffect(draftKey, draftSession) {
        if (layoutPreview == null) {
            NoteWorkspaceStore.begin(draftKey, draftSession)
            val saved = NoteWorkspaceStore.load(context, draftKey)
            if (saved != null && (saved.draft.title != initialTitle || saved.draft.content != initialContent ||
                saved.draft.color != initialColor || saved.draft.formattingEnabled != (initialNote?.formattingEnabled ?: false) || saved.draft.attachments.isNotEmpty() || saved.draft.removedAttachments.isNotEmpty() ||
                saved.draft.folderId != initialFolderId || saved.draft.removeCover || saved.draft.coverIcon != initialNote?.coverIcon ||
                saved.draft.coverX != (initialNote?.coverX ?: .5f) || saved.draft.coverY != (initialNote?.coverY ?: .5f))) { if(saved.session==draftSession){restoreDraft(saved.draft);draftStatus=2}else recoveredDraft = saved }
        }
        draftReady = true
    }
    val draftContents = editorDraft().copy(workspaceModifiedAt = 0L)
    val activeDraft = remember(draftContents) { draftContents.copy(workspaceModifiedAt = android.os.SystemClock.elapsedRealtimeNanos()) }
    LaunchedEffect(activeDraft) { savedEditor=false }
    val hasDraftChanges = activeDraft.title != initialTitle || activeDraft.content != initialContent ||
        activeDraft.color != initialColor || activeDraft.formattingEnabled != (initialNote?.formattingEnabled ?: false) || activeDraft.attachments.isNotEmpty() || activeDraft.removedAttachments.isNotEmpty() ||
        activeDraft.folderId != initialFolderId || activeDraft.removeCover || activeDraft.coverIcon != initialNote?.coverIcon ||
        activeDraft.coverX != (initialNote?.coverX ?: .5f) || activeDraft.coverY != (initialNote?.coverY ?: .5f)
    LaunchedEffect(activeDraft, draftReady, recoveredDraft, savedEditor) {
        if (layoutPreview != null || !draftReady || recoveredDraft != null || savedEditor || !hasDraftChanges) return@LaunchedEffect
        draftStatus = 1
        kotlinx.coroutines.delay(650)
        draftStatus = try { NoteWorkspaceStore.save(context, draftKey, draftSession, activeDraft); 2 }
        catch (cancelled: kotlinx.coroutines.CancellationException) { throw cancelled }
        catch (_: Exception) { 3 }
    }
    val latestDraft by rememberUpdatedState(activeDraft)
    val shouldSaveOnExit by rememberUpdatedState(draftReady && recoveredDraft == null && !savedEditor && hasDraftChanges && layoutPreview == null)
    WorkspaceOnStop { if (shouldSaveOnExit) NoteWorkspaceStore.saveOnExit(context.applicationContext, draftKey, draftSession, latestDraft) }
    DisposableEffect(draftKey, draftSession) {
        onDispose { if (shouldSaveOnExit) NoteWorkspaceStore.saveOnExit(context.applicationContext, draftKey, draftSession, latestDraft) }
    }
    if (recoveredDraft != null) {
        val recoveryShape = RoundedCornerShape(28.dp)
        val recoveryContainer = compositeUiColor(
            automaticUiTextColor(editorBackground).copy(alpha = 0.20f),
            editorBackground
        )
        val recoveryContent = automaticUiTextColor(recoveryContainer)
        val recoverySecondary = recoveryContent.copy(alpha = 0.76f)
        val recoveryAccent = ensureUiContrast(MaterialTheme.colorScheme.primary, recoveryContainer, 3f)
        val recoveryAccentContent = automaticUiTextColor(recoveryAccent)
        val recoveryPreview = compositeUiColor(recoveryContent.copy(alpha = 0.08f), recoveryContainer)
        val recoveryBorder = recoveryContent.copy(alpha = 0.26f)
        val recovered = recoveredDraft!!.draft
        val previewTitle = recovered.title.trim().take(80)
        val previewBody = stripEmbeddedLinkMetadata(recovered.content).trim().take(180)

        AppAlertDialog(
            onDismissRequest = onCancel,
            modifier = Modifier
                .shadow(24.dp, recoveryShape)
                .border(1.dp, recoveryBorder, recoveryShape),
            containerColor = recoveryContainer,
            icon = {
                Surface(
                    shape = CircleShape,
                    color = recoveryAccent,
                    contentColor = recoveryAccentContent,
                    tonalElevation = 0.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp).size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.workspace_draft_found),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = recoveryContent
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = stringResource(R.string.workspace_draft_recovery_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = recoverySecondary
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = recoveryPreview,
                        contentColor = recoveryContent,
                        border = BorderStroke(1.dp, recoveryBorder),
                        tonalElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.workspace_draft_preview),
                                style = MaterialTheme.typography.labelLarge,
                                color = recoveryAccent
                            )
                            if (previewTitle.isNotBlank()) {
                                Text(
                                    text = previewTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = recoveryContent,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (previewBody.isNotBlank()) {
                                Text(
                                    text = previewBody,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = recoverySecondary,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val draft = recoveredDraft!!.draft
                        title = TextFieldValue(draft.title)
                        content = TextFieldValue(if (draft.formattingEnabled) NoteMarkup.editorSource(draft.content) else stripEmbeddedLinkMetadata(draft.content))
                        embeddedLinkState = extractEmbeddedLinkUrls(draft.content).take(3).joinToString("\u001F")
                        selectedColor = draft.color; newAttachments = draft.attachments.filter { it.type != "cover" }
                        pendingCoverUri = draft.attachments.firstOrNull { it.type == "cover" }?.uri?.toString()
                        removedExistingAttachments = draft.removedAttachments
                        folderId = draft.folderId; coverIcon = draft.coverIcon; coverX = draft.coverX; coverY = draft.coverY
                        removeCover = draft.removeCover; richFormatting = draft.formattingEnabled
                        recoveredDraft = null; draftStatus = 2
                        UiSoundPlayer.playFallbackClick()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = recoveryAccent,
                        contentColor = recoveryAccentContent
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(stringResource(R.string.workspace_resume), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        workspaceScope.launch {
                            NoteWorkspaceStore.clearExisting(context, draftKey, draftSession)
                            recoveredDraft = null; draftStatus = 0
                        }
                        UiSoundPlayer.playFallbackClick()
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, recoveryBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = recoveryContent),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(stringResource(R.string.workspace_start_new), fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
    if (historyDialog && initialNote != null) NoteHistoryDialog(initialNote.id, onRestore = { revision ->
        rememberTextEdit()
        title = TextFieldValue(revision.title)
        content = TextFieldValue(if (revision.formatted) NoteMarkup.editorSource(revision.content) else stripEmbeddedLinkMetadata(revision.content))
        selectedColor = revision.color; richFormatting = revision.formatted
        embeddedLinkState = extractEmbeddedLinkUrls(revision.content).take(3).joinToString("\u001F")
        historyDialog = false
    }, onDismiss = { historyDialog = false })
    if (linkedNoteDialog) NotePickerDialog(noteCatalog.filter { it.id != initialNote?.id }, allowNone = false,
        onSelect = { note ->
            if (note != null) {
                rememberTextEdit()
                val inserted = InternalNoteLinks.text(note)
                val start = content.selection.min
                val end = content.selection.max
                val result = content.text.replaceRange(start, end, inserted)
                content = TextFieldValue(result, TextRange(start + inserted.length))
            }
            linkedNoteDialog = false
        }, onDismiss = { linkedNoteDialog = false })
    WorkspaceDropReceiver(onAssets = { assets ->
        newAttachments = newAttachments + assets.map { PendingAttachment(it.uri, it.attachmentType, it.name, it.mimeType) }
    }, onError = { android.widget.Toast.makeText(context, R.string.workspace_drop_error, android.widget.Toast.LENGTH_SHORT).show() })


    if (coverDialog) CoverPickerDialog(visibleCoverUri, coverIcon, coverX, coverY,
        onPickImage = { coverPicker.launch(arrayOf("image/*")) },
        onIcon = { coverIcon = it; pendingCoverUri = null; removeCover = true },
        onCrop = { x, y -> coverX = x; coverY = y }, onDismiss = { coverDialog = false })
    if (folderDialog) FolderPickerDialog(folders, onSelect = { folderId = it; folderDialog = false }, onDismiss = { folderDialog = false })
    if (templatesDialog) TemplatePickerDialog(onSelect = { name, body ->
        rememberTextEdit(); title = TextFieldValue(name); content = TextFieldValue(body); richFormatting = true; templatesDialog = false
    }, onDismiss = { templatesDialog = false })
    fun saveEditor() {
        if (isRecording || recoveredDraft != null) return
        if (!isEditing && title.text.isBlank() && content.text.isBlank() && embeddedLinkUrls.isEmpty() &&
            newAttachments.isEmpty() && pendingCoverUri == null && coverIcon == null) return
        val finalDraft = editorDraft()
        if (draftReady && layoutPreview == null) NoteWorkspaceStore.saveOnExit(context.applicationContext, draftKey, draftSession, finalDraft)
        savedEditor = true
        onSave(finalDraft)
    }
    val canSave = !isRecording && (isEditing || title.text.isNotBlank() ||
        content.text.isNotBlank() || embeddedLinkUrls.isNotEmpty() || newAttachments.isNotEmpty() ||
        pendingCoverUri != null || coverIcon != null)
    val saveColors = rememberAdaptiveUiButtonColors(
        MaterialTheme.colorScheme.primary, editorBackground, settings.textColor
    )
    val saveContainer = saveColors.container
    val saveContent = saveColors.content
    val editorScrollState = rememberScrollState()
    var focusMode by rememberSaveable { mutableStateOf(settings.editorFocusMode) }
    var toolsExpanded by rememberSaveable { mutableStateOf(false) }
    fun setFocus(value: Boolean) {
        focusMode = value
        toolsExpanded = !value
        onFocusModeChange(value)
    }
    PaletteWorkspaceTheme(settings.surfacePanelIntensity, typography = appTypography) {
        Scaffold(modifier = Modifier.imePadding().appScreenBackground(editorBackground, editorTextColor, useGlobalStyle = useGlobalBackground)
                .then(workspaceShortcuts(::saveEditor, ::undoText, ::redoText)), containerColor = Color.Transparent,
            contentColor = editorTextColor,
            topBar = {
                TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = appScreenContainerColor(editorBackground, useGlobalStyle = useGlobalBackground),
                                scrolledContainerColor = appScreenContainerColor(editorBackground, useGlobalStyle = useGlobalBackground),
                                titleContentColor = editorTextColor,
                                navigationIconContentColor = editorGraphicColor,
                                actionIconContentColor = editorGraphicColor),
                    navigationIcon = {
                        AppCircularIconButton(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back),
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
                                }, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    actions = {
                        if (!isEditing && title.text.isBlank() && content.text.isBlank()) IconButton(onClick = { templatesDialog = true ; UiSoundPlayer.playFallbackClick()}) {
                            Icon(Icons.Default.ViewQuilt, stringResource(R.string.organize_templates))
                        }
                        Button(
                            onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Save, ::saveEditor),
                            enabled = canSave,
                            modifier = Modifier.padding(end = 8.dp).heightIn(min = 48.dp).widthIn(min = 96.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = saveContainer,
                                contentColor = saveContent,
                                disabledContainerColor = compositeUiColor(
                                    editorTextColor.copy(alpha = 0.12f), editorBackground
                                ),
                                disabledContentColor = editorTextColor.copy(alpha = 0.50f)
                            ),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    })
            }
        ) { paddingValues ->
            // Save floats over the workspace: no bottom app bar or reserved blank strip.
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AdaptiveWorkspace(modifier = Modifier.fillMaxSize(),
                expandedTools = toolsExpanded || isRecording, forceSinglePane = focusMode,
                toolPanelIntensity = settings.surfacePanelIntensity,
                compactTools = true, toolsDrawer = true, drawerEnabled = !isRecording,
                drawerEntranceHint = !isEditing,
                onDrawerOpenChange = { open ->
                    if (open && focusMode) setFocus(false)
                    else if (!isRecording) toolsExpanded = open
                },
                content = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {

                Column(modifier = Modifier.widthIn(max = 760.dp).fillMaxWidth().verticalScroll(editorScrollState).padding(
                            start = NoteToolsDrawerGeometry.contentStartInsetDp.dp, end = 18.dp)) {
                if (visibleCoverUri != null || coverIcon != null) {
                    NoteCover(Note(title = "", content = "", coverUri = visibleCoverUri, coverIcon = coverIcon, coverX = coverX, coverY = coverY),
                        Modifier.fillMaxWidth().height(160.dp), settings.performanceMode)
                    Spacer(Modifier.height(12.dp))
                }
                val editorFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = editorTextColor, unfocusedTextColor = editorTextColor,
                    focusedPlaceholderColor = editorSecondaryTextColor, unfocusedPlaceholderColor = editorSecondaryTextColor,
                    cursorColor = editorGraphicColor, focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent, focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { newValue ->
                        UiSoundPlayer.playTextInput(context, title.text, newValue.text)
                        if (newValue.text == title.text && newValue.selection != title.selection) {
                            UiSoundPlayer.playThrottled(context = context, sound = UiSound.SliderTick, minimumIntervalMs = 55L)
                        }
                        if (newValue.text != title.text) rememberTextEdit("title")
                        title = newValue
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.title), style = MaterialTheme.typography.headlineMedium) },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(fontFamily = editorFontFamily, color = editorTextColor),
                    shape = RoundedCornerShape(16.dp),
                    colors = editorFieldColors)
                AssistChip(onClick = { folderDialog = true ; UiSoundPlayer.playFallbackClick()},
                    label = { Text(folders.firstOrNull { it.id == folderId }?.name ?: stringResource(R.string.organize_unfiled)) },
                    leadingIcon = { Icon(Icons.Default.Folder, null, Modifier.size(18.dp)) })
                Spacer(modifier = Modifier.height(14.dp))
                com.example.mynotes.studio.PlusInlineLinks(content.text,content.selection.end,noteCatalog){value,caret->rememberTextEdit();richFormatting=true;content=TextFieldValue(value,TextRange(caret))}
                OutlinedTextField(
                    value = content,
                    onValueChange = { newValue ->
                        UiSoundPlayer.playTextInput(context, content.text, newValue.text)
                        if (newValue.text == content.text && newValue.selection != content.selection) {
                            UiSoundPlayer.playThrottled(context = context, sound = UiSound.SliderTick, minimumIntervalMs = 45L)
                        }
                        if (newValue.text != content.text) rememberTextEdit("body")
                        content = newValue
                    },
                    modifier = Modifier.fillMaxWidth().focusRequester(bodyFocus).onFocusChanged { bodyFocused = it.isFocused }
                        .heightIn(min = if (isLandscape) 200.dp else 320.dp,
                            max = if (isLandscape) 400.dp else 720.dp),
                    visualTransformation = remember(editorGraphicColor, settings.fontSize, richFormatting) {
                        if (richFormatting) NoteMarkupTransformation(editorGraphicColor, settings.fontSize) else androidx.compose.ui.text.input.VisualTransformation.None
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = editorFontFamily, fontSize = settings.fontSize.sp,
                        lineHeight = (settings.fontSize * 1.6f).sp, color = editorTextColor),
                    placeholder = {
                        Text(stringResource(R.string.write_your_note))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = editorFieldColors)
                LinkedNotesSection(content.text, noteCatalog)
                val editorLinkUrls = remember(content.text, embeddedLinkState, richFormatting) {
                        (embeddedLinkUrls + extractLinkUrls(if (richFormatting) NoteMarkup.render(content.text).text else content.text)).distinct().take(3)
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

                Spacer(modifier = Modifier.height(80.dp))
                }
                    }
                }, supporting = {
                    val toolColors = MaterialTheme.colorScheme
                    val drawerColors = LocalWorkspaceDrawerColors.current
                    // Herramientas en una sola fila; sin huecos verticales entre acciones.
                    BoxWithConstraints(Modifier.fillMaxWidth()) {
                        val compactLink = maxWidth < 340.dp || LocalDensity.current.fontScale > 1.25f
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            WorkspaceToolIconButton(Icons.Default.Undo, stringResource(R.string.pdf_undo),
                                enabled = textUndo.isNotEmpty(), onClick = { undoText(); UiSoundPlayer.playFallbackClick(UiActionSound.Undo) })
                            WorkspaceToolIconButton(Icons.Default.Redo, stringResource(R.string.pdf_redo),
                                enabled = textRedo.isNotEmpty(), onClick = { redoText(); UiSoundPlayer.playFallbackClick(UiActionSound.Redo) })
                            if (compactLink) {
                                WorkspaceToolIconButton(Icons.Default.Link, stringResource(R.string.workspace_link_note),
                                    onClick = { linkedNoteDialog = true ; UiSoundPlayer.playFallbackClick()})
                            } else {
                                WorkspaceToolActionButton(onClick = { linkedNoteDialog = true ; UiSoundPlayer.playFallbackClick()}) {
                                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(5.dp))
                                    Text(stringResource(R.string.workspace_link_note), maxLines = 1)
                                }
                            }
                            if (initialNote != null) {
                                WorkspaceToolIconButton(Icons.Default.History, stringResource(R.string.workspace_history),
                                    onClick = { historyDialog = true ; UiSoundPlayer.playFallbackClick()})
                            }
                            if (draftStatus > 0) {
                                Spacer(Modifier.weight(1f))
                                Text(stringResource(when (draftStatus) {
                                    1 -> R.string.workspace_saving
                                    3 -> R.string.workspace_save_error
                                    else -> R.string.workspace_saved
                                }), style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }

                    // Formato disponible en la pestaña lateral, fuera de la zona inferior.
                    FormattingBar(modifier = Modifier.fillMaxWidth(), compact = true) { action ->
                        rememberTextEdit()
                        richFormatting = true
                        val edit = NoteMarkup.formatForToolbar(
                            content.text, content.selection.start, content.selection.end, action)
                        content = TextFieldValue(edit.text, TextRange(edit.selectionStart, edit.selectionEnd))
                        bodyFocus.requestFocus()
                    }

                    // El título y el selector de color comparten la misma línea.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = stringResource(R.string.mock_color_palette),
                            modifier = Modifier.widthIn(max = 124.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = toolColors.onSurface, maxLines = 2)
                        Box(Modifier.weight(1f)) {
                            NotePalettePickerField(selectedColor = selectedColor,
                                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                                    paletteDialogVisible = true
                                })
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(R.string.attachments),
                                modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            // Covers are distinct from attachments; keep this operation available.
                            WorkspaceToolActionButton(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                                coverDialog = true
                            }) {
                                Icon(Icons.Default.Collections, contentDescription = null,
                                    modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.organize_cover))
                            }
                        }
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
                        }
                        WorkspaceToolSection(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                AttachmentActionGrid(
                                    actions = listOf(
                                        AttachmentAction(stringResource(R.string.image), Icons.Default.Image) { imagePicker.launch(arrayOf("image/*")) },
                                        AttachmentAction(stringResource(R.string.video), Icons.Default.Videocam) { videoPicker.launch(arrayOf("video/*")) },
                                        AttachmentAction(stringResource(R.string.audio), Icons.Default.MusicNote) { audioPicker.launch(arrayOf("audio/*")) },
                                        AttachmentAction(stringResource(R.string.file), Icons.Default.Description) { filePicker.launch(arrayOf("*/*")) }
                                    ),
                                    contentColor = toolColors.onSurface,
                                    outlineColor = toolColors.outline
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                if (isRecording) {
                                    EditorVoiceButton(
                                        text = stringResource(R.string.stop_recording),
                                        icon = Icons.Default.Stop,
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                        outlineColor = MaterialTheme.colorScheme.error,
                                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.PlayPause, ::stopRecording),
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = stringResource(R.string.recording_voice_note),
                                        color = drawerColors?.sectionErrorContent ?: toolColors.error,
                                        modifier = Modifier.align(Alignment.CenterHorizontally))
                                } else {
                                    EditorVoiceButton(
                                        text = stringResource(R.string.record_voice_note),
                                        icon = Icons.Default.Mic,
                                        containerColor = toolColors.surfaceContainerHigh,
                                        contentColor = toolColors.onSurface,
                                        outlineColor = toolColors.outline,
                                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Add, ::requestVoiceRecording),
                                    )
                                }
                                if (recordingError != null) {
                                    Spacer(modifier = Modifier.height(7.dp))
                                    Text(text = recordingError!!,
                                        color = drawerColors?.sectionErrorContent ?: toolColors.error)
                                }
                            }
                        }
                    }

                })
            }
        }
    }
}

@Composable
private fun AttachmentActionGrid(actions: List<AttachmentAction>, contentColor: Color, outlineColor: Color) {
    val fontScale = LocalDensity.current.fontScale
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val columns = if (maxWidth >= 252.dp && fontScale <= 1.3f) 4 else 2
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            actions.chunked(columns).forEach { group ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    group.forEach { action ->
                        AttachmentButton(Modifier.weight(1f), action, contentColor, outlineColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentButton(modifier: Modifier, action: AttachmentAction, contentColor: Color, outlineColor: Color) {
    val context = LocalContext.current
    Surface(modifier = modifier,
        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Add, action.onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = contentColor,
        border = BorderStroke(1.dp, outlineColor.copy(alpha = 0.55f))) {
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(min = 74.dp).padding(horizontal = 4.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(action.icon, contentDescription = null, modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Text(action.text, style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}

@Composable
private fun EditorVoiceButton(
    text: String, icon: ImageVector, containerColor: Color, contentColor: Color,
    outlineColor: Color, onClick: () -> Unit
) {
    Surface(
        onClick = { onClick(); UiSoundPlayer.playFallbackClick() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, outlineColor.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.heightIn(min = 50.dp).padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp))
            Text(text, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun AttachmentPreview(attachment: PendingAttachment, previewDelayMillis: Long = 0L, performanceMode: String, onRemove: (() -> Unit)?
) {
    val context = LocalContext.current
    val layoutPreview = LocalLayoutPreview.current
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
                            produceState<android.graphics.Bitmap?>(initialValue = layoutPreview?.mediaBitmap, key1 = attachment.uri, key2 = performanceMode) {
                                if (layoutPreview != null) return@produceState
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
                        ; UiSoundPlayer.playFallbackClick()},
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
