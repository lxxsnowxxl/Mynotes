package com.example.mynotes.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import coil3.compose.AsyncImage
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.motion.ConfigurableAnimatedContent
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
        MotionOption("subtle", R.string.motion_style_subtle), MotionOption("random", R.string.motion_style_random))
private val motionEasings = listOf(MotionOption("standard", R.string.motion_easing_standard),
        MotionOption("linear", R.string.motion_easing_linear), MotionOption("accelerate", R.string.motion_easing_accelerate),
        MotionOption("decelerate", R.string.motion_easing_decelerate), MotionOption("emphasized", R.string.motion_easing_emphasized))
private val performanceModes = listOf(MotionOption("performance", R.string.performance_mode_performance),
        MotionOption("balanced", R.string.performance_mode_balanced), MotionOption("quality", R.string.performance_mode_quality))
private val accents = listOf(AccentOption("red", Color(0xFFE55757)), AccentOption("coral", Color(0xFFF27663)),
        AccentOption("orange", Color(0xFFEF8A39)), AccentOption("amber", Color(0xFFE8B632)), AccentOption("yellow", Color(0xFFF2CF45)),
        AccentOption("lime", Color(0xFF9BCB4B)), AccentOption("green", Color(0xFF63A85C)), AccentOption("mint", Color(0xFF58B98E)),
        AccentOption("teal", Color(0xFF3FA4A0)), AccentOption("cyan", Color(0xFF45B9C8)), AccentOption("sky", Color(0xFF4FA9E2)),
        AccentOption("blue", Color(0xFF4B8EDB)), AccentOption("indigo", Color(0xFF6275CF)), AccentOption("violet", Color(0xFF8B6BC5)),
        AccentOption("purple", Color(0xFFA05BC1)), AccentOption("pink", Color(0xFFD96787)), AccentOption("rose", Color(0xFFE16F9A)),
        AccentOption("brown", Color(0xFF9A7157)), AccentOption("graphite", Color(0xFF59636A)))
