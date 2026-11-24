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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import com.example.mynotes.data.NoteFolder
import com.example.mynotes.data.CollectionPolicy
import com.example.mynotes.data.NoteDraft
import com.example.mynotes.ui.state.NoteUndo
import com.example.mynotes.ui.state.NoteOperation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.withLock

class NoteViewModel(application: Application) : AndroidViewModel(application), NoteActions {
    private val failures=kotlinx.coroutines.CoroutineExceptionHandler{_,error->com.example.mynotes.studio.StudioJobs.failure("Notes",error)}
    private val studio=com.example.mynotes.studio.StudioStore.get(application)
    private val database = AppDatabase.getDatabase(application)
    private val noteDao = database.noteDao()
    private val attachmentDao = database.attachmentDao()
    private val folderDao = database.folderDao()
    private val settingsRepository = SettingsRepository(application)
    private val previewSemaphore = Semaphore(permits = 2)
    companion object {
        internal fun clearLibraryUndo(){sessionUndo.value=null;sessionToken.incrementAndGet()}
        val sessionMutex = com.example.mynotes.studio.StudioMutations.lock
        val sessionUndo = MutableStateFlow<NoteUndo?>(null)
        val sessionToken = AtomicLong(0L)
        val cleanupStarted = AtomicBoolean(false)
    }
    private val mutationMutex = sessionMutex
    private val undoState = sessionUndo
    override val pendingUndo = undoState.asStateFlow()
    override val folders = combine(folderDao.folders(),com.example.mynotes.studio.PlusProfiles.active){items,library->items.filter{it.libraryId==library}}.distinctUntilChanged().flowOn(Dispatchers.Default).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val notes = combine(noteDao.getAllNotes(),com.example.mynotes.studio.PlusProfiles.active){items,library->items.filter{it.libraryId==library}}.distinctUntilChanged().flowOn(Dispatchers.Default).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val trash=combine(noteDao.observeTrash(),com.example.mynotes.studio.PlusProfiles.active){items,library->items.filter{it.libraryId==library}}.distinctUntilChanged().flowOn(Dispatchers.Default).stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
    override val noteCatalog get() = notes
    override val allAttachments = combine(attachmentDao.getAllAttachments(),noteDao.getAllNotes(),com.example.mynotes.studio.PlusProfiles.active) { items,notes,library -> val ids=notes.filter{it.libraryId==library}.map{it.id}.toSet();items.filter { it.type != "cover" && it.noteId in ids } }
        .distinctUntilChanged().flowOn(Dispatchers.Default).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private data class PreparedAttachment(val attachment: Attachment, val internalUri: Uri, val type: String, val name: String?,val newPrivateCopy:Boolean)

    init {
        // An undo belongs to one application session. Previous-session deletions can now be finalized.
        if (cleanupStarted.compareAndSet(false, true)) viewModelScope.launch(failures) {
            mutationMutex.withLock {
                studio.load()
                val now=System.currentTimeMillis()
                val deleted=noteDao.deletedNotes()
                deleted.forEach{note->
                    if(!studio.state.value.copy(library=note.libraryId).get("trash",note.id.toString()).has("at"))
                        studio.editInLibrary("trash",note.id.toString(),note.libraryId){it.put("at",now)}
                }
                purgeDeleted(deleted.filter{note->
                    val libraryState=studio.state.value.copy(library=note.libraryId)
                    val days=libraryState.get("preferences","workspace").optInt("trashDays",30).coerceIn(1,365)
                    now-libraryState.get("trash",note.id.toString()).optLong("at",now)>days*86400000L
                })
            }
        }
    }
    private fun refreshWidgets() = MyNotesWidgetUpdater.requestUpdate(getApplication<Application>())
    private fun launchWithWidgetUpdate(noteId:Int,block: suspend () -> Int) {
        viewModelScope.launch(failures) { mutationMutex.withLock { studio.load();if(noteDao.getNoteByIdOnce(noteId)?.libraryId!=com.example.mynotes.studio.PlusProfiles.currentId)return@withLock;if(studio.state.value.get("notes",noteId.toString()).optString("remoteRole")=="viewer")return@withLock
            if(block()>0){studio.changed(noteId,"updated");refreshWidgets()}
        } }
    }
    override fun getAttachments(noteId: Int) = combine(attachmentDao.getAttachments(noteId),notes){items,visible->if(visible.any{it.id==noteId})items else emptyList()}

