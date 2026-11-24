package com.example.mynotes.ui

import com.example.mynotes.ui.components.appScreenBackground
import com.example.mynotes.ui.components.appScreenContainerColor
import com.example.mynotes.ui.theme.noteUsesGlobalBackground
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.theme.rememberUiContentColors
import com.example.mynotes.ui.theme.rememberAppFontFamily
import com.example.mynotes.ui.components.AppIconButton
import com.example.mynotes.ui.components.AppIconLabel
import com.example.mynotes.ui.components.AppTextButton

import android.content.Context
import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.data.Note
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.MenuPreferencePolicy
import com.example.mynotes.ui.components.InlineNoteAttachment
import com.example.mynotes.ui.components.LinkPreviewCard
import com.example.mynotes.ui.components.extractLinkUrls
import com.example.mynotes.ui.components.noteTextForDisplay
import com.example.mynotes.ui.components.CategoryPill
import com.example.mynotes.ui.components.NotePaletteDialog
import com.example.mynotes.ui.components.ConfigurableDropdownMenuItem
import com.example.mynotes.ui.components.PriorityMenuItem
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.noteBackgroundColor
import com.example.mynotes.ui.state.NoteActions
import com.example.mynotes.ui.state.SettingsActions
import com.example.mynotes.ui.notes.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.TextButton
import com.example.mynotes.ui.components.StyledSettingsSlider
import androidx.activity.compose.BackHandler
import java.text.DateFormat
import java.util.Date

private val FavoriteGold = Color(0xFFF5A623)

