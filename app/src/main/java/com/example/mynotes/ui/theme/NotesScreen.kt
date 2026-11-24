package com.example.mynotes.ui

import com.example.mynotes.ui.components.appScreenBackground
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.components.AppHeading
import com.example.mynotes.ui.theme.rememberAppFontFamily
import com.example.mynotes.ui.components.AppIconButton
import com.example.mynotes.ui.components.LocalNoteCardPreviewCache
import com.example.mynotes.ui.components.LocalNoteCardDateFormatter
import com.example.mynotes.ui.components.NoteCardPreviewText
import com.example.mynotes.performance.NoteTextCache

import android.provider.Settings as AndroidSettings
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.produceState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import coil3.compose.AsyncImage
import com.example.mynotes.R
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.performance.PreviewPrefetchPolicy
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.components.ModernNoteCard
import com.example.mynotes.ui.components.QuickCreateFabMenu
import com.example.mynotes.ui.components.ScrollPositionCapsule
import com.example.mynotes.ui.components.preloadLinkPreviews
import com.example.mynotes.ui.components.toNoteCardStyle
import com.example.mynotes.ui.motion.AnimatedScreenEntry
import com.example.mynotes.ui.motion.AppMotion
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiHapticPlayer
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.ensureUiContrast
import com.example.mynotes.ui.theme.softenUiColorToContrast
import com.example.mynotes.ui.theme.harmonizedHomeSurfaces
import com.example.mynotes.ui.notes.*
import com.example.mynotes.ui.components.NotePaletteDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import com.example.mynotes.ui.state.NoteActions
import com.example.mynotes.ui.state.SettingsActions
import com.example.mynotes.ui.state.NoteFeedItem
import com.example.mynotes.ui.state.NoteSections
import com.example.mynotes.ui.components.HomeViewControls
import com.example.mynotes.ui.components.NoteSectionHeading
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Description
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.TimeZone

private enum class NoteFilter {
    ALL, FAVORITES, PINNED, PRIORITY, WORK, PERSONAL, IMAGES, FILES, DUPLICATES, OLD, READ_LATER
}

@Immutable
private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>)

private fun noteFilterFromWidgetKey(key: String?): NoteFilter = when (key) {
    "favorites" -> NoteFilter.FAVORITES
    "pinned" -> NoteFilter.PINNED
    "priority" -> NoteFilter.PRIORITY
    "work" -> NoteFilter.WORK
    "personal" -> NoteFilter.PERSONAL
    "images" -> NoteFilter.IMAGES
    "files" -> NoteFilter.FILES
    "duplicates" -> NoteFilter.DUPLICATES
    "old" -> NoteFilter.OLD
    "read_later" -> NoteFilter.READ_LATER
    else -> NoteFilter.ALL
}

