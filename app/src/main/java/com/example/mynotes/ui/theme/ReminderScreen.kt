package com.example.mynotes.ui

import com.example.mynotes.ui.components.appScreenBackground
import com.example.mynotes.ui.preview.LocalLayoutPreview
import com.example.mynotes.ui.components.AppHeading
import com.example.mynotes.ui.theme.rememberUiTextColors
import com.example.mynotes.ui.theme.rememberUiTextColor
import com.example.mynotes.ui.theme.rememberUiGraphicColor
import com.example.mynotes.ui.components.AppCircularIconButton
import com.example.mynotes.ui.workspace.PaletteWorkspaceTheme
import com.example.mynotes.ui.components.AppIconLabel
import com.example.mynotes.ui.components.installDialogNavigationBarPolicy

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.mynotes.ui.state.ReminderActions
import com.example.mynotes.settings.AppSettings
import com.example.mynotes.ui.motion.ConfigurableAnimatedContent
import com.example.mynotes.ui.sound.UiActionSound
import com.example.mynotes.ui.sound.UiSound
import com.example.mynotes.ui.sound.UiSoundPlayer
import com.example.mynotes.ui.theme.rememberAppFontFamily
import com.example.mynotes.ui.theme.automaticUiTextColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val ReminderQuickMinutes = listOf(5, 10, 15, 30, 60)
private val ReminderRepeatModes = listOf(
    Reminder.REPEAT_NONE, Reminder.REPEAT_DAILY, Reminder.REPEAT_WEEKDAYS,
    Reminder.REPEAT_WEEKLY, Reminder.REPEAT_MONTHLY, Reminder.REPEAT_CUSTOM
)
private val ReminderPriorities = listOf(Reminder.PRIORITY_LOW, Reminder.PRIORITY_NORMAL, Reminder.PRIORITY_HIGH)

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
private val ReminderFixedColorByKey = ReminderFixedColors.associate { it.key to it.color }

