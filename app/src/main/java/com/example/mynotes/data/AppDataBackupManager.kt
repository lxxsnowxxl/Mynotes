package com.example.mynotes.data

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.settings.SettingsRepository
import com.example.mynotes.widget.MyNotesWidgetUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
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
    /*
     * Límites defensivos para importar ZIPs seleccionados por el usuario.
     * No limitamos el tamaño a unos pocos MB porque una copia puede incluir
     * vídeos grandes; sí evitamos cantidades absurdas de entradas y que una
     * copia comprimida intente ocupar todo el almacenamiento disponible.
     */
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
                    /*
                     * Conservamos únicamente el perfil activo creado por una
                     * restauración. Así las importaciones repetidas no dejan
                     * imágenes huérfanas ocupando almacenamiento.
                     */
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
        val input = openInputStream(context, source) ?: return false
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
    private fun openInputStream(context: Context, uri: Uri): InputStream? = try {
            if (uri.scheme == "file") {
                val path = uri.path ?: return null
                FileInputStream(File(path))
            } else {
                context.contentResolver.openInputStream(uri)
            }
        } catch (_: Exception) {
            null
        }
    private fun extractBackup(context: Context, source: Uri, stageDir: File) {
        val input = context.contentResolver.openInputStream(source)?: error("No se pudo abrir la copia")
        /*
         * Dejamos un margen de almacenamiento libre para que Android y la
         * propia aplicación sigan pudiendo escribir archivos temporales.
         */
        val initialFreeSpace = stageDir.usableSpace.coerceAtLeast(0L)
        val extractionLimit = if (initialFreeSpace > 0L) {
                /*
                 * Nunca permitimos extraer más de lo que realmente cabe.
                 * Reservamos al menos 32 MiB (o 15 % del espacio disponible)
                 * para que Android pueda seguir creando archivos temporales.
                 */
                val reserve = maxOf(32L * 1024L * 1024L, initialFreeSpace * 15L / 100L)
                (initialFreeSpace - reserve).coerceAtLeast(0L)
            } else {
                /*
                 * usableSpace puede devolver 0 en proveedores peculiares.
                 * En ese caso usamos un límite defensivo; las escrituras
                 * siguen fallando limpiamente si el sistema se queda sin espacio.
                 */
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
    private fun noteToJson(note: Note) = JSONObject().apply {
            put("id", note.id)
            put("title", note.title)
            put("content", note.content)
            put("createdAt", note.createdAt)
            put("color", note.color)
            put("priority", note.priority)
            put("category", note.category)
            put("isFavorite", note.isFavorite)
            put("isPinned", note.isPinned)
        }
    private fun attachmentToJson(attachment: Attachment, fileEntry: String) = JSONObject().apply {
            put("id", attachment.id)
            put("noteId", attachment.noteId)
            put("type", attachment.type)
            put("name", attachment.name ?: JSONObject.NULL)
            put("createdAt", attachment.createdAt)
            put("fileEntry", fileEntry)
        }
    private fun jsonToNotes(array: JSONArray): List<Note> = buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(Note(id = item.getInt("id"), title = item.optString("title", ""), content = item.optString("content", ""),
                        createdAt = item.optLong("createdAt", System.currentTimeMillis()), color = item.optString("color", "default"),
                        priority = item.optInt("priority", 0), category = item.optString("category", "personal"),
                        isFavorite = item.optBoolean("isFavorite", false), isPinned = item.optBoolean("isPinned", false)))
            }
        }
    private data class AttachmentRecord(val id: Int, val noteId: Int, val type: String, val name: String?, val createdAt: Long,
        val fileEntry: String)
    private fun jsonToAttachmentRecords(array: JSONArray): List<AttachmentRecord> = buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(AttachmentRecord(id = item.getInt("id"), noteId = item.getInt("noteId"), type = item.optString("type", "file"), name =
                            if (item.isNull("name")) null
                            else item.optString("name").takeIf { it.isNotBlank() },
                        createdAt = item.optLong("createdAt", System.currentTimeMillis()), fileEntry = item.getString("fileEntry")))
            }
        }
    private fun settingsToJson(settings: AppSettings, profileEntry: String?) = JSONObject().apply {
        put("configurationMode", settings.configurationMode)
        put("darkMode", settings.darkMode)
        put("backgroundColor", settings.backgroundColor)
        put("backgroundToneIndex", settings.backgroundToneIndex)
        put("backgroundIntensity", settings.backgroundIntensity.toDouble())
        put("settingsPanelTone", settings.settingsPanelTone.toDouble())
        put("surfacePanelIntensity", settings.surfacePanelIntensity.toDouble())
        put("headerIntensity", settings.headerIntensity.toDouble())
        put("textColor", settings.textColor)
        put("textOutlineEnabled", settings.textOutlineEnabled)
        put("sliderStyle", settings.sliderStyle)
        put("font", settings.font)
        put("fontSize", settings.fontSize.toDouble())
        put("soundEffectsEnabled", settings.soundEffectsEnabled)
        put("soundEffectsVolume", settings.soundEffectsVolume.toDouble())
        put("soundEffectsTheme", settings.soundEffectsTheme)
        put("reminderSoundEnabled", settings.reminderSoundEnabled)
        put("reminderSoundVolume", settings.reminderSoundVolume.toDouble())
        put("reminderRingtone", settings.reminderRingtone)
        put("hapticEffectsEnabled", settings.hapticEffectsEnabled)
        put("hapticEffectsIntensity", settings.hapticEffectsIntensity.toDouble())
        put("hapticEffectsStyle", settings.hapticEffectsStyle)
        put("language", settings.language)
        put("gridColumns", settings.gridColumns)
        put("sortOrder", settings.sortOrder)
        put("profileImageSize", settings.profileImageSize.toDouble())
        put("profileEntry", profileEntry ?: "")
        put("iconStyle", settings.iconStyle)
        put("iconSize", settings.iconSize.toDouble())
        put("accentColor", settings.accentColor)
        put("noteCardCornerRadius", settings.noteCardCornerRadius.toDouble())
        put("noteCardElevation", settings.noteCardElevation.toDouble())
        put("noteCardPadding", settings.noteCardPadding.toDouble())
        put("noteCardImageHeight", settings.noteCardImageHeight.toDouble())
        put("noteCardOutlineEnabled", settings.noteCardOutlineWidth > 0.01f)
        put("noteCardOutlineWidth", settings.noteCardOutlineWidth.coerceIn(0f, 6f).toDouble())
        put("noteTitleMaxLines", settings.noteTitleMaxLines)
        put("noteContentMaxLines", settings.noteContentMaxLines)
        put("noteLineSpacing", settings.noteLineSpacing.toDouble())
        put("showNoteDate", settings.showNoteDate)
        put("showCategoryChip", settings.showCategoryChip)
        put("showFavoriteIcon", settings.showFavoriteIcon)
        put("fabSize", settings.fabSize.toDouble())
        put("optionMenuOrder", settings.optionMenuOrder)
        put("optionMenuHiddenItems", settings.optionMenuHiddenItems)
        put("optionMenuShowIcons", settings.optionMenuShowIcons)
        put("optionMenuTextColor", settings.optionMenuTextColor)
        put("optionMenuOpacity", settings.optionMenuOpacity.toDouble())
        put("priorityMenuHiddenItems", settings.priorityMenuHiddenItems)
        put("colorMenuHiddenItems", settings.colorMenuHiddenItems)
        put("performanceMode", settings.performanceMode)
        put("animationsEnabled", settings.animationsEnabled)
        put("animationStyle", settings.animationStyle)
        put("animationEasing", settings.animationEasing)
        put("animationSpeed", settings.animationSpeed.toDouble())
        put("animationIntensity", settings.animationIntensity.toDouble())
    }
    private fun jsonToSettings(json: JSONObject): AppSettings {
        val defaults = AppSettings()
        return AppSettings(configurationMode = json.optString("configurationMode", "advanced"),
            darkMode = json.optBoolean("darkMode", defaults.darkMode),
            backgroundColor = json.optString("backgroundColor", defaults.backgroundColor),
            backgroundToneIndex = json.optInt("backgroundToneIndex", defaults.backgroundToneIndex),
            backgroundIntensity = json.optDouble("backgroundIntensity", defaults.backgroundIntensity.toDouble()).toFloat(),
            settingsPanelTone = json.optDouble("settingsPanelTone", defaults.settingsPanelTone.toDouble()).toFloat(),
            surfacePanelIntensity = json.optDouble("surfacePanelIntensity", defaults.surfacePanelIntensity.toDouble()).toFloat(),
            headerIntensity = json.optDouble("headerIntensity", defaults.headerIntensity.toDouble()).toFloat(),
            textColor = json.optString("textColor", defaults.textColor),
            textOutlineEnabled = json.optBoolean("textOutlineEnabled", defaults.textOutlineEnabled),
            sliderStyle = json.optString("sliderStyle", defaults.sliderStyle), font = json.optString("font", defaults.font),
            fontSize = json.optDouble("fontSize", defaults.fontSize.toDouble()).toFloat(),
            soundEffectsEnabled = json.optBoolean("soundEffectsEnabled", defaults.soundEffectsEnabled),
            soundEffectsVolume = json.optDouble("soundEffectsVolume", defaults.soundEffectsVolume.toDouble()).toFloat(),
            soundEffectsTheme = json.optString("soundEffectsTheme", defaults.soundEffectsTheme),
            reminderSoundEnabled = json.optBoolean("reminderSoundEnabled", defaults.reminderSoundEnabled),
            reminderSoundVolume = json.optDouble("reminderSoundVolume", defaults.reminderSoundVolume.toDouble()).toFloat(),
            reminderRingtone = json.optString("reminderRingtone", defaults.reminderRingtone),
            hapticEffectsEnabled = json.optBoolean("hapticEffectsEnabled", defaults.hapticEffectsEnabled),
            hapticEffectsIntensity = json.optDouble("hapticEffectsIntensity", defaults.hapticEffectsIntensity.toDouble()).toFloat(),
            hapticEffectsStyle = json.optString("hapticEffectsStyle", defaults.hapticEffectsStyle),
            language = json.optString("language", defaults.language), gridColumns = json.optInt("gridColumns", defaults.gridColumns),
            sortOrder = json.optString("sortOrder", defaults.sortOrder), profileImageUri = "",
            profileImageSize = json.optDouble("profileImageSize", defaults.profileImageSize.toDouble()).toFloat(),
            iconStyle = json.optString("iconStyle", defaults.iconStyle),
            iconSize = json.optDouble("iconSize", defaults.iconSize.toDouble()).toFloat(),
            accentColor = json.optString("accentColor", defaults.accentColor),
            noteCardCornerRadius = json.optDouble("noteCardCornerRadius", defaults.noteCardCornerRadius.toDouble()).toFloat(),
            noteCardElevation = json.optDouble("noteCardElevation", defaults.noteCardElevation.toDouble()).toFloat(),
            noteCardPadding = json.optDouble("noteCardPadding", defaults.noteCardPadding.toDouble()).toFloat(),
            noteCardImageHeight = json.optDouble("noteCardImageHeight", defaults.noteCardImageHeight.toDouble()).toFloat(),
            noteCardOutlineEnabled = json.optBoolean("noteCardOutlineEnabled", defaults.noteCardOutlineEnabled),
            noteCardOutlineWidth = if (json.optBoolean("noteCardOutlineEnabled", defaults.noteCardOutlineEnabled)) {
                    json.optDouble("noteCardOutlineWidth", 1.0).toFloat().coerceIn(0f, 6f)
                } else {
                    0f
                },
            noteTitleMaxLines = json.optInt("noteTitleMaxLines", defaults.noteTitleMaxLines),
            noteContentMaxLines = json.optInt("noteContentMaxLines", defaults.noteContentMaxLines),
            noteLineSpacing = json.optDouble("noteLineSpacing", defaults.noteLineSpacing.toDouble()).toFloat(),
            showNoteDate = json.optBoolean("showNoteDate", defaults.showNoteDate),
            showCategoryChip = json.optBoolean("showCategoryChip", defaults.showCategoryChip),
            showFavoriteIcon = json.optBoolean("showFavoriteIcon", defaults.showFavoriteIcon),
            fabSize = json.optDouble("fabSize", defaults.fabSize.toDouble()).toFloat(),
            optionMenuOrder = json.optString("optionMenuOrder", defaults.optionMenuOrder),
            optionMenuHiddenItems = json.optString("optionMenuHiddenItems", defaults.optionMenuHiddenItems),
            optionMenuShowIcons = json.optBoolean("optionMenuShowIcons", defaults.optionMenuShowIcons),
            optionMenuTextColor = json.optString("optionMenuTextColor", defaults.optionMenuTextColor),
            optionMenuOpacity = json.optDouble("optionMenuOpacity", defaults.optionMenuOpacity.toDouble()).toFloat(),
            priorityMenuHiddenItems = json.optString("priorityMenuHiddenItems", defaults.priorityMenuHiddenItems),
            colorMenuHiddenItems = json.optString("colorMenuHiddenItems", defaults.colorMenuHiddenItems),
            performanceMode = json.optString("performanceMode", defaults.performanceMode),
            animationsEnabled = json.optBoolean("animationsEnabled", defaults.animationsEnabled),
            animationStyle = json.optString("animationStyle", defaults.animationStyle),
            animationEasing = json.optString("animationEasing", defaults.animationEasing),
            animationSpeed = json.optDouble("animationSpeed", defaults.animationSpeed.toDouble()).toFloat(),
            animationIntensity = json.optDouble("animationIntensity", defaults.animationIntensity.toDouble()).toFloat())
    }
}
