# AttachmentPreviewTile.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/AttachmentPreviewTile.kt`  **SHA-256:** `02a4cd1294006d450f96ff84ca2d992f00ba900aabd7487bf4ca6b4d93ae1144`  **Líneas:** 329 · **Bytes:** 14733 · **Imports:** 52 · **Declaraciones detectadas:** 9
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Tile visual para previsualizar un adjunto dentro del editor o tarjetas.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`android.content.Context`, `android.net.Uri`, `android.webkit.MimeTypeMap`, `androidx.compose.foundation.Image`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.Mic`, `androidx.compose.material.icons.filled.MusicNote`, `androidx.compose.material.icons.filled.PlayArrow`, `androidx.compose.material.icons.filled.VideoFile`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.produceState`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.Dp`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.openAttachmentViewer`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.delay`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 65 | `fun` | `AttachmentPreviewTile` | `@Composable` |
| 109 | `fun` | `ImageAttachment` | `` |
| 146 | `fun` | `VideoAttachment` | `` |
| 194 | `fun` | `AudioAttachment` | `` |
| 247 | `fun` | `FileAttachment` | `` |
| 294 | `fun` | `DurationBadge` | `` |
| 307 | `fun` | `formatDuration` | `` |
| 318 | `fun` | `openAttachment` | `fun openAttachment(context: Context, attachment: Attachment) {` |
| 322 | `fun` | `resolveMimeType` | `` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 5 aparición/apariciones.
- **safe calls:** 12 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.Attachment`
- `com.example.mynotes.performance.AttachmentPreviewCache`
- `com.example.mynotes.ui.openAttachmentViewer`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`

## 6. Recursos Android referenciados

- **R.string:** `mock_audio`, `mock_file`, `mock_voice_note`

## 7. Puntos de revisión al modificarlo

- No degradar calidad, rutas persistentes ni cachés de adjuntos/miniaturas sin una prueba explícita.
