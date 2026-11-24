package com.example.mynotes.ui.notes

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.data.CollectionPolicy
import com.example.mynotes.data.NoteFolder
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.state.NoteActions
import com.example.mynotes.ui.theme.PaletteCatalog

@Composable
internal fun FolderStrip(folders: List<NoteFolder>, selected: String?, archived: Boolean,
    onSelect: (String?) -> Unit, onArchive: () -> Unit, onManage: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        LazyRow(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "all", contentType = "folder") {
                FilterChip(selected = selected == null && !archived, onClick = { onSelect(null); UiSoundPlayer.playFallbackClick() },
                    label = { Text(stringResource(R.string.organize_all_folders)) })
            }
            items(folders, key = { "folder_${it.id}" }, contentType = { "folder" }) { folder ->
                FilterChip(selected = selected == folder.id && !archived, onClick = { onSelect(folder.id) ; UiSoundPlayer.playFallbackClick()},
                    leadingIcon = { Icon(collectionIcon(folder.icon), null, Modifier.size(18.dp)) },
                    label = { Text(folder.name) }, colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PaletteCatalog.find(folder.color).toneAt(0), selectedLabelColor = androidx.compose.ui.graphics.Color.Black))
            }
            item(key = "archived", contentType = "folder") {
                FilterChip(selected = archived, onClick = { onArchive(); UiSoundPlayer.playFallbackClick() },
                    label = { Text(stringResource(R.string.organize_archived)) },
                    leadingIcon = { Icon(Icons.Default.Archive, null, Modifier.size(18.dp)) })
            }
        }
        IconButton(onClick = { onManage(); UiSoundPlayer.playFallbackClick() }, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.CreateNewFolder, stringResource(R.string.organize_manage_folders)) }
    }
}

@Composable
internal fun FolderPickerDialog(folders: List<NoteFolder>, onSelect: (String?) -> Unit, onDismiss: () -> Unit) {
    AppAlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.organize_move)) },
        text = { Column(Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
            TextButton(onClick = { onSelect(null) ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Icon(Icons.Default.FolderOff, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.organize_unfiled))
            }
            folders.forEach { folder -> TextButton(onClick = { onSelect(folder.id) ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Icon(collectionIcon(folder.icon), null); Spacer(Modifier.width(8.dp)); Text(folder.name)
            } }
            if (folders.isEmpty()) Text(stringResource(R.string.organize_no_folders))
        } }, confirmButton = { TextButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.organize_cancel)) } })
}

@Composable
internal fun FolderManagerDialog(folders: List<NoteFolder>, actions: NoteActions, onDismiss: () -> Unit) {
    var editing by remember { mutableStateOf<NoteFolder?>(null) }
    var creating by remember { mutableStateOf(false) }
    if (editing != null || creating) {
        FolderEditorDialog(editing, onDismiss = { editing = null; creating = false }, onSave = { name, icon, color ->
            val current = editing
            if (current == null) actions.createFolder(name, icon, color) else actions.editFolder(current.copy(name = name, icon = icon, color = color))
            editing = null; creating = false
        }, onDelete = { editing?.let(actions::deleteFolder); onDismiss() })
    } else AppAlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.organize_manage_folders)) },
        text = { Column(Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
            if (folders.isEmpty()) Text(stringResource(R.string.organize_no_folders))
            folders.forEach { folder -> TextButton(onClick = { editing = folder ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Icon(collectionIcon(folder.icon), null); Spacer(Modifier.width(8.dp)); Text(folder.name, Modifier.weight(1f)); Icon(Icons.Default.Edit, null)
            } }
        } }, confirmButton = { TextButton(onClick = { creating = true ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.organize_new_folder)) } },
        dismissButton = { TextButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.organize_done)) } })
}

@Composable
private fun FolderEditorDialog(folder: NoteFolder?, onDismiss: () -> Unit, onSave: (String, String, String) -> Unit, onDelete: () -> Unit) {
    var name by remember(folder?.id) { mutableStateOf(folder?.name.orEmpty()) }
    var icon by remember(folder?.id) { mutableStateOf(folder?.icon ?: "folder") }
    var color by remember(folder?.id) { mutableStateOf(folder?.color ?: "sage") }
    var confirmDelete by remember { mutableStateOf(false) }
    if (confirmDelete) AppAlertDialog(onDismissRequest = { confirmDelete = false },
        title = { Text(stringResource(R.string.organize_delete_folder)) }, text = { Text(stringResource(R.string.organize_delete_folder_message)) },
        confirmButton = { TextButton(onClick = { onDelete(); UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Delete) }) { Text(stringResource(R.string.organize_delete)) } },
        dismissButton = { TextButton(onClick = { confirmDelete = false ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.organize_cancel)) } })
    else AppAlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(if (folder == null) R.string.organize_new_folder else R.string.organize_edit_folder)) },
        text = { Column(Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = CollectionPolicy.limitName(it) }, singleLine = true,
                label = { Text(stringResource(R.string.organize_folder_name)) })
            Row(Modifier.horizontalScroll(rememberScrollState())) { CollectionPolicy.icons.forEach { key ->
                IconToggleButton(checked = icon == key, onCheckedChange = { icon = key ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.size(48.dp)) {
                    Icon(collectionIcon(key), stringResource(collectionIconLabel(key)))
                }
            } }
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CollectionPolicy.colors.forEach { key ->
                    FilterChip(selected = color == key, onClick = { color = key ; UiSoundPlayer.playFallbackClick()}, label = { Text(stringResource(PaletteCatalog.find(key).labelRes)) })
                }
            }
            if (folder != null) TextButton(onClick = { confirmDelete = true ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.organize_delete_folder)) }
        } }, confirmButton = { TextButton(onClick = { onSave(name.trim(), icon, color) ; UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Save)}, enabled = name.isNotBlank()) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = { onDismiss(); UiSoundPlayer.playFallbackClick() }) { Text(stringResource(R.string.organize_cancel)) } })
}
