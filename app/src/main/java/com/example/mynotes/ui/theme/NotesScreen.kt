package com.example.mynotes.ui
import com.example.mynotes.ui.components.AppHeading
import com.example.mynotes.ui.theme.rememberUiTextColors
import com.example.mynotes.ui.theme.rememberUiContentColors
import com.example.mynotes.ui.theme.rememberAppFontFamily
import com.example.mynotes.ui.components.AppIconButton
import com.example.mynotes.ui.components.AppIconLabel
import com.example.mynotes.performance.NoteTextCache

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mynotes.R
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.components.ModernNoteCard
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.components.extractLinkUrls
import com.example.mynotes.ui.components.preloadLinkPreviews
import com.example.mynotes.ui.components.toNoteCardStyle
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiHapticPlayer
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.viewmodel.NoteViewModel
import kotlinx.coroutines.delay

private enum class NoteFilter {
    ALL, FAVORITES, PINNED, PRIORITY, WORK, PERSONAL, IMAGES, FILES
}

@Immutable
private data class NoteFilterOption(val filter: NoteFilter, val labelRes: Int)

@Immutable
private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>)

private data class QuickCreateSpec(val text: String, val icon: ImageVector, val sound: UiActionSound, val action: () -> Unit)

private val FilterOptions = listOf(
    NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all),
    NoteFilterOption(NoteFilter.FAVORITES, R.string.mock_favorites),
    NoteFilterOption(NoteFilter.PINNED, R.string.widget_pinned_collection),
    NoteFilterOption(NoteFilter.PRIORITY, R.string.widget_high_priority),
    NoteFilterOption(NoteFilter.WORK, R.string.mock_work),
    NoteFilterOption(NoteFilter.PERSONAL, R.string.mock_personal),
    NoteFilterOption(NoteFilter.IMAGES, R.string.mock_images),
    NoteFilterOption(NoteFilter.FILES, R.string.mock_files)
)

private fun noteFilterFromWidgetKey(key: String?): NoteFilter = when (key) {
    "favorites" -> NoteFilter.FAVORITES
    "pinned" -> NoteFilter.PINNED
    "priority" -> NoteFilter.PRIORITY
    "work" -> NoteFilter.WORK
    "personal" -> NoteFilter.PERSONAL
    "images" -> NoteFilter.IMAGES
    "files" -> NoteFilter.FILES
    else -> NoteFilter.ALL
}

