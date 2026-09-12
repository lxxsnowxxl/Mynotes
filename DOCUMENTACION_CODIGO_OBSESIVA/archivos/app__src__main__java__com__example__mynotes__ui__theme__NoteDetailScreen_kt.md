# NoteDetailScreen.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/theme/NoteDetailScreen.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `3d042b0eea6bc3d99b7936ec5dbba27b8722b59a35d83a1e75e9a6c8b538df98`  
**Líneas del código real:** 705

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Pantalla Compose de lectura/detalle de una nota. Presenta contenido, metadatos, enlaces y adjuntos, y expone acciones contextuales.

**Arquitectura.** Consume Note/Attachment y preferencias visuales; delega las operaciones reales al ViewModel mediante callbacks y reutiliza componentes de previews/menús.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **98 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`, `android.content.Intent`.

**Jetpack/Compose:** `androidx.compose.animation.Crossfade`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.ExperimentalLayoutApi`, `androidx.compose.foundation.layout.FlowRow`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.navigationBarsPadding`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Add`, `androidx.compose.material.icons.filled.CalendarMonth`, `androidx.compose.material.icons.filled.Delete`, `androidx.compose.material.icons.filled.Edit`, `androidx.compose.material.icons.filled.Folder`, `androidx.compose.material.icons.filled.MoreVert`, `androidx.compose.material.icons.filled.Palette`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material.icons.filled.PriorityHigh`, `androidx.compose.material.icons.filled.PushPin`, `androidx.compose.material.icons.filled.Share`, `androidx.compose.material.icons.filled.Star`, `androidx.compose.material.icons.filled.StarBorder`, `androidx.compose.material.icons.filled.Work`, `androidx.compose.material3.AssistChip`, `androidx.compose.material3.AssistChipDefaults`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.HorizontalDivider`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.material3.TopAppBar`, `androidx.compose.material3.TopAppBarDefaults`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.luminance`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`.

**Proyecto MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppAlertDialog`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.data.Note`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.InlineNoteAttachment`, `com.example.mynotes.ui.components.LinkPreviewCard`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.components.noteTextForDisplay`, `com.example.mynotes.ui.components.CategoryPill`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.compositeUiColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`, `com.example.mynotes.viewmodel.NoteViewModel`.

**Kotlin/corrutinas/Java:** `java.text.DateFormat`, `java.util.Date`.

## 3. Restricciones e invariantes visibles en el archivo

