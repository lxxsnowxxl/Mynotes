# InlineNoteAttachment.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/InlineNoteAttachment.kt`  
**SHA-256:** `b378709c6468100289997cb3027bd6d69820287d9ac94bf13eb91ae1ba4e4908`  
**Líneas:** 746  
**Package:** `com.example.mynotes.ui.components`

## 1. Para qué existe este archivo

Renderiza adjuntos insertados/mostrados dentro del contenido de una nota.

## 2. Tipos/clases declarados

- Línea **88** — `private  object InlinePlaybackCoordinator`.

## 3. Estado, constantes y valores importantes

- **`InlineAttachmentShape`** (línea 85) inicia con `RoundedCornerShape(28.dp`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`activePlayer`** (línea 89) inicia con `null`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`extension`** (línea 117) inicia con `attachment.name?.substringAfterLast("."`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`context`** (línea 131) inicia con `LocalContext.current`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`uri`** (línea 132) inicia con `remember(attachment.uri`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`detailBitmapLimit`** (línea 144) inicia con `remember(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`activityManager`** (línea 145) inicia con `context.getSystemService(Context.ACTIVITY_SERVICE`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`fullResolutionRequest`** (línea 153) inicia con `remember(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`gestureModifier`** (línea 192) inicia con `if (zoomEnabled`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`zoomChange`** (línea 200) inicia con `newZoom / zoom`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`ratio`** (línea 282) inicia con `preview?.aspectRatio?.coerceIn(0.45f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`textureView`** (línea 283) inicia con `remember(uri`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`currentPlayer`** (línea 310) inicia con `player`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`listener`** (línea 315) inicia con `object : Player.Listener {`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`currentDuration`** (línea 381) inicia con `currentPlayer.duration.coerceAtLeast(0L`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`previewBitmap`** (línea 392) inicia con `preview?.bitmap`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`previewDuration`** (línea 425) inicia con `preview?.durationMillis ?: 0L`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`shownDuration`** (línea 426) inicia con `if (duration > 0`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`target`** (línea 442) inicia con `newPosition.toLong(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`bitmap`** (línea 695) inicia con `preview`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`typeLabel`** (línea 715) inicia con `extension.takeIf { it.isNotBlank(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`totalSeconds`** (línea 741) inicia con `durationMillis / 1000L`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`minutes`** (línea 742) inicia con `totalSeconds / 60L`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`seconds`** (línea 743) inicia con `totalSeconds % 60L`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `activate` — líneas 90–98

**Firma:** `fun activate(player: ExoPlayer)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `player: ExoPlayer`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `pause`.

### `clear` — líneas 99–103

**Firma:** `fun clear(player: ExoPlayer)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `player: ExoPlayer`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

### `InlineNoteAttachment` — líneas 115–127

**Firma:** `fun InlineNoteAttachment(attachment: Attachment, fontFamily: FontFamily = FontFamily.Default, previewDelayMillis: Long = 0L, performanceMode: String = "balanced")`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `attachment: Attachment`
- `fontFamily: FontFamily = FontFamily.Default`
- `previewDelayMillis: Long = 0L`
- `performanceMode: String = "balanced"`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `substringAfterLast`, `lowercase`, `orEmpty`, `InlineImageAttachment`, `InlineVideoAttachment`, `InlineAudioAttachment`, `InlinePdfAttachment`, `InlineFileAttachment`.

### `InlineImageAttachment` — líneas 130–268

**Firma:** `private fun InlineImageAttachment(attachment: Attachment)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `attachment: Attachment`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Procesa imágenes/bitmaps.
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `parse`, `getSystemService`, `Size`, `Builder`, `data`, `size`, `maxBitmapSize`, `precision`, `scale`, `allowHardware`, `memoryCachePolicy`, `build`, `LaunchedEffect`, `fillMaxWidth`, `clip`, `pointerInput`, `coerceIn`, `AsyncImage`.

### `InlineVideoAttachment` — líneas 271–487

**Firma:** `private fun InlineVideoAttachment(attachment: Attachment, previewDelayMillis: Long, performanceMode: String)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `attachment: Attachment`
- `previewDelayMillis: Long`
- `performanceMode: String`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Procesa imágenes/bitmaps.
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `parse`, `delay`, `loadVideoPreview`, `coerceIn`, `TextureView`, `mutableIntStateOf`, `DisposableEffect`, `onPlaybackStateChanged`, `coerceAtLeast`, `coerceAtMost`, `toLong`, `toInt`, `onIsPlayingChanged`, `onPlayerError`, `addListener`, `setVideoTextureView`, `setMediaItem`, `fromUri`.

### `onPlaybackStateChanged` — líneas 316–338

**Firma:** `override fun onPlaybackStateChanged(playbackState: Int)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `playbackState: Int`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `coerceAtLeast`, `coerceAtMost`, `toLong`, `toInt`.

### `onIsPlayingChanged` — líneas 339–341

**Firma:** `override fun onIsPlayingChanged(isPlaying: Boolean)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `isPlaying: Boolean`

**Salida:** Unit o inferido por Kotlin.

### `onPlayerError` — líneas 342–347

**Firma:** `override fun onPlayerError(error: PlaybackException)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `error: PlaybackException`

**Salida:** Unit o inferido por Kotlin.

### `InlineAudioAttachment` — líneas 490–681

**Firma:** `private fun InlineAudioAttachment(attachment: Attachment, fontFamily: FontFamily)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `attachment: Attachment`
- `fontFamily: FontFamily`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `parse`, `mutableIntStateOf`, `DisposableEffect`, `onPlaybackStateChanged`, `coerceAtLeast`, `coerceAtMost`, `toLong`, `toInt`, `onIsPlayingChanged`, `onPlayerError`, `addListener`, `setMediaItem`, `fromUri`, `prepare`, `activate`, `removeListener`, `clear`, `release`.

### `onPlaybackStateChanged` — líneas 523–545

**Firma:** `override fun onPlaybackStateChanged(playbackState: Int)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `playbackState: Int`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `coerceAtLeast`, `coerceAtMost`, `toLong`, `toInt`.

### `onIsPlayingChanged` — líneas 546–548

**Firma:** `override fun onIsPlayingChanged(isPlaying: Boolean)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `isPlaying: Boolean`

**Salida:** Unit o inferido por Kotlin.

### `onPlayerError` — líneas 549–554

**Firma:** `override fun onPlayerError(error: PlaybackException)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `error: PlaybackException`

**Salida:** Unit o inferido por Kotlin.

### `InlinePdfAttachment` — líneas 684–710

**Firma:** `private fun InlinePdfAttachment(attachment: Attachment, previewDelayMillis: Long, performanceMode: String)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `attachment: Attachment`
- `previewDelayMillis: Long`
- `performanceMode: String`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Procesa imágenes/bitmaps.
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `parse`, `delay`, `loadPdfFirstPage`, `toFloat`, `Image`, `asImageBitmap`, `fillMaxWidth`, `aspectRatio`, `coerceIn`, `clip`, `playAction`, `openAttachment`, `InlineFileAttachment`.

### `InlineFileAttachment` — líneas 713–738

**Firma:** `private fun InlineFileAttachment(attachment: Attachment, extension: String, fontFamily: FontFamily)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `attachment: Attachment`
- `extension: String`
- `fontFamily: FontFamily`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.

**Operaciones/funciones que coordina:** `isNotBlank`, `uppercase`, `fillMaxWidth`, `clip`, `playAction`, `openAttachment`, `padding`, `RoundedCornerShape`, `size`, `weight`, `spacedBy`.

### `formatInlineDuration` — líneas 740–745

**Firma:** `private fun formatInlineDuration(durationMillis: Long): String`

Convierte un valor interno a texto breve de presentación para la interfaz.

**Entradas:**
- `durationMillis: Long`

**Salida:** String.

**Operaciones/funciones que coordina:** `format`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.R`.
- Usa `com.example.mynotes.data.Attachment`.
- Usa `com.example.mynotes.performance.AttachmentPreviewCache`.
- Usa `com.example.mynotes.ui.sound.UiActionSound`.
- Usa `com.example.mynotes.ui.sound.UiSoundPlayer`.

## 6. Recursos Android que utiliza

- `R.string`: `audio`, `audio_playback_failed`, `file`, `image_format_not_supported`, `pause`, `play`, `tap_to_open`, `video_playback_failed`, `voice_note`

## 7. Tecnologías y efectos relevantes

- Procesa imágenes/bitmaps.
- Participa en estado/efectos de Compose.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `activate` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
2. `clear` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
3. `InlineNoteAttachment` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
4. `onPlaybackStateChanged` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
5. `onIsPlayingChanged` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
6. `onPlayerError` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
7. `onPlaybackStateChanged` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
8. `onIsPlayingChanged` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
9. `onPlayerError` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

## 9. Qué no debe romperse al modificarlo

- Evitar aumentar resoluciones/cargas sin considerar memoria y scroll; preservar caché y liberación de recursos.
- Conservar validaciones de Uri/ruta y no confiar en nombres externos sin sanitizar.
- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.
- Este archivo toca previews/adjuntos; cualquier cambio debe probar visualización y miniaturas en los perfiles de rendimiento.

## 10. Resumen en lenguaje sencillo

En términos simples: Renderiza adjuntos insertados/mostrados dentro del contenido de una nota. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
