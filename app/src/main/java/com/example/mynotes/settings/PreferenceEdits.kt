package com.example.mynotes.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

/** Compara dentro de la transacción, sin depender de una copia atrasada de la UI. */
internal suspend fun DataStore<Preferences>.editIfChanged(
    update: MutablePreferences.() -> Boolean
): Boolean {
    var changed = false
    edit { changed = it.update() }
    return changed
}

internal fun <T> MutablePreferences.setIfChanged(key: Preferences.Key<T>, value: T): Boolean {
    if (this[key] == value) return false
    this[key] = value
    return true
}
