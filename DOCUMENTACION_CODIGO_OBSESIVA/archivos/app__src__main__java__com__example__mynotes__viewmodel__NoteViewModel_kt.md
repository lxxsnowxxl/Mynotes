# NoteViewModel.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/viewmodel/NoteViewModel.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `967ee97f387acebc5493b8cd4a11dfd657f3d383eb65668e87d3ef8601303b51`  
**Líneas del código real:** 502

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

ViewModel principal del dominio de notas. Expone flujos/estado derivados de Room y coordina altas, cambios y borrados de notas y adjuntos.

**Arquitectura.** Mantiene fuera de los composables la lógica de persistencia y ciclo de vida. La UI envía intenciones y observa datos, mientras el ViewModel coordina DAO, archivos y corrutinas.

**Flujo general.** Flujo típico: Room emite notas/adjuntos -> el ViewModel expone estado observable -> NotesScreen/Detail reaccionan. Las acciones de crear/editar/borrar se ejecutan en corrutinas y actualizan la base/archivos, provocando nuevas emisiones.

## 2. Package e imports

El `package` es `com.example.mynotes.viewmodel`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **22 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.app.Application`, `android.net.Uri`, `android.provider.OpenableColumns`.

**Jetpack/Compose:** `androidx.lifecycle.AndroidViewModel`, `androidx.lifecycle.viewModelScope`, `androidx.room.withTransaction`.

**Proyecto MyNotes:** `com.example.mynotes.data.AppDatabase`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.data.PendingAttachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.settings.SettingsRepository`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.flow.SharingStarted`, `kotlinx.coroutines.flow.distinctUntilChanged`, `kotlinx.coroutines.flow.first`, `kotlinx.coroutines.flow.stateIn`, `kotlinx.coroutines.launch`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withPermit`, `kotlinx.coroutines.withContext`, `java.io.File`.

## 3. Restricciones e invariantes visibles en el archivo

- **Nulabilidad segura (5 aparición/apariciones):** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`.
- **Fallback nulo (7 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Filtro condicional (1 aparición/apariciones):** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`.
- **Trabajo IO (6 aparición/apariciones):** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal.

## 4. Bloques de código, uno por uno

### 4.1 `NoteViewModel` — class, líneas 26–502

