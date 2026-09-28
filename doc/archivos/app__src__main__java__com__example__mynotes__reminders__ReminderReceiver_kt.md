# ReminderReceiver.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/reminders/ReminderReceiver.kt`  
**SHA-256:** `dfa8498ebf65eaa7b7cf73632f139aa545d9ee9b0be7dd482bd6ee9b94ff21af`  
**Líneas:** 251  
**Package:** `com.example.mynotes.reminders`

## 1. Para qué existe este archivo

BroadcastReceiver que recibe la alarma, construye la notificación, reproduce feedback y avanza/reprograma recordatorios repetidos.

## 2. Tipos/clases declarados

- Línea **26** — `class ReminderReceiver`.

## 3. Estado, constantes y valores importantes

- **`id`** (línea 33) inicia con `intent.getLongExtra(ReminderAlarmScheduler.EXTRA_REMINDER_ID`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`repository`** (línea 35) inicia con `ReminderRepository(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`reminder`** (línea 36) inicia con `repository.getReminder(id`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`textContext`** (línea 43) inicia con `localizedContext(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`notificationManager`** (línea 44) inicia con `context.getSystemService(Context.NOTIFICATION_SERVICE`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`channel`** (línea 49) inicia con `NotificationChannel(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`contentIntent`** (línea 64) inicia con `PendingIntent.getActivity(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`priority`** (línea 74) inicia con `when (reminder.priority`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`title`** (línea 80) inicia con `reminder.title.ifBlank { textContext.getString(R.string.reminder_untitled`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`description`** (línea 81) inicia con `reminder.description.ifBlank {`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`visual`** (línea 84) inicia con `ReminderFeedbackPreferences.read(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`accent`** (línea 85) inicia con `reminderAccent(reminder.colorKey`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`expandedView`** (línea 86) inicia con `buildCustomView(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`headsUpView`** (línea 98) inicia con `buildCustomView(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`notification`** (línea 111) inicia con `NotificationCompat.Builder(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`view`** (línea 149) inicia con `RemoteViews(context.packageName`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`base`** (línea 166) inicia con `visual.notificationFontSize.coerceIn(12f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`locale`** (línea 195) inicia con `context.resources.configuration.locales[0]`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`dateTime`** (línea 196) inicia con `SimpleDateFormat("EEE`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`repeat`** (línea 197) inicia con `when (reminder.repeatMode`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`preferred`** (línea 213) inicia con `when (colorKey`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`target`** (línea 226) inicia con `if (ColorUtils.calculateLuminance(background`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`best`** (línea 227) inicia con `preferred`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`candidate`** (línea 229) inicia con `ColorUtils.blendARGB(preferred`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`language`** (línea 237) inicia con `context.getSharedPreferences("locale_prefs"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`configuration`** (línea 240) inicia con `Configuration(context.resources.configuration`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`OLD_CHANNEL_ID`** (línea 246) inicia con `"mynotes_reminders"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`CHANNEL_ID`** (línea 247) inicia con `"mynotes_reminders_v2"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`EXTRA_OPEN_REMINDERS`** (línea 248) inicia con `"open_reminders"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `onReceive` — líneas 27–40

**Firma:** `override fun onReceive(context: Context, intent: Intent?)`

Recibe ACTION_FIRE_REMINDER desde AlarmManager, resuelve el recordatorio por id, muestra la notificación, reproduce feedback y pide al repositorio avanzar/reprogramar la recurrencia.

**Entradas:**
- `context: Context`
- `intent: Intent?`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `ReminderRepository`, `rescheduleAll`, `getLongExtra`, `getReminder`, `showNotification`, `advanceAfterTrigger`.

### `showNotification` — líneas 42–134

**Firma:** `private fun showNotification(context: Context, reminder: Reminder)`

Crea/actualiza el canal, prepara PendingIntent a MainActivity, usa la plantilla nativa para el estado colapsado y RemoteViews personalizados para heads-up/expandida, aplica prioridad/acento y publica la notificación.

**Entradas:**
- `context: Context`
- `reminder: Reminder`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Construye o publica notificaciones Android.
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `localizedContext`, `getSystemService`, `deleteNotificationChannel`, `NotificationChannel`, `getString`, `setSound`, `enableVibration`, `createNotificationChannel`, `checkSelfPermission`, `getActivity`, `toInt`, `Intent`, `putExtra`, `read`, `reminderAccent`, `buildCustomView`, `Builder`, `setSmallIcon`.

### `buildCustomView` — líneas 136–192

**Firma:** `private fun buildCustomView( context: Context, textContext: Context, layoutId: Int, reminder: Reminder, title: String, description: String, visual: ReminderFeedbackPreferences.Snapshot, accent: Int, contentIntent: PendingIntent, expanded: Boolean, headsUp: Boolean = false ): RemoteViews`

Rellena uno de los layouts RemoteViews de notificación con título, descripción, metadatos, colores y tamaños compatibles con el estado solicitado.

**Entradas:**
- `context: Context`
- `textContext: Context`
- `layoutId: Int`
- `reminder: Reminder`
- `title: String`
- `description: String`
- `visual: ReminderFeedbackPreferences.Snapshot`
- `accent: Int`
- `contentIntent: PendingIntent`
- `expanded: Boolean`
- `headsUp: Boolean = false`

**Salida:** RemoteViews.

**Efectos/APIs observados en el cuerpo:**
- Opera con RemoteViews/AppWidget fuera de Compose.
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `RemoteViews`, `setInt`, `setTextViewText`, `getString`, `setTextColor`, `coerceIn`, `setTextViewTextSize`, `setOnClickPendingIntent`, `metadata`, `coerceAtLeast`, `setViewVisibility`.

### `metadata` — líneas 194–210

**Firma:** `private fun metadata(context: Context, reminder: Reminder): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `reminder: Reminder`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `SimpleDateFormat`, `format`, `Date`, `getString`.

### `reminderAccent` — líneas 212–234

**Firma:** `private fun reminderAccent(colorKey: String, paletteAccent: Int, background: Int): Int`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `colorKey: String`
- `paletteAccent: Int`
- `background: Int`

**Salida:** Int.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `toInt`, `calculateContrast`, `calculateLuminance`, `blendARGB`.

### `localizedContext` — líneas 236–243

**Firma:** `private fun localizedContext(context: Context): Context`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`

**Salida:** Context.

**Efectos/APIs observados en el cuerpo:**
- Lee o escribe SharedPreferences.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `getSharedPreferences`, `getString`, `Configuration`, `setLocale`, `forLanguageTag`, `createConfigurationContext`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.MainActivity`.
- Usa `com.example.mynotes.R`.

## 6. Recursos Android que utiliza

- `R.drawable`: `ic_reminder_notification`
- `R.id`: `reminder_notification_accent`, `reminder_notification_background`, `reminder_notification_description`, `reminder_notification_icon`, `reminder_notification_label`, `reminder_notification_metadata`, `reminder_notification_root`, `reminder_notification_title`
- `R.layout`: `notification_reminder_expanded`, `notification_reminder_heads_up`
- `R.string`: `reminder_notification_channel`, `reminder_notification_channel_description`, `reminder_notification_default_text`, `reminder_priority_high`, `reminder_priority_low`, `reminder_priority_normal`, `reminder_repeat_daily`, `reminder_repeat_monthly`, `reminder_repeat_none`, `reminder_repeat_weekdays`, `reminder_repeat_weekly`, `reminder_untitled`, `reminders`

## 7. Tecnologías y efectos relevantes

- Lee o escribe SharedPreferences.
- Construye o publica notificaciones Android.
- Opera con RemoteViews/AppWidget fuera de Compose.
- Crea un PendingIntent para una acción futura del sistema.
- Inicia o prepara navegación/acción mediante Intent.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `onReceive` — Recibe ACTION_FIRE_REMINDER desde AlarmManager, resuelve el recordatorio por id, muestra la notificación, reproduce feedback y pide al repositorio avanzar/reprogramar la recurrencia.

## 9. Qué no debe romperse al modificarlo

- RemoteViews tiene restricciones, especialmente en Samsung/API 28; probar el widget en launcher real.
- Probar fechas cercanas, repetición, reinicio, modo idle y permisos/notificaciones según API.

## 10. Resumen en lenguaje sencillo

En términos simples: BroadcastReceiver que recibe la alarma, construye la notificación, reproduce feedback y avanza/reprograma recordatorios repetidos. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
