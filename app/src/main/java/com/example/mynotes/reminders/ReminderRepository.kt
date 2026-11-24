package com.example.mynotes.reminders

import com.example.mynotes.ui.state.ReminderActions
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers

private val ReminderOrder = compareByDescending<Reminder> { it.enabled }.thenBy { it.triggerAtMillis }

/**
 * Almacén ligero e independiente de Room para recordatorios.
 * Esto evita tocar el esquema de notas/adjuntos y, por tanto, no introduce
 * ninguna migración que pueda poner en riesgo datos existentes.
 */
class ReminderRepository private constructor(context: Context) : ReminderActions {
    private val appContext = context.applicationContext.also{com.example.mynotes.studio.PlusProfiles.initialize(it)}
    private val preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    @Volatile
    private var lastKnownEncoded: String? = preferences.getString(KEY_REMINDERS, null)

    private val _reminders = MutableStateFlow(loadReminders(lastKnownEncoded))
    override val reminders: StateFlow<List<Reminder>> = combine(_reminders,com.example.mynotes.studio.PlusProfiles.active){rows,library->rows.filter{it.libraryId==library}}.stateIn(CoroutineScope(SupervisorJob()+Dispatchers.Default),SharingStarted.Eagerly,_reminders.value.filter{it.libraryId==com.example.mynotes.studio.PlusProfiles.currentId})

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_REMINDERS) {
            val encoded = preferences.getString(KEY_REMINDERS, null)
            if (encoded != lastKnownEncoded) {
                lastKnownEncoded = encoded
                _reminders.value = loadReminders(encoded)
            }
        }
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    /**
     * La lista ya está cargada en memoria dentro del StateFlow. Reutilizarla
     * evita volver a parsear todo el JSON de SharedPreferences en cada toque,
     * edición, activación o eliminación de un recordatorio.
     */
    internal fun allReminders():List<Reminder> = _reminders.value
    fun getReminder(id: Long): Reminder? = _reminders.value.firstOrNull { it.id == id }

    @Synchronized override fun upsert(reminder: Reminder) {
        val current = _reminders.value
        val index = current.indexOfFirst { it.id == reminder.id }
        val changed = index < 0 || current[index] != reminder

        if (changed) {
            val updated = current.toMutableList()
            if (index >= 0) updated[index] = reminder else updated += reminder
            save(updated)
        }

        // La programación del AlarmManager se conserva aunque el contenido no
        // haya cambiado; así una llamada explícita a upsert sigue revalidando
        // el estado real de la alarma sin forzar una escritura de preferencias.
        if (reminder.enabled || reminder.snoozedUntil != null) {
            ReminderAlarmScheduler.schedule(appContext, reminder)
        } else {
            ReminderAlarmScheduler.cancel(appContext, reminder.id)
        }
    }

    @Synchronized override fun delete(id: Long) {
        ReminderAlarmScheduler.cancel(appContext, id)
        val current = _reminders.value
        val index = current.indexOfFirst { it.id == id }
        if (index < 0) return
        val updated = current.toMutableList()
        updated.removeAt(index)
        save(updated)
    }

    @Synchronized override fun setEnabled(id: Long, enabled: Boolean) {
        val reminder = getReminder(id) ?: return
        if (reminder.enabled == enabled && reminder.snoozedUntil == null) return
        val now = System.currentTimeMillis()
        val trigger = if (enabled) ReminderSchedule.enableAt(reminder, now) ?: return else reminder.triggerAtMillis
        upsert(reminder.copy(enabled = enabled, triggerAtMillis = trigger, snoozedUntil = null))
    }

    /** Snoozing is a separate occurrence and does not shift the repeating schedule. */
    fun snooze(id: Long, minutes: Int = 10) {
        val reminder = getReminder(id) ?: return
        recordEvent(reminder,"snoozed")
        upsert(reminder.copy(snoozedUntil = System.currentTimeMillis() + minutes.coerceIn(1, 1440) * 60_000L))
    }

    fun recordEvent(reminder:Reminder,action:String){com.example.mynotes.studio.StudioStore.get(appContext).putInLibrary("reminder_events",java.util.UUID.randomUUID().toString(),reminder.libraryId){it.put("reminderId",reminder.id).put("title",reminder.title).put("action",action).put("at",System.currentTimeMillis())}}
    fun complete(id:Long,occurrenceAlreadyFired:Boolean=false){getReminder(id)?.let{recordEvent(it,"completed");clearSnooze(id);if(it.repeatMode==Reminder.REPEAT_NONE)setEnabled(id,false)else if(!occurrenceAlreadyFired)advanceAfterTrigger(id)}}
    fun clearSnooze(id: Long) {
        getReminder(id)?.let { upsert(it.copy(snoozedUntil = null)) }
    }

    @Synchronized fun advanceAfterTrigger(id: Long, wasSnoozed: Boolean = false) {
        val reminder = getReminder(id) ?: return
        if (wasSnoozed) {
            upsert(reminder.copy(snoozedUntil = null))
            return
        }
        val next = ReminderSchedule.next(reminder, System.currentTimeMillis())
        upsert(reminder.copy(triggerAtMillis = next ?: reminder.triggerAtMillis, enabled = next != null,occurrencesDone=reminder.occurrencesDone+1+ReminderSchedule.skippedBetween(reminder,reminder.triggerAtMillis,next?:Long.MAX_VALUE),recurrenceAnchorMillis=reminder.recurrenceAnchorMillis?:reminder.triggerAtMillis))
    }

    @Synchronized fun rescheduleAll() {
        val now = System.currentTimeMillis()
        val current = _reminders.value
        if (current.isEmpty()) return

        var normalized: MutableList<Reminder>? = null
        current.forEachIndexed { index, reminder ->
            val pending = reminder.snoozedUntil?.let { if (it <= now) now + 1_000L else it }
            val next = if (reminder.enabled && !reminder.locationOnly && reminder.triggerAtMillis <= now) ReminderSchedule.next(reminder, now) else reminder.triggerAtMillis
            val updated = reminder.copy(
                triggerAtMillis = next ?: reminder.triggerAtMillis,
                enabled = reminder.enabled && next != null && (reminder.repeatEndAtMillis == null || (next ?: reminder.triggerAtMillis) <= reminder.repeatEndAtMillis),
                snoozedUntil = pending
            )
            if (updated != reminder) {
                val target = normalized ?: current.toMutableList().also { normalized = it }
                target[index] = updated
            }
        }

        val effective = normalized ?: current
        if (normalized != null) {
            save(effective)
        }

        for (reminder in effective) {
            if (reminder.enabled || reminder.snoozedUntil != null) ReminderAlarmScheduler.schedule(appContext, reminder)
        }
    }

    private fun save(items: List<Reminder>) {
        val sorted = if (isInReminderOrder(items)) items else items.sortedWith(ReminderOrder)

        // StateFlow evita notificar Compose si la lista es igual, pero también
        // evitamos serializar/escribir cuando el estado solicitado ya coincide.
        if (sorted == _reminders.value) return

        val encoded = sorted.toJsonString()
        _reminders.value = sorted
        com.example.mynotes.widget.MyNotesWidgetUpdater.requestUpdate(appContext)

        // SharedPreferences no necesita recibir otra escritura si el JSON final
        // ya es idéntico. Esto reduce trabajo del listener y fsync diferido.
        if (lastKnownEncoded != encoded) {
            // Actualizamos primero el espejo en memoria para que el listener
            // ignore el callback producido por nuestra propia escritura.
            lastKnownEncoded = encoded
            preferences.edit().putString(KEY_REMINDERS, encoded).apply()
        }
    }

    private fun loadReminders(rawValue: String? = preferences.getString(KEY_REMINDERS, null)): List<Reminder> {
        val raw = rawValue ?: return emptyList()
        return runCatching { remindersFromJson(raw).sortedWith(ReminderOrder) }.getOrDefault(emptyList())
    }

    private fun isInReminderOrder(items: List<Reminder>): Boolean {
        for (index in 1 until items.size) {
            if (ReminderOrder.compare(items[index - 1], items[index]) > 0) return false
        }
        return true
    }

    companion object {
        private const val PREFS_NAME = "reminder_prefs"
        private const val KEY_REMINDERS = "reminders_json"

        @Volatile
        private var instance: ReminderRepository? = null

        /**
         * Un único repositorio por proceso evita registrar listeners de
         * SharedPreferences repetidos al recrear MainActivity o al recibir
         * varias alarmas. El repositorio solo conserva applicationContext.
         */
        fun getInstance(context: Context): ReminderRepository =
            instance ?: synchronized(this) {
                instance ?: ReminderRepository(context.applicationContext).also { instance = it }
            }

    }
}
