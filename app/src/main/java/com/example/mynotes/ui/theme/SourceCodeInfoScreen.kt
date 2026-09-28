package com.example.mynotes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiTextColor

/**
 * Mapa navegable de la organización del código fuente.
 *
 * Los archivos .kt originales no se empaquetan como texto dentro del APK, por
 * lo que esta pantalla describe las rutas reales del proyecto que pueden
 * abrirse desde Android Studio sin duplicar el código dentro de la aplicación.
 */
@Composable
fun SourceCodeInfoScreen(settings: AppSettings, onBack: () -> Unit) {
    val context = LocalContext.current

    InformationScreenLayout(
        settings = settings, title = stringResource(R.string.development_source_title), onBack = onBack
    ) { fontFamily, primaryText, secondaryText ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = primaryText,
                modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = stringResource(R.string.development_source_heading), color = primaryText,
                    fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = stringResource(R.string.development_source_subtitle), color = secondaryText,
                    fontFamily = fontFamily, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(18.dp))

        DevelopmentSection(title = stringResource(R.string.development_repository_section), settings = settings) {
            SourceCodeRow(path = "GitHub · lxxsnowxxl/Mynotes",
                description = MYNOTES_REPOSITORY_URL, settings = settings)
            TextButton(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                context.openMyNotesRepository()
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(text = stringResource(R.string.development_repository_open),
                    modifier = Modifier.padding(start = 8.dp), fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
            }
        }

        SourceSection(R.string.development_source_location_section, settings,
            "app/src/main/java/com/example/mynotes/" to R.string.development_source_location_body)
        SourceSection(R.string.development_source_core_section, settings,
            "MainActivity.kt" to R.string.development_source_main_activity,
            "viewmodel/NoteViewModel.kt" to R.string.development_source_note_vm,
            "viewmodel/SettingsViewModel.kt" to R.string.development_source_settings_vm)
        SourceSection(R.string.development_source_ui_section, settings,
            "ui/theme/NotesScreen.kt" to R.string.development_source_notes_screen,
            "ui/theme/NoteEditorScreen.kt" to R.string.development_source_editor_screen,
            "ui/theme/NoteDetailScreen.kt" to R.string.development_source_detail_screen,
            "ui/theme/SettingsScreen.kt" to R.string.development_source_settings_screen,
            "ui/theme/DevelopmentInfoScreen.kt" to R.string.development_source_development_screen,
            "ui/theme/SourceCodeInfoScreen.kt" to R.string.development_source_source_screen,
            "ui/components/NoteCard.kt" to R.string.development_source_note_card,
            "ui/components/LinkPreviewCard.kt" to R.string.development_source_link_card)
        SourceSection(R.string.development_source_data_section, settings,
            "data/AppDatabase.kt" to R.string.development_source_database,
            "data/NoteDao.kt" to R.string.development_source_note_dao,
            "data/AttachmentDao.kt" to R.string.development_source_attachment_dao,
            "data/AppDataBackupManager.kt" to R.string.development_source_backup_manager,
            "settings/SettingsRepository.kt" to R.string.development_source_settings_repo,
            "links/LinkPreviewRepository.kt" to R.string.development_source_link_repo)
        SourceSection(R.string.development_source_performance_section, settings,
            "performance/AttachmentPreviewCache.kt" to R.string.development_source_attachment_cache,
            "performance/DisplayPerformanceController.kt" to R.string.development_source_display_controller,
            "ui/motion/AppMotion.kt" to R.string.development_source_motion)
        SourceSection(R.string.development_source_support_section, settings,
            "ui/AttachmentViewerActivity.kt" to R.string.development_source_attachment_viewer,
            "ui/sound/UiSoundPlayer.kt + UiHapticPlayer.kt" to R.string.development_source_audio_haptics,
            "ui/components/BackupRestoreSection.kt" to R.string.development_source_backup_restore,
            "ui/theme/PaletteCatalog.kt" to R.string.development_source_palette_catalog,
            "update/GitHubUpdateManager.kt" to R.string.development_source_update_manager)
        SourceSection(R.string.development_source_resources_section, settings,
            "res/layout/" to R.string.development_source_layouts,
            "res/values*/" to R.string.development_source_values,
            "AndroidManifest.xml" to R.string.development_source_manifest,
            "build.gradle.kts" to R.string.development_source_gradle)
        DevelopmentSection(title = stringResource(R.string.development_source_note_section), settings = settings) {
            DevelopmentParagraph(text = stringResource(R.string.development_source_note_body), settings = settings)
        }
    }
}

@Composable
private fun SourceSection(titleRes: Int, settings: AppSettings, vararg rows: Pair<String, Int>) {
    DevelopmentSection(title = stringResource(titleRes), settings = settings) {
        rows.forEach { (path, descriptionRes) -> SourceCodeRow(path, stringResource(descriptionRes), settings) }
    }
}

@Composable
private fun SourceCodeRow(path: String, description: String, settings: AppSettings) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val text = resolveUiTextColor(settings.textColor, background)
    val secondary = resolveSecondaryUiTextColor(settings.textColor, background)
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = path, color = text, fontFamily = appFontFamily(settings.font),
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(text = description, modifier = Modifier.padding(top = 2.dp), color = secondary,
            fontFamily = appFontFamily(settings.font), fontSize = 12.sp, lineHeight = 17.sp)
    }
}

