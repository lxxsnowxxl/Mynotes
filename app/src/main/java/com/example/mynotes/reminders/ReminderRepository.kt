package com.example.mynotes.reminders

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

private val ReminderOrder = compareByDescending<Reminder> { it.enabled }.thenBy { it.triggerAtMillis }

/**
 * Almacén ligero e independiente de Room para recordatorios.
 * Esto evita tocar el esquema de notas/adjuntos y, por tanto, no introduce
 * ninguna migración que pueda poner en riesgo datos existentes.
 */
class ReminderRepository private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    @Volatile
    private var lastKnownEncoded: String? = preferences.getString(KEY_REMINDERS, null)

    private val _reminders = MutableStateFlow(loadReminders(lastKnownEncoded))
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

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
    fun getReminder(id: Long): Reminder? = _reminders.value.firstOrNull { it.id == id }

    fun upsert(reminder: Reminder) {
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
        if (reminder.enabled) {
            ReminderAlarmScheduler.schedule(appContext, reminder)
        } else {
            ReminderAlarmScheduler.cancel(appContext, reminder.id)
        }
    }

    fun delete(id: Long) {
        ReminderAlarmScheduler.cancel(appContext, id)
        val current = _reminders.value
        val index = current.indexOfFirst { it.id == id }
        if (index < 0) return
        val updated = current.toMutableList()
        updated.removeAt(index)
        save(updated)
    }

    fun setEnabled(id: Long, enabled: Boolean) {
        val reminder = getReminder(id) ?: return
        if (reminder.enabled == enabled) return
        upsert(reminder.copy(enabled = enabled))
    }

    /** Llamado después de disparar la notificación. */
    fun advanceAfterTrigger(id: Long) {
        val reminder = getReminder(id) ?: return
        if (reminder.repeatMode == Reminder.REPEAT_NONE) {
            upsert(reminder.copy(enabled = false))
            return
        }
        val next = nextTrigger(reminder.triggerAtMillis, reminder.repeatMode, System.currentTimeMillis())
        upsert(reminder.copy(triggerAtMillis = next, enabled = true))
    }

    fun rescheduleAll() {
        val now = System.currentTimeMillis()
        val current = _reminders.value
        if (current.isEmpty()) return

        var normalized: MutableList<Reminder>? = null
        current.forEachIndexed { index, reminder ->
            val updated = when {
                !reminder.enabled || reminder.triggerAtMillis > now -> reminder
                reminder.repeatMode == Reminder.REPEAT_NONE -> reminder.copy(enabled = false)
                else -> reminder.copy(
                    triggerAtMillis = nextTrigger(
                        reminder.triggerAtMillis,
                        reminder.repeatMode,
                        now
                    )
                )
            }
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
            if (reminder.enabled) ReminderAlarmScheduler.schedule(appContext, reminder)
        }
    }

    private fun save(items: List<Reminder>) {
        val sorted = if (isInReminderOrder(items)) items else items.sortedWith(ReminderOrder)

        // StateFlow evita notificar Compose si la lista es igual, pero también
        // evitamos serializar/escribir cuando el estado solicitado ya coincide.
        if (sorted == _reminders.value) return

        val encoded = sorted.toJsonString()
        _reminders.value = sorted

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

        fun nextTrigger(previous: Long, repeatMode: String, now: Long): Long {
            var candidate = previous
            val calendar = Calendar.getInstance().apply { timeInMillis = previous }
            do {
                when (repeatMode) {
                    Reminder.REPEAT_DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                    Reminder.REPEAT_WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    Reminder.REPEAT_MONTHLY -> calendar.add(Calendar.MONTH, 1)
                    Reminder.REPEAT_WEEKDAYS -> {
                        do {
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                        } while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    }
                    else -> return previous
                }
                candidate = calendar.timeInMillis
            } while (candidate <= now)
            return candidate
        }
    }
}
