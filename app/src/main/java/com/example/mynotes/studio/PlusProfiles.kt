package com.example.mynotes.studio

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.withLock

/** Local libraries partition content, rather than pretending to be authenticated accounts. */
internal object PlusProfiles {
    private val mutable = MutableStateFlow("default")
    val active = mutable.asStateFlow()
    val currentId get() = mutable.value
    @Volatile private var initialized = false
    fun initialize(context: Context) {
        if (!initialized) synchronized(this) { if (!initialized) {
            mutable.value = context.getSharedPreferences("studio_profiles", Context.MODE_PRIVATE).getString("active", "default")?.takeIf { it.matches(Regex("[a-zA-Z0-9-]{1,80}")) } ?: "default"
            initialized = true
        } }
    }
    private val global = setOf("profiles", "file_owners", "widget_bindings", "auto_sync")
    fun table(name: String, library: String = currentId) = if (library == "default" || name in global || name.startsWith("library:")) name else "library:$library:$name"
    fun ownsFile(context: Context, kind: String, id: String): Boolean = StudioStore.get(context).state.value.get("file_owners", "$kind:$id").optString("library", "default") == currentId
    suspend fun claimFile(context: Context, kind: String, id: String) {
        val library = currentId
        StudioStore.get(context).edit("file_owners", "$kind:$id") { row ->
            require(!row.has("library") || row.optString("library") == library) { "Project belongs to another local library" }
            row.put("library", library)
        }
    }
    suspend fun switch(context: Context, id: String) {
        require(id.matches(Regex("[a-zA-Z0-9-]{1,80}")))
        val store = StudioStore.get(context);com.example.mynotes.data.workspace.NoteWorkspaceStore.flush();com.example.mynotes.data.workspace.DrawingProjects.flush();store.flush()
        StudioMutations.lock.withLock {
            require(StudioJobs.state.value.none { it.status == "running" }) { "Wait for background tasks to finish before switching libraries" }
            StudioFiles.write {
                require(id == "default" || store.state.value.all("profiles").any { it.getString("id") == id })
                check(context.getSharedPreferences("studio_profiles", Context.MODE_PRIVATE).edit().putString("active", id).commit())
                com.example.mynotes.viewmodel.NoteViewModel.clearLibraryUndo()
                mutable.value = id
                store.refreshScope(id)
                StudioFiles.restored()
            }
        }
        com.example.mynotes.widget.MyNotesWidgetUpdater.requestUpdate(context)
    }
}
