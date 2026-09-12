# NoteCard.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/NoteCard.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `2c36c61fc63415aa4e224ce6343c348a7c8d4592d6776c30b26bb861cfec25b0`  
**Líneas del código real:** 747

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Componente Compose que representa cada nota en la cuadrícula/lista principal, incluidos texto, adjuntos, metadatos y menú contextual de tres puntos.

**Arquitectura.** Conecta las preferencias visuales de las tarjetas con callbacks de editar, fijar, favoritos, prioridad, color, mover y borrar. También integra previews cacheados.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.components`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **93 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.net.Uri`.

**Jetpack/Compose:** `androidx.compose.animation.AnimatedVisibility`, `androidx.compose.animation.Crossfade`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.scaleIn`, `androidx.compose.animation.scaleOut`, `androidx.compose.animation.animateColorAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.animation.animateContentSize`, `androidx.compose.foundation.background`, `androidx.compose.foundation.Image`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.interaction.MutableInteractionSource`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.fillMaxHeight`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Delete`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.Edit`, `androidx.compose.material.icons.filled.Mic`, `androidx.compose.material.icons.filled.MusicNote`, `androidx.compose.material.icons.filled.PlayArrow`, `androidx.compose.material.icons.filled.MoreVert`, `androidx.compose.material.icons.filled.Palette`, `androidx.compose.material.icons.filled.PriorityHigh`, `androidx.compose.material.icons.filled.PushPin`, `androidx.compose.material.icons.filled.Star`, `androidx.compose.material.icons.filled.StarBorder`, `androidx.compose.material.icons.filled.Work`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material3.Card`, `androidx.compose.material3.CardDefaults`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.HorizontalDivider`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.produceState`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.vector.ImageVector`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`.

