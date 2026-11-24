package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mynotes.data.Note

@Composable internal fun PlusInlineLinks(text: String, cursor: Int, notes: List<Note>, onInsert: (String, Int) -> Unit) {
    val match = remember(text, cursor) { PlusAlgorithms.completion(text, cursor) } ?: return
    val suggestions = remember(match, notes) { notes.filter { it.title.contains(match.query, true) }.take(6) }
    if (suggestions.isEmpty()) return
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(8.dp)) {
            Text("Link a note", style = MaterialTheme.typography.labelMedium)
            suggestions.forEach { note -> TextButton(onClick = {
                val label = note.title.ifBlank { "Untitled note" }.replace("[", "(").replace("]", ")").replace('\n', ' ')
                val link = "[$label](mynotes://note/${note.id})"
                val end = if (text.substring(match.end).startsWith("]]")) match.end + 2 else match.end
                onInsert(text.replaceRange(match.start, end, link), match.start + link.length)
            ; UiSoundPlayer.playFallbackClick()}) { Text(note.title.ifBlank { "Untitled note" }) } }
        }
    }
}
