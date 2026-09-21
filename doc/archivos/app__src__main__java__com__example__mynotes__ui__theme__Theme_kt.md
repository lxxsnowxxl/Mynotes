# Theme.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/Theme.kt`  **SHA-256:** `130e512bbdbf3177078209c182ce549511fb038baaee6e644db86015f22c07b1`  **Líneas:** 269 · **Bytes:** 13214 · **Imports:** 14 · **Declaraciones detectadas:** 13
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

MaterialTheme de MyNotes y derivación de esquema desde paleta/configuración.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.theme`.

### Android / Jetpack / Compose

`androidx.compose.foundation.isSystemInDarkTheme`, `androidx.compose.material3.ColorScheme`, `androidx.compose.material3.LocalTextStyle`, `androidx.compose.material3.MaterialTheme`, `androidx.compose.material3.Typography as MaterialTypography`, `androidx.compose.material3.darkColorScheme`, `androidx.compose.material3.lightColorScheme`, `androidx.compose.runtime.Composable`, `androidx.compose.runtime.CompositionLocalProvider`, `androidx.compose.runtime.remember`, `androidx.compose.ui.geometry.Offset`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.Shadow`, `androidx.compose.ui.text.TextStyle`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 20 | `fun` | `mixColor` | `private fun mixColor(first: Color, second: Color, amount: Float): Color {` |
| 27 | `fun` | `intensityAmount` | `` |
| 31 | `fun` | `applySectionIntensity` | `` |
| 35 | `fun` | `readableContentColor` | `` |
| 37 | `fun` | `resolvedTextColor` | `` |
| 45 | `fun` | `resolvedPaletteTextColor` | `private fun resolvedPaletteTextColor(textColor: String, toneIndex: Int, background: Color): Color {` |
| 52 | `fun` | `resolvedPaletteSecondaryTextColor` | `private fun resolvedPaletteSecondaryTextColor(textColor: String, toneIndex: Int, background: Color): Color {` |
| 60 | `fun` | `resolveAccentColor` | `` |
| 87 | `fun` | `TextStyle` | `` |
| 95 | `fun` | `typographyWithBlackOutline` | `` |
| 109 | `fun` | `lightScheme` | `` |
| 154 | `fun` | `darkScheme` | `` |
| 207 | `fun` | `MyNotesTheme` | `` |

## 4. Estado, efectos y límites observables

- **Compose state:** 4 aparición/apariciones.
- **coerce*:** 17 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
