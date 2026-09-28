package com.example.mynotes.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.example.mynotes.MainActivity
import com.example.mynotes.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED || intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            ReminderRepository.getInstance(context).rescheduleAll()
            return
        }
        if (intent?.action != ReminderAlarmScheduler.ACTION_FIRE_REMINDER) return
        val id = intent.getLongExtra(ReminderAlarmScheduler.EXTRA_REMINDER_ID, -1L)
        if (id <= 0L) return
        val repository = ReminderRepository.getInstance(context)
        val reminder = repository.getReminder(id) ?: return
        if (!reminder.enabled) return
        showNotification(context, reminder)
        repository.advanceAfterTrigger(id)
    }

    private fun showNotification(context: Context, reminder: Reminder) {
        val textContext = localizedContext(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // El feedback audible/háptico lo controla MyNotes para respetar
            // las opciones de Configuración y evitar duplicarlo con el canal.
            notificationManager.deleteNotificationChannel(OLD_CHANNEL_ID)
            val channel = NotificationChannel(
                CHANNEL_ID,
                textContext.getString(R.string.reminder_notification_channel),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = textContext.getString(R.string.reminder_notification_channel_description)
                setSound(null, null)
                enableVibration(false)
                vibrationPattern = null
            }
            notificationManager.createNotificationChannel(channel)
        }
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val contentIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_OPEN_REMINDERS, true)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = when (reminder.priority) {
            Reminder.PRIORITY_HIGH -> NotificationCompat.PRIORITY_HIGH
            Reminder.PRIORITY_LOW -> NotificationCompat.PRIORITY_LOW
            else -> NotificationCompat.PRIORITY_DEFAULT
        }
        val title = reminder.title.ifBlank { textContext.getString(R.string.reminder_untitled) }
        val description = reminder.description.ifBlank {
            textContext.getString(R.string.reminder_notification_default_text)
        }
        val visual = ReminderFeedbackPreferences.read(context)
        val accent = reminderAccent(reminder.colorKey, visual.notificationAccent, visual.notificationBackground)
        val expandedView = buildCustomView(
            context = context,
            textContext = textContext,
            layoutId = R.layout.notification_reminder_expanded,
            reminder = reminder,
            title = title,
            description = description,
            visual = visual,
            accent = accent,
            contentIntent = contentIntent,
            expanded = true
        )
        val headsUpView = buildCustomView(
            context = context,
            textContext = textContext,
            layoutId = R.layout.notification_reminder_heads_up,
            reminder = reminder,
            title = title,
            description = description,
            visual = visual,
            accent = accent,
            contentIntent = contentIntent,
            expanded = false,
            headsUp = true
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminder_notification)
            .setContentTitle(title)
            .setContentText(description)
            .setSubText(textContext.getString(R.string.reminders))
            .setCustomBigContentView(expandedView)
            .setCustomHeadsUpContentView(headsUpView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setColor(accent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPriority(priority)
            .setWhen(reminder.triggerAtMillis)
            .setShowWhen(true)
            .setSound(null)
            .setVibrate(longArrayOf(0L))
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(context).notify(reminder.id.toInt(), notification)
        ReminderFeedbackPreferences.playReminderAlert(context)
    }

    private fun buildCustomView(
        context: Context,
        textContext: Context,
        layoutId: Int,
        reminder: Reminder,
        title: String,
        description: String,
        visual: ReminderFeedbackPreferences.Snapshot,
        accent: Int,
        contentIntent: PendingIntent,
        expanded: Boolean,
        headsUp: Boolean = false
    ): RemoteViews {
        val view = RemoteViews(context.packageName, layoutId)
        view.setInt(R.id.reminder_notification_background, "setColorFilter", visual.notificationBackground)
        view.setInt(R.id.reminder_notification_accent, "setColorFilter", accent)
        view.setInt(R.id.reminder_notification_icon, "setColorFilter", accent)
        view.setTextViewText(
            R.id.reminder_notification_label,
            "MyNotes • ${textContext.getString(R.string.reminders)}"
        )
        view.setTextViewText(R.id.reminder_notification_title, title)
        view.setTextViewText(R.id.reminder_notification_description, description)
        view.setTextColor(R.id.reminder_notification_label, visual.notificationSecondaryText)
        view.setTextColor(R.id.reminder_notification_title, visual.notificationText)
        view.setTextColor(R.id.reminder_notification_description, visual.notificationSecondaryText)

        // Las notificaciones personalizadas tienen límites de altura estrictos,
        // especialmente en Samsung/Android 9. Se conserva la escala elegida
        // por el usuario, pero dentro de un rango que no pueda recortar la vista.
        val base = visual.notificationFontSize.coerceIn(12f, 17f)
        when {
            headsUp -> {
                view.setTextViewTextSize(R.id.reminder_notification_label, TypedValue.COMPLEX_UNIT_SP, (base - 5f).coerceIn(8f, 10f))
                view.setTextViewTextSize(R.id.reminder_notification_title, TypedValue.COMPLEX_UNIT_SP, base.coerceIn(13f, 16f))
                view.setTextViewTextSize(R.id.reminder_notification_description, TypedValue.COMPLEX_UNIT_SP, (base - 3f).coerceIn(9f, 12f))
            }
            else -> {
                view.setTextViewTextSize(R.id.reminder_notification_label, TypedValue.COMPLEX_UNIT_SP, (base - 5f).coerceIn(8f, 11f))
                view.setTextViewTextSize(R.id.reminder_notification_title, TypedValue.COMPLEX_UNIT_SP, base.coerceIn(13f, 17f))
                view.setTextViewTextSize(R.id.reminder_notification_description, TypedValue.COMPLEX_UNIT_SP, (base - 3f).coerceIn(9f, 13f))
            }
        }
        view.setOnClickPendingIntent(R.id.reminder_notification_root, contentIntent)

        if (expanded) {
            view.setTextViewText(R.id.reminder_notification_metadata, metadata(textContext, reminder))
            view.setTextColor(R.id.reminder_notification_metadata, visual.notificationSecondaryText)
            view.setTextViewTextSize(
                R.id.reminder_notification_metadata,
                TypedValue.COMPLEX_UNIT_SP,
                (base - 5f).coerceAtLeast(9f)
            )
            view.setViewVisibility(R.id.reminder_notification_metadata, View.VISIBLE)
        }
        return view
    }

    private fun metadata(context: Context, reminder: Reminder): String {
        val locale = context.resources.configuration.locales[0]
        val dateTime = SimpleDateFormat("EEE, d MMM • HH:mm", locale).format(Date(reminder.triggerAtMillis))
        val repeat = when (reminder.repeatMode) {
            Reminder.REPEAT_DAILY -> context.getString(R.string.reminder_repeat_daily)
            Reminder.REPEAT_WEEKDAYS -> context.getString(R.string.reminder_repeat_weekdays)
            Reminder.REPEAT_WEEKLY -> context.getString(R.string.reminder_repeat_weekly)
            Reminder.REPEAT_MONTHLY -> context.getString(R.string.reminder_repeat_monthly)
            else -> context.getString(R.string.reminder_repeat_none)
        }
        val priority = when (reminder.priority) {
            Reminder.PRIORITY_HIGH -> context.getString(R.string.reminder_priority_high)
            Reminder.PRIORITY_LOW -> context.getString(R.string.reminder_priority_low)
            else -> context.getString(R.string.reminder_priority_normal)
        }
        return "$dateTime  •  $repeat  •  $priority"
    }

    private fun reminderAccent(colorKey: String, paletteAccent: Int, background: Int): Int {
        val preferred = when (colorKey) {
            "blue" -> 0xFF9DB4FF.toInt()
            "purple" -> 0xFFB49BFF.toInt()
            "pink" -> 0xFFFFA9C7.toInt()
            "red" -> 0xFFFF9A9A.toInt()
            "orange" -> 0xFFFFBD82.toInt()
            "yellow" -> 0xFFFFDC78.toInt()
            "green" -> 0xFF8FD3A8.toInt()
            "teal" -> 0xFF78D7D0.toInt()
            "gray" -> 0xFFB8BEC9.toInt()
            else -> paletteAccent
        }
        if (ColorUtils.calculateContrast(preferred, background) >= 2.25) return preferred
        val target = if (ColorUtils.calculateLuminance(background) > 0.5) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        var best = preferred
        for (step in 1..10) {
            val candidate = ColorUtils.blendARGB(preferred, target, step / 10f)
            best = candidate
            if (ColorUtils.calculateContrast(candidate, background) >= 2.25) return candidate
        }
        return best
    }

    private fun localizedContext(context: Context): Context {
        val language = context.getSharedPreferences("locale_prefs", Context.MODE_PRIVATE)
            .getString("language", "system") ?: "system"
        if (language == "system") return context
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(Locale.forLanguageTag(language))
        return context.createConfigurationContext(configuration)
    }

    companion object {
        private const val OLD_CHANNEL_ID = "mynotes_reminders"
        const val CHANNEL_ID = "mynotes_reminders_v2"
        const val EXTRA_OPEN_REMINDERS = "open_reminders"
    }
}
