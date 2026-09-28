# ReminderFeedbackPreferences.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/reminders/ReminderFeedbackPreferences.kt`  
**SHA-256:** `a39bb2849c6406b315ac111fe43bdfe3be08e3ff5eca52f8d9461dea2f1a6c0c`  
**Líneas:** 173  
**Package:** `com.example.mynotes.reminders`

## 1. Para qué existe este archivo

Puente entre AppSettings y el proceso de notificación: guarda snapshot visual/sonido/vibración y reproduce el feedback del recordatorio.

## 2. Tipos/clases declarados

- Línea **25** — `object ReminderFeedbackPreferences`.
- Línea **39** — `data  class Snapshot`.

## 3. Estado, constantes y valores importantes

- **`PREFS`** (línea 26) inicia con `"reminder_feedback_prefs"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_SOUND_ENABLED`** (línea 27) inicia con `"sound_enabled"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_SOUND_VOLUME`** (línea 28) inicia con `"sound_volume"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_SOUND_THEME`** (línea 29) inicia con `"sound_theme"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_HAPTIC_ENABLED`** (línea 30) inicia con `"haptic_enabled"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_HAPTIC_INTENSITY`** (línea 31) inicia con `"haptic_intensity"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_HAPTIC_STYLE`** (línea 32) inicia con `"haptic_style"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_NOTIFICATION_BACKGROUND`** (línea 33) inicia con `"notification_background"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_NOTIFICATION_TEXT`** (línea 34) inicia con `"notification_text"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_NOTIFICATION_SECONDARY_TEXT`** (línea 35) inicia con `"notification_secondary_text"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_NOTIFICATION_ACCENT`** (línea 36) inicia con `"notification_accent"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`KEY_NOTIFICATION_FONT_SIZE`** (línea 37) inicia con `"notification_font_size"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`systemDark`** (línea 54) inicia con `(context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`effectiveDark`** (línea 56) inicia con `if (settings.configurationMode == "advanced"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`palette`** (línea 57) inicia con `PaletteCatalog.find(settings.backgroundColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`scheme`** (línea 58) inicia con `if (effectiveDark`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`panel`** (línea 79) inicia con `scheme.surfaceContainer`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`primaryText`** (línea 80) inicia con `resolveUiTextColor(settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`secondaryText`** (línea 81) inicia con `resolveSecondaryUiTextColor(settings.textColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`prefs`** (línea 99) inicia con `context.getSharedPreferences(PREFS`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`config`** (línea 120) inicia con `read(context`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`rawName`** (línea 132) inicia con `if (config.soundTheme == UiSoundPlayer.DEFAULT_THEME`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`rawId`** (línea 138) inicia con `context.resources.getIdentifier(rawName`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`volume`** (línea 149) inicia con `(config.soundVolume / 100f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`player`** (línea 150) inicia con `MediaPlayer.create(context.applicationContext`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`activePlayer`** (línea 171) inicia con `null`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `sync` — líneas 53–96

**Firma:** `fun sync(context: Context, settings: AppSettings)`

Convierte AppSettings actuales en un snapshot pequeño almacenado para que un BroadcastReceiver pueda reconstruir apariencia, sonido y vibración aun fuera de una composición Compose.

**Entradas:**
- `context: Context`
- `settings: AppSettings`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Lee o escribe SharedPreferences.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `find`, `darkScheme`, `lightScheme`, `resolveUiTextColor`, `resolveSecondaryUiTextColor`, `getSharedPreferences`, `edit`, `putBoolean`, `putFloat`, `coerceIn`, `putString`, `normalizeTheme`, `normalizeStyle`, `putInt`, `toArgb`.

### `read` — líneas 98–113

**Firma:** `fun read(context: Context): Snapshot`

Lee y transforma datos desde la fuente indicada, devolviendo una representación segura o fallback cuando la lectura no puede completarse.

**Entradas:**
- `context: Context`

**Salida:** Snapshot.

**Efectos/APIs observados en el cuerpo:**
- Lee o escribe SharedPreferences.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.

**Operaciones/funciones que coordina:** `getSharedPreferences`, `Snapshot`, `getBoolean`, `getFloat`, `coerceIn`, `normalizeTheme`, `getString`, `orEmpty`, `normalizeStyle`, `getInt`, `toInt`.

### `playReminderAlert` — líneas 119–168

**Firma:** `fun playReminderAlert(context: Context)`

Lee el snapshot y reproduce el sonido/vibración elegidos para un recordatorio, respetando enable, volumen, tema, intensidad y estilo.

**Entradas:**
- `context: Context`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Reproduce audio.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `read`, `configure`, `play`, `getIdentifier`, `synchronized`, `stop`, `release`, `coerceIn`, `create`, `setVolume`, `start`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.settings.AppSettings`.
- Usa `com.example.mynotes.ui.sound.UiHaptic`.
- Usa `com.example.mynotes.ui.sound.UiHapticPlayer`.
- Usa `com.example.mynotes.ui.sound.UiSoundPlayer`.
- Usa `com.example.mynotes.ui.theme.PaletteCatalog`.
- Usa `com.example.mynotes.ui.theme.darkScheme`.
- Usa `com.example.mynotes.ui.theme.lightScheme`.
- Usa `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`.
- Usa `com.example.mynotes.ui.theme.resolveUiTextColor`.

## 6. Recursos Android que utiliza

- No se detectaron referencias directas `R.*` en este archivo.

## 7. Tecnologías y efectos relevantes

- Lee o escribe SharedPreferences.
- Reproduce audio.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `sync` — Convierte AppSettings actuales en un snapshot pequeño almacenado para que un BroadcastReceiver pueda reconstruir apariencia, sonido y vibración aun fuera de una composición Compose.
2. `read` — Lee y transforma datos desde la fuente indicada, devolviendo una representación segura o fallback cuando la lectura no puede completarse.
3. `playReminderAlert` — Lee el snapshot y reproduce el sonido/vibración elegidos para un recordatorio, respetando enable, volumen, tema, intensidad y estilo.

## 9. Qué no debe romperse al modificarlo

- No renombrar claves persistentes sin migración; ajustes ya guardados dependen de ellas.
- Probar fechas cercanas, repetición, reinicio, modo idle y permisos/notificaciones según API.
- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.

## 10. Resumen en lenguaje sencillo

En términos simples: Puente entre AppSettings y el proceso de notificación: guarda snapshot visual/sonido/vibración y reproduce el feedback del recordatorio. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
