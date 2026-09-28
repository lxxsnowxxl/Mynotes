# ReminderRepository.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/reminders/ReminderRepository.kt`  
**SHA-256:** `825bc3d535ccbf1b43c1f0cdaefd207e72f65a84d3303149ffd78101a2bedc26`  
**Líneas:** 154  
**Package:** `com.example.mynotes.reminders`

## 1. Para qué existe este archivo

Persistencia y lógica temporal de recordatorios. Guarda JSON en SharedPreferences, expone StateFlow y calcula la próxima repetición.

## 2. Tipos/clases declarados

- Línea **17** — `class ReminderRepository`.

## 3. Estado, constantes y valores importantes

- **`appContext`** (línea 18) inicia con `context.applicationContext`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`preferences`** (línea 19) inicia con `appContext.getSharedPreferences(PREFS_NAME`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`_reminders`** (línea 20) inicia con `MutableStateFlow(loadReminders(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`reminders`** (línea 22) inicia con `_reminders.asStateFlow(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`preferenceListener`** (línea 23) inicia con `SharedPreferences.OnSharedPreferenceChangeListener { _`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`current`** (línea 37) inicia con `loadReminders(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`index`** (línea 38) inicia con `current.indexOfFirst { it.id == reminder.id }`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`reminder`** (línea 54) inicia con `getReminder(id`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`next`** (línea 65) inicia con `nextTrigger(reminder.triggerAtMillis`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`normalized`** (línea 71) inicia con `if (reminder.triggerAtMillis <= System.currentTimeMillis(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`sorted`** (línea 84) inicia con `items.sortedWith(compareByDescending<Reminder> { it.enabled }.thenBy { it.triggerAtMillis }`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`array`** (línea 85) inicia con `JSONArray(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`raw`** (línea 104) inicia con `preferences.getString(KEY_REMINDERS`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`item`** (línea 109) inicia con `array.getJSONObject(index`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`PREFS_NAME`** (línea 129) inicia con `"reminder_prefs"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_REMINDERS`** (línea 130) inicia con `"reminders_json"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`candidate`** (línea 133) inicia con `previous`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`calendar`** (línea 135) inicia con `Calendar.getInstance(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `getReminder` — líneas 34–34

**Firma:** `fun getReminder(id: Long): Reminder?`

Obtiene el dato solicitado desde la fuente o estructura que maneja este archivo, sin cambiar el contrato público del resto del módulo.

**Entradas:**
- `id: Long`

**Salida:** Reminder?.

**Operaciones/funciones que coordina:** `loadReminders`.

### `upsert` — líneas 36–46

**Firma:** `fun upsert(reminder: Reminder)`

Inserta o reemplaza un recordatorio por id, ordena/persiste la colección y programa o cancela AlarmManager según el valor enabled.

**Entradas:**
- `reminder: Reminder`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `loadReminders`, `toMutableList`, `save`, `schedule`, `cancel`.

### `delete` — líneas 48–51

**Firma:** `fun delete(id: Long)`

Elimina el elemento indicado. El cuerpo coordina la capa de persistencia y, cuando hay archivos asociados, realiza la limpieza correspondiente.

**Entradas:**
- `id: Long`

**Salida:** Unit o inferido por Kotlin.

**Operaciones/funciones que coordina:** `cancel`, `save`, `loadReminders`.

### `setEnabled` — líneas 53–56

**Firma:** `fun setEnabled(id: Long, enabled: Boolean)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `id: Long`
- `enabled: Boolean`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.

**Operaciones/funciones que coordina:** `getReminder`, `upsert`, `copy`.

### `advanceAfterTrigger` — líneas 59–67

**Firma:** `fun advanceAfterTrigger(id: Long)`

Después de dispararse un recordatorio, calcula la siguiente fecha si es repetitivo; si no se repite, lo desactiva. Persiste el resultado y vuelve a programar cuando corresponde.

**Entradas:**
- `id: Long`

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `getReminder`, `upsert`, `copy`, `nextTrigger`, `currentTimeMillis`.

### `rescheduleAll` — líneas 69–81

**Firma:** `fun rescheduleAll()`

Recorre los recordatorios activos al iniciar/reiniciar y reconstruye sus alarmas. Si una fecha repetitiva quedó atrás, la normaliza a la próxima ocurrencia.

**Salida:** Unit o inferido por Kotlin.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `loadReminders`, `currentTimeMillis`, `copy`, `nextTrigger`, `upsert`, `schedule`.

### `save` — líneas 83–101

**Firma:** `private fun save(items: List<Reminder>)`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `items: List<Reminder>`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Escribe preferencias persistentes.

**Operaciones/funciones que coordina:** `sortedWith`, `JSONArray`, `put`, `JSONObject`, `edit`, `putString`, `toString`.

### `loadReminders` — líneas 103–126

**Firma:** `private fun loadReminders(): List<Reminder>`

Carga la información solicitada. El cuerpo intenta reutilizar datos disponibles y realiza I/O/decodificación sólo cuando es necesario.

**Salida:** List<Reminder>.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.

**Operaciones/funciones que coordina:** `getString`, `emptyList`, `JSONArray`, `length`, `getJSONObject`, `add`, `Reminder`, `optLong`, `currentTimeMillis`, `optString`, `optBoolean`, `sortedWith`, `getOrDefault`.

### `nextTrigger` — líneas 132–151

**Firma:** `fun nextTrigger(previous: Long, repeatMode: String, now: Long): Long`

Calcula matemáticamente la siguiente ocurrencia para diario, semanal, mensual o lunes-viernes, avanzando hasta quedar después de now.

**Entradas:**
- `previous: Long`
- `repeatMode: String`
- `now: Long`

**Salida:** Long.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `getInstance`, `add`, `get`.

## 5. Cómo se conecta con el resto de MyNotes

- No importa directamente otro componente `com.example.mynotes`; funciona como modelo/utilidad base o mediante APIs Android/Jetpack.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Escribe preferencias persistentes.
- Lee o escribe SharedPreferences.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `getReminder` — Obtiene el dato solicitado desde la fuente o estructura que maneja este archivo, sin cambiar el contrato público del resto del módulo.
2. `upsert` — Inserta o reemplaza un recordatorio por id, ordena/persiste la colección y programa o cancela AlarmManager según el valor enabled.
3. `delete` — Elimina el elemento indicado. El cuerpo coordina la capa de persistencia y, cuando hay archivos asociados, realiza la limpieza correspondiente.
4. `setEnabled` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
5. `advanceAfterTrigger` — Después de dispararse un recordatorio, calcula la siguiente fecha si es repetitivo; si no se repite, lo desactiva. Persiste el resultado y vuelve a programar cuando corresponde.
6. `rescheduleAll` — Recorre los recordatorios activos al iniciar/reiniciar y reconstruye sus alarmas. Si una fecha repetitiva quedó atrás, la normaliza a la próxima ocurrencia.
7. `nextTrigger` — Calcula matemáticamente la siguiente ocurrencia para diario, semanal, mensual o lunes-viernes, avanzando hasta quedar después de now.

## 9. Qué no debe romperse al modificarlo

- Probar fechas cercanas, repetición, reinicio, modo idle y permisos/notificaciones según API.

## 10. Resumen en lenguaje sencillo

En términos simples: Persistencia y lógica temporal de recordatorios. Guarda JSON en SharedPreferences, expone StateFlow y calcula la próxima repetición. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
