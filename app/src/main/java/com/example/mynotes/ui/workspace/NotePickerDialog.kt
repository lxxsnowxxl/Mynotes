package com.example.mynotes.ui.workspace

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.example.mynotes.R
import com.example.mynotes.data.Note
import com.example.mynotes.ui.components.AppAlertDialog

@Composable
internal fun NotePickerDialog(notes: List<Note>, allowNone: Boolean = true, onSelect: (Note?) -> Unit, onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val matching = remember(notes, query) { notes.filter { !it.isDeleted && (query.isBlank() || it.title.contains(query, true) || it.content.contains(query, true)) } }
    AppAlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.workspace_link_note)) },
        text = { Column(Modifier.fillMaxWidth().heightIn(max = 440.dp)) {
            OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true,
                label = { Text(stringResource(R.string.workspace_find)) })
            if (allowNone) TextButton(onClick = { onSelect(null) ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.workspace_no_link)) }
            LazyColumn { items(matching, key = { it.id }) { note ->
                TextButton(onClick = { onSelect(note) ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.fillMaxWidth()) {
                    Text(note.title.ifBlank { note.content.take(60) }, Modifier.fillMaxWidth(), maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            } }
            if (matching.isEmpty()) Text(stringResource(R.string.workspace_no_results), Modifier.padding(12.dp))
        } }, confirmButton = { TextButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.workspace_done)) } })
}