@Composable
fun NotesScreen(
    notes: List<Note>,
    noteViewModel: NoteViewModel,
    settings: AppSettings,
    initialFilterKey: String? = null,
    requestSearchFocus: Boolean = false,
    widgetRequestToken: Int = 0,
    onAddNote: () -> Unit,
    onDrawNote: () -> Unit,
    onOpenReminders: () -> Unit,
    onAddPdf: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNote: (Note) -> Unit,
    onEditNote: (Note) -> Unit
) {
    val allAttachments by
        noteViewModel.allAttachments.collectAsStateWithLifecycle()
    val attachmentIndex = remember(allAttachments) {
            val byNote = allAttachments.groupBy {
                    it.noteId
                }
            val kindsByNote = buildMap<Int, Set<String>> {
                    byNote.forEach { (noteId, items) -> put(noteId, items.asSequence().map { it.type }.toSet())
                    }
                }
            AttachmentIndex(byNote = byNote, kindsByNote = kindsByNote)
        }
    val attachmentsByNote = attachmentIndex.byNote
    val fontFamily = rememberAppFontFamily(settings.font)
    val (screenPrimaryTextColor, screenSecondaryTextColor) = rememberUiTextColors(settings.textColor, MaterialTheme.colorScheme.background)
    val quickCreateSurfaceColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val quickCreateTextColor = resolveUiTextColor(value = settings.textColor, background = quickCreateSurfaceColor)
    val controlSurfaceColor = MaterialTheme.colorScheme.surfaceContainerLow
    val (controlTextColor, controlSecondaryTextColor, controlGraphicColor) = rememberUiContentColors(settings.textColor, controlSurfaceColor)
    val selectedControlTextColor = resolveUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.primaryContainer)
    val fabContainerColor = MaterialTheme.colorScheme.inverseSurface
    val fabContentColor = resolveUiTextColor(value = settings.textColor, background = fabContainerColor)
    val noteCardStyle = remember(settings.noteCardCornerRadius, settings.noteCardElevation, settings.noteCardPadding,
            settings.noteCardImageHeight, settings.noteCardOutlineWidth,
            settings.noteTitleMaxLines, settings.noteContentMaxLines, settings.noteLineSpacing,
            settings.iconSize, settings.showNoteDate, settings.showCategoryChip, settings.showFavoriteIcon, settings.animationsEnabled,
            settings.animationSpeed) {
            settings.toNoteCardStyle()
        }
    var query by
        rememberSaveable {
            mutableStateOf("")
        }
    var effectiveQuery by remember { mutableStateOf("") }
    LaunchedEffect(query) {
        delay(90)
        effectiveQuery = query.trim().lowercase()
    }
    val searchIsActive = effectiveQuery.isNotBlank()
    val textCache = remember { NoteTextCache(::extractLinkUrls) }
    val textIndex = remember(notes, searchIsActive) { textCache.update(notes, searchIsActive) }
    val searchableTextByNote = textIndex.searchableTextByNote
    val focusManager = LocalFocusManager.current
    val searchFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val gridState = rememberLazyStaggeredGridState()
    val isGridScrolling by remember {
        derivedStateOf { gridState.isScrollInProgress }
    }
    val linkPreviewUrls = textIndex.linkPreviewUrls
    LaunchedEffect(linkPreviewUrls, isGridScrolling, settings.performanceMode) {
        if (!isGridScrolling && linkPreviewUrls.isNotEmpty()) {
            val idleDelayMs = AppMotion.performanceValue(settings.performanceMode, performance = 900L, balanced = 650L, quality = 450L)
            delay(idleDelayMs)
            if (!gridState.isScrollInProgress) {
                preloadLinkPreviews(context = context.applicationContext, urls = linkPreviewUrls, performanceMode = settings.performanceMode)
            }
        }
    }
    val configuration = LocalConfiguration.current
    val currentOrientation = configuration.orientation
    val isLandscape = currentOrientation == Configuration.ORIENTATION_LANDSCAPE
    var previousOrientation by
        rememberSaveable {
            mutableIntStateOf(currentOrientation)
        }
    LaunchedEffect(currentOrientation) {
        if (previousOrientation != currentOrientation) {
            withFrameNanos { }
            withFrameNanos { }
            focusManager.clearFocus(force = true)
            previousOrientation = currentOrientation
        }
    }
    var selectedFilter by remember { mutableStateOf(NoteFilter.ALL) }

    LaunchedEffect(widgetRequestToken) {
        if (widgetRequestToken > 0) {
            selectedFilter = noteFilterFromWidgetKey(initialFilterKey)
            if (requestSearchFocus) {
                withFrameNanos { }
                searchFocusRequester.requestFocus()
            } else {
                focusManager.clearFocus(force = true)
            }
        }
    }
    val visibleNotes by
        remember(notes, attachmentIndex, searchableTextByNote) {
            derivedStateOf {
                val normalizedQuery = effectiveQuery
                if (normalizedQuery.isBlank() && selectedFilter == NoteFilter.ALL) {
                    return@derivedStateOf notes
                }
                notes.filter { note ->
                    val matchesText = normalizedQuery.isBlank() ||
                        searchableTextByNote[note.id].orEmpty().contains(normalizedQuery)
                    if (!matchesText) {
                        return@filter false
                    }
                    when (selectedFilter) {
                        NoteFilter.ALL -> true
                        NoteFilter.FAVORITES -> note.isFavorite
                        NoteFilter.PINNED -> note.isPinned
                        NoteFilter.PRIORITY -> note.priority == 3
                        NoteFilter.WORK -> note.category == "work"
                        NoteFilter.PERSONAL -> note.category == "personal"
                        NoteFilter.IMAGES -> "image" in attachmentIndex.kindsByNote[note.id].orEmpty()
                        NoteFilter.FILES -> "file" in attachmentIndex.kindsByNote[note.id].orEmpty()
                    }
                }
            }
        }
    val qualityScrollPreviewAttachments = remember(visibleNotes, attachmentIndex, settings.performanceMode) {
        if (settings.performanceMode != "quality") {
            emptyList()
        } else {
            visibleNotes.asSequence()
                .flatMap { note -> attachmentsByNote[note.id].orEmpty().take(4).asSequence() }
                .filter { attachment ->
                    attachment.type == "image" || attachment.type == "video" ||
                        (attachment.type == "file" &&
                            attachment.name?.substringAfterLast('.', "")?.equals("pdf", ignoreCase = true) == true)
                }
                .distinctBy { attachment -> attachment.id to attachment.uri }
                .toList()
        }
    }

    LaunchedEffect(qualityScrollPreviewAttachments, settings.performanceMode) {
        if (settings.performanceMode == "quality") {
            listOf("instant", "quality").forEach { mode ->
                qualityScrollPreviewAttachments.forEach { attachment ->
                    AttachmentPreviewCache.prewarm(
                        context.applicationContext, Uri.parse(attachment.uri), attachment.type, attachment.name, mode
                    )
                }
            }
        }
    }

    val motionDuration = AppMotion.duration(AppMotion.NORMAL, settings.animationsEnabled, settings.animationSpeed)
    val animatedProfileSize by
        animateDpAsState(targetValue = settings.profileImageSize.dp, animationSpec = tween(durationMillis = motionDuration), label =
                "profileSize")
    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled,
        animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            QuickCreateFabMenu(
                settings = settings,
                fontFamily = fontFamily,
                containerColor = quickCreateSurfaceColor,
                contentColor = quickCreateTextColor,
                fabContainerColor = fabContainerColor,
                fabContentColor = fabContentColor,
                onOpenReminders = onOpenReminders,
                onAddNote = onAddNote,
                onAddPdf = onAddPdf,
                onDrawNote = onDrawNote
            )
        }) {
            scaffoldPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(scaffoldPadding).padding(horizontal = 14.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    AppHeading(
                        title = stringResource(R.string.mock_my_notes), subtitle = stringResource(R.string.mock_notes_subtitle),
                        fontFamily = fontFamily, titleColor = screenPrimaryTextColor, subtitleColor = screenSecondaryTextColor,
                        titleSize = 30.sp,
                        subtitleSize = 14.sp,
                        subtitleModifier = Modifier.padding(top = 1.dp)
                    )
                }
                Surface(modifier = Modifier.size(animatedProfileSize).clip(CircleShape).clickable(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Settings, onOpenSettings)), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                    if (settings.profileImageUri.isNotBlank()) {
                        AsyncImage(model = Uri.parse(settings.profileImageUri), contentDescription = stringResource(R.string.mock_settings
                                ), modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = stringResource(R.string.mock_settings), modifier =
                                    Modifier.size(settings.iconSize.coerceIn(18f, 36f).dp))
                        }
                    }
                }
                AppIconButton(Icons.Default.Settings, stringResource(R.string.mock_settings),
                    onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Settings, onOpenSettings), iconModifier = Modifier.size(settings.iconSize.coerceIn(18f, 36f).dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(value = query,
                onValueChange = {
                    UiSoundPlayer.playTextInput(context, query, it)
                    if (it != query) {
                        UiHapticPlayer.playForAction(context = context, action = UiActionSound.Search,
                            throttled = true, minimumIntervalMs = 65L)
                    }
                    query = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(searchFocusRequester),
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp))
                },
                placeholder = {
                    Text(text = stringResource(R.string.mock_search_notes),
                        fontFamily = fontFamily)
                },
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = controlSurfaceColor,
                            unfocusedContainerColor = controlSurfaceColor,
                            focusedTextColor = controlTextColor,
                            unfocusedTextColor = controlTextColor,
                            focusedPlaceholderColor = controlSecondaryTextColor,
                            unfocusedPlaceholderColor = controlSecondaryTextColor,
                            focusedLeadingIconColor = controlGraphicColor,
                            unfocusedLeadingIconColor = controlGraphicColor,
                            cursorColor = controlTextColor,
                            focusedBorderColor = controlGraphicColor,
                            unfocusedBorderColor = controlGraphicColor))
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isLandscape) {
                        Arrangement.spacedBy(space = 7.dp, alignment = Alignment.CenterHorizontally)
                    } else {
                        Arrangement.spacedBy(7.dp)
                    },
                contentPadding = PaddingValues(start = if (isLandscape) 12.dp else 0.dp, end = 12.dp)) {
                items(items = FilterOptions,
                    key = {
                        it.filter.name
                    }) {
                        option ->
                    val selected = option.filter == selectedFilter
                    FilterChip(selected = selected,
                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Select) {
                            selectedFilter = option.filter
                        },
                        colors = FilterChipDefaults.filterChipColors(containerColor = controlSurfaceColor,
                                    labelColor = controlTextColor,
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = selectedControlTextColor),
                        label = {
                            Text(text = stringResource(option.labelRes),
                                fontFamily = fontFamily,
                                fontSize = 15.sp,
                                color = if (selected) {
                                        selectedControlTextColor
                                    } else {
                                        controlTextColor
                                    },
                                fontWeight = if (selected) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Medium
                                    })
                        })
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (visibleNotes.isEmpty()) {
                EmptyNotesState(modifier = Modifier.fillMaxSize(),
                    hasSearch = query.isNotBlank() || selectedFilter != NoteFilter.ALL,
                    fontFamily = fontFamily,
                    textColorMode = settings.textColor)
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val maximumColumnsForWidth = (maxWidth.value / 145f).toInt().coerceIn(1, 3)
                    val effectiveColumns = minOf(settings.gridColumns.coerceIn(1, 3), maximumColumnsForWidth)
                LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(effectiveColumns),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    verticalItemSpacing = 9.dp,
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    contentPadding = PaddingValues(
                        bottom = 100.dp
                    )) {
                    items(items = visibleNotes,
                        key = {
                            it.id
                        },
                        contentType = {
                            "note"
                        }) {
                            note ->
                        val noteAttachments = attachmentsByNote[note.id].orEmpty()
                        ModernNoteCard(note = note,
                            attachments = noteAttachments,
                            fontFamily = fontFamily,
                            fontSize = settings.fontSize,
                            style = noteCardStyle,
                            textColorMode = settings.textColor,
                            optionMenuOrder = settings.optionMenuOrder,
                            optionMenuHiddenItems = settings.optionMenuHiddenItems,
                            optionMenuShowIcons = settings.optionMenuShowIcons,
                            optionMenuTextColor = settings.optionMenuTextColor,
                            optionMenuOpacity = settings.optionMenuOpacity,
                            priorityMenuHiddenItems = settings.priorityMenuHiddenItems,
                            colorMenuHiddenItems = settings.colorMenuHiddenItems,
                            performanceMode = settings.performanceMode,
                            isScrolling = isGridScrolling,
                            onOpen = UiSoundPlayer.actionHandler(context, UiActionSound.Open) { onOpenNote(note) },
                            onEdit = {
                                UiSoundPlayer.play(context = context, sound = UiSound.Edit)
                                onEditNote(note)
                            },
                            onToggleFavorite = UiSoundPlayer.actionHandler(context, UiActionSound.Favorite) { noteViewModel.toggleFavorite(note) },
                            onTogglePinned = UiSoundPlayer.actionHandler(context, UiActionSound.Pin) { noteViewModel.togglePinned(note) },
                            onPriorityChange = {
                                    priority ->
                                UiSoundPlayer.play(context = context, sound = UiSound.Priority)
                                noteViewModel.changePriority(note = note, priority = priority)
                            },
                            onColorChange = UiSoundPlayer.actionHandler(context, UiActionSound.Color) { color ->
                                noteViewModel.changeNoteColor(note = note, color = color)
                            },
                            onCategoryChange = UiSoundPlayer.actionHandler(context, UiActionSound.Category) { category ->
                                noteViewModel.changeCategory(note = note, category = category)
                            },
                            onDelete = {
                                UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                                noteViewModel.deleteNote(note)
                            })
                    }
                }
                ScrollPositionCapsule(
                    state = gridState,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 13.dp),
                    backgroundColor = MaterialTheme.colorScheme.background,
                    preferredColor = MaterialTheme.colorScheme.onBackground,
                    fixedThumbHeight = 54.dp,
                    smoothMovement = true
                )
                }
            }
        }
    }
    }
}