@Composable
fun ExtremeCustomizationSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color,
    graphicColor: Color, onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit,
    onIconStyleChange: (String) -> Unit, onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit,
    onNoteCardCornerRadiusChange: (Float) -> Unit, onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit,
    onNoteCardImageHeightChange: (Float) -> Unit, onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit,
    onNoteLineSpacingChange: (Float) -> Unit, onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit,
    onShowFavoriteIconChange: (Boolean) -> Unit, onFabSizeChange: (Float) -> Unit, onPerformanceModeChange: (String) -> Unit,
    onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit,
    onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit) {
    val context = LocalContext.current
    var iconMenu by remember {
        mutableStateOf(false)
    }
    var profileSize by remember {
        mutableFloatStateOf(settings.profileImageSize)
    }
    var iconSize by remember {
        mutableFloatStateOf(settings.iconSize)
    }
    var radius by remember {
        mutableFloatStateOf(settings.noteCardCornerRadius)
    }
    var elevation by remember {
        mutableFloatStateOf(settings.noteCardElevation)
    }
    var padding by remember {
        mutableFloatStateOf(settings.noteCardPadding)
    }
    var imageHeight by remember {
        mutableFloatStateOf(settings.noteCardImageHeight)
    }
    var titleLines by remember {
        mutableFloatStateOf(settings.noteTitleMaxLines.toFloat())
    }
    var contentLines by remember {
        mutableFloatStateOf(settings.noteContentMaxLines.toFloat())
    }
    var lineSpacing by remember {
        mutableFloatStateOf(settings.noteLineSpacing)
    }
    var fabSize by remember {
        mutableFloatStateOf(settings.fabSize)
    }
    var animationSpeed by remember {
        mutableFloatStateOf(settings.animationSpeed)
    }
    var animationIntensity by remember {
        mutableFloatStateOf(settings.animationIntensity)
    }
    var previewState by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(settings.profileImageSize) {
        profileSize = settings.profileImageSize
    }
    LaunchedEffect(settings.iconSize) {
        iconSize = settings.iconSize
    }
    LaunchedEffect(settings.noteCardCornerRadius) {
        radius = settings.noteCardCornerRadius
    }
    LaunchedEffect(settings.noteCardElevation) {
        elevation = settings.noteCardElevation
    }
    LaunchedEffect(settings.noteCardPadding) {
        padding = settings.noteCardPadding
    }
    LaunchedEffect(settings.noteCardImageHeight) {
        imageHeight = settings.noteCardImageHeight
    }
    LaunchedEffect(settings.noteTitleMaxLines) {
        titleLines = settings.noteTitleMaxLines.toFloat()
    }
    LaunchedEffect(settings.noteContentMaxLines) {
        contentLines = settings.noteContentMaxLines.toFloat()
    }
    LaunchedEffect(settings.noteLineSpacing) {
        lineSpacing = settings.noteLineSpacing
    }
    LaunchedEffect(settings.fabSize) {
        fabSize = settings.fabSize
    }
    LaunchedEffect(settings.animationSpeed) {
        animationSpeed = settings.animationSpeed
    }
    LaunchedEffect(settings.animationIntensity) {
        animationIntensity = settings.animationIntensity
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                uri ->
            if (uri != null) {
                try {
                    context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: SecurityException) {
                }
                UiSoundPlayer.play(context = context, sound = UiSound.Attachment)
                onProfileImageUriChange(uri.toString())
            }
        }
    Text(text = stringResource(R.string.extreme_personalization), color = textColor, fontFamily = fontFamily, fontWeight = FontWeight.Bold,
        fontSize = 20.sp)
    Spacer(Modifier.height(10.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(text = stringResource(R.string.extreme_profile), color = panelColors.text, fontFamily = fontFamily,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(74.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                if (settings.profileImageUri.isNotBlank()) {
                    AsyncImage(model = Uri.parse(settings.profileImageUri), contentDescription = null,
                        modifier = Modifier.fillMaxWidth().clip(CircleShape), contentScale = ContentScale.Crop)
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(34.dp),
                            tint = panelColors.text)
                    }
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                        picker.launch(arrayOf("image/*"))
                    }) {
                    Text(text = stringResource(R.string.extreme_change_photo), color = panelColors.text, fontFamily = fontFamily)
                }
                if (settings.profileImageUri.isNotBlank()) {
                    TextButton(onClick = {
                            UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                            onProfileImageUriChange("")
                        }) {
                        Text(text = stringResource(R.string.extreme_remove_photo), color = panelColors.text, fontFamily = fontFamily)
                    }
                }
            }
        }
        CustomSlider(title = stringResource(R.string.extreme_profile_size), label = "${profileSize.roundToInt()} dp", value = profileSize,
            onValueChange = { profileSize = it }, onFinished = {
                onProfileImageSizeChange(profileSize)
            }, range = 36f..84f, steps = 23, settings = settings, fontFamily = fontFamily, textColor = panelColors.text)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(text = stringResource(R.string.extreme_icons), color = panelColors.text, fontFamily = fontFamily, fontWeight = FontWeight.Bold
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                    iconMenu = true
                }) {
                Text(text = stringResource(iconStyles.firstOrNull {
                            it.key == settings.iconStyle
                        }?.labelRes ?: R.string.extreme_icon_rounded), color = panelColors.text, fontFamily = fontFamily)
            }
            val iconMenuLongestLabel = iconStyles.maxOfOrNull { context.getString(it.labelRes).length } ?: 0
            val iconMenuWidth = when {
                    iconMenuLongestLabel <= 8 -> 118.dp
                    iconMenuLongestLabel <= 12 -> 138.dp
                    else -> 158.dp
                }
            AppDropdownMenu(modifier = Modifier.width(iconMenuWidth), expanded = iconMenu, onDismissRequest = {
                    iconMenu = false
                }, properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                iconStyles.forEach {
                        option -> DropdownMenuItem(modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp), text = {
                            Text(text = stringResource(option.labelRes), modifier = Modifier.fillMaxWidth(), color = panelColors.text,
                                fontFamily = fontFamily, fontSize = 12.sp, maxLines = 1, textAlign = TextAlign.Center)
                        }, onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                            iconMenu = false
                            onIconStyleChange(option.key)
                        })
                }
            }
        }
        CustomSlider(title = stringResource(R.string.extreme_icon_size), label = "${iconSize.roundToInt()} dp", value = iconSize,
            onValueChange = { iconSize = it }, onFinished = {
                onIconSizeChange(iconSize)
            }, range = 16f..36f, steps = 19, settings = settings, fontFamily = fontFamily, textColor = panelColors.text)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(text = stringResource(R.string.extreme_accent), color = panelColors.text, fontFamily = fontFamily, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        /*
         * La paleta se reparte en 4 filas de 5 círculos.
         * Así ningún color queda cortado en pantallas estrechas y todos
         * conservan exactamente el mismo tamaño y separación visual.
         */
        val accentItems = remember {
                listOf<AccentOption?>(null) + accents
            }
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            accentItems.chunked(5).forEach { rowItems -> Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        rowItems.forEach { option -> val key = option?.key ?: "palette"
                            val color = option?.color?: MaterialTheme.colorScheme.primary
                            val selected = settings.accentColor == key
                            Surface(modifier = Modifier.size(48.dp).clickable {
                                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                                        onAccentColorChange(key)
                                    }, shape = CircleShape, border = BorderStroke(width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) {
                                        panelColors.text
                                    } else {
                                        panelColors.graphic
                                    }), color = color, shadowElevation = if (selected) 2.dp else 0.dp) {}
                        }
                        /*
                         * Solo se usa si en el futuro cambia el número de
                         * colores; mantiene la cuadrícula alineada.
                         */
                        repeat(5 - rowItems.size) {
                            Spacer(Modifier.size(48.dp))
                        }
                    }
                }
        }
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        Text(text = stringResource(R.string.extreme_note_cards), color = panelColors.text, fontFamily = fontFamily,
            fontWeight = FontWeight.Bold)
        CustomSlider(stringResource(R.string.extreme_card_radius), "${radius.roundToInt()} dp", radius, { radius = it },
            { onNoteCardCornerRadiusChange(radius) }, 0f..36f, 35, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_card_elevation), "${String.format("%.1f", elevation)} dp", elevation,
            { elevation = it }, { onNoteCardElevationChange(elevation) }, 0f..12f, 23, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_card_padding), "${padding.roundToInt()} dp", padding, { padding = it },
            { onNoteCardPaddingChange(padding) }, 6f..24f, 17, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_image_height), "${imageHeight.roundToInt()} dp", imageHeight, { imageHeight = it },
            { onNoteCardImageHeightChange(imageHeight) }, 72f..220f, 36, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_title_lines), titleLines.roundToInt().toString(), titleLines, { titleLines = it }, {
                onNoteTitleMaxLinesChange(titleLines.roundToInt())
            }, 1f..8f, 6, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_content_lines), contentLines.roundToInt().toString(), contentLines,
            { contentLines = it }, {
                onNoteContentMaxLinesChange(contentLines.roundToInt())
            }, 2f..14f, 11, settings, fontFamily, panelColors.text)
        CustomSlider(stringResource(R.string.extreme_line_spacing), String.format("%.2fx", lineSpacing), lineSpacing, { lineSpacing = it },
            { onNoteLineSpacingChange(lineSpacing) }, 1f..1.8f, 15, settings, fontFamily, panelColors.text)
        ToggleRow(stringResource(R.string.extreme_show_date), settings.showNoteDate, onShowNoteDateChange, fontFamily, panelColors.text)
        ToggleRow(stringResource(R.string.extreme_show_category), settings.showCategoryChip, onShowCategoryChipChange, fontFamily,
            panelColors.text)
        ToggleRow(stringResource(R.string.extreme_show_favorite), settings.showFavoriteIcon, onShowFavoriteIconChange, fontFamily,
            panelColors.text)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors -> CustomSlider(
            stringResource(R.string.extreme_fab_size), "${fabSize.roundToInt()} dp", fabSize, { fabSize = it },
            { onFabSizeChange(fabSize) }, 48f..82f, 33, settings, fontFamily, panelColors.text)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        MotionOptionPicker(title = stringResource(R.string.performance_mode_title), selectedKey = settings.performanceMode,
            options = performanceModes, onSelected = onPerformanceModeChange, fontFamily = fontFamily, textColor = panelColors.text)
        Text(text = stringResource(R.string.performance_mode_description), modifier = Modifier.padding(top = 2.dp, bottom = 14.dp),
            color = panelColors.secondaryText, fontFamily = fontFamily, fontSize = 12.sp)
    }
    Spacer(Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors -> Text(
            text = stringResource(R.string.motion_title), color = panelColors.text, fontFamily = fontFamily, fontWeight = FontWeight.Bold)
        ToggleRow(title = stringResource(R.string.motion_enabled), checked = settings.animationsEnabled,
            onCheckedChange = onAnimationsEnabledChange, fontFamily = fontFamily, textColor = panelColors.text)
        if (settings.animationsEnabled) {
            MotionOptionPicker(title = stringResource(R.string.motion_style), selectedKey = settings.animationStyle, options = motionStyles,
                onSelected = onAnimationStyleChange, fontFamily = fontFamily, textColor = panelColors.text)
            MotionOptionPicker(title = stringResource(R.string.motion_easing), selectedKey = settings.animationEasing,
                options = motionEasings, onSelected = onAnimationEasingChange, fontFamily = fontFamily, textColor = panelColors.text)
            CustomSlider(title = stringResource(R.string.motion_speed), label = String.format("%.1fx", animationSpeed),
                value = animationSpeed, onValueChange = {
                    animationSpeed = it
                }, onFinished = {
                    onAnimationSpeedChange(animationSpeed)
                }, range = 0.5f..2f, steps = 14, settings = settings, fontFamily = fontFamily, textColor = panelColors.text)
            CustomSlider(title = stringResource(R.string.motion_intensity), label = String.format("%.1fx", animationIntensity),
                value = animationIntensity, onValueChange = {
                    animationIntensity = it
                }, onFinished = {
                    onAnimationIntensityChange(animationIntensity)
                }, range = 0.5f..1.5f, steps = 9, settings = settings, fontFamily = fontFamily, textColor = panelColors.text)
            Spacer(Modifier.height(12.dp))
            Text(text = stringResource(R.string.motion_preview), color = panelColors.text, fontFamily = fontFamily, fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold)
            Surface(modifier = Modifier.fillMaxWidth().height(96.dp).padding(top = 8.dp), shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                ConfigurableAnimatedContent(targetState = previewState, animationsEnabled = true, animationSpeed = animationSpeed,
                    animationStyle = settings.animationStyle, animationEasing = settings.animationEasing,
                    animationIntensity = animationIntensity, performanceMode = settings.performanceMode) { state -> Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = if (state) "B" else "A", color = panelColors.text, fontFamily = fontFamily, fontSize = 28.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
            TextButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.PlayPause)
                    previewState = !previewState
                }, modifier = Modifier.align(Alignment.End)) {
                Text(text = stringResource(R.string.motion_preview_button), color = panelColors.text, fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold)
            }
        }
        Text(text = stringResource(R.string.motion_description), modifier = Modifier.padding(top = 6.dp), color = panelColors.secondaryText,
            fontFamily = fontFamily, fontSize = 12.sp)
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
