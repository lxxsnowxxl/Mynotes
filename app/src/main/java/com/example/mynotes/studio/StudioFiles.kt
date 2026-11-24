package com.example.mynotes.studio

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex

/** Serialize private workspace-file writes with complete backup/restore snapshots. */
internal object StudioFiles{
 val lock=Mutex()
 val epoch=MutableStateFlow(0L)
 @Volatile var restoredAt=0L
 suspend inline fun <T> write(block:()->T):T{val generation=epoch.value
lock.lock()
try{if(generation!=epoch.value)throw CancellationException("Workspace was restored")
return block()}finally{lock.unlock()}}
 fun restored(){restoredAt=System.currentTimeMillis()
epoch.value=epoch.value+1}
}
