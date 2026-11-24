package com.example.mynotes.studio

import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class StudioFilesTest {
    @Test fun writesCannotOverlapAcrossSuspensionPoints()=runBlocking {
        var count=0;var inside=0;var maximum=0
        coroutineScope {
            repeat(24) { launch {
                StudioFiles.write {
                    inside++;maximum=maxOf(maximum,inside)
                    val previous=count;yield();count=previous+1;inside--
                }
            } }
        }
        assertEquals(24,count);assertEquals(1,maximum)
    }
    @Test fun queuedWriteCannotOverwriteACompletedRestore()=runBlocking {
        StudioFiles.lock.lock()
        var wrote=false
        val pending=async(start=CoroutineStart.UNDISPATCHED) { StudioFiles.write { wrote=true } }
        StudioFiles.restored()
        StudioFiles.lock.unlock()
        try { pending.await();fail("Old write must be cancelled") } catch(_:CancellationException) { }
        assertFalse(wrote)
        StudioFiles.write { wrote=true }
        assertTrue(wrote)
    }
}
