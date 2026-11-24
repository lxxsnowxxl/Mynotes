package com.example.mynotes.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object ReminderAlarmScheduler {
    const val ACTION_FIRE_REMINDER = "com.example.mynotes.action.FIRE_REMINDER"
    const val EXTRA_REMINDER_ID = "reminder_id"
    const val EXTRA_SCHEDULED_AT = "scheduled_at"
    const val EXTRA_IS_SNOOZE = "is_snooze"

    const val EXTRA_EARLY_MINUTES="early_minutes"
    @Volatile
    private var cachedAlarmManager: AlarmManager? = null

    fun schedule(context: Context, reminder: Reminder) {
        cancelEarly(context,reminder.id)
        val now=System.currentTimeMillis()
        if(reminder.enabled&&!reminder.locationOnly){val offsets=reminder.alertMinutes.filter{it in 1..10080&&reminder.triggerAtMillis-it*60000L>now}.distinct().take(12);context.getSharedPreferences("reminder_offsets",Context.MODE_PRIVATE).edit().putString(reminder.id.toString(),offsets.joinToString(",")).apply()
            offsets.forEach{minutes->val intent=Intent(context,ReminderReceiver::class.java).apply{action=ACTION_FIRE_REMINDER;data=android.net.Uri.parse("mynotes-internal://reminder/${reminder.id}/early/$minutes");putExtra(EXTRA_REMINDER_ID,reminder.id);putExtra(EXTRA_SCHEDULED_AT,reminder.triggerAtMillis);putExtra(EXTRA_EARLY_MINUTES,minutes)};set(context,reminder.triggerAtMillis-minutes*60000L,PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))}
        }
        val alarmAt = ReminderSchedule.alarmAt(reminder, System.currentTimeMillis()) ?: run { cancel(context, reminder.id); return }
        val manager = alarmManager(context)
        val operation = pendingIntent(context, reminder.id, alarmAt, alarmAt == reminder.snoozedUntil)
        set(context,alarmAt,operation)
    }
    private fun set(context:Context,at:Long,operation:PendingIntent){val manager=alarmManager(context);if(Build.VERSION.SDK_INT<Build.VERSION_CODES.S)manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,operation)else manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,operation)}
    private fun cancelEarly(context:Context,id:Long){val prefs=context.getSharedPreferences("reminder_offsets",Context.MODE_PRIVATE);prefs.getString(id.toString(),"").orEmpty().split(',').mapNotNull(String::toIntOrNull).forEach{minutes->val intent=Intent(context,ReminderReceiver::class.java).apply{action=ACTION_FIRE_REMINDER;data=android.net.Uri.parse("mynotes-internal://reminder/$id/early/$minutes")};PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)?.let{alarmManager(context).cancel(it);it.cancel()}};prefs.edit().remove(id.toString()).apply()}

    fun cancel(context: Context, reminderId: Long) {
        cancelEarly(context,reminderId)
        val manager = alarmManager(context)
        manager.cancel(pendingIntent(context, reminderId))
    }

    private fun alarmManager(context: Context): AlarmManager =
        cachedAlarmManager ?: synchronized(this) {
            cachedAlarmManager ?: (context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
                .also { cachedAlarmManager = it }
        }

    private fun pendingIntent(context: Context, reminderId: Long, scheduledAt: Long = 0L, snoozed: Boolean = false): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_FIRE_REMINDER
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_SCHEDULED_AT, scheduledAt)
            putExtra(EXTRA_IS_SNOOZE, snoozed)
        }
        return PendingIntent.getBroadcast(
            context,
            (reminderId xor (reminderId ushr 32)).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
