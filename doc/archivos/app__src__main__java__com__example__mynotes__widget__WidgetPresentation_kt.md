# WidgetPresentation.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/widget/WidgetPresentation.kt`  
**SHA-256:** `ad2a216a39e371194d99cdd6f7d7551a0a5c84390daa9bc4abc511c3f0e0957a`  
**Líneas:** 265  
**Package:** `com.example.mynotes.widget`

## 1. Para qué existe este archivo

Resuelve apariencia y texto de widgets: paleta, fondos, contraste, título, preview, badges y fechas.

## 2. Tipos/clases declarados

- Línea **15** — `object WidgetPresentation`.
- Línea **29** — `data  class WidgetThemeSpec`.

## 3. Estado, constantes y valores importantes

- **`WidgetEmbeddedLinkMarkerRegex`** (línea 16) inicia con `Regex(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`WidgetUrlRegex`** (línea 21) inicia con `Regex("""https?://[^\s<>"']+"""`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`settings`** (línea 59) inicia con `SettingsRepository(context.applicationContext`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`palette`** (línea 60) inicia con `PaletteCatalog.find(settings.backgroundColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`toneIndex`** (línea 61) inicia con `settings.backgroundToneIndex.coerceIn(0`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selectedTone`** (línea 62) inicia con `palette.tones.getOrElse(toneIndex`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`dark`** (línea 63) inicia con `settings.darkMode || selectedTone.luminance(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`key`** (línea 64) inicia con `palette.key.replace(Regex("[^a-z0-9_]"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`name`** (línea 79) inicia con `"widget_palette_${key}_${toneIndex}_root"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`id`** (línea 80) inicia con `context.resources.getIdentifier(name`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`actionColor`** (línea 83) inicia con `if (dark`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`primaryColor`** (línea 89) inicia con `if (dark`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`countColor`** (línea 94) inicia con `if (dark`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`normalizedTitle`** (línea 184) inicia con `note.title.trim(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`lines`** (línea 185) inicia con `visibleNoteContent(note.content`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`contentLine`** (línea 186) inicia con `lines.firstOrNull(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`merged`** (línea 197) inicia con `visibleNoteContent(note.content`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`parts`** (línea 216) inicia con `buildList {`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`t`** (línea 249) inicia con `amount.coerceIn(0f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`normalized`** (línea 259) inicia con `(intensity.coerceIn(0f`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`target`** (línea 260) inicia con `if (dark`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`amount`** (línea 261) inicia con `if (dark`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `visibleNoteContent` — líneas 23–23

**Firma:** `private fun visibleNoteContent(raw: String): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `raw: String`

**Salida:** String.

### `theme` — líneas 57–156

**Firma:** `suspend fun theme(context: Context): WidgetThemeSpec`

Lee AppSettings y resuelve un WidgetThemeSpec completo con recursos/fondos/colores de texto/acento compatibles con RemoteViews.

**Entradas:**
- `context: Context`

**Salida:** WidgetThemeSpec.

**Efectos/APIs observados en el cuerpo:**
- Opera con RemoteViews/AppWidget fuera de Compose.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `SettingsRepository`, `first`, `find`, `coerceIn`, `getOrElse`, `luminance`, `replace`, `Regex`, `themedRoot`, `getIdentifier`, `blend`, `WidgetThemeSpec`, `toInt`, `contrastText`, `toArgb`, `getOrDefault`.

### `themedRoot` — líneas 78–82

**Firma:** `fun themedRoot(fallback: Int): Int`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `fallback: Int`

**Salida:** Int.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `getIdentifier`.

### `accentColor` — líneas 158–158

**Firma:** `suspend fun accentColor(context: Context): Int`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`

**Salida:** Int.

**Operaciones/funciones que coordina:** `theme`.

### `noteAccentColor` — líneas 160–160

**Firma:** `fun noteAccentColor(note: Note, fallback: Int): Int`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `note: Note`
- `fallback: Int`

**Salida:** Int.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

### `title` — líneas 177–181

**Firma:** `fun title(context: Context, note: Note): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `note: Note`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.

**Operaciones/funciones que coordina:** `trim`, `isNotEmpty`, `visibleNoteContent`, `lineSequence`, `isNotBlank`, `getString`.

### `preview` — líneas 183–194

**Firma:** `fun preview(context: Context, note: Note): String`

Construye el texto corto de una nota para widget, evitando mostrar metadata interna de URLs y usando fallback cuando no hay contenido.

**Entradas:**
- `context: Context`
- `note: Note`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `trim`, `visibleNoteContent`, `lineSequence`, `isNotEmpty`, `toList`, `orEmpty`, `getString`, `priorityLabel`.

### `contentPreview` — líneas 196–203

**Firma:** `fun contentPreview(note: Note, maxLength: Int = 170): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `note: Note`
- `maxLength: Int = 170`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `visibleNoteContent`, `lineSequence`, `trim`, `isNotEmpty`, `joinToString`, `isBlank`, `take`, `trimEnd`.

### `badge` — líneas 205–205

**Firma:** `fun badge(context: Context, note: Note): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `note: Note`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

### `metadata` — líneas 215–225

**Firma:** `fun metadata(context: Context, note: Note): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `note: Note`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `add`, `priorityLabel`, `getString`, `shortDate`, `joinToString`.

### `shortDate` — líneas 227–231

**Firma:** `fun shortDate(context: Context, timestamp: Long): String`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `context: Context`
- `timestamp: Long`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.

**Operaciones/funciones que coordina:** `SimpleDateFormat`, `locale`, `format`, `Date`, `getOrDefault`.

### `priorityLabel` — líneas 233–233

**Firma:** `fun priorityLabel(context: Context, priority: Int): String`

Convierte un valor interno a texto breve de presentación para la interfaz.

**Entradas:**
- `context: Context`
- `priority: Int`

**Salida:** String.

**Decisiones y protecciones visibles:**
- Usa `when` para mapear estados/tipos/opciones.

### `contrastText` — líneas 240–246

**Firma:** `private fun contrastText(background: Color): Int`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `background: Color`

**Salida:** Int.

**Decisiones y protecciones visibles:**
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `luminance`, `toInt`.

### `blend` — líneas 248–256

**Firma:** `private fun blend(base: Color, overlay: Color, amount: Float): Color`

Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.

**Entradas:**
- `base: Color`
- `overlay: Color`
- `amount: Float`

**Salida:** Color.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.

**Operaciones/funciones que coordina:** `coerceIn`.

### `applyIntensity` — líneas 258–263

**Firma:** `private fun applyIntensity(color: Color, intensity: Float, dark: Boolean): Color`

Aplica una transformación/configuración al valor o componente recibido y devuelve/deja el resultado listo para ser usado por la UI.

**Entradas:**
- `color: Color`
- `intensity: Float`
- `dark: Boolean`

**Salida:** Color.

**Decisiones y protecciones visibles:**
- Limita valores con `coerce*` para evitar estados fuera de rango.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.

**Operaciones/funciones que coordina:** `coerceIn`, `blend`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.R`.
- Usa `com.example.mynotes.data.Note`.
- Usa `com.example.mynotes.settings.SettingsRepository`.
- Usa `com.example.mynotes.ui.theme.PaletteCatalog`.

## 6. Recursos Android que utiliza

- `R.drawable`: `widget_action_circle_dark`, `widget_action_circle_light`, `widget_action_primary_dark`, `widget_action_primary_light`, `widget_chip_dark`, `widget_chip_light`, `widget_count_pill_dark`, `widget_count_pill_light`, `widget_detail_card_dark`, `widget_detail_card_light`, `widget_metric_card_dark`, `widget_metric_card_light`, `widget_note_card_dark`, `widget_note_card_light`, `widget_panel_card_dark`, `widget_panel_card_light`, `widget_surface_screen_dark`, `widget_surface_screen_light`, `widget_thumb_background_dark`, `widget_thumb_background_light`
- `R.string`: `mock_priority_high`, `mock_priority_low`, `mock_priority_medium`, `mock_priority_none`, `widget_badge_personal`, `widget_badge_pinned`, `widget_badge_work`, `widget_category_personal`, `widget_category_work`, `widget_favorite_note`, `widget_pinned_note`, `widget_tap_to_open`, `widget_untitled_note`

## 7. Tecnologías y efectos relevantes

- Opera con RemoteViews/AppWidget fuera de Compose.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `theme` — Lee AppSettings y resuelve un WidgetThemeSpec completo con recursos/fondos/colores de texto/acento compatibles con RemoteViews.
2. `themedRoot` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
3. `accentColor` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
4. `noteAccentColor` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
5. `title` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
6. `preview` — Construye el texto corto de una nota para widget, evitando mostrar metadata interna de URLs y usando fallback cuando no hay contenido.
7. `contentPreview` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
8. `badge` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
9. `metadata` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
10. `shortDate` — Implementa la operación indicada por su nombre dentro de la responsabilidad de este archivo. La explicación de efectos observables se detalla debajo a partir de las APIs y dependencias usadas en el cuerpo.
11. `priorityLabel` — Convierte un valor interno a texto breve de presentación para la interfaz.

## 9. Qué no debe romperse al modificarlo

- RemoteViews tiene restricciones, especialmente en Samsung/API 28; probar el widget en launcher real.
- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.

## 10. Resumen en lenguaje sencillo

En términos simples: Resuelve apariencia y texto de widgets: paleta, fondos, contraste, título, preview, badges y fechas. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
