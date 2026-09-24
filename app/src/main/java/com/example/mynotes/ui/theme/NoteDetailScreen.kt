package com.example.mynotes.ui

import android.content.Context
import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.data.Note
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.components.InlineNoteAttachment
import com.example.mynotes.ui.components.LinkPreviewCard
import com.example.mynotes.ui.components.extractLinkUrls
import com.example.mynotes.ui.components.noteTextForDisplay
import com.example.mynotes.ui.components.CategoryPill
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.noteBackgroundColor
import com.example.mynotes.viewmodel.NoteViewModel
import java.text.DateFormat
import java.util.Date

private val FavoriteGold = Color(0xFFF5A623)

private val DetailMenuKeys = listOf("edit", "priority", "color")

private val DetailPriorityKeys = listOf("none", "low", "medium", "high")

private val DetailColorKeys = listOf("default", "yellow", "orange", "red", "pink", "purple", "blue", "cyan", "teal", "green", "mint",
        "lime", "brown", "gray")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteDetailScreen(note: Note, noteViewModel: NoteViewModel, settings: AppSettings, onBack: () -> Unit, onEdit: (Note) -> Unit) {
    /*
     * Recordamos el Flow por id para no recrearlo/recolectarlo de nuevo
     * durante recomposiciones visuales de esta pantalla.
     */
    val attachmentsFlow = remember(note.id) {
            noteViewModel.getAttachments(note.id)
        }
    val attachments by
        attachmentsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current
    val noteLinks = remember(note.content) {
            extractLinkUrls(note.content)
        }
    val displayContent = remember(note.content, noteLinks) {
            noteTextForDisplay(content = note.content, links = noteLinks)
        }
    val fontFamily = remember(settings.font) {
            appFontFamily(settings.font)
        }
    val detailBackground = noteBackgroundColor(note.color)
    val noteTextColor = resolveUiTextColor(value = settings.textColor, background = detailBackground)
    val noteSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = detailBackground)
    val noteGraphicColor = resolveUiGraphicColor(value = settings.textColor, background = detailBackground)
    val favoriteIconColor = ensureUiContrast(preferred = FavoriteGold, background = detailBackground, minimumContrast = 3f)
    val attachmentAddButtonContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val attachmentAddButtonContentColor = resolveUiTextColor(value = settings.textColor,
            background = attachmentAddButtonContainerColor)
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
    var mainMenuExpanded by
        remember {
            mutableStateOf(false)
        }
    var priorityMenuExpanded by
        remember {
            mutableStateOf(false)
        }
    var colorMenuExpanded by
        remember {
            mutableStateOf(false)
        }
    var moveMenuExpanded by
        remember {
            mutableStateOf(false)
        }
    var deleteDialogVisible by
        remember {
            mutableStateOf(false)
        }
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
    val optionMenuTextColor = resolveUiTextColor(value = effectiveOptionMenuTextColorMode, background = popupVisualBackground)
    val hiddenMainMenuItems = remember(settings.optionMenuHiddenItems) {
            parseDetailMenuKeys(settings.optionMenuHiddenItems, listOf("edit", "favorite", "pin", "priority", "color", "move", "delete"))
        }
    val visibleDetailMenuItems = remember(settings.optionMenuOrder, hiddenMainMenuItems) {
            normalizedDetailMenuOrder(settings.optionMenuOrder).filter {
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
            parseDetailMenuKeys(settings.priorityMenuHiddenItems, DetailPriorityKeys)
        }
    val hiddenColorMenuItems = remember(settings.colorMenuHiddenItems) {
            parseDetailMenuKeys(settings.colorMenuHiddenItems, DetailColorKeys)
        }
    val detailScrollState = rememberScrollState()
    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled,
        animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = detailBackground,
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = detailBackground,
                            titleContentColor = noteTextColor,
                            navigationIconContentColor = noteTextColor,
                            actionIconContentColor = noteTextColor),
                title = {
                    Text(text = "")
                },
                navigationIcon = {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                            onBack()
                        }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.mock_back))
                    }
                },
                actions = {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Favorite)
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
                        IconButton(onClick = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                mainMenuExpanded = true
                            }) {
                            Icon(imageVector = Icons.Default.MoreVert,
                                contentDescription = null)
                        }
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = mainMenuExpanded,
                            onDismissRequest = {
                                mainMenuExpanded = false
                            },
                            containerColor = popupBackground,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            visibleDetailMenuItems.forEach {
                                        key ->
                                    when (key) {
                                        "priority" -> {
                                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_priority), icon =
                                                    Icons.Default.PriorityHigh, showIcon = settings.optionMenuShowIcons, textColor =
                                                    optionMenuTextColor, fontFamily = fontFamily, onClick = {
                                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                                    mainMenuExpanded = false
                                                    priorityMenuExpanded = true
                                                })
                                        }
                                        "color" -> {
                                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_color), icon =
                                                    Icons.Default.Palette, showIcon = settings.optionMenuShowIcons, textColor =
                                                    optionMenuTextColor, fontFamily = fontFamily, onClick = {
                                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                                    mainMenuExpanded = false
                                                    colorMenuExpanded = true
                                                })
                                        }
                                        else -> {
                                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_edit), icon =
                                                    Icons.Default.Edit, showIcon = settings.optionMenuShowIcons, textColor =
                                                    optionMenuTextColor, fontFamily = fontFamily, onClick = {
                                                    UiSoundPlayer.play(context = context, sound = UiSound.Edit)
                                                    mainMenuExpanded = false
                                                    onEdit(note)
                                                })
                                        }
                                    }
                                }
                        }
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = priorityMenuExpanded,
                            onDismissRequest = {
                                priorityMenuExpanded = false
                            },
                            containerColor = popupBackground,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            listOf("none" to
                                    (0 to
                                            R.string.mock_priority_none), "low" to
                                    (1 to
                                            R.string.mock_priority_low), "medium" to
                                    (2 to
                                            R.string.mock_priority_medium), "high" to
                                    (3 to
                                            R.string.mock_priority_high)).filterNot {
                                    it.first in
                                        hiddenPriorityMenuItems
                                }.ifEmpty {
                                    listOf("none" to
                                            (0 to
                                                    R.string.mock_priority_none))
                                }.forEach {
                                        item ->
                                    DropdownMenuItem(modifier = Modifier.defaultMinSize(minHeight = 38.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        text = {
                                            Text(text = stringResource(item.second.second),
                                                color = optionMenuTextColor,
                                                fontFamily = fontFamily,
                                                fontWeight = if (note.priority == item.second.first) {
                                                        FontWeight.Bold
                                                    } else {
                                                        FontWeight.Normal
                                                    })
                                        },
                                        onClick = {
                                            priorityMenuExpanded = false
                                            UiSoundPlayer.play(context = context, sound = UiSound.Priority)
                                            noteViewModel.changePriority(note = note, priority = item.second.first)
                                        })
                                }
                        }
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = colorMenuExpanded,
                            onDismissRequest = {
                                colorMenuExpanded = false
                            },
                            containerColor = popupBackground,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            listOf("default" to R.string.mock_color_default, "yellow" to R.string.mock_color_yellow,
                                "orange" to R.string.mock_color_orange, "red" to R.string.mock_color_red,
                                "pink" to R.string.mock_color_pink, "purple" to R.string.mock_color_purple,
                                "blue" to R.string.mock_color_blue, "cyan" to R.string.mock_color_cyan, "teal" to R.string.mock_color_teal,
                                "green" to R.string.mock_color_green, "mint" to R.string.mock_color_mint,
                                "lime" to R.string.mock_color_lime, "brown" to R.string.mock_color_brown, "gray" to R.string.mock_color_gray
                            ).filterNot {
                                    it.first in
                                        hiddenColorMenuItems
                                }.ifEmpty {
                                    listOf("default" to
                                            R.string.mock_color_default)
                                }.forEach {
                                        item ->
                                    DropdownMenuItem(modifier = Modifier.defaultMinSize(minHeight = 38.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        text = {
                                            Text(text = stringResource(item.second), color = optionMenuTextColor,
                                                fontFamily = fontFamily)
                                        },
                                        onClick = {
                                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                                            colorMenuExpanded = false
                                            noteViewModel.changeNoteColor(note = note, color = item.first)
                                        })
                                }
                        }
                    }
                })
        },
        bottomBar = {
            Surface(color = detailBackground,
                shadowElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically) {
                    DetailBottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.Share,
                        label = stringResource(R.string.mock_share),
                        color = noteTextColor,
                        fontFamily = fontFamily,
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Share)
                            shareNote(context = context, note = note)
                        })
                    DetailBottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.PushPin,
                        label = if (note.isPinned) {
                                stringResource(R.string.mock_unpin)
                            } else {
                                stringResource(R.string.mock_pin)
                            },
                        color = noteTextColor,
                        fontFamily = fontFamily,
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Pin)
                            noteViewModel.togglePinned(note)
                        })
                    Box(modifier = Modifier.weight(1f)) {
                        DetailBottomAction(modifier = Modifier.fillMaxWidth(), icon = Icons.Default.Folder,
                            label = stringResource(R.string.mock_move),
                            color = noteTextColor,
                            fontFamily = fontFamily,
                            onClick = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Move)
                                moveMenuExpanded = true
                            })
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = moveMenuExpanded,
                            onDismissRequest = {
                                moveMenuExpanded = false
                            },
                            containerColor = popupBackground,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_work), icon = Icons.Default.Work, showIcon =
                                    settings.optionMenuShowIcons, textColor = optionMenuTextColor, fontFamily = fontFamily, onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Category)
                                    moveMenuExpanded = false
                                    noteViewModel.changeCategory(note = note, category = "work")
                                })
                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_personal), icon = Icons.Default.Person,
                                showIcon = settings.optionMenuShowIcons, textColor = optionMenuTextColor, fontFamily = fontFamily,
                                onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Category)
                                    moveMenuExpanded = false
                                    noteViewModel.changeCategory(note = note, category = "personal")
                                })
                        }
                    }
                    DetailBottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.Delete,
                        label = stringResource(R.string.mock_delete),
                        color = Color(0xFFD32F2F),
                        fontFamily = fontFamily,
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                            deleteDialogVisible = true
                        })
                }
            }
        }) {
            paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth().verticalScroll(detailScrollState).padding(start = 22.dp,
                            end = 22.dp, bottom = 28.dp)) {
            Text(text = note.title.ifBlank {
                            stringResource(R.string.mock_untitled)
                        },
                color = noteTextColor,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (settings.fontSize + 13f).sp,
                lineHeight = (settings.fontSize + 17f).sp)
            Spacer(modifier = Modifier.height(11.dp))
            /*
             * Metadata como en el mockup.
             */
            FlowRow(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                itemVerticalAlignment = Alignment.CenterVertically) {
                PriorityPill(priority = note.priority, fontFamily = fontFamily, neutralForeground = dateChipTextColor, neutralBackground =
                        dateChipContainerColor, neutralBorder = dateChipBorderColor)
                CategoryPill(category = note.category, fontFamily = fontFamily)
                AssistChip(onClick = {
                    },
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
            if (displayContent.isNotBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = displayContent,
                    color = noteSecondaryTextColor,
                    fontFamily = fontFamily,
                    fontSize = settings.fontSize.sp,
                    lineHeight = (settings.fontSize + 7f).sp)
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
                Surface(shape = RoundedCornerShape(12.dp),
                    color = attachmentAddButtonContainerColor) {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                            onEdit(note)
                        },
                        modifier = Modifier.size(44.dp)) {
                        Icon(imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.mock_edit),
                            tint = attachmentAddButtonContentColor)
                    }
                }
            }
            if (attachments.isEmpty()) {
                Text(text = stringResource(R.string.mock_no_attachments),
                    color = noteSecondaryTextColor,
                    fontFamily = fontFamily,
                    fontSize = 13.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    attachments.forEachIndexed {
                                index, attachment ->
                            val baseDelay = when (settings.performanceMode) {
                                    "performance" -> 360L
                                    "balanced" -> 180L
                                    else -> 0L
                                }
                            val staggerDelay = when (settings.performanceMode) {
                                    "performance" -> 110L
                                    "balanced" -> 65L
                                    else -> 0L
                                }
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
                TextButton(onClick = {
                        deleteDialogVisible = false
                        UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                        noteViewModel.deleteNote(note)
                        onBack()
                    }) {
                    Text(text = stringResource(R.string.mock_delete),
                        color = Color(0xFFD32F2F),
                        fontFamily = fontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Cancel)
                        deleteDialogVisible = false
                    }) {
                    Text(text = stringResource(R.string.mock_cancel), fontFamily = fontFamily)
                }
            })
    }
    }
}

