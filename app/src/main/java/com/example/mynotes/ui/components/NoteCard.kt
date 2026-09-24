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
import androidx.compose.ui.window.PopupProperties
import com.example.mynotes.R
import com.example.mynotes.ui.components.AppDropdownMenu
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.ui.theme.automaticUiTextColor
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.theme.compositeUiColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.noteBackgroundColor
import com.example.mynotes.ui.theme.paletteMatchedOutlineColor
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import java.text.DateFormat
import java.util.Date

private val FavoriteGold = Color(0xFFF5A623)

private val MainOptionMenuKeys = listOf("edit", "favorite", "pin", "priority", "color", "move", "delete")

private val PriorityOptionKeys = listOf("none", "low", "medium", "high")

private val ColorOptionKeys = listOf("default", "yellow", "orange", "red", "pink", "purple", "blue", "cyan", "teal", "green", "mint",
        "lime", "brown", "gray")
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
    val context = LocalContext.current
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
    var categoryMenuExpanded by
        remember {
            mutableStateOf(false)
        }
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
    /*
     * The outline now stays tied to the palette of the note itself.
     * Light cards get a deeper version of the same hue; dark cards get a
     * lighter version. Only extreme cases fall back to black/white.
     */
    val noteOutlineColor = paletteMatchedOutlineColor(animatedCardColor)
    val menuTextColor = resolveUiTextColor(value = effectiveMenuTextColorMode, background = popupVisualBackground)
    val mainMenuOrder = remember(optionMenuOrder) {
            normalizedMenuOrder(optionMenuOrder, MainOptionMenuKeys)
        }
    val hiddenMainMenuItems = remember(optionMenuHiddenItems) {
            parseMenuKeys(optionMenuHiddenItems, MainOptionMenuKeys)
        }
    val visibleMainMenuItems = remember(mainMenuOrder, hiddenMainMenuItems) {
            mainMenuOrder.filterNot {
                    it in
                        hiddenMainMenuItems
                }.ifEmpty {
                    listOf("edit")
                }
        }
    val hiddenPriorityItems = remember(priorityMenuHiddenItems) {
            parseMenuKeys(priorityMenuHiddenItems, PriorityOptionKeys)
        }
    val visiblePriorityItems = remember(hiddenPriorityItems) {
            PriorityOptionKeys.filterNot {
                    it in
                        hiddenPriorityItems
                }.ifEmpty {
                    PriorityOptionKeys
                }
        }
    val hiddenColorItems = remember(colorMenuHiddenItems) {
            parseMenuKeys(colorMenuHiddenItems, ColorOptionKeys)
        }
    val visibleColorItems = remember(hiddenColorItems) {
            ColorOptionKeys.filterNot {
                    it in
                        hiddenColorItems
                }.ifEmpty {
                    ColorOptionKeys
                }
        }
    val cardInteractionSource = remember {
            MutableInteractionSource()
        }
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
        /*
         * Un borde grueso se dibuja hacia el interior de Card. Sin compensación,
         * el contenido de la nota puede quedar visualmente pegado al contorno,
         * sobre todo en el lateral derecho y en la fila inferior.
         *
         * El inset solo afecta al contenido textual/controles de la tarjeta.
         * La composición de adjuntos y miniaturas permanece intacta.
         */
        val outlineContentInset = if (style.outlineEnabled) {
            (style.outlineWidth * 0.90f).coerceIn(0f, 5.4f)
        } else {
            0f
        }
        Column(modifier = cardContentModifier) {
            if (previewAttachments.isNotEmpty()) {
                /*
                 * En Performance/Balanced seguimos aplazando decodificaciones
                 * nuevas durante el gesto de scroll. En Maximum quality no se
                 * bloquea la lectura de la miniatura: el listado completo ya
                 * se está precalentando en NotesScreen y, cuando una tarjeta
                 * entra al viewport, lo normal es recuperar la variante quality
                 * desde RAM o cacheDir. Esto evita placeholders hasta que el
                 * usuario suelte el dedo, que era el comportamiento anterior.
                 * La suspensión de animateContentSize durante el scroll se
                 * conserva porque es independiente de la carga de thumbnails.
                 */
                NoteCardAttachmentsPreview(attachments = previewAttachments,
                    previewHeight = style.imageHeight.dp,
                    cornerRadius = style.cornerRadius.dp,
                    fontFamily = fontFamily,
                    performanceMode = performanceMode,
                    deferHeavyLoads = isScrolling && performanceMode != "quality",
                    isScrolling = isScrolling)
            }
            Column(modifier = Modifier.fillMaxWidth().padding(
                    start = (style.padding + outlineContentInset).dp,
                    end = (style.padding + outlineContentInset).dp,
                    top = (style.padding * 0.75f + outlineContentInset * 0.45f).dp,
                    bottom = (style.padding * 0.90f + outlineContentInset).dp
                )) {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top) {
                    Spacer(modifier = Modifier.weight(1f))
                    AnimatedVisibility(visible = note.isPinned,
                        enter = fadeIn(animationSpec = tween(durationMillis = motionDuration)) + scaleIn(animationSpec = tween(
                                            durationMillis = motionDuration), initialScale = 0.72f),
                        exit = fadeOut(animationSpec = tween(durationMillis = motionDuration)) + scaleOut(animationSpec = tween(
                                            durationMillis = motionDuration), targetScale = 0.72f)) {
                        // El pin usa el mismo espacio de 40 dp que Favoritos y
                        // Más opciones. Así los tres iconos quedan en una sola
                        // línea y el pin no parece flotando hacia el centro.
                        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = graphicColor,
                                modifier = Modifier.size(18.dp))
                        }
                    }
                    if (style.showFavorite) {
                    IconButton(onClick = {
                            onToggleFavorite()
                        },
                        modifier = Modifier.size(40.dp)) {
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
                                        graphicColor
                                    },
                                modifier = Modifier.size(20.dp))
                        }
                    }
                    }
                    Box {
                        IconButton(onClick = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                mainMenuExpanded = true
                            },
                            modifier = Modifier.size(40.dp)) {
                            Icon(imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier.size(20.dp))
                        }
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = mainMenuExpanded,
                            onDismissRequest = {
                                mainMenuExpanded = false
                            },
                            containerColor = popupColor,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            visibleMainMenuItems.forEachIndexed {
                                        index, key ->
                                    if (key == "delete" && index >
                                            0) {
                                        HorizontalDivider()
                                    }
                                    when (key) {
                                        "edit" -> {
                                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_edit), icon =
                                                    Icons.Default.Edit, showIcon = optionMenuShowIcons, textColor = menuTextColor,
                                                onClick = {
                                                    mainMenuExpanded = false
                                                    onEdit()
                                                })
                                        }
                                        "favorite" -> {
                                            ConfigurableDropdownMenuItem(label = if (note.isFavorite) {
                                                        stringResource(R.string.mock_remove_favorite)
                                                    } else {
                                                        stringResource(R.string.mock_add_favorite)
                                                    }, icon = if (note.isFavorite) {
                                                        Icons.Default.Star
                                                    } else {
                                                        Icons.Default.StarBorder
                                                    }, showIcon = optionMenuShowIcons, textColor = menuTextColor, onClick = {
                                                    mainMenuExpanded = false
                                                    onToggleFavorite()
                                                })
                                        }
                                        "pin" -> {
                                            ConfigurableDropdownMenuItem(label = if (note.isPinned) {
                                                        stringResource(R.string.mock_unpin)
                                                    } else {
                                                        stringResource(R.string.mock_pin)
                                                    }, icon = Icons.Default.PushPin, showIcon = optionMenuShowIcons, textColor =
                                                    menuTextColor, onClick = {
                                                    mainMenuExpanded = false
                                                    onTogglePinned()
                                                })
                                        }
                                        "priority" -> {
                                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_priority), icon =
                                                    Icons.Default.PriorityHigh, showIcon = optionMenuShowIcons, textColor = menuTextColor,
                                                onClick = {
                                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                                    mainMenuExpanded = false
                                                    priorityMenuExpanded = true
                                                })
                                        }
                                        "color" -> {
                                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_color), icon =
                                                    Icons.Default.Palette, showIcon = optionMenuShowIcons, textColor = menuTextColor,
                                                onClick = {
                                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                                    mainMenuExpanded = false
                                                    colorMenuExpanded = true
                                                })
                                        }
                                        "move" -> {
                                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_move), icon = if (
                                                        note.category == "work") {
                                                        Icons.Default.Work
                                                    } else {
                                                        Icons.Default.Person
                                                    }, showIcon = optionMenuShowIcons, textColor = menuTextColor, onClick = {
                                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                                    mainMenuExpanded = false
                                                    categoryMenuExpanded = true
                                                })
                                        }
                                        "delete" -> {
                                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_delete), icon =
                                                    Icons.Default.Delete, showIcon = optionMenuShowIcons, textColor = Color(0xFFC62828),
                                                onClick = {
                                                    mainMenuExpanded = false
                                                    onDelete()
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
                            containerColor = popupColor,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            visiblePriorityItems.forEach {
                                        key ->
                                    val priority = when (key) {
                                            "low" -> 1
                                            "medium" -> 2
                                            "high" -> 3
                                            else -> 0
                                        }
                                    val label = stringResource(when (key) {
                                                "low" -> R.string.mock_priority_low
                                                "medium" -> R.string.mock_priority_medium
                                                "high" -> R.string.mock_priority_high
                                                else -> R.string.mock_priority_none
                                            })
                                    PriorityMenuItem(label = label, selected = note.priority == priority, textColor = menuTextColor,
                                        showIndicator = optionMenuShowIcons) {
                                        priorityMenuExpanded = false
                                        onPriorityChange(priority)
                                    }
                                }
                        }
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = colorMenuExpanded,
                            onDismissRequest = {
                                colorMenuExpanded = false
                            },
                            containerColor = popupColor,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            visibleColorItems.forEach {
                                        key ->
                                    val label = stringResource(when (key) {
                                                "yellow" -> R.string.mock_color_yellow
                                                "orange" -> R.string.mock_color_orange
                                                "red" -> R.string.mock_color_red
                                                "pink" -> R.string.mock_color_pink
                                                "purple" -> R.string.mock_color_purple
                                                "blue" -> R.string.mock_color_blue
                                                "cyan" -> R.string.mock_color_cyan
                                                "teal" -> R.string.mock_color_teal
                                                "green" -> R.string.mock_color_green
                                                "mint" -> R.string.mock_color_mint
                                                "lime" -> R.string.mock_color_lime
                                                "brown" -> R.string.mock_color_brown
                                                "gray" -> R.string.mock_color_gray
                                                else -> R.string.mock_color_default
                                            })
                                    val optionColor = when (key) {
                                            "yellow" -> Color(0xFFFFF4C7)
                                            "orange" -> Color(0xFFFFE7D1)
                                            "red" -> Color(0xFFFFE0E0)
                                            "pink" -> Color(0xFFFFE5EC)
                                            "purple" -> Color(0xFFF0E7FA)
                                            "blue" -> Color(0xFFE5F1FB)
                                            "cyan" -> Color(0xFFE0F7FA)
                                            "teal" -> Color(0xFFDDF4F0)
                                            "green" -> Color(0xFFE4F2E8)
                                            "mint" -> Color(0xFFDFF7EA)
                                            "lime" -> Color(0xFFF1F8D7)
                                            "brown" -> Color(0xFFEDE2D9)
                                            "gray" -> Color(0xFFE9ECEF)
                                            else -> MaterialTheme.colorScheme.surfaceContainerLow
                                        }
                                    ColorMenuItem(label = label, color = optionColor, textColor = menuTextColor, showSwatch =
                                            optionMenuShowIcons) {
                                        colorMenuExpanded = false
                                        onColorChange(key)
                                    }
                                }
                        }
                        AppDropdownMenu(expanded = categoryMenuExpanded,
                            onDismissRequest = {
                                categoryMenuExpanded = false
                            },
                            containerColor = popupColor,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_work), icon = Icons.Default.Work, showIcon =
                                    optionMenuShowIcons, textColor = menuTextColor, onClick = {
                                    categoryMenuExpanded = false
                                    onCategoryChange("work")
                                })
                            ConfigurableDropdownMenuItem(label = stringResource(R.string.mock_personal), icon = Icons.Default.Person,
                                showIcon = optionMenuShowIcons, textColor = menuTextColor, onClick = {
                                    categoryMenuExpanded = false
                                    onCategoryChange("personal")
                                })
                        }
                    }
                }
                /*
                 * Tipografía de la tarjeta:
                 * - el título conserva el tamaño configurado, pero recibe un
                 *   line-height explícito para que las líneas no se amontonen;
                 * - el contenido usa su propio tamaño como base del line-height.
                 *   Antes se calculaba con fontSize (más grande), dejando el
                 *   cuerpo demasiado alto y provocando cortes prematuros en
                 *   tarjetas estrechas de dos/tres columnas;
                 * - el texto aprovecha todo el ancho útil del bloque. El margen
                 *   lateral ya lo proporciona el padding exterior de la tarjeta.
                 */
                val cardTitleFontSize = (fontSize + 1f).coerceAtLeast(12f)
                val cardBodyFontSize = (fontSize - 2f).coerceAtLeast(11f)
                val cardTitleLineHeight = (cardTitleFontSize * 1.22f).coerceAtLeast(cardTitleFontSize + 2f)
                val cardBodyLineHeight = (cardBodyFontSize * style.lineSpacing)
                    .coerceAtLeast(cardBodyFontSize + 1f)

                Text(text = note.title.ifBlank {
                                stringResource(R.string.mock_untitled)
                            },
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    color = textColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = cardTitleFontSize.sp,
                    lineHeight = cardTitleLineHeight.sp,
                    maxLines = style.titleMaxLines,
                    overflow = TextOverflow.Ellipsis)
                if (displayContent.isNotBlank()) {
                    Text(text = displayContent,
                        modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                        color = secondaryTextColor,
                        fontFamily = fontFamily,
                        fontSize = cardBodyFontSize.sp,
                        lineHeight = cardBodyLineHeight.sp,
                        maxLines = style.contentMaxLines,
                        overflow = TextOverflow.Ellipsis)
                }
                if (noteLinks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinkPreviewCard(url = noteLinks.first(), compact = true, textColorMode = textColorMode,
                        deferLoad = isScrolling)
                }
                Spacer(modifier = Modifier.height(10.dp))
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
    val shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
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
                var preview by remember(attachment.uri, performanceMode) {
                    mutableStateOf(AttachmentPreviewCache.peekImagePreview(uri = uri, performanceMode = performanceMode))
                }
                LaunchedEffect(attachment.uri, performanceMode, deferHeavyLoads, isScrolling) {
                    if (!deferHeavyLoads) {
                        if (performanceMode == "quality") {
                            if (!isScrolling) {
                                if (preview == null) {
                                    preview = AttachmentPreviewCache.withPreviewPermit {
                                        AttachmentPreviewCache.loadImagePreview(context = context, uri = uri, performanceMode = "instant")
                                    }
                                }
                                val qualityPreview = AttachmentPreviewCache.withPreviewPermit {
                                    AttachmentPreviewCache.loadImagePreview(context = context, uri = uri, performanceMode = "quality")
                                }
                                if (qualityPreview != null) preview = qualityPreview
                            }
                        } else if (preview == null) {
                            preview = AttachmentPreviewCache.withPreviewPermit {
                                AttachmentPreviewCache.loadImagePreview(context = context, uri = uri, performanceMode = performanceMode)
                            }
                        }
                    }
                }
                if (preview != null) {
                    Image(bitmap = preview!!.asImageBitmap(), contentDescription = attachment.name, modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop)
                } else {
                    FileLikeFallbackTile(icon = Icons.Default.Description, label = stringResource(R.string.image), compact = compact,
                        fontFamily = fontFamily)
                }
            }
            attachment.type == "video" -> {
                var preview by remember(attachment.uri, performanceMode) {
                    mutableStateOf(AttachmentPreviewCache.peekVideoPreview(uri = uri, performanceMode = performanceMode))
                }
                LaunchedEffect(attachment.uri, performanceMode, deferHeavyLoads, isScrolling) {
                    if (!deferHeavyLoads) {
                        if (performanceMode == "quality") {
                            if (!isScrolling) {
                                if (preview?.bitmap == null) {
                                    preview = AttachmentPreviewCache.withPreviewPermit {
                                        AttachmentPreviewCache.loadVideoPreview(context = context, uri = uri, performanceMode = "instant")
                                    }
                                }
                                val qualityPreview = AttachmentPreviewCache.withPreviewPermit {
                                    AttachmentPreviewCache.loadVideoPreview(context = context, uri = uri, performanceMode = "quality")
                                }
                                if (qualityPreview.bitmap != null) preview = qualityPreview
                            }
                        } else if (preview?.bitmap == null) {
                            preview = AttachmentPreviewCache.withPreviewPermit {
                                AttachmentPreviewCache.loadVideoPreview(context = context, uri = uri, performanceMode = performanceMode)
                            }
                        }
                    }
                }
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
                var preview by remember(attachment.uri, performanceMode) {
                    mutableStateOf(AttachmentPreviewCache.peekPdfPreview(uri = uri, performanceMode = performanceMode))
                }
                LaunchedEffect(attachment.uri, performanceMode, deferHeavyLoads, isScrolling) {
                    if (!deferHeavyLoads) {
                        if (performanceMode == "quality") {
                            if (!isScrolling) {
                                if (preview == null) {
                                    preview = AttachmentPreviewCache.withPreviewPermit {
                                        AttachmentPreviewCache.loadPdfFirstPage(context = context, uri = uri, performanceMode = "instant")
                                    }
                                }
                                val qualityPreview = AttachmentPreviewCache.withPreviewPermit {
                                    AttachmentPreviewCache.loadPdfFirstPage(context = context, uri = uri, performanceMode = "quality")
                                }
                                if (qualityPreview != null) preview = qualityPreview
                            }
                        } else if (preview == null) {
                            preview = AttachmentPreviewCache.withPreviewPermit {
                                AttachmentPreviewCache.loadPdfFirstPage(context = context, uri = uri, performanceMode = performanceMode)
                            }
                        }
                    }
                }
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
        Text(text = formatSmallDuration(duration), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White,
            fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

private fun formatSmallDuration(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun ConfigurableDropdownMenuItem(label: String, icon: ImageVector, showIcon: Boolean, textColor: Color, onClick: () -> Unit) {
    DropdownMenuItem(modifier = Modifier.defaultMinSize(minHeight = 38.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
        text = {
            Text(text = label, color = textColor, fontSize = 13.sp, maxLines = 2)
        }, leadingIcon = if (showIcon) {
                {
                    Icon(imageVector = icon, contentDescription = null, tint = textColor)
                }
            } else {
                null
            }, onClick = onClick)
}

@Composable
private fun PriorityMenuItem(label: String, selected: Boolean, textColor: Color, showIndicator: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(modifier = Modifier.defaultMinSize(minHeight = 38.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
        text = {
            Text(text = label,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    })
        },
        leadingIcon = if (showIndicator) {
                {
                    if (selected) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                    }
                }
            } else {
                null
            },
        onClick = onClick)
}

@Composable
private fun ColorMenuItem(label: String, color: Color, textColor: Color, showSwatch: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(modifier = Modifier.defaultMinSize(minHeight = 38.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
        text = {
            Text(text = label, color = textColor, fontSize = 13.sp, maxLines = 2)
        },
        leadingIcon = if (showSwatch) {
                {
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(color))
                }
            } else {
                null
            },
        onClick = onClick)
}

private fun normalizedMenuOrder(raw: String, validKeys: List<String>): List<String> {
    val requested = raw.split(",").map {
                it.trim()
            }.filter {
                it in
                    validKeys
            }.distinct()
    return requested + validKeys.filterNot {
                it in
                    requested
            }
}

private fun parseMenuKeys(raw: String, validKeys: List<String>): Set<String> {
    return raw.split(",").map {
            it.trim()
        }.filter {
            it in
                validKeys
        }.toSet()
}
