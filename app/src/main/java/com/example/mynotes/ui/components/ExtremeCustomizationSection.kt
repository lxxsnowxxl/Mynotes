package com.example.mynotes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.motion.ConfigurableAnimatedContent
import com.example.mynotes.ui.theme.PaletteCatalog
import kotlin.math.roundToInt

@Immutable
private data class IconStyleOption(val key: String, val labelRes: Int)

private val iconStyles = listOf(IconStyleOption("material", R.string.extreme_icon_material),
        IconStyleOption("rounded", R.string.extreme_icon_rounded), IconStyleOption("outlined", R.string.extreme_icon_outlined),
        IconStyleOption("minimal", R.string.extreme_icon_minimal))
@Immutable
private data class AccentOption(val key: String, val color: Color)

@Immutable
private data class MotionOption(val key: String, val labelRes: Int)

private val motionStyles = listOf(MotionOption("zoom", R.string.motion_style_zoom),
        MotionOption("zoom_fade", R.string.motion_style_zoom_fade), MotionOption("fade", R.string.motion_style_fade),
        MotionOption("slide_left", R.string.motion_style_slide_left), MotionOption("slide_right", R.string.motion_style_slide_right),
        MotionOption("slide_up", R.string.motion_style_slide_up), MotionOption("slide_down", R.string.motion_style_slide_down),
        MotionOption("slide_zoom_left", R.string.motion_style_slide_zoom_left),
        MotionOption("slide_zoom_up", R.string.motion_style_slide_zoom_up), MotionOption("axis_x", R.string.motion_style_axis_x),
        MotionOption("axis_y", R.string.motion_style_axis_y), MotionOption("axis_z", R.string.motion_style_axis_z),
        MotionOption("expand", R.string.motion_style_expand), MotionOption("expand_horizontal", R.string.motion_style_expand_horizontal),
        MotionOption("expand_vertical", R.string.motion_style_expand_vertical), MotionOption("bounce", R.string.motion_style_bounce),
        MotionOption("elastic", R.string.motion_style_elastic), MotionOption("pop", R.string.motion_style_pop),
        MotionOption("subtle", R.string.motion_style_subtle),
        MotionOption("expressive_spring", R.string.motion_style_expressive_spring),
        MotionOption("container_transform", R.string.motion_style_container_transform),
        MotionOption("soft_reveal", R.string.motion_style_soft_reveal),
        MotionOption("elastic_slide", R.string.motion_style_elastic_slide),
        MotionOption("predictive", R.string.motion_style_predictive),
        MotionOption("tonal_pop", R.string.motion_style_tonal_pop),
        MotionOption("random", R.string.motion_style_random))
private val motionEasings = listOf(MotionOption("standard", R.string.motion_easing_standard),
        MotionOption("linear", R.string.motion_easing_linear), MotionOption("accelerate", R.string.motion_easing_accelerate),
        MotionOption("decelerate", R.string.motion_easing_decelerate), MotionOption("emphasized", R.string.motion_easing_emphasized),
        MotionOption("expressive", R.string.motion_easing_expressive),
        MotionOption("emphasized_accel", R.string.motion_easing_emphasized_accel),
        MotionOption("emphasized_decel", R.string.motion_easing_emphasized_decel))
private val performanceModes = listOf(MotionOption("performance", R.string.performance_mode_performance),
        MotionOption("balanced", R.string.performance_mode_balanced), MotionOption("quality", R.string.performance_mode_quality))
