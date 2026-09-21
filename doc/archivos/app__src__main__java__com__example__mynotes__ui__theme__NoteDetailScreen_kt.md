# NoteDetailScreen.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/NoteDetailScreen.kt`  **SHA-256:** `1e5c9c6df03155d89ffac32cead81c734acf989fbf9f8a86f63190dd130fb39a`  **Líneas:** 708 · **Bytes:** 37307 · **Imports:** 99 · **Declaraciones detectadas:** 7
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Vista de detalle de una nota.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui`.

### Android / Jetpack / Compose

`android.content.Context`, `android.content.Intent`, `androidx.compose.animation.Crossfade`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.ExperimentalLayoutApi`, `androidx.compose.foundation.layout.FlowRow`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.navigationBarsPadding`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Add`, `androidx.compose.material.icons.filled.CalendarMonth`, `androidx.compose.material.icons.filled.Delete`, `androidx.compose.material.icons.filled.Edit`, `androidx.compose.material.icons.filled.Folder`, `androidx.compose.material.icons.filled.MoreVert`, `androidx.compose.material.icons.filled.Palette`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material.icons.filled.PriorityHigh`, `androidx.compose.material.icons.filled.PushPin`, `androidx.compose.material.icons.filled.Share`, `androidx.compose.material.icons.filled.Star`, `androidx.compose.material.icons.filled.StarBorder`, `androidx.compose.material.icons.filled.Work`, `androidx.compose.material3.AssistChip`, `androidx.compose.material3.AssistChipDefaults`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.HorizontalDivider`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.material3.TopAppBar`, `androidx.compose.material3.TopAppBarDefaults`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.luminance`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.ui.components.AppAlertDialog`, `com.example.mynotes.ui.components.ScrollPositionCapsule`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.data.Note`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.InlineNoteAttachment`, `com.example.mynotes.ui.components.LinkPreviewCard`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.components.noteTextForDisplay`, `com.example.mynotes.ui.components.CategoryPill`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.compositeUiColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`, `com.example.mynotes.viewmodel.NoteViewModel`

### Kotlin / Coroutines / Java

`java.text.DateFormat`, `java.util.Date`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 112 | `fun` | `NoteDetailScreen` | `@Composable` |
| 590 | `fun` | `PriorityPill` | `` |
| 635 | `fun` | `DetailConfigurableMenuItem` | `` |
| 654 | `fun` | `DetailBottomAction` | `` |
| 674 | `fun` | `shareNote` | `` |
| 686 | `fun` | `normalizedDetailMenuOrder` | `` |
| 700 | `fun` | `parseDetailMenuKeys` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 21 aparición/apariciones.
- **Flow/StateFlow:** 1 aparición/apariciones.
- **Room:** 3 aparición/apariciones.
- **coerce*:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.Note`
- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.components.AppAlertDialog`
- `com.example.mynotes.ui.components.AppDropdownMenu`
- `com.example.mynotes.ui.components.CategoryPill`
- `com.example.mynotes.ui.components.InlineNoteAttachment`
- `com.example.mynotes.ui.components.LinkPreviewCard`
- `com.example.mynotes.ui.components.ScrollPositionCapsule`
- `com.example.mynotes.ui.components.extractLinkUrls`
- `com.example.mynotes.ui.components.noteTextForDisplay`
- `com.example.mynotes.ui.motion.AnimatedScreenEntry`
- `com.example.mynotes.ui.motion.AppMotion`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.appFontFamily`
- `com.example.mynotes.ui.theme.compositeUiColor`
- `com.example.mynotes.ui.theme.ensureUiContrast`
- `com.example.mynotes.ui.theme.noteBackgroundColor`
- `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`
- `com.example.mynotes.ui.theme.resolveUiGraphicColor`
- `com.example.mynotes.ui.theme.resolveUiTextColor`
- `com.example.mynotes.viewmodel.NoteViewModel`

## 6. Recursos Android referenciados

- **R.string:** `mock_attachments`, `mock_back`, `mock_cancel`, `mock_color`, `mock_color_blue`, `mock_color_brown`, `mock_color_cyan`, `mock_color_default` ×2, `mock_color_gray`, `mock_color_green`, `mock_color_lime`, `mock_color_mint`, `mock_color_orange`, `mock_color_pink`, `mock_color_purple`, `mock_color_red`, `mock_color_teal`, `mock_color_yellow`, `mock_delete` ×2, `mock_delete_note_message`, `mock_delete_note_title`, `mock_edit` ×2, `mock_favorites`, `mock_move`, `mock_no_attachments`, `mock_personal`, `mock_pin`, `mock_priority`, `mock_priority_high` ×2, `mock_priority_low` ×2, `mock_priority_medium` ×2, `mock_priority_none` ×3, `mock_share`, `mock_unpin`, `mock_untitled`, `mock_work`

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
