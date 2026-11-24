package com.example.mynotes.studio

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mynotes.reminders.Reminder
import com.example.mynotes.reminders.ReminderSchedule
import com.example.mynotes.ui.components.AppAlertDialog
import com.example.mynotes.ui.sound.UiSoundPlayer
import java.text.DateFormat
import java.util.Date

/** Vista activa del calendario de repeticiones; sin editor de ocurrencias sin acceso. */
@Composable
internal fun PlusRecurrencePreview(reminder: Reminder) {
    var show by remember { mutableStateOf(false) }
    TextButton(onClick = { show = true; UiSoundPlayer.playFallbackClick() },
        enabled = reminder.repeatMode != Reminder.REPEAT_NONE) {
        Text("Preview upcoming dates")
    }
    if (show) PlusDatesDialog(reminder) { show = false }
}

@Composable
private fun PlusDatesDialog(reminder: Reminder, onClose: () -> Unit) {
    val dates = remember(reminder) { ReminderSchedule.preview(reminder, System.currentTimeMillis(), 40) }
    AppAlertDialog(onDismissRequest = onClose,
        title = { Text("Upcoming · ${reminder.title}") },
        text = {
            LazyColumn(Modifier.heightIn(max = 430.dp)) {
                if (dates.isEmpty()) item {
                    Text("No remaining occurrences. Check the series end date and occurrence limit.")
                }
                items(dates) { date ->
                    Row {
                        Text(DateFormat.getDateTimeInstance().format(Date(date)),
                            Modifier.weight(1f).padding(vertical = 12.dp))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onClose(); UiSoundPlayer.playFallbackClick() }) { Text("Close") } }
    )
}
