package com.example.mynotes.studio

import android.content.Context
import android.content.ContextWrapper
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class StudioStoreRegressionTest {
    private lateinit var directory: File
    private lateinit var context: Context
    private val stores = mutableListOf<StudioStore>()

    private fun newStore(): StudioStore = StudioStore::class.java
        .getDeclaredConstructor(Context::class.java).apply { isAccessible = true }
        .newInstance(context).also { stores += it }

    @Before fun setup() {
        val app: android.app.Application = RuntimeEnvironment.getApplication()
        directory = File(app.cacheDir, "metadata-test-${UUID.randomUUID()}").apply { mkdirs() }
        context = object : ContextWrapper(app) {
            override fun getFilesDir(): File = directory
        }
    }

    @After fun cleanup() {
        stores.forEach { store ->
            runBlocking { store.flush() }
            val scope = StudioStore::class.java.getDeclaredField("scope").apply { isAccessible = true }
            (scope.get(store) as CoroutineScope).cancel()
        }
        directory.deleteRecursively()
    }

    @Test fun unchangedEditsAndMissingDeletesDoNotWriteOrPublishSaveEvents() = runBlocking {
        val store = newStore()
        store.edit("preferences", "workspace") { it.put("leftHand", true) }
        val before = store.state.value
        val bytes = File(directory, "studio/state.json").readBytes()
        val saved = PlusSaveEvents.last.value
        try {
            PlusSaveEvents.last.value = -1L
            repeat(12) {
                store.edit("preferences", "workspace") { it.put("leftHand", true) }
                store.remove("preferences", "missing")
            }
            assertSame(before, store.state.value)
            assertEquals(-1L, PlusSaveEvents.last.value)
            assertArrayEquals(bytes, File(directory, "studio/state.json").readBytes())
        } finally {
            PlusSaveEvents.last.value = saved
        }
    }

    @Test fun noteAndBoundedJournalCommitTogetherWithoutChangingOtherLibraries() = runBlocking {
        val store = newStore()
        val library = PlusProfiles.currentId
        val notesTable = PlusProfiles.table("notes", library)
        val eventsTable = PlusProfiles.table("events", library)
        val otherTable = PlusProfiles.table("events", "regression-other-$library")
        val events = (0 until 3010).associate { index ->
            index.toString() to JSONObject().put("id", index.toString()).put("at", index.toLong()).toString()
        }
        val other = mapOf("keep" to JSONObject().put("id", "keep").put("at", 1L).toString())
        val note = JSONObject().put("id", "42").put("uuid", "stable").put("remoteRole", "editor").toString()
        store.replace(StudioState(mapOf(notesTable to mapOf("42" to note), eventsTable to events, otherTable to other)))
        store.changed(42, "updated")
        val state = store.state.value
        val metadata = state.copy(library = library).get("notes", "42")
        assertEquals("stable", metadata.optString("uuid"))
        assertEquals("editor", metadata.optString("remoteRole"))
        assertTrue(metadata.optBoolean("dirty"))
        val journal = state.copy(library = library).all("events")
        assertEquals(3000, journal.size)
        assertEquals(1, journal.count { it.optInt("noteId") == 42 && it.optString("action") == "updated" })
        assertFalse(state.tables.getValue(eventsTable).containsKey("10"))
        assertTrue(state.tables.getValue(eventsTable).containsKey("11"))
        assertEquals(other, state.tables[otherTable])
        assertEquals(state.tables, StudioStore.decode(File(directory, "studio/state.json").readText()).tables)
    }

    @Test fun concurrentNoteChangesKeepEveryJournalEntry() = runBlocking {
        val store = newStore()
        coroutineScope {
            repeat(24) { id -> launch(Dispatchers.Default) { store.changed(id + 1, "updated") } }
        }
        val state = store.state.value
        assertEquals((1..24).toSet(), state.all("events").map { it.optInt("noteId") }.toSet())
        assertEquals(24, state.all("notes").size)
        assertEquals(state.tables, StudioStore.decode(File(directory, "studio/state.json").readText()).tables)
    }

    @Test fun scopeChangeDuringWriteKeepsBothTheNewScopeAndMetadata() = runBlocking {
        val store = newStore()
        store.load()
        val library = PlusProfiles.currentId
        store.editInLibrary("preferences", "workspace", library) {
            store.refreshScope("regression-active")
            it.put("leftHand", true)
        }
        assertEquals("regression-active", store.state.value.library)
        assertTrue(store.state.value.copy(library = library).get("preferences", "workspace").optBoolean("leftHand"))
    }

    @Test fun explicitEmptyRestoreClearsExistingDiskDataBeforeLoad() = runBlocking {
        val original = newStore()
        original.edit("preferences", "workspace") { it.put("leftHand", true) }
        val restored = newStore()
        restored.replace(StudioState())
        assertTrue(StudioStore.decode(File(directory, "studio/state.json").readText()).tables.isEmpty())
    }
}
