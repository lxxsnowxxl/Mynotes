package com.example.mynotes.ui.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.roundToInt

/**
 * Persistencia local de proyectos editables de PDF Studio.
 *
 * Cada proyecto vive en files/pdf_projects/<id>/ y contiene:
 * - project.json: estructura editable (trazos, textos, páginas y posiciones)
 * - source.pdf: copia privada del PDF base, cuando existe
 * - images/: PNG de cada imagen insertada/recortada
 * - backgrounds/: fondos de páginas que no dependen del PDF base
 */
object PdfProjectRepository {
    private const val ROOT = "pdf_projects"
    private const val PROJECT_JSON = "project.json"
    private const val SOURCE_PDF = "source.pdf"
    private const val THUMBNAIL_FILE = "thumbnail.png"
    private const val SCHEMA_VERSION = 2
    private const val EXPORT_LINK_PREFS = "pdf_export_links"
    private val saveMutex = Mutex()
    private val thumbnailCache = LruCache<String, Bitmap>(18)

    data class Summary(
        val id: String,
        val name: String,
        val modifiedAt: Long,
        val pageCount: Int
    )

    data class Project(
        val id: String,
        val name: String,
        val modifiedAt: Long,
        val pages: List<PdfPageModel>,
        val sourcePdfUri: Uri?
    )

    fun newProjectId(): String = UUID.randomUUID().toString()

    private fun root(context: Context): File = File(context.filesDir, ROOT).apply { mkdirs() }
    private fun directory(context: Context, id: String): File = File(root(context), id).apply { mkdirs() }
    private fun sourceFile(context: Context, id: String): File = File(directory(context, id), SOURCE_PDF)

