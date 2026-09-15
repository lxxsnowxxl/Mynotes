# NoteCard.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/NoteCard.kt`  
**Paquete:** `com.example.mynotes.ui.components`  
**Líneas:** 2103 → 747 (64.5% menos)

## Responsabilidad

Componente Compose que representa cada nota en la cuadrícula/lista principal, incluidos texto, adjuntos, metadatos y menú contextual de tres puntos.

## Papel dentro de la arquitectura

Conecta las preferencias visuales de las tarjetas con callbacks de editar, fijar, favoritos, prioridad, color, mover y borrar. También integra previews cacheados.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.compositeUiColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`.

**Compose:** `androidx.compose.animation.AnimatedVisibility`, `androidx.compose.animation.Crossfade`, `androidx.compose.animation.fadeIn`, `androidx.compose.animation.fadeOut`, `androidx.compose.animation.scaleIn`, `androidx.compose.animation.scaleOut`, `androidx.compose.animation.animateColorAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.animation.animateContentSize`, `androidx.compose.foundation.background`, `androidx.compose.foundation.Image`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.interaction.MutableInteractionSource`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`….

**Android/Jetpack:** `android.net.Uri`.

**Kotlin/Java/corrutinas:** `java.text.DateFormat`, `java.util.Date`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 119 | composable | `ModernNoteCard` | `fun ModernNoteCard(note: Note, attachments: List<Attachment>, fontFamily: FontFamily, fontSize: Float, noteUiTextColor: String,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 503 | composable | `CategoryPill` | `fun CategoryPill(category: String, fontFamily: FontFamily = FontFamily.Default) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 531 | composable | `NoteCardAttachmentsPreview` | `private fun NoteCardAttachmentsPreview(attachments: List<Attachment>, previewHeight: androidx.compose.ui.unit.Dp,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 564 | composable | `NoteCardAttachmentTile` | `private fun NoteCardAttachmentTile(attachment: Attachment, modifier: Modifier, compact: Boolean, fontFamily: FontFamily,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 643 | composable | `FileLikeFallbackTile` | `private fun FileLikeFallbackTile(icon: ImageVector, label: String, compact: Boolean, fontFamily: FontFamily) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 655 | composable | `SmallDurationBadge` | `private fun SmallDurationBadge(duration: Long, modifier: Modifier = Modifier) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 662 | fun | `formatSmallDuration` | `private fun formatSmallDuration(durationMillis: Long): String {` | Convierte un valor a una representación textual o visual apropiada. |
| 670 | composable | `ConfigurableDropdownMenuItem` | `private fun ConfigurableDropdownMenuItem(label: String, icon: ImageVector, showIcon: Boolean, textColor: Color, onClick: () -> Unit) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 685 | composable | `PriorityMenuItem` | `private fun PriorityMenuItem(label: String, selected: Boolean, textColor: Color, showIndicator: Boolean, onClick: () -> Unit) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 711 | composable | `ColorMenuItem` | `private fun ColorMenuItem(label: String, color: Color, textColor: Color, showSwatch: Boolean, onClick: () -> Unit) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 727 | fun | `normalizedMenuOrder` | `private fun normalizedMenuOrder(raw: String, validKeys: List<String>): List<String> {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 740 | fun | `parseMenuKeys` | `private fun parseMenuKeys(raw: String, validKeys: List<String>): Set<String> {` | Interpreta texto/datos externos y los convierte a una estructura utilizable. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
