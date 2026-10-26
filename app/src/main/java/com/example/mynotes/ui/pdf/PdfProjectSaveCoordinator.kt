package com.example.mynotes.ui.pdf

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Agrupa solicitudes de guardado de una sesión del editor, sin retener páginas ni bitmaps. */
internal class PdfProjectSaveCoordinator {
    private val mutex = Mutex()
    private var savedVersion = 0

    suspend fun saveIfChanged(currentVersion: () -> Int?, save: suspend () -> Unit) {
        mutex.withLock {
            // Lee la revisión después de esperar: otra solicitud puede haberla guardado.
            val version = currentVersion() ?: return@withLock
            if (version == savedVersion) return@withLock
            // El repositorio termina las escrituras iniciadas aunque se cancele el autosave.
            // La confirmación debe terminar junto con ellas, sin cambiar de dispatcher aquí.
            withContext(NonCancellable) {
                save()
                savedVersion = version
            }
        }
    }
}
