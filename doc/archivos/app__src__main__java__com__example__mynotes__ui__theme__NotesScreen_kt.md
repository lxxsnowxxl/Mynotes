# NotesScreen.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/NotesScreen.kt`  
**Paquete:** `com.example.mynotes.ui`  
**Líneas:** 1315 → 510 (61.2% menos)

## Responsabilidad

Pantalla principal Compose con encabezado, búsqueda, filtros/categorías, cuadrícula de notas y botón flotante de creación.

## Papel dentro de la arquitectura

Orquesta la presentación de colecciones de NoteCard y transforma eventos de usuario en callbacks hacia MainActivity/NoteViewModel.

## Flujo funcional principal

Flujo típico: recibe la lista ya observable -> aplica el estado de búsqueda/filtros/orden configurado -> construye la cuadrícula -> cada NoteCard envía acciones mediante callbacks -> el nivel superior modifica datos y la lista se recompone.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.ModernNoteCard`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.components.preloadLinkPreviews`, `com.example.mynotes.ui.components.toNoteCardStyle`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.viewmodel.NoteViewModel`.

**Compose:** `androidx.compose.foundation.clickable`, `androidx.compose.animation.core.animateDpAsState`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.BoxWithConstraints`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.lazy.LazyRow`, `androidx.compose.foundation.lazy.items`, `androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid`….

**Android/Jetpack:** `android.content.res.Configuration`, `android.net.Uri`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`.

**Bibliotecas externas:** `coil3.compose.AsyncImage`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.delay`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 87 | enum class | `NoteFilter` | `private enum class NoteFilter {` | Conjunto cerrado de valores nominales usados por esta parte del sistema. |
| 92 | data class | `NoteFilterOption` | `private data class NoteFilterOption(val filter: NoteFilter, val labelRes: Int)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 95 | data class | `AttachmentIndex` | `private data class AttachmentIndex(val byNote: Map<Int, List<Attachment>>, val kindsByNote: Map<Int, Set<String>>)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 102 | composable | `NotesScreen` | `fun NotesScreen(notes: List<Note>, noteViewModel: NoteViewModel, settings: AppSettings, onAddNote: () -> Unit, onOpenSettings: () -> Unit,` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 479 | composable | `EmptyNotesState` | `private fun EmptyNotesState(modifier: Modifier, hasSearch: Boolean, fontFamily:` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
