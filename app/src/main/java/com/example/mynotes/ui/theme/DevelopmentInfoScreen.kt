package com.example.mynotes.ui

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
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
import java.util.Calendar

private const val MYNOTES_REPOSITORY_URL = "https://github.com/lxxsnowxxl/Mynotes"
private const val MYNOTES_COMPILE_SDK = 37
private const val MYNOTES_JAVA_COMPATIBILITY = 11

/**
 * Pantalla secundaria puramente informativa.
 *
 * No modifica AppSettings, notas, adjuntos ni cachés. Su única responsabilidad
 * es mostrar, con datos obtenidos del paquete y de la configuración actual,
 * cómo está construida MyNotes y qué decisiones técnicas usa en ejecución.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevelopmentInfoScreen(settings: AppSettings, onOpenSourceCode: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    val developmentScrollState = rememberScrollState()
    val packageInfo = remember(context.packageName) {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    val versionName = packageInfo.versionName.orEmpty().ifBlank { "1.0" }
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode.toLong()
    }
    val applicationInfo = context.applicationInfo
    val buildType = if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
        stringResource(R.string.development_build_debug)
    } else {
        stringResource(R.string.development_build_release)
    }
    val minSdk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) applicationInfo.minSdkVersion else 24
    val targetSdk = applicationInfo.targetSdkVersion
    val screenBackground = MaterialTheme.colorScheme.background
    val primaryText = resolveUiTextColor(settings.textColor, screenBackground)
    val secondaryText = resolveSecondaryUiTextColor(settings.textColor, screenBackground)
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }

    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled, animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = screenBackground,
            topBar = {
                TopAppBar(title = {
                        Text(text = stringResource(R.string.development_info_title), fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    },
                    navigationIcon = {
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
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(developmentScrollState)
                    .widthIn(max = 840.dp).padding(horizontal = 14.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = primaryText,
                        modifier = Modifier.size(28.dp))
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(text = "MyNotes", color = primaryText, fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text(text = stringResource(R.string.development_info_subtitle), color = secondaryText,
                            fontFamily = fontFamily, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))

                DevelopmentSection(title = stringResource(R.string.development_app_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_version), "$versionName ($versionCode)", settings)
                    DevelopmentValueRow(stringResource(R.string.development_package), context.packageName, settings)
                    DevelopmentValueRow(stringResource(R.string.development_build_type), buildType, settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_sdk_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_min_sdk), "API $minSdk", settings)
                    DevelopmentValueRow(stringResource(R.string.development_target_sdk), "API $targetSdk", settings)
                    DevelopmentValueRow(stringResource(R.string.development_compile_sdk), "API $MYNOTES_COMPILE_SDK", settings)
                    DevelopmentValueRow(stringResource(R.string.development_device_sdk), "API ${Build.VERSION.SDK_INT}", settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_build_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_build_system),
                        stringResource(R.string.development_build_system_value), settings)
                    DevelopmentValueRow(stringResource(R.string.development_java_compatibility),
                        "Java $MYNOTES_JAVA_COMPATIBILITY", settings)
                    DevelopmentValueRow(stringResource(R.string.development_release_optimization),
                        stringResource(R.string.development_release_optimization_value), settings)
                    Spacer(Modifier.height(6.dp))
                    DevelopmentParagraph(stringResource(R.string.development_build_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_ui_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_ui_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_architecture_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_architecture_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_storage_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_storage_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_media_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_media_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_performance_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_performance_intro), settings)
                    Spacer(Modifier.height(10.dp))
                    PerformanceProfileRow(title = stringResource(R.string.development_profile_performance),
                        detail = stringResource(R.string.development_profile_performance_detail), selected = settings.performanceMode == "performance",
                        settings = settings)
                    Spacer(Modifier.height(8.dp))
                    PerformanceProfileRow(title = stringResource(R.string.development_profile_balanced),
                        detail = stringResource(R.string.development_profile_balanced_detail), selected = settings.performanceMode == "balanced",
                        settings = settings)
                    Spacer(Modifier.height(8.dp))
                    PerformanceProfileRow(title = stringResource(R.string.development_profile_quality),
                        detail = stringResource(R.string.development_profile_quality_detail), selected = settings.performanceMode == "quality",
                        settings = settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_stack_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_stack_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_compatibility_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_compatibility_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_android_integration_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_android_integration_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_credits_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_primary_author),
                        stringResource(R.string.development_primary_author_value), settings)
                    DevelopmentValueRow(stringResource(R.string.development_github_account), "@lxxsnowxxl", settings)
                    DevelopmentValueRow(stringResource(R.string.development_assistance),
                        stringResource(R.string.development_assistance_value), settings)
                    Spacer(Modifier.height(6.dp))
                    DevelopmentParagraph(stringResource(R.string.development_credits_body), settings)
                }

                DevelopmentSection(title = stringResource(R.string.development_repository_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_repository_host), "GitHub", settings)
                    DevelopmentValueRow(stringResource(R.string.development_repository_name), "lxxsnowxxl/Mynotes", settings)
                    DevelopmentParagraph(MYNOTES_REPOSITORY_URL, settings)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MYNOTES_REPOSITORY_URL)))
                        }
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = primaryText)) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(19.dp))
                        Text(text = stringResource(R.string.development_repository_open), modifier = Modifier.padding(start = 8.dp),
                            fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                DevelopmentSection(title = stringResource(R.string.development_source_section), settings = settings) {
                    DevelopmentParagraph(stringResource(R.string.development_source_intro), settings)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                        onOpenSourceCode()
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = primaryText)) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(19.dp))
                        Text(text = stringResource(R.string.development_source_open), modifier = Modifier.padding(start = 8.dp),
                            fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                DevelopmentSection(title = stringResource(R.string.development_legal_section), settings = settings) {
                    DevelopmentValueRow(stringResource(R.string.development_copyright),
                        stringResource(R.string.development_copyright_value, currentYear), settings)
                    DevelopmentValueRow(stringResource(R.string.development_license),
                        stringResource(R.string.development_license_value), settings)
                    Spacer(Modifier.height(6.dp))
                    DevelopmentParagraph(stringResource(R.string.development_legal_body), settings)
                }

                Spacer(Modifier.height(24.dp))
            }
            ScrollPositionCapsule(state = developmentScrollState,
                modifier = Modifier.align(Alignment.CenterEnd).padding(paddingValues),
                backgroundColor = screenBackground, preferredColor = primaryText)
            }
        }
    }
}

@Composable
private fun DevelopmentSection(title: String, settings: AppSettings, content: @Composable () -> Unit) {
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
private fun DevelopmentValueRow(label: String, value: String, settings: AppSettings) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val text = resolveUiTextColor(settings.textColor, background)
    val secondary = resolveSecondaryUiTextColor(settings.textColor, background)
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top) {
        Text(text = label, modifier = Modifier.weight(0.42f), color = secondary,
            fontFamily = appFontFamily(settings.font), fontSize = 13.sp)
        Text(text = value, modifier = Modifier.weight(0.58f), color = text, fontFamily = appFontFamily(settings.font),
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun DevelopmentParagraph(text: String, settings: AppSettings) {
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val secondary = resolveSecondaryUiTextColor(settings.textColor, background)
    Text(text = text, color = secondary, fontFamily = appFontFamily(settings.font), fontSize = 13.sp, lineHeight = 19.sp)
}

@Composable
private fun PerformanceProfileRow(title: String, detail: String, selected: Boolean, settings: AppSettings) {
    val container = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    val titleColor = resolveUiTextColor(settings.textColor, container)
    val detailColor = resolveSecondaryUiTextColor(settings.textColor, container)
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = container, tonalElevation = 0.dp) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, modifier = Modifier.weight(1f), color = titleColor, fontFamily = appFontFamily(settings.font),
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (selected) {
                    Text(text = stringResource(R.string.development_current_profile), color = titleColor,
                        fontFamily = appFontFamily(settings.font), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
            Text(text = detail, modifier = Modifier.padding(top = 3.dp), color = detailColor,
                fontFamily = appFontFamily(settings.font), fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}
