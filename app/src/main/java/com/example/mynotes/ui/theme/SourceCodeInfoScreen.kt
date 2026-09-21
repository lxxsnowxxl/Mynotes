package com.example.mynotes.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiTextColor

private const val MYNOTES_SOURCE_REPOSITORY_URL = "https://github.com/lxxsnowxxl/Mynotes"

/**
 * Mapa navegable de la organización del código fuente.
 *
 * Los archivos .kt originales no se empaquetan como texto dentro del APK, por
 * lo que esta pantalla describe las rutas reales del proyecto que pueden
 * abrirse desde Android Studio sin duplicar el código dentro de la aplicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceCodeInfoScreen(settings: AppSettings, onBack: () -> Unit) {
    val context = LocalContext.current
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    val sourceScrollState = rememberScrollState()
    val screenBackground = MaterialTheme.colorScheme.background
    val primaryText = resolveUiTextColor(settings.textColor, screenBackground)
    val secondaryText = resolveSecondaryUiTextColor(settings.textColor, screenBackground)

    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled, animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = screenBackground,
            topBar = {
                TopAppBar(title = {
                    Text(text = stringResource(R.string.development_source_title), fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }, navigationIcon = {
                    TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                        onBack()
                    }, colors = ButtonDefaults.textButtonColors(contentColor = primaryText)) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.mock_back), modifier = Modifier.size(25.dp))
                    }
                })
            }) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(sourceScrollState)
                .widthIn(max = 840.dp).padding(horizontal = 14.dp, vertical = 16.dp)) {
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

                SourceSection(title = stringResource(R.string.development_repository_section), settings = settings) {
                    SourceCodeRow(path = "GitHub · lxxsnowxxl/Mynotes",
                        description = MYNOTES_SOURCE_REPOSITORY_URL, settings = settings)
                    TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MYNOTES_SOURCE_REPOSITORY_URL)))
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(text = stringResource(R.string.development_repository_open),
                            modifier = Modifier.padding(start = 8.dp), fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
                    }
                }

                SourceSection(title = stringResource(R.string.development_source_location_section), settings = settings) {
                    SourceCodeRow(path = "app/src/main/java/com/example/mynotes/",
                        description = stringResource(R.string.development_source_location_body), settings = settings)
                }
                SourceSection(title = stringResource(R.string.development_source_core_section), settings = settings) {
                    SourceCodeRow("MainActivity.kt", stringResource(R.string.development_source_main_activity), settings)
                    SourceCodeRow("viewmodel/NoteViewModel.kt", stringResource(R.string.development_source_note_vm), settings)
                    SourceCodeRow("viewmodel/SettingsViewModel.kt", stringResource(R.string.development_source_settings_vm), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_ui_section), settings = settings) {
                    SourceCodeRow("ui/theme/NotesScreen.kt", stringResource(R.string.development_source_notes_screen), settings)
                    SourceCodeRow("ui/theme/NoteEditorScreen.kt", stringResource(R.string.development_source_editor_screen), settings)
                    SourceCodeRow("ui/theme/NoteDetailScreen.kt", stringResource(R.string.development_source_detail_screen), settings)
                    SourceCodeRow("ui/theme/SettingsScreen.kt", stringResource(R.string.development_source_settings_screen), settings)
                    SourceCodeRow("ui/theme/DevelopmentInfoScreen.kt",
                        stringResource(R.string.development_source_development_screen), settings)
                    SourceCodeRow("ui/theme/SourceCodeInfoScreen.kt",
                        stringResource(R.string.development_source_source_screen), settings)
                    SourceCodeRow("ui/components/NoteCard.kt", stringResource(R.string.development_source_note_card), settings)
                    SourceCodeRow("ui/components/LinkPreviewCard.kt",
                        stringResource(R.string.development_source_link_card), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_data_section), settings = settings) {
                    SourceCodeRow("data/AppDatabase.kt", stringResource(R.string.development_source_database), settings)
                    SourceCodeRow("data/NoteDao.kt", stringResource(R.string.development_source_note_dao), settings)
                    SourceCodeRow("data/AttachmentDao.kt", stringResource(R.string.development_source_attachment_dao), settings)
                    SourceCodeRow("data/AppDataBackupManager.kt",
                        stringResource(R.string.development_source_backup_manager), settings)
                    SourceCodeRow("settings/SettingsRepository.kt", stringResource(R.string.development_source_settings_repo), settings)
                    SourceCodeRow("links/LinkPreviewRepository.kt", stringResource(R.string.development_source_link_repo), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_performance_section), settings = settings) {
                    SourceCodeRow("performance/AttachmentPreviewCache.kt",
                        stringResource(R.string.development_source_attachment_cache), settings)
                    SourceCodeRow("performance/DisplayPerformanceController.kt",
                        stringResource(R.string.development_source_display_controller), settings)
                    SourceCodeRow("ui/motion/AppMotion.kt", stringResource(R.string.development_source_motion), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_support_section), settings = settings) {
                    SourceCodeRow("ui/AttachmentViewerActivity.kt",
                        stringResource(R.string.development_source_attachment_viewer), settings)
                    SourceCodeRow("ui/sound/UiSoundPlayer.kt + UiHapticPlayer.kt",
                        stringResource(R.string.development_source_audio_haptics), settings)
                    SourceCodeRow("ui/components/BackupRestoreSection.kt",
                        stringResource(R.string.development_source_backup_restore), settings)
                    SourceCodeRow("ui/theme/PaletteCatalog.kt",
                        stringResource(R.string.development_source_palette_catalog), settings)
                    SourceCodeRow("update/GitHubUpdateManager.kt",
                        stringResource(R.string.development_source_update_manager), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_resources_section), settings = settings) {
                    SourceCodeRow("res/layout/", stringResource(R.string.development_source_layouts), settings)
                    SourceCodeRow("res/values*/", stringResource(R.string.development_source_values), settings)
                    SourceCodeRow("AndroidManifest.xml", stringResource(R.string.development_source_manifest), settings)
                    SourceCodeRow("build.gradle.kts", stringResource(R.string.development_source_gradle), settings)
                }
                SourceSection(title = stringResource(R.string.development_source_note_section), settings = settings) {
                    SourceParagraph(text = stringResource(R.string.development_source_note_body), settings = settings)
                }
                Spacer(Modifier.height(24.dp))
            }
            ScrollPositionCapsule(state = sourceScrollState,
                modifier = Modifier.align(Alignment.CenterEnd).padding(paddingValues),
                backgroundColor = screenBackground, preferredColor = primaryText)
            }
        }
    }
}

@Composable
private fun SourceSection(title: String, settings: AppSettings, content: @Composable () -> Unit) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val text = resolveUiTextColor(settings.textColor, background)
    val border = ensureUiContrast(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f), background, 2.2f)
    Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(18.dp),
        color = background, border = BorderStroke(1.dp, border), tonalElevation = 0.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = text, fontFamily = appFontFamily(settings.font),
                fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(10.dp))
            content()
        }
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

@Composable
private fun SourceParagraph(text: String, settings: AppSettings) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val secondary = resolveSecondaryUiTextColor(settings.textColor, background)
    Text(text = text, color = secondary, fontFamily = appFontFamily(settings.font), fontSize = 13.sp, lineHeight = 19.sp)
}
