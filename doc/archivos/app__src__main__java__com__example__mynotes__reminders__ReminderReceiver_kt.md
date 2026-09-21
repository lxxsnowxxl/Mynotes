# ReminderReceiver.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/reminders/ReminderReceiver.kt`  **SHA-256:** `dfa8498ebf65eaa7b7cf73632f139aa545d9ee9b0be7dd482bd6ee9b94ff21af`  **Líneas:** 250 · **Bytes:** 11821 · **Imports:** 22 · **Declaraciones detectadas:** 6
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

BroadcastReceiver que recibe alarmas/reinicio y publica/reprograma notificaciones de recordatorios.
## 2. Package e imports

Package declarado: `com.example.mynotes.reminders`.

### Android / Jetpack / Compose

`android.Manifest`, `android.app.NotificationChannel`, `android.app.NotificationManager`, `android.app.PendingIntent`, `android.content.BroadcastReceiver`, `android.content.Context`, `android.content.Intent`, `android.content.pm.PackageManager`, `android.content.res.Configuration`, `android.os.Build`, `android.util.TypedValue`, `android.view.View`, `android.widget.RemoteViews`, `androidx.core.app.NotificationCompat`, `androidx.core.app.NotificationManagerCompat`, `androidx.core.content.ContextCompat`, `androidx.core.graphics.ColorUtils`

### Proyecto MyNotes

`com.example.mynotes.MainActivity`, `com.example.mynotes.R`

### Kotlin / Coroutines / Java

`java.text.SimpleDateFormat`, `java.util.Date`, `java.util.Locale`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 25 | `class` | `ReminderReceiver` | `` |
| 41 | `fun` | `showNotification` | `` |
| 135 | `fun` | `buildCustomView` | `` |
| 193 | `fun` | `metadata` | `` |
| 211 | `fun` | `reminderAccent` | `` |
| 235 | `fun` | `localizedContext` | `` |

## 4. Estado, efectos y límites observables

- **RemoteViews/widgets:** 8 aparición/apariciones.
- **Alarm/notification:** 5 aparición/apariciones.
- **coerce*:** 8 aparición/apariciones.
- **safe calls:** 3 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.MainActivity`
- `com.example.mynotes.R`

## 6. Recursos Android referenciados

- **R.drawable:** `ic_reminder_notification`

- **R.id:** `reminder_notification_accent`, `reminder_notification_background`, `reminder_notification_description` ×4, `reminder_notification_icon`, `reminder_notification_label` ×4, `reminder_notification_metadata` ×4, `reminder_notification_root`, `reminder_notification_title` ×4

- **R.layout:** `notification_reminder_expanded`, `notification_reminder_heads_up`

- **R.string:** `reminder_notification_channel`, `reminder_notification_channel_description`, `reminder_notification_default_text`, `reminder_priority_high`, `reminder_priority_low`, `reminder_priority_normal`, `reminder_repeat_daily`, `reminder_repeat_monthly`, `reminder_repeat_none`, `reminder_repeat_weekdays`, `reminder_repeat_weekly`, `reminder_untitled`, `reminders` ×2

## 7. Puntos de revisión al modificarlo

- Probar en launcher real/API 28: RemoteViews tiene restricciones distintas a Compose y no admite todos los tintes/Views.
- Verificar fecha/hora, repetición, reinicio del teléfono, permisos de notificación y comportamiento en Android 12+/13+.
