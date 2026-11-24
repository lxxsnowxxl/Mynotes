package com.example.mynotes.data.workspace

import android.content.Context
import android.util.AtomicFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class DrawingPoint(val x: Float, val y: Float, val pressure: Float = 1f)
data class DrawingStroke(val id: String = UUID.randomUUID().toString(), val points: List<DrawingPoint>, val colorArgb: Int, val width: Float, val fillArgb:Int?=null, val layer:String="base")
data class DrawingLayer(val id:String="base",val name:String="Base",val visible:Boolean=true,val locked:Boolean=false,val opacity:Float=1f)
data class DrawingDocument(
    val id: String = UUID.randomUUID().toString(), val name: String = "", val width: Int = 1, val height: Int = 1,
    val paperArgb: Int = -1, val pattern: String = "plain", val strokes: List<DrawingStroke> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis(), val infinite:Boolean=false, val rotation:Float=0f, val layers:List<DrawingLayer> = listOf(DrawingLayer()),val activeLayer:String="base"
)

/** Geometry is shared by the live canvas, editable projects and image export. */
object DrawingGeometry {
    fun shape(mode: String, start: DrawingPoint, end: DrawingPoint): List<DrawingPoint> = when (mode) {
        "rectangle" -> listOf(start, DrawingPoint(end.x, start.y), end, DrawingPoint(start.x, end.y), start)
        "ellipse" -> (0..64).map { index ->
            val angle = index * 2.0 * PI / 64
            DrawingPoint((start.x + end.x) / 2f + (end.x - start.x) / 2f * cos(angle).toFloat(),
                (start.y + end.y) / 2f + (end.y - start.y) / 2f * sin(angle).toFloat())
        }
        else -> listOf(start, end)
    }
    fun hit(stroke: DrawingStroke, x: Float, y: Float, tolerance: Float): Boolean {
        val radius = tolerance + stroke.width / 2
        fun near(a: DrawingPoint, b: DrawingPoint): Boolean {
            val dx = b.x - a.x; val dy = b.y - a.y
            val length = dx * dx + dy * dy
            val t = if (length <= 0f) 0f else (((x - a.x) * dx + (y - a.y) * dy) / length).coerceIn(0f, 1f)
            val px = x - a.x - t * dx; val py = y - a.y - t * dy
            return px * px + py * py <= radius * radius
        }
        if (stroke.points.isEmpty()) return false
        if (near(stroke.points.first(), stroke.points.first())) return true
        for (index in 1 until stroke.points.size) if (near(stroke.points[index - 1], stroke.points[index])) return true
        return false
    }
    fun movedWithin(stroke: DrawingStroke, dx: Float, dy: Float, width: Float, height: Float): DrawingStroke {
        if (stroke.points.isEmpty()) return stroke
        val minX = stroke.points.minOf { it.x }; val maxX = stroke.points.maxOf { it.x }
        val minY = stroke.points.minOf { it.y }; val maxY = stroke.points.maxOf { it.y }
        val safeDx = dx.coerceIn(-minX, (width - maxX).coerceAtLeast(-minX))
        val safeDy = dy.coerceIn(-minY, (height - maxY).coerceAtLeast(-minY))
        return moved(stroke, safeDx, safeDy)
    }
    fun moved(stroke: DrawingStroke, dx: Float, dy: Float) = stroke.copy(points = stroke.points.map { it.copy(x = it.x + dx, y = it.y + dy) })
}

data class DrawingProjectSummary(val id: String, val name: String, val updatedAt: Long)

