package com.example.mynotes.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiTextColor
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.viewmodel.NoteViewModel
import kotlinx.coroutines.delay

private enum class NoteFilter {
    ALL, FAVORITES, PINNED, PRIORITY, WORK, PERSONAL, IMAGES, FILES
}

@Immutable
private data class NoteFilterOption(val filter: NoteFilter, val labelRes: Int)

@Immutable
private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>)

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
    onOpenSettings: () -> Unit,
    onOpenNote: (Note) -> Unit,
    onEditNote: (Note) -> Unit
) {
    val allAttachments by
        noteViewModel.allAttachments.collectAsStateWithLifecycle()
    /*
     * Índice de adjuntos calculado una sola vez por emisión de Room. Además
     * guardamos los tipos por nota para que los filtros Images/Files no
     * recorran cada lista de adjuntos en cada pulsación del buscador.
     */
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
    val fontFamily = remember(settings.font) {
            appFontFamily(settings.font)
        }
    val screenPrimaryTextColor = resolveUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.background)
    val screenSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.background)
    val quickCreateSurfaceColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val quickCreateTextColor = resolveUiTextColor(value = settings.textColor, background = quickCreateSurfaceColor)
    val controlSurfaceColor = MaterialTheme.colorScheme.surfaceContainerLow
    val controlTextColor = resolveUiTextColor(value = settings.textColor, background = controlSurfaceColor)
    val controlSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = controlSurfaceColor)
    val controlGraphicColor = resolveUiGraphicColor(value = settings.textColor, background = controlSurfaceColor)
    val selectedControlTextColor = resolveUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.primaryContainer)
    // El botón flotante (+) forma parte de la interfaz principal, por lo que
    // debe respetar el mismo selector de color de texto: Auto / Black / White.
    // En Auto se calcula negro o blanco según el contraste real del FAB.
    val fabContainerColor = MaterialTheme.colorScheme.inverseSurface
    val fabContentColor = resolveUiTextColor(value = settings.textColor, background = fabContainerColor)
    val noteCardStyle = remember(settings.noteCardCornerRadius, settings.noteCardElevation, settings.noteCardPadding,
            settings.noteCardImageHeight, settings.noteCardOutlineWidth,
            settings.noteTitleMaxLines, settings.noteContentMaxLines, settings.noteLineSpacing,
            settings.iconSize, settings.showNoteDate, settings.showCategoryChip, settings.showFavoriteIcon, settings.animationsEnabled,
            settings.animationSpeed) {
            settings.toNoteCardStyle()
        }
    /*
     * Conservamos el texto escrito al girar la pantalla, pero NO el foco.
     * Android/Compose puede intentar restaurar automáticamente el último
     * campo de texto enfocado después de un cambio de orientación, lo que
     * hacía que "Buscar notas" abriera el teclado por sí solo.
     */
    var query by
        rememberSaveable {
            mutableStateOf("")
        }
    /*
     * El filtrado de una lista grande no necesita ejecutarse por cada evento
     * de teclado. 90 ms sigue sintiéndose instantáneo y evita ráfagas de CPU.
     */
    var effectiveQuery by
        remember {
            mutableStateOf("")
        }
    LaunchedEffect(query) {
        delay(90)
        effectiveQuery = query.trim().lowercase()
    }
    /*
     * Construir el índice de búsqueda implica normalizar título + contenido de
     * todas las notas. Mientras el buscador está vacío no se utiliza, así que
     * evitamos ese trabajo por completo. Cuando el usuario empieza a buscar se
     * crea una sola vez y se reutiliza al seguir escribiendo; solo se reconstruye
     * si Room entrega una lista de notas nueva o el buscador vuelve a activarse.
     *
     * Esto es especialmente útil con notas largas y no modifica el filtrado ni
     * ninguna ruta de adjuntos/miniaturas.
     */
    val searchIsActive = effectiveQuery.isNotBlank()
    val searchableTextByNote = remember(notes, searchIsActive) {
            if (!searchIsActive) {
                emptyMap()
            } else {
                notes.associate { note -> note.id to
                        buildString(note.title.length + note.content.length + 1) {
                            append(note.title.lowercase())
                            append('\n')
                            append(note.content.lowercase())
                        }
                }
            }
        }
    val focusManager = LocalFocusManager.current
    val searchFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    /*
     * Estado real del grid. Además de conservar la posición, nos permite
     * saber cuándo el usuario está desplazándose. Los trabajos pesados de
     * previews se aplazan mientras hay scroll para no competir con VSYNC,
     * medición/layout y rasterizado en dispositivos antiguos (especialmente
     * Android 9 / API 28).
     */
    val gridState = rememberLazyStaggeredGridState()
    val isGridScrolling by remember {
        derivedStateOf { gridState.isScrollInProgress }
    }
    /*
     * Las previews de enlaces siguen precargándose, pero solo cuando el grid
     * lleva un breve periodo en reposo. Si el usuario vuelve a desplazar, el
     * LaunchedEffect se cancela y el precalentamiento se reanuda después.
     * De esta forma la red, JSON, archivos y decodificación de miniaturas no
     * compiten constantemente con los frames del scroll.
     */
    val linkPreviewUrls = remember(notes) {
            notes.asSequence().flatMap { note -> extractLinkUrls(note.content).asSequence().take(3)
                }.distinct().take(80).toList()
        }
    LaunchedEffect(linkPreviewUrls, isGridScrolling, settings.performanceMode) {
        if (!isGridScrolling && linkPreviewUrls.isNotEmpty()) {
            val idleDelayMs = when (settings.performanceMode) {
                "performance" -> 900L
                "quality" -> 450L
                else -> 650L
            }
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
            /*
             * Esperamos dos frames para ejecutar el clearFocus después
             * de la restauración automática de foco de la nueva ventana.
             */
            withFrameNanos { }
            withFrameNanos { }
            focusManager.clearFocus(force = true)
            previousOrientation = currentOrientation
        }
    }
    var selectedFilter by
        remember {
            mutableStateOf(NoteFilter.ALL)
        }

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
    /*
     * Solo recalculamos el filtro cuando cambian notas, adjuntos,
     * búsqueda o filtro. Los cambios visuales ya no recorren la lista.
     */
    val visibleNotes by
        remember(notes, attachmentIndex, searchableTextByNote) {
            derivedStateOf {
                val normalizedQuery = effectiveQuery
                /*
                 * Camino caliente de la pantalla principal: cuando no hay
                 * búsqueda ni filtro devolvemos directamente la lista de Room.
                 * Antes se recorrían todas las notas y se creaba otra List aun
                 * cuando el resultado era exactamente el mismo.
                 */
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
                        /*
                         * Solo consultamos el índice de adjuntos cuando el
                         * filtro seleccionado realmente depende de él. Esto no
                         * cambia cómo se muestran/cargan previews o miniaturas.
                         */
                        NoteFilter.IMAGES -> "image" in attachmentIndex.kindsByNote[note.id].orEmpty()
                        NoteFilter.FILES -> "file" in attachmentIndex.kindsByNote[note.id].orEmpty()
                    }
                }
            }
        }
    /*
     * MODO MAXIMUM QUALITY: precarga del área desplazable completa.
     *
     * LazyVerticalStaggeredGrid, por diseño, solo compone el viewport visible
     * y un pequeño margen alrededor. Eso significa que una miniatura cuya
     * tarjeta todavía está lejos del viewport normalmente no solicita su
     * bitmap hasta que el usuario se acerca a ella. Para los perfiles
     * Performance y Balanced ese comportamiento es deliberado porque reduce
     * CPU, E/S y presión de memoria. En Maximum quality, en cambio, queremos
     * que TODO el contenido que forma parte del scrolling actual tenga sus
     * miniaturas preparadas antes de entrar en pantalla.
     *
     * Por eso construimos una lista independiente de los adjuntos que pueden
     * producir miniatura en las tarjetas principales. Se respetan las mismas
     * reglas visuales de ModernNoteCard: solo los primeros cuatro adjuntos de
     * cada nota pueden formar el mosaico superior. No tiene sentido generar
     * por adelantado una miniatura que esa tarjeta nunca mostrará.
     *
     * Importante: "precargar todo el scrolling" NO significa conservar todos
     * los bitmaps 1280 px simultáneamente en RAM. AttachmentPreviewCache usa
     * una LruCache limitada y una caché persistente en cacheDir. Así se genera
     * la variante quality para todo el contenido desplazable, pero Android
     * puede expulsar de RAM las miniaturas más lejanas y recuperarlas después
     * desde disco sin volver a decodificar el archivo original.
     */
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

    /*
     * LaunchedEffect hace que la precarga siga el contenido real del scroll.
     * Si cambia el filtro, la búsqueda, Room emite nuevos adjuntos o el usuario
     * abandona Maximum quality, Compose cancela automáticamente la corrutina
     * anterior y crea la correspondiente al nuevo conjunto.
     *
     * prewarm() reutiliza exactamente AttachmentPreviewCache, por lo que no
     * existe un segundo sistema de thumbnails. En API 28 el propio caché
     * serializa las decodificaciones pesadas a una por vez; en Android más
     * reciente permite dos. De este modo el modo quality es agresivo en
     * cobertura, pero mantiene las restricciones de concurrencia ya definidas
     * para no destruir la fluidez del render.
     */
    LaunchedEffect(qualityScrollPreviewAttachments, settings.performanceMode) {
        if (settings.performanceMode == "quality") {
            /*
             * PRECALENTAMIENTO PROGRESIVO EN DOS PASADAS.
             *
             * 1) Cubrimos primero TODO el scroll con una miniatura interna de
             *    192/256 px. Es mucho más rápida de generar y queda disponible
             *    como imagen inmediata para un fling rápido.
             * 2) Solo después recorremos de nuevo la lista y construimos la
             *    variante Maximum quality. La tarjeta sustituye la instantánea
             *    por la definitiva cuando no estamos en pleno gesto de scroll.
             *
             * El resultado visual es parecido al "progressive image loading" de
             * galerías: durante el movimiento nunca necesitamos enseñar cómo
             * aparece una imagen pesada desde cero.
             */
            qualityScrollPreviewAttachments.forEach { attachment ->
                AttachmentPreviewCache.prewarm(
                    context = context.applicationContext,
                    uri = Uri.parse(attachment.uri),
                    type = attachment.type,
                    name = attachment.name,
                    performanceMode = "instant"
                )
            }
            qualityScrollPreviewAttachments.forEach { attachment ->
                AttachmentPreviewCache.prewarm(
                    context = context.applicationContext,
                    uri = Uri.parse(attachment.uri),
                    type = attachment.type,
                    name = attachment.name,
                    performanceMode = "quality"
                )
            }
        }
    }

    var addMenuExpanded by remember { mutableStateOf(false) }
    BackHandler(enabled = addMenuExpanded) { addMenuExpanded = false }

    val motionDuration = AppMotion.duration(AppMotion.NORMAL, settings.animationsEnabled, settings.animationSpeed)
    val animatedFabSize by
        animateDpAsState(targetValue = settings.fabSize.dp, animationSpec = tween(durationMillis = motionDuration), label = "fabSize")
    val animatedProfileSize by
        animateDpAsState(targetValue = settings.profileImageSize.dp, animationSpec = tween(durationMillis = motionDuration), label =
                "profileSize")
    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled,
        animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                AnimatedVisibility(
                    visible = addMenuExpanded,
                    enter = fadeIn(animationSpec = tween(motionDuration)) +
                        expandVertically(animationSpec = tween(motionDuration), expandFrom = Alignment.Bottom),
                    exit = fadeOut(animationSpec = tween(motionDuration)) +
                        shrinkVertically(animationSpec = tween(motionDuration), shrinkTowards = Alignment.Bottom)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickCreateActionButton(
                            text = stringResource(R.string.mock_new_note),
                            icon = Icons.Default.NoteAdd,
                            fontFamily = fontFamily,
                            containerColor = quickCreateSurfaceColor,
                            contentColor = quickCreateTextColor,
                            onClick = {
                                addMenuExpanded = false
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                                onAddNote()
                            }
                        )
                        QuickCreateActionButton(
                            text = stringResource(R.string.create_drawing),
                            icon = Icons.Default.Brush,
                            fontFamily = fontFamily,
                            containerColor = quickCreateSurfaceColor,
                            contentColor = quickCreateTextColor,
                            onClick = {
                                addMenuExpanded = false
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                                onDrawNote()
                            }
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                        addMenuExpanded = !addMenuExpanded
                    },
                    modifier = Modifier.size(animatedFabSize),
                    containerColor = fabContainerColor,
                    contentColor = fabContentColor,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (addMenuExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_action_menu),
                        modifier = Modifier.size(settings.iconSize.coerceIn(18f, 36f).dp),
                        tint = fabContentColor
                    )
                }
            }
        }) {
            scaffoldPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(scaffoldPadding).padding(horizontal = 14.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            /*
             * --------------------------------------------------
             * HEADER
             * --------------------------------------------------
             */
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.mock_my_notes),
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = screenPrimaryTextColor)
                    Text(text = stringResource(R.string.mock_notes_subtitle),
                        modifier = Modifier.padding(top = 1.dp),
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = screenSecondaryTextColor)
                }
                Surface(modifier = Modifier.size(animatedProfileSize).clip(CircleShape).clickable(onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Settings)
                                    onOpenSettings()
                                }), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
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
                IconButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Settings)
                        onOpenSettings()
                    }) {
                    Icon(imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.mock_settings),
                        modifier = Modifier.size(settings.iconSize.coerceIn(18f, 36f).dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            /*
             * --------------------------------------------------
             * SEARCH
             * --------------------------------------------------
             */
            OutlinedTextField(value = query,
                onValueChange = {
                    if (it != query) {
                        UiSoundPlayer.playActionThrottled(context = context, action = UiActionSound.Search, minimumIntervalMs = 65L)
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
            /*
             * --------------------------------------------------
             * FILTER CHIPS
             * --------------------------------------------------
             */
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
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
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
            /*
             * --------------------------------------------------
             * GRID
             * --------------------------------------------------
             */
            if (visibleNotes.isEmpty()) {
                EmptyNotesState(modifier = Modifier.fillMaxSize(),
                    hasSearch = query.isNotBlank() || selectedFilter != NoteFilter.ALL,
                    fontFamily = fontFamily)
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    /*
                     * Evita tarjetas demasiado estrechas en teléfonos
                     * pequeños, split-screen y ventanas flotantes. La
                     * preferencia del usuario sigue siendo el máximo; si la
                     * ventana no tiene espacio, reducimos columnas de forma
                     * temporal sin modificar el ajuste guardado.
                     */
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
                            noteUiTextColor = settings.noteUiTextColor,
                            style = noteCardStyle,
                            optionMenuOrder = settings.optionMenuOrder,
                            optionMenuHiddenItems = settings.optionMenuHiddenItems,
                            optionMenuShowIcons = settings.optionMenuShowIcons,
                            optionMenuTextColor = settings.optionMenuTextColor,
                            optionMenuOpacity = settings.optionMenuOpacity,
                            priorityMenuHiddenItems = settings.priorityMenuHiddenItems,
                            colorMenuHiddenItems = settings.colorMenuHiddenItems,
                            performanceMode = settings.performanceMode,
                            isScrolling = isGridScrolling,
                            onOpen = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Open)
                                onOpenNote(note)
                            },
                            onEdit = {
                                UiSoundPlayer.play(context = context, sound = UiSound.Edit)
                                onEditNote(note)
                            },
                            onToggleFavorite = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Favorite)
                                noteViewModel.toggleFavorite(note)
                            },
                            onTogglePinned = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Pin)
                                noteViewModel.togglePinned(note)
                            },
                            onPriorityChange = {
                                    priority ->
                                UiSoundPlayer.play(context = context, sound = UiSound.Priority)
                                noteViewModel.changePriority(note = note, priority = priority)
                            },
                            onColorChange = {
                                    color ->
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                                noteViewModel.changeNoteColor(note = note, color = color)
                            },
                            onCategoryChange = {
                                    category ->
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Category)
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
                        /*
                         * El grid termina en el borde derecho de este contenedor.
                         * La pantalla ya tiene 14 dp de margen exterior, por lo que
                         * desplazamos la cápsula 9 dp hacia ese margen. De esta
                         * forma la barra queda completamente FUERA del área del
                         * LazyVerticalStaggeredGrid y se dibuja sobre el fondo de
                         * la pantalla, sin cubrir tarjetas ni reducir su anchura.
                         *
                         * Con el track interno de 10 dp y la cápsula visual de 4 dp,
                         * este desplazamiento deja la barra centrada dentro del
                         * margen exterior en lugar de pegada al contenido o al
                         * borde físico del dispositivo.
                         */
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
private fun QuickCreateActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(46.dp)
            .clickable(onClick = onClick),
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EmptyNotesState(modifier: Modifier, hasSearch: Boolean, fontFamily:
        androidx.compose.ui.text.font.FontFamily) {
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
                color = resolveSecondaryUiTextColor(value = "auto", background = MaterialTheme.colorScheme.background))
        }
    }
}
