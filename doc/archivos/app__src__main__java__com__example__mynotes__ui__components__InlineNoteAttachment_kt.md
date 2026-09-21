# InlineNoteAttachment.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/InlineNoteAttachment.kt`  **SHA-256:** `b378709c6468100289997cb3027bd6d69820287d9ac94bf13eb91ae1ba4e4908`  **Líneas:** 745 · **Bytes:** 35666 · **Imports:** 82 · **Declaraciones detectadas:** 10
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Renderizado inline de adjuntos dentro de las notas.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`android.app.ActivityManager`, `android.content.Context`, `android.net.Uri`, `android.view.TextureView`, `androidx.compose.foundation.Image`, `androidx.compose.foundation.background`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.gestures.detectTransformGestures`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.aspectRatio`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Close`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.Mic`, `androidx.compose.material.icons.filled.MusicNote`, `androidx.compose.material.icons.filled.Pause`, `androidx.compose.material.icons.filled.PlayArrow`, `androidx.compose.material.icons.filled.ZoomIn`, `androidx.compose.material3.CircularProgressIndicator`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Slider`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.DisposableEffect`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableIntStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.produceState`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.TransformOrigin`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.FilterQuality`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.graphics.graphicsLayer`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.input.pointer.pointerInput`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.viewinterop.AndroidView`, `androidx.media3.common.MediaItem`, `androidx.media3.common.PlaybackException`, `androidx.media3.common.Player`, `androidx.media3.exoplayer.ExoPlayer`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.delay`, `kotlinx.coroutines.withContext`

### Terceros / otros

`coil3.compose.AsyncImage`, `coil3.request.CachePolicy`, `coil3.request.ImageRequest`, `coil3.request.allowHardware`, `coil3.request.maxBitmapSize`, `coil3.size.Precision`, `coil3.size.Scale`, `coil3.size.Size`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 87 | `object` | `InlinePlaybackCoordinator` | `` |
| 90 | `fun` | `activate` | `fun activate(player: ExoPlayer) {` |
| 99 | `fun` | `clear` | `fun clear(player: ExoPlayer) {` |
| 114 | `fun` | `InlineNoteAttachment` | `@Composable` |
| 128 | `fun` | `InlineImageAttachment` | `` |
| 269 | `fun` | `InlineVideoAttachment` | `` |
| 488 | `fun` | `InlineAudioAttachment` | `` |
| 682 | `fun` | `InlinePdfAttachment` | `` |
| 711 | `fun` | `InlineFileAttachment` | `` |
| 739 | `fun` | `formatInlineDuration` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 48 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 7 aparición/apariciones.
- **Coroutines:** 6 aparición/apariciones.
- **try/catch:** 24 aparición/apariciones.
- **coerce*:** 21 aparición/apariciones.
- **safe calls:** 11 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.Attachment`
- `com.example.mynotes.performance.AttachmentPreviewCache`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`

## 6. Recursos Android referenciados

- **R.string:** `audio`, `audio_playback_failed`, `file` ×2, `image_format_not_supported`, `pause`, `play` ×2, `tap_to_open`, `video_playback_failed`, `voice_note`

## 7. Puntos de revisión al modificarlo

- No degradar calidad, rutas persistentes ni cachés de adjuntos/miniaturas sin una prueba explícita.