private val DetailMenuKeys = listOf("edit", "priority", "color")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteDetailScreen(
    note: Note, noteViewModel: NoteActions, settings: AppSettings, onBack: () -> Unit, onEdit: (Note) -> Unit,
    embedded: Boolean = false, settingsActions: SettingsActions? = null,
    backgroundMotionPaused: androidx.compose.runtime.State<Boolean>? = null
) {
    val layoutPreview = LocalLayoutPreview.current
    val attachmentsFlow = remember(note.id) {
            noteViewModel.getAttachments(note.id)
        }
    val allAttachments by attachmentsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val attachments = allAttachments.filter { it.type != "cover" }
    val folders by noteViewModel.folders.collectAsStateWithLifecycle()
    val noteCatalog by noteViewModel.noteCatalog.collectAsStateWithLifecycle()
    var reading by rememberSaveable(note.id) { mutableStateOf(layoutPreview?.reading == true) }
    var readerSettings by remember { mutableStateOf(false) }
    var readerWidth by rememberSaveable { mutableStateOf(settings.readerWidth) }
    var readerSpacing by rememberSaveable { mutableStateOf(settings.readerLineSpacing) }
    BackHandler(enabled = reading) { reading = false }
    if (readerSettings) AppAlertDialog(onDismissRequest = { readerSettings = false },
        title = { Text(stringResource(R.string.organize_reader_settings)) },
        text = { Column {
            Text(stringResource(R.string.organize_reader_width))
            StyledSettingsSlider(
                value = readerWidth,
                onValueChange = { readerWidth = it },
                valueRange = 320f..900f,
                valueLabel = "${readerWidth.toInt()} dp"
            )
            Text(stringResource(R.string.organize_reader_spacing))
            StyledSettingsSlider(
                value = readerSpacing,
                onValueChange = { readerSpacing = it },
                valueRange = 1.2f..2.2f,
                valueLabel = "%.2fx".format(readerSpacing)
            )
        } }, confirmButton = { TextButton(onClick = {
            settingsActions?.setReaderPreferences(readerWidth, readerSpacing); readerSettings = false
        ; UiSoundPlayer.playFallbackClick()}) { Text(stringResource(R.string.organize_done)) } })
    val context = LocalContext.current
    val noteLinks = remember(note.content, note.formattingEnabled) {
            extractLinkUrls(if (note.formattingEnabled) NoteMarkup.render(note.content).text else note.content)
        }
    val displayContent = remember(note.content, noteLinks, note.formattingEnabled) {
            if (note.formattingEnabled) NoteMarkup.visibleSource(note.content) else noteTextForDisplay(note.content, noteLinks)
        }
    val fontFamily = rememberAppFontFamily(settings.font)
    val detailBackground = noteBackgroundColor(note.color)
    val useGlobalBackground = noteUsesGlobalBackground(note.color)
    val (noteTextColor, noteSecondaryTextColor, noteGraphicColor) = rememberUiContentColors(settings.textColor, detailBackground)
    val favoriteIconColor = ensureUiContrast(preferred = FavoriteGold, background = detailBackground, minimumContrast = 3f)
    val attachmentAddButtonContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val attachmentAddButtonContentColor = ensureUiContrast(
        resolveUiTextColor(value = settings.textColor, background = attachmentAddButtonContainerColor),
        attachmentAddButtonContainerColor, 4.5f)
    val useDarkDateChip = noteTextColor.luminance() > 0.7f
    val dateChipContainerColor = if (useDarkDateChip) {
            compositeUiColor(foreground = Color.Black.copy(alpha = 0.26f), background = detailBackground)
        } else {
            compositeUiColor(foreground = noteGraphicColor.copy(alpha = 0.08f), background = detailBackground)
        }
    val dateChipTextColor = if (useDarkDateChip) {
            Color.White
        } else {
            noteTextColor
        }
    val dateChipIconColor = if (useDarkDateChip) {
            Color.White.copy(alpha = 0.92f)
        } else {
            noteGraphicColor
        }
    val dateChipBorderColor = if (useDarkDateChip) {
            Color.White.copy(alpha = 0.30f)
        } else {
            noteGraphicColor.copy(alpha = 0.28f)
        }
    var mainMenuExpanded by remember { mutableStateOf(false) }
    var priorityMenuExpanded by remember { mutableStateOf(false) }
    var colorMenuExpanded by remember { mutableStateOf(false) }
    var moveMenuExpanded by remember { mutableStateOf(false) }
    var deleteDialogVisible by remember { mutableStateOf(layoutPreview?.deleteDialog == true) }
    val motionDuration = AppMotion.duration(AppMotion.FAST, settings.animationsEnabled, settings.animationSpeed)
    val effectiveOptionMenuTextColorMode = if (settings.optionMenuTextColor == "note") settings.textColor else settings.optionMenuTextColor
    val defaultPopupSurface = MaterialTheme.colorScheme.surfaceContainerHigh
    val inversePopupSurface = MaterialTheme.colorScheme.inverseSurface
    val popupBaseColor = when (effectiveOptionMenuTextColorMode) {
            "white" -> if (defaultPopupSurface.luminance() < 0.46f) defaultPopupSurface else inversePopupSurface
            "black" -> if (defaultPopupSurface.luminance() > 0.54f) defaultPopupSurface else inversePopupSurface
            else -> defaultPopupSurface
        }
    val popupAlpha = (settings.optionMenuOpacity / 100f).coerceIn(0.35f, 1f)
    val popupBackground = popupBaseColor.copy(alpha = popupAlpha)
    val popupVisualBackground = compositeUiColor(foreground = popupBackground, background = detailBackground)
    val optionMenuTextColor = ensureUiContrast(
        resolveUiTextColor(value = effectiveOptionMenuTextColorMode, background = popupVisualBackground),
        popupVisualBackground, 4.5f)
    val deleteMenuTextColor = ensureUiContrast(
        preferred = if (popupVisualBackground.luminance() < 0.5f) Color(0xFFFF8A80) else Color(0xFFB3261E),
        background = popupVisualBackground, minimumContrast = 4.5f
    )
    val hiddenMainMenuItems = remember(settings.optionMenuHiddenItems) {
            MenuPreferencePolicy.hiddenKeys(settings.optionMenuHiddenItems, MenuPreferencePolicy.mainKeySet)
        }
    val visibleDetailMenuItems = remember(settings.optionMenuOrder, hiddenMainMenuItems) {
            MenuPreferencePolicy.orderedKeys(settings.optionMenuOrder).filter {
                    it in
                        DetailMenuKeys
                }.filterNot {
                    it in
                        hiddenMainMenuItems
                }.ifEmpty {
                    listOf("edit")
                }
        }
    val hiddenPriorityMenuItems = remember(settings.priorityMenuHiddenItems) {
            MenuPreferencePolicy.hiddenKeys(settings.priorityMenuHiddenItems, MenuPreferencePolicy.priorityKeys)
        }
    val hiddenColorMenuItems = remember(settings.colorMenuHiddenItems) {
            MenuPreferencePolicy.hiddenKeys(settings.colorMenuHiddenItems, MenuPreferencePolicy.colorKeys)
        }
    val detailScrollState = rememberScrollState()
    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled,
        animationSpeed = settings.animationSpeed) {
        Scaffold(modifier = Modifier.appScreenBackground(detailBackground, noteTextColor, useGlobalStyle = useGlobalBackground,
            paused = backgroundMotionPaused), containerColor = Color.Transparent,
        contentColor = noteTextColor,
        topBar = {
            if (!reading) {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = appScreenContainerColor(detailBackground, useGlobalStyle = useGlobalBackground),
                            scrolledContainerColor = appScreenContainerColor(detailBackground, useGlobalStyle = useGlobalBackground),
                            titleContentColor = noteTextColor,
                            navigationIconContentColor = noteTextColor,
                            actionIconContentColor = noteTextColor),
                title = {
                    Text(text = "")
                },
                navigationIcon = {
                    AppIconButton(if (embedded) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                        stringResource(if (embedded) R.string.design_close_preview else R.string.mock_back),
                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Back, onBack))
                },
                actions = {
                    IconButton(onClick = { reading = true ; UiSoundPlayer.playFallbackClick()}) { Icon(Icons.Default.AutoStories, stringResource(R.string.organize_reader)) }
                    IconButton(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Favorite) {
                            noteViewModel.toggleFavorite(note)
                        }) {
                        Crossfade(targetState = note.isFavorite, animationSpec = tween(durationMillis = motionDuration)) {
                                favorite ->
                            Icon(imageVector = if (favorite) {
                                        Icons.Default.Star
                                    } else {
                                        Icons.Default.StarBorder
                                    },
                                contentDescription = stringResource(R.string.mock_favorites),
                                tint = if (favorite) {
                                        favoriteIconColor
                                    } else {
                                        noteGraphicColor
                                    })
                        }
                    }
                    Box {
                        AppIconButton(Icons.Default.MoreVert, stringResource(R.string.mock_settings),
                            onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                                mainMenuExpanded = true
                            })
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 420.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = mainMenuExpanded,
                            onDismissRequest = {
                                mainMenuExpanded = false
                            },
                            containerColor = popupBackground) {
                            visibleDetailMenuItems.forEach {
                                        key ->
                                    when (key) {
                                        "priority" -> {
                                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_priority), icon =
                                                    Icons.Default.PriorityHigh, showIcon = settings.optionMenuShowIcons, textColor =
                                                    optionMenuTextColor, fontFamily = fontFamily, onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                                                    mainMenuExpanded = false
                                                    priorityMenuExpanded = true
                                                })
                                        }
                                        "color" -> {
                                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_color), icon =
                                                    Icons.Default.Palette, showIcon = settings.optionMenuShowIcons, textColor =
                                                    optionMenuTextColor, fontFamily = fontFamily, onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                                                    mainMenuExpanded = false
                                                    colorMenuExpanded = true
                                                })
                                        }
                                        else -> {
                                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_edit), icon =
                                                    Icons.Default.Edit, showIcon = settings.optionMenuShowIcons, textColor =
                                                    optionMenuTextColor, fontFamily = fontFamily, onClick = {
                                                    UiSoundPlayer.play(context = context, sound = UiSound.Edit)
                                                    mainMenuExpanded = false
                                                    onEdit(note)
                                                })
                                        }
                                    }
                                }
                            // Estas acciones siguen disponibles como lo estaban en la barra inferior.
                            HorizontalDivider(color = optionMenuTextColor.copy(alpha = 0.18f))
                            ConfigurableDropdownMenuItem(
                                label = stringResource(R.string.mock_share), icon = Icons.Default.Share,
                                showIcon = settings.optionMenuShowIcons, textColor = optionMenuTextColor,
                                fontFamily = fontFamily, onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Share) {
                                    mainMenuExpanded = false
                                    shareNote(context = context, note = note)
                                })
                            ConfigurableDropdownMenuItem(
                                label = stringResource(if (note.isPinned) R.string.mock_unpin else R.string.mock_pin),
                                icon = Icons.Default.PushPin, showIcon = settings.optionMenuShowIcons,
                                textColor = optionMenuTextColor, fontFamily = fontFamily,
                                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Pin) {
                                    mainMenuExpanded = false
                                    noteViewModel.togglePinned(note)
                                })
                            ConfigurableDropdownMenuItem(
                                label = stringResource(R.string.mock_move), icon = Icons.Default.Folder,
                                showIcon = settings.optionMenuShowIcons, textColor = optionMenuTextColor,
                                fontFamily = fontFamily, onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Move) {
                                    mainMenuExpanded = false
                                    moveMenuExpanded = true
                                })
                            ConfigurableDropdownMenuItem(
                                label = stringResource(R.string.mock_delete), icon = Icons.Default.Delete,
                                showIcon = settings.optionMenuShowIcons, textColor = deleteMenuTextColor,
                                fontFamily = fontFamily, onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                                    mainMenuExpanded = false
                                    deleteDialogVisible = true
                                })
                        }
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = priorityMenuExpanded,
                            onDismissRequest = {
                                priorityMenuExpanded = false
                            },
                            containerColor = popupBackground) {
                            MenuPreferencePolicy.priorityOptions.filterNot { it.key in hiddenPriorityMenuItems }
                                .ifEmpty { MenuPreferencePolicy.priorityOptions.take(1) }.forEach { option ->
                                    PriorityMenuItem(
                                        label = stringResource(option.labelRes), selected = note.priority == option.value,
                                        textColor = optionMenuTextColor, showIndicator = false, fontFamily = fontFamily,
                                        onClick = {
                                            priorityMenuExpanded = false
                                            UiSoundPlayer.play(context = context, sound = UiSound.Priority)
                                            noteViewModel.changePriority(note = note, priority = option.value)
                                        })
                                }
                        }
                        if (colorMenuExpanded) {
                            NotePaletteDialog(
                                selectedColor = note.color,
                                hiddenColorItems = hiddenColorMenuItems,
                                containerColor = popupBackground,
                                contentColor = optionMenuTextColor,
                                onDismiss = { colorMenuExpanded = false },
                                onColorSelected = { color ->
                                    noteViewModel.changeNoteColor(note = note, color = color)
                                }
                            )
                        }
                        if (moveMenuExpanded) FolderPickerDialog(folders,
                            onSelect = { folder -> noteViewModel.moveNotes(setOf(note.id), folder); moveMenuExpanded = false },
                            onDismiss = { moveMenuExpanded = false })
                    }
                })
            }
        }, floatingActionButton = {
            if (reading) Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                Row {
                    IconButton(onClick = { readerSettings = true ; UiSoundPlayer.playFallbackClick()}) { Icon(Icons.Default.TextFields, stringResource(R.string.organize_reader_settings)) }
                    IconButton(onClick = { reading = false ; UiSoundPlayer.playFallbackClick()}) { Icon(Icons.Default.Close, stringResource(R.string.organize_exit_reader)) }
                }
            }
        }) {
            paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.widthIn(max = if (reading) readerWidth.dp else 900.dp).fillMaxWidth().verticalScroll(detailScrollState).padding(start = 22.dp,
                            end = 22.dp, bottom = 28.dp)) {
            if (note.coverUri != null || note.coverIcon != null) {
                NoteCover(note, Modifier.fillMaxWidth().height(220.dp), settings.performanceMode)
                Spacer(Modifier.height(16.dp))
            }
            Text(text = note.title.ifBlank {
                            stringResource(R.string.mock_untitled)
                        },
                color = noteTextColor,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (settings.fontSize + 13f).sp,
                lineHeight = (settings.fontSize + 17f).sp)
            if (!reading) {
            Spacer(modifier = Modifier.height(11.dp))
            FlowRow(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                itemVerticalAlignment = Alignment.CenterVertically) {
                PriorityPill(priority = note.priority, fontFamily = fontFamily, neutralForeground = dateChipTextColor, neutralBackground =
                        dateChipContainerColor, neutralBorder = dateChipBorderColor)
                CategoryPill(category = note.category, fontFamily = fontFamily)
                AssistChip(onClick = {
                    ; UiSoundPlayer.playFallbackClick()},
                    colors = AssistChipDefaults.assistChipColors(containerColor = dateChipContainerColor, labelColor = dateChipTextColor,
                            leadingIconContentColor = dateChipIconColor),
                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = dateChipBorderColor),
                    label = {
                        Text(text = remember(note.createdAt) {
                                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(note.createdAt))
                                },
                            fontFamily = fontFamily,
                            fontSize = 11.sp)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp))
                    })
            }
            folders.firstOrNull { it.id == note.folderId }?.let { Text(it.name, style = MaterialTheme.typography.labelLarge) }
            }
            com.example.mynotes.studio.PlusReadingTools(note, detailScrollState)
            if (displayContent.isNotBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                com.example.mynotes.ui.workspace.LinkedNotesSection(note.content, noteCatalog)
                if (note.formattingEnabled) RichNoteBody(displayContent,
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = fontFamily, fontSize = settings.fontSize.sp,
                        lineHeight = (if (reading) settings.fontSize * readerSpacing else settings.fontSize + 7f).sp),
                    color = noteSecondaryTextColor,
                    onToggleTask = { line -> noteViewModel.toggleTask(note.id, line) })
                else Text(displayContent, color = noteSecondaryTextColor, fontFamily = fontFamily, fontSize = settings.fontSize.sp,
                    lineHeight = (if (reading) settings.fontSize * readerSpacing else settings.fontSize + 7f).sp)
            }
            if (noteLinks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                noteLinks.take(3).forEach { linkUrl -> LinkPreviewCard(url = linkUrl, compact = false,
                            textColorMode = settings.textColor)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
            }
            Spacer(modifier = Modifier.height(26.dp))
            HorizontalDivider(color = noteGraphicColor.copy(alpha = 0.22f))
            Row(modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.mock_attachments),
                    modifier = Modifier.weight(1f),
                    color = noteTextColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp)
                if (!reading) Surface(shape = RoundedCornerShape(12.dp),
                    color = attachmentAddButtonContainerColor) {
                    AppIconButton(Icons.Default.Add, stringResource(R.string.mock_edit),
                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Add) {
                            onEdit(note)
                        }, modifier = Modifier.size(44.dp), tint = attachmentAddButtonContentColor)
                }
            }
            if (attachments.isEmpty()) {
                if (!reading) Text(text = stringResource(R.string.mock_no_attachments),
                    color = noteSecondaryTextColor,
                    fontFamily = fontFamily,
                    fontSize = 13.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    attachments.forEachIndexed {
                                index, attachment ->
                            val baseDelay = AppMotion.performanceValue(settings.performanceMode, 360L, 180L, 0L, 0L)
                            val staggerDelay = AppMotion.performanceValue(settings.performanceMode, 110L, 65L, 0L, 0L)
                            InlineNoteAttachment(attachment = attachment,
                                fontFamily = fontFamily,
                                previewDelayMillis = baseDelay + index * staggerDelay,
                                performanceMode = settings.performanceMode)
                        }
                }
                }
            }
            ScrollPositionCapsule(state = detailScrollState, modifier = Modifier.align(Alignment.CenterEnd), backgroundColor = detailBackground, preferredColor = noteGraphicColor)
        }
    }
    if (deleteDialogVisible) {
        AppAlertDialog(onDismissRequest = {
                deleteDialogVisible = false
            },
            title = {
                Text(text = stringResource(R.string.mock_delete_note_title), fontFamily = fontFamily)
            },
            text = {
                Text(text = stringResource(R.string.mock_delete_note_message), fontFamily = fontFamily)
            },
            confirmButton = {
                AppTextButton(stringResource(R.string.mock_delete),
                    onClick = {
                        deleteDialogVisible = false
                        UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                        noteViewModel.deleteNote(note)
                        onBack()
                    }, color = Color(0xFFD32F2F), fontFamily = fontFamily)
            },
            dismissButton = {
                AppTextButton(stringResource(R.string.mock_cancel),
                    onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Cancel) {
                        deleteDialogVisible = false
                    }, fontFamily = fontFamily)
            })
    }
    }
}