private val accents = listOf(AccentOption("red", Color(0xFFE55757)), AccentOption("coral", Color(0xFFF27663)),
        AccentOption("orange", Color(0xFFEF8A39)), AccentOption("amber", Color(0xFFE8B632)), AccentOption("yellow", Color(0xFFF2CF45)),
        AccentOption("lime", Color(0xFF9BCB4B)), AccentOption("green", Color(0xFF63A85C)), AccentOption("mint", Color(0xFF58B98E)),
        AccentOption("teal", Color(0xFF3FA4A0)), AccentOption("cyan", Color(0xFF45B9C8)), AccentOption("sky", Color(0xFF4FA9E2)),
        AccentOption("blue", Color(0xFF4B8EDB)), AccentOption("indigo", Color(0xFF6275CF)), AccentOption("violet", Color(0xFF8B6BC5)),
        AccentOption("purple", Color(0xFFA05BC1)), AccentOption("pink", Color(0xFFD96787)), AccentOption("rose", Color(0xFFE16F9A)),
        AccentOption("brown", Color(0xFF9A7157)), AccentOption("graphite", Color(0xFF59636A)))


/**
 * Secciones avanzadas separadas para que SettingsScreen pueda colocarlas como
 * items independientes de su LazyColumn. De esta forma una sección pesada
 * (tarjetas, animaciones, acento, etc.) deja de mantener vivas a todas las
 * demás mientras se desplaza la pantalla.
 */
@Composable
fun ExtremeCustomizationHeader(
    fontFamily: FontFamily,
    textColor: Color
) {
    Text(
        text = stringResource(R.string.extreme_personalization),
        color = textColor,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
}

@Composable
fun ExtremeIconsSettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onIconStyleChange: (String) -> Unit,
    onIconSizeChange: (Float) -> Unit
) {
    val context = LocalContext.current
    var iconMenu by remember { mutableStateOf(false) }
    var iconSize by remember(settings.iconSize) { mutableFloatStateOf(settings.iconSize) }

    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(
            text = stringResource(R.string.extreme_icons),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                iconMenu = true
            }) {
                Text(
                    text = stringResource(
                        iconStyles.firstOrNull { it.key == settings.iconStyle }?.labelRes
                            ?: R.string.extreme_icon_rounded
                    ),
                    color = panelColors.text,
                    fontFamily = fontFamily
                )
            }
            val iconMenuLongestLabel = remember(iconStyles, context) {
                iconStyles.maxOfOrNull { context.getString(it.labelRes).length } ?: 0
            }
            val iconMenuWidth = when {
                iconMenuLongestLabel <= 8 -> 118.dp
                iconMenuLongestLabel <= 12 -> 138.dp
                else -> 158.dp
            }
            AppDropdownMenu(
                modifier = Modifier.width(iconMenuWidth),
                expanded = iconMenu,
                onDismissRequest = { iconMenu = false },
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                iconStyles.forEach { option ->
                    DropdownMenuItem(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        text = {
                            Text(
                                text = stringResource(option.labelRes),
                                modifier = Modifier.fillMaxWidth(),
                                color = panelColors.text,
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        },
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                            iconMenu = false
                            onIconStyleChange(option.key)
                        }
                    )
                }
            }
        }
        CustomSlider(
            title = stringResource(R.string.extreme_icon_size),
            label = "${iconSize.roundToInt()} dp",
            value = iconSize,
            onValueChange = { iconSize = it },
            onFinished = { onIconSizeChange(iconSize) },
            range = 16f..36f,
            steps = 19,
            settings = settings,
            fontFamily = fontFamily,
            textColor = panelColors.text
        )
    }
}

@Composable
fun ExtremeAccentSettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onAccentColorChange: (String) -> Unit
) {
    val context = LocalContext.current
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(
            text = stringResource(R.string.extreme_accent),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        val paletteAccent = remember(settings.backgroundColor) {
            PaletteCatalog.find(settings.backgroundColor).accent
        }
        val accentRows = remember { (listOf<AccentOption?>(null) + accents).chunked(5) }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            accentRows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowItems.forEach { option ->
                        val key = option?.key ?: "palette"
                        val color = option?.color ?: paletteAccent
                        val selected = settings.accentColor == key
                        val accentInteraction = remember(key) { MutableInteractionSource() }
                        val checkColor = if (color.luminance() > 0.48f) Color.Black else Color.White
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .drawWithCache {
                                    val selectedStroke = 3.dp.toPx()
                                    val normalStroke = 1.dp.toPx()
                                    val strokeColor = if (selected) panelColors.text
                                    else panelColors.graphic.copy(alpha = 0.65f)
                                    val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = if (selected) selectedStroke else normalStroke
                                    )
                                    onDrawBehind {
                                        drawCircle(color = color)
                                        drawCircle(
                                            color = strokeColor,
                                            style = stroke
                                        )
                                    }
                                }
                                .clickable(
                                    interactionSource = accentInteraction,
                                    indication = null
                                ) {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                                    onAccentColorChange(key)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = checkColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                    repeat(5 - rowItems.size) { Spacer(Modifier.size(48.dp)) }
                }
            }
        }
    }
}

