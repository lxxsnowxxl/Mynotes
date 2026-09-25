package com.example.mynotes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.MenuPreferencePolicy
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import kotlin.math.roundToInt

private data class MenuOptionDescriptor(val key: String, val labelRes: Int)

private val MainMenuOptions = listOf(MenuOptionDescriptor("edit", R.string.mock_edit), MenuOptionDescriptor("favorite",
            R.string.mock_favorites), MenuOptionDescriptor("pin", R.string.mock_pin), MenuOptionDescriptor("priority",
            R.string.mock_priority), MenuOptionDescriptor("color", R.string.mock_color), MenuOptionDescriptor("move", R.string.mock_move),
        MenuOptionDescriptor("delete", R.string.mock_delete))
private val PriorityOptions = listOf(MenuOptionDescriptor("none", R.string.mock_priority_none), MenuOptionDescriptor("low",
            R.string.mock_priority_low), MenuOptionDescriptor("medium", R.string.mock_priority_medium), MenuOptionDescriptor("high",
            R.string.mock_priority_high))
private val ColorOptions = listOf(MenuOptionDescriptor("default", R.string.mock_color_default),
        MenuOptionDescriptor("yellow", R.string.mock_color_yellow), MenuOptionDescriptor("orange", R.string.mock_color_orange),
        MenuOptionDescriptor("red", R.string.mock_color_red), MenuOptionDescriptor("pink", R.string.mock_color_pink),
        MenuOptionDescriptor("purple", R.string.mock_color_purple), MenuOptionDescriptor("blue", R.string.mock_color_blue),
        MenuOptionDescriptor("cyan", R.string.mock_color_cyan), MenuOptionDescriptor("teal", R.string.mock_color_teal),
        MenuOptionDescriptor("green", R.string.mock_color_green), MenuOptionDescriptor("mint", R.string.mock_color_mint),
        MenuOptionDescriptor("lime", R.string.mock_color_lime), MenuOptionDescriptor("brown", R.string.mock_color_brown),
        MenuOptionDescriptor("gray", R.string.mock_color_gray))
private val MainMenuOptionsByKey = MainMenuOptions.associateBy { it.key }

/**
 * Variante por bloques para SettingsScreen. Cada bloque puede ser un item
 * independiente de LazyColumn, evitando que todos los submenús permanezcan
 * compuestos y medidos al mismo tiempo.
 */
@Composable
fun OptionsMenuHeader(
    fontFamily: FontFamily,
    textColor: Color,
    secondaryTextColor: Color
) {
    Text(
        text = stringResource(R.string.option_menu_customization_title),
        color = textColor,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
    Text(
        text = stringResource(R.string.option_menu_customization_description),
        modifier = Modifier.padding(top = 2.dp),
        color = secondaryTextColor,
        fontFamily = fontFamily,
        fontSize = 12.sp
    )
}

@Composable
fun OptionsMenuAppearanceSettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onShowIconsChange: (Boolean) -> Unit,
    onTextColorChange: (String) -> Unit,
    onOpacityChange: (Float) -> Unit
) {
    val context = LocalContext.current
    var opacity by remember(settings.optionMenuOpacity) { mutableFloatStateOf(settings.optionMenuOpacity) }
    var textColorMenuExpanded by remember { mutableStateOf(false) }

    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp)) { panelColors ->
        ToggleRow(
            title = stringResource(R.string.option_menu_show_icons),
            checked = settings.optionMenuShowIcons,
            onCheckedChange = onShowIconsChange,
            fontFamily = fontFamily,
            textColor = panelColors.text
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.option_menu_text_color),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = optionMenuTextColorLabel(settings.optionMenuTextColor),
                modifier = Modifier.weight(1f),
                color = panelColors.secondaryText,
                fontFamily = fontFamily,
                fontSize = 13.sp
            )
            androidx.compose.foundation.layout.Box {
                TextButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                    textColorMenuExpanded = true
                }) {
                    Text(text = stringResource(R.string.option_menu_change), color = panelColors.text, fontFamily = fontFamily)
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = panelColors.text,
                        modifier = Modifier.size(18.dp)
                    )
                }
                AppDropdownMenu(
                    modifier = Modifier.width(164.dp),
                    expanded = textColorMenuExpanded,
                    onDismissRequest = { textColorMenuExpanded = false },
                    properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)
                ) {
                    val options = remember {
                        listOf(
                            "note" to R.string.option_menu_text_follow_note,
                            "black" to R.string.option_menu_text_black,
                            "white" to R.string.option_menu_text_white
                        )
                    }
                    options.forEach { option ->
                        DropdownMenuItem(
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            text = {
                                Text(
                                    text = stringResource(option.second),
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
                                textColorMenuExpanded = false
                                onTextColorChange(option.first)
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.option_menu_opacity),
                color = panelColors.text,
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = "${opacity.roundToInt()}%",
                color = panelColors.secondaryText,
                fontFamily = fontFamily,
                fontSize = 12.sp
            )
        }
        Slider(
            value = opacity,
            onValueChange = {
                opacity = it
                UiSoundPlayer.playThrottled(context = context, sound = UiSound.SliderTick, minimumIntervalMs = 48L)
            },
            onValueChangeFinished = { onOpacityChange(opacity) },
            valueRange = 35f..100f
        )
    }
}