@Composable
private fun reminderColor(key: String): Color {
    if (key == "palette") return MaterialTheme.colorScheme.primary
    return ReminderFixedColorByKey[key] ?: MaterialTheme.colorScheme.primary
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
    repository: ReminderActions,
    onBack: () -> Unit,
    initialCreate: Boolean = false,
    noteCatalog: List<com.example.mynotes.data.Note> = emptyList()
) {
    val layoutPreview = LocalLayoutPreview.current
    val reminderContext = LocalContext.current
    val reminders by repository.reminders.collectAsStateWithLifecycle()
    var editingReminderId by rememberSaveable { mutableStateOf(layoutPreview?.reminderEditingId) }
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

    PaletteWorkspaceTheme(settings.surfacePanelIntensity) {
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
                    noteCatalog = noteCatalog,
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
                    onToggle = { reminder, enabled ->
                        if (enabled && com.example.mynotes.reminders.ReminderSchedule.enableAt(reminder, System.currentTimeMillis()) == null) {
                            android.widget.Toast.makeText(reminderContext, R.string.workspace_end_before_start, android.widget.Toast.LENGTH_LONG).show()
                        } else repository.setEnabled(reminder.id, enabled)
                    }
                )
            }
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
    val fontFamily = rememberAppFontFamily(settings.font)
    val baseFontSize = settings.fontSize.coerceIn(12f, 24f)
    val iconSize = settings.iconSize.coerceIn(18f, 34f)
    val background = MaterialTheme.colorScheme.background
    val panel = MaterialTheme.colorScheme.surfaceContainer
    val (primaryText, secondaryText) = rememberUiTextColors(settings.textColor, background)
    val (panelText, panelSecondary) = rememberUiTextColors(settings.textColor, panel)
    val activeCount = remember(reminders) { reminders.count { it.enabled || it.snoozedUntil != null } }

    Scaffold(
        modifier = Modifier.appScreenBackground(background, primaryText),
        containerColor = Color.Transparent,
        contentColor = primaryText,
        floatingActionButton = {
            FloatingActionButton(
                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Open, onCreate),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp, pressedElevation = 0.dp,
                    focusedElevation = 0.dp, hoveredElevation = 0.dp
                ),
                shape = CircleShape
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
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReminderBackButton(primaryText, iconSize, UiSoundPlayer.actionHandler(context, UiActionSound.Back, onBack))
                    Column(modifier = Modifier.weight(1f)) {
                        AppHeading(
                            title = stringResource(R.string.reminders), subtitle = stringResource(R.string.reminder_active_count, activeCount),
                            fontFamily = fontFamily, titleColor = primaryText, subtitleColor = secondaryText,
                            titleSize = (baseFontSize + 10f).sp,
                            subtitleSize = (baseFontSize - 2f).coerceAtLeast(11f).sp
                        )
                    }
                }
            }

            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
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
                            AppHeading(
                                title = stringResource(R.string.reminder_panel_title), subtitle = stringResource(R.string.reminder_panel_description),
                                fontFamily = fontFamily, titleColor = panelText, subtitleColor = panelSecondary,
                                titleSize = baseFontSize.sp,
                                subtitleSize = (baseFontSize - 3f).coerceAtLeast(11f).sp
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
                        AppHeading(
                            title = stringResource(R.string.reminder_empty), subtitle = stringResource(R.string.reminder_empty_description),
                            fontFamily = fontFamily, titleColor = primaryText, subtitleColor = secondaryText,
                            titleSize = baseFontSize.sp,
                            subtitleSize = (baseFontSize - 2f).coerceAtLeast(11f).sp
                        )
                    }
                }
            } else {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        settings = settings,
                        onEdit = UiSoundPlayer.actionHandler(context, UiActionSound.Open) { onEdit(reminder) },
                        onToggle = UiSoundPlayer.toggleHandler(context) { checked -> onToggle(reminder, checked) }
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
    val fontFamily = rememberAppFontFamily(settings.font)
    val baseFontSize = settings.fontSize.coerceIn(12f, 24f)
    val iconSize = settings.iconSize.coerceIn(18f, 34f)
    val container = MaterialTheme.colorScheme.surfaceContainerLow
    val (primary, secondary) = rememberUiTextColors(settings.textColor, container)
    val locale = reminderLocale()
    val dateFormatter = remember(locale) { SimpleDateFormat("EEE, d MMM · HH:mm", locale) }
    val displayTrigger = reminder.snoozedUntil?.takeIf { !reminder.enabled || it < reminder.triggerAtMillis } ?: reminder.triggerAtMillis
    val triggerLabel = remember(displayTrigger, locale) {
        dateFormatter.format(Date(displayTrigger))
    }
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
                    Switch(checked = reminder.enabled || reminder.snoozedUntil != null, onCheckedChange = onToggle)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIconLabel(Icons.Default.Schedule, triggerLabel,
                        iconModifier = Modifier.size((iconSize * 0.72f).coerceAtLeast(14f).dp), tint = secondary, gap = 5.dp,
                        fontFamily = fontFamily, fontSize = (baseFontSize - 4f).coerceAtLeast(10f).sp, color = secondary)
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

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ReminderEditor(
    settings: AppSettings,
    reminder: Reminder?,
    repository: ReminderActions,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    noteCatalog: List<com.example.mynotes.data.Note> = emptyList()
) {
    val composeResources = androidx.compose.ui.platform.LocalResources.current

    val context = LocalContext.current
    val fontFamily = rememberAppFontFamily(settings.font)
    val now = remember(reminder?.id) { System.currentTimeMillis() }
    var title by rememberSaveable(reminder?.id) { mutableStateOf(reminder?.title.orEmpty()) }
    var description by rememberSaveable(reminder?.id) { mutableStateOf(reminder?.description.orEmpty()) }
    var triggerAtMillis by rememberSaveable(reminder?.id) { mutableLongStateOf(reminder?.triggerAtMillis?.takeIf { reminder.enabled && it > now } ?: reminder?.snoozedUntil?.takeIf { it > now } ?: now + 300_000L) }
    var repeatMode by rememberSaveable(reminder?.id) { mutableStateOf(reminder?.repeatMode ?: Reminder.REPEAT_NONE) }
    var weekDays by rememberSaveable(reminder?.id) { mutableStateOf(reminder?.weekDays?.toList().orEmpty()) }
    var endAt by rememberSaveable(reminder?.id) { mutableStateOf(reminder?.repeatEndAtMillis) }
    var studioRepeat by remember(reminder?.id){mutableStateOf(com.example.mynotes.studio.StudioRepeatOptions.from(reminder))}
    var linkedId by rememberSaveable(reminder?.id) { mutableStateOf(reminder?.linkedNoteId) }
    var priority by rememberSaveable(reminder?.id) { mutableStateOf(reminder?.priority ?: Reminder.PRIORITY_NORMAL) }
    var colorKey by rememberSaveable(reminder?.id) { mutableStateOf(reminder?.colorKey ?: "palette") }
    var enabled by rememberSaveable(reminder?.id) { mutableStateOf(reminder?.let { it.enabled || it.snoozedUntil != null } ?: true) }
    var chooseNote by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val reminders by repository.reminders.collectAsStateWithLifecycle()
    val locale = reminderLocale()
    val dateFormat = remember(locale) { SimpleDateFormat("EEE, d MMM yyyy", locale) }
    val timeFormat = remember(locale) { SimpleDateFormat("HH:mm", locale) }
    val alignedTrigger = remember(triggerAtMillis, repeatMode, weekDays,studioRepeat) {
        val calendar = Calendar.getInstance().apply { timeInMillis = triggerAtMillis }
        if (repeatMode == Reminder.REPEAT_CUSTOM && weekDays.isNotEmpty()) {
            repeat(7) { if (calendar.get(Calendar.DAY_OF_WEEK) !in weekDays) calendar.add(Calendar.DAY_OF_YEAR, 1) }
        }
        val selected=calendar.timeInMillis
        if(repeatMode==Reminder.REPEAT_MONTHLY&&studioRepeat.ordinal!=0){com.example.mynotes.reminders.ReminderSchedule.next(Reminder(title="",triggerAtMillis=selected-1,repeatMode=Reminder.REPEAT_MONTHLY,repeatInterval=studioRepeat.interval,monthOrdinal=studioRepeat.ordinal,monthWeekday=studioRepeat.weekday,recurrenceAnchorMillis=selected),selected-1)?:selected}else selected
    }
    val dateText = dateFormat.format(Date(alignedTrigger))
    val timeText = timeFormat.format(Date(alignedTrigger))
    val panel = MaterialTheme.colorScheme.surfaceContainerLow
    val (primaryText, _) = rememberUiTextColors(settings.textColor, MaterialTheme.colorScheme.background)
    val (panelText, _) = rememberUiTextColors(settings.textColor, panel)
    val permission = androidx.activity.compose.rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    fun save() {
        validationError = when {
            title.isBlank() -> composeResources.getString(R.string.reminder_title_required)
            enabled && alignedTrigger <= System.currentTimeMillis() -> composeResources.getString(R.string.reminder_future_time_required)
            repeatMode == Reminder.REPEAT_CUSTOM && weekDays.isEmpty() -> composeResources.getString(R.string.workspace_select_days)
            repeatMode != Reminder.REPEAT_NONE && endAt != null && endAt!! < alignedTrigger -> composeResources.getString(R.string.workspace_end_before_start)
            else -> null
        }
        if (validationError != null) return
        if (enabled && Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        repository.upsert(Reminder(id = reminder?.id ?: System.currentTimeMillis(), title = title.trim(),
            description = description.trim(), triggerAtMillis = alignedTrigger, repeatMode = repeatMode,
            priority = priority, colorKey = colorKey, enabled = enabled, createdAt = reminder?.createdAt ?: System.currentTimeMillis(),
            weekDays = weekDays.toSet(), repeatEndAtMillis = endAt.takeIf { repeatMode != Reminder.REPEAT_NONE }, linkedNoteId = linkedId,repeatInterval=studioRepeat.interval,monthOrdinal=studioRepeat.ordinal,monthWeekday=studioRepeat.weekday,
            recurrenceAnchorMillis=if(alignedTrigger==reminder?.triggerAtMillis)reminder?.recurrenceAnchorMillis else alignedTrigger,maxOccurrences=studioRepeat.limit,occurrencesDone=reminder?.occurrencesDone?:0,alertMinutes=studioRepeat.alerts,locationOnly=reminder?.locationOnly?:false,libraryId=reminder?.libraryId?:com.example.mynotes.studio.PlusProfiles.currentId,excludedOccurrences=reminder?.excludedOccurrences.orEmpty()))
        UiSoundPlayer.playAction(context, UiActionSound.Save)
        onDone()
    }
    if (chooseNote) com.example.mynotes.ui.workspace.NotePickerDialog(noteCatalog, onSelect = { linkedId = it?.id; chooseNote = false }, onDismiss = { chooseNote = false })
    Scaffold(modifier = Modifier.imePadding().appScreenBackground().then(com.example.mynotes.ui.workspace.workspaceShortcuts(::save)),
        containerColor = Color.Transparent, contentColor = primaryText,
        topBar = {
            BoxWithConstraints(Modifier.fillMaxWidth().statusBarsPadding()) {
            val compactAction=com.example.mynotes.ui.workspace.WorkspaceLayoutPolicy.compactPrimaryAction(maxWidth.value,androidx.compose.ui.platform.LocalDensity.current.fontScale)
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                ReminderBackButton(primaryText, settings.iconSize, onCancel)
                Text(stringResource(if (reminder == null) R.string.reminder_create_title else R.string.reminder_edit_title),
                    Modifier.weight(1f).padding(horizontal = 8.dp), fontFamily = fontFamily, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge, maxLines = 2)
                com.example.mynotes.studio.PlusPrimaryAction(stringResource(R.string.save),::save,compact=compactAction,icon=Icons.Default.Save)
            }
            }
        }) { padding ->
        // Un solo desplazamiento vertical para la edición y las herramientas.
        // No hay panel secundario, control de expansión ni desplazamientos independientes.
        BoxWithConstraints(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 8.dp)) {
            val wide = com.example.mynotes.ui.workspace.WorkspaceLayoutPolicy.useTwoPanes(
                maxWidth.value, maxHeight.value, androidx.compose.ui.platform.LocalDensity.current.fontScale
            )
            val scrollState = rememberScrollState()
            Surface(
                modifier = Modifier.widthIn(max = 840.dp).fillMaxSize().align(Alignment.TopCenter),
                shape = RoundedCornerShape(22.dp),
                color = panel,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        listOf(dateText, timeText, repeatLabel(repeatMode)).joinToString(" · "),
                        fontFamily = fontFamily, color = panelText,
                        style = MaterialTheme.typography.labelLarge
                    )
                    OutlinedTextField(
                        title, { title = it; validationError = null },
                        Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp),
                        label = { Text(stringResource(R.string.reminder_title)) }
                    )
                    OutlinedTextField(
                        description, { description = it },
                        Modifier.fillMaxWidth(), minLines = 2, maxLines = 8, shape = RoundedCornerShape(14.dp),
                        label = { Text(stringResource(R.string.reminder_description)) }
                    )
                    OutlinedButton(onClick = { chooseNote = true ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            noteCatalog.firstOrNull { it.id == linkedId }?.title?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.workspace_link_note),
                            maxLines = 2, overflow = TextOverflow.Ellipsis
                        )
                    }
                    validationError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Las herramientas forman parte del mismo panel y del mismo scroll.
                    ReminderToolsPanel(
                        settings, fontFamily, alignedTrigger,
                        { triggerAtMillis = it; validationError = null },
                        repeatMode, { repeatMode = it },
                        priority, { priority = it },
                        colorKey, { colorKey = it },
                        enabled, { enabled = it }, dateText, timeText
                    )
                    com.example.mynotes.studio.StudioReminderOptions(
                        studioRepeat, repeatMode == Reminder.REPEAT_MONTHLY,
                        repeatMode != Reminder.REPEAT_NONE,
                        eventAtMillis = alignedTrigger
                    ) { studioRepeat = it }
                    if (repeatMode != Reminder.REPEAT_NONE) {
                        com.example.mynotes.studio.PlusRecurrencePreview(
                            Reminder(
                                title = title.ifBlank { "Reminder" },
                                triggerAtMillis = alignedTrigger, repeatMode = repeatMode,
                                weekDays = weekDays.toSet(), repeatEndAtMillis = endAt,
                                repeatInterval = studioRepeat.interval,
                                monthOrdinal = studioRepeat.ordinal,
                                monthWeekday = studioRepeat.weekday,
                                maxOccurrences = studioRepeat.limit,
                                occurrencesDone = reminder?.occurrencesDone ?: 0,
                                recurrenceAnchorMillis = alignedTrigger,
                                excludedOccurrences = reminder?.excludedOccurrences.orEmpty()
                            )
                        )
                    }
                    if (repeatMode == Reminder.REPEAT_CUSTOM) {
                        Text(stringResource(R.string.workspace_custom_days), fontWeight = FontWeight.SemiBold)
                        val names = remember(locale) { java.text.DateFormatSymbols(locale).shortWeekdays }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(2, 3, 4, 5, 6, 7, 1).forEach { day ->
                                FilterChip(
                                    selected = day in weekDays,
                                    onClick = { weekDays = if (day in weekDays) weekDays - day else weekDays + day ; UiSoundPlayer.playFallbackClick()},
                                    label = { Text(names[day]) }
                                )
                            }
                        }
                    }
                    if (repeatMode != Reminder.REPEAT_NONE) {
                        Text(stringResource(R.string.workspace_end_repeat), fontWeight = FontWeight.SemiBold)
                        OutlinedButton(onClick = {
                            showReminderDatePicker(context, endAt ?: alignedTrigger) { chosen ->
                                endAt = Calendar.getInstance().apply {
                                    timeInMillis = chosen
                                    set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
                                    set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
                                }.timeInMillis
                            }
                        ; UiSoundPlayer.playFallbackClick()}, modifier = Modifier.fillMaxWidth()) {
                            Text(endAt?.let { dateFormat.format(Date(it)) } ?: stringResource(R.string.workspace_never))
                        }
                        if (endAt != null) {
                            androidx.compose.material3.TextButton(onClick = { endAt = null ; UiSoundPlayer.playFallbackClick()}) {
                                Text(stringResource(R.string.workspace_never))
                            }
                        }
                    }
                    if (wide) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Text(stringResource(R.string.workspace_calendar), fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
                        androidx.compose.ui.viewinterop.AndroidView(
                            factory = { android.widget.CalendarView(it) },
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            update = { calendarView ->
                                calendarView.date = alignedTrigger
                                calendarView.setOnDateChangeListener { _, year, month, day ->
                                    triggerAtMillis = Calendar.getInstance().apply {
                                        timeInMillis = alignedTrigger
                                        set(Calendar.YEAR, year); set(Calendar.MONTH, month)
                                        set(Calendar.DAY_OF_MONTH, day)
                                    }.timeInMillis
                                }
                            }
                        )
                        Text(stringResource(R.string.workspace_upcoming), fontFamily = fontFamily, fontWeight = FontWeight.SemiBold)
                        val upcomingReminders = remember(reminders) {
                            reminders.filter { it.enabled || it.snoozedUntil != null }
                                .sortedBy { it.snoozedUntil ?: it.triggerAtMillis }.take(5)
                        }
                        upcomingReminders.forEach { upcoming ->
                            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(upcoming.title, fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, color = panelText)
                                Text(
                                    dateFormat.format(Date(upcoming.snoozedUntil ?: upcoming.triggerAtMillis)) + " · " +
                                        timeFormat.format(Date(upcoming.snoozedUntil ?: upcoming.triggerAtMillis)),
                                    color = panelText
                                )
                            }
                        }
                    }
                    if (reminder != null) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        OutlinedButton(
                            onClick = { repository.delete(reminder.id); onDone() ; UiSoundPlayer.playFallbackClick(UiActionSound.Delete)},
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) { Text(stringResource(R.string.reminder_delete)) }
                    }
                }
            }
        }
    }
}


