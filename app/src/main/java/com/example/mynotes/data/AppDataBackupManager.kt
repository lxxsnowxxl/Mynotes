package com.example.mynotes.data

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.widget.MyNotesWidgetUpdater
import com.example.mynotes.util.openUriStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Exporta e importa una copia completa de los datos de MyNotes.
 *
 * El ZIP contiene:
 * - notas;
 * - adjuntos (imágenes, videos, audios, notas de voz y archivos);
 * - preferencias de la aplicación;
 * - imagen de perfil, cuando está disponible.
 *
 * No se copia directamente el archivo SQLite. Los datos se serializan para
 * que la copia sea independiente del estado WAL de Room y pueda restaurarse
 * de forma segura en instalaciones nuevas de la aplicación.
 */
object AppDataBackupManager {
    private const val FORMAT_NAME = "mynotes-backup"
    private const val FORMAT_VERSION = 1
    private const val DATA_ENTRY = "data.json"
    private const val ATTACHMENTS_PREFIX = "attachments/"
    private const val PROFILE_PREFIX = "profile/"
    private const val MAX_BACKUP_ENTRIES = 20_000
    private const val MAX_METADATA_BYTES = 64L * 1024L * 1024L
    data class BackupSummary(val noteCount: Int, val attachmentCount: Int, val skippedAttachmentCount: Int = 0)
    suspend fun exportBackup(context: Context, destination: Uri, settings: AppSettings): Result<BackupSummary> =
        withContext(Dispatchers.IO) {
            runCatching {
                val appContext = context.applicationContext
                val database = AppDatabase.getDatabase(appContext)
                val notes = database.noteDao().getAllNotesOnce()
                val attachments = database.attachmentDao().getAllAttachmentsOnce()
                val output = appContext.contentResolver.openOutputStream(destination, "w"
                ) ?: error("No se pudo abrir el archivo de destino")
                return@runCatching output.use { rawOutput -> ZipOutputStream(rawOutput.buffered()).use { zip ->
                        val attachmentJson = JSONArray()
                        var exportedAttachments = 0
                        var skippedAttachments = 0
                        attachments.forEach { attachment -> val safeName = safeFileName(attachment.name ?: "attachment_${attachment.id}")
                            val entryName = "$ATTACHMENTS_PREFIX${attachment.id}_$safeName"
                            val copied = writeUriEntry(context = appContext, zip = zip, source = Uri.parse(attachment.uri),
                                entryName = entryName)
                            if (copied) {
                                attachmentJson.put(attachmentToJson(attachment = attachment, fileEntry = entryName))
                                exportedAttachments++
                            } else {
                                skippedAttachments++
                            }
                        }
                        var profileEntry: String? = null
                        if (settings.profileImageUri.isNotBlank()) {
                            val profileUri = Uri.parse(settings.profileImageUri)
                            val profileName = safeFileName(profileUri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
                                        ?: "profile_image")
                            val candidate = "$PROFILE_PREFIX$profileName"
                            if (writeUriEntry(context = appContext, zip = zip, source = profileUri, entryName = candidate)) {
                                profileEntry = candidate
                            }
                        }
                        val data = JSONObject().apply {
                            put("format", FORMAT_NAME)
                            put("version", FORMAT_VERSION)
                            put("createdAt", System.currentTimeMillis())
                            put("notes", JSONArray().apply {
                                    notes.forEach { put(noteToJson(it)) }
                                })
                            put("attachments", attachmentJson)
                            put("settings", settingsToJson(settings = settings, profileEntry = profileEntry))
                        }
                        zip.putNextEntry(ZipEntry(DATA_ENTRY))
                        zip.write(data.toString().toByteArray(Charsets.UTF_8))
                        zip.closeEntry()
                        return@use BackupSummary(noteCount = notes.size, attachmentCount = exportedAttachments,
                            skippedAttachmentCount = skippedAttachments)
                    }
                }
            }
        }
    suspend fun importBackup(context: Context, source: Uri): Result<BackupSummary> = withContext(Dispatchers.IO) {
            runCatching {
                val appContext = context.applicationContext
                val stageDir = File(appContext.cacheDir, "backup_restore_${UUID.randomUUID()}")
                if (!stageDir.mkdirs() && !stageDir.isDirectory) {
                    error("No se pudo preparar la restauración")
                }
                val importedFiles = mutableListOf<File>()
                try {
                    extractBackup(context = appContext, source = source, stageDir = stageDir)
                    val dataFile = File(stageDir, DATA_ENTRY)
                    if (!dataFile.isFile) {
                        error("La copia no contiene data.json")
                    }
                    if (dataFile.length() >
                        MAX_METADATA_BYTES) {
                        error("Los metadatos de la copia son demasiado grandes")
                    }
                    val data = JSONObject(dataFile.readText(Charsets.UTF_8))
                    if (data.optString("format") != FORMAT_NAME) {
                        error("El archivo no es una copia de MyNotes")
                    }
                    if (data.optInt("version", -1) != FORMAT_VERSION) {
                        error("Versión de copia no compatible")
                    }
                    val notes = jsonToNotes(data.getJSONArray("notes"))
                    val attachmentRecords = jsonToAttachmentRecords(data.getJSONArray("attachments"))
                    val settingsJson = data.optJSONObject("settings") ?: JSONObject()
                    var restoredSettings = jsonToSettings(settingsJson)
                    val noteIds = notes.map {
                                it.id
                            }.toSet()
                    if (noteIds.size != notes.size) {
                        error("La copia contiene identificadores de nota duplicados")
                    }
                    if (attachmentRecords.map {
                                it.id
                            }.toSet().size != attachmentRecords.size) {
                        error("La copia contiene identificadores de adjunto duplicados")
                    }
                    if (attachmentRecords.any {
                            it.noteId !in
                                noteIds
                        }) {
                        error("La copia contiene adjuntos sin una nota válida")
                    }
                    val attachmentsDirectory = File(appContext.filesDir, "attachments")
                    if (!attachmentsDirectory.exists() && !attachmentsDirectory.mkdirs()) {
                        error("No se pudo preparar el almacenamiento de adjuntos")
                    }
                    val restoreToken = System.currentTimeMillis()
                    val restoredAttachments = attachmentRecords.map { record -> val sourceFile = safeStageFile(stageDir = stageDir,
                                entryName = record.fileEntry)
                            if (!sourceFile.isFile) {
                                error("Falta un adjunto en la copia: ${record.fileEntry}")
                            }
                            val targetName = "restore_${restoreToken}_${record.id}_${safeFileName(record.name ?: sourceFile.name)}"
                            val target = File(attachmentsDirectory, targetName)
                            sourceFile.copyTo(target, overwrite = true)
                            importedFiles += target
                            Attachment(id = record.id, noteId = record.noteId, type = record.type, uri = Uri.fromFile(target).toString(),
                                name = record.name, createdAt = record.createdAt)
                        }
                    val profileEntry = settingsJson.optString("profileEntry", "")
                    if (profileEntry.isNotBlank()) {
                        val profileSource = safeStageFile(stageDir, profileEntry)
                        if (profileSource.isFile) {
                            val profileDir = File(appContext.filesDir, "profile_backup")
                            if (!profileDir.exists() && !profileDir.mkdirs()) {
                                error("No se pudo preparar la imagen de perfil")
                            }
                            val profileTarget = File(profileDir, "restored_${restoreToken}_${safeFileName(profileSource.name)}")
                            profileSource.copyTo(profileTarget, overwrite = true)
                            importedFiles += profileTarget
                            restoredSettings = restoredSettings.copy(profileImageUri = Uri.fromFile(profileTarget).toString())
                        } else {
                            restoredSettings = restoredSettings.copy(profileImageUri = "")
                        }
                    } else {
                        restoredSettings = restoredSettings.copy(profileImageUri = "")
                    }
                    val database = AppDatabase.getDatabase(appContext)
                    try {
                        database.withTransaction {
                            database.attachmentDao().deleteAllAttachments()
                            database.noteDao().deleteAllNotes()
                            if (notes.isNotEmpty()) {
                                database.noteDao().insertNotes(notes)
                            }
                            if (restoredAttachments.isNotEmpty()) {
                                database.attachmentDao().insertAttachmentsForRestore(restoredAttachments)
                            }
                        }
                    } catch (e: Exception) {
                        importedFiles.forEach { it.delete() }
                        throw e
                    }
                    // La base ya apunta únicamente a los archivos recién restaurados.
                    // Eliminamos adjuntos antiguos que hayan quedado en almacenamiento.
                    val keepPaths = importedFiles.filter {
                                it.parentFile == attachmentsDirectory
                            }.map {
                                it.absolutePath
                            }.toSet()
                    attachmentsDirectory.listFiles()?.forEach { file -> if (file.absolutePath !in keepPaths) {
                            file.deleteRecursively()
                        }
                    }
                    SettingsRepository(appContext).restoreFromBackup(restoredSettings)
                    val activeProfilePath = Uri.parse(restoredSettings.profileImageUri).takeIf {
                                it.scheme == "file"
                            }?.path
                    File(appContext.filesDir, "profile_backup").listFiles()?.forEach {
                            file ->
                            if (file.absolutePath != activeProfilePath) {
                                file.delete()
                            }
                        }
                    // MainActivity usa esta preferencia antes de que DataStore esté listo.
                    appContext.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE).edit()
                        .putString("language", restoredSettings.language).commit()
                    // Los previews se vuelven a generar para las nuevas rutas.
                    listOf("video_previews", "audio_previews", "pdf_previews", "docx_previews").forEach { name -> File(appContext.cacheDir,
                            "mynotes_perf_v2/$name").deleteRecursively()
                    }
                    File(appContext.cacheDir, "viewer_video").deleteRecursively()
                    File(appContext.cacheDir, "viewer_audio").deleteRecursively()
                    MyNotesWidgetUpdater.requestUpdate(appContext)
                    BackupSummary(noteCount = notes.size, attachmentCount = restoredAttachments.size)
                } finally {
                    stageDir.deleteRecursively()
                }
            }
        }
    private fun writeUriEntry(context: Context, zip: ZipOutputStream, source: Uri, entryName: String): Boolean {
        val input = context.openUriStream(source) ?: return false
        return try {
            input.use { stream -> zip.putNextEntry(ZipEntry(entryName))
                stream.copyTo(zip, DEFAULT_BUFFER_SIZE)
                zip.closeEntry()
            }
            true
        } catch (_: Exception) {
            try {
                zip.closeEntry()
            } catch (_: Exception) {
            }
            false
        }
    }
    private fun extractBackup(context: Context, source: Uri, stageDir: File) {
        val input = context.contentResolver.openInputStream(source)?: error("No se pudo abrir la copia")
        val initialFreeSpace = stageDir.usableSpace.coerceAtLeast(0L)
        val extractionLimit = if (initialFreeSpace > 0L) {
                val reserve = maxOf(32L * 1024L * 1024L, initialFreeSpace * 15L / 100L)
                (initialFreeSpace - reserve).coerceAtLeast(0L)
            } else {
                2L * 1024L * 1024L * 1024L
            }
        if (initialFreeSpace > 0L && extractionLimit < 8L * 1024L * 1024L) {
            error("No hay espacio suficiente para restaurar la copia")
        }
        var extractedBytes = 0L
        var entryCount = 0
        val buffer = ByteArray(64 * 1024)
        input.use {
            rawInput ->
            ZipInputStream(rawInput.buffered()).use {
                    zip ->
                    while (true) {
                        val entry = zip.nextEntry?: break
                        entryCount++
                        if (entryCount >
                            MAX_BACKUP_ENTRIES) {
                            error("La copia contiene demasiados archivos")
                        }
                        if (!entry.isDirectory) {
                            val target = safeStageFile(stageDir, entry.name)
                            if (target.parentFile?.exists() == false) {
                                target.parentFile?.mkdirs()
                            }
                            target.outputStream().buffered(64 * 1024).use {
                                    output ->
                                    while (true) {
                                        val count = zip.read(buffer)
                                        if (count <= 0) {
                                            break
                                        }
                                        extractedBytes += count.toLong()
                                        if (extractedBytes >
                                            extractionLimit) {
                                            error("La copia requiere demasiado espacio de almacenamiento")
                                        }
                                        output.write(buffer, 0, count)
                                    }
                                }
                        }
                        zip.closeEntry()
                    }
                }
        }
    }
    private fun safeStageFile(stageDir: File, entryName: String): File {
        if (entryName.isBlank() || entryName.startsWith('/') || entryName.startsWith('\\') || entryName.contains("..")) {
            error("Ruta inválida dentro de la copia")
        }
        val target = File(stageDir, entryName)
        val rootPath = stageDir.canonicalPath + File.separator
        val targetPath = target.canonicalPath
        if (!targetPath.startsWith(rootPath)) {
            error("Ruta inválida dentro de la copia")
        }
        return target
    }
    private fun safeFileName(value: String): String = value.replace(Regex("[^A-Za-z0-9._-]"), "_").trim('_').take(100).ifBlank { "file" }

}
