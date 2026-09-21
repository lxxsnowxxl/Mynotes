# ExtremeCustomizationSection.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/ExtremeCustomizationSection.kt`  **SHA-256:** `d0469cb4099d1fff457202be58f71d384be3a1e5a475ba5e21a46d6a55182295`  **Líneas:** 631 · **Bytes:** 37016 · **Imports:** 75 · **Declaraciones detectadas:** 8
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Controles avanzados de apariencia, tarjeta de nota, iconos, sliders y otras opciones extremas.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`androidx.compose.foundation.border`, `androidx.compose.foundation.background`, `android.content.Intent`, `android.net.Uri`, `androidx.activity.compose.rememberLauncherForActivityResult`, `androidx.activity.result.contract.ActivityResultContracts`, `androidx.compose.foundation.BorderStroke`, `androidx.compose.foundation.clickable`, `androidx.compose.foundation.interaction.MutableInteractionSource`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.defaultMinSize`, `androidx.compose.foundation.layout.heightIn`, `androidx.compose.foundation.layout.width`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.shape.CircleShape`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.Person`, `androidx.compose.material.icons.filled.ExpandMore`, `androidx.compose.material.icons.filled.Check`, `androidx.compose.material3.Button`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.Icon`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Switch`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.Immutable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.mutableFloatStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.draw.clip`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.luminance`, `androidx.compose.ui.layout.ContentScale`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.style.TextAlign`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`, `com.example.mynotes.ui.theme.PaletteCatalog`, `com.example.mynotes.ui.theme.adaptiveUiButtonContainer`, `com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors`, `com.example.mynotes.ui.theme.automaticUiTextColor`, `com.example.mynotes.ui.theme.softenUiColorToContrast`

### Kotlin / Coroutines / Java

`kotlin.math.roundToInt`

### Terceros / otros

`coil3.compose.AsyncImage`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 78 | `class` | `IconStyleOption` | `` |
| 85 | `class` | `AccentOption` | `@Immutable` |
| 87 | `class` | `MotionOption` | `` |
| 124 | `fun` | `ExtremeCustomizationSection` | `@Composable` |
| 513 | `fun` | `ProfileActionButton` | `` |
| 554 | `fun` | `MotionOptionPicker` | `` |
| 607 | `fun` | `CustomSlider` | `` |
| 621 | `fun` | `ToggleRow` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 41 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 14 aparición/apariciones.
- **Coroutines:** 1 aparición/apariciones.
- **Room:** 1 aparición/apariciones.
- **I/O/red:** 1 aparición/apariciones.
- **try/catch:** 2 aparición/apariciones.
- **safe calls:** 4 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.components.AppDropdownMenu`
- `com.example.mynotes.ui.motion.ConfigurableAnimatedContent`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.PaletteCatalog`
- `com.example.mynotes.ui.theme.adaptiveUiButtonContainer`
- `com.example.mynotes.ui.theme.automaticUiTextColor`
- `com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors`
- `com.example.mynotes.ui.theme.softenUiColorToContrast`

## 6. Recursos Android referenciados

- **R.string:** `extreme_accent`, `extreme_card_elevation`, `extreme_card_padding`, `extreme_card_radius`, `extreme_change_photo`, `extreme_content_lines`, `extreme_edit_photo`, `extreme_fab_size`, `extreme_icon_material`, `extreme_icon_minimal`, `extreme_icon_outlined`, `extreme_icon_rounded` ×2, `extreme_icon_size`, `extreme_icons`, `extreme_image_height`, `extreme_line_spacing`, `extreme_note_cards`, `extreme_note_outline_width`, `extreme_personalization`, `extreme_profile`, `extreme_profile_size`, `extreme_remove_photo`, `extreme_show_category`, `extreme_show_date`, `extreme_show_favorite`, `extreme_title_lines`, `motion_description`, `motion_easing`, `motion_easing_accelerate`, `motion_easing_decelerate`, `motion_easing_emphasized`, `motion_easing_emphasized_accel`, `motion_easing_emphasized_decel`, `motion_easing_expressive`, `motion_easing_linear`, `motion_easing_standard`, `motion_enabled`, `motion_intensity`, `motion_preview`, `motion_preview_button`, `motion_speed`, `motion_style`, `motion_style_axis_x`, `motion_style_axis_y`, `motion_style_axis_z`, `motion_style_bounce`, `motion_style_container_transform`, `motion_style_elastic`, `motion_style_elastic_slide`, `motion_style_expand`, `motion_style_expand_horizontal`, `motion_style_expand_vertical`, `motion_style_expressive_spring`, `motion_style_fade`, `motion_style_pop`, `motion_style_predictive`, `motion_style_random`, `motion_style_slide_down`, `motion_style_slide_left`, `motion_style_slide_right`, `motion_style_slide_up`, `motion_style_slide_zoom_left`, `motion_style_slide_zoom_up`, `motion_style_soft_reveal`, `motion_style_subtle`, `motion_style_tonal_pop`, `motion_style_zoom`, `motion_style_zoom_fade`, `motion_title`, `performance_mode_balanced`, `performance_mode_description`, `performance_mode_performance`, `performance_mode_quality`, `performance_mode_title`

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
