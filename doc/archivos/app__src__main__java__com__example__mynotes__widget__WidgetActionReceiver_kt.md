# WidgetActionReceiver.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/widget/WidgetActionReceiver.kt`  **SHA-256:** `f4119fe7bdf5b806c33aee9f043d020b0e4a39175debbca8ccf80d55ad057279`  **Líneas:** 37 · **Bytes:** 1366 · **Imports:** 8 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Receiver interno de acciones ejecutadas desde widgets.
## 2. Package e imports

Package declarado: `com.example.mynotes.widget`.

### Android / Jetpack / Compose

`android.content.BroadcastReceiver`, `android.content.Context`, `android.content.Intent`

### Proyecto MyNotes

`com.example.mynotes.data.AppDatabase`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.CoroutineScope`, `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.SupervisorJob`, `kotlinx.coroutines.launch`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 11 | `class` | `WidgetActionReceiver` | `` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 3 aparición/apariciones.
- **Alarm/notification:** 2 aparición/apariciones.
- **try/catch:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.data.AppDatabase`

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
