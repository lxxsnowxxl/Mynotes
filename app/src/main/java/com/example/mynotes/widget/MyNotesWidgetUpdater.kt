package com.example.mynotes.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

/**
 * Punto único para solicitar refrescos de todos los widgets de MyNotes.
 *
 * Varias acciones de la app pueden ocurrir en ráfaga (guardar una nota,
 * eliminar varios adjuntos, cambiar propiedades seguidas, restaurar backup).
 * Enviar seis broadcasts por cada una de esas operaciones provoca consultas
 * repetidas a Room y trabajo redundante del launcher.
 *
 * Agrupamos las solicitudes cercanas en una sola actualización. El retraso es
 * lo bastante corto para ser imperceptible visualmente y evita duplicar
 * trabajo cuando varias escrituras terminan casi al mismo tiempo.
 */
object MyNotesWidgetUpdater {
    private const val COALESCE_DELAY_MS = 120L

    private val providers = arrayOf(
        QuickNoteWidgetProvider::class.java,
        RecentNotesWidgetProvider::class.java,
        FavoritesWidgetProvider::class.java,
        FocusNoteWidgetProvider::class.java,
        CollectionsWidgetProvider::class.java,
        StatsWidgetProvider::class.java
    )

    private val mainHandler = Handler(Looper.getMainLooper())
    private val updateLock = Any()
    private var applicationContext: Context? = null

    private val updateRunnable = Runnable {
        val context = synchronized(updateLock) {
            applicationContext.also { applicationContext = null }
        } ?: return@Runnable

        val manager = AppWidgetManager.getInstance(context)
        providers.forEach { updateProvider(context, manager, it) }
    }

    fun requestUpdate(context: Context) {
        val appContext = context.applicationContext
        synchronized(updateLock) {
            applicationContext = appContext
            mainHandler.removeCallbacks(updateRunnable)
            mainHandler.postDelayed(updateRunnable, COALESCE_DELAY_MS)
        }
    }

    private fun updateProvider(context: Context, manager: AppWidgetManager, providerClass: Class<*>) {
        val component = ComponentName(context, providerClass)
        val ids = manager.getAppWidgetIds(component)
        if (ids.isEmpty()) return

        val intent = Intent(context, providerClass).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
    }
}
