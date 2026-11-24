package com.example.mynotes.studio

import com.example.mynotes.ui.sound.UiSoundPlayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mynotes.R
import com.example.mynotes.reminders.Reminder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal data class StudioRepeatOptions(
    val interval: Int = 1,
    val ordinal: Int = 0,
    val weekday: Int = 2,
    val limit: Int? = null,
    val alerts: List<Int> = emptyList()
) {
    companion object {
        fun from(reminder: Reminder?) = StudioRepeatOptions(
            interval = reminder?.repeatInterval ?: 1,
            ordinal = reminder?.monthOrdinal ?: 0,
            weekday = reminder?.monthWeekday ?: 2,
            limit = reminder?.maxOccurrences,
            alerts = ReminderAdvanceOptions.normalize(reminder?.alertMinutes.orEmpty())
        )
    }
}

@Composable
private fun advanceLabel(minutes: Int): String = when {
    minutes % 1440 == 0 -> {
        val days = minutes / 1440
        stringResource(if (days == 1) R.string.reminder_advance_one_day else R.string.reminder_advance_days, days)
    }
    minutes % 60 == 0 -> {
        val hours = minutes / 60
        stringResource(if (hours == 1) R.string.reminder_advance_one_hour else R.string.reminder_advance_hours, hours)
    }
    minutes == 1 -> stringResource(R.string.reminder_advance_one_minute)
    else -> stringResource(R.string.reminder_advance_minutes, minutes)
}

