# NotesScreen.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/NotesScreen.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `a5fb3a72a2c9895577c4356bf8929b9aff83bfac449878e56dc839023cf691de`  
**Líneas del código real:** 510

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Pantalla principal Compose con encabezado, búsqueda, filtros/categorías, cuadrícula de notas y botón flotante de creación.

**Arquitectura.** Orquesta la presentación de colecciones de NoteCard y transforma eventos de usuario en callbacks hacia MainActivity/NoteViewModel.

**Flujo general.** Flujo típico: recibe la lista ya observable -> aplica el estado de búsqueda/filtros/orden configurado -> construye la cuadrícula -> cada NoteCard envía acciones mediante callbacks -> el nivel superior modifica datos y la lista se recompone.

## 2. Package e imports

El `package` es `com.example.mynotes.ui`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **83 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.res.Configuration`, `android.net.Uri`.

**Jetpack/Compose:** `androidx.compose.foundation.clickable`, `androidx.compose.animation.core.animateDpAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.lazy.LazyRow`, `androidx.compose.foundation.lazy.items`, `androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid`, `androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells`, `androidx.compose.foundation.lazy.staggeredgrid.items`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Add`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material.icons.filled.Search`, `androidx.compose.material.icons.filled.Settings`, `androidx.compose.material3.FilterChip`, `androidx.compose.material3.FilterChipDefaults`, `androidx.compose.material3.FloatingActionButton`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.OutlinedTextField`, `androidx.compose.material3.OutlinedTextFieldDefaults`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.Immutable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.derivedStateOf`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableIntStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.runtime.saveable.rememberSaveable`, `androidx.compose.runtime.withFrameNanos`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalConfiguration`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.platform.LocalFocusManager`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`.

**Proyecto MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.ModernNoteCard`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.components.preloadLinkPreviews`, `com.example.mynotes.ui.components.toNoteCardStyle`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.viewmodel.NoteViewModel`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.delay`.

**Otras librerías:** `coil3.compose.AsyncImage`.

## 3. Restricciones e invariantes visibles en el archivo

- **Límite numérico (5 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Normalización vacía (3 aparición/apariciones):** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior.

## 4. Bloques de código, uno por uno

### 4.1 `NoteFilter` — class, líneas 87–89

```kotlin
private enum class NoteFilter {
    ALL, FAVORITES, WORK, PERSONAL, IMAGES, FILES
}
```

#### Qué hace y por qué existe

Pantalla principal Compose con encabezado, búsqueda, filtros/categorías, cuadrícula de notas y botón flotante de creación.

#### Contrato de la declaración

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `NotesScreen` — fun, líneas 92–94

```kotlin
private data class NoteFilterOption(val filter: NoteFilter, val labelRes: Int)

@Immutable
```

#### Qué hace y por qué existe

Pantalla principal Compose con encabezado, búsqueda, filtros/categorías, cuadrícula de notas y botón flotante de creación.

#### Contrato de la declaración

**Parámetros:**

- `val filter: NoteFilter` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val labelRes: Int` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `NotesScreen` — fun, líneas 95–101

```kotlin
private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>)

private val FilterOptions = listOf(NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all), NoteFilterOption(NoteFilter.FAVORITES,
            R.string.mock_favorites), NoteFilterOption(NoteFilter.WORK, R.string.mock_work), NoteFilterOption(NoteFilter.PERSONAL,
            R.string.mock_personal), NoteFilterOption(NoteFilter.IMAGES, R.string.mock_images), NoteFilterOption(NoteFilter.FILES,
            R.string.mock_files))
@Composable
```

#### Qué hace y por qué existe

Pantalla principal Compose con encabezado, búsqueda, filtros/categorías, cuadrícula de notas y botón flotante de creación.

#### Contrato de la declaración

**Parámetros:**

