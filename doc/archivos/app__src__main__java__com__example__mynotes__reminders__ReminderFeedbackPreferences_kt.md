# ReminderFeedbackPreferences.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/reminders/ReminderFeedbackPreferences.kt`  **SHA-256:** `a39bb2849c6406b315ac111fe43bdfe3be08e3ff5eca52f8d9461dea2f1a6c0c`  **Líneas:** 172 · **Bytes:** 8118 · **Imports:** 13 · **Declaraciones detectadas:** 5
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Lectura de preferencias de sonido, vibración y estilo visual usadas por recordatorios.
## 2. Package e imports

Package declarado: `com.example.mynotes.reminders`.

### Android / Jetpack / Compose

`android.content.Context`, `android.content.res.Configuration`, `android.media.MediaPlayer`, `androidx.compose.ui.graphics.toArgb`

### Proyecto MyNotes

`com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiHaptic`, `com.example.mynotes.ui.sound.UiHapticPlayer`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.PaletteCatalog`, `com.example.mynotes.ui.theme.darkScheme`, `com.example.mynotes.ui.theme.lightScheme`, `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`, `com.example.mynotes.ui.theme.resolveUiTextColor`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 25 | `object` | `ReminderFeedbackPreferences` | `object ReminderFeedbackPreferences {` |
| 38 | `class` | `Snapshot` | `` |
| 52 | `fun` | `sync` | `` |
| 97 | `fun` | `read` | `` |
| 119 | `fun` | `playReminderAlert` | `fun playReminderAlert(context: Context) {` |

## 4. Estado, efectos y límites observables

- **Flow/StateFlow:** 1 aparición/apariciones.
- **Alarm/notification:** 1 aparición/apariciones.
- **try/catch:** 2 aparición/apariciones.
- **coerce*:** 7 aparición/apariciones.
- **safe calls:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.sound.UiHaptic`
- `com.example.mynotes.ui.sound.UiHapticPlayer`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.PaletteCatalog`
- `com.example.mynotes.ui.theme.darkScheme`
- `com.example.mynotes.ui.theme.lightScheme`
- `com.example.mynotes.ui.theme.resolveSecondaryUiTextColor`
- `com.example.mynotes.ui.theme.resolveUiTextColor`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Verificar fecha/hora, repetición, reinicio del teléfono, permisos de notificación y comportamiento en Android 12+/13+.
- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
