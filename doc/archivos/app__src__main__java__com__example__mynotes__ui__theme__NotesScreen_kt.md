# NotesScreen.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/NotesScreen.kt`  
**SHA-256 actual del archivo, sin modificar:** `41af1f32f96765fd67378df855de90a802e31cf10c5e22857efcb75bf938abd3`  
**Líneas del código real:** 627  
**Estado respecto de la documentación anterior:** **archivo modificado desde la instantánea anterior**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

Pantalla principal: búsqueda, filtros, grid escalonado, FAB y composición de NoteCard.

**Cambios recientes cubiertos por esta revisión.** Los cambios recientes optimizan el scroll: detectan isScrollInProgress, aplazan trabajo pesado en perfiles normales y, en máxima calidad, precalientan toda la ventana desplazable con una miniatura instantánea más una miniatura final de alta calidad.

## 2. Package e imports

El package declarado es `com.example.mynotes.ui`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **85 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`android.content.res.Configuration`, `androidx.compose.foundation.clickable`, `android.net.Uri`, `androidx.compose.animation.core.animateDpAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.lazy.LazyRow`, `androidx.compose.foundation.lazy.items`, `androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid`, `androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells`, `androidx.compose.foundation.lazy.staggeredgrid.items`, `androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Add`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material.icons.filled.Search`, `androidx.compose.material.icons.filled.Settings`, `androidx.compose.material3.FilterChip`, `androidx.compose.material3.FilterChipDefaults`, `androidx.compose.material3.FloatingActionButton`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.OutlinedTextField`, `androidx.compose.material3.OutlinedTextFieldDefaults`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.Immutable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.derivedStateOf`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableIntStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.runtime.saveable.rememberSaveable`, `androidx.compose.runtime.withFrameNanos`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalConfiguration`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.platform.LocalFocusManager`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`

### Terceros / otros

`coil3.compose.AsyncImage`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.ModernNoteCard`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.components.preloadLinkPreviews`, `com.example.mynotes.ui.components.toNoteCardStyle`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.viewmodel.NoteViewModel`

### Kotlin Coroutines / extensiones

`kotlinx.coroutines.delay`

## 3. Restricciones, límites e invariantes detectables

- **Llamada segura `?.`: 2 aparición/apariciones.** evita desreferenciar receptores nulos; si el receptor es `null`, la cadena se corta de forma segura.
- **Acotaciones `coerceIn/AtLeast/AtMost`: 5 aparición/apariciones.** imponen límites numéricos para evitar valores fuera del rango aceptado.
- **Límites visuales: 1 aparición/apariciones.** evitan crecimiento o reducción de UI fuera de los límites previstos.
- **Estado Compose: 25 aparición/apariciones.** introduce estado observado por Compose y, por tanto, puntos potenciales de recomposición.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `NoteFilter` — class, líneas 89–91

```kotlin
private enum class NoteFilter {
    ALL, FAVORITES, WORK, PERSONAL, IMAGES, FILES
}
```

**Firma/entrada.** `private enum class NoteFilter {`

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.2 `NoteFilterOption` — class, líneas 94–593

