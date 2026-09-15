# SettingsRepository.kt — documentación exhaustiva por bloques

**Ruta de código real:** `app/src/main/java/com/example/mynotes/settings/SettingsRepository.kt`  
**SHA-256 del archivo ejecutable sin tocar:** `79429b023ece52b638e9e6e1ffef1f6a04df64a13cd3f0f182aeb9fbb577d856`  
**Líneas del código real:** 598

> **Garantía:** esta documentación se genera en una carpeta paralela. El archivo Kotlin/Kotlin DSL anterior no se modifica. Los fragmentos mostrados abajo son copias de lectura; la aplicación compila usando únicamente el archivo original de `app/...`.

## 1. Papel del archivo

Fuente de verdad de la configuración persistente. Lee/escribe DataStore, normaliza valores, aplica límites y mantiene compatibilidad con las claves y opciones aceptadas por la aplicación.

**Arquitectura.** La UI no escribe preferencias directamente: los cambios pasan por SettingsViewModel y terminan en este repositorio. Por eso aquí se concentran las reglas de persistencia y normalización.

**Flujo general.** Flujo típico: DataStore emite Preferences -> el repositorio las transforma en `AppSettings` -> SettingsViewModel publica StateFlow -> Compose recompone. En sentido inverso, un control llama a un setter del ViewModel -> el repositorio normaliza y guarda la clave correspondiente.

## 2. Package e imports

El `package` es `com.example.mynotes.settings`. Determina el namespace lógico del archivo, resolución de visibilidad `internal`/package-related tooling y la ruta conceptual desde la que otras clases lo importan.

El archivo declara **10 imports**. Cada import evita usar el nombre totalmente calificado en el cuerpo y, además, revela las dependencias técnicas del bloque:

**Android/plataforma:** `android.content.Context`.

**Jetpack/Compose:** `androidx.datastore.preferences.core.booleanPreferencesKey`, `androidx.datastore.preferences.core.edit`, `androidx.datastore.preferences.core.floatPreferencesKey`, `androidx.datastore.preferences.core.intPreferencesKey`, `androidx.datastore.preferences.core.stringPreferencesKey`, `androidx.datastore.preferences.preferencesDataStore`.

**Kotlin/corrutinas/Java:** `kotlinx.coroutines.flow.Flow`, `kotlinx.coroutines.flow.distinctUntilChanged`, `kotlinx.coroutines.flow.map`.

## 3. Restricciones e invariantes visibles en el archivo

- **Fallback nulo (52 aparición/apariciones):** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula.
- **Límite numérico (67 aparición/apariciones):** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados.

## 4. Bloques de código, uno por uno

### 4.1 `SettingsRepository` — class, líneas 16–598