    fun saveDraft(note: Note?, draft: NoteDraft, onSaved: () -> Unit = {}) {
        if (note == null) addNote(draft.title, draft.content, draft.color, draft.attachments,
            draft.folderId, draft.coverIcon, draft.coverX, draft.coverY, draft.formattingEnabled, onSaved)
        else updateNote(note, draft.title, draft.content, draft.color, draft.attachments, draft.removedAttachments,
            draft.folderId, draft.coverIcon, draft.coverX, draft.coverY, draft.removeCover, draft.formattingEnabled, onSaved)
    }

    fun addNote(title: String, content: String, color: String = "default", attachments: List<PendingAttachment> = emptyList(),
        folderId: String? = null, coverIcon: String? = null, coverX: Float = 0.5f, coverY: Float = 0.5f, formattingEnabled: Boolean = false, onSaved: () -> Unit = {}) {
        if (title.isBlank() && content.isBlank() && attachments.isEmpty() && coverIcon == null) return
        val library=com.example.mynotes.studio.PlusProfiles.currentId
        viewModelScope.launch(failures) {
            mutationMutex.withLock {
                if(library!=com.example.mynotes.studio.PlusProfiles.currentId)return@withLock
                val validFolder = folderId?.takeIf { folderDao.byId(it)?.libraryId == library }
                val prepared = prepareAttachments(0, attachments, 0)
                requireCompleteAttachments(prepared,attachments.size)
                val id = try {
                    database.withTransaction {
                        val created=noteDao.insertNote(Note(title = title, content = content, color = color, folderId = validFolder,
                            coverIcon = CollectionPolicy.icon(coverIcon), coverX = CollectionPolicy.crop(coverX), coverY = CollectionPolicy.crop(coverY), formattingEnabled = formattingEnabled,libraryId=library)).toInt()
                        attachmentDao.insertAttachments(prepared.map { it.attachment.copy(noteId=created) })
                        prepared.firstOrNull { it.type == "cover" }?.let { noteDao.updateCover(created, it.internalUri.toString()) }
                        created
                    }
                }catch(error:Exception){discardStagedAttachments(prepared);throw error}
                runCatching {
                    studio.changed(id,"created")
                }.onFailure{com.example.mynotes.studio.StudioJobs.failure("Workspace metadata",it)}
                prewarmAttachments(prepared.filter { it.type != "cover" })
                refreshWidgets()
                runCatching { com.example.mynotes.data.workspace.WorkspaceAssets.release(getApplication(), attachments.map { it.uri }) }
                onSaved()
            }
        }
    }