@Composable
fun NotesScreen(
    notes: List<Note>,
    noteViewModel: NoteActions,
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
    onEditNote: (Note) -> Unit,
    settingsActions: SettingsActions? = null,
    onAddInFolder: ((String?) -> Unit)? = null
) {
    val layoutPreview = LocalLayoutPreview.current
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
    val folders by noteViewModel.folders.collectAsStateWithLifecycle()
    val pendingUndo by noteViewModel.pendingUndo.collectAsStateWithLifecycle()
    var selectedFolderId by rememberSaveable { mutableStateOf<String?>(null) }
    var showArchive by rememberSaveable { mutableStateOf(layoutPreview?.archived == true) }
    var selectedIds by rememberSaveable { mutableStateOf(layoutPreview?.selectionIds.orEmpty()) }
    val selectedIdSet = remember(selectedIds) { selectedIds.toSet() }
    val notesById = remember(notes) { notes.associateBy { it.id } }
    val createNote: () -> Unit = {
        showArchive = false
        if (onAddInFolder != null) onAddInFolder(selectedFolderId) else onAddNote()
    }
    var manageFolders by remember { mutableStateOf(false) }
    var moveSelection by remember { mutableStateOf(false) }
    var colorSelection by remember { mutableStateOf(false) }
    var bulkSelectedColor by remember { mutableStateOf("default") }
    val snackbar = remember { SnackbarHostState() }
    val undoText = pendingUndo?.let { undoMessage(it) }.orEmpty()
    val undoLabel = stringResource(R.string.organize_undo)
    LaunchedEffect(pendingUndo?.token) {
        val undo = pendingUndo ?: return@LaunchedEffect
        val result = snackbar.showSnackbar(undoText, undoLabel, withDismissAction = true, duration = SnackbarDuration.Long)
        if (result == SnackbarResult.ActionPerformed) noteViewModel.undo(undo.token) else noteViewModel.dismissUndo(undo.token)
    }
    if (manageFolders) FolderManagerDialog(folders, noteViewModel) { manageFolders = false }
    if (moveSelection) FolderPickerDialog(folders, onSelect = { folder ->
        noteViewModel.moveNotes(selectedIds.toSet(), folder); selectedIds = emptyList(); moveSelection = false
    }, onDismiss = { moveSelection = false })
    if (colorSelection) NotePaletteDialog(
        selectedColor = bulkSelectedColor,
        onDismiss = { colorSelection = false; selectedIds = emptyList() },
        onColorSelected = { color ->
            bulkSelectedColor = color
            noteViewModel.colorNotes(selectedIds.toSet(), color)
        }
    )
    LaunchedEffect(notes) {
        if (selectedIds.isNotEmpty()) selectedIds = selectedIds.filter { it in notesById }
    }
    val attachmentsByNote = attachmentIndex.byNote
    // Evitar la búsqueda O(n) de la carpeta para cada NoteCard que entra al viewport.
    val folderNamesById = remember(folders) { folders.associate { it.id to it.name } }
    val fontFamily = rememberAppFontFamily(settings.font)
    val screenSurface = MaterialTheme.colorScheme.background
    val screenPrimaryTextColor = remember(settings.textColor, screenSurface) {
        ensureUiContrast(resolveUiTextColor(settings.textColor, screenSurface), screenSurface, 4.5f)
    }
    val screenSecondaryTextColor = remember(screenPrimaryTextColor, screenSurface) {
        softenUiColorToContrast(screenPrimaryTextColor, screenSurface, 4.5f, 0.58f)
    }
    val quickCreateSurfaceColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val quickCreateTextColor = remember(settings.textColor, quickCreateSurfaceColor) {
        ensureUiContrast(resolveUiTextColor(settings.textColor, quickCreateSurfaceColor), quickCreateSurfaceColor, 4.5f)
    }
    val controlSurfaceColor = MaterialTheme.colorScheme.surfaceContainerLow
    val controlTextColor = remember(settings.textColor, controlSurfaceColor) {
        ensureUiContrast(resolveUiTextColor(settings.textColor, controlSurfaceColor), controlSurfaceColor, 4.5f)
    }
    val controlSecondaryTextColor = remember(controlTextColor, controlSurfaceColor) {
        softenUiColorToContrast(controlTextColor, controlSurfaceColor, 4.5f, 0.58f)
    }
    val controlGraphicColor = remember(controlTextColor, controlSurfaceColor) {
        softenUiColorToContrast(controlTextColor, controlSurfaceColor, 3f, 0.70f)
    }
    // Tarjetas y FAB de la misma familia que el fondo real, sin elegir
    // muestras demasiado grises o apagadas de forma arbitraria.
    // Se mantiene estable durante el scroll y las animaciones de las notas.
    val homeSurfaces = remember(settings.backgroundColor, screenSurface) {
        harmonizedHomeSurfaces(settings.backgroundColor, screenSurface)
    }
    val noteCardStyle = remember(settings.noteCardCornerRadius, settings.noteCardElevation, settings.noteCardPadding,
            settings.noteCardImageHeight, settings.noteCardOutlineWidth,
            settings.noteTitleMaxLines, settings.noteContentMaxLines, settings.noteLineSpacing,
            settings.iconSize, settings.showNoteDate, settings.showCategoryChip, settings.showFavoriteIcon, settings.animationsEnabled,
            settings.animationSpeed, settings.cardDensity) {
            settings.toNoteCardStyle()
        }
    var query by
        rememberSaveable {
            mutableStateOf(layoutPreview?.query.orEmpty())
        }
    var effectiveQuery by remember { mutableStateOf(layoutPreview?.query.orEmpty().trim().lowercase()) }
    LaunchedEffect(query) {
        delay(90)
        effectiveQuery = query.trim().lowercase()
    }
    val searchIsActive = effectiveQuery.isNotBlank()
    val textCache = remember { NoteTextCache() }
    val searchableTextByNote by produceState(
        initialValue = emptyMap<Int, String>(), key1 = notes, key2 = searchIsActive
    ) {
        value = if (searchIsActive) {
            withContext(Dispatchers.Default) { textCache.update(notes) }
        } else {
            withContext(Dispatchers.Default) { textCache.clear() }
            emptyMap()
        }
    }
    val focusManager = LocalFocusManager.current
    val searchFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val gridState = rememberLazyStaggeredGridState()
    // Suspend idle animation coroutines when the app is in the background.
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val screenResumed = lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
    val allowIdleMotion = remember(context) {
        runCatching { AndroidSettings.Global.getFloat(context.contentResolver,
            AndroidSettings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f }.getOrDefault(true)
    }
    var readLaterIds by remember(context) { mutableStateOf(ReadingQueueStore.ids(context)) }
    val motionOptions = remember(context, screenResumed) { IdleMotionPreferences.get(context) }
    val idleMotion = remember { NotesIdleMotion() }
    var menuNoteId by remember { mutableStateOf<Int?>(null) }
    var quickCreateExpanded by remember { mutableStateOf(false) }
    val scrolling = remember(gridState) { derivedStateOf { gridState.isScrollInProgress } }
    val cardPreviewCache = remember { NoteCardPreviewText.Cache() }
    val configuration = LocalConfiguration.current
    val currentOrientation = configuration.orientation
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
    var selectedFilter by rememberSaveable { mutableStateOf(NoteFilter.ALL) }

    LaunchedEffect(widgetRequestToken) {
        if (widgetRequestToken <= 0) return@LaunchedEffect
        selectedFilter = noteFilterFromWidgetKey(initialFilterKey)
        if (requestSearchFocus) {
            withFrameNanos { }
            searchFocusRequester.requestFocus()
        } else {
            focusManager.clearFocus(force = true)
        }
    }
    var groupingNow by remember { mutableStateOf(layoutPreview?.nowMillis ?: System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        if (layoutPreview != null) return@LaunchedEffect
        while (true) {
            groupingNow = System.currentTimeMillis()
            delay((NoteSections.nextMidnight(groupingNow) - groupingNow).coerceAtLeast(1_000L))
        }
    }
    // Duplicate analysis is expensive: run only when the filter is active.
    val needsDuplicateAnalysis = selectedFilter == NoteFilter.DUPLICATES
    val duplicateIds by produceState<Set<Int>>(
        initialValue = emptySet(), key1 = notes, key2 = needsDuplicateAnalysis
    ) {
        value = if (needsDuplicateAnalysis) withContext(Dispatchers.Default) {
            NoteLibraryInsights.duplicateIds(notes)
        } else emptySet()
    }
    val needsOldNotes = selectedFilter == NoteFilter.OLD
    val oldNoteIds = remember(notes, groupingNow, needsOldNotes) {
        if (needsOldNotes) NoteLibraryInsights.oldNoteIds(notes, groupingNow) else emptySet()
    }
    val visibleNotes by
        remember(notes, attachmentIndex, searchableTextByNote, duplicateIds, oldNoteIds, readLaterIds) {
            derivedStateOf {
                val normalizedQuery = effectiveQuery
                notes.filter { note ->
                    if (!com.example.mynotes.data.CollectionPolicy.contains(note, selectedFolderId, showArchive)) return@filter false
                    val matchesText = normalizedQuery.isBlank() || searchableTextByNote.isEmpty() ||
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
                        NoteFilter.DUPLICATES -> note.id in duplicateIds
                        NoteFilter.OLD -> note.id in oldNoteIds
                        NoteFilter.READ_LATER -> note.id in readLaterIds
                    }
                }
            }
        }
    val feedItems = remember(visibleNotes, settings.sortOrder, groupingNow) {
        NoteSections.build(visibleNotes, settings.sortOrder, groupingNow)
    }
    var selectedDetailId by rememberSaveable { mutableStateOf<Int?>(layoutPreview?.selectedNoteId) }
    LaunchedEffect(notes) {
        if (selectedDetailId?.let { it !in notesById } == true) selectedDetailId = null
    }
    LaunchedEffect(effectiveQuery, selectedFilter, settings.sortOrder, selectedFolderId, showArchive) { gridState.scrollToItem(0) }
    LaunchedEffect(effectiveQuery, selectedFilter, selectedFolderId, showArchive) { if (layoutPreview == null) selectedIds = emptyList() }
    BackHandler(enabled = selectedIds.isNotEmpty()) { selectedIds = emptyList() }
    val overlayOpen = manageFolders || moveSelection || colorSelection || menuNoteId != null || quickCreateExpanded
    val idleMotionEnabled = allowIdleMotion && settings.animationsEnabled && motionOptions.enabled &&
        !searchIsActive && selectedIds.isEmpty() && settings.performanceMode != "performance"
    NotesPreviewPrefetch(gridState, feedItems, attachmentsByNote, settings.performanceMode,
        enabled = layoutPreview == null && screenResumed && !overlayOpen)
    NotesIdleMotionEffect(gridState, feedItems, idleMotion, motionOptions,
        enabled = layoutPreview == null && screenResumed && idleMotionEnabled && !overlayOpen)

    val motionDuration = AppMotion.duration(AppMotion.NORMAL, settings.animationsEnabled, settings.animationSpeed)
    val animatedProfileSize by
        animateDpAsState(targetValue = settings.profileImageSize.dp, animationSpec = tween(durationMillis = motionDuration), label =
                "profileSize")
    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled, animationSpeed = settings.animationSpeed) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val expanded = maxWidth >= 840.dp
            LaunchedEffect(expanded) {
                val savedNote = selectedDetailId?.let(notesById::get)
                if (!expanded && savedNote != null) {
                    selectedDetailId = null
                    onOpenNote(savedNote)
                }
            }
            BackHandler(enabled = expanded && selectedDetailId?.let(notesById::containsKey) == true && selectedIds.isEmpty()) {
                selectedDetailId = null
            }
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.weight(if (expanded) 0.44f else 1f).fillMaxHeight()) {
                    NotesBackground(Modifier.matchParentSize(), scrolling)
                    Scaffold(
                        containerColor = Color.Transparent,
                        snackbarHost = { SnackbarHost(snackbar) },
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        // La navegación inmersiva no debe dejar una franja reservada bajo la lista.
                        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
                            WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                        ),
                        floatingActionButton = {
                            // El botón flotante se presenta después de guardar la primera nota.
                            // Se usa toda la biblioteca (no visibleNotes) para que los filtros,
                            // búsquedas y carpetas vacías no lo oculten por accidente.
                            if (notes.isNotEmpty() && selectedIds.isEmpty()) QuickCreateFabMenu(
                                settings = settings,
                                fontFamily = fontFamily,
                                containerColor = quickCreateSurfaceColor,
                                contentColor = quickCreateTextColor,
                                onOpenReminders = onOpenReminders,
                                onAddNote = createNote,
                                onAddPdf = onAddPdf,
                                onDrawNote = onDrawNote,
                                onExpandedChange = { quickCreateExpanded = it }
                            )
                        }
                    ) { scaffoldPadding ->
                        Column(modifier = Modifier.fillMaxSize().padding(scaffoldPadding).padding(horizontal = 16.dp)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            if (selectedIds.isEmpty()) {
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
                                // No hay una fila fija de clasificación en Inicio.
                                // Los filtros pedidos desde widgets siguen activos;
                                // cuando uno se aplica aparece únicamente el acceso para volver a Todas.
                                if (selectedFilter != NoteFilter.ALL) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Select) {
                                            selectedFilter = NoteFilter.ALL
                                        }) {
                                            Text(stringResource(R.string.v319_show_all_notes), fontFamily = fontFamily)
                                        }
                                    }
                                } else {
                                    Spacer(Modifier.height(10.dp))
                                }
                                HomeViewControls(settings, settingsActions, visibleNotes.size, fontFamily)
                                FolderStrip(folders, selectedFolderId, showArchive,
                                    onSelect = { selectedFolderId = it; showArchive = false },
                                    onArchive = { showArchive = !showArchive; selectedFolderId = null }, onManage = { manageFolders = true })
                            } else SelectionToolbar(selectedIds.size, showArchive,
                                onClose = { selectedIds = emptyList() }, onAll = { selectedIds = visibleNotes.map { it.id } },
                                onMove = { moveSelection = true }, onColor = { bulkSelectedColor = "default"; colorSelection = true },
                                onArchive = { noteViewModel.archiveNotes(selectedIds.toSet(), !showArchive); selectedIds = emptyList() },
                                onDelete = { noteViewModel.deleteNotes(selectedIds.toSet()); selectedIds = emptyList() })
                            if (visibleNotes.isEmpty()) {
                                EmptyNotesState(modifier = Modifier.fillMaxSize(),
                                    hasSearch = query.isNotBlank() || selectedFilter != NoteFilter.ALL || selectedFolderId != null || showArchive,
                                    fontFamily = fontFamily, textColorMode = settings.textColor,
                                    onCreate = createNote, onClear = { query = ""; selectedFilter = NoteFilter.ALL; selectedFolderId = null; showArchive = false })
                            } else {
                                NotesGrid(
                                    feedItems = feedItems,
                                    gridState = gridState,
                                    preferredColumns = settings.gridColumns,
                                    fontFamily = fontFamily,
                                    previewCache = cardPreviewCache,
                                    onInteraction = idleMotion::onInteraction
                                ) { note ->
                                    val noteAttachments = attachmentsByNote[note.id].orEmpty()
                                    SwipeNoteContainer(enabled = selectedIds.isEmpty(), pinned = note.isPinned, archived = note.isArchived,
                                        animate = settings.animationsEnabled, cornerRadius = settings.noteCardCornerRadius,
                                        modifier = idleMotion.modifierFor(note.id),
                                        onPin = { noteViewModel.togglePinned(note) },
                                        onArchive = { noteViewModel.archiveNotes(setOf(note.id), !note.isArchived) }) {
                                        ModernNoteCard(note = note,
                                            attachments = noteAttachments,
                                            fontFamily = fontFamily,
                                            fontSize = settings.fontSize,
                                            style = noteCardStyle,
                                            globalPaletteCardColor = homeSurfaces.noteCard,
                                            globalPaletteCardOutlineColor = homeSurfaces.noteOutline,
                                            textColorMode = settings.textColor,
                                            optionMenuOrder = settings.optionMenuOrder,
                                            optionMenuHiddenItems = settings.optionMenuHiddenItems,
                                            optionMenuShowIcons = settings.optionMenuShowIcons,
                                            optionMenuTextColor = settings.optionMenuTextColor,
                                            optionMenuOpacity = settings.optionMenuOpacity,
                                            priorityMenuHiddenItems = settings.priorityMenuHiddenItems,
                                            colorMenuHiddenItems = settings.colorMenuHiddenItems,
                                            performanceMode = settings.performanceMode,
                                            scrolling = scrolling,
                                            onMenuStateChange = { open ->
                                                if (open) menuNoteId = note.id
                                                else if (menuNoteId == note.id) menuNoteId = null
                                            },
                                            isSelected = note.id in selectedIdSet || (expanded && selectedDetailId == note.id),
                                            selectionMode = selectedIds.isNotEmpty(),
                                            folderName = note.folderId?.let(folderNamesById::get),
                                            isReadLater = note.id in readLaterIds,
                                            onToggleReadLater = { readLaterIds = ReadingQueueStore.toggle(context, note.id) },
                                            onLongPress = {
                                                selectedDetailId = null
                                                selectedIds = if (note.id in selectedIds) selectedIds - note.id else selectedIds + note.id
                                            },
                                            onOpen = UiSoundPlayer.actionHandler(context, UiActionSound.Open) {
                                                if (selectedIds.isNotEmpty()) selectedIds = if (note.id in selectedIds) selectedIds - note.id else selectedIds + note.id
                                                else if (expanded) selectedDetailId = note.id else onOpenNote(note)
                                            },
                                            onEdit = {
                                                UiSoundPlayer.play(context = context, sound = UiSound.Edit)
                                                onEditNote(note)
                                            },
                                            onToggleFavorite = UiSoundPlayer.actionHandler(context, UiActionSound.Favorite) { noteViewModel.toggleFavorite(note) },
                                            onTogglePinned = UiSoundPlayer.actionHandler(context, UiActionSound.Pin) { noteViewModel.togglePinned(note) },
                                            onPriorityChange = { priority ->
                                                UiSoundPlayer.play(context = context, sound = UiSound.Priority)
                                                noteViewModel.changePriority(note = note, priority = priority)
                                            },
                                            // One normal selection click is played by PaletteTone itself.
                                            onColorChange = { color ->
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
                            }
                        }
                    }
                }
                if (expanded) {
                    VerticalDivider()
                    Box(Modifier.weight(0.56f).fillMaxHeight()) {
                        val detailNote = selectedDetailId?.let(notesById::get)
                        if (detailNote != null) {
                            NoteDetailScreen(note = detailNote, noteViewModel = noteViewModel, settings = settings,
                                onBack = { selectedDetailId = null }, onEdit = onEditNote, embedded = true,
                                settingsActions = settingsActions, backgroundMotionPaused = scrolling)
                        } else {
                            Column(Modifier.align(Alignment.Center).padding(32.dp).widthIn(max = 360.dp),
                                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(Icons.Default.Description, null, Modifier.size(56.dp))
                                Text(stringResource(R.string.design_select_note), fontFamily = fontFamily,
                                    style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                                Text(stringResource(R.string.design_select_note_description), fontFamily = fontFamily,
                                    style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesBackground(modifier: Modifier, scrolling: State<Boolean>) {
    Box(modifier.graphicsLayer().appScreenBackground(paused = scrolling))
}

@Composable
private fun NotesGrid(
    feedItems: List<NoteFeedItem>,
    gridState: LazyStaggeredGridState,
    preferredColumns: Int,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    previewCache: NoteCardPreviewText.Cache,
    onInteraction: () -> Unit,
    noteContent: @Composable (Note) -> Unit
) {
    val onGridInteraction by rememberUpdatedState(onInteraction)
    val locale = LocalConfiguration.current.locales[0]
    val timeZone = TimeZone.getDefault()
    val dateFormatter = remember(locale, timeZone) { DateFormat.getDateInstance(DateFormat.MEDIUM, locale) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val maximumColumns = (maxWidth.value / 145f).toInt().coerceIn(1, 3)
        val columns = minOf(preferredColumns.coerceIn(1, 3), maximumColumns)
        CompositionLocalProvider(
            LocalNoteCardPreviewCache provides previewCache,
            LocalNoteCardDateFormatter provides dateFormatter
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(columns),
                state = gridState,
                modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        onGridInteraction()
                    }
                },
                verticalItemSpacing = 12.dp,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(
                    items = feedItems,
                    key = { it.key },
                    contentType = { if (it is NoteFeedItem.Header) "header" else "note" },
                    span = { if (it is NoteFeedItem.Header) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane }
                ) { item ->
                    when (item) {
                        is NoteFeedItem.Header -> NoteSectionHeading(item.kind, item.count, fontFamily)
                        is NoteFeedItem.Entry -> noteContent(item.note)
                    }
                }
            }
        }
        ScrollPositionCapsule(
            state = gridState,
            modifier = Modifier.align(Alignment.CenterEnd).offset(x = 13.dp),
            backgroundColor = MaterialTheme.colorScheme.background,
            preferredColor = MaterialTheme.colorScheme.onBackground,
            fixedThumbHeight = 54.dp
        )
    }
}

@Composable
private fun NotesIdleMotionEffect(
    gridState: LazyStaggeredGridState,
    feedItems: List<NoteFeedItem>,
    motion: NotesIdleMotion,
    options: IdleMotionOptions,
    enabled: Boolean
) {
    LaunchedEffect(gridState, feedItems, motion, options, enabled) {
        if (!enabled || feedItems.isEmpty()) return@LaunchedEffect
        snapshotFlow { if (gridState.isScrollInProgress) null else motion.activityToken }
            .collectLatest { idleToken ->
                if (idleToken != null) {
                    runNotesIdleMotion(motion, options) {
                        gridState.layoutInfo.visibleItemsInfo.mapNotNull { item ->
                            (feedItems.getOrNull(item.index) as? NoteFeedItem.Entry)
                                ?.takeIf { it.key == item.key }?.note?.id
                        }
                    }
                }
            }
    }
}

@Composable
private fun NotesPreviewPrefetch(
    gridState: LazyStaggeredGridState,
    feedItems: List<NoteFeedItem>,
    attachmentsByNote: Map<Int, List<Attachment>>,
    performanceMode: String,
    enabled: Boolean
) {
    val context = LocalContext.current.applicationContext
    LaunchedEffect(gridState, feedItems, attachmentsByNote, performanceMode, enabled) {
        if (!enabled || feedItems.isEmpty() || performanceMode == "performance") return@LaunchedEffect
        val quality = performanceMode == "quality"
        snapshotFlow {
            if (gridState.isScrollInProgress) emptyList()
            else gridState.layoutInfo.visibleItemsInfo.map { it.index }.sorted()
        }.collectLatest { visibleIndices ->
            if (visibleIndices.isEmpty()) return@collectLatest
            delay(if (quality) 250L else 650L)
            val visible = visibleIndices.toSet()
            // Las tarjetas visibles solicitan su propia vista previa. Preparamos sólo las vecinas.
            val neighbors = PreviewPrefetchPolicy.noteIndices(visibleIndices, feedItems.size)
                .filterNot { it in visible }
                .mapNotNull { index -> (feedItems.getOrNull(index) as? NoteFeedItem.Entry)?.note }
            coroutineScope {
                launch {
                    val requests: List<Triple<String, String, String?>> = neighbors.flatMap { note ->
                        note.coverUri?.let { listOf(Triple(it, "image", null)) } ?: if (note.coverIcon == null) {
                            attachmentsByNote[note.id].orEmpty().take(4).filter { attachment ->
                                attachment.type == "image" || attachment.type == "video" ||
                                    (attachment.type == "file" && attachment.name?.substringAfterLast('.', "")
                                        ?.equals("pdf", ignoreCase = true) == true)
                            }.map { Triple(it.uri, it.type, it.name) }
                        } else emptyList()
                    }
                    requests.asSequence().distinctBy { it.first to it.second }
                        .take(PreviewPrefetchPolicy.MAX_ATTACHMENTS)
                        .forEach { (uri, type, name) ->
                            currentCoroutineContext().ensureActive()
                            AttachmentPreviewCache.prewarm(context, Uri.parse(uri), type, name, "instant")
                        }
                }
                launch {
                    val links = withContext(Dispatchers.Default) {
                        val urls = linkedSetOf<String>()
                        for (note in neighbors) {
                            currentCoroutineContext().ensureActive()
                            if (note.coverUri == null && note.coverIcon == null) {
                                NoteCardPreviewText.firstLink(note.content)?.let(urls::add)
                            }
                            if (urls.size >= 4) break
                        }
                        urls.toList()
                    }
                    preloadLinkPreviews(context, links, performanceMode)
                }
            }
        }
    }
}

@Composable
private fun EmptyNotesState(modifier: Modifier, hasSearch: Boolean, fontFamily: androidx.compose.ui.text.font.FontFamily,
    textColorMode: String, onCreate: () -> Unit, onClear: () -> Unit) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(Modifier.widthIn(max = 320.dp).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(if (hasSearch) Icons.Default.SearchOff else Icons.Default.NoteAdd, null,
                    Modifier.padding(24.dp).size(48.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Text(stringResource(if (hasSearch) R.string.mock_no_results else R.string.mock_no_notes),
                fontFamily = fontFamily, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            Text(stringResource(if (hasSearch) R.string.mock_try_other_search else R.string.mock_create_first_note),
                fontFamily = fontFamily, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center,
                color = softenUiColorToContrast(
                    ensureUiContrast(resolveUiTextColor(textColorMode, MaterialTheme.colorScheme.background),
                        MaterialTheme.colorScheme.background, 4.5f),
                    MaterialTheme.colorScheme.background, 4.5f, 0.58f))
            Button(onClick = { if (hasSearch) onClear() else onCreate(); UiSoundPlayer.playFallbackClick(if (hasSearch) UiActionSound.TextErase else UiActionSound.Add) }, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(if (hasSearch) R.string.design_clear_filters else R.string.design_create_first), fontFamily = fontFamily)
            }
        }
    }
}
