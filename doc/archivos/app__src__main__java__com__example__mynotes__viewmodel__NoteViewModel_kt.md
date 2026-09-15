# NoteViewModel.kt — documentación exhaustiva actualizada

**Ruta de código real:** `app/src/main/java/com/example/mynotes/viewmodel/NoteViewModel.kt`  
**SHA-256 actual del archivo, sin modificar:** `f8e67135f1d6cd75c87a45c2344c3aebd38c40a219d41d7bd6b1f74122664a8f`  
**Líneas del código real:** 513  
**Estado respecto de la documentación anterior:** **archivo modificado desde la instantánea anterior**

> **Garantía:** este documento vive fuera de `app/`. No se insertó ni eliminó código en el fuente para crear esta explicación. Los fragmentos siguientes son copias de lectura.

## 1. Papel del archivo

ViewModel de notas y adjuntos. Encapsula operaciones Room, copia de adjuntos a almacenamiento interno, edición, borrado, categorías, favoritos y precalentamiento de previews.

**Cambios recientes cubiertos por esta revisión.** Los cambios recientes conectan el precalentamiento de adjuntos con SettingsRepository/performanceMode para preparar la variante de miniatura adecuada —incluida la estrategia de máxima calidad— sin bloquear la UI.

## 2. Package e imports

El package declarado es `com.example.mynotes.viewmodel`. El package fija el namespace de Kotlin y condiciona cómo se resuelven nombres, visibilidad, imports y referencias desde otros módulos.

El archivo contiene **22 imports**. Se agrupan por responsabilidad:

### Android / Jetpack / Compose

`android.app.Application`, `android.net.Uri`, `android.provider.OpenableColumns`, `androidx.lifecycle.AndroidViewModel`, `androidx.lifecycle.viewModelScope`, `androidx.room.withTransaction`

### Proyecto MyNotes

