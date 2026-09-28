# NotesScreen.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/NotesScreen.kt`  
**SHA-256:** `e25669614fa7fee1f0df28f1f45e2a8a474370916e3ad07a5b6280c44a7eab0a`  
**Líneas:** 855  
**Package:** `com.example.mynotes.ui`

## 1. Para qué existe este archivo

Pantalla principal: búsqueda, filtros, grid escalonado, precarga de previews y speed dial Recordatorios/Nueva nota/Dibujar.

## 2. Tipos/clases declarados

- Línea **105** — `private enum  class NoteFilter`.
- Línea **110** — `private data  class NoteFilterOption`.
- Línea **113** — `private data  class AttachmentIndex`.

## 3. Estado, constantes y valores importantes

- **`FilterOptions`** (línea 114) inicia con `listOf(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`attachmentIndex`** (línea 159) inicia con `remember(allAttachments`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`byNote`** (línea 160) inicia con `allAttachments.groupBy {`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`kindsByNote`** (línea 163) inicia con `buildMap<Int`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`attachmentsByNote`** (línea 169) inicia con `attachmentIndex.byNote`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`fontFamily`** (línea 170) inicia con `remember(settings.font`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`screenPrimaryTextColor`** (línea 173) inicia con `resolveUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`screenSecondaryTextColor`** (línea 174) inicia con `resolveSecondaryUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`quickCreateSurfaceColor`** (línea 175) inicia con `MaterialTheme.colorScheme.surfaceContainerHigh`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`quickCreateTextColor`** (línea 176) inicia con `resolveUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`controlSurfaceColor`** (línea 177) inicia con `MaterialTheme.colorScheme.surfaceContainerLow`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`controlTextColor`** (línea 178) inicia con `resolveUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`controlSecondaryTextColor`** (línea 179) inicia con `resolveSecondaryUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`controlGraphicColor`** (línea 180) inicia con `resolveUiGraphicColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selectedControlTextColor`** (línea 181) inicia con `resolveUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`fabContainerColor`** (línea 185) inicia con `MaterialTheme.colorScheme.inverseSurface`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`fabContentColor`** (línea 186) inicia con `resolveUiTextColor(value = settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`noteCardStyle`** (línea 187) inicia con `remember(settings.noteCardCornerRadius`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`searchIsActive`** (línea 226) inicia con `effectiveQuery.isNotBlank(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`searchableTextByNote`** (línea 227) inicia con `remember(notes`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`focusManager`** (línea 240) inicia con `LocalFocusManager.current`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`searchFocusRequester`** (línea 241) inicia con `remember { FocusRequester(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`context`** (línea 242) inicia con `LocalContext.current`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`gridState`** (línea 250) inicia con `rememberLazyStaggeredGridState(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`linkPreviewUrls`** (línea 261) inicia con `remember(notes`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`idleDelayMs`** (línea 267) inicia con `when (settings.performanceMode`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`configuration`** (línea 278) inicia con `LocalConfiguration.current`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`currentOrientation`** (línea 279) inicia con `configuration.orientation`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`isLandscape`** (línea 280) inicia con `currentOrientation == Configuration.ORIENTATION_LANDSCAPE`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`normalizedQuery`** (línea 320) inicia con `effectiveQuery`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`matchesText`** (línea 331) inicia con `normalizedQuery.isBlank(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`qualityScrollPreviewAttachments`** (línea 379) inicia con `remember(visibleNotes`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`motionDuration`** (línea 447) inicia con `AppMotion.duration(AppMotion.NORMAL`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selected`** (línea 632) inicia con `option.filter == selectedFilter`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`maximumColumnsForWidth`** (línea 678) inicia con `(maxWidth.value / 145f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `noteFilterFromWidgetKey` — líneas 126–126

**Firma:** `private fun noteFilterFromWidgetKey(key: String?): NoteFilter`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `key: String?`

**Salida:** NoteFilter.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

### `NotesScreen` — líneas 138–777

**Firma:** `fun NotesScreen( notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, initialFilterKey: String? = null, requestSearchFocus: Boolean = false, widgetRequestToken: Int = 0, onAddNote: () -> Unit, onDrawNote: () -> Unit, onOpenReminders: () -> Unit, onOpenSettings: () -> Unit, onOpenNote: (Note) -> Unit, onEditNote: (Note) -> Unit )`

Compone la pantalla principal. Observa notas/adjuntos, calcula filtros y búsqueda, configura el LazyVerticalStaggeredGrid, coordina precarga de previews y muestra el speed dial en orden Recordatorios → Nueva nota → Dibujar.

**Entradas:**
- `notes: List<Note>`
- `noteViewModel: NoteViewModel`
- `settings: AppSettings`
- `initialFilterKey: String? = null`
- `requestSearchFocus: Boolean = false`
- `widgetRequestToken: Int = 0`
- `onAddNote: () -> Unit`
- `onDrawNote: () -> Unit`
- `onOpenReminders: () -> Unit`
- `onOpenSettings: () -> Unit`
- `onOpenNote: (Note) -> Unit`
- `onEditNote: (Note) -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Accede a la base Room/DAO.
- Accede al sistema de archivos interno/cache.
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `collectAsStateWithLifecycle`, `put`, `asSequence`, `toSet`, `AttachmentIndex`, `appFontFamily`, `resolveUiTextColor`, `resolveSecondaryUiTextColor`, `resolveUiGraphicColor`, `toNoteCardStyle`, `LaunchedEffect`, `delay`, `trim`, `lowercase`, `isNotBlank`, `emptyMap`, `buildString`, `append`.

### `QuickCreateActionButton` — líneas 780–820

**Firma:** `private fun QuickCreateActionButton( text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, fontFamily: androidx.compose.ui.text.font.FontFamily, containerColor: androidx.compose.ui.graphics.Color, contentColor: androidx.compose.ui.graphics.Color, onClick: () -> Unit )`

Botón tipo pill reutilizado por las tres acciones del speed dial; adapta ancho al contenido y mantiene icono/texto alineados.

**Entradas:**
- `text: String`
- `icon: androidx.compose.ui.graphics.vector.ImageVector`
- `fontFamily: androidx.compose.ui.text.font.FontFamily`
- `containerColor: androidx.compose.ui.graphics.Color`
- `contentColor: androidx.compose.ui.graphics.Color`
- `onClick: () -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Operaciones/funciones que coordina:** `height`, `clickable`, `RoundedCornerShape`, `padding`, `size`, `width`.

### `EmptyNotesState` — líneas 823–854

**Firma:** `private fun EmptyNotesState(modifier: Modifier, hasSearch: Boolean, fontFamily: androidx.compose.ui.text.font.FontFamily)`

Estado vacío contextual: diferencia entre no tener notas y no obtener resultados de búsqueda.

**Entradas:**
- `modifier: Modifier`
- `hasSearch: Boolean`
- `fontFamily: androidx.compose.ui.text.font.FontFamily`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `height`, `padding`, `resolveSecondaryUiTextColor`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.R`.
- Usa `com.example.mynotes.data.Attachment`.
- Usa `com.example.mynotes.data.Note`.
- Usa `com.example.mynotes.performance.AttachmentPreviewCache`.
- Usa `com.example.mynotes.settings.AppSettings`.
- Usa `com.example.mynotes.ui.components.ModernNoteCard`.
- Usa `com.example.mynotes.ui.components.ScrollPositionCapsule`.
- Usa `com.example.mynotes.ui.components.extractLinkUrls`.
- Usa `com.example.mynotes.ui.components.preloadLinkPreviews`.
- Usa `com.example.mynotes.ui.components.toNoteCardStyle`.
- Usa `com.example.mynotes.ui.motion.AnimatedScreenEntry`.
- Usa `com.example.mynotes.ui.motion.AppMotion`.
- Usa `com.example.mynotes.ui.sound.UiActionSound`.
- Usa `com.example.mynotes.ui.sound.UiSound`.
- Usa `com.example.mynotes.ui.sound.UiSoundPlayer`.
- Usa `com.example.mynotes.ui.theme.appFontFamily`.
- Usa `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`.
- Usa `com.example.mynotes.ui.theme.resolveUiTextColor`.
- Usa `com.example.mynotes.ui.theme.resolveUiGraphicColor`.
- Usa `com.example.mynotes.viewmodel.NoteViewModel`.

## 6. Recursos Android que utiliza

- `R.string`: `add_action_menu`, `create_drawing`, `mock_create_first_note`, `mock_favorites`, `mock_files`, `mock_filter_all`, `mock_images`, `mock_my_notes`, `mock_new_note`, `mock_no_notes`, `mock_no_results`, `mock_notes_subtitle`, `mock_personal`, `mock_search_notes`, `mock_settings`, `mock_try_other_search`, `mock_work`, `reminders`, `widget_high_priority`, `widget_pinned_collection`

## 7. Tecnologías y efectos relevantes

- Accede a la base Room/DAO.
- Accede al sistema de archivos interno/cache.
- Participa en estado/efectos de Compose.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `NotesScreen` — Compone la pantalla principal. Observa notas/adjuntos, calcula filtros y búsqueda, configura el LazyVerticalStaggeredGrid, coordina precarga de previews y muestra el speed dial en orden Recordatorios → Nueva nota → Dibujar.

## 9. Qué no debe romperse al modificarlo

- Conservar validaciones de Uri/ruta y no confiar en nombres externos sin sanitizar.
- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.

## 10. Resumen en lenguaje sencillo

En términos simples: Pantalla principal: búsqueda, filtros, grid escalonado, precarga de previews y speed dial Recordatorios/Nueva nota/Dibujar. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
