package com.example.mynotes.ui.pdf

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfProjectSaveCoordinatorTest {
    @Test
    fun skipsUneditedUnavailableAndAlreadySavedVersions() = runBlocking {
        val coordinator = PdfProjectSaveCoordinator()
        var writes = 0
        coordinator.saveIfChanged({ null }) { writes++ }
        coordinator.saveIfChanged({ 0 }) { writes++ }
        assertEquals(0, writes)
        repeat(3) { coordinator.saveIfChanged({ 1 }) { writes++ } }
        assertEquals(1, writes)
    }

    @Test
    fun concurrentRequestsForSameVersionWriteOnce() = runBlocking {
        val coordinator = PdfProjectSaveCoordinator()
        val finish = CompletableDeferred<Unit>()
        var writes = 0
        val autosave = launch(start = CoroutineStart.UNDISPATCHED) {
            coordinator.saveIfChanged({ 1 }) {
                writes++
                finish.await()
            }
        }
        val back = launch(start = CoroutineStart.UNDISPATCHED) {
            coordinator.saveIfChanged({ 1 }) { writes++ }
        }
        val export = launch(start = CoroutineStart.UNDISPATCHED) {
            coordinator.saveIfChanged({ 1 }) { writes++ }
        }
        finish.complete(Unit)
        autosave.join()
        back.join()
        export.join()
        assertEquals(1, writes)
    }

    @Test
    fun readsLatestVersionAfterWaitingAndKeepsNewEditsPending() = runBlocking {
        val coordinator = PdfProjectSaveCoordinator()
        val finish = CompletableDeferred<Unit>()
        val saved = mutableListOf<Int>()
        var version = 1
        val first = launch(start = CoroutineStart.UNDISPATCHED) {
            coordinator.saveIfChanged({ version }) {
                saved += version
                finish.await()
            }
        }
        val queued = launch(start = CoroutineStart.UNDISPATCHED) {
            coordinator.saveIfChanged({ version }) { saved += version }
        }
        version = 2
        finish.complete(Unit)
        first.join()
        queued.join()
        coordinator.saveIfChanged({ version }) { saved += version }
        assertEquals(listOf(1, 2), saved)
    }

    @Test
    fun failedWriteRemainsPendingForRetry() = runBlocking {
        val coordinator = PdfProjectSaveCoordinator()
        var attempts = 0
        val failure = runCatching {
            coordinator.saveIfChanged({ 1 }) {
                attempts++
                error("Write failed")
            }
        }
        assertTrue(failure.isFailure)
        repeat(2) { coordinator.saveIfChanged({ 1 }) { attempts++ } }
        assertEquals(2, attempts)
    }

    @Test
    fun cancelledAutosaveAcknowledgesCompletedWrite() = runBlocking {
        val coordinator = PdfProjectSaveCoordinator()
        val finish = CompletableDeferred<Unit>()
        var writes = 0
        val autosave = launch(start = CoroutineStart.UNDISPATCHED) {
            coordinator.saveIfChanged({ 1 }) {
                writes++
                finish.await()
            }
        }
        autosave.cancel()
        finish.complete(Unit)
        autosave.join()
        coordinator.saveIfChanged({ 1 }) { writes++ }
        assertEquals(1, writes)
    }

    @Test
    fun cancelledWaiterDoesNotStartAnotherWrite() = runBlocking {
        val coordinator = PdfProjectSaveCoordinator()
        val finish = CompletableDeferred<Unit>()
        var writes = 0
        val first = launch(start = CoroutineStart.UNDISPATCHED) {
            coordinator.saveIfChanged({ 1 }) {
                writes++
                finish.await()
            }
        }
        val queued = launch(start = CoroutineStart.UNDISPATCHED) {
            coordinator.saveIfChanged({ 2 }) { writes++ }
        }
        queued.cancelAndJoin()
        finish.complete(Unit)
        first.join()
        assertEquals(1, writes)
        coordinator.saveIfChanged({ 2 }) { writes++ }
        assertEquals(2, writes)
    }
}
