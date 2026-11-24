package com.example.mynotes.studio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.json.JSONObject

/** Observe only the encoded data a component uses, before parsing it or updating the UI. */
@Composable
internal fun rememberStudioRow(store: StudioStore, table: String, id: String): JSONObject {
    val row = remember(store, table, id) {
        store.state.map { state -> state.tables[PlusProfiles.table(table, state.library)]?.get(id) }
            .distinctUntilChanged()
    }
    val encoded by row.collectAsStateWithLifecycle(
        initialValue = store.state.value.let { it.tables[PlusProfiles.table(table, it.library)]?.get(id) }
    )
    return remember(encoded, id) { encoded?.let(::JSONObject) ?: JSONObject().put("id", id) }
}

@Composable
internal fun rememberStudioTable(store: StudioStore, table: String): Map<String, String> {
    val rows = remember(store, table) {
        store.state.map { state -> state.tables[PlusProfiles.table(table, state.library)].orEmpty() }
            .distinctUntilChanged()
    }
    val encoded by rows.collectAsStateWithLifecycle(
        initialValue = store.state.value.let { it.tables[PlusProfiles.table(table, it.library)].orEmpty() }
    )
    return encoded
}