object DrawingProjects {
    private val mutex = Mutex()
    private val deletedIds = mutableSetOf<String>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private fun dir(context: Context) = File(context.filesDir, "workspaces/drawings").apply { mkdirs() }
    private fun file(context: Context, id: String): File {
        require(id.matches(Regex("[a-zA-Z0-9-]{1,80}")))
        return File(dir(context), "$id.json")
    }
    fun active(context: Context): String? = context.getSharedPreferences("workspace_preferences", Context.MODE_PRIVATE).getString("active_drawing_"+com.example.mynotes.studio.PlusProfiles.currentId, null)?:if(com.example.mynotes.studio.PlusProfiles.currentId=="default")context.getSharedPreferences("workspace_preferences",Context.MODE_PRIVATE).getString("active_drawing",null)else null
    fun activate(context: Context, id: String) { context.getSharedPreferences("workspace_preferences", Context.MODE_PRIVATE).edit().putString("active_drawing_"+com.example.mynotes.studio.PlusProfiles.currentId, id).apply() }
    fun finish(context: Context, id: String) {
        if (active(context) == id) context.getSharedPreferences("workspace_preferences", Context.MODE_PRIVATE).edit().remove("active_drawing_"+com.example.mynotes.studio.PlusProfiles.currentId).remove("active_drawing").apply()
    }
    suspend fun load(context: Context, id: String): DrawingDocument? = withContext(Dispatchers.IO) { com.example.mynotes.studio.StudioStore.get(context).load();mutex.withLock { val source=file(context,id);if(!source.exists()&&!File(source.path+".bak").exists())return@withLock null;require(com.example.mynotes.studio.PlusProfiles.ownsFile(context,"drawing",id));read(source) } }
    suspend fun list(context: Context): List<DrawingProjectSummary> = withContext(Dispatchers.IO) { com.example.mynotes.studio.StudioStore.get(context).load();mutex.withLock {
        dir(context).listFiles().orEmpty().filter { it.extension == "json" && com.example.mynotes.studio.PlusProfiles.ownsFile(context,"drawing",it.nameWithoutExtension) }.mapNotNull { file -> readJson(file)?.let { json -> DrawingProjectSummary(json.optString("id"), json.optString("name"), json.optLong("updatedAt")) } }.sortedByDescending { it.updatedAt }
    } }
    suspend fun save(context: Context, document: DrawingDocument) = withContext(Dispatchers.IO + NonCancellable) {com.example.mynotes.studio.StudioFiles.write { mutex.withLock {
        if(document.updatedAt<com.example.mynotes.studio.StudioFiles.restoredAt)throw kotlinx.coroutines.CancellationException("Workspace restored")
        if (document.id in deletedIds) return@withLock
        com.example.mynotes.studio.PlusProfiles.claimFile(context,"drawing",document.id)
        val target = file(context, document.id)
        if ((readJson(target)?.optLong("updatedAt") ?: 0L) > document.updatedAt) return@withLock
        val encoded = encode(document).toString().toByteArray()
        require(encoded.size <= 32 * 1024 * 1024) { "Drawing exceeds the recovery limit" }
        val atomic = AtomicFile(target); val output = atomic.startWrite()
        try { output.write(encoded); atomic.finishWrite(output) }
        catch (error: Exception) { atomic.failWrite(output); throw error }
    } }}
    fun saveOnExit(context: Context, document: DrawingDocument) { scope.launch { runCatching { save(context, document) } } }
    suspend fun delete(context: Context, id: String) = withContext(Dispatchers.IO) {com.example.mynotes.studio.StudioFiles.write { mutex.withLock { deletedIds += id; AtomicFile(file(context, id)).delete(); finish(context, id) } }}
    suspend fun flush(){scope.coroutineContext[kotlinx.coroutines.Job]?.children?.toList()?.forEach{it.join()}}
    suspend fun afterRestore()=mutex.withLock{deletedIds.clear()}
    private fun readJson(file: File): JSONObject? = runCatching {
        val backup = File(file.path + ".bak")
        if ((!file.exists() && !backup.exists()) || maxOf(file.length(), backup.length()) > 32L * 1024 * 1024) return null
        JSONObject(AtomicFile(file).openRead().bufferedReader().use { it.readText() })
    }.getOrNull()
    private fun read(file: File): DrawingDocument? = readJson(file)?.let { runCatching { decode(it) }.getOrNull() }
    internal fun encode(document: DrawingDocument) = JSONObject().apply {
        put("version", 1); put("id", document.id); put("name", document.name); put("width", document.width); put("height", document.height)
        put("paper", document.paperArgb); put("pattern", document.pattern); put("updatedAt", document.updatedAt)
        put("infinite",document.infinite);put("rotation",document.rotation)
        put("activeLayer",document.activeLayer);put("layers",JSONArray(document.layers.map{JSONObject().put("id",it.id).put("name",it.name).put("visible",it.visible).put("locked",it.locked).put("opacity",it.opacity.toDouble())}))
        put("strokes", JSONArray().apply { document.strokes.forEach { stroke -> put(JSONObject().apply {
            put("id", stroke.id);put("layer",stroke.layer); put("color", stroke.colorArgb); put("width", stroke.width);put("fill",stroke.fillArgb?:JSONObject.NULL)
            put("points", JSONArray().apply { stroke.points.forEach { put(JSONArray(listOf(it.x, it.y, it.pressure))) } })
        }) } })
    }
    internal fun decode(json: JSONObject): DrawingDocument {
        require(json.optInt("version") == 1)
        val array = json.getJSONArray("strokes"); require(array.length() <= 20000)
        var totalPoints = 0
        val strokes = (0 until array.length()).map { index -> val stroke = array.getJSONObject(index); val points = stroke.getJSONArray("points")
            totalPoints += points.length(); require(totalPoints <= 1_000_000)
            DrawingStroke(stroke.getString("id"), (0 until points.length()).map { i -> val point = points.getJSONArray(i)
                DrawingPoint(point.getDouble(0).toFloat().also { require(it.isFinite()) }, point.getDouble(1).toFloat().also { require(it.isFinite()) }, point.optDouble(2, 1.0).toFloat().coerceIn(.15f, 2f))
            }, stroke.getInt("color"), stroke.getDouble("width").toFloat().coerceIn(.2f, 200f),if(stroke.isNull("fill"))null else stroke.optInt("fill"),stroke.optString("layer","base"))
        }
        val layerArray=json.optJSONArray("layers")
        val layers=if(layerArray==null)listOf(DrawingLayer())else (0 until layerArray.length().coerceAtMost(30)).map{index->val item=layerArray.getJSONObject(index);DrawingLayer(item.getString("id"),item.optString("name","Layer"),item.optBoolean("visible",true),item.optBoolean("locked"),item.optDouble("opacity",1.0).toFloat().takeIf{it.isFinite()}?.coerceIn(0f,1f)?:1f)}.distinctBy{it.id}.ifEmpty{listOf(DrawingLayer())}
        return DrawingDocument(json.getString("id"), json.optString("name"), json.getInt("width").coerceIn(1, 10000), json.getInt("height").coerceIn(1, 10000), json.getInt("paper"), json.optString("pattern", "plain"), strokes, json.optLong("updatedAt"),json.optBoolean("infinite"),json.optDouble("rotation").toFloat().takeIf{it.isFinite()}?.coerceIn(-180f,180f)?:0f,layers,json.optString("activeLayer","base").takeIf{id->layers.any{it.id==id}}?:layers.first().id)
    }
}