```kotlin
class SettingsRepository(private val context: Context) {
    // … el cuerpo completo permanece en el archivo real; sus miembros se documentan individualmente abajo …
}
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros del constructor/encabezado:**
- `private val context: Context` — No se pudo separar automáticamente nombre/tipo; se conserva la firma exacta como contrato.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 18 | `val DARK_MODE` | `inferido` | `booleanPreferencesKey("dark_mode")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 19 | `val BACKGROUND_COLOR` | `inferido` | `stringPreferencesKey("background_color")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 20 | `val BACKGROUND_TONE_INDEX` | `inferido` | `intPreferencesKey("background_tone_index")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 21 | `val BACKGROUND_INTENSITY` | `inferido` | `floatPreferencesKey("background_intensity")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 22 | `val SETTINGS_PANEL_TONE` | `inferido` | `floatPreferencesKey("settings_panel_tone")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 23 | `val SURFACE_PANEL_INTENSITY` | `inferido` | `floatPreferencesKey("surface_panel_intensity")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 24 | `val HEADER_INTENSITY` | `inferido` | `floatPreferencesKey("header_intensity")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 25 | `val TEXT_COLOR` | `inferido` | `stringPreferencesKey("text_color")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 26 | `val TEXT_OUTLINE_ENABLED` | `inferido` | `booleanPreferencesKey("text_outline_enabled")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 27 | `val NOTE_UI_TEXT_COLOR` | `inferido` | `stringPreferencesKey("note_ui_text_color")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 28 | `val SLIDER_STYLE` | `inferido` | `stringPreferencesKey("slider_style")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 29 | `val FONT` | `inferido` | `stringPreferencesKey("font")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 30 | `val FONT_SIZE` | `inferido` | `floatPreferencesKey("font_size")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 31 | `val SOUND_EFFECTS_ENABLED` | `inferido` | `booleanPreferencesKey("sound_effects_enabled")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 32 | `val SOUND_EFFECTS_VOLUME` | `inferido` | `floatPreferencesKey("sound_effects_volume")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 33 | `val SOUND_EFFECTS_THEME` | `inferido` | `stringPreferencesKey("sound_effects_theme")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 34 | `val HAPTIC_EFFECTS_ENABLED` | `inferido` | `booleanPreferencesKey("haptic_effects_enabled")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 35 | `val HAPTIC_EFFECTS_INTENSITY` | `inferido` | `floatPreferencesKey("haptic_effects_intensity")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 36 | `val HAPTIC_EFFECTS_STYLE` | `inferido` | `stringPreferencesKey("haptic_effects_style")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 37 | `val LANGUAGE` | `inferido` | `stringPreferencesKey("language")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 38 | `val GRID_COLUMNS` | `inferido` | `intPreferencesKey("grid_columns")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 39 | `val SORT_ORDER` | `inferido` | `stringPreferencesKey("sort_order")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 40 | `val PROFILE_IMAGE_URI` | `inferido` | `stringPreferencesKey("profile_image_uri")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 41 | `val PROFILE_IMAGE_SIZE` | `inferido` | `floatPreferencesKey("profile_image_size")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 42 | `val ICON_STYLE` | `inferido` | `stringPreferencesKey("icon_style")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 43 | `val ICON_SIZE` | `inferido` | `floatPreferencesKey("icon_size")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 44 | `val ACCENT_COLOR` | `inferido` | `stringPreferencesKey("accent_color")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 45 | `val NOTE_CARD_CORNER_RADIUS` | `inferido` | `floatPreferencesKey("note_card_corner_radius")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 46 | `val NOTE_CARD_ELEVATION` | `inferido` | `floatPreferencesKey("note_card_elevation")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 47 | `val NOTE_CARD_PADDING` | `inferido` | `floatPreferencesKey("note_card_padding")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 48 | `val NOTE_CARD_IMAGE_HEIGHT` | `inferido` | `floatPreferencesKey("note_card_image_height")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 49 | `val NOTE_TITLE_MAX_LINES` | `inferido` | `intPreferencesKey("note_title_max_lines")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 50 | `val NOTE_CONTENT_MAX_LINES` | `inferido` | `intPreferencesKey("note_content_max_lines")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 51 | `val NOTE_LINE_SPACING` | `inferido` | `floatPreferencesKey("note_line_spacing")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 52 | `val SHOW_NOTE_DATE` | `inferido` | `booleanPreferencesKey("show_note_date")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 53 | `val SHOW_CATEGORY_CHIP` | `inferido` | `booleanPreferencesKey("show_category_chip")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 54 | `val SHOW_FAVORITE_ICON` | `inferido` | `booleanPreferencesKey("show_favorite_icon")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 55 | `val FAB_SIZE` | `inferido` | `floatPreferencesKey("fab_size")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 56 | `val OPTION_MENU_ORDER` | `inferido` | `stringPreferencesKey("option_menu_order")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 57 | `val OPTION_MENU_HIDDEN_ITEMS` | `inferido` | `stringPreferencesKey("option_menu_hidden_items")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 58 | `val OPTION_MENU_SHOW_ICONS` | `inferido` | `booleanPreferencesKey("option_menu_show_icons")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 59 | `val OPTION_MENU_TEXT_COLOR` | `inferido` | `stringPreferencesKey("option_menu_text_color")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 60 | `val OPTION_MENU_OPACITY` | `inferido` | `floatPreferencesKey("option_menu_opacity")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 61 | `val PRIORITY_MENU_HIDDEN_ITEMS` | `inferido` | `stringPreferencesKey("priority_menu_hidden_items")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 62 | `val COLOR_MENU_HIDDEN_ITEMS` | `inferido` | `stringPreferencesKey("color_menu_hidden_items")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 63 | `val PERFORMANCE_MODE` | `inferido` | `stringPreferencesKey("performance_mode")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 64 | `val ANIMATIONS_ENABLED` | `inferido` | `booleanPreferencesKey("animations_enabled")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 65 | `val ANIMATION_SPEED` | `inferido` | `floatPreferencesKey("animation_speed")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 66 | `val ANIMATION_STYLE` | `inferido` | `stringPreferencesKey("animation_style")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 67 | `val ANIMATION_EASING` | `inferido` | `stringPreferencesKey("animation_easing")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 68 | `val ANIMATION_INTENSITY` | `inferido` | `floatPreferencesKey("animation_intensity")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 74 | `val rawPalette` | `inferido` | `preferences[BACKGROUND_COLOR]?: "neutral"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 83 | `val rawTone` | `inferido` | `preferences[BACKGROUND_TONE_INDEX]?: if (isLegacyPalette(rawPalette)) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 88 | `val normalizedTone` | `inferido` | `if (isLegacyPalette(rawPalette)) {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 165 | `val rawPalette` | `inferido` | `preferences[BACKGROUND_COLOR]?: "neutral"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 480 | `val MAIN_OPTION_MENU_KEYS` | `inferido` | `listOf("edit", "favorite", "pin", "priority", "color", "move", "delete")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 481 | `val PRIORITY_OPTION_KEYS` | `inferido` | `listOf("none", "low", "medium", "high")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 482 | `val COLOR_OPTION_KEYS` | `inferido` | `listOf("default", "yellow", "orange", "red", "pink", "purple", "blue", "cyan", "teal", "green", "min…` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 484 | `val DEFAULT_OPTION_MENU_ORDER` | `inferido` | `MAIN_OPTION_MENU_KEYS.joinToString(",")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 486 | `val requested` | `inferido` | `value.split(",").map {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 535 | `val currentPaletteKeys` | `inferido` | `setOf("neutral", "warm", "sage", "ocean", "lavender", "rose", "sunset", "violet_dusk", "marple",` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 545 | `val legacyPaletteKeys` | `inferido` | `setOf("yellow", "purple", "pink", "default", "gray")` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 73 | `preferences ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 160 | `preferences ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 491 | `return (requested + MAIN_OPTION_MENU_KEYS.filterNot {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 496 | `return value.split(",").map {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 503 | `"note", "black", "white" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 504 | `else -> "note"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 507 | `"performance", "balanced", "quality" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 508 | `else -> "balanced"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 511 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 514 | `"random" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 515 | `else -> "zoom"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 519 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 520 | `"standard", "linear", "accelerate", "decelerate", "emphasized" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 521 | `else -> "standard"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 547 | `if (value in currentPaletteKeys) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 548 | `return value` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 550 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 551 | `"yellow" -> "sun"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 552 | `"purple" -> "lavender"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 553 | `"pink" -> "soft_pink"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 554 | `"default", "gray" -> "neutral"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 555 | `else -> "neutral"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 560 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 561 | `"auto", "black", "white" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 562 | `else -> "auto"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 566 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 567 | `"material", "rounded", "outlined", "minimal" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 568 | `else -> "rounded"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 572 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 574 | `"violet", "purple", "pink", "rose", "brown", "graphite" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 575 | `else -> "palette"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 579 | `return when (value.trim().lowercase()) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 581 | `"typewriter", "metal", "pixel", "space", "chime", "paper", "neon" -> value.trim().lowercase()` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 582 | `else -> "classic"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 586 | `return when (value.trim().lowercase()) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 588 | `"heavy", "spring", "echo" -> value.trim().lowercase()` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 589 | `else -> "soft"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |
| 593 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 594 | `"minimal", "capsule", "glow", "glass", "segmented", "dots", "gradient", "neumorphic", "line_pill", "floating" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 595 | `else -> "minimal"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 52.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 67.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `booleanPreferencesKey`, `stringPreferencesKey`, `intPreferencesKey`, `floatPreferencesKey`, `isLegacyPalette`, `rawTone.coerceIn`, `AppSettings`, `normalizePaletteKey`, `coerceIn`, `normalizeUiTextColor`, `normalizeSliderStyle`, `normalizeSoundEffectsTheme`, `normalizeHapticEffectsStyle`, `normalizeIconStyle`, `normalizeAccentColor`, `normalizeOptionMenuOrder`, `normalizeHiddenItems`, `normalizeOptionMenuTextColor`, `normalizePerformanceMode`, `normalizeAnimationStyle`, `normalizeAnimationEasing`, `distinctUntilChanged`, `value.coerceIn`, `value.backgroundToneIndex.coerceIn`, `value.backgroundIntensity.coerceIn`, `value.settingsPanelTone.coerceIn`, `value.surfacePanelIntensity.coerceIn`, `value.headerIntensity.coerceIn`, `value.fontSize.coerceIn`, `value.soundEffectsVolume.coerceIn`, `value.hapticEffectsIntensity.coerceIn`, `value.gridColumns.coerceIn`, `value.profileImageSize.coerceIn`, `value.iconSize.coerceIn`, `value.noteCardCornerRadius.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.
- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.2 `setDarkMode` — fun, líneas 148–152

```kotlin
    suspend fun setDarkMode(value: Boolean) {
        context.dataStore.edit {
                it[DARK_MODE] = value
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.3 `setBackgroundColor` — fun, líneas 153–157

```kotlin
    suspend fun setBackgroundColor(value: String) {
        context.dataStore.edit {
                it[BACKGROUND_COLOR] = normalizePaletteKey(value)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizePaletteKey`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.4 `setBackgroundToneIndex` — fun, líneas 158–169

```kotlin
    suspend fun setBackgroundToneIndex(value: Int) {
        context.dataStore.edit {
                    preferences ->
                /*
                 * Si aún estaba guardada una paleta antigua,
                 * la convertimos cuando el usuario toque un tono.
                 */
                val rawPalette = preferences[BACKGROUND_COLOR]?: "neutral"
                preferences[BACKGROUND_COLOR] = normalizePaletteKey(rawPalette)
                preferences[BACKGROUND_TONE_INDEX] = value.coerceIn(0, 3)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Int` — `value` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 165 | `val rawPalette` | `inferido` | `preferences[BACKGROUND_COLOR]?: "neutral"` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 160 | `preferences ->` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |

**Restricciones concretas que aparecen en este bloque:**
- **Fallback nulo:** El operador Elvis establece explícitamente el valor/comportamiento alternativo cuando la expresión previa es nula. Apariciones en este bloque: 1.
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizePaletteKey`, `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.5 `setBackgroundIntensity` — fun, líneas 170–174

```kotlin
    suspend fun setBackgroundIntensity(value: Float) {
        context.dataStore.edit {
                it[BACKGROUND_INTENSITY] = value.coerceIn(0f, 100f)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.6 `setSettingsPanelTone` — fun, líneas 175–179

```kotlin
    suspend fun setSettingsPanelTone(value: Float) {
        context.dataStore.edit {
                it[SETTINGS_PANEL_TONE] = value.coerceIn(0f, 100f)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.7 `setSurfacePanelIntensity` — fun, líneas 180–184

```kotlin
    suspend fun setSurfacePanelIntensity(value: Float) {
        context.dataStore.edit {
                it[SURFACE_PANEL_INTENSITY] = value.coerceIn(0f, 100f)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.8 `setHeaderIntensity` — fun, líneas 185–189

```kotlin
    suspend fun setHeaderIntensity(value: Float) {
        context.dataStore.edit {
                it[HEADER_INTENSITY] = value.coerceIn(0f, 100f)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.9 `setTextColor` — fun, líneas 190–194

```kotlin
    suspend fun setTextColor(value: String) {
        context.dataStore.edit {
                it[TEXT_COLOR] = normalizeUiTextColor(value)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeUiTextColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.10 `setTextOutlineEnabled` — fun, líneas 195–199

```kotlin
    suspend fun setTextOutlineEnabled(value: Boolean) {
        context.dataStore.edit {
                it[TEXT_OUTLINE_ENABLED] = value
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.11 `setNoteUiTextColor` — fun, líneas 200–204

```kotlin
    suspend fun setNoteUiTextColor(value: String) {
        context.dataStore.edit {
                it[NOTE_UI_TEXT_COLOR] = normalizeUiTextColor(value)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeUiTextColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.12 `setSliderStyle` — fun, líneas 205–209

```kotlin
    suspend fun setSliderStyle(value: String) {
        context.dataStore.edit {
                it[SLIDER_STYLE] = normalizeSliderStyle(value)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeSliderStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.13 `setFont` — fun, líneas 210–214

```kotlin
    suspend fun setFont(value: String) {
        context.dataStore.edit {
                it[FONT] = value
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.14 `setFontSize` — fun, líneas 215–219

```kotlin
    suspend fun setFontSize(value: Float) {
        context.dataStore.edit {
                it[FONT_SIZE] = value.coerceIn(12f, 28f)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.15 `setSoundEffectsEnabled` — fun, líneas 220–224

```kotlin
    suspend fun setSoundEffectsEnabled(value: Boolean) {
        context.dataStore.edit {
                it[SOUND_EFFECTS_ENABLED] = value
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.16 `setSoundEffectsVolume` — fun, líneas 225–229

```kotlin
    suspend fun setSoundEffectsVolume(value: Float) {
        context.dataStore.edit {
                it[SOUND_EFFECTS_VOLUME] = value.coerceIn(0f, 100f)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.17 `setSoundEffectsTheme` — fun, líneas 230–234

```kotlin
    suspend fun setSoundEffectsTheme(value: String) {
        context.dataStore.edit {
                it[SOUND_EFFECTS_THEME] = normalizeSoundEffectsTheme(value)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeSoundEffectsTheme`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.18 `setHapticEffectsEnabled` — fun, líneas 235–239

```kotlin
    suspend fun setHapticEffectsEnabled(value: Boolean) {
        context.dataStore.edit {
                it[HAPTIC_EFFECTS_ENABLED] = value
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.19 `setHapticEffectsIntensity` — fun, líneas 240–244

```kotlin
    suspend fun setHapticEffectsIntensity(value: Float) {
        context.dataStore.edit {
                it[HAPTIC_EFFECTS_INTENSITY] = value.coerceIn(0f, 100f)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.20 `setHapticEffectsStyle` — fun, líneas 245–249

```kotlin
    suspend fun setHapticEffectsStyle(value: String) {
        context.dataStore.edit {
                it[HAPTIC_EFFECTS_STYLE] = normalizeHapticEffectsStyle(value)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeHapticEffectsStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.21 `setLanguage` — fun, líneas 250–254

```kotlin
    suspend fun setLanguage(value: String) {
        context.dataStore.edit {
                it[LANGUAGE] = value
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.22 `setGridColumns` — fun, líneas 255–259

```kotlin
    suspend fun setGridColumns(value: Int) {
        context.dataStore.edit {
                it[GRID_COLUMNS] = value.coerceIn(1, 3)
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Int` — `value` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.23 `setSortOrder` — fun, líneas 260–264

```kotlin
    suspend fun setSortOrder(value: String) {
        context.dataStore.edit {
                it[SORT_ORDER] = value
            }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.24 `setProfileImageUri` — fun, líneas 265–269

```kotlin
    suspend fun setProfileImageUri(value: String) {
        context.dataStore.edit {
            it[PROFILE_IMAGE_URI] = value
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.25 `setProfileImageSize` — fun, líneas 270–274

```kotlin
    suspend fun setProfileImageSize(value: Float) {
        context.dataStore.edit {
            it[PROFILE_IMAGE_SIZE] = value.coerceIn(36f, 84f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.26 `setIconStyle` — fun, líneas 275–279

```kotlin
    suspend fun setIconStyle(value: String) {
        context.dataStore.edit {
            it[ICON_STYLE] = normalizeIconStyle(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeIconStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.27 `setIconSize` — fun, líneas 280–284

```kotlin
    suspend fun setIconSize(value: Float) {
        context.dataStore.edit {
            it[ICON_SIZE] = value.coerceIn(16f, 36f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.28 `setAccentColor` — fun, líneas 285–289

```kotlin
    suspend fun setAccentColor(value: String) {
        context.dataStore.edit {
            it[ACCENT_COLOR] = normalizeAccentColor(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeAccentColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.29 `setNoteCardCornerRadius` — fun, líneas 290–294

```kotlin
    suspend fun setNoteCardCornerRadius(value: Float) {
        context.dataStore.edit {
            it[NOTE_CARD_CORNER_RADIUS] = value.coerceIn(0f, 36f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.30 `setNoteCardElevation` — fun, líneas 295–299

```kotlin
    suspend fun setNoteCardElevation(value: Float) {
        context.dataStore.edit {
            it[NOTE_CARD_ELEVATION] = value.coerceIn(0f, 12f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.31 `setNoteCardPadding` — fun, líneas 300–304

```kotlin
    suspend fun setNoteCardPadding(value: Float) {
        context.dataStore.edit {
            it[NOTE_CARD_PADDING] = value.coerceIn(6f, 24f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.32 `setNoteCardImageHeight` — fun, líneas 305–309

```kotlin
    suspend fun setNoteCardImageHeight(value: Float) {
        context.dataStore.edit {
            it[NOTE_CARD_IMAGE_HEIGHT] = value.coerceIn(72f, 220f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.33 `setNoteTitleMaxLines` — fun, líneas 310–314

```kotlin
    suspend fun setNoteTitleMaxLines(value: Int) {
        context.dataStore.edit {
            it[NOTE_TITLE_MAX_LINES] = value.coerceIn(1, 8)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Int` — `value` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.34 `setNoteContentMaxLines` — fun, líneas 315–319

```kotlin
    suspend fun setNoteContentMaxLines(value: Int) {
        context.dataStore.edit {
            it[NOTE_CONTENT_MAX_LINES] = value.coerceIn(2, 14)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Int` — `value` recibe un valor de tipo `Int`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.35 `setNoteLineSpacing` — fun, líneas 320–324

```kotlin
    suspend fun setNoteLineSpacing(value: Float) {
        context.dataStore.edit {
            it[NOTE_LINE_SPACING] = value.coerceIn(1f, 1.8f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.36 `setShowNoteDate` — fun, líneas 325–329

```kotlin
    suspend fun setShowNoteDate(value: Boolean) {
        context.dataStore.edit {
            it[SHOW_NOTE_DATE] = value
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.37 `setShowCategoryChip` — fun, líneas 330–334

```kotlin
    suspend fun setShowCategoryChip(value: Boolean) {
        context.dataStore.edit {
            it[SHOW_CATEGORY_CHIP] = value
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.38 `setShowFavoriteIcon` — fun, líneas 335–339

```kotlin
    suspend fun setShowFavoriteIcon(value: Boolean) {
        context.dataStore.edit {
            it[SHOW_FAVORITE_ICON] = value
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.39 `setFabSize` — fun, líneas 340–344

```kotlin
    suspend fun setFabSize(value: Float) {
        context.dataStore.edit {
            it[FAB_SIZE] = value.coerceIn(48f, 82f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.40 `setOptionMenuOrder` — fun, líneas 345–349

```kotlin
    suspend fun setOptionMenuOrder(value: String) {
        context.dataStore.edit {
            it[OPTION_MENU_ORDER] = normalizeOptionMenuOrder(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeOptionMenuOrder`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.41 `setOptionMenuHiddenItems` — fun, líneas 350–354

```kotlin
    suspend fun setOptionMenuHiddenItems(value: String) {
        context.dataStore.edit {
            it[OPTION_MENU_HIDDEN_ITEMS] = normalizeHiddenItems(value, MAIN_OPTION_MENU_KEYS)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeHiddenItems`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.42 `setOptionMenuShowIcons` — fun, líneas 355–359

```kotlin
    suspend fun setOptionMenuShowIcons(value: Boolean) {
        context.dataStore.edit {
            it[OPTION_MENU_SHOW_ICONS] = value
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.43 `setOptionMenuTextColor` — fun, líneas 360–364

```kotlin
    suspend fun setOptionMenuTextColor(value: String) {
        context.dataStore.edit {
            it[OPTION_MENU_TEXT_COLOR] = normalizeOptionMenuTextColor(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeOptionMenuTextColor`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.44 `setOptionMenuOpacity` — fun, líneas 365–369

```kotlin
    suspend fun setOptionMenuOpacity(value: Float) {
        context.dataStore.edit {
            it[OPTION_MENU_OPACITY] = value.coerceIn(35f, 100f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.45 `setPriorityMenuHiddenItems` — fun, líneas 370–374

```kotlin
    suspend fun setPriorityMenuHiddenItems(value: String) {
        context.dataStore.edit {
            it[PRIORITY_MENU_HIDDEN_ITEMS] = normalizeHiddenItems(value, PRIORITY_OPTION_KEYS)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeHiddenItems`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.46 `setColorMenuHiddenItems` — fun, líneas 375–379

```kotlin
    suspend fun setColorMenuHiddenItems(value: String) {
        context.dataStore.edit {
            it[COLOR_MENU_HIDDEN_ITEMS] = normalizeHiddenItems(value, COLOR_OPTION_KEYS)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeHiddenItems`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.47 `resetOptionMenuSettings` — fun, líneas 380–390

```kotlin
    suspend fun resetOptionMenuSettings() {
        context.dataStore.edit {
            it[OPTION_MENU_ORDER] = DEFAULT_OPTION_MENU_ORDER
            it[OPTION_MENU_HIDDEN_ITEMS] = ""
            it[OPTION_MENU_SHOW_ICONS] = true
            it[OPTION_MENU_TEXT_COLOR] = "note"
            it[OPTION_MENU_OPACITY] = 100f
            it[PRIORITY_MENU_HIDDEN_ITEMS] = ""
            it[COLOR_MENU_HIDDEN_ITEMS] = ""
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:** ninguno explícito.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.48 `setPerformanceMode` — fun, líneas 391–395

```kotlin
    suspend fun setPerformanceMode(value: String) {
        context.dataStore.edit {
            it[PERFORMANCE_MODE] = normalizePerformanceMode(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizePerformanceMode`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.49 `setAnimationsEnabled` — fun, líneas 396–400

```kotlin
    suspend fun setAnimationsEnabled(value: Boolean) {
        context.dataStore.edit {
            it[ANIMATIONS_ENABLED] = value
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Boolean` — `value` recibe un valor de tipo `Boolean`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.50 `setAnimationSpeed` — fun, líneas 401–405

```kotlin
    suspend fun setAnimationSpeed(value: Float) {
        context.dataStore.edit {
            it[ANIMATION_SPEED] = value.coerceIn(0.5f, 2f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.51 `setAnimationStyle` — fun, líneas 406–410

```kotlin
    suspend fun setAnimationStyle(value: String) {
        context.dataStore.edit {
            it[ANIMATION_STYLE] = normalizeAnimationStyle(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeAnimationStyle`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.52 `setAnimationEasing` — fun, líneas 411–415

```kotlin
    suspend fun setAnimationEasing(value: String) {
        context.dataStore.edit {
            it[ANIMATION_EASING] = normalizeAnimationEasing(value)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizeAnimationEasing`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.53 `setAnimationIntensity` — fun, líneas 416–420

```kotlin
    suspend fun setAnimationIntensity(value: Float) {
        context.dataStore.edit {
            it[ANIMATION_INTENSITY] = value.coerceIn(0.5f, 1.5f)
        }
    }
```

#### Qué hace y por qué existe

Actualiza o persiste el estado representado por sus parámetros y deja el nuevo valor disponible para el resto de la aplicación.

#### Contrato de la declaración

**Parámetros:**

- `value: Float` — `value` recibe un valor de tipo `Float`. El contrato no marca este parámetro como anulable. Es numérico; cualquier restricción efectiva de rango se aplica dentro del bloque o en la API consumidora.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 1.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.54 `restoreFromBackup` — fun, líneas 426–479

```kotlin
    suspend fun restoreFromBackup(value: AppSettings) {
        context.dataStore.edit { preferences -> preferences[DARK_MODE] = value.darkMode
            preferences[BACKGROUND_COLOR] = normalizePaletteKey(value.backgroundColor)
            preferences[BACKGROUND_TONE_INDEX] = value.backgroundToneIndex.coerceIn(0, 3)
            preferences[BACKGROUND_INTENSITY] = value.backgroundIntensity.coerceIn(0f, 100f)
            preferences[SETTINGS_PANEL_TONE] = value.settingsPanelTone.coerceIn(0f, 100f)
            preferences[SURFACE_PANEL_INTENSITY] = value.surfacePanelIntensity.coerceIn(0f, 100f)
            preferences[HEADER_INTENSITY] = value.headerIntensity.coerceIn(0f, 100f)
            preferences[TEXT_COLOR] = normalizeUiTextColor(value.textColor)
            preferences[TEXT_OUTLINE_ENABLED] = value.textOutlineEnabled
            preferences[NOTE_UI_TEXT_COLOR] = normalizeUiTextColor(value.noteUiTextColor)
            preferences[SLIDER_STYLE] = normalizeSliderStyle(value.sliderStyle)
            preferences[FONT] = value.font
            preferences[FONT_SIZE] = value.fontSize.coerceIn(12f, 28f)
            preferences[SOUND_EFFECTS_ENABLED] = value.soundEffectsEnabled
            preferences[SOUND_EFFECTS_VOLUME] = value.soundEffectsVolume.coerceIn(0f, 100f)
            preferences[SOUND_EFFECTS_THEME] = normalizeSoundEffectsTheme(value.soundEffectsTheme)
            preferences[HAPTIC_EFFECTS_ENABLED] = value.hapticEffectsEnabled
            preferences[HAPTIC_EFFECTS_INTENSITY] = value.hapticEffectsIntensity.coerceIn(0f, 100f)
            preferences[HAPTIC_EFFECTS_STYLE] = normalizeHapticEffectsStyle(value.hapticEffectsStyle)
            preferences[LANGUAGE] = value.language
            preferences[GRID_COLUMNS] = value.gridColumns.coerceIn(1, 3)
            preferences[SORT_ORDER] = value.sortOrder
            preferences[PROFILE_IMAGE_URI] = value.profileImageUri
            preferences[PROFILE_IMAGE_SIZE] = value.profileImageSize.coerceIn(36f, 84f)
            preferences[ICON_STYLE] = normalizeIconStyle(value.iconStyle)
            preferences[ICON_SIZE] = value.iconSize.coerceIn(16f, 36f)
            preferences[ACCENT_COLOR] = normalizeAccentColor(value.accentColor)
            preferences[NOTE_CARD_CORNER_RADIUS] = value.noteCardCornerRadius.coerceIn(0f, 36f)
            preferences[NOTE_CARD_ELEVATION] = value.noteCardElevation.coerceIn(0f, 12f)
            preferences[NOTE_CARD_PADDING] = value.noteCardPadding.coerceIn(6f, 24f)
            preferences[NOTE_CARD_IMAGE_HEIGHT] = value.noteCardImageHeight.coerceIn(72f, 220f)
            preferences[NOTE_TITLE_MAX_LINES] = value.noteTitleMaxLines.coerceIn(1, 8)
            preferences[NOTE_CONTENT_MAX_LINES] = value.noteContentMaxLines.coerceIn(2, 14)
            preferences[NOTE_LINE_SPACING] = value.noteLineSpacing.coerceIn(1f, 1.8f)
            preferences[SHOW_NOTE_DATE] = value.showNoteDate
            preferences[SHOW_CATEGORY_CHIP] = value.showCategoryChip
            preferences[SHOW_FAVORITE_ICON] = value.showFavoriteIcon
            preferences[FAB_SIZE] = value.fabSize.coerceIn(48f, 82f)
            preferences[OPTION_MENU_ORDER] = normalizeOptionMenuOrder(value.optionMenuOrder)
            preferences[OPTION_MENU_HIDDEN_ITEMS] = normalizeHiddenItems(value.optionMenuHiddenItems, MAIN_OPTION_MENU_KEYS)
            preferences[OPTION_MENU_SHOW_ICONS] = value.optionMenuShowIcons
            preferences[OPTION_MENU_TEXT_COLOR] = normalizeOptionMenuTextColor(value.optionMenuTextColor)
            preferences[OPTION_MENU_OPACITY] = value.optionMenuOpacity.coerceIn(35f, 100f)
            preferences[PRIORITY_MENU_HIDDEN_ITEMS] = normalizeHiddenItems(value.priorityMenuHiddenItems, PRIORITY_OPTION_KEYS)
            preferences[COLOR_MENU_HIDDEN_ITEMS] = normalizeHiddenItems(value.colorMenuHiddenItems, COLOR_OPTION_KEYS)
            preferences[PERFORMANCE_MODE] = normalizePerformanceMode(value.performanceMode)
            preferences[ANIMATIONS_ENABLED] = value.animationsEnabled
            preferences[ANIMATION_SPEED] = value.animationSpeed.coerceIn(0.5f, 2f)
            preferences[ANIMATION_STYLE] = normalizeAnimationStyle(value.animationStyle)
            preferences[ANIMATION_EASING] = normalizeAnimationEasing(value.animationEasing)
            preferences[ANIMATION_INTENSITY] = value.animationIntensity.coerceIn(0.5f, 1.5f)
        }
    }
```

#### Qué hace y por qué existe

Bloque funcional que encapsula una responsabilidad concreta del archivo; sus entradas, decisiones y efectos se detallan en las secciones siguientes.

#### Contrato de la declaración

**Parámetros:**

- `value: AppSettings` — `value` recibe un valor de tipo `AppSettings`. El contrato no marca este parámetro como anulable.

**Retorno:** `Unit (implícito)`. No entrega un valor de negocio al llamador; su finalidad es coordinar estado, producir efectos o delegar acciones.

**Modificadores/anotaciones relevantes:**
- `suspend` permite suspender sin bloquear el hilo; el llamador debe estar dentro de una corrutina u otra función suspendible.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

El bloque es principalmente lineal: no contiene `if/when/for/while/try` relevantes detectados o delega las decisiones a expresiones/funciones llamadas.

**Restricciones concretas que aparecen en este bloque:**
- **Límite numérico:** Se fuerza el valor a un intervalo o umbral para impedir que salgan valores fuera de los límites esperados. Apariciones en este bloque: 22.

#### Efectos secundarios y recursos

- **Preferencias/DataStore:** Lee o escribe configuración persistente; el resultado sobrevive a recreaciones de la pantalla y normalmente al reinicio de la app.
- **Mutación de colección/estado compartido:** Modifica una colección/mapa/estructura existente; el cambio permanece visible para quienes compartan esa misma instancia.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `normalizePaletteKey`, `value.backgroundToneIndex.coerceIn`, `value.backgroundIntensity.coerceIn`, `value.settingsPanelTone.coerceIn`, `value.surfacePanelIntensity.coerceIn`, `value.headerIntensity.coerceIn`, `normalizeUiTextColor`, `normalizeSliderStyle`, `value.fontSize.coerceIn`, `value.soundEffectsVolume.coerceIn`, `normalizeSoundEffectsTheme`, `value.hapticEffectsIntensity.coerceIn`, `normalizeHapticEffectsStyle`, `value.gridColumns.coerceIn`, `value.profileImageSize.coerceIn`, `normalizeIconStyle`, `value.iconSize.coerceIn`, `normalizeAccentColor`, `value.noteCardCornerRadius.coerceIn`, `value.noteCardElevation.coerceIn`, `value.noteCardPadding.coerceIn`, `value.noteCardImageHeight.coerceIn`, `value.noteTitleMaxLines.coerceIn`, `value.noteContentMaxLines.coerceIn`, `value.noteLineSpacing.coerceIn`, `value.fabSize.coerceIn`, `normalizeOptionMenuOrder`, `normalizeHiddenItems`, `normalizeOptionMenuTextColor`, `value.optionMenuOpacity.coerceIn`, `normalizePerformanceMode`, `value.animationSpeed.coerceIn`, `normalizeAnimationStyle`, `normalizeAnimationEasing`, `value.animationIntensity.coerceIn`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Preservar la normalización y correspondencia de `performanceMode`, porque otras capas usan las mismas claves para rendimiento/calidad.

### 4.55 `normalizeOptionMenuOrder` — fun, líneas 485–494

```kotlin
    private fun normalizeOptionMenuOrder(value: String): String {
        val requested = value.split(",").map {
                    it.trim()
                }.filter {
                    it in MAIN_OPTION_MENU_KEYS
                }.distinct()
        return (requested + MAIN_OPTION_MENU_KEYS.filterNot {
                        it in requested
                    }).joinToString(",")
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

| Línea | Variable | Tipo | Inicializador (inicio) | Lectura detallada |
|---:|---|---|---|---|
| 486 | `val requested` | `inferido` | `value.split(",").map {` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 491 | `return (requested + MAIN_OPTION_MENU_KEYS.filterNot {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.split`, `it.trim`, `distinct`, `joinToString`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.56 `normalizeHiddenItems` — fun, líneas 495–501

```kotlin
    private fun normalizeHiddenItems(value: String, validKeys: List<String>): String {
        return value.split(",").map {
                it.trim()
            }.filter {
                it in validKeys
            }.distinct().joinToString(",")
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.
- `validKeys: List<String>` — `validKeys` recibe un valor de tipo `List<String>`. El contrato no marca este parámetro como anulable. Aunque el tipo sea `String`, semánticamente suele funcionar como clave/enumeración textual; las ramas del código delimitan los valores reconocidos.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 496 | `return value.split(",").map {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.split`, `it.trim`, `distinct`, `joinToString`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Conservar orden de llamadas, valores por defecto, visibilidad y tipos: aunque parezcan detalles de estilo, forman parte del contrato actual del bloque.

### 4.57 `normalizeOptionMenuTextColor` — fun, líneas 502–505

```kotlin
    private fun normalizeOptionMenuTextColor(value: String): String = when (value) {
            "note", "black", "white" -> value
            else -> "note"
        }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 503 | `"note", "black", "white" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 504 | `else -> "note"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.58 `normalizePerformanceMode` — fun, líneas 506–509

```kotlin
    private fun normalizePerformanceMode(value: String): String = when (value) {
            "performance", "balanced", "quality" -> value
            else -> "balanced"
        }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 507 | `"performance", "balanced", "quality" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 508 | `else -> "balanced"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.59 `normalizeAnimationStyle` — fun, líneas 510–517

```kotlin
    private fun normalizeAnimationStyle(value: String): String {
        return when (value) {
            "zoom", "zoom_fade", "fade", "slide_left", "slide_right", "slide_up", "slide_down", "slide_zoom_left", "slide_zoom_up",
            "axis_x", "axis_y", "axis_z", "expand", "expand_horizontal", "expand_vertical", "bounce", "elastic", "pop", "subtle",
            "random" -> value
            else -> "zoom"
        }
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 511 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 514 | `"random" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 515 | `else -> "zoom"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.60 `normalizeAnimationEasing` — fun, líneas 518–523

```kotlin
    private fun normalizeAnimationEasing(value: String): String {
        return when (value) {
            "standard", "linear", "accelerate", "decelerate", "emphasized" -> value
            else -> "standard"
        }
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 519 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 520 | `"standard", "linear", "accelerate", "decelerate", "emphasized" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 521 | `else -> "standard"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.61 `normalizePaletteKey` — fun, líneas 546–557

```kotlin
    private fun normalizePaletteKey(value: String): String {
        if (value in currentPaletteKeys) {
            return value
        }
        return when (value) {
            "yellow" -> "sun"
            "purple" -> "lavender"
            "pink" -> "soft_pink"
            "default", "gray" -> "neutral"
            else -> "neutral"
        }
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 547 | `if (value in currentPaletteKeys) {` | Rama condicional: el contenido solo se ejecuta cuando la expresión booleana es verdadera. Esta condición funciona como una restricción dinámica del flujo. |
| 548 | `return value` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 550 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 551 | `"yellow" -> "sun"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 552 | `"purple" -> "lavender"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 553 | `"pink" -> "soft_pink"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 554 | `"default", "gray" -> "neutral"` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 555 | `else -> "neutral"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.62 `isLegacyPalette` — fun, líneas 558–564

```kotlin
    private fun isLegacyPalette(value: String): Boolean = value in legacyPaletteKeys
    private fun normalizeUiTextColor(value: String): String {
        return when (value) {
            "auto", "black", "white" -> value
            else -> "auto"
        }
    }
```

#### Qué hace y por qué existe

Evalúa una condición y devuelve/representa una decisión booleana utilizada por otras ramas del flujo.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `Boolean`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 560 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 561 | `"auto", "black", "white" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 562 | `else -> "auto"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.63 `normalizeUiTextColor` — fun, líneas 559–564

```kotlin
    private fun normalizeUiTextColor(value: String): String {
        return when (value) {
            "auto", "black", "white" -> value
            else -> "auto"
        }
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 560 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 561 | `"auto", "black", "white" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 562 | `else -> "auto"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.64 `normalizeIconStyle` — fun, líneas 565–570

```kotlin
    private fun normalizeIconStyle(value: String): String {
        return when (value) {
            "material", "rounded", "outlined", "minimal" -> value
            else -> "rounded"
        }
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 566 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 567 | `"material", "rounded", "outlined", "minimal" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 568 | `else -> "rounded"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.65 `normalizeAccentColor` — fun, líneas 571–577

```kotlin
    private fun normalizeAccentColor(value: String): String {
        return when (value) {
            "palette", "red", "coral", "orange", "amber", "yellow", "lime", "green", "mint", "teal", "cyan", "sky", "blue", "indigo",
            "violet", "purple", "pink", "rose", "brown", "graphite" -> value
            else -> "palette"
        }
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 572 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 574 | `"violet", "purple", "pink", "rose", "brown", "graphite" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 575 | `else -> "palette"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.66 `normalizeSoundEffectsTheme` — fun, líneas 578–584

```kotlin
    private fun normalizeSoundEffectsTheme(value: String): String {
        return when (value.trim().lowercase()) {
            "soft", "digital", "glass", "retro", "pop", "mechanical", "bubble", "arcade", "wood", "synth", "minimal", "camera",
            "typewriter", "metal", "pixel", "space", "chime", "paper", "neon" -> value.trim().lowercase()
            else -> "classic"
        }
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 579 | `return when (value.trim().lowercase()) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 581 | `"typewriter", "metal", "pixel", "space", "chime", "paper", "neon" -> value.trim().lowercase()` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 582 | `else -> "classic"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.trim`, `lowercase`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.67 `normalizeHapticEffectsStyle` — fun, líneas 585–591

```kotlin
    private fun normalizeHapticEffectsStyle(value: String): String {
        return when (value.trim().lowercase()) {
            "crisp", "deep", "double", "pulse", "stepped", "mechanical", "minimal", "triple", "ripple", "heartbeat", "snap", "wave",
            "heavy", "spring", "echo" -> value.trim().lowercase()
            else -> "soft"
        }
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 586 | `return when (value.trim().lowercase()) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 588 | `"heavy", "spring", "echo" -> value.trim().lowercase()` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 589 | `else -> "soft"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

Entre las llamadas presentes están: `value.trim`, `lowercase`. Estas llamadas representan los puntos en los que el bloque delega cálculo, UI, persistencia o acceso a plataforma. La semántica exacta depende del receptor y se sigue en el archivo que define cada llamada cuando pertenece al proyecto.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

### 4.68 `normalizeSliderStyle` — fun, líneas 592–597

```kotlin
    private fun normalizeSliderStyle(value: String): String {
        return when (value) {
            "minimal", "capsule", "glow", "glass", "segmented", "dots", "gradient", "neumorphic", "line_pill", "floating" -> value
            else -> "minimal"
        }
    }
```

#### Qué hace y por qué existe

Normaliza una entrada a un conjunto de valores aceptados, proporcionando una salida estable aunque el dato original venga con variantes no canónicas.

#### Contrato de la declaración

**Parámetros:**

- `value: String` — `value` recibe un valor de tipo `String`. El contrato no marca este parámetro como anulable.

**Retorno:** `String`. Este tipo forma parte del contrato: el llamador recibe ese resultado y puede encadenarlo, almacenarlo o usarlo para decidir el siguiente paso.

**Modificadores/anotaciones relevantes:**
- `private` restringe el acceso al contenedor/archivo correspondiente; evita que otras capas dependan accidentalmente de este detalle interno.

#### Variables y estado dentro del bloque

No declara variables locales simples detectables; trabaja directamente con parámetros, propiedades del contenedor o expresiones encadenadas.

#### Flujo de control y restricciones internas

| Línea | Cabecera | Significado operativo |
|---:|---|---|
| 593 | `return when (value) {` | Salida anticipada o entrega de resultado: termina la ejecución del bloque actual en ese punto. |
| 594 | `"minimal", "capsule", "glow", "glass", "segmented", "dots", "gradient", "neumorphic", "line_pill", "floating" -> value` | Caso o rama de una expresión `when`/lambda. La parte izquierda determina cuándo se usa la expresión situada después de `->`; debe conservarse la cobertura de casos y su precedencia. |
| 595 | `else -> "minimal"` | Rama de respaldo: cubre los casos que no fueron aceptados por las condiciones anteriores. |

#### Efectos secundarios y recursos

No se detecta un efecto externo obvio; el bloque parece calcular/devolver valores o delegar trabajo sin una escritura explícita identificable.

#### Dependencias de ejecución / llamadas relevantes

No se detectaron llamadas de función relevantes fuera de la propia estructura de la declaración.

#### Qué no debe romperse al modificar este bloque

- Mantener cubiertos los mismos casos del `when` y el mismo fallback/`else`, porque ahí se define la política para entradas no reconocidas.

## 5. Inventario global de propiedades/variables detectadas

| Línea | Identificador | Mutabilidad | Tipo | Explicación |
|---:|---|---|---|---|
| 18 | `DARK_MODE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 19 | `BACKGROUND_COLOR` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 20 | `BACKGROUND_TONE_INDEX` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 21 | `BACKGROUND_INTENSITY` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 22 | `SETTINGS_PANEL_TONE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 23 | `SURFACE_PANEL_INTENSITY` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 24 | `HEADER_INTENSITY` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 25 | `TEXT_COLOR` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 26 | `TEXT_OUTLINE_ENABLED` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 27 | `NOTE_UI_TEXT_COLOR` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 28 | `SLIDER_STYLE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 29 | `FONT` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 30 | `FONT_SIZE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 31 | `SOUND_EFFECTS_ENABLED` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 32 | `SOUND_EFFECTS_VOLUME` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 33 | `SOUND_EFFECTS_THEME` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 34 | `HAPTIC_EFFECTS_ENABLED` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 35 | `HAPTIC_EFFECTS_INTENSITY` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 36 | `HAPTIC_EFFECTS_STYLE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 37 | `LANGUAGE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 38 | `GRID_COLUMNS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 39 | `SORT_ORDER` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 40 | `PROFILE_IMAGE_URI` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Representa una ubicación Android `Uri`; no debe asumirse que siempre corresponde a una ruta de archivo convencional. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 41 | `PROFILE_IMAGE_SIZE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 42 | `ICON_STYLE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 43 | `ICON_SIZE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 44 | `ACCENT_COLOR` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 45 | `NOTE_CARD_CORNER_RADIUS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 46 | `NOTE_CARD_ELEVATION` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 47 | `NOTE_CARD_PADDING` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 48 | `NOTE_CARD_IMAGE_HEIGHT` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 49 | `NOTE_TITLE_MAX_LINES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 50 | `NOTE_CONTENT_MAX_LINES` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 51 | `NOTE_LINE_SPACING` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 52 | `SHOW_NOTE_DATE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 53 | `SHOW_CATEGORY_CHIP` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 54 | `SHOW_FAVORITE_ICON` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El nombre indica un estado de decisión/visibilidad/selección usado para controlar una rama de comportamiento o presentación. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 55 | `FAB_SIZE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Es un parámetro dimensional; sus consumidores suelen tratarlo como límite o medida y deben evitar valores fuera del rango admitido por la API correspondiente. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 56 | `OPTION_MENU_ORDER` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 57 | `OPTION_MENU_HIDDEN_ITEMS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 58 | `OPTION_MENU_SHOW_ICONS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 59 | `OPTION_MENU_TEXT_COLOR` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 60 | `OPTION_MENU_OPACITY` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 61 | `PRIORITY_MENU_HIDDEN_ITEMS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 62 | `COLOR_MENU_HIDDEN_ITEMS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 63 | `PERFORMANCE_MODE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Actúa como selector de estrategia/perfil; su valor determina qué rama de configuración se aplica. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 64 | `ANIMATIONS_ENABLED` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 65 | `ANIMATION_SPEED` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 66 | `ANIMATION_STYLE` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 67 | `ANIMATION_EASING` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 68 | `ANIMATION_INTENSITY` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 74 | `rawPalette` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 83 | `rawTone` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 88 | `normalizedTone` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 165 | `rawPalette` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. El operador Elvis (`?:`) define un valor alternativo cuando la expresión de la izquierda produce `null`. |
| 480 | `MAIN_OPTION_MENU_KEYS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 481 | `PRIORITY_OPTION_KEYS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 482 | `COLOR_OPTION_KEYS` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 484 | `DEFAULT_OPTION_MENU_ORDER` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 486 | `requested` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. |
| 535 | `currentPaletteKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |
| 545 | `legacyPaletteKeys` | `val` | `inferido` | Referencia inmutable: después de inicializarse, este identificador no puede apuntar a otro valor dentro de su ámbito. El tipo se infiere a partir del inicializador de Kotlin; no hay una anotación de tipo redundante en la declaración. Su visibilidad `private` restringe el acceso al ámbito declarado por Kotlin para esta propiedad. |

## 6. Mapa exhaustivo de TODOS los bloques delimitados por `{}`

Esta tabla recorre todas las llaves estructurales detectadas fuera de strings/comentarios. Así se documentan también lambdas de Compose, callbacks, iteraciones y ámbitos locales que no tienen un nombre propio de función.

| Inicio–fin | Prof. | Cabecera/contexto | Qué significa ese bloque |
|---|---:|---|---|
| 16–598 | 0 | `class SettingsRepository(private val context: Context)` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 17–69 | 1 | `companion object` | Cuerpo de tipo/contenedor: agrupa propiedades y miembros bajo la misma responsabilidad y controla su visibilidad/estado compartido. |
| 72–147 | 1 | `context.dataStore.data.map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 83–85 | 2 | `val rawTone = preferences[BACKGROUND_TONE_INDEX]?: if (isLegacyPalette(rawPalette))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 85–87 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 88–90 | 2 | `val normalizedTone = if (isLegacyPalette(rawPalette))` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 90–92 | 2 | `} else` | Rama alternativa/fallback: absorbe los casos que no pasaron las condiciones previas. |
| 148–152 | 1 | `suspend fun setDarkMode(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 149–151 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 153–157 | 1 | `suspend fun setBackgroundColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 154–156 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 158–169 | 1 | `suspend fun setBackgroundToneIndex(value: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 159–168 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 170–174 | 1 | `suspend fun setBackgroundIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 171–173 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 175–179 | 1 | `suspend fun setSettingsPanelTone(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 176–178 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 180–184 | 1 | `suspend fun setSurfacePanelIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 181–183 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 185–189 | 1 | `suspend fun setHeaderIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 186–188 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 190–194 | 1 | `suspend fun setTextColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 191–193 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 195–199 | 1 | `suspend fun setTextOutlineEnabled(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 196–198 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 200–204 | 1 | `suspend fun setNoteUiTextColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 201–203 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 205–209 | 1 | `suspend fun setSliderStyle(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 206–208 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 210–214 | 1 | `suspend fun setFont(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 211–213 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 215–219 | 1 | `suspend fun setFontSize(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 216–218 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 220–224 | 1 | `suspend fun setSoundEffectsEnabled(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 221–223 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 225–229 | 1 | `suspend fun setSoundEffectsVolume(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 226–228 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 230–234 | 1 | `suspend fun setSoundEffectsTheme(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 231–233 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 235–239 | 1 | `suspend fun setHapticEffectsEnabled(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 236–238 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 240–244 | 1 | `suspend fun setHapticEffectsIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 241–243 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 245–249 | 1 | `suspend fun setHapticEffectsStyle(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 246–248 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 250–254 | 1 | `suspend fun setLanguage(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 251–253 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 255–259 | 1 | `suspend fun setGridColumns(value: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 256–258 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 260–264 | 1 | `suspend fun setSortOrder(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 261–263 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 265–269 | 1 | `suspend fun setProfileImageUri(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 266–268 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 270–274 | 1 | `suspend fun setProfileImageSize(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 271–273 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 275–279 | 1 | `suspend fun setIconStyle(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 276–278 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 280–284 | 1 | `suspend fun setIconSize(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 281–283 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 285–289 | 1 | `suspend fun setAccentColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 286–288 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 290–294 | 1 | `suspend fun setNoteCardCornerRadius(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 291–293 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 295–299 | 1 | `suspend fun setNoteCardElevation(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 296–298 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 300–304 | 1 | `suspend fun setNoteCardPadding(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 301–303 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 305–309 | 1 | `suspend fun setNoteCardImageHeight(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 306–308 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 310–314 | 1 | `suspend fun setNoteTitleMaxLines(value: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 311–313 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 315–319 | 1 | `suspend fun setNoteContentMaxLines(value: Int)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 316–318 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 320–324 | 1 | `suspend fun setNoteLineSpacing(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 321–323 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 325–329 | 1 | `suspend fun setShowNoteDate(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 326–328 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 330–334 | 1 | `suspend fun setShowCategoryChip(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 331–333 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 335–339 | 1 | `suspend fun setShowFavoriteIcon(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 336–338 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 340–344 | 1 | `suspend fun setFabSize(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 341–343 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 345–349 | 1 | `suspend fun setOptionMenuOrder(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 346–348 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 350–354 | 1 | `suspend fun setOptionMenuHiddenItems(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 351–353 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 355–359 | 1 | `suspend fun setOptionMenuShowIcons(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 356–358 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 360–364 | 1 | `suspend fun setOptionMenuTextColor(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 361–363 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 365–369 | 1 | `suspend fun setOptionMenuOpacity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 366–368 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 370–374 | 1 | `suspend fun setPriorityMenuHiddenItems(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 371–373 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 375–379 | 1 | `suspend fun setColorMenuHiddenItems(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 376–378 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 380–390 | 1 | `suspend fun resetOptionMenuSettings()` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 381–389 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 391–395 | 1 | `suspend fun setPerformanceMode(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 392–394 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 396–400 | 1 | `suspend fun setAnimationsEnabled(value: Boolean)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 397–399 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 401–405 | 1 | `suspend fun setAnimationSpeed(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 402–404 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 406–410 | 1 | `suspend fun setAnimationStyle(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 407–409 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 411–415 | 1 | `suspend fun setAnimationEasing(value: String)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 412–414 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 416–420 | 1 | `suspend fun setAnimationIntensity(value: Float)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 417–419 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 426–479 | 1 | `suspend fun restoreFromBackup(value: AppSettings)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 427–478 | 2 | `context.dataStore.edit` | Ámbito delimitado por llaves en profundidad 2. Introduce un contexto local de ejecución/visibilidad; las variables declaradas dentro dejan de estar accesibles al salir del bloque salvo valores capturados o efectos persistidos. |
| 485–494 | 1 | `private fun normalizeOptionMenuOrder(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 486–488 | 2 | `val requested = value.split(",").map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 488–490 | 2 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 491–493 | 2 | `return (requested + MAIN_OPTION_MENU_KEYS.filterNot` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 495–501 | 1 | `private fun normalizeHiddenItems(value: String, validKeys: List<String>): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 496–498 | 2 | `return value.split(",").map` | Transformación de colección: produce un nuevo elemento por cada entrada y normalmente genera una colección de salida. |
| 498–500 | 2 | `}.filter` | Filtro de colección: conserva solo los elementos cuyo predicado devuelve verdadero. |
| 502–505 | 1 | `private fun normalizeOptionMenuTextColor(value: String): String = when (value)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 506–509 | 1 | `private fun normalizePerformanceMode(value: String): String = when (value)` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 510–517 | 1 | `private fun normalizeAnimationStyle(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 511–516 | 2 | `return when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 518–523 | 1 | `private fun normalizeAnimationEasing(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 519–522 | 2 | `return when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 546–557 | 1 | `private fun normalizePaletteKey(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 547–549 | 2 | `if (value in currentPaletteKeys)` | Rama condicional: este bloque existe solo cuando la condición es verdadera; la condición es una restricción de entrada en tiempo de ejecución. |
| 550–556 | 2 | `return when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 559–564 | 1 | `private fun normalizeUiTextColor(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 560–563 | 2 | `return when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 565–570 | 1 | `private fun normalizeIconStyle(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 566–569 | 2 | `return when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 571–577 | 1 | `private fun normalizeAccentColor(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 572–576 | 2 | `return when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 578–584 | 1 | `private fun normalizeSoundEffectsTheme(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 579–583 | 2 | `return when (value.trim().lowercase())` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 585–591 | 1 | `private fun normalizeHapticEffectsStyle(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 586–590 | 2 | `return when (value.trim().lowercase())` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |
| 592–597 | 1 | `private fun normalizeSliderStyle(value: String): String` | Cuerpo de función: crea un ámbito local para parámetros/variables y concentra la secuencia de instrucciones que implementa el contrato de la función. |
| 593–596 | 2 | `return when (value)` | Bloque de selección `when`: agrupa casos y define una política distinta según el valor/condición evaluada. |

## 7. Lectura de seguridad antes de tocar este archivo

La documentación describe **lo que el código actual ya hace**. No constituye una propuesta de refactorización. Si se quisiera cambiar algo en el futuro, primero habría que preservar contratos públicos, claves de configuración, restricciones de API Android, comportamiento de Compose, lifecycle, operaciones de I/O y compatibilidad con los datos ya persistidos. En esta entrega no se hizo ninguna de esas modificaciones.
