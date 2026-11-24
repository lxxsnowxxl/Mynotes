package com.example.mynotes.data.workspace

import android.content.Context
import android.net.Uri
import android.util.AtomicFile
import com.example.mynotes.data.Attachment
import com.example.mynotes.data.Note
import com.example.mynotes.data.NoteDraft
import com.example.mynotes.data.PendingAttachment
import com.example.mynotes.util.openUriStream
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class NoteRevision(val title: String, val content: String, val color: String, val formatted: Boolean, val savedAt: Long)
data class RecoveredNoteDraft(val draft: NoteDraft, val savedAt: Long, val session:String?=null)

object NoteWorkspaceStore {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutexes = ConcurrentHashMap<String, Mutex>()
    private val pendingWrites = ConcurrentHashMap<String,Job>()
    private val sessions = ConcurrentHashMap<String, String>()
    private val closed = ConcurrentHashMap.newKeySet<String>()
    fun key(noteId: Int?, workspaceId:String?=null):String {
        val original=noteId?.let { "note_" + it } ?: if(com.example.mynotes.studio.PlusProfiles.currentId=="default")"new_note"else "new_note_"+com.example.mynotes.studio.PlusProfiles.currentId
        return if(noteId==null&&!workspaceId.isNullOrBlank())original+":workspace:"+workspaceId else original
    }
    suspend fun flush(){pendingWrites.values.toList().forEach{it.join()}}
    fun begin(key: String, session: String) { sessions[key] = session }
    private fun folder(context: Context, key: String): File {
        val hash = MessageDigest.getInstance("SHA-256").digest(key.toByteArray()).joinToString("") { "%02x".format(it) }
        return File(context.filesDir, "workspaces/note_drafts/" + hash).apply { check(exists() || mkdirs()) }
    }
    private fun nullable(json: JSONObject, key: String) = if (json.isNull(key)) null else json.optString(key).takeIf { it.isNotEmpty() }
    private fun read(file: File): JSONObject? = runCatching {
        val backup = File(file.path + ".bak")
        if ((!file.exists() && !backup.exists()) || maxOf(file.length(), backup.length()) > 16L * 1024 * 1024) null else JSONObject(AtomicFile(file).openRead().bufferedReader().use { it.readText() })
    }.getOrNull()
    private fun write(file: File, json: JSONObject) {
        val encoded = json.toString().toByteArray(Charsets.UTF_8)
        require(encoded.size <= 16 * 1024 * 1024) { "Workspace metadata exceeds the recovery limit" }
        val atomic = AtomicFile(file)
        val output = atomic.startWrite()
        try { output.write(encoded); atomic.finishWrite(output) }
        catch (error: Exception) { atomic.failWrite(output); throw error }
    }
    suspend fun load(context: Context, key: String): RecoveredNoteDraft? = withContext(Dispatchers.IO) {
        pendingWrites[key]?.join()
        mutexes.getOrPut(key) { Mutex() }.withLock {
            val json = read(File(folder(context, key), "draft.json")) ?: return@withLock null
            val attachments = json.optJSONArray("attachments") ?: JSONArray()
            val pending = (0 until attachments.length()).map { index ->
                val item = attachments.getJSONObject(index)
                PendingAttachment(Uri.parse(item.getString("uri")), item.getString("type"), nullable(item, "name"), nullable(item, "mime"))
            }
            val removed = json.optJSONArray("removed") ?: JSONArray()
            val removedItems = (0 until removed.length()).map { index ->
                val item = removed.getJSONObject(index)
                Attachment(id = item.getInt("id"), noteId = item.getInt("noteId"), type = item.getString("type"),
                    uri = item.getString("uri"), name = nullable(item, "name"), createdAt = item.optLong("createdAt"))
            }
            RecoveredNoteDraft(NoteDraft(json.optString("title"), json.optString("content"), json.optString("color", "default"),
                pending, removedItems, nullable(json, "folderId"), nullable(json, "coverIcon"),
                json.optDouble("coverX", .5).toFloat(), json.optDouble("coverY", .5).toFloat(),
                json.optBoolean("removeCover"), json.optBoolean("formattingEnabled")), json.optLong("savedAt"),json.optString("session").takeIf{it.isNotBlank()})
        }
    }
    suspend fun save(context: Context, key: String, session: String, draft: NoteDraft) {
        withContext(Dispatchers.IO + NonCancellable) {com.example.mynotes.studio.StudioFiles.write {
            mutexes.getOrPut(key) { Mutex() }.withLock {
                if (session in closed || sessions[key] != session) return@withLock
                val directory = folder(context, key)
                val previous = read(File(directory, "draft.json"))
                if (previous?.optString("session") == session && previous.optLong("modifiedAt") > draft.workspaceModifiedAt) return@withLock
                val old = previous?.optJSONArray("attachments") ?: JSONArray()
                val existing = (0 until old.length()).map { old.getJSONObject(it) }.associateBy { it.optString("sourceUri") }
                val assets = File(directory, "assets").apply { check(exists() || mkdirs()) }
                val copied = mutableListOf<File>()
                var attachmentFailure:Exception?=null
                try {
                    val attachmentJson = JSONArray()
                    for (item in draft.attachments) {
                        val source = item.uri.toString()
                        val known = existing[source]?.optString("uri")?.takeIf { stored -> File(Uri.parse(stored).path.orEmpty()).isFile }
                        val own = item.uri.scheme == "file" && File(item.uri.path.orEmpty()).isFile && File(item.uri.path.orEmpty()).canonicalPath.startsWith(assets.canonicalPath + File.separator)
                        val uri = when {
                            own -> source
                            known != null -> known
                            else -> {
                                val file = File(assets, UUID.randomUUID().toString())
                                copied += file
                                try {
                                context.openUriStream(item.uri)?.use { input -> file.outputStream().use { output ->
                                    val buffer = ByteArray(64 * 1024); var total = 0L
                                    while (true) {
                                        val count = input.read(buffer); if (count < 0) break
                                        total += count; check(total <= 128L * 1024 * 1024)
                                        output.write(buffer, 0, count)
                                    }
                                } } ?: error("Draft attachment cannot be opened")
                                Uri.fromFile(file).toString()
                                } catch(error:Exception){
                                    file.delete();copied.remove(file);attachmentFailure=error
                                    source // Keep the text and the unavailable reference so the user can re-select it.
                                }
                            }
                        }
                        attachmentJson.put(JSONObject().put("sourceUri", source).put("uri", uri).put("type", item.type)
                            .put("name", item.name ?: JSONObject.NULL).put("mime", item.mimeType ?: JSONObject.NULL))
                    }
                    val removed = JSONArray()
                    draft.removedAttachments.forEach { item -> removed.put(JSONObject().put("id", item.id).put("noteId", item.noteId)
                        .put("type", item.type).put("uri", item.uri).put("name", item.name ?: JSONObject.NULL).put("createdAt", item.createdAt)) }
                    val json = JSONObject().put("title", draft.title).put("content", draft.content).put("color", draft.color)
                        .put("attachments", attachmentJson).put("removed", removed).put("folderId", draft.folderId ?: JSONObject.NULL)
                        .put("coverIcon", draft.coverIcon ?: JSONObject.NULL).put("coverX", draft.coverX.toDouble()).put("coverY", draft.coverY.toDouble())
                        .put("removeCover", draft.removeCover).put("formattingEnabled", draft.formattingEnabled).put("savedAt", System.currentTimeMillis()).put("session", session).put("modifiedAt", draft.workspaceModifiedAt)
                    if (session !in closed && sessions[key] == session) {
                        write(File(directory, "draft.json"), json)
                        val used = (0 until attachmentJson.length()).map { Uri.parse(attachmentJson.getJSONObject(it).getString("uri")).path }.toSet()
                        assets.listFiles()?.filter { it.path !in used }?.forEach { it.delete() }
                    } else copied.forEach { it.delete() }
                } catch (error: Exception) { copied.forEach { it.delete() }; throw error }
                if(attachmentFailure!=null)throw IllegalStateException("The draft text was kept. Re-select or remove an unavailable attachment.",attachmentFailure)
            }
        }}
    }
    fun saveOnExit(context: Context, key: String, session: String, draft: NoteDraft) {
        val job=scope.launch(start=CoroutineStart.LAZY){runCatching{save(context.applicationContext,key,session,draft)}}
        pendingWrites[key]=job;job.invokeOnCompletion{pendingWrites.remove(key,job)};job.start()
    }
    suspend fun clearExisting(context: Context, key: String, session: String) = withContext(Dispatchers.IO) {
        mutexes.getOrPut(key) { Mutex() }.withLock {
            if (sessions[key] == session) folder(context, key).deleteRecursively()
        }
    }
    fun discard(context: Context, key: String, session: String?) {
        if (session == null) return
        closed += session
        scope.launch {
            mutexes.getOrPut(key) { Mutex() }.withLock {
                if (sessions[key] == session) {
                    folder(context, key).deleteRecursively()
                    sessions.remove(key, session)
                }
                closed.remove(session)
            }
        }
    }
    suspend fun clearNoteStateAfterRestore(context: Context) = withContext(Dispatchers.IO) {
        sessions.clear()
        mutexes.values.toList().forEach { mutex -> mutex.withLock { } }
        File(context.filesDir, "workspaces/note_drafts").deleteRecursively()
        File(context.filesDir, "workspaces/note_history").deleteRecursively()
    }
    suspend fun removeNotes(context: Context, ids: List<Int>) = withContext(Dispatchers.IO) {
        for (id in ids) {
            val key = key(id)
            sessions.remove(key)
            mutexes.getOrPut(key) { Mutex() }.withLock { folder(context, key).deleteRecursively() }
            mutexes.getOrPut("history_" + id) { Mutex() }.withLock { AtomicFile(File(context.filesDir, "workspaces/note_history/$id.json")).delete() }
        }
    }
    suspend fun rememberRevision(context: Context, note: Note) = withContext(Dispatchers.IO) {
        val key = "history_" + note.id
        mutexes.getOrPut(key) { Mutex() }.withLock {
            val file = File(context.filesDir, "workspaces/note_history/" + note.id + ".json").apply { parentFile?.mkdirs() }
            val old = read(file)?.optJSONArray("versions") ?: JSONArray()
            val versions = (0 until old.length()).map { old.getJSONObject(it) }.toMutableList()
            val last = versions.lastOrNull()
            if (last != null && last.optString("title") == note.title && last.optString("content") == note.content &&
                last.optString("color") == note.color && last.optBoolean("formatted") == note.formattingEnabled) return@withLock
            versions += JSONObject().put("title", note.title).put("content", note.content).put("color", note.color)
                .put("formatted", note.formattingEnabled).put("savedAt", System.currentTimeMillis())
            while (versions.size > 20 || (versions.size > 1 && versions.sumOf { it.toString().length.toLong() } > 4_000_000L)) versions.removeAt(0)
            if (versions.last().toString().length <= 4_000_000) write(file, JSONObject().put("versions", JSONArray(versions)))
        }
    }
    suspend fun revisions(context: Context, noteId: Int): List<NoteRevision> = withContext(Dispatchers.IO) {
        val key = "history_" + noteId
        mutexes.getOrPut(key) { Mutex() }.withLock {
            val array = read(File(context.filesDir, "workspaces/note_history/" + noteId + ".json"))?.optJSONArray("versions") ?: return@withLock emptyList()
            (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)
                NoteRevision(item.optString("title"), item.optString("content"), item.optString("color", "default"), item.optBoolean("formatted"), item.optLong("savedAt"))
            }.reversed()
        }
    }
}

