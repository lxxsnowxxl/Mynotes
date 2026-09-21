# FocusNoteWidgetProvider.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/widget/FocusNoteWidgetProvider.kt`  **SHA-256:** `c245da786b39799731847ab11b371c235110cb117d795793735a6c73a5d94dbd`  **Líneas:** 93 · **Bytes:** 7242 · **Imports:** 11 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

AppWidgetProvider para una nota destacada/en foco.
## 2. Package e imports

Package declarado: `com.example.mynotes.widget`.

### Android / Jetpack / Compose

`android.appwidget.AppWidgetManager`, `android.appwidget.AppWidgetProvider`, `android.content.Context`, `android.view.View`, `android.widget.RemoteViews`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.data.AppDatabase`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.CoroutineScope`, `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.SupervisorJob`, `kotlinx.coroutines.launch`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 14 | `class` | `FocusNoteWidgetProvider` | `` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 2 aparición/apariciones.
- **RemoteViews/widgets:** 2 aparición/apariciones.
- **try/catch:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.AppDatabase`

## 6. Recursos Android referenciados

- **R.drawable:** `widget_ic_pin`, `widget_ic_pin_outline`, `widget_ic_star`, `widget_ic_star_outline`, `widget_thumb_placeholder`

- **R.id:** `widget_focus_add` ×4, `widget_focus_badge` ×3, `widget_focus_card` ×4, `widget_focus_empty` ×5, `widget_focus_favorite` ×5, `widget_focus_header`, `widget_focus_meta` ×2, `widget_focus_open` ×4, `widget_focus_pin` ×5, `widget_focus_preview` ×2, `widget_focus_root`, `widget_focus_subtitle` ×2, `widget_focus_thumb` ×6, `widget_focus_title` ×2, `widget_focus_title_label` ×2

- **R.layout:** `widget_focus_note`

- **R.string:** `widget_empty_notes`, `widget_focus_note`, `widget_focus_note_hint`, `widget_new_note`, `widget_open_note`, `widget_toggle_favorite`, `widget_toggle_pin`

## 7. Puntos de revisión al modificarlo

- Probar en launcher real/API 28: RemoteViews tiene restricciones distintas a Compose y no admite todos los tintes/Views.