```kotlin
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

ViewModel principal del dominio de notas. Expone flujos/estado derivados de Room y coordina altas, cambios y borrados de notas y adjuntos.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `application: Application` — `application` recibe un valor de tipo `Application`. El contrato no marca este parámetro como anulable.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 27 | `val database` | `inferido` | `AppDatabase.getDatabase(application)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 28 | `val noteDao` | `inferido` | `database.noteDao()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Referencia un DAO de Room; sus operaciones representan acceso estructurado a la base de datos. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 29 | `val attachmentDao` | `inferido` | `database.attachmentDao()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Referencia un DAO de Room; sus operaciones representan acceso estructurado a la base de datos. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 30 | `val settingsRepository` | `inferido` | `SettingsRepository(application)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Referencia una capa de repositorio, separando la coordinación de UI/estado del acceso y persistencia de datos. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 35 | `val previewSemaphore` | `inferido` | `Semaphore(permits = 2)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 37 | `val notes` | `inferido` | `noteDao.getAllNotes().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarte…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 46 | `val allAttachments` | `inferido` | `attachmentDao.getAllAttachments().distinctUntilChanged().stateIn(scope = viewModelScope, started = S…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 66 | `val note` | `inferido` | `Note(title = title, content = content, color = color)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 67 | `val noteId` | `inferido` | `noteDao.insertNote(note).toInt()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 80 | `val prepared` | `inferido` | `prepareAttachments(noteId = noteId, attachments = attachments, startingIndex = 0)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 98 | `val realIndex` | `inferido` | `startingIndex + index` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 99 | `val internalUri` | `inferido` | `copyAttachmentToInternalStorage(pendingAttachment = pendingAttachment, noteId = noteId, index =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 102 | `val attachmentName` | `inferido` | `pendingAttachment.name?: getDisplayName(pendingAttachment.uri)?: defaultAttachmentName(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 152 | `val context` | `inferido` | `getApplication<Application>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 153 | `val attachmentsDirectory` | `inferido` | `File(context.filesDir, "attachments")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 157 | `val sourceUri` | `inferido` | `pendingAttachment.uri` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 163 | `val sourcePath` | `inferido` | `sourceUri.path` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 165 | `val sourceFile` | `inferido` | `File(sourcePath)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 167 | `val rootPath` | `inferido` | `attachmentsDirectory.canonicalFile.path` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 168 | `val sourceCanonical` | `inferido` | `sourceFile.canonicalFile.path` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 175 | `val originalName` | `inferido` | `pendingAttachment.name?: getDisplayName(sourceUri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 176 | `val extension` | `inferido` | `getExtension(originalName = originalName, mimeType = pendingAttachment.mimeType?: context.contentRes…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 178 | `val fileName` | `inferido` | `"note_${noteId}_${System.currentTimeMillis()}_${index}.$extension"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 179 | `val destinationFile` | `inferido` | `File(attachmentsDirectory, fileName)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 182 | `val inputStream` | `inferido` | `context.contentResolver.openInputStream(sourceUri)?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 192 | `val sourcePath` | `inferido` | `sourceUri.path?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 193 | `val sourceFile` | `inferido` | `File(sourcePath)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 206 | `val inputStream` | `inferido` | `context.contentResolver.openInputStream(sourceUri)?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 232 | `val context` | `inferido` | `getApplication<Application>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 240 | `val nameIndex` | `inferido` | `cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 262 | `val extension` | `inferido` | `originalName.substringAfterLast(".").lowercase()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 356 | `val updatedNote` | `inferido` | `note.copy(title = title, content = content, color = color)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 361 | `val currentAttachments` | `inferido` | `attachmentDao.getAttachmentsOnce(note.id)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 362 | `val prepared` | `inferido` | `prepareAttachments(noteId = note.id, attachments = newAttachments, startingIndex = currentAttachment…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 424 | `val normalized` | `inferido` | `when (category) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 475 | `val attachments` | `inferido` | `attachmentDao.getAttachmentsOnce(note.id)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 62 | `if (title.isBlank() && content.isBlank() && attachments.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 63 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 77 | `if (attachments.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 78 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 81 | `if (prepared.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 82 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 94 | `return withContext(Dispatchers.IO) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 97 | `index, pendingAttachment ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 101 | `if (internalUri != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 115 | `prepared ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 142 | `return withContext(Dispatchers.IO) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 151 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 154 | `if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 155 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 162 | `if (sourceUri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 164 | `if (sourcePath != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 166 | `if (sourceFile.exists()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 169 | `if (sourceCanonical == rootPath \|\| sourceCanonical.startsWith(rootPath + File.separator)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 170 | `return Uri.fromFile(sourceFile)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 180 | `when (sourceUri.scheme) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 181 | `"content" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 184 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 186 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 191 | `"file" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 194 | `if (!sourceFile.exists()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 195 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 198 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 200 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 205 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 208 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 210 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 216 | `if (!destinationFile.exists() \|\| destinationFile.length() <= 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 218 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 233 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 234 | `return uri.path?.let {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 238 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 241 | `if (nameIndex >= 0 && cursor.moveToFirst()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 261 | `if (!originalName.isNullOrBlank() && originalName.contains(".")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 263 | `if (extension.isNotBlank() && extension.length <= 10) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 264 | `return extension` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 271 | `return when (mimeType) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 275 | `"image/jpeg", "image/jpg" -> "jpg"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 276 | `"image/png" -> "png"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 277 | `"image/webp" -> "webp"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 278 | `"image/gif" -> "gif"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 279 | `"image/heic" -> "heic"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 280 | `"image/heif" -> "heif"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 284 | `"video/mp4" -> "mp4"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 285 | `"video/3gpp" -> "3gp"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 286 | `"video/webm" -> "webm"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 287 | `"video/quicktime" -> "mov"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 291 | `"audio/mpeg" -> "mp3"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 292 | `"audio/mp4", "audio/x-m4a" -> "m4a"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 293 | `"audio/wav", "audio/x-wav" -> "wav"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 294 | `"audio/ogg" -> "ogg"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 295 | `"audio/flac" -> "flac"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 296 | `"audio/aac" -> "aac"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 300 | `"application/pdf" -> "pdf"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 301 | `"text/plain" -> "txt"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 302 | `"application/zip" -> "zip"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 303 | `"application/json" -> "json"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 304 | `"application/msword" -> "doc"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 305 | `"application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 306 | `"application/vnd.ms-excel" -> "xls"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 307 | `"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 308 | `"application/vnd.ms-powerpoint" -> "ppt"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 309 | `"application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 313 | `else ->` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 314 | `when (type) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 315 | `"image" -> "jpg"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 316 | `"video" -> "mp4"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 317 | `"audio" -> "mp3"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 318 | `"voice" -> "m4a"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 319 | `else -> "bin"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 329 | `return when (type) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 330 | `"image" -> "Imagen ${index + 1}"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 331 | `"video" -> "Video ${index + 1}"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 332 | `"audio" -> "Audio ${index + 1}"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 333 | `"voice" -> "Nota de voz ${index + 1}"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 334 | `else -> "Archivo ${index + 1}"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

> Se detectaron 88 estructuras; la tabla limita la vista a las primeras 80 para no duplicar de forma inútil el código completo. El fragmento de código anterior conserva todas.

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 5.
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 7.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 6.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.
- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `AndroidViewModel`, `AppDatabase.getDatabase`, `database.noteDao`, `database.attachmentDao`, `SettingsRepository`, `Semaphore`, `noteDao.getAllNotes`, `distinctUntilChanged`, `stateIn`, `WhileSubscribed`, `emptyList`, `attachmentDao.getAllAttachments`, `title.isBlank`, `content.isBlank`, `attachments.isEmpty`, `Note`, `noteDao.insertNote`, `toInt`, `saveAttachments`, `prepareAttachments`, `prepared.isEmpty`, `attachmentDao.insertAttachments`, `prewarmAttachments`, `withContext`, `copyAttachmentToInternalStorage`, `getDisplayName`, `defaultAttachmentName`, `add`, `PreparedAttachment`, `Attachment`, `internalUri.toString`, `viewModelScope.launch`, `AttachmentPreviewCache.prewarm`, `settingsRepository.settings.first`, `copyAttachmentToInternalStorageBlocking`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.2 `PreparedAttachment` — class, líneas 36–70

```kotlin
    private data class PreparedAttachment(val attachment: Attachment, val internalUri: Uri, val type: String, val name: String?)
    val notes = noteDao.getAllNotes().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarted
                        .WhileSubscribed(5000), initialValue = emptyList())
    /*
     * =========================================================
     * TODOS LOS ADJUNTOS EN UN SOLO FLOW
     * =========================================================
     *
     * NotesScreen observa esta lista una sola vez.
     */
    val allAttachments = attachmentDao.getAllAttachments().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarted
                        .WhileSubscribed(5000), initialValue = emptyList())
    /*
     * =========================================================
     * CREAR NOTA
     * =========================================================
     *
     * Ahora una nota puede recibir:
     *
     * image
     * video
     * audio
     * voice
     * file
     */
    fun addNote(title: String, content: String, color: String = "default", attachments: List<PendingAttachment> = emptyList()) {
        if (title.isBlank() && content.isBlank() && attachments.isEmpty()) {
            return
        }
        viewModelScope.launch {
            val note = Note(title = title, content = content, color = color)
            val noteId = noteDao.insertNote(note).toInt()
            saveAttachments(noteId = noteId, attachments = attachments)
        }
    }
```

#### Qué hace y por qué existe

ViewModel principal del dominio de notas. Expone flujos/estado derivados de Room y coordina altas, cambios y borrados de notas y adjuntos.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `val attachment: Attachment` — `attachment` recibe un valor de tipo `Attachment`. El contrato no marca este parámetro como anulable. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val internalUri: Uri` — `internalUri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val type: String` — `type` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
- `val name: String?` — `name` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Además, al declararse con `val/var` en el constructor se convierte en propiedad de la instancia.
**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 37 | `val notes` | `inferido` | `noteDao.getAllNotes().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarte…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 46 | `val allAttachments` | `inferido` | `attachmentDao.getAllAttachments().distinctUntilChanged().stateIn(scope = viewModelScope, started = S…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 66 | `val note` | `inferido` | `Note(title = title, content = content, color = color)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 67 | `val noteId` | `inferido` | `noteDao.insertNote(note).toInt()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 62 | `if (title.isBlank() && content.isBlank() && attachments.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 63 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `noteDao.getAllNotes`, `distinctUntilChanged`, `stateIn`, `WhileSubscribed`, `emptyList`, `attachmentDao.getAllAttachments`, `title.isBlank`, `content.isBlank`, `attachments.isEmpty`, `Note`, `noteDao.insertNote`, `toInt`, `saveAttachments`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `addNote` — fun, líneas 61–75

```kotlin
    fun addNote(title: String, content: String, color: String = "default", attachments: List<PendingAttachment> = emptyList()) {
        if (title.isBlank() && content.isBlank() && attachments.isEmpty()) {
            return
        }
        viewModelScope.launch {
            val note = Note(title = title, content = content, color = color)
            val noteId = noteDao.insertNote(note).toInt()
            saveAttachments(noteId = noteId, attachments = attachments)
        }
    }
    /*
     * =========================================================
     * GUARDAR ADJUNTOS
     * =========================================================
     */
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `content: String` — `content` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `color: String = "default"` — `color` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `"default"`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `attachments: List<PendingAttachment> = emptyList()` — `attachments` recibe un valor de tipo `List<PendingAttachment>`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `emptyList()`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 66 | `val note` | `inferido` | `Note(title = title, content = content, color = color)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 67 | `val noteId` | `inferido` | `noteDao.insertNote(note).toInt()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 62 | `if (title.isBlank() && content.isBlank() && attachments.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 63 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `emptyList`, `title.isBlank`, `content.isBlank`, `attachments.isEmpty`, `Note`, `noteDao.insertNote`, `toInt`, `saveAttachments`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `saveAttachments` — fun, líneas 76–88

```kotlin
    private suspend fun saveAttachments(noteId: Int, attachments: List<PendingAttachment>) {
        if (attachments.isEmpty()) {
            return
        }
        val prepared = prepareAttachments(noteId = noteId, attachments = attachments, startingIndex = 0)
        if (prepared.isEmpty()) {
            return
        }
        attachmentDao.insertAttachments(prepared.map {
                    it.attachment
                })
        prewarmAttachments(prepared)
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `noteId: Int` — `noteId` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `attachments: List<PendingAttachment>` — `attachments` recibe un valor de tipo `List<PendingAttachment>`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 80 | `val prepared` | `inferido` | `prepareAttachments(noteId = noteId, attachments = attachments, startingIndex = 0)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 77 | `if (attachments.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 78 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 81 | `if (prepared.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 82 | `return` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `attachments.isEmpty`, `prepareAttachments`, `prepared.isEmpty`, `attachmentDao.insertAttachments`, `prewarmAttachments`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `prepareAttachments` — fun, líneas 92–111

```kotlin
    private suspend fun prepareAttachments(noteId: Int, attachments: List<PendingAttachment>, startingIndex: Int
    ): List<PreparedAttachment> {
        return withContext(Dispatchers.IO) {
            buildList {
                attachments.forEachIndexed {
                            index, pendingAttachment ->
                        val realIndex = startingIndex + index
                        val internalUri = copyAttachmentToInternalStorage(pendingAttachment = pendingAttachment, noteId = noteId, index =
                                    realIndex)
                        if (internalUri != null) {
                            val attachmentName = pendingAttachment.name?: getDisplayName(pendingAttachment.uri)?: defaultAttachmentName(
                                        pendingAttachment.type, realIndex)
                            add(PreparedAttachment(attachment = Attachment(noteId = noteId, type = pendingAttachment.type, uri =
                                                internalUri.toString(), name = attachmentName), internalUri = internalUri, type =
                                        pendingAttachment.type, name = attachmentName))
                        }
                    }
            }
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `noteId: Int` — `noteId` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `attachments: List<PendingAttachment>` — `attachments` recibe un valor de tipo `List<PendingAttachment>`. El contrato no marca este parámetro como anulable.
- `startingIndex: Int` — `startingIndex` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `List<PreparedAttachment>`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 98 | `val realIndex` | `inferido` | `startingIndex + index` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 99 | `val internalUri` | `inferido` | `copyAttachmentToInternalStorage(pendingAttachment = pendingAttachment, noteId = noteId, index =` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 102 | `val attachmentName` | `inferido` | `pendingAttachment.name?: getDisplayName(pendingAttachment.uri)?: defaultAttachmentName(` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 94 | `return withContext(Dispatchers.IO) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 97 | `index, pendingAttachment ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 101 | `if (internalUri != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 2.
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `copyAttachmentToInternalStorage`, `getDisplayName`, `defaultAttachmentName`, `add`, `PreparedAttachment`, `Attachment`, `internalUri.toString`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.

### 4.6 `prewarmAttachments` — fun, líneas 112–124

```kotlin
    private fun prewarmAttachments(attachments:
        List<PreparedAttachment>) {
        attachments.forEach {
                prepared ->
            viewModelScope.launch(Dispatchers.IO) {
                previewSemaphore.withPermit {
                        AttachmentPreviewCache.prewarm(context = getApplication<Application>(), uri = prepared.internalUri, type =
                                    prepared.type, name = prepared.name, performanceMode =
                                    settingsRepository.settings.first().performanceMode)
                    }
            }
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `attachments: List<PreparedAttachment>` — `attachments` recibe un valor de tipo `List<PreparedAttachment>`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 115 | `prepared ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `viewModelScope.launch`, `AttachmentPreviewCache.prewarm`, `settingsRepository.settings.first`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.7 `copyAttachmentToInternalStorage` — fun, líneas 137–145

```kotlin
    private suspend fun copyAttachmentToInternalStorage(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {
        /*
         * Cambiamos al dispatcher de I/O y delegamos en una
         * función normal que devuelve Uri? directamente.
         */
        return withContext(Dispatchers.IO) {
            copyAttachmentToInternalStorageBlocking(pendingAttachment = pendingAttachment, noteId = noteId, index = index)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `pendingAttachment: PendingAttachment` — `pendingAttachment` recibe un valor de tipo `PendingAttachment`. El contrato no marca este parámetro como anulable.
- `noteId: Int` — `noteId` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `index: Int` — `index` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Uri?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 142 | `return withContext(Dispatchers.IO) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `withContext`, `copyAttachmentToInternalStorageBlocking`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.

### 4.8 `copyAttachmentToInternalStorageBlocking` — fun, líneas 150–225

```kotlin
    private fun copyAttachmentToInternalStorageBlocking(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {
        return try {
            val context = getApplication<Application>()
            val attachmentsDirectory = File(context.filesDir, "attachments")
            if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs()) {
                return null
            }
            val sourceUri = pendingAttachment.uri
            /*
             * Si ya es un archivo privado de MyNotes,
             * no hacemos una copia adicional.
             */
            if (sourceUri.scheme == "file") {
                val sourcePath = sourceUri.path
                if (sourcePath != null) {
                    val sourceFile = File(sourcePath)
                    if (sourceFile.exists()) {
                        val rootPath = attachmentsDirectory.canonicalFile.path
                        val sourceCanonical = sourceFile.canonicalFile.path
                        if (sourceCanonical == rootPath || sourceCanonical.startsWith(rootPath + File.separator)) {
                            return Uri.fromFile(sourceFile)
                        }
                    }
                }
            }
            val originalName = pendingAttachment.name?: getDisplayName(sourceUri)
            val extension = getExtension(originalName = originalName, mimeType = pendingAttachment.mimeType?: context.contentResolver
                                .getType(sourceUri), type = pendingAttachment.type)
            val fileName = "note_${noteId}_${System.currentTimeMillis()}_${index}.$extension"
            val destinationFile = File(attachmentsDirectory, fileName)
            when (sourceUri.scheme) {
                "content" -> {
                    val inputStream = context.contentResolver.openInputStream(sourceUri)?: return null
                    inputStream.buffered(64 * 1024).use {
                                input ->
                            destinationFile.outputStream().buffered(64 * 1024).use {
                                        output ->
                                    input.copyTo(out = output, bufferSize = 64 * 1024)
                                }
                        }
                }
                "file" -> {
                    val sourcePath = sourceUri.path?: return null
                    val sourceFile = File(sourcePath)
                    if (!sourceFile.exists()) {
                        return null
                    }
                    sourceFile.inputStream().buffered(64 * 1024).use {
                                input ->
                            destinationFile.outputStream().buffered(64 * 1024).use {
                                        output ->
                                    input.copyTo(out = output, bufferSize = 64 * 1024)
                                }
                        }
                }
                else -> {
                    val inputStream = context.contentResolver.openInputStream(sourceUri)?: return null
                    inputStream.buffered(64 * 1024).use {
                                input ->
                            destinationFile.outputStream().buffered(64 * 1024).use {
                                        output ->
                                    input.copyTo(out = output, bufferSize = 64 * 1024)
                                }
                        }
                }
            }
            if (!destinationFile.exists() || destinationFile.length() <= 0L) {
                destinationFile.delete()
                return null
            }
            Uri.fromFile(destinationFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `pendingAttachment: PendingAttachment` — `pendingAttachment` recibe un valor de tipo `PendingAttachment`. El contrato no marca este parámetro como anulable.
- `noteId: Int` — `noteId` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.
- `index: Int` — `index` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Uri?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 152 | `val context` | `inferido` | `getApplication<Application>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 153 | `val attachmentsDirectory` | `inferido` | `File(context.filesDir, "attachments")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 157 | `val sourceUri` | `inferido` | `pendingAttachment.uri` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 163 | `val sourcePath` | `inferido` | `sourceUri.path` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 165 | `val sourceFile` | `inferido` | `File(sourcePath)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 167 | `val rootPath` | `inferido` | `attachmentsDirectory.canonicalFile.path` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 168 | `val sourceCanonical` | `inferido` | `sourceFile.canonicalFile.path` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 175 | `val originalName` | `inferido` | `pendingAttachment.name?: getDisplayName(sourceUri)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 176 | `val extension` | `inferido` | `getExtension(originalName = originalName, mimeType = pendingAttachment.mimeType?: context.contentRes…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 178 | `val fileName` | `inferido` | `"note_${noteId}_${System.currentTimeMillis()}_${index}.$extension"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 179 | `val destinationFile` | `inferido` | `File(attachmentsDirectory, fileName)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 182 | `val inputStream` | `inferido` | `context.contentResolver.openInputStream(sourceUri)?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 192 | `val sourcePath` | `inferido` | `sourceUri.path?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 193 | `val sourceFile` | `inferido` | `File(sourcePath)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 206 | `val inputStream` | `inferido` | `context.contentResolver.openInputStream(sourceUri)?: return null` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 151 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 154 | `if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 155 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 162 | `if (sourceUri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 164 | `if (sourcePath != null) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 166 | `if (sourceFile.exists()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 169 | `if (sourceCanonical == rootPath \|\| sourceCanonical.startsWith(rootPath + File.separator)) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 170 | `return Uri.fromFile(sourceFile)` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 180 | `when (sourceUri.scheme) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 181 | `"content" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 184 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 186 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 191 | `"file" -> {` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 194 | `if (!sourceFile.exists()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 195 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 198 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 200 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 205 | `else -> {` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 208 | `input ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 210 | `output ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 216 | `if (!destinationFile.exists() \|\| destinationFile.length() <= 0L) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 218 | `return null` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 5.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `attachmentsDirectory.exists`, `attachmentsDirectory.mkdirs`, `sourceFile.exists`, `sourceCanonical.startsWith`, `Uri.fromFile`, `getDisplayName`, `getExtension`, `getType`, `context.contentResolver.openInputStream`, `inputStream.buffered`, `destinationFile.outputStream`, `buffered`, `input.copyTo`, `sourceFile.inputStream`, `destinationFile.exists`, `destinationFile.length`, `destinationFile.delete`, `e.printStackTrace`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.9 `getDisplayName` — fun, líneas 231–250

```kotlin
    private fun getDisplayName(uri: Uri): String? {
        val context = getApplication<Application>()
        if (uri.scheme == "file") {
            return uri.path?.let {
                    File(it).name
                }
        }
        return try {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0 && cursor.moveToFirst()) {
                        cursor.getString(nameIndex)
                    } else {
                        null
                    }
                }
        } catch (e: Exception) {
            null
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `String?`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 232 | `val context` | `inferido` | `getApplication<Application>()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 240 | `val nameIndex` | `inferido` | `cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 233 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 234 | `return uri.path?.let {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 238 | `return try {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 241 | `if (nameIndex >= 0 && cursor.moveToFirst()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 2.

#### Efectos secundarios y recursos

- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `File`, `context.contentResolver.query`, `arrayOf`, `cursor.getColumnIndex`, `cursor.moveToFirst`, `cursor.getString`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.10 `getExtension` — fun, líneas 256–322

```kotlin
    private fun getExtension(originalName: String?, mimeType: String?, type: String): String {
        /*
         * Primero intentamos utilizar
         * la extensión original.
         */
        if (!originalName.isNullOrBlank() && originalName.contains(".")) {
            val extension = originalName.substringAfterLast(".").lowercase()
            if (extension.isNotBlank() && extension.length <= 10) {
                return extension
            }
        }
        /*
         * Si no encontramos extensión,
         * utilizamos MIME.
         */
        return when (mimeType) {
            /*
             * IMÁGENES
             */
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/gif" -> "gif"
            "image/heic" -> "heic"
            "image/heif" -> "heif"
            /*
             * VIDEO
             */
            "video/mp4" -> "mp4"
            "video/3gpp" -> "3gp"
            "video/webm" -> "webm"
            "video/quicktime" -> "mov"
            /*
             * AUDIO
             */
            "audio/mpeg" -> "mp3"
            "audio/mp4", "audio/x-m4a" -> "m4a"
            "audio/wav", "audio/x-wav" -> "wav"
            "audio/ogg" -> "ogg"
            "audio/flac" -> "flac"
            "audio/aac" -> "aac"
            /*
             * DOCUMENTOS
             */
            "application/pdf" -> "pdf"
            "text/plain" -> "txt"
            "application/zip" -> "zip"
            "application/json" -> "json"
            "application/msword" -> "doc"
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
            "application/vnd.ms-excel" -> "xls"
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"
            "application/vnd.ms-powerpoint" -> "ppt"
            "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx"
            /*
             * RESPALDO SEGÚN EL TIPO
             */
            else ->
                when (type) {
                    "image" -> "jpg"
                    "video" -> "mp4"
                    "audio" -> "mp3"
                    "voice" -> "m4a"
                    else -> "bin"
                }
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `originalName: String?` — `originalName` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia.
- `mimeType: String?` — `mimeType` recibe un valor de tipo `String?`. El contrato permite `null`; el bloque debe decidir explícita o implícitamente qué significa la ausencia. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `type: String` — `type` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 262 | `val extension` | `inferido` | `originalName.substringAfterLast(".").lowercase()` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 261 | `if (!originalName.isNullOrBlank() && originalName.contains(".")) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 263 | `if (extension.isNotBlank() && extension.length <= 10) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 264 | `return extension` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 271 | `return when (mimeType) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 275 | `"image/jpeg", "image/jpg" -> "jpg"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 276 | `"image/png" -> "png"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 277 | `"image/webp" -> "webp"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 278 | `"image/gif" -> "gif"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 279 | `"image/heic" -> "heic"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 280 | `"image/heif" -> "heif"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 284 | `"video/mp4" -> "mp4"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 285 | `"video/3gpp" -> "3gp"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 286 | `"video/webm" -> "webm"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 287 | `"video/quicktime" -> "mov"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 291 | `"audio/mpeg" -> "mp3"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 292 | `"audio/mp4", "audio/x-m4a" -> "m4a"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 293 | `"audio/wav", "audio/x-wav" -> "wav"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 294 | `"audio/ogg" -> "ogg"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 295 | `"audio/flac" -> "flac"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 296 | `"audio/aac" -> "aac"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 300 | `"application/pdf" -> "pdf"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 301 | `"text/plain" -> "txt"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 302 | `"application/zip" -> "zip"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 303 | `"application/json" -> "json"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 304 | `"application/msword" -> "doc"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 305 | `"application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 306 | `"application/vnd.ms-excel" -> "xls"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 307 | `"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 308 | `"application/vnd.ms-powerpoint" -> "ppt"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 309 | `"application/vnd.openxmlformats-officedocument.presentationml.presentation" -> "pptx"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 313 | `else ->` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 314 | `when (type) {` | Selección múltiple de Kotlin: concentra casos mutuamente evaluados y normalmente define una normalización o estrategia según el valor de entrada. |
| 315 | `"image" -> "jpg"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 316 | `"video" -> "mp4"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 317 | `"audio" -> "mp3"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 318 | `"voice" -> "m4a"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 319 | `else -> "bin"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `originalName.isNullOrBlank`, `originalName.contains`, `originalName.substringAfterLast`, `lowercase`, `extension.isNotBlank`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.11 `defaultAttachmentName` — fun, líneas 328–336

```kotlin
    private fun defaultAttachmentName(type: String, index: Int): String {
        return when (type) {
            "image" -> "Imagen ${index + 1}"
            "video" -> "Video ${index + 1}"
            "audio" -> "Audio ${index + 1}"
            "voice" -> "Nota de voz ${index + 1}"
            else -> "Archivo ${index + 1}"
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `type: String` — `type` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.
- `index: Int` — `index` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 329 | `return when (type) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 330 | `"image" -> "Imagen ${index + 1}"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 331 | `"video" -> "Video ${index + 1}"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 332 | `"audio" -> "Audio ${index + 1}"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 333 | `"voice" -> "Nota de voz ${index + 1}"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 334 | `else -> "Archivo ${index + 1}"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.12 `getAttachments` — fun, líneas 342–352

```kotlin
    fun getAttachments(noteId: Int) = attachmentDao.getAttachments(noteId)
    /*
     * =========================================================
     * EDITAR NOTA
     * =========================================================
     *
     * Los adjuntos existentes permanecen.
     *
     * Además dejamos preparado el método para que posteriormente
     * el editor pueda añadir nuevos archivos mientras editamos.
     */
```

#### Qué hace y por qué existe

Contrato DAO de Room: define operaciones de consulta/escritura que Room implementa para la base de datos.

#### Contrato de la declaración

**Parámetros:**

- `noteId: Int` — `noteId` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `attachmentDao.getAttachments`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.13 `updateNote` — fun, líneas 353–380

```kotlin
    fun updateNote(note: Note, title: String, content: String, color: String = note.color, newAttachments:
        List<PendingAttachment> = emptyList()) {
        viewModelScope.launch {
            val updatedNote = note.copy(title = title, content = content, color = color)
            if (newAttachments.isEmpty()) {
                noteDao.updateNote(updatedNote)
                return@launch
            }
            val currentAttachments = attachmentDao.getAttachmentsOnce(note.id)
            val prepared = prepareAttachments(noteId = note.id, attachments = newAttachments, startingIndex = currentAttachments.size)
            database.withTransaction {
                    noteDao.updateNote(updatedNote)
                    if (prepared.isNotEmpty()) {
                        attachmentDao.insertAttachments(prepared.map {
                                    it.attachment
                                })
                    }
                }
            if (prepared.isNotEmpty()) {
                prewarmAttachments(prepared)
            }
        }
    }
    /*
     * =========================================================
     * CAMBIAR COLOR
     * =========================================================
     */
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.
- `title: String` — `title` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `content: String` — `content` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `color: String = note.color` — `color` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `note.color`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.
- `newAttachments: List<PendingAttachment> = emptyList()` — `newAttachments` recibe un valor de tipo `List<PendingAttachment>`. El contrato no marca este parámetro como anulable. Tiene valor por defecto `emptyList()`, por lo que el llamador puede omitirlo y aceptar esa política predeterminada.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 356 | `val updatedNote` | `inferido` | `note.copy(title = title, content = content, color = color)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 361 | `val currentAttachments` | `inferido` | `attachmentDao.getAttachmentsOnce(note.id)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 362 | `val prepared` | `inferido` | `prepareAttachments(noteId = note.id, attachments = newAttachments, startingIndex = currentAttachment…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 357 | `if (newAttachments.isEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 359 | `return@launch` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 365 | `if (prepared.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 371 | `if (prepared.isNotEmpty()) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `emptyList`, `note.copy`, `newAttachments.isEmpty`, `noteDao.updateNote`, `attachmentDao.getAttachmentsOnce`, `prepareAttachments`, `prepared.isNotEmpty`, `attachmentDao.insertAttachments`, `prewarmAttachments`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.14 `changeNoteColor` — fun, líneas 381–385

```kotlin
    fun changeNoteColor(note: Note, color: String) {
        viewModelScope.launch {
            noteDao.updateColor(noteId = note.id, color = color)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.
- `color: String` — `color` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `noteDao.updateColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.15 `changePriority` — fun, líneas 391–395

```kotlin
    fun changePriority(note: Note, priority: Int) {
        viewModelScope.launch {
            noteDao.updatePriority(noteId = note.id, priority = priority)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.
- `priority: Int` — `priority` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `noteDao.updatePriority`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.16 `toggleFavorite` — fun, líneas 401–405

```kotlin
    fun toggleFavorite(note: Note) {
        viewModelScope.launch {
            noteDao.updateFavorite(noteId = note.id, isFavorite = !note.isFavorite)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `noteDao.updateFavorite`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.17 `togglePinned` — fun, líneas 411–415

```kotlin
    fun togglePinned(note: Note) {
        viewModelScope.launch {
            noteDao.updatePinned(noteId = note.id, isPinned = !note.isPinned)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `noteDao.updatePinned`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.18 `changeCategory` — fun, líneas 423–431

```kotlin
    fun changeCategory(note: Note, category: String) {
        val normalized = when (category) {
                "work" -> "work"
                else -> "personal"
            }
        viewModelScope.launch {
            noteDao.updateCategory(noteId = note.id, category = normalized)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.
- `category: String` — `category` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 424 | `val normalized` | `inferido` | `when (category) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 425 | `"work" -> "work"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 426 | `else -> "personal"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `noteDao.updateCategory`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.19 `deleteAttachment` — fun, líneas 444–456

```kotlin
    fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch {
            /*
             * Quitamos primero el registro de Room para que la UI responda
             * inmediatamente. La limpieza de caché/archivo ocurre después en IO.
             */
            attachmentDao.deleteAttachment(attachment)
            AttachmentPreviewCache.invalidate(context = getApplication<Application>(), uri = Uri.parse(attachment.uri))
            withContext(Dispatchers.IO) {
                deletePrivateFileIfPresent(Uri.parse(attachment.uri))
            }
        }
    }
```

#### Qué hace y por qué existe

Elimina o invalida el estado/recurso indicado, incluyendo las limpiezas auxiliares previstas por el bloque.

#### Contrato de la declaración

**Parámetros:**

- `attachment: Attachment` — `attachment` recibe un valor de tipo `Attachment`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `attachmentDao.deleteAttachment`, `AttachmentPreviewCache.invalidate`, `Uri.parse`, `withContext`, `deletePrivateFileIfPresent`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.20 `deleteNote` — fun, líneas 473–492

```kotlin
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            val attachments = attachmentDao.getAttachmentsOnce(note.id)
            /*
             * Primero actualizamos Room. La nota desaparece de Compose sin esperar
             * a que termine la E/S de todos sus adjuntos.
             */
            database.withTransaction {
                    attachmentDao.deleteAttachmentsForNote(note.id)
                    noteDao.deleteNote(note)
                }
            attachments.forEach { attachment -> AttachmentPreviewCache.invalidate(context = getApplication<Application>(),
                    uri = Uri.parse(attachment.uri))
            }
            withContext(Dispatchers.IO) {
                attachments.forEach { attachment -> deletePrivateFileIfPresent(Uri.parse(attachment.uri))
                }
            }
        }
    }
```

#### Qué hace y por qué existe

Elimina o invalida el estado/recurso indicado, incluyendo las limpiezas auxiliares previstas por el bloque.

#### Contrato de la declaración

**Parámetros:**

- `note: Note` — `note` recibe un valor de tipo `Note`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.


#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 475 | `val attachments` | `inferido` | `attachmentDao.getAttachmentsOnce(note.id)` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Trabajo IO:** El trabajo potencialmente bloqueante se desplaza al dispatcher de I/O en vez de ocupar el hilo principal. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Caché:** Lee, llena, invalida o administra caché para evitar recomputación o decodificación repetida.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `attachmentDao.getAttachmentsOnce`, `attachmentDao.deleteAttachmentsForNote`, `noteDao.deleteNote`, `AttachmentPreviewCache.invalidate`, `Uri.parse`, `withContext`, `deletePrivateFileIfPresent`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- No mover trabajo bloqueante al hilo principal; el uso de `Dispatchers.IO` forma parte de la protección de rendimiento.
- Mantener coherencia entre creación, clave, lectura e invalidación de caché; cambiar solo una de esas etapas puede dejar datos obsoletos.

### 4.21 `deletePrivateFileIfPresent` — fun, líneas 493–501

```kotlin
    private fun deletePrivateFileIfPresent(uri: Uri) {
        try {
            if (uri.scheme == "file") {
                uri.path?.let(::File)?.takeIf { it.exists() }?.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
```

#### Qué hace y por qué existe

Elimina o invalida el estado/recurso indicado, incluyendo las limpiezas auxiliares previstas por el bloque.

#### Contrato de la declaración

**Parámetros:**

- `uri: Uri` — `uri` recibe un valor de tipo `Uri`. El contrato no marca este parámetro como anulable. Es una referencia de contenido Android; puede apuntar a content providers y no solo a archivos locales.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 494 | `try {` | Frontera de manejo de errores: agrupa operaciones que pueden lanzar excepciones para permitir recuperación o limpieza controlada. |
| 495 | `if (uri.scheme == "file") {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |

**Restricciones concretas que aparecen en este bloque:**
- **Nulabilidad segura:** El operador de llamada segura evita desreferenciar receptores nulos; si el receptor es `null`, la cadena devuelve `null`. Apariciones en este bloque: 3.
- **Filtro condicional:** `takeIf` conserva el valor únicamente cuando el predicado se cumple; en caso contrario lo convierte en `null`. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Persistencia Room/DAO:** Realiza una operación de persistencia o modificación a través de una capa de datos/DAO.
- **Sistema de archivos/caché:** Accede o modifica almacenamiento/caché; son efectos externos al estado puramente en memoria.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `let`, `it.exists`, `delete`, `e.printStackTrace`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 27 | `database` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 28 | `noteDao` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Referencia un DAO de Room; sus operaciones representan acceso estructurado a la base de datos. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 29 | `attachmentDao` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Referencia un DAO de Room; sus operaciones representan acceso estructurado a la base de datos. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 30 | `settingsRepository` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Referencia una capa de repositorio, separando la coordinación de UI/estado del acceso y persistencia de datos. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 35 | `previewSemaphore` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 37 | `notes` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 46 | `allAttachments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 66 | `note` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 67 | `noteId` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 80 | `prepared` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 98 | `realIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 99 | `internalUri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 102 | `attachmentName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 152 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 153 | `attachmentsDirectory` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 157 | `sourceUri` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. |
| 163 | `sourcePath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 165 | `sourceFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 167 | `rootPath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 168 | `sourceCanonical` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 175 | `originalName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 176 | `extension` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 178 | `fileName` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 179 | `destinationFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 182 | `inputStream` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 192 | `sourcePath` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 193 | `sourceFile` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 206 | `inputStream` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 232 | `context` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El `Context` da acceso a recursos/servicios/almacenamiento Android; debe respetarse el ciclo de vida del contexto recibido. |
| 240 | `nameIndex` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 262 | `extension` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 356 | `updatedNote` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 361 | `currentAttachments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 362 | `prepared` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 424 | `normalized` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 475 | `attachments` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 26–502 | 0 | `class NoteViewModel(application: Application) : AndroidViewModel(application)` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 61–70 | 1 | `fun addNote(title: String, content: String, color: String = "default", attachments: List<PendingAttachment> = emptyList())` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 62–64 | 2 | `if (title.isBlank() && content.isBlank() && attachments.isEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 65–69 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 76–88 | 1 | `private suspend fun saveAttachments(noteId: Int, attachments: List<PendingAttachment>)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 77–79 | 2 | `if (attachments.isEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 81–83 | 2 | `if (prepared.isEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 84–86 | 2 | `attachmentDao.insertAttachments(prepared.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 93–111 | 1 | `): List<PreparedAttachment>` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 94–110 | 2 | `return withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 95–109 | 3 | `buildList` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 96–108 | 4 | `attachments.forEachIndexed` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 101–107 | 5 | `if (internalUri != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 113–124 | 1 | `List<PreparedAttachment>)` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 114–123 | 2 | `attachments.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 116–122 | 3 | `viewModelScope.launch(Dispatchers.IO)` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 117–121 | 4 | `previewSemaphore.withPermit` | Sección protegida por semáforo: limita cuántos bloques equivalentes pueden ejecutarse simultáneamente. |
| 137–145 | 1 | `private suspend fun copyAttachmentToInternalStorage(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 142–144 | 2 | `return withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 150–225 | 1 | `private fun copyAttachmentToInternalStorageBlocking(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 151–221 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 154–156 | 3 | `if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 162–174 | 3 | `if (sourceUri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 164–173 | 4 | `if (sourcePath != null)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 166–172 | 5 | `if (sourceFile.exists())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 169–171 | 6 | `if (sourceCanonical == rootPath \|\| sourceCanonical.startsWith(rootPath + File.separator))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 180–215 | 3 | `when (sourceUri.scheme)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 181–190 | 4 | `"content" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 183–189 | 5 | `inputStream.buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 185–188 | 6 | `destinationFile.outputStream().buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 191–204 | 4 | `"file" ->` | Lambda/callback: define una función anónima capturando el contexto léxico necesario. Su momento de ejecución lo decide la API receptora. |
| 194–196 | 5 | `if (!sourceFile.exists())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 197–203 | 5 | `sourceFile.inputStream().buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 199–202 | 6 | `destinationFile.outputStream().buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 205–214 | 4 | `else ->` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 207–213 | 5 | `inputStream.buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 209–212 | 6 | `destinationFile.outputStream().buffered(64 * 1024).use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 216–219 | 3 | `if (!destinationFile.exists() \|\| destinationFile.length() <= 0L)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 221–224 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 231–250 | 1 | `private fun getDisplayName(uri: Uri): String?` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 233–237 | 2 | `if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 234–236 | 3 | `return uri.path?.let` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 238–247 | 2 | `return try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 239–246 | 3 | `context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use` | Bloque `use`: el recurso cerrable se libera automáticamente al terminar el cuerpo, incluso si ocurre una excepción. |
| 241–243 | 4 | `if (nameIndex >= 0 && cursor.moveToFirst())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 243–245 | 4 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 247–249 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |
| 256–322 | 1 | `private fun getExtension(originalName: String?, mimeType: String?, type: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 261–266 | 2 | `if (!originalName.isNullOrBlank() && originalName.contains("."))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 263–265 | 3 | `if (extension.isNotBlank() && extension.length <= 10)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 271–321 | 2 | `return when (mimeType)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 314–320 | 3 | `when (type)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 328–336 | 1 | `private fun defaultAttachmentName(type: String, index: Int): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 329–335 | 2 | `return when (type)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 354–375 | 1 | `List<PendingAttachment> = emptyList())` | Ámbito delimitado por llaves en profundidad 1. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 355–374 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 357–360 | 3 | `if (newAttachments.isEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 363–370 | 3 | `database.withTransaction` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 365–369 | 4 | `if (prepared.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 366–368 | 5 | `attachmentDao.insertAttachments(prepared.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 371–373 | 3 | `if (prepared.isNotEmpty())` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 381–385 | 1 | `fun changeNoteColor(note: Note, color: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 382–384 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 391–395 | 1 | `fun changePriority(note: Note, priority: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 392–394 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 401–405 | 1 | `fun toggleFavorite(note: Note)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 402–404 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 411–415 | 1 | `fun togglePinned(note: Note)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 412–414 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 423–431 | 1 | `fun changeCategory(note: Note, category: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 424–427 | 2 | `val normalized = when (category)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 428–430 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 444–456 | 1 | `fun deleteAttachment(attachment: Attachment)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 445–455 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 452–454 | 3 | `withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 473–492 | 1 | `fun deleteNote(note: Note)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 474–491 | 2 | `viewModelScope.launch` | Nueva corrutina: el bloque se ejecuta asíncronamente dentro del `CoroutineScope` receptor y respeta su cancelación. |
| 480–483 | 3 | `database.withTransaction` | Ámbito delimitado por llaves en profundidad 3. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 484–486 | 3 | `attachments.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 487–490 | 3 | `withContext(Dispatchers.IO)` | Cambio de contexto de corrutina: el cuerpo se ejecuta bajo el dispatcher/contexto indicado y devuelve el resultado al contexto llamador. |
| 488–489 | 4 | `attachments.forEach` | Bloque iterativo: se ejecuta una vez por elemento de una colección/rango. El trabajo total depende del tamaño de la entrada. |
| 493–501 | 1 | `private fun deletePrivateFileIfPresent(uri: Uri)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 494–498 | 2 | `try` | Bloque protegido frente a excepciones: delimita operaciones que pueden fallar y que serán tratadas por `catch/finally` asociados. |
| 495–497 | 3 | `if (uri.scheme == "file")` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 496–496 | 4 | `uri.path?.let(::File)?.takeIf` | Lambda `let`: transforma/usa el receptor dentro de un ámbito corto; con llamada segura suele ejecutarse solo cuando el receptor no es nulo. |
| 498–500 | 2 | `} catch (e: Exception)` | Bloque de recuperación de excepción: se ejecuta únicamente para el tipo de error capturado. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
