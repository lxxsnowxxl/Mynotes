# ExtremeCustomizationSection.kt — explicación completa del código

**Ruta:** `app/src/main/java/com/example/mynotes/ui/components/ExtremeCustomizationSection.kt`  
**SHA-256:** `d0469cb4099d1fff457202be58f71d384be3a1e5a475ba5e21a46d6a55182295`  
**Líneas:** 632  
**Package:** `com.example.mynotes.ui.components`

## 1. Para qué existe este archivo

Panel avanzado con personalización de iconos, acento, tarjetas, contorno, FAB y animaciones.

## 2. Tipos/clases declarados

- Línea **80** — `private data  class IconStyleOption`.
- Línea **86** — `private data  class AccentOption`.
- Línea **89** — `private data  class MotionOption`.

## 3. Estado, constantes y valores importantes

- **`iconStyles`** (línea 81) inicia con `listOf(IconStyleOption("material"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`motionStyles`** (línea 90) inicia con `listOf(MotionOption("zoom"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`motionEasings`** (línea 109) inicia con `listOf(MotionOption("standard"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`performanceModes`** (línea 115) inicia con `listOf(MotionOption("performance"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`accents`** (línea 117) inicia con `listOf(AccentOption("red"`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`context`** (línea 136) inicia con `LocalContext.current`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`picker`** (línea 222) inicia con `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`iconMenuLongestLabel`** (línea 331) inicia con `iconStyles.maxOfOrNull { context.getString(it.labelRes`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`iconMenuWidth`** (línea 332) inicia con `when {`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`paletteAccent`** (línea 368) inicia con `remember(settings.backgroundColor`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`accentItems`** (línea 371) inicia con `remember {`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`color`** (línea 378) inicia con `option?.color ?: paletteAccent`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`selected`** (línea 379) inicia con `settings.accentColor == key`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`accentInteraction`** (línea 380) inicia con `remember(key`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`checkColor`** (línea 381) inicia con `if (color.luminance(`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`accentTonalBase`** (línea 523) inicia con `MaterialTheme.colorScheme.primaryContainer`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`resolvedColors`** (línea 524) inicia con `remember(accentTonalBase`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`containerColor`** (línea 533) inicia con `resolvedColors.container`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`textColor`** (línea 534) inicia con `resolvedColors.content`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`longestOptionLength`** (línea 565) inicia con `options.maxOfOrNull { context.getString(it.labelRes`. Su valor se usa dentro de la responsabilidad descrita para este archivo.
- **`compactMenuWidth`** (línea 566) inicia con `when {`. Su valor se usa dentro de la responsabilidad descrita para este archivo.

## 4. Funciones y flujo, una por una

### `ExtremeCustomizationSection` — líneas 125–512

**Firma:** `fun ExtremeCustomizationSection(settings: AppSettings, fontFamily: FontFamily, textColor: Color, secondaryTextColor: Color, graphicColor: Color, profileOnly: Boolean = false, showProfileSection: Boolean = true, onProfileImageUriChange: (String) -> Unit, onProfileImageSizeChange: (Float) -> Unit, onIconStyleChange: (String) -> Unit, onIconSizeChange: (Float) -> Unit, onAccentColorChange: (String) -> Unit, onNoteCardCornerRadiusChange: (Float) -> Unit, onNoteCardElevationChange: (Float) -> Unit, onNoteCardPaddingChange: (Float) -> Unit, onNoteCardImageHeightChange: (Float) -> Unit, onNoteCardOutlineWidthChange: (Float) -> Unit, onNoteTitleMaxLinesChange: (Int) -> Unit, onNoteContentMaxLinesChange: (Int) -> Unit, onNoteLineSpacingChange: (Float) -> Unit, onShowNoteDateChange: (Boolean) -> Unit, onShowCategoryChipChange: (Boolean) -> Unit, onShowFavoriteIconChange: (Boolean) -> Unit, onFabSizeChange: (Float) -> Unit, onPerformanceModeChange: (String) -> Unit, onAnimationsEnabledChange: (Boolean) -> Unit, onAnimationStyleChange: (String) -> Unit, onAnimationEasingChange: (String) -> Unit, onAnimationSpeedChange: (Float) -> Unit, onAnimationIntensityChange: (Float) -> Unit)`

Renderiza los controles avanzados y mantiene estados temporales de sliders para una interacción fluida antes de confirmar el valor persistido.

**Entradas:**
- `settings: AppSettings`
- `fontFamily: FontFamily`
- `textColor: Color`
- `secondaryTextColor: Color`
- `graphicColor: Color`
- `profileOnly: Boolean = false`
- `showProfileSection: Boolean = true`
- `onProfileImageUriChange: (String) -> Unit`
- `onProfileImageSizeChange: (Float) -> Unit`
- `onIconStyleChange: (String) -> Unit`
- `onIconSizeChange: (Float) -> Unit`
- `onAccentColorChange: (String) -> Unit`
- `onNoteCardCornerRadiusChange: (Float) -> Unit`
- `onNoteCardElevationChange: (Float) -> Unit`
- `onNoteCardPaddingChange: (Float) -> Unit`
- `onNoteCardImageHeightChange: (Float) -> Unit`
- `onNoteCardOutlineWidthChange: (Float) -> Unit`
- `onNoteTitleMaxLinesChange: (Int) -> Unit`
- `onNoteContentMaxLinesChange: (Int) -> Unit`
- `onNoteLineSpacingChange: (Float) -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Accede al sistema de archivos interno/cache.
- Lee/escribe Uris mediante ContentResolver.
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Contiene manejo de fallos/excepciones y una ruta de recuperación/fallback.
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `mutableFloatStateOf`, `toFloat`, `LaunchedEffect`, `rememberLauncherForActivityResult`, `OpenDocument`, `takePersistableUriPermission`, `play`, `ProfileImageEditorDialog`, `playAction`, `onProfileImageUriChange`, `toString`, `height`, `SettingsSectionPanel`, `PaddingValues`, `size`, `clickable`, `isNotBlank`, `managedProfileSourceUri`.

### `ProfileActionButton` — líneas 516–553

**Firma:** `private fun ProfileActionButton( text: String, panelBackground: Color, textColorMode: String, fontFamily: FontFamily, onClick: () -> Unit )`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `text: String`
- `panelBackground: Color`
- `textColorMode: String`
- `fontFamily: FontFamily`
- `onClick: () -> Unit`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Participa en estado/efectos de Compose.

**Operaciones/funciones que coordina:** `resolveAdaptiveUiButtonColors`, `Button`, `defaultMinSize`, `RoundedCornerShape`, `PaddingValues`, `buttonColors`.

### `MotionOptionPicker` — líneas 556–606

**Firma:** `private fun MotionOptionPicker(title: String, selectedKey: String, options: List<MotionOption>, onSelected: (String) -> Unit, fontFamily: FontFamily, textColor: Color)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `title: String`
- `selectedKey: String`
- `options: List<MotionOption>`
- `onSelected: (String) -> Unit`
- `fontFamily: FontFamily`
- `textColor: Color`

**Salida:** Unit o inferido por Kotlin.

**Efectos/APIs observados en el cuerpo:**
- Participa en estado/efectos de Compose.

**Decisiones y protecciones visibles:**
- Usa operadores Elvis/fallback para datos nulos o ausentes.
- Contiene decisiones condicionales (`if`) para seleccionar comportamiento.
- Usa `when` para mapear estados/tipos/opciones.

**Operaciones/funciones que coordina:** `first`, `getString`, `height`, `fillMaxWidth`, `weight`, `TextButton`, `playAction`, `size`, `AppDropdownMenu`, `heightIn`, `width`, `PopupProperties`, `DropdownMenuItem`, `PaddingValues`, `onSelected`.

### `CustomSlider` — líneas 609–620

**Firma:** `private fun CustomSlider(title: String, label: String, value: Float, onValueChange: (Float) -> Unit, onFinished: () -> Unit, range: ClosedFloatingPointRange<Float>, steps: Int, settings: AppSettings, fontFamily: FontFamily, textColor: Color)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `title: String`
- `label: String`
- `value: Float`
- `onValueChange: (Float) -> Unit`
- `onFinished: () -> Unit`
- `range: ClosedFloatingPointRange<Float>`
- `steps: Int`
- `settings: AppSettings`
- `fontFamily: FontFamily`
- `textColor: Color`

**Salida:** Unit o inferido por Kotlin.

**Operaciones/funciones que coordina:** `height`, `fillMaxWidth`, `weight`, `StyledSettingsSlider`, `copy`.

### `ToggleRow` — líneas 623–631

**Firma:** `private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, fontFamily: FontFamily, textColor: Color)`

Componente de interfaz Compose. Construye esta parte del layout a partir de sus parámetros y estado; los callbacks recibidos trasladan las acciones hacia la capa propietaria del dato.

**Entradas:**
- `title: String`
- `checked: Boolean`
- `onCheckedChange: (Boolean) -> Unit`
- `fontFamily: FontFamily`
- `textColor: Color`

**Salida:** Unit o inferido por Kotlin.

**Operaciones/funciones que coordina:** `fillMaxWidth`, `padding`, `weight`, `Switch`, `playToggle`, `onCheckedChange`.

## 5. Cómo se conecta con el resto de MyNotes

- Usa `com.example.mynotes.R`.
- Usa `com.example.mynotes.ui.components.AppDropdownMenu`.
- Usa `com.example.mynotes.settings.AppSettings`.
- Usa `com.example.mynotes.ui.sound.UiActionSound`.
- Usa `com.example.mynotes.ui.sound.UiSound`.
- Usa `com.example.mynotes.ui.sound.UiSoundPlayer`.
- Usa `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`.
- Usa `com.example.mynotes.ui.theme.PaletteCatalog`.
- Usa `com.example.mynotes.ui.theme.adaptiveUiButtonContainer`.
- Usa `com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors`.
- Usa `com.example.mynotes.ui.theme.automaticUiTextColor`.
- Usa `com.example.mynotes.ui.theme.softenUiColorToContrast`.

## 6. Recursos Android que utiliza

- `R.string`: `extreme_accent`, `extreme_card_elevation`, `extreme_card_padding`, `extreme_card_radius`, `extreme_change_photo`, `extreme_content_lines`, `extreme_edit_photo`, `extreme_fab_size`, `extreme_icon_material`, `extreme_icon_minimal`, `extreme_icon_outlined`, `extreme_icon_rounded`, `extreme_icon_size`, `extreme_icons`, `extreme_image_height`, `extreme_line_spacing`, `extreme_note_cards`, `extreme_note_outline_width`, `extreme_personalization`, `extreme_profile`, `extreme_profile_size`, `extreme_remove_photo`, `extreme_show_category`, `extreme_show_date`, `extreme_show_favorite`, `extreme_title_lines`, `motion_description`, `motion_easing`, `motion_easing_accelerate`, `motion_easing_decelerate`, `motion_easing_emphasized`, `motion_easing_emphasized_accel`, `motion_easing_emphasized_decel`, `motion_easing_expressive`, `motion_easing_linear`, `motion_easing_standard`, `motion_enabled`, `motion_intensity`, `motion_preview`, `motion_preview_button`, `motion_speed`, `motion_style`, `motion_style_axis_x`, `motion_style_axis_y`, `motion_style_axis_z`, `motion_style_bounce`, `motion_style_container_transform`, `motion_style_elastic`, `motion_style_elastic_slide`, `motion_style_expand`, `motion_style_expand_horizontal`, `motion_style_expand_vertical`, `motion_style_expressive_spring`, `motion_style_fade`, `motion_style_pop`, `motion_style_predictive`, `motion_style_random`, `motion_style_slide_down`, `motion_style_slide_left`, `motion_style_slide_right`, `motion_style_slide_up`, `motion_style_slide_zoom_left`, `motion_style_slide_zoom_up`, `motion_style_soft_reveal`, `motion_style_subtle`, `motion_style_tonal_pop`, `motion_style_zoom`, `motion_style_zoom_fade`, `motion_title`, `performance_mode_balanced`, `performance_mode_description`, `performance_mode_performance`, `performance_mode_quality`, `performance_mode_title`

## 7. Tecnologías y efectos relevantes

- Accede al sistema de archivos interno/cache.
- Lee/escribe Uris mediante ContentResolver.
- Participa en estado/efectos de Compose.

## 8. Lectura práctica del flujo

Una forma útil de seguir este archivo en el depurador es recorrer estas operaciones en este orden aproximado:
1. `ExtremeCustomizationSection` — Renderiza los controles avanzados y mantiene estados temporales de sliders para una interacción fluida antes de confirmar el valor persistido.

## 9. Qué no debe romperse al modificarlo

- Conservar validaciones de Uri/ruta y no confiar en nombres externos sin sanitizar.
- Evitar trabajo bloqueante durante composición y mantener estado estable para limitar recomposiciones.

## 10. Resumen en lenguaje sencillo

En términos simples: Panel avanzado con personalización de iconos, acento, tarjetas, contorno, FAB y animaciones. La sección función por función anterior describe qué entra, qué devuelve y qué efectos produce cada operación detectada en el fuente actual.