@Composable
fun OptionsMenuMainActionsSettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onOrderChange: (String) -> Unit,
    onHiddenItemsChange: (String) -> Unit
) {
    val orderedKeys = remember(settings.optionMenuOrder) { MenuPreferencePolicy.orderedKeys(settings.optionMenuOrder) }
    val hiddenMain = remember(settings.optionMenuHiddenItems) {
        MenuPreferencePolicy.hiddenKeys(settings.optionMenuHiddenItems, MenuPreferencePolicy.mainKeySet)
    }
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp)) { panelColors ->
        Text(
            text = stringResource(R.string.option_menu_main_actions),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.option_menu_main_actions_hint),
            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp),
            color = panelColors.secondaryText,
            fontFamily = fontFamily,
            fontSize = 12.sp
        )
        val enabledCount = orderedKeys.count { it !in hiddenMain }
        orderedKeys.forEachIndexed { index, key ->
            val descriptor = MainMenuOptionsByKey.getValue(key)
            val isVisible = key !in hiddenMain
            MenuOrderRow(
                label = stringResource(descriptor.labelRes),
                visible = isVisible,
                canHide = !isVisible || enabledCount > 1,
                canMoveUp = index > 0,
                canMoveDown = index < orderedKeys.lastIndex,
                onVisibleChange = { visible ->
                    val next = hiddenMain.toMutableSet()
                    if (visible) next.remove(key) else next.add(key)
                    onHiddenItemsChange(next.joinToString(","))
                },
                onMoveUp = {
                    val next = orderedKeys.toMutableList()
                    val previous = index - 1
                    val temp = next[previous]
                    next[previous] = next[index]
                    next[index] = temp
                    onOrderChange(next.joinToString(","))
                },
                onMoveDown = {
                    val next = orderedKeys.toMutableList()
                    val following = index + 1
                    val temp = next[following]
                    next[following] = next[index]
                    next[index] = temp
                    onOrderChange(next.joinToString(","))
                },
                fontFamily = fontFamily,
                textColor = panelColors.text
            )
        }
    }
}

@Composable
fun OptionsMenuPrioritySettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onPriorityHiddenItemsChange: (String) -> Unit
) {
    val hiddenPriorities = remember(settings.priorityMenuHiddenItems) {
        MenuPreferencePolicy.hiddenKeys(settings.priorityMenuHiddenItems, MenuPreferencePolicy.priorityKeys)
    }
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp)) { panelColors ->
        Text(
            text = stringResource(R.string.option_menu_priority_submenu),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        )
        val visiblePriorityCount = PriorityOptions.count { it.key !in hiddenPriorities }
        CompactToggleGrid(
            options = PriorityOptions,
            hiddenItems = hiddenPriorities,
            visibleCount = visiblePriorityCount,
            onHiddenItemsChange = onPriorityHiddenItemsChange,
            fontFamily = fontFamily,
            textColor = panelColors.text
        )
    }
}

