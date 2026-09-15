# NoteViewModel.kt

**Ruta:** `app/src/main/java/com/example/mynotes/viewmodel/NoteViewModel.kt`  
**Paquete:** `com.example.mynotes.viewmodel`  
**Líneas:** 1186 → 502 (57.7% menos)

## Responsabilidad

ViewModel principal del dominio de notas. Expone flujos/estado derivados de Room y coordina altas, cambios y borrados de notas y adjuntos.

## Papel dentro de la arquitectura

Mantiene fuera de los composables la lógica de persistencia y ciclo de vida. La UI envía intenciones y observa datos, mientras el ViewModel coordina DAO, archivos y corrutinas.

## Flujo funcional principal

Flujo típico: Room emite notas/adjuntos -> el ViewModel expone estado observable -> NotesScreen/Detail reaccionan. Las acciones de crear/editar/borrar se ejecutan en corrutinas y actualizan la base/archivos, provocando nuevas emisiones.

## Dependencias y relaciones

**Dependencias internas de MyNotes:** `com.example.mynotes.data.AppDatabase`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.data.PendingAttachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.settings.SettingsRepository`.

**Android/Jetpack:** `android.app.Application`, `android.net.Uri`, `android.provider.OpenableColumns`, `androidx.lifecycle.AndroidViewModel`, `androidx.lifecycle.viewModelScope`, `androidx.room.withTransaction`.

**Kotlin/Java/corrutinas:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.flow.SharingStarted`, `kotlinx.coroutines.flow.distinctUntilChanged`, `kotlinx.coroutines.flow.first`, `kotlinx.coroutines.flow.stateIn`, `kotlinx.coroutines.launch`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withPermit`, `kotlinx.coroutines.withContext`, `java.io.File`.

## Declaraciones importantes detectadas

| Línea | Tipo | Nombre | Firma / declaración | Qué hace |
|---:|---|---|---|---|
| 26 | class | `NoteViewModel` | `class NoteViewModel(application: Application) : AndroidViewModel(application) {` | Clase que encapsula estado y comportamiento de esta parte del sistema. |
| 36 | data class | `PreparedAttachment` | `private data class PreparedAttachment(val attachment: Attachment, val internalUri: Uri, val type: String, val name: String?)` | Modelo de datos que agrupa valores relacionados con esta responsabilidad. |
| 61 | fun | `addNote` | `fun addNote(title: String, content: String, color: String = "default", attachments: List<PendingAttachment> = emptyList()) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 76 | fun | `saveAttachments` | `private suspend fun saveAttachments(noteId: Int, attachments: List<PendingAttachment>) {` | Guarda el estado o recurso indicado. |
| 92 | fun | `prepareAttachments` | `private suspend fun prepareAttachments(noteId: Int, attachments: List<PendingAttachment>, startingIndex: Int` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 112 | fun | `prewarmAttachments` | `private fun prewarmAttachments(attachments:` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 137 | fun | `copyAttachmentToInternalStorage` | `private suspend fun copyAttachmentToInternalStorage(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 150 | fun | `copyAttachmentToInternalStorageBlocking` | `private fun copyAttachmentToInternalStorageBlocking(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 231 | fun | `getDisplayName` | `private fun getDisplayName(uri: Uri): String? {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 256 | fun | `getExtension` | `private fun getExtension(originalName: String?, mimeType: String?, type: String): String {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 328 | fun | `defaultAttachmentName` | `private fun defaultAttachmentName(type: String, index: Int): String {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 342 | fun | `getAttachments` | `fun getAttachments(noteId: Int) = attachmentDao.getAttachments(noteId)` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 353 | fun | `updateNote` | `fun updateNote(note: Note, title: String, content: String, color: String = note.color, newAttachments:` | Modifica un elemento existente conservando su identidad lógica. |
| 381 | fun | `changeNoteColor` | `fun changeNoteColor(note: Note, color: String) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 391 | fun | `changePriority` | `fun changePriority(note: Note, priority: Int) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 401 | fun | `toggleFavorite` | `fun toggleFavorite(note: Note) {` | Alterna un estado booleano o equivalente. |
| 411 | fun | `togglePinned` | `fun togglePinned(note: Note) {` | Alterna un estado booleano o equivalente. |
| 423 | fun | `changeCategory` | `fun changeCategory(note: Note, category: String) {` | Función auxiliar o de coordinación; su firma muestra las entradas necesarias y el tipo de resultado que entrega a la siguiente etapa. |
| 444 | fun | `deleteAttachment` | `fun deleteAttachment(attachment: Attachment) {` | Elimina el dato/recurso indicado y ejecuta la limpieza asociada. |
| 473 | fun | `deleteNote` | `fun deleteNote(note: Note) {` | Elimina el dato/recurso indicado y ejecuta la limpieza asociada. |
| 493 | fun | `deletePrivateFileIfPresent` | `private fun deletePrivateFileIfPresent(uri: Uri) {` | Elimina el dato/recurso indicado y ejecuta la limpieza asociada. |

## Cómo leer este archivo

1. Empieza por las declaraciones públicas o composables: representan el contrato que usa el resto del proyecto. 2. Después revisa las funciones privadas: normalmente encapsulan normalización, cálculo, rendering o acceso a plataforma. 3. Los comentarios existentes se conservaron durante la compactación y explican decisiones específicas allí donde el código necesita contexto. 4. Consulta las dependencias internas anteriores para seguir el flujo hacia la capa que consume o persiste el resultado.

## Garantía de la compactación

En este archivo no se cambiaron identificadores, literales, operadores, comentarios, llamadas ni orden de tokens. La reducción proviene exclusivamente de compactar espacios/saltos de línea seguros. El proyecto fue comparado mediante el lexer de Kotlin y validado sintácticamente con PSI después del cambio.
