# NoteColors.kt — documentación del código actual
**Ruta real:** `app/src/main/java/com/example/mynotes/ui/theme/NoteColors.kt`  **SHA-256:** `cf9df405cc74513f684ec07b1ce73c4d15079ac16ba53969e34bfc2e651a4b17`  **Líneas:** 328 · **Bytes:** 12434 · **Imports:** 4 · **Declaraciones detectadas:** 18
> Documento generado fuera de `app/` a partir de lectura del código. El fuente es la única fuente de verdad; no se modificó para generar esta documentación.
## 1. Responsabilidad

Resolución de colores de notas, contraste, texto, botones y contornos adaptativos.
## 2. Package e imports

Package declarado: `com.example.mynotes.ui.theme`.

### Android / Jetpack / Compose

`androidx.compose.material3.MaterialTheme`, `androidx.compose.runtime.Composable`, `androidx.compose.ui.graphics.Color`, `androidx.compose.ui.graphics.luminance`

## 3. Declaraciones detectadas

| Línea | Tipo | Nombre | Firma/inicio |
|---:|---|---|---|
| 10 | `fun` | `noteBackgroundColor` | `` |
| 43 | `fun` | `compositeUiColor` | `fun compositeUiColor(foreground: Color, background: Color): Color {` |
| 51 | `fun` | `uiContrastRatio` | `fun uiContrastRatio(foreground: Color, background: Color): Float {` |
| 64 | `fun` | `automaticUiTextColor` | `fun automaticUiTextColor(background: Color): Color {` |
| 83 | `fun` | `resolveUiTextColor` | `fun resolveUiTextColor(value: String, background: Color): Color {` |
| 95 | `fun` | `mixOpaqueUiColor` | `` |
| 108 | `fun` | `softenUiColorToContrast` | `fun softenUiColorToContrast(foreground: Color, background: Color, minimumContrast: Float, maximumSoftening: Float = 0.62f): Color {` |
| 133 | `fun` | `resolveSecondaryUiTextColor` | `fun resolveSecondaryUiTextColor(value: String, background: Color): Color {` |
| 146 | `fun` | `resolveUiGraphicColor` | `fun resolveUiGraphicColor(value: String, background: Color): Color {` |
| 160 | `fun` | `ensureUiContrast` | `fun ensureUiContrast(preferred: Color, background: Color, minimumContrast: Float = 3f): Color {` |
| 176 | `fun` | `paletteMatchedOutlineColor` | `fun paletteMatchedOutlineColor(background: Color, minimumContrast: Float = 3f): Color {` |
| 205 | `fun` | `adaptiveUiButtonContainer` | `fun adaptiveUiButtonContainer(` |
| 215 | `fun` | `distanceSquared` | `` |
| 224 | `fun` | `add` | `fun add(candidate: Color) {` |
| 227 | `fun` | `addBlendSeries` | `fun addBlendSeries(from: Color, to: Color, steps: Int = 48) {` |
| 264 | `class` | `AdaptiveUiButtonColors` | `` |
| 282 | `fun` | `resolveAdaptiveUiButtonColors` | `fun resolveAdaptiveUiButtonColors(` |
| 328 | `fun` | `manualUiTextColor` | `fun manualUiTextColor(value: String): Color = resolveUiTextColor(value = value, background = Color.White)` |

## 4. Estado, efectos y límites observables

- **coerce*:** 3 aparición/apariciones.

Estas cifras son indicadores de superficie de cambio, no diagnósticos de error. Cualquier modificación debe preservar contratos de persistencia, lifecycle, límites numéricos y nulabilidad visibles en el fuente.

## 5. Dependencias internas directas

No importa directamente otros símbolos `com.example.mynotes.*`.

## 6. Recursos Android referenciados

No se detectaron referencias `R.*` directas.

## 7. Puntos de revisión al modificarlo

- Validar sus llamadores y el comportamiento visible asociado antes de alterar firmas o valores por defecto.