`com.example.mynotes.data.AppDatabase`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`, `com.example.mynotes.data.PendingAttachment`, `com.example.mynotes.performance.AttachmentPreviewCache`, `com.example.mynotes.settings.SettingsRepository`

### Kotlin Coroutines / extensiones

`kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.flow.SharingStarted`, `kotlinx.coroutines.flow.distinctUntilChanged`, `kotlinx.coroutines.flow.first`, `kotlinx.coroutines.flow.stateIn`, `kotlinx.coroutines.launch`, `kotlinx.coroutines.sync.Semaphore`, `kotlinx.coroutines.sync.withPermit`, `kotlinx.coroutines.withContext`

### Java / Kotlin estándar

`java.io.File`

## 3. Restricciones, límites e invariantes detectables

- **Llamada segura `?.`: 5 aparición/apariciones.** evita desreferenciar receptores nulos; si el receptor es `null`, la cadena se corta de forma segura.
- **Elvis `?:`: 7 aparición/apariciones.** define un fallback explícito cuando el operando izquierdo es nulo.
- **Corrutinas / dispatcher: 20 aparición/apariciones.** separan trabajo concurrente o pesado del hilo que compone/renderiza la interfaz.

Estas apariciones no implican por sí solas un error: son puntos donde el código expresa contratos que deben preservarse al modificarlo.

## 4. Declaraciones y funciones

### 4.1 `NoteViewModel` — class, líneas 26–513

```kotlin
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val noteDao = database.noteDao()
    private val attachmentDao = database.attachmentDao()
    private val settingsRepository = SettingsRepository(application)
    /*
     * Máximo dos previews multimedia pesados al mismo tiempo.
     * Evita picos de CPU/RAM al adjuntar muchos archivos.
     */
    private val previewSemaphore = Semaphore(permits = 2)
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
    /*
     * =========================================================
     * GUARDAR ADJUNTOS
     * =========================================================
     */
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
    /*
     * Toda la copia y lectura de metadatos ocurre en IO.
     */
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
    private fun prewarmAttachments(attachments:
        List<PreparedAttachment>) {
        attachments.forEach {
                prepared ->
            viewModelScope.launch(Dispatchers.IO) {
                previewSemaphore.withPermit {
                        val selectedPerformanceMode = settingsRepository.settings.first().performanceMode
                        /*
                         * En Maximum quality guardamos también la miniatura
                         * instantánea. Así una nota recién creada ya tiene un
                         * placeholder fotográfico real y persistente antes de
                         * volver al listado; no necesita mostrar un icono y
                         * reemplazarlo visualmente durante el primer scroll.
                         */
                        if (selectedPerformanceMode == "quality") {
                            AttachmentPreviewCache.prewarm(context = getApplication<Application>(), uri = prepared.internalUri,
                                type = prepared.type, name = prepared.name, performanceMode = "instant")
                        }
                        AttachmentPreviewCache.prewarm(context = getApplication<Application>(), uri = prepared.internalUri, type =
                                    prepared.type, name = prepared.name, performanceMode = selectedPerformanceMode)
                    }
            }
        }
    }
    /*
     * =========================================================
     * COPIAR CUALQUIER ARCHIVO AL ALMACENAMIENTO PRIVADO
     * =========================================================
     *
     * Destino:
     *
     * /data/data/com.example.mynotes/files/attachments/
     *
     * Después de copiarlo, la aplicación ya no necesita
     * cargar el archivo desde Galería, Descargas, Música, etc.
     */
    private suspend fun copyAttachmentToInternalStorage(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {
        /*
         * Cambiamos al dispatcher de I/O y delegamos en una
         * función normal que devuelve Uri? directamente.
         */
        return withContext(Dispatchers.IO) {
            copyAttachmentToInternalStorageBlocking(pendingAttachment = pendingAttachment, noteId = noteId, index = index)
        }
    }
    /*
     * Esta función se ejecuta dentro de Dispatchers.IO
     * y devuelve Uri? directamente.
     */
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
    /*
     * =========================================================
     * OBTENER NOMBRE ORIGINAL
     * =========================================================
     */
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
    /*
     * =========================================================
     * DETERMINAR EXTENSIÓN
     * =========================================================
     */
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
    /*
     * =========================================================
     * NOMBRE PREDETERMINADO
     * =========================================================
     */
    private fun defaultAttachmentName(type: String, index: Int): String {
        return when (type) {
            "image" -> "Imagen ${index + 1}"
            "video" -> "Video ${index + 1}"
            "audio" -> "Audio ${index + 1}"
            "voice" -> "Nota de voz ${index + 1}"
            else -> "Archivo ${index + 1}"
        }
    }
    /*
     * =========================================================
     * OBTENER ADJUNTOS DE UNA NOTA
     * =========================================================
     */
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
    fun changeNoteColor(note: Note, color: String) {
        viewModelScope.launch {
            noteDao.updateColor(noteId = note.id, color = color)
        }
    }
    /*
     * =========================================================
     * CAMBIAR PRIORIDAD
     * =========================================================
     */
    fun changePriority(note: Note, priority: Int) {
        viewModelScope.launch {
            noteDao.updatePriority(noteId = note.id, priority = priority)
        }
    }
    /*
     * =========================================================
     * FAVORITA
     * =========================================================
     */
    fun toggleFavorite(note: Note) {
        viewModelScope.launch {
            noteDao.updateFavorite(noteId = note.id, isFavorite = !note.isFavorite)
        }
    }
    /*
     * =========================================================
     * FIJAR / DESFIJAR
     * =========================================================
     */
    fun togglePinned(note: Note) {
        viewModelScope.launch {
            noteDao.updatePinned(noteId = note.id, isPinned = !note.isPinned)
        }
    }
    /*
     * =========================================================
     * MOVER ENTRE CATEGORÍAS
     * =========================================================
     *
     * El diseño utiliza Work y Personal.
     */
    fun changeCategory(note: Note, category: String) {
        val normalized = when (category) {
                "work" -> "work"
                else -> "personal"
            }
        viewModelScope.launch {
            noteDao.updateCategory(noteId = note.id, category = normalized)
        }
    }
    /*
     * =========================================================
     * ELIMINAR UN ADJUNTO
     * =========================================================
     *
     * Se usa desde la pantalla de edición cuando el usuario
     * marca un adjunto existente con la X y después pulsa Guardar.
     *
     * Borra:
     * 1. el archivo físico del almacenamiento interno;
     * 2. el registro Attachment de Room.
     */
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
    /*
     * =========================================================
     * ELIMINAR NOTA
     * =========================================================
     *
     * Ya no eliminamos únicamente imágenes.
     *
     * Eliminamos físicamente:
     *
     * imágenes
     * videos
     * canciones
     * notas de voz
     * documentos
     * cualquier otro archivo privado.
     */
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
    private fun deletePrivateFileIfPresent(uri: Uri) {
        try {
            if (uri.scheme == "file") {
                uri.path?.let(::File)?.takeIf { it.exists() }?.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```

**Firma/entrada.** `class NoteViewModel(application: Application) : AndroidViewModel(application) {`

**Parámetros.**
- `application: Application) : AndroidViewModel(application` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** desplaza I/O/decodificación fuera del hilo principal; accede o prepara almacenamiento local/caché; integra el pipeline de miniaturas y caché de adjuntos; ramifica o parametriza comportamiento según el perfil de rendimiento; lanza trabajo ligado a un scope con lifecycle definido.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo; la concurrencia está deliberadamente limitada para evitar saturación.

### 4.2 `PreparedAttachment` — class, líneas 36–36

```kotlin
    private data class PreparedAttachment(val attachment: Attachment, val internalUri: Uri, val type: String, val name: String?)
```

**Firma/entrada.** `private data class PreparedAttachment(val attachment: Attachment, val internalUri: Uri, val type: String, val name: String?) val notes = noteDao.getAllNotes().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarted .WhileSubscribed(5000), initialValue = emptyList()) /* * ========================================================= * TODOS LOS ADJUNTOS EN UN SOLO FLOW * ========================================================= * * NotesScreen observa esta lista una sola vez. */ val allAttachments = attachmentDao.getAllAttachments().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarted .WhileSubscribed(5000), initialValue = emptyList()) /* * ========================================================= * CREAR NOTA * ================================`

**Parámetros.**
- `val attachment: Attachment` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val internalUri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val type: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `val name: String?) val notes = noteDao.getAllNotes().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarted .WhileSubscribed(5000), initialValue = emptyList()) /* * ========================================================= * TODOS LOS ADJUNTOS EN UN SOLO FLOW * ========================================================= * * NotesScreen observa esta lista una sola vez. */ val allAttachments = attachmentDao.getAllAttachments().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarted .WhileSubscribed(5000), initialValue = emptyList()` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

### 4.3 `addNote` — fun, líneas 61–70

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
```

**Firma/entrada.** `fun addNote(title: String, content: String, color: String = "default", attachments: List<PendingAttachment> = emptyList()) {`

**Parámetros.**
- `title: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `content: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `color: String = "default"` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `attachments: List<PendingAttachment> = emptyList()` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** lanza trabajo ligado a un scope con lifecycle definido.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

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

**Firma/entrada.** `private suspend fun saveAttachments(noteId: Int, attachments: List<PendingAttachment>) {`

**Parámetros.**
- `noteId: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `attachments: List<PendingAttachment>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

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

**Firma/entrada.** `private suspend fun prepareAttachments(noteId: Int, attachments: List<PendingAttachment>, startingIndex: Int ): List<PreparedAttachment> {`

**Parámetros.**
- `noteId: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `attachments: List<PendingAttachment>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `startingIndex: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** desplaza I/O/decodificación fuera del hilo principal.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.6 `prewarmAttachments` — fun, líneas 112–135

```kotlin
    private fun prewarmAttachments(attachments:
        List<PreparedAttachment>) {
        attachments.forEach {
                prepared ->
            viewModelScope.launch(Dispatchers.IO) {
                previewSemaphore.withPermit {
                        val selectedPerformanceMode = settingsRepository.settings.first().performanceMode
                        /*
                         * En Maximum quality guardamos también la miniatura
                         * instantánea. Así una nota recién creada ya tiene un
                         * placeholder fotográfico real y persistente antes de
                         * volver al listado; no necesita mostrar un icono y
                         * reemplazarlo visualmente durante el primer scroll.
                         */
                        if (selectedPerformanceMode == "quality") {
                            AttachmentPreviewCache.prewarm(context = getApplication<Application>(), uri = prepared.internalUri,
                                type = prepared.type, name = prepared.name, performanceMode = "instant")
                        }
                        AttachmentPreviewCache.prewarm(context = getApplication<Application>(), uri = prepared.internalUri, type =
                                    prepared.type, name = prepared.name, performanceMode = selectedPerformanceMode)
                    }
            }
        }
    }
```

**Firma/entrada.** `private fun prewarmAttachments(attachments: List<PreparedAttachment>) {`

**Parámetros.**
- `attachments: List<PreparedAttachment>` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** desplaza I/O/decodificación fuera del hilo principal; integra el pipeline de miniaturas y caché de adjuntos; ramifica o parametriza comportamiento según el perfil de rendimiento; lanza trabajo ligado a un scope con lifecycle definido.

**Puntos que no conviene romper:** la concurrencia está deliberadamente limitada para evitar saturación.

### 4.7 `copyAttachmentToInternalStorage` — fun, líneas 148–156

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

**Firma/entrada.** `private suspend fun copyAttachmentToInternalStorage(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {`

**Parámetros.**
- `pendingAttachment: PendingAttachment` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `noteId: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `index: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** desplaza I/O/decodificación fuera del hilo principal.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.8 `copyAttachmentToInternalStorageBlocking` — fun, líneas 161–236

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

**Firma/entrada.** `private fun copyAttachmentToInternalStorageBlocking(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {`

**Parámetros.**
- `pendingAttachment: PendingAttachment` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `noteId: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `index: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.9 `getDisplayName` — fun, líneas 242–261

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

**Firma/entrada.** `private fun getDisplayName(uri: Uri): String? {`

**Parámetros.**
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** accede o prepara almacenamiento local/caché.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque; incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

### 4.10 `getExtension` — fun, líneas 267–333

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

**Firma/entrada.** `private fun getExtension(originalName: String?, mimeType: String?, type: String): String {`

**Parámetros.**
- `originalName: String?` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia.
- `mimeType: String?` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Su tipo admite `null`, por lo que el cuerpo debe contemplar ausencia.
- `type: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.11 `defaultAttachmentName` — fun, líneas 339–347

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

**Firma/entrada.** `private fun defaultAttachmentName(type: String, index: Int): String {`

**Parámetros.**
- `type: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `index: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.12 `getAttachments` — fun, líneas 353–386

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
```

**Firma/entrada.** `fun getAttachments(noteId: Int) = attachmentDao.getAttachments(noteId) /* * ========================================================= * EDITAR NOTA * ========================================================= * * Los adjuntos existentes permanecen. * * Además dejamos preparado el método para que posteriormente * el editor pueda añadir nuevos archivos mientras editamos. */ fun updateNote(note: Note, title: String, content: String, color: String = note.color, newAttachments: List<PendingAttachment> = emptyList()) {`

**Parámetros.**
- `noteId: Int) = attachmentDao.getAttachments(noteId) /* * ========================================================= * EDITAR NOTA * ========================================================= * * Los adjuntos existentes permanecen. * * Además dejamos preparado el método para que posteriormente * el editor pueda añadir nuevos archivos mientras editamos. */ fun updateNote(note: Note, title: String, content: String, color: String = note.color, newAttachments: List<PendingAttachment> = emptyList()` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** lanza trabajo ligado a un scope con lifecycle definido.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.13 `updateNote` — fun, líneas 364–386

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
```

**Firma/entrada.** `fun updateNote(note: Note, title: String, content: String, color: String = note.color, newAttachments: List<PendingAttachment> = emptyList()) {`

**Parámetros.**
- `note: Note` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `title: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `content: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `color: String = note.color` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.
- `newAttachments: List<PendingAttachment> = emptyList()` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente. Tiene valor por defecto y puede omitirse en la llamada.

**Funcionamiento observable.** lanza trabajo ligado a un scope con lifecycle definido.

**Puntos que no conviene romper:** contiene retornos explícitos; algunos caminos pueden terminar antes de ejecutar el resto del bloque.

### 4.14 `changeNoteColor` — fun, líneas 392–396

```kotlin
    fun changeNoteColor(note: Note, color: String) {
        viewModelScope.launch {
            noteDao.updateColor(noteId = note.id, color = color)
        }
    }
```

**Firma/entrada.** `fun changeNoteColor(note: Note, color: String) {`

**Parámetros.**
- `note: Note` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `color: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** lanza trabajo ligado a un scope con lifecycle definido.

### 4.15 `changePriority` — fun, líneas 402–406

```kotlin
    fun changePriority(note: Note, priority: Int) {
        viewModelScope.launch {
            noteDao.updatePriority(noteId = note.id, priority = priority)
        }
    }
```

**Firma/entrada.** `fun changePriority(note: Note, priority: Int) {`

**Parámetros.**
- `note: Note` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `priority: Int` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** lanza trabajo ligado a un scope con lifecycle definido.

### 4.16 `toggleFavorite` — fun, líneas 412–416

```kotlin
    fun toggleFavorite(note: Note) {
        viewModelScope.launch {
            noteDao.updateFavorite(noteId = note.id, isFavorite = !note.isFavorite)
        }
    }
```

**Firma/entrada.** `fun toggleFavorite(note: Note) {`

**Parámetros.**
- `note: Note` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** lanza trabajo ligado a un scope con lifecycle definido.

### 4.17 `togglePinned` — fun, líneas 422–426

```kotlin
    fun togglePinned(note: Note) {
        viewModelScope.launch {
            noteDao.updatePinned(noteId = note.id, isPinned = !note.isPinned)
        }
    }
```

**Firma/entrada.** `fun togglePinned(note: Note) {`

**Parámetros.**
- `note: Note` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** lanza trabajo ligado a un scope con lifecycle definido.

### 4.18 `changeCategory` — fun, líneas 434–442

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

**Firma/entrada.** `fun changeCategory(note: Note, category: String) {`

**Parámetros.**
- `note: Note` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.
- `category: String` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** lanza trabajo ligado a un scope con lifecycle definido.

### 4.19 `deleteAttachment` — fun, líneas 455–467

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

**Firma/entrada.** `fun deleteAttachment(attachment: Attachment) {`

**Parámetros.**
- `attachment: Attachment` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** desplaza I/O/decodificación fuera del hilo principal; integra el pipeline de miniaturas y caché de adjuntos; lanza trabajo ligado a un scope con lifecycle definido.

### 4.20 `deleteNote` — fun, líneas 484–503

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

**Firma/entrada.** `fun deleteNote(note: Note) {`

**Parámetros.**
- `note: Note` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** desplaza I/O/decodificación fuera del hilo principal; integra el pipeline de miniaturas y caché de adjuntos; lanza trabajo ligado a un scope con lifecycle definido.

### 4.21 `deletePrivateFileIfPresent` — fun, líneas 504–512

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

**Firma/entrada.** `private fun deletePrivateFileIfPresent(uri: Uri) {`

**Parámetros.**
- `uri: Uri` — El parámetro entra desde el llamador y no se debe reinterpretar silenciosamente.

**Funcionamiento observable.** encapsula una operación local cuyo contrato se deriva de sus parámetros, retorno y llamadas internas.

**Puntos que no conviene romper:** incluye manejo de excepciones; la ruta de error forma parte del contrato, no es un detalle decorativo.

## 5. Variables y propiedades, una por una

Se detectaron **37 declaraciones `val`/`var`** en la forma léxica principal. La tabla explica mutabilidad, tipo visible/inferido, inicialización y función práctica.

| Línea | Variable | Declaración | Explicación detallada |
|---:|---|---|---|
| 27 | `database` | `private val database: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val database = AppDatabase.getDatabase(application)` |
| 28 | `noteDao` | `private val noteDao: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val noteDao = database.noteDao()` |
| 29 | `attachmentDao` | `private val attachmentDao: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `private val attachmentDao = database.attachmentDao()` |
| 30 | `settingsRepository` | `private val settingsRepository: inferido` | `val` fija la referencia después de inicializarla; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `private val settingsRepository = SettingsRepository(application)` |
| 35 | `previewSemaphore` | `private val previewSemaphore: inferido` | `val` fija la referencia después de inicializarla; impone un máximo de trabajos concurrentes para proteger CPU/memoria/I/O; forma parte del camino de previsualización/caché; cambios aquí pueden afectar latencia, memoria y pop-in. **Inicialización visible:** `private val previewSemaphore = Semaphore(permits = 2)` |
| 37 | `notes` | `local/pública por contexto val notes: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val notes = noteDao.getAllNotes().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarted` |
| 46 | `allAttachments` | `local/pública por contexto val allAttachments: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val allAttachments = attachmentDao.getAllAttachments().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarted` |
| 66 | `note` | `local/pública por contexto val note: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val note = Note(title = title, content = content, color = color)` |
| 67 | `noteId` | `local/pública por contexto val noteId: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val noteId = noteDao.insertNote(note).toInt()` |
| 80 | `prepared` | `local/pública por contexto val prepared: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val prepared = prepareAttachments(noteId = noteId, attachments = attachments, startingIndex = 0)` |
| 98 | `realIndex` | `local/pública por contexto val realIndex: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val realIndex = startingIndex + index` |
| 99 | `internalUri` | `local/pública por contexto val internalUri: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val internalUri = copyAttachmentToInternalStorage(pendingAttachment = pendingAttachment, noteId = noteId, index =` |
| 102 | `attachmentName` | `local/pública por contexto val attachmentName: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val attachmentName = pendingAttachment.name?: getDisplayName(pendingAttachment.uri)?: defaultAttachmentName(` |
| 118 | `selectedPerformanceMode` | `local/pública por contexto val selectedPerformanceMode: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI; está ligado a configuración y por ello no debe desacoplarse del valor persistido que representa. **Inicialización visible:** `val selectedPerformanceMode = settingsRepository.settings.first().performanceMode` |
| 163 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = getApplication<Application>()` |
| 164 | `attachmentsDirectory` | `local/pública por contexto val attachmentsDirectory: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val attachmentsDirectory = File(context.filesDir, "attachments")` |
| 168 | `sourceUri` | `local/pública por contexto val sourceUri: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val sourceUri = pendingAttachment.uri` |
| 174 | `sourcePath` | `local/pública por contexto val sourcePath: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val sourcePath = sourceUri.path` |
| 176 | `sourceFile` | `local/pública por contexto val sourceFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val sourceFile = File(sourcePath)` |
| 178 | `rootPath` | `local/pública por contexto val rootPath: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val rootPath = attachmentsDirectory.canonicalFile.path` |
| 179 | `sourceCanonical` | `local/pública por contexto val sourceCanonical: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val sourceCanonical = sourceFile.canonicalFile.path` |
| 186 | `originalName` | `local/pública por contexto val originalName: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val originalName = pendingAttachment.name?: getDisplayName(sourceUri)` |
| 187 | `extension` | `local/pública por contexto val extension: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val extension = getExtension(originalName = originalName, mimeType = pendingAttachment.mimeType?: context.contentResolver` |
| 189 | `fileName` | `local/pública por contexto val fileName: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val fileName = "note_${noteId}_${System.currentTimeMillis()}_${index}.$extension"` |
| 190 | `destinationFile` | `local/pública por contexto val destinationFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val destinationFile = File(attachmentsDirectory, fileName)` |
| 193 | `inputStream` | `local/pública por contexto val inputStream: inferido` | `val` fija la referencia después de inicializarla; admite ausencia (`null`) y por tanto sus consumidores deben manejar la nulabilidad. **Inicialización visible:** `val inputStream = context.contentResolver.openInputStream(sourceUri)?: return null` |
| 203 | `sourcePath` | `local/pública por contexto val sourcePath: inferido` | `val` fija la referencia después de inicializarla; admite ausencia (`null`) y por tanto sus consumidores deben manejar la nulabilidad. **Inicialización visible:** `val sourcePath = sourceUri.path?: return null` |
| 204 | `sourceFile` | `local/pública por contexto val sourceFile: inferido` | `val` fija la referencia después de inicializarla; representa una ruta/archivo y puede implicar I/O en usos posteriores. **Inicialización visible:** `val sourceFile = File(sourcePath)` |
| 217 | `inputStream` | `local/pública por contexto val inputStream: inferido` | `val` fija la referencia después de inicializarla; admite ausencia (`null`) y por tanto sus consumidores deben manejar la nulabilidad. **Inicialización visible:** `val inputStream = context.contentResolver.openInputStream(sourceUri)?: return null` |
| 243 | `context` | `local/pública por contexto val context: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val context = getApplication<Application>()` |
| 251 | `nameIndex` | `local/pública por contexto val nameIndex: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)` |
| 273 | `extension` | `local/pública por contexto val extension: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val extension = originalName.substringAfterLast(".").lowercase()` |
| 367 | `updatedNote` | `local/pública por contexto val updatedNote: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val updatedNote = note.copy(title = title, content = content, color = color)` |
| 372 | `currentAttachments` | `local/pública por contexto val currentAttachments: inferido` | `val` fija la referencia después de inicializarla; su nombre indica estado de interacción/selección/visibilidad y debe mantenerse coherente con la UI. **Inicialización visible:** `val currentAttachments = attachmentDao.getAttachmentsOnce(note.id)` |
| 373 | `prepared` | `local/pública por contexto val prepared: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val prepared = prepareAttachments(noteId = note.id, attachments = newAttachments, startingIndex = currentAttachments.size)` |
| 435 | `normalized` | `local/pública por contexto val normalized: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val normalized = when (category) {` |
| 486 | `attachments` | `local/pública por contexto val attachments: inferido` | `val` fija la referencia después de inicializarla. **Inicialización visible:** `val attachments = attachmentDao.getAttachmentsOnce(note.id)` |

## 6. Mapa de ámbitos y bloques `{ ... }`

Se documentan **74 bloques estructurales** relevantes. La profundidad indica cuántos ámbitos externos contienen al bloque.

| Inicio–fin | Prof. | Tipo de bloque | Cabecera | Qué implica |
|---|---:|---|---|---|
| 26–513 | 0 | ámbito/lambda anónima | `class NoteViewModel(application: Application) : AndroidViewModel(application) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 61–70 | 1 | ámbito/lambda anónima | `fun addNote(title: String, content: String, color: String = "default", attachments: List<PendingAttachment> = emptyList()) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 62–64 | 2 | condición `if` | `if (title.isBlank() && content.isBlank() && attachments.isEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 65–69 | 2 | corrutina `launch` | `viewModelScope.launch {` | Crea trabajo concurrente estructurado. Su lifecycle depende del scope que lo contiene y no debe retener referencias más allá de ese scope. |
| 76–88 | 1 | ámbito/lambda anónima | `private suspend fun saveAttachments(noteId: Int, attachments: List<PendingAttachment>) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 77–79 | 2 | condición `if` | `if (attachments.isEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 81–83 | 2 | condición `if` | `if (prepared.isEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 84–86 | 2 | ámbito/lambda anónima | `attachmentDao.insertAttachments(prepared.map {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 93–111 | 1 | ámbito/lambda anónima | `): List<PreparedAttachment> {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 94–110 | 2 | `withContext` / cambio de dispatcher | `return withContext(Dispatchers.IO) {` | Mueve temporalmente la ejecución a otro dispatcher; normalmente evita hacer I/O/decodificación en el hilo principal. |
| 95–109 | 3 | ámbito/lambda anónima | `buildList {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 96–108 | 4 | iteración funcional | `attachments.forEachIndexed {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 101–107 | 5 | condición `if` | `if (internalUri != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 113–135 | 1 | ámbito/lambda anónima | `List<PreparedAttachment>) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 114–134 | 2 | iteración funcional | `attachments.forEach {` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 116–133 | 3 | ámbito/lambda anónima | `viewModelScope.launch(Dispatchers.IO) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 126–129 | 5 | condición `if` | `if (selectedPerformanceMode == "quality") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 148–156 | 1 | ámbito/lambda anónima | `private suspend fun copyAttachmentToInternalStorage(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 153–155 | 2 | `withContext` / cambio de dispatcher | `return withContext(Dispatchers.IO) {` | Mueve temporalmente la ejecución a otro dispatcher; normalmente evita hacer I/O/decodificación en el hilo principal. |
| 161–236 | 1 | ámbito/lambda anónima | `private fun copyAttachmentToInternalStorageBlocking(pendingAttachment: PendingAttachment, noteId: Int, index: Int): Uri? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 162–232 | 2 | `try` / manejo de error | `return try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 165–167 | 3 | condición `if` | `if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 173–185 | 3 | condición `if` | `if (sourceUri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 175–184 | 4 | condición `if` | `if (sourcePath != null) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 177–183 | 5 | condición `if` | `if (sourceFile.exists()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 180–182 | 6 | condición `if` | `if (sourceCanonical == rootPath \|\| sourceCanonical.startsWith(rootPath + File.separator)) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 191–226 | 3 | selección `when` | `when (sourceUri.scheme) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 205–207 | 5 | condición `if` | `if (!sourceFile.exists()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 227–230 | 3 | condición `if` | `if (!destinationFile.exists() \|\| destinationFile.length() <= 0L) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 232–235 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 242–261 | 1 | ámbito/lambda anónima | `private fun getDisplayName(uri: Uri): String? {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 244–248 | 2 | condición `if` | `if (uri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 245–247 | 3 | ámbito/lambda anónima | `return uri.path?.let {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 249–258 | 2 | `try` / manejo de error | `return try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 250–257 | 3 | ámbito/lambda anónima | `context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 252–254 | 4 | condición `if` | `if (nameIndex >= 0 && cursor.moveToFirst()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 258–260 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |
| 267–333 | 1 | ámbito/lambda anónima | `private fun getExtension(originalName: String?, mimeType: String?, type: String): String {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 272–277 | 2 | condición `if` | `if (!originalName.isNullOrBlank() && originalName.contains(".")) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 274–276 | 3 | condición `if` | `if (extension.isNotBlank() && extension.length <= 10) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 282–332 | 2 | selección `when` | `return when (mimeType) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 325–331 | 3 | selección `when` | `when (type) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 339–347 | 1 | ámbito/lambda anónima | `private fun defaultAttachmentName(type: String, index: Int): String {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 340–346 | 2 | selección `when` | `return when (type) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 365–386 | 1 | ámbito/lambda anónima | `List<PendingAttachment> = emptyList()) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 366–385 | 2 | corrutina `launch` | `viewModelScope.launch {` | Crea trabajo concurrente estructurado. Su lifecycle depende del scope que lo contiene y no debe retener referencias más allá de ese scope. |
| 368–371 | 3 | condición `if` | `if (newAttachments.isEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 374–381 | 3 | ámbito/lambda anónima | `database.withTransaction {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 376–380 | 4 | condición `if` | `if (prepared.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 382–384 | 3 | condición `if` | `if (prepared.isNotEmpty()) {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 392–396 | 1 | ámbito/lambda anónima | `fun changeNoteColor(note: Note, color: String) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 393–395 | 2 | corrutina `launch` | `viewModelScope.launch {` | Crea trabajo concurrente estructurado. Su lifecycle depende del scope que lo contiene y no debe retener referencias más allá de ese scope. |
| 402–406 | 1 | ámbito/lambda anónima | `fun changePriority(note: Note, priority: Int) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 403–405 | 2 | corrutina `launch` | `viewModelScope.launch {` | Crea trabajo concurrente estructurado. Su lifecycle depende del scope que lo contiene y no debe retener referencias más allá de ese scope. |
| 412–416 | 1 | ámbito/lambda anónima | `fun toggleFavorite(note: Note) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 413–415 | 2 | corrutina `launch` | `viewModelScope.launch {` | Crea trabajo concurrente estructurado. Su lifecycle depende del scope que lo contiene y no debe retener referencias más allá de ese scope. |
| 422–426 | 1 | ámbito/lambda anónima | `fun togglePinned(note: Note) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 423–425 | 2 | corrutina `launch` | `viewModelScope.launch {` | Crea trabajo concurrente estructurado. Su lifecycle depende del scope que lo contiene y no debe retener referencias más allá de ese scope. |
| 434–442 | 1 | ámbito/lambda anónima | `fun changeCategory(note: Note, category: String) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 435–438 | 2 | selección `when` | `val normalized = when (category) {` | Despacha comportamiento según un valor/condición. Las ramas representan contratos mutuamente seleccionados y el `else`, si existe, actúa como fallback. |
| 439–441 | 2 | corrutina `launch` | `viewModelScope.launch {` | Crea trabajo concurrente estructurado. Su lifecycle depende del scope que lo contiene y no debe retener referencias más allá de ese scope. |
| 455–467 | 1 | ámbito/lambda anónima | `fun deleteAttachment(attachment: Attachment) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 456–466 | 2 | corrutina `launch` | `viewModelScope.launch {` | Crea trabajo concurrente estructurado. Su lifecycle depende del scope que lo contiene y no debe retener referencias más allá de ese scope. |
| 463–465 | 3 | `withContext` / cambio de dispatcher | `withContext(Dispatchers.IO) {` | Mueve temporalmente la ejecución a otro dispatcher; normalmente evita hacer I/O/decodificación en el hilo principal. |
| 484–503 | 1 | ámbito/lambda anónima | `fun deleteNote(note: Note) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 485–502 | 2 | corrutina `launch` | `viewModelScope.launch {` | Crea trabajo concurrente estructurado. Su lifecycle depende del scope que lo contiene y no debe retener referencias más allá de ese scope. |
| 491–494 | 3 | ámbito/lambda anónima | `database.withTransaction {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 495–497 | 3 | iteración funcional | `attachments.forEach { attachment -> AttachmentPreviewCache.invalidate(context = getApplication<Application>(),` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 498–501 | 3 | `withContext` / cambio de dispatcher | `withContext(Dispatchers.IO) {` | Mueve temporalmente la ejecución a otro dispatcher; normalmente evita hacer I/O/decodificación en el hilo principal. |
| 499–500 | 4 | iteración funcional | `attachments.forEach { attachment -> deletePrivateFileIfPresent(Uri.parse(attachment.uri))` | Repite el cuerpo para elementos/condiciones; el coste crece con la cantidad de entradas y las operaciones internas deben evitar trabajo pesado innecesario en UI. |
| 504–512 | 1 | ámbito/lambda anónima | `private fun deletePrivateFileIfPresent(uri: Uri) {` | Introduce un ámbito/lambda; las variables locales quedan limitadas por este scope y las capturas externas pueden afectar lifecycle/recomposición. |
| 505–509 | 2 | `try` / manejo de error | `try {` | Protege una operación susceptible de excepción. El camino de éxito y el de fallo deben dejar recursos/estado en condiciones coherentes. |
| 506–508 | 3 | condición `if` | `if (uri.scheme == "file") {` | Evalúa una condición y ejecuta este ámbito solo cuando se cumple; cualquier estado modificado dentro debe considerarse al razonar sobre caminos alternativos. |
| 509–511 | 2 | `catch` / recuperación de error | `} catch (e: Exception) {` | Captura una excepción del bloque protegido y define recuperación/limpieza en vez de propagarla sin control. |

## 7. Side effects, rendimiento y lifecycle

- **Persistencia / base de datos:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Sistema de archivos / caché:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Corrutinas:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.
- **Decodificación multimedia:** presente. Cualquier refactor futuro debe considerar el coste y el lifecycle del recurso, no solo el resultado visual.

## 8. Relación con los cambios recientes

Después de copiar adjuntos al almacenamiento interno, el precalentamiento usa el perfil actual de SettingsRepository. Esto conecta persistencia y performance: la nota guarda el archivo original, mientras el cache genera derivados desechables que pueden reconstruirse.

## 9. Regla de mantenimiento

Cualquier modificación futura debería actualizar primero el archivo Kotlin real y después regenerar esta documentación. **No debe editarse el código para que coincida con el documento; el documento es el derivado y el código es la fuente de verdad.**