@Composable
private fun QuickCreateFabMenu(
    settings: AppSettings,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    fabContainerColor: androidx.compose.ui.graphics.Color,
    fabContentColor: androidx.compose.ui.graphics.Color,
    onOpenReminders: () -> Unit,
    onAddNote: () -> Unit,
    onAddPdf: () -> Unit,
    onDrawNote: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    BackHandler(enabled = expanded) { expanded = false }

    val context = LocalContext.current
    val motionDuration = AppMotion.duration(AppMotion.NORMAL, settings.animationsEnabled, settings.animationSpeed)
    val animatedFabSize by animateDpAsState(
        targetValue = settings.fabSize.dp,
        animationSpec = tween(durationMillis = motionDuration),
        label = "fabSize"
    )

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(motionDuration)) +
                expandVertically(animationSpec = tween(motionDuration), expandFrom = Alignment.Bottom),
            exit = fadeOut(animationSpec = tween(motionDuration)) +
                shrinkVertically(animationSpec = tween(motionDuration), shrinkTowards = Alignment.Bottom)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    QuickCreateSpec(stringResource(R.string.reminders), Icons.Default.NotificationsActive, UiActionSound.Select, onOpenReminders),
                    QuickCreateSpec(stringResource(R.string.mock_new_note), Icons.Default.NoteAdd, UiActionSound.Add, onAddNote),
                    QuickCreateSpec(stringResource(R.string.add_pdf), Icons.Default.PictureAsPdf, UiActionSound.Add, onAddPdf),
                    QuickCreateSpec(stringResource(R.string.create_drawing), Icons.Default.Brush, UiActionSound.Select, onDrawNote)
                ).forEach { item ->
                    QuickCreateActionButton(
                        text = item.text,
                        icon = item.icon,
                        fontFamily = fontFamily,
                        containerColor = containerColor,
                        contentColor = contentColor,
                        animationsEnabled = settings.animationsEnabled,
                        animationSpeed = settings.animationSpeed,
                        animationEasing = settings.animationEasing
                    ) {
                        expanded = false
                        UiSoundPlayer.runAction(context, item.sound, item.action)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                expanded = !expanded
            },
            modifier = Modifier.size(animatedFabSize),
            containerColor = fabContainerColor,
            contentColor = fabContentColor,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = stringResource(R.string.add_action_menu),
                modifier = Modifier.size(settings.iconSize.coerceIn(18f, 36f).dp),
                tint = fabContentColor
            )
        }
    }
}

