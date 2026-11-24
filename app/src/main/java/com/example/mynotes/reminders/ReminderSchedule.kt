package com.example.mynotes.reminders

import java.util.Calendar
import java.util.TimeZone

/** Calendar-based scheduling preserves local clock time across daylight-saving changes. */
object ReminderSchedule {
    fun next(reminder: Reminder, now: Long, timeZone: TimeZone = TimeZone.getDefault()): Long? {
        var probe=reminder
        var after=now
        repeat(reminder.excludedOccurrences.size+1) {
            val candidate=nextBase(probe,after,timeZone)?:return null
            if(candidate !in reminder.excludedOccurrences)return candidate
            probe=probe.copy(triggerAtMillis=candidate,occurrencesDone=probe.occurrencesDone+1,recurrenceAnchorMillis=probe.recurrenceAnchorMillis?:reminder.triggerAtMillis)
            after=candidate
        }
        return null
    }
    fun skippedBetween(reminder:Reminder,after:Long,before:Long)=reminder.excludedOccurrences.count{it>after&&it<before}
    fun preview(reminder:Reminder,from:Long,limit:Int=30,timeZone:TimeZone=TimeZone.getDefault()):List<Long>{
        if(reminder.locationOnly)return emptyList()
        val result=mutableListOf<Long>()
        var current=reminder
        if(current.triggerAtMillis<from||current.triggerAtMillis in current.excludedOccurrences){
            val next=next(current,maxOf(from-1,current.triggerAtMillis),timeZone)?:return emptyList()
            current=current.copy(triggerAtMillis=next,occurrencesDone=current.occurrencesDone+1+skippedBetween(current,current.triggerAtMillis,next))
        }
        repeat(limit.coerceIn(1,1000)){
            val maximum=current.maxOccurrences;val ends=current.repeatEndAtMillis
            if(maximum!=null&&current.occurrencesDone>=maximum)return result
            if(ends!=null&&current.triggerAtMillis>ends)return result
            result+=current.triggerAtMillis
            val next=next(current,current.triggerAtMillis,timeZone)?:return result
            current=current.copy(triggerAtMillis=next,occurrencesDone=current.occurrencesDone+1+skippedBetween(current,current.triggerAtMillis,next),recurrenceAnchorMillis=current.recurrenceAnchorMillis?:reminder.triggerAtMillis)
        }
        return result
    }
    private fun nextBase(reminder: Reminder, now: Long, timeZone: TimeZone): Long? {
        if(reminder.repeatMode==Reminder.REPEAT_NONE||reminder.locationOnly)return null
        if(reminder.maxOccurrences!=null&&reminder.occurrencesDone+1>=reminder.maxOccurrences)return null
        if(reminder.repeatInterval>1||reminder.monthOrdinal!=0||(reminder.repeatMode==Reminder.REPEAT_MONTHLY&&reminder.recurrenceAnchorMillis!=null))return advanced(reminder,now,timeZone)
        val days = when (reminder.repeatMode) {
            Reminder.REPEAT_WEEKDAYS -> setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY)
            Reminder.REPEAT_CUSTOM -> reminder.weekDays.filter { it in Calendar.SUNDAY..Calendar.SATURDAY }.toSet()
            else -> emptySet()
        }
        if (reminder.repeatMode == Reminder.REPEAT_CUSTOM && days.isEmpty()) return null
        val previous = Calendar.getInstance(timeZone).apply { timeInMillis = reminder.triggerAtMillis }
        val candidate = previous.clone() as Calendar
        val today = Calendar.getInstance(timeZone).apply { timeInMillis = maxOf(now, reminder.triggerAtMillis) }
        when (reminder.repeatMode) {
            Reminder.REPEAT_DAILY, Reminder.REPEAT_WEEKDAYS, Reminder.REPEAT_CUSTOM -> {
                candidate.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
                if (candidate.timeInMillis <= maxOf(now, reminder.triggerAtMillis)) candidate.add(Calendar.DAY_OF_YEAR, 1)
                while (days.isNotEmpty() && candidate.get(Calendar.DAY_OF_WEEK) !in days) candidate.add(Calendar.DAY_OF_YEAR, 1)
            }
            Reminder.REPEAT_WEEKLY -> {
                candidate.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
                val delta = (previous.get(Calendar.DAY_OF_WEEK) - candidate.get(Calendar.DAY_OF_WEEK) + 7) % 7
                candidate.add(Calendar.DAY_OF_YEAR, delta)
                if (candidate.timeInMillis <= maxOf(now, reminder.triggerAtMillis)) candidate.add(Calendar.WEEK_OF_YEAR, 1)
            }
            Reminder.REPEAT_MONTHLY -> {
                val day = previous.get(Calendar.DAY_OF_MONTH)
                candidate.set(Calendar.DAY_OF_MONTH, 1)
                candidate.set(Calendar.YEAR, today.get(Calendar.YEAR)); candidate.set(Calendar.MONTH, today.get(Calendar.MONTH))
                candidate.set(Calendar.DAY_OF_MONTH, minOf(day, candidate.getActualMaximum(Calendar.DAY_OF_MONTH)))
                if (candidate.timeInMillis <= maxOf(now, reminder.triggerAtMillis)) {
                    candidate.set(Calendar.DAY_OF_MONTH, 1); candidate.add(Calendar.MONTH, 1)
                    candidate.set(Calendar.DAY_OF_MONTH, minOf(day, candidate.getActualMaximum(Calendar.DAY_OF_MONTH)))
                }
            }
            else -> return null
        }
        return candidate.timeInMillis.takeIf { reminder.repeatEndAtMillis == null || it <= reminder.repeatEndAtMillis }
    }

    private fun advanced(reminder:Reminder,now:Long,zone:TimeZone):Long?{
        val anchor=Calendar.getInstance(zone).apply{timeInMillis=reminder.recurrenceAnchorMillis?:reminder.triggerAtMillis};val limit=maxOf(now,reminder.triggerAtMillis);val interval=reminder.repeatInterval.coerceIn(1,365);val candidate=anchor.clone()as Calendar;val monthly=reminder.repeatMode==Reminder.REPEAT_MONTHLY;val day=anchor.get(Calendar.DAY_OF_MONTH)
        val field=when(reminder.repeatMode){Reminder.REPEAT_DAILY,Reminder.REPEAT_WEEKDAYS,Reminder.REPEAT_CUSTOM->Calendar.DAY_OF_YEAR;Reminder.REPEAT_WEEKLY->Calendar.WEEK_OF_YEAR;Reminder.REPEAT_MONTHLY->Calendar.MONTH;else->return null}
        val today=Calendar.getInstance(zone).apply{timeInMillis=limit};val estimate=if(monthly)(today.get(Calendar.YEAR)-anchor.get(Calendar.YEAR))*12+today.get(Calendar.MONTH)-anchor.get(Calendar.MONTH)else((limit-anchor.timeInMillis)/(if(field==Calendar.WEEK_OF_YEAR)604800000L else 86400000L)).toInt()
        if(monthly)candidate.set(Calendar.DAY_OF_MONTH,1);candidate.add(field,(estimate/interval-1).coerceAtLeast(0)*interval)
        repeat(4000){var validMonth=true
            if(monthly){when{reminder.monthOrdinal==0->candidate.set(Calendar.DAY_OF_MONTH,minOf(day,candidate.getActualMaximum(Calendar.DAY_OF_MONTH)))
                reminder.monthOrdinal== -1->{candidate.set(Calendar.DAY_OF_MONTH,candidate.getActualMaximum(Calendar.DAY_OF_MONTH));candidate.add(Calendar.DAY_OF_MONTH,-((candidate.get(Calendar.DAY_OF_WEEK)-reminder.monthWeekday+7)%7))}
                else->{candidate.set(Calendar.DAY_OF_MONTH,1);val target=1+(reminder.monthWeekday-candidate.get(Calendar.DAY_OF_WEEK)+7)%7+(reminder.monthOrdinal-1)*7;validMonth=target<=candidate.getActualMaximum(Calendar.DAY_OF_MONTH);candidate.set(Calendar.DAY_OF_MONTH,if(validMonth)target else 1)}}}
            val validDay=when(reminder.repeatMode){Reminder.REPEAT_WEEKDAYS->candidate.get(Calendar.DAY_OF_WEEK)in Calendar.MONDAY..Calendar.FRIDAY;Reminder.REPEAT_CUSTOM->candidate.get(Calendar.DAY_OF_WEEK)in reminder.weekDays;else->true}
            if(validMonth&&validDay&&candidate.timeInMillis>limit)return candidate.timeInMillis.takeIf{reminder.repeatEndAtMillis==null||it<=reminder.repeatEndAtMillis}
            if(monthly)candidate.set(Calendar.DAY_OF_MONTH,1);candidate.add(field,interval)
        };return null
    }

    /** A switch must never show a series as active when its end date prevents any future alarm. */
    fun enableAt(reminder: Reminder, now: Long, timeZone: TimeZone = TimeZone.getDefault()): Long? {
        if(reminder.maxOccurrences!=null&&reminder.occurrencesDone>=reminder.maxOccurrences)return null
        val candidate = if (reminder.triggerAtMillis > now && reminder.triggerAtMillis !in reminder.excludedOccurrences) reminder.triggerAtMillis
            else if (reminder.repeatMode == Reminder.REPEAT_NONE) now + 300_000L
            else next(reminder, now, timeZone) ?: return null
        return candidate.takeIf { reminder.repeatEndAtMillis == null || it <= reminder.repeatEndAtMillis }
    }

    fun alarmAt(reminder: Reminder, now: Long): Long? {
        val regular = reminder.triggerAtMillis.takeIf {
            !reminder.locationOnly && reminder.enabled && it !in reminder.excludedOccurrences && it > now && (reminder.repeatEndAtMillis == null || it <= reminder.repeatEndAtMillis)
        }
        val snoozed = reminder.snoozedUntil?.takeIf { it > now }
        return listOfNotNull(regular, snoozed).minOrNull()
    }
}
