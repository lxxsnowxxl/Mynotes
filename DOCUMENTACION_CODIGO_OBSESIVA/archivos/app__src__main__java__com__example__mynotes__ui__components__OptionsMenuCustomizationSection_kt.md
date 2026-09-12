# OptionsMenuCustomizationSection.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/ui/components/OptionsMenuCustomizationSection.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `1efc2c1178ca4a0b5f734e21baf96d444186df0d8f1b6983c52165c224c1bf34`  
**Líneas del código real:** 368

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Sección de Configuración que permite personalizar el menú contextual de las notas: orden, visibilidad, iconos y apariencia relacionada.

**Arquitectura.** Produce los valores que NoteCard/NoteDetailScreen usan al construir sus menús sin cambiar la lógica funcional de las acciones.

**Flujo general.** El flujo se describe declaración por declaración en este documento.

## 2. Package e imports

El `package` es `com.example.mynotes.ui.components`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **48 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Jetpack/Compose:** `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.ExpandMore`, `androidx.compose.material.icons.filled.KeyboardArrowDown`, `androidx.compose.material.icons.filled.KeyboardArrowUp`, `androidx.compose.material.icons.filled.Refresh`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Slider`, `androidx.compose.material3.Switch`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableFloatStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextAlign`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`.

**Proyecto MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Kotlin/corrutinas/Java:** `kotlin.math.roundToInt`.

## 3. Restricciones e invariantes visibles en el archivo

- **Límite visual (2 aparición/apariciones):** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado.
- **Sin foco de popup (1 aparición/apariciones):** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android.

## 4. Bloques de código, uno por uno

### 4.1 `MenuOptionDescriptor` — class, líneas 52–71

```kotlin
private data class MenuOptionDescriptor(val key: String, val labelRes: Int)

private val MainMenuOptions = listOf(MenuOptionDescriptor("edit", R.string.mock_edit), MenuOptionDescriptor("favorite",
            R.string.mock_favorites), MenuOptionDescriptor("pin", R.string.mock_pin), MenuOptionDescriptor("priority",
            R.string.mock_priority), MenuOptionDescriptor("color", R.string.mock_color), MenuOptionDescriptor("move", R.string.mock_move),
        MenuOptionDescriptor("delete", R.string.mock_delete))
private val PriorityOptions = listOf(MenuOptionDescriptor("none", R.string.mock_priority_none), MenuOptionDescriptor("low",
            R.string.mock_priority_low), MenuOptionDescriptor("medium", R.string.mock_priority_medium), MenuOptionDescriptor("high",
            R.string.mock_priority_high))
private val ColorOptions = listOf(MenuOptionDescriptor("default", R.string.mock_color_default),
        MenuOptionDescriptor("yellow", R.string.mock_color_yellow), MenuOptionDescriptor("orange", R.string.mock_color_orange),
        MenuOptionDescriptor("red", R.string.mock_color_red), MenuOptionDescriptor("pink", R.string.mock_color_pink),
        MenuOptionDescriptor("purple", R.string.mock_color_purple), MenuOptionDescriptor("blue", R.string.mock_color_blue),
        MenuOptionDescriptor("cyan", R.string.mock_color_cyan), MenuOptionDescriptor("teal", R.string.mock_color_teal),
        MenuOptionDescriptor("green", R.string.mock_color_green), MenuOptionDescriptor("mint", R.string.mock_color_mint),
        MenuOptionDescriptor("lime", R.string.mock_color_lime), MenuOptionDescriptor("brown", R.string.mock_color_brown),
        MenuOptionDescriptor("gray", R.string.mock_color_gray))
private val DefaultMainOrder = MainMenuOptions.map {
            it.key
        }
```

#### Qué hace y por qué existe

Sección de Configuración que permite personalizar el menú contextual de las notas: orden, visibilidad, iconos y apariencia relacionada.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `val key: String` — `key` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val labelRes: Int` — `labelRes` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 54 | `val MainMenuOptions` | `inferido` | `listOf(MenuOptionDescriptor("edit", R.string.mock_edit), MenuOptionDescriptor("favorite",` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 58 | `val PriorityOptions` | `inferido` | `listOf(MenuOptionDescriptor("none", R.string.mock_priority_none), MenuOptionDescriptor("low",` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 61 | `val ColorOptions` | `inferido` | `listOf(MenuOptionDescriptor("default", R.string.mock_color_default),` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 69 | `val DefaultMainOrder` | `inferido` | `MainMenuOptions.map {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `listOf`, `MenuOptionDescriptor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.2 `OptionsMenuCustomizationSection` — fun, líneas 73–244

