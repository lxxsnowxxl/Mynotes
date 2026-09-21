# ReminderScreen.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/ReminderScreen.kt`  **SHA-256:** `8bac9372df3bfe0175f20ae7ad75b1fc275e86add4ea7174fd50af1c15c5d731`  **Líneas:** 901 · **Bytes:** 41090 · **Imports:** 87 · **Declaraciones detectadas:** 11
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Lista y editor Compose de recordatorios; fecha/hora, repetición, prioridad y color.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui`.

### Android / Jetpack / Compose

`android.Manifest`, `android.app.DatePickerDialog`, `android.app.TimePickerDialog`, `android.content.pm.PackageManager`, `android.os.Build`, `androidx.activity.compose.BackHandler`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.background`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.lazy.LazyColumn`, `androidx.compose.foundation.lazy.LazyRow`, `androidx.compose.foundation.lazy.items`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Add`, `androidx.compose.material.icons.filled.ArrowBack`, `androidx.compose.material.icons.filled.CalendarMonth`, `androidx.compose.material.icons.filled.Delete`, `androidx.compose.material.icons.filled.Edit`, `androidx.compose.material.icons.filled.NotificationsActive`, `androidx.compose.material.icons.filled.Schedule`, `androidx.compose.material3.Button`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.Card`, `androidx.compose.material3.CardDefaults`, `androidx.compose.material3.FilterChip`, `androidx.compose.material3.FilterChipDefaults`, `androidx.compose.material3.FloatingActionButton`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.OutlinedButton`, `androidx.compose.material3.OutlinedTextField`, `androidx.compose.material3.Scaffold`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Switch`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableLongStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.saveable.rememberSaveable`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.platform.LocalConfiguration`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextOverflow`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.core.content.ContextCompat`, `androidx.lifecycle.compose.collectAsStateWithLifecycle`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.reminders.Reminder`, `com.example.mynotes.reminders.ReminderRepository`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.appFontFamily`, `com.example.mynotes.ui.theme.automaticUiTextColor`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiGraphicColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`

### Kotlin / Coroutines / Java

`java.text.SimpleDateFormat`, `java.util.Calendar`, `java.util.Date`, `java.util.Locale`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 90 | `class` | `ReminderColorOption` | `` |
| 104 | `fun` | `reminderColor` | `` |
| 110 | `fun` | `reminderLocale` | `` |
| 118 | `fun` | `ReminderScreen` | `` |
| 177 | `fun` | `ReminderList` | `` |
| 349 | `fun` | `ReminderCard` | `` |
| 434 | `fun` | `ReminderEditor` | `` |
| 473 | `fun` | `saveReminder` | `` |
| 625 | `fun` | `ReminderToolsPanel` | `` |
| 886 | `fun` | `repeatLabel` | `` |
| 895 | `fun` | `priorityLabel` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 32 aparición/apariciones.
- **Coroutines:** 1 aparición/apariciones.
- **Room:** 3 aparición/apariciones.
- **coerce*:** 30 aparición/apariciones.
- **safe calls:** 21 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.reminders.Reminder`
- `com.example.mynotes.reminders.ReminderRepository`
- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.appFontFamily`
- `com.example.mynotes.ui.theme.automaticUiTextColor`
- `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`
- `com.example.mynotes.ui.theme.resolveUiGraphicColor`
- `com.example.mynotes.ui.theme.resolveUiTextColor`

## 6. Recursos Android referenciados

- **R.string:** `back` ×2, `edit_note`, `reminder_active_count`, `reminder_color`, `reminder_content_section`, `reminder_create_title`, `reminder_delete`, `reminder_description`, `reminder_edit_title`, `reminder_editor_subtitle`, `reminder_empty`, `reminder_empty_description`, `reminder_enabled`, `reminder_enabled_description`, `reminder_future_time_required`, `reminder_in_minutes`, `reminder_new`, `reminder_panel_description`, `reminder_panel_title`, `reminder_priority`, `reminder_priority_high`, `reminder_priority_low`, `reminder_priority_normal`, `reminder_quick_time`, `reminder_repeat`, `reminder_repeat_daily`, `reminder_repeat_monthly`, `reminder_repeat_none`, `reminder_repeat_weekdays`, `reminder_repeat_weekly`, `reminder_title`, `reminder_title_required`, `reminder_tools`, `reminder_untitled`, `reminder_when`, `reminders`, `save`

## 7. Puntos de revisión al modificarlo

- Verificar fecha/hora, repetición, reinicio del teléfono, permisos de notificación y comportamiento en Android 12+/13+.
- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
