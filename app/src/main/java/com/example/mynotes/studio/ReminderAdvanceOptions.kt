package com.example.mynotes.studio

/** Rules shared by the reminder editor and its quick-alert controls.
 * The stored values are minutes before the event to preserve existing reminders.
 */
internal object ReminderAdvanceOptions {
    const val MAX_MINUTES = 7 * 24 * 60
    const val MAX_ALERTS = 12

    enum class Unit(val factor: Int) { MINUTES(1), HOURS(60), DAYS(1440) }

    fun normalize(alerts: List<Int>): List<Int> = alerts.asSequence()
        .filter { it in 1..MAX_MINUTES }
        .distinct()
        .sorted()
        .take(MAX_ALERTS)
        .toList()

    fun toMinutes(amount: Int?, unit: Unit): Int? {
        if (amount == null || amount <= 0) return null
        val minutes = amount.toLong() * unit.factor
        return if (minutes in 1L..MAX_MINUTES.toLong()) minutes.toInt() else null
    }

    fun isUpcoming(triggerAtMillis: Long, minutes: Int, nowMillis: Long): Boolean =
        minutes in 1..MAX_MINUTES && triggerAtMillis - minutes.toLong() * 60_000L > nowMillis
}
