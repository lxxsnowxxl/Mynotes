package com.example.mynotes.studio

import android.content.Context
import android.net.Uri
import android.util.AtomicFile
import com.example.mynotes.data.*
import com.example.mynotes.util.openUriStream
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.*
import java.io.File
import java.security.MessageDigest
import java.util.UUID

internal object StudioHistory{
 private val lock=Mutex()
 private fun root(context:Context)=File(context.filesDir,"studio_history").apply{mkdirs()}
 private fun read(file:File)=runCatching{require(maxOf(file.length(),File(file.path+".bak").length())<=16*1024*1024)
JSONObject(AtomicFile(file).openRead().bufferedReader().use{it.readText()})}.getOrNull()
 suspend fun capture(context:Context,note:Note,attachments:List<Attachment>)=withContext(Dispatchers.IO){com.example.mynotes.studio.StudioFiles.write {lock.withLock{
  val directory=root(context)
val objects=File(directory,"objects").apply{mkdirs()}
val file=File(directory,"${note.id}.json")
val old=read(file)?.rows("versions").orEmpty()
val assets=JSONArray()
  for(asset in attachments){val known=old.asReversed().flatMap{it.rows("attachments")}.firstOrNull{it.optString("source")==asset.uri}?.optString("hash")?.takeIf{File(objects,it).isFile}
   val hash=known?:run{val temp=File(objects,"${UUID.randomUUID()}.part")
val digest=MessageDigest.getInstance("SHA-256")
try{context.openUriStream(Uri.parse(asset.uri))?.use{input->temp.outputStream().use{output->val buffer=ByteArray(65536)
while(true){val n=input.read(buffer)
if(n<0)break
digest.update(buffer,0,n)
output.write(buffer,0,n)}}}?:error("Cannot preserve attachment history")
val sum=digest.digest().joinToString(""){"%02x".format(it)}
val target=File(objects,sum)
if(target.exists())temp.delete()else check(temp.renameTo(target))
sum}finally{temp.delete()}}
   assets.put(JSONObject().put("hash",hash).put("source",asset.uri).put("type",asset.type).put("name",asset.name?:JSONObject.NULL))
  }
  val value=JSONObject().put("title",note.title).put("content",note.content).put("color",note.color).put("formatted",note.formattingEnabled).put("folder",note.folderId?:JSONObject.NULL).put("icon",note.coverIcon?:JSONObject.NULL).put("coverX",note.coverX).put("coverY",note.coverY).put("attachments",assets)
  if(old.lastOrNull()?.let{JSONObject(it.toString()).apply{remove("at")}.toString()==value.toString()}==true)return@withLock
  value.put("at",System.currentTimeMillis())
val bytes=JSONObject().put("versions",JSONArray((old+value).takeLast(20))).toString().toByteArray()
require(bytes.size<=16*1024*1024)
val atomic=AtomicFile(file)
val output=atomic.startWrite()
try{output.write(bytes)
atomic.finishWrite(output)}catch(error:Throwable){atomic.failWrite(output)
throw error}
collect(context)
 }}}
 suspend fun list(context:Context,id:Int)=withContext(Dispatchers.IO){lock.withLock{read(File(root(context),"$id.json"))?.rows("versions").orEmpty().asReversed()}}
 fun assets(context:Context,version:JSONObject)=version.rows("attachments").map{item->val hash=item.getString("hash")
require(hash.matches(Regex("[a-f0-9]{64}")))
val file=File(root(context),"objects/$hash")
check(file.isFile)
PendingAttachment(Uri.fromFile(file),item.getString("type"),item.optString("name").takeUnless{it=="null"})}
 suspend fun remove(context:Context,ids:List<Int>)=withContext(Dispatchers.IO){lock.withLock{ids.forEach{AtomicFile(File(root(context),"$it.json")).delete()}
collect(context)}}
 private fun collect(context:Context){val directory=root(context)
val used=directory.listFiles().orEmpty().filter{it.extension=="json"}.flatMap{read(it)?.rows("versions").orEmpty().flatMap{version->version.rows("attachments").map{asset->asset.optString("hash")}}}.toSet()
File(directory,"objects").listFiles().orEmpty().filter{it.name !in used&&it.extension!="part"}.forEach{it.delete()}}
}
