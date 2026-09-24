package com.example.mynotes.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.R
import com.example.mynotes.reminders.Reminder
import com.example.mynotes.reminders.ReminderRepository
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.motion.ConfigurableAnimatedContent
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.appFontFamily
import com.example.mynotes.ui.theme.automaticUiTextColor
import com.example.mynotes.ui.theme.resolveSecondaryUiTextColor
import com.example.mynotes.ui.theme.resolveUiGraphicColor
import com.example.mynotes.ui.theme.resolveUiTextColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class ReminderColorOption(val key: String, val color: Color)

private val ReminderFixedColors = listOf(
    ReminderColorOption("blue", Color(0xFF9DB4FF)),
    ReminderColorOption("purple", Color(0xFFB49BFF)),
    ReminderColorOption("pink", Color(0xFFFFA9C7)),
    ReminderColorOption("red", Color(0xFFFF9A9A)),
    ReminderColorOption("orange", Color(0xFFFFBD82)),
    ReminderColorOption("yellow", Color(0xFFFFDC78)),
    ReminderColorOption("green", Color(0xFF8FD3A8)),
    ReminderColorOption("teal", Color(0xFF78D7D0)),
    ReminderColorOption("gray", Color(0xFFB8BEC9))
)

@Composable
private fun reminderColor(key: String): Color {
    if (key == "palette") return MaterialTheme.colorScheme.primary
    return ReminderFixedColors.firstOrNull { it.key == key }?.color ?: MaterialTheme.colorScheme.primary
}

@Composable
private fun reminderLocale(): Locale {
    val configuration = LocalConfiguration.current
    return remember(configuration) {
        configuration.locales[0] ?: Locale.getDefault()
    }
}

@Composable
fun ReminderScreen(
    settings: AppSettings,
    repository: ReminderRepository,
    onBack: () -> Unit,
    initialCreate: Boolean = false
) {
    val reminders by repository.reminders.collectAsStateWithLifecycle()
    var editingReminderId by rememberSaveable { mutableStateOf<Long?>(null) }
    var creatingReminder by rememberSaveable { mutableStateOf(initialCreate) }

    val editingReminder = editingReminderId?.let { id -> reminders.firstOrNull { it.id == id } }
    val editorVisible = creatingReminder || editingReminder != null

    BackHandler {
        if (editorVisible) {
            creatingReminder = false
            editingReminderId = null
        } else {
            onBack()
        }
    }

    ConfigurableAnimatedContent(
        targetState = editorVisible,
        animationsEnabled = settings.animationsEnabled,
        animationSpeed = settings.animationSpeed,
        animationStyle = settings.animationStyle,
        animationEasing = settings.animationEasing,
        animationIntensity = settings.animationIntensity,
        performanceMode = settings.performanceMode
    ) { showEditor ->
        if (showEditor) {
            ReminderEditor(
                settings = settings,
                reminder = editingReminder,
                repository = repository,
                onDone = {
                    creatingReminder = false
                    editingReminderId = null
                },
                onCancel = {
                    creatingReminder = false
                    editingReminderId = null
                }
            )
        } else {
            ReminderList(
                settings = settings,
                reminders = reminders,
                onBack = onBack,
                onCreate = { creatingReminder = true },
                onEdit = { editingReminderId = it.id },
                onToggle = { reminder, enabled -> repository.setEnabled(reminder.id, enabled) }
            )
        }
    }
}

