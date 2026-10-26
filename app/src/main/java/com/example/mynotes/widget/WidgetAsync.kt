package com.example.mynotes.widget

import android.content.BroadcastReceiver
import android.appwidget.AppWidgetProvider
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import java.util.concurrent.ConcurrentHashMap

/**
 * Scope de proceso reutilizable para el trabajo corto de App Widgets.
 *
 * Cada PendingResult se finaliza en su propia coroutine. Los refrescos del
 * mismo proveedor comparten una cola; las acciones se ejecutan por separado.
 */
internal val WidgetIoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
private val WidgetUpdateQueues = ConcurrentHashMap<Class<*>, WidgetUpdateQueue>()

/** Evita refrescos solapados; las acciones de favoritos/fijados siguen siendo individuales. */
internal fun AppWidgetProvider.launchWidgetUpdate(ids: IntArray, update: suspend (IntArray) -> Unit) {
    if (ids.isEmpty()) return
    val queue = WidgetUpdateQueues.getOrPut(javaClass) { WidgetUpdateQueue() }
    queue.enqueue(ids)
    launchAsyncIo {
        try {
            queue.drain(update)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Log.w("MyNotesWidgets", "Widget refresh failed", error)
        }
    }
}

internal fun BroadcastReceiver.launchAsyncIo(block: suspend () -> Unit) {
    val pendingResult = goAsync()
    WidgetIoScope.launch {
        try { block() } finally { pendingResult.finish() }
    }
}