    fun internalSourceUri(context: Context, id: String): Uri? {
        val file = sourceFile(context, id)
        if (!file.exists()) return null
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    suspend fun listProjects(context: Context): List<Summary> = withContext(Dispatchers.IO) {
        root(context).listFiles()
            .orEmpty()
            .filter { it.isDirectory }
            .mapNotNull { dir ->
                runCatching {
                    val json = JSONObject(File(dir, PROJECT_JSON).readText())
                    Summary(
                        id = json.optString("id", dir.name),
                        name = json.optString("name", "PDF"),
                        modifiedAt = json.optLong("modifiedAt", dir.lastModified()),
                        pageCount = json.optJSONArray("pages")?.length() ?: 1
                    )
                }.getOrNull()
            }
            .sortedByDescending { it.modifiedAt }
    }

    suspend fun loadProjectThumbnail(
        context: Context,
        id: String,
        modifiedAt: Long,
        targetWidthPx: Int = 260
    ): Bitmap? {
        val key = "$id:$modifiedAt:$targetWidthPx"
        thumbnailCache.get(key)?.let { return it }

        val thumbnailFile = File(directory(context, id), THUMBNAIL_FILE)
        if (thumbnailFile.exists()) {
            BitmapFactory.decodeFile(thumbnailFile.absolutePath)?.let { stored ->
                val scaled = if (stored.width > targetWidthPx && targetWidthPx > 0) {
                    val height = (stored.height * (targetWidthPx / stored.width.toFloat())).roundToInt().coerceAtLeast(1)
                    Bitmap.createScaledBitmap(stored, targetWidthPx, height, true)
                } else {
                    stored
                }
                thumbnailCache.put(key, scaled)
                return scaled
            }
        }

        val project = runCatching { loadProject(context, id) }.getOrNull() ?: return null
        val firstPage = project.pages.firstOrNull() ?: return null
        val bitmap = runCatching {
            PdfDocumentEngine.renderProjectThumbnail(
                context = context,
                page = firstPage,
                sourcePdfUri = project.sourcePdfUri,
                targetWidthPx = targetWidthPx
            )
        }.getOrNull() ?: return null

        runCatching {
            FileOutputStream(thumbnailFile).use { bitmap.compress(Bitmap.CompressFormat.PNG, 92, it) }
        }
        thumbnailCache.put(key, bitmap)
        return bitmap
    }

    suspend fun loadProject(context: Context, id: String): Project = withContext(Dispatchers.IO) {
        val dir = directory(context, id)
        val jsonFile = File(dir, PROJECT_JSON)
        require(jsonFile.exists()) { "Proyecto PDF inexistente" }
        val json = JSONObject(jsonFile.readText())
        val pagesArray = json.optJSONArray("pages") ?: JSONArray()
        val pages = buildList {
            for (pageIndex in 0 until pagesArray.length()) {
                val pageJson = pagesArray.getJSONObject(pageIndex)
                val background = pageJson.optString("backgroundFile")
                    .takeIf { it.isNotBlank() }
                    ?.let { BitmapFactory.decodeFile(File(dir, it).absolutePath) }

                val strokes = buildList {
                    val array = pageJson.optJSONArray("strokes") ?: JSONArray()
                    for (i in 0 until array.length()) {
                        val strokeJson = array.getJSONObject(i)
                        val pointsJson = strokeJson.optJSONArray("points") ?: JSONArray()
                        val points = buildList {
                            for (j in 0 until pointsJson.length()) {
                                val point = pointsJson.getJSONObject(j)
                                add(PdfPoint(point.optDouble("x").toFloat(), point.optDouble("y").toFloat()))
                            }
                        }
                        add(
                            PdfStroke(
                                id = if (strokeJson.has("id")) {
                                    strokeJson.optLong("id")
                                } else {
                                    // Compatibilidad con proyectos creados antes de V95.
                                    -((pageIndex.toLong() + 1L) * 100_000L + i + 1L)
                                },
                                points = points,
                                colorArgb = strokeJson.optInt("colorArgb"),
                                widthPt = strokeJson.optDouble("widthPt", 3.0).toFloat()
                            )
                        )
                    }
                }

                val texts = buildList {
                    val array = pageJson.optJSONArray("texts") ?: JSONArray()
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        add(
                            PdfTextElement(
                                id = item.optLong("id"),
                                text = item.optString("text"),
                                x = item.optDouble("x").toFloat(),
                                y = item.optDouble("y").toFloat(),
                                sizePt = item.optDouble("sizePt", 18.0).toFloat(),
                                colorArgb = item.optInt("colorArgb")
                            )
                        )
                    }
                }

                val images = buildList {
                    val array = pageJson.optJSONArray("images") ?: JSONArray()
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        val imageFile = File(dir, item.optString("file"))
                        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath) ?: continue
                        add(
                            PdfImageElement(
                                id = item.optLong("id"),
                                bitmap = bitmap,
                                x = item.optDouble("x").toFloat(),
                                y = item.optDouble("y").toFloat(),
                                width = item.optDouble("width").toFloat(),
                                height = item.optDouble("height").toFloat(),
                                rotationDegrees = item.optDouble("rotationDegrees", 0.0).toFloat()
                            )
                        )
                    }
                }

                add(
                    PdfPageModel(
                        widthPt = pageJson.optInt("widthPt", 595),
                        heightPt = pageJson.optInt("heightPt", 842),
                        background = background,
                        sourcePdfPageIndex = if (pageJson.isNull("sourcePdfPageIndex")) null else pageJson.optInt("sourcePdfPageIndex"),
                        strokes = strokes,
                        texts = texts,
                        images = images
                    )
                )
            }
        }.ifEmpty { listOf(PdfPageModel.blank()) }

        Project(
            id = id,
            name = json.optString("name", "PDF"),
            modifiedAt = json.optLong("modifiedAt", jsonFile.lastModified()),
            pages = pages,
            sourcePdfUri = internalSourceUri(context, id)
        )
    }

    suspend fun saveProject(
        context: Context,
        id: String,
        name: String,
        pages: List<PdfPageModel>,
        sourcePdfUri: Uri?
    ): Project = saveMutex.withLock {
        withContext(Dispatchers.IO + NonCancellable) {
        val dir = directory(context, id)
        val source = sourceFile(context, id)
        val expectedInternalUri = runCatching {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", source)
        }.getOrNull()

        if (sourcePdfUri != null && sourcePdfUri != expectedInternalUri) {
            val temp = File(dir, "$SOURCE_PDF.tmp")
            context.contentResolver.openInputStream(sourcePdfUri)?.use { input ->
                FileOutputStream(temp).use { output -> input.copyTo(output) }
            } ?: error("No se pudo copiar el PDF base")
            if (source.exists()) source.delete()
            if (!temp.renameTo(source)) {
                temp.copyTo(source, overwrite = true)
                temp.delete()
            }
        }

        val imagesDir = File(dir, "images").apply { mkdirs() }
        val backgroundsDir = File(dir, "backgrounds").apply { mkdirs() }
        val referencedImages = mutableSetOf<String>()
        val referencedBackgrounds = mutableSetOf<String>()

        val pagesJson = JSONArray()
        pages.forEachIndexed { pageIndex, page ->
            val pageJson = JSONObject()
                .put("widthPt", page.widthPt)
                .put("heightPt", page.heightPt)
                .put("sourcePdfPageIndex", page.sourcePdfPageIndex ?: JSONObject.NULL)

            if (page.sourcePdfPageIndex == null && page.background != null) {
                val file = File(backgroundsDir, "page_$pageIndex.png")
                FileOutputStream(file).use { page.background.compress(Bitmap.CompressFormat.PNG, 100, it) }
                referencedBackgrounds += file.name
                pageJson.put("backgroundFile", "backgrounds/${file.name}")
            }

            val strokesJson = JSONArray()
            page.strokes.forEach { stroke ->
                val points = JSONArray()
                stroke.points.forEach { point ->
                    points.put(JSONObject().put("x", point.x.toDouble()).put("y", point.y.toDouble()))
                }
                strokesJson.put(
                    JSONObject()
                        .put("id", stroke.id)
                        .put("colorArgb", stroke.colorArgb)
                        .put("widthPt", stroke.widthPt.toDouble())
                        .put("points", points)
                )
            }
            pageJson.put("strokes", strokesJson)

            val textsJson = JSONArray()
            page.texts.forEach { text ->
                textsJson.put(
                    JSONObject()
                        .put("id", text.id)
                        .put("text", text.text)
                        .put("x", text.x.toDouble())
                        .put("y", text.y.toDouble())
                        .put("sizePt", text.sizePt.toDouble())
                        .put("colorArgb", text.colorArgb)
                )
            }
            pageJson.put("texts", textsJson)

            val imagesJson = JSONArray()
            page.images.forEach { image ->
                val file = File(imagesDir, "p${pageIndex}_${image.id}.png")
                FileOutputStream(file).use { image.bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                referencedImages += file.name
                imagesJson.put(
                    JSONObject()
                        .put("id", image.id)
                        .put("file", "images/${file.name}")
                        .put("x", image.x.toDouble())
                        .put("y", image.y.toDouble())
                        .put("width", image.width.toDouble())
                        .put("height", image.height.toDouble())
                        .put("rotationDegrees", image.rotationDegrees.toDouble())
                )
            }
            pageJson.put("images", imagesJson)
            pagesJson.put(pageJson)
        }

        val modifiedAt = System.currentTimeMillis()
        val json = JSONObject()
            .put("schemaVersion", SCHEMA_VERSION)
            .put("id", id)
            .put("name", name)
            .put("modifiedAt", modifiedAt)
            .put("pages", pagesJson)
        val jsonTarget = File(dir, PROJECT_JSON)
        val jsonTemp = File(dir, "$PROJECT_JSON.tmp")
        jsonTemp.writeText(json.toString(2))
        if (jsonTarget.exists()) jsonTarget.delete()
        if (!jsonTemp.renameTo(jsonTarget)) {
            jsonTemp.copyTo(jsonTarget, overwrite = true)
            jsonTemp.delete()
        }

        // Elimina recursos obsoletos sólo después de que project.json quedó escrito.
        imagesDir.listFiles().orEmpty().filter { it.name !in referencedImages }.forEach { it.delete() }
        backgroundsDir.listFiles().orEmpty().filter { it.name !in referencedBackgrounds }.forEach { it.delete() }

        // Mantiene una miniatura real de la primera página para la biblioteca.
        // Se actualiza junto con el proyecto y evita rasterizar PDFs completos al abrir Mis PDF.
        pages.firstOrNull()?.let { firstPage ->
            runCatching {
                val preview = PdfDocumentEngine.renderProjectThumbnail(
                    context = context,
                    page = firstPage,
                    sourcePdfUri = internalSourceUri(context, id),
                    targetWidthPx = 260
                )
                FileOutputStream(File(dir, THUMBNAIL_FILE)).use { output ->
                    preview.compress(Bitmap.CompressFormat.PNG, 92, output)
                }
                thumbnailCache.put("$id:$modifiedAt:260", preview)
            }
        }

        // El proyecto editable (PDF base + capas) es la copia maestra dentro de MyNotes.
        // El PDF plano sólo se genera cuando el usuario pulsa Exportar.

        Project(id, name, modifiedAt, pages, internalSourceUri(context, id))
        }
    }


    /**
     * Relaciona un PDF exportado con su proyecto editable.
     *
     * El PDF final es visualmente plano por compatibilidad con cualquier lector, pero
     * MyNotes conserva el vínculo mediante el URI y una huella SHA-256. Si el usuario
     * vuelve a abrir exactamente ese archivo, PDF Studio recupera las capas editables
     * (imágenes, textos, trazos y figuras) desde el proyecto en lugar de tratarlas como
     * píxeles permanentes del fondo.
     */
    suspend fun registerExportedPdf(context: Context, uri: Uri, projectId: String) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(EXPORT_LINK_PREFS, Context.MODE_PRIVATE)
        val hash = runCatching { sha256(context, uri) }.getOrNull()
        val editor = prefs.edit().putString("uri:${uri}", projectId)
        if (!hash.isNullOrBlank()) editor.putString("sha256:$hash", projectId)
        editor.apply()
    }

    /** Devuelve el proyecto editable asociado a un PDF exportado por MyNotes, si existe. */
    suspend fun loadProjectForExportedPdf(context: Context, uri: Uri): Project? = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(EXPORT_LINK_PREFS, Context.MODE_PRIVATE)
        val uriKey = "uri:${uri}"
        var projectId = prefs.getString(uriKey, null)

        if (projectId == null) {
            val hash = runCatching { sha256(context, uri) }.getOrNull()
            if (!hash.isNullOrBlank()) projectId = prefs.getString("sha256:$hash", null)
        }

        val id = projectId ?: return@withContext null
        val projectDir = File(root(context), id)
        if (!File(projectDir, PROJECT_JSON).exists()) {
            // Evita conservar vínculos rotos si el proyecto original fue eliminado.
            prefs.edit().remove(uriKey).apply()
            return@withContext null
        }
        runCatching { loadProject(context, id) }.getOrNull()
    }

    private fun sha256(context: Context, uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-256")
        context.contentResolver.openInputStream(uri)?.use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val count = input.read(buffer)
                if (count <= 0) break
                digest.update(buffer, 0, count)
            }
        } ?: error("No se pudo leer el PDF exportado")
        return digest.digest().joinToString("") { "%02x".format(it.toInt() and 0xFF) }
    }


    suspend fun renameProject(context: Context, id: String, newName: String): Summary = withContext(Dispatchers.IO + NonCancellable) {
        val dir = directory(context, id)
        val file = File(dir, PROJECT_JSON)
        require(file.exists()) { "Proyecto PDF inexistente" }
        val json = JSONObject(file.readText())
        val cleanName = newName.trim().ifBlank { json.optString("name", "PDF") }
        val modifiedAt = System.currentTimeMillis()
        json.put("name", cleanName)
        json.put("modifiedAt", modifiedAt)
        val temp = File(dir, "$PROJECT_JSON.tmp")
        temp.writeText(json.toString(2))
        if (file.exists()) file.delete()
        if (!temp.renameTo(file)) {
            temp.copyTo(file, overwrite = true)
            temp.delete()
        }
        Summary(id, cleanName, modifiedAt, json.optJSONArray("pages")?.length() ?: 1)
    }

    suspend fun duplicateProject(context: Context, id: String, copyName: String): Summary = withContext(Dispatchers.IO + NonCancellable) {
        val sourceDir = File(root(context), id)
        require(sourceDir.exists()) { "Proyecto PDF inexistente" }
        val newId = newProjectId()
        val targetDir = File(root(context), newId)
        sourceDir.copyRecursively(targetDir, overwrite = true)
        val jsonFile = File(targetDir, PROJECT_JSON)
        val json = JSONObject(jsonFile.readText())
        val modifiedAt = System.currentTimeMillis()
        json.put("id", newId)
        json.put("name", copyName)
        json.put("modifiedAt", modifiedAt)
        jsonFile.writeText(json.toString(2))
        Summary(newId, copyName, modifiedAt, json.optJSONArray("pages")?.length() ?: 1)
    }

    suspend fun deleteProject(context: Context, id: String) = withContext(Dispatchers.IO) {
        File(root(context), id).deleteRecursively()
    }
}
