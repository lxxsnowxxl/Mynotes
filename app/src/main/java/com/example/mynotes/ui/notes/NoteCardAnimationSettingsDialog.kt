package com.example.mynotes.ui.notes

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.components.LocalAppBackground
import com.example.mynotes.ui.components.SettingsSecondaryText
import com.example.mynotes.ui.components.SettingsTitle
import com.example.mynotes.ui.components.StyledSettingsSlider
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.softenUiColorToContrast
import com.example.mynotes.ui.workspace.WorkspaceToolTheme
import com.example.mynotes.ui.workspace.workspaceToolColorScheme
import kotlin.math.roundToInt

private data class MotionChoice(
    val id: String, @StringRes val title: Int, @StringRes val description: Int, @DrawableRes val illustration: Int
)

private val choices = listOf(
    MotionChoice(IdleNoteMotionStyles.ANGULAR, R.string.card_motion_angular, R.string.card_motion_angular_desc, R.drawable.card_motion_art_angular),
    MotionChoice(IdleNoteMotionStyles.SWAY, R.string.card_motion_sway, R.string.card_motion_sway_desc, R.drawable.card_motion_art_sway),
    MotionChoice(IdleNoteMotionStyles.BREATHE, R.string.card_motion_breathe, R.string.card_motion_breathe_desc, R.drawable.card_motion_art_breathe),
    MotionChoice(IdleNoteMotionStyles.HEARTBEAT, R.string.card_motion_heartbeat, R.string.card_motion_heartbeat_desc, R.drawable.card_motion_art_heartbeat),
    MotionChoice(IdleNoteMotionStyles.SPRING, R.string.card_motion_spring, R.string.card_motion_spring_desc, R.drawable.card_motion_art_spring),
    MotionChoice(IdleNoteMotionStyles.RANDOM, R.string.card_motion_random, R.string.card_motion_random_desc, R.drawable.card_motion_art_random)
)
private val choiceRows = choices.chunked(2)
private val singleChoiceRows = choices.map { listOf(it) }

@Composable
private fun rememberMotionPanelColors(panelIntensity: Float): ColorScheme {
    val appColors = MaterialTheme.colorScheme
    val palette = LocalAppBackground.current.palette
    return remember(appColors, palette, panelIntensity) {
        workspaceToolColorScheme(appColors, palette, panelIntensity)
    }
}