```kotlin
fun OptionsMenuCustomizationSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color,
    graphicColor: Color, onOrderChange: (String) -> Unit, onHiddenItemsChange: (String) -> Unit, onShowIconsChange: (Boolean) -> Unit,
    onTextColorChange: (String) -> Unit, onOpacityChange: (Float) -> Unit, onPriorityHiddenItemsChange: (String) -> Unit,
    onColorHiddenItemsChange: (String) -> Unit, onReset: () -> Unit) {
    val context = LocalContext.current
    val orderedKeys = remember(settings.optionMenuOrder) {
            normalizedOrder(settings.optionMenuOrder)
        }
    val hiddenMain = remember(settings.optionMenuHiddenItems) {
            parseKeys(settings.optionMenuHiddenItems, DefaultMainOrder)
        }
    val hiddenPriorities = remember(settings.priorityMenuHiddenItems) {
            parseKeys(settings.priorityMenuHiddenItems, PriorityOptions.map {
                    it.key
                })
        }
    val hiddenColors = remember(settings.colorMenuHiddenItems) {
            parseKeys(settings.colorMenuHiddenItems, ColorOptions.map {
                    it.key
                })
        }
    var opacity by
        remember {
            mutableFloatStateOf(settings.optionMenuOpacity)
        }
    LaunchedEffect(settings.optionMenuOpacity) {
        opacity = settings.optionMenuOpacity
    }
    var textColorMenuExpanded by
        remember {
            mutableStateOf(false)
        }
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = stringResource(R.string.option_menu_customization_title), color = textColor, fontFamily = fontFamily, fontWeight =
            FontWeight.Bold, fontSize = 20.sp)
    Text(text = stringResource(R.string.option_menu_customization_description), modifier = Modifier.padding(top = 2.dp), color =
            secondaryTextColor, fontFamily = fontFamily, fontSize = 12.sp)
    Spacer(modifier = Modifier.height(10.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp)) { panelColors ->
        ToggleRow(title = stringResource(R.string.option_menu_show_icons), checked = settings.optionMenuShowIcons, onCheckedChange =
                onShowIconsChange, fontFamily = fontFamily, textColor = panelColors.text)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(R.string.option_menu_text_color), color = panelColors.text, fontFamily = fontFamily, fontWeight =
                FontWeight.SemiBold, fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = optionMenuTextColorLabel(settings.optionMenuTextColor), modifier = Modifier.weight(1f), color =
                    panelColors.secondaryText, fontFamily = fontFamily, fontSize = 13.sp)
            androidx.compose.foundation.layout.Box {
                TextButton(onClick = {
                        UiSoundPlayer.playAction(context = context, action = UiActionSound.Menu)
                        textColorMenuExpanded = true
                    }) {
                    Text(text = stringResource(R.string.option_menu_change), color = panelColors.text, fontFamily = fontFamily)
                    Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null, tint = panelColors.text, modifier =
                            Modifier.size(18.dp))
                }
                AppDropdownMenu(modifier = Modifier.width(164.dp), expanded = textColorMenuExpanded, onDismissRequest = {
                        textColorMenuExpanded = false
                    }, properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true)) {
                    listOf("note" to
                            R.string.option_menu_text_follow_note, "black" to
                            R.string.option_menu_text_black, "white" to
                            R.string.option_menu_text_white).forEach {
                                option ->
                            DropdownMenuItem(modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 4.dp,
                                        vertical = 0.dp), text = {
                                    Text(text = stringResource(option.second), modifier = Modifier.fillMaxWidth(), color = panelColors.text,
                                        fontFamily = fontFamily, fontSize = 12.sp, maxLines = 1, textAlign = TextAlign.Center)
                                }, onClick = {
                                    UiSoundPlayer.playAction(context = context, action = UiActionSound.Select)
                                    textColorMenuExpanded = false
                                    onTextColorChange(option.first)
                                })
                        }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement =
                Arrangement.SpaceBetween) {
            Text(text = stringResource(R.string.option_menu_opacity), color = panelColors.text, fontFamily = fontFamily, fontWeight =
                    FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = "${opacity.roundToInt()}%", color = panelColors.secondaryText, fontFamily = fontFamily, fontSize = 12.sp)
        }
        Slider(value = opacity, onValueChange = {
                opacity = it
                UiSoundPlayer.playThrottled(context = context, sound = UiSound.SliderTick, minimumIntervalMs = 48L)
            }, onValueChangeFinished = {
                onOpacityChange(opacity)
            }, valueRange = 35f..100f)
    }
    Spacer(modifier = Modifier.height(12.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp)) { panelColors ->
        Text(text = stringResource(R.string.option_menu_main_actions), color = panelColors.text, fontFamily = fontFamily, fontWeight =
                FontWeight.Bold)
        Text(text = stringResource(R.string.option_menu_main_actions_hint), modifier = Modifier.padding(top = 2.dp, bottom = 4.dp), color =
                panelColors.secondaryText, fontFamily = fontFamily, fontSize = 12.sp)
        val enabledCount = orderedKeys.count {
                    it !in hiddenMain
                }
        orderedKeys.forEachIndexed {
                    index, key ->
                val descriptor = MainMenuOptions.first {
                            it.key == key
                        }
                val isVisible = key !in
                        hiddenMain
                MenuOrderRow(label = stringResource(descriptor.labelRes), visible = isVisible, canHide = !isVisible || enabledCount >
                                1, canMoveUp = index >
                            0, canMoveDown = index <
                            orderedKeys.lastIndex, onVisibleChange = {
                            visible ->
                        val next = hiddenMain.toMutableSet()
                        if (visible) {
                            next.remove(key)
                        } else {
                            next.add(key)
                        }
                        onHiddenItemsChange(next.joinToString(","))
                    }, onMoveUp = {
                        val next = orderedKeys.toMutableList()
                        val previous = index - 1
                        val temp = next[previous]
                        next[previous] = next[index]
                        next[index] = temp
                        onOrderChange(next.joinToString(","))
                    }, onMoveDown = {
                        val next = orderedKeys.toMutableList()
                        val following = index + 1
                        val temp = next[following]
                        next[following] = next[index]
                        next[index] = temp
                        onOrderChange(next.joinToString(","))
                    }, fontFamily = fontFamily, textColor = panelColors.text)
            }
    }
    Spacer(modifier = Modifier.height(8.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp)) { panelColors ->
        Text(text = stringResource(R.string.option_menu_priority_submenu), color = panelColors.text, fontFamily = fontFamily, fontWeight =
                FontWeight.Bold)
        val visiblePriorityCount = PriorityOptions.count {
                    it.key !in
                        hiddenPriorities
                }
        CompactToggleGrid(options = PriorityOptions, hiddenItems = hiddenPriorities, visibleCount = visiblePriorityCount,
            onHiddenItemsChange = onPriorityHiddenItemsChange, fontFamily = fontFamily, textColor = panelColors.text)
    }
    Spacer(modifier = Modifier.height(8.dp))
    SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp)) { panelColors ->
        Text(text = stringResource(R.string.option_menu_color_submenu), color = panelColors.text, fontFamily = fontFamily, fontWeight =
                FontWeight.Bold)
        val visibleColorCount = ColorOptions.count {
                    it.key !in
                        hiddenColors
                }
        CompactToggleGrid(options = ColorOptions, hiddenItems = hiddenColors, visibleCount = visibleColorCount, onHiddenItemsChange =
                onColorHiddenItemsChange, fontFamily = fontFamily, textColor = panelColors.text)
    }
    Spacer(modifier = Modifier.height(8.dp))
    // El restablecimiento queda como una acción limpia, sin el panel/sombreado
    // oscuro que antes envolvía al botón. Se conserva la misma alineación.
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Restore)
                onReset()
            }) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
            Text(text = stringResource(R.string.option_menu_reset), modifier = Modifier.padding(start = 6.dp), color =
                    MaterialTheme.colorScheme.onBackground, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
        }
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `settings: AppSettings` — `settings` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `secondaryTextColor: Color` — `secondaryTextColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `graphicColor: Color` — `graphicColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `onOrderChange: (String) -> Unit` — `onOrderChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onHiddenItemsChange: (String) -> Unit` — `onHiddenItemsChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onShowIconsChange: (Boolean) -> Unit` — `onShowIconsChange` recibe un valor de tipo `(Boolean) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onTextColorChange: (String) -> Unit` — `onTextColorChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onOpacityChange: (Float) -> Unit` — `onOpacityChange` recibe un valor de tipo `(Float) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onPriorityHiddenItemsChange: (String) -> Unit` — `onPriorityHiddenItemsChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onColorHiddenItemsChange: (String) -> Unit` — `onColorHiddenItemsChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onReset: () -> Unit` — `onReset` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 77 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 78 | `val orderedKeys` | `inferido` | `remember(settings.optionMenuOrder) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 81 | `val hiddenMain` | `inferido` | `remember(settings.optionMenuHiddenItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 84 | `val hiddenPriorities` | `inferido` | `remember(settings.priorityMenuHiddenItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 89 | `val hiddenColors` | `inferido` | `remember(settings.colorMenuHiddenItems) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 170 | `val enabledCount` | `inferido` | `orderedKeys.count {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 175 | `val descriptor` | `inferido` | `MainMenuOptions.first {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 178 | `val isVisible` | `inferido` | `key !in` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 185 | `val next` | `inferido` | `hiddenMain.toMutableSet()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 193 | `val next` | `inferido` | `orderedKeys.toMutableList()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 194 | `val previous` | `inferido` | `index - 1` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 195 | `val temp` | `inferido` | `next[previous]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 200 | `val next` | `inferido` | `orderedKeys.toMutableList()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 201 | `val following` | `inferido` | `index + 1` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 202 | `val temp` | `inferido` | `next[following]` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 213 | `val visiblePriorityCount` | `inferido` | `PriorityOptions.count {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 224 | `val visibleColorCount` | `inferido` | `ColorOptions.count {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 74 | `graphicColor: Color, onOrderChange: (String) -> Unit, onHiddenItemsChange: (String) -> Unit, onShowIconsChange: (Boolean) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 75 | `onTextColorChange: (String) -> Unit, onOpacityChange: (Float) -> Unit, onPriorityHiddenItemsChange: (String) -> Unit,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 76 | `onColorHiddenItemsChange: (String) -> Unit, onReset: () -> Unit) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 98 | `LaunchedEffect(settings.optionMenuOpacity) {` | Efecto de Compose basado en corrutina: se inicia cuando entra en composición y se reinicia si cambian sus claves; se cancela al salir de composición. |
| 136 | `option ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 174 | `index, key ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 184 | `visible ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 186 | `if (visible) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.
- **Sin foco de popup:** El popup se configura para no tomar el foco de ventana; en este proyecto ayuda a preservar el modo inmersivo y evita reaparición indeseada de la navegación Android. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`remember`:** Conserva valores entre recomposiciones bajo las mismas claves.
- **`mutableStateOf`:** Crea estado observable; las lecturas registradas por Compose quedan enlazadas a sus cambios.
- **`LaunchedEffect`:** Ejecuta una corrutina ligada al ciclo de vida de la composición y a sus claves.
- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Estado Compose:** Declara o utiliza estado observable de Compose; una escritura en ese estado puede desencadenar recomposición de los lectores.
- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `remember`, `normalizedOrder`, `parseKeys`, `mutableFloatStateOf`, `LaunchedEffect`, `mutableStateOf`, `Spacer`, `Modifier.height`, `Text`, `stringResource`, `Modifier.padding`, `SettingsSectionPanel`, `PaddingValues`, `ToggleRow`, `Row`, `Modifier.fillMaxWidth`, `optionMenuTextColorLabel`, `Modifier.weight`, `TextButton`, `UiSoundPlayer.playAction`, `Icon`, `Modifier.size`, `AppDropdownMenu`, `Modifier.width`, `PopupProperties`, `listOf`, `DropdownMenuItem`, `onTextColorChange`, `Slider`, `UiSoundPlayer.playThrottled`, `onOpacityChange`, `MenuOrderRow`, `hiddenMain.toMutableSet`, `next.remove`, `next.add`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar `focusable = false` en estos popups si se quiere mantener el comportamiento inmersivo que evita que reaparezcan los botones de navegación del sistema.
- No convertir estado de Compose en variables ordinarias sin considerar recomposición y persistencia temporal.

### 4.3 `MenuOrderRow` — fun, líneas 247–278

```kotlin
private fun MenuOrderRow(label: String, visible: Boolean, canHide: Boolean, canMoveUp: Boolean, canMoveDown: Boolean,
    onVisibleChange: (Boolean) -> Unit, onMoveUp: () -> Unit, onMoveDown: () -> Unit, fontFamily: FontFamily, textColor: Color) {
    val context = LocalContext.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, modifier = Modifier.weight(1f), color = textColor, fontFamily = fontFamily, fontSize = 14.sp)
        IconButton(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Move)
                onMoveUp()
            }, enabled = canMoveUp, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = stringResource(R.string.option_menu_move_up), tint =
                    textColor.copy(alpha = if (canMoveUp) {
                                1f
                            } else {
                                0.28f
                            }))
        }
        IconButton(onClick = {
                UiSoundPlayer.playAction(context = context, action = UiActionSound.Move)
                onMoveDown()
            }, enabled = canMoveDown, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = stringResource(R.string.option_menu_move_down), tint =
                    textColor.copy(alpha = if (canMoveDown) {
                                1f
                            } else {
                                0.28f
                            }))
        }
        Switch(checked = visible, onCheckedChange = { newChecked -> UiSoundPlayer.playToggle(context = context, checked = newChecked)
                onVisibleChange(newChecked)
            }, enabled = canHide)
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `label: String` — `label` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `visible: Boolean` — `visible` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `canHide: Boolean` — `canHide` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `canMoveUp: Boolean` — `canMoveUp` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `canMoveDown: Boolean` — `canMoveDown` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `onVisibleChange: (Boolean) -> Unit` — `onVisibleChange` recibe un valor de tipo `(Boolean) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onMoveUp: () -> Unit` — `onMoveUp` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `onMoveDown: () -> Unit` — `onMoveDown` recibe un valor de tipo `() -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 249 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 248 | `onVisibleChange: (Boolean) -> Unit, onMoveUp: () -> Unit, onMoveDown: () -> Unit, fontFamily: FontFamily, textColor: Color) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

#### Semántica Compose/lifecycle

- **Callbacks `onClick`:** La lógica asociada se ejecuta por evento de usuario, no durante la composición inicial.
- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Row`, `Modifier.fillMaxWidth`, `padding`, `Text`, `Modifier.weight`, `IconButton`, `UiSoundPlayer.playAction`, `onMoveUp`, `Modifier.size`, `Icon`, `stringResource`, `textColor.copy`, `onMoveDown`, `Switch`, `UiSoundPlayer.playToggle`, `onVisibleChange`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `CompactToggleGrid` — fun, líneas 281–309

```kotlin
private fun CompactToggleGrid(options: List<MenuOptionDescriptor>, hiddenItems: Set<String>, visibleCount: Int,
    onHiddenItemsChange: (String) -> Unit, fontFamily: FontFamily, textColor: Color) {
    options.chunked(2).forEach {
                rowOptions ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment =
                    Alignment.CenterVertically) {
                rowOptions.forEach {
                            option ->
                        val visible = option.key !in
                                hiddenItems
                        ToggleRow(title = stringResource(option.labelRes), checked = visible, onCheckedChange = {
                                checked ->
                                val next = hiddenItems.toMutableSet()
                                if (checked) {
                                    next.remove(option.key)
                                } else if (visibleCount >
                                    1) {
                                    next.add(option.key)
                                }
                                onHiddenItemsChange(next.joinToString(","))
                            }, enabled = !visible || visibleCount >
                                        1, fontFamily = fontFamily, textColor = textColor, modifier = Modifier.weight(1f), compact = true)
                    }
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `options: List<MenuOptionDescriptor>` — `options` recibe un valor de tipo `List<MenuOptionDescriptor>`. El contrato no marca este parámetro como anulable.
- `hiddenItems: Set<String>` — `hiddenItems` recibe un valor de tipo `Set<String>`. El contrato no marca este parámetro como anulable.
- `visibleCount: Int` — `visibleCount` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `onHiddenItemsChange: (String) -> Unit` — `onHiddenItemsChange` recibe un valor de tipo `(String) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 289 | `val visible` | `inferido` | `option.key !in` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 293 | `val next` | `inferido` | `hiddenItems.toMutableSet()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 282 | `onHiddenItemsChange: (String) -> Unit, fontFamily: FontFamily, textColor: Color) {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 284 | `rowOptions ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 288 | `option ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 292 | `checked ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 294 | `if (checked) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 304 | `if (rowOptions.size == 1) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `options.chunked`, `Row`, `Modifier.fillMaxWidth`, `Arrangement.spacedBy`, `ToggleRow`, `stringResource`, `hiddenItems.toMutableSet`, `next.remove`, `next.add`, `onHiddenItemsChange`, `next.joinToString`, `Modifier.weight`, `Spacer`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `ToggleRow` — fun, líneas 312–337

```kotlin
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color,
    enabled: Boolean = true, modifier: Modifier = Modifier, compact: Boolean = false) {
    val context = LocalContext.current
    Row(modifier = modifier.then(if (compact) {
                        Modifier
                    } else {
                        Modifier.fillMaxWidth()
                    }).padding(vertical = 0.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, modifier = Modifier.weight(1f), color = textColor.copy(alpha = if (enabled) {
                            1f
                        } else {
                            0.42f
                        }), fontFamily = fontFamily, fontSize = if (compact) {
                    13.sp
                } else {
                    14.sp
                }, maxLines = if (compact) {
                    2
                } else {
                    Int.MAX_VALUE
                })
        Switch(checked = checked, onCheckedChange = { newChecked -> UiSoundPlayer.playToggle(context = context, checked = newChecked)
                onCheckedChange(newChecked)
            }, enabled = enabled)
    }
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `checked: Boolean` — `checked` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.
- `onCheckedChange: (Boolean) -> Unit` — `onCheckedChange` recibe un valor de tipo `(Boolean) -> Unit`. El contrato no marca este parámetro como anulable. Es un callback: el bloque no posee necesariamente la acción final, sino que notifica/delega al consumidor.
- `fontFamily: FontFamily` — `fontFamily` recibe un valor de tipo `FontFamily`. El contrato no marca este parámetro como anulable.
- `textColor: Color` — `textColor` recibe un valor de tipo `Color`. El contrato no marca este parámetro como anulable.
- `enabled: Boolean = true` — `enabled` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `true`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `modifier: Modifier = Modifier` — `modifier` recibe un valor de tipo `Modifier`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `Modifier`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `compact: Boolean = false` — `compact` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `false`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 314 | `val context` | `inferido` | `LocalContext.current` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 312 | `private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color,` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Límite visual:** La UI impone un límite explícito de tamaño o cantidad de líneas para impedir crecimiento visual no controlado. Apariciones en este bloque: 1.

#### Semántica Compose/lifecycle

- **`Modifier`:** La cadena de modificadores define tamaño, interacción, dibujo, semántica o posicionamiento; el orden de los modificadores puede ser significativo.

#### Efectos secundarios y recursos

- **Sonido/háptica:** Produce feedback perceptible para el usuario; debe ejecutarse en respuesta a la interacción/estado correspondiente.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `Row`, `modifier.then`, `Modifier.fillMaxWidth`, `padding`, `Text`, `Modifier.weight`, `textColor.copy`, `Switch`, `UiSoundPlayer.playToggle`, `onCheckedChange`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `optionMenuTextColorLabel` — fun, líneas 340–346

```kotlin
private fun optionMenuTextColorLabel(value: String): String {
    return stringResource(when (value) {
            "black" -> R.string.option_menu_text_black
            "white" -> R.string.option_menu_text_white
            else -> R.string.option_menu_text_follow_note
        })
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `@Composable` convierte la función en una unidad declarativa de Compose y la somete a reglas de recomposición.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 341 | `return stringResource(when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 342 | `"black" -> R.string.option_menu_text_black` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 343 | `"white" -> R.string.option_menu_text_white` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 344 | `else -> R.string.option_menu_text_follow_note` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `stringResource`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.7 `normalizedOrder` — fun, líneas 348–359

```kotlin
private fun normalizedOrder(raw: String): List<String> {
    val requested = raw.split(",").map {
                it.trim()
            }.filter {
                it in
                    DefaultMainOrder
            }.distinct()
    return requested + DefaultMainOrder.filterNot {
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
| 349 | `val requested` | `inferido` | `raw.split(",").map {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 355 | `return requested + DefaultMainOrder.filterNot {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `raw.split`, `it.trim`, `distinct`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `parseKeys` — fun, líneas 361–368

```kotlin
private fun parseKeys(raw: String, valid: List<String>): Set<String> {
    return raw.split(",").map {
            it.trim()
        }.filter {
            it in
                valid
        }.toSet()
}
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `raw: String` — `raw` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `valid: List<String>` — `valid` recibe un valor de tipo `List<String>`. El contrato no marca este parámetro como anulable.

**Retorno:** `Set<String>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 362 | `return raw.split(",").map {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `raw.split`, `it.trim`, `toSet`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 54 | `MainMenuOptions` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 58 | `PriorityOptions` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 61 | `ColorOptions` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 69 | `DefaultMainOrder` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 77 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 78 | `orderedKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 81 | `hiddenMain` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 84 | `hiddenPriorities` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 89 | `hiddenColors` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. La inicialización está ligada a `remember`, por lo que el valor se conserva entre recomposiciones mientras permanezca la misma instancia lógica de la composición y no cambien sus claves. |
| 170 | `enabledCount` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 175 | `descriptor` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 178 | `isVisible` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. |
| 185 | `next` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 193 | `next` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 194 | `previous` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 195 | `temp` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 200 | `next` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 201 | `following` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 202 | `temp` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 213 | `visiblePriorityCount` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 224 | `visibleColorCount` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 249 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 289 | `visible` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 293 | `next` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 314 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 349 | `requested` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 69–71 | 0 | `private val DefaultMainOrder = MainMenuOptions.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 76–244 | 0 | `onColorHiddenItemsChange: (String) -> Unit, onReset: () -> Unit)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 78–80 | 1 | `val orderedKeys = remember(settings.optionMenuOrder)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 81–83 | 1 | `val hiddenMain = remember(settings.optionMenuHiddenItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 84–88 | 1 | `val hiddenPriorities = remember(settings.priorityMenuHiddenItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 85–87 | 2 | `parseKeys(settings.priorityMenuHiddenItems, PriorityOptions.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 89–93 | 1 | `val hiddenColors = remember(settings.colorMenuHiddenItems)` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 90–92 | 2 | `parseKeys(settings.colorMenuHiddenItems, ColorOptions.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 95–97 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 98–100 | 1 | `LaunchedEffect(settings.optionMenuOpacity)` | Efecto de Compose con corrutina: su ciclo de vida depende de las claves; se cancela/reinicia al cambiar dichas claves o salir de composición. |
| 102–104 | 1 | `remember` | Inicializador memorizado de Compose: el bloque calcula el valor solo cuando la entrada a la composición o sus claves requieren recrearlo. |
| 111–163 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 117–149 | 2 | `Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 120–148 | 3 | `androidx.compose.foundation.layout.Box` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 121–124 | 4 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 124–128 | 4 | `})` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 129–131 | 4 | `AppDropdownMenu(modifier = Modifier.width(164.dp), expanded = textColorMenuExpanded, onDismissRequest =` | Callback de cierre: se ejecuta cuando la UI solicita descartar/cerrar el popup, diálogo o superficie asociada. |
| 131–147 | 4 | `}, properties = PopupProperties(focusable = false, dismissOnBackPress = true, dismissOnClickOutside = true))` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 135–146 | 5 | `R.string.option_menu_text_white).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 138–141 | 6 | `vertical = 0.dp), text =` | Ámbito delimitado por llaves en profundidad 6. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 141–145 | 6 | `}, onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 152–156 | 2 | `Arrangement.SpaceBetween)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 157–160 | 2 | `Slider(value = opacity, onValueChange =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 160–162 | 2 | `}, onValueChangeFinished =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 165–208 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 170–172 | 2 | `val enabledCount = orderedKeys.count` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 173–207 | 2 | `orderedKeys.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 175–177 | 3 | `val descriptor = MainMenuOptions.first` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 183–192 | 3 | `orderedKeys.lastIndex, onVisibleChange =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 186–188 | 4 | `if (visible)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 188–190 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 192–199 | 3 | `}, onMoveUp =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 199–206 | 3 | `}, onMoveDown =` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 210–219 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 213–216 | 2 | `val visiblePriorityCount = PriorityOptions.count` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 221–230 | 1 | `SettingsSectionPanel(textColorMode = settings.textColor, contentPadding = PaddingValues(12.dp))` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 224–227 | 2 | `val visibleColorCount = ColorOptions.count` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 234–243 | 1 | `Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 235–238 | 2 | `TextButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 238–242 | 2 | `})` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 248–278 | 0 | `onVisibleChange: (Boolean) -> Unit, onMoveUp: () -> Unit, onMoveDown: () -> Unit, fontFamily: FontFamily, textColor: Color)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 250–277 | 1 | `Row(modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp), verticalAlignment = Alignment.CenterVertically)` | Lambda de contenido de UI Compose: declara hijos y configuración visual dentro del contenedor. El orden y los `Modifier` influyen en medida, posición, dibujo e interacción. |
| 252–255 | 2 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 255–262 | 2 | `}, enabled = canMoveUp, modifier = Modifier.size(32.dp))` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 257–259 | 3 | `textColor.copy(alpha = if (canMoveUp)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 259–261 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 263–266 | 2 | `IconButton(onClick =` | Callback de interacción: este código no se ejecuta al componer la UI, sino cuando el usuario activa el control asociado. |
| 266–273 | 2 | `}, enabled = canMoveDown, modifier = Modifier.size(32.dp))` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 268–270 | 3 | `textColor.copy(alpha = if (canMoveDown)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 270–272 | 3 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 274–276 | 2 | `Switch(checked = visible, onCheckedChange =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 282–309 | 0 | `onHiddenItemsChange: (String) -> Unit, fontFamily: FontFamily, textColor: Color)` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 283–308 | 1 | `options.chunked(2).forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 286–307 | 2 | `Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 287–303 | 3 | `rowOptions.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 291–301 | 4 | `ToggleRow(title = stringResource(option.labelRes), checked = visible, onCheckedChange =` | Ámbito delimitado por llaves en profundidad 4. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 294–296 | 5 | `if (checked)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 297–299 | 5 | `1)` | Ámbito delimitado por llaves en profundidad 5. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 304–306 | 3 | `if (rowOptions.size == 1)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 313–337 | 0 | `enabled: Boolean = true, modifier: Modifier = Modifier, compact: Boolean = false)` | Ámbito delimitado por llaves en profundidad 0. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 315–317 | 1 | `Row(modifier = modifier.then(if (compact)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 317–319 | 1 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 319–336 | 1 | `}).padding(vertical = 0.dp), verticalAlignment = Alignment.CenterVertically)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 320–322 | 2 | `Text(text = title, modifier = Modifier.weight(1f), color = textColor.copy(alpha = if (enabled)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 322–324 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 324–326 | 2 | `}), fontFamily = fontFamily, fontSize = if (compact)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 326–328 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 328–330 | 2 | `}, maxLines = if (compact)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 330–332 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 333–335 | 2 | `Switch(checked = checked, onCheckedChange =` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 340–346 | 0 | `private fun optionMenuTextColorLabel(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 341–345 | 1 | `return stringResource(when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 348–359 | 0 | `private fun normalizedOrder(raw: String): List<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 349–351 | 1 | `val requested = raw.split(",").map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 351–354 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 355–358 | 1 | `return requested + DefaultMainOrder.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 361–368 | 0 | `private fun parseKeys(raw: String, valid: List<String>): Set<String>` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 362–364 | 1 | `return raw.split(",").map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 364–367 | 1 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
