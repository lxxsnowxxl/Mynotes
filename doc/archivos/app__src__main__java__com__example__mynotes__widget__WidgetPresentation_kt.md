# WidgetPresentation.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/widget/WidgetPresentation.kt`  **SHA-256:** `ad2a216a39e371194d99cdd6f7d7551a0a5c84390daa9bc4abc511c3f0e0957a`  **Líneas:** 264 · **Bytes:** 12313 · **Imports:** 11 · **Declaraciones detectadas:** 15
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Paleta, colores, textos y presentación compartida por widgets.
## 2. Package e imports

Package declarado: `com.example.mynotes.widget`.

### Android / Jetpack / Compose

`android.content.Context`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.luminance`, `androidx.compose.ui.graphics.toArgb`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.data.Note`, `com.example.mynotes.settings.SettingsRepository`, `com.example.mynotes.ui.theme.PaletteCatalog`

### Kotlin / Coroutines / Java

`kotlinx.coroutines.flow.first`, `java.text.SimpleDateFormat`, `java.util.Date`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 14 | `object` | `WidgetPresentation` | `` |
| 22 | `fun` | `visibleNoteContent` | `` |
| 28 | `class` | `WidgetThemeSpec` | `` |
| 78 | `fun` | `themedRoot` | `fun themedRoot(fallback: Int): Int {` |
| 159 | `fun` | `noteAccentColor` | `` |
| 176 | `fun` | `title` | `` |
| 182 | `fun` | `preview` | `` |
| 195 | `fun` | `contentPreview` | `` |
| 204 | `fun` | `badge` | `` |
| 214 | `fun` | `metadata` | `` |
| 226 | `fun` | `shortDate` | `` |
| 232 | `fun` | `priorityLabel` | `` |
| 239 | `fun` | `contrastText` | `` |
| 247 | `fun` | `blend` | `` |
| 257 | `fun` | `applyIntensity` | `` |

## 4. Estado, efectos y límites observables

- **RemoteViews/widgets:** 1 aparición/apariciones.
- **coerce*:** 3 aparición/apariciones.
- **safe calls:** 1 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.data.Note`
- `com.example.mynotes.settings.SettingsRepository`
- `com.example.mynotes.ui.theme.PaletteCatalog`

## 6. Recursos Android referenciados

- **R.drawable:** `widget_action_circle_dark` ×2, `widget_action_circle_light`, `widget_action_primary_dark` ×2, `widget_action_primary_light`, `widget_chip_dark` ×2, `widget_chip_light`, `widget_count_pill_dark` ×2, `widget_count_pill_light`, `widget_detail_card_dark` ×2, `widget_detail_card_light`, `widget_metric_card_dark` ×2, `widget_metric_card_light`, `widget_note_card_dark` ×2, `widget_note_card_light`, `widget_panel_card_dark` ×2, `widget_panel_card_light`, `widget_surface_screen_dark` ×2, `widget_surface_screen_light`, `widget_thumb_background_dark` ×2, `widget_thumb_background_light`

- **R.string:** `mock_priority_high`, `mock_priority_low`, `mock_priority_medium`, `mock_priority_none`, `widget_badge_personal`, `widget_badge_pinned`, `widget_badge_work`, `widget_category_personal`, `widget_category_work`, `widget_favorite_note`, `widget_pinned_note`, `widget_tap_to_open`, `widget_untitled_note`

## 7. Puntos de revisión al modificarlo

- Probar en launcher real/API 28: RemoteViews tiene restricciones distintas a Compose y no admite todos los tintes/Views.
