# NoteCard.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/NoteCard.kt`  
**SHA-256 actual del archivo, sin modificar:** `bf918f121be99c5320bec6db75ec4e24f5e7de699c76825014a49fa09ab8af7c`  
**Líneas del código real:** 838  
**Estado respecto de la documentación anterior:** **archivo modificado desde la instantánea anterior**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

Componente visual de una tarjeta de nota en la pantalla principal. Renderiza texto, adjuntos, link previews, categoría, prioridad, favorito y menús de acciones.

**Cambios recientes cubiertos por esta revisión.** Los cambios recientes coordinan la carga de miniaturas con el estado de scroll, priorizan datos ya precargados en máxima calidad y mantienen popups no focusables para no sacar la app del modo inmersivo.

## 2. Package e imports

El package declarado es `com.example.mynotes.ui.components`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **93 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`android.net.Uri`, `androidx.compose.animation.AnimatedVisibility`, `androidx.compose.animation.Crossfade`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.scaleIn`, `androidx.compose.animation.scaleOut`, `androidx.compose.animation.animateColorAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.animation.animateContentSize`, `androidx.compose.foundation.background`, `androidx.compose.foundation.Image`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.interaction.MutableInteractionSource`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.fillMaxHeight`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Delete`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.Edit`, `androidx.compose.material.icons.filled.Mic`, `androidx.compose.material.icons.filled.MusicNote`, `androidx.compose.material.icons.filled.PlayArrow`, `androidx.compose.material.icons.filled.MoreVert`, `androidx.compose.material.icons.filled.Palette`, `androidx.compose.material.icons.filled.PriorityHigh`, `androidx.compose.material.icons.filled.PushPin`, `androidx.compose.material.icons.filled.Star`, `androidx.compose.material.icons.filled.StarBorder`, `androidx.compose.material.icons.filled.Work`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material3.Card`, `androidx.compose.material3.CardDefaults`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.HorizontalDivider`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.vector.ImageVector`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.compositeUiColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`

### Java / Kotlin estándar

`java.text.DateFormat`, `java.util.Date`

## 3. Restricciones, límites e invariantes detectables

- **Llamada segura `?.`: 8 aparición/apariciones.** evita desreferenciar receptores nulos; si el receptor es `null`, la cadena se corta de forma segura.
- **Elvis `?:`: 1 aparición/apariciones.** define un fallback explícito cuando el operando izquierdo es nulo.
- **Aserción `!!`: 2 aparición/apariciones.** convierte una suposición de no nulidad en una posible excepción si se incumple.
- **Acotaciones `coerceIn/AtLeast/AtMost`: 2 aparición/apariciones.** imponen límites numéricos para evitar valores fuera del rango aceptado.
- **Límites visuales: 17 aparición/apariciones.** evitan crecimiento o reducción de UI fuera de los límites previstos.
- **Estado Compose: 30 aparición/apariciones.** introduce estado observado por Compose y, por tanto, puntos potenciales de recomposición.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `ModernNoteCard` — fun, líneas 119–525

```kotlin
fun ModernNoteCard(note: Note, attachments: List<Attachment>, fontFamily: FontFamily, fontSize: Float, noteUiTextColor: String,
    style: NoteCardStyle, optionMenuOrder: String, optionMenuHiddenItems: String, optionMenuShowIcons: Boolean, optionMenuTextColor: String,
    optionMenuOpacity: Float, priorityMenuHiddenItems: String, colorMenuHiddenItems: String, performanceMode: String,
    isScrolling: Boolean = false, onOpen: () -> Unit, onEdit: () -> Unit, onToggleFavorite: () -> Unit, onTogglePinned: () -> Unit,
    onPriorityChange: (Int) -> Unit,
    onColorChange: (String) -> Unit, onCategoryChange: (String) -> Unit, onDelete: () -> Unit) {
    val context = LocalContext.current
    val cardColor = noteBackgroundColor(note.color)
    val textColor = resolveUiTextColor(value = noteUiTextColor, background = cardColor)
    val secondaryTextColor = resolveSecondaryUiTextColor(value = noteUiTextColor, background = cardColor)
    val graphicColor = resolveUiGraphicColor(value = noteUiTextColor, background = cardColor)
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
        val cardContentModifier = if (isScrolling) {
            Modifier
        } else {
            Modifier.animateContentSize(animationSpec = tween(durationMillis = motionDuration))
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
                    LinkPreviewCard(url = noteLinks.first(), compact = true, textColorMode = noteUiTextColor,
                        deferLoad = isScrolling, modifier = Modifier.padding(end = 8.dp))
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

**Firma/entrada.** `fun ModernNoteCard(note: Note, attachments: List<Attachment>, fontFamily: FontFamily, fontSize: Float, noteUiTextColor: String, style: NoteCardStyle, optionMenuOrder: String, optionMenuHiddenItems: String, optionMenuShowIcons: Boolean, optionMenuTextColor: String, optionMenuOpacity: Float, priorityMenuHiddenItems: String, colorMenuHiddenItems: String, performanceMode: String, isScrolling: Boolean = false, onOpen: () -> Unit, onEdit: () -> Unit, onToggleFavorite: () -> Unit, onTogglePinned: () -> Unit, onPriorityChange: (Int) -> Unit, onColorChange: (String) -> Unit, onCategoryChange: (String) -> Unit, onDelete: () -> Unit) {`

**Parámetros.**
- `note: Note` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `attachments: List<Attachment>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontSize: Float` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `noteUiTextColor: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `style: NoteCardStyle` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `optionMenuOrder: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `optionMenuHiddenItems: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `optionMenuShowIcons: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `optionMenuTextColor: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `optionMenuOpacity: Float` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `priorityMenuHiddenItems: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `colorMenuHiddenItems: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `isScrolling: Boolean = false` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `onOpen: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onEdit: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onToggleFavorite: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onTogglePinned: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onPriorityChange: (Int) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onColorChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onCategoryChange: (String) -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.
- `onDelete: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; accede o prepara almacenamiento local/caché; ramifica o parametriza comportamiento según el perfil de rendimiento; expone o consume callbacks de interacción.

**Puntos que no conviene romper:** captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen; acota valores antes de usarlos para proteger rangos de UI/rendimiento.

### 4.2 `CategoryPill` — fun, líneas 528–553

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

**Firma/entrada.** `fun CategoryPill(category: String, fontFamily: FontFamily = FontFamily.Default) {`

**Parámetros.**
- `category: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: FontFamily = FontFamily.Default` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.3 `NoteCardAttachmentsPreview` — fun, líneas 556–588

```kotlin
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
```

**Firma/entrada.** `private fun NoteCardAttachmentsPreview(attachments: List<Attachment>, previewHeight: androidx.compose.ui.unit.Dp, cornerRadius: androidx.compose.ui.unit.Dp, fontFamily: FontFamily, performanceMode: String, deferHeavyLoads: Boolean, isScrolling: Boolean) {`

**Parámetros.**
- `attachments: List<Attachment>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `previewHeight: androidx.compose.ui.unit.Dp` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `cornerRadius: androidx.compose.ui.unit.Dp` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `deferHeavyLoads: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `isScrolling: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.4 `NoteCardAttachmentTile` — fun, líneas 591–731

```kotlin
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
```

**Firma/entrada.** `private fun NoteCardAttachmentTile(attachment: Attachment, modifier: Modifier, compact: Boolean, fontFamily: FontFamily, performanceMode: String, deferHeavyLoads: Boolean, isScrolling: Boolean) {`

**Parámetros.**
- `attachment: Attachment` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `modifier: Modifier` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `compact: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `performanceMode: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `deferHeavyLoads: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `isScrolling: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; integra el pipeline de miniaturas y caché de adjuntos; ramifica o parametriza comportamiento según el perfil de rendimiento.

**Puntos que no conviene romper:** captura estado/objetos de composición; sus claves determinan cuándo se reutilizan o reconstruyen.

### 4.5 `FileLikeFallbackTile` — fun, líneas 734–743

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

**Firma/entrada.** `private fun FileLikeFallbackTile(icon: ImageVector, label: String, compact: Boolean, fontFamily: FontFamily) {`

**Parámetros.**
- `icon: ImageVector` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `label: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `compact: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `fontFamily: FontFamily` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.6 `SmallDurationBadge` — fun, líneas 746–751

```kotlin
private fun SmallDurationBadge(duration: Long, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.padding(8.dp), color = Color.Black.copy(alpha = 0.68f), shape = RoundedCornerShape(8.dp)) {
        Text(text = formatSmallDuration(duration), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White,
            fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
```

**Firma/entrada.** `private fun SmallDurationBadge(duration: Long, modifier: Modifier = Modifier) {`

**Parámetros.**
- `duration: Long` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `modifier: Modifier = Modifier` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee.

### 4.7 `formatSmallDuration` — fun, líneas 753–758

```kotlin
private fun formatSmallDuration(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}
```

**Firma/entrada.** `private fun formatSmallDuration(durationMillis: Long): String {`

**Parámetros.**
- `durationMillis: Long` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.8 `ConfigurableDropdownMenuItem` — fun, líneas 761–773

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

**Firma/entrada.** `private fun ConfigurableDropdownMenuItem(label: String, icon: ImageVector, showIcon: Boolean, textColor: Color, onClick: () -> Unit) {`

**Parámetros.**
- `label: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `icon: ImageVector` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `showIcon: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `textColor: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onClick: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; expone o consume callbacks de interacción.

### 4.9 `PriorityMenuItem` — fun, líneas 776–799

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

**Firma/entrada.** `private fun PriorityMenuItem(label: String, selected: Boolean, textColor: Color, showIndicator: Boolean, onClick: () -> Unit) {`

**Parámetros.**
- `label: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `selected: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `textColor: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `showIndicator: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onClick: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; expone o consume callbacks de interacción.

### 4.10 `ColorMenuItem` — fun, líneas 802–816

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

**Firma/entrada.** `private fun ColorMenuItem(label: String, color: Color, textColor: Color, showSwatch: Boolean, onClick: () -> Unit) {`

**Parámetros.**
- `label: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `color: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `textColor: Color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `showSwatch: Boolean` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `onClick: () -> Unit` — Es un callback/lambda: el archivo no controla necesariamente qué efecto produce el llamador cuando se invoca.

**Funcionamiento observable.** participa en la UI declarativa Compose y puede recomponerse al cambiar el estado que lee; expone o consume callbacks de interacción.

### 4.11 `normalizedMenuOrder` — fun, líneas 818–829

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

**Firma/entrada.** `private fun normalizedMenuOrder(raw: String, validKeys: List<String>): List<String> {`

**Parámetros.**
- `raw: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `validKeys: List<String>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.12 `parseMenuKeys` — fun, líneas 831–838

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

**Firma/entrada.** `private fun parseMenuKeys(raw: String, validKeys: List<String>): Set<String> {`

**Parámetros.**
- `raw: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `validKeys: List<String>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

## 5. Variables y propiedades, una por una

Se detectaron **58 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 97 | `FavoriteGold` | `private val FavoriteGold: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val FavoriteGold = Color(0xFFF5A623)` |
| 99 | `MainOptionMenuKeys` | `private val MainOptionMenuKeys: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val MainOptionMenuKeys = listOf("edit", "favorite", "pin", "priority", "color", "move", "delete")` |
| 101 | `PriorityOptionKeys` | `private val PriorityOptionKeys: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val PriorityOptionKeys = listOf("none", "low", "medium", "high")` |
| 103 | `ColorOptionKeys` | `private val ColorOptionKeys: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val ColorOptionKeys = listOf("default", "yellow", "orange", "red", "pink", "purple", "blue", "cyan", "teal", "green", "mint",` |
| 125 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 126 | `cardColor` | `local/pública por contexto val cardColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val cardColor = noteBackgroundColor(note.color)` |
| 127 | `textColor` | `local/pública por contexto val textColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val textColor = resolveUiTextColor(value = noteUiTextColor, background = cardColor)` |
| 128 | `secondaryTextColor` | `local/pública por contexto val secondaryTextColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val secondaryTextColor = resolveSecondaryUiTextColor(value = noteUiTextColor, background = cardColor)` |
| 129 | `graphicColor` | `local/pública por contexto val graphicColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val graphicColor = resolveUiGraphicColor(value = noteUiTextColor, background = cardColor)` |
| 130 | `favoriteIconColor` | `local/pública por contexto val favoriteIconColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val favoriteIconColor = ensureUiContrast(preferred = FavoriteGold, background = cardColor, minimumContrast = 3f)` |
| 137 | `motionDuration` | `local/pública por contexto val motionDuration: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val motionDuration = if (isScrolling) 0 else AppMotion.duration(AppMotion.FAST, style.animationsEnabled, style.animationSpeed)` |
| 138 | `animatedCardColor` | `local/pública por contexto val animatedCardColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val animatedCardColor by` |
| 140 | `previewAttachments` | `local/pública por contexto val previewAttachments: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val previewAttachments = remember(attachments) {` |
| 143 | `noteLinks` | `local/pública por contexto val noteLinks: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val noteLinks = remember(note.content) {` |
| 146 | `displayContent` | `local/pública por contexto val displayContent: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val displayContent = remember(note.content, noteLinks) {` |
| 149 | `mainMenuExpanded` | `local/pública por contexto var mainMenuExpanded: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var mainMenuExpanded by` |
| 153 | `priorityMenuExpanded` | `local/pública por contexto var priorityMenuExpanded: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var priorityMenuExpanded by` |
| 157 | `colorMenuExpanded` | `local/pública por contexto var colorMenuExpanded: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var colorMenuExpanded by` |
| 161 | `categoryMenuExpanded` | `local/pública por contexto var categoryMenuExpanded: inferido` | `var` permite sustituir el valor durante la vida del ámbito. **Inicialización visible:** `var categoryMenuExpanded by` |
| 165 | `popupBaseColor` | `local/pública por contexto val popupBaseColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val popupBaseColor = when (optionMenuTextColor) {` |
| 169 | `popupAlpha` | `local/pública por contexto val popupAlpha: inferido` | `val` fija la referencia después de inicializarla; su inicialización está acotada a un intervalo explícito con `coerceIn`. **Inicialización visible:** `val popupAlpha = (optionMenuOpacity / 100f).coerceIn(0.35f, 1f)` |
| 170 | `popupColor` | `local/pública por contexto val popupColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val popupColor = popupBaseColor.copy(alpha = popupAlpha)` |
| 171 | `popupVisualBackground` | `local/pública por contexto val popupVisualBackground: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val popupVisualBackground = compositeUiColor(foreground = popupColor, background = cardColor)` |
| 172 | `menuTextColor` | `local/pública por contexto val menuTextColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val menuTextColor = resolveUiTextColor(value = optionMenuTextColor, background = popupVisualBackground)` |
| 173 | `mainMenuOrder` | `local/pública por contexto val mainMenuOrder: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val mainMenuOrder = remember(optionMenuOrder) {` |
| 176 | `hiddenMainMenuItems` | `local/pública por contexto val hiddenMainMenuItems: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val hiddenMainMenuItems = remember(optionMenuHiddenItems) {` |
| 179 | `visibleMainMenuItems` | `local/pública por contexto val visibleMainMenuItems: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val visibleMainMenuItems = remember(mainMenuOrder, hiddenMainMenuItems) {` |
| 187 | `hiddenPriorityItems` | `local/pública por contexto val hiddenPriorityItems: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val hiddenPriorityItems = remember(priorityMenuHiddenItems) {` |
| 190 | `visiblePriorityItems` | `local/pública por contexto val visiblePriorityItems: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val visiblePriorityItems = remember(hiddenPriorityItems) {` |
| 198 | `hiddenColorItems` | `local/pública por contexto val hiddenColorItems: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val hiddenColorItems = remember(colorMenuHiddenItems) {` |
| 201 | `visibleColorItems` | `local/pública por contexto val visibleColorItems: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val visibleColorItems = remember(hiddenColorItems) {` |
| 209 | `cardInteractionSource` | `local/pública por contexto val cardInteractionSource: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val cardInteractionSource = remember {` |
| 218 | `cardContentModifier` | `local/pública por contexto val cardContentModifier: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val cardContentModifier = if (isScrolling) {` |
| 394 | `priority` | `local/pública por contexto val priority: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val priority = when (key) {` |
| 400 | `label` | `local/pública por contexto val label: inferido` | `val` fija la referencia después de inicializarla; resuelve texto localizado desde recursos. **Inicialización visible:** `val label = stringResource(when (key) {` |
| 422 | `label` | `local/pública por contexto val label: inferido` | `val` fija la referencia después de inicializarla; resuelve texto localizado desde recursos. **Inicialización visible:** `val label = stringResource(when (key) {` |
| 438 | `optionColor` | `local/pública por contexto val optionColor: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val optionColor = when (key) {` |
| 529 | `work` | `local/pública por contexto val work: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val work = category == "work"` |
| 530 | `background` | `local/pública por contexto val background: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val background = if (work) {` |
| 535 | `foreground` | `local/pública por contexto val foreground: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val foreground = if (work) {` |
| 559 | `shape` | `local/pública por contexto val shape: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)` |
| 567 | `multiHeight` | `local/pública por contexto val multiHeight: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val multiHeight = (previewHeight.value * 1.35f).dp` |
| 569 | `rows` | `local/pública por contexto val rows: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val rows = attachments.take(4).chunked(2)` |
| 593 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = LocalContext.current` |
| 594 | `uri` | `local/pública por contexto val uri: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val uri = remember(attachment.uri) {` |
| 597 | `extension` | `local/pública por contexto val extension: inferido` | `val` fija la referencia después de inicializarla; participa en estado Compose; los cambios pueden provocar recomposición de los lectores. **Inicialización visible:** `val extension = remember(attachment.name, attachment.uri) {` |
| 613 | `preview` | `local/pública por contexto var preview: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `var preview by remember(attachment.uri, performanceMode) {` |
| 625 | `qualityPreview` | `local/pública por contexto val qualityPreview: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val qualityPreview = AttachmentPreviewCache.withPreviewPermit {` |
| 646 | `preview` | `local/pública por contexto var preview: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `var preview by remember(attachment.uri, performanceMode) {` |
| 658 | `qualityPreview` | `local/pública por contexto val qualityPreview: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val qualityPreview = AttachmentPreviewCache.withPreviewPermit {` |
| 670 | `bitmap` | `local/pública por contexto val bitmap: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val bitmap = preview?.bitmap` |
| 682 | `duration` | `local/pública por contexto val duration: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val duration = preview?.durationMillis ?: 0L` |
| 693 | `preview` | `local/pública por contexto var preview: inferido` | `var` permite sustituir el valor durante la vida del ámbito; participa en estado Compose; los cambios pueden provocar recomposición de los lectores; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `var preview by remember(attachment.uri, performanceMode) {` |
| 705 | `qualityPreview` | `local/pública por contexto val qualityPreview: inferido` | `val` fija la referencia después de inicializarla; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `val qualityPreview = AttachmentPreviewCache.withPreviewPermit {` |
| 754 | `totalSeconds` | `local/pública por contexto val totalSeconds: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val totalSeconds = durationMillis / 1000L` |
| 755 | `minutes` | `local/pública por contexto val minutes: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val minutes = totalSeconds / 60L` |
| 756 | `seconds` | `local/pública por contexto val seconds: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val seconds = totalSeconds % 60L` |
| 819 | `requested` | `local/pública por contexto val requested: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val requested = raw.split(",").map {` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **132 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 124–525 | 0 | ámbito/lambda anónima | `onColorChange: (String) -> Unit, onCategoryChange: (String) -> Unit, onDelete: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 140–142 | 1 | `remember` / memoria de composición | `val previewAttachments = remember(attachments) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 143–145 | 1 | `remember` / memoria de composición | `val noteLinks = remember(note.content) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 146–148 | 1 | `remember` / memoria de composición | `val displayContent = remember(note.content, noteLinks) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 150–152 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 154–156 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 158–160 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 162–164 | 1 | `remember` / memoria de composición | `remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 165–168 | 1 | selección `when` | `val popupBaseColor = when (optionMenuTextColor) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 173–175 | 1 | `remember` / memoria de composición | `val mainMenuOrder = remember(optionMenuOrder) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 176–178 | 1 | `remember` / memoria de composición | `val hiddenMainMenuItems = remember(optionMenuHiddenItems) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 179–186 | 1 | `remember` / memoria de composición | `val visibleMainMenuItems = remember(mainMenuOrder, hiddenMainMenuItems) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 180–183 | 2 | ámbito/lambda anónima | `mainMenuOrder.filterNot {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 183–185 | 2 | ámbito/lambda anónima | `}.ifEmpty {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 187–189 | 1 | `remember` / memoria de composición | `val hiddenPriorityItems = remember(priorityMenuHiddenItems) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 190–197 | 1 | `remember` / memoria de composición | `val visiblePriorityItems = remember(hiddenPriorityItems) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 191–194 | 2 | ámbito/lambda anónima | `PriorityOptionKeys.filterNot {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 194–196 | 2 | ámbito/lambda anónima | `}.ifEmpty {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 198–200 | 1 | `remember` / memoria de composición | `val hiddenColorItems = remember(colorMenuHiddenItems) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 201–208 | 1 | `remember` / memoria de composición | `val visibleColorItems = remember(hiddenColorItems) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 202–205 | 2 | ámbito/lambda anónima | `ColorOptionKeys.filterNot {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 205–207 | 2 | ámbito/lambda anónima | `}.ifEmpty {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 209–211 | 1 | `remember` / memoria de composición | `val cardInteractionSource = remember {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 217–524 | 1 | ámbito/lambda anónima | `elevation = CardDefaults.cardElevation(defaultElevation = style.elevation.dp)) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 218–220 | 2 | condición `if` | `val cardContentModifier = if (isScrolling) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 220–222 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 223–523 | 2 | bloque UI Compose | `Column(modifier = cardContentModifier) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 224–243 | 3 | condición `if` | `if (previewAttachments.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 245–522 | 3 | ámbito/lambda anónima | `top = (style.padding * 0.75f).dp, bottom = (style.padding * 0.90f).dp)) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 257–262 | 6 | bloque UI Compose | `Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 264–285 | 5 | condición `if` | `if (style.showFavorite) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 271–273 | 8 | condición `if` | `Icon(imageVector = if (favorite) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 277–279 | 8 | condición `if` | `tint = if (favorite) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 304–383 | 7 | iteración funcional | `visibleMainMenuItems.forEachIndexed {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 310–382 | 8 | selección `when` | `when (key) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 320–322 | 10 | condición `if` | `ConfigurableDropdownMenuItem(label = if (note.isFavorite) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 324–326 | 10 | condición `if` | `}, icon = if (note.isFavorite) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 334–336 | 10 | condición `if` | `ConfigurableDropdownMenuItem(label = if (note.isPinned) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 392–411 | 7 | iteración funcional | `visiblePriorityItems.forEach {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 394–399 | 8 | selección `when` | `val priority = when (key) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 400–405 | 8 | selección `when` | `val label = stringResource(when (key) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 420–459 | 7 | iteración funcional | `visibleColorItems.forEach {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 422–437 | 8 | selección `when` | `val label = stringResource(when (key) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 438–453 | 8 | selección `when` | `val optionColor = when (key) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 490–499 | 4 | condición `if` | `if (displayContent.isNotBlank()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 500–504 | 4 | condición `if` | `if (noteLinks.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 508–510 | 5 | condición `if` | `if (style.showCategory) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 512–520 | 5 | condición `if` | `if (style.showDate) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 513–515 | 6 | `remember` / memoria de composición | `Text(text = remember(note.createdAt) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 528–553 | 0 | ámbito/lambda anónima | `fun CategoryPill(category: String, fontFamily: FontFamily = FontFamily.Default) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 530–532 | 1 | condición `if` | `val background = if (work) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 532–534 | 1 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 535–537 | 1 | condición `if` | `val foreground = if (work) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 537–539 | 1 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 541–552 | 1 | ámbito/lambda anónima | `color = background) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 542–544 | 2 | condición `if` | `Text(text = stringResource(if (work) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 544–546 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 558–588 | 0 | ámbito/lambda anónima | `isScrolling: Boolean) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 560–566 | 1 | condición `if` | `if (attachments.size == 1) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 561–564 | 2 | bloque UI Compose | `Box(modifier = Modifier.fillMaxWidth().height(previewHeight).clip(shape)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 568–587 | 1 | bloque UI Compose | `Column(modifier = Modifier.fillMaxWidth().height(multiHeight).clip(shape), verticalArrangement = Arrangement.spacedBy(0.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 570–585 | 3 | iteración funcional | `rows.forEach { rowAttachments -> Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(0.dp)) {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 570–586 | 2 | iteración funcional | `rows.forEach { rowAttachments -> Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(0.dp)) {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 571–579 | 4 | condición `if` | `if (rowAttachments.size == 1) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 580–583 | 5 | iteración funcional | `rowAttachments.forEach { attachment -> NoteCardAttachmentTile(attachment = attachment, modifier = Modifier.weight(1f)` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 592–731 | 0 | ámbito/lambda anónima | `performanceMode: String, deferHeavyLoads: Boolean, isScrolling: Boolean) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 594–596 | 1 | `remember` / memoria de composición | `val uri = remember(attachment.uri) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 597–601 | 1 | `remember` / memoria de composición | `val extension = remember(attachment.name, attachment.uri) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 598–600 | 2 | ámbito/lambda anónima | `attachment.name?.substringAfterLast('.')?.lowercase().orEmpty().ifBlank {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 602–730 | 1 | bloque UI Compose | `Box(modifier = modifier) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 603–729 | 2 | ámbito/lambda anónima | `when {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 604–644 | 3 | ámbito/lambda anónima | `attachment.type == "image" -> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 613–615 | 4 | `remember` / memoria de composición | `var preview by remember(attachment.uri, performanceMode) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 616–636 | 4 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(attachment.uri, performanceMode, deferHeavyLoads, isScrolling) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 617–635 | 5 | condición `if` | `if (!deferHeavyLoads) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 618–630 | 6 | condición `if` | `if (performanceMode == "quality") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 619–629 | 7 | condición `if` | `if (!isScrolling) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 620–624 | 8 | condición `if` | `if (preview == null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 630–634 | 6 | condición `if` | `} else if (preview == null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 637–640 | 4 | condición `if` | `if (preview != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 645–686 | 3 | ámbito/lambda anónima | `attachment.type == "video" -> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 646–648 | 4 | `remember` / memoria de composición | `var preview by remember(attachment.uri, performanceMode) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 649–669 | 4 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(attachment.uri, performanceMode, deferHeavyLoads, isScrolling) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 650–668 | 5 | condición `if` | `if (!deferHeavyLoads) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 651–663 | 6 | condición `if` | `if (performanceMode == "quality") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 652–662 | 7 | condición `if` | `if (!isScrolling) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 653–657 | 8 | condición `if` | `if (preview?.bitmap == null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 663–667 | 6 | condición `if` | `} else if (preview?.bitmap == null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 671–674 | 4 | condición `if` | `if (bitmap != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 678–681 | 4 | bloque UI Compose | `Surface(modifier = Modifier.align(Alignment.Center), shape = CircleShape, color = Color.Black.copy(alpha = 0.56f)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 683–685 | 4 | condición `if` | `if (duration > 0L) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 687–691 | 3 | ámbito/lambda anónima | `attachment.type == "voice" \|\| attachment.type == "audio" -> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 692–724 | 3 | ámbito/lambda anónima | `extension == "pdf" -> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 693–695 | 4 | `remember` / memoria de composición | `var preview by remember(attachment.uri, performanceMode) {` | Conserva el resultado entre recomposiciones mientras las claves no cambien. No es persistencia permanente; su alcance es la composición. |
| 696–716 | 4 | `LaunchedEffect` / efecto Compose | `LaunchedEffect(attachment.uri, performanceMode, deferHeavyLoads, isScrolling) {` | Lanza una corrutina ligada a la composición y a sus claves. Si cambian las claves, Compose cancela/reinicia el efecto; por eso no debe duplicar trabajo no idempotente sin protección. |
| 697–715 | 5 | condición `if` | `if (!deferHeavyLoads) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 698–710 | 6 | condición `if` | `if (performanceMode == "quality") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 699–709 | 7 | condición `if` | `if (!isScrolling) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 700–704 | 8 | condición `if` | `if (preview == null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 710–714 | 6 | condición `if` | `} else if (preview == null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 717–720 | 4 | condición `if` | `if (preview != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 725–728 | 3 | ámbito/lambda anónima | `else -> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 734–743 | 0 | ámbito/lambda anónima | `private fun FileLikeFallbackTile(icon: ImageVector, label: String, compact: Boolean, fontFamily: FontFamily) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 736–742 | 1 | ámbito/lambda anónima | `horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 746–751 | 0 | ámbito/lambda anónima | `private fun SmallDurationBadge(duration: Long, modifier: Modifier = Modifier) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 747–750 | 1 | bloque UI Compose | `Surface(modifier = modifier.padding(8.dp), color = Color.Black.copy(alpha = 0.68f), shape = RoundedCornerShape(8.dp)) {` | Define una parte del árbol declarativo. Al cambiar estado leído por este ámbito, Compose puede recomponerlo; el bloque debe ser relativamente barato e idempotente. |
| 753–758 | 0 | ámbito/lambda anónima | `private fun formatSmallDuration(durationMillis: Long): String {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 761–773 | 0 | ámbito/lambda anónima | `private fun ConfigurableDropdownMenuItem(label: String, icon: ImageVector, showIcon: Boolean, textColor: Color, onClick: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 764–766 | 1 | ámbito/lambda anónima | `text = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 766–770 | 1 | condición `if` | `}, leadingIcon = if (showIcon) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 767–769 | 2 | ámbito/lambda anónima | `{` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 770–772 | 1 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 776–799 | 0 | ámbito/lambda anónima | `private fun PriorityMenuItem(label: String, selected: Boolean, textColor: Color, showIndicator: Boolean, onClick: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 779–788 | 1 | ámbito/lambda anónima | `text = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 783–785 | 2 | condición `if` | `fontWeight = if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 785–787 | 2 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 789–795 | 1 | condición `if` | `leadingIcon = if (showIndicator) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 790–794 | 2 | ámbito/lambda anónima | `{` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 791–793 | 3 | condición `if` | `if (selected) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 795–797 | 1 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 802–816 | 0 | ámbito/lambda anónima | `private fun ColorMenuItem(label: String, color: Color, textColor: Color, showSwatch: Boolean, onClick: () -> Unit) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 805–807 | 1 | ámbito/lambda anónima | `text = {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 808–812 | 1 | condición `if` | `leadingIcon = if (showSwatch) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 809–811 | 2 | ámbito/lambda anónima | `{` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 812–814 | 1 | ámbito/lambda anónima | `} else {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 818–829 | 0 | ámbito/lambda anónima | `private fun normalizedMenuOrder(raw: String, validKeys: List<String>): List<String> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 819–821 | 1 | ámbito/lambda anónima | `val requested = raw.split(",").map {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 821–824 | 1 | ámbito/lambda anónima | `}.filter {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 825–828 | 1 | ámbito/lambda anónima | `return requested + validKeys.filterNot {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 831–838 | 0 | ámbito/lambda anónima | `private fun parseMenuKeys(raw: String, validKeys: List<String>): Set<String> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 832–834 | 1 | ámbito/lambda anónima | `return raw.split(",").map {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 834–837 | 1 | ámbito/lambda anónima | `}.filter {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |

## 7. Side effects, rendimiento y lifecycle

- **Sistema de archivos / caché:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Audio / vibración:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Corrutinas:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Recomposición Compose:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Decodificación multimedia:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

La tarjeta recibe/deriva el estado de scrolling para decidir si inicia trabajo visual pesado. En máxima calidad puede usar un bitmap instantáneo ya en RAM, evitando que el usuario observe el salto de placeholder a miniatura durante el movimiento. El bitmap final puede reemplazarlo cuando el contexto deja de ser crítico para el frame.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**
