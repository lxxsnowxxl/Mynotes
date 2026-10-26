package com.example.mynotes.widget

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Un lote por proveedor; conserva todos los IDs al agrupar refrescos pendientes. */
internal class WidgetUpdateQueue {
    private val mutex = Mutex()
    private val pendingIds = linkedSetOf<Int>()

    fun enqueue(ids: IntArray) = synchronized(pendingIds) {
        ids.forEach { pendingIds.add(it) }
    }

    suspend fun drain(update: suspend (IntArray) -> Unit) {
        mutex.withLock {
            val ids = synchronized(pendingIds) {
                pendingIds.toIntArray().also { pendingIds.clear() }
            }
            if (ids.isNotEmpty()) update(ids)
        }
    }
}
