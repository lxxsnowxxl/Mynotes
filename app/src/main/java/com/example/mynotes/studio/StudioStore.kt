package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer

import android.content.Context
import android.util.AtomicFile
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.mynotes.ui.components.AppAlertDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.io.File
import java.util.UUID

internal fun JSONObject.rows(name:String):List<JSONObject> = optJSONArray(name)?.let{a->(0 until a.length()).mapNotNull{a.optJSONObject(it)}}.orEmpty()
internal fun JSONObject.strings(name:String):List<String> = optJSONArray(name)?.let{a->(0 until a.length()).map{a.optString(it)}}.orEmpty()
internal data class StudioState(val tables:Map<String,Map<String,String>> = emptyMap(),val library:String=PlusProfiles.currentId){
 fun get(table:String,id:String)=tables[PlusProfiles.table(table,library)]?.get(id)?.let(::JSONObject)?:JSONObject().put("id",id)
 fun all(table:String)=tables[PlusProfiles.table(table,library)].orEmpty().values.map(::JSONObject)
}
internal class StudioStore private constructor(private val context:Context){
 private val lock=Mutex()
private var loaded=false
private val mutable=MutableStateFlow(StudioState())
val state=mutable.asStateFlow()
 private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
 private val queue=kotlinx.coroutines.channels.Channel<suspend()->Unit>(kotlinx.coroutines.channels.Channel.UNLIMITED)
 init{scope.launch{for(command in queue)runCatching{command()}.onFailure{StudioJobs.failure("Save",it)}}}
 private val file get()=File(context.filesDir,"studio/state.json").apply{parentFile!!.mkdirs()}
 private fun read():StudioState{if(!file.exists()&&!File(file.path+".bak").exists())return StudioState()
require(maxOf(file.length(),File(file.path+".bak").length())<=32*1024*1024)
val data=JSONObject(AtomicFile(file).openRead().bufferedReader().use{it.readText()})
require(data.optInt("version")==1)
val tables=data.getJSONObject("tables")
return StudioState(tables.keys().asSequence().associateWith{key->val values=tables.getJSONObject(key)
values.keys().asSequence().associateWith{values.getJSONObject(it).toString()}})}
 suspend fun load()=withContext(Dispatchers.IO){lock.withLock{if(!loaded){mutable.value=read()
loaded=true}}}
 suspend fun edit(logicalTable:String,id:String,change:(JSONObject)->Unit)=editCaptured(logicalTable,id,PlusProfiles.currentId,change)
 internal suspend fun editInLibrary(table:String,id:String,library:String,change:(JSONObject)->Unit){require(library.matches(Regex("[a-zA-Z0-9-]{1,80}")));editCaptured(table,id,library,change)}
 private suspend fun editCaptured(logicalTable:String,id:String,library:String,change:(JSONObject)->Unit){val table=PlusProfiles.table(logicalTable,library)
withContext(Dispatchers.IO+NonCancellable){lock.withLock{if(!loaded){mutable.value=read()
loaded=true}
require(id.length in 1..180)
val current=mutable.value
val row=current.tables[table]?.get(id)?.let(::JSONObject)?:JSONObject().put("id",id)
change(row)
row.put("id",id)
val encoded=row.toString()
if(current.tables[table]?.get(id)==encoded)return@withLock
write(current.copy(tables=current.tables+(table to (current.tables[table].orEmpty()+(id to encoded)))))}}
 }
 suspend fun remove(logicalTable:String,id:String)=removeCaptured(logicalTable,id,PlusProfiles.currentId)
 internal suspend fun removeInLibrary(table:String,id:String,library:String){require(library.matches(Regex("[a-zA-Z0-9-]{1,80}")));removeCaptured(table,id,library)}
 private suspend fun removeCaptured(logicalTable:String,id:String,library:String){val table=PlusProfiles.table(logicalTable,library)
withContext(Dispatchers.IO+NonCancellable){lock.withLock{if(!loaded){mutable.value=read()
loaded=true}
val current=mutable.value
if(id !in current.tables[table].orEmpty())return@withLock
write(current.copy(tables=current.tables+(table to (current.tables[table].orEmpty()-id))))}}
 }
 suspend fun replace(value:StudioState)=withContext(Dispatchers.IO+NonCancellable){lock.withLock{write(value)
loaded=true}}
 fun refreshScope(id:String){mutable.update{it.copy(library=id)}}
 private fun write(value:StudioState){val bytes=encode(value).toByteArray()
require(bytes.size<=32*1024*1024){"Workspace metadata limit reached"}
val atomic=AtomicFile(file)
val output=atomic.startWrite()
val widgetChange=(value.tables.keys+mutable.value.tables.keys).any{it.substringAfterLast(':') in setOf("tasks","sync_status")&&value.tables[it]!=mutable.value.tables[it]}
try{output.write(bytes)
atomic.finishWrite(output)
mutable.update{latest->value.copy(library=latest.library)}
PlusSaveEvents.saved()
if(widgetChange)com.example.mynotes.widget.MyNotesWidgetUpdater.requestUpdate(context)
}catch(error:Throwable){atomic.failWrite(output)
throw error}}
 fun putInLibrary(table:String,id:String,library:String,change:(JSONObject)->Unit){check(queue.trySend{editCaptured(table,id,library,change)}.isSuccess)}
 fun put(table:String,id:String,change:(JSONObject)->Unit){val library=PlusProfiles.currentId;check(queue.trySend{editCaptured(table,id,library,change)}.isSuccess)}
 fun delete(table:String,id:String){val library=PlusProfiles.currentId;check(queue.trySend{removeCaptured(table,id,library)}.isSuccess)}
 suspend fun flush(){val done=CompletableDeferred<Unit>()
queue.send{done.complete(Unit)}
done.await()}
 suspend fun changed(id:Int,action:String){
  val library=PlusProfiles.currentId
  withContext(Dispatchers.IO+NonCancellable){lock.withLock{
   if(!loaded){mutable.value=read();loaded=true}
   val current=mutable.value
   val notesTable=PlusProfiles.table("notes",library)
   val eventsTable=PlusProfiles.table("events",library)
   val noteId=id.toString()
   val note=current.tables[notesTable]?.get(noteId)?.let(::JSONObject)?:JSONObject().put("id",noteId)
   if(note.optString("uuid").isBlank())note.put("uuid",UUID.randomUUID())
   val now=System.currentTimeMillis()
   note.put("modified",now).put("dirty",true)
   val eventId=UUID.randomUUID().toString()
   val event=JSONObject().put("id",eventId).put("noteId",id).put("action",action).put("at",now)
   val events=current.tables[eventsTable].orEmpty()+(eventId to event.toString())
   // Bound the journal and commit both rows in one atomic write, away from the UI thread.
   val retained=if(events.size<=3000)events else events.entries
    .sortedByDescending{JSONObject(it.value).optLong("at")}.take(3000).associate{it.toPair()}
   write(current.copy(tables=current.tables+
    (notesTable to (current.tables[notesTable].orEmpty()+(noteId to note.toString())))+
    (eventsTable to retained)))
  }}
 }
 companion object{
  @Volatile private var instance:StudioStore?=null
  fun get(context:Context):StudioStore{PlusProfiles.initialize(context);return instance?:synchronized(this){instance?:StudioStore(context.applicationContext?:context).also{instance=it}}}
  fun encode(state:StudioState)=JSONObject().put("version",1).put("tables",JSONObject().apply{state.tables.forEach{(table,rows)->put(table,JSONObject().apply{rows.forEach{(id,value)->put(id,JSONObject(value))}})}}).toString()
  fun decode(raw:String):StudioState{val tables=JSONObject(raw).getJSONObject("tables")
return StudioState(tables.keys().asSequence().associateWith{key->val rows=tables.getJSONObject(key)
rows.keys().asSequence().associateWith{rows.getJSONObject(it).toString()}})}
 }
}
internal object StudioMutations{val lock=Mutex()}
internal data class StudioJob(val id:String,val title:String,val status:String="running",val progress:Float?=null,val message:String="")
internal object StudioJobs{
 private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
private val mutable=MutableStateFlow<List<StudioJob>>(emptyList())
val state=mutable.asStateFlow()
 private val running=java.util.concurrent.ConcurrentHashMap<String,Job>()
 fun start(title:String,task:suspend(String)->Unit):String{val id=UUID.randomUUID().toString()
mutable.update{(listOf(StudioJob(id,title))+it).take(30)}
  val job=scope.launch(start=CoroutineStart.LAZY){try{task(id)
update(id,"done",1f,"")}catch(cancelled:CancellationException){update(id,"cancelled",null,"")
throw cancelled}catch(error:Throwable){update(id,"error",null,error.message.orEmpty().take(240))}finally{running.remove(id)}}
running[id]=job
job.start()
return id}
 fun progress(id:String,value:Float?,message:String="")=update(id,"running",value?.coerceIn(0f,1f),message)
 private fun update(id:String,status:String,progress:Float?,message:String){mutable.update{rows->rows.map{if(it.id==id)it.copy(status=status,progress=progress,message=message)else it}}}
 fun cancel(id:String){running[id]?.cancel()}
 fun failure(title:String,error:Throwable){mutable.update{(listOf(StudioJob(UUID.randomUUID().toString(),title,"error",message=error.message.orEmpty().take(240)))+it).take(30)}}
}
@Composable internal fun StudioForm(title:String,name:String="",value:String?=null,onClose:()->Unit,onInteraction:()->Unit={},onSave:(String,String)->Unit){
 var heading by remember{mutableStateOf(name)}
var text by remember{mutableStateOf(value.orEmpty())}
 AppAlertDialog(onDismissRequest=onClose,title={Text(title)},text={Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).pointerInput(Unit){awaitPointerEventScope{while(true){awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial);onInteraction()}}},verticalArrangement=Arrangement.spacedBy(10.dp)){OutlinedTextField(heading,{heading=it.take(160);onInteraction()},label={Text("Name")},singleLine=true)
if(value!=null)OutlinedTextField(text,{text=it.take(200000);onInteraction()},label={Text("Content")},minLines=2,maxLines=9)}},confirmButton={TextButton(onClick={onSave(heading.trim(),text); UiSoundPlayer.playFallbackClick()},enabled=heading.isNotBlank()||text.isNotBlank()){Text("Save")}},dismissButton={TextButton(onClick = { onClose(); UiSoundPlayer.playFallbackClick() }){Text("Cancel")}})
}
