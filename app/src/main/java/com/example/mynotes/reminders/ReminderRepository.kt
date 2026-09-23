package com.example.mynotes.reminders

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

/**
 * Almacén ligero e independiente de Room para recordatorios.
 * Esto evita tocar el esquema de notas/adjuntos y, por tanto, no introduce
 * ninguna migración que pueda poner en riesgo datos existentes.
 */
class ReminderRepository(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _reminders = MutableStateFlow(loadReminders())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_REMINDERS) {
            _reminders.value = loadReminders()
        }
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    fun getReminder(id: Long): Reminder? = loadReminders().firstOrNull { it.id == id }

    fun upsert(reminder: Reminder) {
        val current = loadReminders().toMutableList()
        val index = current.indexOfFirst { it.id == reminder.id }
        if (index >= 0) current[index] = reminder else current += reminder
        save(current)
        if (reminder.enabled) {
            ReminderAlarmScheduler.schedule(appContext, reminder)
        } else {
            ReminderAlarmScheduler.cancel(appContext, reminder.id)
        }
    }

    fun delete(id: Long) {
        ReminderAlarmScheduler.cancel(appContext, id)
        save(loadReminders().filterNot { it.id == id })
    }

    fun setEnabled(id: Long, enabled: Boolean) {
        val reminder = getReminder(id) ?: return
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
        loadReminders().filter { it.enabled }.forEach { reminder ->
            val normalized = if (reminder.triggerAtMillis <= System.currentTimeMillis()) {
                if (reminder.repeatMode == Reminder.REPEAT_NONE) {
                    reminder.copy(enabled = false)
                } else {
                    reminder.copy(triggerAtMillis = nextTrigger(reminder.triggerAtMillis, reminder.repeatMode, System.currentTimeMillis()))
                }
            } else reminder
            if (normalized != reminder) upsert(normalized)
            if (normalized.enabled) ReminderAlarmScheduler.schedule(appContext, normalized)
        }
    }

    private fun save(items: List<Reminder>) {
        val sorted = items.sortedWith(compareByDescending<Reminder> { it.enabled }.thenBy { it.triggerAtMillis })
        val array = JSONArray()
        sorted.forEach { reminder ->
            array.put(JSONObject().apply {
                put("id", reminder.id)
                put("title", reminder.title)
                put("description", reminder.description)
                put("triggerAtMillis", reminder.triggerAtMillis)
                put("repeatMode", reminder.repeatMode)
                put("priority", reminder.priority)
                put("colorKey", reminder.colorKey)
                put("enabled", reminder.enabled)
                put("createdAt", reminder.createdAt)
            })
        }
        preferences.edit().putString(KEY_REMINDERS, array.toString()).apply()
        _reminders.value = sorted
    }

    private fun loadReminders(): List<Reminder> {
        val raw = preferences.getString(KEY_REMINDERS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        Reminder(
                            id = item.optLong("id", System.currentTimeMillis() + index),
                            title = item.optString("title"),
                            description = item.optString("description"),
                            triggerAtMillis = item.optLong("triggerAtMillis"),
                            repeatMode = item.optString("repeatMode", Reminder.REPEAT_NONE),
                            priority = item.optString("priority", Reminder.PRIORITY_NORMAL),
                            colorKey = item.optString("colorKey", "palette"),
                            enabled = item.optBoolean("enabled", true),
                            createdAt = item.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }.sortedWith(compareByDescending<Reminder> { it.enabled }.thenBy { it.triggerAtMillis })
        }.getOrDefault(emptyList())
    }

    companion object {
        private const val PREFS_NAME = "reminder_prefs"
        private const val KEY_REMINDERS = "reminders_json"

        fun nextTrigger(previous: Long, repeatMode: String, now: Long): Long {
            var candidate = previous
            do {
                val calendar = Calendar.getInstance().apply { timeInMillis = candidate }
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
