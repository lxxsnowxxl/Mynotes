# InlineNoteAttachment.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/InlineNoteAttachment.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 1334 → 745 (44.2% menos)

## Responsabilidad

Representa adjuntos embebidos dentro del flujo visual de una nota y decide cómo mostrarlos de acuerdo con el tipo de contenido.

## Papel dentro de la arquitectura

Permite que detalle/editor presenten imágenes, medios o archivos con un tratamiento consistente sin duplicar la lógica de composición.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Compose:** `androidx.compose.foundation.Image`, `androidx.compose.foundation.background`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.gestures.detectTransformGestures`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.aspectRatio`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`….

**Android/Jetpack:** `android.app.ActivityManager`, `android.content.Context`, `android.net.Uri`, `android.view.TextureView`, `androidx.media3.common.MediaItem`, `androidx.media3.common.PlaybackException`, `androidx.media3.common.Player`, `androidx.media3.exoplayer.ExoPlayer`.

**Bibliotecas externas:** `coil3.compose.AsyncImage`, `coil3.request.CachePolicy`, `coil3.request.ImageRequest`, `coil3.request.allowHardware`, `coil3.request.maxBitmapSize`, `coil3.size.Precision`, `coil3.size.Scale`, `coil3.size.Size`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.delay`, `kotlinx.coroutines.withContext`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 88 | object | `InlinePlaybackCoordinator` | `private object InlinePlaybackCoordinator {` | Singleton que centraliza funciones/estado compartido sin crear múltiples instancias. |
| 90 | fun | `activate` | `fun activate(player: ExoPlayer) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 99 | fun | `clear` | `fun clear(player: ExoPlayer) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 115 | composable | `InlineNoteAttachment` | `fun InlineNoteAttachment(attachment: Attachment, fontFamily: FontFamily = FontFamily.Default, previewDelayMillis: Long = 0L,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 130 | composable | `InlineImageAttachment` | `private fun InlineImageAttachment(attachment: Attachment) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 271 | composable | `InlineVideoAttachment` | `private fun InlineVideoAttachment(attachment: Attachment, previewDelayMillis: Long, performanceMode: String) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 316 | fun | `onPlaybackStateChanged` | `override fun onPlaybackStateChanged(playbackState: Int) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 339 | fun | `onIsPlayingChanged` | `override fun onIsPlayingChanged(isPlaying: Boolean) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 342 | fun | `onPlayerError` | `override fun onPlayerError(error: PlaybackException) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 490 | composable | `InlineAudioAttachment` | `private fun InlineAudioAttachment(attachment: Attachment, fontFamily: FontFamily) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 523 | fun | `onPlaybackStateChanged` | `override fun onPlaybackStateChanged(playbackState: Int) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 546 | fun | `onIsPlayingChanged` | `override fun onIsPlayingChanged(isPlaying: Boolean) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 549 | fun | `onPlayerError` | `override fun onPlayerError(error: PlaybackException) {` | Manejador de un evento/ciclo de vida que coordina la respuesta correspondiente sin ser la fuente de datos. |
| 684 | composable | `InlinePdfAttachment` | `private fun InlinePdfAttachment(attachment: Attachment, previewDelayMillis: Long, performanceMode: String) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 713 | composable | `InlineFileAttachment` | `private fun InlineFileAttachment(attachment: Attachment, extension: String, fontFamily: FontFamily) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 740 | fun | `formatInlineDuration` | `private fun formatInlineDuration(durationMillis: Long): String {` | Convierte un valor a una representación textual o visual apropiada. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
