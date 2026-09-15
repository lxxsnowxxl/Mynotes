# AttachmentPreviewTile.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/AttachmentPreviewTile.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 1008 → 329 (67.4% menos)

## Responsabilidad

Componente Compose reutilizable para mostrar una miniatura/tarjeta de un adjunto con sus controles contextuales.

## Papel dentro de la arquitectura

Encapsula la representación visual del adjunto para evitar duplicación entre editor u otras superficies y delega la generación pesada de previews al sistema de caché.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.openAttachmentViewer`.

**Compose:** `androidx.compose.foundation.Image`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Description`, `androidx.compose.material.icons.filled.Mic`….

**Android/Jetpack:** `android.content.Context`, `android.net.Uri`, `android.webkit.MimeTypeMap`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.delay`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 66 | composable | `AttachmentPreviewTile` | `fun AttachmentPreviewTile(attachment: Attachment, modifier: Modifier = Modifier, fontFamily: FontFamily = FontFamily.Default,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 111 | composable | `ImageAttachment` | `private fun ImageAttachment(attachment: Attachment, showName: Boolean, fontFamily: FontFamily, previewDelayMillis: Long,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 148 | composable | `VideoAttachment` | `private fun VideoAttachment(attachment: Attachment, uri: Uri, showName: Boolean, fontFamily: FontFamily, previewDelayMillis: Long,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 196 | composable | `AudioAttachment` | `private fun AudioAttachment(attachment: Attachment, uri: Uri, showName: Boolean, fontFamily: FontFamily, previewDelayMillis: Long,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 249 | composable | `FileAttachment` | `private fun FileAttachment(attachment: Attachment, uri: Uri, showName: Boolean, fontFamily: FontFamily, previewDelayMillis: Long,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 296 | composable | `DurationBadge` | `private fun DurationBadge(duration: Long, modifier: Modifier = Modifier) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 308 | fun | `formatDuration` | `private fun formatDuration(durationMillis: Long): String {` | Convierte un valor a una representación textual o visual apropiada. |
| 318 | fun | `openAttachment` | `fun openAttachment(context: Context, attachment: Attachment) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 323 | fun | `resolveMimeType` | `private fun resolveMimeType(context: Context, uri: Uri, name: String?): String {` | Resuelve una clave/estado a su representación efectiva aplicando reglas y fallbacks. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
