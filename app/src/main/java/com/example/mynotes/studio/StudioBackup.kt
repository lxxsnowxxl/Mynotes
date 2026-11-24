package com.example.mynotes.studio

import android.content.Context
import android.net.Uri
import com.example.mynotes.data.AppDataBackupManager
import com.example.mynotes.reminders.*
import com.example.mynotes.settings.AppSettings
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.*
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import java.util.zip.*

internal object StudioBackups{
 val groups=linkedMapOf("notes" to "Notes, attachments, history and organization","drawings" to "Editable drawings and stamps","pdf" to "PDF projects and signatures","audio" to "Audio and transcripts","vault" to "Encrypted notebooks","settings" to "Preferences and reminders")
 private val roots=linkedMapOf("workspaces/note_drafts" to "notes","workspaces/note_history" to "notes","studio_history" to "notes","workspaces/drawings" to "drawings","workspaces/drawing_exports" to "drawings","pdf_projects" to "pdf","workspaces/signatures" to "pdf","studio_pdf" to "pdf","studio_audio" to "audio","studio_vault" to "vault")
 private val lock=Mutex()
 private fun group(table:String)=when(table.substringAfterLast(':')){"stamps"->"drawings"
"pdf_tags","pdf_ocr","ocr"->"pdf"
"recordings","transcripts"->"audio"
"preferences","reminder_events","pomodoro","locations"->"settings"
else->"notes"}
 data class Preview(val text:String,val categories:Set<String>)
 suspend fun preview(context:Context,source:Uri,selected:Set<String>):Preview=withContext(Dispatchers.IO){
  val stage=File(context.cacheDir,"studio_inspect_${UUID.randomUUID()}").apply{check(mkdirs())}
  try{
   val manifest=extract(context,source,stage)
   val lines=mutableListOf("Backup date: "+java.text.DateFormat.getDateTimeInstance().format(java.util.Date(manifest.optLong("at"))))
   if("notes" in selected){
    java.util.zip.ZipFile(File(stage,"notes.zip")).use{zip->
     require(zip.size()<=20000)
     zip.entries().asSequence().forEach{entry->require(!entry.name.startsWith('/')&&!entry.name.contains('\\')&&entry.name.split('/').none{it==".."||it=="."})}
     val entry=zip.getEntry("data.json")?:error("Missing note data");require(entry.size in 0..64L*1024*1024)
     val data=JSONObject(zip.getInputStream(entry).bufferedReader().use{it.readText()})
     val incoming=com.example.mynotes.data.jsonToNotes(data.getJSONArray("notes"))
     val db=com.example.mynotes.data.AppDatabase.getDatabase(context)
     val current=db.noteDao().getAllNotesOnce()+db.noteDao().deletedNotes()
     val ids=incoming.map{it.id}.toSet();val old=current.map{it.id}.toSet()
     lines+="Notes across all local libraries: ${ids.size} incoming · ${(ids-old).size} added · ${(ids intersect old).size} replaced · ${(old-ids).size} removed"
     lines+="${data.optJSONArray("attachments")?.length()?:0} attachments · ${data.optJSONArray("folders")?.length()?:0} folders"
    }
   }
   selected.forEach{category->val files=manifest.rows("files").filter{it.optString("group")==category};lines+="${groups[category]}: ${files.size} archive files · ${files.sumOf{it.optLong("size")}/1024} KB"}
   lines+="Selected categories replace their current data. A failed restore rolls back the previous snapshot. Server credentials and biometric keys are excluded."
   Preview(lines.joinToString("\n\n"),selected)
  }finally{stage.deleteRecursively()}
 }
 private fun hash(file:File):String{val digest=MessageDigest.getInstance("SHA-256")
file.inputStream().use{input->val b=ByteArray(65536)
while(true){val n=input.read(b)
if(n<0)break
digest.update(b,0,n)}}
return digest.digest().joinToString(""){"%02x".format(it)}}
 private fun safe(root:File,path:String):File{require(path.isNotBlank()&&!path.startsWith('/')&&!path.contains('\\')&&path.split('/').none{it=="."||it==".."})
return File(root,path).also{require(it.canonicalPath.startsWith(root.canonicalPath+File.separator))}}
 private fun allowed(path:String)=roots.keys.any{path.startsWith("$it/")}
 private fun preferences(context:Context):String = JSONObject()
  .put("reminders",JSONArray(context.getSharedPreferences("reminder_prefs",Context.MODE_PRIVATE).getString("reminders_json","[]")))
  .put("workspace",JSONObject(context.getSharedPreferences("workspace_preferences",Context.MODE_PRIVATE).all))
  .toString()
 suspend fun export(context:Context,destination:Uri,settings:AppSettings,job:String)=withContext(Dispatchers.IO){lock.withLock{StudioMutations.lock.withLock{StudioFiles.write{
  check(StudioJobs.state.value.none{it.status=="running"&&it.id!=job}){"Wait for other operations to finish before making a backup"}
delay(500)
val stage=File(context.cacheDir,"studio_backup_${UUID.randomUUID()}").apply{check(mkdirs())}
  try{val store=StudioStore.get(context)
store.flush()
store.load()
val inner=File(stage,"notes.zip")
AppDataBackupManager.exportBackup(context,Uri.fromFile(inner),settings).getOrThrow().also{require(it.skippedAttachmentCount==0){"Some attachments cannot be read; backup not completed"}}
   val stateFile=File(stage,"state.json")
stateFile.writeText(StudioStore.encode(store.state.value))
val reminders=File(stage,"reminders.json")
reminders.writeText(preferences(context))
   val files=mutableListOf(Triple("notes.zip",inner,"notes"),Triple("state.json",stateFile,"state"),Triple("reminders.json",reminders,"settings"))
roots.forEach{(root,group)->File(context.filesDir,root).takeIf{it.isDirectory}?.walkTopDown()?.filter{it.isFile&&it.extension !in listOf("tmp","new","bak","part")}?.forEach{file->files+=Triple("files/"+file.relativeTo(context.filesDir).invariantSeparatorsPath,file,group)}}
   require(files.size<=50000)
val manifest=JSONObject().put("format","mynotes-studio-backup").put("version",1).put("filesDir",context.filesDir.path).put("at",System.currentTimeMillis()).put("files",JSONArray())
val entries=manifest.getJSONArray("files")
   context.contentResolver.openOutputStream(destination,"wt")?.use{output->ZipOutputStream(output.buffered()).use{zip->files.forEachIndexed{i,(name,file,category)->currentCoroutineContext().ensureActive()
entries.put(JSONObject().put("path",name).put("group",category).put("size",file.length()).put("sha256",hash(file)))
zip.putNextEntry(ZipEntry(name))
file.inputStream().use{it.copyTo(zip)}
zip.closeEntry()
StudioJobs.progress(job,(i+1f)/(files.size+1))}
zip.putNextEntry(ZipEntry("manifest.json"))
zip.write(manifest.toString().toByteArray())
zip.closeEntry()}}?:error("Cannot write backup")
  }finally{stage.deleteRecursively()}
 }}}}
 private suspend fun extract(context:Context,source:Uri,stage:File):JSONObject{val seen=mutableSetOf<String>()
var total=0L
var count=0
val limit=minOf(4L*1024*1024*1024,(stage.usableSpace*.65).toLong())
context.contentResolver.openInputStream(source)?.use{input->ZipInputStream(input.buffered()).use{zip->while(true){val entry=zip.nextEntry?:break
currentCoroutineContext().ensureActive()
require(++count<=50001&&seen.add(entry.name))
val name=entry.name
require(name in setOf("manifest.json","notes.zip","state.json","reminders.json")||(name.startsWith("files/")&&allowed(name.removePrefix("files/")))){"Unrecognized backup entry"}
val target=safe(stage,name)
require(!entry.isDirectory)
target.parentFile!!.mkdirs()
target.outputStream().use{out->val b=ByteArray(65536)
var size=0L
while(true){currentCoroutineContext().ensureActive();val n=zip.read(b)
if(n<0)break
total+=n
size+=n
require(total<=limit)
if(name in listOf("manifest.json","state.json","reminders.json"))require(size<=32L*1024*1024)
out.write(b,0,n)}}
zip.closeEntry()}}}?:error("Cannot read backup")
  val manifest=JSONObject(File(stage,"manifest.json").readText())
require(manifest.optString("format")=="mynotes-studio-backup"&&manifest.optInt("version")==1)
require(manifest.optString("filesDir").startsWith('/')&&manifest.optString("filesDir").length<=512)
val entries=manifest.rows("files")
require(entries.size==seen.size-1&&entries.map{it.getString("path")}.toSet()==seen-setOf("manifest.json"))
entries.forEach{row->val file=safe(stage,row.getString("path"))
require(file.length()==row.getLong("size")&&hash(file)==row.getString("sha256")){"Backup checksum failed: ${file.name}"}
val path=row.getString("path")
if(path.startsWith("files/")){val actual=roots.entries.first{path.removePrefix("files/").startsWith(it.key+"/")}.value
require(actual==row.getString("group"))}}
return manifest
 }
 suspend fun restore(context:Context,source:Uri,selected:Set<String>,settings:AppSettings,job:String)=withContext(Dispatchers.IO){lock.withLock{StudioMutations.lock.withLock{StudioFiles.write{
  require(selected.isNotEmpty()&&selected.all{it in groups})
check(StudioJobs.state.value.none{it.status=="running"&&it.id!=job}){"Wait for other operations to finish before restoring"}
delay(500)
val stage=File(context.cacheDir,"studio_restore_${UUID.randomUUID()}").apply{check(mkdirs())}
  try{val manifest=extract(context,source,stage)
val store=StudioStore.get(context)
store.flush()
store.load()
val transaction=File(context.filesDir,"studio_restore_transaction")
check(!transaction.exists()){ "Recover the interrupted restore before starting another" }
check(transaction.mkdirs())
   AppDataBackupManager.exportBackup(context,Uri.fromFile(File(transaction,"notes.zip")),settings).getOrThrow().also{require(it.skippedAttachmentCount==0)}
File(transaction,"state.json").writeText(StudioStore.encode(store.state.value))
File(transaction,"reminders.json").writeText(preferences(context))
   val selectedRoots=roots.filterValues{it in selected}.keys.toList()
File(transaction,"journal.json").writeText(JSONObject().put("roots",JSONArray(selectedRoots)).put("existed",JSONArray(selectedRoots.filter{safe(context.filesDir,it).exists()})).put("notes","notes" in selected).put("settings","settings" in selected).toString())
   try{selectedRoots.forEach{root->val current=safe(context.filesDir,root)
if(current.exists()){val old=safe(File(transaction,"old").apply{mkdirs()},root)
old.parentFile!!.mkdirs()
check(current.renameTo(old)){"Cannot prepare rollback"}}}
    if("notes" in selected||"settings" in selected)AppDataBackupManager.importBackup(context,Uri.fromFile(File(stage,"notes.zip")),"notes" in selected,"settings" in selected).getOrThrow()
    val oldPath=manifest.getString("filesDir")
manifest.rows("files").filter{it.optString("group") in selected&&it.getString("path").startsWith("files/")}.forEachIndexed{i,row->currentCoroutineContext().ensureActive()
val relative=row.getString("path").removePrefix("files/")
val target=safe(context.filesDir,relative)
target.parentFile!!.mkdirs()
val input=safe(stage,row.getString("path"))
if(input.extension=="json")target.writeText(input.readText().replace(oldPath,context.filesDir.path))else input.copyTo(target,true)
StudioJobs.progress(job,.4f+i*.5f/(manifest.rows("files").size.coerceAtLeast(1)))}
    val incoming=StudioStore.decode(File(stage,"state.json").readText().replace(oldPath,context.filesDir.path))
val tables=store.state.value.tables.toMutableMap()
(tables.keys+incoming.tables.keys).filter{it !in setOf("file_owners","profiles")&&group(it) in selected}.forEach{table->tables[table]=incoming.tables[table].orEmpty()}
val owners=tables["file_owners"].orEmpty().toMutableMap()
val incomingOwners=incoming.tables["file_owners"].orEmpty()
fun ownerSelected(key:String)=when(key.substringBefore(':')){"pdf"->"pdf" in selected;"drawing"->"drawings" in selected;"vault"->"vault" in selected;else->"notes" in selected}
(owners.keys+incomingOwners.keys).filter(::ownerSelected).forEach{key->if(key in incomingOwners)owners[key]=incomingOwners.getValue(key)else owners.remove(key)}
tables["file_owners"]=owners
if(selected.any{it!="settings"})tables["profiles"]=store.state.value.tables["profiles"].orEmpty()+incoming.tables["profiles"].orEmpty()
store.replace(StudioState(tables))
    if("settings" in selected)reminders(context,File(stage,"reminders.json").readText())
File(transaction,"committed").writeText("1")
transaction.deleteRecursively()
com.example.mynotes.data.workspace.DrawingProjects.afterRestore()
StudioFiles.restored()
StudioJobs.progress(job,1f)
   }catch(error:Throwable){withContext(NonCancellable){runCatching{rollback(context,transaction)}.onFailure{StudioJobs.failure("Restore recovery",it)}}
throw error}
  }finally{stage.deleteRecursively()}
 }}}}
 private fun reminders(context:Context,raw:String){
val data=if(raw.trimStart().startsWith("["))JSONObject().put("reminders",JSONArray(raw)) else JSONObject(raw)
val restored=data.getJSONArray("reminders").toString()
remindersFromJson(restored)
data.optJSONObject("workspace")?.let{values->
 val editor=context.getSharedPreferences("workspace_preferences",Context.MODE_PRIVATE).edit().clear()
 values.keys().forEach{key->when(val value=values.get(key)){
  is String->editor.putString(key,value)
  is Boolean->editor.putBoolean(key,value)
  is Int->editor.putInt(key,value)
  is Long->editor.putLong(key,value)
  is Number->editor.putFloat(key,value.toFloat())
 }}
 check(editor.commit()) { "Cannot restore workspace preferences" }
}
val repo=ReminderRepository.getInstance(context)
repo.allReminders().forEach{ReminderAlarmScheduler.cancel(context,it.id)}
check(context.getSharedPreferences("reminder_prefs",Context.MODE_PRIVATE).edit().putString("reminders_json",restored).commit())
repo.rescheduleAll()
StudioGeofences.restore(context){}}
 private suspend fun rollback(context:Context,transaction:File){val journal=JSONObject(File(transaction,"journal.json").readText())
AppDataBackupManager.importBackup(context,Uri.fromFile(File(transaction,"notes.zip")),journal.optBoolean("notes"),journal.optBoolean("settings")).getOrThrow()
journal.strings("roots").reversed().forEach{root->require(root in roots)
val current=safe(context.filesDir,root)
val old=safe(File(transaction,"old"),root)
if(old.exists()){current.deleteRecursively()
current.parentFile!!.mkdirs()
check(old.renameTo(current))}else if(root !in journal.strings("existed"))current.deleteRecursively()}
StudioStore.get(context).replace(StudioStore.decode(File(transaction,"state.json").readText()))
if(journal.optBoolean("settings"))reminders(context,File(transaction,"reminders.json").readText())
transaction.deleteRecursively()
com.example.mynotes.data.workspace.DrawingProjects.afterRestore()
StudioFiles.restored()}
 suspend fun recover(context:Context)=withContext(Dispatchers.IO){
  lock.withLock{StudioMutations.lock.withLock{StudioFiles.write{
   // Wait for an active restore before inspecting its transaction directory.
   val transaction=File(context.filesDir,"studio_restore_transaction")
   if(!transaction.exists())return@write
   if(File(transaction,"committed").exists()||!File(transaction,"journal.json").exists()){
    transaction.deleteRecursively()
    return@write
   }
   withContext(NonCancellable){rollback(context,transaction)}
  }}}
 }
}
