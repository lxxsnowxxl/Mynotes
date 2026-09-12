package com.example.mynotes.ui.components

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.data.AppDataBackupManager
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupRestoreSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color, graphicColor: Color) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val exportLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/zip")) { uri ->
            if (uri != null && !busy) {
                scope.launch {
                    busy = true
                    statusMessage = null
                    val result = AppDataBackupManager.exportBackup(context = context, destination = uri, settings = settings)
                    result.onSuccess { summary -> statusMessage = context.getString(R.string.backup_export_success, summary.noteCount,
                                    summary.attachmentCount)
                            if (summary.skippedAttachmentCount > 0) {
                                statusMessage += " " + context.getString(R.string.backup_export_skipped, summary.skippedAttachmentCount)
                            }
                        }.onFailure { error -> statusMessage = context.getString(R.string.backup_operation_error,
                                    error.message ?: context.getString(R.string.backup_unknown_error))
                        }
                    busy = false
                }
            }
        }
    val importLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null && !busy) {
                pendingImportUri = uri
            }
        }
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(16.dp)) { panelColors -> Text(
            text = stringResource(R.string.backup_restore_title), color = panelColors.text, fontFamily = fontFamily,
            fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Spacer(Modifier.height(4.dp))
        Text(text = stringResource(R.string.backup_restore_description), color = panelColors.secondaryText, fontFamily = fontFamily,
            fontSize = 12.sp, lineHeight = 17.sp)
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Backup)
                    val stamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date())
                    exportLauncher.launch("MyNotes_backup_$stamp.zip")
                }, enabled = !busy, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp), tint = panelColors.text
                )
                Spacer(Modifier.size(7.dp))
                Text(text = stringResource(R.string.backup_export_button), color = panelColors.text, fontFamily = fontFamily)
            }
            OutlinedButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Restore)
                    importLauncher.launch(arrayOf("application/zip", "application/octet-stream", "application/x-zip-compressed"))
                }, enabled = !busy, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp),
                    tint = panelColors.text)
                Spacer(Modifier.size(7.dp))
                Text(text = stringResource(R.string.backup_import_button), color = panelColors.text, fontFamily = fontFamily)
            }
        }
        if (busy) {
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = panelColors.text)
                Spacer(Modifier.size(9.dp))
                Text(text = stringResource(R.string.backup_processing), color = panelColors.secondaryText, fontFamily = fontFamily,
                    fontSize = 12.sp)
            }
        } else if (!statusMessage.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(text = statusMessage.orEmpty(), color = panelColors.secondaryText, fontFamily = fontFamily, fontSize = 12.sp,
                lineHeight = 16.sp)
        }
        }
    val importUri = pendingImportUri
    if (importUri != null) {
        AppAlertDialog(onDismissRequest = {
                if (!busy) pendingImportUri = null
            }, title = {
                Text(text = stringResource(R.string.backup_import_confirm_title), fontFamily = fontFamily)
            }, text = {
                Text(text = stringResource(R.string.backup_import_confirm_message), fontFamily = fontFamily)
            }, confirmButton = {
                TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Confirm)
                        pendingImportUri = null
                        scope.launch {
                            busy = true
                            statusMessage = null
                            val result = AppDataBackupManager.importBackup(context = context, source = importUri)
                            result.onSuccess { summary -> val message = context.getString(R.string.backup_import_success, summary.noteCount,
                                            summary.attachmentCount)
                                    statusMessage = message
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    /*
                                     * También vuelve a leer el idioma guardado
                                     * por MainActivity.attachBaseContext().
                                     */
                                    (context as? Activity)?.recreate()
                                }.onFailure { error -> statusMessage = context.getString(R.string.backup_operation_error,
                                            error.message ?: context.getString(R.string.backup_unknown_error))
                                }
                            busy = false
                        }
                    }) {
                    Text(stringResource(R.string.backup_import_confirm_action))
                }
            }, dismissButton = {
                TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Cancel)
                        pendingImportUri = null
                    }) {
                    Text(stringResource(R.string.backup_cancel))
                }
            })
    }
}
