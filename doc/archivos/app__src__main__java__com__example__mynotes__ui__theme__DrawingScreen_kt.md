# DrawingScreen.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/DrawingScreen.kt`  **SHA-256:** `313cea20b67577d8000f15174ff73905b26c167106bd2efbd2f150089739eded`  **Líneas:** 843 · **Bytes:** 35465 · **Imports:** 82 · **Declaraciones detectadas:** 9
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Editor de dibujo integrado: herramientas, colores, lienzo, borrado, expansión y guardado.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui`.

### Android / Jetpack / Compose

`android.content.Context`, `android.app.Activity`, `android.content.ContextWrapper`, `android.graphics.Bitmap`, `android.graphics.Paint`, `android.net.Uri`, `androidx.compose.foundation.Canvas`, `androidx.compose.foundation.background`, `androidx.compose.foundation.border`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.gestures.detectDragGestures`, `androidx.compose.foundation.horizontalScroll`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.rememberScrollState`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.ArrowBack`, `androidx.compose.material.icons.filled.Check`, `androidx.compose.material.icons.filled.DeleteSweep`, `androidx.compose.material.icons.filled.Fullscreen`, `androidx.compose.material.icons.filled.FullscreenExit`, `androidx.compose.material.icons.filled.Redo`, `androidx.compose.material.icons.filled.Undo`, `androidx.compose.material3.FilledTonalIconButton`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.DisposableEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableIntStateOf`, `androidx.compose.runtime.mutableStateListOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.rememberCoroutineScope`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.geometry.Offset`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.Path`, `androidx.compose.ui.graphics.StrokeCap`, `androidx.compose.ui.graphics.StrokeJoin`, `androidx.compose.ui.graphics.drawscope.Stroke`, `androidx.compose.ui.graphics.luminance`, `androidx.compose.ui.graphics.toArgb`, `androidx.compose.ui.input.pointer.pointerInput`, `androidx.compose.ui.layout.onSizeChanged`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.platform.LocalDensity`, `androidx.compose.ui.platform.LocalView`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.IntSize`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.core.view.ViewCompat`, `androidx.core.view.WindowCompat`, `androidx.core.view.WindowInsetsCompat`, `androidx.core.view.WindowInsetsControllerCompat`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.launch`, `kotlinx.coroutines.withContext`, `java.io.File`, `java.io.FileOutputStream`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 85 | `class` | `DrawingStrokeData` | `` |
| 92 | `class` | `DrawingTool` | `` |
| 105 | `fun` | `DrawingScreen` | `` |
| 339 | `fun` | `drawStrokeData` | `fun drawStrokeData(strokeData: DrawingStrokeData) {` |
| 665 | `fun` | `DrawingToolChip` | `` |
| 705 | `fun` | `DrawingThicknessButton` | `` |
| 730 | `fun` | `DrawingToolButton` | `` |
| 755 | `fun` | `DrawingStrokeData` | `` |
| 762 | `fun` | `smoothComposePath` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 26 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 2 aparición/apariciones.
- **Coroutines:** 5 aparición/apariciones.
- **I/O/red:** 3 aparición/apariciones.
- **coerce*:** 2 aparición/apariciones.
- **safe calls:** 2 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.appFontFamily`

## 6. Recursos Android referenciados

- **R.string:** `drawing_back`, `drawing_canvas_color`, `drawing_clear` ×2, `drawing_colors`, `drawing_expand_canvas`, `drawing_medium`, `drawing_redo` ×2, `drawing_restore_canvas`, `drawing_save`, `drawing_subtitle`, `drawing_thick`, `drawing_thickness`, `drawing_thin`, `drawing_title`, `drawing_tool_eraser` ×2, `drawing_tool_fine`, `drawing_tool_highlighter`, `drawing_tool_marker`, `drawing_tool_pen`, `drawing_tool_pencil`, `drawing_tools` ×2, `drawing_undo` ×2

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
- Mantener coordenadas/trazos al expandir el lienzo y coherencia entre borrador y color de fondo.
