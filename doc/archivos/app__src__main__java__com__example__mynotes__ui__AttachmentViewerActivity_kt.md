# AttachmentViewerActivity.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/AttachmentViewerActivity.kt`  **SHA-256:** `101848b08850039e7856855274f6c36b7abd876fb83250f0359e935e0bdc4276`  **Líneas:** 1288 · **Bytes:** 57143 · **Imports:** 118 · **Declaraciones detectadas:** 33
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Visor multimedia de adjuntos con imagen, vídeo y audio.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui`.

### Android / Jetpack / Compose

`android.app.Activity`, `android.app.ActivityManager`, `android.content.ActivityNotFoundException`, `android.content.Context`, `android.content.Intent`, `android.graphics.Bitmap`, `android.graphics.Canvas`, `android.graphics.Color as AndroidColor`, `android.graphics.pdf.PdfRenderer`, `android.net.Uri`, `android.os.Build`, `android.os.Bundle`, `android.os.ParcelFileDescriptor`, `android.text.Html`, `android.webkit.MimeTypeMap`, `android.widget.Toast`, `androidx.activity.ComponentActivity`, `androidx.activity.compose.setContent`, `androidx.activity.enableEdgeToEdge`, `androidx.compose.foundation.Image`, `androidx.compose.foundation.isSystemInDarkTheme`, `androidx.compose.foundation.background`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.lazy.LazyColumn`, `androidx.compose.foundation.lazy.items`, `androidx.compose.foundation.lazy.rememberLazyListState`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.foundation.verticalScroll`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.automirrored.filled.ArrowBack`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.OpenInNew`, `androidx.compose.material.icons.filled.Pause`, `androidx.compose.material.icons.filled.PictureAsPdf`, `androidx.compose.material.icons.filled.PlayArrow`, `androidx.compose.material3.Button`, `androidx.compose.material3.CircularProgressIndicator`, `androidx.compose.material3.ExperimentalMaterial3Api`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Slider`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TopAppBar`, `androidx.compose.material3.TopAppBarDefaults`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.DisposableEffect`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableIntStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.produceState`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.rememberCoroutineScope`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.platform.LocalDensity`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.viewinterop.AndroidView`, `androidx.core.content.FileProvider`, `androidx.core.view.WindowCompat`, `androidx.core.view.WindowInsetsCompat`, `androidx.core.view.WindowInsetsControllerCompat`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`, `androidx.lifecycle.viewmodel.compose.viewModel`, `androidx.media3.common.MediaItem`, `androidx.media3.common.PlaybackException`, `androidx.media3.common.Player`, `androidx.media3.exoplayer.ExoPlayer`, `androidx.media3.ui.AspectRatioFrameLayout`, `androidx.media3.ui.PlayerView`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.ui.components.ScrollPositionCapsule`, `com.example.mynotes.performance.DisplayPerformanceController`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.MyNotesTheme`, `com.example.mynotes.viewmodel.SettingsViewModel`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.delay`, `kotlinx.coroutines.launch`, `kotlinx.coroutines.withContext`, `java.io.Closeable`, `java.io.File`, `java.io.InputStream`, `java.util.Locale`, `java.util.zip.ZipInputStream`, `kotlin.math.roundToInt`

### Terceros / otros

`coil3.compose.AsyncImage`, `coil3.request.CachePolicy`, `coil3.request.ImageRequest`, `coil3.request.allowHardware`, `coil3.request.maxBitmapSize`, `coil3.size.Precision`, `coil3.size.Scale`, `coil3.size.Size`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 132 | `fun` | `openAttachmentViewer` | `fun openAttachmentViewer(context: Context, uri: String, type: String, name: String? = null, mimeType: String? = null) {` |
| 140 | `class` | `AttachmentViewerActivity` | `` |
| 142 | `fun` | `applyAndroidNavigationBarPolicy` | `private fun applyAndroidNavigationBarPolicy() {` |
| 161 | `fun` | `applySystemBarAppearance` | `private fun applySystemBarAppearance(darkMode: Boolean) {` |
| 233 | `fun` | `AttachmentViewerScreen` | `@Composable` |
| 300 | `fun` | `ImageViewer` | `` |
| 339 | `fun` | `VideoViewer` | `` |
| 554 | `fun` | `AudioFileViewer` | `` |
| 768 | `fun` | `PdfViewer` | `` |
| 808 | `fun` | `PdfPage` | `` |
| 845 | `class` | `PdfHandle` | `` |
| 849 | `fun` | `renderPage` | `@Synchronized` |
| 865 | `fun` | `openPdfHandle` | `` |
| 867 | `fun` | `createHandle` | `fun createHandle(descriptor: ParcelFileDescriptor): PdfHandle {` |
| 901 | `fun` | `TextFileViewer` | `` |
| 930 | `fun` | `OfficeTextViewer` | `` |
| 960 | `fun` | `GenericFileViewer` | `` |
| 1012 | `fun` | `copyDocumentToViewerCache` | `` |
| 1034 | `fun` | `copyVideoToPlaybackCache` | `` |
| 1058 | `fun` | `copyAudioToPlaybackCache` | `` |
| 1090 | `fun` | `readTextPreview` | `` |
| 1110 | `fun` | `extractOfficeText` | `` |
| 1179 | `fun` | `decodeXml` | `` |
| 1188 | `fun` | `InputStream` | `` |
| 1201 | `fun` | `openInputStream` | `` |
| 1213 | `fun` | `openExternally` | `` |
| 1226 | `fun` | `toShareableUri` | `` |
| 1234 | `fun` | `resolveMimeType` | `` |
| 1240 | `fun` | `fileExtension` | `` |
| 1248 | `fun` | `isTextExtension` | `` |
| 1253 | `fun` | `resolveFileSize` | `` |
| 1267 | `fun` | `formatFileSize` | `` |
| 1277 | `fun` | `formatTime` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 42 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 9 aparición/apariciones.
- **Coroutines:** 13 aparición/apariciones.
- **I/O/red:** 16 aparición/apariciones.
- **try/catch:** 51 aparición/apariciones.
- **coerce*:** 23 aparición/apariciones.
- **safe calls:** 21 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.performance.DisplayPerformanceController`
- `com.example.mynotes.ui.components.ScrollPositionCapsule`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.MyNotesTheme`
- `com.example.mynotes.viewmodel.SettingsViewModel`

## 6. Recursos Android referenciados

- **R.string:** `audio` ×2, `audio_playback_failed_inside`, `back`, `file`, `image`, `image_format_not_supported`, `no_compatible_app`, `open_file_failed`, `open_with`, `open_with_other_app` ×4, `pause` ×3, `pdf_document`, `pdf_page_description`, `pdf_page_load_failed`, `play` ×3, `video`, `video_playback_failed_inside`

## 7. Puntos de revisión al modificarlo

- No degradar calidad, rutas persistentes ni cachés de adjuntos/miniaturas sin una prueba explícita.
