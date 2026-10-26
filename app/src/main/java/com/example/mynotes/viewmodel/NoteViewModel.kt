package com.example.mynotes.viewmodel

import com.example.mynotes.ui.state.NoteActions
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.mynotes.data.AppDatabase
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.data.PendingAttachment
import com.example.mynotes.util.openUriStream
import com.example.mynotes.util.uriDisplayName
import com.example.mynotes.performance.AttachmentPreviewCache
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.widget.MyNotesWidgetUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File

class NoteViewModel(application: Application) : AndroidViewModel(application), NoteActions {
    private val database = AppDatabase.getDatabase(application)
    private val noteDao = database.noteDao()
    private val attachmentDao = database.attachmentDao()
    private val settingsRepository = SettingsRepository(application)
    /*
     * Máximo dos previews multimedia pesados al mismo tiempo.
     * Evita picos de CPU/RAM al adjuntar muchos archivos.
     */
    private val previewSemaphore = Semaphore(permits = 2)
    private fun launchWithWidgetUpdate(block: suspend () -> Int) {
        viewModelScope.launch {
            if (block() > 0) MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
        }
    }
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
    override val allAttachments = attachmentDao.getAllAttachments().distinctUntilChanged().stateIn(scope = viewModelScope, started = SharingStarted
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
            MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
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
                            val attachmentName = pendingAttachment.name?: getApplication<Application>().uriDisplayName(pendingAttachment.uri)?: defaultAttachmentName(
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
            val originalName = pendingAttachment.name?: context.uriDisplayName(sourceUri)
            val extension = getExtension(originalName = originalName, mimeType = pendingAttachment.mimeType?: context.contentResolver
                                .getType(sourceUri), type = pendingAttachment.type)
            val fileName = "note_${noteId}_${System.currentTimeMillis()}_${index}.$extension"
            val destinationFile = File(attachmentsDirectory, fileName)
            context.openUriStream(sourceUri)?.buffered(64 * 1024)?.use { input ->
                destinationFile.outputStream().buffered(64 * 1024).use { output ->
                    input.copyTo(out = output, bufferSize = 64 * 1024)
                }
            } ?: return null
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
    override fun getAttachments(noteId: Int) = attachmentDao.getAttachments(noteId)
    /*
     * =========================================================
     * EDITAR NOTA
     * =========================================================
     *
     * Guarda los campos editados y los cambios de adjuntos en una transacción.
     * Sólo notifica a los widgets cuando hubo cambios reales.
     */
    fun updateNote(
        note: Note,
        title: String,
        content: String,
        color: String = note.color,
        newAttachments: List<PendingAttachment> = emptyList(),
        removedAttachments: List<Attachment> = emptyList()
    ) {
        viewModelScope.launch {
            val prepared = if (newAttachments.isEmpty()) emptyList() else prepareAttachments(
                noteId = note.id,
                attachments = newAttachments,
                startingIndex = attachmentDao.countAttachmentsForNote(note.id)
            )
            val changed = database.withTransaction {
                val updated = noteDao.updateEditedFields(note.id, title, content, color)
                val removed = if (removedAttachments.isEmpty()) 0 else
                    attachmentDao.deleteAttachments(removedAttachments)
                if (prepared.isNotEmpty()) {
                    attachmentDao.insertAttachments(prepared.map { it.attachment })
                }
                updated > 0 || removed > 0 || prepared.isNotEmpty()
            }
            if (changed) MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
            if (prepared.isNotEmpty()) {
                prewarmAttachments(prepared)
            }
            cleanUpAttachmentFiles(removedAttachments)
        }
    }
    /*
     * =========================================================
     * CAMBIAR COLOR
     * =========================================================
     */
    override fun changeNoteColor(note: Note, color: String) {
        launchWithWidgetUpdate { noteDao.updateColor(noteId = note.id, color = color) }
    }
    /*
     * =========================================================
     * CAMBIAR PRIORIDAD
     * =========================================================
     */
    override fun changePriority(note: Note, priority: Int) {
        launchWithWidgetUpdate { noteDao.updatePriority(noteId = note.id, priority = priority) }
    }
    /*
     * =========================================================
     * FAVORITA
     * =========================================================
     */
    override fun toggleFavorite(note: Note) {
        launchWithWidgetUpdate { noteDao.updateFavorite(noteId = note.id, isFavorite = !note.isFavorite) }
    }
    /*
     * =========================================================
     * FIJAR / DESFIJAR
     * =========================================================
     */
    override fun togglePinned(note: Note) {
        launchWithWidgetUpdate { noteDao.updatePinned(noteId = note.id, isPinned = !note.isPinned) }
    }
    /*
     * =========================================================
     * MOVER ENTRE CATEGORÍAS
     * =========================================================
     *
     * El diseño utiliza Work y Personal.
     */
    override fun changeCategory(note: Note, category: String) {
        val normalized = when (category) {
                "work" -> "work"
                else -> "personal"
            }
        launchWithWidgetUpdate { noteDao.updateCategory(noteId = note.id, category = normalized) }
    }
    // Room se actualiza antes de limpiar archivos; toda la limpieza ocurre en IO.
    private suspend fun cleanUpAttachmentFiles(attachments: List<Attachment>) {
        if (attachments.isEmpty()) return
        withContext(Dispatchers.IO) {
            attachments.forEach { attachment ->
                val uri = Uri.parse(attachment.uri)
                AttachmentPreviewCache.invalidate(context = getApplication<Application>(), uri = uri)
                deletePrivateFileIfPresent(uri)
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
    override fun deleteNote(note: Note) {
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
            MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
            cleanUpAttachmentFiles(attachments)
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
