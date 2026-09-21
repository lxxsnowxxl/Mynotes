# WidgetMediaPreview.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/widget/WidgetMediaPreview.kt`  **SHA-256:** `ce79e30686a95596cddb2bdc10100bba9e11a44cee318ba5349ca99a35cde675`  **Líneas:** 142 · **Bytes:** 5993 · **Imports:** 13 · **Declaraciones detectadas:** 10
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Preparación segura de imágenes/miniaturas para RemoteViews.
## 2. Package e imports

Package declarado: `com.example.mynotes.widget`.

### Android / Jetpack / Compose

`android.content.ContentResolver`, `android.content.Context`, `android.graphics.Bitmap`, `android.graphics.BitmapFactory`, `android.graphics.Canvas`, `android.media.MediaMetadataRetriever`, `android.net.Uri`

### Proyecto MyNotes

`com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.links.LinkPreviewRepository`

### Kotlin / Coroutines / Java

`java.io.File`, `java.io.InputStream`, `java.security.MessageDigest`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 16 | `object` | `WidgetMediaPreview` | `` |
| 18 | `fun` | `firstVisualByNote` | `` |
| 26 | `fun` | `loadBestPreviewBitmap` | `` |
| 45 | `fun` | `cachedLinkPreviewFile` | `` |
| 51 | `fun` | `sha256` | `` |
| 58 | `fun` | `loadPreviewBitmap` | `` |
| 68 | `fun` | `loadVideoFrame` | `` |
| 82 | `fun` | `decodeSampledBitmap` | `` |
| 113 | `fun` | `scaleFitInside` | `` |
| 128 | `fun` | `calculateInSampleSize` | `` |

## 4. Estado, efectos y límites observables

- **I/O/red:** 6 aparición/apariciones.
- **try/catch:** 1 aparición/apariciones.
- **coerce*:** 3 aparición/apariciones.
- **safe calls:** 5 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.data.Attachment`
- `com.example.mynotes.data.Note`
- `com.example.mynotes.links.LinkPreviewRepository`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- No degradar calidad, rutas persistentes ni cachés de adjuntos/miniaturas sin una prueba explícita.
