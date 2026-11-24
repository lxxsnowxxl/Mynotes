package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mynotes.data.Note
import com.example.mynotes.ui.components.AppAlertDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID
import kotlin.math.roundToInt

@OptIn(FlowPreview::class)
@Composable
internal fun PlusReadingTools(note: Note, scroll: ScrollState) {
    val context = LocalContext.current
    val store = remember { StudioStore.get(context) }
    val marks = rememberStudioTable(store, "reading_marks")
    val library = remember(note.id) { PlusProfiles.currentId }
    val scope = rememberCoroutineScope()
    var dialog by remember(note.id) { mutableIntStateOf(0) }
    var comment by remember(note.id) { mutableStateOf<PlusAlgorithms.Paragraph?>(null) }
    var selected by remember(note.id) { mutableStateOf<String?>(null) }
    var restored by remember(note.id) { mutableStateOf(false) }

    val paragraphs = remember(note.content) { PlusAlgorithms.paragraphs(note.content) }
    val noteMarks = remember(marks, note.id) {
        marks.values.map { org.json.JSONObject(it) }.filter { it.optInt("noteId") == note.id }
    }
    val marksByStart = remember(noteMarks) { noteMarks.groupBy { it.optInt("start") } }
    val paragraphsByStart = remember(paragraphs) { paragraphs.associateBy { it.start } }
    val staleMarks = remember(noteMarks, paragraphsByStart) {
        noteMarks.filter { row -> paragraphsByStart[row.optInt("start")]?.text != row.optString("excerpt") }
    }
    val maxDialogHeight = (LocalConfiguration.current.screenHeightDp * 0.52f)
        .coerceIn(180f, 450f).dp

    LaunchedEffect(note.id) {
        store.load()
        val fraction = store.state.value.copy(library = library).get("reading", note.id.toString())
            .optDouble("fraction").coerceIn(0.0, 1.0)
        if (fraction > 0) {
            withTimeoutOrNull(3000) { snapshotFlow { scroll.maxValue }.first { it > 0 } }
            scroll.scrollTo((scroll.maxValue * fraction).roundToInt())
        }
        restored = true
    }
    LaunchedEffect(note.id, restored) {
        if (restored) snapshotFlow { scroll.value to scroll.maxValue }
            .debounce(600).collect { (value, maximum) ->
                if (maximum > 0) store.editInLibrary("reading", note.id.toString(), library) {
                    it.put("fraction", value.toDouble() / maximum)
                }
            }
    }
    fun readingFraction() = if (scroll.maxValue > 0) scroll.value.toDouble() / scroll.maxValue else 0.0
    DisposableEffect(note.id, scroll, library) {
        onDispose {
            if (restored) {
                val fraction = readingFraction()
                store.putInLibrary("reading", note.id.toString(), library) { it.put("fraction", fraction) }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(onClick = { dialog = 20 ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.weight(1f)) {
            Text("Bookmarks", maxLines = 1)
        }
        OutlinedButton(onClick = { dialog = 21 ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.weight(1f)) {
            Text("Comments", maxLines = 1)
        }
    }

    if (dialog == 19) {
        AppAlertDialog(
            onDismissRequest = { dialog = 0 },
            title = { Text("Reading position") },
            text = { Text("This note resumes at its saved scroll position. Current progress: ${(readingFraction() * 100).roundToInt()}%.") },
            confirmButton = {
                TextButton(onClick = { scope.launch { scroll.animateScrollTo(0) }; dialog = 0 ; UiSoundPlayer.playFallbackClick()}) {
                    Text("Start from top")
                }
            },
            dismissButton = { TextButton(onClick = { dialog = 0 ; UiSoundPlayer.playFallbackClick()}) { Text("Close") } }
        )
    }

    // Only one dialog is displayed at a time. The main list remains ready to reopen
    // after the comment editor or full paragraph preview is dismissed.
    if (dialog in 20..21 && comment == null && selected == null) {
        AppAlertDialog(
            onDismissRequest = { dialog = 0 },
            title = { Text(if (dialog == 20) "Paragraph bookmarks" else "Comments on text") },
            text = {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (dialog == 20) "Save a paragraph with the bookmark icon, or open its text."
                        else "Add a comment to any paragraph in this note.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = maxDialogHeight),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (paragraphs.isEmpty()) {
                            item { Text("This note does not contain any paragraphs yet.") }
                        }
                        itemsIndexed(paragraphs, key = { _, paragraph -> paragraph.start }) { index, paragraph ->
                            val rows = marksByStart[paragraph.start].orEmpty().filter {
                                it.optString("excerpt") == paragraph.text
                            }
                            val bookmark = rows.firstOrNull { it.optBoolean("bookmark") }
                            val comments = rows.filter { it.optString("comment").isNotBlank() }
                            OutlinedCard(Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Column(Modifier.weight(1f)) {
                                            Text("Paragraph ${index + 1}", style = MaterialTheme.typography.titleSmall)
                                            if (bookmark != null) {
                                                Text("Bookmarked", style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                        IconButton(onClick = {
                                            if (bookmark == null) {
                                                store.put("reading_marks", UUID.randomUUID().toString()) {
                                                    it.put("noteId", note.id)
                                                        .put("start", paragraph.start)
                                                        .put("excerpt", paragraph.text)
                                                        .put("bookmark", true)
                                                }
                                            } else {
                                                store.delete("reading_marks", bookmark.getString("id"))
                                            }
                                        ; UiSoundPlayer.playFallbackClick()}) {
                                            Icon(
                                                if (bookmark == null) Icons.Default.BookmarkBorder else Icons.Default.Bookmark,
                                                contentDescription = if (bookmark == null) "Bookmark paragraph ${index + 1}"
                                                    else "Remove bookmark from paragraph ${index + 1}"
                                            )
                                        }
                                    }
                                    Text(
                                        text = paragraph.text.trim(),
                                        maxLines = 4,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (dialog == 21) {
                                        comments.forEach { saved ->
                                            Column(Modifier.fillMaxWidth()) {
                                                Text(
                                                    "Comment: ${saved.optString("comment")}",
                                                    maxLines = 3,
                                                    overflow = TextOverflow.Ellipsis,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                TextButton(onClick = {
                                                    store.delete("reading_marks", saved.getString("id"))
                                                ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Delete)}) { Text("Delete comment") }
                                            }
                                        }
                                    } else if (comments.isNotEmpty()) {
                                        Text("${comments.size} comment(s)", style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    // Use two equally sized, short labels. Three TextButtons in
                                    // one row caused "Read fragment" to break letter by letter.
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { comment = paragraph ; UiSoundPlayer.playFallbackClick()},
                                            modifier = Modifier.weight(1f)
                                        ) { Text("Comment", maxLines = 1) }
                                        OutlinedButton(
                                            onClick = { selected = paragraph.text ; UiSoundPlayer.playFallbackClick()},
                                            modifier = Modifier.weight(1f)
                                        ) { Text("Read", maxLines = 1) }
                                    }
                                }
                            }
                        }
                        if (staleMarks.isNotEmpty()) {
                            item {
                                Text("Saved fragments from an earlier version",
                                    style = MaterialTheme.typography.titleSmall)
                            }
                            itemsIndexed(staleMarks, key = { _, row -> row.optString("id") }) { _, row ->
                                OutlinedCard(Modifier.fillMaxWidth()) {
                                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                        Text(
                                            row.optString("excerpt"), maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        val savedComment = row.optString("comment")
                                        if (savedComment.isNotBlank()) {
                                            Text("Comment: $savedComment", maxLines = 2,
                                                overflow = TextOverflow.Ellipsis)
                                        }
                                        TextButton(onClick = {
                                            store.delete("reading_marks", row.getString("id"))
                                        ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Delete)}) { Text("Remove saved fragment") }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { dialog = 0 ; UiSoundPlayer.playFallbackClick()}) { Text("Close") } }
        )
    }

    comment?.let { paragraph ->
        var draft by remember(note.id, paragraph.start) { mutableStateOf("") }
        AppAlertDialog(
            onDismissRequest = { comment = null },
            title = { Text("Add comment") },
            text = {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(paragraph.text.trim(), maxLines = 3, overflow = TextOverflow.Ellipsis)
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it.take(4000) },
                        label = { Text("Comment") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val text = draft.trim()
                    if (text.isNotEmpty()) {
                        store.put("reading_marks", UUID.randomUUID().toString()) {
                            it.put("noteId", note.id)
                                .put("start", paragraph.start)
                                .put("excerpt", paragraph.text)
                                .put("comment", text)
                        }
                    }
                    comment = null
                ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Save)}, enabled = draft.isNotBlank()) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { comment = null ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Cancel)}) { Text("Cancel") } }
        )
    }

    selected?.let { fragment ->
        AppAlertDialog(
            onDismissRequest = { selected = null },
            title = { Text("Paragraph text") },
            text = {
                Column(
                    Modifier.fillMaxWidth().heightIn(max = maxDialogHeight)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(fragment, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            },
            confirmButton = { TextButton(onClick = { selected = null ; UiSoundPlayer.playFallbackClick()}) { Text("Close") } }
        )
    }
}
