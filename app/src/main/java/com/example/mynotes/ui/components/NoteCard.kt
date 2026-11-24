package com.example.mynotes.ui.components

import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.sound.UiSemanticSoundPolicy

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import com.example.mynotes.ui.notes.NoteCover
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkRemove
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.settings.MenuPreferencePolicy
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.rememberUiContentColors
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.noteBackgroundColor
import com.example.mynotes.ui.theme.noteUsesGlobalBackground
import com.example.mynotes.ui.theme.paletteMatchedOutlineColor
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.media.formatMinuteSecondDuration
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import kotlinx.coroutines.delay

private val FavoriteGold = Color(0xFFF5A623)

private enum class NoteCardMenu { MAIN, PRIORITY, COLOR, CATEGORY }

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
@OptIn(ExperimentalLayoutApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ModernNoteCard(note: Note, attachments: List<Attachment>, fontFamily: FontFamily, fontSize: Float,
    style: NoteCardStyle, textColorMode: String, optionMenuOrder: String, optionMenuHiddenItems: String, optionMenuShowIcons: Boolean, optionMenuTextColor: String,
    optionMenuOpacity: Float, priorityMenuHiddenItems: String, colorMenuHiddenItems: String, performanceMode: String,
    onOpen: () -> Unit, onEdit: () -> Unit, onToggleFavorite: () -> Unit, onTogglePinned: () -> Unit,
    onPriorityChange: (Int) -> Unit,
    onColorChange: (String) -> Unit, onCategoryChange: (String) -> Unit, onDelete: () -> Unit, isSelected: Boolean = false,
    onLongPress: () -> Unit = {}, selectionMode: Boolean = false, folderName: String? = null,
    isReadLater: Boolean = false, onToggleReadLater: () -> Unit = {},
    globalPaletteCardColor: Color? = null, globalPaletteCardOutlineColor: Color? = null,
    scrolling: State<Boolean>? = null, onMenuStateChange: (Boolean) -> Unit = {}) {
    // Sólo cambiar tarjetas que antes copiaban el fondo global de la pantalla.
    // Colores elegidos explícitamente por el usuario permanecen intactos.
    val isGlobalCard = noteUsesGlobalBackground(note.color)
    val cardColor = if (isGlobalCard && globalPaletteCardColor != null) globalPaletteCardColor else noteBackgroundColor(note.color)
    val (textColor, secondaryTextColor, graphicColor) = rememberUiContentColors(textColorMode, cardColor)
    val favoriteIconColor = remember(cardColor) { ensureUiContrast(FavoriteGold, cardColor, 3f) }
    val motionDuration = AppMotion.duration(AppMotion.FAST, style.animationsEnabled, style.animationSpeed)
    val animatedCardColor = animateColorAsState(
        targetValue = cardColor, animationSpec = tween(durationMillis = motionDuration), label = "noteCardColor"
    )
    val previewAttachments = remember(attachments) {
        attachments.take(4)
    }
    val previewCache = LocalNoteCardPreviewCache.current
    val preview = remember(previewCache, note.id, note.content, note.formattingEnabled) {
        previewCache?.previewFor(note) ?: NoteCardPreviewText.build(note.content, note.formattingEnabled)
    }
    val renderedCardColor = if (animatedCardColor.value != cardColor && scrolling?.value == true) {
        cardColor
    } else {
        animatedCardColor.value
    }
    val border = when {
        isSelected -> BorderStroke(2.dp, graphicColor)
        style.outlineEnabled -> BorderStroke(style.outlineWidth.dp, paletteMatchedOutlineColor(renderedCardColor))
        isGlobalCard && globalPaletteCardColor != null ->
            BorderStroke(1.dp, globalPaletteCardOutlineColor ?: paletteMatchedOutlineColor(renderedCardColor).copy(alpha = 0.22f))
        else -> null
    }
    val cardInteractionSource = remember { MutableInteractionSource() }

    Card(modifier = Modifier.fillMaxWidth().combinedClickable(interactionSource = cardInteractionSource,
                indication = null,
                onClick = { UiSoundPlayer.runWithFallbackClick(UiActionSound.Open, onOpen) },
                onLongClick = { UiSoundPlayer.runWithFallbackClick(actionBlock = onLongPress) },
                onLongClickLabel = stringResource(R.string.organize_select_note)),
        shape = RoundedCornerShape(style.cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = renderedCardColor),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = style.elevation.dp)) {
        val outlineContentInset = if (style.outlineEnabled) {
            (style.outlineWidth * 0.90f).coerceIn(0f, 5.4f)
        } else {
            0f
        }

        Column {
            val contentHorizontalPadding = (style.padding + outlineContentInset).dp
            val mediaHorizontalPadding = (outlineContentInset * 0.10f).coerceAtLeast(1f).dp
            val topContentPadding = (style.padding * 0.75f + outlineContentInset * 0.45f).dp
            val mediaTopPadding = (outlineContentInset * 0.10f).coerceAtLeast(1f).dp
            val bottomContentPadding = (style.padding * 0.90f + outlineContentInset).dp
            val hasMediaPreview = previewAttachments.isNotEmpty() || preview.link != null
            val mediaPreviewHeight = (style.imageHeight * 1.10f).dp
            val cardTitleFontSize = (fontSize + 1f).coerceAtLeast(12f)
            val cardBodyFontSize = (fontSize - 2f).coerceAtLeast(11f)
            val cardTitleLineHeight = (cardTitleFontSize * 1.22f).coerceAtLeast(cardTitleFontSize + 2f)
            val cardBodyLineHeight = (cardBodyFontSize * style.lineSpacing).coerceAtLeast(cardBodyFontSize + 1f)
            val actionRow: @Composable (Modifier, Boolean) -> Unit = { actionModifier, overlayMode ->
                if (selectionMode) Row(actionModifier.padding(8.dp), horizontalArrangement = Arrangement.End) {
                    Icon(if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, null, Modifier.size(24.dp))
                } else NoteCardActionRow(modifier = actionModifier, overlayMode = overlayMode, note = note, showFavorite = style.showFavorite,
                    graphicColor = graphicColor, favoriteIconColor = favoriteIconColor, cardColor = cardColor,
                    motionDuration = motionDuration, optionMenuShowIcons = optionMenuShowIcons, optionMenuOrder = optionMenuOrder,
                    optionMenuHiddenItems = optionMenuHiddenItems, priorityMenuHiddenItems = priorityMenuHiddenItems,
                    colorMenuHiddenItems = colorMenuHiddenItems,
                    menuTextColorMode = if (optionMenuTextColor == "note") textColorMode else optionMenuTextColor,
                    optionMenuOpacity = optionMenuOpacity, onMenuStateChange = onMenuStateChange,
                    onEdit = onEdit, onToggleFavorite = onToggleFavorite,
                    onTogglePinned = onTogglePinned, onPriorityChange = onPriorityChange, onColorChange = onColorChange,
                    onCategoryChange = onCategoryChange, onDelete = onDelete,
                    isReadLater = isReadLater, onToggleReadLater = onToggleReadLater)
            }

            if (note.coverUri != null || note.coverIcon != null) {
                Box(Modifier.fillMaxWidth()) {
                    NoteCover(note, Modifier.fillMaxWidth().height(mediaPreviewHeight), performanceMode, scrolling = scrolling)
                    actionRow(Modifier.align(Alignment.TopEnd).padding(6.dp), true)
                    if (attachments.isNotEmpty()) AttachmentMetaBadge(stringResource(R.string.organize_attachment_count, attachments.size), Modifier.align(Alignment.BottomEnd))
                }
            } else if (hasMediaPreview) {
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
                                performanceMode = performanceMode, scrolling = scrolling)
                        }
                        if (preview.link != null) {
                            Spacer(modifier = Modifier.height(if (previewAttachments.isNotEmpty()) 6.dp else 0.dp))
                            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(style.cornerRadius.dp))) {
                                NoteListLinkPreview(url = preview.link, modifier = Modifier.fillMaxWidth(), textColorMode = textColorMode,
                                    scrolling = scrolling)
                            }
                        }
                    }
                    if (attachments.size > previewAttachments.size) {
                        AttachmentMetaBadge(stringResource(R.string.design_more_attachments, attachments.size - previewAttachments.size),
                            Modifier.align(Alignment.BottomEnd))
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
                if (preview.text.isNotBlank()) {
                    Text(text = preview.text,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        color = secondaryTextColor,
                        fontFamily = fontFamily,
                        fontSize = cardBodyFontSize.sp,
                        lineHeight = cardBodyLineHeight.sp,
                        maxLines = style.contentMaxLines,
                        overflow = TextOverflow.Ellipsis)
                }
                if (preview.tasks.total > 0) {
                    Text(text = stringResource(R.string.v319_tasks_progress, preview.tasks.complete, preview.tasks.total),
                        color = secondaryTextColor, fontFamily = fontFamily,
                    style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 6.dp))
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(
                    start = contentHorizontalPadding,
                    end = contentHorizontalPadding,
                    top = 8.dp,
                    bottom = bottomContentPadding
                )) {
                FlowRow(modifier = Modifier.fillMaxWidth().padding(bottom = (outlineContentInset * 0.20f).dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (style.showCategory) {
                        if (folderName == null) CategoryPill(category = note.category)
                        else Text(folderName, style = MaterialTheme.typography.labelMedium, color = secondaryTextColor,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    if (style.showDate) {
                        val locale = LocalConfiguration.current.locales[0]
                        val formatter = LocalNoteCardDateFormatter.current
                        Text(text = remember(note.createdAt, formatter, locale) {
                                    formatNoteCardDate(note.createdAt, formatter, locale)
                                },
                            color = secondaryTextColor,
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis)
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
    val click = { UiSoundPlayer.runWithFallbackClick(actionBlock = onClick) }
    if (overlayMode) Box(overlayModifier.clickable(onClick = click), contentAlignment = Alignment.Center) { content() }
    else IconButton(onClick = click, modifier = buttonModifier) { content() }
}

@Composable
private fun NoteCardActionRow(
    modifier: Modifier = Modifier,
    overlayMode: Boolean,
    note: Note,
    showFavorite: Boolean,
    graphicColor: Color,
    favoriteIconColor: Color,
    cardColor: Color,
    motionDuration: Int,
    optionMenuShowIcons: Boolean,
    optionMenuOrder: String,
    optionMenuHiddenItems: String,
    priorityMenuHiddenItems: String,
    colorMenuHiddenItems: String,
    menuTextColorMode: String,
    optionMenuOpacity: Float,
    onMenuStateChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTogglePinned: () -> Unit,
    onPriorityChange: (Int) -> Unit,
    onColorChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDelete: () -> Unit,
    isReadLater: Boolean,
    onToggleReadLater: () -> Unit
) {
    val context = LocalContext.current
    // El oscurecimiento más fuerte mantiene los iconos claros sobre fotos muy luminosas.
    val overlayBackground = Color.Black.copy(alpha = 0.68f)
    val baseIconTint = if (overlayMode) Color.White else graphicColor
    val overlayButtonModifier = Modifier.size(30.dp).clip(CircleShape).background(overlayBackground)
    val buttonModifier = Modifier.size(40.dp)
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
            val layoutPreview = LocalLayoutPreview.current
            val previewMenu = layoutPreview?.takeIf { it.menuNoteId == note.id }?.noteMenu
            var menu by remember(note.id) {
                mutableStateOf(when (previewMenu) {
                    "main" -> NoteCardMenu.MAIN
                    "priority" -> NoteCardMenu.PRIORITY
                    "color" -> NoteCardMenu.COLOR
                    "category" -> NoteCardMenu.CATEGORY
                    else -> null
                })
            }
            var menuWasOpened by remember(note.id) { mutableStateOf(menu != null) }
            val reportMenuState by rememberUpdatedState(onMenuStateChange)
            val changeMenu: (NoteCardMenu?) -> Unit = { next ->
                menu = next
                if (next != null) {
                    menuWasOpened = true
                    reportMenuState(true)
                }
            }
            NoteCardActionButton(overlayMode, overlayButtonModifier, buttonModifier,
                UiSoundPlayer.actionHandler(context, UiActionSound.Menu) { changeMenu(NoteCardMenu.MAIN) }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, tint = baseIconTint,
                    modifier = Modifier.size(if (overlayMode) 17.dp else 20.dp))
            }
            if (menuWasOpened) {
                DisposableEffect(note.id) {
                    onDispose { reportMenuState(false) }
                }
                LaunchedEffect(menu) {
                    if (menu == null) {
                        // Conserva la salida del popup antes de liberar su composición.
                        delay(180L)
                        menuWasOpened = false
                    }
                }
                NoteCardMenus(
                    note, menu, changeMenu, cardColor, menuTextColorMode, optionMenuOpacity,
                    optionMenuShowIcons, optionMenuOrder, optionMenuHiddenItems, priorityMenuHiddenItems, colorMenuHiddenItems,
                    onEdit, onToggleFavorite, onTogglePinned, onPriorityChange, onColorChange, onCategoryChange, onDelete,
                    isReadLater, onToggleReadLater
                )
            }
        }
    }
}

@Composable
private fun NoteCardMenus(
    note: Note,
    menu: NoteCardMenu?,
    onMenuChange: (NoteCardMenu?) -> Unit,
    cardColor: Color,
    textColorMode: String,
    opacity: Float,
    optionMenuShowIcons: Boolean,
    optionMenuOrder: String,
    hiddenMain: String,
    hiddenPriority: String,
    hiddenColors: String,
    onEdit: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTogglePinned: () -> Unit,
    onPriorityChange: (Int) -> Unit,
    onColorChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDelete: () -> Unit,
    isReadLater: Boolean,
    onToggleReadLater: () -> Unit
) {
    val context = LocalContext.current
    val surface = MaterialTheme.colorScheme.surfaceContainerHigh
    val inverseSurface = MaterialTheme.colorScheme.inverseSurface
    val popupColor = remember(textColorMode, surface, inverseSurface, opacity) {
        val base = when (textColorMode) {
            "white" -> if (surface.luminance() < 0.46f) surface else inverseSurface
            "black" -> if (surface.luminance() > 0.54f) surface else inverseSurface
            else -> surface
        }
        base.copy(alpha = (opacity / 100f).coerceIn(0.35f, 1f))
    }
    val menuTextColor = remember(popupColor, cardColor, textColorMode) {
        val background = compositeUiColor(foreground = popupColor, background = cardColor)
        ensureUiContrast(resolveUiTextColor(value = textColorMode, background = background), background, 4.5f)
    }
    val visibleMainMenuItems = remember(optionMenuOrder, hiddenMain) {
        val hidden = MenuPreferencePolicy.hiddenKeys(hiddenMain, MenuPreferencePolicy.mainKeySet)
        MenuPreferencePolicy.orderedKeys(optionMenuOrder).filterNot { it in hidden }.ifEmpty { listOf("edit") }
    }
    val visiblePriorityOptions = remember(hiddenPriority) {
        val hidden = MenuPreferencePolicy.hiddenKeys(hiddenPriority, MenuPreferencePolicy.priorityKeys)
        MenuPreferencePolicy.priorityOptions.filterNot { it.key in hidden }.ifEmpty { MenuPreferencePolicy.priorityOptions }
    }
    fun finishMainMenu(action: () -> Unit) {
        onMenuChange(null)
        action()
    }
    fun openSubmenu(next: NoteCardMenu) {
        UiSoundPlayer.playAction(context, UiActionSound.Menu)
        onMenuChange(next)
    }

    AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
        expanded = menu == NoteCardMenu.MAIN,
        onDismissRequest = {
            if (menu == NoteCardMenu.MAIN) onMenuChange(null)
        },
        containerColor = popupColor) {
        ConfigurableDropdownMenuItem(
            label = stringResource(if (isReadLater) R.string.v319_remove_read_later else R.string.v319_read_later),
            icon = if (isReadLater) Icons.Default.BookmarkRemove else Icons.Default.BookmarkAdd,
            showIcon = optionMenuShowIcons, textColor = menuTextColor) {
            finishMainMenu(onToggleReadLater)
        }
        HorizontalDivider()
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
                        openSubmenu(NoteCardMenu.PRIORITY)
                    }
                }
                "color" -> {
                    ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_color), icon = Icons.Default.Palette,
                        showIcon = optionMenuShowIcons, textColor = menuTextColor) {
                        openSubmenu(NoteCardMenu.COLOR)
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
                        openSubmenu(NoteCardMenu.CATEGORY)
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
        expanded = menu == NoteCardMenu.PRIORITY,
        onDismissRequest = {
            if (menu == NoteCardMenu.PRIORITY) onMenuChange(null)
        },
        containerColor = popupColor) {
        visiblePriorityOptions.forEach { option ->
            PriorityMenuItem(label = stringResource(option.labelRes), selected = note.priority == option.value,
                textColor = menuTextColor, showIndicator = optionMenuShowIcons) {
                onMenuChange(null)
                onPriorityChange(option.value)
            }
        }
    }
    if (menu == NoteCardMenu.COLOR) {
        val hiddenColorItems = remember(hiddenColors) {
            MenuPreferencePolicy.hiddenKeys(hiddenColors, MenuPreferencePolicy.colorKeys)
        }
        NotePaletteDialog(
            selectedColor = note.color,
            hiddenColorItems = hiddenColorItems,
            containerColor = popupColor,
            contentColor = menuTextColor,
            onDismiss = { onMenuChange(null) },
            onColorSelected = { color ->
                // Keep palette open so several tones can be previewed/applied.
                onColorChange(color)
            }
        )
    }
    AppDropdownMenu(expanded = menu == NoteCardMenu.CATEGORY,
        onDismissRequest = {
            if (menu == NoteCardMenu.CATEGORY) onMenuChange(null)
        },
        containerColor = popupColor) {
        listOf("work" to (R.string.mock_work to Icons.Default.Work), "personal" to (R.string.mock_personal to Icons.Default.Person))
            .forEach { (category, item) ->
                ConfigurableDropdownMenuItem(stringResource(item.first), item.second, optionMenuShowIcons, menuTextColor) {
                    onMenuChange(null)
                    onCategoryChange(category)
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
private fun NoteCardAttachmentsPreview(
    attachments: List<Attachment>, previewHeight: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp, fontFamily: FontFamily, performanceMode: String, scrolling: State<Boolean>?
) {
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    if (attachments.size == 1) {
        Box(Modifier.fillMaxWidth().height(previewHeight).clip(shape)) {
            NoteCardAttachmentTile(attachments.first(), Modifier.fillMaxSize(), compact = false,
                fontFamily = fontFamily, performanceMode = performanceMode, scrolling = scrolling)
        }
        return
    }
    val rows = remember(attachments) { attachments.chunked(2) }
    Column(Modifier.fillMaxWidth().height(previewHeight * 1.35f).clip(shape)) {
        rows.forEach { rowAttachments ->
            Row(Modifier.weight(1f)) {
                rowAttachments.forEach { attachment ->
                    key(attachment.id, attachment.uri) {
                        // Con tres adjuntos, el último ocupa el ancho de la segunda fila.
                        NoteCardAttachmentTile(attachment, Modifier.weight(1f).fillMaxHeight(), compact = true,
                            fontFamily = fontFamily, performanceMode = performanceMode, scrolling = scrolling)
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCardAttachmentTile(attachment: Attachment, modifier: Modifier, compact: Boolean, fontFamily: FontFamily,
    performanceMode: String, scrolling: State<Boolean>?) {
    val context = LocalContext.current.applicationContext
    val layoutPreview = LocalLayoutPreview.current
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
                 * Reutiliza la RAM y los JPEG pequeños de disco durante el scroll.
                 * La decodificación del original y la mejora a la calidad elegida
                 * esperan hasta que la lista quede quieta.
                 */
                val preview = rememberCardAttachmentPreview(attachment.uri, performanceMode, scrolling,
                    initial = { layoutPreview?.mediaBitmap ?: AttachmentPreviewCache.peekImagePreview(uri = uri, performanceMode = performanceMode) },
                    cachedLoader = { AttachmentPreviewCache.loadCachedPreview(context, uri, "image", performanceMode)?.bitmap },
                    isTargetPreview = { AttachmentPreviewCache.isExactBitmapPreview("image", uri, performanceMode, it) },
                    loader = { mode -> AttachmentPreviewCache.loadImagePreview(context = context, uri = uri, performanceMode = mode) })
                if (preview != null) {
                    Image(bitmap = remember(preview) { preview.asImageBitmap() }, contentDescription = attachment.name, modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop)
                } else {
                    FileLikeFallbackTile(icon = Icons.Default.Description, label = stringResource(R.string.image), compact = compact,
                        fontFamily = fontFamily)
                }
            }
            attachment.type == "video" -> {
                val preview = rememberCardAttachmentPreview(attachment.uri, performanceMode, scrolling,
                    initial = { layoutPreview?.let { sample -> sample.mediaBitmap?.let { bitmap ->
                        AttachmentPreviewCache.MediaPreview(bitmap, sample.durationMillis.toLong(), bitmap.width, bitmap.height)
                    } }
                        ?: AttachmentPreviewCache.peekVideoPreview(uri = uri, performanceMode = performanceMode) },
                    accept = { it?.bitmap != null },
                    cachedLoader = { AttachmentPreviewCache.loadCachedPreview(context, uri, "video", performanceMode) },
                    isTargetPreview = { it != null && it.width > 0 && it.height > 0 &&
                        AttachmentPreviewCache.isExactBitmapPreview("video", uri, performanceMode, it.bitmap) },
                    loader = { mode -> AttachmentPreviewCache.loadVideoPreview(context = context, uri = uri, performanceMode = mode) })
                val bitmap = preview?.bitmap
                if (bitmap != null) {
                    Image(bitmap = remember(bitmap) { bitmap.asImageBitmap() }, contentDescription = attachment.name, modifier = Modifier.fillMaxSize(),
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
                val preview = rememberCardAttachmentPreview(attachment.uri, performanceMode, scrolling,
                    initial = { layoutPreview?.let { AttachmentPreviewCache.MediaPreview(durationMillis = it.durationMillis.toLong()) } },
                    progressive = false,
                    loader = { mode -> AttachmentPreviewCache.loadAudioPreview(context, uri, loadAlbumArt = false, performanceMode = mode) })
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceContainerLow) {
                    Column(Modifier.padding(if (compact) 8.dp else 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(if (attachment.type == "voice") Icons.Default.Mic else Icons.Default.MusicNote, null,
                            Modifier.size(if (compact) 20.dp else 28.dp))
                        AudioWaveformGlyph(Modifier.fillMaxWidth().padding(vertical = 8.dp).height(if (compact) 20.dp else 28.dp))
                        Text(if ((preview?.durationMillis ?: 0L) > 0L) formatMinuteSecondDuration(preview!!.durationMillis)
                            else stringResource(if (attachment.type == "voice") R.string.voice_note else R.string.audio),
                            fontFamily = fontFamily, style = MaterialTheme.typography.labelMedium, maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            extension == "pdf" -> {
                val preview = rememberCardAttachmentPreview(attachment.uri, performanceMode, scrolling,
                    initial = { layoutPreview?.documentBitmap ?: AttachmentPreviewCache.peekPdfPreview(uri = uri, performanceMode = performanceMode) },
                    cachedLoader = { AttachmentPreviewCache.loadCachedPreview(context, uri, "pdf", performanceMode)?.bitmap },
                    isTargetPreview = { AttachmentPreviewCache.isExactBitmapPreview("pdf", uri, performanceMode, it) },
                    loader = { mode -> AttachmentPreviewCache.loadPdfFirstPage(context = context, uri = uri, performanceMode = mode) })
                if (preview != null) {
                    Image(bitmap = remember(preview) { preview.asImageBitmap() }, contentDescription = attachment.name, modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop)
                } else {
                    FileLikeFallbackTile(icon = Icons.Default.Description, label = extension.uppercase(), compact = compact,
                        fontFamily = fontFamily)
                }
                val pages = rememberDocumentPageCount(attachment, scrolling = scrolling)
                AttachmentMetaBadge(if (pages == null) "PDF" else stringResource(R.string.design_pdf_pages, pages),
                    Modifier.align(Alignment.BottomStart))
            }
            else -> {
                FileLikeFallbackTile(icon = Icons.Default.Description,
                    label = extension.ifBlank { stringResource(R.string.file) }.uppercase(), compact = compact, fontFamily = fontFamily)
            }
        }
    }
}

@Composable
internal fun <T> rememberCardAttachmentPreview(
    uriKey: String,
    performanceMode: String,
    scrolling: State<Boolean>?,
    initial: () -> T?,
    progressive: Boolean = true,
    accept: (T?) -> Boolean = { it != null },
    cachedLoader: (suspend () -> T?)? = null,
    isTargetPreview: ((T?) -> Boolean)? = null,
    loader: suspend (String) -> T?
): T? {
    val layoutPreview = LocalLayoutPreview.current
    var preview by remember(uriKey, performanceMode) { mutableStateOf(initial()) }
    // Una vista previa provisional en RAM puede pertenecer a otra resolución.
    var loaded by remember(uriKey, performanceMode) {
        mutableStateOf(accept(preview) && (isTargetPreview?.invoke(preview) ?: (performanceMode != "quality")))
    }
    LaunchedEffect(uriKey, performanceMode) {
        if (layoutPreview != null || accept(preview) || cachedLoader == null) return@LaunchedEffect
        // Cached JPEGs are small, bounded reads; original media stays deferred until idle.
        val cached = AttachmentPreviewCache.withPreviewPermit { cachedLoader() }
        if (!accept(preview) && accept(cached)) preview = cached
    }
    val canLoad = layoutPreview == null && !loaded && scrolling?.value != true
    LaunchedEffect(uriKey, performanceMode, canLoad) {
        if (!canLoad) return@LaunchedEffect
        delay(130L)
        if (performanceMode == "quality" && progressive && !accept(preview)) {
            AttachmentPreviewCache.withPreviewPermit {
                if (scrolling?.value == true) null else loader("instant")
            }?.let { preview = it }
        }
        val result = AttachmentPreviewCache.withPreviewPermit {
            if (scrolling?.value == true) null
            else if (performanceMode == "quality") loader("quality")
            else loader(performanceMode)
        }
        if (accept(result)) {
            preview = result
            loaded = true
        } else if (result != null && (performanceMode != "quality" || preview == null)) {
            // Algunos vídeos permiten leer la duración aunque no entreguen una miniatura.
            preview = result
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
        onClick = { UiSoundPlayer.runWithFallbackClick(UiSemanticSoundPolicy.forLabel(label), onClick) }
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

