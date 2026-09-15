# NoteEditorScreen.kt

**Ruta:** `app/src/main/java/com/example/mynotes/ui/theme/NoteEditorScreen.kt`  
**Paquete:** `com.example.mynotes.ui`  
**Líneas:** 2478 → 1004 (59.5% menos)

## Responsabilidad

Pantalla Compose para crear o editar una nota. Gestiona campos de texto, selección de color, adjuntos pendientes, audio/voz y acciones de guardar/cancelar.

## Papel dentro de la arquitectura

Mantiene estado transitorio de edición y lo entrega al nivel superior al confirmar; no debe sustituir la persistencia de NoteViewModel.

## Flujo funcional principal

Flujo típico: se inicializa el estado desde una nota existente o valores por defecto -> el usuario modifica campos/adjuntos/color -> los selectores del sistema devuelven URIs -> se mantienen como estado pendiente -> al guardar se entrega un snapshot coherente al callback superior.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.R`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.PendingAttachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.ui.components.LinkPreviewCard`, `com.example.mynotes.ui.components.extractLinkUrls`, `com.example.mynotes.ui.openAttachmentViewer`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.resolveUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.noteBackgroundColor`.

**Compose:** `androidx.compose.foundation.background`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.ExperimentalLayoutApi`, `androidx.compose.foundation.layout.FlowRow`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.rememberScrollState`….

**Android/Jetpack:** `android.Manifest`, `android.content.Context`, `android.content.res.Configuration`, `android.content.Intent`, `android.content.pm.PackageManager`, `android.media.MediaRecorder`, `android.os.Build`, `android.net.Uri`, `android.provider.OpenableColumns`, `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.core.content.ContextCompat`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.delay`, `java.io.File`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 108 | composable | `NoteEditorScreen` | `fun NoteEditorScreen(settings: AppSettings, initialTitle: String = "", initialContent: String = "", initialColor: String = "default",` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 205 | fun | `startRecording` | `fun startRecording() {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 248 | fun | `stopRecording` | `fun stopRecording() {` | Detiene el recurso o proceso activo y realiza la limpieza necesaria. |
| 331 | fun | `requestVoiceRecording` | `fun requestVoiceRecording() {` | Solicita a una API/capa inferior la operación descrita por el nombre de la función. |
| 813 | composable | `AttachmentButton` | `private fun AttachmentButton(modifier: Modifier = Modifier, text: String, icon: ImageVector, onClick: () -> Unit) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 837 | composable | `AttachmentPreview` | `private fun AttachmentPreview(attachment: PendingAttachment, previewDelayMillis: Long = 0L, performanceMode: String, onRemove: (() -> Unit)?` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 950 | composable | `AttachmentIconPreview` | `private fun AttachmentIconPreview(icon: ImageVector, title: String, name: String?) {` | Composable que construye una parte de la interfaz y reacciona al estado/callbacks recibidos. |
| 975 | fun | `getFileName` | `private fun getFileName(context: Context, uri: Uri): String? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
