package com.example.mynotes.ui.workspace

import com.example.mynotes.ui.sound.UiSoundPlayer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.example.mynotes.MainActivity
import com.example.mynotes.R
import com.example.mynotes.data.Note
import com.example.mynotes.data.workspace.NoteRevision
import com.example.mynotes.data.workspace.NoteWorkspaceStore
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.preview.LocalLayoutPreview
import java.text.DateFormat
import java.util.Date

internal data class EditorTextSnapshot(val title: androidx.compose.ui.text.input.TextFieldValue,
    val body: androidx.compose.ui.text.input.TextFieldValue, val formatted: Boolean, val color: String)

internal object InternalNoteLinks {
    private val pattern = Regex("mynotes://note/([1-9][0-9]{0,9})(?![0-9])")
    fun targets(source: String): List<Int> = pattern.findAll(source).mapNotNull { it.groupValues[1].toIntOrNull() }.distinct().take(20).toList()
    fun text(note: Note) = note.title.ifBlank { note.content.take(60) } + "\nmynotes://note/" + note.id
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun LinkedNotesSection(source: String, notes: List<Note>) {
    val ids = remember(source) { InternalNoteLinks.targets(source) }
    val linked = remember(ids, notes) { ids.mapNotNull { id -> notes.firstOrNull { it.id == id && !it.isDeleted } } }
    val context = LocalContext.current
    if (linked.isNotEmpty()) {
        Text(stringResource(R.string.workspace_linked_notes), style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            linked.forEach { note ->
                AssistChip(onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse("mynotes://note/" + note.id)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                ; UiSoundPlayer.playFallbackClick()}, label = { Text(note.title.ifBlank { note.content.take(60) }, maxLines = 1, overflow = TextOverflow.Ellipsis) })
            }
        }
    }
}

@Composable
internal fun NoteHistoryDialog(noteId: Int, onRestore: (NoteRevision) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val preview = LocalLayoutPreview.current
    var revisions by remember(noteId) { mutableStateOf<List<NoteRevision>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(noteId) {
        if (preview == null) revisions = NoteWorkspaceStore.revisions(context, noteId)
        loading = false
    }
    AppAlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.workspace_history)) },
        text = { Column(Modifier.fillMaxWidth().heightIn(max = 440.dp)) {
            if (loading) CircularProgressIndicator()
            else if (revisions.isEmpty()) Text(stringResource(R.string.workspace_no_history))
            else LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(revisions) { revision ->
                    OutlinedCard(onClick = { onRestore(revision) ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(revision.savedAt)),
                                style = MaterialTheme.typography.labelSmall)
                            Text(revision.title, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text(revision.content.take(180), maxLines = 4, overflow = TextOverflow.Ellipsis)
                            Text(stringResource(R.string.workspace_restore_version), style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        } }, confirmButton = { TextButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.workspace_done)) } })
}

