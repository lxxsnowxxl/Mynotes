# QuickNoteWidgetProvider.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/widget/QuickNoteWidgetProvider.kt`  **SHA-256:** `0a537061d639aab8528197a80831a667935dd0ca81f63baca2885d7fec3d5b10`  **Líneas:** 45 · **Bytes:** 2906 · **Imports:** 9 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

AppWidgetProvider de Nota rápida.
## 2. Package e imports

Package declarado: `com.example.mynotes.widget`.

### Android / Jetpack / Compose

`android.appwidget.AppWidgetManager`, `android.appwidget.AppWidgetProvider`, `android.content.Context`, `android.widget.RemoteViews`

### Proyecto MyNotes

`com.example.mynotes.R`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.CoroutineScope`, `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.SupervisorJob`, `kotlinx.coroutines.launch`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 12 | `class` | `QuickNoteWidgetProvider` | `` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 2 aparición/apariciones.
- **RemoteViews/widgets:** 2 aparición/apariciones.
- **try/catch:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`

## 6. Recursos Android referenciados

- **R.id:** `widget_quick_accent`, `widget_quick_icon`, `widget_quick_plus` ×4, `widget_quick_root` ×2, `widget_quick_search` ×4, `widget_quick_subtitle` ×2, `widget_quick_title` ×2

- **R.layout:** `widget_quick_note`

- **R.string:** `widget_new_note`, `widget_quick_capture`, `widget_quick_capture_hint`, `widget_search`

## 7. Puntos de revisión al modificarlo

- Probar en launcher real/API 28: RemoteViews tiene restricciones distintas a Compose y no admite todos los tintes/Views.