@Composable
private fun PriorityPill(priority: Int, fontFamily: FontFamily = FontFamily.Default, neutralForeground: Color = Color(0xFF4A4A4A),
    neutralBackground: Color = Color(0xFFF1F1F1), neutralBorder: Color = Color(0x334A4A4A)) {
    val label = stringResource(MenuPreferencePolicy.priorityOptions.firstOrNull { it.value == priority }?.labelRes
        ?: R.string.mock_priority_none)
    val foreground = when (priority) {
            3 -> Color(0xFFC62828)
            2 -> Color(0xFFB46A00)
            1 -> Color(0xFF4C6B8A)
            else -> neutralForeground
        }
    val background = if (priority == 0) {
            neutralBackground
        } else {
            foreground.copy(alpha = 0.10f)
        }
    Surface(shape = RoundedCornerShape(10.dp),
        color = background,
        border = if (priority == 0) {
                BorderStroke(width = 1.dp, color = neutralBorder)
            } else {
                null
            }) {
        Row(modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            AppIconLabel(Icons.Default.PriorityHigh, label, tint = foreground, iconModifier = Modifier.size(15.dp),
                textModifier = Modifier.padding(start = 4.dp), color = foreground, fontFamily = fontFamily, fontSize = 11.sp,
                fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

private fun shareNote(context: Context, note: Note) {
    val text = buildString {
            if (note.title.isNotBlank()) {
                append(note.title)
                append("\n\n")
            }
            append(NoteMarkup.shareText(note.content, note.formattingEnabled))
        }
    val intent = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
    context.startActivity(Intent.createChooser(intent, null))
}