@Composable
private fun QuickCreateActionButton(
    text: String,
    icon: ImageVector,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    animationsEnabled: Boolean,
    animationSpeed: Float,
    animationEasing: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressDuration = AppMotion.duration(110, animationsEnabled, animationSpeed)
    val pressScale by animateFloatAsState(
        targetValue = if (pressed && animationsEnabled) 0.972f else 1f,
        animationSpec = tween(
            durationMillis = pressDuration,
            easing = AppMotion.easing(animationEasing)
        ),
        label = "quickCreatePressScale"
    )

    Surface(
        modifier = Modifier
            .height(46.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 5.dp,
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AppIconLabel(icon, text, iconModifier = Modifier.size(20.dp), tint = contentColor, gap = 8.dp, fontFamily = fontFamily,
                fontWeight = FontWeight.Bold, fontSize = 14.sp, color = contentColor, maxLines = 1)
        }
    }
}

@Composable
private fun EmptyNotesState(modifier: Modifier, hasSearch: Boolean, fontFamily:
        androidx.compose.ui.text.font.FontFamily, textColorMode: String) {
    Box(modifier = modifier,
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = if (hasSearch) {
                        "🔍"
                    } else {
                        "📝"
                    },
                fontSize = 38.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(if (hasSearch) {
                            R.string.mock_no_results
                        } else {
                            R.string.mock_no_notes
                        }),
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground)
            Text(text = stringResource(if (hasSearch) {
                            R.string.mock_try_other_search
                        } else {
                            R.string.mock_create_first_note
                        }),
                modifier = Modifier.padding(top = 4.dp),
                fontFamily = fontFamily,
                fontSize = 13.sp,
                color = resolveSecondaryUiTextColor(value = textColorMode, background = MaterialTheme.colorScheme.background))
        }
    }
}
