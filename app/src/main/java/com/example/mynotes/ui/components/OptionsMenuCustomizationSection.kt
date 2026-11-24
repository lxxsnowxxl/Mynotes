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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import kotlin.math.roundToInt

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
    SettingsTitle(stringResource(R.string.option_menu_customization_title), textColor, fontFamily,
        fontSize = 20.sp)
    SettingsSecondaryText(stringResource(R.string.option_menu_customization_description), secondaryTextColor, fontFamily,
        Modifier.padding(top = 2.dp))
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

    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(14.dp)) { panelColors ->
        OptionsMenuHeader(fontFamily, panelColors.text, panelColors.secondaryText)
        Spacer(modifier = Modifier.height(12.dp))
        SettingsToggleRow(
            title = stringResource(R.string.option_menu_show_icons),
            checked = settings.optionMenuShowIcons,
            onCheckedChange = { checked -> onShowIconsChange(checked); UiSoundPlayer.playFallbackClick() },
            fontFamily = fontFamily,
            textColor = panelColors.text
        )
        Spacer(modifier = Modifier.height(8.dp))
        SettingsTitle(stringResource(R.string.option_menu_text_color), panelColors.text, fontFamily,
            fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                    val options = remember {
                        listOf(
                            "note" to R.string.option_menu_text_follow_note,
                            "black" to R.string.option_menu_text_black,
                            "white" to R.string.option_menu_text_white
                        )
                    }
                    options.forEach { option ->
                        SettingsDropdownItem(
                            label = stringResource(option.second),
                            textColor = panelColors.text, fontFamily = fontFamily,
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
            SettingsTitle(stringResource(R.string.option_menu_opacity), panelColors.text, fontFamily,
                fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            SettingsSecondaryText("${opacity.roundToInt()}%", panelColors.secondaryText, fontFamily)
        }
        StyledSettingsSlider(
            value = opacity,
            onValueChange = { opacity = it },
            onValueChangeFinished = { onOpacityChange(opacity) },
            valueRange = 35f..100f,
            valueLabel = "${opacity.roundToInt()}%",
            playTick = true
        )
    }
}

@Composable
private fun optionMenuTextColorLabel(value: String): String = stringResource(
    when (value) {
        "black" -> R.string.option_menu_text_black
        "white" -> R.string.option_menu_text_white
        else -> R.string.option_menu_text_follow_note
    }
)