@Composable
fun OptionsMenuColorSettingsSection(
    settings: AppSettings,
    fontFamily: FontFamily,
    onColorHiddenItemsChange: (String) -> Unit
) {
    val hiddenColors = remember(settings.colorMenuHiddenItems) {
        MenuPreferencePolicy.hiddenKeys(settings.colorMenuHiddenItems, MenuPreferencePolicy.colorKeys)
    }
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp)) { panelColors ->
        Text(
            text = stringResource(R.string.option_menu_color_submenu),
            color = panelColors.text,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
        )
        val visibleColorCount = ColorOptions.count { it.key !in hiddenColors }
        CompactToggleGrid(
            options = ColorOptions,
            hiddenItems = hiddenColors,
            visibleCount = visibleColorCount,
            onHiddenItemsChange = onColorHiddenItemsChange,
            fontFamily = fontFamily,
            textColor = panelColors.text
        )
    }
}

@Composable
fun OptionsMenuResetAction(
    fontFamily: FontFamily,
    textColor: Color,
    onReset: () -> Unit
) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = {
            UiSoundPlayer.playAction(context = context, action = UiActionSound.Restore)
            onReset()
        }) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = textColor)
            Text(
                text = stringResource(R.string.option_menu_reset),
                modifier = Modifier.padding(start = 6.dp),
                color = textColor,
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MenuOrderRow(label: String, visible: Boolean, canHide: Boolean, canMoveUp: Boolean, canMoveDown: Boolean,
    onVisibleChange: (Boolean) -> Unit, onMoveUp: () -> Unit, onMoveDown: () -> Unit, fontFamily: FontFamily, textColor: Color) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, modifier = Modifier.weight(1f), color = textColor, fontFamily = fontFamily, fontSize = 14.sp)
        IconButton(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Move)
                onMoveUp()
            }, enabled = canMoveUp, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = stringResource(R.string.option_menu_move_up), tint =
                    textColor.copy(alpha = if (canMoveUp) {
                                1f
                            } else {
                                0.28f
                            }))
        }
        IconButton(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Move)
                onMoveDown()
            }, enabled = canMoveDown, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = stringResource(R.string.option_menu_move_down), tint =
                    textColor.copy(alpha = if (canMoveDown) {
                                1f
                            } else {
                                0.28f
                            }))
        }
        Switch(checked = visible, onCheckedChange = { newChecked -> UiSoundPlayer.playToggle(context = context, checked = newChecked)
                onVisibleChange(newChecked)
            }, enabled = canHide)
    }
}

@Composable
private fun CompactToggleGrid(options: List<MenuOptionDescriptor>, hiddenItems: Set<String>, visibleCount: Int,
    onHiddenItemsChange: (String) -> Unit, fontFamily: FontFamily, textColor: Color) {
    val rows = remember(options) { options.chunked(2) }
    rows.forEach {
                rowOptions ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment =
                    Alignment.CenterVertically) {
                rowOptions.forEach {
                            option ->
                        val visible = option.key !in
                                hiddenItems
                        ToggleRow(title = stringResource(option.labelRes), checked = visible, onCheckedChange = {
                                checked ->
                                val next = hiddenItems.toMutableSet()
                                if (checked) {
                                    next.remove(option.key)
                                } else if (visibleCount >
                                    1) {
                                    next.add(option.key)
                                }
                                onHiddenItemsChange(next.joinToString(","))
                            }, enabled = !visible || visibleCount >
                                        1, fontFamily = fontFamily, textColor = textColor, modifier = Modifier.weight(1f), compact = true)
                    }
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color,
    enabled: Boolean = true, modifier: Modifier = Modifier, compact: Boolean = false) {
    val context = LocalContext.current
    Row(modifier = modifier.then(if (compact) {
                        Modifier
                    } else {
                        Modifier.fillMaxWidth()
                    }).padding(vertical = 0.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.weight(1f), color = textColor.copy(alpha = if (enabled) {
                            1f
                        } else {
                            0.42f
                        }), fontFamily = fontFamily, fontSize = if (compact) {
                    13.sp
                } else {
                    14.sp
                }, maxLines = if (compact) {
                    2
                } else {
                    Int.MAX_VALUE
                })
        Switch(checked = checked, onCheckedChange = { newChecked -> UiSoundPlayer.playToggle(context = context, checked = newChecked)
                onCheckedChange(newChecked)
            }, enabled = enabled)
    }
}

@Composable
private fun optionMenuTextColorLabel(value: String): String {
    return stringResource(when (value) {
            "black" -> R.string.option_menu_text_black
            "white" -> R.string.option_menu_text_white
            else -> R.string.option_menu_text_follow_note
        })
}
