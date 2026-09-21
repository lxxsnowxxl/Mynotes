# NotesScreen.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/NotesScreen.kt`  **SHA-256:** `e25669614fa7fee1f0df28f1f45e2a8a474370916e3ad07a5b6280c44a7eab0a`  **Líneas:** 854 · **Bytes:** 42499 · **Imports:** 101 · **Declaraciones detectadas:** 7
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Pantalla principal: búsqueda, filtros, grid de notas y speed dial New note/Draw/Reminders.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui`.

### Android / Jetpack / Compose

`android.content.res.Configuration`, `androidx.activity.compose.BackHandler`, `androidx.compose.foundation.clickable`, `android.net.Uri`, `androidx.compose.animation.AnimatedVisibility`, `androidx.compose.animation.expandVertically`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.shrinkVertically`, `androidx.compose.animation.core.animateDpAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.offset`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.lazy.LazyRow`, `androidx.compose.foundation.lazy.items`, `androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid`, `androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells`, `androidx.compose.foundation.lazy.staggeredgrid.items`, `androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Add`, `androidx.compose.material.icons.filled.Brush`, `androidx.compose.material.icons.filled.Close`, `androidx.compose.material.icons.filled.NoteAdd`, `androidx.compose.material.icons.filled.NotificationsActive`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material.icons.filled.Search`, `androidx.compose.material.icons.filled.Settings`, `androidx.compose.material3.FilterChip`, `androidx.compose.material3.FilterChipDefaults`, `androidx.compose.material3.FloatingActionButton`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.OutlinedTextField`, `androidx.compose.material3.OutlinedTextFieldDefaults`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.Immutable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.derivedStateOf`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableIntStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.runtime.saveable.rememberSaveable`, `androidx.compose.runtime.withFrameNanos`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.focus.FocusRequester`, `androidx.compose.ui.focus.focusRequester`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalConfiguration`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.platform.LocalFocusManager`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.ModernNoteCard`, `com.example.mynotes.ui.components.ScrollPositionCapsule`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.components.preloadLinkPreviews`, `com.example.mynotes.ui.components.toNoteCardStyle`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.viewmodel.NoteViewModel`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.delay`

### Terceros / otros

`coil3.compose.AsyncImage`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 104 | `class` | `NoteFilter` | `` |
| 108 | `class` | `NoteFilterOption` | `` |
| 111 | `class` | `AttachmentIndex` | `` |
| 125 | `fun` | `noteFilterFromWidgetKey` | `` |
| 136 | `fun` | `NotesScreen` | `` |
| 778 | `fun` | `QuickCreateActionButton` | `` |
| 821 | `fun` | `EmptyNotesState` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 23 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 8 aparición/apariciones.
- **Coroutines:** 3 aparición/apariciones.
- **Room:** 5 aparición/apariciones.
- **I/O/red:** 1 aparición/apariciones.
- **coerce*:** 5 aparición/apariciones.
- **safe calls:** 2 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.Attachment`
- `com.example.mynotes.data.Note`
- `com.example.mynotes.performance.AttachmentPreviewCache`
- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.components.ModernNoteCard`
- `com.example.mynotes.ui.components.ScrollPositionCapsule`
- `com.example.mynotes.ui.components.extractLinkUrls`
- `com.example.mynotes.ui.components.preloadLinkPreviews`
- `com.example.mynotes.ui.components.toNoteCardStyle`
- `com.example.mynotes.ui.motion.AnimatedScreenEntry`
- `com.example.mynotes.ui.motion.AppMotion`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.appFontFamily`
- `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`
- `com.example.mynotes.ui.theme.resolveUiGraphicColor`
- `com.example.mynotes.ui.theme.resolveUiTextColor`
- `com.example.mynotes.viewmodel.NoteViewModel`

## 6. Recursos Android referenciados

- **R.string:** `add_action_menu`, `create_drawing`, `mock_create_first_note`, `mock_favorites`, `mock_files`, `mock_filter_all`, `mock_images`, `mock_my_notes`, `mock_new_note`, `mock_no_notes`, `mock_no_results`, `mock_notes_subtitle`, `mock_personal`, `mock_search_notes`, `mock_settings` ×3, `mock_try_other_search`, `mock_work`, `reminders`, `widget_high_priority`, `widget_pinned_collection`

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