- `val byNote: Map<Int, List<Attachment>>` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.
- `val kindsByNote: Map<Int, Set<String>>` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 97 | `val FilterOptions` | `inferido` | `listOf(NoteFilterOption(NoteFilter.ALL, R.string.mock_filter_all), NoteFilterOption(NoteFilter.FAVOR…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Semántica Compose/lifecycle

- **`@Composable`:** La función describe UI declarativa y puede ejecutarse nuevamente por recomposición; no debe interpretarse como una ejecución única imperativa.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `NoteFilterOption`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `NotesScreen` — fun, líneas 102–476

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
     * Resolvemos las previews antes de que las tarjetas entren al viewport.
     * Así el LazyVerticalStaggeredGrid no dispara peticiones de red mientras
     * el usuario está desplazándose. El repositorio además persiste título,
     * descripción y miniatura para los siguientes arranques de la app.
     */
    val linkPreviewUrls = remember(notes) {
            notes.asSequence().flatMap { note -> extractLinkUrls(note.content).asSequence().take(3)
                }.distinct().take(80).toList()
        }
    LaunchedEffect(linkPreviewUrls) {
        preloadLinkPreviews(context = context.applicationContext, urls = linkPreviewUrls)
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

#### Qué hace y por qué existe

Componente de estado/lógica de presentación que coordina datos y operaciones para la UI sin depender de una instancia visual concreta.

#### Contrato de la declaración

**Parámetros:**

- `notes: List<Note>` — `notes` recibe un valor de tipo `List<Note>`. El contrato no marca este parámetro como anulable.
- `noteViewModel: NoteViewModel` — `noteViewModel` recibe un valor de tipo `NoteViewModel`. El contrato no marca este parámetro como anulable.
- `settings: AppSettings` — `settings` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.
- `onAddNote: () -> Unit` — `onAddNote` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onOpenSettings: () -> Unit` — `onOpenSettings` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onOpenNote: (Note) -> Unit` — `onOpenNote` recibe un valor de tipo `(Note) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onEditNote: (Note) -> Unit` — `onEditNote` recibe un valor de tipo `(Note) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 111 | `val attachmentIndex` | `inferido` | `remember(allAttachments) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 112 | `val byNote` | `inferido` | `allAttachments.groupBy {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 115 | `val kindsByNote` | `inferido` | `buildMap<Int, Set<String>> {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 121 | `val attachmentsByNote` | `inferido` | `attachmentIndex.byNote` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 122 | `val fontFamily` | `inferido` | `remember(settings.font) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 125 | `val screenSecondaryTextColor` | `inferido` | `resolveSecondaryUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.backg…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 127 | `val controlSurfaceColor` | `inferido` | `MaterialTheme.colorScheme.surfaceContainerLow` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `val controlTextColor` | `inferido` | `resolveUiTextColor(value = settings.textColor, background = controlSurfaceColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 129 | `val controlSecondaryTextColor` | `inferido` | `resolveSecondaryUiTextColor(value = settings.textColor, background = controlSurfaceColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `val controlGraphicColor` | `inferido` | `resolveUiGraphicColor(value = settings.textColor, background = controlSurfaceColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 131 | `val selectedControlTextColor` | `inferido` | `resolveUiTextColor(value = settings.textColor, background = MaterialTheme.colorScheme.primaryContain…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 135 | `val fabContainerColor` | `inferido` | `MaterialTheme.colorScheme.inverseSurface` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 136 | `val fabContentColor` | `inferido` | `resolveUiTextColor(value = settings.textColor, background = fabContainerColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 137 | `val noteCardStyle` | `inferido` | `remember(settings.noteCardCornerRadius, settings.noteCardElevation, settings.noteCardPadding,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 165 | `val searchableTextByNote` | `inferido` | `remember(notes) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 174 | `val focusManager` | `inferido` | `LocalFocusManager.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 175 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 182 | `val linkPreviewUrls` | `inferido` | `remember(notes) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 189 | `val isLandscape` | `inferido` | `LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 190 | `val currentOrientation` | `inferido` | `LocalConfiguration.current.orientation` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `val normalizedQuery` | `inferido` | `effectiveQuery` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 221 | `val attachmentKinds` | `inferido` | `attachmentIndex.kindsByNote[note.id].orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 222 | `val matchesText` | `inferido` | `normalizedQuery.isBlank() \|\| searchableTextByNote[note.id].orEmpty().contains(normalizedQuery)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 223 | `val matchesFilter` | `inferido` | `when (selectedFilter) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 235 | `val motionDuration` | `inferido` | `AppMotion.duration(AppMotion.NORMAL, settings.animationsEnabled, settings.animationSpeed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 358 | `val selected` | `inferido` | `option.filter == selectedFilter` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 404 | `val maximumColumnsForWidth` | `inferido` | `(maxWidth.value / 145f).toInt().coerceIn(1, 3)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 405 | `val effectiveColumns` | `inferido` | `minOf(settings.gridColumns.coerceIn(1, 3), maximumColumnsForWidth)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 419 | `val noteAttachments` | `inferido` | `attachmentsByNote[note.id].orEmpty()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 102 | `fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit, onOpenSettings: () -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 103 | `onOpenNote: (Note) -> Unit, onEditNote: (Note) -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 161 | `LaunchedEffect(query) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 186 | `LaunchedEffect(linkPreviewUrls) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 195 | `LaunchedEffect(currentOrientation) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 196 | `if (previousOrientation != currentOrientation) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 220 | `note ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 224 | `NoteFilter.ALL -> true` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 225 | `NoteFilter.FAVORITES -> note.isFavorite` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 226 | `NoteFilter.WORK -> note.category == "work"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 227 | `NoteFilter.PERSONAL -> note.category == "personal"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 228 | `NoteFilter.IMAGES -> "image" in attachmentKinds` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 229 | `NoteFilter.FILES -> "file" in attachmentKinds` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 259 | `scaffoldPadding ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 285 | `if (settings.profileImageUri.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 312 | `if (it != query) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 357 | `option ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 391 | `if (visibleNotes.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 418 | `note ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 451 | `priority ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 456 | `color ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 461 | `category ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 5.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 3.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **`collectAsStateWithLifecycle`:** Observa un flujo de forma consciente del lifecycle y entrega el último valor como estado de Compose.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `noteViewModel.allAttachments.collectAsStateWithLifecycle`, `remember`, `put`, `items.asSequence`, `toSet`, `AttachmentIndex`, `appFontFamily`, `resolveSecondaryUiTextColor`, `resolveUiTextColor`, `resolveUiGraphicColor`, `settings.toNoteCardStyle`, `mutableStateOf`, `LaunchedEffect`, `delay`, `query.trim`, `lowercase`, `buildString`, `append`, `note.title.lowercase`, `note.content.lowercase`, `notes.asSequence`, `extractLinkUrls`, `asSequence`, `take`, `distinct`, `toList`, `preloadLinkPreviews`, `mutableIntStateOf`, `focusManager.clearFocus`, `orEmpty`, `normalizedQuery.isBlank`, `contains`, `AppMotion.duration`, `animateDpAsState`, `tween`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.5 `EmptyNotesState` — fun, líneas 479–510

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

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `modifier: Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable.
- `hasSearch: Boolean` — `hasSearch` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `fontFamily: androidx.compose.ui.text.font.FontFamily` — `fontFamily` recibe un valor de tipo `androidx.compose.ui.text.font.FontFamily`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Box`, `Column`, `Text`, `Spacer`, `Modifier.height`, `stringResource`, `Modifier.padding`, `resolveSecondaryUiTextColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 97 | `FilterOptions` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 111 | `attachmentIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 112 | `byNote` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 115 | `kindsByNote` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 121 | `attachmentsByNote` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 122 | `fontFamily` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 125 | `screenSecondaryTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 127 | `controlSurfaceColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `controlTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 129 | `controlSecondaryTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `controlGraphicColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 131 | `selectedControlTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 135 | `fabContainerColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 136 | `fabContentColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 137 | `noteCardStyle` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 165 | `searchableTextByNote` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 174 | `focusManager` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 175 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 182 | `linkPreviewUrls` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 189 | `isLandscape` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 190 | `currentOrientation` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 218 | `normalizedQuery` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 221 | `attachmentKinds` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 222 | `matchesText` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |
| 223 | `matchesFilter` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 235 | `motionDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 358 | `selected` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 404 | `maximumColumnsForWidth` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 405 | `effectiveColumns` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 419 | `noteAttachments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Normaliza una ausencia hacia un valor vacío/alternativo para simplificar el código consumidor. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 87–89 | 0 | `private enum class NoteFilter` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 103–476 | 0 | `onOpenNote: (Note) -> Unit, onEditNote: (Note) -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 111–120 | 1 | `val attachmentIndex = remember(allAttachments)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 112–114 | 2 | `val byNote = allAttachments.groupBy` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 115–118 | 2 | `val kindsByNote = buildMap<Int, Set<String>>` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 116–117 | 3 | `byNote.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 116–116 | 4 | `byNote.forEach { (noteId, items) -> put(noteId, items.asSequence().map` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 122–124 | 1 | `val fontFamily = remember(settings.font)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 140–142 | 1 | `settings.animationSpeed)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 150–152 | 1 | `rememberSaveable` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 158–160 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 161–164 | 1 | `LaunchedEffect(query)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 165–173 | 1 | `val searchableTextByNote = remember(notes)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 166–172 | 2 | `notes.associate` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 167–171 | 3 | `buildString(note.title.length + note.content.length + 1)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 182–185 | 1 | `val linkPreviewUrls = remember(notes)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 183–184 | 2 | `notes.asSequence().flatMap` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 186–188 | 1 | `LaunchedEffect(linkPreviewUrls)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 192–194 | 1 | `rememberSaveable` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 195–206 | 1 | `LaunchedEffect(currentOrientation)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 196–205 | 2 | `if (previousOrientation != currentOrientation)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 201–201 | 3 | `withFrameNanos` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 202–202 | 3 | `withFrameNanos` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 208–210 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 216–234 | 1 | `remember(notes, attachmentIndex, searchableTextByNote)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 217–233 | 2 | `derivedStateOf` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 219–232 | 3 | `notes.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 223–230 | 4 | `val matchesFilter = when (selectedFilter)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 242–475 | 1 | `animationSpeed = settings.animationSpeed)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 244–258 | 2 | `floatingActionButton =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 245–248 | 3 | `FloatingActionButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 252–257 | 3 | `shape = CircleShape)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 258–474 | 2 | `})` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 260–473 | 3 | `Column(modifier = Modifier.fillMaxSize().padding(scaffoldPadding).padding(horizontal = 14.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 268–303 | 4 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 269–280 | 5 | `Column(modifier = Modifier.weight(1f))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 281–284 | 5 | `Surface(modifier = Modifier.size(animatedProfileSize).clip(CircleShape).clickable(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 284–294 | 5 | `}), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 285–288 | 6 | `if (settings.profileImageUri.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 288–293 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 289–292 | 7 | `Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 295–298 | 5 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 298–302 | 5 | `})` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 311–316 | 4 | `onValueChange =` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 312–314 | 5 | `if (it != query)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 319–323 | 4 | `leadingIcon =` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 324–327 | 4 | `placeholder =` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 347–349 | 4 | `horizontalArrangement = if (isLandscape)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 349–351 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 352–384 | 4 | `contentPadding = PaddingValues(start = if (isLandscape) 12.dp else 0.dp, end = 12.dp))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 354–356 | 5 | `key =` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 356–383 | 5 | `})` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 360–363 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 368–382 | 6 | `label =` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 372–374 | 7 | `color = if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 374–376 | 7 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 377–379 | 7 | `fontWeight = if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 379–381 | 7 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 391–395 | 4 | `if (visibleNotes.isEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 395–472 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 396–471 | 5 | `BoxWithConstraints(modifier = Modifier.fillMaxSize())` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 410–470 | 6 | `contentPadding = PaddingValues(bottom = 100.dp))` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 412–414 | 7 | `key =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 415–417 | 7 | `contentType =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 417–469 | 7 | `})` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 434–437 | 8 | `onOpen =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 438–441 | 8 | `onEdit =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 442–445 | 8 | `onToggleFavorite =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 446–449 | 8 | `onTogglePinned =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 450–454 | 8 | `onPriorityChange =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 455–459 | 8 | `onColorChange =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 460–464 | 8 | `onCategoryChange =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 465–468 | 8 | `onDelete =` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 480–510 | 0 | `androidx.compose.ui.text.font.FontFamily)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 482–509 | 1 | `contentAlignment = Alignment.Center)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 483–508 | 2 | `Column(horizontalAlignment = Alignment.CenterHorizontally)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 484–486 | 3 | `Text(text = if (hasSearch)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 486–488 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 491–493 | 3 | `Text(text = stringResource(if (hasSearch)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 493–495 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 499–501 | 3 | `Text(text = stringResource(if (hasSearch)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 501–503 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