@Composable
fun ExtremeNoteCardsSettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onNoteCardCornerRadiusChange: (Float) -> Unit,
    onNoteCardElevationChange: (Float) -> Unit,
    onNoteCardPaddingChange: (Float) -> Unit,
    onNoteCardImageHeightChange: (Float) -> Unit,
    onNoteCardOutlineWidthChange: (Float) -> Unit,
    onNoteTitleMaxLinesChange: (Int) -> Unit,
    onNoteContentMaxLinesChange: (Int) -> Unit,
    onNoteLineSpacingChange: (Float) -> Unit,
    onShowNoteDateChange: (Boolean) -> Unit,
    onShowCategoryChipChange: (Boolean) -> Unit,
    onShowFavoriteIconChange: (Boolean) -> Unit
) {
    var radius by remember(settings.noteCardCornerRadius) { mutableFloatStateOf(settings.noteCardCornerRadius) }
    var elevation by remember(settings.noteCardElevation) { mutableFloatStateOf(settings.noteCardElevation) }
    var padding by remember(settings.noteCardPadding) { mutableFloatStateOf(settings.noteCardPadding) }
    var imageHeight by remember(settings.noteCardImageHeight) { mutableFloatStateOf(settings.noteCardImageHeight) }
    var noteOutlineWidth by remember(settings.noteCardOutlineEnabled, settings.noteCardOutlineWidth) {
        mutableFloatStateOf(if (settings.noteCardOutlineEnabled) settings.noteCardOutlineWidth else 0f)
    }
    var titleLines by remember(settings.noteTitleMaxLines) { mutableFloatStateOf(settings.noteTitleMaxLines.toFloat()) }
    var contentLines by remember(settings.noteContentMaxLines) { mutableFloatStateOf(settings.noteContentMaxLines.toFloat()) }
    var lineSpacing by remember(settings.noteLineSpacing) { mutableFloatStateOf(settings.noteLineSpacing) }

    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(
            text = stringResource(R.string.extreme_note_cards),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        )
        CustomSlider(stringResource(R.string.extreme_card_radius), "${radius.roundToInt()} dp", radius, { radius = it },
            { onNoteCardCornerRadiusChange(radius) }, 0f..36f, 35, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_card_elevation), "${String.format("%.1f", elevation)} dp", elevation,
            { elevation = it }, { onNoteCardElevationChange(elevation) }, 0f..12f, 23, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_card_padding), "${padding.roundToInt()} dp", padding, { padding = it },
            { onNoteCardPaddingChange(padding) }, 6f..24f, 17, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_image_height), "${imageHeight.roundToInt()} dp", imageHeight, { imageHeight = it },
            { onNoteCardImageHeightChange(imageHeight) }, 72f..220f, 36, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_note_outline_width), String.format("%.1f dp", noteOutlineWidth), noteOutlineWidth,
            { noteOutlineWidth = it }, { onNoteCardOutlineWidthChange(noteOutlineWidth) }, 0f..6f, 11, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_title_lines), titleLines.roundToInt().toString(), titleLines, { titleLines = it },
            { onNoteTitleMaxLinesChange(titleLines.roundToInt()) }, 1f..8f, 6, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_content_lines), contentLines.roundToInt().toString(), contentLines,
            { contentLines = it }, { onNoteContentMaxLinesChange(contentLines.roundToInt()) }, 2f..14f, 11, settings, fontFamily,
            panelColors.text)
        CustomSlider(stringResource(R.string.extreme_line_spacing), String.format("%.2fx", lineSpacing), lineSpacing,
            { lineSpacing = it }, { onNoteLineSpacingChange(lineSpacing) }, 1f..1.8f, 15, settings, fontFamily, panelColors.text)
        ToggleRow(stringResource(R.string.extreme_show_date), settings.showNoteDate, onShowNoteDateChange, fontFamily, panelColors.text)
        ToggleRow(stringResource(R.string.extreme_show_category), settings.showCategoryChip, onShowCategoryChipChange, fontFamily,
            panelColors.text)
        ToggleRow(stringResource(R.string.extreme_show_favorite), settings.showFavoriteIcon, onShowFavoriteIconChange, fontFamily,
            panelColors.text)
    }
}