    fun updateNote(note: Note, title: String, content: String, color: String = note.color,
        newAttachments: List<PendingAttachment> = emptyList(), removedAttachments: List<Attachment> = emptyList(),
        folderId: String? = note.folderId, coverIcon: String? = note.coverIcon,
        coverX: Float = note.coverX, coverY: Float = note.coverY, removeCover: Boolean = false, formattingEnabled: Boolean = note.formattingEnabled, onSaved: () -> Unit = {}) {
        viewModelScope.launch(failures) {
            mutationMutex.withLock {
                studio.load();if(studio.state.value.get("notes",note.id.toString()).optString("remoteRole")=="viewer"){com.example.mynotes.studio.StudioJobs.failure("Read-only notebook",IllegalStateException("Make a local copy to edit this note"));return@withLock}
                val current = noteDao.getNoteByIdOnce(note.id)?.takeIf{it.libraryId==com.example.mynotes.studio.PlusProfiles.currentId} ?: return@withLock
                com.example.mynotes.studio.StudioHistory.capture(getApplication(),current,attachmentDao.getAttachmentsOnce(note.id))
                val prepared = prepareAttachments(note.id, newAttachments, attachmentDao.countAttachmentsForNote(note.id))
                requireCompleteAttachments(prepared,newAttachments.size)
                val newCover = prepared.firstOrNull { it.type == "cover" }
                val coverRequested = newAttachments.any { it.type == "cover" }
                val iconEdited = coverIcon != note.coverIcon
                val effectiveIcon = if (coverRequested && newCover == null) current.coverIcon
                    else if (iconEdited || removeCover || newCover != null) coverIcon else current.coverIcon
                val replaceCover = newCover != null || removeCover || (iconEdited && effectiveIcon != null)
                val requestedFolder = if (folderId != note.folderId) folderId else current.folderId
                val requestedX = if (coverX != note.coverX) coverX else current.coverX
                val requestedY = if (coverY != note.coverY) coverY else current.coverY
                val previousCover = if (replaceCover) attachmentDao.getAttachmentsOnce(note.id).filter { it.type == "cover" } else emptyList()
                val removed = (removedAttachments.filter { it.noteId == note.id } + previousCover).distinctBy { it.id }
                val validFolder = requestedFolder?.takeIf { folderDao.byId(it)?.libraryId == current.libraryId }
                if (current.title != title || current.content != content || current.color != color || current.formattingEnabled != formattingEnabled) {
                    runCatching { com.example.mynotes.data.workspace.NoteWorkspaceStore.rememberRevision(getApplication(), current) }
                }
                val changed = try { database.withTransaction {
                    val edited = noteDao.updateEditedFields(note.id, title, content, color)
                    val presentation = noteDao.updatePresentation(note.id, validFolder, CollectionPolicy.icon(effectiveIcon), CollectionPolicy.crop(requestedX), CollectionPolicy.crop(requestedY), formattingEnabled)
                    val removedCount = if (removed.isNotEmpty()) attachmentDao.deleteAttachments(removed) else 0
                    if (prepared.isNotEmpty()) attachmentDao.insertAttachments(prepared.map { it.attachment })
                    val coverChanged = if (replaceCover) noteDao.updateCover(note.id, newCover?.internalUri?.toString()) else 0
                    edited > 0 || presentation > 0 || removedCount > 0 || coverChanged > 0 || prepared.isNotEmpty()
                } }catch(error:Exception){discardStagedAttachments(prepared);throw error}
                runCatching {
                    studio.changed(note.id,"edited")
                }.onFailure{com.example.mynotes.studio.StudioJobs.failure("Workspace metadata",it)}
                runCatching{cleanUpAttachmentFiles(removed)}.onFailure{com.example.mynotes.studio.StudioJobs.failure("Attachment cleanup",it)}
                prewarmAttachments(prepared.filter { it.type != "cover" })
                if (changed) refreshWidgets()
                runCatching { com.example.mynotes.data.workspace.WorkspaceAssets.release(getApplication(), newAttachments.map { it.uri }) }
                onSaved()
            }
        }
    }

    override fun createFolder(name: String, icon: String, color: String) {
        val title = CollectionPolicy.name(name)
        if (title.isBlank()) return
        viewModelScope.launch(failures) { folderDao.upsert(NoteFolder(UUID.randomUUID().toString(), title,
            CollectionPolicy.icon(icon) ?: "folder", CollectionPolicy.color(color))) }
    }
    override fun editFolder(folder: NoteFolder) {
        val title = CollectionPolicy.name(folder.name)
        if (title.isBlank()) return
        viewModelScope.launch(failures) { mutationMutex.withLock {
            if (folderDao.byId(folder.id)?.libraryId == com.example.mynotes.studio.PlusProfiles.currentId) folderDao.upsert(folder.copy(name = title,
                icon = CollectionPolicy.icon(folder.icon) ?: "folder", color = CollectionPolicy.color(folder.color)))
        } }
    }
    override fun deleteFolder(folder: NoteFolder) {
        viewModelScope.launch(failures) { mutationMutex.withLock {
            finishPreviousUndo()
            val actual = folderDao.byId(folder.id)?.takeIf{it.libraryId==com.example.mynotes.studio.PlusProfiles.currentId} ?: return@withLock
            val before = noteDao.getAllNotesOnce().filter { it.folderId == folder.id }
            database.withTransaction { noteDao.detachFolder(folder.id); folderDao.delete(folder.id) }
            undoState.value = NoteUndo(sessionToken.incrementAndGet(), NoteOperation.FOLDER_DELETE, before, actual)
            refreshWidgets()
        } }
    }

