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
    val createdAt: Long = System.currentTimeMillis(),
    val weekDays: Set<Int> = emptySet(),
    val repeatEndAtMillis: Long? = null,
    val linkedNoteId: Int? = null,
    val snoozedUntil: Long? = null,
    val repeatInterval:Int=1,val monthOrdinal:Int=0,val monthWeekday:Int=2,val recurrenceAnchorMillis:Long?=null,
    val maxOccurrences:Int?=null,val occurrencesDone:Int=0,val alertMinutes:List<Int> = emptyList(),val locationOnly:Boolean=false,val excludedOccurrences:Set<Long> = emptySet(),val libraryId:String=com.example.mynotes.studio.PlusProfiles.currentId
) {
    companion object {
        const val REPEAT_NONE = "none"
        const val REPEAT_DAILY = "daily"
        const val REPEAT_WEEKLY = "weekly"
        const val REPEAT_MONTHLY = "monthly"
        const val REPEAT_WEEKDAYS = "weekdays"
        const val REPEAT_CUSTOM = "custom"

        const val PRIORITY_LOW = "low"
        const val PRIORITY_NORMAL = "normal"
        const val PRIORITY_HIGH = "high"
    }
}