@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
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
    val panelColor = MaterialTheme.colorScheme.surfaceContainerLow
    val (panelText, panelSecondary) = rememberUiTextColors(settings.textColor, panelColor)
    val chipColor = MaterialTheme.colorScheme.surfaceVariant
    val chipText = rememberUiTextColor(settings.textColor, chipColor)
    val selectedChipColor = MaterialTheme.colorScheme.primary
    val selectedChipText = remember(selectedChipColor) { automaticUiTextColor(selectedChipColor) }
    val paletteColor = MaterialTheme.colorScheme.primary
    val colorOptions = remember(paletteColor) {
        listOf(ReminderColorOption("palette", paletteColor)) + ReminderFixedColors
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            stringResource(R.string.reminder_tools),
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            color = panelText,
            fontSize = (baseFontSize + 1f).sp
        )

        ReminderToolTitle(stringResource(R.string.reminder_when), fontFamily, panelText, baseFontSize)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                    showReminderDatePicker(context, triggerAtMillis, onTriggerChange)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = panelText)
            ) {
                AppIconLabel(Icons.Default.CalendarMonth, dateText, gap = 6.dp, fontFamily = fontFamily, maxLines = 1,
                    fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp)
            }
            OutlinedButton(
                onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Menu) {
                    showReminderTimePicker(context, triggerAtMillis, onTriggerChange)
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = panelText)
            ) {
                AppIconLabel(Icons.Default.Schedule, timeText, gap = 6.dp, fontFamily = fontFamily,
                    fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp)
            }
        }

        ReminderToolTitle(stringResource(R.string.reminder_quick_time), fontFamily, panelText, baseFontSize)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReminderQuickMinutes.forEach { minutes ->
                ReminderChoiceChip(false, stringResource(R.string.reminder_in_minutes, minutes), fontFamily, baseFontSize,
                    chipColor, chipText, onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Select) {
                        val now = System.currentTimeMillis()
                        val updated = Calendar.getInstance().apply {
                            timeInMillis = now + minutes * 60_000L
                            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                            if (timeInMillis <= now) add(Calendar.MINUTE, 1)
                        }
                        onTriggerChange(updated.timeInMillis)
                    })
            }
        }

        ReminderToolTitle(stringResource(R.string.reminder_repeat), fontFamily, panelText, baseFontSize)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReminderRepeatModes.forEach { mode ->
                ReminderChoiceChip(repeatMode == mode, repeatLabel(mode), fontFamily, baseFontSize, chipColor, chipText,
                    selectedChipColor, selectedChipText, UiSoundPlayer.actionHandler(context, UiActionSound.Select) { onRepeatChange(mode) })
            }
        }

        ReminderToolTitle(stringResource(R.string.reminder_priority), fontFamily, panelText, baseFontSize)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReminderPriorities.forEach { item ->
                ReminderChoiceChip(priority == item, priorityLabel(item), fontFamily, baseFontSize, chipColor, chipText,
                    selectedChipColor, selectedChipText) {
                    UiSoundPlayer.play(context, UiSound.Priority)
                    onPriorityChange(item)
                }
            }
        }

        ReminderToolTitle(stringResource(R.string.reminder_color), fontFamily, panelText, baseFontSize)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(colorOptions) { item ->
                val selectedBorder = rememberUiGraphicColor(settings.textColor, item.color)
                Surface(
                    modifier = Modifier.size(34.dp).clickable(
                        onClick = UiSoundPlayer.actionHandler(context, UiActionSound.Color) { onColorChange(item.key) }
                    ),
                    shape = CircleShape,
                    color = item.color,
                    border = if (item.key == colorKey) BorderStroke(3.dp, selectedBorder) else null
                ) { }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                ReminderToolTitle(stringResource(R.string.reminder_enabled), fontFamily, panelText, baseFontSize)
                Text(
                    stringResource(R.string.reminder_enabled_description),
                    fontFamily = fontFamily,
                    color = panelSecondary,
                    fontSize = (baseFontSize - 4f).coerceAtLeast(10f).sp
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = UiSoundPlayer.toggleHandler(context, actionBlock = onEnabledChange)
            )
        }
    }
}

