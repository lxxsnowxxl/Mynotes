# ProfileImageEditorDialog.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/ProfileImageEditorDialog.kt`  **SHA-256:** `599379aad0d59ffcc4ab89b6574953d8a9652572cb8cb78196eda25c5769e4d5`  **Líneas:** 492 · **Bytes:** 23052 · **Imports:** 74 · **Declaraciones detectadas:** 10
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Edición, recorte y confirmación de la imagen de perfil.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`android.content.Context`, `android.graphics.Bitmap`, `android.graphics.BitmapFactory`, `android.graphics.Canvas as AndroidCanvas`, `android.graphics.ImageDecoder`, `android.graphics.Matrix`, `android.media.ExifInterface`, `android.graphics.Paint`, `android.graphics.RectF`, `android.net.Uri`, `android.os.Build`, `androidx.activity.compose.BackHandler`, `androidx.compose.foundation.Canvas`, `androidx.compose.foundation.background`, `androidx.compose.foundation.border`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.gestures.detectTransformGestures`, `androidx.compose.foundation.interaction.MutableInteractionSource`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.aspectRatio`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material3.CircularProgressIndicator`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Slider`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.DisposableEffect`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableFloatStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.rememberCoroutineScope`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.geometry.Offset`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.asImageBitmap`, `androidx.compose.ui.input.pointer.pointerInput`, `androidx.compose.ui.layout.onSizeChanged`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextAlign`, `androidx.compose.ui.unit.IntOffset`, `androidx.compose.ui.unit.IntSize`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.window.Popup`, `androidx.compose.ui.window.PopupProperties`

### Proyecto MyNotes

`com.example.mynotes.R`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.launch`, `kotlinx.coroutines.withContext`, `java.io.File`, `java.io.FileInputStream`, `java.io.FileOutputStream`, `java.io.InputStream`, `kotlin.math.max`, `kotlin.math.roundToInt`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 96 | `fun` | `ProfileImageEditorDialog` | `@Composable` |
| 333 | `fun` | `managedProfileSourceUri` | `fun managedProfileSourceUri(context: Context, currentProfileImageUri: String): Uri? {` |
| 341 | `fun` | `clearManagedProfileImages` | `fun clearManagedProfileImages(context: Context) {` |
| 344 | `fun` | `clampProfileOffset` | `` |
| 354 | `fun` | `decodeProfileBitmap` | `` |
| 381 | `fun` | `decodeProfileBitmapLegacy` | `` |
| 394 | `fun` | `applyLegacyExifOrientation` | `` |
| 428 | `fun` | `saveProfileCrop` | `` |
| 468 | `fun` | `persistProfileSource` | `` |
| 480 | `fun` | `openProfileInputStream` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 18 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 4 aparición/apariciones.
- **Coroutines:** 5 aparición/apariciones.
- **I/O/red:** 16 aparición/apariciones.
- **try/catch:** 10 aparición/apariciones.
- **coerce*:** 12 aparición/apariciones.
- **safe calls:** 9 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`

## 6. Recursos Android referenciados

- **R.string:** `cancel`, `extreme_profile_apply`, `extreme_profile_editor_hint`, `extreme_profile_editor_title`, `extreme_profile_error`, `extreme_profile_loading`, `extreme_profile_reset`, `extreme_profile_zoom`

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