private fun formattedAlertTime(millis: Long): String =
    SimpleDateFormat("EEE, d MMM · HH:mm", Locale.getDefault()).format(Date(millis))

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
internal fun StudioReminderOptions(
    value: StudioRepeatOptions,
    monthly: Boolean,
    repeating: Boolean,
    eventAtMillis: Long,
    onChange: (StudioRepeatOptions) -> Unit
) {
    var interval by remember(value.interval) { mutableStateOf(value.interval.toString()) }
    var limit by remember(value.limit) { mutableStateOf(value.limit?.toString().orEmpty()) }
    val selectedAlerts = remember(value.alerts) { ReminderAdvanceOptions.normalize(value.alerts) }
    val nowMillis = System.currentTimeMillis()
    val minutesUntilEvent = ((eventAtMillis - nowMillis) / 60_000L).coerceAtLeast(0L)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.34f)),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    stringResource(R.string.reminder_advanced_schedule),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                stringResource(R.string.reminder_advanced_schedule_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                stringResource(R.string.reminder_advanced_event_summary, formattedAlertTime(eventAtMillis)),
                style = MaterialTheme.typography.labelLarge
            )
            if (!repeating) {
                Text(
                    stringResource(R.string.reminder_repeat_unlock_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (repeating) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Repeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.reminder_repeat_rules_heading),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            stringResource(R.string.reminder_repeat_rules_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                OutlinedTextField(
                    value = interval,
                    onValueChange = {
                        interval = it.filter(Char::isDigit).take(3)
                        interval.toIntOrNull()?.takeIf { number -> number in 1..365 }
                            ?.let { number -> onChange(value.copy(interval = number)) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.reminder_repeat_interval)) },
                    supportingText = { Text(stringResource(R.string.reminder_repeat_interval_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = interval.toIntOrNull() !in 1..365
                )
                OutlinedTextField(
                    value = limit,
                    onValueChange = {
                        limit = it.filter(Char::isDigit).take(6)
                        if (limit.isBlank()) onChange(value.copy(limit = null))
                        else limit.toIntOrNull()?.takeIf { number -> number in 1..100000 }
                            ?.let { number -> onChange(value.copy(limit = number)) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.reminder_repeat_limit)) },
                    supportingText = { Text(stringResource(R.string.reminder_repeat_limit_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = limit.isNotBlank() && limit.toIntOrNull() !in 1..100000
                )
                if (monthly) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        stringResource(R.string.reminder_monthly_pattern),
                        style = MaterialTheme.typography.labelLarge
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            0 to stringResource(R.string.reminder_month_same_day),
                            1 to stringResource(R.string.reminder_month_first),
                            2 to stringResource(R.string.reminder_month_second),
                            3 to stringResource(R.string.reminder_month_third),
                            4 to stringResource(R.string.reminder_month_fourth),
                            5 to stringResource(R.string.reminder_month_fifth),
                            -1 to stringResource(R.string.reminder_month_last)
                        ).forEach { (ordinal, label) ->
                            FilterChip(
                                selected = value.ordinal == ordinal,
                                onClick = {
                                    onChange(value.copy(ordinal = ordinal))
                                    UiSoundPlayer.playFallbackClick()
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                    if (value.ordinal != 0) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val names = java.text.DateFormatSymbols.getInstance().shortWeekdays
                            (1..7).forEach { day ->
                                FilterChip(
                                    selected = value.weekday == day,
                                    onClick = {
                                        onChange(value.copy(weekday = day))
                                        UiSoundPlayer.playFallbackClick()
                                    },
                                    label = { Text(names[day]) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.reminder_advance_heading),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        stringResource(R.string.reminder_advance_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.reminder_main_alert_label),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            formattedAlertTime(eventAtMillis),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        stringResource(R.string.reminder_always_on),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            fun updateAlerts(next: List<Int>) {
                onChange(value.copy(alerts = ReminderAdvanceOptions.normalize(next)))
            }

            Text(
                stringResource(R.string.reminder_advance_quick_options),
                style = MaterialTheme.typography.labelLarge
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(1, 5, 10, 30, 60, 1440).forEach { minutes ->
                    val selected = minutes in selectedAlerts
                    val upcoming = ReminderAdvanceOptions.isUpcoming(eventAtMillis, minutes, nowMillis)
                    FilterChip(
                        selected = selected,
                        enabled = selected || (selectedAlerts.size < ReminderAdvanceOptions.MAX_ALERTS && upcoming),
                        onClick = {
                            updateAlerts(if (selected) selectedAlerts - minutes else selectedAlerts + minutes)
                            UiSoundPlayer.playFallbackClick()
                        },
                        label = { Text(advanceLabel(minutes)) }
                    )
                }
            }
            Text(
                stringResource(R.string.reminder_advance_availability_hint, minutesUntilEvent),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            var amountText by rememberSaveable { mutableStateOf("1") }
            var selectedUnitName by rememberSaveable { mutableStateOf(ReminderAdvanceOptions.Unit.MINUTES.name) }
            val unit = ReminderAdvanceOptions.Unit.valueOf(selectedUnitName)
            val amount = amountText.toIntOrNull()
            val candidate = ReminderAdvanceOptions.toMinutes(amount, unit)
            val canAdd = candidate != null && candidate !in selectedAlerts &&
                selectedAlerts.size < ReminderAdvanceOptions.MAX_ALERTS &&
                ReminderAdvanceOptions.isUpcoming(eventAtMillis, candidate, nowMillis)

            Text(
                stringResource(R.string.reminder_advance_custom),
                style = MaterialTheme.typography.labelLarge
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        amountText,
                        onValueChange = { amountText = it.filter(Char::isDigit).take(5) },
                        modifier = Modifier.width(112.dp),
                        singleLine = true,
                        label = { Text(stringResource(R.string.reminder_advance_amount)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = candidate == null
                    )
                    Spacer(Modifier.width(10.dp))
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ReminderAdvanceOptions.Unit.entries.forEach { choice ->
                            val unitLabel = when (choice) {
                                ReminderAdvanceOptions.Unit.MINUTES -> stringResource(R.string.reminder_advance_unit_minutes)
                                ReminderAdvanceOptions.Unit.HOURS -> stringResource(R.string.reminder_advance_unit_hours)
                                ReminderAdvanceOptions.Unit.DAYS -> stringResource(R.string.reminder_advance_unit_days)
                            }
                            FilterChip(
                                selected = unit == choice,
                                onClick = {
                                    selectedUnitName = choice.name
                                    UiSoundPlayer.playFallbackClick()
                                },
                                label = { Text(unitLabel) }
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        candidate?.let { updateAlerts(selectedAlerts + it) }
                        UiSoundPlayer.playFallbackClick()
                    },
                    enabled = canAdd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.reminder_advance_add))
                }
                val status = when {
                    candidate == null -> R.string.reminder_advance_invalid
                    candidate in selectedAlerts -> R.string.reminder_advance_duplicate
                    selectedAlerts.size >= ReminderAdvanceOptions.MAX_ALERTS -> R.string.reminder_advance_limit
                    !ReminderAdvanceOptions.isUpcoming(eventAtMillis, candidate, nowMillis) -> R.string.reminder_advance_past
                    else -> R.string.reminder_advance_help
                }
                Text(
                    stringResource(status),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (canAdd || status == R.string.reminder_advance_help) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Text(
                stringResource(R.string.reminder_notification_plan),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            val validAlerts = selectedAlerts.filter {
                ReminderAdvanceOptions.isUpcoming(eventAtMillis, it, nowMillis)
            }.sortedDescending()

            if (validAlerts.isEmpty()) {
                Text(
                    stringResource(R.string.reminder_advance_none),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                validAlerts.forEach { minutes ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, top = 7.dp, bottom = 7.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    advanceLabel(minutes),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    formattedAlertTime(eventAtMillis - minutes.toLong() * 60_000L),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = {
                                updateAlerts(selectedAlerts - minutes)
                                UiSoundPlayer.playFallbackClick()
                            }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(R.string.reminder_advance_remove)
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.reminder_main_alert_label),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            formattedAlertTime(eventAtMillis),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        stringResource(R.string.reminder_required),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (selectedAlerts.any { !ReminderAdvanceOptions.isUpcoming(eventAtMillis, it, nowMillis) }) {
                Text(
                    stringResource(R.string.reminder_advance_past_selected),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
