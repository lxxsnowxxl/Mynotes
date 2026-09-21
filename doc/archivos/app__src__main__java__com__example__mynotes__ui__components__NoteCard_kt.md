# NoteCard.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/NoteCard.kt`  **SHA-256:** `6388b10cdc88379f74e39a8e94f57e722b8d93b08290cf512d7dbedd1382115d`  **Líneas:** 883 · **Bytes:** 48809 · **Imports:** 96 · **Declaraciones detectadas:** 12
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Tarjeta principal de nota: contenido, menús, categoría, adjuntos, enlaces, borde y acciones.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`android.net.Uri`, `androidx.compose.animation.AnimatedVisibility`, `androidx.compose.animation.Crossfade`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.scaleIn`, `androidx.compose.animation.scaleOut`, `androidx.compose.animation.animateColorAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.animation.animateContentSize`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.background`, `androidx.compose.foundation.Image`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.interaction.MutableInteractionSource`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.fillMaxHeight`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Delete`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.Edit`, `androidx.compose.material.icons.filled.Mic`, `androidx.compose.material.icons.filled.MusicNote`, `androidx.compose.material.icons.filled.PlayArrow`, `androidx.compose.material.icons.filled.MoreVert`, `androidx.compose.material.icons.filled.Palette`, `androidx.compose.material.icons.filled.PriorityHigh`, `androidx.compose.material.icons.filled.PushPin`, `androidx.compose.material.icons.filled.Star`, `androidx.compose.material.icons.filled.StarBorder`, `androidx.compose.material.icons.filled.Work`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material3.Card`, `androidx.compose.material3.CardDefaults`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.HorizontalDivider`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.vector.ImageVector`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.theme.automaticUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.compositeUiColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`, `com.example.mynotes.ui.theme.paletteMatchedOutlineColor`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`

### Kotlin / Coroutines / Java

`java.text.DateFormat`, `java.util.Date`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 121 | `fun` | `ModernNoteCard` | `@Composable` |
| 571 | `fun` | `CategoryPill` | `` |
| 599 | `fun` | `NoteCardAttachmentsPreview` | `` |
| 634 | `fun` | `NoteCardAttachmentTile` | `` |
| 777 | `fun` | `FileLikeFallbackTile` | `` |
| 789 | `fun` | `SmallDurationBadge` | `` |
| 797 | `fun` | `formatSmallDuration` | `` |
| 804 | `fun` | `ConfigurableDropdownMenuItem` | `` |
| 819 | `fun` | `PriorityMenuItem` | `` |
| 845 | `fun` | `ColorMenuItem` | `` |
| 862 | `fun` | `normalizedMenuOrder` | `` |
| 875 | `fun` | `parseMenuKeys` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 30 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 4 aparición/apariciones.
- **Room:** 2 aparición/apariciones.
- **I/O/red:** 1 aparición/apariciones.
- **coerce*:** 6 aparición/apariciones.
- **safe calls:** 8 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.Attachment`
- `com.example.mynotes.data.Note`
- `com.example.mynotes.performance.AttachmentPreviewCache`
- `com.example.mynotes.ui.components.AppDropdownMenu`
- `com.example.mynotes.ui.motion.AppMotion`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.automaticUiTextColor`
- `com.example.mynotes.ui.theme.compositeUiColor`
- `com.example.mynotes.ui.theme.ensureUiContrast`
- `com.example.mynotes.ui.theme.noteBackgroundColor`
- `com.example.mynotes.ui.theme.paletteMatchedOutlineColor`
- `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`
- `com.example.mynotes.ui.theme.resolveUiGraphicColor`
- `com.example.mynotes.ui.theme.resolveUiTextColor`

## 6. Recursos Android referenciados

- **R.string:** `audio`, `file`, `image`, `mock_add_favorite`, `mock_color`, `mock_color_blue`, `mock_color_brown`, `mock_color_cyan`, `mock_color_default`, `mock_color_gray`, `mock_color_green`, `mock_color_lime`, `mock_color_mint`, `mock_color_orange`, `mock_color_pink`, `mock_color_purple`, `mock_color_red`, `mock_color_teal`, `mock_color_yellow`, `mock_delete`, `mock_edit`, `mock_favorites`, `mock_move`, `mock_personal` ×2, `mock_pin`, `mock_priority`, `mock_priority_high`, `mock_priority_low`, `mock_priority_medium`, `mock_priority_none`, `mock_remove_favorite`, `mock_unpin`, `mock_untitled`, `mock_work` ×2, `video`, `voice_note`

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
