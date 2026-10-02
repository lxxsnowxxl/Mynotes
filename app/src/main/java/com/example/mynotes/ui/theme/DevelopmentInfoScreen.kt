package com.example.mynotes.ui
import com.example.mynotes.ui.components.AppHeading
import com.example.mynotes.ui.theme.rememberUiTextColors
import com.example.mynotes.ui.components.AppIconLabel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.DeveloperFeatures
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.rememberAppFontFamily
import com.example.mynotes.ui.theme.ensureUiContrast
import java.util.Calendar

internal const val MYNOTES_REPOSITORY_URL = "https://github.com/lxxsnowxxl/Mynotes"
internal fun Context.openMyNotesRepository() { runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MYNOTES_REPOSITORY_URL))) } }
internal val LocalDevelopmentSettings = staticCompositionLocalOf<AppSettings> { error("Development settings unavailable") }

@Immutable
internal data class DevelopmentSectionStyle(val fontFamily: FontFamily, val text: Color, val secondary: Color)
internal val LocalDevelopmentSectionStyle = staticCompositionLocalOf<DevelopmentSectionStyle> { error("Development section unavailable") }

private const val MYNOTES_COMPILE_SDK = 37
private const val MYNOTES_JAVA_COMPATIBILITY = 11

@Composable
fun DevelopmentInfoScreen(settings: AppSettings, onOpenSourceCode: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val packageInfo = remember(context.packageName) {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    val versionName = packageInfo.versionName.orEmpty().ifBlank { "1.9.0" }
    val applicationInfo = context.applicationInfo
    val buildType = stringResource(R.string.development_build_release)
    val minSdk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) applicationInfo.minSdkVersion else 24
    val targetSdk = applicationInfo.targetSdkVersion
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    var applicationTapCount by remember { mutableIntStateOf(0) }
    var lastApplicationTapAt by remember { mutableLongStateOf(0L) }
    var developerFontUnlocked by remember(context) {
        mutableStateOf(DeveloperFeatures.isGoogleSansFlexUnlocked(context))
    }

    InformationScreenLayout(
        settings = settings, title = stringResource(R.string.development_info_title), onBack = onBack
    ) { fontFamily, primaryText, secondaryText ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = primaryText,
                modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.padding(start = 12.dp)) {
                AppHeading(
                    title = "MyNotes", subtitle = stringResource(R.string.development_info_subtitle),
                    fontFamily = fontFamily, titleColor = primaryText, subtitleColor = secondaryText,
                    titleSize = 24.sp
                )
            }
        }
        Spacer(Modifier.height(18.dp))

        DevelopmentSection(
            title = stringResource(R.string.development_app_section),
            modifier = Modifier.clickable {
                val now = SystemClock.elapsedRealtime()
                applicationTapCount = if (now - lastApplicationTapAt > 1_500L) 1 else applicationTapCount + 1
                lastApplicationTapAt = now
                if (applicationTapCount >= 3) {
                    applicationTapCount = 0
                    val messageRes = if (developerFontUnlocked) R.string.development_font_already_unlocked else {
                        DeveloperFeatures.unlockGoogleSansFlex(context)
                        developerFontUnlocked = true
                        R.string.development_font_unlocked
                    }
                    Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            DevelopmentValueRow(stringResource(R.string.development_version), "v$versionName")
            DevelopmentValueRow(stringResource(R.string.development_package), context.packageName)
            DevelopmentValueRow(stringResource(R.string.development_build_type), buildType)
            if (developerFontUnlocked) {
                DevelopmentValueRow(stringResource(R.string.development_developer_font),
                    stringResource(R.string.development_developer_font_enabled))
            }
        }

        DevelopmentSection(title = stringResource(R.string.development_sdk_section)) {
            DevelopmentValueRow(stringResource(R.string.development_min_sdk), "API $minSdk")
            DevelopmentValueRow(stringResource(R.string.development_target_sdk), "API $targetSdk")
            DevelopmentValueRow(stringResource(R.string.development_compile_sdk), "API $MYNOTES_COMPILE_SDK")
            DevelopmentValueRow(stringResource(R.string.development_device_sdk), "API ${Build.VERSION.SDK_INT}")
        }

        DevelopmentSection(title = stringResource(R.string.development_build_section)) {
            DevelopmentValueRow(stringResource(R.string.development_build_system),
                stringResource(R.string.development_build_system_value))
            DevelopmentValueRow(stringResource(R.string.development_java_compatibility),
                "Java $MYNOTES_JAVA_COMPATIBILITY")
            DevelopmentValueRow(stringResource(R.string.development_release_optimization),
                stringResource(R.string.development_release_optimization_value))
            Spacer(Modifier.height(6.dp))
            DevelopmentParagraph(stringResource(R.string.development_build_body))
        }

        listOf(
            R.string.development_ui_section to R.string.development_ui_body,
            R.string.development_architecture_section to R.string.development_architecture_body,
            R.string.development_storage_section to R.string.development_storage_body,
            R.string.development_media_section to R.string.development_media_body
        ).forEach { (titleRes, bodyRes) ->
            DevelopmentSection(stringResource(titleRes)) { DevelopmentParagraph(stringResource(bodyRes)) }
        }

        DevelopmentSection(title = stringResource(R.string.development_performance_section)) {
            DevelopmentParagraph(stringResource(R.string.development_performance_intro))
            Spacer(Modifier.height(10.dp))
            PerformanceProfileRow(title = stringResource(R.string.development_profile_performance),
                detail = stringResource(R.string.development_profile_performance_detail), selected = settings.performanceMode == "performance")
            Spacer(Modifier.height(8.dp))
            PerformanceProfileRow(title = stringResource(R.string.development_profile_balanced),
                detail = stringResource(R.string.development_profile_balanced_detail), selected = settings.performanceMode == "balanced")
            Spacer(Modifier.height(8.dp))
            PerformanceProfileRow(title = stringResource(R.string.development_profile_quality),
                detail = stringResource(R.string.development_profile_quality_detail), selected = settings.performanceMode == "quality")
        }

        listOf(
            R.string.development_stack_section to R.string.development_stack_body,
            R.string.development_compatibility_section to R.string.development_compatibility_body,
            R.string.development_android_integration_section to R.string.development_android_integration_body
        ).forEach { (titleRes, bodyRes) ->
            DevelopmentSection(stringResource(titleRes)) { DevelopmentParagraph(stringResource(bodyRes)) }
        }

        DevelopmentSection(title = stringResource(R.string.development_credits_section)) {
            DevelopmentValueRow(stringResource(R.string.development_primary_author),
                stringResource(R.string.development_primary_author_value))
            DevelopmentValueRow(stringResource(R.string.development_github_account), "@lxxsnowxxl")
            DevelopmentValueRow(stringResource(R.string.development_assistance),
                stringResource(R.string.development_assistance_value))
            Spacer(Modifier.height(6.dp))
            DevelopmentParagraph(stringResource(R.string.development_credits_body))
        }

        DevelopmentSection(title = stringResource(R.string.development_repository_section)) {
            DevelopmentValueRow(stringResource(R.string.development_repository_host), "GitHub")
            DevelopmentValueRow(stringResource(R.string.development_repository_name), "lxxsnowxxl/Mynotes")
            DevelopmentParagraph(MYNOTES_REPOSITORY_URL)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                context.openMyNotesRepository()
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = primaryText)) {
                AppIconLabel(Icons.Default.OpenInNew, stringResource(R.string.development_repository_open), iconModifier = Modifier.size(19.dp),
                    textModifier = Modifier.padding(start = 8.dp), fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }

        DevelopmentSection(title = stringResource(R.string.development_source_section)) {
            DevelopmentParagraph(stringResource(R.string.development_source_intro))
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu, onOpenSourceCode), modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.textButtonColors(contentColor = primaryText)) {
                AppIconLabel(Icons.Default.Code, stringResource(R.string.development_source_open), iconModifier = Modifier.size(19.dp),
                    textModifier = Modifier.padding(start = 8.dp), fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }

        DevelopmentSection(title = stringResource(R.string.development_legal_section)) {
            DevelopmentValueRow(stringResource(R.string.development_copyright),
                stringResource(R.string.development_copyright_value, currentYear))
            DevelopmentValueRow(stringResource(R.string.development_license),
                stringResource(R.string.development_license_value))
            DevelopmentValueRow(stringResource(R.string.development_third_party_licenses),
                stringResource(R.string.development_third_party_licenses_value))
            DevelopmentValueRow(stringResource(R.string.development_bundled_fonts),
                stringResource(R.string.development_bundled_fonts_value))
            Spacer(Modifier.height(6.dp))
            DevelopmentParagraph(stringResource(R.string.development_legal_body))
            Spacer(Modifier.height(8.dp))
            DevelopmentParagraph(stringResource(R.string.development_mlkit_notice))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DevelopmentTopBar(
    title: String, fontFamily: androidx.compose.ui.text.font.FontFamily, contentColor: Color, onBack: () -> Unit
) {
    val context = LocalContext.current
    TopAppBar(
        title = { Text(title, fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
        navigationIcon = {
            TextButton(
                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Back, onBack),
                colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.mock_back), Modifier.size(25.dp))
            }
        }
    )
}

@Composable
internal fun InformationScreenLayout(
    settings: AppSettings, title: String, onBack: () -> Unit,
    content: @Composable ColumnScope.(FontFamily, Color, Color) -> Unit
) {
    val fontFamily = rememberAppFontFamily(settings.font)
    val scrollState = rememberScrollState()
    val screenBackground = MaterialTheme.colorScheme.background
    val (primaryText, secondaryText) = rememberUiTextColors(settings.textColor, screenBackground)
    CompositionLocalProvider(LocalDevelopmentSettings provides settings) {
        AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled, animationSpeed = settings.animationSpeed) {
            Scaffold(containerColor = screenBackground, topBar = {
            DevelopmentTopBar(title, fontFamily, primaryText, onBack)
        }) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState)
                    .widthIn(max = 840.dp).padding(horizontal = 14.dp, vertical = 16.dp)) {
                    content(fontFamily, primaryText, secondaryText)
                    Spacer(Modifier.height(24.dp))
                }
                ScrollPositionCapsule(state = scrollState,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(paddingValues),
                    backgroundColor = screenBackground, preferredColor = primaryText)
            }
        }
    }
}
}

