# NoteDetailScreen.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/NoteDetailScreen.kt`  
**Paquete:** `com.example.mynotes.ui`  
**Líneas:** 2093 → 705 (66.3% menos)

## Responsabilidad

Pantalla Compose de lectura/detalle de una nota. Presenta contenido, metadatos, enlaces y adjuntos, y expone acciones contextuales.

## Papel dentro de la arquitectura

Consume Note/Attachment y preferencias visuales; delega las operaciones reales al ViewModel mediante callbacks y reutiliza componentes de previews/menús.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.ui.components.AppAlertDialog`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.data.Note`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.components.InlineNoteAttachment`, `com.example.mynotes.ui.components.LinkPreviewCard`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.components.noteTextForDisplay`, `com.example.mynotes.ui.components.CategoryPill`, `com.example.mynotes.ui.motion.AnimatedScreenEntry`, `com.example.mynotes.ui.motion.AppMotion`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.compositeUiColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`, `com.example.mynotes.viewmodel.NoteViewModel`.

**Compose:** `androidx.compose.animation.Crossfade`, `androidx.compose.animation.core.tween`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.ExperimentalLayoutApi`, `androidx.compose.foundation.layout.FlowRow`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.navigationBarsPadding`, `androidx.compose.foundation.layout.padding`….

**Android/Jetpack:** `android.content.Context`, `android.content.Intent`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`.

**Kotlin/Java/corrutinas:** `java.text.DateFormat`, `java.util.Date`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 112 | composable | `NoteDetailScreen` | `fun NoteDetailScreen(note: Note, noteViewModel: NoteViewModel, settings: AppSettings, onBack: () -> Unit, onEdit: (Note) -> Unit) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 589 | composable | `PriorityPill` | `private fun PriorityPill(priority: Int, fontFamily: FontFamily = FontFamily.Default, neutralForeground: Color = Color(0xFF4A4A4A),` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 634 | composable | `DetailConfigurableMenuItem` | `private fun DetailConfigurableMenuItem(label: String, icon:` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 653 | composable | `DetailBottomAction` | `private fun DetailBottomAction(modifier: Modifier = Modifier, icon:` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 672 | fun | `shareNote` | `private fun shareNote(context: Context, note: Note) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 684 | fun | `normalizedDetailMenuOrder` | `private fun normalizedDetailMenuOrder(raw: String): List<String> {` | Normaliza una entrada para limitarla a los valores/formato admitidos por la aplicación. |
| 698 | fun | `parseDetailMenuKeys` | `private fun parseDetailMenuKeys(raw: String, validKeys: List<String>): Set<String> {` | Interpreta texto/datos externos y los convierte a una estructura utilizable. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
