package com.example.mynotes.reminders

data class Reminder(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val description: String = "",
    val triggerAtMillis: Long,
    val repeatMode: String = REPEAT_NONE,
    val priority: String = PRIORITY_NORMAL,
    val colorKey: String = "palette",
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val REPEAT_NONE = "none"
        const val REPEAT_DAILY = "daily"
        const val REPEAT_WEEKLY = "weekly"
        const val REPEAT_MONTHLY = "monthly"
        const val REPEAT_WEEKDAYS = "weekdays"

        const val PRIORITY_LOW = "low"
        const val PRIORITY_NORMAL = "normal"
        const val PRIORITY_HIGH = "high"
    }
}