@Composable
internal fun DevelopmentSection(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val settings = LocalDevelopmentSettings.current
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val outline = MaterialTheme.colorScheme.outline
    val (text, secondary) = rememberUiTextColors(settings.textColor, background)
    val border = remember(background, outline) { ensureUiContrast(outline.copy(alpha = 0.55f), background, 2.2f) }
    val style = DevelopmentSectionStyle(rememberAppFontFamily(settings.font), text, secondary)
    Surface(modifier = modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(18.dp),
        color = background, border = BorderStroke(1.dp, border), tonalElevation = 0.dp) {
        CompositionLocalProvider(LocalDevelopmentSectionStyle provides style) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, color = text, fontFamily = style.fontFamily, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(10.dp))
                content()
            }
        }
    }
}

@Composable
private fun DevelopmentValueRow(label: String, value: String) {
    val style = LocalDevelopmentSectionStyle.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top) {
        Text(text = label, modifier = Modifier.weight(0.42f), color = style.secondary, fontFamily = style.fontFamily, fontSize = 13.sp)
        Text(text = value, modifier = Modifier.weight(0.58f), color = style.text, fontFamily = style.fontFamily,
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
internal fun DevelopmentParagraph(text: String) {
    val style = LocalDevelopmentSectionStyle.current
    Text(text = text, color = style.secondary, fontFamily = style.fontFamily, fontSize = 13.sp, lineHeight = 19.sp)
}

@Composable
private fun PerformanceProfileRow(title: String, detail: String, selected: Boolean) {
    val settings = LocalDevelopmentSettings.current
    val container = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    val (titleColor, detailColor) = rememberUiTextColors(settings.textColor, container)
    val fontFamily = rememberAppFontFamily(settings.font)
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = container, tonalElevation = 0.dp) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, modifier = Modifier.weight(1f), color = titleColor, fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (selected) {
                    Text(text = stringResource(R.string.development_current_profile), color = titleColor,
                        fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
            Text(text = detail, modifier = Modifier.padding(top = 3.dp), color = detailColor,
                fontFamily = fontFamily, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}