- **Límite numérico (1 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.
- **Límite visual (3 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.
- **Sin foco de popup (4 aparición/apariciones):** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android.

## 4. Bloques de código, uno por uno

### 4.1 `NoteDetailScreen` — fun, líneas 112–586

```kotlin
fun NoteDetailScreen(note: Note, noteViewModel: NoteViewModel, settings: AppSettings, onBack: () -> Unit, onEdit: (Note) -> Unit) {
    /*
     * Recordamos el Flow por id para no recrearlo/recolectarlo de nuevo
     * durante recomposiciones visuales de esta pantalla.
     */
    val attachmentsFlow = remember(note.id) {
            noteViewModel.getAttachments(note.id)
        }
    val attachments by
        attachmentsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current
    val noteLinks = remember(note.content) {
            extractLinkUrls(note.content)
        }
    val displayContent = remember(note.content, noteLinks) {
            noteTextForDisplay(content = note.content, links = noteLinks)
        }
    val fontFamily = remember(settings.font) {
            appFontFamily(settings.font)
        }
    val detailBackground = noteBackgroundColor(note.color)
    val noteTextColor = resolveUiTextColor(value = settings.noteUiTextColor, background = detailBackground)
    val noteSecondaryTextColor = resolveSecondaryUiTextColor(value = settings.noteUiTextColor, background = detailBackground)
    val noteGraphicColor = resolveUiGraphicColor(value = settings.noteUiTextColor, background = detailBackground)
    val favoriteIconColor = ensureUiContrast(preferred = FavoriteGold, background = detailBackground, minimumContrast = 3f)
    val attachmentAddButtonContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val attachmentAddButtonContentColor = resolveUiTextColor(value = settings.noteUiTextColor,
            background = attachmentAddButtonContainerColor)
    val useDarkDateChip = noteTextColor.luminance() > 0.7f
    val dateChipContainerColor = if (useDarkDateChip) {
            compositeUiColor(foreground = Color.Black.copy(alpha = 0.26f), background = detailBackground)
        } else {
            compositeUiColor(foreground = noteGraphicColor.copy(alpha = 0.08f), background = detailBackground)
        }
    val dateChipTextColor = if (useDarkDateChip) {
            Color.White
        } else {
            noteTextColor
        }
    val dateChipIconColor = if (useDarkDateChip) {
            Color.White.copy(alpha = 0.92f)
        } else {
            noteGraphicColor
        }
    val dateChipBorderColor = if (useDarkDateChip) {
            Color.White.copy(alpha = 0.30f)
        } else {
            noteGraphicColor.copy(alpha = 0.28f)
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
    var moveMenuExpanded by
        remember {
            mutableStateOf(false)
        }
    var deleteDialogVisible by
        remember {
            mutableStateOf(false)
        }
    val motionDuration = AppMotion.duration(AppMotion.FAST, settings.animationsEnabled, settings.animationSpeed)
    val popupBaseColor = when (settings.optionMenuTextColor) {
            "white" -> MaterialTheme.colorScheme.inverseSurface
            else -> MaterialTheme.colorScheme.surfaceContainerHigh
        }
    val popupAlpha = (settings.optionMenuOpacity / 100f).coerceIn(0.35f, 1f)
    val popupBackground = popupBaseColor.copy(alpha = popupAlpha)
    val popupVisualBackground = compositeUiColor(foreground = popupBackground, background = detailBackground)
    val optionMenuTextColor = resolveUiTextColor(value = settings.optionMenuTextColor, background = popupVisualBackground)
    val hiddenMainMenuItems = remember(settings.optionMenuHiddenItems) {
            parseDetailMenuKeys(settings.optionMenuHiddenItems, listOf("edit", "favorite", "pin", "priority", "color", "move", "delete"))
        }
    val visibleDetailMenuItems = remember(settings.optionMenuOrder, hiddenMainMenuItems) {
            normalizedDetailMenuOrder(settings.optionMenuOrder).filter {
                    it in
                        DetailMenuKeys
                }.filterNot {
                    it in
                        hiddenMainMenuItems
                }.ifEmpty {
                    listOf("edit")
                }
        }
    val hiddenPriorityMenuItems = remember(settings.priorityMenuHiddenItems) {
            parseDetailMenuKeys(settings.priorityMenuHiddenItems, DetailPriorityKeys)
        }
    val hiddenColorMenuItems = remember(settings.colorMenuHiddenItems) {
            parseDetailMenuKeys(settings.colorMenuHiddenItems, DetailColorKeys)
        }
    AnimatedScreenEntry(animationsEnabled = settings.animationsEnabled,
        animationSpeed = settings.animationSpeed) {
        Scaffold(containerColor = detailBackground,
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = detailBackground,
                            titleContentColor = noteTextColor,
                            navigationIconContentColor = noteTextColor,
                            actionIconContentColor = noteTextColor),
                title = {
                    Text(text = "")
                },
                navigationIcon = {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Back)
                            onBack()
                        }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.mock_back))
                    }
                },
                actions = {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Favorite)
                            noteViewModel.toggleFavorite(note)
                        }) {
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
                                        noteGraphicColor
                                    })
                        }
                    }
                    Box {
                        IconButton(onClick = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                mainMenuExpanded = true
                            }) {
                            Icon(imageVector = Icons.Default.MoreVert,
                                contentDescription = null)
                        }
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = mainMenuExpanded,
                            onDismissRequest = {
                                mainMenuExpanded = false
                            },
                            containerColor = popupBackground,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            visibleDetailMenuItems.forEach {
                                        key ->
                                    when (key) {
                                        "priority" -> {
                                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_priority), icon =
                                                    Icons.Default.PriorityHigh, showIcon = settings.optionMenuShowIcons, textColor =
                                                    optionMenuTextColor, fontFamily = fontFamily, onClick = {
                                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                                    mainMenuExpanded = false
                                                    priorityMenuExpanded = true
                                                })
                                        }
                                        "color" -> {
                                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_color), icon =
                                                    Icons.Default.Palette, showIcon = settings.optionMenuShowIcons, textColor =
                                                    optionMenuTextColor, fontFamily = fontFamily, onClick = {
                                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                                                    mainMenuExpanded = false
                                                    colorMenuExpanded = true
                                                })
                                        }
                                        else -> {
                                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_edit), icon =
                                                    Icons.Default.Edit, showIcon = settings.optionMenuShowIcons, textColor =
                                                    optionMenuTextColor, fontFamily = fontFamily, onClick = {
                                                    UiSoundPlayer.play(context = context, sound = UiSound.Edit)
                                                    mainMenuExpanded = false
                                                    onEdit(note)
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
                            containerColor = popupBackground,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            listOf("none" to
                                    (0 to
                                            R.string.mock_priority_none), "low" to
                                    (1 to
                                            R.string.mock_priority_low), "medium" to
                                    (2 to
                                            R.string.mock_priority_medium), "high" to
                                    (3 to
                                            R.string.mock_priority_high)).filterNot {
                                    it.first in
                                        hiddenPriorityMenuItems
                                }.ifEmpty {
                                    listOf("none" to
                                            (0 to
                                                    R.string.mock_priority_none))
                                }.forEach {
                                        item ->
                                    DropdownMenuItem(modifier = Modifier.defaultMinSize(minHeight = 38.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        text = {
                                            Text(text = stringResource(item.second.second),
                                                color = optionMenuTextColor,
                                                fontFamily = fontFamily,
                                                fontWeight = if (note.priority == item.second.first) {
                                                        FontWeight.Bold
                                                    } else {
                                                        FontWeight.Normal
                                                    })
                                        },
                                        onClick = {
                                            priorityMenuExpanded = false
                                            UiSoundPlayer.play(context = context, sound = UiSound.Priority)
                                            noteViewModel.changePriority(note = note, priority = item.second.first)
                                        })
                                }
                        }
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = colorMenuExpanded,
                            onDismissRequest = {
                                colorMenuExpanded = false
                            },
                            containerColor = popupBackground,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            listOf("default" to R.string.mock_color_default, "yellow" to R.string.mock_color_yellow,
                                "orange" to R.string.mock_color_orange, "red" to R.string.mock_color_red,
                                "pink" to R.string.mock_color_pink, "purple" to R.string.mock_color_purple,
                                "blue" to R.string.mock_color_blue, "cyan" to R.string.mock_color_cyan, "teal" to R.string.mock_color_teal,
                                "green" to R.string.mock_color_green, "mint" to R.string.mock_color_mint,
                                "lime" to R.string.mock_color_lime, "brown" to R.string.mock_color_brown, "gray" to R.string.mock_color_gray
                            ).filterNot {
                                    it.first in
                                        hiddenColorMenuItems
                                }.ifEmpty {
                                    listOf("default" to
                                            R.string.mock_color_default)
                                }.forEach {
                                        item ->
                                    DropdownMenuItem(modifier = Modifier.defaultMinSize(minHeight = 38.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        text = {
                                            Text(text = stringResource(item.second), color = optionMenuTextColor,
                                                fontFamily = fontFamily)
                                        },
                                        onClick = {
                                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Color)
                                            colorMenuExpanded = false
                                            noteViewModel.changeNoteColor(note = note, color = item.first)
                                        })
                                }
                        }
                    }
                })
        },
        bottomBar = {
            Surface(color = detailBackground,
                shadowElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically) {
                    DetailBottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.Share,
                        label = stringResource(R.string.mock_share),
                        color = noteTextColor,
                        fontFamily = fontFamily,
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Share)
                            shareNote(context = context, note = note)
                        })
                    DetailBottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.PushPin,
                        label = if (note.isPinned) {
                                stringResource(R.string.mock_unpin)
                            } else {
                                stringResource(R.string.mock_pin)
                            },
                        color = noteTextColor,
                        fontFamily = fontFamily,
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Pin)
                            noteViewModel.togglePinned(note)
                        })
                    Box(modifier = Modifier.weight(1f)) {
                        DetailBottomAction(modifier = Modifier.fillMaxWidth(), icon = Icons.Default.Folder,
                            label = stringResource(R.string.mock_move),
                            color = noteTextColor,
                            fontFamily = fontFamily,
                            onClick = {
                                UiSoundPlayer.playAction(context = context, action = UiActionSound.Move)
                                moveMenuExpanded = true
                            })
                        AppDropdownMenu(modifier = Modifier.heightIn(max = 300.dp).widthIn(min = 156.dp, max = 224.dp),
                            expanded = moveMenuExpanded,
                            onDismissRequest = {
                                moveMenuExpanded = false
                            },
                            containerColor = popupBackground,
                            properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_work), icon = Icons.Default.Work, showIcon =
                                    settings.optionMenuShowIcons, textColor = optionMenuTextColor, fontFamily = fontFamily, onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Category)
                                    moveMenuExpanded = false
                                    noteViewModel.changeCategory(note = note, category = "work")
                                })
                            DetailConfigurableMenuItem(label = stringResource(R.string.mock_personal), icon = Icons.Default.Person,
                                showIcon = settings.optionMenuShowIcons, textColor = optionMenuTextColor, fontFamily = fontFamily,
                                onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Category)
                                    moveMenuExpanded = false
                                    noteViewModel.changeCategory(note = note, category = "personal")
                                })
                        }
                    }
                    DetailBottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.Delete,
                        label = stringResource(R.string.mock_delete),
                        color = Color(0xFFD32F2F),
                        fontFamily = fontFamily,
                        onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                            deleteDialogVisible = true
                        })
                }
            }
        }) {
            paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter) {
            Column(modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth().verticalScroll(rememberScrollState()).padding(start = 22.dp,
                            end = 22.dp, bottom = 28.dp)) {
            Text(text = note.title.ifBlank {
                            stringResource(R.string.mock_untitled)
                        },
                color = noteTextColor,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (settings.fontSize + 13f).sp,
                lineHeight = (settings.fontSize + 17f).sp)
            Spacer(modifier = Modifier.height(11.dp))
            /*
             * Metadata como en el mockup.
             */
            FlowRow(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                itemVerticalAlignment = Alignment.CenterVertically) {
                PriorityPill(priority = note.priority, fontFamily = fontFamily, neutralForeground = dateChipTextColor, neutralBackground =
                        dateChipContainerColor, neutralBorder = dateChipBorderColor)
                CategoryPill(category = note.category, fontFamily = fontFamily)
                AssistChip(onClick = {
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = dateChipContainerColor, labelColor = dateChipTextColor,
                            leadingIconContentColor = dateChipIconColor),
                    border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = dateChipBorderColor),
                    label = {
                        Text(text = remember(note.createdAt) {
                                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(note.createdAt))
                                },
                            fontFamily = fontFamily,
                            fontSize = 11.sp)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp))
                    })
            }
            if (displayContent.isNotBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = displayContent,
                    color = noteSecondaryTextColor,
                    fontFamily = fontFamily,
                    fontSize = settings.fontSize.sp,
                    lineHeight = (settings.fontSize + 7f).sp)
            }
            if (noteLinks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                noteLinks.take(3).forEach { linkUrl -> LinkPreviewCard(url = linkUrl, compact = false,
                            textColorMode = settings.noteUiTextColor)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
            }
            Spacer(modifier = Modifier.height(26.dp))
            HorizontalDivider(color = noteGraphicColor.copy(alpha = 0.22f))
            Row(modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.mock_attachments),
                    modifier = Modifier.weight(1f),
                    color = noteTextColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp)
                Surface(shape = RoundedCornerShape(12.dp),
                    color = attachmentAddButtonContainerColor) {
                    IconButton(onClick = {
                            UiSoundPlayer.playAction(context = context, action = UiActionSound.Add)
                            onEdit(note)
                        },
                        modifier = Modifier.size(44.dp)) {
                        Icon(imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.mock_edit),
                            tint = attachmentAddButtonContentColor)
                    }
                }
            }
            if (attachments.isEmpty()) {
                Text(text = stringResource(R.string.mock_no_attachments),
                    color = noteSecondaryTextColor,
                    fontFamily = fontFamily,
                    fontSize = 13.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    attachments.forEachIndexed {
                                index, attachment ->
                            val baseDelay = when (settings.performanceMode) {
                                    "performance" -> 360L
                                    "balanced" -> 180L
                                    else -> 0L
                                }
                            val staggerDelay = when (settings.performanceMode) {
                                    "performance" -> 110L
                                    "balanced" -> 65L
                                    else -> 0L
                                }
                            InlineNoteAttachment(attachment = attachment,
                                fontFamily = fontFamily,
                                previewDelayMillis = baseDelay + index * staggerDelay,
                                performanceMode = settings.performanceMode)
                        }
                }
                }
            }
        }
    }
    if (deleteDialogVisible) {
        AppAlertDialog(onDismissRequest = {
                deleteDialogVisible = false
            },
            title = {
                Text(text = stringResource(R.string.mock_delete_note_title), fontFamily = fontFamily)
            },
            text = {
                Text(text = stringResource(R.string.mock_delete_note_message), fontFamily = fontFamily)
            },
            confirmButton = {
                TextButton(onClick = {
                        deleteDialogVisible = false
                        UiSoundPlayer.play(context = context, sound = UiSound.Delete)
                        noteViewModel.deleteNote(note)
                        onBack()
                    }) {
                    Text(text = stringResource(R.string.mock_delete),
                        color = Color(0xFFD32F2F),
                        fontFamily = fontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Cancel)
                        deleteDialogVisible = false
                    }) {
                    Text(text = stringResource(R.string.mock_cancel), fontFamily = fontFamily)
                }
            })
    }
    }
}
```

#### Qué hace y por qué existe

Componente de estado/lógica de presentación que coordina datos y operaciones para la UI sin depender de una instancia visual concreta.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.
- `noteViewModel: NoteViewModel` — `noteViewModel` recibe un valor de tipo `NoteViewModel`. El contrato no marca este parámetro como anulable.
- `settings: AppSettings` — `settings` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.
- `onBack: () -> Unit` — `onBack` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onEdit: (Note) -> Unit` — `onEdit` recibe un valor de tipo `(Note) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 117 | `val attachmentsFlow` | `inferido` | `remember(note.id) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 122 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 123 | `val noteLinks` | `inferido` | `remember(note.content) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 126 | `val displayContent` | `inferido` | `remember(note.content, noteLinks) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 129 | `val fontFamily` | `inferido` | `remember(settings.font) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 132 | `val detailBackground` | `inferido` | `noteBackgroundColor(note.color)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 133 | `val noteTextColor` | `inferido` | `resolveUiTextColor(value = settings.noteUiTextColor, background = detailBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 134 | `val noteSecondaryTextColor` | `inferido` | `resolveSecondaryUiTextColor(value = settings.noteUiTextColor, background = detailBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 135 | `val noteGraphicColor` | `inferido` | `resolveUiGraphicColor(value = settings.noteUiTextColor, background = detailBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 136 | `val favoriteIconColor` | `inferido` | `ensureUiContrast(preferred = FavoriteGold, background = detailBackground, minimumContrast = 3f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 137 | `val attachmentAddButtonContainerColor` | `inferido` | `MaterialTheme.colorScheme.surfaceContainerHigh` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 138 | `val attachmentAddButtonContentColor` | `inferido` | `resolveUiTextColor(value = settings.noteUiTextColor,` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 140 | `val useDarkDateChip` | `inferido` | `noteTextColor.luminance() > 0.7f` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 141 | `val dateChipContainerColor` | `inferido` | `if (useDarkDateChip) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 146 | `val dateChipTextColor` | `inferido` | `if (useDarkDateChip) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `val dateChipIconColor` | `inferido` | `if (useDarkDateChip) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 156 | `val dateChipBorderColor` | `inferido` | `if (useDarkDateChip) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 181 | `val motionDuration` | `inferido` | `AppMotion.duration(AppMotion.FAST, settings.animationsEnabled, settings.animationSpeed)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 182 | `val popupBaseColor` | `inferido` | `when (settings.optionMenuTextColor) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 186 | `val popupAlpha` | `inferido` | `(settings.optionMenuOpacity / 100f).coerceIn(0.35f, 1f)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 187 | `val popupBackground` | `inferido` | `popupBaseColor.copy(alpha = popupAlpha)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 188 | `val popupVisualBackground` | `inferido` | `compositeUiColor(foreground = popupBackground, background = detailBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 189 | `val optionMenuTextColor` | `inferido` | `resolveUiTextColor(value = settings.optionMenuTextColor, background = popupVisualBackground)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 190 | `val hiddenMainMenuItems` | `inferido` | `remember(settings.optionMenuHiddenItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 193 | `val visibleDetailMenuItems` | `inferido` | `remember(settings.optionMenuOrder, hiddenMainMenuItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 204 | `val hiddenPriorityMenuItems` | `inferido` | `remember(settings.priorityMenuHiddenItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 207 | `val hiddenColorMenuItems` | `inferido` | `remember(settings.colorMenuHiddenItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 534 | `val baseDelay` | `inferido` | `when (settings.performanceMode) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 539 | `val staggerDelay` | `inferido` | `when (settings.performanceMode) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 112 | `fun NoteDetailScreen(note: Note, noteViewModel: NoteViewModel, settings: AppSettings, onBack: () -> Unit, onEdit: (Note) -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 183 | `"white" -> MaterialTheme.colorScheme.inverseSurface` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 184 | `else -> MaterialTheme.colorScheme.surfaceContainerHigh` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 236 | `favorite ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 266 | `key ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 267 | `when (key) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 268 | `"priority" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 277 | `"color" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 286 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 321 | `item ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 361 | `item ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 446 | `paddingValues ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 487 | `if (displayContent.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 495 | `if (noteLinks.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 525 | `if (attachments.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 533 | `index, attachment ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 535 | `"performance" -> 360L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 536 | `"balanced" -> 180L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 537 | `else -> 0L` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 540 | `"performance" -> 110L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 541 | `"balanced" -> 65L` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 542 | `else -> 0L` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 554 | `if (deleteDialogVisible) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 4.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`collectAsStateWithLifecycle`:** Observa un flujo de forma consciente del lifecycle y entrega el último valor como estado de Compose.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `noteViewModel.getAttachments`, `attachmentsFlow.collectAsStateWithLifecycle`, `emptyList`, `extractLinkUrls`, `noteTextForDisplay`, `appFontFamily`, `noteBackgroundColor`, `resolveUiTextColor`, `resolveSecondaryUiTextColor`, `resolveUiGraphicColor`, `ensureUiContrast`, `noteTextColor.luminance`, `compositeUiColor`, `Color.Black.copy`, `noteGraphicColor.copy`, `Color.White.copy`, `mutableStateOf`, `AppMotion.duration`, `coerceIn`, `popupBaseColor.copy`, `parseDetailMenuKeys`, `listOf`, `normalizedDetailMenuOrder`, `AnimatedScreenEntry`, `Scaffold`, `TopAppBar`, `TopAppBarDefaults.topAppBarColors`, `Text`, `IconButton`, `UiSoundPlayer.playAction`, `onBack`, `Icon`, `stringResource`, `noteViewModel.toggleFavorite`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.2 `PriorityPill` — fun, líneas 589–631

```kotlin
private fun PriorityPill(priority: Int, fontFamily: FontFamily = FontFamily.Default, neutralForeground: Color = Color(0xFF4A4A4A),
    neutralBackground: Color = Color(0xFFF1F1F1), neutralBorder: Color = Color(0x334A4A4A)) {
    val label = when (priority) {
            3 -> stringResource(R.string.mock_priority_high)
            2 -> stringResource(R.string.mock_priority_medium)
            1 -> stringResource(R.string.mock_priority_low)
            else -> stringResource(R.string.mock_priority_none)
        }
    val foreground = when (priority) {
            3 -> Color(0xFFC62828)
            2 -> Color(0xFFB46A00)
            1 -> Color(0xFF4C6B8A)
            else -> neutralForeground
        }
    val background = if (priority == 0) {
            neutralBackground
        } else {
            foreground.copy(alpha = 0.10f)
        }
    Surface(shape = RoundedCornerShape(10.dp),
        color = background,
        border = if (priority == 0) {
                BorderStroke(width = 1.dp, color = neutralBorder)
            } else {
                null
            }) {
        Row(modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.PriorityHigh,
                contentDescription = null,
                tint = foreground,
                modifier = Modifier.size(15.dp))
            Text(text = label,
                modifier = Modifier.padding(start = 4.dp),
                color = foreground,
                fontFamily = fontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `priority: Int` — `priority` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `fontFamily: FontFamily = FontFamily.Default` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `FontFamily.Default`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `neutralForeground: Color = Color(0xFF4A4A4A)` — `neutralForeground` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Color(0xFF4A4A4A)`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `neutralBackground: Color = Color(0xFFF1F1F1)` — `neutralBackground` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Color(0xFFF1F1F1)`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `neutralBorder: Color = Color(0x334A4A4A)` — `neutralBorder` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Color(0x334A4A4A)`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 591 | `val label` | `inferido` | `when (priority) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 597 | `val foreground` | `inferido` | `when (priority) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 603 | `val background` | `inferido` | `if (priority == 0) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 592 | `3 -> stringResource(R.string.mock_priority_high)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 593 | `2 -> stringResource(R.string.mock_priority_medium)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 594 | `1 -> stringResource(R.string.mock_priority_low)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 595 | `else -> stringResource(R.string.mock_priority_none)` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 598 | `3 -> Color(0xFFC62828)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 599 | `2 -> Color(0xFFB46A00)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 600 | `1 -> Color(0xFF4C6B8A)` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 601 | `else -> neutralForeground` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Color`, `stringResource`, `foreground.copy`, `Surface`, `RoundedCornerShape`, `BorderStroke`, `Row`, `Modifier.padding`, `Icon`, `Modifier.size`, `Text`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.3 `DetailConfigurableMenuItem` — fun, líneas 634–650

```kotlin
private fun DetailConfigurableMenuItem(label: String, icon:
        androidx.compose.ui.graphics.vector.ImageVector, showIcon: Boolean, textColor: Color, fontFamily: FontFamily = FontFamily.Default,
    onClick: () -> Unit) {
    DropdownMenuItem(modifier = Modifier.defaultMinSize(minHeight = 38.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
        text = {
            Text(text = label, color = textColor, fontFamily = fontFamily, fontSize = 13.sp, maxLines = 2)
        },
        leadingIcon = if (showIcon) {
                {
                    Icon(imageVector = icon, contentDescription = null, tint = textColor)
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
- `icon: androidx.compose.ui.graphics.vector.ImageVector` — `icon` recibe un valor de tipo `androidx.compose.ui.graphics.vector.ImageVector`. El contrato no marca este parámetro como anulable.
- `showIcon: Boolean` — `showIcon` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily = FontFamily.Default` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `FontFamily.Default`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
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
| 636 | `onClick: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

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

### 4.4 `DetailBottomAction` — fun, líneas 653–670

```kotlin
private fun DetailBottomAction(modifier: Modifier = Modifier, icon:
        androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, fontFamily: FontFamily = FontFamily.Default,
    onClick: () -> Unit) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick,
            modifier = Modifier.size(48.dp)) {
            Icon(imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(21.dp))
        }
        Text(text = label,
            color = color,
            fontFamily = fontFamily,
            fontSize = 10.sp,
            maxLines = 1)
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `icon: androidx.compose.ui.graphics.vector.ImageVector` — `icon` recibe un valor de tipo `androidx.compose.ui.graphics.vector.ImageVector`. El contrato no marca este parámetro como anulable.
- `label: String` — `label` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `color: Color` — `color` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily = FontFamily.Default` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `FontFamily.Default`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
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
| 655 | `onClick: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Column`, `IconButton`, `Modifier.size`, `Icon`, `Text`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `shareNote` — fun, líneas 672–682

```kotlin
private fun shareNote(context: Context, note: Note) {
    val text = buildString {
            if (note.title.isNotBlank()) {
                append(note.title)
                append("\n\n")
            }
            append(note.content)
        }
    val intent = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
    context.startActivity(Intent.createChooser(intent, null))
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `context: Context` — `context` recibe un valor de tipo `Context`. El contrato no marca este parámetro como anulable. Da acceso a recursos y APIs Android; su vida útil debe corresponder con el uso realizado.
- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 673 | `val text` | `inferido` | `buildString {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 680 | `val intent` | `inferido` | `Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 674 | `if (note.title.isNotBlank()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Efectos secundarios y recursos

- **Navegación/Activity:** Modifica navegación, ciclo de vida o contenido de una Activity.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `note.title.isNotBlank`, `append`, `Intent`, `setType`, `putExtra`, `context.startActivity`, `Intent.createChooser`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `normalizedDetailMenuOrder` — fun, líneas 684–696

```kotlin
private fun normalizedDetailMenuOrder(raw: String): List<String> {
    val allKeys = listOf("edit", "favorite", "pin", "priority", "color", "move", "delete")
    val requested = raw.split(",").map {
                it.trim()
            }.filter {
                it in
                    allKeys
            }.distinct()
    return requested + allKeys.filterNot {
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

**Retorno:** `List<String>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 685 | `val allKeys` | `inferido` | `listOf("edit", "favorite", "pin", "priority", "color", "move", "delete")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 686 | `val requested` | `inferido` | `raw.split(",").map {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 692 | `return requested + allKeys.filterNot {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `raw.split`, `it.trim`, `distinct`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `parseDetailMenuKeys` — fun, líneas 698–705

```kotlin
private fun parseDetailMenuKeys(raw: String, validKeys: List<String>): Set<String> {
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
| 699 | `return raw.split(",").map {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `raw.split`, `it.trim`, `toSet`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 102 | `FavoriteGold` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 104 | `DetailMenuKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 106 | `DetailPriorityKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 108 | `DetailColorKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 117 | `attachmentsFlow` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 122 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 123 | `noteLinks` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 126 | `displayContent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 129 | `fontFamily` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 132 | `detailBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 133 | `noteTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 134 | `noteSecondaryTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 135 | `noteGraphicColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 136 | `favoriteIconColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 137 | `attachmentAddButtonContainerColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 138 | `attachmentAddButtonContentColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 140 | `useDarkDateChip` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 141 | `dateChipContainerColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 146 | `dateChipTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 151 | `dateChipIconColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 156 | `dateChipBorderColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 181 | `motionDuration` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 182 | `popupBaseColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 186 | `popupAlpha` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El inicializador limita explícitamente el valor a un rango, evitando que dimensiones/intensidades/sizes inválidos se propaguen. |
| 187 | `popupBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 188 | `popupVisualBackground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 189 | `optionMenuTextColor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 190 | `hiddenMainMenuItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 193 | `visibleDetailMenuItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 204 | `hiddenPriorityMenuItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 207 | `hiddenColorMenuItems` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 534 | `baseDelay` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 539 | `staggerDelay` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 591 | `label` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 597 | `foreground` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 603 | `background` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 673 | `text` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 680 | `intent` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 685 | `allKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 686 | `requested` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 112–586 | 0 | `fun NoteDetailScreen(note: Note, noteViewModel: NoteViewModel, settings: AppSettings, onBack: () -> Unit, onEdit: (Note) -> Unit)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 117–119 | 1 | `val attachmentsFlow = remember(note.id)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 123–125 | 1 | `val noteLinks = remember(note.content)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 126–128 | 1 | `val displayContent = remember(note.content, noteLinks)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 129–131 | 1 | `val fontFamily = remember(settings.font)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 141–143 | 1 | `val dateChipContainerColor = if (useDarkDateChip)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 143–145 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 146–148 | 1 | `val dateChipTextColor = if (useDarkDateChip)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 148–150 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 151–153 | 1 | `val dateChipIconColor = if (useDarkDateChip)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 153–155 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 156–158 | 1 | `val dateChipBorderColor = if (useDarkDateChip)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 158–160 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 162–164 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 166–168 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 170–172 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 174–176 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 178–180 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 182–185 | 1 | `val popupBaseColor = when (settings.optionMenuTextColor)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 190–192 | 1 | `val hiddenMainMenuItems = remember(settings.optionMenuHiddenItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 193–203 | 1 | `val visibleDetailMenuItems = remember(settings.optionMenuOrder, hiddenMainMenuItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 194–197 | 2 | `normalizedDetailMenuOrder(settings.optionMenuOrder).filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 197–200 | 2 | `}.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 200–202 | 2 | `}.ifEmpty` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 204–206 | 1 | `val hiddenPriorityMenuItems = remember(settings.priorityMenuHiddenItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 207–209 | 1 | `val hiddenColorMenuItems = remember(settings.colorMenuHiddenItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 211–585 | 1 | `animationSpeed = settings.animationSpeed)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 213–377 | 2 | `topBar =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 218–220 | 3 | `title =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 221–229 | 3 | `navigationIcon =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 222–225 | 4 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 225–228 | 4 | `})` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 230–376 | 3 | `actions =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 231–234 | 4 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 234–249 | 4 | `})` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 235–248 | 5 | `Crossfade(targetState = note.isFavorite, animationSpec = tween(durationMillis = motionDuration))` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 237–239 | 6 | `Icon(imageVector = if (favorite)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 239–241 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 243–245 | 6 | `tint = if (favorite)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 245–247 | 6 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 250–375 | 4 | `Box` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 251–254 | 5 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 254–257 | 5 | `})` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 260–262 | 5 | `onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 264–297 | 5 | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 265–296 | 6 | `visibleDetailMenuItems.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 267–295 | 7 | `when (key)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 268–276 | 8 | `"priority" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 271–275 | 9 | `optionMenuTextColor, fontFamily = fontFamily, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 277–285 | 8 | `"color" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 280–284 | 9 | `optionMenuTextColor, fontFamily = fontFamily, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 286–294 | 8 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 289–293 | 9 | `optionMenuTextColor, fontFamily = fontFamily, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 300–302 | 5 | `onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 304–340 | 5 | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 313–316 | 6 | `R.string.mock_priority_high)).filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 316–320 | 6 | `}.ifEmpty` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 320–339 | 6 | `}.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 324–333 | 7 | `text =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 328–330 | 8 | `fontWeight = if (note.priority == item.second.first)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 330–332 | 8 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 334–338 | 7 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 343–345 | 5 | `onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 347–374 | 5 | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 354–357 | 6 | `).filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 357–360 | 6 | `}.ifEmpty` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 360–373 | 6 | `}.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 364–367 | 7 | `text =` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 368–372 | 7 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 378–445 | 2 | `bottomBar =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 380–444 | 3 | `shadowElevation = 8.dp)` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 383–443 | 4 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 388–391 | 5 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 393–395 | 5 | `label = if (note.isPinned)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 395–397 | 5 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 400–403 | 5 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 404–434 | 5 | `Box(modifier = Modifier.weight(1f))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 409–412 | 6 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 415–417 | 6 | `onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 419–433 | 6 | `properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 421–425 | 7 | `settings.optionMenuShowIcons, textColor = optionMenuTextColor, fontFamily = fontFamily, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 428–432 | 7 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 439–442 | 5 | `onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 445–553 | 2 | `})` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 447–552 | 3 | `Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.TopCenter)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 449–551 | 4 | `end = 22.dp, bottom = 28.dp))` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 450–452 | 5 | `Text(text = note.title.ifBlank` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 465–486 | 5 | `itemVerticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 469–470 | 6 | `AssistChip(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 474–480 | 6 | `label =` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 475–477 | 7 | `Text(text = remember(note.createdAt)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 481–485 | 6 | `leadingIcon =` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 487–494 | 5 | `if (displayContent.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 495–501 | 5 | `if (noteLinks.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 497–500 | 6 | `noteLinks.take(3).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 505–524 | 5 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 513–523 | 6 | `color = attachmentAddButtonContainerColor)` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 514–517 | 7 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 518–522 | 7 | `modifier = Modifier.size(44.dp))` | Ámbito delimitado por llaves en profundidad 7. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 525–530 | 5 | `if (attachments.isEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 530–550 | 5 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 531–549 | 6 | `Column(verticalArrangement = Arrangement.spacedBy(14.dp))` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 532–548 | 7 | `attachments.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 534–538 | 8 | `val baseDelay = when (settings.performanceMode)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 539–543 | 8 | `val staggerDelay = when (settings.performanceMode)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 554–584 | 2 | `if (deleteDialogVisible)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 555–557 | 3 | `AppAlertDialog(onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 558–560 | 3 | `title =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 561–563 | 3 | `text =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 564–575 | 3 | `confirmButton =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 565–570 | 4 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 570–574 | 4 | `})` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 576–583 | 3 | `dismissButton =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 577–580 | 4 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 580–582 | 4 | `})` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 590–631 | 0 | `neutralBackground: Color = Color(0xFFF1F1F1), neutralBorder: Color = Color(0x334A4A4A))` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 591–596 | 1 | `val label = when (priority)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 597–602 | 1 | `val foreground = when (priority)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 603–605 | 1 | `val background = if (priority == 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 605–607 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 610–612 | 1 | `border = if (priority == 0)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 612–614 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 614–630 | 1 | `})` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 616–629 | 2 | `verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 636–650 | 0 | `onClick: () -> Unit)` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 639–641 | 1 | `text =` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 642–646 | 1 | `leadingIcon = if (showIcon)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 643–645 | 2 | `leadingIcon = if (showIcon) {` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 646–648 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 655–670 | 0 | `onClick: () -> Unit)` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 656–669 | 1 | `Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 658–663 | 2 | `modifier = Modifier.size(48.dp))` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 672–682 | 0 | `private fun shareNote(context: Context, note: Note)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 673–679 | 1 | `val text = buildString` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 674–677 | 2 | `if (note.title.isNotBlank())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 684–696 | 0 | `private fun normalizedDetailMenuOrder(raw: String): List<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 686–688 | 1 | `val requested = raw.split(",").map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 688–691 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 692–695 | 1 | `return requested + allKeys.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 698–705 | 0 | `private fun parseDetailMenuKeys(raw: String, validKeys: List<String>): Set<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 699–701 | 1 | `return raw.split(",").map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 701–704 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
