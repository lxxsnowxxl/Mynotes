package com.example.mynotes.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.settings.MenuPreferencePolicy
import com.example.mynotes.ui.theme.automaticUiTextColor
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.noteBackgroundColor
import com.example.mynotes.ui.theme.paletteMatchedOutlineColor
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.media.formatMinuteSecondDuration
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import java.text.DateFormat
import java.util.Date

private val FavoriteGold = Color(0xFFF5A623)

/*
 * Tarjeta reutilizable de la pantalla principal.
 *
 * Mantiene:
 * - imagen superior cuando existe;
 * - título;
 * - contenido;
 * - favorita;
 * - fijada;
 * - categoría;
 * - fecha;
 * - menú de prioridad/color/categoría.
 */
@Composable
fun ModernNoteCard(note: Note, attachments: List<Attachment>, fontFamily: FontFamily, fontSize: Float,
    style: NoteCardStyle, textColorMode: String, optionMenuOrder: String, optionMenuHiddenItems: String, optionMenuShowIcons: Boolean, optionMenuTextColor: String,
    optionMenuOpacity: Float, priorityMenuHiddenItems: String, colorMenuHiddenItems: String, performanceMode: String,
    isScrolling: Boolean = false, onOpen: () -> Unit, onEdit: () -> Unit, onToggleFavorite: () -> Unit, onTogglePinned: () -> Unit,
    onPriorityChange: (Int) -> Unit,
    onColorChange: (String) -> Unit, onCategoryChange: (String) -> Unit, onDelete: () -> Unit) {
    val cardColor = noteBackgroundColor(note.color)
    val textColor = resolveUiTextColor(value = textColorMode, background = cardColor)
    val secondaryTextColor = resolveSecondaryUiTextColor(value = textColorMode, background = cardColor)
    val graphicColor = resolveUiGraphicColor(value = textColorMode, background = cardColor)
    val favoriteIconColor = ensureUiContrast(preferred = FavoriteGold, background = cardColor, minimumContrast = 3f)
    /*
     * Durante un gesto de scroll evitamos animaciones internas de tamaño y
     * transiciones que pueden obligar al StaggeredGrid a re-medir tarjetas
     * durante varios frames. Fuera del scroll se conserva exactamente la
     * configuración de animaciones elegida por el usuario.
     */
    val motionDuration = if (isScrolling) 0 else AppMotion.duration(AppMotion.FAST, style.animationsEnabled, style.animationSpeed)
    val animatedCardColor by
        animateColorAsState(targetValue = cardColor, animationSpec = tween(durationMillis = motionDuration), label = "noteCardColor")
    val previewAttachments = remember(attachments) {
        attachments.take(4)
    }
    val noteLinks = remember(note.content) {
        extractLinkUrls(note.content)
    }
    val displayContent = remember(note.content, noteLinks) {
        noteTextForDisplay(content = note.content, links = noteLinks)
    }
    var mainMenuExpanded by remember { mutableStateOf(false) }
    var priorityMenuExpanded by remember { mutableStateOf(false) }
    var colorMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val effectiveMenuTextColorMode = if (optionMenuTextColor == "note") textColorMode else optionMenuTextColor
    val defaultPopupSurface = MaterialTheme.colorScheme.surfaceContainerHigh
    val inversePopupSurface = MaterialTheme.colorScheme.inverseSurface
    val popupBaseColor = when (effectiveMenuTextColorMode) {
        "white" -> if (defaultPopupSurface.luminance() < 0.46f) defaultPopupSurface else inversePopupSurface
        "black" -> if (defaultPopupSurface.luminance() > 0.54f) defaultPopupSurface else inversePopupSurface
        else -> defaultPopupSurface
    }
    val popupAlpha = (optionMenuOpacity / 100f).coerceIn(0.35f, 1f)
    val popupColor = popupBaseColor.copy(alpha = popupAlpha)
    val popupVisualBackground = compositeUiColor(foreground = popupColor, background = cardColor)
    val noteOutlineColor = paletteMatchedOutlineColor(animatedCardColor)
    val menuTextColor = resolveUiTextColor(value = effectiveMenuTextColorMode, background = popupVisualBackground)
    val mainMenuOrder = remember(optionMenuOrder) {
        MenuPreferencePolicy.orderedKeys(optionMenuOrder)
    }
    val hiddenMainMenuItems = remember(optionMenuHiddenItems) {
        MenuPreferencePolicy.hiddenKeys(optionMenuHiddenItems, MenuPreferencePolicy.mainKeySet)
    }
    val visibleMainMenuItems = remember(mainMenuOrder, hiddenMainMenuItems) {
        mainMenuOrder.filterNot { it in hiddenMainMenuItems }.ifEmpty { listOf("edit") }
    }
    val hiddenPriorityItems = remember(priorityMenuHiddenItems) {
        MenuPreferencePolicy.hiddenKeys(priorityMenuHiddenItems, MenuPreferencePolicy.priorityKeys)
    }
    val visiblePriorityOptions = remember(hiddenPriorityItems) {
        MenuPreferencePolicy.priorityOptions.filterNot { it.key in hiddenPriorityItems }.ifEmpty { MenuPreferencePolicy.priorityOptions }
    }
    val hiddenColorItems = remember(colorMenuHiddenItems) {
        MenuPreferencePolicy.hiddenKeys(colorMenuHiddenItems, MenuPreferencePolicy.colorKeys)
    }
    val visibleColorOptions = remember(hiddenColorItems) {
        MenuPreferencePolicy.colorOptions.filterNot { it.key in hiddenColorItems }.ifEmpty { MenuPreferencePolicy.colorOptions }
    }
    val cardInteractionSource = remember { MutableInteractionSource() }

    Card(modifier = Modifier.fillMaxWidth().clickable(interactionSource = cardInteractionSource,
                indication = null,
                onClick = onOpen),
        shape = RoundedCornerShape(style.cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = animatedCardColor),
        border = if (style.outlineEnabled) BorderStroke(style.outlineWidth.dp, noteOutlineColor) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = style.elevation.dp)) {
        val cardContentModifier = if (isScrolling) {
            Modifier
        } else {
            Modifier.animateContentSize(animationSpec = tween(durationMillis = motionDuration))
        }
        val outlineContentInset = if (style.outlineEnabled) {
            (style.outlineWidth * 0.90f).coerceIn(0f, 5.4f)
        } else {
            0f
        }

        Column(modifier = cardContentModifier) {
            val contentHorizontalPadding = (style.padding + outlineContentInset).dp
            val mediaHorizontalPadding = (outlineContentInset * 0.10f).coerceAtLeast(1f).dp
            val topContentPadding = (style.padding * 0.75f + outlineContentInset * 0.45f).dp
            val mediaTopPadding = (outlineContentInset * 0.10f).coerceAtLeast(1f).dp
            val bottomContentPadding = (style.padding * 0.90f + outlineContentInset).dp
            val hasMediaPreview = previewAttachments.isNotEmpty() || noteLinks.isNotEmpty()
            val mediaPreviewHeight = (style.imageHeight * 1.10f).dp
            val cardTitleFontSize = (fontSize + 1f).coerceAtLeast(12f)
            val cardBodyFontSize = (fontSize - 2f).coerceAtLeast(11f)
            val cardTitleLineHeight = (cardTitleFontSize * 1.22f).coerceAtLeast(cardTitleFontSize + 2f)
            val cardBodyLineHeight = (cardBodyFontSize * style.lineSpacing).coerceAtLeast(cardBodyFontSize + 1f)
            val actionRow: @Composable (Modifier, Boolean) -> Unit = { actionModifier, overlayMode ->
                NoteCardActionRow(modifier = actionModifier, overlayMode = overlayMode, note = note, showFavorite = style.showFavorite,
                    graphicColor = graphicColor, favoriteIconColor = favoriteIconColor, menuTextColor = menuTextColor, popupColor = popupColor,
                    motionDuration = motionDuration, optionMenuShowIcons = optionMenuShowIcons, visibleMainMenuItems = visibleMainMenuItems,
                    visiblePriorityOptions = visiblePriorityOptions, visibleColorOptions = visibleColorOptions, mainMenuExpanded = mainMenuExpanded,
                    onMainMenuExpandedChange = { mainMenuExpanded = it }, priorityMenuExpanded = priorityMenuExpanded,
                    onPriorityMenuExpandedChange = { priorityMenuExpanded = it }, colorMenuExpanded = colorMenuExpanded,
                    onColorMenuExpandedChange = { colorMenuExpanded = it }, categoryMenuExpanded = categoryMenuExpanded,
                    onCategoryMenuExpandedChange = { categoryMenuExpanded = it }, onEdit = onEdit, onToggleFavorite = onToggleFavorite,
                    onTogglePinned = onTogglePinned, onPriorityChange = onPriorityChange, onColorChange = onColorChange,
                    onCategoryChange = onCategoryChange, onDelete = onDelete)
            }

            if (hasMediaPreview) {
                Box(modifier = Modifier.fillMaxWidth().padding(
                        start = mediaHorizontalPadding,
                        end = mediaHorizontalPadding,
                        top = mediaTopPadding,
                        bottom = 0.dp
                    )) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (previewAttachments.isNotEmpty()) {
                            NoteCardAttachmentsPreview(attachments = previewAttachments,
                                previewHeight = mediaPreviewHeight,
                                cornerRadius = style.cornerRadius.dp,
                                fontFamily = fontFamily,
                                performanceMode = performanceMode,
                                deferHeavyLoads = isScrolling && performanceMode != "quality",
                                isScrolling = isScrolling)
                        }
                        if (noteLinks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(if (previewAttachments.isNotEmpty()) 6.dp else 0.dp))
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(style.cornerRadius.dp))) {
                                LinkPreviewCard(url = noteLinks.first(), modifier = Modifier.fillMaxWidth(), textColorMode = textColorMode,
                                    deferLoad = isScrolling)
                            }
                        }
                    }
                    actionRow(Modifier.align(Alignment.TopEnd).padding(top = 6.dp, end = 6.dp), true)
                }
            } else {
                actionRow(Modifier.fillMaxWidth().padding(
                        start = contentHorizontalPadding, end = contentHorizontalPadding, top = topContentPadding, bottom = 0.dp), false)
            }

            Column(modifier = Modifier.fillMaxWidth().padding(
                    start = contentHorizontalPadding,
                    end = contentHorizontalPadding,
                    top = if (hasMediaPreview) 6.dp else 4.dp,
                    bottom = 0.dp
                )) {
                Text(text = note.title.ifBlank {
                            stringResource(R.string.mock_untitled)
                        },
                    modifier = Modifier.fillMaxWidth(),
                    color = textColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = cardTitleFontSize.sp,
                    lineHeight = cardTitleLineHeight.sp,
                    maxLines = style.titleMaxLines,
                    overflow = TextOverflow.Ellipsis)
                if (displayContent.isNotBlank()) {
                    Text(text = displayContent,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        color = secondaryTextColor,
                        fontFamily = fontFamily,
                        fontSize = cardBodyFontSize.sp,
                        lineHeight = cardBodyLineHeight.sp,
                        maxLines = style.contentMaxLines,
                        overflow = TextOverflow.Ellipsis)
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(
                    start = contentHorizontalPadding,
                    end = contentHorizontalPadding,
                    top = 8.dp,
                    bottom = bottomContentPadding
                )) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = (outlineContentInset * 0.20f).dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    if (style.showCategory) {
                        CategoryPill(category = note.category)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (style.showDate) {
                        Text(text = remember(note.createdAt) {
                                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(note.createdAt))
                                },
                            color = secondaryTextColor,
                            fontFamily = fontFamily,
                            fontSize = 10.sp,
                            maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCardActionButton(
    overlayMode: Boolean, overlayModifier: Modifier, buttonModifier: Modifier, onClick: () -> Unit, content: @Composable () -> Unit
) {
    if (overlayMode) Box(overlayModifier.clickable(onClick = onClick), contentAlignment = Alignment.Center) { content() }
    else IconButton(onClick = onClick, modifier = buttonModifier) { content() }
}

@Composable
private fun NoteCardActionRow(
    modifier: Modifier = Modifier,
    overlayMode: Boolean,
    note: Note,
    showFavorite: Boolean,
    graphicColor: Color,
    favoriteIconColor: Color,
    menuTextColor: Color,
    popupColor: Color,
    motionDuration: Int,
    optionMenuShowIcons: Boolean,
    visibleMainMenuItems: List<String>,
    visiblePriorityOptions: List<MenuPreferencePolicy.PriorityOption>,
    visibleColorOptions: List<MenuPreferencePolicy.ColorOption>,
    mainMenuExpanded: Boolean,
    onMainMenuExpandedChange: (Boolean) -> Unit,
    priorityMenuExpanded: Boolean,
    onPriorityMenuExpandedChange: (Boolean) -> Unit,
    colorMenuExpanded: Boolean,
    onColorMenuExpandedChange: (Boolean) -> Unit,
    categoryMenuExpanded: Boolean,
    onCategoryMenuExpandedChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTogglePinned: () -> Unit,
    onPriorityChange: (Int) -> Unit,
    onColorChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val overlayBackground = Color.Black.copy(alpha = 0.30f)
    val baseIconTint = if (overlayMode) Color.White else graphicColor
    val overlayButtonModifier = Modifier.size(30.dp).clip(CircleShape).background(overlayBackground)
    val buttonModifier = Modifier.size(40.dp)
    fun finishMainMenu(action: () -> Unit) { onMainMenuExpandedChange(false); action() }
    fun openSubmenu(setExpanded: (Boolean) -> Unit) {
        UiSoundPlayer.playAction(context, UiActionSound.Menu)
        onMainMenuExpandedChange(false)
        setExpanded(true)
    }

    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (overlayMode) 7.dp else 0.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(visible = note.isPinned,
            enter = fadeIn(animationSpec = tween(durationMillis = motionDuration)) + scaleIn(animationSpec = tween(
                                durationMillis = motionDuration), initialScale = 0.72f),
            exit = fadeOut(animationSpec = tween(durationMillis = motionDuration)) + scaleOut(animationSpec = tween(
                                durationMillis = motionDuration), targetScale = 0.72f)) {
            Box(modifier = if (overlayMode) overlayButtonModifier else buttonModifier, contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.PushPin,
                    contentDescription = null,
                    tint = baseIconTint,
                    modifier = Modifier.size(if (overlayMode) 17.dp else 18.dp))
            }
        }
        if (showFavorite) {
            NoteCardActionButton(overlayMode, overlayButtonModifier, buttonModifier, onToggleFavorite) {
                Crossfade(targetState = note.isFavorite, animationSpec = tween(durationMillis = motionDuration)) { favorite ->
                    Icon(imageVector = if (favorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = stringResource(R.string.mock_favorites), tint = if (favorite) favoriteIconColor else baseIconTint,
                        modifier = Modifier.size(if (overlayMode) 17.dp else 20.dp))
                }
            }
        }
        Box {
            NoteCardActionButton(overlayMode, overlayButtonModifier, buttonModifier,
                UiSoundPlayer.actionHandler(context, UiActionSound.Menu) { onMainMenuExpandedChange(true) }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, tint = baseIconTint,
                    modifier = Modifier.size(if (overlayMode) 17.dp else 20.dp))
            }
            AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                expanded = mainMenuExpanded,
                onDismissRequest = {
                    onMainMenuExpandedChange(false)
                },
                containerColor = popupColor) {
                visibleMainMenuItems.forEachIndexed { index, key ->
                    if (key == "delete" && index > 0) {
                        HorizontalDivider()
                    }
                    when (key) {
                        "edit" -> {
                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_edit), icon = Icons.Default.Edit,
                                showIcon = optionMenuShowIcons, textColor = menuTextColor) {
                                finishMainMenu(onEdit)
                            }
                        }
                        "favorite" -> {
                            ConfigurableDropdownMenuItem(label = if (note.isFavorite) {
                                        stringResource(R.string.mock_remove_favorite)
                                    } else {
                                        stringResource(R.string.mock_add_favorite)
                                    },
                                icon = if (note.isFavorite) {
                                        Icons.Default.Star
                                    } else {
                                        Icons.Default.StarBorder
                                    },
                                showIcon = optionMenuShowIcons,
                                textColor = menuTextColor) {
                                finishMainMenu(onToggleFavorite)
                            }
                        }
                        "pin" -> {
                            ConfigurableDropdownMenuItem(label = if (note.isPinned) {
                                        stringResource(R.string.mock_unpin)
                                    } else {
                                        stringResource(R.string.mock_pin)
                                    },
                                icon = Icons.Default.PushPin,
                                showIcon = optionMenuShowIcons,
                                textColor = menuTextColor) {
                                finishMainMenu(onTogglePinned)
                            }
                        }
                        "priority" -> {
                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_priority), icon = Icons.Default.PriorityHigh,
                                showIcon = optionMenuShowIcons, textColor = menuTextColor) {
                                openSubmenu(onPriorityMenuExpandedChange)
                            }
                        }
                        "color" -> {
                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_color), icon = Icons.Default.Palette,
                                showIcon = optionMenuShowIcons, textColor = menuTextColor) {
                                openSubmenu(onColorMenuExpandedChange)
                            }
                        }
                        "move" -> {
                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_move), icon = if (note.category == "work") {
                                        Icons.Default.Work
                                    } else {
                                        Icons.Default.Person
                                    },
                                showIcon = optionMenuShowIcons,
                                textColor = menuTextColor) {
                                openSubmenu(onCategoryMenuExpandedChange)
                            }
                        }
                        "delete" -> {
                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_delete), icon = Icons.Default.Delete,
                                showIcon = optionMenuShowIcons, textColor = Color(0xFFC62828)) {
                                finishMainMenu(onDelete)
                            }
                        }
                    }
                }
            }
            AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                expanded = priorityMenuExpanded,
                onDismissRequest = {
                    onPriorityMenuExpandedChange(false)
                },
                containerColor = popupColor) {
                visiblePriorityOptions.forEach { option ->
                    PriorityMenuItem(label = stringResource(option.labelRes), selected = note.priority == option.value,
                        textColor = menuTextColor, showIndicator = optionMenuShowIcons) {
                        onPriorityMenuExpandedChange(false)
                        onPriorityChange(option.value)
                    }
                }
            }
            AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                expanded = colorMenuExpanded,
                onDismissRequest = {
                    onColorMenuExpandedChange(false)
                },
                containerColor = popupColor) {
                visibleColorOptions.forEach { option ->
                    val optionColor = option.argb?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceContainerLow
                    ColorMenuItem(label = stringResource(option.labelRes), color = optionColor, textColor = menuTextColor,
                        showSwatch = optionMenuShowIcons) {
                        onColorMenuExpandedChange(false)
                        onColorChange(option.key)
                    }
                }
            }
            AppDropdownMenu(expanded = categoryMenuExpanded,
                onDismissRequest = {
                    onCategoryMenuExpandedChange(false)
                },
                containerColor = popupColor) {
                listOf("work" to (R.string.mock_work to Icons.Default.Work), "personal" to (R.string.mock_personal to Icons.Default.Person))
                    .forEach { (category, item) ->
                        ConfigurableDropdownMenuItem(stringResource(item.first), item.second, optionMenuShowIcons, menuTextColor) {
                            onCategoryMenuExpandedChange(false)
                            onCategoryChange(category)
                        }
                    }
            }
        }
    }
}
@Composable
fun CategoryPill(category: String, fontFamily: FontFamily = FontFamily.Default) {
    val work = category == "work"
    val background = if (work) {
            Color(0xFFDDEEFF)
        } else {
            Color(0xFFFFE3E3)
        }
    val foreground = if (work) {
            Color(0xFF27669D)
        } else {
            Color(0xFFA64848)
        }
    Surface(shape = RoundedCornerShape(8.dp),
        color = background) {
        Text(text = stringResource(if (work) {
                        R.string.mock_work
                    } else {
                        R.string.mock_personal
                    }),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = foreground,
            fontFamily = fontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun NoteCardAttachmentsPreview(attachments: List<Attachment>, previewHeight: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp, fontFamily: FontFamily, performanceMode: String, deferHeavyLoads: Boolean,
    isScrolling: Boolean) {
    val shape = RoundedCornerShape(cornerRadius)
    if (attachments.size == 1) {
        Box(modifier = Modifier.fillMaxWidth().height(previewHeight).clip(shape)) {
            NoteCardAttachmentTile(attachment = attachments.first(), modifier = Modifier.fillMaxSize(), compact = false,
                fontFamily = fontFamily, performanceMode = performanceMode, deferHeavyLoads = deferHeavyLoads, isScrolling = isScrolling)
        }
        return
    }
    val multiHeight = (previewHeight.value * 1.35f).dp
    Column(modifier = Modifier.fillMaxWidth().height(multiHeight).clip(shape), verticalArrangement = Arrangement.spacedBy(0.dp)) {
        val rows = attachments.take(4).chunked(2)
        rows.forEach { rowAttachments -> Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                if (rowAttachments.size == 1) {
                    /*
                     * Con tres adjuntos, el último ocupa toda la segunda
                     * fila. Antes quedaba medio mosaico vacío, algo muy
                     * visible en tablets y en tarjetas de una sola columna.
                     */
                    NoteCardAttachmentTile(attachment = rowAttachments.first(), modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        compact = true, fontFamily = fontFamily, performanceMode = performanceMode, deferHeavyLoads = deferHeavyLoads, isScrolling = isScrolling)
                } else {
                    rowAttachments.forEach { attachment -> NoteCardAttachmentTile(attachment = attachment, modifier = Modifier.weight(1f)
                                .fillMaxHeight(), compact = true, fontFamily = fontFamily, performanceMode = performanceMode,
                            deferHeavyLoads = deferHeavyLoads, isScrolling = isScrolling)
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCardAttachmentTile(attachment: Attachment, modifier: Modifier, compact: Boolean, fontFamily: FontFamily,
    performanceMode: String, deferHeavyLoads: Boolean, isScrolling: Boolean) {
    val context = LocalContext.current
    val uri = remember(attachment.uri) {
        Uri.parse(attachment.uri)
    }
    val extension = remember(attachment.name, attachment.uri) {
        attachment.name?.substringAfterLast('.')?.lowercase().orEmpty().ifBlank {
                uri.path?.substringAfterLast('.')?.lowercase().orEmpty()
            }
    }
    Box(modifier = modifier) {
        when {
            attachment.type == "image" -> {
                /*
                 * Maximum quality usa carga progresiva sin "pop-in": primero
                 * intentamos dibujar de forma síncrona cualquier preview que el
                 * precalentamiento ya dejó en RAM. Si estamos desplazándonos no
                 * sustituimos la imagen visible a mitad del gesto. Al quedar el
                 * grid quieto recuperamos/generamos la instantánea si hiciera
                 * falta y después la versión quality definitiva.
                 */
                val preview = rememberCardAttachmentPreview(attachment.uri, performanceMode, deferHeavyLoads, isScrolling,
                    initial = { AttachmentPreviewCache.peekImagePreview(uri = uri, performanceMode = performanceMode) },
                    loader = { mode -> AttachmentPreviewCache.loadImagePreview(context = context, uri = uri, performanceMode = mode) })
                if (preview != null) {
                    Image(bitmap = preview!!.asImageBitmap(), contentDescription = attachment.name, modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop)
                } else {
                    FileLikeFallbackTile(icon = Icons.Default.Description, label = stringResource(R.string.image), compact = compact,
                        fontFamily = fontFamily)
                }
            }
            attachment.type == "video" -> {
                val preview = rememberCardAttachmentPreview(attachment.uri, performanceMode, deferHeavyLoads, isScrolling,
                    initial = { AttachmentPreviewCache.peekVideoPreview(uri = uri, performanceMode = performanceMode) },
                    isMissing = { it?.bitmap == null }, acceptQuality = { it?.bitmap != null },
                    loader = { mode -> AttachmentPreviewCache.loadVideoPreview(context = context, uri = uri, performanceMode = mode) })
                val bitmap = preview?.bitmap
                if (bitmap != null) {
                    Image(bitmap = bitmap.asImageBitmap(), contentDescription = attachment.name, modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop)
                } else {
                    FileLikeFallbackTile(icon = Icons.Default.PlayArrow, label = stringResource(R.string.video), compact = compact,
                        fontFamily = fontFamily)
                }
                Surface(modifier = Modifier.align(Alignment.Center), shape = CircleShape, color = Color.Black.copy(alpha = 0.56f)) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White,
                        modifier = Modifier.padding(if (compact) 10.dp else 14.dp).size(if (compact) 22.dp else 28.dp))
                }
                val duration = preview?.durationMillis ?: 0L
                if (duration > 0L) {
                    SmallDurationBadge(duration = duration, modifier = Modifier.align(Alignment.BottomEnd))
                }
            }
            attachment.type == "voice" || attachment.type == "audio" -> {
                FileLikeFallbackTile(icon = if (attachment.type == "voice") Icons.Default.Mic else Icons.Default.MusicNote,
                    label = if (attachment.type == "voice") stringResource(R.string.voice_note) else stringResource(R.string.audio),
                    compact = compact, fontFamily = fontFamily)
            }
            extension == "pdf" -> {
                val preview = rememberCardAttachmentPreview(attachment.uri, performanceMode, deferHeavyLoads, isScrolling,
                    initial = { AttachmentPreviewCache.peekPdfPreview(uri = uri, performanceMode = performanceMode) },
                    loader = { mode -> AttachmentPreviewCache.loadPdfFirstPage(context = context, uri = uri, performanceMode = mode) })
                if (preview != null) {
                    Image(bitmap = preview!!.asImageBitmap(), contentDescription = attachment.name, modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop)
                } else {
                    FileLikeFallbackTile(icon = Icons.Default.Description, label = extension.uppercase(), compact = compact,
                        fontFamily = fontFamily)
                }
            }
            else -> {
                FileLikeFallbackTile(icon = Icons.Default.Description,
                    label = extension.ifBlank { stringResource(R.string.file) }.uppercase(), compact = compact, fontFamily = fontFamily)
            }
        }
    }
}

@Composable
private fun <T> rememberCardAttachmentPreview(
    uriKey: String,
    performanceMode: String,
    deferHeavyLoads: Boolean,
    isScrolling: Boolean,
    initial: () -> T?,
    isMissing: (T?) -> Boolean = { it == null },
    acceptQuality: (T?) -> Boolean = { it != null },
    loader: suspend (String) -> T?
): T? {
    var preview by remember(uriKey, performanceMode) { mutableStateOf(initial()) }
    LaunchedEffect(uriKey, performanceMode, deferHeavyLoads, isScrolling) {
        if (deferHeavyLoads) return@LaunchedEffect
        if (performanceMode == "quality") {
            if (isScrolling) return@LaunchedEffect
            if (isMissing(preview)) preview = AttachmentPreviewCache.withPreviewPermit { loader("instant") }
            val qualityPreview = AttachmentPreviewCache.withPreviewPermit { loader("quality") }
            if (acceptQuality(qualityPreview)) preview = qualityPreview
        } else if (isMissing(preview)) {
            preview = AttachmentPreviewCache.withPreviewPermit { loader(performanceMode) }
        }
    }
    return preview
}

@Composable
private fun FileLikeFallbackTile(icon: ImageVector, label: String, compact: Boolean, fontFamily: FontFamily) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainerLow),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(if (compact) 24.dp else 34.dp))
        Spacer(modifier = Modifier.height(if (compact) 6.dp else 8.dp))
        Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold,
            fontSize = if (compact) 10.sp else 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun SmallDurationBadge(duration: Long, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.padding(8.dp), color = Color.Black.copy(alpha = 0.68f), shape = RoundedCornerShape(8.dp)) {
        Text(text = formatMinuteSecondDuration(duration), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White,
            fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CompactNoteMenuItem(
    label: String, textColor: Color, fontFamily: FontFamily?, fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE, leadingIcon: (@Composable () -> Unit)? = null, onClick: () -> Unit
) {
    DropdownMenuItem(
        modifier = Modifier.defaultMinSize(minHeight = 38.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
        text = { Text(label, color = textColor, fontFamily = fontFamily, fontSize = 13.sp, fontWeight = fontWeight, maxLines = maxLines) },
        leadingIcon = leadingIcon,
        onClick = onClick
    )
}

@Composable
internal fun ConfigurableDropdownMenuItem(
    label: String, icon: ImageVector, showIcon: Boolean, textColor: Color,
    fontFamily: FontFamily? = null, onClick: () -> Unit
) = CompactNoteMenuItem(label, textColor, fontFamily, maxLines = 2,
    leadingIcon = if (showIcon) {{ Icon(icon, contentDescription = null, tint = textColor) }} else null, onClick = onClick)

@Composable
internal fun PriorityMenuItem(
    label: String, selected: Boolean, textColor: Color, showIndicator: Boolean,
    fontFamily: FontFamily? = null, onClick: () -> Unit
) = CompactNoteMenuItem(label, textColor, fontFamily, if (selected) FontWeight.Bold else FontWeight.Normal,
    leadingIcon = if (showIndicator) {{ if (selected) Box(Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary)) }} else null,
    onClick = onClick)

@Composable
internal fun ColorMenuItem(
    label: String, color: Color, textColor: Color, showSwatch: Boolean,
    fontFamily: FontFamily? = null, onClick: () -> Unit
) = CompactNoteMenuItem(label, textColor, fontFamily, maxLines = 2,
    leadingIcon = if (showSwatch) {{ Box(Modifier.size(20.dp).clip(CircleShape).background(color)) }} else null, onClick = onClick)
