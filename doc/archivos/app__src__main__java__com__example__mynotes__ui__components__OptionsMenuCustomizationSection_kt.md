# OptionsMenuCustomizationSection.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/OptionsMenuCustomizationSection.kt`  **SHA-256:** `e90945c9af4c2025c57fda46b1d4e1a99927ab3a05733504369eb3fa0848e6de`  **Líneas:** 368 · **Bytes:** 20066 · **Imports:** 48 · **Declaraciones detectadas:** 8
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Personalización del menú de opciones de cada nota.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.PaddingValues`, `androidx.compose.foundation.layout.Row`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.size`, `androidx.compose.foundation.layout.width`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.ExpandMore`, `androidx.compose.material.icons.filled.KeyboardArrowDown`, `androidx.compose.material.icons.filled.KeyboardArrowUp`, `androidx.compose.material.icons.filled.Refresh`, `androidx.compose.material3.DropdownMenuItem`, `androidx.compose.material3.Icon`, `androidx.compose.material3.IconButton`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Slider`, `androidx.compose.material3.Switch`, `androidx.compose.material3.Text`, `androidx.compose.material3.TextButton`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.LaunchedEffect`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableFloatStateOf`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.text.style.TextAlign`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.window.PopupProperties`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.ui.components.AppDropdownMenu`, `com.example.mynotes.settings.AppSettings`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`

### Kotlin / Coroutines / Java

`kotlin.math.roundToInt`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 51 | `class` | `MenuOptionDescriptor` | `` |
| 72 | `fun` | `OptionsMenuCustomizationSection` | `@Composable` |
| 245 | `fun` | `MenuOrderRow` | `` |
| 279 | `fun` | `CompactToggleGrid` | `` |
| 310 | `fun` | `ToggleRow` | `` |
| 338 | `fun` | `optionMenuTextColorLabel` | `` |
| 347 | `fun` | `normalizedOrder` | `` |
| 360 | `fun` | `parseKeys` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 11 aparición/apariciones.
- **LaunchedEffect/DisposableEffect:** 2 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.settings.AppSettings`
- `com.example.mynotes.ui.components.AppDropdownMenu`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`

## 6. Recursos Android referenciados

- **R.string:** `mock_color`, `mock_color_blue`, `mock_color_brown`, `mock_color_cyan`, `mock_color_default`, `mock_color_gray`, `mock_color_green`, `mock_color_lime`, `mock_color_mint`, `mock_color_orange`, `mock_color_pink`, `mock_color_purple`, `mock_color_red`, `mock_color_teal`, `mock_color_yellow`, `mock_delete`, `mock_edit`, `mock_favorites`, `mock_move`, `mock_pin`, `mock_priority`, `mock_priority_high`, `mock_priority_low`, `mock_priority_medium`, `mock_priority_none`, `option_menu_change`, `option_menu_color_submenu`, `option_menu_customization_description`, `option_menu_customization_title`, `option_menu_main_actions`, `option_menu_main_actions_hint`, `option_menu_move_down`, `option_menu_move_up`, `option_menu_opacity`, `option_menu_priority_submenu`, `option_menu_reset`, `option_menu_show_icons`, `option_menu_text_black` ×2, `option_menu_text_color`, `option_menu_text_follow_note` ×2, `option_menu_text_white` ×2

## 7. Puntos de revisión al modificarlo

- Conservar rangos `coerce*`, claves DataStore y compatibilidad con backups existentes.
