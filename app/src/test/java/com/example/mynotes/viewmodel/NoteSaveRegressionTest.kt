package com.example.mynotes.viewmodel

import android.app.Application
import android.net.Uri
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.mynotes.data.AppDatabase
import com.example.mynotes.data.Note
import com.example.mynotes.data.PendingAttachment
import com.example.mynotes.data.NoteDraft
import com.example.mynotes.data.workspace.NoteWorkspaceStore
import com.example.mynotes.studio.StudioJobs
import com.example.mynotes.studio.StudioState
import com.example.mynotes.studio.StudioStore
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[28])
@LooperMode(LooperMode.Mode.PAUSED)
@OptIn(ExperimentalCoroutinesApi::class)
class NoteSaveRegressionTest {
    private lateinit var app:Application
    private lateinit var db:AppDatabase
    private lateinit var studio:StudioStore
    private val models=ViewModelStore()
    private val mainDispatcher=StandardTestDispatcher()
    private fun singleton(type:Class<*>,name:String)=type.getDeclaredField(name).apply{isAccessible=true}

    @Before fun setup() {
        Dispatchers.setMain(mainDispatcher)
        app=RuntimeEnvironment.getApplication()
        val oldStore=singleton(StudioStore::class.java,"instance").get(null)
        if(oldStore!=null){
            runBlocking{(oldStore as StudioStore).flush()}
            (singleton(StudioStore::class.java,"scope").get(oldStore) as CoroutineScope).cancel()
        }
        singleton(StudioStore::class.java,"instance").set(null,null)
        (singleton(AppDatabase::class.java,"INSTANCE").get(null) as? AppDatabase)?.close()
        singleton(AppDatabase::class.java,"INSTANCE").set(null,null)
        app.deleteDatabase("notes_database")
        File(app.filesDir,"attachments").deleteRecursively()
        studio=StudioStore.get(app)
        runBlocking{studio.replace(StudioState())}
        NoteViewModel.cleanupStarted.set(false)
        NoteViewModel.clearLibraryUndo()
        db=AppDatabase.getDatabase(app)
    }
    private fun model():NoteViewModel = ViewModelProvider(models,object:ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T:ViewModel> create(modelClass:Class<T>):T=NoteViewModel(app) as T
    })[NoteViewModel::class.java]
    private fun waitUntil(condition:()->Boolean) {
        repeat(1000){mainDispatcher.scheduler.runCurrent();shadowOf(Looper.getMainLooper()).idle();if(condition())return;Thread.sleep(10)}
        fail("The background note operation did not finish")
    }
    private fun originalFile()=File(app.cacheDir,"original.txt").apply{writeText("Keep original bytes")}
    private fun pending(file:File)=PendingAttachment(type="file",uri=Uri.fromFile(file),name=file.name,mimeType="text/plain")
    private fun noteErrors()=StudioJobs.state.value.filter{it.title=="Notes"&&it.status=="error"}.map{it.id}.toSet()

    @After fun cleanup(){
        models.clear()
        mainDispatcher.scheduler.runCurrent()
        runBlocking{studio.flush()}
        db.close()
        singleton(AppDatabase::class.java,"INSTANCE").set(null,null)
        Dispatchers.resetMain()
    }
    @Test fun unreadableAttachmentCannotLeaveNotesOrDuplicatesOnRetry() {
        val vm=model();val source=originalFile();val missing=File(app.cacheDir,"unavailable.txt")
        val saved=AtomicBoolean(false)
        repeat(2){
            val before=noteErrors()
            vm.addNote("Draft","Keep this text",attachments=listOf(pending(source),pending(missing)),onSaved={saved.set(true)})
            waitUntil{(noteErrors()-before).isNotEmpty()}
            assertTrue(runBlocking{db.noteDao().getAllNotesOnce()}.isEmpty())
            assertFalse(saved.get());assertEquals("Keep original bytes",source.readText())
            assertTrue(File(app.filesDir,"attachments").listFiles().orEmpty().isEmpty())
        }
    }
    @Test fun failedEditKeepsTheExistingNoteAndAttachments() {
        val vm=model();val saved=AtomicBoolean(false)
        vm.addNote("Original","Original content",color="blue",onSaved={saved.set(true)})
        waitUntil{saved.get()}
        val note=runBlocking{db.noteDao().getAllNotesOnce().single()};val before=noteErrors();saved.set(false)
        vm.updateNote(note,"Changed","Changed content",newAttachments=listOf(pending(originalFile()),pending(File(app.cacheDir,"missing.txt"))),onSaved={saved.set(true)})
        waitUntil{(noteErrors()-before).isNotEmpty()}
        val current=runBlocking{db.noteDao().getNoteByIdOnce(note.id)}!!
        assertEquals("Original",current.title);assertEquals("Original content",current.content)
        assertEquals("blue",current.color);assertFalse(saved.get())
        assertTrue(runBlocking{db.attachmentDao().getAttachmentsOnce(note.id)}.isEmpty())
    }
    @Test fun successfulSaveCommitsOneNoteAndItsAttachment() {
        val vm=model();val saved=AtomicBoolean(false);val source=originalFile()
        vm.addNote("Original","Content",attachments=listOf(pending(source)),onSaved={saved.set(true)})
        waitUntil{saved.get()}
        val note=runBlocking{db.noteDao().getAllNotesOnce().single()}
        val attachment=runBlocking{db.attachmentDao().getAttachmentsOnce(note.id).single()}
        assertEquals(note.id,attachment.noteId);assertEquals("file",attachment.type)
        assertEquals("Keep original bytes",File(Uri.parse(attachment.uri).path!!).readText())
        assertEquals("Keep original bytes",source.readText())
    }
    @Test fun linkOnlyDraftCommitsWithoutATitleOrVisibleBody() {
        val url="https://example.com/link-only?source=paste"
        val content=com.example.mynotes.ui.components.noteContentForStorage("",listOf(url))
        val draft=NoteDraft("",content,"default",emptyList(),emptyList())
        val vm=model();val saved=AtomicBoolean(false)
        vm.saveDraft(null,draft){saved.set(true)}
        waitUntil{saved.get()}
        val note=runBlocking{db.noteDao().getAllNotesOnce().single()}
        assertTrue(com.example.mynotes.ui.components.stripEmbeddedLinkMetadata(note.content).isBlank())
        assertEquals(listOf(url),com.example.mynotes.ui.components.extractEmbeddedLinkUrls(note.content))
    }
    @Test fun startupRetentionUsesEachLibrarySettingsAndMetadata() {
        val now=System.currentTimeMillis();val old=now-2*86_400_000L
        val work=runBlocking{db.noteDao().insertNote(Note(title="Work trash",content="",isDeleted=true,libraryId="work"))}.toInt()
        val personal=runBlocking{db.noteDao().insertNote(Note(title="Personal trash",content="",isDeleted=true,libraryId="default"))}.toInt()
        runBlocking{
            studio.editInLibrary("preferences","workspace","work"){it.put("trashDays",1)}
            studio.editInLibrary("preferences","workspace","default"){it.put("trashDays",90)}
            studio.editInLibrary("trash",work.toString(),"work"){it.put("at",old)}
            studio.editInLibrary("trash",personal.toString(),"default"){it.put("at",old)}
        }
        model()
        waitUntil{runBlocking{db.noteDao().deletedNotes()}.none{it.id==work}&&
            !studio.state.value.copy(library="work").get("trash",work.toString()).has("at")}
        assertTrue(runBlocking{db.noteDao().deletedNotes()}.any{it.id==personal})
        assertFalse(studio.state.value.copy(library="work").get("trash",work.toString()).has("at"))
        assertFalse(studio.state.value.copy(library="default").get("trash",work.toString()).has("at"))
    }
    @Test fun viewerCannotPermanentlyDeleteSharedContent() {
        val id=runBlocking{db.noteDao().insertNote(Note(title="Shared",content="Read only",isDeleted=true))}.toInt()
        runBlocking{studio.edit("notes",id.toString()){it.put("remoteRole","viewer").put("remoteBook","shared").put("dirty",false)}}
        val before=StudioJobs.state.value.map{it.id}.toSet()
        model().purgeTrash(setOf(id))
        waitUntil{StudioJobs.state.value.any{it.id !in before&&it.title=="Trash"&&it.status=="error"}}
        assertTrue(runBlocking{db.noteDao().deletedNotes()}.any{it.id==id})
    }
    @Test fun newWorkspaceDraftsAreIndependentAndLegacyKeysStillWork() {
        val a=NoteWorkspaceStore.key(null,"tab-a");val b=NoteWorkspaceStore.key(null,"tab-b")
        assertNotEquals(a,b);assertEquals("note_7",NoteWorkspaceStore.key(7,"tab-a"))
        assertEquals("new_note",NoteWorkspaceStore.key(null))
        NoteWorkspaceStore.begin(a,"session-a");NoteWorkspaceStore.begin(b,"session-b")
        runBlocking{
            NoteWorkspaceStore.save(app,a,"session-a",NoteDraft("First","First draft","default",emptyList(),emptyList()))
            NoteWorkspaceStore.save(app,b,"session-b",NoteDraft("Second","Second draft","default",emptyList(),emptyList()))
            assertEquals("First",NoteWorkspaceStore.load(app,a)!!.draft.title)
            assertEquals("Second",NoteWorkspaceStore.load(app,b)!!.draft.title)
        }
        NoteWorkspaceStore.discard(app,a,"session-a")
        waitUntil{runBlocking{NoteWorkspaceStore.load(app,a)}==null}
        assertEquals("Second",runBlocking{NoteWorkspaceStore.load(app,b)}!!.draft.title)
    }
    @Test fun recoveryKeepsLatestTextWhenAnAttachmentIsUnavailable() {
        val key=NoteWorkspaceStore.key(null,"unavailable-file");val session="unavailable-session"
        val missing=pending(File(app.cacheDir,"unavailable.txt"))
        NoteWorkspaceStore.begin(key,session)
        val result=runCatching{runBlocking{NoteWorkspaceStore.save(app,key,session,NoteDraft("Latest title","Latest text","default",attachments=listOf(missing),removedAttachments=emptyList()))}}
        assertTrue(result.isFailure) // The UI gets a warning while the recoverable text is retained.
        val recovered=runBlocking{NoteWorkspaceStore.load(app,key)}!!
        assertEquals("Latest title",recovered.draft.title);assertEquals("Latest text",recovered.draft.content)
        assertEquals(missing.uri,recovered.draft.attachments.single().uri)
    }
}
