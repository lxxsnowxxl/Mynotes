package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.workspace.WorkspaceToolFilterChip
import com.example.mynotes.ui.workspace.workspaceToolActionButtonColors

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mynotes.data.workspace.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StudioDrawingPanel(
    canFill: Boolean,
    canSaveStamp: Boolean,
    canExportSvg: Boolean,
    onFill: () -> Unit,
    onStamp: (String) -> Unit,
    onSaveStamp: () -> Unit,
    stamps: List<Pair<String, String>>,
    onSvg: () -> Unit,
    selectedStamp: String?
) {
    val toolButtonColors = workspaceToolActionButtonColors()
    if (canFill || canExportSvg) {
        Row {
            if (canFill) OutlinedButton(onClick = {
                onFill()
                UiSoundPlayer.playFallbackClick()
            }, colors = toolButtonColors ?: ButtonDefaults.outlinedButtonColors()) { Text("Fill closed area") }
            if (canExportSvg) TextButton(onClick = {
                onSvg()
                UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Save)
            }, colors = toolButtonColors ?: ButtonDefaults.textButtonColors()) { Text("Export SVG") }
        }
    }

    Text("Stamps · tap canvas to place")
    Row(Modifier.horizontalScroll(rememberScrollState())) {
        (listOf("star" to "Star", "arrow" to "Arrow", "check" to "Check") + stamps).forEach { (id, name) ->
            // AssistChip did not indicate which stamp had been armed and made
            // the control appear unresponsive until the next canvas tap.
            WorkspaceToolFilterChip(
                selected = selectedStamp == id,
                onClick = {
                    onStamp(id)
                    UiSoundPlayer.playFallbackClick()
                },
                label = { Text(name) }
            )
        }
    }
    if (selectedStamp != null) {
        Text("Selected stamp: tap the canvas to place it", style = MaterialTheme.typography.labelSmall)
    }
    if (canSaveStamp) TextButton(onClick = {
        onSaveStamp()
        UiSoundPlayer.playFallbackClick(com.example.mynotes.ui.sound.UiActionSound.Save)
    }, colors = toolButtonColors ?: ButtonDefaults.textButtonColors()) { Text("Save selected stroke as stamp") }
}
