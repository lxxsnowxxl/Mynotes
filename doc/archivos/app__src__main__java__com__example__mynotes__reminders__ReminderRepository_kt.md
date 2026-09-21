# ReminderRepository.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/reminders/ReminderRepository.kt`  **SHA-256:** `825bc3d535ccbf1b43c1f0cdaefd207e72f65a84d3303149ffd78101a2bedc26`  **Líneas:** 153 · **Bytes:** 6564 · **Imports:** 8 · **Declaraciones detectadas:** 10
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Persistencia y operaciones CRUD de recordatorios.
## 2. Package e imports

Package declarado: `com.example.mynotes.reminders`.

### Android / Jetpack / Compose

`android.content.Context`, `android.content.SharedPreferences`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.flow.MutableStateFlow`, `kotlinx.coroutines.flow.StateFlow`, `kotlinx.coroutines.flow.asStateFlow`, `java.util.Calendar`

### Terceros / otros

`org.json.JSONArray`, `org.json.JSONObject`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 17 | `class` | `ReminderRepository` | `class ReminderRepository(context: Context) {` |
| 33 | `fun` | `getReminder` | `` |
| 35 | `fun` | `upsert` | `` |
| 47 | `fun` | `delete` | `` |
| 52 | `fun` | `setEnabled` | `` |
| 59 | `fun` | `advanceAfterTrigger` | `fun advanceAfterTrigger(id: Long) {` |
| 68 | `fun` | `rescheduleAll` | `` |
| 82 | `fun` | `save` | `` |
| 102 | `fun` | `loadReminders` | `` |
| 131 | `fun` | `nextTrigger` | `` |

## 4. Estado, efectos y límites observables

- **Flow/StateFlow:** 2 aparición/apariciones.
- **Room:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Verificar fecha/hora, repetición, reinicio del teléfono, permisos de notificación y comportamiento en Android 12+/13+.