@Composable
private fun MotionStyleArtwork(choice: MotionChoice, tint: Color, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(choice.illustration),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CardMotionSettingsEntry(
    options: IdleMotionOptions,
    animationsEnabled: Boolean,
    panelIntensity: Float,
    textColor: Color,
    secondaryColor: Color,
    fontFamily: FontFamily,
    onOpen: () -> Unit
) {
    val style = IdleNoteMotionStyles.normalize(options.styleId)
    val choice = choices.first { it.id == style }
    val controlsEnabled = options.enabled && animationsEnabled
    // The settings sample only moves after an explicit preview request.
    var previewToken by remember(style, options.intensityLevel, controlsEnabled) { mutableIntStateOf(0) }
    val colors = rememberMotionPanelColors(panelIntensity)
    val background = MaterialTheme.colorScheme.surfaceContainerLow
    val accent = ensureUiContrast(colors.primary, background, 4.5f)
    val badgeInk = ensureUiContrast(textColor, colors.primaryContainer, 4.5f)
    val largeText = LocalDensity.current.fontScale > 1.25f
    val status = stringResource(when {
        !options.enabled -> R.string.card_motion_off
        !animationsEnabled -> R.string.card_motion_entry_paused
        else -> R.string.card_motion_entry_enabled
    })
    val frequency = stringResource(when (options.frequency.coerceIn(1, 3)) {
        1 -> R.string.card_motion_frequency_low
        3 -> R.string.card_motion_frequency_high
        else -> R.string.card_motion_frequency_normal
    })

    Surface(modifier = Modifier.fillMaxWidth(), color = Color.Transparent, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CardMotionEntrySample(options, controlsEnabled && previewToken > 0, previewToken, colors)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTitle(stringResource(R.string.card_motion_settings_title), textColor, fontFamily,
                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    SettingsSecondaryText(stringResource(R.string.card_motion_entry_summary), secondaryColor,
                        fontFamily, lineHeight = 18.sp)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(stringResource(R.string.card_motion_selected_effect), color = secondaryColor,
                    fontFamily = fontFamily, fontSize = 11.sp)
                Text(stringResource(choice.title), color = textColor, fontFamily = fontFamily,
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                SettingsSecondaryText(stringResource(choice.description), secondaryColor, fontFamily, lineHeight = 18.sp)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                CardMotionDetailPill(status, colors.primaryContainer, badgeInk, fontFamily)
                CardMotionDetailPill(stringResource(R.string.card_motion_entry_intensity, options.intensityLevel.coerceIn(1, 5)),
                    colors.surfaceContainerHigh, ensureUiContrast(textColor, colors.surfaceContainerHigh, 4.5f), fontFamily)
                CardMotionDetailPill(stringResource(R.string.card_motion_entry_frequency, frequency),
                    colors.surfaceContainerHigh, ensureUiContrast(textColor, colors.surfaceContainerHigh, 4.5f), fontFamily)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.TouchApp, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                SettingsSecondaryText(
                    stringResource(when {
                        !animationsEnabled -> R.string.card_motion_global_disabled
                        !options.enabled -> R.string.card_motion_entry_disabled_hint
                        else -> R.string.card_motion_entry_behavior
                    }), secondaryColor, fontFamily, Modifier.weight(1f), lineHeight = 18.sp
                )
            }
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val previewButton: @Composable (Modifier) -> Unit = { modifier ->
                    TextButton(
                        enabled = controlsEnabled,
                        onClick = { previewToken++; UiSoundPlayer.playFallbackClick() },
                        modifier = modifier.heightIn(min = 48.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = accent)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(stringResource(R.string.card_motion_entry_preview), fontFamily = fontFamily,
                            modifier = Modifier.padding(start = 6.dp))
                    }
                }
                val customizeButton: @Composable (Modifier) -> Unit = { modifier ->
                    OutlinedButton(
                        onClick = onOpen, modifier = modifier.heightIn(min = 48.dp), shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.primaryContainer, contentColor = badgeInk),
                        border = BorderStroke(1.dp, ensureUiContrast(colors.outline, colors.primaryContainer, 3f))
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(stringResource(R.string.card_motion_entry_customize), fontFamily = fontFamily,
                            modifier = Modifier.padding(start = 6.dp), fontWeight = FontWeight.SemiBold)
                    }
                }
                if (largeText || maxWidth < 240.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        previewButton(Modifier.fillMaxWidth())
                        customizeButton(Modifier.fillMaxWidth())
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        previewButton(Modifier.weight(1f))
                        customizeButton(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CardMotionDetailPill(label: String, background: Color, ink: Color, fontFamily: FontFamily) {
    Surface(color = background, shape = RoundedCornerShape(10.dp), tonalElevation = 0.dp) {
        Text(label, Modifier.padding(horizontal = 9.dp, vertical = 6.dp), color = ink,
            fontFamily = fontFamily, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CardMotionEntrySample(options: IdleMotionOptions, enabled: Boolean, replayToken: Int, colors: ColorScheme) {
    Box(Modifier.size(width = 104.dp, height = 84.dp).background(colors.primaryContainer, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.size(width = 78.dp, height = 56.dp).then(rememberIdleNoteMotionPreview(
                enabled = enabled, replayToken = replayToken,
                intensityLevel = options.intensityLevel, styleId = options.styleId
            )),
            shape = RoundedCornerShape(10.dp), color = colors.surface,
            border = BorderStroke(1.dp, colors.outlineVariant), tonalElevation = 0.dp
        ) {
            val lineInk = ensureUiContrast(colors.primary, colors.surface, 3f)
            Column(Modifier.padding(9.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(Modifier.size(6.dp).background(lineInk, CircleShape))
                    Box(Modifier.width(31.dp).height(3.dp).background(lineInk, CircleShape))
                }
                Box(Modifier.fillMaxWidth().height(2.dp).background(lineInk.copy(alpha = 0.60f), CircleShape))
                Box(Modifier.width(34.dp).height(2.dp).background(lineInk.copy(alpha = 0.45f), CircleShape))
            }
        }
    }
}

@Composable
internal fun NoteCardAnimationSettingsDialog(
    options: IdleMotionOptions,
    globalAnimationsEnabled: Boolean,
    panelIntensity: Float,
    onChange: (IdleMotionOptions) -> Unit,
    onDismiss: () -> Unit
) {
    var previewToken by remember { mutableIntStateOf(0) }
    val selectedStyle = IdleNoteMotionStyles.normalize(options.styleId)
    val controlsEnabled = options.enabled && globalAnimationsEnabled
    val rows = if (LocalDensity.current.fontScale > 1.25f) singleChoiceRows else choiceRows

    val colors = rememberMotionPanelColors(panelIntensity)
    WorkspaceToolTheme(colors) {
        AppAlertDialog(
            onDismissRequest = onDismiss,
            containerColor = colors.background,
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(R.string.card_motion_settings_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = stringResource(R.string.card_motion_settings_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 525.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardMotionPreview(
                        options = options,
                        enabled = controlsEnabled,
                        replayToken = previewToken,
                        onReplay = {
                            previewToken++
                            UiSoundPlayer.playFallbackClick()
                        }
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            tonalElevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.card_motion_toggle),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = stringResource(R.string.card_motion_safety_hint),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = options.enabled,
                                    onCheckedChange = {
                                        onChange(options.copy(enabled = it))
                                        UiSoundPlayer.playFallbackClick()
                                    }
                                )
                            }
                        }

                        if (!globalAnimationsEnabled) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = colors.surfaceContainer,
                                tonalElevation = 0.dp
                            ) {
                                Text(
                                    text = stringResource(R.string.card_motion_global_disabled),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    color = colors.onSurface,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(R.string.card_motion_style_label),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            rows.forEach { rowChoices ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowChoices.forEach { choice ->
                                        MotionStyleTile(
                                            choice = choice,
                                            selected = selectedStyle == choice.id,
                                            enabled = options.enabled,
                                            modifier = Modifier.weight(1f).fillMaxHeight(),
                                            onClick = {
                                                onChange(options.copy(styleId = choice.id))
                                                previewToken++
                                                UiSoundPlayer.playFallbackClick()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            tonalElevation = 0.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CompactMotionSlider(
                                    label = stringResource(R.string.card_motion_intensity_label),
                                    value = options.intensityLevel,
                                    valueRange = 1..5,
                                    steps = 3,
                                    enabled = options.enabled,
                                    onValueChange = {
                                        onChange(options.copy(intensityLevel = it))
                                    }
                                )
                                CompactMotionSlider(
                                    label = stringResource(R.string.card_motion_frequency_label),
                                    value = options.frequency,
                                    valueRange = 1..3,
                                    steps = 1,
                                    enabled = options.enabled,
                                    onValueChange = { onChange(options.copy(frequency = it)) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        UiSoundPlayer.playFallbackClick()
                    }
                ) {
                    Text(stringResource(R.string.workspace_done))
                }
            }
        )
    }
}

@Composable
private fun CardMotionPreview(
    options: IdleMotionOptions,
    enabled: Boolean,
    replayToken: Int,
    onReplay: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.surfaceContainerHighest,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f).then(
                    rememberIdleNoteMotionPreview(
                        enabled = enabled,
                        replayToken = replayToken,
                        intensityLevel = options.intensityLevel,
                        styleId = options.styleId
                    )
                ),
                shape = RoundedCornerShape(13.dp),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.outlineVariant),
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.card_motion_preview),
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.onSurface
                    )
                    Box(Modifier.width(42.dp).height(2.dp).clip(CircleShape).background(colors.outlineVariant))
                    Box(Modifier.width(29.dp).height(2.dp).clip(CircleShape).background(colors.outlineVariant))
                }
            }
            TextButton(
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                onClick = onReplay
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp).padding(end = 2.dp)
                )
                Text(stringResource(R.string.card_motion_replay))
            }
        }
    }
}

@Composable
private fun MotionStyleTile(
    choice: MotionChoice,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val container = if (selected) colors.primaryContainer else colors.surfaceContainerHigh
    val label = if (selected) colors.onPrimaryContainer else colors.onSurface
    val supporting = remember(label, container) {
        softenUiColorToContrast(label, container, 4.5f, 0.48f)
    }
    val outline = if (selected) ensureUiContrast(colors.primary, container, 3f)
    else colors.outlineVariant.copy(alpha = 0.6f)
    val artInk = if (enabled) colors.primary else colors.primary.copy(alpha = 0.55f)

    Surface(
        modifier = modifier
            .heightIn(min = 48.dp)
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = container,
        contentColor = label,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, outline),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(62.dp)
                    .background(colors.surface, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                MotionStyleArtwork(choice, artInk, Modifier.size(width = 84.dp, height = 50.dp))
                if (selected) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(16.dp),
                        shape = CircleShape,
                        color = colors.primary,
                        contentColor = colors.onPrimary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(11.dp))
                        }
                    }
                }
            }
            Text(
                text = stringResource(choice.title),
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) label else supporting
            )
            Text(
                text = stringResource(choice.description),
                style = MaterialTheme.typography.bodySmall,
                color = supporting
            )
        }
    }
}

@Composable
private fun CompactMotionSlider(
    label: String,
    value: Int,
    valueRange: IntRange,
    steps: Int,
    enabled: Boolean,
    onValueChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                tonalElevation = 0.dp
            ) {
                Text(
                    text = value.toString(),
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        StyledSettingsSlider(
            value = value.toFloat(),
            onValueChange = {
                val newValue = it.roundToInt().coerceIn(valueRange.first, valueRange.last)
                if (newValue != value) onValueChange(newValue)
            },
            valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
            steps = steps,
            valueLabel = value.toString(),
            enabled = enabled
        )
    }
}