```kotlin
private data class NoteFilterOption(val filter: NoteFilter, val labelRes: Int)

@Immutable
private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>)

private val FilterOptions = listOf(NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all), NoteFilterOption(NoteFilter.FAVORITES,
            R.string.mock_favorites), NoteFilterOption(NoteFilter.WORK, R.string.mock_work), NoteFilterOption(NoteFilter.PERSONAL,
            R.string.mock_personal), NoteFilterOption(NoteFilter.IMAGES, R.string.mock_images), NoteFilterOption(NoteFilter.FILES,
            R.string.mock_files))
@Composable
fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit, onOpenSettings: () -> Unit,
    onOpenNote: (Note) -> Unit, onEditNote: (Note) -> Unit) {
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
    val screenSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.background
        )
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
            settings.noteCardImageHeight, settings.noteTitleMaxLines, settings.noteContentMaxLines, settings.noteLineSpacing,
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
    val searchableTextByNote = remember(notes) {
            notes.associate { note -> note.id to
                    buildString(note.title.length + note.content.length + 1) {
                        append(note.title.lowercase())
                        append('\n')
                        append(note.content.lowercase())
                    }
            }
        }
    val focusManager = LocalFocusManager.current
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
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val currentOrientation = LocalConfiguration.current.orientation
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
    /*
     * Solo recalculamos el filtro cuando cambian notas, adjuntos,
     * búsqueda o filtro. Los cambios visuales ya no recorren la lista.
     */
    val visibleNotes by
        remember(notes, attachmentIndex, searchableTextByNote) {
            derivedStateOf {
                val normalizedQuery = effectiveQuery
                notes.filter {
                        note ->
                    val attachmentKinds = attachmentIndex.kindsByNote[note.id].orEmpty()
                    val matchesText = normalizedQuery.isBlank() || searchableTextByNote[note.id].orEmpty().contains(normalizedQuery)
                    val matchesFilter = when (selectedFilter) {
                            NoteFilter.ALL -> true
                            NoteFilter.FAVORITES -> note.isFavorite
                            NoteFilter.WORK -> note.category == "work"
                            NoteFilter.PERSONAL -> note.category == "personal"
                            NoteFilter.IMAGES -> "image" in attachmentKinds
                            NoteFilter.FILES -> "file" in attachmentKinds
                        }
                    matchesText && matchesFilter
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
            FloatingActionButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                    onAddNote()
                },
                modifier = Modifier.size(animatedFabSize),
                containerColor = fabContainerColor,
                contentColor = fabContentColor,
                shape = CircleShape) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.mock_new_note),
                    modifier = Modifier.size(settings.iconSize.coerceIn(18f, 36f).dp),
                    tint = fabContentColor)
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
                        color = MaterialTheme.colorScheme.onBackground)
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
                modifier = Modifier.fillMaxWidth(),
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
                    contentPadding = PaddingValues(bottom = 100.dp)) {
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
                }
            }
        }
    }
    }
}
```

**Firma/entrada.** `private data class NoteFilterOption(val filter: NoteFilter, val labelRes: Int) @Immutable private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>) private val FilterOptions = listOf(NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all), NoteFilterOption(NoteFilter.FAVORITES, R.string.mock_favorites), NoteFilterOption(NoteFilter.WORK, R.string.mock_work), NoteFilterOption(NoteFilter.PERSONAL, R.string.mock_personal), NoteFilterOption(NoteFilter.IMAGES, R.string.mock_images), NoteFilterOption(NoteFilter.FILES, R.string.mock_files)) @Composable fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit, onOpenSettings: () -> Unit, onOpenNote: (Note) -> Unit, onEditNote: (Note) -> `

**Parámetros.**
- `val filter: NoteFilter` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val labelRes: Int) @Immutable private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>) private val FilterOptions = listOf(NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all), NoteFilterOption(NoteFilter.FAVORITES, R.string.mock_favorites), NoteFilterOption(NoteFilter.WORK, R.string.mock_work), NoteFilterOption(NoteFilter.PERSONAL, R.string.mock_personal), NoteFilterOption(NoteFilter.IMAGES, R.string.mock_images), NoteFilterOption(NoteFilter.FILES, R.string.mock_files)) @Composable fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca. Tiene valor por defecto y puede omitirse en la llamada.
- `onOpenSettings: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onOpenNote: (Note) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onEditNote: (Note) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; accede o prepara almacenamiento local/caché; integra el pipeline de miniaturas y caché de adjuntos; ramifica o parametriza comportamiento según el perfil de rendimiento; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.3 `AttachmentIndex` — class, líneas 97–593

```kotlin
private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>)

private val FilterOptions = listOf(NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all), NoteFilterOption(NoteFilter.FAVORITES,
            R.string.mock_favorites), NoteFilterOption(NoteFilter.WORK, R.string.mock_work), NoteFilterOption(NoteFilter.PERSONAL,
            R.string.mock_personal), NoteFilterOption(NoteFilter.IMAGES, R.string.mock_images), NoteFilterOption(NoteFilter.FILES,
            R.string.mock_files))