**Proyecto MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.compositeUiColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Kotlin/corrutinas/Java:** `java.text.DateFormat`, `java.util.Date`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (6 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (1 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Aserción no nula (2 aparición/apariciones):** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción.
- **Límite numérico (2 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Normalización vacía (2 aparición/apariciones):** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior.
- **Límite visual (6 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.
- **Sin foco de popup (4 aparición/apariciones):** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android.

## 4. Bloques de código, uno por uno

### 4.1 `ModernNoteCard` — fun, líneas 119–500

```kotlin
fun ModernNoteCard(note: Note, attachments: List<Attachment>, fontFamily: FontFamily, fontSize: Float, noteUiTextColor: String,
    style: NoteCardStyle, optionMenuOrder: String, optionMenuHiddenItems: String, optionMenuShowIcons: Boolean, optionMenuTextColor: String,
    optionMenuOpacity: Float, priorityMenuHiddenItems: String, colorMenuHiddenItems: String, performanceMode: String, onOpen: () -> Unit,
    onEdit: () -> Unit, onToggleFavorite: () -> Unit, onTogglePinned: () -> Unit, onPriorityChange: (Int) -> Unit,
    onColorChange: (String) -> Unit, onCategoryChange: (String) -> Unit, onDelete: () -> Unit) {
    val context = LocalContext.current
    val cardColor = noteBackgroundColor(note.color)
    val textColor = resolveUiTextColor(value = noteUiTextColor, background = cardColor)
    val secondaryTextColor = resolveSecondaryUiTextColor(value = noteUiTextColor, background = cardColor)
    val graphicColor = resolveUiGraphicColor(value = noteUiTextColor, background = cardColor)
    val favoriteIconColor = ensureUiContrast(preferred = FavoriteGold, background = cardColor, minimumContrast = 3f)
    val motionDuration = AppMotion.duration(AppMotion.FAST, style.animationsEnabled, style.animationSpeed)
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
    val popupBaseColor = when (optionMenuTextColor) {
            "white" -> MaterialTheme.colorScheme.inverseSurface
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        }
    val popupAlpha = (optionMenuOpacity / 100f).coerceIn(0.35f, 1f)
    val popupColor = popupBaseColor.copy(alpha = popupAlpha)
    val popupVisualBackground = compositeUiColor(foreground = popupColor, background = cardColor)
    val menuTextColor = resolveUiTextColor(value = optionMenuTextColor, background = popupVisualBackground)
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
        elevation = CardDefaults.cardElevation(defaultElevation = style.elevation.dp)) {
        Column(modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = motionDuration))) {
            if (previewAttachments.isNotEmpty()) {
                NoteCardAttachmentsPreview(attachments = previewAttachments,
                    previewHeight = style.imageHeight.dp,
                    cornerRadius = style.cornerRadius.dp,
                    fontFamily = fontFamily,
                    performanceMode = performanceMode)
            }
            Column(modifier = Modifier.fillMaxWidth().padding(start = style.padding.dp, end = (style.padding * 0.55f).dp,
                            top = (style.padding * 0.75f).dp, bottom = (style.padding * 0.90f).dp)) {
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
                Text(text = note.title.ifBlank {
                                stringResource(R.string.mock_untitled)
                            },
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp, end = 8.dp),
                    color = textColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (fontSize + 1f).sp,
                    maxLines = style.titleMaxLines,
                    overflow = TextOverflow.Ellipsis)
                if (displayContent.isNotBlank()) {
                    Text(text = displayContent,
                        modifier = Modifier.padding(top = 3.dp, end = 8.dp),
                        color = secondaryTextColor,
                        fontFamily = fontFamily,
                        fontSize = (fontSize - 2f).coerceAtLeast(11f).sp,
                        lineHeight = (fontSize * style.lineSpacing).sp,
                        maxLines = style.contentMaxLines,
                        overflow = TextOverflow.Ellipsis)
                }
                if (noteLinks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinkPreviewCard(url = noteLinks.first(), compact = true, textColorMode = noteUiTextColor, modifier = Modifier.padding(
                                end = 8.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
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
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.
- `attachments: List<Attachment>` — `attachments` recibe un valor de tipo `List<Attachment>`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `fontSize: Float` — `fontSize` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `noteUiTextColor: String` — `noteUiTextColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `style: NoteCardStyle` — `style` recibe un valor de tipo `NoteCardStyle`. El contrato no marca este parámetro como anulable.
- `optionMenuOrder: String` — `optionMenuOrder` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `optionMenuHiddenItems: String` — `optionMenuHiddenItems` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `optionMenuShowIcons: Boolean` — `optionMenuShowIcons` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `optionMenuTextColor: String` — `optionMenuTextColor` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `optionMenuOpacity: Float` — `optionMenuOpacity` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `priorityMenuHiddenItems: String` — `priorityMenuHiddenItems` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `colorMenuHiddenItems: String` — `colorMenuHiddenItems` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `performanceMode: String` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `onOpen: () -> Unit` — `onOpen` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onEdit: () -> Unit` — `onEdit` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onToggleFavorite: () -> Unit` — `onToggleFavorite` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onTogglePinned: () -> Unit` — `onTogglePinned` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onPriorityChange: (Int) -> Unit` — `onPriorityChange` recibe un valor de tipo `(Int) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onColorChange: (String) -> Unit` — `onColorChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onCategoryChange: (String) -> Unit` — `onCategoryChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onDelete: () -> Unit` — `onDelete` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 124 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 125 | `val cardColor` | `inferido` | `noteBackgroundColor(note.color)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 126 | `val textColor` | `inferido` | `resolveUiTextColor(value = noteUiTextColor, background = cardColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 127 | `val secondaryTextColor` | `inferido` | `resolveSecondaryUiTextColor(value = noteUiTextColor, background = cardColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `val graphicColor` | `inferido` | `resolveUiGraphicColor(value = noteUiTextColor, background = cardColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 129 | `val favoriteIconColor` | `inferido` | `ensureUiContrast(preferred = FavoriteGold, background = cardColor, minimumContrast = 3f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `val motionDuration` | `inferido` | `AppMotion.duration(AppMotion.FAST, style.animationsEnabled, style.animationSpeed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 133 | `val previewAttachments` | `inferido` | `remember(attachments) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 136 | `val noteLinks` | `inferido` | `remember(note.content) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 139 | `val displayContent` | `inferido` | `remember(note.content, noteLinks) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 158 | `val popupBaseColor` | `inferido` | `when (optionMenuTextColor) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 162 | `val popupAlpha` | `inferido` | `(optionMenuOpacity / 100f).coerceIn(0.35f, 1f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 163 | `val popupColor` | `inferido` | `popupBaseColor.copy(alpha = popupAlpha)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 164 | `val popupVisualBackground` | `inferido` | `compositeUiColor(foreground = popupColor, background = cardColor)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 165 | `val menuTextColor` | `inferido` | `resolveUiTextColor(value = optionMenuTextColor, background = popupVisualBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 166 | `val mainMenuOrder` | `inferido` | `remember(optionMenuOrder) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 169 | `val hiddenMainMenuItems` | `inferido` | `remember(optionMenuHiddenItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 172 | `val visibleMainMenuItems` | `inferido` | `remember(mainMenuOrder, hiddenMainMenuItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 180 | `val hiddenPriorityItems` | `inferido` | `remember(priorityMenuHiddenItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 183 | `val visiblePriorityItems` | `inferido` | `remember(hiddenPriorityItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 191 | `val hiddenColorItems` | `inferido` | `remember(colorMenuHiddenItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 194 | `val visibleColorItems` | `inferido` | `remember(hiddenColorItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 202 | `val cardInteractionSource` | `inferido` | `remember {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 369 | `val priority` | `inferido` | `when (key) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 375 | `val label` | `inferido` | `stringResource(when (key) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 397 | `val label` | `inferido` | `stringResource(when (key) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 413 | `val optionColor` | `inferido` | `when (key) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 121 | `optionMenuOpacity: Float, priorityMenuHiddenItems: String, colorMenuHiddenItems: String, performanceMode: String, onOpen: () -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 122 | `onEdit: () -> Unit, onToggleFavorite: () -> Unit, onTogglePinned: () -> Unit, onPriorityChange: (Int) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 123 | `onColorChange: (String) -> Unit, onCategoryChange: (String) -> Unit, onDelete: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 159 | `"white" -> MaterialTheme.colorScheme.inverseSurface` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 160 | `else -> MaterialTheme.colorScheme.surfaceContainerHigh` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 212 | `if (previewAttachments.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 239 | `if (style.showFavorite) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 245 | `favorite ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 280 | `index, key ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 281 | `if (key == "delete" && index >` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 285 | `when (key) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 286 | `"edit" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 294 | `"favorite" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 308 | `"pin" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 319 | `"priority" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 328 | `"color" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 337 | `"move" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 349 | `"delete" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 368 | `key ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 370 | `"low" -> 1` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 371 | `"medium" -> 2` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 372 | `"high" -> 3` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 373 | `else -> 0` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 376 | `"low" -> R.string.mock_priority_low` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 377 | `"medium" -> R.string.mock_priority_medium` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 378 | `"high" -> R.string.mock_priority_high` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 379 | `else -> R.string.mock_priority_none` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 396 | `key ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 398 | `"yellow" -> R.string.mock_color_yellow` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 399 | `"orange" -> R.string.mock_color_orange` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 400 | `"red" -> R.string.mock_color_red` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 401 | `"pink" -> R.string.mock_color_pink` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 402 | `"purple" -> R.string.mock_color_purple` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 403 | `"blue" -> R.string.mock_color_blue` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 404 | `"cyan" -> R.string.mock_color_cyan` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 405 | `"teal" -> R.string.mock_color_teal` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 406 | `"green" -> R.string.mock_color_green` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 407 | `"mint" -> R.string.mock_color_mint` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 408 | `"lime" -> R.string.mock_color_lime` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 409 | `"brown" -> R.string.mock_color_brown` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 410 | `"gray" -> R.string.mock_color_gray` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 411 | `else -> R.string.mock_color_default` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 414 | `"yellow" -> Color(0xFFFFF4C7)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 415 | `"orange" -> Color(0xFFFFE7D1)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 416 | `"red" -> Color(0xFFFFE0E0)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 417 | `"pink" -> Color(0xFFFFE5EC)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 418 | `"purple" -> Color(0xFFF0E7FA)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 419 | `"blue" -> Color(0xFFE5F1FB)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 420 | `"cyan" -> Color(0xFFE0F7FA)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 421 | `"teal" -> Color(0xFFDDF4F0)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 422 | `"green" -> Color(0xFFE4F2E8)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 423 | `"mint" -> Color(0xFFDFF7EA)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 424 | `"lime" -> Color(0xFFF1F8D7)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 425 | `"brown" -> Color(0xFFEDE2D9)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 426 | `"gray" -> Color(0xFFE9ECEF)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 427 | `else -> MaterialTheme.colorScheme.surfaceContainerLow` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 465 | `if (displayContent.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 475 | `if (noteLinks.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 483 | `if (style.showCategory) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 487 | `if (style.showDate) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 2.
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 3.
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 4.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `noteBackgroundColor`, `resolveUiTextColor`, `resolveSecondaryUiTextColor`, `resolveUiGraphicColor`, `ensureUiContrast`, `AppMotion.duration`, `animateColorAsState`, `tween`, `remember`, `attachments.take`, `extractLinkUrls`, `noteTextForDisplay`, `mutableStateOf`, `coerceIn`, `popupBaseColor.copy`, `compositeUiColor`, `normalizedMenuOrder`, `parseMenuKeys`, `listOf`, `MutableInteractionSource`, `Card`, `Modifier.fillMaxWidth`, `clickable`, `RoundedCornerShape`, `CardDefaults.cardColors`, `CardDefaults.cardElevation`, `Column`, `Modifier.animateContentSize`, `previewAttachments.isNotEmpty`, `NoteCardAttachmentsPreview`, `padding`, `Row`, `Spacer`, `Modifier.weight`, `AnimatedVisibility`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.2 `CategoryPill` — fun, líneas 503–528

```kotlin
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
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `category: String` — `category` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily = FontFamily.Default` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `FontFamily.Default`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 504 | `val work` | `inferido` | `category == "work"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 505 | `val background` | `inferido` | `if (work) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 510 | `val foreground` | `inferido` | `if (work) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Color`, `Surface`, `RoundedCornerShape`, `Text`, `stringResource`, `Modifier.padding`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `NoteCardAttachmentsPreview` — fun, líneas 531–561

```kotlin
private fun NoteCardAttachmentsPreview(attachments: List<Attachment>, previewHeight: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp, fontFamily: FontFamily, performanceMode: String) {
    val shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
    if (attachments.size == 1) {
        Box(modifier = Modifier.fillMaxWidth().height(previewHeight).clip(shape)) {
            NoteCardAttachmentTile(attachment = attachments.first(), modifier = Modifier.fillMaxSize(), compact = false,
                fontFamily = fontFamily, performanceMode = performanceMode)
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
                        compact = true, fontFamily = fontFamily, performanceMode = performanceMode)
                } else {
                    rowAttachments.forEach { attachment -> NoteCardAttachmentTile(attachment = attachment, modifier = Modifier.weight(1f)
                                .fillMaxHeight(), compact = true, fontFamily = fontFamily, performanceMode = performanceMode)
                    }
                }
            }
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `attachments: List<Attachment>` — `attachments` recibe un valor de tipo `List<Attachment>`. El contrato no marca este parámetro como anulable.
- `previewHeight: androidx.compose.ui.unit.Dp` — `previewHeight` recibe un valor de tipo `androidx.compose.ui.unit.Dp`. El contrato no marca este parámetro como anulable.
- `cornerRadius: androidx.compose.ui.unit.Dp` — `cornerRadius` recibe un valor de tipo `androidx.compose.ui.unit.Dp`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `performanceMode: String` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 533 | `val shape` | `inferido` | `RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 541 | `val multiHeight` | `inferido` | `(previewHeight.value * 1.35f).dp` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 543 | `val rows` | `inferido` | `attachments.take(4).chunked(2)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 534 | `if (attachments.size == 1) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 539 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 545 | `if (rowAttachments.size == 1) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `RoundedCornerShape`, `Box`, `Modifier.fillMaxWidth`, `height`, `clip`, `NoteCardAttachmentTile`, `attachments.first`, `Modifier.fillMaxSize`, `Column`, `Arrangement.spacedBy`, `attachments.take`, `chunked`, `Row`, `Modifier.weight`, `rowAttachments.first`, `fillMaxHeight`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.4 `NoteCardAttachmentTile` — fun, líneas 564–640

```kotlin
private fun NoteCardAttachmentTile(attachment: Attachment, modifier: Modifier, compact: Boolean, fontFamily: FontFamily,
    performanceMode: String) {
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
                val preview by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = performanceMode) {
                    value = AttachmentPreviewCache.withPreviewPermit {
                        AttachmentPreviewCache.loadImagePreview(context = context, uri = uri, performanceMode = performanceMode)
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
                val preview by produceState<AttachmentPreviewCache.MediaPreview?>(initialValue = null, key1 = attachment.uri,
                    key2 = performanceMode) {
                    value = AttachmentPreviewCache.withPreviewPermit {
                        AttachmentPreviewCache.loadVideoPreview(context = context, uri = uri, performanceMode = performanceMode)
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
                val preview by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = performanceMode) {
                    value = AttachmentPreviewCache.withPreviewPermit {
                        AttachmentPreviewCache.loadPdfFirstPage(context = context, uri = uri, performanceMode = performanceMode)
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
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `attachment: Attachment` — `attachment` recibe un valor de tipo `Attachment`. El contrato no marca este parámetro como anulable.
- `modifier: Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable.
- `compact: Boolean` — `compact` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `performanceMode: String` — `performanceMode` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 566 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 567 | `val uri` | `inferido` | `remember(attachment.uri) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 570 | `val extension` | `inferido` | `remember(attachment.name, attachment.uri) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 578 | `val preview` | `inferido` | `by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = perform…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 592 | `val preview` | `inferido` | `by produceState<AttachmentPreviewCache.MediaPreview?>(initialValue = null, key1 = attachment.uri,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 598 | `val bitmap` | `inferido` | `preview?.bitmap` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 610 | `val duration` | `inferido` | `preview?.durationMillis ?: 0L` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 621 | `val preview` | `inferido` | `by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = perform…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 576 | `when {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 577 | `attachment.type == "image" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 583 | `if (preview != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 591 | `attachment.type == "video" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 599 | `if (bitmap != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 611 | `if (duration > 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 615 | `attachment.type == "voice" \|\| attachment.type == "audio" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 620 | `extension == "pdf" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 626 | `if (preview != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 634 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 6.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Aserción no nula:** La aserción `!!` exige un valor no nulo en tiempo de ejecución; si la suposición falla se produce una excepción. Apariciones en este bloque: 2.
- **Normalización vacía:** `orEmpty` transforma una referencia nula en una colección/cadena vacía, simplificando el consumo posterior. Apariciones en este bloque: 2.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `Uri.parse`, `substringAfterLast`, `lowercase`, `orEmpty`, `Box`, `AttachmentPreviewCache.loadImagePreview`, `Image`, `asImageBitmap`, `Modifier.fillMaxSize`, `FileLikeFallbackTile`, `stringResource`, `AttachmentPreviewCache.loadVideoPreview`, `bitmap.asImageBitmap`, `Surface`, `Modifier.align`, `Color.Black.copy`, `Icon`, `Modifier.padding`, `size`, `SmallDurationBadge`, `AttachmentPreviewCache.loadPdfFirstPage`, `extension.uppercase`, `uppercase`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.5 `FileLikeFallbackTile` — fun, líneas 643–652

```kotlin
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
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `icon: ImageVector` — `icon` recibe un valor de tipo `ImageVector`. El contrato no marca este parámetro como anulable.
- `label: String` — `label` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `compact: Boolean` — `compact` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Column`, `Modifier.fillMaxSize`, `background`, `Icon`, `Modifier.size`, `Spacer`, `Modifier.height`, `Text`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `SmallDurationBadge` — fun, líneas 655–660

```kotlin
private fun SmallDurationBadge(duration: Long, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.padding(8.dp), color = Color.Black.copy(alpha = 0.68f), shape = RoundedCornerShape(8.dp)) {
        Text(text = formatSmallDuration(duration), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White,
            fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `duration: Long` — `duration` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

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

Entre las llamadas presentes están: `Surface`, `modifier.padding`, `Color.Black.copy`, `RoundedCornerShape`, `Text`, `formatSmallDuration`, `Modifier.padding`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `formatSmallDuration` — fun, líneas 662–667

```kotlin
private fun formatSmallDuration(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `durationMillis: Long` — `durationMillis` recibe un valor de tipo `Long`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 663 | `val totalSeconds` | `inferido` | `durationMillis / 1000L` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 664 | `val minutes` | `inferido` | `totalSeconds / 60L` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 665 | `val seconds` | `inferido` | `totalSeconds % 60L` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 666 | `return "%d:%02d".format(minutes, seconds)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `format`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `ConfigurableDropdownMenuItem` — fun, líneas 670–682

```kotlin
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
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `label: String` — `label` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `icon: ImageVector` — `icon` recibe un valor de tipo `ImageVector`. El contrato no marca este parámetro como anulable.
- `showIcon: Boolean` — `showIcon` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `onClick: () -> Unit` — `onClick` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 670 | `private fun ConfigurableDropdownMenuItem(label: String, icon: ImageVector, showIcon: Boolean, textColor: Color, onClick: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `DropdownMenuItem`, `Modifier.defaultMinSize`, `PaddingValues`, `Text`, `Icon`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.9 `PriorityMenuItem` — fun, líneas 685–708

```kotlin
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
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `label: String` — `label` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `selected: Boolean` — `selected` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `showIndicator: Boolean` — `showIndicator` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `onClick: () -> Unit` — `onClick` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 685 | `private fun PriorityMenuItem(label: String, selected: Boolean, textColor: Color, showIndicator: Boolean, onClick: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 700 | `if (selected) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Semántica Compose/lifecycle

- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `DropdownMenuItem`, `Modifier.defaultMinSize`, `PaddingValues`, `Text`, `Box`, `Modifier.size`, `clip`, `background`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.10 `ColorMenuItem` — fun, líneas 711–725

```kotlin
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
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `label: String` — `label` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `color: Color` — `color` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `showSwatch: Boolean` — `showSwatch` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `onClick: () -> Unit` — `onClick` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 711 | `private fun ColorMenuItem(label: String, color: Color, textColor: Color, showSwatch: Boolean, onClick: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `DropdownMenuItem`, `Modifier.defaultMinSize`, `PaddingValues`, `Text`, `Box`, `Modifier.size`, `clip`, `background`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.11 `normalizedMenuOrder` — fun, líneas 727–738

```kotlin
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
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `raw: String` — `raw` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `validKeys: List<String>` — `validKeys` recibe un valor de tipo `List<String>`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `List<String>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 728 | `val requested` | `inferido` | `raw.split(",").map {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 734 | `return requested + validKeys.filterNot {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `raw.split`, `it.trim`, `distinct`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.12 `parseMenuKeys` — fun, líneas 740–747

```kotlin
private fun parseMenuKeys(raw: String, validKeys: List<String>): Set<String> {
    return raw.split(",").map {
            it.trim()
        }.filter {
            it in
                validKeys
        }.toSet()
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `raw: String` — `raw` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `validKeys: List<String>` — `validKeys` recibe un valor de tipo `List<String>`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `Set<String>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 741 | `return raw.split(",").map {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `raw.split`, `it.trim`, `toSet`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 97 | `FavoriteGold` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 99 | `MainOptionMenuKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 101 | `PriorityOptionKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 103 | `ColorOptionKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 124 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 125 | `cardColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 126 | `textColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 127 | `secondaryTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 128 | `graphicColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 129 | `favoriteIconColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 130 | `motionDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 133 | `previewAttachments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 136 | `noteLinks` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 139 | `displayContent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 158 | `popupBaseColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 162 | `popupAlpha` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 163 | `popupColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 164 | `popupVisualBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 165 | `menuTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 166 | `mainMenuOrder` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 169 | `hiddenMainMenuItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 172 | `visibleMainMenuItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 180 | `hiddenPriorityItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 183 | `visiblePriorityItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 191 | `hiddenColorItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 194 | `visibleColorItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 202 | `cardInteractionSource` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 369 | `priority` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 375 | `label` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 397 | `label` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 413 | `optionColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 504 | `work` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 505 | `background` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 510 | `foreground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 533 | `shape` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 541 | `multiHeight` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. |
| 543 | `rows` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 566 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 567 | `uri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 570 | `extension` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 578 | `preview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 592 | `preview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 598 | `bitmap` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 610 | `duration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. Usa acceso seguro (`?.`): la cadena se detiene y produce `null` si el receptor es nulo, evitando una excepción por desreferencia. |
| 621 | `preview` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Usa delegación (`by`): las operaciones de lectura/escritura se redirigen al delegado. En Compose esto suele enlazar una variable directamente con un `State`, permitiendo sintaxis de valor normal sin acceder manualmente a `.value`. |
| 663 | `totalSeconds` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 664 | `minutes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 665 | `seconds` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 728 | `requested` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 123–500 | 0 | `onColorChange: (String) -> Unit, onCategoryChange: (String) -> Unit, onDelete: () -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 133–135 | 1 | `val previewAttachments = remember(attachments)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 136–138 | 1 | `val noteLinks = remember(note.content)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 139–141 | 1 | `val displayContent = remember(note.content, noteLinks)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 143–145 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 147–149 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 151–153 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 155–157 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 158–161 | 1 | `val popupBaseColor = when (optionMenuTextColor)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 166–168 | 1 | `val mainMenuOrder = remember(optionMenuOrder)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 169–171 | 1 | `val hiddenMainMenuItems = remember(optionMenuHiddenItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 172–179 | 1 | `val visibleMainMenuItems = remember(mainMenuOrder, hiddenMainMenuItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 173–176 | 2 | `mainMenuOrder.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 176–178 | 2 | `}.ifEmpty` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 180–182 | 1 | `val hiddenPriorityItems = remember(priorityMenuHiddenItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 183–190 | 1 | `val visiblePriorityItems = remember(hiddenPriorityItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 184–187 | 2 | `PriorityOptionKeys.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 187–189 | 2 | `}.ifEmpty` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 191–193 | 1 | `val hiddenColorItems = remember(colorMenuHiddenItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 194–201 | 1 | `val visibleColorItems = remember(hiddenColorItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 195–198 | 2 | `ColorOptionKeys.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 198–200 | 2 | `}.ifEmpty` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 202–204 | 1 | `val cardInteractionSource = remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 210–499 | 1 | `elevation = CardDefaults.cardElevation(defaultElevation = style.elevation.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 211–498 | 2 | `Column(modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = motionDuration)))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 212–218 | 3 | `if (previewAttachments.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 220–497 | 3 | `top = (style.padding * 0.75f).dp, bottom = (style.padding * 0.90f).dp))` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 222–454 | 4 | `verticalAlignment = Alignment.Top)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 228–238 | 5 | `durationMillis = motionDuration), targetScale = 0.72f))` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 232–237 | 6 | `Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 239–260 | 5 | `if (style.showFavorite)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 240–242 | 6 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 243–259 | 6 | `modifier = Modifier.size(40.dp))` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 244–258 | 7 | `Crossfade(targetState = note.isFavorite, animationSpec = tween(durationMillis = motionDuration))` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 246–248 | 8 | `Icon(imageVector = if (favorite)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 248–250 | 8 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 252–254 | 8 | `tint = if (favorite)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 254–256 | 8 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 261–453 | 5 | `Box` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 262–265 | 6 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 266–271 | 6 | `modifier = Modifier.size(40.dp))` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 274–276 | 6 | `onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 278–359 | 6 | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 279–358 | 7 | `visibleMainMenuItems.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 282–284 | 8 | `0)` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 285–357 | 8 | `when (key)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 286–293 | 9 | `"edit" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 289–292 | 10 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 294–307 | 9 | `"favorite" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 295–297 | 10 | `ConfigurableDropdownMenuItem(label = if (note.isFavorite)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 297–299 | 10 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 299–301 | 10 | `}, icon = if (note.isFavorite)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 301–303 | 10 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 303–306 | 10 | `}, showIcon = optionMenuShowIcons, textColor = menuTextColor, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 308–318 | 9 | `"pin" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 309–311 | 10 | `ConfigurableDropdownMenuItem(label = if (note.isPinned)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 311–313 | 10 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 314–317 | 10 | `menuTextColor, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 319–327 | 9 | `"priority" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 322–326 | 10 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 328–336 | 9 | `"color" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 331–335 | 10 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 337–348 | 9 | `"move" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 339–341 | 10 | `note.category == "work")` | Ámbito delimitado por llaves en profundidad 10. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 341–343 | 10 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 343–347 | 10 | `}, showIcon = optionMenuShowIcons, textColor = menuTextColor, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 349–356 | 9 | `"delete" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 352–355 | 10 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 362–364 | 6 | `onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 366–387 | 6 | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 367–386 | 7 | `visiblePriorityItems.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 369–374 | 8 | `val priority = when (key)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 375–380 | 8 | `val label = stringResource(when (key)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 382–385 | 8 | `showIndicator = optionMenuShowIcons)` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 390–392 | 6 | `onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 394–435 | 6 | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 395–434 | 7 | `visibleColorItems.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 397–412 | 8 | `val label = stringResource(when (key)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 413–428 | 8 | `val optionColor = when (key)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 430–433 | 8 | `optionMenuShowIcons)` | Ámbito delimitado por llaves en profundidad 8. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 437–439 | 6 | `onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 441–452 | 6 | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 443–446 | 7 | `optionMenuShowIcons, textColor = menuTextColor, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 448–451 | 7 | `showIcon = optionMenuShowIcons, textColor = menuTextColor, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 455–457 | 4 | `Text(text = note.title.ifBlank` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 465–474 | 4 | `if (displayContent.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 475–479 | 4 | `if (noteLinks.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 482–496 | 4 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 483–485 | 5 | `if (style.showCategory)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 487–495 | 5 | `if (style.showDate)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 488–490 | 6 | `Text(text = remember(note.createdAt)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 503–528 | 0 | `fun CategoryPill(category: String, fontFamily: FontFamily = FontFamily.Default)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 505–507 | 1 | `val background = if (work)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 507–509 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 510–512 | 1 | `val foreground = if (work)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 512–514 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 516–527 | 1 | `color = background)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 517–519 | 2 | `Text(text = stringResource(if (work)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 519–521 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 532–561 | 0 | `cornerRadius: androidx.compose.ui.unit.Dp, fontFamily: FontFamily, performanceMode: String)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 534–540 | 1 | `if (attachments.size == 1)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 535–538 | 2 | `Box(modifier = Modifier.fillMaxWidth().height(previewHeight).clip(shape))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 542–560 | 1 | `Column(modifier = Modifier.fillMaxWidth().height(multiHeight).clip(shape), verticalArrangement = Arrangement.spacedBy(0.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 544–559 | 2 | `rows.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 544–558 | 3 | `rows.forEach { rowAttachments -> Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(0.dp))` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 545–553 | 4 | `if (rowAttachments.size == 1)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 553–557 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 554–556 | 5 | `rowAttachments.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 565–640 | 0 | `performanceMode: String)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 567–569 | 1 | `val uri = remember(attachment.uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 570–574 | 1 | `val extension = remember(attachment.name, attachment.uri)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 571–573 | 2 | `attachment.name?.substringAfterLast('.')?.lowercase().orEmpty().ifBlank` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 575–639 | 1 | `Box(modifier = modifier)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 576–638 | 2 | `when` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 577–590 | 3 | `attachment.type == "image" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 578–582 | 4 | `val preview by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = performanceMode)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 579–581 | 5 | `value = AttachmentPreviewCache.withPreviewPermit` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 583–586 | 4 | `if (preview != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 586–589 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 591–614 | 3 | `attachment.type == "video" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 593–597 | 4 | `key2 = performanceMode)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 594–596 | 5 | `value = AttachmentPreviewCache.withPreviewPermit` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 599–602 | 4 | `if (bitmap != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 602–605 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 606–609 | 4 | `Surface(modifier = Modifier.align(Alignment.Center), shape = CircleShape, color = Color.Black.copy(alpha = 0.56f))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 611–613 | 4 | `if (duration > 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 615–619 | 3 | `attachment.type == "voice" \|\| attachment.type == "audio" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 620–633 | 3 | `extension == "pdf" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 621–625 | 4 | `val preview by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = attachment.uri, key2 = performanceMode)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 622–624 | 5 | `value = AttachmentPreviewCache.withPreviewPermit` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 626–629 | 4 | `if (preview != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 629–632 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 634–637 | 3 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 636–636 | 4 | `label = extension.ifBlank` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 643–652 | 0 | `private fun FileLikeFallbackTile(icon: ImageVector, label: String, compact: Boolean, fontFamily: FontFamily)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 645–651 | 1 | `horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 655–660 | 0 | `private fun SmallDurationBadge(duration: Long, modifier: Modifier = Modifier)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 656–659 | 1 | `Surface(modifier = modifier.padding(8.dp), color = Color.Black.copy(alpha = 0.68f), shape = RoundedCornerShape(8.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 662–667 | 0 | `private fun formatSmallDuration(durationMillis: Long): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 670–682 | 0 | `private fun ConfigurableDropdownMenuItem(label: String, icon: ImageVector, showIcon: Boolean, textColor: Color, onClick: () -> Unit)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 673–675 | 1 | `text =` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 675–679 | 1 | `}, leadingIcon = if (showIcon)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 676–678 | 2 | `}, leadingIcon = if (showIcon) {` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 679–681 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 685–708 | 0 | `private fun PriorityMenuItem(label: String, selected: Boolean, textColor: Color, showIndicator: Boolean, onClick: () -> Unit)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 688–697 | 1 | `text =` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 692–694 | 2 | `fontWeight = if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 694–696 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 698–704 | 1 | `leadingIcon = if (showIndicator)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 699–703 | 2 | `leadingIcon = if (showIndicator) {` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 700–702 | 3 | `if (selected)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 704–706 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 711–725 | 0 | `private fun ColorMenuItem(label: String, color: Color, textColor: Color, showSwatch: Boolean, onClick: () -> Unit)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 714–716 | 1 | `text =` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 717–721 | 1 | `leadingIcon = if (showSwatch)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 718–720 | 2 | `leadingIcon = if (showSwatch) {` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 721–723 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 727–738 | 0 | `private fun normalizedMenuOrder(raw: String, validKeys: List<String>): List<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 728–730 | 1 | `val requested = raw.split(",").map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 730–733 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 734–737 | 1 | `return requested + validKeys.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 740–747 | 0 | `private fun parseMenuKeys(raw: String, validKeys: List<String>): Set<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 741–743 | 1 | `return raw.split(",").map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 743–746 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
