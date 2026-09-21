# NoteViewModel.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/viewmodel/NoteViewModel.kt`  **SHA-256:** `5550157f9fe02c9e1aa09780223e9c0e96e3fe26c4402a8e6858020aca5fda19`  **Líneas:** 523 · **Bytes:** 21967 · **Imports:** 23 · **Declaraciones detectadas:** 18
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Estado/operaciones de notas y adjuntos; coordinación con Room y cachés.
## 2. Package e imports

Package declarado: `com.example.mynotes.viewmodel`.

### Android / Jetpack / Compose

`android.app.Application`, `android.net.Uri`, `android.provider.OpenableColumns`, `androidx.lifecycle.AndroidViewModel`, `androidx.lifecycle.viewModelScope`, `androidx.room.withTransaction`

### Proyecto MyNotes

`com.example.mynotes.data.AppDatabase`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.data.PendingAttachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.settings.SettingsRepository`, `com.example.mynotes.widget.MyNotesWidgetUpdater`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.flow.SharingStarted`, `kotlinx.coroutines.flow.distinctUntilChanged`, `kotlinx.coroutines.flow.first`, `kotlinx.coroutines.flow.stateIn`, `kotlinx.coroutines.launch`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withPermit`, `kotlinx.coroutines.withContext`, `java.io.File`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 26 | `class` | `NoteViewModel` | `` |
| 37 | `class` | `PreparedAttachment` | `private data class PreparedAttachment(val attachment: Attachment, val internalUri: Uri, val type: String, val name: String?)` |
| 62 | `fun` | `addNote` | `fun addNote(title: String, content: String, color: String = "default", attachments: List<PendingAttachment> = emptyList()) {` |
| 114 | `fun` | `prewarmAttachments` | `private fun prewarmAttachments(attachments:` |
| 163 | `fun` | `copyAttachmentToInternalStorageBlocking` | `private fun copyAttachmentToInternalStorageBlocking(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {` |
| 244 | `fun` | `getDisplayName` | `private fun getDisplayName(uri: Uri): String? {` |
| 269 | `fun` | `getExtension` | `private fun getExtension(originalName: String?, mimeType: String?, type: String): String {` |
| 341 | `fun` | `defaultAttachmentName` | `private fun defaultAttachmentName(type: String, index: Int): String {` |
| 355 | `fun` | `getAttachments` | `fun getAttachments(noteId: Int) = attachmentDao.getAttachments(noteId)` |
| 366 | `fun` | `updateNote` | `fun updateNote(note: Note, title: String, content: String, color: String = note.color, newAttachments:` |
| 396 | `fun` | `changeNoteColor` | `fun changeNoteColor(note: Note, color: String) {` |
| 407 | `fun` | `changePriority` | `fun changePriority(note: Note, priority: Int) {` |
| 418 | `fun` | `toggleFavorite` | `fun toggleFavorite(note: Note) {` |
| 429 | `fun` | `togglePinned` | `fun togglePinned(note: Note) {` |
| 442 | `fun` | `changeCategory` | `fun changeCategory(note: Note, category: String) {` |
| 464 | `fun` | `deleteAttachment` | `fun deleteAttachment(attachment: Attachment) {` |
| 493 | `fun` | `deleteNote` | `fun deleteNote(note: Note) {` |
| 514 | `fun` | `deletePrivateFileIfPresent` | `private fun deletePrivateFileIfPresent(uri: Uri) {` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 17 aparición/apariciones.
- **Flow/StateFlow:** 3 aparición/apariciones.
- **Room:** 3 aparición/apariciones.
- **I/O/red:** 6 aparición/apariciones.
- **try/catch:** 6 aparición/apariciones.
- **safe calls:** 5 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.data.AppDatabase`
- `com.example.mynotes.data.Attachment`
- `com.example.mynotes.data.Note`
- `com.example.mynotes.data.PendingAttachment`
- `com.example.mynotes.performance.AttachmentPreviewCache`
- `com.example.mynotes.settings.SettingsRepository`
- `com.example.mynotes.widget.MyNotesWidgetUpdater`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
