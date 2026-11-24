package com.example.mynotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.SettingsSectionColors
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.rememberAdaptiveUiButtonColors
import com.example.mynotes.ui.theme.settingsSectionColors
import com.example.mynotes.update.GitHubUpdateManager

/** Stateless presentation: the settings screen owns the requests and installation permission. */
@Composable
internal fun AppUpdatesSection(
    installedVersion: String,
    result: GitHubUpdateManager.CheckResult?,
    checking: Boolean,
    downloading: Boolean,
    actionMessage: String?,
    colors: SettingsSectionColors,
    textColorMode: String,
    fontFamily: FontFamily,
    onCheck: () -> Unit,
    onDownload: (GitHubUpdateManager.Release) -> Unit,
    onOpenRelease: (String?) -> Unit
) {
    val context = LocalContext.current
    val release = (result as? GitHubUpdateManager.CheckResult.UpdateAvailable)?.release
    val busy = checking || downloading
    val failed = !busy && (actionMessage != null || result is GitHubUpdateManager.CheckResult.Failure)
    val accent = ensureUiContrast(MaterialTheme.colorScheme.primary, colors.background, 4.5f)
    val statusAccent = if (failed) MaterialTheme.colorScheme.error else accent
    val statusBackground = lerp(colors.background, statusAccent, 0.08f)
    val statusIconBackground = lerp(statusBackground, statusAccent, 0.10f)
    val statusIconTint = ensureUiContrast(statusAccent, statusIconBackground, 3f)
    val statusColors = remember(statusBackground, textColorMode) {
        settingsSectionColors(statusBackground, textColorMode)
    }
    val statusIcon = when {
        failed -> Icons.Default.ErrorOutline
        downloading -> Icons.Default.Download
        checking -> Icons.Default.Refresh
        result is GitHubUpdateManager.CheckResult.UpToDate -> Icons.Default.CheckCircle
        release != null -> Icons.Default.SystemUpdateAlt
        else -> Icons.Default.Info
    }
    val statusTitle = stringResource(when {
        failed -> R.string.update_status_attention
        downloading -> R.string.update_downloading
        checking -> R.string.update_status_checking
        release != null -> R.string.update_status_available
        result is GitHubUpdateManager.CheckResult.UpToDate -> R.string.update_status_current
        result is GitHubUpdateManager.CheckResult.NoPublishedRelease -> R.string.update_status_no_release
        else -> R.string.update_status_ready
    })
    val statusDetail = when {
        checking -> stringResource(R.string.update_status_checking_detail)
        downloading -> stringResource(R.string.update_status_downloading_detail)
        actionMessage != null -> actionMessage
        result is GitHubUpdateManager.CheckResult.Failure -> stringResource(R.string.update_error, result.reason)
        release != null && release.apkDownloadUrl == null -> stringResource(R.string.update_release_without_apk)
        release != null -> release.title.takeIf { it.isNotBlank() && it != release.version }
            ?: stringResource(R.string.update_status_available_detail)
        result is GitHubUpdateManager.CheckResult.UpToDate -> stringResource(R.string.update_up_to_date, result.latestVersion)
        result is GitHubUpdateManager.CheckResult.NoPublishedRelease -> stringResource(R.string.update_no_release)
        else -> stringResource(R.string.update_status_ready_detail)
    }
    val buttonColors = rememberAdaptiveUiButtonColors(
        MaterialTheme.colorScheme.primary, colors.background, textColorMode
    )
    val disabledBackground = lerp(colors.background, buttonColors.container, 0.22f)
    val disabledContent = ensureUiContrast(colors.secondaryText, disabledBackground, 4.5f)

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_app_update_illustration),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = accent
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.update_settings_title), color = colors.text,
                    fontFamily = fontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.update_panel_subtitle), color = colors.secondaryText,
                    fontFamily = fontFamily, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }

        UpdateVersionRow(stringResource(R.string.update_installed_label), installedVersion,
            Icons.Default.PhoneAndroid, colors, fontFamily)
        if (release != null) {
            UpdateVersionRow(stringResource(R.string.update_latest_label), release.version,
                Icons.Default.SystemUpdateAlt, statusColors, fontFamily)
        }

        Surface(
            modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
            shape = RoundedCornerShape(16.dp),
            color = statusBackground,
            contentColor = statusColors.text
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(shape = CircleShape, color = statusIconBackground) {
                    if (busy) {
                        CircularProgressIndicator(Modifier.padding(8.dp).size(20.dp), strokeWidth = 2.dp,
                            color = statusIconTint)
                    } else {
                        Icon(statusIcon, contentDescription = null, modifier = Modifier.padding(8.dp).size(20.dp),
                            tint = statusIconTint)
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(statusTitle, color = statusColors.text, fontFamily = fontFamily,
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(statusDetail, color = statusColors.secondaryText, fontFamily = fontFamily,
                        fontSize = 12.sp, lineHeight = 18.sp)
                }
            }
        }

        val downloadAvailable = release?.apkDownloadUrl != null
        val label = stringResource(when {
            downloading -> R.string.update_downloading
            checking -> R.string.update_checking
            downloadAvailable -> R.string.update_download_install
            failed -> R.string.update_retry
            result != null -> R.string.update_check_again
            else -> R.string.update_check
        })
        Button(
            onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                if (downloadAvailable && release != null) onDownload(release) else onCheck()
            },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColors.container, contentColor = buttonColors.content,
                disabledContainerColor = disabledBackground,
                disabledContentColor = disabledContent
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(if (downloadAvailable) Icons.Default.Download else Icons.Default.Refresh,
                    contentDescription = null, modifier = Modifier.size(20.dp))
                Text(label, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
            }
        }
        if (release != null || result is GitHubUpdateManager.CheckResult.NoPublishedRelease) {
            Column(Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) { onOpenRelease(release?.htmlUrl) },
                    enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = accent)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.padding(end = 8.dp).size(18.dp))
                    Text(stringResource(R.string.update_open_release), fontFamily = fontFamily)
                }
                if (downloadAvailable) {
                    TextButton(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu, actionBlock = onCheck),
                        enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = colors.secondaryText)) {
                        Text(stringResource(R.string.update_check_again), fontFamily = fontFamily)
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateVersionRow(
    label: String, version: String, icon: ImageVector, colors: SettingsSectionColors, fontFamily: FontFamily
) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = colors.background,
        border = BorderStroke(1.dp, colors.graphic.copy(alpha = 0.28f))) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, contentDescription = null, tint = colors.graphic, modifier = Modifier.size(22.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, color = colors.secondaryText, fontFamily = fontFamily, fontSize = 11.sp)
                Text(version, color = colors.text, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
