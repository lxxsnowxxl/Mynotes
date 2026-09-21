# NoteEditorScreen.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/NoteEditorScreen.kt`  **SHA-256:** `13f492397673d122e0d7ac83b25562ac32f935e8315b86b4cf041a6fd4f7e4f9`  **Líneas:** 1068 · **Bytes:** 52713 · **Imports:** 107 · **Declaraciones detectadas:** 11
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Creación/edición de notas, texto, paleta y adjuntos.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui`.

### Android / Jetpack / Compose

`android.Manifest`, `android.content.Context`, `android.content.res.Configuration`, `android.content.Intent`, `android.content.pm.PackageManager`, `android.media.MediaRecorder`, `android.os.Build`, `android.net.Uri`, `android.provider.OpenableColumns`, `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.compose.foundation.background`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.ExperimentalLayoutApi`, `androidx.compose.foundation.layout.FlowRow`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.ArrowBack`, `androidx.compose.material.icons.filled.Check`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.Image`, `androidx.compose.material.icons.filled.Mic`, `androidx.compose.material.icons.filled.MusicNote`, `androidx.compose.material.icons.filled.Stop`, `androidx.compose.material.icons.filled.Videocam`, `androidx.compose.material3.Button`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.Card`, `androidx.compose.material3.CardDefaults`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.OutlinedButton`, `androidx.compose.material3.OutlinedTextField`, `androidx.compose.material3.OutlinedTextFieldDefaults`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TopAppBar`, `androidx.compose.material3.TopAppBarDefaults`, `androidx.compose.material3.Typography`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.DisposableEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.produceState`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.saveable.rememberSaveable`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.graphics.vector.ImageVector`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalConfiguration`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.Font`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontStyle`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.text.TextRange`, `androidx.compose.ui.text.input.TextFieldValue`, `androidx.compose.ui.unit.dp`, `androidx.core.content.ContextCompat`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.PendingAttachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.components.LinkPreviewCard`, `com.example.mynotes.ui.components.ScrollPositionCapsule`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.components.extractEmbeddedLinkUrls`, `com.example.mynotes.ui.components.stripEmbeddedLinkMetadata`, `com.example.mynotes.ui.components.noteContentForStorage`, `com.example.mynotes.ui.openAttachmentViewer`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.delay`, `java.io.File`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 110 | `fun` | `removeProcessedUrl` | `` |
| 127 | `fun` | `shifted` | `fun shifted(position: Int): Int = when {` |
| 140 | `fun` | `NoteEditorScreen` | `@Composable` |
| 245 | `fun` | `startRecording` | `fun startRecording() {` |
| 288 | `fun` | `stopRecording` | `fun stopRecording() {` |
| 371 | `fun` | `requestVoiceRecording` | `fun requestVoiceRecording() {` |
| 876 | `fun` | `AttachmentButton` | `@Composable` |
| 900 | `fun` | `AttachmentPreview` | `@Composable` |
| 1012 | `fun` | `AttachmentIconPreview` | `` |
| 1039 | `fun` | `getFileName` | `private fun getFileName(context: Context, uri: Uri): String? {` |
| 1058 | `fun` | `Typography` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 29 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 2 aparición/apariciones.
- **Coroutines:** 7 aparición/apariciones.
- **Room:** 3 aparición/apariciones.
- **I/O/red:** 6 aparición/apariciones.
- **try/catch:** 22 aparición/apariciones.
- **coerce*:** 2 aparición/apariciones.
- **safe calls:** 10 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.Attachment`
- `com.example.mynotes.data.PendingAttachment`
- `com.example.mynotes.performance.AttachmentPreviewCache`
- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.components.LinkPreviewCard`
- `com.example.mynotes.ui.components.ScrollPositionCapsule`
- `com.example.mynotes.ui.components.extractEmbeddedLinkUrls`
- `com.example.mynotes.ui.components.extractLinkUrls`
- `com.example.mynotes.ui.components.noteContentForStorage`
- `com.example.mynotes.ui.components.stripEmbeddedLinkMetadata`
- `com.example.mynotes.ui.openAttachmentViewer`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.ensureUiContrast`
- `com.example.mynotes.ui.theme.noteBackgroundColor`
- `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`
- `com.example.mynotes.ui.theme.resolveUiGraphicColor`
- `com.example.mynotes.ui.theme.resolveUiTextColor`

## 6. Recursos Android referenciados

- **R.string:** `attachments`, `audio` ×2, `back`, `cancel`, `create_note`, `edit_note`, `file` ×2, `image`, `microphone_not_available`, `microphone_permission_required`, `mock_color_blue`, `mock_color_brown`, `mock_color_cyan`, `mock_color_default`, `mock_color_gray`, `mock_color_green`, `mock_color_lime`, `mock_color_mint`, `mock_color_orange`, `mock_color_palette`, `mock_color_pink`, `mock_color_purple`, `mock_color_red`, `mock_color_teal`, `mock_color_yellow`, `new_note`, `record_voice_note`, `recording_start_error`, `recording_too_short`, `recording_voice_note`, `save` ×2, `stop_recording`, `title`, `video` ×2, `voice_note` ×2, `write_your_note`

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