    private fun mutate(ids: Set<Int>, operation: NoteOperation, predicate: (Note) -> Boolean = { true }, change: suspend (List<Note>) -> Unit) {
        if (ids.isEmpty()) return
        viewModelScope.launch(failures) { mutationMutex.withLock {
            studio.load()
            val before = noteDao.byIds(ids.toList()).filter{it.libraryId==com.example.mynotes.studio.PlusProfiles.currentId}.filter{studio.state.value.get("notes",it.id.toString()).optString("remoteRole")!="viewer"}.filter(predicate)
            if (before.isEmpty()) return@withLock
            finishPreviousUndo()
            before.forEach{com.example.mynotes.studio.StudioHistory.capture(getApplication(),it,attachmentDao.getAttachmentsOnce(it.id))}
            database.withTransaction { change(before) }
            before.forEach{note->if(operation==NoteOperation.DELETE)studio.edit("trash",note.id.toString()){it.put("at",System.currentTimeMillis())};studio.changed(note.id,operation.name.lowercase())}
            undoState.value = NoteUndo(sessionToken.incrementAndGet(), operation, before)
            refreshWidgets()
        } }
    }
    override fun deleteNotes(ids: Set<Int>) = mutate(ids, NoteOperation.DELETE) { noteDao.setDeleted(it.map { n -> n.id }, true) }
    override fun archiveNotes(ids: Set<Int>, archived: Boolean) = mutate(ids, NoteOperation.ARCHIVE, { it.isArchived != archived }) { noteDao.setArchived(it.map { n -> n.id }, archived) }
    override fun pinNotes(ids: Set<Int>, pinned: Boolean) = mutate(ids, NoteOperation.PIN, { it.isPinned != pinned }) { items -> items.forEach { noteDao.updatePinned(it.id, pinned) } }
    override fun colorNotes(ids: Set<Int>, color: String) = mutate(ids, NoteOperation.COLOR, { it.color != color }) { items -> items.forEach { noteDao.updateColor(it.id, color) } }
    override fun moveNotes(ids: Set<Int>, folderId: String?) = mutate(ids, NoteOperation.MOVE, { it.folderId != folderId }) {
        val folder = folderId?.takeIf { id -> folderDao.byId(id)?.libraryId == com.example.mynotes.studio.PlusProfiles.currentId }
        noteDao.move(it.map { n -> n.id }, folder)
    }
    override fun deleteNote(note: Note) = deleteNotes(setOf(note.id))
    override fun togglePinned(note: Note) = pinNotes(setOf(note.id), !note.isPinned)
    override fun changeNoteColor(note: Note, color: String) = colorNotes(setOf(note.id), color)
    override fun changePriority(note: Note, priority: Int) { launchWithWidgetUpdate(note.id) { noteDao.updatePriority(note.id, priority.coerceIn(0, 3)) } }
    override fun toggleFavorite(note: Note) { launchWithWidgetUpdate(note.id) { noteDao.updateFavorite(note.id, !note.isFavorite) } }
    override fun changeCategory(note: Note, category: String) { launchWithWidgetUpdate(note.id) { noteDao.updateCategory(note.id, if (category == "work") "work" else "personal") } }
    override fun toggleTask(noteId: Int, line: Int) {
        viewModelScope.launch(failures) { mutationMutex.withLock {
            val current = noteDao.getNoteByIdOnce(noteId)?.takeIf{it.libraryId==com.example.mynotes.studio.PlusProfiles.currentId} ?: return@withLock
            if (!current.formattingEnabled||studio.state.value.get("notes",noteId.toString()).optString("remoteRole")=="viewer") return@withLock
            val content = com.example.mynotes.ui.notes.NoteMarkup.toggleTask(current.content, line)
            if (noteDao.updateContent(noteId, content) > 0) {studio.changed(noteId,"task");refreshWidgets()}
        } }
    }

