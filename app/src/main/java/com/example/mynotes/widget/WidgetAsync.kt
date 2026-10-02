package com.example.mynotes.widget

import android.content.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Scope de proceso reutilizable para el trabajo corto de App Widgets.
 *
 * Antes cada broadcast creaba un SupervisorJob y un CoroutineScope nuevos.
 * Los PendingResult siguen siendo independientes y siempre se finalizan en su
 * propia coroutine; únicamente se reutiliza el contenedor de ejecución IO.
 */
private val WidgetIoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

internal fun BroadcastReceiver.launchAsyncIo(block: suspend () -> Unit) {
    val pendingResult = goAsync()
    WidgetIoScope.launch {
        try { block() } finally { pendingResult.finish() }
    }
}
