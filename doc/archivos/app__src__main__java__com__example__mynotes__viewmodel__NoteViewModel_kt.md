# NoteViewModel.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/viewmodel/NoteViewModel.kt`  
**SHA-256:** `5550157f9fe02c9e1aa09780223e9c0e96e3fe26c4402a8e6858020aca5fda19`  
**Líneas:** 524  
**Package:** `com.example.mynotes.viewmodel`

## 1. Para qué existe este archivo

Lógica de negocio de notas/adjuntos: CRUD, copia segura a almacenamiento interno, precalentamiento y limpieza de archivos.

## 2. Tipos/clases declarados

- Línea **27** — `class NoteViewModel`.
- Línea **37** — `private data  class PreparedAttachment`.

## 3. Estado, constantes y valores importantes

- **`database`** (línea 28) inicia con `AppDatabase.getDatabase(application`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`noteDao`** (línea 29) inicia con `database.noteDao(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`attachmentDao`** (línea 30) inicia con `database.attachmentDao(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settingsRepository`** (línea 31) inicia con `SettingsRepository(application`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`previewSemaphore`** (línea 36) inicia con `Semaphore(permits = 2`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`notes`** (línea 38) inicia con `noteDao.getAllNotes(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`allAttachments`** (línea 47) inicia con `attachmentDao.getAllAttachments(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`note`** (línea 67) inicia con `Note(title = title`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`noteId`** (línea 68) inicia con `noteDao.insertNote(note`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`prepared`** (línea 82) inicia con `prepareAttachments(noteId = noteId`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`realIndex`** (línea 100) inicia con `startingIndex + index`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`internalUri`** (línea 101) inicia con `copyAttachmentToInternalStorage(pendingAttachment = pendingAttachment`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`attachmentName`** (línea 104) inicia con `pendingAttachment.name?: getDisplayName(pendingAttachment.uri`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selectedPerformanceMode`** (línea 120) inicia con `settingsRepository.settings.first(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`context`** (línea 165) inicia con `getApplication<Application>(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`attachmentsDirectory`** (línea 166) inicia con `File(context.filesDir`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`sourceUri`** (línea 170) inicia con `pendingAttachment.uri`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`sourcePath`** (línea 176) inicia con `sourceUri.path`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`sourceFile`** (línea 178) inicia con `File(sourcePath`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`rootPath`** (línea 180) inicia con `attachmentsDirectory.canonicalFile.path`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`sourceCanonical`** (línea 181) inicia con `sourceFile.canonicalFile.path`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`originalName`** (línea 188) inicia con `pendingAttachment.name?: getDisplayName(sourceUri`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`extension`** (línea 189) inicia con `getExtension(originalName = originalName`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`fileName`** (línea 191) inicia con `"note_${noteId}_${System.currentTimeMillis(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`destinationFile`** (línea 192) inicia con `File(attachmentsDirectory`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`inputStream`** (línea 195) inicia con `context.contentResolver.openInputStream(sourceUri`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`nameIndex`** (línea 253) inicia con `cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`updatedNote`** (línea 369) inicia con `note.copy(title = title`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`currentAttachments`** (línea 375) inicia con `attachmentDao.getAttachmentsOnce(note.id`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`normalized`** (línea 443) inicia con `when (category`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`attachments`** (línea 495) inicia con `attachmentDao.getAttachmentsOnce(note.id`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `addNote` — líneas 62–72

**Firma:** `fun addNote(title: String, content: String, color: String = "default", attachments: List<PendingAttachment> = emptyList())`

Inserta la Note en Room; cuando obtiene el id real, prepara/copia los PendingAttachment y los guarda asociados. Después solicita actualización de widgets.

**Entradas:**
- `title: String`
- `content: String`
- `color: String = "default"`
- `attachments: List<PendingAttachment> = emptyList()`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `isBlank`, `isEmpty`, `Note`, `insertNote`, `toInt`, `saveAttachments`, `requestUpdate`.

### `saveAttachments` — líneas 78–90

**Firma:** `private suspend fun saveAttachments(noteId: Int, attachments: List<PendingAttachment>)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `noteId: Int`
- `attachments: List<PendingAttachment>`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Accede a la base Room/DAO.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `isEmpty`, `prepareAttachments`, `insertAttachments`, `prewarmAttachments`.

### `prepareAttachments` — líneas 94–113

**Firma:** `private suspend fun prepareAttachments(noteId: Int, attachments: List<PendingAttachment>, startingIndex: Int ): List<PreparedAttachment>`

Convierte adjuntos temporales en Attachment persistibles, copiando contenido a almacenamiento privado y generando nombres/tipos estables.

**Entradas:**
- `noteId: Int`
- `attachments: List<PendingAttachment>`
- `startingIndex: Int`

**Salida:** List<PreparedAttachment>.

**Efectos/APIs observados en el cuerpo:**
- Ejecuta trabajo de I/O fuera del hilo principal.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `withContext`, `copyAttachmentToInternalStorage`, `getDisplayName`, `defaultAttachmentName`, `add`, `PreparedAttachment`, `Attachment`, `toString`.

### `prewarmAttachments` — líneas 114–137

**Firma:** `private fun prewarmAttachments(attachments: List<PreparedAttachment>)`

Pide al AttachmentPreviewCache precargar previews según el modo performance/balanced/quality sin bloquear la UI.

**Entradas:**
- `attachments: List<PreparedAttachment>`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Ejecuta trabajo de I/O fuera del hilo principal.
- Lanza trabajo asíncrono mediante coroutines.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `launch`, `first`, `prewarm`.

### `copyAttachmentToInternalStorage` — líneas 150–158

**Firma:** `private suspend fun copyAttachmentToInternalStorage(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri?`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `pendingAttachment: PendingAttachment`
- `noteId: Int`
- `index: Int`

**Salida:** Uri?.

**Efectos/APIs observados en el cuerpo:**
- Ejecuta trabajo de I/O fuera del hilo principal.

**Operaciones/funciones que coordina:** `withContext`, `copyAttachmentToInternalStorageBlocking`.

### `copyAttachmentToInternalStorageBlocking` — líneas 163–238

**Firma:** `private fun copyAttachmentToInternalStorageBlocking(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri?`

Abre el Uri de origen, determina nombre/extensión, crea un archivo privado único y copia bytes; evita depender indefinidamente de permisos externos.

**Entradas:**
- `pendingAttachment: PendingAttachment`
- `noteId: Int`
- `index: Int`

**Salida:** Uri?.

**Efectos/APIs observados en el cuerpo:**
- Accede al sistema de archivos interno/cache.
- Lee/escribe Uris mediante ContentResolver.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `File`, `exists`, `mkdirs`, `startsWith`, `fromFile`, `getDisplayName`, `getExtension`, `getType`, `currentTimeMillis`, `openInputStream`, `buffered`, `outputStream`, `copyTo`, `inputStream`, `length`, `delete`, `printStackTrace`.

### `getDisplayName` — líneas 244–263

**Firma:** `private fun getDisplayName(uri: Uri): String?`

Obtiene el dato solicitado desde la fuente o estructura que maneja este archivo, sin cambiar el contrato público del resto del módulo.

**Entradas:**
- `uri: Uri`

**Salida:** String?.

**Efectos/APIs observados en el cuerpo:**
- Accede al sistema de archivos interno/cache.
- Lee/escribe Uris mediante ContentResolver.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `File`, `query`, `arrayOf`, `getColumnIndex`, `moveToFirst`, `getString`.

### `getExtension` — líneas 269–335

**Firma:** `private fun getExtension(originalName: String?, mimeType: String?, type: String): String`

Obtiene el dato solicitado desde la fuente o estructura que maneja este archivo, sin cambiar el contrato público del resto del módulo.

**Entradas:**
- `originalName: String?`
- `mimeType: String?`
- `type: String`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `isNullOrBlank`, `contains`, `substringAfterLast`, `lowercase`, `isNotBlank`.

### `defaultAttachmentName` — líneas 341–349

**Firma:** `private fun defaultAttachmentName(type: String, index: Int): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `type: String`
- `index: Int`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

### `getAttachments` — líneas 355–355

**Firma:** `fun getAttachments(noteId: Int)`

Obtiene el dato solicitado desde la fuente o estructura que maneja este archivo, sin cambiar el contrato público del resto del módulo.

**Entradas:**
- `noteId: Int`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Accede a la base Room/DAO.

**Operaciones/funciones que coordina:** `getAttachments`.

### `updateNote` — líneas 366–390

**Firma:** `fun updateNote(note: Note, title: String, content: String, color: String = note.color, newAttachments: List<PendingAttachment> = emptyList())`

Actualiza campos de la nota, compara adjuntos existentes/nuevos, persiste altas/bajas necesarias y refresca widgets.

**Entradas:**
- `note: Note`
- `title: String`
- `content: String`
- `color: String = note.color`
- `newAttachments: List<PendingAttachment> = emptyList()`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `copy`, `isEmpty`, `updateNote`, `requestUpdate`, `getAttachmentsOnce`, `prepareAttachments`, `isNotEmpty`, `insertAttachments`, `prewarmAttachments`.

### `changeNoteColor` — líneas 396–401

**Firma:** `fun changeNoteColor(note: Note, color: String)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `note: Note`
- `color: String`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.

**Operaciones/funciones que coordina:** `updateColor`, `requestUpdate`.

### `changePriority` — líneas 407–412

**Firma:** `fun changePriority(note: Note, priority: Int)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `note: Note`
- `priority: Int`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.

**Operaciones/funciones que coordina:** `updatePriority`, `requestUpdate`.

### `toggleFavorite` — líneas 418–423

**Firma:** `fun toggleFavorite(note: Note)`

Invierte el estado booleano asociado al elemento y propaga el cambio a la capa persistente; después actualiza las superficies que dependen de ese estado cuando el cuerpo lo solicita.

**Entradas:**
- `note: Note`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.

**Operaciones/funciones que coordina:** `updateFavorite`, `requestUpdate`.

### `togglePinned` — líneas 429–434

**Firma:** `fun togglePinned(note: Note)`

Invierte el estado booleano asociado al elemento y propaga el cambio a la capa persistente; después actualiza las superficies que dependen de ese estado cuando el cuerpo lo solicita.

**Entradas:**
- `note: Note`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.

**Operaciones/funciones que coordina:** `updatePinned`, `requestUpdate`.

### `changeCategory` — líneas 442–451

**Firma:** `fun changeCategory(note: Note, category: String)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `note: Note`
- `category: String`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `updateCategory`, `requestUpdate`.

### `deleteAttachment` — líneas 464–476

**Firma:** `fun deleteAttachment(attachment: Attachment)`

Elimina el elemento indicado. El cuerpo coordina la capa de persistencia y, cuando hay archivos asociados, realiza la limpieza correspondiente.

**Entradas:**
- `attachment: Attachment`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Ejecuta trabajo de I/O fuera del hilo principal.
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.

**Operaciones/funciones que coordina:** `deleteAttachment`, `invalidate`, `parse`, `withContext`, `deletePrivateFileIfPresent`.

### `deleteNote` — líneas 493–513

**Firma:** `fun deleteNote(note: Note)`

Elimina adjuntos/archivos privados relacionados y después borra la nota en Room para no dejar basura interna.

**Entradas:**
- `note: Note`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Ejecuta trabajo de I/O fuera del hilo principal.
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.

**Operaciones/funciones que coordina:** `getAttachmentsOnce`, `deleteAttachmentsForNote`, `deleteNote`, `requestUpdate`, `invalidate`, `parse`, `withContext`, `deletePrivateFileIfPresent`.

### `deletePrivateFileIfPresent` — líneas 514–522

**Firma:** `private fun deletePrivateFileIfPresent(uri: Uri)`

Elimina el elemento indicado. El cuerpo coordina la capa de persistencia y, cuando hay archivos asociados, realiza la limpieza correspondiente.

**Entradas:**
- `uri: Uri`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `exists`, `delete`, `printStackTrace`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.data.AppDatabase`.
- Usa `com.example.mynotes.data.Attachment`.
- Usa `com.example.mynotes.data.Note`.
- Usa `com.example.mynotes.data.PendingAttachment`.
- Usa `com.example.mynotes.performance.AttachmentPreviewCache`.
- Usa `com.example.mynotes.settings.SettingsRepository`.
- Usa `com.example.mynotes.widget.MyNotesWidgetUpdater`.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Ejecuta trabajo de I/O fuera del hilo principal.
- Lanza trabajo asíncrono mediante coroutines.
- Accede a la base Room/DAO.
- Accede al sistema de archivos interno/cache.
- Lee/escribe Uris mediante ContentResolver.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `addNote` — Inserta la Note en Room; cuando obtiene el id real, prepara/copia los PendingAttachment y los guarda asociados. Después solicita actualización de widgets.
2. `getAttachments` — Obtiene el dato solicitado desde la fuente o estructura que maneja este archivo, sin cambiar el contrato público del resto del módulo.
3. `updateNote` — Actualiza campos de la nota, compara adjuntos existentes/nuevos, persiste altas/bajas necesarias y refresca widgets.
4. `changeNoteColor` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
5. `changePriority` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
6. `toggleFavorite` — Invierte el estado booleano asociado al elemento y propaga el cambio a la capa persistente; después actualiza las superficies que dependen de ese estado cuando el cuerpo lo solicita.
7. `togglePinned` — Invierte el estado booleano asociado al elemento y propaga el cambio a la capa persistente; después actualiza las superficies que dependen de ese estado cuando el cuerpo lo solicita.
8. `changeCategory` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
9. `deleteAttachment` — Elimina el elemento indicado. El cuerpo coordina la capa de persistencia y, cuando hay archivos asociados, realiza la limpieza correspondiente.
10. `deleteNote` — Elimina adjuntos/archivos privados relacionados y después borra la nota en Room para no dejar basura interna.

## 9. Qué no debe romperse al modificarlo

- Conservar validaciones de Uri/ruta y no confiar en nombres externos sin sanitizar.

## 10. Resumen en lenguaje sencillo

En términos simples: Lógica de negocio de notas/adjuntos: CRUD, copia segura a almacenamiento interno, precalentamiento y limpieza de archivos. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