@Composable
private fun ReminderList(
    settings: AppSettings,
    reminders: List<Reminder>,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onEdit: (Reminder) -> Unit,
    onToggle: (Reminder, Boolean) -> Unit
) {
    val context = LocalContext.current
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    val baseFontSize = settings.fontSize.coerceIn(12f, 24f)
    val iconSize = settings.iconSize.coerceIn(18f, 34f)
    val background = MaterialTheme.colorScheme.background
    val panel = MaterialTheme.colorScheme.surfaceContainer
    val primaryText = resolveUiTextColor(settings.textColor, background)
    val secondaryText = resolveSecondaryUiTextColor(settings.textColor, background)
    val panelText = resolveUiTextColor(settings.textColor, panel)
    val panelSecondary = resolveSecondaryUiTextColor(settings.textColor, panel)
    val activeCount = remember(reminders) { reminders.count { it.enabled } }

    Scaffold(
        containerColor = background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    UiSoundPlayer.playAction(context, UiActionSound.Open)
                    onCreate()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape((settings.noteCardCornerRadius + 2f).coerceIn(16f, 28f).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.reminder_new),
                    modifier = Modifier.size(iconSize.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        UiSoundPlayer.playAction(context, UiActionSound.Back)
                        onBack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = primaryText,
                            modifier = Modifier.size(iconSize.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.reminders),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = (baseFontSize + 10f).sp,
                            color = primaryText
                        )
                        Text(
                            text = stringResource(R.string.reminder_active_count, activeCount),
                            fontFamily = fontFamily,
                            fontSize = (baseFontSize - 2f).coerceAtLeast(11f).sp,
                            color = secondaryText
                        )
                    }
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape((settings.noteCardCornerRadius + 6f).coerceIn(14f, 34f).dp),
                    color = panel,
                    tonalElevation = settings.noteCardElevation.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(iconSize.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                stringResource(R.string.reminder_panel_title),
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = baseFontSize.sp,
                                color = panelText
                            )
                            Text(
                                stringResource(R.string.reminder_panel_description),
                                fontFamily = fontFamily,
                                fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp,
                                color = panelSecondary
                            )
                        }
                    }
                }
            }

            if (reminders.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp),
                            tint = secondaryText
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.reminder_empty),
                            fontFamily = fontFamily,
                            fontSize = baseFontSize.sp,
                            color = primaryText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.reminder_empty_description),
                            fontFamily = fontFamily,
                            fontSize = (baseFontSize - 2f).coerceAtLeast(11f).sp,
                            color = secondaryText
                        )
                    }
                }
            } else {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        settings = settings,
                        onEdit = {
                            UiSoundPlayer.playAction(context, UiActionSound.Open)
                            onEdit(reminder)
                        },
                        onToggle = { checked ->
                            UiSoundPlayer.playToggle(context, checked)
                            onToggle(reminder, checked)
                        }
                    )
                }
            }
            item { Spacer(Modifier.height(96.dp)) }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    settings: AppSettings,
    onEdit: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    val baseFontSize = settings.fontSize.coerceIn(12f, 24f)
    val iconSize = settings.iconSize.coerceIn(18f, 34f)
    val container = MaterialTheme.colorScheme.surfaceContainerLow
    val primary = resolveUiTextColor(settings.textColor, container)
    val secondary = resolveSecondaryUiTextColor(settings.textColor, container)
    val locale = reminderLocale()
    val dateFormatter = remember(locale) { SimpleDateFormat("EEE, d MMM · HH:mm", locale) }
    val indicatorColor = reminderColor(reminder.colorKey)

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit),
        shape = RoundedCornerShape(settings.noteCardCornerRadius.coerceIn(8f, 34f).dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = settings.noteCardElevation.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.width(7.dp).height(116.dp).background(indicatorColor))
            Column(modifier = Modifier.weight(1f).padding(settings.noteCardPadding.coerceIn(8f, 22f).dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reminder.title.ifBlank { stringResource(R.string.reminder_untitled) },
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = (baseFontSize + 1f).sp,
                            color = primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (reminder.description.isNotBlank()) {
                            Text(
                                text = reminder.description,
                                fontFamily = fontFamily,
                                fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp,
                                color = secondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Switch(checked = reminder.enabled, onCheckedChange = onToggle)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size((iconSize * 0.72f).coerceAtLeast(14f).dp),
                        tint = secondary
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        dateFormatter.format(Date(reminder.triggerAtMillis)),
                        fontFamily = fontFamily,
                        fontSize = (baseFontSize - 4f).coerceAtLeast(10f).sp,
                        color = secondary
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        repeatLabel(reminder.repeatMode),
                        fontFamily = fontFamily,
                        fontSize = (baseFontSize - 4f).coerceAtLeast(10f).sp,
                        color = secondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_note),
                        modifier = Modifier.size((iconSize * 0.78f).coerceAtLeast(15f).dp),
                        tint = secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderEditor(
    settings: AppSettings,
    reminder: Reminder?,
    repository: ReminderRepository,
    onDone: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val fontFamily = remember(settings.font) { appFontFamily(settings.font) }
    val baseFontSize = settings.fontSize.coerceIn(12f, 24f)
    val iconSize = settings.iconSize.coerceIn(18f, 34f)
    val initialTrigger = remember(reminder?.id) {
        val now = System.currentTimeMillis()
        val saved = reminder?.triggerAtMillis
        if (saved != null && saved > now) saved else now + 5L * 60L * 1000L
    }
    var title by remember(reminder?.id) { mutableStateOf(reminder?.title.orEmpty()) }
    var description by remember(reminder?.id) { mutableStateOf(reminder?.description.orEmpty()) }
    var triggerAtMillis by remember(reminder?.id) { mutableLongStateOf(initialTrigger) }
    var repeatMode by remember(reminder?.id) { mutableStateOf(reminder?.repeatMode ?: Reminder.REPEAT_NONE) }
    var priority by remember(reminder?.id) { mutableStateOf(reminder?.priority ?: Reminder.PRIORITY_NORMAL) }
    var colorKey by remember(reminder?.id) { mutableStateOf(reminder?.colorKey ?: "palette") }
    var enabled by remember(reminder?.id) { mutableStateOf(reminder?.enabled ?: true) }
    var validationError by remember(reminder?.id) { mutableStateOf<String?>(null) }

    val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    val background = MaterialTheme.colorScheme.background
    val contentPanel = MaterialTheme.colorScheme.surfaceContainer
    val primaryText = resolveUiTextColor(settings.textColor, background)
    val secondaryText = resolveSecondaryUiTextColor(settings.textColor, background)
    val contentPanelText = resolveUiTextColor(settings.textColor, contentPanel)
    val locale = reminderLocale()
    val dateFormatter = remember(locale) { SimpleDateFormat("EEE, d MMM yyyy", locale) }
    val timeFormatter = remember(locale) { SimpleDateFormat("HH:mm", locale) }

    fun saveReminder() {
        if (title.isBlank()) {
            validationError = context.getString(R.string.reminder_title_required)
            return
        }
        if (enabled && triggerAtMillis <= System.currentTimeMillis()) {
            validationError = context.getString(R.string.reminder_future_time_required)
            return
        }
        if (enabled && Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        repository.upsert(
            Reminder(
                id = reminder?.id ?: System.currentTimeMillis(),
                title = title.trim(),
                description = description.trim(),
                triggerAtMillis = triggerAtMillis,
                repeatMode = repeatMode,
                priority = priority,
                colorKey = colorKey,
                enabled = enabled,
                createdAt = reminder?.createdAt ?: System.currentTimeMillis()
            )
        )
        UiSoundPlayer.playAction(context, UiActionSound.Save)
        onDone()
    }

    Scaffold(containerColor = background) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(onClick = {
                        UiSoundPlayer.playAction(context, UiActionSound.Back)
                        onCancel()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = primaryText,
                            modifier = Modifier.size(iconSize.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 2.dp)
                    ) {
                        Text(
                            text = stringResource(if (reminder == null) R.string.reminder_create_title else R.string.reminder_edit_title),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = (baseFontSize + 9f).sp,
                            color = primaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Reserve one extra visual line under the title so the
                        // subtitle starts below the Save button instead of
                        // competing with it for the same vertical area.
                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            stringResource(R.string.reminder_editor_subtitle),
                            modifier = Modifier.padding(end = 4.dp),
                            fontFamily = fontFamily,
                            color = secondaryText,
                            fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = ::saveReminder,
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text(
                            stringResource(R.string.save),
                            fontFamily = fontFamily,
                            fontSize = (baseFontSize - 2f).coerceAtLeast(11f).sp,
                            maxLines = 1
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape((settings.noteCardCornerRadius + 6f).coerceIn(14f, 34f).dp),
                    colors = CardDefaults.cardColors(containerColor = contentPanel),
                    elevation = CardDefaults.cardElevation(defaultElevation = settings.noteCardElevation.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            stringResource(R.string.reminder_content_section),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = baseFontSize.sp,
                            color = contentPanelText
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it; validationError = null },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = fontFamily, fontSize = baseFontSize.sp),
                            label = { Text(stringResource(R.string.reminder_title), fontFamily = fontFamily) }
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = fontFamily, fontSize = baseFontSize.sp),
                            label = { Text(stringResource(R.string.reminder_description), fontFamily = fontFamily) }
                        )
                        validationError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, fontFamily = fontFamily, fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp)
                        }
                    }
                }
            }

            item {
                ReminderToolsPanel(
                    settings = settings,
                    fontFamily = fontFamily,
                    triggerAtMillis = triggerAtMillis,
                    onTriggerChange = { triggerAtMillis = it; validationError = null },
                    repeatMode = repeatMode,
                    onRepeatChange = { repeatMode = it },
                    priority = priority,
                    onPriorityChange = { priority = it },
                    colorKey = colorKey,
                    onColorChange = { colorKey = it },
                    enabled = enabled,
                    onEnabledChange = { enabled = it },
                    dateText = dateFormatter.format(Date(triggerAtMillis)),
                    timeText = timeFormatter.format(Date(triggerAtMillis))
                )
            }

            if (reminder != null) {
                item {
                    OutlinedButton(
                        onClick = {
                            UiSoundPlayer.play(context, UiSound.Delete)
                            repository.delete(reminder.id)
                            onDone()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(iconSize.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.reminder_delete), fontFamily = fontFamily, fontSize = (baseFontSize - 1f).sp)
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun ReminderToolsPanel(
    settings: AppSettings,
    fontFamily: FontFamily,
    triggerAtMillis: Long,
    onTriggerChange: (Long) -> Unit,
    repeatMode: String,
    onRepeatChange: (String) -> Unit,
    priority: String,
    onPriorityChange: (String) -> Unit,
    colorKey: String,
    onColorChange: (String) -> Unit,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    dateText: String,
    timeText: String
) {
    val context = LocalContext.current
    val baseFontSize = settings.fontSize.coerceIn(12f, 24f)
    val panelColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val panelText = resolveUiTextColor(settings.textColor, panelColor)
    val panelSecondary = resolveSecondaryUiTextColor(settings.textColor, panelColor)
    val chipColor = MaterialTheme.colorScheme.surfaceVariant
    val chipText = resolveUiTextColor(settings.textColor, chipColor)
    val selectedChipColor = MaterialTheme.colorScheme.primary
    val selectedChipText = automaticUiTextColor(selectedChipColor)
    val paletteColor = MaterialTheme.colorScheme.primary
    val colorOptions = remember(paletteColor) {
        listOf(ReminderColorOption("palette", paletteColor)) + ReminderFixedColors
    }

    Surface(
        shape = RoundedCornerShape((settings.noteCardCornerRadius + 6f).coerceIn(14f, 34f).dp),
        color = panelColor,
        tonalElevation = settings.noteCardElevation.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                stringResource(R.string.reminder_tools),
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = panelText,
                fontSize = (baseFontSize + 1f).sp
            )

            Text(
                stringResource(R.string.reminder_when),
                fontFamily = fontFamily,
                color = panelText,
                fontWeight = FontWeight.SemiBold,
                fontSize = baseFontSize.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        UiSoundPlayer.playAction(context, UiActionSound.Menu)
                        val calendar = Calendar.getInstance().apply { timeInMillis = triggerAtMillis }
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val updated = Calendar.getInstance().apply {
                                    timeInMillis = triggerAtMillis
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, day)
                                }
                                UiSoundPlayer.playAction(context, UiActionSound.Select)
                                onTriggerChange(updated.timeInMillis)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = panelText)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(dateText, fontFamily = fontFamily, maxLines = 1, fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp)
                }
                OutlinedButton(
                    onClick = {
                        UiSoundPlayer.playAction(context, UiActionSound.Menu)
                        val calendar = Calendar.getInstance().apply { timeInMillis = triggerAtMillis }
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                val updated = Calendar.getInstance().apply {
                                    timeInMillis = triggerAtMillis
                                    set(Calendar.HOUR_OF_DAY, hour)
                                    set(Calendar.MINUTE, minute)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                UiSoundPlayer.playAction(context, UiActionSound.Select)
                                onTriggerChange(updated.timeInMillis)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = panelText)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(timeText, fontFamily = fontFamily, fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp)
                }
            }

            Text(
                stringResource(R.string.reminder_quick_time),
                fontFamily = fontFamily,
                color = panelText,
                fontWeight = FontWeight.SemiBold,
                fontSize = baseFontSize.sp
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf(5, 10, 15, 30, 60)) { minutes ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            UiSoundPlayer.playAction(context, UiActionSound.Select)
                            val now = System.currentTimeMillis()
                            val updated = Calendar.getInstance().apply {
                                timeInMillis = now + minutes * 60_000L
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                                // If rounding to the minute would accidentally land in the
                                // past (only possible around a minute boundary), keep at
                                // least one full minute in the future.
                                if (timeInMillis <= now) add(Calendar.MINUTE, 1)
                            }
                            onTriggerChange(updated.timeInMillis)
                        },
                        label = {
                            Text(
                                stringResource(R.string.reminder_in_minutes, minutes),
                                fontFamily = fontFamily,
                                fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = chipColor,
                            labelColor = chipText
                        )
                    )
                }
            }

            Text(
                stringResource(R.string.reminder_repeat),
                fontFamily = fontFamily,
                color = panelText,
                fontWeight = FontWeight.SemiBold,
                fontSize = baseFontSize.sp
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(
                    listOf(
                        Reminder.REPEAT_NONE,
                        Reminder.REPEAT_DAILY,
                        Reminder.REPEAT_WEEKDAYS,
                        Reminder.REPEAT_WEEKLY,
                        Reminder.REPEAT_MONTHLY
                    )
                ) { mode ->
                    FilterChip(
                        selected = repeatMode == mode,
                        onClick = {
                            UiSoundPlayer.playAction(context, UiActionSound.Select)
                            onRepeatChange(mode)
                        },
                        label = { Text(repeatLabel(mode), fontFamily = fontFamily, fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = chipColor,
                            labelColor = chipText,
                            selectedContainerColor = selectedChipColor,
                            selectedLabelColor = selectedChipText
                        )
                    )
                }
            }

            Text(
                stringResource(R.string.reminder_priority),
                fontFamily = fontFamily,
                color = panelText,
                fontWeight = FontWeight.SemiBold,
                fontSize = baseFontSize.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(Reminder.PRIORITY_LOW, Reminder.PRIORITY_NORMAL, Reminder.PRIORITY_HIGH).forEach { item ->
                    FilterChip(
                        selected = priority == item,
                        onClick = {
                            UiSoundPlayer.play(context, UiSound.Priority)
                            onPriorityChange(item)
                        },
                        label = { Text(priorityLabel(item), fontFamily = fontFamily, fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = chipColor,
                            labelColor = chipText,
                            selectedContainerColor = selectedChipColor,
                            selectedLabelColor = selectedChipText
                        )
                    )
                }
            }

            Text(
                stringResource(R.string.reminder_color),
                fontFamily = fontFamily,
                color = panelText,
                fontWeight = FontWeight.SemiBold,
                fontSize = baseFontSize.sp
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(colorOptions) { item ->
                    val selectedBorder = resolveUiGraphicColor(settings.textColor, item.color)
                    Surface(
                        modifier = Modifier.size(34.dp).clickable {
                            UiSoundPlayer.playAction(context, UiActionSound.Color)
                            onColorChange(item.key)
                        },
                        shape = CircleShape,
                        color = item.color,
                        border = if (item.key == colorKey) BorderStroke(3.dp, selectedBorder) else null
                    ) { }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.reminder_enabled),
                        fontFamily = fontFamily,
                        color = panelText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = baseFontSize.sp
                    )
                    Text(
                        stringResource(R.string.reminder_enabled_description),
                        fontFamily = fontFamily,
                        color = panelSecondary,
                        fontSize = (baseFontSize - 4f).coerceAtLeast(10f).sp
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        UiSoundPlayer.playToggle(context, it)
                        onEnabledChange(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun repeatLabel(mode: String): String = when (mode) {
    Reminder.REPEAT_DAILY -> stringResource(R.string.reminder_repeat_daily)
    Reminder.REPEAT_WEEKLY -> stringResource(R.string.reminder_repeat_weekly)
    Reminder.REPEAT_MONTHLY -> stringResource(R.string.reminder_repeat_monthly)
    Reminder.REPEAT_WEEKDAYS -> stringResource(R.string.reminder_repeat_weekdays)
    else -> stringResource(R.string.reminder_repeat_none)
}

@Composable
private fun priorityLabel(priority: String): String = when (priority) {
    Reminder.PRIORITY_LOW -> stringResource(R.string.reminder_priority_low)
    Reminder.PRIORITY_HIGH -> stringResource(R.string.reminder_priority_high)
    else -> stringResource(R.string.reminder_priority_normal)
}
