# ConfigurationModeDialog.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/components/ConfigurationModeDialog.kt`  **SHA-256:** `b9f0581f2c1ed9ab9146a4b89d30d9ad0acd886d9f99b86ad18df604e63719d5`  **Líneas:** 294 · **Bytes:** 12244 · **Imports:** 43 · **Declaraciones detectadas:** 2
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Diálogo para elegir modo Básico o Avanzado.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.components`.

### Android / Jetpack / Compose

`androidx.compose.foundation.BorderStroke`, `androidx.activity.compose.BackHandler`, `androidx.compose.foundation.background`, `androidx.compose.foundation.gestures.detectTapGestures`, `androidx.compose.foundation.layout.Arrangement`, `androidx.compose.foundation.layout.Box`, `androidx.compose.foundation.layout.Column`, `androidx.compose.foundation.layout.Spacer`, `androidx.compose.foundation.layout.fillMaxSize`, `androidx.compose.foundation.layout.fillMaxWidth`, `androidx.compose.foundation.layout.height`, `androidx.compose.foundation.layout.padding`, `androidx.compose.foundation.layout.widthIn`, `androidx.compose.foundation.shape.RoundedCornerShape`, `androidx.compose.material3.Button`, `androidx.compose.material3.ButtonDefaults`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.OutlinedButton`, `androidx.compose.material3.Surface`, `androidx.compose.material3.Text`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.getValue`, `androidx.compose.runtime.mutableStateOf`, `androidx.compose.runtime.remember`, `androidx.compose.runtime.setValue`, `androidx.compose.ui.Alignment`, `androidx.compose.ui.Modifier`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.input.pointer.pointerInput`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontWeight`, `androidx.compose.ui.unit.dp`, `androidx.compose.ui.unit.sp`, `androidx.compose.ui.res.stringResource`

### Proyecto MyNotes

`com.example.mynotes.R`, `com.example.mynotes.ui.sound.UiActionSound`, `com.example.mynotes.ui.sound.UiSoundPlayer`, `com.example.mynotes.ui.theme.adaptiveUiButtonContainer`, `com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors`, `com.example.mynotes.ui.theme.automaticUiTextColor`, `com.example.mynotes.ui.theme.ensureUiContrast`, `com.example.mynotes.ui.theme.softenUiColorToContrast`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 58 | `fun` | `ConfigurationModeDialog` | `@Composable` |
| 234 | `fun` | `ConfigurationModeChoiceButton` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 9 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

- `com.example.mynotes.R`
- `com.example.mynotes.ui.sound.UiActionSound`
- `com.example.mynotes.ui.sound.UiSoundPlayer`
- `com.example.mynotes.ui.theme.adaptiveUiButtonContainer`
- `com.example.mynotes.ui.theme.automaticUiTextColor`
- `com.example.mynotes.ui.theme.ensureUiContrast`
- `com.example.mynotes.ui.theme.resolveAdaptiveUiButtonColors`
- `com.example.mynotes.ui.theme.softenUiColorToContrast`

## 6. Recursos Android referenciados

- **R.string:** `configuration_mode_advanced`, `configuration_mode_advanced_description`, `configuration_mode_basic`, `configuration_mode_basic_description`, `configuration_mode_change_later`, `configuration_mode_confirm`, `configuration_mode_welcome_description`, `configuration_mode_welcome_title`

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
