package com.example.mynotes.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class PreferenceEditsTest {
    @get:Rule val temporaryFolder = TemporaryFolder()
    private lateinit var storeJob: Job
    private lateinit var store: DataStore<Preferences>

    @Before
    fun setUp() {
        storeJob = SupervisorJob()
        store = PreferenceDataStoreFactory.create(scope = CoroutineScope(storeJob + Dispatchers.IO)) {
            File(temporaryFolder.root, "settings.preferences_pb")
        }
    }

    @After
    fun tearDown() = runBlocking { storeJob.cancelAndJoin() }

    @Test
    fun repeatedValueReportsNoChange() = runBlocking {
        val key = intPreferencesKey("volume")
        assertTrue(store.editIfChanged { setIfChanged(key, 65) })
        repeat(10) { assertFalse(store.editIfChanged { setIfChanged(key, 65) }) }
        assertEquals(65, store.data.first()[key])
    }

    @Test
    fun returningToEarlierValueIsSavedDespiteStaleUiSnapshot() = runBlocking {
        val key = booleanPreferencesKey("enabled")
        store.editIfChanged { setIfChanged(key, false) }
        val staleUiValue = store.data.first()[key]!!
        assertTrue(store.editIfChanged { setIfChanged(key, true) })
        assertTrue(store.editIfChanged { setIfChanged(key, staleUiValue) })
        assertEquals(false, store.data.first()[key])
    }

    @Test
    fun independentConcurrentSettingsAreNotLost() = runBlocking {
        val keys = (0 until 50).map { intPreferencesKey("setting_$it") }
        coroutineScope {
            keys.forEachIndexed { index, key ->
                launch { store.editIfChanged { setIfChanged(key, index) } }
            }
        }
        val saved = store.data.first()
        keys.forEachIndexed { index, key -> assertEquals(index, saved[key]) }
    }

    @Test
    fun failureRollsBackAndAllowsRetry() = runBlocking {
        val key = intPreferencesKey("tone")
        store.editIfChanged { setIfChanged(key, 1) }
        val failure = runCatching {
            store.editIfChanged {
                setIfChanged(key, 2)
                error("Failed edit")
            }
        }
        assertTrue(failure.isFailure)
        assertEquals(1, store.data.first()[key])
        assertTrue(store.editIfChanged { setIfChanged(key, 2) })
    }
}
