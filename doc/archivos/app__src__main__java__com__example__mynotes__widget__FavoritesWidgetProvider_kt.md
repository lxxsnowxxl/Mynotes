# FavoritesWidgetProvider.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/widget/FavoritesWidgetProvider.kt`  **SHA-256:** `06a28b27157d6fade78c977a809890de5366125bc3fe994a33e466e8bd6e0d53`  **Líneas:** 103 · **Bytes:** 6501 · **Imports:** 13 · **Declaraciones detectadas:** 1
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

AppWidgetProvider para notas favoritas.
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
| 16 | `class` | `FavoritesWidgetProvider` | `` |

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

- **R.id:** `widget_favorite_accent_1`, `widget_favorite_accent_2`, `widget_favorite_meta_1`, `widget_favorite_meta_2`, `widget_favorite_preview_1`, `widget_favorite_preview_2`, `widget_favorite_row_1`, `widget_favorite_row_2`, `widget_favorite_thumb_1`, `widget_favorite_thumb_2`, `widget_favorite_title_1`, `widget_favorite_title_2`, `widget_favorite_toggle_1`, `widget_favorite_toggle_2`, `widget_favorites_add` ×4, `widget_favorites_cards`, `widget_favorites_count` ×2, `widget_favorites_empty` ×3, `widget_favorites_header`, `widget_favorites_root`, `widget_favorites_title` ×2

- **R.layout:** `widget_favorites`

- **R.string:** `widget_empty_favorites`, `widget_favorites`, `widget_new_note`, `widget_toggle_favorite`

## 7. Puntos de revisión al modificarlo

- Probar en launcher real/API 28: RemoteViews tiene restricciones distintas a Compose y no admite todos los tintes/Views.