@Composable
private fun PriorityPill(priority: Int, fontFamily: FontFamily = FontFamily.Default, neutralForeground: Color = Color(0xFF4A4A4A),
    neutralBackground: Color = Color(0xFFF1F1F1), neutralBorder: Color = Color(0x334A4A4A)) {
    val label = when (priority) {
            3 -> stringResource(R.string.mock_priority_high)
            2 -> stringResource(R.string.mock_priority_medium)
            1 -> stringResource(R.string.mock_priority_low)
            else -> stringResource(R.string.mock_priority_none)
        }
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
            Icon(imageVector = Icons.Default.PriorityHigh,
                contentDescription = null,
                tint = foreground,
                modifier = Modifier.size(15.dp))
            Text(text = label,
                modifier = Modifier.padding(start = 4.dp),
                color = foreground,
                fontFamily = fontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun DetailConfigurableMenuItem(label: String, icon:
        androidx.compose.ui.graphics.vector.ImageVector, showIcon: Boolean, textColor: Color, fontFamily: FontFamily = FontFamily.Default,
    onClick: () -> Unit) {
    DropdownMenuItem(modifier = Modifier.defaultMinSize(minHeight = 38.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
        text = {
            Text(text = label, color = textColor, fontFamily = fontFamily, fontSize = 13.sp, maxLines = 2)
        },
        leadingIcon = if (showIcon) {
                {
                    Icon(imageVector = icon, contentDescription = null, tint = textColor)
                }
            } else {
                null
            },
        onClick = onClick)
}

@Composable
private fun DetailBottomAction(modifier: Modifier = Modifier, icon:
        androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, fontFamily: FontFamily = FontFamily.Default,
    onClick: () -> Unit) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick,
            modifier = Modifier.size(48.dp)) {
            Icon(imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(21.dp))
        }
        Text(text = label,
            color = color,
            fontFamily = fontFamily,
            fontSize = 10.sp,
            maxLines = 1)
    }
}

private fun shareNote(context: Context, note: Note) {
    val text = buildString {
            if (note.title.isNotBlank()) {
                append(note.title)
                append("\n\n")
            }
            append(noteTextForDisplay(note.content, extractLinkUrls(note.content)))
        }
    val intent = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
    context.startActivity(Intent.createChooser(intent, null))
}

private fun normalizedDetailMenuOrder(raw: String): List<String> {
    val allKeys = listOf("edit", "favorite", "pin", "priority", "color", "move", "delete")
    val requested = raw.split(",").map {
                it.trim()
            }.filter {
                it in
                    allKeys
            }.distinct()
    return requested + allKeys.filterNot {
                it in
                    requested
            }
}

private fun parseDetailMenuKeys(raw: String, validKeys: List<String>): Set<String> {
    return raw.split(",").map {
            it.trim()
        }.filter {
            it in
                validKeys
        }.toSet()
}
