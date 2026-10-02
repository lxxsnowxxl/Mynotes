package com.example.mynotes.ui
import com.example.mynotes.ui.components.AppHeading
import com.example.mynotes.ui.components.AppIconLabel

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
                AppHeading(
                    title = stringResource(R.string.development_source_heading), subtitle = stringResource(R.string.development_source_subtitle),
                    fontFamily = fontFamily, titleColor = primaryText, subtitleColor = secondaryText
                )
            }
        }
        Spacer(Modifier.height(18.dp))

        DevelopmentSection(title = stringResource(R.string.development_repository_section)) {
            SourceCodeRow(path = "GitHub · lxxsnowxxl/Mynotes", description = MYNOTES_REPOSITORY_URL)
            TextButton(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                context.openMyNotesRepository()
            }, modifier = Modifier.fillMaxWidth()) {
                AppIconLabel(Icons.Default.OpenInNew, stringResource(R.string.development_repository_open), iconModifier = Modifier.size(18.dp),
                    textModifier = Modifier.padding(start = 8.dp), fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
            }
        }

        SourceSection(R.string.development_source_location_section,
            "app/src/main/java/com/example/mynotes/" to R.string.development_source_location_body)
        SourceSection(R.string.development_source_core_section,
            "MainActivity.kt" to R.string.development_source_main_activity,
            "viewmodel/NoteViewModel.kt" to R.string.development_source_note_vm,
            "viewmodel/SettingsViewModel.kt" to R.string.development_source_settings_vm)
        SourceSection(R.string.development_source_ui_section,
            "ui/theme/NotesScreen.kt" to R.string.development_source_notes_screen,
            "ui/theme/NoteEditorScreen.kt" to R.string.development_source_editor_screen,
            "ui/theme/NoteDetailScreen.kt" to R.string.development_source_detail_screen,
            "ui/theme/SettingsScreen.kt" to R.string.development_source_settings_screen,
            "ui/theme/DevelopmentInfoScreen.kt" to R.string.development_source_development_screen,
            "ui/theme/SourceCodeInfoScreen.kt" to R.string.development_source_source_screen,
            "ui/components/NoteCard.kt" to R.string.development_source_note_card,
            "ui/components/LinkPreviewCard.kt" to R.string.development_source_link_card)
        SourceSection(R.string.development_source_data_section,
            "data/AppDatabase.kt" to R.string.development_source_database,
            "data/NoteDao.kt" to R.string.development_source_note_dao,
            "data/AttachmentDao.kt" to R.string.development_source_attachment_dao,
            "data/AppDataBackupManager.kt" to R.string.development_source_backup_manager,
            "settings/SettingsRepository.kt" to R.string.development_source_settings_repo,
            "links/LinkPreviewRepository.kt" to R.string.development_source_link_repo)
        SourceSection(R.string.development_source_performance_section,
            "performance/AttachmentPreviewCache.kt" to R.string.development_source_attachment_cache,
            "performance/DisplayPerformanceController.kt" to R.string.development_source_display_controller,
            "ui/motion/AppMotion.kt" to R.string.development_source_motion)
        SourceSection(R.string.development_source_support_section,
            "ui/AttachmentViewerActivity.kt" to R.string.development_source_attachment_viewer,
            "ui/sound/UiSoundPlayer.kt + UiHapticPlayer.kt" to R.string.development_source_audio_haptics,
            "ui/components/BackupRestoreSection.kt" to R.string.development_source_backup_restore,
            "ui/theme/PaletteCatalog.kt" to R.string.development_source_palette_catalog,
            "update/GitHubUpdateManager.kt" to R.string.development_source_update_manager)
        SourceSection(R.string.development_source_resources_section,
            "res/layout/" to R.string.development_source_layouts,
            "res/values*/" to R.string.development_source_values,
            "AndroidManifest.xml" to R.string.development_source_manifest,
            "build.gradle.kts" to R.string.development_source_gradle)
        DevelopmentSection(title = stringResource(R.string.development_source_note_section)) {
            DevelopmentParagraph(text = stringResource(R.string.development_source_note_body))
        }
    }
}

@Composable
private fun SourceSection(titleRes: Int, vararg rows: Pair<String, Int>) {
    DevelopmentSection(title = stringResource(titleRes)) {
        rows.forEach { (path, descriptionRes) -> SourceCodeRow(path, stringResource(descriptionRes)) }
    }
}

@Composable
private fun SourceCodeRow(path: String, description: String) {
    val style = LocalDevelopmentSectionStyle.current
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = path, color = style.text, fontFamily = style.fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(text = description, modifier = Modifier.padding(top = 2.dp), color = style.secondary, fontFamily = style.fontFamily,
            fontSize = 12.sp, lineHeight = 17.sp)
    }
}