@Composable
private fun ReminderBackButton(tint: Color, iconSize: Float, onClick: () -> Unit) {
    AppCircularIconButton(Icons.Default.ArrowBack, stringResource(R.string.back), onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        iconModifier = Modifier.size(iconSize.coerceIn(20f, 28f).dp), tint = tint)
}

private fun showReminderDatePicker(context: Context, triggerAtMillis: Long, onChange: (Long) -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = triggerAtMillis }
    val pickerContext = context.withPickerTheme(android.R.attr.datePickerDialogTheme)
    val dialog = DatePickerDialog(pickerContext, R.style.ReminderPickerWindow, { _, year, month, day ->
        val updated = Calendar.getInstance().apply {
            timeInMillis = triggerAtMillis; set(Calendar.YEAR, year); set(Calendar.MONTH, month); set(Calendar.DAY_OF_MONTH, day)
        }
        UiSoundPlayer.runAction(context, UiActionSound.Select) { onChange(updated.timeInMillis) }
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    dialog.showWithNavigationBarPolicy()
}

private fun showReminderTimePicker(context: Context, triggerAtMillis: Long, onChange: (Long) -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = triggerAtMillis }
    val pickerContext = context.withPickerTheme(android.R.attr.timePickerDialogTheme)
    val dialog = TimePickerDialog(pickerContext, R.style.ReminderPickerWindow, { _, hour, minute ->
        val updated = Calendar.getInstance().apply {
            timeInMillis = triggerAtMillis; set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        UiSoundPlayer.runAction(context, UiActionSound.Select) { onChange(updated.timeInMillis) }
    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
    dialog.showWithNavigationBarPolicy()
}

private fun Context.withPickerTheme(attribute: Int): Context {
    val pickerTheme = TypedValue()
    return if (theme.resolveAttribute(attribute, pickerTheme, true) && pickerTheme.resourceId != 0) {
        ContextThemeWrapper(this, pickerTheme.resourceId)
    } else this
}

private fun AlertDialog.showWithNavigationBarPolicy() {
    val dialogWindow = requireNotNull(window)
    WindowCompat.setDecorFitsSystemWindows(dialogWindow, false)
    dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    val stopPolicy = dialogWindow.installDialogNavigationBarPolicy(preventFocusFlash = true)
    setOnDismissListener { stopPolicy() }
    try {
        show()
        dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        getButton(DialogInterface.BUTTON_POSITIVE)?.isSoundEffectsEnabled = false
    } catch (failure: Throwable) {
        setOnDismissListener(null)
        runCatching { dismiss() }
        stopPolicy()
        throw failure
    }
}

@Composable
private fun ReminderChoiceChip(
    selected: Boolean, label: String, fontFamily: FontFamily, baseFontSize: Float, container: Color, labelColor: Color,
    selectedContainer: Color? = null, selectedLabel: Color? = null, onClick: () -> Unit
) {
    FilterChip(
        selected, onClick, { Text(label, fontFamily = fontFamily, fontSize = (baseFontSize - 3f).coerceAtLeast(11f).sp) },
        colors = if (selectedContainer == null || selectedLabel == null) FilterChipDefaults.filterChipColors(containerColor = container, labelColor = labelColor)
        else FilterChipDefaults.filterChipColors(containerColor = container, labelColor = labelColor, selectedContainerColor = selectedContainer, selectedLabelColor = selectedLabel)
    )
}

@Composable
private fun ReminderToolTitle(text: String, fontFamily: FontFamily, color: Color, fontSize: Float) {
    Text(text, fontFamily = fontFamily, color = color,
        fontWeight = FontWeight.SemiBold, fontSize = fontSize.sp)
}

@Composable
private fun repeatLabel(mode: String): String = when (mode) {
    Reminder.REPEAT_DAILY -> stringResource(R.string.reminder_repeat_daily)
    Reminder.REPEAT_WEEKLY -> stringResource(R.string.reminder_repeat_weekly)
    Reminder.REPEAT_MONTHLY -> stringResource(R.string.reminder_repeat_monthly)
    Reminder.REPEAT_CUSTOM -> stringResource(R.string.workspace_custom_days)
    Reminder.REPEAT_WEEKDAYS -> stringResource(R.string.reminder_repeat_weekdays)
    else -> stringResource(R.string.reminder_repeat_none)
}

@Composable
private fun priorityLabel(priority: String): String = when (priority) {
    Reminder.PRIORITY_LOW -> stringResource(R.string.reminder_priority_low)
    Reminder.PRIORITY_HIGH -> stringResource(R.string.reminder_priority_high)
    else -> stringResource(R.string.reminder_priority_normal)
}