@Composable
fun ExtremeFabSettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onFabSizeChange: (Float) -> Unit
) {
    var fabSize by remember(settings.fabSize) { mutableFloatStateOf(settings.fabSize) }
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        CustomSlider(
            stringResource(R.string.extreme_fab_size),
            "${fabSize.roundToInt()} dp",
            fabSize,
            { fabSize = it },
            { onFabSizeChange(fabSize) },
            48f..82f,
            33,
            settings,
            fontFamily,
            panelColors.text
        )
    }
}

@Composable
fun ExtremePerformanceSettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onPerformanceModeChange: (String) -> Unit
) {
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        MotionOptionPicker(
            title = stringResource(R.string.performance_mode_title),
            selectedKey = settings.performanceMode,
            options = performanceModes,
            onSelected = onPerformanceModeChange,
            fontFamily = fontFamily,
            textColor = panelColors.text
        )
        Text(
            text = stringResource(R.string.performance_mode_description),
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp),
            color = panelColors.secondaryText,
            fontFamily = fontFamily,
            fontSize = 12.sp
        )
    }
}

@Composable
fun ExtremeMotionSettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onAnimationsEnabledChange: (Boolean) -> Unit,
    onAnimationStyleChange: (String) -> Unit,
    onAnimationEasingChange: (String) -> Unit,
    onAnimationSpeedChange: (Float) -> Unit,
    onAnimationIntensityChange: (Float) -> Unit
) {
    val context = LocalContext.current
    var animationSpeed by remember(settings.animationSpeed) { mutableFloatStateOf(settings.animationSpeed) }
    var animationIntensity by remember(settings.animationIntensity) { mutableFloatStateOf(settings.animationIntensity) }
    var previewState by remember { mutableStateOf(false) }

    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(
            text = stringResource(R.string.motion_title),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        )
        ToggleRow(
            title = stringResource(R.string.motion_enabled),
            checked = settings.animationsEnabled,
            onCheckedChange = onAnimationsEnabledChange,
            fontFamily = fontFamily,
            textColor = panelColors.text
        )
        if (settings.animationsEnabled) {
            MotionOptionPicker(stringResource(R.string.motion_style), settings.animationStyle, motionStyles,
                onAnimationStyleChange, fontFamily, panelColors.text)
            MotionOptionPicker(stringResource(R.string.motion_easing), settings.animationEasing, motionEasings,
                onAnimationEasingChange, fontFamily, panelColors.text)
            CustomSlider(stringResource(R.string.motion_speed), String.format("%.1fx", animationSpeed), animationSpeed,
                { animationSpeed = it }, { onAnimationSpeedChange(animationSpeed) }, 0.5f..2f, 14, settings, fontFamily,
                panelColors.text)
            CustomSlider(stringResource(R.string.motion_intensity), String.format("%.1fx", animationIntensity), animationIntensity,
                { animationIntensity = it }, { onAnimationIntensityChange(animationIntensity) }, 0.5f..1.5f, 9, settings,
                fontFamily, panelColors.text)
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.motion_preview),
                color = panelColors.text,
                fontFamily = fontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Surface(
                modifier = Modifier.fillMaxWidth().height(96.dp).padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                ConfigurableAnimatedContent(
                    targetState = previewState,
                    animationsEnabled = true,
                    animationSpeed = animationSpeed,
                    animationStyle = settings.animationStyle,
                    animationEasing = settings.animationEasing,
                    animationIntensity = animationIntensity,
                    performanceMode = settings.performanceMode
                ) { state ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (state) "B" else "A",
                            color = panelColors.text,
                            fontFamily = fontFamily,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            TextButton(
                onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                    previewState = !previewState
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = stringResource(R.string.motion_preview_button),
                    color = panelColors.text,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Text(
            text = stringResource(R.string.motion_description),
            modifier = Modifier.padding(top = 6.dp),
            color = panelColors.secondaryText,
            fontFamily = fontFamily,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun MotionOptionPicker(title: String, selectedKey: String, options: List<MotionOption>, onSelected: (String) -> Unit,
    fontFamily: FontFamily, textColor: Color) {
    val context = LocalContext.current
    var expanded by remember {
        mutableStateOf(false)
    }
    val selected = options.firstOrNull { it.key == selectedKey }?: options.first()
    // El ancho del popup se adapta a la opción más larga para evitar
    // columnas anchas con espacio vacío a los lados.
    val longestOptionLength = options.maxOfOrNull { context.getString(it.labelRes).length } ?: 0
    val compactMenuWidth = when {
            longestOptionLength <= 8 -> 118.dp
            longestOptionLength <= 12 -> 138.dp
            longestOptionLength <= 16 -> 158.dp
            longestOptionLength <= 20 -> 178.dp
            else -> 196.dp
        }
    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.weight(1f), color = textColor, fontFamily = fontFamily, fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold)
        Box {
            TextButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                    expanded = true
                }) {
                Text(text = stringResource(selected.labelRes), color = textColor, fontFamily = fontFamily, fontSize = 12.sp)
                Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null, tint = textColor, modifier = Modifier.size(18.dp))
            }
            AppDropdownMenu(modifier = Modifier.heightIn(max = 176.dp).width(compactMenuWidth), expanded = expanded, onDismissRequest = {
                    expanded = false
                }, properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                options.forEach { option -> DropdownMenuItem(modifier = Modifier.height(32.dp), contentPadding = PaddingValues(
                            horizontal = 4.dp, vertical = 0.dp), text = {
                            Text(text = stringResource(option.labelRes), modifier = Modifier.fillMaxWidth(), color = textColor,
                                fontFamily = fontFamily, fontSize = 12.sp, maxLines = 1, textAlign = TextAlign.Center, fontWeight =
                                    if (option.key == selectedKey) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    })
                        }, onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                            expanded = false
                            onSelected(option.key)
                        })
                }
            }
        }
    }
}

@Composable
private fun CustomSlider(title: String, label: String, value: Float, onValueChange: (Float) -> Unit, onFinished: () -> Unit,
    range: ClosedFloatingPointRange<Float>, steps: Int, settings: AppSettings, fontFamily: FontFamily, textColor: Color) {
    Spacer(Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.weight(1f), fontFamily = fontFamily, color = textColor, fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold)
        Text(text = label, fontFamily = fontFamily, color = textColor, fontSize = 12.sp)
    }
    StyledSettingsSlider(value = value, onValueChange = onValueChange, onValueChangeFinished = onFinished, valueRange = range,
        steps = steps, activeColor = MaterialTheme.colorScheme.primary,
        inactiveColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), style = settings.sliderStyle, valueLabel = label)
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.weight(1f), fontFamily = fontFamily, color = textColor, fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = { newChecked -> UiSoundPlayer.playToggle(context = context, checked = newChecked)
                onCheckedChange(newChecked)
            })
    }
}
