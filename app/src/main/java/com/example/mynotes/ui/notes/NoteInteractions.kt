package com.example.mynotes.ui.notes

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.ui.state.NoteOperation
import com.example.mynotes.ui.state.NoteUndo
import kotlin.math.roundToInt

@Composable
internal fun SwipeNoteContainer(enabled: Boolean, pinned: Boolean, archived: Boolean, animate: Boolean,
    onPin: () -> Unit, onArchive: () -> Unit, cornerRadius: Float = 16f, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var drag by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val threshold = with(LocalDensity.current) { 88.dp.toPx() }
    val offset = animateFloatAsState(drag, if (dragging || !animate) snap() else tween(180), label = "noteSwipe")
    val direction by remember(offset) { derivedStateOf { offset.value.compareTo(0f) } }
    val pinAction by rememberUpdatedState(onPin)
    val archiveAction by rememberUpdatedState(onArchive)
    val pinLabel = stringResource(if (pinned) R.string.organize_unpin else R.string.organize_pin)
    val archiveLabel = stringResource(if (archived) R.string.organize_unarchive else R.string.organize_archive)
    Box(modifier.fillMaxWidth().clip(RoundedCornerShape(cornerRadius.dp)).semantics {
        if (enabled) customActions = listOf(
            CustomAccessibilityAction(pinLabel) { pinAction(); true },
            CustomAccessibilityAction(archiveLabel) { archiveAction(); true })
    }) {
        if (direction != 0) Box(Modifier.matchParentSize().background(MaterialTheme.colorScheme.primaryContainer)) {
            Icon(if (direction > 0) Icons.Default.PushPin else Icons.Default.Archive, null,
                Modifier.align(if (direction > 0) Alignment.CenterStart else Alignment.CenterEnd).padding(24.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Box(Modifier.offset { IntOffset(offset.value.roundToInt(), 0) }.pointerInput(enabled, threshold) {
            if (enabled) detectHorizontalDragGestures(
                onDragStart = { dragging = true }, onDragCancel = { dragging = false; drag = 0f },
                onDragEnd = {
                    val distance = drag
                    dragging = false; drag = 0f
                    if (distance >= threshold) pinAction() else if (distance <= -threshold) archiveAction()
                }, onHorizontalDrag = { change, amount -> change.consume(); drag = (drag + amount).coerceIn(-threshold * 1.6f, threshold * 1.6f) })
        }) { content() }
    }
}

@Composable
internal fun SelectionToolbar(count: Int, archived: Boolean, onClose: () -> Unit, onAll: () -> Unit,
    onMove: () -> Unit, onColor: () -> Unit, onArchive: () -> Unit, onDelete: () -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onClose(); UiSoundPlayer.playFallbackClick() }) { Icon(Icons.Default.Close, stringResource(R.string.organize_cancel_selection)) }
            Text(pluralStringResource(R.plurals.organize_selected, count, count), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { onAll(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.organize_select_all)) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            listOf(Triple(Icons.Default.DriveFileMove, R.string.organize_move, onMove),
                Triple(Icons.Default.Palette, R.string.organize_color, onColor),
                Triple(Icons.Default.Archive, if (archived) R.string.organize_unarchive else R.string.organize_archive, onArchive),
                Triple(Icons.Default.Delete, R.string.organize_delete, onDelete)).forEach { (icon, label, click) ->
                TextButton(onClick = { click(); UiSoundPlayer.playFallbackClick() }, modifier = Modifier.heightIn(min = 48.dp)) { Icon(icon, null); Spacer(Modifier.width(6.dp)); Text(stringResource(label)) }
            }
        }
    }
}

@Composable
internal fun undoMessage(undo: NoteUndo): String = when (undo.operation) {
    NoteOperation.DELETE -> pluralStringResource(R.plurals.organize_deleted, undo.before.size, undo.before.size)
    NoteOperation.ARCHIVE -> stringResource(R.string.organize_archive_changed)
    NoteOperation.PIN -> stringResource(R.string.organize_pin_changed)
    NoteOperation.MOVE -> stringResource(R.string.organize_moved)
    NoteOperation.COLOR -> stringResource(R.string.organize_color_changed)
    NoteOperation.FOLDER_DELETE -> stringResource(R.string.organize_folder_deleted)
}