@Composable
fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit, onOpenSettings: () -> Unit,
    onOpenNote: (Note) -> Unit, onEditNote: (Note) -> Unit) {
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
    val screenSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.background
        )
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
            settings.noteCardImageHeight, settings.noteTitleMaxLines, settings.noteContentMaxLines, settings.noteLineSpacing,
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
    val searchableTextByNote = remember(notes) {
            notes.associate { note -> note.id to
                    buildString(note.title.length + note.content.length + 1) {
                        append(note.title.lowercase())
                        append('\n')
                        append(note.content.lowercase())
                    }
            }
        }
    val focusManager = LocalFocusManager.current
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
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val currentOrientation = LocalConfiguration.current.orientation
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
    /*
     * Solo recalculamos el filtro cuando cambian notas, adjuntos,
     * búsqueda o filtro. Los cambios visuales ya no recorren la lista.
     */
    val visibleNotes by
        remember(notes, attachmentIndex, searchableTextByNote) {
            derivedStateOf {
                val normalizedQuery = effectiveQuery
                notes.filter {
                        note ->
                    val attachmentKinds = attachmentIndex.kindsByNote[note.id].orEmpty()
                    val matchesText = normalizedQuery.isBlank() || searchableTextByNote[note.id].orEmpty().contains(normalizedQuery)
                    val matchesFilter = when (selectedFilter) {
                            NoteFilter.ALL -> true
                            NoteFilter.FAVORITES -> note.isFavorite
                            NoteFilter.WORK -> note.category == "work"
                            NoteFilter.PERSONAL -> note.category == "personal"
                            NoteFilter.IMAGES -> "image" in attachmentKinds
                            NoteFilter.FILES -> "file" in attachmentKinds
                        }
                    matchesText && matchesFilter
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
            FloatingActionButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                    onAddNote()
                },
                modifier = Modifier.size(animatedFabSize),
                containerColor = fabContainerColor,
                contentColor = fabContentColor,
                shape = CircleShape) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.mock_new_note),
                    modifier = Modifier.size(settings.iconSize.coerceIn(18f, 36f).dp),
                    tint = fabContentColor)
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
                        color = MaterialTheme.colorScheme.onBackground)
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
                modifier = Modifier.fillMaxWidth(),
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
                    contentPadding = PaddingValues(bottom = 100.dp)) {
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
                }
            }
        }
    }
    }
}
```

**Firma/entrada.** `private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>) private val FilterOptions = listOf(NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all), NoteFilterOption(NoteFilter.FAVORITES, R.string.mock_favorites), NoteFilterOption(NoteFilter.WORK, R.string.mock_work), NoteFilterOption(NoteFilter.PERSONAL, R.string.mock_personal), NoteFilterOption(NoteFilter.IMAGES, R.string.mock_images), NoteFilterOption(NoteFilter.FILES, R.string.mock_files)) @Composable fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit, onOpenSettings: () -> Unit, onOpenNote: (Note) -> Unit, onEditNote: (Note) -> Unit) {`

**Parámetros.**
- `val byNote: Map<Int, List<Attachment>>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val kindsByNote: Map<Int, Set<String>>) private val FilterOptions = listOf(NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all), NoteFilterOption(NoteFilter.FAVORITES, R.string.mock_favorites), NoteFilterOption(NoteFilter.WORK, R.string.mock_work), NoteFilterOption(NoteFilter.PERSONAL, R.string.mock_personal), NoteFilterOption(NoteFilter.IMAGES, R.string.mock_images), NoteFilterOption(NoteFilter.FILES, R.string.mock_files)) @Composable fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca. Tiene valor por defecto y puede omitirse en la llamada.
- `onOpenSettings: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onOpenNote: (Note) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onEditNote: (Note) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; accede o prepara almacenamiento local/caché; integra el pipeline de miniaturas y caché de adjuntos; ramifica o parametriza comportamiento según el perfil de rendimiento; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.4 `NotesScreen` — fun, líneas 104–593

```kotlin
fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit, onOpenSettings: () -> Unit,
    onOpenNote: (Note) -> Unit, onEditNote: (Note) -> Unit) {
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
    val screenSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.background
        )
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
            settings.noteCardImageHeight, settings.noteTitleMaxLines, settings.noteContentMaxLines, settings.noteLineSpacing,
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
    val searchableTextByNote = remember(notes) {
            notes.associate { note -> note.id to
                    buildString(note.title.length + note.content.length + 1) {
                        append(note.title.lowercase())
                        append('\n')
                        append(note.content.lowercase())
                    }
            }
        }
    val focusManager = LocalFocusManager.current
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
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val currentOrientation = LocalConfiguration.current.orientation
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
    /*
     * Solo recalculamos el filtro cuando cambian notas, adjuntos,
     * búsqueda o filtro. Los cambios visuales ya no recorren la lista.
     */
    val visibleNotes by
        remember(notes, attachmentIndex, searchableTextByNote) {
            derivedStateOf {
                val normalizedQuery = effectiveQuery
                notes.filter {
                        note ->
                    val attachmentKinds = attachmentIndex.kindsByNote[note.id].orEmpty()
                    val matchesText = normalizedQuery.isBlank() || searchableTextByNote[note.id].orEmpty().contains(normalizedQuery)
                    val matchesFilter = when (selectedFilter) {
                            NoteFilter.ALL -> true
                            NoteFilter.FAVORITES -> note.isFavorite
                            NoteFilter.WORK -> note.category == "work"
                            NoteFilter.PERSONAL -> note.category == "personal"
                            NoteFilter.IMAGES -> "image" in attachmentKinds
                            NoteFilter.FILES -> "file" in attachmentKinds
                        }
                    matchesText && matchesFilter
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
            FloatingActionButton(onClick = {
                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                    onAddNote()
                },
                modifier = Modifier.size(animatedFabSize),
                containerColor = fabContainerColor,
                contentColor = fabContentColor,
                shape = CircleShape) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.mock_new_note),
                    modifier = Modifier.size(settings.iconSize.coerceIn(18f, 36f).dp),
                    tint = fabContentColor)
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
                        color = MaterialTheme.colorScheme.onBackground)
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
                modifier = Modifier.fillMaxWidth(),
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
                    contentPadding = PaddingValues(bottom = 100.dp)) {
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
                }
            }
        }
    }
    }
}
```

**Firma/entrada.** `fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit, onOpenSettings: () -> Unit, onOpenNote: (Note) -> Unit, onEditNote: (Note) -> Unit) {`

**Parámetros.**
- `notes: List<Note>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `noteViewModel: NoteViewModel` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `settings: AppSettings` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onAddNote: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onOpenSettings: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onOpenNote: (Note) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onEditNote: (Note) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; accede o prepara almacenamiento local/caché; integra el pipeline de miniaturas y caché de adjuntos; ramifica o parametriza comportamiento según el perfil de rendimiento; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.5 `EmptyNotesState` — fun, líneas 596–627

```kotlin
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
```

**Firma/entrada.** `private fun EmptyNotesState(modifier: Modifier, hasSearch: Boolean, fontFamily: androidx.compose.ui.text.font.FontFamily) {`

**Parámetros.**
- `modifier: Modifier` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `hasSearch: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

## 5. Variables y propiedades, una por una

Se detectaron **42 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 99 | `FilterOptions` | `private val FilterOptions: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val FilterOptions = listOf(NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all), NoteFilterOption(NoteFilter.FAVORITES,` |
| 106 | `allAttachments` | `local/pública por contexto val allAttachments: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val allAttachments by` |
| 113 | `attachmentIndex` | `local/pública por contexto val attachmentIndex: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val attachmentIndex = remember(allAttachments) {` |
| 114 | `byNote` | `local/pública por contexto val byNote: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val byNote = allAttachments.groupBy {` |
| 117 | `kindsByNote` | `local/pública por contexto val kindsByNote: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val kindsByNote = buildMap<Int, Set<String>> {` |
| 123 | `attachmentsByNote` | `local/pública por contexto val attachmentsByNote: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val attachmentsByNote = attachmentIndex.byNote` |
| 124 | `fontFamily` | `local/pública por contexto val fontFamily: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val fontFamily = remember(settings.font) {` |
| 127 | `screenSecondaryTextColor` | `local/pública por contexto val screenSecondaryTextColor: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val screenSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.background` |
| 129 | `controlSurfaceColor` | `local/pública por contexto val controlSurfaceColor: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val controlSurfaceColor = MaterialTheme.colorScheme.surfaceContainerLow` |
| 130 | `controlTextColor` | `local/pública por contexto val controlTextColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val controlTextColor = resolveUiTextColor(value = settings.textColor, background = controlSurfaceColor)` |
| 131 | `controlSecondaryTextColor` | `local/pública por contexto val controlSecondaryTextColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val controlSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.textColor, background = controlSurfaceColor)` |
| 132 | `controlGraphicColor` | `local/pública por contexto val controlGraphicColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val controlGraphicColor = resolveUiGraphicColor(value = settings.textColor, background = controlSurfaceColor)` |
| 133 | `selectedControlTextColor` | `local/pública por contexto val selectedControlTextColor: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val selectedControlTextColor = resolveUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.primaryContainer)` |
| 137 | `fabContainerColor` | `local/pública por contexto val fabContainerColor: inferido` | `val` fija la referencia después de inicializarla; deriva su valor del tema activo, por lo que cambia con paleta/modo. **Inicialización visible:** `val fabContainerColor = MaterialTheme.colorScheme.inverseSurface` |
| 138 | `fabContentColor` | `local/pública por contexto val fabContentColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val fabContentColor = resolveUiTextColor(value = settings.textColor, background = fabContainerColor)` |
| 139 | `noteCardStyle` | `local/pública por contexto val noteCardStyle: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val noteCardStyle = remember(settings.noteCardCornerRadius, settings.noteCardElevation, settings.noteCardPadding,` |
| 151 | `query` | `local/pública por contexto var query: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var query by` |
| 159 | `effectiveQuery` | `local/pública por contexto var effectiveQuery: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var effectiveQuery by` |
| 167 | `searchableTextByNote` | `local/pública por contexto val searchableTextByNote: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val searchableTextByNote = remember(notes) {` |
| 176 | `focusManager` | `local/pública por contexto val focusManager: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val focusManager = LocalFocusManager.current` |
| 177 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 185 | `gridState` | `local/pública por contexto val gridState: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val gridState = rememberLazyStaggeredGridState()` |
| 186 | `isGridScrolling` | `local/pública por contexto val isGridScrolling: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val isGridScrolling by remember {` |
| 196 | `linkPreviewUrls` | `local/pública por contexto val linkPreviewUrls: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val linkPreviewUrls = remember(notes) {` |
| 202 | `idleDelayMs` | `local/pública por contexto val idleDelayMs: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val idleDelayMs = when (settings.performanceMode) {` |
| 213 | `isLandscape` | `local/pública por contexto val isLandscape: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE` |
| 214 | `currentOrientation` | `local/pública por contexto val currentOrientation: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val currentOrientation = LocalConfiguration.current.orientation` |
| 215 | `previousOrientation` | `local/pública por contexto var previousOrientation: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var previousOrientation by` |
| 231 | `selectedFilter` | `local/pública por contexto var selectedFilter: inferido` | `var` permite sustituir el valor durante la vida del ámbito; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `var selectedFilter by` |
| 239 | `visibleNotes` | `local/pública por contexto val visibleNotes: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val visibleNotes by` |
| 242 | `normalizedQuery` | `local/pública por contexto val normalizedQuery: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val normalizedQuery = effectiveQuery` |
| 245 | `attachmentKinds` | `local/pública por contexto val attachmentKinds: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val attachmentKinds = attachmentIndex.kindsByNote[note.id].orEmpty()` |
| 246 | `matchesText` | `local/pública por contexto val matchesText: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val matchesText = normalizedQuery.isBlank() \|\| searchableTextByNote[note.id].orEmpty().contains(normalizedQuery)` |
| 247 | `matchesFilter` | `local/pública por contexto val matchesFilter: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val matchesFilter = when (selectedFilter) {` |
| 284 | `qualityScrollPreviewAttachments` | `local/pública por contexto val qualityScrollPreviewAttachments: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val qualityScrollPreviewAttachments = remember(visibleNotes, attachmentIndex, settings.performanceMode) {` |
| 350 | `motionDuration` | `local/pública por contexto val motionDuration: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val motionDuration = AppMotion.duration(AppMotion.NORMAL, settings.animationsEnabled, settings.animationSpeed)` |
| 351 | `animatedFabSize` | `local/pública por contexto val animatedFabSize: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val animatedFabSize by` |
| 353 | `animatedProfileSize` | `local/pública por contexto val animatedProfileSize: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val animatedProfileSize by` |
| 473 | `selected` | `local/pública por contexto val selected: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val selected = option.filter == selectedFilter` |
| 519 | `maximumColumnsForWidth` | `local/pública por contexto val maximumColumnsForWidth: inferido` | `val` fija la referencia después de inicializarla; su inicialización está acotada a un intervalo explícito con `coerceIn`. **Inicialización visible:** `val maximumColumnsForWidth = (maxWidth.value / 145f).toInt().coerceIn(1, 3)` |
| 520 | `effectiveColumns` | `local/pública por contexto val effectiveColumns: inferido` | `val` fija la referencia después de inicializarla; su inicialización está acotada a un intervalo explícito con `coerceIn`. **Inicialización visible:** `val effectiveColumns = minOf(settings.gridColumns.coerceIn(1, 3), maximumColumnsForWidth)` |
| 535 | `noteAttachments` | `local/pública por contexto val noteAttachments: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val noteAttachments = attachmentsByNote[note.id].orEmpty()` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **68 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 89–91 | 0 | ámbito/lambda anónima | `private enum class NoteFilter {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 105–593 | 0 | ámbito/lambda anónima | `onOpenNote: (Note) -> Unit, onEditNote: (Note) -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 113–122 | 1 | `remember` / memoria de composición | `val attachmentIndex = remember(allAttachments) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 114–116 | 2 | ámbito/lambda anónima | `val byNote = allAttachments.groupBy {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 117–120 | 2 | ámbito/lambda anónima | `val kindsByNote = buildMap<Int, Set<String>> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 118–118 | 4 | iteración funcional | `byNote.forEach { (noteId, items) -> put(noteId, items.asSequence().map { it.type }.toSet())` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 118–119 | 3 | iteración funcional | `byNote.forEach { (noteId, items) -> put(noteId, items.asSequence().map { it.type }.toSet())` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 124–126 | 1 | `remember` / memoria de composición | `val fontFamily = remember(settings.font) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 142–144 | 1 | ámbito/lambda anónima | `settings.animationSpeed) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 152–154 | 1 | `remember` / memoria de composición | `rememberSaveable {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 160–162 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 163–166 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(query) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 167–175 | 1 | `remember` / memoria de composición | `val searchableTextByNote = remember(notes) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 168–174 | 2 | ámbito/lambda anónima | `notes.associate { note -> note.id to` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 169–173 | 3 | ámbito/lambda anónima | `buildString(note.title.length + note.content.length + 1) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 186–188 | 1 | `remember` / memoria de composición | `val isGridScrolling by remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 187–187 | 2 | ámbito/lambda anónima | `derivedStateOf { gridState.isScrollInProgress }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 196–199 | 1 | `remember` / memoria de composición | `val linkPreviewUrls = remember(notes) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 197–198 | 2 | ámbito/lambda anónima | `notes.asSequence().flatMap { note -> extractLinkUrls(note.content).asSequence().take(3)` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 200–212 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(linkPreviewUrls, isGridScrolling, settings.performanceMode) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 201–211 | 2 | condición `if` | `if (!isGridScrolling && linkPreviewUrls.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 202–206 | 3 | selección `when` | `val idleDelayMs = when (settings.performanceMode) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 208–210 | 3 | condición `if` | `if (!gridState.isScrollInProgress) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 216–218 | 1 | `remember` / memoria de composición | `rememberSaveable {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 219–230 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(currentOrientation) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 220–229 | 2 | condición `if` | `if (previousOrientation != currentOrientation) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 225–225 | 3 | ámbito/lambda anónima | `withFrameNanos { }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 226–226 | 3 | ámbito/lambda anónima | `withFrameNanos { }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 232–234 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 240–258 | 1 | `remember` / memoria de composición | `remember(notes, attachmentIndex, searchableTextByNote) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 241–257 | 2 | ámbito/lambda anónima | `derivedStateOf {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 243–256 | 3 | ámbito/lambda anónima | `notes.filter {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 247–254 | 4 | selección `when` | `val matchesFilter = when (selectedFilter) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 284–298 | 1 | `remember` / memoria de composición | `val qualityScrollPreviewAttachments = remember(visibleNotes, attachmentIndex, settings.performanceMode) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 285–287 | 2 | condición `if` | `if (settings.performanceMode != "quality") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 287–297 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 289–289 | 3 | ámbito/lambda anónima | `.flatMap { note -> attachmentsByNote[note.id].orEmpty().take(4).asSequence() }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 290–294 | 3 | ámbito/lambda anónima | `.filter { attachment ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 295–295 | 3 | ámbito/lambda anónima | `.distinctBy { attachment -> attachment.id to attachment.uri }` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 313–348 | 1 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(qualityScrollPreviewAttachments, settings.performanceMode) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 314–347 | 2 | condición `if` | `if (settings.performanceMode == "quality") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 329–337 | 3 | iteración funcional | `qualityScrollPreviewAttachments.forEach { attachment ->` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 338–346 | 3 | iteración funcional | `qualityScrollPreviewAttachments.forEach { attachment ->` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 357–592 | 1 | ámbito/lambda anónima | `animationSpeed = settings.animationSpeed) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 359–373 | 2 | ámbito/lambda anónima | `floatingActionButton = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 360–363 | 3 | ámbito/lambda anónima | `FloatingActionButton(onClick = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 367–372 | 3 | ámbito/lambda anónima | `shape = CircleShape) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 373–591 | 2 | ámbito/lambda anónima | `}) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 375–590 | 3 | bloque UI Compose | `Column(modifier = Modifier.fillMaxSize().padding(scaffoldPadding).padding(horizontal = 14.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 384–395 | 5 | bloque UI Compose | `Column(modifier = Modifier.weight(1f)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 396–399 | 5 | bloque UI Compose | `Surface(modifier = Modifier.size(animatedProfileSize).clip(CircleShape).clickable(onClick = {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 400–403 | 6 | condición `if` | `if (settings.profileImageUri.isNotBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 404–407 | 7 | bloque UI Compose | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 427–429 | 5 | condición `if` | `if (it != query) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 462–464 | 4 | condición `if` | `horizontalArrangement = if (isLandscape) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 467–499 | 4 | condición `if` | `contentPadding = PaddingValues(start = if (isLandscape) 12.dp else 0.dp, end = 12.dp)) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 487–489 | 7 | condición `if` | `color = if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 492–494 | 7 | condición `if` | `fontWeight = if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 506–510 | 4 | condición `if` | `if (visibleNotes.isEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 597–627 | 0 | ámbito/lambda anónima | `androidx.compose.ui.text.font.FontFamily) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 599–626 | 1 | ámbito/lambda anónima | `contentAlignment = Alignment.Center) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 600–625 | 2 | bloque UI Compose | `Column(horizontalAlignment = Alignment.CenterHorizontally) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 601–603 | 3 | condición `if` | `Text(text = if (hasSearch) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 603–605 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 608–610 | 3 | condición `if` | `Text(text = stringResource(if (hasSearch) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 610–612 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 616–618 | 3 | condición `if` | `Text(text = stringResource(if (hasSearch) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 618–620 | 3 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |

## 7. Side effects, rendimiento y lifecycle

- **Persistencia / base de datos:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Sistema de archivos / caché:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Audio / vibración:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Corrutinas:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Recomposición Compose:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Navegación / Intents:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Decodificación multimedia:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

En perfiles performance/balanced el estado `isScrollInProgress` actúa como señal para aplazar trabajo pesado. En **quality** el objetivo cambia: se prepara el conjunto desplazable con previews instantáneos y finales para que un fling rápido no revele placeholders entrando al viewport.

La técnica evita exigir que todos los bitmaps grandes permanezcan a la vez en RAM; el cache de disco permite tener preparada la representación sin convertir la pantalla principal en un consumidor de memoria ilimitado.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**
