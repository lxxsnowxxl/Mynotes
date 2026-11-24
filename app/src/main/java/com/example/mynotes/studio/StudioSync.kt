package com.example.mynotes.studio

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.example.mynotes.data.AppDatabase
import com.example.mynotes.data.Attachment
import com.example.mynotes.util.openUriStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

internal object StudioSync {
 suspend fun copyLocal(context:Context,noteId:Int):Int=withContext(Dispatchers.IO){StudioMutations.lock.withLock{val db=AppDatabase.getDatabase(context)
val note=(db.noteDao().getAllNotesOnce()+db.noteDao().deletedNotes()).first{it.id==noteId&&it.libraryId==PlusProfiles.currentId}
val assets=mutableListOf<Attachment>()
val created=mutableListOf<File>()
var result=0
try{db.withTransaction{result=db.noteDao().insertNote(note.copy(id=0,title=note.title+" · Local copy",isDeleted=false)).toInt()
db.attachmentDao().getAttachmentsOnce(noteId).forEach{item->val file=File(context.filesDir,"attachments/copy_${UUID.randomUUID()}").apply{parentFile!!.mkdirs()}
created+=file
context.openUriStream(Uri.parse(item.uri))?.use{input->file.outputStream().use{input.copyTo(it)}}?:error("Cannot copy attachment")
assets+=item.copy(id=0,noteId=result,uri=Uri.fromFile(file).toString())}
db.attachmentDao().insertAttachments(assets)
db.noteDao().updateCover(result,assets.firstOrNull{it.type=="cover"}?.uri)}
val store=StudioStore.get(context)
store.changed(result,"local_copy")
val old=store.state.value.get("notes",noteId.toString())
store.edit("notes",result.toString()){it.put("properties",old.optJSONObject("properties")?:JSONObject()).put("stage",old.optString("stage","pending")).put("tags",old.optJSONArray("tags")?:JSONArray())}
result}catch(error:Throwable){created.forEach{it.delete()}
throw error}}}
}