    override fun undo(token: Long) {
        viewModelScope.launch(failures) { mutationMutex.withLock {
            val operation = undoState.value?.takeIf { it.token == token } ?: return@withLock
            database.withTransaction {
                when (operation.operation) {
                    NoteOperation.DELETE -> noteDao.setDeleted(operation.before.map { it.id }, false)
                    NoteOperation.ARCHIVE -> operation.before.forEach { noteDao.setArchived(listOf(it.id), it.isArchived) }
                    NoteOperation.PIN -> operation.before.forEach { noteDao.updatePinned(it.id, it.isPinned) }
                    NoteOperation.COLOR -> operation.before.forEach { noteDao.updateColor(it.id, it.color) }
                    NoteOperation.MOVE -> operation.before.forEach { noteDao.move(listOf(it.id), it.folderId?.takeIf { id -> folderDao.byId(id) != null }) }
                    NoteOperation.FOLDER_DELETE -> {
                        operation.folder?.let { folderDao.upsert(it) }
                        operation.before.forEach { noteDao.move(listOf(it.id), it.folderId) }
                    }
                }
            }
            operation.before.forEach{studio.changed(it.id,"undo");if(operation.operation==NoteOperation.DELETE)studio.remove("trash",it.id.toString())}
            undoState.value = null
            refreshWidgets()
        } }
    }
    override fun dismissUndo(token: Long) {
        viewModelScope.launch(failures) { mutationMutex.withLock {
            if (undoState.value?.token == token) finishPreviousUndo()
        } }
    }
    private suspend fun finishPreviousUndo() {
        val previous = undoState.value ?: return
        // Retain deleted notes until their configured retention date.
        undoState.value = null
    }
    private suspend fun purgeDeleted(requested: List<Note>) {
        val items=requested.filter{val meta=studio.state.value.copy(library=it.libraryId).get("notes",it.id.toString());meta.optString("remoteRole")!="viewer"&&(meta.optString("remoteBook").isBlank()||!meta.optBoolean("dirty"))}
        if(items.size<requested.size)com.example.mynotes.studio.StudioJobs.failure("Trash",IllegalStateException("Read-only shared notes and unsynchronized shared changes cannot be permanently deleted"))
        val attachments = items.flatMap { attachmentDao.getAttachmentsOnce(it.id) }
        database.withTransaction { items.forEach { attachmentDao.deleteAttachmentsForNote(it.id); noteDao.deleteNote(it) } }
        cleanUpAttachmentFiles(attachments)
        com.example.mynotes.data.workspace.NoteWorkspaceStore.removeNotes(getApplication(), items.map { it.id })
        com.example.mynotes.studio.StudioHistory.remove(getApplication(),items.map{it.id});items.forEach{studio.removeInLibrary("notes",it.id.toString(),it.libraryId);studio.removeInLibrary("trash",it.id.toString(),it.libraryId)}
    }
    fun purgeTrash(ids:Set<Int>){viewModelScope.launch(failures){mutationMutex.withLock{purgeDeleted(noteDao.deletedNotes().filter{it.id in ids&&it.libraryId==com.example.mynotes.studio.PlusProfiles.currentId});refreshWidgets()}}}
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
                                        pendingAttachment.type, name = attachmentName,newPrivateCopy=runCatching{
                                            pendingAttachment.uri.scheme!="file"||File(requireNotNull(internalUri.path)).canonicalFile!=File(requireNotNull(pendingAttachment.uri.path)).canonicalFile
                                        }.getOrDefault(false)))
                        }
                    }
            }
        }
    }
    private suspend fun discardStagedAttachments(prepared:List<PreparedAttachment>){
        cleanUpAttachmentFiles(prepared.filter{it.newPrivateCopy}.map{it.attachment})
    }
    private suspend fun requireCompleteAttachments(prepared:List<PreparedAttachment>,expected:Int){
        if(prepared.size==expected)return
        discardStagedAttachments(prepared)
        throw IllegalStateException("An attachment could not be read. Re-select the unavailable file and try again; the draft has been kept.")
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
        var incompleteFile:File?=null
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
                    if (sourceFile.isFile) {
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
            val fileName = "note_${noteId}_${UUID.randomUUID()}_${index}.$extension"
            val destinationFile = File(attachmentsDirectory, fileName)
            incompleteFile=destinationFile
            context.openUriStream(sourceUri)?.buffered(64 * 1024)?.use { input ->
                destinationFile.outputStream().buffered(64 * 1024).use { output ->
                    input.copyTo(out = output, bufferSize = 64 * 1024)
                }
            } ?: run { destinationFile.delete();return null }
            if (!destinationFile.exists() || destinationFile.length() <= 0L) {
                destinationFile.delete()
                return null
            }
            Uri.fromFile(destinationFile)
        } catch (e: Exception) {
            incompleteFile?.delete()
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
    private suspend fun cleanUpAttachmentFiles(attachments: List<Attachment>) {
        if (attachments.isEmpty()) return
        withContext(Dispatchers.IO) {
            val remaining=attachmentDao.getAllAttachmentsOnce().map{it.uri}.toSet()
            attachments.filter{it.uri !in remaining}.forEach { attachment ->
                val uri = Uri.parse(attachment.uri)
                AttachmentPreviewCache.invalidate(context = getApplication<Application>(), uri = uri)
                deletePrivateFileIfPresent(uri)
            }
        }
    }
    private fun deletePrivateFileIfPresent(uri: Uri) {
        if (uri.scheme != "file") return
        val root = File(getApplication<Application>().filesDir, "attachments").canonicalFile
        val file = uri.path?.let(::File)?.canonicalFile ?: return
        if (file.path.startsWith(root.path + File.separator)) file.delete()
    }
}
