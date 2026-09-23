package com.example.mynotes.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object ReminderAlarmScheduler {
    const val ACTION_FIRE_REMINDER = "com.example.mynotes.action.FIRE_REMINDER"
    const val EXTRA_REMINDER_ID = "reminder_id"

    fun schedule(context: Context, reminder: Reminder) {
        if (!reminder.enabled) return
        if (reminder.triggerAtMillis <= System.currentTimeMillis()) return
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val operation = pendingIntent(context, reminder.id)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // En Android 7–11 podemos conservar la hora exacta sin permiso especial.
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.triggerAtMillis,
                operation
            )
        } else {
            // Android 12+ puede exigir acceso especial para alarmas exactas.
            // MyNotes no obliga a concederlo: usa una alarma compatible si no está disponible.
            manager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.triggerAtMillis,
                operation
            )
        }
    }

    fun cancel(context: Context, reminderId: Long) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pendingIntent(context, reminderId))
    }

    private fun pendingIntent(context: Context, reminderId: Long): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_FIRE_REMINDER
            putExtra(EXTRA_REMINDER_ID, reminderId)
        }
        return PendingIntent.getBroadcast(
            context,
            (reminderId xor (reminderId ushr 32)).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
