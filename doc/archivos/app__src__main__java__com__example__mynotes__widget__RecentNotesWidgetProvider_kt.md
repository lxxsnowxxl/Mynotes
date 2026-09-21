# RecentNotesWidgetProvider.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/widget/RecentNotesWidgetProvider.kt`  **SHA-256:** `2d4cda8ead1872b77c85c11cd06e98532ccdf625b45ece3b72054c0b5ca6c0a3`  **Líneas:** 107 · **Bytes:** 6629 · **Imports:** 13 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

AppWidgetProvider de notas recientes.
## 2. Package e imports

Package declarado: `com.example.mynotes.widget`.

### Android / Jetpack / Compose

`android.appwidget.AppWidgetManager`, `android.appwidget.AppWidgetProvider`, `android.content.Context`, `android.view.View`, `android.widget.RemoteViews`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.data.AppDatabase`, `com.example.mynotes.data.Attachment`, `com.example.mynotes.data.Note`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.CoroutineScope`, `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.SupervisorJob`, `kotlinx.coroutines.launch`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 16 | `class` | `RecentNotesWidgetProvider` | `` |

## 4. Estado, efectos y límites observables

- **Coroutines:** 2 aparición/apariciones.
- **RemoteViews/widgets:** 2 aparición/apariciones.
- **try/catch:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.AppDatabase`
- `com.example.mynotes.data.Attachment`
- `com.example.mynotes.data.Note`

## 6. Recursos Android referenciados

- **R.drawable:** `widget_thumb_placeholder`

- **R.id:** `widget_note_accent_1`, `widget_note_accent_2`, `widget_note_badge_1`, `widget_note_badge_2`, `widget_note_meta_1`, `widget_note_meta_2`, `widget_note_preview_1`, `widget_note_preview_2`, `widget_note_row_1`, `widget_note_row_2`, `widget_note_thumb_1`, `widget_note_thumb_2`, `widget_note_title_1`, `widget_note_title_2`, `widget_recent_add` ×4, `widget_recent_cards`, `widget_recent_count` ×2, `widget_recent_empty` ×3, `widget_recent_header`, `widget_recent_root`, `widget_recent_search` ×4, `widget_recent_title` ×2

- **R.layout:** `widget_recent_notes`

- **R.string:** `widget_empty_notes`, `widget_new_note`, `widget_recent_notes`, `widget_search`

## 7. Puntos de revisión al modificarlo

- Probar en launcher real/API 28: RemoteViews tiene restricciones distintas a Compose y no admite todos los tintes/Views.
